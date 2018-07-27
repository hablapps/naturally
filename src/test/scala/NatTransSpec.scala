package shapelens
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


  describe("Running `echo` with plain instance"){
    it("should work"){
      // TBD: problems in inferring Monad with 2.10
      echo[IOState.Program]().run(IOState(List("hi"),List())) shouldBe
        Right((IOState(List(), List("hi")), ()))
    }
  }


}
