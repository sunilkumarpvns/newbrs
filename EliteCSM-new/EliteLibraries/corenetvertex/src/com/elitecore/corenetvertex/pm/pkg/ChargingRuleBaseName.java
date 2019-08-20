package com.elitecore.corenetvertex.pm.pkg;

import com.elitecore.commons.base.Maps;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import org.apache.commons.lang.SystemUtils;

import java.io.Serializable;
import java.util.Map;

public class ChargingRuleBaseName implements Serializable{

	private static final long serialVersionUID = 1L;
	private static final ToStringStyle CHARGING_RULE_BASE_NAME_DATA_TO_STRING_STYLE = new ChargingRuleBaseNameToString();
	private String id;
	private String name;
	private Map<String,DataServiceType> monitoringKeyServiceTypeMap;
	private int fupLevel = 0;
	private Map<String,SliceInformation> monitoringKeySliceInformationMap;

	public ChargingRuleBaseName(String id,
								String name,
								Map<String,DataServiceType> monitoringKeyServiceTypeMap,
								int fupLevel ,
								Map<String,SliceInformation> monitoringKeySliceInformationMap) {

		this.id = id;
		this.name = name;
		this.monitoringKeyServiceTypeMap = monitoringKeyServiceTypeMap;
		this.fupLevel = fupLevel;
		this.monitoringKeySliceInformationMap = monitoringKeySliceInformationMap;
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public Map<String,DataServiceType> getMonitoringKeyServiceTypeMap() {
		return monitoringKeyServiceTypeMap;
	}

	public int getFupLevel() {
		return fupLevel;
	}

	public UsageMetering getUsageMetering(String monitoringKey) {

		if(monitoringKeySliceInformationMap.keySet().contains(monitoringKey)){
			return UsageMetering.TIME_VOLUME_QUOTA;
		}
		return UsageMetering.DISABLE_QUOTA;
	}

	public Map<String, SliceInformation> getMonitoringKeySliceInformationMap() {
		return monitoringKeySliceInformationMap;
	}


	@Override
	public String toString() {
		return toString(CHARGING_RULE_BASE_NAME_DATA_TO_STRING_STYLE);
	}

	public String toString(ToStringStyle toStringStyle) {

		ToStringBuilder stringBuilder = new ToStringBuilder(this, toStringStyle).append("Name", name);
		stringBuilder.append("MonitoringKey : DataServiceType");
		for(Map.Entry<String,DataServiceType>  entry : monitoringKeyServiceTypeMap.entrySet()){
			String monitoringKey = entry.getKey();
			DataServiceType dataServiceType = entry.getValue();
			stringBuilder.append(monitoringKey, dataServiceType.getName());
		}

		if(Maps.isNullOrEmpty(monitoringKeyServiceTypeMap) == false) {
			stringBuilder.append("MonitoringKey : SliceInformation");
			for(Map.Entry<String,SliceInformation>  entry : monitoringKeySliceInformationMap.entrySet()){
				String monitoringKey = entry.getKey();
				SliceInformation sliceInformation = entry.getValue();

				StringBuilder sliceInfo = new StringBuilder();
				sliceInfo.append("Total: ").append(sliceInformation.getSliceTotal()).append(", ")
						.append("Upload: ").append(sliceInformation.getSliceUpload()).append(", ")
						.append("Download: ").append(sliceInformation.getSliceDownload()).append(", ")
						.append("Time: ").append(sliceInformation.getSliceTime());

				stringBuilder.append(monitoringKey, sliceInfo.toString());
			}
		}


		return stringBuilder.toString();
	}

	public void printToQosSelectionSummary(IndentingWriter printWriter) {

		printWriter.append(getName() + "[Service: ");
		for(DataServiceType dataServiceType : getMonitoringKeyServiceTypeMap().values()){
			printWriter.append(dataServiceType.getName()).append(", ");
		}
		printWriter.append("], ");
	}

	static class ChargingRuleBaseNameToString extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		ChargingRuleBaseNameToString() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setContentEnd("");
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(8) + getTabs(3));
		}
	}

	
}
