package com.williamhill.paris.source

import java.net.InetSocketAddress

import akka.actor.ActorSystem
import akka.util.ByteString
import com.williamhill.paris.source.actor.{Subscribe, TcpSourceManager}
import com.williamhill.paris.util.Monoid
import com.williamhill.paris.util.implicits._
import rx.lang.scala.{Subscription, Observable}
import akka.pattern.ask

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration

/**
 * Created by patrick on 28/01/15.
 */
object Source {

  def createTcpSource(address: InetSocketAddress)(implicit system: ActorSystem): Observable[ByteString] = Observable[ByteString] { observer =>
    val manager = system.actorOf(TcpSourceManager.props(address))

    import system.dispatcher

    Observable.create[ByteString] { observer =>
      val fSubscription: Future[Subscription] = (manager ? Subscribe(observer)).map(_.asInstanceOf[Subscription])

      Await.result(fSubscription, Duration.Inf)
    }
  }
  
  def createDivider[R:Monoid, T](source:Observable[R], f: R => (Seq[T], R)): Observable[T] = {
    var buffer: R = implicitly[Monoid[R]].zero

    Observable[R] { observer =>
      source.doOnNext { r =>
        val (rs, rest) = f(buffer |+| r)
        rs foreach (observer.onNext(_))
        buffer = rest
      }
    }
  }

}




