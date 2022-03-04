cat src/main/scala/PatternExample.scala | \
  sed s/'} \.elsewhen'/'elif'/g | sed s/'} \.otherwise'/'else'/g | \
  sed s/'}\.elsewhen'/'elif'/g |  sed s/'}\.otherwise'/'else'/g | \
  sed s/'when'/'if'/g | sed s/'{$'/':'/g | sed s/'==='/'=='/g | sed s/':='/'='/g | \
  sed s/'\.U'//g | sed s/'val '/''/g | sed s/'^  '/'      '/g | sed s/'}$'/''/g | \
  sed 's|/\*|#|g' | sed 's|\*/|#|g' | sed 's|// |# |g' | sed 's|//-|#-|g' | \
  sed s/'!'/'not '/g | sed s/'&&'/'and'/g | sed s/'||'/'or'/g
