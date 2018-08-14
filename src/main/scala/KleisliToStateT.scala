package naturally

import cats._, cats.data._, cats.implicits._

trait KleisliToStateT[P[_], E2, E1] extends Serializable{
  val apply: Kleisli[P, E1, ?] ~> StateT[P, E2, ?]
}

object KleisliToStateT{

  def apply[P[_], E2, E1](implicit S: KleisliToStateT[P, E2, E1]) = S

  implicit def kleisliToStateT[P[_]: Applicative, E2, E1](implicit
      G: SurfaceGetter[P, E2, E1]) = new KleisliToStateT[P, E2, E1]{
    val apply = λ[Kleisli[P, E1,?] ~> StateT[P, E2,?]]{
      from => StateT{ e2 => G.apply(from)(e2) map ((e2, _)) }
    }
  }
}
