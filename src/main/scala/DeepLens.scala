package shapelens

import scalaz._, Scalaz._
import shapeless._, labelled._
import monocle.Lens

trait DeepLens[S, Ctx <: HList] {
  type A
  val value: Lens[S, A]
}

object DeepLens {

  type Aux[S, Ctx <: HList, A2] = DeepLens[S, Ctx] { type A = A2 }

  def apply[S, Ctx <: HList](implicit ln: DeepLens[S, Ctx]): Lens[S, ln.A] =
    ln.value

  def apply[S, Ctx <: HList, A2](ln: Lens[S, A2]): Aux[S, Ctx, A2] =
    new DeepLens[S, Ctx] { type A = A2; val value = ln }

  implicit def productHead[K, H, T <: HList]
      : Aux[FieldType[K, H] :: T, K :: HNil, H] =
    DeepLens(
      Lens[FieldType[K, H] :: T, H](_.head)(h2 => field[K](h2) :: _.tail))

  implicit def productTail[Ctx <: HList, J, K: J =:!= ?, H, A, T <: HList](implicit
      ln: Aux[T, K :: Ctx, A]): Aux[FieldType[J, H] :: T, K :: Ctx, A] =
    DeepLens(Lens[FieldType[J, H] :: T, A](
      l => ln.value.get(l.tail))(
      a2 => l => l.head :: ln.value.set(a2)(l.tail)))

  implicit def productDepth[Ctx <: HList, K, H, A, T <: HList](implicit
      ln: Aux[H, Ctx, A]): Aux[FieldType[K, H] :: T, K :: Ctx, A] =
    DeepLens(Lens[FieldType[K, H] :: T, A](
      l => ln.value.get(l.head))(
      a2 => l => field[K](ln.value.set(a2)(l.head)) :: l.tail))

  implicit def genericDeepLens[C, R, Ctx <: HList, A](implicit
      generic: LabelledGeneric.Aux[C, R],
      rInstance: Lazy[Aux[R, Ctx, A]]): Aux[C, Ctx, A] =
    rInstance.value.value |> (ln => DeepLens(Lens[C, A](
      c => ln.get(generic.to(c)))(
      a2 => c => generic.from(ln.set(a2)(generic.to(c))))))
}

