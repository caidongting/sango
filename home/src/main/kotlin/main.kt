fun main() {
  val map = hashMapOf(
    1 to 1,
    2 to 2
  )
  map.mapValues { entry -> entry.value * entry.value }
}

data class Attribute<T : Comparable<T>>(
  var value: T,
  val min: T,
  val max: T
) : Comparable<Attribute<T>> {

  init {
    require(min <= value)
    require(value <= max)
  }

  override fun compareTo(other: Attribute<T>): Int {
    return value.compareTo(other.value)
  }
}