fun main() {
    val fullName = FullName(FirstName("taro"), LastName("tanaka"))
    println(fullName.lastName)
}

@JvmInline
value class FirstName(private val value: String) {
    init {
        require(value.isNotEmpty()) { "1文字以上である必要があります" }
        require(!Regex("^[a-zA-Z]+$").containsMatchIn(value)) { "利用不可の文字が含まれています: $value" }
    }
}

@JvmInline
value class LastName(private val value: String){
    init {
        require(value.isNotEmpty()) { "1文字以上である必要があります" }
        require(!Regex("^[a-zA-Z]+$").containsMatchIn(value)) { "利用不可の文字が含まれています: $value" }
    }
}

data class FullName(val firstName: FirstName, val lastName: LastName)