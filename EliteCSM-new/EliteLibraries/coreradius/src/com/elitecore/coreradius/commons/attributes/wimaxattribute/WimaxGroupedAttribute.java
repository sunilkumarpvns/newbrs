package com.elitecore.coreradius.commons.attributes.wimaxattribute;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.AttributeTypeNotFoundException;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.BaseRadiusWiMAXAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusGroupedAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

public class WimaxGroupedAttribute extends BaseRadiusWiMAXAttribute implements IRadiusGroupedAttribute {

	private static final long serialVersionUID = -497389412086194679L;

	private final static String MODULE = "WiMAX Grouped Attribute";

	private ArrayList<IRadiusAttribute> attrList = new ArrayList<IRadiusAttribute>();

	public WimaxGroupedAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public WimaxGroupedAttribute() {
	}

	public int readFrom(InputStream sourceStream) throws IOException{
		int totalBytes = 0;
		setType(sourceStream.read());
		totalBytes++;
		length = sourceStream.read();
		totalBytes++;
		setContinuationByte(sourceStream.read());
		totalBytes++;
		while(totalBytes < getLength()-1) {
			int intTLVTag = sourceStream.read();				
			totalBytes++;
			try{
				BaseRadiusAttribute tlvAttr = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(getParentId()+":"+intTLVTag);
				tlvAttr.setType((byte)intTLVTag);				
				totalBytes += tlvAttr.readLengthOnwardsFrom(sourceStream);				
				attrList.add(tlvAttr);
			}catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Can't find Attribute for Attribute-Id : "+getParentId()+":"+intTLVTag);
			}
		}
		return totalBytes;
	}

	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException{
		int totalBytes = 0;
		length = sourceStream.read();
		totalBytes++;
		setContinuationByte(sourceStream.read());
		totalBytes++;
		while(totalBytes < getLength()-1) {
			int intTLVTag = sourceStream.read();				
			totalBytes++;
			try{
				BaseRadiusAttribute tlvAttr = (BaseRadiusAttribute)Dictionary.getInstance().getAttribute(getParentId()+":"+intTLVTag);
				tlvAttr.setType((byte)intTLVTag);				
				totalBytes += tlvAttr.readLengthOnwardsFrom(sourceStream);				
				attrList.add(tlvAttr);
			}catch (Exception e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Can't find Attribute for Attribute-Id : "+getParentId()+":"+intTLVTag);
			}
		}
		return totalBytes;
	}

	public List<IRadiusAttribute> getTLVAttrList() {
		return attrList;
	}

	@Override
	public void addTLVAttribute(IRadiusAttribute tlvAttr) {
		attrList.add(tlvAttr);
		//refreshAttributeHeader();
	}

	public void setStringValue(String value) {
		if(value != null){
			try{
				buildSubAttribute(JSONObject.fromObject(value));
			} catch (JSONException e){
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
		length = getBytes().length;
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
					IRadiusAttribute radiusAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getKnownAttribute(getParentId()+":"+strId);
					if(radiusAttribute != null){
						radiusAttribute.setStringValue(valueArray.getString(i));
						addTLVAttribute(radiusAttribute);
					}else{
						throw new AttributeTypeNotFoundException("Unknown attribute " + getParentId()+":"+strId);
					}
				}
			}else{
				IRadiusAttribute radiusAttribute = (BaseRadiusAttribute)Dictionary.getInstance().getKnownAttribute(getParentId()+":"+strId);
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
		// TODO Auto-generated method stub

	}

	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		// TODO Auto-generated method stub
		return null;
	}

	public byte[] getBytes() {
		List<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		int totalLength = 0;
		int valueLength = 0;
		if(tlvAttrList != null && !tlvAttrList.isEmpty()){
			final int size = tlvAttrList.size();
			int tmp=0;
			for(int i = 0 ; i < size ; i++){
				BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
				totalLength += attr.getBytes().length;
				valueLength += attr.getBytes().length;
				tmp +=attr.getBytes().length;
				if(tmp > 246){
					totalLength += 9;
					tmp -= 246;
				}
			}
			//Handle contination bit
			byte[] finalBytes = new byte[totalLength];
			tmp=0;
			int pos=0;
			for(int i = 0 ; i < size ; i++){
				BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
				byte[] currentAttrBytes = attr.getBytes();
				tmp += currentAttrBytes.length;
				if(tmp > 246){
					System.arraycopy(currentAttrBytes, 0, finalBytes, pos, currentAttrBytes.length - (tmp - 246));
					pos = pos + currentAttrBytes.length - (tmp - 246);
					valueLength -= currentAttrBytes.length - (tmp - 246);
					//Add bytes of Vendor Specific Attribute(26)
					if(valueLength > 246){
						byte[] vendorSpecific = {26,(byte)255,0,0,96,(byte)(181)};
						System.arraycopy(vendorSpecific,0,finalBytes,pos ,vendorSpecific.length);
						pos = pos + 6;
						byte [] wimaxGrouped = {(byte)getType(),(byte)249,1};
						System.arraycopy(wimaxGrouped,0,finalBytes, pos,wimaxGrouped.length);
						pos = pos + 3;
					}else{							
						byte[] vendorSpecific = {26,(byte)(valueLength + 9),0,0,96,(byte)(181)};							
						System.arraycopy(vendorSpecific,0,finalBytes,pos ,vendorSpecific.length);
						pos = pos + 6;
						byte [] wimaxGrouped = {(byte)getType(),(byte)(valueLength + 3),0};
						System.arraycopy(wimaxGrouped,0,finalBytes, pos,wimaxGrouped.length);
						pos = pos + 3;

					}

					System.arraycopy(currentAttrBytes,currentAttrBytes.length-(tmp-246), finalBytes, pos,tmp-246);
					pos = pos + tmp-246;
					valueLength = valueLength - (tmp-246);
					tmp -= 246;
				}else{
					System.arraycopy(currentAttrBytes, 0, finalBytes, pos, currentAttrBytes.length);
					pos += currentAttrBytes.length;
					valueLength -= currentAttrBytes.length;
				}
			}
			byte[] attrBytes = new byte[finalBytes.length + 3];
			attrBytes[0] = (byte)getType();
			attrBytes[1] = (byte)((totalLength>246)?249:finalBytes.length+3);
			attrBytes[2] = (byte)((totalLength > 246)?1:0);
			System.arraycopy(finalBytes, 0, attrBytes, 3, finalBytes.length);
			return attrBytes;
		}
		byte[] defaultBytes = {(byte)getType(),3,0};
		return defaultBytes;
	}

	public int getCalculatedLength() {
		List<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		int length = 0;
		if(tlvAttrList != null && !tlvAttrList.isEmpty()){
			final int size = tlvAttrList.size();
			for(int i = 0 ; i < size ; i++){
				BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
				length += attr.getLength();
			}
		}
		return length;
	}

	public void refreshAttributeHeader(){
		//		List tlvAttrList = getTLVAttrList();
		//		int length = 0;
		//		byte[] allAttributeBytes = null;
		//		if(tlvAttrList != null && !tlvAttrList.isEmpty()){
		//			final int size = tlvAttrList.size();
		//			for(int i = 0 ; i < size ; i++){
		//				BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
		//				attr.refreshAttributeHeader();
		//				length = length + attr.getLength();
		//				allAttributeBytes = RadiusUtility.appendBytes(allAttributeBytes, attr.getBytes());
		//			}
		//		}
		byte[] valueBytes = getBytes();
		setValueBytes(valueBytes);
		length = valueBytes.length;
	}

	public String toString() {
		List<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		StringBuffer result = new StringBuffer();
		if(tlvAttrList != null && !tlvAttrList.isEmpty()){
			final int size = tlvAttrList.size();
			for(int i = 0 ; i < size ; i++){
				BaseRadiusAttribute attr = (BaseRadiusAttribute)tlvAttrList.get(i);
				result.append("\n");
				int iLevel = getLevel();
				result.append(indentStringArray[iLevel]);
				result.append(Dictionary.getInstance().getAttributeName(getParentId()+":"+attr.getType()));
				result.append(attr.toString());
				result.append('\t');
			}
		}
		return result.toString();
	}

	public Object clone() throws CloneNotSupportedException {
		WimaxGroupedAttribute result = null;
		result = (WimaxGroupedAttribute)super.clone();
		result.attrList = new ArrayList<IRadiusAttribute>();
		for(int i=0;i<attrList.size();i++){
			BaseRadiusAttribute tlvAttr = (BaseRadiusAttribute)this.attrList.get(i).clone(); 
			result.attrList.add(tlvAttr);
		}
		return result;
	}

	public BaseRadiusAttribute getAttribute(int id){
		List<IRadiusAttribute> attrList = getTLVAttrList();
		if(attrList != null && !attrList.isEmpty()){
			final int size = attrList.size();
			for(int i = 0 ; i < size ; i++){
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
			int newAttrIds[] = new int[attributeIds.length-1];
			System.arraycopy(attributeIds, 1, newAttrIds, 0, newAttrIds.length);
			attr = getAttribute(id1);
			if(attr != null){
				attr = ((BaseRadiusAttribute)getAttribute(id1)).getSubAttribute(newAttrIds);
			}
		}else{
			attr = getAttribute(id1);
		}
		return attr;
	}

	public ArrayList<IRadiusAttribute> getSubAttributes(int ... attributeIds){
		ArrayList<IRadiusAttribute> retAttrList = null;
		int id1 = attributeIds[0];
		if(attributeIds.length > 1){
			int newAttrIds[] = new int[attributeIds.length-1];
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
		ArrayList<IRadiusAttribute> attributeList = new ArrayList<IRadiusAttribute>(1);
		for(int i=0; i < attrList.size(); i++){
			if(attributeID == ((BaseRadiusAttribute)attrList.get(i)).getType()){
				attributeList.add((BaseRadiusAttribute) attrList.get(i));
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

	
	public String getStringValue(){
		List<IRadiusAttribute> tlvAttrList = getTLVAttrList();
		StringBuffer result = new StringBuffer();
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
			IRadiusAttribute radiusAttribute = attrList.get(i);
			radiusAttribute.reencryptValue(oldSecret, oldAuthenticator, newSecret, newAuthenticator);
		}
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
	
}
