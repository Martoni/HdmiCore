package hdmicore

import chisel3._
import chisel3.util._

import video.{VideoParams, HVSync}
import fpgamacro.gowin.{Oser10Module, TLVDS_OBUF}


sealed trait PatternType
case object ptRainbow extends PatternType
case object ptVStripes extends PatternType
case object ptHStripes extends PatternType
case object ptFrenchFlag extends PatternType
case object ptIrishFlag extends PatternType
case object ptItalianFlag extends PatternType
case object ptBelgianFlag extends PatternType
case object ptDutchFlag extends PatternType
case object ptLuxembourgishFlag extends PatternType
case object ptGermanFlag extends PatternType
case object ptSpanishFlag extends PatternType
case object ptAustrianFlag extends PatternType
case object ptGreekFlag extends PatternType
case object ptDanishFlag extends PatternType
case object ptSwedishFlag extends PatternType
case object ptFinnishFlag extends PatternType
case object ptNorwegianFlag extends PatternType


class PatternExample(pt: PatternType = ptFrenchFlag) extends Module {
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

  val pblue  = Wire(UInt(8.W))
  val pred = Wire(UInt(8.W))
  val pgreen = Wire(UInt(8.W))
  val hpos = hv_sync.io.hpos
  val vpos = hv_sync.io.vpos
  if(pt == ptRainbow){
    /* generate rainbow */
    /* inspired from http://blog.vermot.net/2011/11/03/generer-un-degrade-en-arc-en-ciel-en-fonction-d-une-valeur-programmatio/ */
    val cTrig1 = 255.U
    val cTrig2 = 510.U
    val cTrig3 = 765.U
    val cTrig4 = 1020.U
    val cTrig5 = 1275.U
    val cTrig6 = 1530.U
    val x = RegNext((hpos*cTrig6)/vp.H_DISPLAY.U)
    when(x < cTrig1){
      pred := cTrig1
    }.elsewhen(x < cTrig2) {
      pred := cTrig2 - x
    }.elsewhen(x < cTrig4){
      pred := 0.U
    }.elsewhen(x < cTrig5){
      pred := x - cTrig4
    }.otherwise{
      pred := cTrig1
    }
    when(x < cTrig1){
      pgreen := x 
    }.elsewhen(x < cTrig3){
      pgreen := cTrig1
    }.elsewhen(x < cTrig4){
      pgreen := cTrig4 - x
    }.otherwise{
      pgreen := 0.U
    }
  
    when(x < cTrig2){
      pblue := 0.U
    }.elsewhen(x < cTrig3){
      pblue := x - cTrig2
    }.elsewhen(x < cTrig5){
      pblue := cTrig1
    }.elsewhen(x < cTrig6){
      pblue := cTrig6 - x
    }.otherwise {
      pblue := 0.U
    }
  }
  if(pt == ptVStripes){
    pred   := Mux(0.U === hpos % 2.U, 0.U, 255.U)
    pgreen := Mux(0.U === hpos % 2.U, 0.U, 255.U)
    pblue  := Mux(0.U === hpos % 2.U, 0.U, 255.U)
  }
  if(pt == ptHStripes){
    pred   := Mux(0.U === vpos % 2.U, 0.U, 255.U)
    pgreen := Mux(0.U === vpos % 2.U, 0.U, 255.U)
    pblue  := Mux(0.U === vpos % 2.U, 0.U, 255.U)
  }
  if(pt == ptFrenchFlag){
    val swidth = 1280
    pred := Mux(hpos < (swidth/3).U, 0.U, 255.U)
    pgreen := Mux((hpos > (swidth/3).U) && (hpos < (swidth*2/3).U), 255.U, 0.U)
    pblue := Mux(hpos < (swidth*2/3).U, 255.U, 0.U)
  }
  if(pt == ptIrishFlag){
    val swidth = 1280
    pred := Mux(hpos < (swidth/3).U, 0.U, 255.U)
    pgreen := Mux(hpos < (swidth*2/3).U, 255.U, 2.U)
    pblue := Mux((hpos > (swidth/3).U) && (hpos < (swidth*2/3).U), 255.U, 0.U)
  }
  if(pt == ptItalianFlag){
    val swidth = 1280
    pred := Mux(hpos < (swidth/3).U, 0.U, 255.U)
    pgreen := Mux(hpos < (swidth*2/3).U, 255.U, 0.U)
    pblue := Mux((hpos > (swidth/3).U) && (hpos < (swidth*2/3).U), 255.U, 0.U)
  }
  if(pt == ptBelgianFlag){
    val swidth = 1280
    pred := Mux(hpos > (swidth/3).U, 255.U, 0.U)
    pgreen := Mux((hpos > (swidth/3).U) && (hpos < (swidth*2/3).U), 255.U, 0.U)
    pblue := 0.U
  }
  if(pt == ptDutchFlag){
    val sheight = 720
    val prbright = Mux(vpos < (sheight/3).U, 2.U, 255.U)
    val pbbright = Mux(vpos < (sheight*2/3).U, 255.U, 2.U)
    pred := Mux(vpos < (sheight*2/3).U, prbright, 0.U)
    pgreen := Mux((vpos >= (sheight/3).U) && (vpos < (sheight*2/3).U), 255.U, 0.U)
    pblue := Mux(vpos < (sheight/3).U, 0.U, pbbright)
  }
  if(pt == ptLuxembourgishFlag){
    val sheight = 720
    pred := Mux(vpos < (sheight*2/3).U, 255.U, 0.U)
    pgreen := Mux((vpos >= (sheight/3).U) && (vpos < (sheight*2/3).U), 255.U, 0.U)
    pblue := Mux(vpos < (sheight/3).U, 0.U, 255.U)
  }
  if(pt == ptGermanFlag){
    val sheight = 720
    pred := Mux(vpos > (sheight/3).U, 255.U, 0.U)
    pgreen := Mux(vpos < (sheight*2/3).U, 0.U, 255.U)
    pblue := 0.U
  }
  if(pt == ptSpanishFlag){
    val sheight = 720
    pred := 255.U
    pgreen := Mux((vpos > (sheight/4).U) && (vpos < (sheight*3/4).U), 255.U, 0.U)
    pblue := 0.U
  }
  if(pt == ptAustrianFlag){
    val sheight = 720
    pred := 255.U
    pgreen :=  Mux((vpos > (sheight/3).U) && (vpos < (sheight*2/3).U), 255.U, 0.U)
    pblue := Mux((vpos > (sheight/3).U) && (vpos < (sheight*2/3).U), 255.U, 0.U)
  }
  if(pt == ptGreekFlag){
    val swidth = 1280
    val sheight = 720
    val swstep = swidth*3/80
    val shstep = sheight/9
    val oinv = Mux((hpos <= (swstep*10).U) && (vpos > (shstep*2).U) && (vpos <= (shstep*3).U), 255.U, 0.U)
    val pinv = Mux((hpos > (swstep*4).U) && (hpos <= (swstep*6).U) && (vpos <= (shstep*5).U), 255.U, oinv)
    val ninv = Mux(((hpos <= (swstep*4).U) || (hpos > (swstep*6).U)) && (hpos <= (swstep*10).U) && (vpos <= (shstep*5).U), 0.U, 255.U)
    pred := Mux(vpos % (sheight*2/9).U > (sheight/9).U, ninv, pinv)
    pgreen := Mux(vpos % (sheight*2/9).U > (sheight/9).U, ninv, pinv)
    pblue := 255.U
  }
  if(pt == ptDanishFlag){
    val swidth = 1280
    val sheight = 720
    val swstep = swidth*4/37
    val shstep = sheight/7
    val pinv = Mux((hpos > (swstep*3).U) && (hpos <= (swstep*4).U), 255.U, 0.U)
    pred := 255.U
    pgreen := Mux((vpos > (shstep*3).U) && (vpos <= (shstep*4).U), 255.U, pinv)
    pblue := Mux((vpos > (shstep*3).U) && (vpos <= (shstep*4).U), 255.U, pinv)
  }
  if(pt == ptSwedishFlag){
    val swidth = 1280
    val sheight = 720
    val swstep = swidth/16
    val shstep = sheight/5
    val pinv = Mux((hpos > (swstep*5).U) && (hpos <= (swstep*7).U), 255.U, 0.U)
    val ninv = Mux((hpos > (swstep*5).U) && (hpos <= (swstep*7).U), 0.U, 255.U)
    pred := Mux((vpos > (shstep*2).U) && (vpos <= (shstep*3).U), 255.U, pinv)
    pgreen := Mux((vpos > (shstep*2).U) && (vpos <= (shstep*3).U), 255.U, pinv)
    pblue := Mux((vpos > (shstep*2).U) && (vpos <= (shstep*3).U), 0.U, ninv)
  }
  if(pt == ptFinnishFlag){
    val swidth = 1280
    val sheight = 720
    val swstep = swidth/18
    val shstep = sheight/11
    val ninv = Mux((hpos > (swstep*5).U) && (hpos <= (swstep*8).U), 0.U, 255.U)
    pred := Mux((vpos > (shstep*4).U) && (vpos <= (shstep*7).U), 0.U, ninv)
    pgreen := Mux((vpos > (shstep*4).U) && (vpos <= (shstep*7).U), 0.U, ninv)
    pblue := 255.U
  }
  if(pt == ptNorwegianFlag){
    val swidth = 1280
    val sheight = 720
    val swstep = swidth/22
    val shstep = sheight/16
    val minv = Mux((vpos > (shstep*7).U) && (vpos <= (shstep*9).U), 0.U, 255.U)
    val linv = Mux(((hpos > (swstep*6).U) && (hpos <= (swstep*7).U)) || ((hpos > (swstep*9).U) && (hpos <= (swstep*10).U)), minv, 0.U)
    val kinv = Mux((hpos > (swstep*7).U) && (hpos <= (swstep*9).U), 0.U, 255.U)
    val pgbright = Mux(((vpos > (shstep*6).U) && (vpos <= (shstep*7).U)) || ((vpos > (shstep*9).U) && (vpos <= (shstep*10).U)), kinv, linv)
    val prbright = Mux(pgbright > 0.U, 255.U, 2.U)
    val pbbright = Mux(pgbright > 0.U, 255.U, 2.U)
    val pinv = Mux((hpos > (swstep*6).U) && (hpos <= (swstep*10).U), pbbright, 0.U)
    val ninv = Mux((hpos > (swstep*7).U) && (hpos <= (swstep*9).U), 0.U, prbright)
    pred := Mux((vpos > (shstep*7).U) && (vpos <= (shstep*9).U), 0.U, ninv)
    pgreen := Mux(((vpos > (shstep*6).U) && (vpos <= (shstep*7).U)) || ((vpos > (shstep*9).U) && (vpos <= (shstep*10).U)), kinv, linv)
    pblue := Mux((vpos > (shstep*6).U) && (vpos <= (shstep*10).U), pbbright, pinv)
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
