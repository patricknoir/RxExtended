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

  implicit def seqMonoid[T] = new Monoid[Seq[T]] {
    val zero = Nil
    def combine(s1: Seq[T], s2: Seq[T]): Seq[T] = s1 ++ s2
  }

}

package object implicits {
  implicit class MonoidOp[T:Monoid](t:T) {

    val monoid = implicitly[Monoid[T]]

    def |+|(that: T): T = monoid.combine(t, that)

  }

  implicit class BufferedObservable[R:Monoid](o:Observable[R]) {
    def divide[T](f: R => (Seq[T], R)): Observable[T] = Source.storeMap[R, T](o, f)
  }
}
