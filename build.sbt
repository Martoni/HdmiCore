// See README.md for license details.

val majorChiselVersion = "6"
val minorChiselVersion = "2.0"

val chiselVersion = majorChiselVersion + "." + minorChiselVersion

scalaVersion     := "2.13.8"
version          := chiselVersion
organization     := "eu.fabienm"

lazy val root = (project in file("."))
  .settings(
    name := "hdmicore",
    libraryDependencies ++= Seq(
      "org.chipsalliance" %% "chisel" % chiselVersion,
      "Martoni" %% "fpgamacro" % "6.2.1"
    ),
    scalacOptions ++= Seq(
      "-language:reflectiveCalls",
      "-deprecation",
      "-feature",
      "-Xcheckinit",
      "-Ymacro-annotations",
    ),
    addCompilerPlugin("org.chipsalliance" % "chisel-plugin" % chiselVersion cross CrossVersion.full),
  )
