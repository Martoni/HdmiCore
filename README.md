HDMI Chisel core 
================

This HDMI core is mainly inspired from
[vhdl-hdmi-out](https://github.com/fcayci/vhdl-hdmi-out) project.

# Install

To use it locally, first clone the repository then publish it locally :

```Shell
$ git clone https://github.com/Martoni/HdmiCore.git
$ cd HdmiCore
$ sbt publishLocal
```

On your personnal `build.sbt` project add the following line :

```Scala
    libraryDependencies ++= Seq(
//    ...
      "com.armadeus" %% "HdmiCore" % "0.1.0"
    ),

```
