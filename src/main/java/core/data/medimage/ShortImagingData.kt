package core.data.medimage

import core.array.BooleanArray2D
import core.array.ShortArray2D
import core.array.deepCopy
import core.array.to1D
import core.data.MirfException
import java.awt.image.BufferedImage

class ShortImagingData(
        val rawPixels: ShortArray2D,
        val rawPixelTransformer: RawPixelToRgbTransformer) : ImagingData<BufferedImage> {

    override fun applyMask(mask: BooleanArray2D) {
        for (i in 0 until rawPixels.rows)
            for (j in 0 until rawPixels.columns)
                rawPixels[i][j] = if (mask[i][j]) rawPixels[i][j] else 0
    }

    override val width: Int = rawPixels.columns
    override val height: Int = rawPixels.rows

    override fun copy(): ImagingData<BufferedImage> {
        return ShortImagingData(rawPixels.deepCopy(), rawPixelTransformer.copy())
    }


    override fun getImage(): BufferedImage {
        TODO("not implemented")
    }

    override fun getImageDataAsShortArray(): ShortArray {
        return rawPixels.to1D()
    }

    override fun getImageDataAsByteArray(): ShortArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

    override fun getImageDataAsIntArray(): ShortArray {
        TODO("not implemented") //To change body of created functions use File | Settings | File Templates.
    }

}