package com.elitecore.netvertexsm.datamanager.gateway;

import java.util.List;

import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.base.DataManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.DiameterGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayLocationData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IDiameterGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IRadiusGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.RadiusGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DefaultAttributeMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterAttributeMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterValueMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.RadiusAttributeMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.VendorData;

public interface GatewayDataManager extends DataManager {
	/**
     * This method returns all the available Gateway Location.
     * @return List
     */
    public List<GatewayLocationData> getLocationList() throws DataManagerException;
    
    /**
     * This method returns all the available Gateway List.
     * @return List
     */
    public List<GatewayData> getGatewayList() throws DataManagerException;
    
    public List<GatewayData> getDiameterGatewayDataList(String commProtocolId) throws DataManagerException;
    
    public List<GatewayData> getDiameterGatewayDataAssociation(String commProtocolId,long gatewayId) throws DataManagerException;
    
    public List<GatewayData> getUpdatedDiameterGatewayDataList(String commProtocolId,long gatewayId) throws DataManagerException;
    
    /**
     * This method returns all the available Gateway Profiles.
     * @return List
     */
    public List<GatewayProfileData> getProfileList() throws DataManagerException;
    
    /**
     * This method returns all the available Gateway Profiles.
     * @return List
     */
    public List<GatewayProfileData> getProfileList(String commProtocol) throws DataManagerException;
    
    /**
     * This method returns all the available Vendors.
     * @return List
     */
    public List<VendorData> getVendorList() throws DataManagerException;
    
    /**
     * This method returns all the Gateway matching the given criteria.
     * All the not null fields will be compared for equality.
     * @param  gatewayData
     * @return List
     */
    public List getList(IGatewayData gatewayData) throws DataManagerException;

    /**
     * This method returns all the Gateway matching the give criteria with Paging.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param  gatewayData, pageNo, pageSize
     * @return List
     */
    public PageList search(GatewayData gatewayData, int pageNo, int pageSize) throws DataManagerException;
    
    /**
     * This method returns all the Gateway matching the give criteria with Paging.
     * All the not null @see java.lang.String fields will be compared with
     * contains operator.
     * 
     * @param  profileData, pageNo, pageSize
     * @return List
     */
    public PageList search(GatewayProfileData profileData, int pageNo, int pageSize) throws DataManagerException;    

    /** 
     * This method creates a Gateway.
     * Object of ISearchGateway must be supplied to it.
     * @param  gatewayData
     * @return
     */
    public void create(GatewayData gatewayData) throws DataManagerException;

    /** 
     * This method creates a Gateway Profile.
     * Object of IGatewayProfileData must be supplied to it.
     * @param  gatewayProfileData
     * @return
     */
    public void create(GatewayProfileData gatewayProfileData) throws DataManagerException;
     
    /**
     * This method updates the Status of Gateway.
     * Object of Gateway Id must be supplied to it along with required Status.
     * @param  gatewayId, status
     * @return
     */
   
    
    /**
     * This method delete a gateway.
     * Gateway Id must be supplied to it.
     * @param  gatewayId
     * @throws DataManagerException
     */
    public void deleteProfile(List<Long> profileIdList, String actionAlias) throws DataManagerException;
    

    
//    public void update(IStaffData staffData,String staffId) throws DataManagerException;

    /**
     * This method returns the list of IGatewayData
     * gatewayId must be supplied. 
     * @param  gatewayName
     * @return GatewayData
     * @throws DataManagerException
     */
    public GatewayData getGatewayData (IGatewayData gatewayData) throws DataManagerException;
    
    /**
     * This method returns the list of IGatewayData
     * gatewayId must be supplied. 
     * @param  radiusGatewayData
     * @return radiusGatewayData
     * @throws DataManagerException
     */
    public RadiusGatewayData getRadiusGatewayData(IRadiusGatewayData radiusGatewayData) throws DataManagerException;
    
    /**
     * This method returns the list of IGatewayData
     * gatewayId must be supplied. 
     * @param  diameterGatewayData
     * @return diameterGatewayData
     * @throws DataManagerException
     */
    public DiameterGatewayData getDiameterGatewayData(IDiameterGatewayData diameterGatewayData) throws DataManagerException; 
    
    public List<IGatewayData> getGatewayData (String gatewayName) throws DataManagerException;
    
    /**
     * This method returns the list of IGatewayProfileData
     * profileName must be supplied. 
     * @param  profileName
     * @return List
     * @throws DataManagerException
     */
    public List<GatewayProfileData> getProfileData (String profileName) throws DataManagerException;

    /**
     * @param gateway profile Id
     * @return<code>GatewayProfileData</code>
     */
	GatewayProfileData getGatewayProfileData(long gatewayProfileId);
	
	GatewayData getGatewayDetail(GatewayData gatewayData) throws DataManagerException;

	/**
	 * Return updated gateway profile
	 * @param gatewayProfileData
	 * @return<code>IGatewayProfileData</code>
	 * @throws DataManagerException 
	 */


	void updateGatewayProfile(GatewayProfileData gatewayProfileData) throws DataManagerException;
	void updateRadiusGatewayProfile(GatewayProfileData gatewayProfileData) throws DataManagerException;
	void updateGatewayProfileFieldMap(GatewayProfileData gatewayProfileData) throws DataManagerException;
	
	
	//void updateGatewayData(IGatewayData gatewayData,String oldCommProtocolId) throws DataManagerException;
	void updateGatewayBasicDetail(IGatewayData gatewayData) throws DataManagerException;
	void updateGatewayData(IGatewayData gatewayData) throws DataManagerException;
	
	/**
	 * Return database datasource 
	 * @return<code>List</code>
	 * @throws DataManagerException 
	 */
	List<DatabaseDSData> getDataSourceList() throws DataManagerException;
	

	public void delete(List<Long> gatewayIdList)throws DataManagerException;
	
	public void deleteGWPacketMapData(Long profileId)throws DataManagerException;
	public void deleteRadiusAttributeMapData(Long profileId)throws DataManagerException;
	
	public void create(DiameterAttributeMapData diameterAttributeMapData) throws DataManagerException;
	public void create(RadiusAttributeMapData radiusAttributeMapData) throws DataManagerException;
	
	public List<GatewayProfilePacketMapData> getProfilePacketMapList(long profileId) throws DataManagerException;
	
	
	public List<DiameterValueMapData> getDiameterValueMapData(long attributeId) throws DataManagerException;

	public List<DefaultAttributeMappingData> getDefaultDiameterMapping(String type)throws DataManagerException;
	
	public List<PCCRuleMappingData> getPCCRuleMapList(long profileId) throws DataManagerException;

	public List<GatewayProfilePacketMapData> getDiameterToPCRFProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException;
	public List<GatewayProfilePacketMapData> getPCRFToDiameterProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException;

	public List<GatewayProfilePacketMapData> getRadiusToPCRFProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException;
	public List<GatewayProfilePacketMapData> getPCRFToRadiusProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException;
	public List<DiameterProfileData> getDiameterProfileDataList() throws DataManagerException ;
	public List<GroovyScriptData> getGroovyScriptDataList(long profileId) throws DataManagerException;

	public List<GatewayData> getGatewayListbyProfileId(Long profileId)  throws DataManagerException;

	public List<GatewayProfileRuleMappingData> getProfileRuleMappingRelList(GatewayProfileData gatewayProfileData) throws DataManagerException;

}
