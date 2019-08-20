package com.elitecore.corenetvertex.constants;
/**
 * 
 * @author Ajay Pandey
 *
 */
public enum TrafficType {
	
	
	INCOMING("Incoming"),
	OUTGOING("Outgoing"),
	TRANSIT("Transit"),
	MVNO_IDD("MVNO IDD"),
	MVNO_DOMESTIC("MVNO Domestic"),
	MVNO_ROAMING("MVNO Roaming");
	
	public final String val;
	
	TrafficType(String val) {
		this.val = val;
	}
	
	public String getVal() {
		return val;
	}
}


