package features.dicomimage.data

import com.pixelmed.dicom.AttributeList
import com.pixelmed.display.ConsumerFormatImageMaker
import core.data.AttributeCollection
import core.data.attribute.DataAttribute
import features.dicomimage.copy
import features.dicomimage.getPixelmedAnalogue
import features.dicomimage.hasPixelmedAnalogue
import java.awt.image.BufferedImage

class DicomAttributeCollection : AttributeCollection {

    private val dicomAttributes: AttributeList

    var dicomAttributesVersion = 0
        private set

    var mirfAttributesVersion = 0
        private set

    /**
     * Version of the newest attribute, related to pixelData
     */
    val pixelDataAttributesVersion = dicomAttributesVersion

    override val version
        get() = maxOf(dicomAttributesVersion, mirfAttributesVersion)

    constructor(dicomAttributes: AttributeList, mirfAttributes: Collection<DataAttribute<*>> = ArrayList()) : super(mirfAttributes){
        this.dicomAttributes = dicomAttributes
    }

    fun buildHumanReadableImage(): BufferedImage {
        updateDicomAttrByMirfAttr()
        return ConsumerFormatImageMaker.makeEightBitImage(dicomAttributes)
    }

    override fun clone(): DicomAttributeCollection{
        val clonedAttributes = attributes.map { it.copy()}
        val clonedDicomAttributes = dicomAttributes.copy()
        return DicomAttributeCollection(clonedDicomAttributes, clonedAttributes)
    }

    /**
    Checks if internal [attributes] contains newer version of DICOM related attributes, and updates [dicomAttributes] if so.
     If Attribute is missing from the [dicomAttributes], but presented in [attributes], it will be ADDED too
     */
    private fun updateDicomAttrByMirfAttr() {
        attributes.forEach {
            if (it.hasPixelmedAnalogue()) {
                val analogue = it.getPixelmedAnalogue()
                dicomAttributes.put(analogue)
            }
        }
    }

    /**
     * Checks if internal [attributes] contains newer version of DICOM related attributes, and updates [dicomAttributes] if so.
     * If Attribute is missing from the [attributes], but presented in [dicomAttributes], it will be SKIPPED
     */
    private fun updateMirfAttrByMirfAttr(){
        //TODO:(avlomakin)
    }

    private var cacheRequestedDicomAttributesInMirfCollection: Boolean = true

    override fun find(attributeTag: String): DataAttribute<*>? {
        val mirfAttr =  attributes.firstOrNull { x -> x.tag == attributeTag }

        if(mirfAttr == null){

            log.info("Attribute with '$attributeTag' tag is not presented as mirf attribute. Trying to get corresponding pixelmed attribute")
            val pixelmedTag = MirfPixelmedAttributeMapper.findPixelmedAnalogue(attributeTag)

            if(pixelmedTag == null){
                log.info("no pixelmed analogue for attribute with '$attributeTag' tag")
                return null
            }

            //pixelmed attributeList has no find
            return try {
                val attr = dicomAttributes[pixelmedTag]

                val transformedAttr = MirfPixelmedAttributeMapper.transofrmPixelmedAttributeToMirf(attr)
                if(cacheRequestedDicomAttributesInMirfCollection){
                    attributes.add(transformedAttr)
                }

                transformedAttr

            }catch(e: Exception){
                log.error(e.message)
                null
            }
        }

        return mirfAttr
    }
}
