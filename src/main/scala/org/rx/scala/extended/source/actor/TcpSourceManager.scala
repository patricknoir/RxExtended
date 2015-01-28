package com.williamhill.paris.source.actor

import java.net.InetSocketAddress

import akka.actor._
import akka.event.LoggingReceive
import akka.util.ByteString
import rx.lang.scala.Notification.{OnCompleted, OnNext, OnError}
import rx.lang.scala.{Subscription, Observer}
import scala.concurrent.duration._

case class Subscribe(observer: Observer[ByteString])
case class Unsubscribe(consumer: ActorRef)

/**
 * Created by patrick on 28/01/15.
 */
class TcpSourceManager(address:InetSocketAddress) extends Actor with ActorLogging {

  val connection = context actorOf TcpSourceConnection.props(address, self)

  connection ! InitConnection

  //Defining an ad hoc supervisor strategy to terminate in case of any error.
  override val supervisorStrategy = OneForOneStrategy(maxNrOfRetries = 0, withinTimeRange = 1 second) {
    case _ => SupervisorStrategy.Stop
  }

  var consumers = Set[ActorRef]()

  def receive = LoggingReceive {
    case Subscribe(observer: Observer[ByteString]) =>
      val consumer = context.actorOf(TcpSourceConsumer.props(observer))
      log.debug(s"$observer subscribing")
      consumers = consumers + consumer
      val manager = self
      sender ! Subscription { manager ! Unsubscribe(consumer) }
    case Unsubscribe(consumer) =>
      log.debug(s"$consumer unsubscribing")
      consumers = consumers - consumer
    case err @ OnError(ex)  =>
      log.error(ex, ex.getMessage)
      consumers foreach (_ ! err)
      consumers foreach (_ ! PoisonPill)
      context stop self
    case value @ OnNext(_) => consumers foreach  (_ ! value)
    case OnCompleted => consumers foreach (_ ! OnCompleted)
  }
}

object TcpSourceManager {
  def props(address: InetSocketAddress): Props = Props(new TcpSourceManager(address))
}