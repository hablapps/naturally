package naturally
package monocle

import _root_.monocle.{Getter => MGetter}
import cats.Id, cats.data.Kleisli

object Getter{

  def apply[E2, E1](implicit G: MGetter[E2, E1]) = G

  trait Implicits{
    implicit def monocleGetter[P[_], E2, E1](implicit
      S: SurfaceGetter[Id, E2, E1]): MGetter[E2, E1] =
      MGetter(S.apply(Kleisli.ask).apply)
  }
}
