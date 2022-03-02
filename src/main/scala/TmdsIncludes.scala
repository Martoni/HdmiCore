package hdmicore

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

class RGBColors extends Bundle {
  val red = UInt(8.W)
  val green = UInt(8.W)
  val blue = UInt(8.W)
}

class VideoHdmi extends Bundle {
  val pixel = new RGBColors()
  val de = Bool()
  val hsync = Bool()
  val vsync = Bool()
}

class Tmds extends Bundle {
  val clk = Bool()
  val data = UInt(3.W)
}

class DiffPair extends Bundle {
    val p = Bool()
    val n = Bool()
}

class TMDSDiff extends Bundle {
    val clk  = new DiffPair()
    val data = Vec(3, new DiffPair())
}

