import ErrorHandling_ws.CookingException.*
import arrow.core.*
import arrow.core.continuations.either
import arrow.core.continuations.nullable
import arrow.typeclasses.Semigroup
import kotlinx.coroutines.runBlocking

object Lettuce
object Knife
object Salad

fun takeFoodFromRefrigerator(): Lettuce? = null
fun getKnife(): Knife? = null
fun prepare(tool: Knife, ingredient: Lettuce): Salad? = null

// nullableブロック内でbindした値がnullになったらブロックを抜ける
// for式みたいに扱える？yieldないけど
// eitherとかoptionのブロックもあるけどその型を返せないとだめそう
// eitherTみたいなのがほしくなったりしないんだろうか
suspend fun prepareLunch(): Salad? =
    nullable {
        // nullがかえるのでこの式で終わる
        val lettuce = takeFoodFromRefrigerator().bind()
        val knife = getKnife().bind()
        prepare(knife, lettuce).bind()
    }

runBlocking {
    println(prepareLunch())
}


sealed class CookingException {
    object NastyLettuce: CookingException()
    object KnifeIsDull: CookingException()
    data class InsufficientAmountOfLettuce(val quantityInGrams : Int): CookingException()
}

fun takeFoodFromRefrigeratorEither(): Either<NastyLettuce, Lettuce> = Either.Right(Lettuce)
fun getKnifeEither(): Either<KnifeIsDull, Knife> = Either.Right(Knife)
fun lunch(knife: Knife, food: Lettuce): Either<InsufficientAmountOfLettuce, Salad> =
    Either.Left(InsufficientAmountOfLettuce(5))

// eitherの場合も同様に扱える
suspend fun prepareEither(): Either<CookingException, Salad> =
    either {
        val lettuce = takeFoodFromRefrigeratorEither().bind()
        val knife = getKnifeEither().bind()
        val salad = lunch(knife, lettuce).bind()
        salad
    }

runBlocking {
    println(prepareEither())
}

// model
sealed class ValidationError(val msg: String) {
    data class DoesNotContain(val value: String) : ValidationError("Did not contain $value")
    data class MaxLength(val value: Int) : ValidationError("Exceeded length of $value")
    data class NotAnEmail(val reasons: Nel<ValidationError>) : ValidationError("Not a valid email")
}

data class FormField(val label: String, val value: String)
data class Email(val value: String)

sealed class Strategy {
    object FailFast : Strategy()
    object ErrorAccumulation : Strategy()
}
object Rules {
    // Non Empty List(NEL)の制約を見ている
    private fun FormField.contains(needle: String): ValidatedNel<ValidationError, FormField> =
        if (value.contains(needle, false)) validNel()
        else ValidationError.DoesNotContain(needle).invalidNel()

    private fun FormField.maxLength(maxLength: Int): ValidatedNel<ValidationError, FormField> =
        if (value.length <= maxLength) validNel()
        else ValidationError.MaxLength(maxLength).invalidNel()
}