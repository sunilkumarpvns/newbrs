/*
 * @author pankit
 * Created Date : 17-Sept-2009
 */

package com.elitecore.license.configuration;

import com.elitecore.license.base.LicenseData;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * LicenseConfigurationManager class manages all the configuration details provided by License Configuration.  	 
 * @author Elitecore Technologies Ltd.
 */
public class LicenseConfigurationManager {

	private static LicenseConfigurationManager licenseConfigurationManager;
	private String strFileName;
	private Map<Integer,String> SupportedVendorList;
	private List<LicenseData> aaaServiceList;
	private List<LicenseData> nvServiceList;
	private List<LicenseData> SystemLicenseList;
/*	private List<LicenseData> RadiusServiceList;
	private List<LicenseData> DiameterLicenseList;
	private List<LicenseData> RMLicenseList;
*/	private boolean isInitialized=false;
	private List<String> nasTypeList;
	private Map<String,Object> licenseDataMap; 
	
	private LicenseConfigurationManager(){
		licenseDataMap = new HashMap<String,Object>();
	}

	static {
		licenseConfigurationManager = new LicenseConfigurationManager();
	}

	public static LicenseConfigurationManager getInstance() {
		return licenseConfigurationManager;
	}	

	@SuppressWarnings("unchecked")
	public void init(){
		if(!isInitialized){
			SupportedVendorList = (Map<Integer,String>)licenseDataMap.get("SUPPORTED_VENDOR_LIST");
			aaaServiceList   = (List<LicenseData>)licenseDataMap.get("AAA_SERVICE_LIST");
			nvServiceList	 = (List<LicenseData>)licenseDataMap.get("NV_SERVICE_LIST");
		//	RadiusServiceList   = (List<LicenseData>)licenseDataMap.get("RADIUS_SERVICE_LIST");
			SystemLicenseList   = (List<LicenseData>)licenseDataMap.get("SYSTEM_LICENSE_LIST");
		//	DiameterLicenseList = (List<LicenseData>)licenseDataMap.get("DIAMETER_SERVICE_LIST");
		//	RMLicenseList       = (List<LicenseData>)licenseDataMap.get("RESOURCE_MANAGER_SERVICE_LIST");
			nasTypeList			= (List<String>)licenseDataMap.get("NAS_TYPE_LIST");
			isInitialized		= true;
		}
	}

	public void setFileName(String strFileName){
		this.strFileName=strFileName;
	}
	public String getFileName(){
		return this.strFileName;
	}
	public Map<Integer,String> getSupportedVendorList(){
		return SupportedVendorList;
	}
	
	public List<LicenseData> getAAAServiceList(){
		return aaaServiceList;
	}
	public List<LicenseData> getNVServiceList(){
		return nvServiceList;
	}
	
/*	public List<LicenseData> getRadiusServiceList(){
		return RadiusServiceList;
	}
*/	public List<LicenseData> getSystemLicenseList(){
		return SystemLicenseList;
	}
/*	public List<LicenseData> getDiameterLicenseList(){
		return DiameterLicenseList;
	}
	public List<LicenseData> getRMLicenseList(){
		return RMLicenseList;
	}*/
	public List<String> getNasTypeList(){
		return nasTypeList;
	}
	
	public void setLicenseData(Map<String,Object> licenseDataMap){
		this.licenseDataMap = licenseDataMap;
	}

}

