package naturally

import scalaz._, Scalaz._
import shapeless._

trait DeepLens[S, Ctx <: HList] {
  type A
  def apply(): Lens[S, A]
}

object DeepLens {

  type Aux[S, Ctx <: HList, A2] = DeepLens[S, Ctx] { type A = A2 }

  def apply[S, Ctx <: HList](implicit ln: DeepLens[S, Ctx]): Lens[S, ln.A] =
    ln()

  def apply[S, Ctx <: HList, A2](ln: Lens[S, A2]): Aux[S, Ctx, A2] =
    new DeepLens[S, Ctx] { type A = A2; def apply() = ln }

  implicit def base[S]: Aux[S, HNil, S] =
    apply(NaturalTransformation.refl[State[S, ?]])

  implicit def inductive[S, H, T <: HList, A, B](implicit
      hln: MkFieldLens.Aux[S, H, A],
      tln: Aux[A, T, B]): Aux[S, H :: T, B] =
    apply(λ[State[A, ?] ~> State[S, ?]] { sa => 
      State(s => sa(hln().get(s)).leftMap(hln().set(s)))
    }.compose[State[B, ?]](tln()))
}

