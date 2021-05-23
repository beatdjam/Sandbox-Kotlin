fun main() {
    // 氏名の値オブジェクト
    val fullName = FullName(FirstName("taro"), LastName("tanaka"))
    println(fullName.lastName)

    // 通貨の値オブジェクト
    val myMoney = Money(1000.0, "JPY")
    val allowance = Money(3000.0, "JPY")
    val result = myMoney.add(allowance)
    println(result)

    try {
        val jpy = Money(1000.0, "JPY")
        val usd = Money(3000.0, "USD")
        println(jpy.add(usd))
    } catch (e : IllegalArgumentException) {
        println(e)
    }
}

@JvmInline
value class FirstName(private val value: String) {
    init {
        require(value.isNotEmpty()) { "1文字以上である必要があります" }
        require(Regex("^[a-zA-Z]+$").containsMatchIn(value)) { "利用不可の文字が含まれています: $value" }
    }
}

@JvmInline
value class LastName(private val value: String){
    init {
        require(value.isNotEmpty()) { "1文字以上である必要があります" }
        require(Regex("^[a-zA-Z]+$").containsMatchIn(value)) { "利用不可の文字が含まれています: $value" }
    }
}

data class FullName(val firstName: FirstName, val lastName: LastName)

data class Money(val amount :Double, val currency : String) {
    fun add(arg : Money) : Money {
        require(this.currency == arg.currency) {"通貨単位が異なります"}
        return this.copy(amount = this.amount + arg.amount)
    }
}