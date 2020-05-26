import org.codehaus.commons.compiler.ICompilerFactory
import org.codehaus.janino.CompilerFactory
import java.io.File
import java.time.LocalDate
import java.util.*


fun main() {
  val filename: String = "patch_${LocalDate.now()}"
  val source: String = """
    fun test() {
      println("hello world!")
    }
  """.trimIndent()

  val compilerFactory: ICompilerFactory = CompilerFactory()
  val compiler = compilerFactory.newSimpleCompiler()
  compiler.cook(source)
//  compiler.classLoader.

  val classLoader = compilerFactory.newJavaSourceClassLoader()
//  classLoader.set
//  classLoader.setSourcePath(splitPath())
//  val clazz = classLoader.loadClass(filename)
//  val instance = clazz.newInstance()
}

fun splitPath(string: String): Array<File>? {
  val l: MutableList<File> = ArrayList()
  val st = StringTokenizer(string, File.pathSeparator)
  while (st.hasMoreTokens()) {
    l.add(File(st.nextToken()))
  }
  return l.toTypedArray()
}