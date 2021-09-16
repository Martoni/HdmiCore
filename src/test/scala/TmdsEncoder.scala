package HdmiCore

import org.scalatest._
import chiseltest._
import chisel3._

import chiseltest.experimental.TestOptionBuilder._
import chiseltest.internal.VerilatorBackendAnnotation
import scala.util.control.Breaks._
import java.io._ // for files read/write access
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
