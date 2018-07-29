package naturally

import cats.{Applicative, ~>}
import cats.data.{Kleisli, StateT}

trait NatTrans[P[_], Q[_]]{
  val nat: P ~> Q
}

object NatTrans{

  implicit def toCatsNatTrans[P[_], Q[_]](implicit
      N: NatTrans[P, Q]) = N.nat

  implicit def SurfaceGetterNatTrans[P[_], E2, E1](implicit
      S: SurfaceGetter[P, E2, E1]) =
    new NatTrans[Kleisli[P, E1,?], Kleisli[P, E2,?]]{
      val nat = S.apply
    }

  implicit def SurfaceLensNatTrans[P[_], E2, E1](implicit
      S: SurfaceLens[P, E2, E1]) =
    new NatTrans[StateT[P, E1, ?], StateT[P, E2, ?]]{
      val nat = S.apply
    }

  implicit def KleisliToStateTNatTrans[P[_]: Applicative, E1, E2](implicit
      K: KleisliToStateT[P, E2, E1]) =
    new NatTrans[Kleisli[P, E1, ?], StateT[P, E2, ?]]{
      val nat = new (Kleisli[P, E1, ?] ~> StateT[P, E2, ?]){
        def apply[T](p: Kleisli[P, E1, T]) =
          K.apply(p)
      }
    }

  implicit def IdToOptionNatTrans =
    new NatTrans[cats.Id, Option]{
      val nat = λ[cats.Id ~> Option](x => Option(x))
    }
}

trait NatTransLPI{

  implicit def composition[P[_], Q[_], R[_]](implicit
      PQ: NatTrans[P,Q],
      QR: NatTrans[Q,R]) =
    new NatTrans[P, R]{
      val nat: P ~> R = λ[P ~> R]{
        p => QR.nat(PQ.nat(p))
      }
    }
}
