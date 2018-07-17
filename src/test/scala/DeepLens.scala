package shapelens
package test

import org.scalatest._
import shapeless._

class DeepLensSpec extends FlatSpec with Matchers {

  case class City(population: Int, name: String, university: University)
  case class University(name: String, math: Department)
  case class Department(budget: Int)

  val math = Department(50000)
  val urjc = University("urjc", math)
  val uc3m = University("uc3m", math)
  val mostoles = City(200000, "mostoles", urjc)
  val leganes  = City(150000, "leganes",  uc3m)

  "DeepLens" should "generate a lens for a particular field" in {
    val population = DeepLens[City, population :: HNil]
    population.get(mostoles) shouldBe 200000
    population.modify(_ * 2)(mostoles) shouldBe mostoles.copy(population = 400000)
  }

  it should "generate a lens for a nested field" in {
    val budget = DeepLens[University, math :: budget :: HNil]
    budget.get(urjc) shouldBe math.budget
    budget.set(0)(urjc) shouldBe urjc.copy(math = Department(0))
  }

  it should "disambiguate conflicts using path" in {
    val cityName = DeepLens[City, name :: HNil]
    val univName = DeepLens[City, university :: name :: HNil]
    cityName.get(mostoles) shouldBe mostoles.name
    univName.get(mostoles) shouldBe mostoles.university.name
  }

  it should "generate the identity lens" in {
    val city = Shapelens[City, HNil]
    city.get(mostoles) shouldBe mostoles
    city.set(leganes)(mostoles) shouldBe leganes
  }

  "Shapelens[City, budget :: HNil]" shouldNot compile

  // XXX: is it possible to integrate `LabelledGeneric` with literal types? If
  // so, we could avoid these aliases and use `'population` directly!
  type population = Witness.`'population`.T
  type name = Witness.`'name`.T
  type university = Witness.`'university`.T
  type math = Witness.`'math`.T
  type budget = Witness.`'budget`.T
}

