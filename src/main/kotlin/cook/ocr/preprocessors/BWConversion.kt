package cook.ocr.preprocessors

import cook.ocr.Processor
import java.awt.Color
import java.awt.image.BufferedImage

object BWConversion : Processor {
	override fun process(img: BufferedImage): BufferedImage {
		// convert image to black and white
		BufferedImage(
			img.width,
			img.height,
			BufferedImage.TYPE_BYTE_BINARY // Black and white
		).also { newImage ->
			newImage.createGraphics().apply {
				drawImage(img, 0, 0, Color.WHITE, null)
				dispose()
			}
			return newImage
		}
	}
}
