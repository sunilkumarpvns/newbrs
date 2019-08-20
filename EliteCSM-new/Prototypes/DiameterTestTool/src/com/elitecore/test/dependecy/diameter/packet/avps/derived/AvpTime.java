package com.elitecore.test.dependecy.diameter.packet.avps.derived;

import com.elitecore.test.dependecy.diameter.DiameterUtility;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpOctetString;

import org.apache.commons.net.ntp.TimeStamp;

import java.nio.ByteBuffer;
import java.text.SimpleDateFormat;
import java.util.Date;

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
	
	
	public static void main(String args[]) {
		/*int year = Integer.parseInt(args[0]);
		int month = Integer.parseInt(args[1]);
		int day = Integer.parseInt(args[2]);
		int hr = Integer.parseInt(args[3]);
		int min = Integer.parseInt(args[4]);
		int sec = Integer.parseInt(args[5]);
		
		GregorianCalendar temp = new GregorianCalendar(year, month, day, hr, min, sec);
		
		Date date = temp.getTime();
		new Date().getTime();
		
		TimeStamp t = TimeStamp.getNtpTime(date.getTime());
		System.out.println("NTP Time :-- " + t.getSeconds());
		*/
		Date currentDate = new Date("01/01/2012");
		TimeStamp t = TimeStamp.getNtpTime(currentDate.getTime());
		AvpTime avp = new AvpTime(0,0,(byte)0,"",null);
		avp.setTime(currentDate);
		System.out.println("NTP Time :--        " + t.getSeconds());
		System.out.println("AVP String Time :-- " + avp.getStringValue());		
		System.out.println("AVP Log Time:--     " + avp.getLogValue());
		System.out.println("Input Time :--      " + currentDate);
		
		
	}
}
