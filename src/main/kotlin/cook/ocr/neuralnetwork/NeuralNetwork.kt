package cook.ocr.neuralnetwork


import cook.ocr.utils.convertToBufferedImage
import cook.ocr.utils.getPixels
import org.datavec.image.loader.Java2DNativeImageLoader
import org.datavec.image.loader.NativeImageLoader
import org.deeplearning4j.datasets.iterator.impl.EmnistDataSetIterator
import org.deeplearning4j.nn.conf.NeuralNetConfiguration
import org.deeplearning4j.nn.conf.inputs.InputType
import org.deeplearning4j.nn.conf.layers.*
import org.deeplearning4j.nn.multilayer.MultiLayerNetwork
import org.deeplearning4j.nn.weights.WeightInit
import org.deeplearning4j.optimize.listeners.ScoreIterationListener
import org.deeplearning4j.util.ModelSerializer
import org.nd4j.evaluation.classification.Evaluation
import org.nd4j.evaluation.classification.ROCMultiClass
import org.nd4j.linalg.activations.Activation
import org.nd4j.linalg.api.ndarray.BaseNDArray
import org.nd4j.linalg.api.ndarray.INDArray
import org.nd4j.linalg.api.ops.LossFunction
import org.nd4j.linalg.api.ops.impl.indexaccum.IMax
import org.nd4j.linalg.dataset.api.preprocessor.ImagePreProcessingScaler
import org.nd4j.linalg.factory.Nd4j
import org.nd4j.linalg.learning.config.Adam
import org.nd4j.linalg.lossfunctions.LossFunctions
import java.awt.Color
import java.awt.Image
import java.awt.image.BufferedImage
import java.awt.image.DataBufferByte
import java.awt.image.DataBufferInt
import java.io.File
import javax.imageio.ImageIO
import kotlin.random.Random


/**
 * @author cookiedragon234 17/Apr/2020
 */
object NeuralNetwork {
	// numbers + characters
	private val emnistSet = EmnistDataSetIterator.Set.LETTERS
	// labels (nn output -> character)
	private val labels = EmnistDataSetIterator.getLabelsArray(emnistSet)
	// Iterator used for training
	private val trainIt by lazy {
		EmnistDataSetIterator(emnistSet, 128, true)
	}
	// Iterator used for testing
	private val testIt by lazy {
		EmnistDataSetIterator(emnistSet, 128, false)
	}
	// Where we store a generated neural network model
	private val networkFile = File("model.data")
	private val network by lazy {
		if (networkFile.exists()) {
			println("Restoring existing model...")
			ModelSerializer.restoreMultiLayerNetwork(networkFile)
		} else createModel()
	}
	
	private fun createModel(): MultiLayerNetwork {
		println("Creating new neural network...")
		// Number of output classes, 47 = 0-10, a-z, A-Z
		val numClasses = EmnistDataSetIterator.numLabels(emnistSet)
		// MNIST dataset is 28x28
		val rows = 28
		val columns = 28
		
		val conf = NeuralNetConfiguration.Builder()
			.seed(1234) // random number generator seed -> ensures consistent rng
			.l2(0.0005) // weight coefficient
			.weightInit(WeightInit.XAVIER) // initial weighting
			.updater(Adam(1e-3)) // learning rate
			.list()
			.layer(DenseLayer.Builder() //create the first, input layer with xavier initialization
				.nIn(rows * columns)
				.nOut(1000)
				.activation(Activation.RELU)
				.weightInit(WeightInit.XAVIER)
				.build())
			.layer(OutputLayer.Builder(LossFunctions.LossFunction.NEGATIVELOGLIKELIHOOD) //create hidden layer
				.nIn(1000)
				.nOut(numClasses)
				.activation(Activation.SOFTMAX)
				.weightInit(WeightInit.XAVIER)
				.build())
			.build()
		
		
		val network = MultiLayerNetwork(conf).also {
			it.init()
			// reports the loss score once ever 10 datset iterations
			it.setListeners(ScoreIterationListener(10))
		}
		
		println("Training...")
		for (i in 0..10) {
			println("Epoch $i")
			// train the network using the training iterator
			network.fit(trainIt)
			// evaluate the neural network using the test dataset
			val eval = network.evaluate<Evaluation>(testIt)
			println("\tAccuracy: ${eval.accuracy()}") // accuracy = proportion of predictions that were correct
			println("\tPrecision: ${eval.precision()}") // precision = proportion of identified positives that were actually positive
			println("\tRecall: ${eval.recall()}") // recall = proportion of positives identified correctly
		}
		
		ModelSerializer.writeModel(network, networkFile, true)
		return network
	}
	
	fun run() {
		// evaluate ROC and calculate the Area Under Curve
		val roc = network.evaluateROCMultiClass<ROCMultiClass>(testIt, 0)
		println("ROC:")
		var sum = 0.0
		for (i in roc.underlying.indices) {
			val auc = roc.calculateAUC(i)
			sum += auc
			println("\t$i: $auc") // probability that a positive is ranked higher than a negative
		}
		val av = sum / roc.underlying.size
		println("\tAv: $av")
	}
	
	fun processImage(file: File): Char {
		val img = NativeImageLoader(28, 28, 1).asMatrix(file)
		val output = network.output(img.reshape(intArrayOf(1, 28*28)))
		println(output)
		println(labels.contentToString())
		val result = Nd4j.getExecutioner().execAndReturn(IMax(output)).finalResult.toInt()
		return labels[result]
	}
	
	fun processImage(image: BufferedImage): Char {
		val scaled = image.getScaledInstance(28, 28, Image.SCALE_DEFAULT).convertToBufferedImage()
		
		for (x in 0 until scaled.width) {
			for (y in 0 until scaled.height) {
				var rgb = image.getRGB(x, y)
				if (rgb > 0) rgb = 1
				image.setRGB(x, y, rgb)
			}
		}
		
		val matrix = Java2DNativeImageLoader(28, 28, 1).asMatrix(scaled)
		
		val input = matrix.reshape(intArrayOf(1, 28*28))
		//printImage(input)
		val output = network.output(input) as BaseNDArray
		println(buildString {
			append('[')
			for (x in 0 until output.size(1)) {
				when (val v = output.getRow(0).getDouble(x)) {
					0.0 -> append('0')
					1.0 -> append('1')
					else -> append(v)
				}
				if (x < output.size(1) - 1) {
					append(", ")
				}
			}
			append(']')
		})
		println(labels.contentToString())
		val result = Nd4j.getExecutioner().execAndReturn(IMax(output)).finalResult
		println("Result: ${labels[result.toInt()]}")
		return labels[result.toInt()]
	}
	
	fun printImage(image_: INDArray, classifier: Int = Random.nextInt(25)) {
		var image = image_.muli(255)
		val img = Java2DNativeImageLoader(28, 28, 1).asBufferedImage(image.reshape(intArrayOf(1, 28, 28)))
		ImageIO.write(img, "png", File("out$classifier.png"))
	}
}
