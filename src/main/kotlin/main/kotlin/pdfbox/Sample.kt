package main.kotlin.pdfbox

import org.apache.pdfbox.cos.COSDictionary
import org.apache.pdfbox.cos.COSName
import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDFont
import org.apache.pdfbox.pdmodel.font.PDType1Font
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
            // 文字の印字
            cs.writeText("Hello World", font, 12f, 200f, 500f)
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