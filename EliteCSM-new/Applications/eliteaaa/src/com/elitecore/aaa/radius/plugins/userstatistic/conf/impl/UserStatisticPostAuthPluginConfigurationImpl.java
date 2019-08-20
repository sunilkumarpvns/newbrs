package com.elitecore.aaa.radius.plugins.userstatistic.conf.impl;

import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import net.sf.json.JSONObject;

import com.elitecore.aaa.radius.plugins.userstatistic.conf.UserStatisticPostAuthPluginConfiguration;
import com.elitecore.commons.base.Differentiable;
import com.elitecore.core.commons.plugins.PluginInfo;

@XmlType(propOrder={})
@XmlRootElement(name = "user-statistic-post-auth-plugin")
public class UserStatisticPostAuthPluginConfigurationImpl implements UserStatisticPostAuthPluginConfiguration ,Differentiable{

	private String name;
	private String description;
	private String status;
	private String dataSourceName;
	private String tableName;
	private long dbQueryTimeoutInMs;
	private long maxQueryTimeoutCount;
	private long batchUpdateIntervalInMs;
	private List<AttributeDetail> attributeList;

	/* Transient properties */
	private List<Map<String, Object>> dbFieldMapping;
	private PluginInfo pluginInfo;
	
	public UserStatisticPostAuthPluginConfigurationImpl() {
		this.attributeList = new ArrayList<AttributeDetail>();		
		this.dbFieldMapping = new ArrayList<Map<String, Object>>();
	}
	
	@Override
	@XmlElement(name="name", type = String.class)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	@XmlElement(name="description", type = String.class)
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	
	@Override
	@XmlElement(name = "status", type = String.class)
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	
	@XmlElementWrapper(name ="db-field-mapping")
	@XmlElement(name = "attribute-detail")
	public List<AttributeDetail> getAttributeList() {
		return attributeList;
	}
	public void setAttributeList(List<AttributeDetail> attributeList) {
		this.attributeList = attributeList;
	}
	
	@Override
	@XmlElement(name ="datasource-name",type =String.class)
	public String getDataSourceName() {
		return dataSourceName;
	}
	public void setDataSourceName(String dataSourceName) {
		this.dataSourceName = dataSourceName;
	}
	
	@Override
	@XmlElement(name ="tablename",type = String.class)
	public String getTableName() {
		return tableName;
	}
	public void setTableName(String tableName) {
		this.tableName = tableName;
	}
	
	@Override
	@XmlElement(name ="db-query-timeout-in-ms", type = long.class, defaultValue = "" + UserStatisticPostAuthPluginConfiguration.QUERY_TIMEOUT_IN_MS)
	public long getDbQueryTimeoutInMs() {
		return dbQueryTimeoutInMs;
	}
	public void setDbQueryTimeoutInMs(long dbQueryTimeoutInMs) {
		this.dbQueryTimeoutInMs = dbQueryTimeoutInMs;
	}

	
	@Override
	@XmlElement(name ="max-query-timeout-count", type = long.class, defaultValue = "" + UserStatisticPostAuthPluginConfiguration.MAX_QUERY_TIMEOUT_COUNT)
	public long getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	public void setMaxQueryTimeoutCount(long maxQueryTimeoutCount) {
		this.maxQueryTimeoutCount = maxQueryTimeoutCount;
	}
	
	@Override
	@XmlElement(name ="batch-update-interval-in-ms",type = long.class,defaultValue = "" + UserStatisticPostAuthPluginConfiguration.BATCH_UPDATE_INTERVAL_MS)
	public long getBatchUpdateIntervalInMs() {
		return batchUpdateIntervalInMs;
	}
	public void setBatchUpdateIntervalInMs(long batchUpdateIntervalInMs) {
		this.batchUpdateIntervalInMs = batchUpdateIntervalInMs;
	}
	
	@Override
	@XmlTransient
	public List<Map<String, Object>> getDbFieldMapping() {
		return this.dbFieldMapping;
	}
	
	public void postRead() {
		pluginInfo = new PluginInfo();
		pluginInfo.setPluginName(name);
		parseAttributeMapping();
	}
	
	@XmlTransient
	public PluginInfo getPluginInfo() {
		return pluginInfo;
	}
	
	public void parseAttributeMapping() {

		ArrayList<Map<String, Object>> dbFieldMappingList = new ArrayList<Map<String, Object>>();
		List<AttributeDetail> attributeList =this.getAttributeList();
		if(attributeList != null && attributeList.size()>0){
			int noOfAttributes = attributeList.size();
			AttributeDetail attributeDetail;
			for(int i=0;i<noOfAttributes;i++){
				HashMap<String, Object> tempMap = new HashMap<String, Object>();
				attributeDetail =  attributeList.get(i);
				if(attributeDetail.getAttributeId()!=null && attributeDetail.getAttributeId().trim().length()>0){
					String strVendorAttributeId = attributeDetail.getAttributeId().trim();
					List<String> attributeIds = new ArrayList<String>();
					StringTokenizer tokenizer = new StringTokenizer(strVendorAttributeId, ",;");
					while (tokenizer.hasMoreElements()) {
						attributeIds.add(tokenizer.nextToken().trim());
					}

					tempMap.put(ATTRIBUTE_IDS,attributeIds);
				}
				if(attributeDetail.getPacketType() !=null)
					tempMap.put(PACKET_TYPE,attributeDetail.getPacketType());

				if(attributeDetail.getDbField() !=null)
					tempMap.put(DB_FIELD,attributeDetail.getDbField());
				if(attributeDetail.getDataType() !=null)
					tempMap.put(DATA_TYPE,attributeDetail.getDataType());
				if(attributeDetail.getDefaultValue() !=null)
					tempMap.put(DEFAULT_VALUE,attributeDetail.getDefaultValue());
				if(attributeDetail.getUseDictionaryValue() !=null)
					tempMap.put(USE_DICTIONARY_VALUE,attributeDetail.getUseDictionaryValue());

				dbFieldMappingList.add(tempMap);
			}
		}
		this.dbFieldMapping = dbFieldMappingList;
	}
	
	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println();
		out.println("---------User Statistic Auth Plugin Configuration--------");
		out.println();
		out.println(format("%-30s: %s", "Data Source Name", dataSourceName));
		out.println(format("%-30s: %s", "Table Name", tableName));
		out.println(format("%-30s: %s", "DB Query TimeOut(ms)", dbQueryTimeoutInMs));
		out.println(format("%-30s: %s", "Maximum Query TimeOut", maxQueryTimeoutCount));
		out.println(format("%-30s: %s", "Batch Update Interval(ms)", batchUpdateIntervalInMs));
		out.println();
		out.println("----DB Field Mappings----");
		out.println(format("%-20s %-20s %-23s %-20s %-25s %-20s", "| Attribute-Id","| Packet Type","| DB Field Name","| Data Type","| Default Value","| Use Dictionary Value"));
		out.println();
		for (AttributeDetail attributeDetail : this.getAttributeList()) {
			out.println(attributeDetail);
		}
		out.close();
		return stringBuffer.toString();
	}


@XmlType(propOrder={})
public static class AttributeDetail  implements Differentiable{

	private String attributeId;
	private String packetType;
	private String dbField;
	private String dataType;
	private String defaultValue;
	private String useDictionaryValue;

	public AttributeDetail() {
		//required By Jaxb.
	}

	@XmlElement(name ="attribute-id",type = String.class)
	public String getAttributeId() {
		return attributeId;
	}

	public void setAttributeId(String attributeId) {
		this.attributeId = attributeId;
	}
	@XmlElement(name ="packet-type",type = String.class)
	public String getPacketType() {
		return packetType;
	}

	public void setPacketType(String packetType) {
		this.packetType = packetType;
	}

	@XmlElement(name ="db-field",type = String.class)
	public String getDbField() {
		return dbField;
	}

	public void setDbField(String dbField) {
		this.dbField = dbField;
	}

	@XmlElement(name ="data-type",type = String.class)
	public String getDataType() {
		return dataType;
	}

	public void setDataType(String dataType) {
		this.dataType = dataType;
	}

	@XmlElement(name ="default-value",type = String.class)
	public String getDefaultValue() {
		return defaultValue;
	}

	public void setDefaultValue(String defaultValue) {
		this.defaultValue = defaultValue;
	}
	
	@XmlElement(name ="use-dictionary-value",type = String.class)
	public String getUseDictionaryValue() {
		return useDictionaryValue;
	}

	public void setUseDictionaryValue(String useDictionaryValue) {
		this.useDictionaryValue = useDictionaryValue;
	}

	@Override
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.print(format("%-20s %-20s %-23s %-20s %-25s %-20s", "| " + attributeId, "| " + packetType, "| " + dbField, "| " + dataType, defaultValue != null ? "| " +defaultValue : "| ", "| " + useDictionaryValue));
		out.println();
		out.close();

		return stringBuffer.toString();
	}
	public JSONObject toJson() {
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("PacketType", packetType);
		jsonObject.put("DBField", dbField);
		jsonObject.put("DataType", dataType);
		jsonObject.put("DefaultValue", defaultValue);
		jsonObject.put("UseDictionaryValue", useDictionaryValue);
		
		return jsonObject;
	}
}
public JSONObject toJson() {
	JSONObject object = new JSONObject();
	
	object.put("Name", name);
	object.put("Description", description);
	object.put("Status", status);
	object.put("DatasourceName", dataSourceName);
	object.put("TableName", tableName);
	object.put("DbqueryTimeout", dbQueryTimeoutInMs);
	object.put("MaxqueryTimeoutCount", maxQueryTimeoutCount);
	object.put("BatchUpdateInterval", batchUpdateIntervalInMs);
	
	if (attributeList != null) {
		for (AttributeDetail element : attributeList) {
			object.put("Attribute Id ["+element.getAttributeId()+"]", element.toJson());
		}
	}
	return object;
}
}