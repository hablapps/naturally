# Shapelens

This is just an experiment to deploy [monocle](https://github.com/julien-truffaut/Monocle) `Lens`es as implicit values, using [shapeless](https://github.com/milessabin/shapeless) instead of macros. 

Consider the following hierarchy of nested case classes as an example:

```scala
case class City(population: Int, name: String, university: University)
case class University(name: String, math: Department)
case class Department(budget: Int)
```

We could use `Shapelens` to generate monocle's `Lens`es for each field. For instance, we could generate the lens that points at the `name` field of a `City` as follows:

```scala
import monocle.Lens
val nameLn: Lens[City, String] = Shapelens['name :: HNil, City, Int]
```

_(*) The current encodings use `Witness.`'name`.T` instead of `'name`, so we need to provide ad hoc aliases to make the invocation readable._

As you can see, we need to provide the context where we expect to find the field. Thereby, if we were interested in pointing at a nested field we'd need to provide the full path:

```scala
val univBudgetLn = Shapelens['math :: 'budget :: HNil, University, Int]
``` 

We find the context useful to disambiguate lenses that share the same type. For instance, there are two possible lenses with type `Lens[City, String]`: the one that points at the city name and the one that points at the university name of a city. We use the context path to be precise about the field that we are interested in:

```scala
val cityNameLn = Shapelens['name :: HNil, City, String]
val univNameLn = Shapelens['university :: 'name :: HNil, City, String]
```

You can find the whole example [here](src/test/scala/University.scala).

Finally, we must say that we are exploiting this idea to provide automatic instances of state-based algebraic theories in the upcoming new version of [Stateless](http://github.com/hablapps/stateless). However, we found this technique generic enough to be published on its own. Having said so, we hope you find a crazy scenario where you can apply it! 

