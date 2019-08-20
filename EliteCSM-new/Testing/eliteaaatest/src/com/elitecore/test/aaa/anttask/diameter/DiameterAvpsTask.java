package com.elitecore.test.aaa.anttask.diameter;

import java.util.ArrayList;
import java.util.List;

import org.apache.tools.ant.Task;

public class DiameterAvpsTask extends Task {
    
	private List<DiameterAvpTask> avps = new ArrayList<DiameterAvpTask>();
	
	public void addAvp(DiameterAvpTask avp) {
		avps.add(avp);
	}
	
	public List<DiameterAvpTask> getAvps() {
		return avps;
	}

}
