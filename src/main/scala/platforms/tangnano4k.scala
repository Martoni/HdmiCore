package hdmicore.platforms

/** Pattern generation HDMI example for TangNano4k
 */

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

import fpgamacro.gowin.{CLKDIV, TMDS_PLLVR, TLVDS_OBUF}
import hdmicore.PatternExample

class TangNano4k extends RawModule {

    /************/
    /** outputs */
    /* Clock and reset */
    val I_clk = IO(Input(Clock()))
    val I_reset_n = IO(Input(Bool()))

    /* Debug leds */
    val O_led = IO(Output(UInt(2.W)))

    /* TMDS (HDMI) signals */
    val O_tmds_clk_p  = IO(Output(Bool()))
    val O_tmds_clk_n  = IO(Output(Bool()))
    val O_tmds_data_p = IO(Output(UInt(3.W)))
    val O_tmds_data_n = IO(Output(UInt(3.W)))

    /* button */
    val I_button = IO(Input(Bool()))
    val O_trig = IO(Output(Bool()))
    /********************************************/

    O_trig := I_button

    O_led := 1.U(2.W)

    val pll_lock =  Wire(Bool())
    val serial_clk = Wire(Clock())
    val pix_clk = Wire(Clock())

    val glb_rst = ~(pll_lock & I_reset_n)

    /* CLKDIV */
    val clkDiv = Module(new CLKDIV())
    clkDiv.io.RESETN := ~glb_rst
    clkDiv.io.HCLKIN := serial_clk
    pix_clk := clkDiv.io.CLKOUT
    clkDiv.io.CALIB := true.B

    /* TMDS PLL */
    val tmdsPllvr = Module(new TMDS_PLLVR())
    tmdsPllvr.io.clkin := I_clk
    serial_clk := tmdsPllvr.io.clkout
    pll_lock := tmdsPllvr.io.lock

    withClockAndReset(pix_clk, glb_rst) {

      /* counter debug */
      val max_count = 27000000
      val (counterReg, counterPulse) = Counter(true.B, max_count)
      O_led := (counterReg >= (max_count/2).U)

      val patternExample = Module(new PatternExample())
      patternExample.io.serClk := serial_clk

      /* LVDS output */
      val buffDiffBlue = Module(new TLVDS_OBUF())
      buffDiffBlue.io.I := patternExample.io.tmds.data(0)
      val buffDiffGreen = Module(new TLVDS_OBUF())
      buffDiffGreen.io.I := patternExample.io.tmds.data(1)
      val buffDiffRed = Module(new TLVDS_OBUF())
      buffDiffRed.io.I := patternExample.io.tmds.data(2)
      val buffDiffClk = Module(new TLVDS_OBUF())
      buffDiffClk.io.I := patternExample.io.tmds.clk

      O_tmds_clk_p  := buffDiffClk.io.O
      O_tmds_clk_n  := buffDiffClk.io.OB
      O_tmds_data_p := buffDiffRed.io.O ## buffDiffGreen.io.O ## buffDiffBlue.io.O
      O_tmds_data_n := buffDiffRed.io.OB ## buffDiffGreen.io.OB ## buffDiffBlue.io.OB
    }
}

object TangNano4k extends App {
  (new ChiselStage).execute(args,
    Seq(ChiselGeneratorAnnotation(() => new TangNano4k())))
}
