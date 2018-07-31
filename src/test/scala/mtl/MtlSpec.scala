package naturally
package mtl
package test

import org.scalatest._

class MtlSpec extends FunSpec with Matchers{

  describe("MonadState"){
    import cats.MonadState, cats.data.State

    it("should be inferred for lenses"){
      implicitly[MonadState[State[(Int, String), ?], Int]]
    }
  }

  describe("MonadReader"){
    import cats.MonadReader, cats.data.Reader

    it("should be inferred from getters"){
      // Alias required for Reader, but not for State ...
      type Program[t] = Reader[(Int, String), t]
      implicitly[MonadReader[Program, Int]]
    }
  }
}
