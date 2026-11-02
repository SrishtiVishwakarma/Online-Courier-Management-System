package com.example.online_courier.service;

import com.example.online_courier.entity.Courier;
import com.lowagie.text.*;
import com.lowagie.text.pdf.PdfWriter;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.time.format.DateTimeFormatter;

@Service
public class PdfService {

    public byte[] generateCourierReceipt(Courier courier) {
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            Document document = new Document(PageSize.A4);
            PdfWriter.getInstance(document, baos);

            document.open();

            // Title
            Font titleFont = new Font(Font.HELVETICA, 18, Font.BOLD);
            Paragraph title = new Paragraph("COURIER RECEIPT", titleFont);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);

            document.add(new Paragraph(" "));

            // Courier ID
            Font boldFont = new Font(Font.HELVETICA, 12, Font.BOLD);
            Font normalFont = new Font(Font.HELVETICA, 12, Font.NORMAL);

            Paragraph courierId = new Paragraph();
            courierId.add(new Chunk("Courier ID: ", boldFont));
            courierId.add(new Chunk(courier.getCourierId(), normalFont));
            document.add(courierId);

            Paragraph createdDate = new Paragraph();
            createdDate.add(new Chunk("Created Date: ", boldFont));
            createdDate.add(new Chunk(courier.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm")), normalFont));
            document.add(createdDate);

            Paragraph status = new Paragraph();
            status.add(new Chunk("Status: ", boldFont));
            status.add(new Chunk(courier.getStatus().name(), normalFont));
            document.add(status);

            document.add(new Paragraph(" "));

            // Sender Details
            document.add(new Paragraph("SENDER DETAILS", boldFont));
            document.add(new Paragraph("Name: " + courier.getSenderName(), normalFont));
            document.add(new Paragraph("Address: " + courier.getSenderAddress(), normalFont));
            document.add(new Paragraph("City: " + courier.getSenderCity(), normalFont));
            if (courier.getSenderMobile() != null && !courier.getSenderMobile().isEmpty()) {
                document.add(new Paragraph("Mobile: " + courier.getSenderMobile(), normalFont));
            }

            document.add(new Paragraph(" "));

            // Receiver Details
            document.add(new Paragraph("RECEIVER DETAILS", boldFont));
            document.add(new Paragraph("Name: " + courier.getReceiverName(), normalFont));
            document.add(new Paragraph("Address: " + courier.getReceiverAddress(), normalFont));
            document.add(new Paragraph("City: " + courier.getReceiverCity(), normalFont));
            if (courier.getRecipientMobile() != null && !courier.getRecipientMobile().isEmpty()) {
                document.add(new Paragraph("Mobile: " + courier.getRecipientMobile(), normalFont));
            }

            document.add(new Paragraph(" "));

            // Package Details
            document.add(new Paragraph("PACKAGE DETAILS", boldFont));
            document.add(new Paragraph("Actual Weight: " + courier.getWeight() + " kg", normalFont));
            
            // Calculate chargeable weight (rounded up)
            long chargeableWeight = (long) Math.ceil(courier.getWeight().doubleValue());
            document.add(new Paragraph("Chargeable Weight: " + chargeableWeight + " kg", normalFont));
            
            if (courier.getNotes() != null && !courier.getNotes().isEmpty()) {
                document.add(new Paragraph("Notes: " + courier.getNotes(), normalFont));
            }

            document.add(new Paragraph(" "));

            // Delivery Charge - Calculate if null (for backward compatibility)
            java.math.BigDecimal deliveryCharge = courier.getDeliveryCharge();
            if (deliveryCharge == null) {
                java.math.BigDecimal chargePerKg = new java.math.BigDecimal("30");
                deliveryCharge = chargePerKg.multiply(java.math.BigDecimal.valueOf(chargeableWeight));
            }
            
            document.add(new Paragraph("DELIVERY CHARGES", boldFont));
            document.add(new Paragraph("Rate: ₹30 per kg", normalFont));
            document.add(new Paragraph("Calculation: " + chargeableWeight + " kg × ₹30 = ₹" + deliveryCharge, normalFont));
            
            Paragraph totalCharge = new Paragraph();
            totalCharge.add(new Chunk("Total Delivery Charge: ", boldFont));
            totalCharge.add(new Chunk("₹" + deliveryCharge, new Font(Font.HELVETICA, 14, Font.BOLD)));
            document.add(totalCharge);

            document.close();

            return baos.toByteArray();
        } catch (Exception e) {
            throw new RuntimeException("Error generating PDF", e);
        }
    }
}
