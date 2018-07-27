package shapelens

import cats.{~>, Id, Eval}
import cats.data.{Reader => CReader, State}

trait NatTrans[P[_], Q[_]]{
  val nat: P ~> Q
}

object NatTrans{

  implicit def toCatsNatTrans[P[_], Q[_]](implicit
      N: NatTrans[P, Q]) = N.nat

  implicit def SurfaceGetterNatTrans[E2, E1](implicit
      S: SurfaceGetter[Id, E2, E1]) =
    new NatTrans[CReader[E1,?], CReader[E2,?]]{
      val nat = S.apply
    }

  implicit def SurfaceLensNatTrans[E2, E1](implicit
      S: SurfaceLens[Eval, E2, E1]) =
    new NatTrans[State[E1,?], State[E2,?]]{
      val nat = S.apply
    }

  implicit def KleisliToStateTNatTrans[E1, E2](implicit
      K: KleisliToStateT[Id, E2, E1]) =
    new NatTrans[CReader[E1,?], State[E2,?]]{
      val nat = λ[CReader[E1,?] ~> State[E2,?]]{
        p => K.apply(p).transformF(cats.Eval.now)
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
