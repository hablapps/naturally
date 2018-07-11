package org.hablapps.shapelens
package test

import org.scalatest._
import shapeless._

class UniversitySpec extends FlatSpec with Matchers {

  case class City(population: Int, name: String, university: University)
  case class University(name: String, math: Department)
  case class Department(budget: Int)

  val math = Department(50000)
  val urjc = University("urjc", math)
  val mostoles = City(200000, "mostoles", urjc)

  "Shapelens" should "generate a lens for a particular field" in {
    val population = Shapelens[population :: HNil, City, Int]
    population.get(mostoles) shouldBe 200000
    population.modify(_ * 2)(mostoles) shouldBe mostoles.copy(population = 400000)
  }

  it should "generate a lens for a nested field" in {
    val budget = Shapelens[math :: budget :: HNil, University, Int]
    budget.get(urjc) shouldBe math.budget
    budget.set(0)(urjc) shouldBe urjc.copy(math = Department(0))
  }

  it should "disambiguate conflicts using path" in {
    val cityName = Shapelens[name :: HNil, City, String]
    val univName = Shapelens[university :: name :: HNil, City, String]
    cityName.get(mostoles) shouldBe mostoles.name
    univName.get(mostoles) shouldBe mostoles.university.name
  }

  // Shapelens with incorrect type
  "Shapelens[name :: HNil, City, Int]" shouldNot compile

  // Shapelens with incorrect name
  "Shapelens[budget :: HNil, City, Int]" shouldNot compile

  // XXX: is it possible to integrate `LabelledGeneric` with literal types? If
  // so, we could avoid these aliases and use `'population` directly!
  type population = Witness.`'population`.T
  type name = Witness.`'name`.T
  type university = Witness.`'university`.T
  type math = Witness.`'math`.T
  type budget = Witness.`'budget`.T 
}

