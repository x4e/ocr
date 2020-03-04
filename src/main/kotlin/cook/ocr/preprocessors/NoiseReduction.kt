package cook.ocr.preprocessors

import cook.ocr.Processor
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

/**
 * @author cookiedragon234 04/Mar/2020
 */
object NoiseReduction: Processor {
	private const val threshold = 6
	
	
	override fun process(img: BufferedImage): BufferedImage {
		return removeNoise(img)
	}
	
	fun removeNoise(image: BufferedImage): BufferedImage {
		val width = image.width
		val height = image.height
		
		for (x in 0 until width) {
			for (y in 0 until height) {
				if (image.isBlack(x, y)) {
					val surroundingPixels = Array(5) {_x ->
						Array(5) { _y ->
							image.isBlack(x + (_x - 2), y + (_y - 2))
						}
					}
					
					var numSurrounding = 0
					
					surroundingPixels.forEach { numSurrounding += it.count {`val` -> `val` } }
					
					println(numSurrounding)
					
					if (numSurrounding < threshold) {
						image.setRGB(x, y, Color.WHITE.rgb)
					}
				}
			}
		}
		
		return image
	}
	
	fun BufferedImage.isBlack(x: Int, y: Int): Boolean {
		if (x < 0 || y < 0 || x >= this.width || y >= this.height) return false
		
		return isBlack(Color(getRGB(x, y)))
	}
	
	fun isBlack(color: Color) = color.rgb == Color.BLACK.rgb
}
