package com.elitecore.corenetvertex.sm.driver.csv;

import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Contains field mapping information of CSV Driver Management
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.driver.csv.CsvDriverFieldMappingData")
@Table(name = "TBLM_CSV_DRIVER_FIELD_MAP")
public class CsvDriverFieldMappingData implements Serializable{

    private String id;
    private String headerField;
    private String pcrfKey;
    private Integer orderNo;

    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "HEADER_FIELD")
    public String getHeaderField() {
        return headerField;
    }

    public void setHeaderField(String headerField) {
        this.headerField = headerField;
    }

    @Column(name = "PCRFKEY")
    public String getPcrfKey() {
        return pcrfKey;
    }

    public void setPcrfKey(String pcrfKey) {
        this.pcrfKey = pcrfKey;
    }

    @Column(name = "ORDER_NO")
    public Integer getOrderNo() {
        return orderNo;
    }

    public void setOrderNo(Integer orderNo) {
        this.orderNo = orderNo;
    }

    public JsonObject toJson() {
        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty("Header Field", headerField);
        jsonObject.addProperty("Order Number", orderNo);
        return  jsonObject;
    }
}
