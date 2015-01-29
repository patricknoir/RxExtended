package com.williamhill.paris.source

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.util.{Timeout, ByteString}
import com.williamhill.paris.source.actor.{Subscribe, TcpSourceManager}
import com.williamhill.paris.util.Monoid
import com.williamhill.paris.util.implicits._
import rx.lang.scala.{Subscription, Observable}
import akka.pattern.ask

import scala.concurrent.{Future, Await}
import scala.concurrent.duration._

/**
 * Created by patrick on 28/01/15.
 */
object Source {

  def createTcpSource(address: InetSocketAddress)(implicit system: ActorSystem,  timeout: Timeout = Timeout(1 minute)): Observable[ByteString] = Observable[ByteString] { observer =>
    val manager = system.actorOf(TcpSourceManager.props(address))

    import system.dispatcher

    Observable.create[ByteString] { observer =>
      val fSubscription: Future[Subscription] = (manager ? Subscribe(observer)).map(_.asInstanceOf[Subscription])

      Await.result(fSubscription, timeout.duration)
    }
  }
  
  def storeMap[R:Monoid, T](source:Observable[R], f: R => (Seq[T], R)): Observable[T] = {
    var buffer: R = implicitly[Monoid[R]].zero

    Observable[T] { observer =>
      source.doOnNext { r =>
        val (ts, rest) = f(buffer |+| r)
        ts foreach ( t => observer.onNext(t))
        buffer = rest
      }
    }
  }

}




