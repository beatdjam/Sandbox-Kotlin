import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.InputStream
import java.net.HttpURLConnection
import java.net.URL
import javax.imageio.ImageIO
import kotlin.math.ceil
import kotlin.math.floor

fun main() {
    val origins = mapOf(
        "正方形" to "https://1.bp.blogspot.com/-D2I7Z7-HLGU/Xlyf7OYUi8I/AAAAAAABXq4/jZ0035aDGiE5dP3WiYhlSqhhMgGy8p7zACNcBGAsYHQ/s400/no_image_square.jpg",
        "縦長" to "https://2.bp.blogspot.com/-cG25cL7bC2I/W95-Gb15WdI/AAAAAAABP7c/4FtjlHw5NAQKR_P23jU8qLCP3f5umvgQwCLcBGAs/s600/zabuton_smartphone_black_long.png",
        "横長" to "https://2.bp.blogspot.com/-F1KumaY7SHI/W9RZNTMutvI/AAAAAAABPnM/wkl3_b31ETgTJYNdL5R_PxwRbstyiLcxwCLcBGAs/s800/zabuton_tv_enpitsu.png"
    )
    val size = mapOf(
        "正方形" to Pair(100, 100),
        "縦長" to Pair(100, 300),
        "横長" to Pair(300, 100),
        "正方形小" to Pair(10, 10),
        "縦長小" to Pair(10, 30),
        "横長小" to Pair(30, 10),
        "正方形大" to Pair(1000, 1000),
        "縦長大" to Pair(1000, 3000),
        "横長大" to Pair(3000, 1000),
    )

    origins.forEach { (name, value) ->
        // 横長
        val imageBytes = getImageBytes(value)
        size.forEach { (sizeName, sizeValue) ->
            val result = getAutoResizedImage(imageBytes.inputStream(), sizeValue.first, sizeValue.second)
            File("$name to ${sizeName}.png").writeBytes(result)
        }
    }
}

/**
 * URLから画像のバイト配列を取得します
 *
 * @param url
 * @return
 */
fun getImageBytes(url: String): ByteArray {
    lateinit var conn: HttpURLConnection
    return try {
        conn = URL(url).openConnection() as HttpURLConnection
        conn.requestMethod = "GET"
        conn.inputStream.readBytes()
    } finally {
        conn.disconnect()
    }
}

/**
 * 出力したい画像の領域に合わせてリサイズ・クロッピングを行う
 *
 * @param imageBytes 元画像のバイト配列
 * @param width 出力したい画像の横幅
 * @param height 出力したい画像の縦幅
 * @return
 */
fun getAutoResizedImage(imageBytes: InputStream, width: Int, height: Int): ByteArray {
    // 画像読み込み
    val originalImage = ImageIO.read(imageBytes)

    // リサイズ
    val resizedImage = resize(originalImage, width, height)

    // 切り取り
    val croppedImage = crop(resizedImage, width, height)

    // 画像をOutputStreamに出力してバイト配列に変換
    return ByteArrayOutputStream().let {
        ImageIO.write(croppedImage, "PNG", it)
        it.toByteArray()
    }
}

/**
 * 出力したい画像の短辺に合わせたスケールで画像を拡大縮小します
 *
 * @param image
 * @param width
 * @param height
 * @return
 */
private fun resize(image: BufferedImage, width: Int, height: Int): BufferedImage {
    // 横幅・縦幅の比率を計算して、大きい方を基準にリサイズする
    val widthScale = width.toDouble() / image.width.toDouble()
    val heightScale = height.toDouble() / image.height.toDouble()
    val scale = if (widthScale > heightScale) widthScale else heightScale

    val resizeWidth = ceil(image.width * scale).toInt()
    val resizeHeight = ceil(image.height * scale).toInt()
    return BufferedImage(resizeWidth, resizeHeight, BufferedImage.TYPE_INT_RGB).also {
        val g = it.createGraphics()
        g.drawImage(image, 0, 0, resizeWidth, resizeHeight, null)
        g.dispose()
    }
}

/**
 * 画像の中心を基準に指定した幅・高さで画像を切り取ります
 *
 * @param image
 * @param width
 * @param height
 */
private fun crop(image: BufferedImage, width: Int, height: Int): BufferedImage? {
    // 元画像が横長か縦長かを判定
    val isHorizontalImage = image.width > image.height
    // 出力結果が縦長か横長かを判定
    val isHorizontalView = width > height
    return when {
        // 横長の画像を縦長のViewに表示する場合
        isHorizontalImage && !isHorizontalView -> {
            val x = floor(image.width.toDouble() / 2 - width.toDouble() / 2).toInt()
            image.getSubimage(x, 0, width, height)
        }
        // 縦長の画像を縦長のViewに表示する場合
        else -> {
            val y = floor(image.height.toDouble() / 2 - height.toDouble() / 2).toInt()
            image.getSubimage(0, y, width, height)
        }
    }
}