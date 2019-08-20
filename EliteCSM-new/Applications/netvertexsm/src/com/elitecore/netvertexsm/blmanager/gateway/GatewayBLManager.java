package com.elitecore.netvertexsm.blmanager.gateway;

import java.util.List;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.constraintviolation.ConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.EliteGenericValidator;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.database.data.DatabaseDSData;
import com.elitecore.netvertexsm.datamanager.gateway.GatewayDataManager;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.DiameterGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayLocationData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IDiameterGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.IRadiusGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.RadiusGatewayData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DefaultAttributeMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterValueMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.VendorData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class GatewayBLManager extends BaseBLManager {
	private static final String MODULE = "GATEWAY";
	
	/**
	 * @author Manjil Purohit
	 * @param gatewayData
	 * @param actionAlias
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public void create(GatewayData gatewayData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	createValidate(gatewayData);
        	session.beginTransaction();        	
        	gatewayDataManager.create(gatewayData);
            systemAuditDataManager.updateTbltSystemAudit(staffData,actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate Gateway Name. : "+exp.getMessage(),exp);
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
        }finally{
        	session.close();
        }
    }
	
	/**
	 * @author Manjil Purohit
	 * @param gatewayProfileData
	 * @param actionAlias
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public void create(GatewayProfileData gatewayProfileData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
        GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
        SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        
        if (gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	gatewayDataManager.create(gatewayProfileData);
        	systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
        	session.commit();
        }catch(DuplicateParameterFoundExcpetion exp){
        	session.rollback();
        	throw new DuplicateParameterFoundExcpetion("Duplicate User Name. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	session.rollback();
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
    }
	
	public List<GatewayData> getGatewayList() throws DataManagerException {
		List<GatewayData> gatewayList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	gatewayList = gatewayDataManager.getGatewayList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return gatewayList;
	}
	
	public List<GatewayData> getDiameterGatewayDataList(String commProtocolId) throws DataManagerException {
		List<GatewayData> gatewayList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	gatewayList = gatewayDataManager.getDiameterGatewayDataList(commProtocolId);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
		return gatewayList;
	}
	
	public List<GatewayData> getUpdatedDiameterGatewayDataList(String commProtocolId,long gatewayId) throws DataManagerException {
		List<GatewayData> gatewayList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	gatewayList = gatewayDataManager.getUpdatedDiameterGatewayDataList(commProtocolId,gatewayId);                                         
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
		return gatewayList;
	}
/*	private DiameterProfileData getDiameterProfileData(long profileId){
		
	}*/
	public List<DiameterProfileData> getDiameterProfileDataList() throws DataManagerException {
		List<DiameterProfileData>  diameterProfileDataList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	diameterProfileDataList = gatewayDataManager.getDiameterProfileDataList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return diameterProfileDataList;
	}	
	public List<GatewayLocationData> getLocationList() throws DataManagerException {
		List<GatewayLocationData> locationList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	locationList = gatewayDataManager.getLocationList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return locationList;
	}
	
	
	public List<GatewayProfileData> getProfileList() throws DataManagerException {
		List<GatewayProfileData> profileList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	profileList = gatewayDataManager.getProfileList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return profileList;
	}
	
	public List<GatewayProfileData> getProfileList(String commProtocolId) throws DataManagerException {
		List<GatewayProfileData> profileList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	profileList = gatewayDataManager.getProfileList(commProtocolId);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return profileList;
	}
	
	public List<PCCRuleMappingData> getPCCRuleMapList(long profileId) throws DataManagerException {
		List<PCCRuleMappingData> pccRuleMapList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	pccRuleMapList = gatewayDataManager.getPCCRuleMapList(profileId);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return pccRuleMapList;
	}
	public List<GroovyScriptData> getGroovyScriptsDataList(long profileId) throws DataManagerException {
		List<GroovyScriptData> groovyScriptsDataList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	groovyScriptsDataList = gatewayDataManager.getGroovyScriptDataList(profileId);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
		return groovyScriptsDataList;
	}	
	public List<VendorData> getVendorList() throws DataManagerException {
		List<VendorData> vendorList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	vendorList = gatewayDataManager.getVendorList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return vendorList;
	}
	
	public List<GatewayProfilePacketMapData> getProfilePacketMapList(long profileId) throws DataManagerException {
		List<GatewayProfilePacketMapData> diameterPacketMapList = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	 
        	session.beginTransaction();	        
        	diameterPacketMapList = gatewayDataManager.getProfilePacketMapList(profileId);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return diameterPacketMapList;
	}
	
	
	public List<DiameterValueMapData> getDiameterValueMapData(long attributeId) throws DataManagerException {
		List<DiameterValueMapData> diameterAttributeMap = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	diameterAttributeMap = gatewayDataManager.getDiameterValueMapData(attributeId);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return diameterAttributeMap;
	}
	
	public GatewayData getGatewayDetail(GatewayData gatewayData) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		
		GatewayData gatewayDataInfo = new GatewayData();
		
		if(dataManager == null || auditDataManager == null)
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		
		try {
			session.beginTransaction();
			gatewayDataInfo = dataManager.getGatewayDetail(gatewayData);
			session.commit();
		} catch (Exception e) {
			session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}
		return gatewayDataInfo;
	}
	
	public GatewayData getGatewayData(IGatewayData gatewayData,IStaffData staffData,String actionAlias) throws DataManagerException {
		GatewayData gatewayInfo = null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		if(gatewayDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	gatewayInfo = gatewayDataManager.getGatewayData(gatewayData);
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return gatewayInfo;
	}
	
	public GatewayData getGatewayData(IGatewayData gatewayData) throws DataManagerException {
		GatewayData gatewayInfo = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	gatewayInfo = gatewayDataManager.getGatewayData(gatewayData);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
       return gatewayInfo;
	}
	
	
	/**
	 * @author  Manjil Purohit
	 * @param   radiusGatewayData
	 * @return  radiusGatewayInfo
	 * @throws  DataManagerException 
	 */
	public RadiusGatewayData getRadiusGatewayData(IRadiusGatewayData radiusGatewayData) throws DataManagerException {
		RadiusGatewayData radiusGatewayInfo = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	radiusGatewayInfo = gatewayDataManager.getRadiusGatewayData(radiusGatewayData);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
          session.close(); 	
        }		
       return radiusGatewayInfo;
	}
	
	/**
	 * @author  Manjil Purohit
	 * @param   diameterGatewayData
	 * @return  diameterGatewayInfo
	 * @throws  DataManagerException 
	 */
	public DiameterGatewayData getDiameterGatewayData(IDiameterGatewayData diameterGatewayData) throws DataManagerException {
		DiameterGatewayData diameterGatewayInfo = null;
		
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if(gatewayDataManager == null || systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	diameterGatewayInfo = gatewayDataManager.getDiameterGatewayData(diameterGatewayData);
        	Long alternateHostId = diameterGatewayInfo.getAlternateHostId();
			if(alternateHostId != null && alternateHostId > 0){
        		GatewayData gatewayData = new GatewayData();
        		gatewayData.setGatewayId(alternateHostId);
        		diameterGatewayInfo.setAlternateHostData(getGatewayData(gatewayData));
        	}else{
        		diameterGatewayInfo.setAlternateHostData(null);
        	}
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return diameterGatewayInfo;
	}
	
	/**
	 * @author  Manjil Purohit
	 * @param   gatewayData
	 * @param   pageNo
	 * @param   pageSize
	 * @return  List
	 * @throws  DataManagerException 
	 */
	public PageList search(GatewayData gatewayData, int pageNo, int pageSize, IStaffData staffData, String actionAlias) throws DataManagerException {
        IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
        GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
        PageList gatewayList;
        if(gatewayDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
        	gatewayList = gatewayDataManager.search(gatewayData, pageNo, pageSize);
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }
        return gatewayList;
        
    }
	
	public PageList search(GatewayProfileData profileData, int pageNo, int pageSize, IStaffData staffData, String actionAlias) throws DataManagerException {                 
       IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
       GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
       PageList gatewayList;
       if(gatewayDataManager == null)
           throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
       try{        	
		   gatewayList = gatewayDataManager.search(profileData, pageNo, pageSize);
       }catch(Exception e){
       		throw new DataManagerException("Action failed :"+e.getMessage());
       }finally{
    	   session.close();
       }
       return gatewayList;       
   }
	
	public void createValidate(IGatewayData gatewayData) throws DataValidationException {
		//DeviceType
		if(EliteGenericValidator.isBlankOrNull(gatewayData.getCommProtocol())){
			throw (new DataValidationException("Invalid Gateway Name",(MODULE+"."+"name").toLowerCase()));
		}
		//ConnectionUrl
		if(EliteGenericValidator.isCreditCard(gatewayData.getConnectionUrl())){
			throw (new DataValidationException("Invalid Connection URL",(MODULE+"."+"name".toLowerCase())));
		}
		//AreaName
		if(EliteGenericValidator.isCreditCard(gatewayData.getAreaName())){
			throw (new DataValidationException("Invalid Area Name",(MODULE+"."+"name".toLowerCase())));
		}		
	}
	
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
        return systemAuditDataManager; 
    }
	
	/**
     * @author MilanPaliwal
     * @param packageData
     * @param actionAlias
     * @throws DataManagerException
     * @throws DuplicateParameterFoundExcpetion
     */
    public void delete(List<Long> gatewayIdList, IStaffData staffData, String actionAlias) throws DataManagerException{
            IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
            GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
            SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
            
            if (gatewayDataManager == null && systemAuditDataManager == null)
                    throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

            try{
                    session.beginTransaction();
                    gatewayDataManager.delete(gatewayIdList);
                    systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
                    session.commit();                
            }catch(org.hibernate.exception.ConstraintViolationException ce){
                session.rollback();
            	throw new org.hibernate.exception.ConstraintViolationException(ce.getMessage(), ce.getSQLException(), ce.getConstraintName());
            }catch(Exception de){
                 session.rollback();
                 throw new DataManagerException("Action Failed .",de.getMessage(),de);
            }finally{
            	session.close();
            }
    }
	
	/**
	 * @author Manjil Purohit
     * @return Returns Data Manager instance for gateway data.
     */
    public GatewayDataManager getGatewayDataManager(IDataManagerSession session) {
    	GatewayDataManager gatewayDataManager = (GatewayDataManager) DataManagerFactory.getInstance().getDataManager(GatewayDataManager.class, session);
        return gatewayDataManager; 
    }

    /**
     * 
     * @return <code>GatewayProfileData</code>
     * @param gatewayProfileData
     */
	public void updateGatewayProfile(GatewayProfileData gatewayProfileData,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		try{
		if(dataManager == null || auditDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		session.beginTransaction();
		dataManager.updateGatewayProfile(gatewayProfileData);
		auditDataManager.updateTbltSystemAudit(staffData, actionAlias);
		session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException(e);
		}finally{
			session.close();
		}
	}
	
	
	public void updateRadiusGatewayProfile(GatewayProfileData gatewayProfileData,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		try{
		if(dataManager == null || auditDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		session.beginTransaction();
		dataManager.updateRadiusGatewayProfile(gatewayProfileData);
		auditDataManager.updateTbltSystemAudit(staffData, actionAlias);
		session.commit();
		}catch(Exception e){
			session.close();
			throw new DataManagerException(e);
		}finally{
			session.close();
		}
		
	}
	
	public void updateGatewayProfileFieldMap(GatewayProfileData gatewayProfileData,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
       try{		
		if(dataManager == null || auditDataManager == null){
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		session.beginTransaction();
		dataManager.updateGatewayProfileFieldMap(gatewayProfileData);
		auditDataManager.updateTbltSystemAudit(staffData, actionAlias);
		session.commit();
       }catch(Exception e){
    	   session.rollback();
       }finally{
    	   session.close();
       }
	}
	
	
	public void updateGatewayBasicDetail(IGatewayData gatewayData,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		try{
		if(dataManager == null || auditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		session.beginTransaction();
		dataManager.updateGatewayBasicDetail(gatewayData);
		auditDataManager.updateTbltSystemAudit(staffData, actionAlias);
		session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException(e);
		}finally{
			session.close();
			
		}
		
	}
	public void updateGatewayData(IGatewayData gatewayData,IStaffData staffData,String actionAlias) throws DataManagerException{
		IDataManagerSession session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		try{
		if(dataManager == null || auditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		session.beginTransaction();
		dataManager.updateGatewayData(gatewayData);
		auditDataManager.updateTbltSystemAudit(staffData, actionAlias);
		session.commit();
		}catch(Exception e){
          session.rollback();
          throw new DataManagerException(e);
		}finally{
			session.close();
			
		}
	}
	
	public void deleteProfile(List<Long> profileIdList,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if(gatewayDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			session.beginTransaction();
			gatewayDataManager.deleteProfile(profileIdList,actionAlias);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(ConstraintViolationException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception exp){
			session.rollback();
			throw new DataManagerException("Action failed :"+exp.getMessage(),exp);
		}finally{
			session.close();
		}

	}
	/*public void updateStatusValidate(List lstDatabaseDSIds,String commonStatusId)throws DataValidationException{

		// commonStatusId
		if(EliteGenericValidator.isBlankOrNull(commonStatusId)){
			throw (new DataValidationException("Invalid Database Datasource  commonStatusId",(MODULE+"."+"commonStatusId").toLowerCase()));
		}
	}
	
	public void updateStatus(List<String> lstProfileIds, String commonStatusId,
			IStaffData staffData, String actionAlias) throws DataManagerException {

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		Date currentDate = new Date();

		if(gatewayDataManager == null || systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for "+getClass().getName());

		try{
			updateStatusValidate(lstProfileIds,commonStatusId);
			session.beginTransaction();

			if(lstProfileIds != null){
				for(int i=0;i<lstProfileIds.size();i++){
					if(lstProfileIds.get(i) != null){

						String transactionId = lstProfileIds.get(i).toString();
						long datasourceId= Long.parseLong(transactionId);
						gatewayDataManager.updateStatus(datasourceId,commonStatusId, new Timestamp(currentDate.getTime()));

						//systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias,transactionId);
					}
				}

				session.commit();
				session.close();
			}else{
				throw new DataManagerException("Data Manager implementation not found for ");    		
			}
		}catch(Exception exp){
			session.rollback();
			session.close();
			throw new DataManagerException("Action failed :"+exp.getMessage());
		}
	}*/


	
	public List<DatabaseDSData> getDataSorceList() throws DataManagerException{

		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		try{
			if(dataManager == null || auditDataManager == null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}

			session.beginTransaction();
			List<DatabaseDSData> datasourceList = dataManager.getDataSourceList();
			return datasourceList;
		}finally{
			session.close();
		}
	}

	public List<DefaultAttributeMappingData> getDefaultDiameterMapping(String type) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		try{
		if(dataManager == null || auditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		
		session.beginTransaction();
		List<DefaultAttributeMappingData> defaultAttributeMappingDataList=dataManager.getDefaultDiameterMapping(type);	
		return defaultAttributeMappingDataList;
		}finally{
			session.close();
		}
		
	}

	public List<GatewayProfilePacketMapData> getDiameterToPCRFProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		List<GatewayProfilePacketMapData> diameterToPCRFProfilePacketMapList=null;
		if(dataManager == null || auditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{
			diameterToPCRFProfilePacketMapList=dataManager.getDiameterToPCRFProfilePacketMapList(gatewayProfileData);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		
		return diameterToPCRFProfilePacketMapList;
	}
	
	public List<GatewayProfilePacketMapData> getPCRFToDiameterProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		List<GatewayProfilePacketMapData> pcrfToDiameterProfilePacketMapList=null;
		if(dataManager == null || auditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{
			pcrfToDiameterProfilePacketMapList=dataManager.getPCRFToDiameterProfilePacketMapList(gatewayProfileData);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		
		return pcrfToDiameterProfilePacketMapList;
	}
	public List<GatewayProfilePacketMapData> getRadiusToPCRFProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		List<GatewayProfilePacketMapData> radiusToPCRFProfilePacketMapList=null;
		if(dataManager == null || auditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{
			radiusToPCRFProfilePacketMapList=dataManager.getRadiusToPCRFProfilePacketMapList(gatewayProfileData);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		
		return radiusToPCRFProfilePacketMapList;
	}
	
	public List<GatewayProfilePacketMapData> getPCRFToRadiusProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		List<GatewayProfilePacketMapData> pcrfToRadiusProfilePacketMapList=null;
		if(dataManager == null || auditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{
			pcrfToRadiusProfilePacketMapList=dataManager.getPCRFToRadiusProfilePacketMapList(gatewayProfileData);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		
		return pcrfToRadiusProfilePacketMapList;
	}
	
	public List<GatewayProfileRuleMappingData> getProfileRuleMappingList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		SystemAuditDataManager auditDataManager = getSystemAuditDataManager(session);
		List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList=null;
		if(dataManager == null || auditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{
			gatewayProfileRuleMappingList=dataManager.getProfileRuleMappingRelList(gatewayProfileData);
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
		
		return gatewayProfileRuleMappingList;
	}
	
	public GatewayProfileData getGatewayProfileData(long gatewayProfileId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager dataManager = getGatewayDataManager(session);
		if(dataManager == null)
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		GatewayProfileData gatewayProfileData;
		try {
			gatewayProfileData = dataManager.getGatewayProfileData(gatewayProfileId);
		} catch (Exception e) {
        	throw new DataManagerException("Action failed :"+e.getMessage() , e);
		}finally{
			session.close();
		}
		return gatewayProfileData;
	}
	
	
	public List<GatewayData> getGatewayListByProfileId(Long profileId) throws DataManagerException {
		List<GatewayData> gatewayList = null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		
		if(gatewayDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
	    	gatewayList = gatewayDataManager.getGatewayListbyProfileId(profileId);                                         
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
        }finally{
        	session.close();
        }		
		return gatewayList;
	}
	
	public List<GatewayData> getGatewayAssociations(String commProtocolId,Long gatewayId) throws DataManagerException {
		List<GatewayData> gatewayList = null;
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		GatewayDataManager gatewayDataManager = getGatewayDataManager(session);
		
		if(gatewayDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        try{        	
	    	gatewayList = gatewayDataManager.getDiameterGatewayDataAssociation(commProtocolId, gatewayId);                                         
        }catch(Exception e){
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
        }finally{
        	session.close();
        }		
		return gatewayList;
	}
	
}
