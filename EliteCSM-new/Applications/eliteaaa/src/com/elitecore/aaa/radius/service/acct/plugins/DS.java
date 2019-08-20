package com.elitecore.aaa.radius.service.acct.plugins;


public class DS {
	private  Pair curr;
	private String fixedValue;
	private Pair dyn;
	
	public Pair getCurr() {
		return curr;
	}
	public void setCurr(Pair curr) {
		this.curr = curr;
	}
	public String getFixedValue() {
		return fixedValue;
	}
	public void setFixedValue(String fixedValue) {
		this.fixedValue = fixedValue;
	}
	public Pair getDyn() {
		return dyn;
	}
	public void setDyn(Pair dyn) {
		this.dyn = dyn;
	}	
}
