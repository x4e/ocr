package cook.ocr.utils

import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte


/**
 * @author cookiedragon234 26/Mar/2020
 */
fun BufferedImage.getPixels(): Array<Array<Int>> {
	return Array(width) { x ->
		Array(height) { y ->
			getRGB(x, y)
		}
	}
}
