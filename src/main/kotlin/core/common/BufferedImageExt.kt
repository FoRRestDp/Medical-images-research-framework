package core.common

import core.array.BooleanArray2D
import java.awt.Color
import java.awt.image.BufferedImage
import jdk.nashorn.internal.codegen.ObjectClassGenerator.pack
import javax.swing.JFrame
import javax.swing.ImageIcon
import javax.swing.JLabel
import java.io.File
import javax.imageio.ImageIO
import javax.swing.JPanel




fun BufferedImage.logSize(): String = "${this.height} x ${this.width}"

fun BufferedImage.convertColorspace(newType: Int): BufferedImage {
    val result = BufferedImage(
            this.width,
            this.height,
            newType)
    result.graphics.drawImage(this, 0, 0, null)
    return result
}

fun BufferedImage.toBicolor(threshold: Int = 384): BooleanArray2D {
    fun passThreshold(colorBinary: Int, threshold: Int): Boolean {
        val color = Color(colorBinary)
        return (color.blue + color.green + color.red) >= threshold
    }

    val result = BooleanArray2D.create(this.height, this.width)
    for (i in 0 until this.height) {
        for (j in 0 until this.width) {
            result[i][j] = passThreshold(this.getRGB(j, i), threshold)
        }
    }
    return result
}

fun BufferedImage.debugDisplayInWindow(){
    val panel = JPanel()

    val label = JLabel(ImageIcon(this))
    panel.add(label)

    JFrame.setDefaultLookAndFeelDecorated(true)
    val frame = JFrame("Debug image viewer")
    frame.defaultCloseOperation = JFrame.EXIT_ON_CLOSE

    frame.add(panel)

    frame.pack()
    frame.isVisible = true
}