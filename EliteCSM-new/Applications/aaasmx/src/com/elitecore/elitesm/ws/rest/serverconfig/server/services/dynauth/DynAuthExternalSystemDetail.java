package com.elitecore.elitesm.ws.rest.serverconfig.server.services.dynauth;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.validation.Valid;
import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

@XmlRootElement(name="process-request-from-external-system")
@XmlType(propOrder={"datasourceDetail", "responceCodeData", "actionOnSucess", "actionOnFailure"})
public class DynAuthExternalSystemDetail {
	
	private boolean bAddEventTimeAttribute;
	@Pattern(regexp = "update|delete|Update|Delete|UPDATE|DELETE" , message = "Invalid value of Action On Success. It can be Update or Delete only.")
	private String actionOnSucess = "1";
	@Pattern(regexp = "update|delete|Update|Delete|UPDATE|DELETE" , message = "Invalid value of Action On Failure. It can be Update or Delete only.")
	private String actionOnFailure ="1";
	@Valid
	private DynAuthDBScanDetail datasourceDetail;
	@Valid
	private DynAuthResponseCodeDetail responceCodeData;
		
	public  DynAuthExternalSystemDetail() {
		this.datasourceDetail = new DynAuthDBScanDetail();
		this.responceCodeData = new DynAuthResponseCodeDetail();
	}
	
	@XmlElement(name="response-code-to-retry")
	public DynAuthResponseCodeDetail getResponceCodeData() {
		return responceCodeData;
	}
	public void setResponceCodeData(DynAuthResponseCodeDetail responceCodeData) {
		this.responceCodeData = responceCodeData;
	}
	
	@XmlElement(name="scan-from-database")
	public DynAuthDBScanDetail getDatasourceDetail() {
		return datasourceDetail;
	}
	public void setDatasourceDetail(DynAuthDBScanDetail datasourceDetail) {
		this.datasourceDetail = datasourceDetail;
	}
	
	@XmlElement(name="action-on-success")
	public String getActionOnSucess() {
		return actionOnSucess;
	}

	public void setActionOnSucess(String actionOnSucess) {
		this.actionOnSucess =actionOnSucess.toLowerCase();
	}
	
	@XmlElement(name="action-on-failure")
	public String getActionOnFailure() {
		return actionOnFailure;
	}
	
	public void setActionOnFailure(String actionOnFailure) {
		this.actionOnFailure = actionOnFailure.toLowerCase();
	}
	
	@XmlTransient
	public boolean getIsAddEventTimeAttribute() {
		return bAddEventTimeAttribute;
	}
	public void setIsAddEventTimeAttribute(boolean bAddEventTimeAttribute) {
		this.bAddEventTimeAttribute= bAddEventTimeAttribute;
	}
	
	@Override
	public String toString() {
		
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);

		
		out.println("External System Detail");
		if(datasourceDetail!=null)
		out.println("    "+datasourceDetail);
		out.println("    Action-On-Success = "+getActionOnSucess());
		out.println("    Action-On-Failure = "+getActionOnFailure());
		if(responceCodeData!=null)
		out.println("    "+this.responceCodeData);
		out.close();
		return stringBuffer.toString();

		
	}


}
