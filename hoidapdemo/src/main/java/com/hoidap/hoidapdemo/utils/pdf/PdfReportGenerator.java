package com.hoidap.hoidapdemo.utils.pdf;

import com.hoidap.hoidapdemo.dto.report.DashboardStats;
import com.hoidap.hoidapdemo.dto.report.StudentStat;
import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfPCell;
import com.itextpdf.text.pdf.PdfPTable;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.stream.Stream;

public class PdfReportGenerator {
    public static ByteArrayInputStream exportStatsToPdf(DashboardStats stats) {
        Document document = new Document();
        ByteArrayOutputStream out = new ByteArrayOutputStream();

        try {
            PdfWriter.getInstance(document, out);
            document.open();

            Font fontHeader = FontFactory.getFont(FontFactory.HELVETICA_BOLD, 20);
            Paragraph title = new Paragraph("BAO CAO HE THONG HOI DAP", fontHeader);
            title.setAlignment(Element.ALIGN_CENTER);
            document.add(title);
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Tong so cau hoi: " + stats.getTotalQuestions()));
            document.add(new Paragraph("Da giai quyet: " + stats.getTotalAnswered()));
            document.add(new Paragraph("Ty le: " + stats.getResolutionRate() + "%"));
            document.add(Chunk.NEWLINE);

            document.add(new Paragraph("Top Sinh Vien Hoi Nhieu Nhat:", FontFactory.getFont(FontFactory.HELVETICA_BOLD)));
            document.add(Chunk.NEWLINE);

            PdfPTable table = new PdfPTable(3);
            Stream.of("Ma SV", "Ho Ten", "So Cau Hoi").forEach(headerTitle -> {
                PdfPCell header = new PdfPCell();
                header.setBackgroundColor(BaseColor.LIGHT_GRAY);
                header.setBorderWidth(2);
                header.setPhrase(new Phrase(headerTitle));
                table.addCell(header);
            });

            for (StudentStat sv : stats.getTopStudents()) {
                table.addCell(sv.getMaSv());
                table.addCell(sv.getName());
                table.addCell(sv.getQuestionCount().toString());
            }
            document.add(table);

            document.close();

        } catch (DocumentException e) {
            e.printStackTrace();
        }

        return new ByteArrayInputStream(out.toByteArray());
    }
}
