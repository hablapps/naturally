package naturally

import cats.{Applicative, ~>, Id, Eval}
import cats.data.{Kleisli, State}

trait NatTrans[P[_], Q[_]]{
  val nat: P ~> Q
}

object NatTrans extends NatTransLPI{

  def apply[P[_], Q[_]](_nat: P ~> Q) = new NatTrans[P, Q]{
    val nat = _nat
  }

  implicit def SurfaceGetterNatTrans[E2, E1](implicit
      S: SurfaceGetter[Id, E2, E1]) =
    NatTrans(S.apply)

  implicit def SurfaceLensNatTrans[E2, E1](implicit
      S: SurfaceLens[Eval, E2, E1]) =
    NatTrans(S.apply)

  implicit def KleisliToStateTNatTrans[E1, E2](implicit
      K: KleisliToStateT[Id, E2, E1]) =
    NatTrans(
      new (Kleisli[Id, E1, ?] ~> State[E2, ?]){
        def apply[T](p: Kleisli[Id, E1, T]) =
          K.apply(p).transformF(Eval.now)
      }
    )

  implicit def IdToOptionNatTrans =
    NatTrans(λ[Id ~> Option](x => Option(x)))

  implicit def IdToEvalNatTrans =
    NatTrans(λ[Id ~> Eval](x => Eval.now(x)))

  // implicit def StateTHoistNatTrans[P[_]: FlatMap, Q[_]: Applicative, E](implicit
  //     K: NatTrans[P, Q]) =
  //   NatTrans(
  //     new (StateT[P, E, ?] ~> StateT[Q, E, ?]){
  //       def apply[T](p: StateT[P, E, T]) =
  //         p.transformF[Q, T](K.nat.apply _)
  //     }
  //   )
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
