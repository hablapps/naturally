package naturally
package test

import cats.{Id, ~>}, cats.data.{Kleisli, Reader}

import shapeless._, record._
import shapeless.syntax.singleton._

import org.scalatest._

class SurfaceGetterKSpec extends FunSpec with Matchers {

  describe("Getter for particular fields"){

    val rI: Reader[Int, Unit] =
      Reader{ _ => () }

    val rIS: Reader[(Int, String), Unit] =
      Reader{ _ => () }

    it("should work without syntax"){
      SurfaceGetterK[Id,
        (Boolean, Int, String, Char),
        (Int, String),
        Witness.`'_2`.T :: Witness.`'_3`.T :: HNil
      ].apply(rIS):
        Reader[(Boolean, Int, String, Char), Unit]
    }

    it("should work with syntax"){
      (rIS at '_2.narrow :: '_3.narrow :: HNil):
        Reader[(Boolean, Int, String, Char), Unit]
    }

    it("should work for single selections"){

      SurfaceGetterK[Id,
        (Boolean, Int, String, Char),
        Int,
        Witness.`'_2`.T
      ].apply(rI):
        Reader[(Boolean, Int, String, Char), Unit]

      val rBISC_2: Reader[(Boolean, Int, String, Char), Int] =
        Reader[Int, Int](identity).at[(Boolean, Int, String, Char)]('_2)(
          SurfaceGetterK[Id,(Boolean, Int, String, Char),Int,Witness.`'_2`.T])
            // TBD: why is this parameter not found implicitly?

      rBISC_2((true, 1, "", 'a')) shouldBe 1
    }
  }
}

