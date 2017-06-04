package cn.tools.service.impl;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import cn.tools.common.exception.BusinessException;
import cn.tools.common.util.DateUtil;
import cn.tools.entity.User;
import cn.tools.service.ExcelService;

@Service
public class ExcelServiceImpl implements ExcelService {

    private static final Logger log = LoggerFactory.getLogger(ExcelServiceImpl.class);

    private static List<String> list = Arrays.asList("yyyy/mm;@", "m/d/yy", "yy/m/d", "mm/dd/yy", "dd-mmm-yy", "yyyy/m/d", "m/d/yy h:mm", "yyyy/mm/dd hh:mm", "yyyy/m/d hh:mm");

    // 从excel的单元格当中解析出字符串
    public static String getStringCellValue(Row row, int index) {
        Cell cell = row.getCell(index);
        if (cell == null) {
            return null;
        }
        String cellValue = "";
        if (cell.getCellType() == Cell.CELL_TYPE_BLANK || cell.getCellType() == Cell.CELL_TYPE_STRING) {
            cellValue = cell.getStringCellValue();
        } else if (cell.getCellType() == Cell.CELL_TYPE_NUMERIC) {
            cellValue = new BigDecimal(Double.toString(cell.getNumericCellValue())).toString();// doubel 先转为string，否则 1.2会变为 1.9999999
        } else {
            throw new BusinessException("import_point_data_fail", "上传失败,第" + (row.getRowNum() + 1) + "行," + "第" + (index + 1) + "列的格式不正确"); // 小数点后保留一位
        }
        return cellValue;
    }

    // 从excel的单元格当中解析出日期
    public static Date getDateCellValue(Row row, int index) throws BusinessException {
        Cell cell = row.getCell(index);
        if (cell == null)
            return null;

        Date date = null;

        for (String string : list) {
            if (string.equals(cell.getCellStyle().getDataFormatString())) {
                date = cell.getDateCellValue();
                break;
            }
        }
        return date;
    }

    // 当前日期是周未还是工作日
    public String getDateRemark(Date date) {
        int s = DateUtil.getWeekByDate(date);
        // log.info("# 日期备注：{} , {}", DateUtil.dateToString(date, DateUtil.fm_yyyy_MM_dd_HHmmssSSS), s);
        if (s == 6 || s == 7)
            return "休息日";
        else
            return "工作日";
    }

    @Override
    public byte[] upload(byte[] file) {
        try {
            log.info("#import excel data...");

            if (file.length == 0) {
                log.error("# 非法文件");
                throw new BusinessException("import_fail", "参数信息错误。");
            }

            Date curDate = DateUtil.parse(Calendar.getInstance().getTime());// 当前日期，去除时分秒
            Date start = DateUtil.firstDayOfLastMonth(curDate);// 上个月第一天
            Date end = DateUtil.yesterday(DateUtil.firstDayOfMonth(curDate));// 上个月最后一天
            log.info("# startTime={} ，endTime={}", DateUtil.dateToString(start, DateUtil.fm_yyyy_MM_dd_HHmmssSSS), DateUtil.dateToString(end, DateUtil.fm_yyyy_MM_dd_HHmmssSSS));

            List<Date> dates = DateUtil.getDateRangeList(start, end);// 上个月日期,第一天至最后一天
            Map<String, String> dateMap = new TreeMap<String, String>();
            for (Date d : dates) {
                // 转成Map，方便后面取值
                dateMap.put(DateUtil.dateToString(d, DateUtil.fmx_yyyy_MM_dd), getDateRemark(d));
                // log.info("# {} {}", DateUtil.dateToString(d, DateUtil.fmx_yyyy_MM_dd), getDateRemark(d));
            }

            // TreeMap<用户中, TreeMap<当天日期, List<当天所有日期>>>
            TreeMap<String, TreeMap<String, List<Date>>> map = new TreeMap<String, TreeMap<String, List<Date>>>();// 临时存放考勤数据
            List<String> names = new ArrayList<String>();// 主要用于排序，没其它意义

            final int nameIndex = 0, timeIndex = 1;
            Workbook wb = new HSSFWorkbook(new ByteArrayInputStream(file));// v.2007 office,即支持.xls格式
            Sheet sheet = wb.getSheetAt(0);// 获取当前sheet
            // Workbook wb = new XSSFWorkbook(stream);// v.2007+ office
            int rows = sheet.getPhysicalNumberOfRows();// 获取总行号
            if (rows > 0) {
                for (int i = 0; i < rows; i++) {
                    Row row = sheet.getRow(i);
                    String name = getStringCellValue(row, nameIndex);// 获取姓名
                    Date time = getDateCellValue(row, timeIndex);// 获取打卡时间
                    if (time == null)
                        continue;
                    time = DateUtil.removeMillisecond(time);

                    String dateString = DateUtil.dateToString(time, DateUtil.fmx_yyyy_MM_dd);// 当天日期字符串

                    // 打卡数据按用度维度分类，并按日期进行分组，同一天的考虑数据放一块
                    TreeMap<String, List<Date>> treeMap = null;
                    List<Date> list = null;
                    if (!map.containsKey(name)) {
                        treeMap = new TreeMap<String, List<Date>>();
                        list = new ArrayList<Date>();
                        names.add(name);
                    } else {
                        treeMap = map.get(name);
                        if (!treeMap.containsKey(dateString)) {
                            list = new ArrayList<Date>();
                        } else {
                            list = treeMap.get(dateString);
                        }
                    }

                    list.add(time);
                    treeMap.put(dateString, list);
                    map.put(name, treeMap);
                    log.info("# import , rowIndex={}, name={} , time={} ", (row.getRowNum() + 1), name, DateUtil.dateToString(time, DateUtil.fm_yyyy_MM_dd_HHmmssSSS));
                }
            }

            // 解析成想要的格式：User [name=朱文彬, dateString=2017/05/26, tag=正常班, remark=工作日, status=正常, startTime=09:09, endTime=18:03]
            List<User> users = new ArrayList<User>();
            for (String n : names) {
                String name = n;
                TreeMap<String, List<Date>> treeMap = map.get(n);

                for (Map.Entry<String, String> entry : dateMap.entrySet()) {
                    String key = entry.getKey();

                    String remark = dateMap.get(key), tag = "正常班", startTime = "", endTime = "";
                    Date date, startDate, endDate;

                    if (treeMap.containsKey(key)) {
                        List<Date> dateList = treeMap.get(key);
                        if (dateList.size() > 1) {
                            Collections.sort(dateList);// 升序
                            startDate = dateList.iterator().next();// 上班开始时间
                            endDate = dateList.get(dateList.size() - 1);// 上班结束时间

                            if (startDate.compareTo(endDate) == 0) {// 日期相等
                                final Calendar c = Calendar.getInstance();
                                c.setTime(startDate);
                                if (c.get(Calendar.AM_PM) == 0) {// 判断是不是上午
                                    startTime = DateUtil.dateToString(startDate, DateUtil.fm_HHmm);// 上班打卡时间
                                } else {
                                    endTime = DateUtil.dateToString(startDate, DateUtil.fm_HHmm);// 下班打卡时间
                                }
                            } else {
                                startTime = DateUtil.dateToString(startDate, DateUtil.fm_HHmm);// 上班打卡时间
                                endTime = DateUtil.dateToString(endDate, DateUtil.fm_HHmm);// 上班打卡时间
                            }
                        } else {
                            date = dateList.iterator().next();
                            final Calendar c = Calendar.getInstance();
                            c.setTime(date);
                            if (c.get(Calendar.AM_PM) == 0) {// 判断是不是上午
                                startTime = DateUtil.dateToString(date, DateUtil.fm_HHmm);// 上班打卡时间
                            } else {
                                endTime = DateUtil.dateToString(date, DateUtil.fm_HHmm);// 下班打卡时间
                            }
                        }
                    }

                    if (StringUtils.equals(remark, "休息日"))
                        tag = "休息";

                    users.add(new User(name, key, tag, remark, startTime, endTime));

                }
            }

            Sheet newSheet = wb.createSheet("分析"); //
            for (int i = 0; i < users.size(); i++) {
                User u = users.get(i);
                Row row = newSheet.createRow(i); // 创建一个行

                // [name=朱文彬, dateString=2017/05/26, tag=正常班, remark=工作日, status=正常, startTime=09:09, endTime=18:03]
                row.createCell(0).setCellValue(u.getName()); // 创建一个单元格 第1列,名称
                row.createCell(1).setCellValue(u.getDateString()); // 创建一个单元格 第2列，日期字符串
                row.createCell(2).setCellValue(u.getTag()); // 创建一个单元格 第3列，休息 or 正常班
                row.createCell(3).setCellValue(u.getRemark()); // 创建一个单元格 第4列， 休息日 or 工作日
                row.createCell(4).setCellValue(u.getStatus()); // 创建一个单元格 第5列， 正常
                row.createCell(5).setCellValue(u.getStartTime()); // 创建一个单元格 第6列， 正常
                row.createCell(6).setCellValue(u.getEndTime()); // 创建一个单元格 第7列， 正常
                row.createCell(7).setCellValue(u.getRemark()); // 创建一个单元格 第8列， 休息日 or 工作日
            }

            // 将wb对象转成byte流
            ByteArrayOutputStream os = new ByteArrayOutputStream();
            wb.write(os);
            wb.close();
            return os.toByteArray();
        } catch (

        BusinessException e) {
            log.error("## import excel fail , error message={}", e.getLocalizedMessage());
            throw e;
        } catch (Exception e) {
            log.error("## import excel fail , error message={}", e.getLocalizedMessage());
            throw new BusinessException("个性化扣点导入校验出错");
        }

    }

}
