package com.elitecore.diameterapi.diameter.common.packet.avps.derived;

import static org.junit.Assert.assertEquals;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.commons.net.ntp.TimeStamp;
import org.junit.Test;

/**
 * 
 * @author narendra.pathai
 *
 */
public class AvpTimeTest {
	
	@Test
	public void justATest() {
		Date date = new GregorianCalendar(2017, Calendar.JANUARY, 1).getTime();
		TimeStamp t = TimeStamp.getNtpTime(date.getTime());
		
		AvpTime avp = new AvpTime(0, 0, (byte)0, "", null);
		avp.setTime(date);
		
		
		System.out.println("NTP Time :--        " + t.getSeconds());
		System.out.println("AVP String Time :-- " + avp.getStringValue());		
		System.out.println("AVP Log Time:--     " + avp.getLogValue());
		System.out.println("Input Time :--      " + date);
		
		assertEquals(t.getTime(), avp.getTime());
	}

}
