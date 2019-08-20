package com.elitecore.coreradius.commons.attributes;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;
import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class GroupedAttribute extends BaseRadiusAttribute implements IRadiusGroupedAttribute {
	
	private static final long serialVersionUID = -5805735201814658953L;

	private static final String MODULE = "Grouped Attribute";

	private ArrayList<IRadiusAttribute> attrList = new ArrayList<IRadiusAttribute>();
	
	public GroupedAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public GroupedAttribute() {
	}

	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException{
		int totalBytes = 0;
		length = sourceStream.read();
		totalBytes++;
		
		int bytesToRead = length - padding.length;
		
		if(isTLVLengthFormat){
			// As we already read Type Field reducing by 1
			bytesToRead--;
		}else{
			// Incrementing bytes to read as length field is not included in length
			bytesToRead++;
		}
			
		while(totalBytes < bytesToRead) {
			int intTLVTag = sourceStream.read();				
			totalBytes++;
			try{
				BaseRadiusAttribute tlvAttr = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(getParentId()+":"+intTLVTag);
				tlvAttr.setType((byte)intTLVTag);
				totalBytes += tlvAttr.readLengthOnwardsFrom(sourceStream);
				attrList.add(tlvAttr);
			}catch (Exception e) {
				LogManager.ignoreTrace(e);
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Can't find Attribute for Attribute-Id : "+getParentId()+":"+intTLVTag);
			}
			
		}
		if(padding.length > 0 ){
			sourceStream.read(padding);
			totalBytes+=padding.length;
		}
		return totalBytes;
	}
	
	public ArrayList<IRadiusAttribute> getTLVAttrList() {
		return attrList;
	}
	
	@Override
	public void addTLVAttribute(IRadiusAttribute tlvAttr) {
		attrList.add(tlvAttr);
		refreshAttributeHeader();
	}
	
	public void setStringValue(String value) {
		if(value != null){
			try{
				buildSubAttribute(JSONObject.fromObject(value));
			} catch (JSONException e){
				LogManager.ignoreTrace(e);
//				Handling exception in below manner just to provide backward compatibility for 1 level nesting.
				StringTokenizer st = new StringTokenizer(value,";");
				while(st.hasMoreTokens()){
					String strToken = st.nextToken();
					StringTokenizer st1 = new StringTokenizer(strToken,"=");
					String strAttributeId = "0";
					String strValue = "";
					if(st1.countTokens()==2){
						strAttributeId = st1.nextToken();
						strValue = st1.nextToken();
					}
					IRadiusAttribute subAttribute = Dictionary.getInstance().getKnownAttribute(getParentId()+":"+strAttributeId);
					if(subAttribute != null){
						subAttribute.setStringValue(strValue);
						addTLVAttribute((BaseRadiusAttribute)subAttribute);
					}else{
						throw new AttributeTypeNotFoundException("Unknown attribute " + getParentId()+":"+strAttributeId);
					}
				}
			}
		}
			
		//Update length
		int tmpLength = 0;
		ArrayList<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		final int size = tlvAttrList.size();			
		for(int i =0 ; i < size ; i++){
			BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
			tmpLength += attr.getBytes().length;
		}
		
		if(isTLVLengthFormat){
			length = tmpLength + 2 + padding.length; // 2 bytes for Type (1 byte) and Length (1 byte) 
		}else{
			length = tmpLength + padding.length; 	// Skip Type and Length bytes as length represent only (value and padding)
		}
	}
	
	private void buildSubAttribute(JSONObject attributeValueMapping){
		Set<?> attributeSet = attributeValueMapping.keySet();
		Iterator<?> setItr = attributeSet.iterator();
		while(setItr.hasNext()){
			String strId = (String)setItr.next();
			JSONArray valueArray = attributeValueMapping.optJSONArray(strId);
			if(valueArray != null){
				final int valueArraySize = valueArray.size();
				for(int i=0; i<valueArraySize; i++){
					IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(getParentId()+":"+strId);
					if(radiusAttribute != null){
						radiusAttribute.setStringValue(valueArray.getString(i));
						addTLVAttribute(radiusAttribute);
					}else{
						throw new AttributeTypeNotFoundException("Unknown attribute " + getParentId()+":"+strId);
					}
				}
			}else{
				IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(getParentId()+":"+strId);
				if(radiusAttribute != null){
					radiusAttribute.setStringValue(attributeValueMapping.optString(strId));
					addTLVAttribute(radiusAttribute);
				}else{
					throw new AttributeTypeNotFoundException("Unknown attribute " + getParentId()+":"+strId);
				}
			}
		}
	}

	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		//not supported but not throwing any runtime exception
	}

	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return null;
	}
	
	public byte[] getBytes() {
		ArrayList<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		byte[] totalBytes = new byte[getLength()];
		int cnt = 0;		
		totalBytes[cnt] = (byte)getType();
		cnt++;
		totalBytes[cnt] = (byte)length;
		cnt++;
		final int listSize = tlvAttrList.size();
		for(int i=0;i<listSize;i++) {			
			BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
			byte [] valueByte = attr.getBytes();			
			System.arraycopy(valueByte, 0, totalBytes, cnt, valueByte.length);
			cnt += valueByte.length;
		}
		
		if(padding.length > 0){
			System.arraycopy(padding, 0, totalBytes, totalBytes.length - padding.length, padding.length);
		}
		
		return totalBytes;
	}
	
	public int getCalculatedLength() {
		ArrayList<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		int length = 0;
		final int listSize = tlvAttrList.size();
		for(int i=0;i<listSize;i++) {
			BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
			length += attr.getLength();
		}
		return length;
	}

	@Override
	public void refreshAttributeHeader(){
		ArrayList<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		int length = 0;
		byte[] allAttributeBytes = null;
		final int listSize = tlvAttrList.size();
		for(int i=0;i<listSize;i++) {
			BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
			attr.refreshAttributeHeader();
			length = length + attr.getLength();
			allAttributeBytes = RadiusUtility.appendBytes(allAttributeBytes, attr.getBytes());
		}
		setValueBytes(allAttributeBytes);
	}

	@Override
	public String toString() {
		ArrayList<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		StringBuilder result = new StringBuilder();
		final int listSize = tlvAttrList.size();
		for(int i=0;i<listSize;i++) {
			BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
			result.append("\n");
			int iLevel = getLevel();
			result.append(indentStringArray[iLevel]);
			result.append(Dictionary.getInstance().getAttributeName(getParentId()+":"+attr.getType()));
			result.append(attr.toString());
			result.append('\t');
		}
		return result.toString();
	}
	
	public Object clone() throws CloneNotSupportedException {
		GroupedAttribute result = null;
		result = (GroupedAttribute)super.clone();
		result.attrList = new ArrayList<IRadiusAttribute>();
		final int listSize = attrList.size();
		for(int i=0;i<listSize;i++){
			BaseRadiusAttribute tlvAttr = (BaseRadiusAttribute)this.attrList.get(i).clone(); 
			result.attrList.add(tlvAttr);
		}
		return result;
	}

	@Override
	public BaseRadiusAttribute getAttribute(int id){
		ArrayList<IRadiusAttribute> attrList = getTLVAttrList();
		if(attrList != null && !attrList.isEmpty()){
			final int listSize = attrList.size();
			for(int i=0;i<listSize;i++) {
				BaseRadiusAttribute attr = (BaseRadiusAttribute)attrList.get(i);
				if(attr.getType() == id){
					return attr;
				}
			}
		}
		return null;
	}
	
	public IRadiusAttribute getSubAttribute(int ... attributeIds){
		IRadiusAttribute attr = null;
		int id1 = attributeIds[0];
		if(attributeIds.length > 1){
			int[] newAttrIds = new int[attributeIds.length-1];
			System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
			attr = getAttribute(id1);
			if(attr != null){
				attr = (getAttribute(id1)).getSubAttribute(newAttrIds);
			}
		}else{
			attr = getAttribute(id1);
		}
		return attr;
	}
	
	@Override
	public ArrayList<IRadiusAttribute> getSubAttributes(int ... attributeIds){
		ArrayList<IRadiusAttribute> retAttrList = null;
		int id1 = attributeIds[0];
		if(attributeIds.length > 1){
			int[] newAttrIds = new int[attributeIds.length-1];
			System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
			ArrayList<IRadiusAttribute> mainAttrList = getAttributes(id1);
			if(mainAttrList != null){
				final int listSize = mainAttrList.size();
				for(int i=0;i<listSize;i++) {
					ArrayList<IRadiusAttribute> subAttrList = ((BaseRadiusAttribute)mainAttrList.get(i)).getSubAttributes(newAttrIds);
					if(subAttrList != null){
						if(retAttrList == null){
							retAttrList = new ArrayList<IRadiusAttribute>();
						}
						retAttrList.addAll(subAttrList);
					}
				}
			}
		}else{
			retAttrList = getAttributes(id1);
		}
		return retAttrList;
	}
	
	public ArrayList<IRadiusAttribute> getAttributes(int attributeID) {
		ArrayList<IRadiusAttribute> attributeList = new ArrayList<IRadiusAttribute>();
		for(int i=0; i< attrList.size() ;i++){
			if(attributeID == ((BaseRadiusAttribute)attrList.get(i)).getType()){
				attributeList.add(attrList.get(i));
			}
		}
		if(!attributeList.isEmpty())
			return attributeList;
		return null;
	}
	
	public ArrayList<IRadiusAttribute> getAttributes(String attributeID) {
		return getAttributes(Integer.parseInt(attributeID));
	}
	
	public ArrayList<IRadiusAttribute> getAttributes() {
		return attrList;
	}
	
	@Override
	public void addSubAttributes(IRadiusAttribute... subAttributes) {
		if(subAttributes == null){
			return;
		}
		for(int i = 0 ; i < subAttributes.length ; i++){
			addTLVAttribute(subAttributes[i]);
		}
	}

	public String getStringValue(){
		ArrayList<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		StringBuilder result = new StringBuilder();
		if(tlvAttrList != null && !tlvAttrList.isEmpty()){
	
			result.append("{");
			BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(0);

			result.append(attr.getType()+"="+attr.getStringValue());
			
			final int size = tlvAttrList.size();
			for(int i = 1 ; i < size ; i++){
				attr = (BaseRadiusAttribute)tlvAttrList.get(i);
				result.append(";"+attr.getType()+"="+attr.getStringValue());
				
			}
			result.append("}");
		}
		return result.toString();
	}

	
	public void reencryptValue(String oldSecret, byte[] oldAuthenticator, String newSecret, byte[] newAuthenticator){
		final int listSize = attrList.size();
		for(int i=0;i<listSize;i++) {
			IRadiusAttribute attr = attrList.get(i);
			attr.reencryptValue(oldSecret, oldAuthenticator, newSecret, newAuthenticator);
		}
	}

	public void addsubAttribute(String id, String value) {
		IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(id);
		if (radiusAttribute == null) {
			return;
		} else {
			radiusAttribute.setStringValue(value);
		}
		addTLVAttribute(radiusAttribute);
	}

	public void addsubAttribute(String id, long value) {
		IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(id);
		if (radiusAttribute == null) {
			return;
		}

		radiusAttribute.setLongValue(value);
		addTLVAttribute(radiusAttribute);
	}

	public void addsubAttribute(String id, int value) {
		IRadiusAttribute radiusAttribute = Dictionary.getInstance().getKnownAttribute(id);
		if (radiusAttribute == null) {
			return;
		} else {
			radiusAttribute.setIntValue(value);
		}
		addTLVAttribute(radiusAttribute);
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)  {
			return true;
		}
		if ((o instanceof GroupedAttribute) == false) {
			return false;
		}

		return super.equals(o);
	}

	@Override
	public int hashCode() {
		return super.hashCode();
	}
}
