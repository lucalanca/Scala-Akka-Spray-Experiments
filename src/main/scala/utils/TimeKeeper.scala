package utils

object TimeKeeper {
  var s = Long.MinValue

  def start() = { s = System.nanoTime() }
  def stop()  = { println("time: "+(System.nanoTime-s)/1e6+"ms"); s = Long.MinValue }
}
