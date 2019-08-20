package com.elitecore.nvsmx.sm.model.license;

import com.elitecore.corenetvertex.sm.ResourceData;

import javax.persistence.Transient;
import java.io.File;
import java.sql.Timestamp;

public class LicenseData extends ResourceData {

    private String instanceName;
    private String ip;
    private String status;
    private Timestamp lastUpdateTime;

    private String version;
    private String key;
    private String value;

    private String licenseKey;

    private File licenseFile;
    private String message;
    private int messageCode;

    public LicenseData(){
        super();
    }

    public String getInstanceName() {
        return instanceName;
    }

    public void setInstanceName(String instanceName) {
        this.instanceName = instanceName;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }

    @Override
    public String getStatus() {
        return status;
    }

    @Override
    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    @Transient
    public String getResourceName(){
        return getInstanceName();
    }

    public String getVersion(){
        return version;
    }

    public void setVersion(String version){
        this.version = version;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public void setLastUpdateTime(Timestamp lastUpdateTime) {
        this.lastUpdateTime = lastUpdateTime;
    }

    public File getLicenseFile() {
        return licenseFile;
    }

    public void setLicenseFile(File licenseFile) {
        this.licenseFile = licenseFile;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getMessageCode() {
        return messageCode;
    }

    public void setMessageCode(int message) {
        this.messageCode = message;
    }

    public Timestamp getLastUpdateTime() {
        return lastUpdateTime;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getLicenseKey(){
        return licenseKey;
    }

    public void setLicenseKey(String licenseKey) {
        this.licenseKey = licenseKey;
    }
}
