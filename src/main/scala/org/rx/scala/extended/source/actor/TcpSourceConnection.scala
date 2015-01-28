package com.williamhill.paris.source.actor

import java.net.InetSocketAddress

import akka.actor.{Props, ActorRef, Actor, ActorLogging}
import akka.event.LoggingReceive
import akka.io.Tcp.{Received, CommandFailed, Connect}
import akka.io.{IO, Tcp}
import akka.util.ByteString
import rx.lang.scala.Notification.{OnError, OnNext}

case object InitConnection

/**
 * Created by patrick on 28/01/15.
 */
class TcpSourceConnection(address:InetSocketAddress, manager: ActorRef) extends Actor with ActorLogging {

  val io = IO(Tcp)

  def init() = {
    io ! Connect(address)
    context become receive
  }

  def receive = LoggingReceive {
    case InitConnection => init()
    case CommandFailed(_: Connect) =>
      log.error(s"error while connecting to $address")
      manager ! OnError(new Exception(s"error while connecting to $address"))
      context stop self
    case Connect(remote, local) =>
      val connection = sender
      log.debug(s"connected to $remote")
      context become processing(connection)
  }

  def processing(connection: ActorRef): Receive = LoggingReceive {
    case CommandFailed(_) =>
      log.error("error while processing data, terminating connection")
      manager ! OnError(new Exception("error while processing data, terminating connection"))
      context stop self
    case Received(data:ByteString) =>
      manager ! OnNext(data)
  }

}

object TcpSourceConnection {
  def props(address: InetSocketAddress, manager:ActorRef): Props = Props(new TcpSourceConnection(address, manager))
}
