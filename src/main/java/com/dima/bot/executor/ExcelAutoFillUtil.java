package com.dima.bot.executor;

import com.dima.bot.executor.model.AutoFillEntity;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.File;
import java.io.FileInputStream;
import java.util.*;

/**
 * Created with IntelliJ IDEA.
 * User: ShemiareiD
 * Date: 4/24/14
 * Time: 4:38 PM
 * To change this template use File | Settings | File Templates.
 */
public class ExcelAutoFillUtil {

    private static Map<Integer,String> deliveryTimeList = new HashMap<Integer,String>();
    private static Map<Integer,String> stateList = new HashMap<Integer,String>();

    static {
        deliveryTimeList.put(1,"в наличии");
        deliveryTimeList.put(2,"1 день");
        deliveryTimeList.put(3,"1-2 дня");
        deliveryTimeList.put(4,"2-3 дня");
        deliveryTimeList.put(5,"3 - 5 дней");
        deliveryTimeList.put(6,"5-7 дней");
        deliveryTimeList.put(7,"7-10 дней");
        deliveryTimeList.put(8,"10-12 дней");
        deliveryTimeList.put(9,"12-15 суток");
        deliveryTimeList.put(10,"15-20 суток");
        deliveryTimeList.put(11,"20-30 дней");
        deliveryTimeList.put(12,"30-45 дней");
        deliveryTimeList.put(13,"от 45 суток");

        stateList.put(1,"новый(я) оригинал");
        stateList.put(2,"новый(я) неоригинал");
        stateList.put(3,"б.у. оригинал");
        stateList.put(4,"б.у. БЕЗ дефектов");
        stateList.put(5,"б.у. с дефектом");
        stateList.put(6,"восстановленный(я)");
        stateList.put(7,"б.у. неоригинал");
        stateList.put(8,"ремонт");
    }

    public List<AutoFillEntity> getEntities(String path) {
        List<AutoFillEntity> entities = new ArrayList<AutoFillEntity>();
        if(path != null && !path.trim().isEmpty()) {
            try
            {
                FileInputStream file = new FileInputStream(new File(path));

                XSSFWorkbook workbook = new XSSFWorkbook(file);
                XSSFSheet sheet = workbook.getSheetAt(0);

                int rowi = 0;
                Iterator<Row> rowIterator = sheet.iterator();
                while (rowIterator.hasNext())                {
                    Row row = rowIterator.next();
                    if(rowi > 0) {
                        Iterator<Cell> cellIterator = row.cellIterator();

                        AutoFillEntity autoFillEntity = new AutoFillEntity();
                        int celli = 0;
                        while (cellIterator.hasNext())
                        {
                            Cell cell = cellIterator.next();
                            if(celli == 0) {
                                autoFillEntity.setSeries(getString(cell));
                            } else if(celli == 1) {
                                autoFillEntity.setCarcass(getString(cell));
                            } else if(celli == 2) {
                                autoFillEntity.setStartYear(getInteger(cell));
                            } else if(celli == 3) {
                                autoFillEntity.setStopYear(getInteger(cell));
                            } else if(celli == 4) {
                                autoFillEntity.setDetail(getString(cell));
                            } else if(celli == 5) {
                                autoFillEntity.setCost(getInteger(cell));
                            } else if(celli == 6) {
                                autoFillEntity.setDeliveryTime(deliveryTimeList.get(getInteger(cell)));
                            } else if(celli == 7) {
                                autoFillEntity.setState(stateList.get(getInteger(cell)));
                            } else if(celli == 8) {
                                autoFillEntity.setNote(getString(cell));
                            }
                            celli++;
                        }
                        entities.add(autoFillEntity);
                    }
                    rowi++;
                }
                file.close();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return entities;
    }

    private Integer getInteger(Cell cell) {
        switch (cell.getCellType())
        {
            case Cell.CELL_TYPE_NUMERIC:
                try {
                    return (int) cell.getNumericCellValue();
                } catch (NumberFormatException e) {
                    return null;
                }
            case Cell.CELL_TYPE_STRING:
                try {
                    return Integer.valueOf(cell.getStringCellValue());
                } catch (NumberFormatException e) {
                    return null;
                }
        }
        return null;
    }

    private String getString(Cell cell) {
        switch (cell.getCellType())
        {
            case Cell.CELL_TYPE_NUMERIC:
                return String.valueOf(cell.getNumericCellValue());
            case Cell.CELL_TYPE_STRING:
                 return cell.getStringCellValue();
        }
        return null;
    }
}
