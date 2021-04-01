package main.kotlin.pdfbox

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

fun main() {
    val result = createNewPDF()
    File("output.pdf").writeBytes(result.readBytes())
    val result2 = openPDF()
    File("output2.pdf").writeBytes(result2.readBytes())
}

private fun openPDF(): ByteArrayInputStream {
    // resourcesから既存のPDFを読み込む
    val template = object : Any() {}.javaClass
        .classLoader
        .getResourceAsStream("sample.pdf")
    return editDocument(PDDocument.load(template))
}

private fun createNewPDF(): ByteArrayInputStream {
    val doc = PDDocument().also {
        // A4のページを追加
        it.addPage(PDPage(PDRectangle.A4))
    }
    return editDocument(doc)
}

fun editDocument(doc: PDDocument): ByteArrayInputStream {
    // フォントの指定
    val font = PDType1Font.HELVETICA_BOLD
    val page = doc.pages[0]

    // 指定のページオブジェクトに文字の印字
    PDPageContentStream(doc, page).use { cs ->
        cs.beginText()
        cs.setFont(font, 12f)
        cs.newLineAtOffset(200f, 500f)
        cs.showText("Hello World")
        cs.endText()
    }

    // ByteArrayInputStreamに変換
    val out = ByteArrayOutputStream()
    doc.save(out)
    doc.close()
    return ByteArrayInputStream(out.toByteArray())
}