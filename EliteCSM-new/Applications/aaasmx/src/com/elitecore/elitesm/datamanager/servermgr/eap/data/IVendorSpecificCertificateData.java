/**
 * 
 */
package com.elitecore.elitesm.datamanager.servermgr.eap.data;

/**
 * @author pratikchauhan
 *
 */
public interface IVendorSpecificCertificateData {
	
	public String getId();
	public void setId(String id);
	public String getEaptlsId();
	public void setEaptlsId(String eaptlsId);
	public String getOui();
	public void setOui(String oui);
	public String getServerCertificateIdForVSC();
	public void setServerCertificateIdForVSC(String serverCertificateIdForVSC);
	public Integer getOrderNumber() ;
	public void setOrderNumber(Integer orderNumber) ;
	
}
