package cook.ocr

import cook.ocr.gui.Gui
import cook.ocr.neuralnetwork.NeuralNetwork
import java.io.File
import java.util.*
import javax.imageio.ImageIO

fun main(args: Array<String>) {
	if (args.isEmpty())
		throw RuntimeException("Not enough args")
	
	//Gui.show()
	//Scanner(System.`in`).next()
	
	if (args[0] == "gui") {
		Gui.show()
	} else if (args[0] == "train") {
		NeuralNetwork.run()
	} else if (args[0] == "preprocess") {
		ImageIO.write(
			CharacterRecognition.preprocess(
				ImageIO.read(File(args[1]))
			),
			"png",
			File("output.png")
		)
	} else {
		NeuralNetwork.processImage(File(args[0]))
	}
	return
}
