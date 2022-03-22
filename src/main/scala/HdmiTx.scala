package hdmicore

import chisel3._
import chisel3.util._

import fpgamacro.gowin.{Oser10Module, TLVDS_OBUF}

class HdmiTx() extends Module {
  val io = IO(new Bundle {
    val videoSig = Input(new VideoHdmi())
    val serClk = Input(Clock())
    val tmds = Output(new Tmds())
  })

  /* hdmi transmission */
  val rgb2tmds = Module(new Rgb2Tmds())
  rgb2tmds.io.videoSig <> io.videoSig

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
