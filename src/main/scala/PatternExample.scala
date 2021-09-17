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
  /* hdmi transmission */

  val rgb2tmds = Module(new Rgb2Tmds())
  rgb2tmds.io.videoSig.de := video_de 
  rgb2tmds.io.videoSig.hsync := hv_sync.io.hsync
  rgb2tmds.io.videoSig.vsync := hv_sync.io.vsync
  rgb2tmds.io.videoSig.pixel.red   := Mux(video_de, "h00".U(8.W), 0.U)
  rgb2tmds.io.videoSig.pixel.green := Mux(video_de, "h00".U(8.W), 0.U)
  rgb2tmds.io.videoSig.pixel.blue  := Mux(video_de, "hFF".U(8.W), 0.U)

  

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
