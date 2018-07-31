package naturally
package mtl

import cats._, cats.data._

trait MonadStateInstances{

  implicit def MS[F[_]: Monad, E2, E1](implicit
      S: SurfaceLens[F, E2, E1]) =
    new MonadState[StateT[F, E2, ?], E1]{

      def get: StateT[F, E2, E1] = S.apply(StateT.get[F, E1])
      def set(e1: E1): StateT[F, E2, Unit] = S.apply(StateT.set[F, E1](e1))

      // Needs a modifier
      def local[A](f: E1 => E1)(fa: StateT[F, E2, A]): StateT[F, E2, A] = ???

      def pure[A](a: A) = Monad[StateT[F, E2, ?]].pure(a)

      def flatMap[A,B](p: StateT[F, E2, A])(f: A => StateT[F, E2, B]) =
        MonadState[StateT[F, E2, ?], E2].flatMap(p)(f)

      def tailRecM[A, B](a: A)(f: A => StateT[F, E2, Either[A,B]]): StateT[F, E2, B] = ???

    }
}
