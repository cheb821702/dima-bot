package com.dima.bot.manager.util;

import com.dima.bot.manager.model.AutoFillEntity;
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
                        AutoFillEntity autoFillEntity = new AutoFillEntity();
                        autoFillEntity.setSeries(getString(row.getCell(0)));
                        autoFillEntity.setCarcass(getString(row.getCell(1)));
                        autoFillEntity.setStartYear(getInteger(row.getCell(2)));
                        autoFillEntity.setStopYear(getInteger(row.getCell(3)));
                        autoFillEntity.setDetail(getString(row.getCell(4)));
                        autoFillEntity.setCost(getInteger(row.getCell(5)));
                        autoFillEntity.setDeliveryTime(deliveryTimeList.get(getInteger(row.getCell(6))));
                        autoFillEntity.setState(stateList.get(getInteger(row.getCell(7))));
                        autoFillEntity.setNote(getString(row.getCell(8)));

                        if(autoFillEntity.getSeries()!=null && autoFillEntity.getCarcass()!=null &&
                                autoFillEntity.getDetail()!=null && autoFillEntity.getCost()!=null &&
                                autoFillEntity.getDeliveryTime()!=null && autoFillEntity.getState()!=null)
                        {
                            entities.add(autoFillEntity);
                        }
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
        if(cell != null) {
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
        }
        return null;
    }

    private String getString(Cell cell) {
        if(cell != null) {
            switch (cell.getCellType())
            {
                case Cell.CELL_TYPE_NUMERIC:
                    return String.valueOf((int)cell.getNumericCellValue());
                case Cell.CELL_TYPE_STRING:
                     return cell.getStringCellValue();
            }
        }
        return null;
    }
}
