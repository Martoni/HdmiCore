// See README.md for license details.


scalaVersion     := "2.13.8"
version          := "0.1.1"
organization     := "com.armadeus"

lazy val root = (project in file("."))
  .settings(
    name := "hdmicore",
    libraryDependencies ++= Seq(
      "edu.berkeley.cs" %% "chisel3" % "3.5.1",
      "edu.berkeley.cs" %% "chiseltest" % "0.5.0" % "test",
      "Martoni" %% "fpgamacro" % "0.2.2"
    ),
    scalacOptions ++= Seq(
      "-Xsource:2.11",
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
    ),
    addCompilerPlugin("edu.berkeley.cs" % "chisel3-plugin" % "3.5.1" cross CrossVersion.full),
  )

