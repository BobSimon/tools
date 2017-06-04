package cn.tools.entity;

import java.io.Serializable;
import java.util.Date;

public class User implements Serializable {

    private static final long serialVersionUID = -4204906018558114350L;

    private String name;// 姓名
    private String dateString;// 日期字符串
    private String tag;// 休息 or 正常班
    private String remark;// 休息日 or 工作日
    private String status = "正常";
    private String startTime;// 打卡开始时间，示例：09:00
    private String endTime;// 打卡结束时间，示例：18:00

    private Date date;// 当天日期
    private Date startDate;// 当天上班打卡时间
    private Date endDate;// 当天下班打卡时间

    public User() {
        super();
    }

    public User(String name, String dateString, String tag, String remark, String startTime, String endTime) {
        super();
        this.name = name;
        this.dateString = dateString;
        this.tag = tag;
        this.remark = remark;
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDateString() {
        return dateString;
    }

    public void setDateString(String dateString) {
        this.dateString = dateString;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public Date getStartDate() {
        return startDate;
    }

    public void setStartDate(Date startDate) {
        this.startDate = startDate;
    }

    public Date getEndDate() {
        return endDate;
    }

    public void setEndDate(Date endDate) {
        this.endDate = endDate;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", dateString=" + dateString + ", tag=" + tag + ", remark=" + remark + ", status=" + status + ", startTime=" + startTime + ", endTime=" + endTime + "]";
    }

}
