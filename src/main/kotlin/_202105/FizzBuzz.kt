package _202105

class FizzBuzz {
    fun fizzBuzz(value: Int): String = when {
        value % 3 == 0 && value % 5 == 0 -> "FizzBuzz"
        value % 3 == 0 -> "Fizz"
        value % 5 == 0 -> "Buzz"
        else -> value.toString()
    }
}
