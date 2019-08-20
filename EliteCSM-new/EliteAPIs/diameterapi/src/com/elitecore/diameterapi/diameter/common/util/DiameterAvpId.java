package com.elitecore.diameterapi.diameter.common.util;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpRule;

public class DiameterAvpId {

	private int vendorId;
	private String vendorName;
	private int attributeId;
	private String attributeName;
	// Represents attribute id in the form of vendor-id : avpcode
	private String strAvpId="";
	private String isMandatory;
	private String isProtected;
	private String isEncryption;
	private String attributeDataType;
	private Map<Integer,String> KeyValueMap;
	private Map<String,Integer> ValueKeyMap;
	private List<AvpRule> requiredAVPRuleList;
	private List<AvpRule> optionalAVPRuleList;
	private List<AvpRule> fixedAVPRuleList;
	
	private int hash;
	private boolean isGrouped;
	private String xmlFileName;

	@Override
	public String toString(){
		StringWriter stringWriter = new StringWriter(); 
		PrintWriter out = new PrintWriter ( stringWriter);
		out.println("Vendor: " + vendorId + ":" + vendorName);
		out.println("Attribute           :"+attributeId);
		out.println("AttributeName       :"+attributeName);
		out.println("isMandatory         :"+isMandatory);
		out.println("isProtected         :"+isProtected);
		out.println("isEncryption        :"+isEncryption);
		out.println("AttributeDataType   :"+attributeDataType);
		out.println("xmlFileName         :"+xmlFileName);
		
		if (KeyValueMap!=null)
		out.println("KVMAP" +KeyValueMap.toString());
		if (isGrouped){
			out.println("REQAVP" + requiredAVPRuleList.toString());
			out.println("OPTAVP" + optionalAVPRuleList.toString());
			out.println("FIXDAVP" + fixedAVPRuleList.toString());
		}
		return stringWriter.toString();
	}

	public DiameterAvpId(int vendorId, String strVendorName, int attributeId, String attributeName, String isMandatory, String isProtected,
			String isEncryption, String attributeDataType, Map<Integer, String> keyValueMap, List<AvpRule> requiredAVPRuleList,
			List<AvpRule> optionalAVPRuleList, List<AvpRule> fixedAVPRuleList, boolean isGrouped,String xmlFileName) {
		this.vendorId = vendorId;
		this.vendorName = strVendorName;
		this.attributeId = attributeId;
		this.strAvpId = vendorId+":"+attributeId;
		this.attributeName = attributeName;
		this.isMandatory = isMandatory;
		this.isProtected = isProtected;
		this.isEncryption = isEncryption;
		this.attributeDataType = attributeDataType;
		this.xmlFileName = xmlFileName;
		if (keyValueMap != null){
			this.KeyValueMap = keyValueMap;
			this.ValueKeyMap = new HashMap<String, Integer>();
			Iterator<Integer> KeyValueMapIterator = KeyValueMap.keySet().iterator();
			while(KeyValueMapIterator.hasNext()){
				Integer key = KeyValueMapIterator.next();
				String strVal = KeyValueMap.get(key);
				ValueKeyMap.put(strVal, key);
			}
		}
		if (isGrouped){
			this.isGrouped = true;
			this.requiredAVPRuleList = requiredAVPRuleList;
			this.optionalAVPRuleList = optionalAVPRuleList;	
			this.fixedAVPRuleList = fixedAVPRuleList;
		}
		else{
			this.isGrouped = false;
		}
	}

	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0) {
			h = (int) vendorId;
			int val = attributeId;
			h = 31*h + val;
			hash = h;
		}
		return h;
	}
	@Override
	public boolean equals(Object obj) {
		try{
			DiameterAvpId tmpAttributeId = (DiameterAvpId)obj;
			if(vendorId == tmpAttributeId.getVendorId() && attributeId == tmpAttributeId.getAttrbuteId()){
				return true;
			}
		}catch(Exception e){
		}
		return false;
	}

	public int getAttrbuteId() {
		return attributeId;
	}

	public long getVendorId() {
		return vendorId;
	}

	public String getAttributeDataType(){
		return attributeDataType;
	}

	public String getAttributeName(){
		return attributeName;
	}

	public String getVendorName(){
		return vendorName;
	}

	public long getKeyForValue(String val){
		if (ValueKeyMap  == null )
			return -1;
		Integer key = ValueKeyMap.get(val);
		if (key == null)
			return -1;
		return key.longValue();

	}

	public String getValueForKey(long id){
		if(KeyValueMap == null)
			return String.valueOf(id);
		String value = KeyValueMap.get(id);
		if(value == null){
			value = String.valueOf(id);
		}
		return value;
	}

	public String isMandatory() {
		return isMandatory;
	}

	public String isProtected() {
		return isProtected;
	}

	public String isEncryption() {
		return isEncryption;
	}

	public List<AvpRule> getRequiredAVPRuleList() {
		return requiredAVPRuleList;
	}

	public List<AvpRule> getOptionalAVPRuleList() {
		return optionalAVPRuleList;
	}
	
	public List<AvpRule> getFixedAVPRuleList() {
		return fixedAVPRuleList;
	}
	
	public String getFileName() {
		return this.xmlFileName;
	}


	public boolean isGrouped() {
		return isGrouped;
	}
	public Map<Integer,String> getSuppotedValueMap() {
		return KeyValueMap;
	}
	
	/**
	 * Returns attribute id in the form of "Vendor-id : AvpCode"
	 */
	public String getAVPId(){
		return this.strAvpId;
	}
}

