package org.rx.scala.extended

import java.net.InetSocketAddress
import org.rx.scala.extended.source.Source
import akka.actor.ActorSystem

import org.rx.scala.extended.util.implicits._

/**
 * Created by patrick on 29/01/15.
 */
object Main {


  def simpleExample() = {
    implicit val system = ActorSystem("test")

    val tcp = Source.createTcpSource(new InetSocketAddress("localhost", 1234))

    val jsonStream = tcp.map(_.decodeString("UTF-8")).storeMap(in => {
      val lines = in.split("\n").toSeq
      if(in.endsWith("\n")) (lines, None) else (lines.dropRight(1), Some(lines.last))
    }).map(toJson(_))
  }

  def toJson(input:String): JsObject = ???

}

abstract class JsObject()