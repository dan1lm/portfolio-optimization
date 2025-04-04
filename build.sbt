ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "3.0.0"

lazy val root = (project in file("."))
  .settings(
    name := "portfolio-optimization",

    libraryDependencies ++= Seq(
      // breeze is used for linear algebra and numerical computing
      ("org.scalanlp" %% "breeze" % "1.2").cross(CrossVersion.for3Use2_13),
      ("org.scalanlp" %% "breeze-viz" % "1.2").cross(CrossVersion.for3Use2_13),

      // optimization algorithms
      "org.apache.commons" % "commons-math3" % "3.6.1",

      // handling dates and time
      ("com.github.nscala-time" %% "nscala-time" % "2.30.0").cross(CrossVersion.for3Use2_13),

      // csv parsing
      ("com.github.tototoshi" %% "scala-csv" % "1.3.8").cross(CrossVersion.for3Use2_13),

      // logging
      "ch.qos.logback" % "logback-classic" % "1.2.6",
      ("com.typesafe.scala-logging" %% "scala-logging" % "3.9.4").cross(CrossVersion.for3Use2_13),

      // testing
      ("org.scalatest" %% "scalatest" % "3.2.9" % Test).cross(CrossVersion.for3Use2_13),

      // http requests
      "com.softwaremill.sttp.client3" %% "core" % "3.8.16",
    ),

    // resolver for breeze dependencies
    resolvers ++= Seq(
      "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
      "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
    ),

    scalacOptions ++= Seq(
      "-feature",
      "-deprecation",
      "-unchecked",
      "-language:postfixOps"
    )
  )