import kotlin.concurrent.thread

fun main() {
  val o = Object()
  val thread = thread {
    println("enter thread ${Thread.currentThread().name}")
    synchronized(o) {
      o.wait(1000L) // release the monitor
    }
    println("thread ${Thread.currentThread().name} notified")
  }

  synchronized(o) {
    o.notify()
    Thread.sleep(3000L)
    println("thread ${Thread.currentThread().name} fire notify")
  }
  thread.join()
}