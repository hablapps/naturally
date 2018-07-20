package org.hablapps.shapelens

import scalaz._

import shapeless._, ops.hlist._

object SurfaceGetter{

  trait Implicits extends LPI{
    implicit def surfaceGetter_singleField[
        P[_], E2, E1, LE2 <: HList](implicit
        GE2: Generic.Aux[E2, LE2],
        S: Selector[LE2, E1]) =
      λ[Kleisli[P, E1,?] ~> Kleisli[P, E2,?]]{
        _.local(GE2.to _ andThen S.apply)
      }
  }

  trait LPI{
    implicit def surfaceGetter_multipleField[
        P[_], E2, E1, LE2 <: HList, LE1 <: HList](implicit
        GE2: Generic.Aux[E2, LE2],
        GE1: Generic.Aux[E1, LE1],
        S: SelectAll[LE2, LE1]) =
      λ[Kleisli[P, E1,?] ~> Kleisli[P, E2,?]]{
        _.local(GE2.to _ andThen S.apply andThen GE1.from)
      }
  }

  trait Syntax{
    implicit def readerView[P[_], E2, E1, T](
        r: Kleisli[P, E1, T])(implicit
        nat: Kleisli[P, E1, ?] ~> Kleisli[P, E2, ?]): Kleisli[P, E2, T] =
      nat(r)
  }
}
