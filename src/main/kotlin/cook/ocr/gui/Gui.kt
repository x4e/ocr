package cook.ocr.gui

import cook.ocr.neuralnetwork.NeuralNetwork
import java.awt.*
import java.awt.event.*
import java.awt.image.BufferedImage
import java.awt.image.ColorModel
import javax.swing.BorderFactory
import javax.swing.JButton
import javax.swing.JPanel
import javax.swing.SwingUtilities
import kotlin.system.exitProcess

/**
 * @author cookiedragon234 18/Apr/2020
 */
object Gui {
	private val frame = Frame().also { frame ->
		frame.title = "OCR Neural Network"
		frame.isResizable = true
		val panel = JPanel()
		val canvas = Canvas(280, 280)
		panel.add(canvas)
		panel.add(JButton("Run").also { btn ->
			btn.addActionListener { event ->
				NeuralNetwork.processImage(canvas.image)
			}
		})
		panel.add(JButton("Clear").also { btn ->
			btn.addActionListener { event ->
				canvas.clear()
			}
		})
		frame.add(panel)
		frame.addWindowListener(object: WindowAdapter() {
			override fun windowClosing(e: WindowEvent?) {
				frame.dispose()
				exitProcess(0)
			}
		})
		//frame.size.width = 500
		//frame.size.height = 500
		frame.pack()
	}
	
	fun show() {
		frame.isVisible = true
		frame.toFront()
	}
	
	fun hide() {
		frame.isVisible = false
	}
}

private class Canvas(val dfltWidth: Int, val dfltHeight: Int): JPanel() {
	val image = BufferedImage(dfltWidth, dfltHeight, BufferedImage.TYPE_INT_ARGB)
	private var pencilSize = 30
	private var eraserSize = 50
	private var lastPoint: Point? = null
	
	init {
		this.border = BorderFactory.createLineBorder(Color.RED)
		this.size.width = dfltWidth
		this.size.height = dfltHeight
		
		addMouseListener(object: MouseAdapter() {
			override fun mousePressed(e: MouseEvent) {
				draw(e.point, SwingUtilities.isLeftMouseButton(e))
			}
			override fun mouseReleased(e: MouseEvent?) {
				lastPoint = null
			}
			override fun mouseExited(e: MouseEvent?) {
				lastPoint = null
			}
		})
		
		addMouseMotionListener(object: MouseAdapter() {
			override fun mouseDragged(e: MouseEvent) {
				draw(e.point, SwingUtilities.isLeftMouseButton(e))
			}
		})
		
		clear()
	}
	
	fun clear() {
		val graphics = image.graphics
		graphics.color = Color.BLACK
		graphics.fillRect(0, 0, image.width, image.height)
		graphics.drawRect(0, 0, image.width, image.height)
		graphics.dispose()
		repaint()
	}
	
	override fun paintComponent(g: Graphics) {
		g.drawImage(image, 0, 0, null)
	}
	
	fun draw(point: Point, leftButton: Boolean) {
		val x = point.x
		val y = point.y
		if (x > image.width || y > image.height) {
			return
		}
		
		val graphics = image.createGraphics()
		graphics.color = if (leftButton) Color.WHITE else Color.BLACK
		val size = if (leftButton) pencilSize else eraserSize
		graphics.fillRect(point.x - (size / 2), point.y - (size / 2), size, size)
		
		if (lastPoint != null) {
			val lastPointX = lastPoint!!.x
			val lastPointY = lastPoint!!.y
			
			val posX = lastPointX > x
			val dxs = if (posX) (lastPointX - x) else (x - lastPointX)
			val posY = lastPointY > y
			val dys = if (posY) (lastPointY - y) else (y - lastPointY)
			
			for (dx in 0..dxs) {
				for (dy in 0..dys) {
					val rx = ((point.x + dx) - (size / 2))// else ((point.x - dx) - (size / 2))
					val ry = ((point.y + dy) - (size / 2))// else ((point.y - dy) - (size / 2))
					graphics.fillRect(rx, ry, size, size)
				}
			}
		}
		
		//for (i in -size..size) {
		//	if (lastPoint != null) {
		//		graphics.drawLine(lastPoint!!.x + i, lastPoint!!.y + i, point.x + i, point.y + i)
		//	}
		//}
		graphics.dispose()
		repaint()
		lastPoint = point
	}
	
	override fun getPreferredSize(): Dimension = Dimension(dfltWidth, dfltHeight)
}
