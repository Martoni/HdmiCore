// See README.md for license details.

scalaVersion     := "2.12.13"
version          := "0.1.0"
organization     := "Martoni"

githubOwner := "Martoni"
githubRepository := "hdmicore"

lazy val root = (project in file("."))
  .settings(
    name := "hdmicore",
    externalResolvers += "fpgamacro packages" at "https://maven.pkg.github.com/Martoni/fpgamacro",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % "3.5.1",
      "edu.berkeley.cs" %% "chiseltest" % "0.5.0" % "test",
      "Martoni" %% "fpgamacro" % "0.1.0-SNAPSHOT"
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.5.1" cross CrossVersion.full),
    addCompilerPlugin("org.scalamacros" % "paradise" % "2.1.1" cross CrossVersion.full)
  )

