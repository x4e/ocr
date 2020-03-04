package cook.ocr.preprocessors

import cook.ocr.Processor
import java.awt.Color
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte




/**
 * @author cookiedragon234 04/Mar/2020
 */
object ImageTrim: Processor {
	override fun process(img: BufferedImage): BufferedImage {
		return trim(img)
	}
	
	fun trim(_img: BufferedImage): BufferedImage {
		var img = _img
		// Check columns from left
		var leftTrim = 0
		leftTrim@for (x in 0 until img.width) {
			for (y in 0 until img.height) {
				val color = Color(img.getRGB(x, y))
				if (color.rgb != Color.WHITE.rgb) {
					leftTrim = x
					break@leftTrim
				}
			}
		}
		
		img = img.getSubimage(leftTrim, 0, img.width - leftTrim, img.height)
		
		// check right
		var rightTrim = img.width
		rightTrim@for (x in (img.width - 1) downTo 0) {
			for (y in (img.height - 1) downTo 0) {
				val color = Color(img.getRGB(x, y))
				if (color.rgb != Color.WHITE.rgb) {
					rightTrim = x
					break@rightTrim
				}
			}
		}
		
		img = img.getSubimage(img.width - rightTrim, 0, rightTrim, img.height)
		
		// check top
		var topTrim = 0
		topTrim@for (y in 0 until img.height) {
			for (x in 0 until img.width) {
				val color = Color(img.getRGB(x, y))
				if (color.rgb != Color.WHITE.rgb) {
					topTrim = y
					break@topTrim
				}
			}
		}
		
		img = img.getSubimage(0, topTrim, img.width, img.height - topTrim)
		
		// check bottom
		var bottomTrim = img.height
		bottomTrim@for (y in (img.height - 1) downTo 0) {
			for (x in (img.width - 1) downTo 0) {
				val color = Color(img.getRGB(x, y))
				if (color.rgb != Color.WHITE.rgb) {
					bottomTrim = y
					break@bottomTrim
				}
			}
		}
		
		img = img.getSubimage(0, img.height - bottomTrim, img.width, bottomTrim)
		
		return img
	}
}
