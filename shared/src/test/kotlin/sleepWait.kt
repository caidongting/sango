import kotlin.concurrent.thread

fun main() {
  val o = Object()
  thread {
    println("enter thread ${Thread.currentThread().name}")
    synchronized(o) {
      o.wait() // release the monitor
//      o.notify()
      Thread.sleep(500L)
      println("thread ${Thread.currentThread().name} notified")
    }
  }

  thread {
    println("enter thread ${Thread.currentThread().name}")
    synchronized(o) {
      o.wait() // release the monitor
//      o.notify()
      Thread.sleep(500L)
      println("thread ${Thread.currentThread().name} notified")
    }
  }

  Thread.sleep(1000L)
  synchronized(o) {
    o.notifyAll()
    println("thread ${Thread.currentThread().name} fire notify")
  }
}