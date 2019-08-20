package com.elitecore.netvertexsm.blmanager.servermgr.certificate;

import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.ServerCertificateDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class ServerCertificateBLManager extends com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager{

	public PageList search(ServerCertificateData serverCertificateData, Map infoMap) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			ServerCertificateDataManager serverCertificateDataManager=getServerCertificateDataManager(session);
			PageList lstServerCertificateList;

			if(serverCertificateDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}
			session.beginTransaction();
			lstServerCertificateList=serverCertificateDataManager.search(serverCertificateData, infoMap);
			session.commit();
			return lstServerCertificateList;
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			session.close();
		}
	}

	public void create(ServerCertificateData serverCertificateData, IStaffData staffData, String actionAlias) throws DataManagerException {
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			ServerCertificateDataManager serverCertificateDataManager=getServerCertificateDataManager(session);
			if(serverCertificateDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}
			
			SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
			if(systemAuditDataManager==null){
				throw new DataManagerException("Data manager not found : "+systemAuditDataManager);
			}
			session.beginTransaction();
			serverCertificateDataManager.create(serverCertificateData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			session.close();
		}
	}

	public ServerCertificateData initUpdate(Long serverCertificateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			ServerCertificateDataManager ServerCertificateDataManager = getServerCertificateDataManager(session);

			if(ServerCertificateDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			session.beginTransaction();
			ServerCertificateData serverCertificateData = ServerCertificateDataManager.getServerCertificateDataByServerCertificateId(serverCertificateId);
			session.commit();
			return serverCertificateData;
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
	} 

	public void update(ServerCertificateData serverCertificateData,IStaffData staffData, String actionAlias) throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			ServerCertificateDataManager serverCertificateDataManager=getServerCertificateDataManager(session);
			if(serverCertificateDataManager==null){
				throw new DataManagerException("Data manager implementation not found for "+getClass().getName());				
			}
			
			SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
			if(systemAuditDataManager==null){
				throw new DataManagerException("Data manager not found : "+systemAuditDataManager);
			}
			session.beginTransaction();
			serverCertificateDataManager.update(serverCertificateData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("ACtion failed : "+exp.getMessage(),exp);			
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed  : "+e.getMessage());			
		}finally{
			session.close();
		}		
	}

	public void delete(List<Long> lstServerCertificateId,IStaffData staffData, String actionAlias)throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			ServerCertificateDataManager serverCertificateDataManager=getServerCertificateDataManager(session);
			if(serverCertificateDataManager==null){
				throw new DataManagerException("Data Managerimplementation not found for "+getClass().getName());			
			}
			
			SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
			if(systemAuditDataManager==null){
				throw new DataManagerException("Data manager not found : "+systemAuditDataManager);
			}

			session.beginTransaction();
			if(lstServerCertificateId!=null & lstServerCertificateId.size() > 0){
				serverCertificateDataManager.delete(lstServerCertificateId);
				systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			}else{
				throw new DataManagerException("Data manager implementation not found for");
			}
			session.commit();
		}catch(ConstraintViolationException cve){
			session.rollback();
			throw cve;
		}catch(Exception exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage());
		}finally{
			session.close();
		}
	}

	public ServerCertificateData view(Long serverCertificateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         

		try{
			ServerCertificateDataManager serverCertificateDataManager = getServerCertificateDataManager(session);

			if(serverCertificateDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			
			session.beginTransaction();
			ServerCertificateData serverCertificateData = serverCertificateDataManager.getServerCertificateDataByServerCertificateId(serverCertificateId);
			session.commit();
			return serverCertificateData;
		}catch(DataManagerException exp){
			session.rollback();
			throw new DataManagerException("Action failed : "+exp.getMessage(),exp);
		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action failed :"+e.getMessage(),e);
		}finally{
			session.close();
		}
	}

	public ServerCertificateDataManager getServerCertificateDataManager(IDataManagerSession session)throws Exception{
		ServerCertificateDataManager serverCertificateDatamanager=(ServerCertificateDataManager)DataManagerFactory.getInstance().getDataManager(ServerCertificateDataManager.class, session);
		return serverCertificateDatamanager;
	}	
}