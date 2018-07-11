package org.hablapps.shapelens

import scalaz._, Scalaz._
import shapeless._, labelled._
import monocle.Lens

trait Shapelens[Ctx <: HList, S, A] {
  val value: FieldType[Ctx, Lens[S, A]]
}

object Shapelens {

  def apply[Ctx <: HList, S, A](implicit 
      ln: Shapelens[Ctx, S, A]): FieldType[Ctx, Lens[S, A]] = 
    ln.value

  def apply[Ctx <: HList, S, A](
      ft: FieldType[Ctx, Lens[S, A]]): Shapelens[Ctx, S, A] =
    new Shapelens[Ctx, S, A] { val value = ft }

  implicit def productHead[K, H, T <: HList]
      : Shapelens[K :: HNil, FieldType[K, H] :: T, H] =
    Shapelens(field[K :: HNil](Lens[FieldType[K, H] :: T, H](
      _.head)(h2 => field[K](h2) :: _.tail)))

  implicit def productTail[Ctx <: HList, K, H, A, T <: HList](implicit
      ev: Shapelens[K :: Ctx, T, A]): Shapelens[K :: Ctx, H :: T, A] =
    Shapelens(field[K :: Ctx](Lens[H :: T, A](
      l => ev.value.get(l.tail))(
      a2 => l => l.head :: ev.value.set(a2)(l.tail))))

  implicit def productDepth[Ctx <: HList, J, K, H, A, T <: HList](implicit
      ev: Shapelens[Ctx, H, A])
      : Shapelens[K :: Ctx, FieldType[J, H] :: T, A] =
    ev.value |> (ln =>
      Shapelens(field[K :: Ctx](Lens[FieldType[J, H] :: T, A](
        l => ln.get(l.head))(
        a2 => l => field[J](ln.set(a2)(l.head)) :: l.tail))))

  implicit def genericShapelens[C, R, Ctx <: HList, A](implicit
      generic: LabelledGeneric.Aux[C, R],
      rInstance: Lazy[Shapelens[Ctx, R, A]]): Shapelens[Ctx, C, A] =
    rInstance.value.value |> (ln => Shapelens(field[Ctx](Lens[C, A](
      c => ln.get(generic.to(c)))(
      a2 => c => generic.from(ln.set(a2)(generic.to(c)))))))
}

