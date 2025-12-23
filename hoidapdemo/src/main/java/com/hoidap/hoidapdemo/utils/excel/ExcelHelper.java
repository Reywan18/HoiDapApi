package com.hoidap.hoidapdemo.utils.excel;

import com.hoidap.hoidapdemo.entity.lop.LopJpaEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class ExcelHelper {
    public static String TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
    static String[] HEADERS = { "Mã Lớp", "Khóa Học", "Chuyên Ngành" };
    static String SHEET = "Lops";

    public static boolean hasExcelFormat(MultipartFile file) {
        return TYPE.equals(file.getContentType());
    }

    //Export: List<Lop>
    public static ByteArrayInputStream lopsToExcel(List<LopJpaEntity> lops) {
        try (Workbook workbook = new XSSFWorkbook();
             ByteArrayOutputStream out = new ByteArrayOutputStream()) {

            Sheet sheet = workbook.createSheet(SHEET);

            Row headerRow = sheet.createRow(0);
            for (int col = 0; col < HEADERS.length; col++) {
                Cell cell = headerRow.createCell(col);
                cell.setCellValue(HEADERS[col]);
            }

            int rowIdx = 1;
            for (LopJpaEntity lop : lops) {
                Row row = sheet.createRow(rowIdx++);
                row.createCell(0).setCellValue(lop.getMaLop());
                row.createCell(1).setCellValue(lop.getKhoaHoc());
                row.createCell(2).setCellValue(lop.getChuyenNganh());
            }

            workbook.write(out);
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi tạo file Excel: " + e.getMessage());
        }
    }

    //Import: List<Lop>
    public static List<LopJpaEntity> excelToLops(InputStream is) {
        try {
            Workbook workbook = new XSSFWorkbook(is);
            Sheet sheet = workbook.getSheetAt(0);
            Iterator<Row> rows = sheet.iterator();
            List<LopJpaEntity> lops = new ArrayList<>();

            int rowNumber = 0;
            while (rows.hasNext()) {
                Row currentRow = rows.next();
                if (rowNumber == 0) {
                    rowNumber++;
                    continue;
                }

                Iterator<Cell> cellsInRow = currentRow.iterator();
                LopJpaEntity lop = new LopJpaEntity();

                int cellIdx = 0;
                while (cellsInRow.hasNext()) {
                    Cell currentCell = cellsInRow.next();
                    switch (cellIdx) {
                        case 0:
                            lop.setMaLop(currentCell.getStringCellValue());
                            break;
                        case 1:
                            lop.setKhoaHoc(currentCell.getStringCellValue());
                            break;
                        case 2:
                            lop.setChuyenNganh(currentCell.getStringCellValue());
                            break;
                        default:
                            break;
                    }
                    cellIdx++;
                }
                lops.add(lop);
            }
            workbook.close();
            return lops;
        } catch (IOException e) {
            throw new RuntimeException("Lỗi khi đọc file Excel: " + e.getMessage());
        }
    }
}
