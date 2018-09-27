name := "naturally"
version := "0.1-SNAPSHOT"

organization := "org.hablapps"

scalaVersion := "2.10.6"
crossScalaVersions := Seq("2.12.6", "2.11.12", "2.10.6")

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
  "org.typelevel" %% "cats-core" % "0.9.0",
  "org.scalactic" %% "scalactic" % "3.0.5",
  "org.scalatest" %% "scalatest" % "3.0.5" % "test",
  "com.chuusai" %% "shapeless" % "2.3.2",
  "com.github.julien-truffaut" %%  "monocle-core"  % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-macro" % monocleVersion,
  "com.github.julien-truffaut" %%  "monocle-law"   % monocleVersion % "test")

// Sonatype publishing conf.
import xerial.sbt.Sonatype._

publishTo := sonatypePublishTo.value
sonatypeProfileName := "org.hablapps"
publishMavenStyle := true
licenses := Seq("APL2" -> url("http://www.apache.org/licenses/LICENSE-2.0"))
sonatypeProjectHosting := Some(GitHubHosting("hablapps", "naturally", "juanmanuel.serrano@hablapps.com"))
homepage := Some(url("https://github.com/hablapps/naturally"))
scmInfo := Some(
  ScmInfo(
    url("https://github.com/hablapps/naturally"),
    "scm:git@github.com:hablapps/naturally.git"
  )
)
developers := List(
  Developer(id="jserranohidalgo",
    name="Juan Manuel Serrano Hidalgo",
    email="juanmanuel.serrano@hablapps.com",
    url=url("http://www.hablapps.com")),
  Developer(id="javierfs89",
    name="Javier Fuentes Sánchez",
    email="javier.fuentes@hablapps.com",
    url=url("http://www.hablapps.com")),
  Developer(id="jeslg",
    name="Jesús López González",
    email="jesus.lopez@hablapps.com",
    url=url("http://www.hablapps.com"))
)
