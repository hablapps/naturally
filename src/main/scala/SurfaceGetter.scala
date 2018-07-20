package org.hablapps.shapelens

import scalaz._

import shapeless._, ops.hlist._

object SurfaceGetter{

  trait Syntax{

    implicit def readerView[E2, E1, T](r: Reader[E1, T])(implicit
        nat: Reader[E1, ?] ~> Reader[E2, ?]): Reader[E2, T] =
      nat(r)
  }

  trait Implicits extends LPI{

    implicit def surfaceGetter_singleField[
        E2, E1, LE2 <: HList](implicit
        GE2: Generic.Aux[E2, LE2],
        S: Selector[LE2, E1]) =
      λ[Reader[E1,?] ~> Reader[E2,?]]{
        _.local((GE2.to _) andThen S.apply) // compose?
      }

  }

  trait LPI{

    implicit def surfaceGetter_multipleField[
        E2, E1, LE2 <: HList, LE1 <: HList](implicit
        GE2: Generic.Aux[E2, LE2],
        GE1: Generic.Aux[E1, LE1],
        S: SelectAll[LE2, LE1]) =
      λ[Reader[E1,?] ~> Reader[E2,?]]{
        _.local((GE2.to _) andThen S.apply andThen GE1.from)
      }
  }
}
