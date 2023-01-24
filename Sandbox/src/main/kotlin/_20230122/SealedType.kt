package _20230122

// 型で認証・認可チェックを守るメモ
// https://speakerdeck.com/yuitosato/xing-taketehakuwojian-rasou-kotlinnoxing-hawawoshi-tutashi-jian-taihusehuensiniarinku?slide=55
// throwじゃなくてEitherとかResultがほしいな
@JvmInline
value class Id(val value: String)

sealed interface Authenticated {
 val id : Id
}

private data class AuthenticatedImpl(override val id: Id): Authenticated

class Checker {
 fun  checkAuthorized(id: Id): Authenticated {
  if (id.value == "OK") return AuthenticatedImpl(id)
  throw IllegalStateException("権限なし")
 }
}