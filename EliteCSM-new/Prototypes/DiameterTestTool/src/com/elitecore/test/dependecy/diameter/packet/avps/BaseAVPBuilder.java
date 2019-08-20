/**
 * 
 */
package com.elitecore.test.dependecy.diameter.packet.avps;

import com.elitecore.test.dependecy.diameter.DiameterUtility;

/**
 * @author pulin
 *
 */
public abstract class BaseAVPBuilder {

	protected int intAVPCode;
	protected int intVendorId;
	protected String strAvpId="";
	protected String strAVPEncryption;
	protected byte bAVPFlag;

	public abstract IDiameterAVP createAVP() ;

	/**
	 * Set Code for specified AVP 
	 * @param code Code related to AVP.
	 */
	public void setAVPCode(int code) {
		intAVPCode = code;
	}
	/**
	 * Set vendor id for Specified AVP.
	 */
	public void setVendorId(int vendorId){
		this.intVendorId = vendorId;
	}

	public void setVendorBit(){
		bAVPFlag = (byte)(bAVPFlag | DiameterUtility.BIT_10000000);
	}

	public void setMandatoryBit(){
		bAVPFlag = (byte)(bAVPFlag | DiameterUtility.BIT_01000000);
	}

	public void setProtectedBit(){
		bAVPFlag = (byte)(bAVPFlag | DiameterUtility.BIT_00100000);
	}

	public void setAVPEncryption(String encryption) {
		strAVPEncryption = encryption;
	}
	/**
	 * Sets attribute id in the form of "Vendor-id : AvpCode"
	 */
	public void setAVPId(int vendorId,int AVPCode){
		this.strAvpId = vendorId+":"+AVPCode;
	}

}
