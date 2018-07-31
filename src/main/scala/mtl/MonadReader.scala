package naturally
package mtl

import cats._, cats.data._

trait MonadReaderInstances{

  implicit def MR_Kleisli[F[_]: Monad, E2, E1](implicit
      S: SurfaceGetter[F, E2, E1]) =
    new MonadReader[Kleisli[F, E2, ?], E1]{

      def ask: Kleisli[F, E2, E1] = S.apply(Kleisli.ask[F, E1])

      // Needs a modifier
      def local[A](f: E1 => E1)(fa: Kleisli[F, E2, A]): Kleisli[F, E2, A] = ???

      def pure[A](a: A) = Monad[Kleisli[F, E2, ?]].pure(a)

      def flatMap[A,B](p: Kleisli[F, E2, A])(f: A => Kleisli[F, E2, B]) =
        MonadReader[Kleisli[F, E2, ?], E2].flatMap(p)(f)

      def tailRecM[A, B](a: A)(f: A => Kleisli[F, E2, Either[A,B]]): Kleisli[F, E2, B] = ???

    }

  implicit def MR_State[P[_]: Monad, E2, E1](implicit
      S: SurfaceLens[P, E2, E1]) =
    new MonadReader[StateT[P, E2, ?], E1]{

      def ask: StateT[P, E2, E1] = S.apply(StateT.get[P, E1])

      // Needs a modifier
      def local[A](f: E1 => E1)(fa: StateT[P, E2, A]): StateT[P, E2, A] =
        ???

      def pure[A](a: A) = Monad[StateT[P, E2, ?]].pure(a)

      def flatMap[A,B](p: StateT[P, E2, A])(f: A => StateT[P, E2, B]) =
        MonadState[StateT[P, E2, ?], E2].flatMap(p)(f)

      def tailRecM[A, B](a: A)(f: A => StateT[P, E2, Either[A,B]]): StateT[P, E2, B] =
        ???

    }
}
