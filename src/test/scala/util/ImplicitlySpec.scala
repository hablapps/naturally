package naturally
package util

import org.scalatest._
import cats._, cats.data._
import mtl._

class ImplicitlySpec extends FunSpec with Matchers{

  type Program[t] = Kleisli[Id, (Int, String), t]

  describe("Case classes with implicits"){

    case class A[P[_]]()(implicit
      R1: MonadReader[P, Int],
      R2: MonadReader[P, String])

    it("should be instantiated implicitly"){

      // Type inference problems
      // A[Kleisli[Id, (Int, String), ?]]()

      """A[Program]""" should compile
    }
  }

  describe("Case classes without implicits"){

    case class B[P[_]](
      R1: MonadReader[P, Int],
      R2: MonadReader[P, String])

    it("should be instantiated Implicitly"){

      """Implicitly[B[Program]]""" should compile
    }

    describe("And with nested case classes"){

      case class B0[P[_]](
        B: B[P],
        C: scala.reflect.ClassTag[Int])

      it("should be instantiated Implicitly"){
        """Implicitly[B0[Program]]""" should compile
      }
    }
  }

  describe("Case classes with some parameters implicits"){

    case class B[P[_]: cats.Monad](
      R1: MonadReader[P, Int],
      R2: MonadReader[P, String])

    it("should be instantiated Implicitly"){

      """Implicitly[B[Program]]""" should compile
    }
  }

  describe("Classes with implicit values in scope"){

    it("should be summoned with Implicitly"){
      """Implicitly[scala.reflect.ClassTag[Int]]""" should compile
    }
  }
}
