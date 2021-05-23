package _202105

import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class FizzBuzzTest {
    @Test
    internal fun 数字を渡した時に数字を出力する() {
        val fizzBuzz = FizzBuzz()
        val result = fizzBuzz.fizzBuzz(1)
        assertEquals("1", result)
    }

    @Test
    internal fun `3で割り切れるときはFizzを返す`() {
        val fizzBuzz = FizzBuzz()
        val result = fizzBuzz.fizzBuzz(3)
        assertEquals("Fizz", result)
    }

    @Test
    internal fun `5で割り切れるときはBuzzを返す`() {
        val fizzBuzz = FizzBuzz()
        val result = fizzBuzz.fizzBuzz(5)
        assertEquals("Buzz", result)
    }

    @Test
    internal fun `3と5で割り切れるときはFizzBuzzを返す`() {
        val fizzBuzz = FizzBuzz()
        val result = fizzBuzz.fizzBuzz(15)
        assertEquals("FizzBuzz", result)
    }

    @ParameterizedTest
    @CsvSource(
        "1, 1",
        "3, Fizz",
        "5, Buzz",
        "15, FizzBuzz"
    )
    fun parameterized(value: Int, expected: String) {
        val fizzBuzz = FizzBuzz()
        val result = fizzBuzz.fizzBuzz(value)
        assertEquals(expected, result)
    }
}