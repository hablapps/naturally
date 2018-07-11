# Shapelens

This is just an experiment to generate implicit [monocle](https://github.com/julien-truffaut/Monocle) `Lens`es using [shapeless](https://github.com/milessabin/shapeless). Basically, if we have defined a bunch of nested case classes:

```scala
case class City(population: Int, name: String, university: University)
case class University(name: String, math: Department)
case class Department(budget: Int)
```

we could use `Shapelens` to generate a monocle's `Lens` for any field. For instance, we could generate the lens that points at the `name` field of a `City` as follows:

```scala
val nameLn: Lens[City, String] = Shapelens['name :: HNil, City, Int]
```

As you can see, we need to provide the context where we expect to find the field. For example, if we want to point at a nested field, we use:

```scala
val univBudgetLn: Lens[University, Int] = 
  Shapelens['math :: 'budget :: HNil, University, Int]
``` 

We find the context useful to disambiguate implicit lenses that share the same type. For instance, there are two possible lenses with type `Lens[City, String]`: the one that points at the city name and the one that points at the university name:

```scala
val cityNameLn = Shapelens['name :: HNil, City, String]
val univNameLn = Shapelens['university :: 'name :: HNil, City, String]
```

Shapelens is an utility that we are planning to exploit in the upcoming redesign of [Stateless](https://github.com/hablapps/stateless), to provide automatic instances of optic algebras.

