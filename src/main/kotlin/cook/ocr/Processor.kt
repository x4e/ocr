package cook.ocr

import java.awt.image.BufferedImage

interface Processor {
	fun process(img: BufferedImage): BufferedImage
}
