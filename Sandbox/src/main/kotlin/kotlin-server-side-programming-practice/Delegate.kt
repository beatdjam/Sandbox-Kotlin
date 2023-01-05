package `kotlin-server-side-programming-practice`

interface CalculationExecutor {
    val message: String
    fun calc(num1: Int, num2: Int): Int
    fun printStartMessage()
}

class CommonCalculationExecutor(override val message: String = "Calc"): CalculationExecutor {
    override fun calc(num1: Int, num2: Int): Int {
        throw IllegalStateException("Not implements")
    }

    override fun printStartMessage() {
        println("start $message")
    }
}

class AddCalculationExecutor(private val calculationExecutor: CommonCalculationExecutor): CalculationExecutor by calculationExecutor {
    override fun calc(num1: Int, num2: Int): Int {
        return num1 + num2
    }
}

fun main() {
    val executor = AddCalculationExecutor(CommonCalculationExecutor())
    executor.printStartMessage()
    println(executor.calc(8, 11))
}