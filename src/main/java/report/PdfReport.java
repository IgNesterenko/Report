package report;

import com.itextpdf.text.DocumentException;
import com.itextpdf.text.pdf.BaseFont;
import com.itextpdf.text.pdf.PdfContentByte;
import com.itextpdf.text.pdf.PdfReader;
import com.itextpdf.text.pdf.PdfStamper;

import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDate;
import java.time.YearMonth;
import java.time.format.TextStyle;
//import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class PdfReport {
    static String pathPdf = "src/main/FilesRepository/PLANTILLA REGISTRO JORNADA EMPTY.pdf";
    static String pathPdfFilled = "src/main/FilesRepository/PLANTILLA REGISTRO JORNADA FILLED.pdf";

    public static void generateReport (int id, int month, int year, List<Integer> vocationList) throws IOException, DocumentException {
        Locale spanishLocal = new Locale("es", "ES");
        TextStyle style = TextStyle.FULL;
        Integer totalHours = 0;
        YearMonth billingYearMonth = YearMonth.of(year, month);

        PdfReader pdfReader = new PdfReader(pathPdf);
        PdfStamper pdfStamper = new PdfStamper(pdfReader, new FileOutputStream(pathPdfFilled));
        BaseFont baseFont = BaseFont.createFont(BaseFont.HELVETICA, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        BaseFont baseFondBold = BaseFont.createFont(BaseFont.HELVETICA_BOLD, BaseFont.CP1252, BaseFont.NOT_EMBEDDED);
        PdfContentByte pageContent = pdfStamper.getOverContent(1);
        //Name surname
        pageContent.beginText();
        pageContent.setFontAndSize(baseFont, 6);
        pageContent.setTextMatrix(360,734);
        pageContent.showText(EmployeesList.getNameSurnameById(id));
        pageContent.endText();
        //NIF
        pageContent.beginText();
        pageContent.setFontAndSize(baseFont, 6);
        pageContent.setTextMatrix(360,725);
        pageContent.showText(EmployeesList.getNieById(id));
        pageContent.endText();
        //Afiliación
        pageContent.beginText();
        pageContent.setFontAndSize(baseFont, 6);
        pageContent.setTextMatrix(360,716);
        pageContent.showText(EmployeesList.getAfiliacionById(id));
        pageContent.endText();
        //Year Month
        pageContent.beginText();
        pageContent.setFontAndSize(baseFont, 6);
        pageContent.setTextMatrix(360,707);
        pageContent.showText(billingYearMonth.getMonth().getDisplayName(style, spanishLocal) + " " + year);
        pageContent.endText();
        //futter
        pageContent.beginText();
        pageContent.setFontAndSize(baseFont, 6);
        pageContent.setTextMatrix(63,306);
        pageContent.showText("En________Valencia_______________, a __" + billingYearMonth.atEndOfMonth().getDayOfMonth() + "__ de __" + billingYearMonth.getMonth().getDisplayName(style, spanishLocal) + "__ de " + year);
        pageContent.endText();

        int yCoordinate = 672;
        List<Integer> weekEnds = BillingPeriod.getWeekEnds(month,year);
        List <Integer> publickHollidays = BillingPeriod.getPublickHollidaysNew(month,year);
        for (LocalDate day = billingYearMonth.atDay(1);
             day.isBefore(billingYearMonth.atEndOfMonth())
                     || day.isEqual(billingYearMonth.atEndOfMonth());
             day = day.plusDays(1)) {
            if (weekEnds.contains(day.getDayOfMonth())) {
                yCoordinate -= 9;
            } else if (publickHollidays.contains(day.getDayOfMonth())) {
                pageContent.beginText();
                pageContent.setFontAndSize(baseFont, 6);
                pageContent.setTextMatrix(82, yCoordinate);
                pageContent.showText("F                         F");
                pageContent.endText();
                yCoordinate -= 9;
            } else if (vocationList.contains(day.getDayOfMonth())) {
                pageContent.beginText();
                pageContent.setFontAndSize(baseFont, 6);
                pageContent.setTextMatrix(82, yCoordinate);
                pageContent.showText("V                         V                            8");
                pageContent.endText();
                yCoordinate -= 9;
                totalHours += 8;
            } else {
                pageContent.beginText();
                pageContent.setFontAndSize(baseFont, 6);
                pageContent.setTextMatrix(80, yCoordinate);
                pageContent.showText("9:00                 17.00                          8");
                pageContent.endText();
                yCoordinate -= 9;
                totalHours += 8;
            }
        }

        pageContent.beginText();
        pageContent.setFontAndSize(baseFondBold, 6);
        pageContent.setTextMatrix(173,393);
        pageContent.showText(String.valueOf(totalHours));
        pageContent.endText();
        pdfStamper.close();

    }

}
