package com.elitecore.elitesm.ws.rest.adapter;

import javax.xml.bind.annotation.adapters.XmlAdapter;

/**
 * 
 * Week Name Adapter do conversion of week day name, full form to short form and vice versa. <br>
 * It takes full form week day name as input and give short form week day name as output in unmarshal. <br>
 * It takes short form week day name as input and give full form week day name as output in marshal. <br>
 * For invalid values it gives String invalid. 
 * 
 * <pre>
 * for Example:- <br>
 * if input is: 
 * {@code
 * 
 * <start-day>sunday</start-day>
 * 
 * }
 * 
 * than output is :
 * SUN
 * 
 * And vice-versa.
 * </pre>
 * 
 * @author Shekhar Vyas
 *
 */


public class WeekNameAdapter extends XmlAdapter<String, String> {

	private static final String SUNDAY = "Sunday";
	private static final String MONDAY = "Monday";
	private static final String TUESDAY = "Tuesday";
	private static final String WEDNESDAY = "Wednesday";
	private static final String THURSDAY = "Thursday";
	private static final String FRIDAY = "Friday";
	private static final String SATURDAY = "Saturday";
	
	private static final String SUN = "SUN";
	private static final String MON = "MON";
	private static final String TUE = "TUE";
	private static final String WED = "WED";
	private static final String THU = "THU";
	private static final String FRI = "FRI";
	private static final String SAT = "SAT";
	
	@Override
	public String unmarshal(String value) throws Exception {
		
		String weekName = null;
		
		if (SUNDAY.equalsIgnoreCase(value)) {
			weekName = SUN;
		} else if (MONDAY.equalsIgnoreCase(value)) {
			weekName = MON;
		} else if (TUESDAY.equalsIgnoreCase(value)) {
			weekName = TUE;
		} else if (WEDNESDAY.equalsIgnoreCase(value)) {
			weekName = WED;
		} else if (THURSDAY.equalsIgnoreCase(value)) {
			weekName = THU;
		} else if (FRIDAY.equalsIgnoreCase(value)) {
			weekName = FRI;
		} else if (SATURDAY.equalsIgnoreCase(value)) {
			weekName = SAT;
		} else {
			weekName = "invalid";
		}      
		
		return weekName;
		
	}

	@Override
	public String marshal(String value) throws Exception {
		
		String weekName = null;
		
		if (SUN.equals(value)) {
			weekName = SUNDAY;
		} else if (MON.equals(value)) {
			weekName = MONDAY;
		} else if (TUE.equals(value)) {
			weekName = TUESDAY;
		} else if (WED.equals(value)) {
			weekName = WEDNESDAY;
		} else if (THU.equals(value)) {
			weekName = THURSDAY;
		} else if (FRI.equals(value)) {
			weekName = FRIDAY;
		} else if (SAT.equals(value)) {
			weekName = SATURDAY;
		} else {
			weekName = null;
		}      
		
		return weekName;
		
	}

}
