import com.alibaba.fastjson.JSON;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;

import java.io.*;
import java.lang.reflect.Field;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;


public class Parse {
    public final static String FILEPATH = "/Users/liujingqiang/Documents/chainbow/parseExcel/metadata.xlsx";
    public final static String TARGET_PATH = "/Users/liujingqiang/Documents/chainbow/parseExcel/pfp/";
    public static Map<Integer, PictureData> rowIndexPDataMap = new HashMap<>();
    public static Map<String, Integer> filedIndexMap = new HashMap<>();
    public static Map<String, List<String>> filedDataListMap = new ConcurrentHashMap<>();

    private static final String split = " ";
    private static final String _split = "-";

    private static final String[] deleteFields = {"description", "note", "tale_cn", "tale_文言_这一列并不会有 XD"};

    private static final String DESCRIPTION_TEMPLATE = "Nagas are creatures that is half snake and half human, with green scales covering their skin. They are tall in stature, but surprisingly agile in movement. Nagas primarily reside in Wetland Kolamuniya ('The Lake of Illusion') and have established a vast kingdom called Pelawarata ('the Realm of Swamps') there.";


    private static List<String> skipNftId = List.of("5000");

    public static void main(String[] args) throws Exception {
        savePictureFromExcel(FILEPATH, 0);
        readXlsx(FILEPATH);
        List<PFPInfo> pfpInfoList = new ArrayList<>();
        for (int i = 0; i < rowIndexPDataMap.size(); i++) {
            Field[] fields = PFPInfo.class.getDeclaredFields();
            PFPInfo pfpInfo = new PFPInfo();
            List<AttributeInfo> attributes = new ArrayList<>();
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

            List<String> collect = Arrays.stream(fields).map(Field::getName).collect(Collectors.toList());
            collect.remove("job");

            for (String filedName : filedDataListMap.keySet()) {
                if (collect.contains(filedName)) continue;
                List<String> strings = filedDataListMap.get(filedName);
                if (strings == null || strings.size() == 0) {
                    continue;
                }
                String value = filedDataListMap.get(filedName).get(i);

                if (value.equals("")) {
                    continue;
                }
                AttributeInfo attributeInfo = new AttributeInfo();
                attributeInfo.setTrait_type(filedName);
                attributeInfo.setValue(value);
                attributes.add(attributeInfo);
            }
            pfpInfo.setAttributes(attributes);
            pfpInfoList.add(pfpInfo);
        }


        for (int i = 0; i < pfpInfoList.size(); i++) {
            PFPInfo pfpInfo = pfpInfoList.get(i);
            if (skipNftId.contains(pfpInfo.getId())) {
                continue;
            }

            String dirPath = TARGET_PATH + pfpInfo.getId();
            File file = new File(dirPath);
            if (!file.exists()) {
                file.mkdirs();
            }
            convertDesc(pfpInfo);
            convertName(pfpInfo);
            pfpInfo.setImage("https://dagen.io/pfp/" + pfpInfo.getId() + "/image.png");
            saveFile(pfpInfo);
            saveImg(i + 1, pfpInfo.getId());
        }

    }

    private static void convertDesc(PFPInfo pfpInfo) {
        StringBuilder sb = new StringBuilder();
        if (!pfpInfo.getTitle().equals("")) {
            sb.append(pfpInfo.getTitle());
        }

        if (!pfpInfo.getName().equals("")) {
            sb.append(split).append(pfpInfo.getName());
        }

        if (!pfpInfo.getAppellation().equals("")) {
            sb.append(split).append(pfpInfo.getAppellation());
        }

        sb.append(split).append("is a Naga");

        if (!pfpInfo.getJob().equals("")) {
            sb.append(split).append(pfpInfo.getJob());
        }


        sb.append(split).append(DESCRIPTION_TEMPLATE);


        pfpInfo.setDescription(sb.toString().trim());

    }

    private static void convertName(PFPInfo pfpInfo) {
        StringBuilder sb = new StringBuilder();
        if (!pfpInfo.getTitle().equals("")) {
            sb.append(pfpInfo.getTitle()).append(split);
        }

        if (!pfpInfo.getName().equals("")) {
            sb.append(pfpInfo.getName());
        }

        if (!pfpInfo.getAppellation().equals("")) {
            sb.append(split).append(pfpInfo.getAppellation());
        }

        if (!pfpInfo.getMoniker().equals("") || !pfpInfo.getMoniker_meaning().equals("")) {
            sb.append(split).append(_split);

        }

        if (!pfpInfo.getMoniker().equals("")) {
            sb.append(split).append("'").append(pfpInfo.getMoniker());
        }

        if (!pfpInfo.getMoniker_meaning().equals("")) {
            sb.append(split).append("(").append(pfpInfo.getMoniker_meaning()).append(")").append("'");
        }

        pfpInfo.setName(sb.toString());

    }

    private static void saveFile(PFPInfo pfpInfo) throws IOException {
        FileWriter fileWriter = new FileWriter(TARGET_PATH + pfpInfo.getId() + "/" + pfpInfo.getId() + ".json");
        BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
        bufferedWriter.write(JSON.toJSONString(pfpInfo));
        bufferedWriter.close();
        fileWriter.close();
    }


    public static void readXlsx(String path) throws IOException {
        XSSFWorkbook excel = null;
        try {
            OPCPackage pkg = OPCPackage.open(path);
            excel = new XSSFWorkbook(pkg);
            XSSFSheet sheet = excel.getSheetAt(0);
            XSSFRow firstRow = sheet.getRow(0);
            for (Iterator<Cell> iterator = firstRow.cellIterator(); iterator.hasNext(); ) {
                XSSFCell cell = (XSSFCell) iterator.next();
                int columnIndex = cell.getColumnIndex();
                String filed = cell.getStringCellValue();
                int index = Arrays.binarySearch(deleteFields, filed);
                if (index > -1) continue;
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
                        CellType cellType = cell.getCellType();
                        if (cell.getCellType() == CellType.STRING) {
                            String string = cell.getRichStringCellValue().getString();
                            colValue = cell.getStringCellValue();
                        } else if (cell.getCellType() == CellType.NUMERIC) {
                            colValue = cell.getNumericCellValue() + "";
                        } else if (cell.getCellType() == CellType.FORMULA) {
                            colValue = cell.getCellFormula();
                        } else if (cell.getCellType() == CellType.FORMULA) {
                            colValue = cell.getCellFormula();
                        }

                    }


                    if (!Objects.equals(colValue, "") && (Objects.equals(filed, "index") || Objects.equals(filed, "id") || Objects.equals(filed, "age") || Objects.equals(filed, "age_human"))) {
                        colValue = String.valueOf((int) Math.ceil(Double.parseDouble(colValue)));
                    }

                    filedDataListMap.computeIfAbsent(filed, k -> new ArrayList<>()).add(colValue);
                }

            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (excel != null) excel.close();
        }

    }

    public static void savePictureFromExcel(String filePath, int sheetNum) {
        try (FileInputStream fis = new FileInputStream(filePath); XSSFWorkbook workbook = (XSSFWorkbook) WorkbookFactory.create(fis)) {
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
        } catch (Exception exception) {
            System.out.println(exception);
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
