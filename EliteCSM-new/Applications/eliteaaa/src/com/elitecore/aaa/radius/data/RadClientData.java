/**
 * 
 */
package com.elitecore.aaa.radius.data;

import java.util.List;

import com.elitecore.aaa.core.data.Vendor;
import com.elitecore.commons.base.Optional;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;

/**
 * @author jatin.zaveri
 *
 */
public interface RadClientData {
	
	public String getClientIp();
	public Long getTimeout();
	public String getSharedSecret(int requestType);
	
	public String getFramedPoolName();
	public String getProfileName();
	public String getClientPolicy();
	public String getDHCPAddress();
	public Optional<Integer> getDynauthPort();
	public String getSupportedAttributesStrCOA();
	public String getUnsupportedAttributesStrCOA();
	public String getSupportedAttributesStrDM();
	public String getUnsupportedAttributesStrDM();
	
	public String getHAAddress();
	
	public String getHotlinePolicy();
		
	public Integer getVendorType();
	
	public String getPrepaidStandard();
	
	public String getVendorName();
	
	public List<byte[]> getDnsList();
			
	public List<Vendor> getSupportedVendorsList();
	
	public List<String> getUserIdentities();
		
	public Long getVendorId();
	
	public boolean isSupportedVendorId(long vendorId);
	
	public List<IRadiusAttribute> getWimaxDnsList();
	
	public List<IRadiusAttribute>get3GPP2DnsList();
	public boolean isMultipleClassAttributeSupported();
	public boolean isFilterUnsupportedVSA();
	public Integer getSubnetMask();
	public Long getStartAddress();
	public Long getEndAddress();
	public boolean containsNetworkAddress();
}
