package shapelens

import cats._, cats.data._

import shapeless._, ops.hlist._

trait SurfaceGetter[E2, E1]{
  def apply[P[_]]: Kleisli[P, E1, ?] ~> Kleisli[P, E2, ?]
}

object SurfaceGetter extends SurfaceGetterImplicits{

  trait Syntax{
    implicit def readerView[P[_], E2, E1, T](
        r: Kleisli[P, E1, T])(implicit
        S: SurfaceGetter[E2, E1]): Kleisli[P, E2, T] =
      S.apply(r)
  }
}

trait SurfaceGetterImplicits extends SurfaceGetterLPI{

  implicit def surfaceGetter_singleField[
      E2, E1, LE2 <: HList](implicit
      GE2: Generic.Aux[E2, LE2],
      S: Selector[LE2, E1]) = new SurfaceGetter[E2, E1]{
    def apply[P[_]] = λ[Kleisli[P, E1,?] ~> Kleisli[P, E2,?]]{
      _.local(GE2.to _ andThen S.apply)
    }
  }
}

trait SurfaceGetterLPI{
  implicit def surfaceGetter_multipleField[
      E2, E1, LE2 <: HList, LE1 <: HList](implicit
      GE2: Generic.Aux[E2, LE2],
      GE1: Generic.Aux[E1, LE1],
      S: SelectAll[LE2, LE1]) = new SurfaceGetter[E2, E1]{
    def apply[P[_]] = λ[Kleisli[P, E1,?] ~> Kleisli[P, E2,?]]{
      _.local(GE2.to _ andThen S.apply andThen GE1.from)
    }
  }
}

