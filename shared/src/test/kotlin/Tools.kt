import com.github.benmanes.caffeine.cache.Cache
import com.github.benmanes.caffeine.cache.Caffeine
import java.util.concurrent.TimeUnit


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