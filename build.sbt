ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "portfolio-optimization",

    libraryDependencies ++= Seq(
      ("org.scalanlp" %% "breeze" % "2.0.0-M1").cross(CrossVersion.for3Use2_13),
      ("org.scalanlp" %% "breeze-viz" % "2.0.0-M1").cross(CrossVersion.for3Use2_13),

      "org.apache.commons" % "commons-math3" % "3.6.1",
      "com.github.nscala-time" %% "nscala-time" % "2.32.0",
      "com.github.tototoshi" %% "scala-csv" % "1.3.10",
      "ch.qos.logback" % "logback-classic" % "1.4.6",
      "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",

      "com.typesafe" % "config" % "1.4.2",
      "org.json4s" %% "json4s-native" % "4.0.3",

      "org.scalatest" %% "scalatest" % "3.2.9" % Test
    ),

    resolvers ++= Seq(
      "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/",
      "jitpack" at "https://jitpack.io"
    ),

    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-explain",
      "-explain-types"
    )
  )