package HdmiCore

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

class TMDSEncoder extends Module {
  val io = IO(new Bundle {
    val en = Input(Bool())
    val ctrl = Input(UInt(2.W))
    val din = Input(UInt(8.W))
    val dout = Output(UInt(10.W))
  })

  io.dout := DontCare

}

object TMDSEncoder extends App {
  (new ChiselStage).execute(args,
    Seq(ChiselGeneratorAnnotation(() => new TMDSEncoder())))
}
