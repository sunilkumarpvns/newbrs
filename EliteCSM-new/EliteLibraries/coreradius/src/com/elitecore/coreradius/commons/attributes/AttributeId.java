package com.elitecore.coreradius.commons.attributes;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.elitecore.coreradius.commons.util.constants.PaddingType;

public class AttributeId{
	private long lVendorId;
	private String strVendorName;
	private int[] attributeId;
	private String attributeName;
	private String attributeDataType;
	private boolean bHasTag;
	private Map<Long,String> KeyValueMap;
	private Map<String,Long> ValueKeyMap;
	private int hash;
	
	private boolean isTLVLengthFormat;
	private PaddingType paddingType;
	private boolean bIgnoreCase;
	private boolean bAvpair;
	private int encryptStandard;

	public AttributeId(long lVendorId,int[] attributeId){
		this.lVendorId = lVendorId;
		this.attributeId = attributeId;
		encryptStandard = 0;
		bHasTag = false;
		isTLVLengthFormat = true;
		bIgnoreCase = false;
		bAvpair = false;
		paddingType = PaddingType.NONE;
	}
	
	public AttributeId(long lVendorId, String strVendorName,int attributeId,String attributeName, String attributeDataType,
			boolean bHasTag,Map<Long, String> KeyValueMap , boolean isTLVLengthFormat , PaddingType paddingType ,
			boolean bIgnoreCase , boolean bAvpair ,int iEncryptStandard){
		this( lVendorId, strVendorName, new int[]{attributeId},attributeName, attributeDataType,
				 bHasTag, KeyValueMap ,  isTLVLengthFormat , paddingType , bIgnoreCase , bAvpair , iEncryptStandard);
	}
	
	public AttributeId(long lVendorId, String strVendorName,int[] attributeId,String attributeName, String attributeDataType,
			boolean bHasTag,Map<Long, String> KeyValueMap , boolean isTLVLengthFormat , PaddingType paddingType, boolean bIgnoreCase ,
			boolean bAvpair , int iEncryptStandard){
		
		this.lVendorId = lVendorId;
		this.strVendorName = strVendorName;
		this.attributeId = attributeId;
		this.attributeName = attributeName;
		this.isTLVLengthFormat = isTLVLengthFormat;
		if(paddingType != null){
			this.paddingType = paddingType;
		}else{
			this.paddingType = PaddingType.NONE;
		}
		this.attributeDataType = attributeDataType;
		this.bHasTag = bHasTag;
		this.bIgnoreCase = bIgnoreCase;
		this.encryptStandard = iEncryptStandard;
		this.bAvpair = bAvpair;
		if(KeyValueMap != null){
			this.KeyValueMap = KeyValueMap;
		
			this.ValueKeyMap = new HashMap<String, Long>();
			Iterator<Long> KeyValueMapIterator = KeyValueMap.keySet().iterator();
			while(KeyValueMapIterator.hasNext()){
				Long longKey = KeyValueMapIterator.next();
				String strVal = KeyValueMap.get(longKey);
				ValueKeyMap.put(strVal, longKey);
			}
		}
	}

	public long getVendorId() {
		return lVendorId;
	}

	public String getVendorName() {
		return strVendorName;
	}
	
	public int[] getAttrbuteId() {
		return attributeId;
	}
	
	public int getAttributeLevel() {
		return attributeId.length;
	}
	
	public int getAttrId() {
		return attributeId[attributeId.length - 1];
	}
	
	public String getAttributeName() {
		return attributeName;
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
	
	public long getKeyForValue(String Val){
		if(ValueKeyMap == null)
			return -1;
		Long key = ValueKeyMap.get(Val);
		if(key == null)
			return -1;
		else
			return key.longValue();
	}
	
	public String getAttributeDataType() {
		return attributeDataType;
	}

	public boolean hasTag() {
		return bHasTag;
	}
	
	public boolean isAvpair() {
		return bAvpair;
	}
	
	public PaddingType getPaddingType() {
		return paddingType;
	}
	
	public boolean isTLVLengthFormat() {
		return isTLVLengthFormat;
	}
	
	public int getEncryptStandard() {
		return encryptStandard;
	}

	public String getStringID(){
		StringBuffer stringID = new StringBuffer(Long.toString(lVendorId));
		for(int i=0;i<attributeId.length;i++){
			stringID.append(':');
			stringID.append(Integer.toString(attributeId[i]));
		}
		return stringID.toString();
	}
	
	@Override
	public int hashCode() {
		int h = hash;
		if (h == 0) {
		    h = (int) lVendorId;
		    int val[] = attributeId;
		    int len = attributeId.length;
            for (int i = 0; i < len; i++) {
                h = 31*h + val[i];
            }
            hash = h;
        }
        return h;
    }
	@Override
	public boolean equals(Object obj) {
		try{
			AttributeId tmpAttributeId = (AttributeId)obj;
			if(lVendorId == tmpAttributeId.getVendorId() 
					&& attributeId.length == tmpAttributeId.getAttrbuteId().length){
				return Arrays.equals(attributeId, tmpAttributeId.getAttrbuteId());
			}
		}catch(Exception e){
		}
		return false;
	}
	@Override
	public String toString() {
		return getStringID();
	}
	public boolean isIgnoreCase() {
		return bIgnoreCase;
	}
}
