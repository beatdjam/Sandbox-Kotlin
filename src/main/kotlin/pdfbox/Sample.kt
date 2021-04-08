package pdfbox

import org.apache.pdfbox.cos.COSDictionary
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
import org.apache.pdfbox.util.Matrix
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

fun main() {
    createNewPDF()
    openPDF()
}

/**
 * 既存のPDFを開くサンプル
 *
 */
private fun openPDF() {
    // resourcesから既存のPDFを読み込む
    val doc = openPDF("sample.pdf")
    File("output.pdf").writeBytes(doc.saveToByteArrayInputStream().readBytes())
}

/**
 * 新しくPDFを作成するサンプル
 *
 */
private fun createNewPDF() {
    val doc = PDDocument().also { it.addPage(PDPage(PDRectangle.A4)) }
    val result = editDocument(doc)
    File("output2.pdf").writeBytes(result.readBytes())
}

/**
 * ファイル編集サンプル
 *
 * @param doc
 * @return
 */
fun editDocument(doc: PDDocument): ByteArrayInputStream {
    // フォントの指定
    val font = PDType1Font.HELVETICA_BOLD

    // とりあえずすべてのページに同じ内容を書き込む
    doc.pages.forEach { page ->
        PDPageContentStream(doc, page).use { cs ->
            cs.saveGraphicsState()

            // 描画領域の中心を算出する
            val tx: Float = (page.mediaBox.lowerLeftX + page.mediaBox.upperRightX) / 2
            val ty: Float = (page.mediaBox.lowerLeftY + page.mediaBox.upperRightY) / 2
            cs.rotate(180.0, tx, ty)
            // 中心に文字を描画
            cs.writeText("Hello World", font, 12f, tx, ty)
            cs.restoreGraphicsState()
            // 描画の向きを戻して再描画
            cs.writeText("Hello World", font, 12f, tx, ty)
            val str =
                """To obtain the electronic dictionary which pronounces even a long sentence in an easy-to-hear state by preventing the pronunciation from being broken halfway irrelevantly to the meaning as to a long example sentence and a phrase entered into a dictionary and an English equivalent in a Japanese- English dictionary."""
            cs.writeWrapedText(
                str,
                font,
                12f,
                0f,
                page.mediaBox.height - 10,
                300f,
            )
        }
    }
    return doc.saveToByteArrayInputStream()
}

/**
 * 指定したリソース配下のディレクトリに存在するファイルを読み取ってPDFとして開く
 *
 * @param path
 * @return
 */
fun openPDF(path: String): PDDocument {
    val template = object : Any() {}.javaClass
        .classLoader
        .getResourceAsStream(path)
    return PDDocument.load(template)
}

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
    val lines = createParagraphLists(s, font, fontSize, width)

    this.beginText()
    // テキストの原点を指定
    this.newLineAtOffset(tx, ty)
    this.setFont(font, fontSize)

    // フォントの高さ分改行しながら1行ずつテキストを印字する
    // 最大表示行かつテキストがそれ以上に存在する場合は、末尾を…で埋めて省略する
    lines.forEach {
        this.showText(it)
        this.setLeading(font.height(fontSize) + lineSpace)
        this.newLine()
    }
    this.endText()
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
        if (tempIndex > it) return@mapNotNull null
        if (font.width(text.substring(tempIndex..it), fontSize) > width) {
            val result = text.substring(tempIndex until it)
            tempIndex = it
            return@mapNotNull result
        }

        // 末尾のテキストはすべて出力する
        if (it == text.length - 1) text.substring(tempIndex) else null
    }
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