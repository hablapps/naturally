package shapelens

import cats._, cats.data._, cats.implicits._

import shapeless._, ops.hlist._
import util.SetAll

trait SurfaceLens[P[_], E2, E1]{
  val apply: StateT[P, E1, ?] ~> StateT[P, E2, ?]
}

object SurfaceLens extends SurfaceLensImplicits{

  trait Syntax{
    implicit def stateView[P[_], E2, E1, T](
        r: StateT[P, E1, T])(implicit
        S: SurfaceLens[P, E2, E1]): StateT[P, E2, T] =
      S.apply(r)
  }
}

trait SurfaceLensImplicits extends SurfaceLensLPI{

  def apply[P[_], E2, E1](implicit S: SurfaceLens[P, E2, E1]) = S

  implicit def surfaceLens_singleField[
      P[_]: Monad, E2, E1, LE2 <: HList](implicit
      GE2: Generic.Aux[E2, LE2],
      Mod: Modifier.Aux[LE2, E1, E1, (E1, LE2)]) = new SurfaceLens[P, E2, E1]{
    val apply = new (StateT[P, E1,?] ~> StateT[P, E2,?]){
      def apply[T](stateE1: StateT[P, E1, T]) = StateT{ e2 =>
        val (get, _) = Mod(GE2.to(e2), identity[E1])
        stateE1.run(get) map {
          case (ne1, out) =>
            val (_, set) = Mod(GE2.to(e2), _ => ne1)
            (GE2.from(set), out)
        }
      }
    }
  }
}

trait SurfaceLensLPI{

  implicit def surfaceLens_multipleField[
      P[_]: Monad, E2, E1, LE2 <: HList, LE1 <: HList](implicit
      GE2: Generic.Aux[E2, LE2],
      GE1: Generic.Aux[E1, LE1],
      Get: SelectAll[LE2, LE1],
      Set: SetAll[LE2, LE1]) = new SurfaceLens[P, E2, E1]{
    val apply = new (StateT[P, E1,?] ~> StateT[P, E2,?]){
      def apply[T](stateE1: StateT[P, E1, T]) = StateT{ e2 =>
        stateE1.run(GE1.from(Get(GE2.to(e2)))) map {
          case (e1, out) =>
            (GE2.from(Set(GE2.to(e2), GE1.to(e1))), out)
        }
      }
    }
  }
}

