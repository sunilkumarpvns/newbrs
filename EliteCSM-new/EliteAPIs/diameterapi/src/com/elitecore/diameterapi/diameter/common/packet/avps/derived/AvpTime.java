package com.elitecore.diameterapi.diameter.common.packet.avps.derived;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.net.ntp.TimeStamp;

import com.elitecore.diameterapi.diameter.common.packet.avps.basic.AvpOctetString;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;

public class AvpTime  extends AvpOctetString{
	
	
	public AvpTime(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
	
	/*	 
	 * SetTime(long )
	 * This method accepts the number of SECONDS since January 1, 1970, 00:00:00 GMT
	 * 
	 * Jatin 
	 */
	public void setTime(long lTime){
		byte[] valueBuffer;
		TimeStamp t = TimeStamp.getNtpTime((lTime * 1000));
		ByteBuffer bData;
		int iData = (int) t.getSeconds();
		
		bData = ByteBuffer.allocate(4);
		bData.putInt(iData);
		valueBuffer = bData.array();
		this.setValueBytes(valueBuffer);
	}
	public void setTime(Date dDate){
		setTime(dDate.getTime()/1000);
	}
	public long getTime() {
		TimeStamp t = new TimeStamp(DiameterUtility.bytesToHex(getValueBytes()));
		return t.getTime();
	}
	
	@Override
	public String getStringValue() {
/*		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//String strTemp =sdf.format(new TimeStamp(DiameterUtility.bytesToHex(getValueBytes())).getDate());
		String strTemp =sdf.format(new Date(getTime()*1000l));
		return 	strTemp;
*/		
		return String.valueOf((getTime()/1000));
	}
	
	/*	 
	 * SetStringValue(String )
	 * This method accepts the number of seconds since January 1, 1970, 00:00:00 GMT
	 * 
	 * Jatin 
	 */
	@Override
	public void setStringValue(String data) {
		setTime(Long.parseLong(data));
	}
	
	@Override
	public final String getLogValue(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		return sdf.format(new Date(getTime()));
	}
	
	@Override
	public void doPlus(String value) {
		//Adds no of milliseconds to current time
		if(value!=null){
			try{
				setTime(getTime() + Long.parseLong(value));
			}catch(NumberFormatException nfe){
			}
		}
	}	
}
