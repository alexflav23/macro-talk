lazy val Versions = new {
  val macrocompat = "1.1.1"
  val paradise = "2.1.0"
  val scalatest = "3.0.1"
}

val sharedSettings: Seq[Def.Setting[_]] = Defaults.coreDefaultSettings ++ Seq(
  organization := "com.outworkers",
  scalaVersion := "2.11.8",
  credentials ++= Publishing.defaultCredentials,
  resolvers ++= Seq(
    "Twitter Repository" at "http://maven.twttr.com",
    Resolver.typesafeRepo("releases"),
    Resolver.sonatypeRepo("releases"),
    Resolver.jcenterRepo
  ),
  scalacOptions ++= Seq(
    "-language:experimental.macros",
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
       // Gives us Scala 2.10 compatibility, can skip if we don't care
      "org.typelevel" %% "macro-compat" % Versions.macrocompat,
      // compiler plugin to patch macros across Scala versions
      compilerPlugin("org.scalamacros" % "paradise" % Versions.paradise cross CrossVersion.full)
    )
  )
