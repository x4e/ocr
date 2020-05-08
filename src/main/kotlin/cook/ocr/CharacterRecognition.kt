package cook.ocr

import cook.ocr.preprocessors.*
import cook.ocr.utils.convertToBufferedImage
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


object CharacterRecognition {
	fun preprocess(image_: BufferedImage): BufferedImage {
		var image = image_
		image = image.getScaledInstance(28, 28, Image.SCALE_SMOOTH).convertToBufferedImage()
		val processors = arrayOf(
			//ImageResizer,
			BWConversion//,
			//NoiseReduction,
			//PixelTester
		)
		
		for (processor in processors) {
			image = processor.process(image)
		}
		
		return image
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
