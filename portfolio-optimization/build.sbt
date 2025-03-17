name := "portfolio-optimization"
version := "0.1.0"
scalaVersion := "3.6.4"

libraryDependencies ++= Seq(
  
  ("org.scalanlp" %% "breeze" % "2.1.0").cross(CrossVersion.for3Use2_13),
  ("org.scalanlp" %% "breeze-viz" % "2.1.0").cross(CrossVersion.for3Use2_13),
  
  "org.apache.commons" % "commons-math3" % "3.6.1",
  
  "com.github.nscala-time" %% "nscala-time" % "2.32.0",
  
  "io.circe" %% "circe-core" % "0.14.3",
  "io.circe" %% "circe-generic" % "0.14.3",
  "io.circe" %% "circe-parser" % "0.14.3",
  
  "com.softwaremill.sttp.client3" %% "core" % "3.8.11",
  
  "com.github.tototoshi" %% "scala-csv" % "1.3.10",
  
  "ch.qos.logback" % "logback-classic" % "1.4.6",
  "com.typesafe.scala-logging" %% "scala-logging" % "3.9.5",
  
  "org.scalatest" %% "scalatest" % "3.2.15" % Test,
  
  "com.typesafe.akka" %% "akka-actor-typed" % "2.7.0",
  "com.typesafe.akka" %% "akka-stream" % "2.7.0",
  "com.typesafe.akka" %% "akka-http" % "10.5.0",
  
  "org.scala-js" %%% "scalajs-dom" % "2.8.0" % Provided
)

resolvers ++= Seq(
  "Sonatype Releases" at "https://oss.sonatype.org/content/repositories/releases/",
  "Sonatype Snapshots" at "https://oss.sonatype.org/content/repositories/snapshots/"
)

scalacOptions ++= Seq(
  "-feature",
  "-deprecation",
  "-unchecked",
  "-explain",
  "-explain-types",
  "-language:postfixOps",
  "-language:implicitConversions"
)

assembly / assemblyJarName := s"${name.value}-${version.value}.jar"
assembly / assemblyMergeStrategy := {
  case PathList("META-INF", xs @ _*) => MergeStrategy.discard
  case "reference.conf" => MergeStrategy.concat
  case x => MergeStrategy.first
}