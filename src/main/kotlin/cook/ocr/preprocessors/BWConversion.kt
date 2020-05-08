package cook.ocr.preprocessors

import cook.ocr.Processor
import java.awt.Color
import java.awt.image.BufferedImage
import kotlin.math.abs

object BWConversion : Processor {
	override fun process(img: BufferedImage): BufferedImage {
		// convert image to black and white
		
		val WHITE = Color.WHITE.rgb
		val BLACK = Color.BLACK.rgb
		
		BufferedImage(
			img.width,
			img.height,
			BufferedImage.TYPE_BYTE_BINARY // Black and white
		).also { newImage ->
			newImage.createGraphics().apply {
				drawImage(img, 0, 0, Color.WHITE, null)
				dispose()
			}
			for (x in 0 until newImage.width) {
				for (y in 0 until newImage.height) {
					val rgb = abs(newImage.getRGB(x, y))
					if (rgb > 5) {
						newImage.setRGB(x, y, WHITE)
					} else {
						newImage.setRGB(x, y, BLACK)
					}
				}
			}
			return newImage
		}
	}
}
