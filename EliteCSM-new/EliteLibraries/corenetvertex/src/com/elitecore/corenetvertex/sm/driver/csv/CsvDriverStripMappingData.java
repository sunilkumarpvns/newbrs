package com.elitecore.corenetvertex.sm.driver.csv;

import com.google.gson.JsonObject;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;
import java.io.Serializable;

/**
 * Contains strip mapping information of CSV Driver Management
 * @author dhyani.raval
 */

@Entity(name = "com.elitecore.corenetvertex.sm.driver.csv.CsvDriverStripMappingData")
@Table(name = "TBLM_CSV_DRIVER_STRIP_MAP")
public class CsvDriverStripMappingData implements Serializable {

    private String id;
    private String pcrfKey;
    private String pattern;
    private String separator;

    @Id
    @GeneratedValue(generator = "eliteSequenceGenerator")
    @Column(name =  "ID")
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    @Column(name = "PCRFKEY")
    public String getPcrfKey() {
        return pcrfKey;
    }

    public void setPcrfKey(String pcrfKey) {
        this.pcrfKey = pcrfKey;
    }

    @Column(name = "PATTERN")
    public String getPattern() {
        return pattern;
    }

    public void setPattern(String pattern) {
        this.pattern = pattern;
    }

    @Column(name = "SEPARATOR")
    public String getSeparator() {
        return separator;
    }

    public void setSeparator(String separator) {
        this.separator = separator;
    }

    public JsonObject toJson() {
        JsonObject jsonObject =  new JsonObject();
        jsonObject.addProperty("Pattern",pattern);
        jsonObject.addProperty("Separator",separator);
        return jsonObject;
    }
}
