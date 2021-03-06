import org.codehaus.commons.compiler.ICompilerFactory
import org.codehaus.commons.compiler.util.ResourceFinderClassLoader
import org.codehaus.commons.compiler.util.resource.*
import org.codehaus.janino.CompilerFactory
import java.io.File
import java.util.*


fun main() {
//  val filename: String = "patch_${LocalDate.now()}"
//  val source: String = """
//    fun test() {
//      println("hello world!")
//    }
//  """.trimIndent()

  val compilerFactory: ICompilerFactory = CompilerFactory()
  val compiler = compilerFactory.newCompiler()

// Store generated .class files in a Map:
  val classes: Map<String, ByteArray> = HashMap()
  compiler.setClassFileCreator(MapResourceCreator(classes))

  val finder = object : ResourceFinder() {
    override fun findResource(resourceName: String?): Resource {
      TODO("Not yet implemented")
    }
  }

// Now compile two units from strings:
  compiler.compile(
    arrayOf(
      StringResource(
        "pkg1/A.java",
        """
          package pkg1; 
          public class A { 
            public static int meth() { 
              return pkg2.B.meth(); 
            }
          }
        """.trimIndent()
      ),
      StringResource(
        "pkg2/B.java",
        "package pkg2; public class B { public static int meth() { return 77;            } }"
      )
    )
  )

  // Set up a class loader that uses the generated classes.
  val cl: ClassLoader = ResourceFinderClassLoader(
    MapResourceFinder(classes),  // resourceFinder
    ClassLoader.getSystemClassLoader() // parent
  )

  println(cl.loadClass("pkg1.A").getDeclaredMethod("meth").invoke(null))
//  compiler.compile(source)

  val classLoader = compilerFactory.newJavaSourceClassLoader()
  classLoader.setSourceFinder(finder)
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

//fun <E> getList(request: WebRequest, field: String, clazz: Class<E>): List<E>? {
//  val parameter: String = request.getParameter(field) ?: return emptyList()
//  val result: MutableList<E> = ArrayList()
//  val strings = parameter.split("|").toTypedArray()
//  for (string in strings) {
//    val constructor = clazz.getConstructor(String::class.java)
//    val e = constructor.newInstance(string)
//    result.add(e)
//  }
//  return result
//}