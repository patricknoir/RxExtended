package org.rx.scala.extended.util

import org.rx.scala.extended.source.Source
import rx.lang.scala.Observable

/**
 * Created by patrick on 28/01/15.
 * The Monoid trait represents the Mathematical Monoid definition,
 * Is a collection of object with 2 properties:
 *  - Have an identity element which combined with any other element X will always produce X as result
 *    id combined X = X combined id = X
 *  - Componing two element of type T with T:Monoid will produce a new element of type T
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

  implicit val strMonoid = new Monoid[String] {
    val zero = ""
    def combine(s1: String, s2: String) = s1 concat s2
  }

}

package object implicits {

  /**
   * Implicit converter to use the Monoid.combine as infix operator
   */
  implicit class MonoidOp[T:Monoid](t:T) {

    val monoid = implicitly[Monoid[T]]

    def |+|(that: T): T = monoid.combine(t, that)

  }

  implicit class BufferedObservable[R:Monoid](o:Observable[R]) {
    def storeMap[T](f: R => (Seq[T], R)): Observable[T] = Source.storeMap[R, T](o, f)
  }

}
