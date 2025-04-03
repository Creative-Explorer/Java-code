package spring_security.JWT_Token.Utils;

//import com.itextpdf.io.font.constants.StandardFonts;
//import com.itextpdf.io.image.ImageData;
//import com.itextpdf.io.image.ImageDataFactory;
//import com.itextpdf.io.source.ByteArrayOutputStream;
//import com.itextpdf.kernel.colors.DeviceRgb;
//import com.itextpdf.kernel.font.PdfFont;
//import com.itextpdf.kernel.font.PdfFontFactory;
//import com.itextpdf.kernel.geom.PageSize;
//import com.itextpdf.kernel.pdf.PdfDocument;
//import com.itextpdf.kernel.pdf.PdfWriter;
//import com.itextpdf.kernel.pdf.canvas.draw.SolidLine;
//import com.itextpdf.layout.Document;
//import com.itextpdf.layout.borders.Border;
//import com.itextpdf.layout.element.*;
//import com.itextpdf.layout.properties.HorizontalAlignment;
//import com.itextpdf.layout.properties.TextAlignment;
//import com.itextpdf.layout.properties.UnitValue;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import spring_security.JWT_Token.dto.PaymentResponseDTO;
import spring_security.JWT_Token.dto.ProductDTO;
import spring_security.JWT_Token.entity.CartEntity;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.math.BigDecimal;

@Service
@Slf4j
public class EmailService {

    @Autowired
    private JavaMailSender javaMailSender;

    public void sendSimpleEmail(String to, String subject, String text) {
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setTo(to);
            message.setSubject(subject);
            message.setText(text);
            javaMailSender.send(message);
        } catch (Exception e) {
            // Log or handle the exception
            System.err.println("Error sending email: " + e.getMessage());
        }
    }

    public void sendHtmlEmail(String to, String subject, String htmlContent) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(htmlContent, true); // true indicates HTML

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Log or handle the exception
            System.err.println("Error sending HTML email: " + e.getMessage());
        }
    }

    public void sendEmailWithAttachment(String to, String cc, String bcc, String subject,
                                        String text, File attachment, File pdfInvoice) {
        try {
            MimeMessage mimeMessage = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            // Set CC recipients
            if (cc != null && !cc.isEmpty()) {
                helper.setCc(cc.split(","));
            }

            // Set BCC recipients
            if (bcc != null && !bcc.isEmpty()) {
                helper.setBcc(bcc.split(","));
            }
            helper.setSubject(subject);
            helper.setText(text);

            // Add attachments
            if (attachment != null && attachment.exists()) {
                helper.addAttachment(attachment.getName(), attachment);
            }

            if (pdfInvoice != null && pdfInvoice.exists()) {
                helper.addAttachment(pdfInvoice.getName(), pdfInvoice);
            }

            javaMailSender.send(mimeMessage);
        } catch (MessagingException e) {
            // Log or handle the exception
            log.info("Error sending email with attachment: {}", e.getMessage());
        }
    }

//    public File generateInvoicePdf(PaymentResponseDTO paymentResponseDTO) {
//        ByteArrayOutputStream baos = new ByteArrayOutputStream();
//        try {
//            PdfWriter writer = new PdfWriter(baos);
//            PdfDocument pdfDoc = new PdfDocument(writer);
//            Document document = new Document(pdfDoc, PageSize.A4);
//
//            // Create fonts
//            PdfFont fontBold = PdfFontFactory.createFont(StandardFonts.HELVETICA_BOLD);
//            PdfFont fontRegular = PdfFontFactory.createFont(StandardFonts.HELVETICA);
//            PdfFont fontItalic = PdfFontFactory.createFont(StandardFonts.HELVETICA_OBLIQUE);
//
//            // Add Company Logo
//            try {
//                ImageData imageData = ImageDataFactory.create("C:/Users/Balaraju.S/Desktop/Sesat-Logo.png");
//                Image logo = new Image(imageData).setWidth(70).setHeight(30);
//                document.add(logo.setHorizontalAlignment(HorizontalAlignment.LEFT).setMarginBottom(20));
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//
//            // Add Company Information
//            document.add(new Paragraph("SESAT SOLUTIONS PVT LTD")
//                    .setFont(fontBold)
//                    .setFontSize(16)
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setMarginBottom(5));
//            document.add(new Paragraph("Survey. No. 142, SOHINI TECH PARK, Cabin # A 11, 8th Floor, iSprout Business Center, Nanakramguda, Telangana 500032")
//                    .setFont(fontRegular)
//                    .setFontSize(10)
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setMarginBottom(5));
//
//            // Add line separator
//            document.add(new LineSeparator(new SolidLine()));
//
//            // Add Invoice Information
//            document.add(new Paragraph("User Email: " + paymentResponseDTO.getUser().getEmail())
//                    .setFont(fontBold)
//                    .setFontSize(14)
//                    .setMarginBottom(5));
//            document.add(new Paragraph("Payment Method: " + paymentResponseDTO.getPaymentMethod())
//                    .setFont(fontRegular)
//                    .setFontSize(12)
//                    .setMarginBottom(5));
//            document.add(new Paragraph("Bill To / Ship To")
//                    .setFont(fontBold)
//                    .setFontSize(14)
//                    .setMarginBottom(10));
//
//            // Customer Details (Assumed from cartEntity or paymentResponseDTO)
//            document.add(new Paragraph(paymentResponseDTO.getUser().getName())
//                    .setFont(fontBold)
//                    .setFontSize(12)
//                    .setMarginBottom(5));
//            document.add(new Paragraph(paymentResponseDTO.getUser().getEmail())
//                    .setFont(fontRegular)
//                    .setFontSize(12)
//                    .setMarginBottom(5));
//            document.add(new Paragraph("Phone: " + paymentResponseDTO.getUser().getEmail())
//                    .setFont(fontRegular)
//                    .setFontSize(12)
//                    .setMarginBottom(5));
//
//            // Payment Information
//            document.add(new Paragraph("Payment Method: " + paymentResponseDTO.getPaymentMethod())
//                    .setFont(fontRegular)
//                    .setFontSize(12)
//                    .setMarginBottom(5));
//            document.add(new Paragraph("Payment Date: " + paymentResponseDTO.getPaymentDate())
//                    .setFont(fontRegular)
//                    .setFontSize(12)
//                    .setMarginBottom(20));
//
//            // Add line separator
//            document.add(new LineSeparator(new SolidLine()));
//
//            // Add Table for Products
//            Table table = new Table(UnitValue.createPercentArray(new float[]{10, 50, 15, 15, 10}));
//            table.setWidth(UnitValue.createPercentValue(100));
//            table.setMarginBottom(20);
//
//            // Table headers
//            table.addHeaderCell(new Cell().add(new Paragraph("Qty")).setFont(fontBold).setBackgroundColor(new DeviceRgb(0, 102, 204)).setFontColor(DeviceRgb.WHITE).setTextAlignment(TextAlignment.CENTER));
//            table.addHeaderCell(new Cell().add(new Paragraph("Description")).setFont(fontBold).setBackgroundColor(new DeviceRgb(0, 102, 204)).setFontColor(DeviceRgb.WHITE).setTextAlignment(TextAlignment.CENTER));
//            table.addHeaderCell(new Cell().add(new Paragraph("Unit Price")).setFont(fontBold).setBackgroundColor(new DeviceRgb(0, 102, 204)).setFontColor(DeviceRgb.WHITE).setTextAlignment(TextAlignment.CENTER));
//            table.addHeaderCell(new Cell().add(new Paragraph("Tax")).setFont(fontBold).setBackgroundColor(new DeviceRgb(0, 102, 204)).setFontColor(DeviceRgb.WHITE).setTextAlignment(TextAlignment.CENTER));
//            table.addHeaderCell(new Cell().add(new Paragraph("Total")).setFont(fontBold).setBackgroundColor(new DeviceRgb(0, 102, 204)).setFontColor(DeviceRgb.WHITE).setTextAlignment(TextAlignment.CENTER));
//
//            // Add products from paymentResponseDTO
//            for (ProductDTO product : paymentResponseDTO.getProducts()) {
//                table.addCell(new Cell().add(new Paragraph("1")).setFont(fontRegular).setTextAlignment(TextAlignment.CENTER));
//                table.addCell(new Cell().add(new Paragraph(product.getName())).setFont(fontRegular).setTextAlignment(TextAlignment.LEFT));
//                table.addCell(new Cell().add(new Paragraph("₹" + product.getPrice())).setFont(fontRegular).setTextAlignment(TextAlignment.RIGHT));
//                table.addCell(new Cell().add(new Paragraph("₹0.00")).setFont(fontRegular).setTextAlignment(TextAlignment.RIGHT)); // Assuming tax is 0
//                table.addCell(new Cell().add(new Paragraph("₹" + product.getPrice())).setFont(fontRegular).setTextAlignment(TextAlignment.RIGHT));
//            }
//
//            // Add line separator
//            document.add(new LineSeparator(new SolidLine()));
//
//            // Add Summary
//            Double totalAmount = paymentResponseDTO.getAmount();
//            addSummaryRow(table, "Subtotal", "₹" + totalAmount, fontBold, fontRegular);
//            addSummaryRow(table, "Tax", "₹0.00", fontBold, fontRegular); // Assuming no tax for now
//            addSummaryRow(table, "Total", "₹" + totalAmount, fontBold, fontBold);
//
//            document.add(table);
//
//            // Add Footer Information
//            document.add(new Paragraph("Thank you for your business!")
//                    .setFont(fontRegular)
//                    .setFontSize(12)
//                    .setTextAlignment(TextAlignment.CENTER)
//                    .setMarginTop(20));
//
//            document.close();
//
//            File file = new File("invoice.pdf");
//            try (FileOutputStream fos = new FileOutputStream(file)) {
//                fos.write(baos.toByteArray());
//            }
//            return file;
//
//        } catch (IOException e) {
//            e.printStackTrace();
//            return null; // Handle the exception
//        }
//    }
//
//    // Method to add summary row
//    private void addSummaryRow(Table table, String label, String value, PdfFont fontLabel, PdfFont fontValue) {
//        table.addCell(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
//        table.addCell(new Cell().add(new Paragraph(label)).setFont(fontLabel));
//        table.addCell(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
//        table.addCell(new Cell().add(new Paragraph(" ")).setBorder(Border.NO_BORDER));
//        table.addCell(new Cell().add(new Paragraph(value)).setFont(fontValue));
//    }


}
