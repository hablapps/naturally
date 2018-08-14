package naturally

import cats.~>, cats.data.Kleisli

import shapeless._, ops.record._

trait SurfaceGetterK[P[_], E2, E1, K] extends Serializable{
  val apply: Kleisli[P, E1, ?] ~> Kleisli[P, E2, ?]
}

object SurfaceGetterK extends SurfaceGetterKImplicits{

  trait Syntax{
    implicit class SurfaceGetterKOps[P[_], E1, T](r: Kleisli[P, E1, T]){
      def at[E2](k: Witness)(implicit R: SurfaceGetterK[P, E2, E1, k.T]) =
        R.apply(r)

      def at[K <: HList, E2](k: K)(implicit R: SurfaceGetterK[P, E2, E1, K]) =
        R.apply(r)
    }
  }
}

trait SurfaceGetterKImplicits extends SurfaceGetterKImplicitsLPI{

  def apply[P[_], E1, E2, K](implicit R: SurfaceGetterK[P, E1, E2, K]) = R

  implicit def surfaceGetterK_singleField[
      P[_], E2, E1, K, LE2 <: HList](implicit
      GE2: LabelledGeneric.Aux[E2, LE2],
      S: Selector.Aux[LE2, K, E1]) =
    new SurfaceGetterK[P, E2, E1, K]{
      val apply = λ[Kleisli[P, E1, ?] ~> Kleisli[P, E2, ?]]{
        _.local((GE2.to _) andThen S.apply)
      }
    }
}

trait SurfaceGetterKImplicitsLPI{

  implicit def surfaceGetterK_multipleField[
      P[_], E2, E1, K <: HList, LE2 <: HList, LE1 <: HList](implicit
      GE2: LabelledGeneric.Aux[E2, LE2],
      GE1: Generic.Aux[E1, LE1],
      S: SelectAll.Aux[LE2, K, LE1]) =
    new SurfaceGetterK[P, E2, E1, K]{
      val apply = λ[Kleisli[P, E1, ?] ~> Kleisli[P, E2, ?]]{
        _.local((GE2.to _) andThen S.apply andThen GE1.from)
      }
    }
}

