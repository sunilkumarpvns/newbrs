package com.elitecore.netvertexsm.datamanager.devicemgmt.data;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.commons.base.Strings;
import com.elitecore.netvertexsm.web.core.base.BaseData;


public class TACDetailData extends BaseData{
	
    private Long tacDetailId;
    private Long tac;
    private String brand;
    private String model;
    private String hardwareType;
    private String operatingSystem;
    private Integer year;
    private String additionalInfo;
    private final String DELIMITER = ",";
 
    
	public Long getTacDetailId() {
		return tacDetailId;
	}
	public void setTacDetailId(Long tacDetailId) {
		this.tacDetailId = tacDetailId;
	}
	public Long getTac() {
		return tac;
	}
	public void setTac(Long tac) {
		this.tac = tac;
	}
	public String getBrand() {
		return brand;
	}
	public void setBrand(String brand) {
		this.brand = brand;
	}
	public String getModel() {
		return model;
	}
	public void setModel(String model) {
		this.model = model;
	}
	public String getHardwareType() {
		return hardwareType;
	}
	public void setHardwareType(String hardwareType) {
		this.hardwareType = hardwareType;
	}
	public String getOperatingSystem() {
		return operatingSystem;
	}
	public void setOperatingSystem(String operatingSystem) {
		this.operatingSystem = operatingSystem;
	}
	public Integer getYear() {
		return year;
	}
	public void setYear(Integer year) {
		this.year = year;
	}
	
	public String getCSVData(){
		StringWriter sw = new StringWriter();
		PrintWriter out = new PrintWriter(sw);
		if(tac!=null){
			out.print(tac);
		}
		out.print(DELIMITER);
		if(Strings.isNullOrBlank(brand) == false){
			out.print(brand);
		}
		out.print(DELIMITER);
		if(Strings.isNullOrBlank(model) == false){
			out.print(model);
		}
		out.print(DELIMITER);
		if(Strings.isNullOrBlank(hardwareType) == false){
			out.print(hardwareType);
		}
		out.print(DELIMITER);
		if(Strings.isNullOrBlank(operatingSystem) == false){
			out.print(operatingSystem);
		}
		out.print(DELIMITER);
		if(year!=null){
			out.print(year);
		}
		out.print(DELIMITER);
		if(Strings.isNullOrBlank(additionalInfo) == false){
			out.print(additionalInfo);
		}
		out.print(DELIMITER);
		out.println();
		return sw.toString();
	}
	public String getAdditionalInfo() {
		return additionalInfo;
	}
	public void setAdditionalInfo(String additionalInfo) {
		this.additionalInfo = additionalInfo;
	}


}
