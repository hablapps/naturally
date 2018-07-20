package shapelens

import cats.~>, cats.data.Kleisli

import shapeless._, ops.record._

trait SurfaceGetterK[E2, E1, K]{
  def apply[P[_]]: Kleisli[P, E1, ?] ~> Kleisli[P, E2, ?]
}

object SurfaceGetterK extends SurfaceGetterImplicits{

  trait Syntax{
    implicit class SurfaceGetterKOps[P[_], E1, T](r: Kleisli[P, E1, T]){
      def at[E2](k: Witness)(implicit R: SurfaceGetterK[E2, E1, k.T]) =
        R.apply(r)

      def at[K <: HList, E2](k: K)(implicit R: SurfaceGetterK[E2, E1, K]) =
        R.apply(r)
    }
  }
}

trait SurfaceGetterImplicits extends SurfaceGetterImplicitsLPI{

  def apply[E1, E2, K](implicit R: SurfaceGetterK[E1, E2, K]) = R

  implicit def surfaceGetterK_singleField[
      E2, E1, K, LE2 <: HList](implicit
      GE2: LabelledGeneric.Aux[E2, LE2],
      S: Selector.Aux[LE2, K, E1]) =
    new SurfaceGetterK[E2, E1, K]{
      def apply[P[_]] = λ[Kleisli[P, E1, ?] ~> Kleisli[P, E2, ?]]{
        _.local((GE2.to _) andThen S.apply)
      }
    }
}

trait SurfaceGetterImplicitsLPI{

  implicit def surfaceGetterK_multipleField[
      E2, E1, K <: HList, LE2 <: HList, LE1 <: HList](implicit
      GE2: LabelledGeneric.Aux[E2, LE2],
      GE1: Generic.Aux[E1, LE1],
      S: SelectAll.Aux[LE2, K, LE1]) =
    new SurfaceGetterK[E2, E1, K]{
      def apply[P[_]] = λ[Kleisli[P, E1, ?] ~> Kleisli[P, E2, ?]]{
        _.local((GE2.to _) andThen S.apply andThen GE1.from)
      }
    }
}

