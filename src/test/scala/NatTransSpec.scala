package naturally
package test

import cats.data._, cats._, cats.instances.either._, cats.syntax.flatMap._

import org.scalatest._


class NatTransSpec extends FunSpec with Matchers{

  // A simple IO algebra

  trait IO[P[_]]{
    def read(): P[String]
    def write(msg: String): P[Unit]
  }

  object IO{

    def apply[P[_]](implicit IO: IO[P]) = IO

    implicit class IOLift[P[_]](io: IO[P]){
      def lift[Q[_]](implicit N: NatTrans[P, Q]): IO[Q] =
        new IO[Q]{
          def read() = N.nat(io.read())
          def write(msg: String) = N.nat(io.write(msg))
        }
    }
  }

  // A state instance

  case class IOState(toBeRead: List[String], written: List[String])

  object IOState{

    type Program[T] = StateT[Either[NothingToBeRead,?], IOState, T]

    case class NothingToBeRead()

    implicit val StateMonadIO = new IO[Program]{
      def read() = StateT[Either[NothingToBeRead,?], IOState, String]{
        case IOState(Nil, _) =>
          Left(NothingToBeRead())
        case IOState(head :: tail, written) =>
          Right((IOState(tail, written), head))
      }

      def write(msg: String) = StateT[Either[NothingToBeRead,?], IOState, Unit]{
        iostate => Right((IOState(iostate.toBeRead, msg :: iostate.written), ()))
      }
    }
  }


  // A simple program

  def echo[P[_]: IO: Monad](): P[Unit] =
    IO[P].read() >>= IO[P].write

  // Tests

  describe("Running `echo` with plain program"){
    it("should work"){
      echo[IOState.Program]().run(IOState(List("hi"),List())) shouldBe
        Right((IOState(List(), List("hi")), ()))
    }
  }

  describe("Invoking `echo` with subsuming programs"){

    type Program[t] = StateT[Either[IOState.NothingToBeRead, ?], (IOState, String), t]

    it ("should work"){
      echo[Program]()(IOState.StateMonadIO.lift[Program], implicitly): Program[Unit]
    }
  }

  describe("NatTrans"){

    it("should be found implicitly with type aliases"){
      type KI[t]=Kleisli[Id, Int, t]
      type KIS[t]=Kleisli[Id, (Int, String), t]
      type SIS[t]=StateT[Id, (Int, String), t]
      type SISEval[t]=StateT[Eval, (Int, String), t]

      """implicitly[NatTrans[KI, KIS]]""" should compile
      """implicitly[NatTrans[KI, SIS]]""" should compile
      """implicitly[NatTrans[SIS, SISEval]]""" should compile

// implicitly[NatTrans[KI, SISEval]]

      // implicitly[NatTrans[KI, SISEval]](
      //   NatTrans.composition[KI, SIS, SISEval])
      """implicitly[NatTrans[KI, SISEval]]""" should compile
    }

    ignore("should be found implicitly without type aliases for non-transformers"){
      """implicitly[NatTrans[State[Int,?], State[(Int, String), ?]]]""" should compile
      """implicitly[NatTrans[Reader[Int,?], Reader[(Int, String), ?]]]""" should compile
    }

    ignore("should be found implicitly without type aliases for transformers"){
      """implicitly[NatTrans[StateT[Id, Int, ?], StateT[Id, (Int, String), ?]]]""" should compile
      """implicitly[NatTrans[Kleisli[Id, Int,?], Kleisli[Id, (Int, String), ?]]]""" should compile
    }
  }
}
