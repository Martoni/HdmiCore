package hdmicore.video
/* Horizontal, vertical Video signals generation
* inspired from fpga4fun
* and Stephen Hugg book "Designing Video Game Hardware in Verilog"
* */

import chisel3._
import chisel3.util._
import chisel3.stage.{ChiselGeneratorAnnotation, ChiselStage}

case class VideoParams(
  val H_DISPLAY: Int,// horizontal display width
  val H_FRONT: Int,  // front porch
  val H_SYNC: Int,   // sync width
  val H_BACK: Int,   // back porch
  val V_SYNC: Int,   // sync width
  val V_BACK: Int,   // back porch
  val V_TOP: Int,    // top border
  val V_DISPLAY: Int,// vertical display width
  val V_BOTTOM: Int  // bottom border
)

class HVSync(val vp: VideoParams = VideoParams(
    H_DISPLAY = 640, H_FRONT = 8, H_SYNC = 96, H_BACK = 40,
    V_SYNC = 4,  V_BACK = 25,   V_TOP = 4, V_DISPLAY = 480, V_BOTTOM = 14
    )) extends Module { // with Formal { scala version problem

  val hregsize = log2Ceil(vp.H_DISPLAY + vp.H_BACK + vp.H_FRONT  + vp.H_SYNC)
  val vregsize = log2Ceil(vp.V_DISPLAY + vp.V_TOP  + vp.V_BOTTOM + vp.V_SYNC)
  val io = IO(new Bundle {
     val hsync = Output(Bool())
     val vsync = Output(Bool())
     val display_on = Output(Bool())
     val hpos = Output(UInt(hregsize.W))
     val vpos = Output(UInt(vregsize.W))
  })

  val H_DISPLAY = vp.H_DISPLAY.U  // horizontal display width
  val H_FRONT   = vp.H_FRONT.U    // front porch
  val H_SYNC    = vp.H_SYNC.U     // sync width
  val H_BACK    = vp.H_BACK.U     // back porch
  val V_SYNC    = vp.V_SYNC.U     // sync width
  val V_BACK    = vp.V_BACK.U     // back porch
  val V_TOP     = vp.V_TOP.U      // top border
  val V_DISPLAY = vp.V_DISPLAY.U  // vertical display width
  val V_BOTTOM  = vp.V_BOTTOM.U   // bottom border
  val H_SYNC_START = H_DISPLAY + H_FRONT
  val H_SYNC_END   = H_DISPLAY + H_FRONT + H_SYNC - 1.U
  val H_MAX        = H_DISPLAY + H_BACK + H_FRONT + H_SYNC - 1.U
  val V_SYNC_START = V_DISPLAY + V_BOTTOM
  val V_SYNC_END   = V_DISPLAY + V_BOTTOM + V_SYNC - 1.U
  val V_MAX        = V_DISPLAY + V_TOP + V_BOTTOM + V_SYNC - 1.U

  println(s"H_DISPLAY $H_DISPLAY")
  println(s"V_DISPLAY $V_DISPLAY")
  println(s"hregsize $hregsize")
  println(s"vregsize $vregsize")

  val hpos_count = RegInit(0.U((hregsize).W))
  val vpos_count = RegInit(0.U((vregsize).W))
  io.vpos := vpos_count
  io.hpos := hpos_count

  io.display_on := (hpos_count < H_DISPLAY) && (vpos_count < V_DISPLAY)

  /* Horizontal counter */
  io.hsync := !((hpos_count >= H_SYNC_START) &&
                (hpos_count <= H_SYNC_END))

  val hpos_max = hpos_count === H_MAX
  val vpos_max = vpos_count === V_MAX

  hpos_count := hpos_count + 1.U
  when(hpos_max){
    hpos_count := 0.U
  }

  /* Vertical counter */
  io.vsync := !((vpos_count >= V_SYNC_START) &&
              (vpos_count <= V_SYNC_END))
  when(hpos_max) {
    vpos_count := vpos_count + 1.U
    when(vpos_max) {
      vpos_count := 0.U
    }
  }
}

object HVSyncDriver extends App {
  (new ChiselStage).execute(args,
    Seq(ChiselGeneratorAnnotation(() => new HVSync())))
}
