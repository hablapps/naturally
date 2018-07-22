name := "shapelens"
version := "0.1-SNAPSHOT"

organization := "org.hablapps"

scalaVersion := "2.10.6"
crossScalaVersions := Seq("2.12.6", "2.10.6")

addCompilerPlugin("org.spire-math" %% "kind-projector" % "0.9.3")

libraryDependencies += scalaVersion{
    case v if v.startsWith("2.10") =>
      compilerPlugin("org.scalamacros" % "paradise" % "2.1.0" cross CrossVersion.full)
    case _ =>
      compilerPlugin("io.tryp" % "splain" % "0.3.1" cross CrossVersion.patch)
  }.value

scalacOptions ++= Seq(
  "-Xlint",
  "-unchecked",
  "-deprecation",
  "-feature",
  "-language:postfixOps",
  "-language:implicitConversions",
  "-language:higherKinds")

scalacOptions ++= scalaVersion{
  case v if v.startsWith("2.12") =>
    Seq("-Ypartial-unification")
  case _ =>
    Seq()
}.value

scalacOptions in (Compile, console) ~= (_ filterNot (_ == "-Xlint"))
scalacOptions in (Test, console) := (scalacOptions in (Compile, console)).value

val monocleVersion = "1.5.0"

libraryDependencies ++= Seq(
  "org.scalaz" %% "scalaz-core" % "7.2.8",
  "org.typelevel" %% "cats-core" % "1.1.0",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-law"   % monocleVersion % "test")
