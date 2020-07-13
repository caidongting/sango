import java.time.*
import java.time.temporal.ChronoField
import java.time.temporal.TemporalField

fun main() {
//  val n = -1
//  println(tableSizeFor(n))
//  val a = 1
//  val b = 2
  val dateTime = Instant.now().atZone(ZoneId.systemDefault())
  val year = dateTime[ChronoField.YEAR]
  val month = getLocalField(dateTime, ChronoField.MONTH_OF_YEAR)
  val day = getLocalField(dateTime, ChronoField.DAY_OF_MONTH)
  val hour = getLocalField(dateTime, ChronoField.HOUR_OF_DAY)
  val minute = getLocalField(dateTime, ChronoField.MINUTE_OF_HOUR)
  val second = getLocalField(dateTime, ChronoField.SECOND_OF_MINUTE)
  println("${year}年${month}月${day}日${hour}时${minute}分${second}秒")
}

const val MAXIMUM_CAPACITY = 1 shl 30

fun tableSizeFor(cap: Int): Int {
  var n = cap - 1
  n = n or (n ushr 1)
  n = n or (n ushr 2)
  n = n or (n ushr 4)
  n = n or (n ushr 8)
  n = n or (n ushr 16)
  return when {
    n < 0 -> 1
    n >= MAXIMUM_CAPACITY -> MAXIMUM_CAPACITY
    else -> n + 1
  }
}

fun currentHour(): Int {
  return Instant.now().atZone(ZoneId.systemDefault()).hour
}

fun getCurrentHour(): Int {
//        Calendar c = Calendar.getInstance();
//        return c.get(Calendar.HOUR_OF_DAY);
  return getLocalField(Instant.now(), ChronoField.HOUR_OF_DAY)
}

fun getHour(second: Int): Int {
  val instant = Instant.ofEpochSecond(second.toLong())
  //        Calendar c = Calendar.getInstance();
//        c.setTime(new Date(second * SECOND_MS));
//        return c.get(Calendar.HOUR_OF_DAY);
  return getLocalField(instant, ChronoField.HOUR_OF_DAY)
}

/**
 * 获取instant中本地field属性
 *
 * @param instant instant
 * @param field field of instant
 * @return value of field
 */
fun getLocalField(instant: Instant, field: TemporalField): Int {
  return getLocalField(instant.atZone(ZoneId.systemDefault()), field)
}

fun getLocalField(dateTime: ZonedDateTime, field: TemporalField): Int {
  return dateTime[field]
}
