import com.alibaba.fastjson.JSON;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class Parse {
    public final static String FILEPATH = "/Users/liujingqiang/Documents/chainbow/parseExcel/metadata.xlsx";
    public final static String TARGET_PATH = "/Users/liujingqiang/Documents/chainbow/parseExcel/pfp/";
    public static Map<Integer, PictureData> rowIndexPDataMap = new HashMap<>();
    public static Map<String, Integer> filedIndexMap = new HashMap<>();
    public static Map<String, List<String>> filedDataListMap = new ConcurrentHashMap<>();


    public static void main(String[] args) throws Exception {
        savePictureFromExcel(FILEPATH, 0);
        readXlsx(FILEPATH);
        List<PFPInfo> pfpInfoList = new ArrayList<>();
        for (int i = 1; i < rowIndexPDataMap.size() + 1; i++) {
            Field[] fields = PFPInfo.class.getDeclaredFields();
            PFPInfo pfpInfo = new PFPInfo();
            for (Field field : fields) {
                Class<? extends PFPInfo> c = pfpInfo.getClass();
                Field f = c.getDeclaredField(field.getName());
                List<String> strings = filedDataListMap.get(field.getName());
                if (strings == null || strings.size() == 0) {
                    continue;
                }
                f.setAccessible(true);
                f.set(pfpInfo, filedDataListMap.get(field.getName()).get(i));
            }
            pfpInfoList.add(pfpInfo);

        }


        for (int i = 0; i < pfpInfoList.size(); i++) {
            PFPInfo pfpInfo = pfpInfoList.get(i);
            String dirPath = TARGET_PATH + pfpInfo.getId();
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            pfpInfo.setImage_preview_url("https://dagen.io/gens/" + pfpInfo.getId() + "+/image.png");
            saveFile(pfpInfo);
            saveImg(i + 1, pfpInfo.getId());
        }

    }

    private static void saveFile(PFPInfo pfpInfo) throws IOException {
        FileWriter fileWriter = new FileWriter(TARGET_PATH + pfpInfo.getId() + "/" + pfpInfo.getId() + ".json");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(JSON.toJSONString(pfpInfo));
        bufferedWriter.close();
    }


    public static void readXlsx(String path) {
        try {
            OPCPackage pkg = OPCPackage.open(path);
            XSSFWorkbook excel = new XSSFWorkbook(pkg);
            XSSFSheet sheet = excel.getSheetAt(0);
            XSSFRow firstRow = sheet.getRow(0);
            for (Iterator<Cell> iterator = firstRow.cellIterator(); iterator.hasNext(); ) {
                XSSFCell cell = (XSSFCell) iterator.next();
                int columnIndex = cell.getColumnIndex();
                String filed = cell.getStringCellValue();
                filedIndexMap.put(filed, columnIndex);
            }

            for (Row row : sheet) {
                int rowNum = row.getRowNum();
                Cell firstCell = row.getCell(0);
                if (firstCell == null) return;
                if (rowNum == 0) continue;
                for (String filed : filedIndexMap.keySet()) {
                    int columnIndex = filedIndexMap.get(filed);
                    Cell cell = row.getCell(columnIndex);
                    String colValue = "";

                    if (cell != null) {
                        if (cell.getCellType() == CellType.STRING) {
                            colValue = cell.getStringCellValue();
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            colValue = cell.getNumericCellValue() + "";
                        } else if (cell.getCellType() == CellType.FORMULA) {
                            colValue = cell.getCellFormula();
                        } else if (cell.getCellType() == CellType.FORMULA) {
                            colValue = cell.getCellFormula();
                        }

                    }


                    if (!Objects.equals(colValue, "") && (Objects.equals(filed, "index") || Objects.equals(filed, "id"))) {
                        colValue = String.valueOf((int) Math.ceil(Double.parseDouble(colValue)));
                    }

                    filedDataListMap.computeIfAbsent(filed, k -> new ArrayList<>()).add(colValue);
                }

            }

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void savePictureFromExcel(String filePath, int sheetNum) throws Exception {
        FileInputStream fis = new FileInputStream(filePath);
        XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(fis);
        XSSFSheet sheet = workbook.getSheetAt(sheetNum);
        List<XSSFShape> list = sheet.getDrawingPatriarch().getShapes();
        for (int i = 0; i < sheet.getDrawingPatriarch().getShapes().size(); i++) {
            XSSFShape shape = list.get(i);
            XSSFClientAnchor anchor = (XSSFClientAnchor) shape.getAnchor();
            if (shape instanceof XSSFPicture) {
                XSSFPicture pic = (XSSFPicture) shape;
                int rowIndex = anchor.getRow1();
                XSSFPictureData picData = pic.getPictureData();
                rowIndexPDataMap.put(rowIndex, picData);
            }
        }
    }

    public static void saveImg(int rowIndex, String id) {
        String dirPath = TARGET_PATH + id;
        File file = new File(dirPath);
        if (!file.exists()) {
            file.mkdirs();
        }

        String filePath = dirPath + "/image.png";
        PictureData picData = rowIndexPDataMap.get(rowIndex);
        if (picData == null) {
            return;
        }
        byte[] data = picData.getData();
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filePath);
            out.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) out.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
