package cook.ocr

import java.io.File
import javax.imageio.ImageIO

fun main(args: Array<String>) {
	if (args.isEmpty())
		throw RuntimeException("Not enough args")

	val file = File(args[0])
	if (!file.exists())
		throw RuntimeException("File not found")

	CharacterRecognition.init(ImageIO.read(file))
}
