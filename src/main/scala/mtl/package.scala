package naturally

import cats._, cats.data._

package object mtl{

  implicit def MR[P[_]: Monad, E2, E1](implicit
      S: SurfaceGetter[P, E2, E1]) =
    new MonadReader[Kleisli[P, E2, ?], E1]{

      def ask: Kleisli[P, E2, E1] = S.apply(Kleisli.ask[P, E1])

      // Needs a modifier
      def local[A](f: E1 => E1)(fa: Kleisli[P, E2, A]): Kleisli[P, E2, A] = ???

      def pure[A](a: A) = Monad[Kleisli[P, E2, ?]].pure(a)

      def flatMap[A,B](p: Kleisli[P, E2, A])(f: A => Kleisli[P, E2, B]) =
        MonadReader[Kleisli[P, E2, ?], E2].flatMap(p)(f)

      def tailRecM[A, B](a: A)(f: A => Kleisli[P, E2, Either[A,B]]): Kleisli[P, E2, B] = ???

    }
}
