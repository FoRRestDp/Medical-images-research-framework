package features.numinfofromimage

import core.array.BooleanArray2D
import core.array.map2d
import core.common.toBicolor
import core.data.MirfException
import core.data.attribute.MirfAttributes
import core.data.medimage.ImageSeries
import core.data.medimage.MedImage

fun ImageSeries.getVolumeInMm3(): Double {

    val atomicVolume = this.attributes[MirfAttributes.ATOMIC_ELEMENT_VOLUME_MM3]

    if (!this.isThresholded)
        throw MirfException("Volume calculation can only be applied to the thresholded series")

    var result = 0.0

    for ((i, image) in this.images.withIndex()) {
        val mask = image.toBicolor()
        val allPixelVolumes = mask.map2d { if (it) atomicVolume else 0.0 }
        result += allPixelVolumes.sum()
    }
    return result
}

private fun MedImage.toBicolor() : BooleanArray2D {
    return this.image!!.toBicolor()
}

private val ImageSeries.isThresholded: Boolean
    get() = this.attributes.hasAttribute(MirfAttributes.THRESHOLDED)
