package cook.ocr.preprocessors

import cook.ocr.Processor
import cook.ocr.utils.getPixels
import java.awt.image.BufferedImage

/**
 * @author cookiedragon234 26/Mar/2020
 */
object PixelTester: Processor {
	override fun process(img: BufferedImage): BufferedImage {
		val pixels = img.getPixels()
		
		val br = BufferedImage(pixels.size, pixels[0].size, BufferedImage.TYPE_INT_ARGB)
		
		for ((x, row) in pixels.withIndex()) {
			for ((y, pixel) in row.withIndex()) {
				br.setRGB(x, y, pixel)
			}
		}
		
		return br
	}
}
