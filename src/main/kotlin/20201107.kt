import java.io.PrintWriter
import kotlin.math.sqrt

val pw = PrintWriter(System.out)
fun main(args : Array<String>) {
    func()
    pw.flush()
}

fun func() {
    val s = next()
        .replace("eraser","")
        .replace("erase","")
        .replace("dreamer","")
        .replace("dream","")
    println( if(s.isEmpty()) "YES" else "NO")
}


// 入力取得
fun next() = readLine()!!
fun nextInt() = next().toInt()
fun nextLong() = next().toLong()
fun nextDouble() = next().toDouble()
fun listOfString() = next().split(" ")
fun listOfInt() =listOfString().map { it.toInt() }
fun listOfLong() =listOfString().map { it.toLong() }
fun listOfDouble() =listOfString().map { it.toDouble() }

// 約数のList
fun divisor(value : Long) : List<Long> {
    val max = sqrt(value.toDouble()).toLong()
    return (1..max)
        .filter { value % it == 0L }
        .map { listOf(it, value / it) }
        .flatten()
        .sorted()
}

// 範囲内の素数を取得
// fromだけ指定すると戻り値の個数で素数判定ができる
fun prime(from : Long, to : Long = from) : List<Long> = (from..to).filter { i ->
    val max = sqrt(i.toDouble()).toLong()
    (2..max).all { j ->  i % j != 0L }
}

// 素因数分解
fun decom(value : Long) : List<Long>{
    if (value == 1L) return listOf(1)
    val max = sqrt(value.toDouble()).toLong()
    return prime(2, max).filter { value % it == 0L }
}

// 最大公約数
fun gcd(a : Long, b : Long) : Long = if (a % b == 0L) b else gcd(b, a % b)

// 文字列を入れ替え
fun swap(base : String, a : String, b : String) : String = base.map {
    when (it) {
        a.toCharArray()[0] -> b
        b.toCharArray()[0] -> a
        else -> it.toString()
    }
}.joinToString()

fun println(value : Any) {
    pw.println(value)
}