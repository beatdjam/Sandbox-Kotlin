package _202105

import io.mockk.every
import io.mockk.mockk
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

internal class SystemUnderTestTest {

    @Test
    fun calculateAddsValues() {
        val doc1 = mockk<Dependency1>()
        val doc2 = mockk<Dependency2>()

        every { doc1.value1 } returns 5
        every { doc2.value2 } returns "6"

        val sut = SystemUnderTest(doc1, doc2)

        assertEquals(11, sut.calculate())
    }
}