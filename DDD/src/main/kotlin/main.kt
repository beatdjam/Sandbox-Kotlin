fun main() {
    val fullName = FullName(FirstName("taro"), LastName("tanaka"))
    println(fullName.lastName)
}

@JvmInline
value class FirstName(val value: String)

@JvmInline
value class LastName(val  value: String)

data class FullName(val firstName: FirstName, val lastName: LastName)