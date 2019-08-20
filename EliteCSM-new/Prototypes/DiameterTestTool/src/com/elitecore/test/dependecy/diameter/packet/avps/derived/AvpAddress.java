package com.elitecore.test.dependecy.diameter.packet.avps.derived;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpOctetString;

import java.net.InetAddress;
import java.net.UnknownHostException;

public class AvpAddress extends AvpOctetString{
	private static final byte IPv4 = 1;
	private static final byte IPv6 = 2;
	
	private static final String MODULE = "AVP Address";
	
	public AvpAddress(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	// This method automatically identifies and prepend "type" of address.
	public void setStringValue(String strAddress) {
		byte []dstValueBytes,srcValueBytes;
		InetAddress address = null;
		try {
		address = InetAddress.getByName(strAddress);
		}catch(UnknownHostException e) {
			LogManager.getLogger().warn(MODULE, "Invalid IPAddress Value: "+strAddress+".Provide valid IPv4 or IPv6 syntax. "+e.getMessage());
			return;
		}
		if(address!=null){
			srcValueBytes = address.getAddress();
			dstValueBytes = new byte[2 + srcValueBytes.length];
			dstValueBytes[0] = 0;
			dstValueBytes[1] = (srcValueBytes.length == 4) ? IPv4:IPv6;
			System.arraycopy(srcValueBytes, 0, dstValueBytes, 2, srcValueBytes.length);
			setValueBytes(dstValueBytes);
		}
	}
	
	public String getStringValue() {
		byte [] data = getValueBytes();
		if(data == null ) {
			return null;
		}
		byte[]address = data;
		if(data.length==6 || data.length==18) {
			address = new byte[data.length-2];
			System.arraycopy(data, 2, address, 0, address.length);
		}
		try {
			return InetAddress.getByAddress(address).getHostAddress();
		} catch (UnknownHostException e) {
			LogManager.getLogger().warn(MODULE, "Invalid IPAddress Value: "+e.getMessage());
			return null;
		}
	}
	
	@Override
	public void doPlus(String value){
		if(value!=null)
			setStringValue(value);
	}
	
	
}
