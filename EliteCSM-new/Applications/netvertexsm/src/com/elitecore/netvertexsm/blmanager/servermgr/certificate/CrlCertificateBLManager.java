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
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.CrlCertificateDataManager;
import com.elitecore.netvertexsm.datamanager.servermgr.certificate.data.CrlCertificateData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;

public class CrlCertificateBLManager extends BaseBLManager{

	public PageList search(CrlCertificateData crlCertificateData, Map infoMap) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();

		try{
			CrlCertificateDataManager crlCertificateDataManager=getCrlCertificateDataManager(session);

			if(crlCertificateDataManager==null){
				throw new DataManagerException ("Data Manager implementation not found for "+getClass().getName());
			}

			session.beginTransaction();
			PageList lstCrlCertificateList=crlCertificateDataManager.search(crlCertificateData, infoMap);
			session.commit();
			return lstCrlCertificateList;

		}catch(Exception e){
			session.rollback();
			throw new DataManagerException("Action Failed : "+e.getMessage());
		}finally{
			session.close();
		}
	}

	public void create(CrlCertificateData crlCertificateDatas,IStaffData staffData, String actionAlias) throws DataManagerException {

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			CrlCertificateDataManager crlCertificateDataManager=getCrlCertificateDataManager(session);
			if(crlCertificateDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			}

			SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);
			if(systemAuditDataManager==null){
				throw new DataManagerException("Data Manager implementation not found for SystemAuditDataManager");
			}

			session.beginTransaction();

			crlCertificateDataManager.create(crlCertificateDatas);
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);

			session.commit();
		}catch(Exception e){
			session.rollback();
		}finally{
			session.close();
		}
	}

	public CrlCertificateData initUpdate(Long crlCertificateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			CrlCertificateDataManager crlCertificateDataManager = getCrlCertificateDataManager(session);

			if(crlCertificateDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());
			session.beginTransaction();
			CrlCertificateData crlCertificateData = crlCertificateDataManager.getCrlCertificateDataByCrlCertificateId(crlCertificateId);
			session.commit();
			return crlCertificateData;
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

	public void update(CrlCertificateData crlCertificateDatas, IStaffData staffData, String actionAlias) throws DataManagerException{

		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{			

			CrlCertificateDataManager crlCertificateDataManager=getCrlCertificateDataManager(session);
			if(crlCertificateDataManager==null){
				throw new DataManagerException("Data manager implementation not found for "+getClass().getName());				
			}

			SystemAuditDataManager systemAuditDataManager=getSystemAuditDataManager(session);
			if(systemAuditDataManager==null){
				throw new DataManagerException("Data manager not found : "+systemAuditDataManager);
			}

			session.beginTransaction();

			crlCertificateDataManager.update(crlCertificateDatas);
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

	public void delete(List<Long> lstCrlCertificateId, IStaffData staffData, String actionAlias)throws DataManagerException{
		IDataManagerSession session=DataManagerSessionFactory.getInstance().getDataManagerSession();
		try{
			CrlCertificateDataManager crlCertificateDataManager=getCrlCertificateDataManager(session);
			if(crlCertificateDataManager==null){
				throw new DataManagerException("Data Managerimplementation not found for "+getClass().getName());			
			}

			SystemAuditDataManager systemAuditDatamanager=getSystemAuditDataManager(session);
			if(systemAuditDatamanager==null){
				throw new DataManagerException("Data manager not found : "+systemAuditDatamanager);
			}

			session.beginTransaction();

			if(lstCrlCertificateId!=null && lstCrlCertificateId.size() > 0){
				crlCertificateDataManager.deleteAll(lstCrlCertificateId);
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

	public CrlCertificateData view(Long crlCertificateId) throws DataManagerException {
		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();         

		try{
			CrlCertificateDataManager crlCertificateDataManager = getCrlCertificateDataManager(session);
			if(crlCertificateDataManager == null)
				throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

			session.beginTransaction();
			CrlCertificateData crlCertificateData = crlCertificateDataManager.getCrlCertificateDataByCrlCertificateId(crlCertificateId);
			session.commit();
			return crlCertificateData;
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
	
	public CrlCertificateDataManager getCrlCertificateDataManager(IDataManagerSession session)throws Exception{
		CrlCertificateDataManager crlCertificateDataManager=(CrlCertificateDataManager)DataManagerFactory.getInstance().getDataManager(CrlCertificateDataManager.class, session);
		return crlCertificateDataManager;
	}	
}