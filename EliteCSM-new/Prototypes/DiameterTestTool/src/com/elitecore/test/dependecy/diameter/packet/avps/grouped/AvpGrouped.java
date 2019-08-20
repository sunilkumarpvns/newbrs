package com.elitecore.test.dependecy.diameter.packet.avps.grouped;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.dependecy.diameter.DiameterDictionary;
import com.elitecore.test.dependecy.diameter.DiameterUtility;
import com.elitecore.test.dependecy.diameter.packet.avps.BaseDiameterAVP;
import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;

import net.sf.json.JSONArray;
import net.sf.json.JSONException;
import net.sf.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.nio.ByteBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class AvpGrouped extends BaseDiameterAVP{

	private ArrayList <IDiameterAVP>subAvpList = new ArrayList<IDiameterAVP>();

	private static final String MODULE = "AVP-GROUPED";

	public AvpGrouped(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption,ArrayList<AvpRule> fixedArrayList,ArrayList<AvpRule> requiredaArrayList,ArrayList<AvpRule> optionalArrayList) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	@Override
	public void setGroupedAvp(ArrayList childAttr) {
		int len = 0;
		BaseDiameterAVP baseDiameterAVP = null;
		final int listSize = childAttr.size();
		for(int i=0;i<listSize;i++) {
			baseDiameterAVP = (BaseDiameterAVP)childAttr.get(i);
			len += baseDiameterAVP.getLength() + baseDiameterAVP.getPaddingLength();
		}
		final byte valueByte[] = new byte[len];
		int mainBufferCnt = 0;
		final int listSize2 = childAttr.size();
		for (int i = 0; i < listSize2; ++i) {
			final BaseDiameterAVP attr = (BaseDiameterAVP)childAttr.get(i);
			byte[] temp = DiameterUtility.intToByteArray(attr.getAVPCode());
			for (int cnt = 0; cnt < temp.length; ++cnt) {
				valueByte[mainBufferCnt] = temp[cnt];
				++mainBufferCnt;
			}
			valueByte[mainBufferCnt] = (byte)attr.getFlag();
			temp = DiameterUtility.intToByteArray(attr.getLength(), 3);

			for (int cnt = 0; cnt < temp.length; ++cnt) {
				++mainBufferCnt;
				valueByte[mainBufferCnt] = temp[cnt];
			}
			if (attr.getVendorId() != 0){
				temp = DiameterUtility.intToByteArray(attr.getVendorId());
				for (int cnt = 0; cnt < 4; ++cnt) {
					++mainBufferCnt;
					valueByte[mainBufferCnt] = temp[cnt];

				}
			}

			temp = attr.getValueBytes();
			for (int cnt = 0; cnt < temp.length; ++cnt) {
				++mainBufferCnt;
				valueByte[mainBufferCnt] = temp[cnt];
			}
			temp = attr.getPaddingBytes();
			if (temp != null) {
				for (int cnt = 0; cnt < temp.length; ++cnt) {
					++mainBufferCnt;
					valueByte[mainBufferCnt] = temp[cnt];
				}
			}
			++mainBufferCnt;
		}
		setValueBytes(valueByte);
	}

	@Override
	public ArrayList<IDiameterAVP> getGroupedAvp() {
		if(subAvpList == null) {
			parseGroupedAttribute();
		}
		return subAvpList;
	}

	@Override
	public int readFlagOnwardsFrom(InputStream sourceStream) {
		// TODO Auto-generated method stub
		final int totalBytes = super.readFlagOnwardsFrom(sourceStream);
		parseGroupedAttribute();
		return totalBytes;
	}
	private String getLogValue(String newLineString) {
		final ArrayList <IDiameterAVP>temp = this.getGroupedAvp();
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
		final ArrayList <IDiameterAVP>temp = this.getGroupedAvp();
		String data = new String();
		IDiameterAVP diameterAvp = null;
		final int listSize = temp.size();
		for(int i=0;i<listSize;i++) {
			diameterAvp = temp.get(i);
			if(diameterAvp.isGrouped()){
				data = data + "\n\t\t\t" + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(),diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")" + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? "[V-" + diameterAvp.getVendorId() + "]" : "" ) + (diameterAvp.isProtected() ? "[P]" : "") + " = " + ((AvpGrouped)diameterAvp).getLogValue("\n\t\t\t") ;
			}else {
				data = data + "\n\t\t\t" + DiameterDictionary.getInstance().getAttributeName(diameterAvp.getVendorId(),diameterAvp.getAVPCode()) + "(" + diameterAvp.getAVPCode() + ")" + (diameterAvp.isMandatory() ? " [M]" : "") + (diameterAvp.isVendorSpecificAttribute() ? " [V-" + diameterAvp.getVendorId() + "]" : "" ) + (diameterAvp.isProtected() ? "[P]" : "") + " = " + diameterAvp.getLogValue();
			}
		}
		return data;
	}
	@Override
	public String getStringValue() {
		final JSONObject jsonObject = new JSONObject();
		for (int i=0 ; i<subAvpList.size() ; i++){
			final IDiameterAVP subAvp = subAvpList.get(i);
			jsonObject.accumulate(subAvp.getAVPId(), subAvp.getStringValue());
		}
		return jsonObject.toString();
	}

	@Override
	public boolean isGrouped() {
		return true;
	}


	//	@Override
	//	public byte[] getBytes(){
	//		//setGroupedAvp(subAvpList);
	//		return super.getBytes();
	//	}

	@Override
	public void setValueBytes(byte []valueBuffer) {
		super.setValueBytes(valueBuffer);
		parseGroupedAttribute();
	}
	@Override
	public int getLength() {

		int intAVPLength = 8;
		if(getVendorId() > 0 ) {
			intAVPLength += 4;
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
		final List<IDiameterAVP> subAttributeList = new ArrayList<IDiameterAVP>();

		if(Strings.isNullOrBlank(iAttrId)){
			return subAttributeList;
		}
		if(DiameterUtility.isGroupAvpId(iAttrId) == false){
			for(final IDiameterAVP diameterAVP: subAvpList){
				if(diameterAVP.getAVPId().equals(iAttrId)){
					subAttributeList.add(diameterAVP);
				}
			}
			return subAttributeList;
		}
		return getSubAttributeList(DiameterUtility.diaAVPIdSplitter.split(iAttrId));
	}

	private List<IDiameterAVP> getSubAttributeList(final List<String> avpIds) {
		final List<IDiameterAVP> subAttributeList = new ArrayList<IDiameterAVP>();
		if(Collectionz.isNullOrEmpty(avpIds)){
			return subAttributeList;
		}
		if(avpIds.size() == 1){
			return getSubAttributeList(avpIds.get(0));
		}
		for(final IDiameterAVP mainSubAvp: subAvpList){
			if(mainSubAvp.getAVPId().equals(avpIds.get(0))){
				if(mainSubAvp.isGrouped() == false){
					break;
				}
				final List<IDiameterAVP> childAvps = ((AvpGrouped)mainSubAvp).getSubAttributeList(avpIds.subList(1, avpIds.size()));
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
			for(final IDiameterAVP diameterAVP: subAvpList){
				if(diameterAVP.getAVPId().equals(strAvpId)){
					return diameterAVP;
				}
			}
		}
		return getSubAttribute(DiameterUtility.diaAVPIdSplitter.split(strAvpId));
	}


	private IDiameterAVP getSubAttribute(final List<String> avpIds) {

		for(final IDiameterAVP mainSubAvp: subAvpList){
			if(mainSubAvp.getAVPId().equals(avpIds.get(0))){
				if(mainSubAvp.isGrouped() == false){
					return null;
				}
				final IDiameterAVP currentAvp =  ((AvpGrouped)mainSubAvp).getSubAttribute(avpIds.subList(1, avpIds.size()));
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

	public void addSubAvp(IDiameterAVP diameterAVP){
		if(subAvpList ==null) {
			subAvpList = new ArrayList<IDiameterAVP>();
		}
		subAvpList.add(diameterAVP);
		setGroupedAvp(subAvpList);
	}

	public void addSubAvps(List<IDiameterAVP> subAVPs) {
		if(Collectionz.isNullOrEmpty(subAVPs)){
			return;
		}
		if(subAvpList ==null) {
			subAvpList = new ArrayList<IDiameterAVP>();
		}
		subAvpList.addAll(subAVPs);
		setGroupedAvp(subAvpList);
	}

	@Override
	public void setStringValue(String data) {
		try {
			final JSONObject jsonObject = JSONObject.fromObject(data);
			final Set attributeSet = jsonObject.keySet();
			final Iterator setItr = attributeSet.iterator();
			while(setItr.hasNext()){
				final String strId = (String)setItr.next();
				String strValue = "";
				final JSONArray valueArray = jsonObject.optJSONArray(strId);
				if (valueArray == null){
					strValue = jsonObject.optString(strId);
					final IDiameterAVP subAvp = DiameterDictionary.getInstance().getAttribute(strId);
					if (subAvp!=null){
						subAvp.setStringValue(strValue);
						addSubAvp(subAvp);
					}
				} else {
					// If valueArray is not null that means this grouped AVP has multiple same type of Sub AVP.
					for (int i=0 ; i< valueArray.size() ; i++){
						strValue = valueArray.getString(i);
						final IDiameterAVP subAvp = DiameterDictionary.getInstance().getAttribute(strId);
						if (subAvp!=null){
							subAvp.setStringValue(strValue);
							addSubAvp(subAvp);
						}
					}
				}
			}
		} catch (final JSONException e){
			if (LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
				LogManager.getLogger().warn(MODULE, data + " is not in json format so could not form " +getAVPId() +" grouped AVP.");
			}
		}
	}

	protected void parseGroupedAttribute(){
		int dataLength = 0;
		int iAvpCode,cnt=0,iAvpLength = 0,iAvpVendorId = 0;
		subAvpList = new ArrayList<IDiameterAVP>();
		final byte []valueBuffer = this.getValueBytes();
		final ByteBuffer bData = ByteBuffer.allocate(valueBuffer.length);
		ByteBuffer bDataforExtraWork = ByteBuffer.allocate(40);
		byte bAvpFlag;
		IDiameterAVP diameterAvp ;

		bData.put(valueBuffer);

		while(cnt != valueBuffer.length) {
			byte []temp = new byte[4];
			iAvpCode = bData.getInt(cnt);

			cnt = cnt + 4;
			bAvpFlag = bData.get(cnt);

			cnt = cnt + 1 ;
			temp[0] = 0;

			for(int cnt1=1;cnt1<4;cnt1++) {
				temp[cnt1] = bData.get(cnt);
				cnt++;
			}

			bDataforExtraWork = null;
			bDataforExtraWork = ByteBuffer.allocate(4);
			bDataforExtraWork.put(temp);

			dataLength = bDataforExtraWork.getInt(0);

			if((bAvpFlag & 0x80) == 0x80){
				iAvpVendorId = bData.getInt(cnt);
				diameterAvp = DiameterDictionary.getInstance().getAttribute(iAvpVendorId,iAvpCode);
				cnt = cnt + 4;
				iAvpLength = dataLength - 12;
			}else{
				diameterAvp = DiameterDictionary.getInstance().getAttribute(iAvpCode);
				//				cnt = cnt + 1;
				iAvpLength = dataLength - 8;
			}

			diameterAvp.setLength(iAvpLength);
			diameterAvp.setFlag(bAvpFlag);


			temp = new byte[iAvpLength];
			for(int cnt1=0;cnt1<iAvpLength;cnt1++) {
				temp[cnt1] = bData.get(cnt);
				cnt++;
			}

			if(iAvpLength%4 != 0) {
				final int remender = iAvpLength % 4;
				final int restByte = 4 - remender;
				for(int cnt2 = 0;cnt2<restByte;cnt2++) {
					cnt++;
				}
			}
			bDataforExtraWork = null;
			bDataforExtraWork = ByteBuffer.allocate(iAvpLength);
			bDataforExtraWork.put(temp);
			diameterAvp.setValueBytes(bDataforExtraWork.array());
			subAvpList.add(diameterAvp);
		}
	}


	@Override
	public Object clone() throws CloneNotSupportedException {
		final AvpGrouped clonedAVPGrouped = (AvpGrouped) super.clone();

		final ArrayList <IDiameterAVP>subAvpList = new ArrayList<IDiameterAVP>(this.subAvpList.size());

		for(final IDiameterAVP diameteAVP : this.subAvpList){
			subAvpList.add((IDiameterAVP) diameteAVP.clone());
		}

		clonedAVPGrouped.subAvpList = subAvpList;

		return clonedAVPGrouped;
	}

	@Override
	public void refreshAVPHeader() {
		if(subAvpList != null){
			for(int i=0;i<subAvpList.size();i++) {
				subAvpList.get(i).refreshAVPHeader();
			}
			setGroupedAvp(subAvpList);
		}
	}

	public static void main(String[] args) {
		final File file = new File("D:/Branches/EliteAAA/Applications/eliteaaa/dictionary/diameter");
		try {
			DiameterDictionary.getInstance().load(file);
			//			IDiameterAVP diameterAVP = Dictionary.getInstance().getAttribute(423);
			//			IDiameterAVP diameterGroupedAVP = Dictionary.getInstance().getAttribute(425);
			final IDiameterAVP diameterAVP = DiameterDictionary.getInstance().getAttribute(10415, 873);
			final IDiameterAVP maindiameterAVP = DiameterDictionary.getInstance().getAttribute(10415, 876);
			final IDiameterAVP diameterGroupedAVP = DiameterDictionary.getInstance().getAttribute(10415, 829);
			diameterGroupedAVP.setInteger(123);
			final ArrayList<IDiameterAVP> temp = new ArrayList<IDiameterAVP>();
			temp.add(diameterGroupedAVP);
			diameterAVP.setGroupedAvp(temp);
			final ArrayList<IDiameterAVP> temp1 = new ArrayList<IDiameterAVP>();
			temp1.add(diameterAVP);
			maindiameterAVP.setGroupedAvp(temp1);
			System.out.println(diameterAVP.getStringValue());
			System.out.println(maindiameterAVP.getStringValue());

		}catch (final Exception e) {
			e.printStackTrace();
			// TODO: handle exception
		}
	}

	@Override
	public boolean equals(Object obj) {
		if(obj == null) {
			return false;
		}
		try{
			final AvpGrouped avp = (AvpGrouped)obj;
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
		} catch (final ClassCastException e){
		}
		return false;
	}



}
