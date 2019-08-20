package com.elitecore.corenetvertex.sm.driver;

import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.sm.DefaultGroupResourceData;
import com.elitecore.corenetvertex.sm.driver.csv.CsvDriverData;
import com.elitecore.corenetvertex.sm.driver.dbcdr.DbCdrDriverData;
import com.google.gson.JsonObject;
import org.hibernate.annotations.Fetch;
import org.hibernate.annotations.FetchMode;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.Transient;
import java.io.Serializable;

/**
 * Contains Basic information of Driver Management
 * @author dhyani.raval
 */
@Entity(name = "com.elitecore.corenetvertex.sm.driver.DriverData")
@Table (name = "TBLM_DRIVER")
public class DriverData extends DefaultGroupResourceData implements Serializable {

    private String name;
    private String description;
    private String driverType;
    private CsvDriverData csvDriverData;
    private DbCdrDriverData dbCdrDriverData;

    public DriverData(){
        csvDriverData = new CsvDriverData();
        dbCdrDriverData = new DbCdrDriverData();
    }

    @Column(name = "NAME")
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Column(name = "DESCRIPTION")
    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    @Column(name = "DRIVER_TYPE")
    public String getDriverType() {
        return driverType;
    }

    public void setDriverType(String driverType) {
        this.driverType = driverType;
    }

    @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "driverData")
    @Fetch(FetchMode.SELECT)
    public CsvDriverData getCsvDriverData() {
        return csvDriverData;
    }

    public void setCsvDriverData(CsvDriverData csvDriverData) {
        this.csvDriverData = csvDriverData;
    }

    @OneToOne(cascade = { CascadeType.ALL }, orphanRemoval = true, mappedBy = "driverData")
    @Fetch(FetchMode.SELECT)
    public DbCdrDriverData getDbCdrDriverData() {
        return dbCdrDriverData;
    }

    public void setDbCdrDriverData(DbCdrDriverData dbCdrDriverData) {
        this.dbCdrDriverData = dbCdrDriverData;
    }

    @Column(name = "STATUS")
    @Override
    public String getStatus() {
        return super.getStatus();
    }

    @Transient
    @Override
    public String getResourceName() {
        return getName();
    }

    @Override
    public JsonObject toJson(){

        JsonObject jsonObject = new JsonObject();
        jsonObject.addProperty(FieldValueConstants.NAME, name);
        jsonObject.addProperty(FieldValueConstants.DESCRIPTION, description);
        if(csvDriverData != null) {
            jsonObject.add("CSV Driver", csvDriverData.toJson());
        }

        if(dbCdrDriverData != null) {
            jsonObject.add("DB CDR Driver", dbCdrDriverData.toJson());
        }
        return jsonObject;
    }
}
