package cook.ocr.preprocessors

import cook.ocr.Processor
import cook.ocr.preprocessors.NoiseReduction.isBlack
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

object NoiseReduction: Processor {
	private const val THRESHOLD = 6
	private const val WHITE = 0xFFFFFF
	private const val BLACK = 0
	
	override fun process(image: BufferedImage): BufferedImage {
		val width = image.width
		val height = image.height
		
		for (x in 0 until width) {
			for (y in 0 until height) {
				if (image.isBlack(x, y)) {
					var numSurrounding = 0
					
					for (_x in -2..2) {
						for (_y in -2..2) {
							if(image.isBlack(x + _x, y + _y)) {
								numSurrounding += 1
							}
						}
					}
					
					println(numSurrounding)
					
					if (numSurrounding < THRESHOLD) {
						image.setRGB(x, y, WHITE)
					}
				}
			}
		}
		
		return image
	}
	
	private fun BufferedImage.isBlack(x: Int, y: Int): Boolean {
		if (x < 0 || y < 0 || x >= this.width || y >= this.height) return false
		
		return isBlack(getRGB(x, y))
	}
	
	private inline fun isBlack(rgb: Int): Boolean = (rgb == BLACK)
}
