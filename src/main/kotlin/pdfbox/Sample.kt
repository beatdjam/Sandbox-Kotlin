package pdfbox

import org.apache.pdfbox.pdmodel.PDDocument
import org.apache.pdfbox.pdmodel.PDPage
import org.apache.pdfbox.pdmodel.PDPageContentStream
import org.apache.pdfbox.pdmodel.common.PDRectangle
import org.apache.pdfbox.pdmodel.font.PDType1Font
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File

fun main() {
    val result = PDDocument().let { doc ->
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
    File("output.pdf").writeBytes(result.readBytes())
}