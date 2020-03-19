package cook.ocr

import cook.ocr.preprocessors.BWConversion
import cook.ocr.preprocessors.ImageTrim
import cook.ocr.preprocessors.NoiseReduction
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


class CharacterRecognition(var image: BufferedImage) {
	init {
		val processors = arrayOf(
			BWConversion,
			NoiseReduction,
			ImageTrim
		)
		
		for (processor in processors) {
			image = processor.process(image)
		}
		
		
		val output = File("output.png")
		ImageIO.write(image, "png", output)
	}
	
	fun getPixelArray(image: BufferedImage): Array<IntArray> {
		val width = image.width
		val height = image.height
		
		val out = Array(width) { IntArray(height) }
		
		for (x in 0 until width) {
			for (y in 0 until height) {
				out[x][y] = image.getRGB(x, y)
			}
		}
		return out
	}
}
