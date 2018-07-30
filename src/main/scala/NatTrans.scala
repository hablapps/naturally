package naturally

import cats.{Applicative, ~>}
import cats.data.{Kleisli, StateT}

trait NatTrans[P[_], Q[_]]{
  val nat: P ~> Q
}

object NatTrans extends NatTransLPI{

  def apply[P[_], Q[_]](_nat: P ~> Q) = new NatTrans[P, Q]{
    val nat = _nat
  }

  implicit def SurfaceGetterNatTrans[P[_], E2, E1](implicit
      S: SurfaceGetter[P, E2, E1]) =
    apply(S.apply)

  implicit def SurfaceLensNatTrans[P[_], E2, E1](implicit
      S: SurfaceLens[P, E2, E1]) =
    apply(S.apply)

  implicit def KleisliToStateTNatTrans[P[_]: Applicative, E1, E2](implicit
      K: KleisliToStateT[P, E2, E1]) =
    apply(
      new (Kleisli[P, E1, ?] ~> StateT[P, E2, ?]){
        def apply[T](p: Kleisli[P, E1, T]) =
          K.apply(p)
      }
    )

  implicit def IdToOptionNatTrans =
    apply(λ[cats.Id ~> Option](x => Option(x)))

  implicit def IdToEvalNatTrans =
    apply(λ[cats.Id ~> cats.Eval](x => cats.Eval.now(x)))

  implicit def StateTHoistNatTrans[P[_]: cats.FlatMap, Q[_]: Applicative, E](implicit
      K: NatTrans[P, Q]) =
    apply(
      new (StateT[P, E, ?] ~> StateT[Q, E, ?]){
        def apply[T](p: StateT[P, E, T]) =
          p.transformF[Q, T](K.nat.apply _)
      }
    )
}

trait NatTransLPI{

  trait NaturalComposition[P[_], R[_]]{
    type Q[_]
    val PQ: NatTrans[P, Q]
    val QR: NatTrans[Q, R]
  }

  object NaturalComposition{
    implicit def instance[P[_], Q2[_], R[_]](implicit
        N1: shapeless.Lazy[NatTrans[P, Q2]],
        N2: NatTrans[Q2, R]) = new NaturalComposition[P, R]{
      type Q[t] = Q2[t]
      val PQ = N1.value
      val QR = N2
    }
  }

  implicit def composition[P[_], Q[_], R[_]](implicit
      C: NaturalComposition[P, R]) =
    new NatTrans[P, R]{
      val nat = λ[P ~> R]{
        p => C.QR.nat(C.PQ.nat( p))
      }
    }
}
