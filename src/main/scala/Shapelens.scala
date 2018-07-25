package org.hablapps.shapelens

import scalaz._, Scalaz._
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

  implicit def id[S]: Aux[S, HNil, S] =
    apply(Lens.id)
  
  implicit def base[S, H, A](implicit 
      mkLens: MkFieldLens.Aux[S, H, A]): Aux[S, H :: HNil, A] =
    mkLens() |> (ln => apply(Lens(ln.get)(a => s => ln.set(s)(a))))

  implicit def inductive[S, H, T <: HList, A, B](implicit
      hLens: Aux[S, H :: HNil, A],
      tLens: Aux[A, T, B]): Aux[S, H :: T, B] =
    apply(hLens.value composeLens tLens.value)
}

