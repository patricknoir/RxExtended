package org.rx.scala.extended.util

/**
 * Created by patrick on 28/01/15.
 */
trait State[S, A] {

  def apply(s: S): (A, S)
  def map[B](f: A => B): State[S, B] = State[S, B] { s =>
    val (a, s2) = this(s)
    (f(a), s2)
  }
  def flatMap[B](f: A => State[S, B]): State[S, B] = State[S, B] { s =>
    val (a, s2) = this(s)
    f(a)(s2)
  }

}

object State {
  def apply[S, A](r: S => (A, S)): State[S, A] = new State[S, A] {
    def apply(s: S) = r(s)
  }
}
