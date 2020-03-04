package cook.ocr

import java.awt.image.BufferedImage

/**
 * @author cookiedragon234 04/Mar/2020
 */
interface Processor {
	fun process(img: BufferedImage): BufferedImage
}
