package com.elitecore.diameterapi.diameter.common.packet.avps.grouped;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.avps.BaseDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.DiameterDictionary;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import javax.annotation.concurrent.NotThreadSafe;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.ByteBuffer;
import java.util.*;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

@NotThreadSafe
public class AvpGrouped extends BaseDiameterAVP {
	
	private ArrayList <IDiameterAVP>subAvpList = new ArrayList<IDiameterAVP>();
	
	private static final String MODULE = "AVP-GROUPED";
	
	public AvpGrouped(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption,ArrayList<AvpRule> fixedArrayList,ArrayList<AvpRule> requiredaArrayList,ArrayList<AvpRule> optionalArrayList) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	
	@Override
	public void setGroupedAvp(ArrayList<IDiameterAVP> childAttr) {
		subAvpList = childAttr;
	}
	
	
	public ArrayList<IDiameterAVP> getGroupedAvp() {
		return subAvpList;
	}
	
	private String getLogValue(String newLineString) {
		ArrayList <IDiameterAVP>temp = this.getGroupedAvp();
		String data = new String();
		IDiameterAVP diameterAvp = null;
		final int listSize = temp.size();
		newLineString = newLineString + "\t";
		for(int i=0;i<listSize;i++) {
			diameterAvp = temp.get(i);
			if(diameterAvp.isGrouped()){
				data = data + newLineString + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(),diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")"  + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? "[V-" + diameterAvp.getVendorId() + "]" : "" ) + (diameterAvp.isProtected() ? "[P]" : "") + " = " + ((AvpGrouped)diameterAvp).getLogValue(newLineString) ;
			}else {
				data = data + newLineString + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(),diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")" + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? "[V-" + diameterAvp.getVendorId() + "]" : "" ) + (diameterAvp.isProtected() ? "[P]" : "") + " = " + diameterAvp.getLogValue();
			}
		}
		return data;
	}
	@Override
	public final String getLogValue(){
		ArrayList <IDiameterAVP>temp = this.getGroupedAvp();
		String data = new String();
		IDiameterAVP diameterAvp = null;
		final int listSize = temp.size();
		for(int i=0;i<listSize;i++) {
			diameterAvp = temp.get(i);
			if(diameterAvp.isGrouped()){
				data = data + "\n\t\t\t" + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(),diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")" + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? "[V-" + diameterAvp.getVendorId() + "]" : "" ) + (diameterAvp.isProtected() ? "[P]" : "") + " = " + ((AvpGrouped)diameterAvp).getLogValue("\n\t\t\t") ;
			}else {
				data = data + "\n\t\t\t" + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(),diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")" + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? " [V-" + diameterAvp.getVendorId() + "]" : "" ) + (diameterAvp.isProtected() ? "[P]" : "") + " = " + ((IDiameterAVP)diameterAvp).getLogValue();
			}
		}
		return data;
	}
	public String getStringValue() {
		JSONObject jsonObject = new JSONObject();
		for (int i=0 ; i<subAvpList.size() ; i++){
			IDiameterAVP subAvp = subAvpList.get(i);
			jsonObject.accumulate(subAvp.getAVPId(), subAvp.getStringValue());
		}
		return jsonObject.toString();
	}	

	@Override
	public boolean isGrouped() {
		return true;
	}

	@Override
	public void setValueBytes(byte []valueBuffer) {
		parseGroupedAttribute(valueBuffer);
	}
	@Override
	public int getLength() {
		int intAVPLength;
		if (isVendorSpecificAttribute()) {
			intAVPLength = VS_AVP_HEADER_LENGTH;
		} else {
			intAVPLength = STANDARD_AVP_HEADER_LENGTH;
		}
		if (Collectionz.isNullOrEmpty(subAvpList) == false) {
			IDiameterAVP attr;
			for (int i = 0; i < subAvpList.size(); i++) {
				attr = subAvpList.get(i);
				intAVPLength += attr.getLength();
				intAVPLength += attr.getPaddingLength();
			}
		}
		return intAVPLength;
	}
	public List<IDiameterAVP> getSubAttributeList(String iAttrId) {
		List<IDiameterAVP> subAttributeList = new ArrayList<IDiameterAVP>();
		
		if(Strings.isNullOrBlank(iAttrId)){
			return subAttributeList;
		}
		if(DiameterUtility.isGroupAvpId(iAttrId) == false){
			for(IDiameterAVP diameterAVP: subAvpList){
				if(diameterAVP.getAVPId().equals(iAttrId)){
					subAttributeList.add(diameterAVP);
				}
			}
			return subAttributeList;
		}
		return getSubAttributeList(DiameterUtility.diaAVPIdSplitter.split(iAttrId));
	}
	
	private List<IDiameterAVP> getSubAttributeList(final List<String> avpIds) {
		List<IDiameterAVP> subAttributeList = new ArrayList<IDiameterAVP>();
		if(Collectionz.isNullOrEmpty(avpIds)){
			return subAttributeList;
		}
		if(avpIds.size() == 1){
			return getSubAttributeList(avpIds.get(0));
		}
		for(IDiameterAVP mainSubAvp: subAvpList){
			if(mainSubAvp.getAVPId().equals(avpIds.get(0))){
				if(mainSubAvp.isGrouped() == false){
					break;
				}
				List<IDiameterAVP> childAvps = ((AvpGrouped)mainSubAvp).getSubAttributeList(avpIds.subList(1, avpIds.size()));
				if(Collectionz.isNullOrEmpty(childAvps) == false){
					subAttributeList.addAll(childAvps);
				}
			}
		}
		return subAttributeList;
	}
	public IDiameterAVP getSubAttribute(String strAvpId) {
		if(Strings.isNullOrBlank(strAvpId)){
			return null;
		}
		if(DiameterUtility.isGroupAvpId(strAvpId) == false){
			for(IDiameterAVP diameterAVP: subAvpList){
				if(diameterAVP.getAVPId().equals(strAvpId)){
					return diameterAVP;
				}
			}
		}
		return getSubAttribute(DiameterUtility.diaAVPIdSplitter.split(strAvpId));
	}
	
	private IDiameterAVP getSubAttribute(final List<String> avpIds) {

		for(IDiameterAVP mainSubAvp: subAvpList){
			if(mainSubAvp.getAVPId().equals(avpIds.get(0))){
				if(avpIds.size() == 1){
					return mainSubAvp;
				}
				if(mainSubAvp.isGrouped() == false){
					return null;
				}
				IDiameterAVP currentAvp =  ((AvpGrouped)mainSubAvp).getSubAttribute(avpIds.subList(1, avpIds.size()));
				if(currentAvp != null){
					return currentAvp;
				}
			}
		}
		return null;
	}
	public IDiameterAVP getSubAttribute(int vendorId,int avpCode) {
		return getSubAttribute(vendorId+":"+avpCode);
	}
	public IDiameterAVP getSubAttribute(int avpCode) {
		return getSubAttribute(0+":"+avpCode);
	}
	
	
	/**
	 * Adds an attribute to this Grouped AVP
	 * 
	 * @param diameterAVP
	 */
	public void addSubAvp(IDiameterAVP diameterAVP){
		
		if (diameterAVP == null) {
			return;
		}
		if(subAvpList ==null)
			subAvpList = new ArrayList<IDiameterAVP>();
		subAvpList.add(diameterAVP);
	}
	
	public void addSubAvps(List<IDiameterAVP> subAVPs) {
		if(Collectionz.isNullOrEmpty(subAVPs)){
			return;
		}
		if(subAvpList ==null)
			subAvpList = new ArrayList<IDiameterAVP>();
		subAvpList.addAll(subAVPs);
	}
	
	public void addSubAvp(String avpCode, String val) {
		IDiameterAVP subAvp = DiameterDictionary.getInstance().getAttribute(avpCode);
		subAvp.setStringValue(val);
		subAvpList.add(subAvp);
	}
	
	public void addSubAvp(String avpCode, long val) {
		addSubAvp(avpCode, String.valueOf(val));
	}

	public void addSubAvp(String strAvpCode, Date time) {
		IDiameterAVP avp = DiameterDictionary.getInstance().getAttribute(strAvpCode);
		if (avp != null) {
			avp.setTime(time);
			subAvpList.add(avp);
		}
	}

	
	public void setStringValue(String data) {
		try {
			JSONObject jsonObject = JSONObject.fromObject(data);
			Set<?> attributeSet = jsonObject.keySet();
			Iterator<?> setItr = attributeSet.iterator();
			while(setItr.hasNext()){
				String strId = (String)setItr.next();
				String strValue = "";
				JSONArray valueArray = jsonObject.optJSONArray(strId);
				if (valueArray == null){
					strValue = jsonObject.optString(strId);
					IDiameterAVP subAvp = DiameterDictionary.getInstance().getAttribute(strId);
					if (subAvp!=null){
						subAvp.setStringValue(strValue);
						addSubAvp(subAvp);
					}
				} else {
					// If valueArray is not null that means this grouped AVP has multiple same type of Sub AVP. 
					for (int i=0 ; i< valueArray.size() ; i++){
						strValue = valueArray.getString(i);
						IDiameterAVP subAvp = DiameterDictionary.getInstance().getAttribute(strId);
						if (subAvp!=null){
							subAvp.setStringValue(strValue);
							addSubAvp(subAvp);
						}
					}
				}
			}
		} catch (JSONException e){ 
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, data + " is not in json format so could not form " +getAVPId() +" grouped AVP.");
			ignoreTrace(e);
		}
	}
	
	@Override
	public byte[] getValueBytes() {
		int len = 0;
		IDiameterAVP baseDiameterAVP = null;
		
		for(int i = 0 ; i < subAvpList.size() ; i++) {
			baseDiameterAVP = subAvpList.get(i);
			len += baseDiameterAVP.getLength() + baseDiameterAVP.getPaddingLength();
		}
		ByteArrayOutputStream out = new ByteArrayOutputStream(len);
		
		for (int i = 0; i < subAvpList.size(); ++i) {
			try {
				subAvpList.get(i).writeTo(out);
			} catch (IOException e) { 
				ignoreTrace(e);
				throw new AssertionError(e.getMessage());
			}
		}	  
		return out.toByteArray();
	}
	
	@Override
	public void writeTo(OutputStream out) throws IOException {
	
		setLength(getLength());
		super.writeTo(out);
	}
	
	protected void parseGroupedAttribute(byte[] valueBuffer){
		int avpLength = 0;
		int iAvpCode, cnt=0, iAvpLength = 0, iAvpVendorId = 0;
		
		subAvpList = new ArrayList<IDiameterAVP>();
		ByteBuffer sourceByteBuffer = ByteBuffer.wrap(valueBuffer);
		byte bAvpFlag;
		IDiameterAVP diameterAvp ;

		while(cnt != valueBuffer.length) {
			byte []temp = new byte[0];		
			iAvpCode = sourceByteBuffer.getInt(cnt);

			cnt = cnt + 4;
			bAvpFlag = sourceByteBuffer.get(cnt++);

			avpLength = sourceByteBuffer.get(cnt++);
			avpLength = (avpLength << 8) | (sourceByteBuffer.get(cnt++) & 0xFF);
			avpLength = (avpLength << 8) | (sourceByteBuffer.get(cnt++) & 0xFF);

			if ((bAvpFlag & 0x80) == 0x80) {
				iAvpVendorId = sourceByteBuffer.getInt(cnt);
				diameterAvp = DiameterDictionary.getInstance().getAttribute(iAvpVendorId,iAvpCode);
				cnt = cnt + 4;
				iAvpLength = avpLength - VS_AVP_HEADER_LENGTH;
			} else {
				diameterAvp = DiameterDictionary.getInstance().getAttribute(iAvpCode);
				iAvpLength = avpLength - STANDARD_AVP_HEADER_LENGTH;
			}

			temp = new byte[iAvpLength];
			sourceByteBuffer.position(cnt);
			sourceByteBuffer.get(temp);
			cnt = cnt + iAvpLength;

			int restByte  =0 ;
			if (iAvpLength%4 != 0) {
				int remender = iAvpLength % 4;
				restByte = 4 - remender;
				cnt = cnt + restByte;
			}
			diameterAvp.setLength(avpLength);
			diameterAvp.setFlag(bAvpFlag);
			diameterAvp.setValueBytes(temp);
			subAvpList.add(diameterAvp);
		}
		setLength(getLength());
	}
	

	@Override
	public Object clone() throws CloneNotSupportedException {
		AvpGrouped clonedAVPGrouped = (AvpGrouped) super.clone();

		ArrayList <IDiameterAVP>subAvpList = new ArrayList<IDiameterAVP>(this.subAvpList.size());

		for(IDiameterAVP diameteAVP : this.subAvpList){
			subAvpList.add((IDiameterAVP) diameteAVP.clone());
		}

		clonedAVPGrouped.subAvpList = subAvpList;

		return clonedAVPGrouped;
	}
	
	@Override
	public void refreshAVPHeader() {
		if(subAvpList == null){
			return;
		}
		for (int i=0;i<subAvpList.size();i++) {
			subAvpList.get(i).refreshAVPHeader();
		}
		setLength(getLength());
	}
	
	@Override
	public boolean equals(Object obj) {
		if(obj == null)
			return false;
		try{
			AvpGrouped avp = (AvpGrouped)obj;
			if(getAVPCode() == avp.getAVPCode() && 
					getVendorId() == avp.getVendorId() &&
					getLength() == avp.getLength() &&
					getGroupedAvp().size() == avp.getGroupedAvp().size()) {
				
				for(int i= 0 ; i < subAvpList.size() ; i++ ){
					if(subAvpList.get(i).equals(avp.getGroupedAvp().get(i)) == false ){
						return false;
					}
				}
				return true;
			}
		} catch (ClassCastException e){ 
			ignoreTrace(e);
		}
		return false;
	}


		
}
