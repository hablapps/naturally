package org.hablapps.shapelens

import scalaz._, Scalaz._
import shapeless._, labelled._
import monocle.Lens

trait ShapeValue[S, Ctx <: HList] {
  type V
  val value: V
}

trait ShapeValueCompanion{

  // def headValue[K, H, T <: HList]: V

  // def tailValue[]

  type Aux[S, Ctx <: HList, V2] = ShapeValue[S, Ctx] { type V = V2 }

  def apply[S, Ctx <: HList](implicit ln: ShapeValue[S, Ctx]): ln.V =
    ln.value

  def apply[S, Ctx <: HList, V2](ln: V2): Aux[S, Ctx, V2] =
    new ShapeValue[S, Ctx] { type V = V2 ; val value = ln }

  // implicit def productHead[K, H, T <: HList]
  //     : Aux[FieldType[K, H] :: T, K :: HNil] =
  //   apply[FieldType[K, H] :: T, K :: HNil](headValue[K, H, T])

    // ShapeValue(
    //   Lens[FieldType[K, H] :: T, H](_.head)(h2 => field[K](h2) :: _.tail))

  // implicit def productTail[Ctx <: HList, J, K: J =:!= ?, H, A, T <: HList](implicit
  //     ln: Aux[T, K :: Ctx, A]): Aux[FieldType[J, H] :: T, K :: Ctx, A] =
  //   ShapeValue(Lens[FieldType[J, H] :: T, A](
  //     l => ln.value.get(l.tail))(
  //     a2 => l => l.head :: ln.value.set(a2)(l.tail)))

  // implicit def productDepth[Ctx <: HList, K, H, A, T <: HList](implicit
  //     ln: Aux[H, Ctx, A]): Aux[FieldType[K, H] :: T, K :: Ctx, A] =
  //   ShapeValue(Lens[FieldType[K, H] :: T, A](
  //     l => ln.value.get(l.head))(
  //     a2 => l => field[K](ln.value.set(a2)(l.head)) :: l.tail))

  // implicit def genericShapeValue[C, R, Ctx <: HList, A](implicit
  //     generic: LabelledGeneric.Aux[C, R],
  //     rInstance: Lazy[Aux[R, Ctx, A]]): Aux[C, Ctx, A] =
  //   rInstance.value.value |> (ln => ShapeValue(Lens[C, A](
  //     c => ln.get(generic.to(c)))(
  //     a2 => c => generic.from(ln.set(a2)(generic.to(c))))))
}

