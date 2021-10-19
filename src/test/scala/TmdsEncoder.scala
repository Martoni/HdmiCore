package hdmicore

import org.scalatest._
import chiseltest._
import chiseltest.formal._
import chisel3._
import chisel3.util.PopCount
import org.scalatest.flatspec.AnyFlatSpec

import scala.util.control.Breaks._
import java.io._
import scala.util.Random


/**
 * TMDSEncoder class test
 * For documentation about TMDS encoding see wikipedia :
 * https://en.wikipedia.org/wiki/Transition-minimized_differential_signaling
 * Or this google doc
 * https://docs.google.com/document/d/1v7AJK4cVG3uDJo_rn0X9vxMvBwXKBSL1VaJgiXgFo5A/edit#
 */

class TMDSEncoderSpec extends FlatSpec with ChiselScalatestTester with Matchers {
  behavior of "TMDSEncoder"

  it should " return fixed patterns if de is false " in {
    test(new TMDSEncoder()) { dut =>
      dut.io.en.poke(false.B)
      dut.io.ctrl.poke(0.U)
      dut.io.din.poke(0.U)
      dut.clock.step(1)
      dut.io.dout.expect("b1101010100".U(10.W))
      dut.io.ctrl.poke(1.U)
      dut.clock.step(1)
      dut.io.dout.expect("b0010101011".U(10.W))
      dut.io.ctrl.poke(2.U)
      dut.clock.step(1)
      dut.io.dout.expect("b0101010100".U(10.W))
      dut.io.ctrl.poke(3.U)
      dut.clock.step(1)
      dut.io.dout.expect("b1010101011".U(10.W))
    }
  }



}

class TMDSFormalSpec extends Module {
  val dut = Module(new TMDSEncoder)
  val io = IO(chiselTypeOf(dut.io))
  io <> dut.io

  // the number of ones and zeros should always be balanced
  val ones = dontTouch(WireInit(PopCount(dut.io.dout)))
  assert(ones === 5.U || ones === 6.U)
}

class TMDSEncoderFormalTest extends AnyFlatSpec with ChiselScalatestTester with Formal {
  "TMDSEncoder" should "pass a bounded check" in {
    verify(new TMDSFormalSpec, Seq(BoundedCheck(4), CVC4EngineAnnotation))
  }
}

