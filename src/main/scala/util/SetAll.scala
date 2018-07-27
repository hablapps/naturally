package naturally
package util

import shapeless._
import shapeless.ops.hlist.Modifier

trait SetAll[L, S] extends DepFn2[L, S] with Serializable {
  type Out = L
}

object SetAll extends SetAllLPI{

  def apply[L, S](implicit P: SetAll[L, S]): SetAll[L, S] = P

  implicit def hnilSetAll[L <: HList]: SetAll[L, HNil] =
    new SetAll[L, HNil] {
      def apply(l: L, nil: HNil): L = l
    }

  implicit def hconsSetAll[L <: HList, H, S <: HList](implicit
      M: Modifier.Aux[L, H, H, (H,L)],
      P: SetAll[L, S]): SetAll[L, H :: S] =
    new SetAll[L, H :: S] {
      def apply(l: L, hs: H :: S): L =
        M(P(l, hs.tail), _ => hs.head)._2
    }

  implicit def genericSingleSetAll[P1, P2, L1 <: HList](implicit
      G1: Generic.Aux[P1, L1],
      SA: Lazy[SetAll[L1, P2 :: HNil]]): SetAll[P1, P2] =
    new SetAll[P1, P2]{
      def apply(p1: P1, p2: P2): P1 =
        G1.from(SA.value(G1.to(p1), p2 :: HNil))
    }
}

trait SetAllLPI{

  implicit def genericSetAll[P1, P2, L1 <: HList, L2 <: HList](implicit
      G1: Generic.Aux[P1, L1],
      G2: Generic.Aux[P2, L2],
      SA: Lazy[SetAll[L1, L2]]): SetAll[P1, P2] =
    new SetAll[P1, P2]{
      def apply(p1: P1, p2: P2): P1 =
        G1.from(SA.value(G1.to(p1), G2.to(p2)))
    }
}
