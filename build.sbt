ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.13"

lazy val root = (project in file("."))
  .settings(
    name := "library-management-system",
  )
  .settings(
    scalacOptions ++= Seq(
      "-encoding",
      "utf8",
      "-Ymacro-annotations",
      "-deprecation",
      "-unchecked",
      "-feature",
      "-language:implicitConversions",
      "-language:higherKinds",
      "-language:existentials",
      "-language:postfixOps"
    )
  )
  .settings(
    mainClass.withRank(KeyRanks.Invisible) := Some("src.main.scala.library.management.system.com.Main"),
    libraryDependencies ++= Dependencies.initialization,
  )