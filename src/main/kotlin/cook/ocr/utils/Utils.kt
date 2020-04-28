package cook.ocr.utils

import java.awt.Image
import java.awt.image.BufferedImage


/**
 * @author cookiedragon234 18/Apr/2020
 */
fun Image.convertToBufferedImage(): BufferedImage {
	if (this is BufferedImage) {
		return this
	}
	
	return BufferedImage(getWidth(null), getHeight(null), BufferedImage.TYPE_INT_ARGB).also { bimage ->
		bimage.createGraphics().also { graphics ->
			graphics.drawImage(this, 0, 0, null)
			graphics.dispose()
		}
	}
}
