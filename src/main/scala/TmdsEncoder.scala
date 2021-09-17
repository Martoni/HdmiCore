package hdmicore

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

  /* ones counter for input data */
  val n_one_din = PopCount(io.din)
  /* create xor encodings */
  def xorfct(value: UInt): UInt = {
    value.getWidth match {
      case 1 => value(0)
      case s => val res = xorfct(VecInit(value.asBools.drop(1)).asUInt)
          value.asBools.head ^ res.asBools.head ## res
    }
  }
  val xored = 1.U(1.W) ## xorfct(io.din)

  /* create xnor encodings */
  def xnorfct(value: UInt): UInt = {
    value.getWidth match {
      case 1 => value(0)
      case s => val res = xnorfct(VecInit(value.asBools.drop(1)).asUInt)
          !(value.asBools.head ^ res.asBools.head) ## res
    }
  }
  val xnored = 0.U(1.W) ## xnorfct(io.din)

  /* use xnored or xored data based on the ones */
  val q_m = Mux(
    (n_one_din > 4.U) || (n_one_din === 4.U && io.din(0) === 0.U),
    xnored, xored)

  /* ones counter for internal data */
  val diff = PopCount(q_m).asSInt - 4.S
  val diffSize = diff.getWidth

  val disparitySize = 4
  val disparityReg = RegInit(0.S(disparitySize.W))
  val doutReg = RegInit("b1010101011".U(10.W))
  when(io.en === false.B){
    disparityReg := 0.S
    doutReg := "b1010101011".U(10.W)
    switch(io.ctrl){
        is("b00".U(2.W)){doutReg := "b1101010100".U(10.W)}
        is("b01".U(2.W)){doutReg := "b0010101011".U(10.W)}
        is("b10".U(2.W)){doutReg := "b0101010100".U(10.W)}
    }
  }.otherwise{
    when(disparityReg === 0.S || diff === 0.S){
      /* xnored data */
      when(q_m(8) === false.B){
        doutReg := "b10".U(2.W) ## q_m(7, 0)
        disparityReg := disparityReg - diff
      }.otherwise{
        doutReg := "b01".U(2.W) ## q_m(7, 0)
        disparityReg := disparityReg + diff
      }
    }.elsewhen( (!diff(diffSize-1) && !disparityReg(disparitySize - 1))
              || (diff(diffSize-1) && disparityReg(disparitySize - 1))){
      doutReg := 1.U(1.W) ## q_m(8) ## ~q_m(7, 0)
      when(q_m(8)){
        disparityReg := disparityReg + 1.S - diff
      }.otherwise{
        disparityReg := disparityReg - diff
      }
    }.otherwise{
      doutReg := 0.U(1.W) ## q_m
      when(q_m(8)){
        disparityReg := disparityReg + diff
      }.otherwise{
        disparityReg := disparityReg - 1.S + diff
      }
    }
  }
  io.dout := doutReg
}

object TMDSEncoder extends App {
  (new ChiselStage).execute(args,
    Seq(ChiselGeneratorAnnotation(() => new TMDSEncoder())))
}
