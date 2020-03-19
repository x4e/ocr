package cook.ocr.preprocessors

import cook.ocr.Processor
import cook.ocr.preprocessors.NoiseReduction.isBlack
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte

object NoiseReduction: Processor {
	private const val threshold = 6
	
	override fun process(image: BufferedImage): BufferedImage {
		val width = image.width
		val height = image.height
		val ignores = mutableSetOf<Pair<Int, Int>>()
		
		for (x in 0 until width) {
			for (y in 0 until height) {
				if (image.isBlack(x, y)) {
					val numSurrounding = image.countSurrounding(ignores, Pair(x, y), 0)
					
					println(numSurrounding)
					
					if (numSurrounding < 1) {
						image.setRGB(x, y, Color.WHITE.rgb)
					}
				}
			}
		}
		
		return image
	}
	
	private fun BufferedImage.countSurrounding(ignores: MutableSet<Pair<Int, Int>>, coords: Pair<Int, Int>, total: Int): Int {
		var total = total
		
		if (ignores.add(coords)) {
			if (isBlack(coords.first, coords.second)) {
				total += 1
				
				for (_x in (coords.first - 2)..(coords.first + 2)) {
					for (_y in (coords.second - 2)..(coords.second + 2)) {
						val pair = Pair(_x, _y)
						if (!ignores.contains(pair)) {
							total = countSurrounding(ignores, pair, total)
						}
					}
				}
			}
		}
		return total
	}
	
	private fun BufferedImage.isBlack(x: Int, y: Int): Boolean {
		if (x < 0 || y < 0 || x >= this.width || y >= this.height) return false
		
		return isBlack(getRGB(x, y))
	}
	
	private inline fun isBlack(rgb: Int) = rgb == Color.BLACK.rgb
}
