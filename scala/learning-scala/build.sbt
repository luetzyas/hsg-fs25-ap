val scala3Version = "3.3.1"

lazy val root = project
  .in(file("."))
  .settings(
    name := "learning-scala",
    version := "0.1.0-SNAPSHOT",
    scalaVersion := scala3Version,
    scalacOptions ++= Seq("-language:implicitConversions"),
    libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.17"
  )
