package cook.ocr.preprocessors

import cook.ocr.Processor
import java.awt.Image
import java.awt.image.BufferedImage

/**
 * @author cookiedragon234 26/Mar/2020
 */
object ImageResizer: Processor {
	override fun process(img: BufferedImage): BufferedImage {
		val scaled = img.getScaledInstance(32, 32, Image.SCALE_DEFAULT)
		
		return if (scaled is BufferedImage) {
			scaled
		} else {
			BufferedImage(32, 32, BufferedImage.TYPE_INT_ARGB).also {
				it.createGraphics().apply {
					drawImage(scaled, 0, 0, null)
					dispose()
				}
			}
		}
	}
}
