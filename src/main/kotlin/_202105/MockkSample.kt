package _202105

class Dependency1(val value1: Int)
class Dependency2(val value2: String)

class SystemUnderTest(private val dependency1: Dependency1, private val dependency2: Dependency2) {
    fun calculate() = dependency1.value1 + dependency2.value2.toInt()
}