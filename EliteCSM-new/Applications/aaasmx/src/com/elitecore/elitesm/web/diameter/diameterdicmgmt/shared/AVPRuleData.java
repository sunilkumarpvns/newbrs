package com.elitecore.elitesm.web.diameter.diameterdicmgmt.shared;

import com.google.gwt.user.client.rpc.IsSerializable;

public class AVPRuleData implements IsSerializable{

	private static final long serialVersionUID = 1L;
	
	private String name;
	private String maximum;
	private String minimum;
	private String type;
	private int vendorId;
	private int attrId;
	
	
	public AVPRuleData() {
		// TODO Auto-generated constructor stub
	}



	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}



	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}



	/**
	 * @return the maximum
	 */
	public String getMaximum() {
		return maximum;
	}



	/**
	 * @param maximum the maximum to set
	 */
	public void setMaximum(String maximum) {
		this.maximum = maximum;
	}



	/**
	 * @return the minimum
	 */
	public String getMinimum() {
		return minimum;
	}



	/**
	 * @param minimum the minimum to set
	 */
	public void setMinimum(String minimum) {
		this.minimum = minimum;
	}



	/**
	 * @return the type
	 */
	public String getType() {
		return type;
	}



	/**
	 * @param type the type to set
	 */
	public void setType(String type) {
		this.type = type;
	}



	public int getVendorId() {
		return vendorId;
	}



	public void setVendorId(int vendorId) {
		this.vendorId = vendorId;
	}



	public int getAttrId() {
		return attrId;
	}



	public void setAttrId(int attrId) {
		this.attrId = attrId;
	}
	
   	
	
	
	
}
