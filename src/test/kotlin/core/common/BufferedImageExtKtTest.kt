package core.common


import core.array.BooleanArray2D
import core.array.logSize
import core.array.map2d
import core.data.attribute.MirfAttributes
import core.data.attribute.Switch
import features.ij.asImageSeries
import features.nifti.util.Nifti1Reader
import features.numinfofromimage.getVolumeInMm3
import org.junit.Assert.*
import org.junit.Test
import java.awt.Color

class BufferedImageExtKtTest {

    @Test
    fun toBicolor() {
        val masksPath = javaClass.getResource("/msReport/mask.nii").path
        val masks = Nifti1Reader.read(masksPath).asImageSeries()
        masks.attributes.add(MirfAttributes.THRESHOLDED.new(Switch.get()))

        val image = masks.images[367].image
        val bl =  image!!.toBicolor();

        for(w in 0 until image.width) {
            for (h in 0 until image.height) {
                print("${bl[h][w]} ")
            }
            println()
        }

//        for(w in 0 until image.width) {
//            for (h in 0 until image.height) {
//                val color = Color(image.getRGB(w, h))
//                print("${color.red + color.green + color.blue} ")
//            }
//            println()
//        }

        println(bl.map2d { if (it) 1 else 0 }.size)
        println("${bl.logSize()} ${image.logSize()}")
    }
}