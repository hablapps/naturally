package naturally
package util
package test

import shapeless._

import org.scalatest._

class SetAllSpec extends FunSpec with Matchers{

  describe("SetAll"){

    it("should find evidences for HLists"){
      SetAll[HNil, HNil]
      SetAll[Int::String::HNil, Int::HNil]
      SetAll[Int::String::HNil, Int::String::HNil]
    }

    it("should find evidences for Generics"){
      SetAll[(Int, String), Int]
      SetAll[(Int, String, Boolean), (Int, String)]
    }

    it("should work for single fields"){
      SetAll[(Int, String), Int].apply((1,""), 2) shouldBe
        ((2, ""))

      SetAll[(Int, String), String].apply((1,""), "hola") shouldBe
        ((1, "hola"))
    }

    it("should work for multiple fields"){
      SetAll[(Int, String, Boolean), (Int, Boolean)].apply((1,"",false), (2,true)) shouldBe
        ((2, "", true))
    }
  }
}
