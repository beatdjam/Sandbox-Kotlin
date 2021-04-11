package pdfbox

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType0Font
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.io.ByteArrayInputStream
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
    val jpFont = PDType0Font.load(
        doc,
        object : Any() {}.javaClass.classLoader.getResourceAsStream("ipag.ttf")
    )
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
            // 右寄せで文字列を描画
            cs.writeTextAlignRight("Hello World", font, 12f, tx, ty + 15)
            // 中央寄せで文字列を描画
            cs.writeTextAlignCenter("Hello World", font, 12f, tx, 20f)

            // 印字文字色変更
            cs.setFontColorByColorCode("#F15B5B")
            val str =
                """To obtain the electronic dictionary which pronounces even a long sentence in an easy-to-hear state by preventing the pronunciation from being broken halfway irrelevantly to the meaning as to a long example sentence and a phrase entered into a dictionary and an English equivalent in a Japanese- English dictionary."""
            cs.writeWrapedText(str, font, 12f, 0f, page.mediaBox.height - 10, 300f)

            cs.setFontColorByColorCode("#000000")
            // 日本語を描画
            cs.writeText("IPA Pゴシック", jpFont, 12f, tx, 40f)
        }
    }

    // 指定し指定したindexのページを指定数末尾に追加する
    doc.clonePage(0, 3)

    // 書き込んだドキュメントをByteArrayInputStreamに変換
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