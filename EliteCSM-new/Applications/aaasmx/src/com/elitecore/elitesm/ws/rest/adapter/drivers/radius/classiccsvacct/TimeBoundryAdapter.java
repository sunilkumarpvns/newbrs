package com.elitecore.elitesm.ws.rest.adapter.drivers.radius.classiccsvacct;

import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.adapters.XmlAdapter;
/**
*This adaptor developed for Time Boundry fields for Classic CSV ACCT Driver which 
* takes time bounary duration and conver into Long value. 
*@author Tejas.p.Shah
*	
*/
public class TimeBoundryAdapter extends XmlAdapter<String, Long> {

	public enum TimeBoundry {
		_0(0,"NONE"), _1_MIN(1,"1 MIN"), _2_MIN(2,"2 MIN"), _3_MIN(3,"3 MIN"),
		_5_MIN(5,"5 MIN"), _10_MIN(10,"10 MIN"), _15_MIN(15,"15 MIN"),
		_20_MIN(20,"20 MIN"), _30_MIN(30,"30 MIN"), _60(60,"HOURLY"), _1440(1440,"DAILY");

		private TimeBoundry(long time_value,String time_duration) {
			this.time_duration = time_duration;
			this.time_value = time_value;
		}

		private String time_duration;
		private long time_value;

		public static Map<String, TimeBoundry> unmarshallMap = new HashMap<String, TimeBoundry>();
		public static Map<Long, TimeBoundry> marshallMap = new HashMap<Long, TimeBoundry>();
		
		static {
			for (TimeBoundry timeBoundry : TimeBoundry.values()) {
				unmarshallMap.put(timeBoundry.time_duration, timeBoundry);
				marshallMap.put(timeBoundry.time_value, timeBoundry);
			}
		}
	}

	@Override
	public Long unmarshal(String v) throws Exception {
		if (v.isEmpty() == false && TimeBoundry.unmarshallMap.get(v.toUpperCase().trim()) != null) {
		switch (TimeBoundry.unmarshallMap.get(v.toUpperCase().trim())) {
		case _0:
			return 0L;

		case _1_MIN:
			return 1L;

		case _2_MIN:
			return 2L;

		case _3_MIN:
			return 3L;

		case _5_MIN:
			return 5L;

		case _10_MIN:
			return 10L;

		case _15_MIN:
			return 15L;

		case _20_MIN:
			return 20L;

		case _30_MIN:
			return 30L;

		case _60:
			return 60L;

		case _1440:
			return 1440L;
		}
		
	}
		return -1L;
}

	@Override
	public String marshal(Long v) throws Exception {
		if(TimeBoundry.marshallMap.get(v) != null){
		switch (TimeBoundry.marshallMap.get(v)) {
		case _0:
			return "NONE";
			
		case _1_MIN:
			return "1 min";

		case _2_MIN:
			return "2 min";

		case _3_MIN:
			return "3 min";

		case _5_MIN:
			return "5 min";

		case _10_MIN:
			return "10 min";

		case _15_MIN:
			return "15 min";

		case _20_MIN:
			return "20 min";

		case _30_MIN:
			return "30 min";

		case _60:
			return "Hourly";

		case _1440:
			return "Daily";
		}
	}
		return "Invalid";
	}

}
