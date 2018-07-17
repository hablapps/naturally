package shapelens
package monocle

import _root_.monocle.{Getter => MGetter}
import cats.Id, cats.data.Kleisli

object Getter{

  def apply[E2, E1](implicit G: MGetter[E2, E1]) = G

  trait Implicits{
    implicit def monocleGetter[E2, E1](implicit
      S: SurfaceGetter[E2, E1]): MGetter[E2, E1] =
      MGetter(S[Id](Kleisli.ask).apply)
  }
}
