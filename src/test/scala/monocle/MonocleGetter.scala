package shapelens
package monocle
package test

import org.scalatest._

class MonocleGetterSpec extends FunSpec with Matchers {

  describe("Monocle's Getter"){

    it("should be obtained for single fields"){
      Getter[(Int, String, Boolean), Int].get((1,"",true)) shouldBe 1

      Getter[(Int, String, Boolean), String].get((1,"",true)) shouldBe ""

      Getter[(Int, String, Boolean), Boolean].get((1,"",true)) shouldBe true
    }

    it("should be obtained for multiple fields"){
      Getter[(Int, String, Boolean), (Int, String)].get((1,"",true)) shouldBe ((1, ""))

      Getter[(Int, String, Boolean), (String, Int)].get((1,"",true)) shouldBe (("", 1))

      Getter[(Int, String, Boolean), (Int, Boolean)].get((1,"",true)) shouldBe ((1, true))
    }

    it("should be obtained for arbitrary products, not just tuples"){
      case class IS(i: Int, s: String)
      case class SI(s: String, i: Int)
      case class IB(i: Int, b: Boolean)

      Getter[(Int, String, Boolean), IS].get((1,"",true)) shouldBe IS(1, "")

      Getter[(Int, String, Boolean), SI].get((1,"",true)) shouldBe SI("", 1)

      Getter[(Int, String, Boolean), IB].get((1,"",true)) shouldBe IB(1, true)
    }

    it("shouldn't be obtained if no surface view is available"){
      """Getter[(Int, String, Boolean), Char]""" shouldNot compile

      """Getter[((Int, Char), String, Boolean), Char]""" shouldNot compile
    }
  }
}

