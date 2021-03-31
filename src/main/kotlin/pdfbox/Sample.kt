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

    // ByteArrayOutputStreamに書き込んでByteArrayInputStreamに変換する
    return PDDocument.load(template).let { doc ->
        val out = ByteArrayOutputStream()
        doc.save(out)
        doc.close()
        ByteArrayInputStream(out.toByteArray())
    }
}

private fun createNewPDF() = PDDocument().let { doc ->
    // A4のページを追加
    val page = PDPage(PDRectangle.A4)
    doc.addPage(page)

    // フォントの指定
    val font = PDType1Font.HELVETICA_BOLD

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
    ByteArrayInputStream(out.toByteArray())
}