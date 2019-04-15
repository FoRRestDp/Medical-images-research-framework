package modules.ms

import com.itextpdf.io.font.constants.StandardFonts
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.kernel.colors.Color
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.font.PdfFontFactory
import com.itextpdf.kernel.geom.PageSize
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.borders.SolidBorder
import com.itextpdf.layout.element.*
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import com.itextpdf.layout.property.UnitValue
import com.itextpdf.layout.property.VerticalAlignment
import features.reports.pdf.PdfDocumentInfo
import features.reports.pdf.asPdfImage
import java.awt.image.BufferedImage
import java.io.ByteArrayOutputStream
import javax.imageio.ImageIO
import com.itextpdf.kernel.colors.DeviceCmyk
import com.itextpdf.kernel.colors.DeviceGray
import com.itextpdf.kernel.pdf.canvas.PdfCanvas


class MsPdfReportCreator(val spec: MsPdfReportSpec) {

    private val marginBlock = Paragraph().setMarginBottom(10f)

    fun createReport(): PdfDocumentInfo {

        val resultStream = ByteArrayOutputStream()
        val writer = PdfWriter(resultStream)
        val pdf = PdfDocument(writer)

        val document = Document(pdf, PageSize.A4)
        document.setMargins(0f, 10f, 10f, 10f)
        val pageWidth = pdf.defaultPageSize.width
        val pageHeight = pdf.defaultPageSize.height

        document.add(createHeader())

        document.add(marginBlock)

        if (spec.seriesDesc != null) {
            document.add(createScansDesc())
            document.add(marginBlock)
        }

        if (spec.seriesVisualization != null) {
            document.add(createSeriesVisual())
            document.add(marginBlock)
        }

        document.add(createVolumeBlock())
        document.add(marginBlock)
        document.add(createConclusion())

        addFooter(document)
        //placeLogo(document, pageWidth, pageHeight)

        document.close()
        return PdfDocumentInfo(pdf, resultStream)
    }

    private fun addFooter(document: Document) {
        val pdfDoc = document.pdfDocument
        val canvas = PdfCanvas(pdfDoc.getPage(1))

        val black = DeviceGray(0f)

        canvas.setStrokeColor(black)

                .moveTo(450.0, 30.0)

                .lineTo(550.0, 30.0)

                .closePathStroke()
    }

    private fun createVolumeBlock(): Table {
        val table = Table(arrayOf(UnitValue.createPercentValue(50f), UnitValue.createPercentValue(50f)))
        table.addText("Total volume")
                .addText("20.2cm\u00B3")
                .addText("Active volume")
                .addText("7.1cm\u00B3")
                .addText("Total volume grow rate")
                .addText("7%")
                .addText("Active volume grow rate")
                .addText("12%")
                .setTextAlignment(TextAlignment.CENTER)
                .setFontSize(14f)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setWidth(UnitValue.createPercentValue(100f))
                .setMargins(0f, 5f, 0f, 5f)
        return table
    }

    private fun createConclusion(): Paragraph {
        val result = Paragraph("Conclusion").setFontSize(16f)
                .setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD))
                .setWidth(UnitValue.createPercentValue(100f))
                .setMargins(0f, 5f, 0f, 5f)
        return result
    }

    private fun createSeriesVisual(): IBlockElement {
        val result = Paragraph()
        for (image in spec.seriesVisualization!!)
            result.addImg(image) { img ->
                img
                        //.setWidth(UnitValue.createPercentValue(100f / spec.seriesVisualization.size))
                        .setMargins(0f, 5f, 0f, 5f)
            }

        return result.setBackgroundColor(ColorConstants.BLACK).setTextAlignment(TextAlignment.CENTER)
    }


    private fun createHeader(): Paragraph {

        val header = Paragraph()
        header.setTextAlignment(TextAlignment.CENTER)

        header.width = UnitValue.createPercentValue(100f)
        header.setVerticalAlignment(VerticalAlignment.MIDDLE)


        val logo: Image = spec.companyImage.asPdfImage()
        logo.setRelativePosition(150f, 0f, 0f, 5f)

        if (DEBUG_SHOW_BORDERS)
            header.setBorder(SolidBorder(1f))

        val titleText = Text(TITLE).setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD)).setFontSize(18f)
        val title = Paragraph(titleText).setRelativePosition(logo.width.value / 2, 0f, 0f, 0f)
        header.add(title)
        header.add(logo)

        return header
    }

    private fun placeLogo(document: Document, pageWidth: Float, pageHeight: Float) {

        val logo: Image = spec.companyImage.asPdfImage()
        logo.setHorizontalAlignment(HorizontalAlignment.RIGHT)

        logo.setFixedPosition(pageWidth - document.rightMargin - logo.width.value, pageHeight - document.topMargin - logo.imageScaledHeight)
        document.add(logo)
    }

    private fun createScansDesc(): Table {

        if (spec.seriesDesc == null)
            throw Exception("scans description must not be null")

        val result = Table(arrayOf(UnitValue.createPercentValue(30f), UnitValue.createPercentValue(70f)))
                .setWidth(UnitValue.createPercentValue(100f))
                .setMargins(0f, 5f, 0f, 5f)

        //creates:    John Doe
        //              63 y.o.
        val patientName = Text(spec.patientName)
        patientName.setFontSize(14f)
        patientName.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))

        val patientAge = Text(spec.patientAge)
        patientName.setFontSize(12f)
        patientName.setFont(PdfFontFactory.createFont(StandardFonts.HELVETICA))
        patientAge.setFontColor(ColorConstants.GRAY)

        val patientInfo = Paragraph().add(patientName).add("\n").add(patientAge)
        patientInfo.setHorizontalAlignment(HorizontalAlignment.LEFT)

        val patientCell = Cell().add(patientInfo).setBorder(Border.NO_BORDER)
        if (DEBUG_SHOW_BORDERS)
            patientCell.setBorder(SolidBorder(1f))

        result.addCell(patientCell)

        //creates: baseline    8/12/2016      total
        //         followup    12/12/2017     total

        val scanInfo = Table(3)
        scanInfo.addText("Baseline") { x -> x.setBorder(Border.NO_BORDER).setMarginLeft(0f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(0f) }
                .addText(spec.seriesDesc.baselineScan.date.toString()) { x -> x.setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.CENTER) }
                .addText("17.7cm³") { x -> x.setBorder(Border.NO_BORDER).setMarginRight(0f).setTextAlignment(TextAlignment.RIGHT).setPaddingRight(5f) }

        scanInfo.addText("Followup") { x -> x.setBorder(Border.NO_BORDER).setMarginLeft(0f).setTextAlignment(TextAlignment.LEFT).setPaddingLeft(0f) }
                .addText(spec.seriesDesc.followupScan.date.toString()) { x -> x.setBorder(Border.NO_BORDER).setHorizontalAlignment(HorizontalAlignment.CENTER) }
                .addText("20.2cm³") { x -> x.setBorder(Border.NO_BORDER).setMarginRight(0f).setTextAlignment(TextAlignment.RIGHT).setPaddingRight(5f) }

        scanInfo.setHorizontalAlignment(HorizontalAlignment.RIGHT)
                .setTextAlignment(TextAlignment.CENTER)
                .width = UnitValue.createPercentValue(100F)

        val scanCell = Cell()
                .add(scanInfo)//.setBorder(Border.NO_BORDER)
                .setPaddingLeft(5f)
                .setPaddingRight(5f)

//        if (DEBUG_SHOW_BORDERS)
//            scanCell.setBorder(SolidBorder(1f))

        result.addCell(scanCell)

        return result
    }

    companion object {
        const val TITLE = "MS AI-supported report"
        var DEBUG_SHOW_BORDERS = false
    }
}

private fun Paragraph.addImg(image: BufferedImage, decorator: (Image) -> Unit = {}): Paragraph {
    val stream = com.itextpdf.io.source.ByteArrayOutputStream()
    ImageIO.write(image, "png", stream)

    val pdfImage = Image(ImageDataFactory.create(stream.toByteArray()))
    decorator(pdfImage)

    this.add(pdfImage)
    return this
}

private fun Table.addText(value: String, decorator: ((Cell) -> Unit) = { }): Table {
    val cell = Cell().add(Paragraph(value))
    decorator(cell)

    this.addCell(cell)
    return this
}

