package com.elitecore.test.dependecy.diameter.packet.avps.grouped;

public class AvpRule implements Cloneable{
	private int vendorId;
	private int attrId;
	private String name;
	private String minimum;
	private String maximum;
	
	public AvpRule(){
		name    = "";
		minimum = "0";
		maximum = "0xFFFFFFF";
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getMinimum() {
		return minimum;
	}
	public void setMinimum(String minimum) {
		this.minimum = minimum;
	}
	public String getMaximum() {
		return maximum;
	}
	public void setMaximum(String maximum) {
		this.maximum = maximum;
	}
	@Override
	public Object clone() throws CloneNotSupportedException {
		AvpRule avpRule = new AvpRule();
		avpRule.attrId = this.attrId;
		avpRule.vendorId =this.vendorId;
		avpRule.setName(new String(this.name));
		avpRule.setMinimum(new String(this.minimum));
		avpRule.setMaximum(new String(this.maximum));		
		return avpRule;		
	}

	public int getVendorId() {
		return vendorId;
	}

	public int getAttrId() {
		return attrId;
	}

	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}

	public void setAttrId(int attrId) {
		this.attrId = attrId;
	}

}
