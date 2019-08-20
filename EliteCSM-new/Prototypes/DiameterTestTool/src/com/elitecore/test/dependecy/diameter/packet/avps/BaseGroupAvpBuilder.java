package com.elitecore.test.dependecy.diameter.packet.avps;

import com.elitecore.test.dependecy.diameter.packet.avps.grouped.AvpRule;

import java.util.ArrayList;

public abstract class BaseGroupAvpBuilder extends BaseAVPBuilder {
	
	protected ArrayList<AvpRule> fixedAttrList = new ArrayList<AvpRule>();
	protected ArrayList<AvpRule> requiredAttrList = new ArrayList<AvpRule>();
	protected ArrayList<AvpRule> optionalAttrList = new ArrayList<AvpRule>();
	

	public void setFixedAttrList(ArrayList<AvpRule> fixedAttrList) {
		this.fixedAttrList = fixedAttrList;
	}

	public void setOptionalAttrList(ArrayList<AvpRule> optionalAttrList) {
		this.optionalAttrList = optionalAttrList;
	}

	public void setRequiredAttrList(ArrayList<AvpRule> requiredAttrList) {
		this.requiredAttrList = requiredAttrList;
	}

}
