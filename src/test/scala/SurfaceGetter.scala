package shapelens
package test

import cats.data.{Kleisli, Reader}

import shapeless._

import org.scalatest._

class SurfaceGetterSpec extends FunSpec with Matchers {

  describe("Getter extractor from type info"){

    describe("When there is no ambiguity"){

      it("should work for single fields"){
        Reader[Int,Unit](_ => ()):
          Reader[(Int, String, Boolean), Unit]

        Reader[String, Unit](_ => ()):
          Reader[(Int, String, Boolean), Unit]

        Reader[Boolean, Unit](_ => ()):
          Reader[(Int, String, Boolean), Unit]
        }

      it("should work for two consecutive fields"){
        Reader[(Int,String), Unit](_ => ()):
          Reader[(Int, String, Boolean), Unit]

        Reader[(String, Boolean), Unit](_ => ()):
          Reader[(Int, String, Boolean), Unit]

        Reader[(Int, Boolean), Unit](_ => ()):
          Reader[(Int, String, Boolean), Unit]
        }

      it("should work for non-consecutive fields"){
        Reader[(Int,Boolean), Unit](_ => ()):
          Reader[(Int, String, Boolean, Char), Unit]

        Reader[(Int,Char), Unit](_ => ()):
          Reader[(Int, String, Boolean, Char), Unit]

        Reader[(String,Char), Unit](_ => ()):
          Reader[(Int, String, Boolean, Char), Unit]
        }

      it("should work for reverse fields"){
        Reader[(Boolean, Int), Unit](_ => ()):
          Reader[(Int, String, Boolean, Char), Unit]

        Reader[(Char, Int), Unit](_ => ()):
          Reader[(Int, String, Boolean, Char), Unit]

        Reader[(String,Int), Unit](_ => ()):
          Reader[(Int, String, Boolean, Char), Unit]
        }
    }

    describe("In ambigous settings"){
      it("should work for the first matching (single fields)"){
        val r: Reader[(Int, Int), Int] =
          Kleisli.ask[Id, Int]

        r((1,2)) shouldBe 1
      }

      it("should work for the first matching (multiple fields)"){
        val r: Reader[(Int, String, Int, String), (Int, String)] =
          Kleisli.ask[Id, (Int, String)]

        r((1, "a", 2, "b")) shouldBe ((1,"a"))
      }
    }

    describe("When both single and multiple fields match"){

      it("should have precedence the single field selector"){
        val r1: Reader[((Int, String), Int, String), (Int, String)] =
          Kleisli.ask[Id, (Int, String)]

        r1(((1,"a"),2,"b")) shouldBe ((1,"a"))

        val r2: Reader[(Int, String, (Int, String)), (Int, String)] =
          Kleisli.ask[Id, (Int, String)]

        r2((2, "b", (1,"a"))) shouldBe ((1,"a"))
      }
    }

    describe("When there is no possible selector"){

      it("shouldn't work for non-existing fields"){
        """val r: Reader[(Int, String), Unit] =
             Reader[Boolean, Unit](_ => ())""" shouldNot compile
      }

      it("shouldn't work for nested existing fields"){
        """val r: Reader[((Boolean, Int), String), Unit] =
             Reader[Boolean, Unit](_ => ())""" shouldNot compile
      }
    }
  }
}

