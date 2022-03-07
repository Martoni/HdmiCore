import os
from struct import pack
from PIL import Image

class Bitmap():
  def __init__(s, width, height):
    s._bfType = 19778 # Bitmap signature
    s._bfReserved1 = 0
    s._bfReserved2 = 0
    s._bcPlanes = 1
    s._bcSize = 12
    s._bcBitCount = 24
    s._bfOffBits = 26
    s._bcWidth = width
    s._bcHeight = height
    s._bfSize = 26+s._bcWidth*3*s._bcHeight
    s.clear()


  def clear(s):
    s._graphics = [(0,0,0)]*s._bcWidth*s._bcHeight


  def setPixel(s, x, y, color):
    if isinstance(color, tuple):
      if x<0 or y<0 or x>s._bcWidth-1 or y>s._bcHeight-1:
        raise ValueError('Coords out of range')
      if len(color) != 3:
        raise ValueError('Color must be a tuple of 3 elems')
      s._graphics[y*s._bcWidth+x] = (color[2], color[1], color[0])
    else:
      raise ValueError('Color must be a tuple of 3 elems')


  def write(s, file):
    with open(file, 'wb') as f:
      f.write(pack('<HLHHL', 
                   s._bfType, 
                   s._bfSize, 
                   s._bfReserved1, 
                   s._bfReserved2, 
                   s._bfOffBits)) # Writing BITMAPFILEHEADER
      f.write(pack('<LHHHH', 
                   s._bcSize, 
                   s._bcWidth, 
                   s._bcHeight, 
                   s._bcPlanes, 
                   s._bcBitCount)) # Writing BITMAPINFO
      for px in s._graphics:
        f.write(pack('<BBB', *px))
      for i in range((4 - ((s._bcWidth*3) % 4)) % 4):
        f.write(pack('B', 0))

class VideoParams:
    def __init__(self, width, height):
        self.H_DISPLAY = width
        self.V_DISPLAY = height


def Mux(c, a, b):
  if c:
    return a
  else:
    return b

def RegNext(v):
  return v

def pattern(ptIdx):
  ptIdxRainbow = 0
  ptIdxVStripes = 1
  ptIdxHStripes = 2
  ptIdxFrenchFlag = 3
  ptIdxIrishFlag = 4
  ptIdxItalianFlag = 5
  ptIdxBelgianFlag = 6
  ptIdxDutchFlag = 7
  ptIdxLuxembourgishFlag = 8
  ptIdxUkraineFlag = 9
  ptIdxSpanishFlag = 10
  ptIdxAustrianFlag = 11
  ptIdxGreekFlag = 12
  ptIdxDanishFlag = 13
  ptIdxSwedishFlag = 14
  ptIdxFinnishFlag = 15
  ptIdxNorwegianFlag = 16
  ptIdxGermanFlag = 17
  ptIdxVGradient = 18
  ptIdxHGradient = 19
  ptIdxBlackVoid = 30
  ptIdxNotSet = 31

  patternNames = {ptIdxRainbow: 'Rainbow',
      ptIdxVStripes: 'VStripes',
      ptIdxHStripes: 'HStripes',
      ptIdxFrenchFlag: 'FrenchFlag',
      ptIdxIrishFlag: 'IrishFlag',
      ptIdxItalianFlag: 'ItalianFlag',
      ptIdxBelgianFlag: 'BelgianFlag',
      ptIdxDutchFlag: 'DutchFlag',
      ptIdxLuxembourgishFlag: 'LuxembourgishFlag',
      ptIdxUkraineFlag: 'UkraineFlag',
      ptIdxSpanishFlag: 'SpanishFlag',
      ptIdxAustrianFlag: 'AustrianFlag',
      ptIdxGreekFlag: 'GreekFlag',
      ptIdxDanishFlag: 'DanishFlag',
      ptIdxSwedishFlag: 'SwedishFlag',
      ptIdxFinnishFlag: 'FinnishFlag',
      ptIdxNorwegianFlag: 'NorwegianFlag',
      ptIdxGermanFlag: 'GermanFlag',
      ptIdxVGradient: 'VGradient',
      ptIdxHGradient: 'HGradient',
      ptIdxBlackVoid: 'BlackVoid',
      ptIdxNotSet: 'NotSet'}

  n = patternNames[ptIdx].lower()
  print(n+'.png')

  video_de = 1
  vp = VideoParams(1280, 720)
  b = Bitmap(1280, 720)
  for hpos in range(0, 1280):
    for vpos in range(0, 720):
      if(not video_de):
        pred = 0
        pgreen = 0
        pblue = 0
      elif(ptIdx == ptIdxVGradient):
        x = RegNext((vpos*255)/vp.V_DISPLAY)
        pred = x
        pgreen = x
        pblue = 0
      elif(ptIdx == ptIdxHGradient):
        x = RegNext((hpos*255)/vp.H_DISPLAY)
        pred = 0
        pgreen = 0
        pblue = x
      elif(ptIdx == ptIdxRainbow):
        # generate rainbow #
        # inspired from http://blog.vermot.net/2011/11/03/generer-un-degrade-en-arc-en-ciel-en-fonction-d-une-valeur-programmatio/ #
        cTrig1 = 255
        cTrig2 = 510
        cTrig3 = 765
        cTrig4 = 1020
        cTrig5 = 1275
        cTrig6 = 1530
        x = RegNext((hpos*cTrig6)/vp.H_DISPLAY)

        if(x < cTrig1):
          pred = cTrig1
        elif(x < cTrig2):
          pred = cTrig2 - x
        elif(x < cTrig4):
          pred = 0
        elif(x < cTrig5):
          pred = x - cTrig4
        else:
          pred = cTrig1

        if(x < cTrig1):
          pgreen = x
        elif(x < cTrig3):
          pgreen = cTrig1
        elif(x < cTrig4):
          pgreen = cTrig4 - x
        else:
          pgreen = 0

        if(x < cTrig2):
          pblue = 0
        elif(x < cTrig3):
          pblue = x - cTrig2
        elif(x < cTrig5):
          pblue = cTrig1
        elif(x < cTrig6):
          pblue = cTrig6 - x
        else:
          pblue = 0

      elif(ptIdx == ptIdxVStripes):
        pred   = Mux(0 == hpos % 2, 0, 255)
        pgreen = Mux(0 == hpos % 2, 0, 255)
        pblue  = Mux(0 == hpos % 2, 0, 255)
      elif(ptIdx == ptIdxHStripes):
        pred   = Mux(0 == vpos % 2, 0, 255)
        pgreen = Mux(0 == vpos % 2, 0, 255)
        pblue  = Mux(0 == vpos % 2, 0, 255)
      elif(ptIdx == ptIdxFrenchFlag):
        swidth = 1280
        pred = Mux(hpos < (swidth/3), 0, 255)
        pgreen = Mux((hpos >= (swidth/3)) and (hpos < (swidth*2/3)), 255, 0)
        pblue = Mux(hpos < (swidth*2/3), 255, 0)
      elif(ptIdx == ptIdxIrishFlag):
        swidth = 1280
        pred = Mux(hpos < (swidth/3), 0, 255)
        pgreen = Mux(hpos < (swidth*2/3), 255, 128)
        pblue = Mux((hpos >= (swidth/3)) and (hpos < (swidth*2/3)), 255, 0)
      elif(ptIdx == ptIdxItalianFlag):
        swidth = 1280
        pred = Mux(hpos < (swidth/3), 0, 255)
        pgreen = Mux(hpos < (swidth*2/3), 255, 0)
        pblue = Mux((hpos >= (swidth/3)) and (hpos < (swidth*2/3)), 255, 0)
      elif(ptIdx == ptIdxBelgianFlag):
        swidth = 1280
        pred = Mux(hpos >= (swidth/3), 255, 0)
        pgreen = Mux((hpos >= (swidth/3)) and (hpos < (swidth*2/3)), 255, 0)
        pblue = 0
      elif(ptIdx == ptIdxDutchFlag):
        sheight = 720
        prbright = Mux(vpos < (sheight/3), 128, 255)
        pbbright = Mux(vpos < (sheight*2/3), 255, 128)
        pred = Mux(vpos < (sheight*2/3), prbright, 0)
        pgreen = Mux((vpos >= (sheight/3)) and (vpos < (sheight*2/3)), 255, 0)
        pblue = Mux(vpos < (sheight/3), 0, pbbright)
      elif(ptIdx == ptIdxLuxembourgishFlag):
        sheight = 720
        pred = Mux(vpos < (sheight*2/3), 255, 0)
        pgreen = Mux((vpos >= (sheight/3)) and (vpos < (sheight*2/3)), 255, 0)
        pblue = Mux(vpos < (sheight/3), 0, 255)
      elif(ptIdx == ptIdxGermanFlag):
        sheight = 720
        pred = Mux(vpos >= (sheight/3), 255, 0)
        pgreen = Mux(vpos < (sheight*2/3), 0, 255)
        pblue = 0
      elif(ptIdx == ptIdxSpanishFlag):
        sheight = 720
        pred = 255
        pgreen = Mux((vpos >= (sheight/4)) and (vpos < (sheight*3/4)), 255, 0)
        pblue = 0
      elif(ptIdx == ptIdxAustrianFlag):
        sheight = 720
        pred = 255
        pgreen =  Mux((vpos >= (sheight/3)) and (vpos < (sheight*2/3)), 255, 0)
        pblue = Mux((vpos >= (sheight/3)) and (vpos < (sheight*2/3)), 255, 0)
      elif(ptIdx == ptIdxGreekFlag):
        swidth = 1280
        sheight = 720
        swstep = swidth*3/80
        shstep = sheight/9
        oinv = Mux((hpos <= (swstep*10)) and (vpos > (shstep*2)) and (vpos <= (shstep*3)), 255, 0)
        pinv = Mux((hpos > (swstep*4)) and (hpos <= (swstep*6)) and (vpos <= (shstep*5)), 255, oinv)
        ninv = Mux(((hpos <= (swstep*4)) or (hpos > (swstep*6))) and (hpos <= (swstep*10)) and (vpos <= (shstep*5)), 0, 255)
        pred = Mux(vpos % (sheight*2/9) > (sheight/9), ninv, pinv)
        pgreen = Mux(vpos % (sheight*2/9) > (sheight/9), ninv, pinv)
        pblue = 255
      elif(ptIdx == ptIdxDanishFlag):
        swidth = 1280
        sheight = 720
        swstep = swidth*4/37
        shstep = sheight/7
        pinv = Mux((hpos > (swstep*3)) and (hpos <= (swstep*4)), 255, 0)
        pred = 255
        pgreen = Mux((vpos > (shstep*3)) and (vpos <= (shstep*4)), 255, pinv)
        pblue = Mux((vpos > (shstep*3)) and (vpos <= (shstep*4)), 255, pinv)
      elif(ptIdx == ptIdxSwedishFlag):
        swidth = 1280
        sheight = 720
        swstep = swidth/16
        shstep = sheight/5
        pinv = Mux((hpos > (swstep*5)) and (hpos <= (swstep*7)), 255, 0)
        ninv = Mux((hpos > (swstep*5)) and (hpos <= (swstep*7)), 0, 255)
        pred = Mux((vpos > (shstep*2)) and (vpos <= (shstep*3)), 255, pinv)
        pgreen = Mux((vpos > (shstep*2)) and (vpos <= (shstep*3)), 255, pinv)
        pblue = Mux((vpos > (shstep*2)) and (vpos <= (shstep*3)), 0, ninv)
      elif(ptIdx == ptIdxFinnishFlag):
        swidth = 1280
        sheight = 720
        swstep = swidth/18
        shstep = sheight/11
        ninv = Mux((hpos > (swstep*5)) and (hpos <= (swstep*8)), 0, 255)
        pred = Mux((vpos > (shstep*4)) and (vpos <= (shstep*7)), 0, ninv)
        pgreen = Mux((vpos > (shstep*4)) and (vpos <= (shstep*7)), 0, ninv)
        pblue = 255
      elif(ptIdx == ptIdxNorwegianFlag):
        swidth = 1280
        sheight = 720
        swstep = swidth/22
        shstep = sheight/16
        minv = Mux((vpos > (shstep*7)) and (vpos <= (shstep*9)), 0, 255)
        linv = Mux(((hpos > (swstep*6)) and (hpos <= (swstep*7))) or ((hpos > (swstep*9)) and (hpos <= (swstep*10))), minv, 0)
        kinv = Mux((hpos > (swstep*7)) and (hpos <= (swstep*9)), 0, 255)
        pgbright = Mux(((vpos > (shstep*6)) and (vpos <= (shstep*7))) or ((vpos > (shstep*9)) and (vpos <= (shstep*10))), kinv, linv)
        prbright = Mux(pgbright > 0, 255, 128)
        pbbright = Mux(pgbright > 0, 255, 128)
        pinv = Mux((hpos > (swstep*6)) and (hpos <= (swstep*10)), pbbright, 0)
        ninv = Mux((hpos > (swstep*7)) and (hpos <= (swstep*9)), 0, prbright)
        pred = Mux((vpos > (shstep*7)) and (vpos <= (shstep*9)), 0, ninv)
        pgreen = Mux(((vpos > (shstep*6)) and (vpos <= (shstep*7))) or ((vpos > (shstep*9)) and (vpos <= (shstep*10))), kinv, linv)
        pblue = Mux((vpos > (shstep*6)) and (vpos <= (shstep*10)), pbbright, pinv)
      elif(ptIdx == ptIdxUkraineFlag):
       # blue #00 57 b7, yellow #ff d7 00  #
        sheight = 720
        pred  = Mux(vpos <= (sheight/2), 0x00, 0xFF)
        pgreen= Mux(vpos <= (sheight/2), 0x00, 0xFF)
        pblue = Mux(vpos <= (sheight/2), 0xFF, 0x00)
      else:
        pred   = 0
        pgreen = 0
        pblue  = 0

      b.setPixel(hpos, vp.V_DISPLAY-1-vpos, (pred, pgreen, pblue))

  b.write(n+'.bmp')
  Image.open(n+'.bmp').save(n+'.png')
  os.remove(n+'.bmp')


if __name__ == '__main__':
  for p in range(20):
    pattern(p)
