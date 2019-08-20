package com.elitecore.test.dependecy.diameter.packet;

import com.elitecore.test.dependecy.diameter.packet.avps.IDiameterAVP;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * @author pulindani
 * @version 1.0
 * @created 17-Jul-2008 3:16:11 PM
 */
public interface IDiameterPacket {
	
	public int getCommandCode();
	public void setCommandCode(int type);
	
	public void setVersion(int version);
	public int getVersion();
	public long getApplicationID();
	public void setApplicationID(long applicationId);
	
	public int getCommandFlag();

	public void setRequestBit();
	public void setProxiableBit();
	public void setErrorBit();
	public void setReTransmittedBit();
	
	public void resetRequestBit();
	public void resetProxiableBit();
	public void resetErrorBit();
	public void resetReTransmittedBit();
	
	public int getLength();
	public int getInfoLength();
	public void setLength(int length);
	
	public int getHop_by_hopIdentifier();
	//public void setHop_by_hopIdentifier(int hop_by_hopIdentifier);
	
	public int getEnd_to_endIdentifier();
//	public void setEnd_to_endIdentifier(int end_to_endIdentifier);
	
	public Map getAVPMap();
	public ArrayList<IDiameterAVP> getAVPList();
	public ArrayList getPacketAVPCodeList();

	public IDiameterAVP getAVP(String strAvpName);
	public IDiameterAVP getInfoAVP(String strAvpName);
	public List<IDiameterAVP> getInfoAVPList(String strAVPCode);
	public IDiameterAVP getAVP(String strAvpId, boolean bIncludeInfoAttr);
	
	public boolean isRequest();
	public boolean isProxiable();
	public boolean isError();
	public boolean isReTransmitted();
	
	public void setResponsePacketHeader(IDiameterPacket requestPacket);
	
	public void addAvp(IDiameterAVP diameterAvp);
	public void addInfoAvp(IDiameterAVP diameterAvp);
	public List<IDiameterAVP> getVendorSpeficAvps(long vendorID, int avpCode);
	
	public byte[] getBytes();
	public byte[] getBytes(boolean bIncludeInfoAttr);
	public void setBytes(byte[] bData);
	public void refreshPacketHeader();
	public void refreshInfoPacketHeader();
	public ArrayList<IDiameterAVP> getAVPList(String strAVPCode) ;
	String getInfoAVPValue(String strAvpId);

}
