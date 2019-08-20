package com.elitecore.nvsmx.ws.subscription.data;

import javax.xml.bind.annotation.XmlType;
import java.io.Serializable;
@XmlType(propOrder={"dayOfWeek", "timePeriod","orderNo","type"})
public class TimeSlotInformation implements Serializable {

    private static final long serialVersionUID = 3369608460484901596L;
    private String dayOfWeek;
    private String timePeriod;
    private Integer orderNo;
    private String type;

    public TimeSlotInformation() {
    }

    public TimeSlotInformation(String dayOfWeek, String timePeriod, String type, Integer orderNumber) {
        this.dayOfWeek = dayOfWeek;
        this.timePeriod = timePeriod;
        this.type = type;
        this.orderNo = orderNumber;
    }

    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getDayOfWeek() {
        return dayOfWeek;
    }

    public void setDayOfWeek(String dayOfWeek) {
        this.dayOfWeek = dayOfWeek;
    }

    public String getTimePeriod() {
        return timePeriod;
    }

    public void setTimePeriod(String timePeriod) {
        this.timePeriod = timePeriod;
    }


}
