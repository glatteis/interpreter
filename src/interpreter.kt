import java.util.*

data class Call(val function: FunctionType, val values: MutableMap<String, Value>)

val callStack = Stack<Call>()

fun interpret(function: FunctionType, arguments: Map<String, Value>): Value {
    callStack.push(Call(function, arguments.toMutableMap()))
    everyLine@ for (line in function.instructions) {
        if (line.isBlank()) continue@everyLine
        val result = interpretOperations(line)
        if (result != None) {
            return result
        }
    }
    return None
}

val ops = listOf("<-", "#", "==", "!=", "*", "/", "+", "-", "..", "xor")

fun interpretOperations(inOperations: String): Value {
    val operations = inOperations.trim()
    val (nextOperation, nextOperationLocation) = findNext(operations, ops)
    when (nextOperation) {
        // Nothing
        "" -> {
            return identify(operations)
        }
        // Return Statement
        "<-" -> {
            return interpretOperations(operations.substring(2))
        }
        // Native functions
        "#" -> {
            val returnValue = interpretOperations(operations.substring(1))
            doOperation(nextOperation, listOf(returnValue))
            return None
        }
        // Operators
        "+", "-", "*", "/", "..", "==", "!=", "xor" -> {
            val argument1 = interpretOperations(operations.substring(0, nextOperationLocation))
            val argument2 = interpretOperations(operations.substring(nextOperationLocation + nextOperation.length))
            return doOperation(nextOperation, listOf(argument1, argument2))
        }
    }
    return None
}

fun doOperation(operation: String, values: List<Value>): Value {
    when (operation) {
        "#" -> {
            println(values[0].toString())
            return None
        }
        "+", "-", "*", "/" -> {
            val a = values[0]
            val b = values[1]
            a as NumberType
            b as NumberType
            return NumberType(when (operation) {
                "+" -> {
                    a.value + b.value
                }
                "-" -> {
                    a.value - b.value
                }
                "*" -> {
                    a.value * b.value
                }
                "/" -> {
                    a.value / b.value
                }
                else -> {
                    throw RuntimeException()
                }
            })
        }
        ".." -> {
            val a = values[0]
            val b = values[1]
            a as StringType
            b as StringType
            return StringType(a.string + b.string)
        }
        "==", "!=" -> {
            val a = values[0]
            val b = values[1]
            return BooleanType(when (operation) {
                "==" -> {
                    a == b
                }
                "!=" -> {
                    a != b
                }
                else -> {
                    throw RuntimeException()
                }
            })
        }
        "xor" -> {
            val a = values[0]
            val b = values[1]
            a as BooleanType
            b as BooleanType
            return BooleanType(a.boolean xor b.boolean)
        }

    }
    return None
}

fun findNext(s: String, args: List<String>): Pair<String, Int> {
    var min = Int.MAX_VALUE
    var argmin = -1
    args.forEachIndexed { i, a ->
        val foundIndex = s.indexOf(a)
        if (foundIndex != -1 && foundIndex < min) {
            min = foundIndex
            argmin = i
        }
    }
    if (argmin == -1) {
        return Pair("", -1)
    }
    return Pair(args[argmin], min)
}


fun identify(identifier: String): Value {
    if (identifier == "None") {
        return None
    }
    if (identifier == "true") {
        return BooleanType(true)
    }
    if (identifier == "false") {
        return BooleanType(false)
    }
    if (identifier.startsWith("\"") && identifier.endsWith("\"")) {
        return StringType(identifier.substring(1, identifier.length - 1))
    }
    if (identifier.toFloatOrNull() != null) {
        return NumberType(identifier.toFloat())
    }
    if (callStack.peek().values.containsKey(identifier)) {
        return callStack.peek().values[identifier]!!
    }
    throw RuntimeException("Don't know identifier: $identifier")
}