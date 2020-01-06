fun main() {
  val map = hashMapOf(
    1 to 1,
    2 to 2
  )
  map.mapValues { entry -> entry.value * entry.value }
}