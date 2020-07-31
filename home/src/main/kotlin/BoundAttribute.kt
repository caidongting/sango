data class BoundAttribute<T : Comparable<T>>(
  var value: T,
  val min: T,
  val max: T
) : Comparable<BoundAttribute<T>> {

  init {
    require(min <= value)
    require(value <= max)
  }

  override fun compareTo(other: BoundAttribute<T>): Int {
    return value.compareTo(other.value)
  }
}

fun main() {
  val arr = listOf(1, 2, 3, 4, 5)
  println(String.format("%s", arr))
}