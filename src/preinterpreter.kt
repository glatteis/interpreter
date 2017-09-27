import java.io.File

fun preInterpret(file: File) {
    val codeInText = file.readText()
    val lines = codeInText.split("\n")
    val result = interpret(FunctionType(lines), HashMap())
    if (result != None) {
        println("-> " + result)
    }
}