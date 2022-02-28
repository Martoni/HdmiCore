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



def cFix(v):
  return v # (v & 128) + (v & 64) + (v & 32) + (v & 16)

def main():
  b = Bitmap(1280, 720)
  for hpos in range(0, 1280):
    for vpos in range(0, 720):
      cTrig1 = 255
      cTrig2 = 510
      cTrig3 = 765
      cTrig4 = 1020
      cTrig5 = 1275
      cTrig6 = 1530
      x = (hpos*cTrig6)/1280

      if(x < cTrig1):
        pred = cTrig1
      elif(x < cTrig2):
        pred = cFix(cTrig2 - x)
      elif(x < cTrig4):
        pred = 0
      elif(x < cTrig5):
        pred = cFix(x - cTrig4)
      else:
        pred = cTrig1

      if(x < cTrig1):
        pgreen = cFix(x)
      elif(x < cTrig3):
        pgreen = cTrig1
      elif(x < cTrig4):
        pgreen = cFix(cTrig4 - x)
      else:
        pgreen = 0

  
      if(x < cTrig2):
        pblue = 0
      elif(x < cTrig3):
        pblue = cFix(x - cTrig2)
      elif(x < cTrig5):
        pblue = cTrig1
      elif(x < cTrig6):
        pblue = cFix(cTrig6 - x)
      else:
        pblue = 0

      #pred = 0
      #pblue = hpos * 255 / 1280
      #pgreen = 0

      b.setPixel(hpos, vpos, (pred, pgreen, pblue))

  n = 'rainbow'
  b.write(n+'.bmp')
  Image.open(n+'.bmp').save(n+'.png')


if __name__ == '__main__':
  main()
