package com.mirf.core.common

import com.mirf.core.array.logSize
import com.mirf.core.array.map2d
import com.mirf.core.data.attribute.MirfAttributes
import com.mirf.core.data.attribute.Switch
import com.mirf.features.ij.asImageSeries
import com.mirf.features.nifti.util.Nifti1Reader
import org.junit.Test

class BufferedImageExtKtTest {

    @Test
    fun toBicolor() {
        val masksPath = javaClass.getResource("/msReport/mask.nii") ?: return
        val masks = Nifti1Reader.read(masksPath.path).asImageSeries()
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