package hdmicore

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

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
  trM.io.ctrl := io.videoSig.vsync ## io.videoSig.hsync
  trM.io.din := io.videoSig.pixel.red
  io.tmds_red := trM.io.dout

  /* green */
  val tgM = Module(new TMDSEncoder())
  tgM.io.en := io.videoSig.de
  tgM.io.ctrl := io.videoSig.vsync ## io.videoSig.hsync
  tgM.io.din := io.videoSig.pixel.green
  io.tmds_green := tgM.io.dout
}

object Rgb2Tmds extends App {
  (new ChiselStage).execute(args,
    Seq(ChiselGeneratorAnnotation(() => new Rgb2Tmds())))
}
