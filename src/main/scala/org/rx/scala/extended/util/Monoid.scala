package com.williamhill.paris.util

import akka.util.ByteString
import com.williamhill.paris.source.Source
import rx.lang.scala.Observable

/**
 * Created by patrick on 28/01/15.
 */
trait Monoid[T] {
  val zero: T
  def combine(t1:T, t2:T): T
}

object Monoid {

  implicit val byteStringMonoid = new Monoid[ByteString] {
    val zero = ByteString()
    def combine(b1:ByteString, b2:ByteString): ByteString = b1 ++ b2
  }

}

package object implicits {
  implicit class MonoidOp[T:Monoid](t:T) {

    val monoid = implicitly[Monoid[T]]

    def |+|(that: T): T = monoid.combine(t, that)

  }

  implicit class DivisibleObservable[R:Monoid](o:Observable[T]) {
    def divide[T](f: R => (Seq[T], R)): Observable[T] = Source.createDivider[R, T](o, f)
  }
}
