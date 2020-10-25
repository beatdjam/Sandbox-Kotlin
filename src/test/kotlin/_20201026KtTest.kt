import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertAll

internal class _20201026KtTest {

    @Test
    fun 重複するパターン() {
        val target = Pair("2020/10/05","2020/11/15")

        // 各テストケースのパターン
        //   t---t
        // 1---1
        //    2---2
        //       3---3
        //  4-----4
        //     55
        //  66
        assertAll(
            { assertEquals(true, isRangeOverlap(target, Pair("2020/09/01", "2020/10/31"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("2020/10/10", "2020/11/20"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("2020/11/15", "2020/12/10"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("2020/10/01", "2020/11/30"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("2020/10/15", "2020/11/05"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("2020/10/01", "2020/10/05"))) },
        )
    }

    @Test
    fun 重複しないパターン() {
        val target = Pair("2020/10/05","2020/11/15")

        // 各テストケースのパターン
        //    t---t
        // 1-1
        //         2-2
        assertAll(
            { assertEquals(false, isRangeOverlap(target, Pair("2020/09/01", "2020/10/04"))) },
            { assertEquals(false, isRangeOverlap(target, Pair("2020/11/16", "2020/11/30"))) },
        )
    }


    @Test
    fun 重複するパターン_文字() {
        val target = Pair("C","G")
        assertAll(
            { assertEquals(true, isRangeOverlap(target, Pair("A", "E"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("D", "H"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("G", "K"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("B", "H"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("E", "F"))) },
            { assertEquals(true, isRangeOverlap(target, Pair("B", "C"))) },
        )
    }

    @Test
    fun 重複しないパターン_文字() {
        val target = Pair("C","G")
        assertAll(
            { assertEquals(false, isRangeOverlap(target, Pair("A", "B"))) },
            { assertEquals(false, isRangeOverlap(target, Pair("H", "I"))) },
        )
    }
}