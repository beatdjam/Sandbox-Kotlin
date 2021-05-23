package pdfbox

import org.apache.pdfbox.cos.COSDictionary
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject
import org.apache.pdfbox.util.Matrix
import java.awt.Color
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream

/**
 * ページにテキストを書き込む
 *
 * @param s
 * @param font
 * @param fontSize
 * @param tx
 * @param ty
 */
fun PDPageContentStream.writeText(s: String, font: PDFont, fontSize: Float, tx: Float, ty: Float) {
    this.beginText()
    this.setFont(font, fontSize)
    this.newLineAtOffset(tx, ty)
    this.showText(s)
    this.endText()
}

/**
 * 指定した座標を文字列の終端として右寄せで文字を描画する
 *
 * @param s
 * @param font
 * @param fontSize
 * @param tx
 * @param ty
 */
fun PDPageContentStream.writeTextAlignRight(
    s: String,
    font: PDFont,
    fontSize: Float,
    tx: Float,
    ty: Float
) {
    this.writeText(s, font, fontSize, tx - font.width(s, fontSize), ty)
}

/**
 * 指定した座標を文字列の中心として中央寄せで文字を描画する
 *
 * @param s
 * @param font
 * @param fontSize
 * @param tx
 * @param ty
 */
fun PDPageContentStream.writeTextAlignCenter(
    s: String,
    font: PDFont,
    fontSize: Float,
    tx: Float,
    ty: Float
) {
    // 印字する文字の幅・高さの半分を取得
    val halfWidth = font.getStringWidth(s) / 1000 * fontSize / 2
    val halfHeight = (font.fontDescriptor.fontBoundingBox.height / 1000 * fontSize / 2)
    this.writeText(s, font, fontSize, tx - halfWidth, ty - halfHeight)
}

/**
 * 指定幅で折返しのあるテキストを描画する
 *
 * @param s
 * @param font
 * @param fontSize
 * @param tx
 * @param ty
 * @param width
 * @param lineSpace
 */
fun PDPageContentStream.writeWrapedText(
    s: String,
    font: PDFont,
    fontSize: Float,
    tx: Float,
    ty: Float,
    width: Float,
    lineSpace: Float = 0f,
) {
    // フォントの高さ+行間分改行しながら1行ずつテキストを印字する
    val newLineOffset = font.height(fontSize) + lineSpace
    createParagraphLists(s, font, fontSize, width)
        .forEachIndexed { i, v -> this.writeText(v, font, fontSize, tx, ty - newLineOffset * i) }
}

/**
 * 折返し用の文字列のリストを生成します
 *
 * @param text
 * @param font
 * @param fontSize
 * @param width
 * @return
 */
private fun createParagraphLists(
    text: String,
    font: PDFont,
    fontSize: Float,
    width: Float
): List<String> {
    var tempIndex = 0
    return text.indices.mapNotNull {
        // 指定の文字幅に収まる文字数を計算してテキストを分割する
        when {
            font.width(text.substring(tempIndex..it), fontSize) > width -> {
                val result = text.substring(tempIndex until it)
                tempIndex = it
                result
            }
            it == text.length - 1 -> text.substring(tempIndex)
            else -> null
        }
    }
}

/**
 * ファイルパスから画像を取得し書き込む
 *
 * @param path
 * @param doc
 * @param tx
 * @param ty
 */
fun PDPageContentStream.drawImageFromFilePath(doc: PDDocument, path: String, tx: Float, ty: Float) {
    // ローカルの画像を書き込む
    val realPath = object : Any() {}.javaClass.classLoader.getResource(path)?.path ?: ""
    val img = PDImageXObject.createFromFile(realPath, doc)
    this.drawImage(img, tx, ty)
}

/**
 * ByteArrayから画像を取得し書き込む
 *
 * @param doc
 * @param byteArray
 * @param tx
 * @param ty
 */
fun PDPageContentStream.drawImageFromByteArray(
    doc: PDDocument,
    byteArray: ByteArray,
    tx: Float,
    ty: Float
) {
    // 貼り付け用のオブジェクトに変換
    val img = PDImageXObject.createFromByteArray(doc, byteArray, null)
    // 描画
    this.drawImage(img, tx, ty)
}

/**
 * 与えられた中心座標と角度で描画位置を回転させる
 * 回転させた結果ページ外に文字が出ても考慮されないため注意
 *
 * @param degree
 * @param tx
 * @param ty
 */
fun PDPageContentStream.rotate(degree: Double, tx: Float, ty: Float) {
    // 原点を中心に移動させる
    this.transform(Matrix.getTranslateInstance(tx, ty))
    // 中心を軸に描画領域の角度を変化させる
    this.transform(Matrix.getRotateInstance(Math.toRadians(degree), 0f, 0f))
    // 原点を最初に移動させたのと同じだけ戻す
    this.transform(Matrix.getTranslateInstance(-tx, -ty))
}

/**
 * PDFドキュメントを保存したByteArrayInputStreamを作成して返す
 *
 * @return
 */
fun PDDocument.saveToByteArrayInputStream(): ByteArrayInputStream {
    val out = ByteArrayOutputStream()
    this.save(out)
    this.close()
    return ByteArrayInputStream(out.toByteArray())
}

/**
 * 指定したページを複製して末尾に指定数追加する
 *
 * @param index
 * @param size
 */
fun PDDocument.clonePage(index: Int, size: Int = 1) {
    // 指定のページを読み込む
    val page = this.pages[index]
    // 読み込んだページの構成情報を複製して注釈情報を削除
    val dict = COSDictionary(page.cosObject).also { it.removeItem(COSName.ANNOTS) }
    // 元のPDFにページとして追加する
    repeat(size) { this.importPage(PDPage(dict)) }
}

/**
 * 渡された文字列が当該フォントで描画できるかを確認
 *
 * @param c
 * @return
 */
fun PDFont.isWritableChar(c: Char): Boolean = try {
    this.encode(c.toString())
    true
} catch (e: IllegalArgumentException) {
    // 未収録の文字列はIllegalArgumentExceptionを吐くのでfalseで返す
    false
}

/**
 * 文字を印字する色をカラーコードから指定
 *
 * @param code
 */
fun PDPageContentStream.setFontColorByColorCode(code: String) {
    this.setNonStrokingColor(Color.decode(code))
}


/**
 * 渡された文字列のフォントサイズに対応する幅を取得
 *
 * @param text
 * @param fontSize
 * @return
 */
fun PDFont.width(text: String, fontSize: Float): Float =
    this.getStringWidth(text) / 1000 * fontSize

/**
 * フォントサイズに対応する文字列の高さを取得
 *
 * @param fontSize
 * @return
 */
fun PDFont.height(fontSize: Float): Float =
    this.fontDescriptor.fontBoundingBox.height / 1000 * fontSize