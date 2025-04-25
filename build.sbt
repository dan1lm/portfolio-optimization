ThisBuild / version := "0.1.0-SNAPSHOT"
ThisBuild / scalaVersion := "3.2.2"

lazy val root = (project in file("."))
  .settings(
    name := "portfolio-optimization",
    libraryDependencies ++= Seq(
      "org.scalanlp" %% "breeze" % "2.1.0",
      "org.scalanlp" %% "breeze-viz" % "2.1.0",

      "com.github.tototoshi" %% "scala-csv" % "1.3.10",

      "com.softwaremill.sttp.client3" %% "core" % "3.8.16",

      "com.lihaoyi" %% "ujson" % "3.0.0"
    )
  )