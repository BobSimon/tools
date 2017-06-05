package cn.tools.entity;

import java.io.Serializable;

public class User implements Serializable {

    private static final long serialVersionUID = -4204906018558114350L;

    private String name;// 姓名
    private String dateString;// 日期字符串
    private String type;// 休息 or 正常班
    private String option;// 休息日 or 工作日
    private String status = "正常";
    private String startTime;// 打卡开始时间，示例：09:00
    private String endTime;// 打卡结束时间，示例：18:00
    private String remark;// 备注

    public User() {
        super();
    }

    public User(String name, String dateString, String type, String option, String startTime, String endTime, String remark) {
        super();
        this.name = name;
        this.dateString = dateString;
        this.type = type;
        this.option = option;
        this.startTime = startTime;
        this.endTime = endTime;
        this.remark = remark;
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

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getOption() {
        return option;
    }

    public void setOption(String option) {
        this.option = option;
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

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    @Override
    public String toString() {
        return "User [name=" + name + ", dateString=" + dateString + ", type=" + type + ", option=" + option + ", status=" + status + ", startTime=" + startTime + ", endTime=" + endTime + ", startTime=" + startTime + ", remark=" + remark + "]";
    }

}
