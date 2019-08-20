package com.elitecore.aaa.radius.plugins.strop.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.radius.plugins.strop.conf.StringOperationPluginConf;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;
import com.elitecore.coreradius.commons.util.RadiusUtility;

@XmlType(propOrder = {})
@XmlRootElement(name = "string-operation-plugin")
@ConfigurationProperties(moduleName ="STRING_OPERATION-PLUGIN_CONFIGURABLE",synchronizeKey ="STRING_OPERATION_PLUGIN", readWith = XMLReader.class, reloadWith = XMLReader.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","plugins"},name = "string-operation-plugin")
@Reloadable(type = StringOperationPluginConfigurable.class)
public class StringOperationPluginConfigurable extends Configurable implements StringOperationPluginConf{

	private static final String VENDOR_ID = "VENDOR_ID";
	private static final String ATTRIBUTE_ID = "ATTRIBUTE_ID";
	private static final String OPERATION = "OPERATION";
	private static final String FIRST_ONLY = "FIRST_ONLY";
	private static final String CONDITIONS = "CONDITIONS";
	private static final String MODULE = "STRING_OPERATION-PLUGIN_CONFIGURABLE";
	private List<StringOperationParamsDetail> stringOperationList;
	private List<Map<String, Object>> operationDetails;

	
	public StringOperationPluginConfigurable() { 
		stringOperationList = new ArrayList<StringOperationParamsDetail>();
		operationDetails = new ArrayList<Map<String, Object>>();
	}

	@XmlElementWrapper(name ="string-operation-list")
	@XmlElement(name="string-operation")
	@Reloadable(type = StringOperationParamsDetail.class)
	public List<StringOperationParamsDetail> getStringOperationList() {
		return stringOperationList;
	}
	public void setStringOperationList(
			List<StringOperationParamsDetail> stringOperationList) {
		this.stringOperationList = stringOperationList;
	}
	
	@PostRead @PostReload
	public void postReadAndReloadProcessing() {
		List<Map<String, Object>> tmpOperationDetails = new ArrayList<Map<String, Object>>();

		List<StringOperationParamsDetail> paramsList = getStringOperationList();
		if(paramsList !=null && paramsList.size() >0){
			int noOfOperation = paramsList.size();
			StringOperationParamsDetail operationParamsDetail;
			for(int i=0;i<noOfOperation;i++){
				operationParamsDetail = paramsList.get(i);
				String strAttributeId="";
				try{
					Map<String, Object> details = new HashMap<String, Object>();
					if(operationParamsDetail.getAttributeId() != null){
						strAttributeId = operationParamsDetail.getAttributeId(); 
						int[] tmpAttrId = RadiusUtility.getAttributeIds(strAttributeId);
						long vendorId = tmpAttrId[0];
						int[] attributeId = new int[tmpAttrId.length-1];
						System.arraycopy(tmpAttrId, 1, attributeId, 0, tmpAttrId.length-1);
						details.put(VENDOR_ID, vendorId);
						details.put(ATTRIBUTE_ID, attributeId);
					} 
					if(operationParamsDetail.getOperation() !=null){
						details.put(OPERATION, operationParamsDetail.getOperation());
					}
					if(operationParamsDetail.getFirstOnly() !=null){
						boolean bFirstOnly;
						bFirstOnly = Boolean.parseBoolean(operationParamsDetail.getFirstOnly());
						details.put(FIRST_ONLY, bFirstOnly);
					}
					if(operationParamsDetail.getConditions()!=null){
						details.put(CONDITIONS, operationParamsDetail.getConditions());
					}

					if(details.get(VENDOR_ID)!=null && details.get(ATTRIBUTE_ID) !=null)
						tmpOperationDetails.add(details);
				}catch(NumberFormatException e){
					LogManager.getLogger().debug(MODULE,"Invalid Attribute Id :"+strAttributeId+ ",operation can't apply for this attribute id");
				}
			}
			operationDetails = tmpOperationDetails;
		}
		operationDetails = tmpOperationDetails;
	}

	@PostWrite
	public void postWriteProcessing() {

	}
	
	@Override
	@XmlTransient
	public List<Map<String, Object>> getOperationDetails() {
		return operationDetails;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("---String Operation Plugin Details---");
		for(StringOperationParamsDetail stringOperationParameter : stringOperationList){
			out.println("--String Operation--");
			out.println(stringOperationParameter);
		}
		out.close();
		return stringBuffer.toString();
	}
}

@XmlType(propOrder={})
@Reloadable(type = StringOperationParamsDetail.class)
class StringOperationParamsDetail{

	private String attributeId;
	private String conditions;
	private String firstOnly;
	private String operation;

	public StringOperationParamsDetail() {
		//required by Jaxb.
	}

	@XmlElement(name="attribute-id",type = String.class)
	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	@XmlElement(name="conditions",type = String.class)
	public String getConditions() {
		return conditions;
	}

	public void setConditions(String conditions) {
		this.conditions = conditions;
	}
	@XmlElement(name="first-only",type = String.class)
	public String getFirstOnly() {
		return firstOnly;
	}

	public void setFirstOnly(String firstOnly) {
		this.firstOnly = firstOnly;
	}
	@XmlElement(name="operation",type = String.class)
	public String getOperation() {
		return operation;
	}

	public void setOperation(String operation) {
		this.operation = operation;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Operation: " + operation);
		out.println("Attribute ID: " + attributeId);
		out.println("Condition: " + conditions);
		out.println("First Only: " + firstOnly);
		out.close();
		return stringBuffer.toString();
	}
}