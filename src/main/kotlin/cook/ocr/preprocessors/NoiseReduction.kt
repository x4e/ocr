package cook.ocr.preprocessors

import cook.ocr.Processor
import cook.ocr.preprocessors.NoiseReduction.isBlack
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.util.*

object NoiseReduction: Processor {
	private const val threshold = 12
	
	override fun process(image: BufferedImage): BufferedImage {
		val width = image.width
		val height = image.height
		
		for (x in 0 until width) {
			for (y in 0 until height) {
				if (image.isBlack(x, y)) {
					val numSurrounding = image.countSurrounding(x, y)
					
					if (numSurrounding < threshold) {
						image.setRGB(x, y, Color.WHITE.rgb)
					}
				}
			}
		}
		
		return image
	}
	
	val dxdy = arrayOf(0 to 1, 0 to -1, 1 to 0, 1 to 1, 1 to -1, -1 to 0, -1 to 1, -1 to -1)
	
	private fun BufferedImage.countSurrounding(x: Int, y: Int): Int {
		var num = 0
		val ignored = hashSetOf<Pair<Int, Int>>()
		val queue = ArrayDeque<Pair<Int, Int>>()
		if (isBlack(x, y)) {
			val pair = Pair(x, y)
			queue.add(pair)
			ignored.add(pair)
		}
		while (!queue.isEmpty()) {
			val new = queue.pop()
			num += 1
			
			for ((dx, dy) in dxdy) {
				val pair = Pair(new.first + dx, new.second + dy)
				if (isBlack(pair.first, pair.second)) {
					if (ignored.add(pair)) {
						queue.add(pair)
					}
				}
			}
		}
		return num
	}
	
	private fun BufferedImage.isBlack(x: Int, y: Int): Boolean {
		if (x < 0 || y < 0 || x >= this.width || y >= this.height) return false
		
		return isBlack(getRGB(x, y))
	}
	
	private fun isBlack(rgb: Int) = rgb == Color.BLACK.rgb
}
