lazy val Versions = new {
  val macrocompat = "1.1.1"
  val paradise = "2.1.0"
  val scalatest = "3.0.1"
  val util = "0.31.3"
  val scalacheck = "1.13.4"
}

val sharedSettings: Seq[Def.Setting[_]] = Defaults.coreDefaultSettings ++ Seq(
  organization := "com.github.alexflav23",
  scalaVersion := "2.11.8",
  crossScalaVersions := Seq("2.11.8", "2.12.1"),
  resolvers ++= Seq(
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("releases"),
    Resolver.bintrayRepo("outworkers", "oss-releases"),
    Resolver.jcenterRepo
  ),
  scalacOptions in ThisBuild ++= Seq(
    "-language:experimental.macros",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:reflectiveCalls",
    "-language:higherKinds",
    "-language:existentials",
    "-Xlint",
    "-deprecation",
    "-feature",
    "-unchecked"
  )
)

lazy val macroTalk = (project in file("."))
  .settings(sharedSettings: _*)
  .settings(
    moduleName := "first-macros",
    libraryDependencies ++= Seq(
       // We need a provided dependency on the compiler itself
      "org.scala-lang" % "scala-compiler" % scalaVersion.value % "provided",
       // Gives us Scala 2.10 compatibility, can skip if we don't care
      "org.typelevel" %% "macro-compat" % Versions.macrocompat,
      // compiler plugin to patch macros across Scala versions
      compilerPlugin("org.scalamacros" % "paradise" % Versions.paradise cross CrossVersion.full),
      // automated sampling framework for case class generation
      "com.outworkers" %% "util-samplers" % Versions.util,
      // a testing framework
      "org.scalatest" %% "scalatest" % Versions.scalatest % Test,
      // Property style checks
      "org.scalacheck" %% "scalacheck" % Versions.scalacheck % Test
    )
  )
