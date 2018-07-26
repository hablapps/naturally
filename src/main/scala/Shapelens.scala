package org.hablapps.shapelens

import scalaz._
import shapeless._
import monocle.Lens

trait Shapelens[S, Ctx <: HList] {
  type A
  val value: Lens[S, A]
}

object Shapelens {

  type Aux[S, Ctx <: HList, A2] = Shapelens[S, Ctx] { type A = A2 }

  def apply[S, Ctx <: HList](implicit ln: Shapelens[S, Ctx]): Lens[S, ln.A] = 
    ln.value

  def apply[S, Ctx <: HList, A2](ln: Lens[S, A2]): Aux[S, Ctx, A2] =
    new Shapelens[S, Ctx] { type A = A2; val value = ln }

  implicit def base[S]: Aux[S, HNil, S] =
    apply(Lens.id)
  
  implicit def inductive[S, H, T <: HList, A, B](implicit
      hLens: MkFieldLens.Aux[S, H, A],
      tLens: Aux[A, T, B]): Aux[S, H :: T, B] =
    apply(toLens(hLens()) composeLens tLens.value)

  private def toLens[S, A](sln: shapeless.Lens[S, A]): Lens[S, A] =
    Lens(sln.get)(a => s => sln.set(s)(a))
}

