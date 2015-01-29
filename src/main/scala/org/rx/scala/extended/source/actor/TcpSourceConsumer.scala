package org.rx.scala.extended.source.actor

import akka.actor.{Props, Actor, ActorLogging}
import akka.event.LoggingReceive
import akka.util.ByteString
import rx.lang.scala.Notification.{OnCompleted, OnError, OnNext}
import rx.lang.scala.Observer

/**
 * Created by patrick on 28/01/15.
 */
class TcpSourceConsumer(observer: Observer[ByteString]) extends Actor with ActorLogging {

  def receive = LoggingReceive {
    case OnNext(value: ByteString) => observer.onNext(value)
    case OnError(err) => observer.onError(err)
    case OnCompleted => observer.onCompleted()
  }

}

object TcpSourceConsumer {
  def props(observer:Observer[ByteString]): Props = Props(new TcpSourceConsumer(observer))
}
