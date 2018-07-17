package org.hablapps.shapelens

import shapeless._
import monocle.Getter

trait Shapegetter[S, Ctx <: HList] {
  type A
  val value: Getter[S, A]
}

// trait Shapegetter[S, A] {
//   type Ctx <: HList
//   val value: Getter[S, A]
// }

object Shapegetter {

  type Aux[S, Ctx <: HList, A2] = Shapegetter[S, Ctx] { type A = A2 }

  def apply[S, Ctx <: HList](implicit ln: Shapegetter[S, Ctx]): Getter[S, ln.A] =
    ln.value

  implicit def fromShapeLens[S, Ctx <: HList](implicit ln: Shapelens[S, Ctx]): Aux[S, Ctx, ln.A] =
    new Shapegetter[S, Ctx]{
      type A = ln.A
      val value = ln.value.asGetter
    }
}

