package hdmicore.video

import chisel3._
import fpgamacro.gowin.PLLParams

case class VideoMode(
  val params: VideoParams,
  val pll: PLLParams
)

package object VideoConsts {
  val p25920khz  = PLLParams(IDIV_SEL = 4, FBDIV_SEL = 23, ODIV_SEL = 8, DYN_SDIV_SEL =  4) // DYN_SDIV_SEL = 10
  val p27000khz  = PLLParams(IDIV_SEL = 0, FBDIV_SEL =  4, ODIV_SEL = 8, DYN_SDIV_SEL = 10)
  val p40000khz  = PLLParams(IDIV_SEL = 4, FBDIV_SEL = 36, ODIV_SEL = 4, DYN_SDIV_SEL = 16)
  val p51400khz  = PLLParams(IDIV_SEL = 1, FBDIV_SEL = 18, ODIV_SEL = 4, DYN_SDIV_SEL = 20)
  val p65000khz  = PLLParams(IDIV_SEL = 0, FBDIV_SEL = 11, ODIV_SEL = 2, DYN_SDIV_SEL = 26)
  val p74250khz  = PLLParams(IDIV_SEL = 3, FBDIV_SEL = 54, ODIV_SEL = 2, DYN_SDIV_SEL = 30)
  val p85500khz  = PLLParams(IDIV_SEL = 3, FBDIV_SEL = 62, ODIV_SEL = 2, DYN_SDIV_SEL = 34)
  val p106500khz = PLLParams(IDIV_SEL = 2, FBDIV_SEL = 58, ODIV_SEL = 2, DYN_SDIV_SEL = 42)
  val p108000khz = PLLParams(IDIV_SEL = 0, FBDIV_SEL = 19, ODIV_SEL = 2, DYN_SDIV_SEL = 44)

  // to be checked
  // D: 27.00 MHz, H: 31.469 kHz, V: 59.94 Hz
  val m720x480 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 720, H_FRONT = 16,
      H_SYNC = 62, H_BACK = 60,
      V_SYNC = 6,  V_BACK = 30,
      V_TOP = 9, V_DISPLAY = 480,
      V_BOTTOM = 30
    ),
    pll = p27000khz
  )

  // D: 27.00 MHz, H: 31.250 kHz, V: 50.00 Hz
  val m720x576 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 720, H_FRONT = 12,
      H_SYNC = 64, H_BACK = 68,
      V_SYNC = 5,  V_BACK = 39,
      V_TOP = 5, V_DISPLAY = 576,
      V_BOTTOM = 39
    ),
    pll = p27000khz
  )

  // D: 25,92 MHz, only for supported displays
  val m800x480 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 800, H_FRONT = 210,
      H_SYNC = 1, H_BACK = 182,
      V_SYNC = 5, V_BACK = 0,
      V_TOP = 45, V_DISPLAY = 480,
      V_BOTTOM = 0
    ),
    pll = p25920khz
  )

  // to be checked
  // D: 40.00 MHz, H: 37.879 kHz, V: 60.32 Hz
  val m800x600 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 800, H_FRONT = 40,
      H_SYNC = 128, H_BACK = 88,
      V_SYNC = 4,  V_BACK = 23,
      V_TOP = 1, V_DISPLAY = 600,
      V_BOTTOM = 23
    ),
    pll = p40000khz
  )

  // D: 51.40 MHz, H: 38.280 kHz, V: 60.00 Hz
  val m1024x600 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 1024, H_FRONT = 24,
      H_SYNC = 136, H_BACK = 160,
      V_SYNC = 6,  V_BACK = 29,
      V_TOP = 3, V_DISPLAY = 600,
      V_BOTTOM = 29
    ),
    pll = p51400khz
  )

  // D: 65.00 MHz, H: 48.363 kHz, V: 60.00 Hz
  val m1024x768 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 1024, H_FRONT = 24,
      H_SYNC = 136, H_BACK = 160,
      V_SYNC = 6,  V_BACK = 29,
      V_TOP = 3, V_DISPLAY = 768,
      V_BOTTOM = 29
    ),
    pll = p65000khz
  )

  // D: 74.25 MHz, H: 45.000 kHz, V: 60.00 Hz
  val m1280x720 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 1280, H_FRONT = 110,
      H_SYNC = 40, H_BACK = 220,
      V_SYNC = 5,  V_BACK = 20,
      V_TOP = 5, V_DISPLAY = 720,
      V_BOTTOM = 20
    ),
    pll = p74250khz
  )

  // D: 85.50 MHz, H: 47.700 kHz, V: 60.00 Hz
  val m1360x768 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 1360, H_FRONT = 64,
      H_SYNC = 112, H_BACK = 256,
      V_SYNC = 6,  V_BACK = 18,
      V_TOP = 3, V_DISPLAY = 768,
      V_BOTTOM = 18
    ),
    pll = p85500khz
  )

  // D: 85.50 MHz, H: 47.880 kHz, V: 60.00 Hz
  val m1366x768 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 1366, H_FRONT = 70,
      H_SYNC = 143, H_BACK = 213,
      V_SYNC = 3,  V_BACK = 24,
      V_TOP = 3, V_DISPLAY = 768,
      V_BOTTOM = 24
    ),
    pll = p85500khz
  )

  // D: 106.50 MHz, H: 56.040 kHz, V: 60.00 Hz
  val m1440x900 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 1440, H_FRONT = 80,
      H_SYNC = 152, H_BACK = 232,
      V_SYNC = 6,  V_BACK = 25,
      V_TOP = 3, V_DISPLAY = 900,
      V_BOTTOM = 25
    ),
    pll = p106500khz
  )

  // D: 108.00 MHz, H: 63.981 kHz, V: 60.02 Hz
  val m1280x1024 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 1280, H_FRONT = 48,
      H_SYNC = 112, H_BACK = 248,
      V_SYNC = 3,  V_BACK = 38,
      V_TOP = 1, V_DISPLAY = 1024,
      V_BOTTOM = 38
    ),
    pll = p108000khz
  )

  // D: 108.00 MHz, H: 60.000 kHz, V: 60.00 Hz
  val m1600x900 = VideoMode(
    params = VideoParams(
      H_DISPLAY = 1600, H_FRONT = 24,
      H_SYNC = 80, H_BACK = 96,
      V_SYNC = 3,  V_BACK = 96,
      V_TOP = 1, V_DISPLAY = 900,
      V_BOTTOM = 96
    ),
    pll = p108000khz
  )
}
