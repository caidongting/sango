import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking


fun main() = runBlocking {
  launch {
    delay(1000L)
    println("World")
  }

  coroutineScope { // Creates a coroutine scope
    launch {
      delay(500L)
      println("Task from nested launch")
    }

    delay(100L)
    println("Task from coroutine scope") // This line will be printed before the nested launch
  }

  println("hello")
}
