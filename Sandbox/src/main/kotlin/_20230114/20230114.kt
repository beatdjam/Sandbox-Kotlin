package _20230114
@OptIn(ExperimentalStdlibApi::class)
fun sortA(list: List<Int>): List<Int> {
    return if(list.size > 1) {
        val pivot = list[list.size / 2 - 1]
        val mid =  list.filter { pivot == it }
        val left = sortA(list.filter { pivot > it })
        val right = sortA(list.filter { pivot < it })
        buildList {
            this.addAll(left)
            this.addAll(mid)
            this.addAll(right)
        }
    } else {
        list
    }
}

fun sortB(list: List<Int>): List<Int> {
    val mut = list.toMutableList()
    for(i in 0 until mut.size - 1) {
        for(j in i + 1 until mut.size - 1) {
            val a = mut[i]
            val b = mut[j]
            if(a > b) {
                mut[i] = b
                mut[j] = a
            }
        }
    }
    return mut
}

fun main() {
    val list =  listOf(2 ,1, 1,3)
    println(sortA(list))
    println(sortB(list))
}


