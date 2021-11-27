import com.caidt.util.RandomUtil
import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit
import kotlin.test.Test


fun main() {
  val cache: Cache<String, String> = Caffeine.newBuilder()
      .expireAfterAccess(1L, TimeUnit.MINUTES)
      .build {
        build(it)
      }
//    .buildAsync<String, String> { key ->
//      key
//    }
//  cache.put("dadas", "adasd")
}


fun <K, V> build(key: K): V {
  return key as V
}

data class Person(
    val id: Int,
    val age: Int,
    val money: Int
)

@Test
fun testRandom() {
  val arr = listOf<Person>()
  RandomUtil.select(arr, Person::id)
}
