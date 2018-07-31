package naturally
package util

import shapeless._

trait Implicitly[C]{
  val value: C
}

object Implicitly{

  def apply[C](implicit I: Implicitly[C]): C = I.value

  implicit def implicitlySearch[C](implicit c: C) = new Implicitly[C]{
    val value = c
  }

  implicit def implicitsHNil = new Implicitly[HNil]{
    val value = HNil
  }

  implicit def implicitsHConsImplicitly[H, T <: HList](implicit
      h: Lazy[Implicitly[H]],
      I: Implicitly[T]) = new Implicitly[H :: T]{
    val value = h.value.value :: I.value
  }

  implicit def implicitGen[C, L <: HList](implicit
      G: Generic.Aux[C, L],
      I: Lazy[Implicitly[L]]) = new Implicitly[C]{
    val value = G.from(I.value.value)
  }
}

