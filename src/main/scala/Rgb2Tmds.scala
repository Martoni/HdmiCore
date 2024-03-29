package hdmicore

import chisel3._
import circt.stage.ChiselStage
import chisel3.util._

class Rgb2Tmds extends Module {
  val io = IO(new Bundle {
    val videoSig = Input(new VideoHdmi())
    val tmds_blue = Output(UInt(10.W))
    val tmds_red = Output(UInt(10.W))
    val tmds_green = Output(UInt(10.W))
  })

  /* Blue and controls */
  val tbM = Module(new TMDSEncoder())
  tbM.io.en := io.videoSig.de
  tbM.io.ctrl := io.videoSig.vsync ## io.videoSig.hsync
  tbM.io.din := io.videoSig.pixel.blue
  io.tmds_blue := tbM.io.dout

  /* red */
  val trM = Module(new TMDSEncoder())
  trM.io.en := io.videoSig.de
  trM.io.ctrl := 0.U
  trM.io.din := io.videoSig.pixel.red
  io.tmds_red := trM.io.dout

  /* green */
  val tgM = Module(new TMDSEncoder())
  tgM.io.en := io.videoSig.de
  tgM.io.ctrl := 0.U 
  tgM.io.din := io.videoSig.pixel.green
  io.tmds_green := tgM.io.dout
}

object Rgb2Tmds extends App {
  val verilog_src = ChiselStage
    .emitSystemVerilog(new Rgb2Tmds,
        firtoolOpts = Array(
          "-disable-all-randomization",
           "--lowering-options=disallowLocalVariables", // avoid 'automatic logic'
           "-strip-debug-info"))
  val fverilog = os.pwd / "Rgb2Tmds.v"
  if(os.exists(fverilog))
    os.remove(fverilog)
  os.write(fverilog, verilog_src)
}
