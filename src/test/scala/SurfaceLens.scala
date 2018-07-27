package naturally
package test

import cats.data.State, cats.{Id, Eval}
import org.scalatest._

class SurfaceLens extends FunSpec with Matchers{

  describe("Surface lenses"){

    it("should be obtained implicitly"){
      SurfaceLens[Id, (Int, String), Int]
      SurfaceLens[Id, (Int, String), String]
      SurfaceLens[Id, (Int, String, Boolean), (Int, String)]
      SurfaceLens[Id, (Int, String, Boolean), (Boolean, String)]
      SurfaceLens[Id, ((Boolean, String), String, Boolean), (Boolean, String)]
    }

    it("should work for single fields"){
      SurfaceLens[Eval, (Int, String), Int].apply
        .apply(State.get[Int])
        .runA((1,""))
        .value shouldBe 1
    }

    it("should work for multiple fields"){
      SurfaceLens[Eval, (Int, String, Boolean), (Int, String)].apply
        .apply(State.get[(Int, String)])
        .runA((1, "", false))
        .value shouldBe ((1, ""))
    }

    it("should give preference to single fields"){
      SurfaceLens[Eval, ((Boolean, String), String, Boolean), (Boolean, String)].apply
        .apply(State.get[(Boolean, String)])
        .runA(((false, ""), "a", true))
        .value shouldBe ((false, ""))
    }

    it("should allow us to lift state computations"){

      State.get[(Boolean, String)]:
        State[(Boolean, String), (Boolean, String)]

      State.get[(Boolean, String)]:
        State[(Boolean, String, Int), (Boolean, String)]

      State.get[(String, Boolean)]:
        State[(Boolean, String, Int), (String, Boolean)]
    }
  }
}
