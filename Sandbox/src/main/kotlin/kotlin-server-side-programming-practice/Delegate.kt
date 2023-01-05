package `kotlin-server-side-programming-practice`

import kotlin.reflect.KProperty

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


class DelegateWithMessage<T> {
    private var value: T? = null

    operator fun getValue(thisRef: Any?, property: KProperty<*>): T {
        println("${property.name}を取得します")
        return value!!
    }

    operator fun setValue(thisRef: Any?, property: KProperty<*>, value: T) {
        println("${property.name}を設定します")
        this.value = value
    }
}

class Person {
    var name : String by DelegateWithMessage()
    var address : String by DelegateWithMessage()
}

fun main() {
    val executor = AddCalculationExecutor(CommonCalculationExecutor())
    executor.printStartMessage()
    println(executor.calc(8, 11))
    val person = Person()
    person.name = "taro"
    person.address = "nihon"
    println(person.name)
    println(person.address)
    println(person)
}
