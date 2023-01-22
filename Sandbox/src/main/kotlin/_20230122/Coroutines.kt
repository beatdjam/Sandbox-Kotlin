package _20230122

import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.async

fun main() {
    basic()
    runBlockingScopeBuilder()
    asyncScopeBuilder()
}

// コルーチンの基本
private fun basic() {
    GlobalScope.launch {
        delay(1000L)
        println("2")
    }
    println("1")
    Thread.sleep(2000L)
    println("3")
}

// コルーチンスコープビルダー
private fun runBlockingScopeBuilder() {
    // スコープ内のcoroutinesがすべて完了するまで待つ
    runBlocking {
        launch {
            delay(1000L)
            println("2")
        }
        println("1")
    }
}

private fun asyncScopeBuilder() {
    runBlocking {
        val result = async {
            delay(2000L)
            var sum = 0
            for (i in 1..10) {
                sum += i
            }
            sum
        }
        val result2 = async {
            delay(2000L)
            var sum = 0
            for (i in 1..10) {
                sum += i
            }
            sum
        }
        println("calc")
        println("${result.await() + result2.await()}")
    }
}