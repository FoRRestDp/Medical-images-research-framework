package features.dicomimage.util;

import com.pixelmed.dicom.Attribute;
import com.pixelmed.dicom.AttributeList;
import com.pixelmed.dicom.AttributeTag;
import com.pixelmed.dicom.TagFromName;
import org.junit.Assert;
import org.junit.Test;

public class DicomReaderTest {

    @Test
    public void readDicomImageAttributesFromLocalFile_localDicom_readsWithoutErrors() {
        var dicomInputFile = "src/test/resources/exampleDicom.dcm";
        var list = DicomReader.INSTANCE.readDicomImageAttributesFromLocalFile(dicomInputFile);

        // Assert that tags are in correspondence with what is expected from file
        Assert.assertEquals("1.2.840.10008.1.2.1", getTagInformation(list, TagFromName.TransferSyntaxUID));
        Assert.assertEquals("1", getTagInformation(list, TagFromName.SamplesPerPixel));
        Assert.assertTrue(list.get(TagFromName.PixelData) != null);
    }

    @Test
    public void readDicomImagePixelDataFromLocalFile_localDicom_readsWithoutErrors() {
        var dicomInputFile = "src/test/resources/exampleDicom.dcm";
        var bufferedImages = DicomReader.INSTANCE.readDicomImagePixelDataFromLocalFile(dicomInputFile);

        // Just check the size of images array
        Assert.assertEquals(16, bufferedImages.size());
    }

    private static String getTagInformation(AttributeList list, AttributeTag attrTag) {
        return Attribute.getDelimitedStringValuesOrEmptyString(list, attrTag);
    }
}