package hdmicore

import chisel3._
import chisel3.util._

import video.{VideoParams, HVSync}
import fpgamacro.gowin.{Oser10Module, TLVDS_OBUF}

class PatternExample extends Module {
  val io = IO(new Bundle {
    val serClk = Input(Clock())
    val tmds = Output(new Tmds())
  })

  val vp = VideoParams(
      H_DISPLAY = 1280, H_FRONT = 110,
      H_SYNC = 40, H_BACK = 220,
      V_SYNC = 5,  V_BACK = 20,
      V_TOP = 5, V_DISPLAY = 720,
      V_BOTTOM = 20)

  val hv_sync = Module(new HVSync(vp)) // Synchronize VGA module
  val video_de = hv_sync.io.display_on

  /* generate rainbow */
  /* inspired from http://blog.vermot.net/2011/11/03/generer-un-degrade-en-arc-en-ciel-en-fonction-d-une-valeur-programmatio/ */
  val cTrig1 = 255.U
  val cTrig2 = 510.U
  val cTrig3 = 765.U
  val cTrig4 = 1020.U
  val cTrig5 = 1275.U
  val cTrig6 = 1530.U
  val hpos = hv_sync.io.hpos
  val pred = Wire(UInt(8.W))
  when(hpos < cTrig1){
    pred := cTrig1
  }.elsewhen(hpos < cTrig2) {
    pred := cTrig2 - hpos
  }.elsewhen(hpos < cTrig4){
    pred := 0.U
  }.elsewhen(hpos < cTrig5){
    pred := hpos - cTrig4
  }.otherwise{
    pred := cTrig1
  }
  val pgreen = Wire(UInt(8.W))
  when(hpos < cTrig1){
    pgreen := hpos 
  }.elsewhen(hpos < cTrig3){
    pgreen := cTrig1
  }.elsewhen(hpos < cTrig4){
    pgreen := cTrig4 - hpos
  }.otherwise{
    pgreen := 0.U
  }

  val pblue  = Wire(UInt(8.W))
  when(hpos < cTrig2){
    pblue := 0.U
  }.elsewhen(hpos < cTrig3){
    pblue := hpos - cTrig2
  }.elsewhen(hpos < cTrig5){
    pblue := cTrig1
  }.elsewhen(hpos < cTrig6){
    pblue := cTrig6 - hpos
  }.otherwise {
    pblue := 0.U
  }
  


  /* hdmi transmission */
  val rgb2tmds = Module(new Rgb2Tmds())
  rgb2tmds.io.videoSig.de := video_de 
  rgb2tmds.io.videoSig.hsync := hv_sync.io.hsync
  rgb2tmds.io.videoSig.vsync := hv_sync.io.vsync
  rgb2tmds.io.videoSig.pixel.red   := Mux(video_de, pred, 0.U)
  rgb2tmds.io.videoSig.pixel.green := Mux(video_de, pgreen, 0.U)
  rgb2tmds.io.videoSig.pixel.blue  := Mux(video_de, pblue, 0.U)

  

  /* serdes */
  // Blue -> data 0
  val serdesBlue = Module(new Oser10Module())
  serdesBlue.io.data := rgb2tmds.io.tmds_blue
  serdesBlue.io.fclk := io.serClk

  // Green -> data 1
  val serdesGreen = Module(new Oser10Module())
  serdesGreen.io.data := rgb2tmds.io.tmds_green
  serdesGreen.io.fclk := io.serClk

  // Red -> data 2
  val serdesRed = Module(new Oser10Module())
  serdesRed.io.data := rgb2tmds.io.tmds_red
  serdesRed.io.fclk := io.serClk

  io.tmds.data := serdesRed.io.q ## serdesGreen.io.q ## serdesBlue.io.q

  // clock
  val serdesClk = Module(new Oser10Module())
  serdesClk.io.data := "b1111100000".U(10.W)
  serdesClk.io.fclk := io.serClk
  io.tmds.clk := serdesClk.io.q
}
