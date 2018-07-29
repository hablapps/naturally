package naturally
package test

import cats.data._, cats._, cats.implicits._
import shapeless.{Id => _, _}

import org.scalatest._

trait AlgebraInstance[Alg[_[_]]]{
  type Out[_]
  val instance: Alg[Out]
}

object AlgebraInstance{

  def apply[Alg[_[_]], P[_]](implicit I: Alg[P]) = new AlgebraInstance[Alg]{
    type Out[t] = P[t]
    val instance = I
  }

  implicit def MR[A] = apply[MonadReader[?[_], A], A => ?]

  implicit def MS[A] = apply[MonadState[?[_], A], State[A,?]]
}

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


// trait AlgebraComposition[L <: HList]{
//   type Out[_]
//   val instance:
// }


object implicitsS{

  implicit def MR[P[_]: Monad, E2, E1](implicit
      S: SurfaceGetter[P, E2, E1]): MonadReader[Kleisli[P, E2, ?], E1] = new MonadReader[Kleisli[P, E2, ?], E1]{

    def ask: Kleisli[P, E2, E1] = S.apply(Kleisli.ask[P, E1])

    // Needs a modifier
    def local[A](f: E1 => E1)(fa: Kleisli[P, E2, A]): Kleisli[P, E2, A] = ???

    def pure[A](a: A) = Monad[Kleisli[P, E2, ?]].pure(a)
    def flatMap[A,B](p: Kleisli[P, E2, A])(f: A => Kleisli[P, E2, B]) =
      MonadReader[Kleisli[P, E2, ?], E2].flatMap(p)(f)

    def tailRecM[A, B](a: A)(f: A => Kleisli[P, E2, Either[A,B]]): Kleisli[P, E2, B] = ???

  }

}
class SpecTest extends FunSpec with Matchers{

  import implicitsS._
  // A[String => ?]
  // implicitly[SurfaceGetter[Id, (Int, String), String]]
  // implicitly[Monad[Id]]
  type C[t] = Kleisli[Id, (Int, String), t]
  // A[Kleisli[Id, (Int, String), ?]]()//(MR[Id, (Int, String), String])

  case class A[P[_]]()(implicit
    R1: MonadReader[P, Int],
    R2: MonadReader[P, String])

  A[C]()//(MR[Id, (Int, String), String])

  Implicitly[scala.reflect.ClassTag[Int]]

  case class B[P[_]](
    R1: MonadReader[P, Int],
    R2: MonadReader[P, String])

  Implicitly[B[C]]

  case class B0[P[_]](
    B: B[P],
    C: scala.reflect.ClassTag[Int])

  Implicitly[B0[C]]

}
