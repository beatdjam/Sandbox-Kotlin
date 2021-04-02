package main.kotlin.pdfbox

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

    // 指定のページオブジェクトに文字の印字
    val page = doc.pages[0]
    PDPageContentStream(doc, page).use { cs ->
        cs.writeText("Hello World", font, 12f, 200f, 500f)
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