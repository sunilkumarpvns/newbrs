package com.elitecore.netvertexsm.blmanager.servermgr.certificate;

import java.util.List;
import java.util.Map;

import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.netvertexsm.blmanager.core.base.BaseBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.TrustedCertificateDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.TrustedCertificateData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class TrustedCertificateBLManager extends BaseBLManager{

	public PageList search(TrustedCertificateData trustedCertificateData, Map infoMap) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();

		try{
			TrustedCertificateDataManager trustedCertificateDataManager=getTrustedCertificateDataManager(session);

			if(trustedCertificateDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}

			session.beginTransaction();
			PageList lstTrustedCertificateList=trustedCertificateDataManager.search(trustedCertificateData, infoMap);
			session.commit();
			return lstTrustedCertificateList;

		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			session.close();
		}
	}

	public void create(TrustedCertificateData trustedCertificateData,IStaffData staffData, String actionAlias) throws DataManagerException {

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			TrustedCertificateDataManager trustedCertificateDataManager=getTrustedCertificateDataManager(session);
			if(trustedCertificateDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}

			SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
			if(systemAuditDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for SystemAuditDataManager");
			}

			session.beginTransaction();

			trustedCertificateDataManager.create(trustedCertificateData);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);

			session.commit();
		}catch(Exception e){
			session.rollback();
		}finally{
			session.close();
		}
	}

	public TrustedCertificateData initUpdate(Long trustedCertificateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			TrustedCertificateDataManager trustedCertificateDataManager = getTrustedCertificateDataManager(session);

			if(trustedCertificateDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());


			session.beginTransaction();
			TrustedCertificateData trustedCertificateData = trustedCertificateDataManager.getTrustedCertificateDataByTrustedCertificateId(trustedCertificateId);
			session.commit();
			return trustedCertificateData;
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

	public void update(TrustedCertificateData trustedCertificateData, IStaffData staffData, String actionAlias) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{			

			TrustedCertificateDataManager trustedCertificateDataManager=getTrustedCertificateDataManager(session);
			if(trustedCertificateDataManager==null){
				throw new DataManagerException("Data manager implementation not found for "+getClass().getName());				
			}

			SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
			if(systemAuditDataManager==null){
				throw new DataManagerException("Data manager not found : "+systemAuditDataManager);
			}

			session.beginTransaction();

			trustedCertificateDataManager.update(trustedCertificateData);
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

	public void delete(List<Long> lstTrustedCertificateId, IStaffData staffData, String actionAlias)throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			TrustedCertificateDataManager trustedCertificateDataManager=getTrustedCertificateDataManager(session);
			if(trustedCertificateDataManager==null){
				throw new DataManagerException("Data Managerimplementation not found for "+getClass().getName());			
			}

			SystemAuditDataManager systemAuditDatamanager=getSystemAuditDataManager(session);
			if(systemAuditDatamanager==null){
				throw new DataManagerException("Data manager not found : "+systemAuditDatamanager);
			}

			session.beginTransaction();

			/*if(lstTrustedCertificateId!=null){
				for(int i=0 ;i <lstTrustedCertificateId.size() ; i++){
					if(lstTrustedCertificateId.get(i)!=null){
						Long trustedCertificateId=lstTrustedCertificateId.get(i);
						trustedCertificateDataManager.delete(trustedCertificateId);
						systemAuditDatamanager.updateTbltSystemAudit(staffData, actionAlias);
					}
				}
			}else{

			}*/
			if(lstTrustedCertificateId!=null && lstTrustedCertificateId.size() > 0){
				trustedCertificateDataManager.deleteAll(lstTrustedCertificateId);
				systemAuditDatamanager.updateTbltSystemAudit(staffData, actionAlias);
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

	public TrustedCertificateData view(Long trustedCertificateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         

		try{
			TrustedCertificateDataManager trustedCertificateDataManager = getTrustedCertificateDataManager(session);
			if(trustedCertificateDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			session.beginTransaction();
			TrustedCertificateData trustedCertificateData = trustedCertificateDataManager.getTrustedCertificateDataByTrustedCertificateId(trustedCertificateId);
			session.commit();
			return trustedCertificateData;
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
	
	public TrustedCertificateDataManager getTrustedCertificateDataManager(IDataManagerSession session)throws Exception{
		TrustedCertificateDataManager trustedCertificateDataManager=(TrustedCertificateDataManager)DataManagerFactory.getInstance().getDataManager(TrustedCertificateDataManager.class, session);
		return trustedCertificateDataManager;
	}	
}