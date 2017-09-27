// Interpreted values like Strings

abstract class Value

object None : Value() {
    override fun toString(): String {
        return "None"
    }
}

data class NumberType(val value: Float) : Value() {
    override fun toString(): String {
        return value.toString()
    }
}

data class StringType(val string: String): Value() {
    override fun toString(): String {
        return string
    }
}

data class BooleanType(val boolean: Boolean): Value() {
    override fun toString(): String {
        return boolean.toString()
    }
}

class FunctionType(val instructions: List<String>): Value()

class Structure(val values: HashMap<String, Value>): Value()