import ErrorHandling_ws.CookingException.*
import arrow.core.Either
import arrow.core.continuations.either
import arrow.core.continuations.nullable
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