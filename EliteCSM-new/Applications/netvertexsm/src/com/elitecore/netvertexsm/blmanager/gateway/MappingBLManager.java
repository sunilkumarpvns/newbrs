package com.elitecore.netvertexsm.blmanager.gateway;

import java.util.List;

import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.PacketMapDataManager;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class MappingBLManager extends BaseBLManager{
	
	/**
	 * @author Manjil Purohit
	 * @param packetMappingData
	 * @param pageNo
	 * @param pageSize
	 * @param actionAlias
	 * @return 
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public PageList search(PacketMappingData packetMappingData, int pageNo, int pageSize,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		PageList pageList = null;
		if (mappingDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	pageList = mappingDataManager.search(packetMappingData, pageNo, pageSize);
        }catch(DuplicateParameterFoundExcpetion exp){
        	throw new DuplicateParameterFoundExcpetion("Duplicate User Name. : "+exp.getMessage());
        }catch(DataManagerException exp){
        	throw new DataManagerException("Action failed : "+exp.getMessage());
        }finally{
        	session.close();
        }
        return pageList;
    }
	
	/**
	 * @author Manjil Purohit
	 * @param packetMappingData
	 * @param actionAlias
	 * @throws DataManagerException
	 * @throws DuplicateParameterFoundExcpetion
	 */
	public void create(PacketMappingData packetMappingData,IStaffData staffData, String actionAlias) throws DataManagerException,DuplicateParameterFoundExcpetion {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
        if (mappingDataManager == null && systemAuditDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{
        	session.beginTransaction();        	
        	mappingDataManager.create(packetMappingData);
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
	
	public void update(PacketMappingData packetMappingData,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session =  DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		if(mappingDataManager == null && systemAuditDataManager == null){
			 throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
		}
		try{
		session.beginTransaction();
		mappingDataManager.update(packetMappingData);
		systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
		session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException(e);
		}finally{
			session.close();
		}
	}
	
	public PacketMappingData getPacketMappingData(PacketMappingData packetMappingData) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		PacketMappingData packetMappingInfo = null;
		
		if(mappingDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	packetMappingInfo = mappingDataManager.getPacketMappingData(packetMappingData);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
        	session.close();
        }		
		return packetMappingInfo;
	}
	
	public void changeMappingOrder(Long[] order, long profileId, IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		
		try{				     
			if(mappingDataManager==null){
				throw new DataManagerException("Data Manager Not Found: ServicePoilcyDataManager.");
			}
			session.beginTransaction();
			mappingDataManager.changeMappingOrder(order, profileId);
			session.commit();
			
		}catch(DataManagerException e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
	}
	
	public List searchPacketMap(long profileId) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		List listOfSearchData = null;		
		
		if (mappingDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
		try{
			listOfSearchData = mappingDataManager.searchPacketMap(profileId);		
			return listOfSearchData;
		}catch(Exception e){
        	throw new DataManagerException("Action Failed. : "+e.getMessage());
		}finally{
			session.close();
		}
	}
	
	public void delete(List<Long> packetMapIdList,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession(); 
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
		
		if (mappingDataManager == null && systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();
			mappingDataManager.delete(packetMapIdList);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();		
		}catch(ConstraintViolationException de){
			session.rollback();
			throw new ConstraintViolationException(de.getMessage(),de.getSQLException(),de.getConstraintName());
		}catch(Exception de){
			session.rollback();
			throw new DataManagerException("Action Failed .",de.getMessage());
		}finally{
			session.close();
		}
	}
	
	public List<PacketMappingData> getPacketMappingList(String commProtocol) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		List<PacketMappingData> packetMappingList = null;
		
		if(mappingDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	packetMappingList = mappingDataManager.getPacketMappingList(commProtocol);                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return packetMappingList;
	}
	/**
	 * @author Manjil Purohit
     * @return Returns Data Manager instance for Mapping data.
     */
    public PacketMapDataManager getMappingDataManager(IDataManagerSession session) {
    	PacketMapDataManager mappingDataManager = (PacketMapDataManager) DataManagerFactory.getInstance().getDataManager(PacketMapDataManager.class, session);
        return mappingDataManager; 
    }

	public List<PacketMappingData> getPCRFToDiameterPacketMappingList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		List<PacketMappingData> packetMappingList = null;
		
		if(mappingDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	packetMappingList = mappingDataManager.getPCRFToDiameterPacketMappingList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return packetMappingList;
	}
	
	public List<PacketMappingData> getDiameterToPCRFPacketMappingList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		List<PacketMappingData> packetMappingList = null;
		
		if(mappingDataManager == null)
            throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
        
        try{        	
        	session.beginTransaction();	        
        	packetMappingList = mappingDataManager.getDiameterToPCRFPacketMappingList();                                         
        	session.commit();
        }catch(Exception e){
        	session.rollback();
        	throw new DataManagerException("Action failed :"+e.getMessage());
        }finally{
			session.close();
		}		
		return packetMappingList;
	}
	public List<PacketMappingData> getPCRFToRadiusPacketMappingList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		List<PacketMappingData> packetMappingList = null;

		if(mappingDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			session.beginTransaction();	        
			packetMappingList = mappingDataManager.getPCRFToRadiusPacketMappingList();                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return packetMappingList;
	}
	
	public List<PacketMappingData> getRadiusToPCRFPacketMappingList() throws DataManagerException{
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		List<PacketMappingData> packetMappingList = null;

		if(mappingDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			session.beginTransaction();	        
			packetMappingList = mappingDataManager.getRadiusToPCRFPacketMappingList();                                         
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}		
		return packetMappingList;
	}
	
	public SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession session) {
		SystemAuditDataManager systemAuditDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return systemAuditDataManager; 
	}
	
	public boolean isPacketMappingConfiguredWithGatewayProfile(String[] strPacketMapIds)  throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		boolean isConfigured=false;
		if(mappingDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{        	
			Long[] ids = new Long[strPacketMapIds.length];  
			for (int i = 0; i < strPacketMapIds.length; i++) {  
				ids[i] = Long.valueOf(strPacketMapIds[i]);  
			}
			isConfigured = mappingDataManager.isPacketMappingConfiguredWithGatewayProfile(ids);                                         
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}					
		return isConfigured;
	}
	
	public List<GatewayProfileData> getPacketMapConfiguredProfileNames(long packetMapId)  throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		PacketMapDataManager mappingDataManager = getMappingDataManager(session);
		List<GatewayProfileData> gatewayProfileDataList = null;
		if(mappingDataManager == null){            
			throw new DataManagerException("Action failed :");
		}
		try{        	  				 								
			gatewayProfileDataList =  mappingDataManager.getPacketMapConfiguredProfileList(packetMapId);                                         
		}catch(Exception e){
			throw new DataManagerException("Action failed :"+e.getMessage());
		}finally{
			session.close();
		}										
		return gatewayProfileDataList;
	}
}
