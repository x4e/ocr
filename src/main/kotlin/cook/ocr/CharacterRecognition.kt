package cook.ocr

import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.io.File
import javax.imageio.ImageIO


object CharacterRecognition {
	fun init(image_: BufferedImage) {
		var image = image_
		// Scale to 32 pixels
		//val scaledIage = image.getScaledInstance(64, 64, Image.SCALE_DEFAULT)
		BufferedImage(
				image.width,
				image.height,
				BufferedImage.TYPE_BYTE_BINARY // Black and white
		).also {newImage ->
			newImage.createGraphics().apply {
				drawImage(image, 0, 0, Color.WHITE, null)
				dispose()
			}
			image = newImage
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
