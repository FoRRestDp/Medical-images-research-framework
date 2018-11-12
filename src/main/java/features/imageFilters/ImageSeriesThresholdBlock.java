package features.imageFilters;

import core.algorithm.Algorithm;
import core.data.medImage.AttributeValueRepresentation;
import core.data.medImage.ImageSeries;
import core.data.medImage.MedImage;
import core.data.medImage.MedImageAttribute;
import core.data.medImage.MirfAttributes;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Filters the pixels of all images provided in {@link ImageSeries} to be within specified boundaries.
 */
public class ImageSeriesThresholdBlock implements Algorithm<ImageSeries, ImageSeries> {
    private byte upperBound;
    private byte lowerBound;

    public ImageSeriesThresholdBlock(byte lowerBound, byte upperBound) {
        this.lowerBound = lowerBound;
        this.upperBound = upperBound;
    }

    @Override
    public ImageSeries execute(ImageSeries input) {

        List<MedImage> newImages = input.images.stream().map(this::thresholdImage).collect(Collectors.toList());

        return new ImageSeries(input.attributes, newImages);
    }

    private MedImage thresholdImage(MedImage medImage){
        byte[][] pixels = medImage.getImagePixels();

        // arrays are storages of fixed size, while collections are extensible. Like arrays and vectors in C++
        for (byte[] pixel : pixels)
            for (int i = 0; i < pixel.length; i++)
                pixel[i] = ((pixel[i] >= lowerBound) && (pixel[i] <= upperBound)) ? pixel[i] : 0;

        MedImage result = medImage.clone();
        MedImageAttribute thresholded = MedImageAttribute.createFromMock(MirfAttributes.THRESHOLDED.tag, true);

        result.addAttribute(thresholded);
        result.setImagePixels(pixels);

        return result;
    }
}
