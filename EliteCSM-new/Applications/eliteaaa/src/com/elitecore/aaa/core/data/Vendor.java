/**
 * 
 */
package com.elitecore.aaa.core.data;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;


/**
 * @author narendra.pathai
 *
 */

@XmlType(propOrder = {})
public class Vendor implements Cloneable {

	private long lVendorID;	
	private String strVendorName = null;

	public Vendor(){	
		//required By Jaxb.
	}

	public Vendor(long lVendorId, String strVendorName){
		this.lVendorID = lVendorId;
		this.strVendorName = strVendorName;
	}
	
	@XmlElement(name = "id", type = long.class)
	public long getVendorID() {
		return lVendorID;
	}
	public void setVendorID(long strVendorID) {
		this.lVendorID = strVendorID;
	}
	
	@XmlElement(name = "name", type = String.class)
	public String getVendorName() {
		return strVendorName;
	}
	public void setVendorName(String strVendorName) {
		this.strVendorName = strVendorName;
	}
	
	@Override
	public boolean equals(Object obj) {
		return this.lVendorID==((Vendor)obj).lVendorID;
	}
	
	@Override
	public int hashCode() {
		return (int)lVendorID;
	}
	
	@Override
	public Vendor clone() throws CloneNotSupportedException {
		return (Vendor) super.clone();
	}
	
	@Override	
	public String toString(){		
		return "VENDOR_NAME = " + this.strVendorName + "(" + this.lVendorID + ")";
	}
}
