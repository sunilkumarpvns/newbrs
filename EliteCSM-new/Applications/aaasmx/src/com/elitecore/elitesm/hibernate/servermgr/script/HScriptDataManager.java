package com.elitecore.elitesm.hibernate.servermgr.script;

import java.util.ArrayList;
import java.util.List;

import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.servermanager.script.ScriptDataManager;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.script.data.ScriptTypeData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.ws.logger.Logger;
public class HScriptDataManager extends HBaseDataManager implements ScriptDataManager{

	private static final String SCRIPT_ID = "scriptId";
	private static final String SCRIPT_INSTANCE_NAME = "name";
	private static final String SCRIPT_INSTANCE_ID = SCRIPT_ID;
	private static final String SCRIPT_TYPE_ID = "scriptTypeId";
	private static final String MODULE = "Script";

	@Override
	public String create(Object obj) throws DataManagerException {
		ScriptInstanceData scriptInstData = (ScriptInstanceData) obj;
		try{						
			Session session = getSession();
			session.clear();
			String auditId= UUIDGenerator.generate();
			
			scriptInstData.setAuditUId(auditId);
			
			session.save(scriptInstData);		
			
			String scriptId = scriptInstData.getScriptId();

			List<ScriptData> scriptDataList = scriptInstData.getScriptDataList();
			int orderNumber = 1;
			if(Collectionz.isNullOrEmpty(scriptDataList) == false ){
				for (ScriptData scriptData :  scriptDataList ){
					scriptData.setScriptId(scriptId);
					scriptData.setOrderNumber(orderNumber);
					session.save(scriptData);
					orderNumber++;
				}
			}
			
			session.flush();
			session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+ scriptInstData.getName() +REASON
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve.getCause());
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+ scriptInstData.getName() +REASON+ hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+ scriptInstData.getName() +REASON+ exp.getMessage(),exp);
		}
		return scriptInstData.getName();
	}
	@Override
	public List<ScriptInstanceData> getListOfScriptInstData(List<String> scriptInstanceIdList) throws DataManagerException {

		try{
			List<ScriptInstanceData> scriptInstanceDataList = new ArrayList<ScriptInstanceData>();
			
			Session session = getSession();
			Criteria criteria = null;
			String scriptInstanceId;
			
			if( Collectionz.isNullOrEmpty(scriptInstanceIdList) == false){
				int size = scriptInstanceIdList.size();
				for(int i=0;i<size;i++){
					scriptInstanceId = scriptInstanceIdList.get(i);
					criteria = session.createCriteria(ScriptInstanceData.class);
					ScriptInstanceData tempData = (ScriptInstanceData)criteria.add(Restrictions.eq(SCRIPT_INSTANCE_ID, scriptInstanceId)).uniqueResult();
					scriptInstanceDataList.add(tempData);
				}		
			}else{
				criteria = session.createCriteria(ScriptInstanceData.class);
				scriptInstanceDataList = criteria.list();
			}

			return scriptInstanceDataList;
		}catch(ConstraintViolationException cve){
			throw new ConstraintViolationException(cve.getMessage(),cve.getSQLException() ,cve.getConstraintName());
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	@Override
	public PageList search(ScriptInstanceData scriptInstanceData, int requiredPageNo, Integer pageSize) throws DataManagerException {

			List<ScriptInstanceData> scriptList = null;
			PageList pageList = null;
			try{		
				Session session = getSession();
				Criteria criteria = session.createCriteria(ScriptInstanceData.class);
				String name=scriptInstanceData.getName();
				String selectedScript=scriptInstanceData.getScriptTypeId();

				if((Strings.isNullOrBlank(name ) == false)){
					name = "%"+name+"%";
					criteria.add(Restrictions.ilike("name",name));
				}

				if(Strings.isNullOrBlank(selectedScript) == false){
					criteria.add(Restrictions.eq("scriptTypeId",selectedScript));
				}
				int totalItems =  criteria.list().size();
				criteria.setFirstResult(((requiredPageNo-1) * pageSize));

				if (pageSize > 0 ){
					criteria.setMaxResults(pageSize);
				}

				scriptList = criteria.list();

				long totalPages = (long)Math.ceil(totalItems/pageSize);
				if(totalItems%pageSize == 0)
					totalPages-=1;

				int size = scriptList.size();
				for(int i =0;i<size;i++){

					ScriptInstanceData tempData = scriptList.get(i);
					tempData.setScriptTypeName(tempData.getScriptTypeData().getName());

				}
				pageList = new PageList(scriptList, requiredPageNo, totalPages ,totalItems);

			}catch(HibernateException hbe){
				throw new DataManagerException(hbe.getMessage(),hbe);
			}catch(Exception e){
				Logger.logTrace(MODULE, e);
				throw new DataManagerException(e.getMessage(),e);
			}
			return pageList;
	}
	@Override
	public List<ScriptTypeData> getListOfAllScriptTypesData() throws DataManagerException {

			try{	
				Session session = getSession();
				Criteria criteria = session.createCriteria(ScriptTypeData.class);
				List<ScriptTypeData> scriptServiceDataList = criteria.list();
				return scriptServiceDataList;			
			}catch(HibernateException hbe){
				throw new DataManagerException(hbe.getMessage(),hbe);
			}
	}
	
	@Override
	public ScriptTypeData getScriptTypeDataById(String scriptTypeId) throws DataManagerException {
		try{	
			Session session = getSession();
			Criteria criteria = session.createCriteria(ScriptTypeData.class).add(Restrictions.eq(SCRIPT_TYPE_ID,scriptTypeId));
			ScriptTypeData scriptServiceData = (ScriptTypeData) criteria.uniqueResult();
			return scriptServiceData;			
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}
	@Override
	public ScriptInstanceData getScriptInstanceByScriptId(String scriptInstanceId, boolean isfetchById) throws DataManagerException {

		try{
			Session session = getSession();
			Criteria criteria = null;
			
			criteria = session.createCriteria(ScriptInstanceData.class);
			ScriptInstanceData tempData = null;
			
			if( isfetchById ){
				tempData = (ScriptInstanceData)criteria.add(Restrictions.eq(SCRIPT_INSTANCE_ID, scriptInstanceId)).uniqueResult();
			}else{
				tempData = (ScriptInstanceData)criteria.add(Restrictions.eq(SCRIPT_INSTANCE_NAME, scriptInstanceId)).uniqueResult();
			}
			
			if( tempData == null){
				throw new InvalidValueException("Script Instance Data not found");
			}
			
			return tempData;
		}catch(ConstraintViolationException cve){
			throw new ConstraintViolationException(cve.getMessage(),cve.getSQLException() ,cve.getConstraintName());
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	@Override
	public List<ScriptData> getScriptDataByScriptInstanceId(String scriptId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = null;
			
			criteria = session.createCriteria(ScriptData.class);
			List<ScriptData> tempDataList = (List<ScriptData>)criteria.add(Restrictions.eq(SCRIPT_INSTANCE_ID, scriptId)).list();
			return tempDataList;
		}catch(ConstraintViolationException cve){
			throw new ConstraintViolationException(cve.getMessage(),cve.getSQLException() ,cve.getConstraintName());
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
	}
	
	@Override
	public void updateScriptById(ScriptInstanceData scriptInstanceData, IStaffData staffData ) throws DataManagerException {
		update(scriptInstanceData, staffData, SCRIPT_INSTANCE_ID, scriptInstanceData.getScriptId());
	}
	
	private void update(ScriptInstanceData scriptInstanceData, IStaffData staffData, String propertyName, Object value) throws DataManagerException{
		try{
			ScriptInstanceData scriptInstDBData = null;
			Session session = getSession();
			Criteria criteria = session.createCriteria(ScriptInstanceData.class);
			scriptInstDBData = (ScriptInstanceData)criteria.add(Restrictions.eq(propertyName,value)).uniqueResult();
			
			if(scriptInstDBData == null){
				throw new InvalidValueException("Script Instance not found");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(scriptInstDBData,scriptInstanceData);
			
			String scriptId = scriptInstDBData.getScriptId();

			criteria = session.createCriteria(ScriptData.class);

			List<ScriptData> scriptDataList = (List<ScriptData>) criteria.add(Restrictions.eq(SCRIPT_INSTANCE_ID, scriptId)).list();

			if (Collectionz.isNullOrEmpty(scriptDataList) == false) {
				for (ScriptData data : scriptDataList) {
					session.delete(data);
					session.flush();
				}
			}			
			
			List<ScriptData> dataList =  scriptInstanceData.getScriptDataList();
			
			int orderNumber = 1;
			
			if( Collectionz.isNullOrEmpty(dataList) == false ){
				for(ScriptData data : dataList) {
					data.setScriptId(scriptId);
					data.setOrderNumber(orderNumber);
					session.save(data);
					session.flush();
					orderNumber++;
				}
			}
			
			scriptInstDBData.setAuditUId(scriptInstanceData.getAuditUId());
			scriptInstDBData.setCreateDate(scriptInstanceData.getCreateDate());
			scriptInstDBData.setDescription(scriptInstanceData.getDescription());
			scriptInstDBData.setLastModifiedDate(scriptInstanceData.getLastModifiedDate());
			scriptInstDBData.setLastModifiedByStaffId(scriptInstanceData.getLastModifiedByStaffId());
			scriptInstDBData.setName(scriptInstanceData.getName());
			scriptInstDBData.setScriptDataList(scriptInstanceData.getScriptDataList());
			scriptInstDBData.setScriptTypeData(scriptInstanceData.getScriptTypeData());
			scriptInstDBData.setScriptTypeId(scriptInstanceData.getScriptTypeId());
			scriptInstDBData.setStatus(scriptInstanceData.getStatus());
			
			session.update(scriptInstDBData);
			
			staffData.setAuditId(scriptInstDBData.getAuditUId());
			staffData.setAuditName(scriptInstDBData.getName());
			
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_SCRIPT);
			session.flush();
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Script , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Script , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Script , Reason: " + exp.getMessage(), exp);
		}
	}
	@Override
	public void updateScriptByName(ScriptInstanceData scriptInstanceData,IStaffData staffData, String name) throws DataManagerException {
		update(scriptInstanceData, staffData, SCRIPT_INSTANCE_NAME, name);		
	}
	
	@Override
	public ScriptData getScriptFileByName(String scriptFileName, String scriptId) throws DataManagerException {
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(ScriptData.class);
			ScriptData scriptData = (ScriptData)criteria.add(Restrictions.eq(SCRIPT_ID,scriptId)).add(Restrictions.eq("scriptFileName",scriptFileName)).uniqueResult();
			if(scriptData == null){
				throw new DataManagerException("Script Data not found");
			}
			return scriptData;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Script File, Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public void updateScriptFile(ScriptData scriptDataNewObject, IStaffData staffData, ScriptInstanceData scriptInstanceData) throws DataManagerException {
	
	try{
		Session session = getSession();
		
		Criteria criteria = session.createCriteria(ScriptData.class);
		criteria = session.createCriteria(ScriptData.class);
		
		ScriptData scriptFileData = (ScriptData)criteria.add(Restrictions.eq("scriptDataId", scriptDataNewObject.getScriptDataId())).uniqueResult();
		
		if(scriptFileData == null){
			throw new InvalidValueException("Script File Data not found");
		}
		
		JSONArray jsonArray=ObjectDiffer.diff(scriptFileData,scriptDataNewObject);

		scriptFileData.setScriptFile(scriptDataNewObject.getScriptFile());
		scriptFileData.setLastUpdatedTime(scriptDataNewObject.getLastUpdatedTime());
		session.update(scriptFileData);
		session.flush();
		
		staffData.setAuditId(scriptInstanceData.getAuditUId());
		staffData.setAuditName(scriptInstanceData.getName());
		
		doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_SCRIPT);
		
	}catch (ConstraintViolationException cve) {
		cve.printStackTrace();
		throw new DataManagerException("Failed to update Script File, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
	}catch(HibernateException hExp){
		hExp.printStackTrace();
		throw new DataManagerException("Failed to update Script File, Reason: " + hExp.getMessage(), hExp);
	}catch(Exception exp){
		exp.printStackTrace();
		throw new DataManagerException("Failed to update Script File, Reason: " + exp.getMessage(), exp);
	}

}
	
	@Override
	public List<String> delete(List<String> scriptList) throws DataManagerException {
		String scriptName = "Script Instance";
		
		try{
			
			List<String> deletedScriptLst = new ArrayList<String>();
			Session session = getSession();
			
			MultiIdentifierLoadAccess<ScriptInstanceData> multiLoadAccess = session.byMultipleIds(ScriptInstanceData.class);
			List<ScriptInstanceData> scriptInstDataLst = multiLoadAccess.multiLoad(scriptList);
			
			if(Collectionz.isNullOrEmpty(scriptInstDataLst) == false){
				for(ScriptInstanceData scriptInstance : scriptInstDataLst){
					if(scriptInstance != null){
						scriptName = scriptInstance.getName();
						session.delete(scriptInstance);
						session.flush();
						deletedScriptLst.add(scriptName);
					}
				}
			}
		
			return deletedScriptLst;
		}catch(ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete "+ scriptName +", Reason: " +EliteExceptionUtils.extractConstraintName(cve.getSQLException()),cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to delete "+ scriptName +", Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to delete "+ scriptName +", Reason: "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateStatus(List<String> scriptInstanceIds, String status) throws DataManagerException {
		String scriptInstanceId = null;
		Session session = getSession();
		Criteria criteria = null;

		for(int i=0;i<scriptInstanceIds.size();i++){
			scriptInstanceId = scriptInstanceIds.get(i);
			criteria = session.createCriteria(ScriptInstanceData.class);
			ScriptInstanceData scriptInstData = (ScriptInstanceData)criteria.add(Restrictions.eq(SCRIPT_INSTANCE_ID,scriptInstanceId)).uniqueResult();
			if(status.equals(BaseConstant.SHOW_STATUS_ID)){
				if(scriptInstData.getStatus().equals(BaseConstant.HIDE_STATUS_ID)){
					Criteria newCriteria = session.createCriteria(ScriptInstanceData.class); 
					newCriteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID)); 					
					List scriptInstanceDataList = newCriteria.list();
					if(Collectionz.isNullOrEmpty(scriptInstanceDataList) == false){
						criteria = session.createCriteria(ScriptInstanceData.class);
						criteria.add(Restrictions.eq("status",BaseConstant.SHOW_STATUS_ID));
					}
				}				
			}
			scriptInstData.setStatus(status);			
			session.update(scriptInstData);			
			session.flush();
		}
	}
	
	@Override
	public List<ScriptInstanceData> getScriptInstanceDataByTypeId(String scriptTypeId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = null;
			
			criteria = session.createCriteria(ScriptInstanceData.class);
			List<ScriptInstanceData> scriptInstanceDataList = criteria.add(Restrictions.eq(SCRIPT_TYPE_ID, scriptTypeId)).list();
			return scriptInstanceDataList;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(), hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(e.getMessage(), e);
		}
	}
	
	@Override
	public void updateScriptBasicDetails(ScriptInstanceData scriptInstanceData, IStaffData staffData, String name) throws DataManagerException {
		try{
			ScriptInstanceData scriptInstDBData = null;
			Session session = getSession();
			Criteria criteria = session.createCriteria(ScriptInstanceData.class);
			scriptInstDBData = (ScriptInstanceData)criteria.add(Restrictions.eq(SCRIPT_INSTANCE_NAME,name)).uniqueResult();
			
			if(scriptInstDBData == null){
				throw new InvalidValueException("Script Instance not found");
			}
			
			JSONArray jsonArray=ObjectDiffer.diff(scriptInstDBData, scriptInstanceData);
			
			String scriptId = scriptInstDBData.getScriptId();

			criteria = session.createCriteria(ScriptData.class);
			
			scriptInstDBData.setDescription(scriptInstanceData.getDescription());
			scriptInstDBData.setLastModifiedDate(scriptInstanceData.getLastModifiedDate());
			scriptInstDBData.setLastModifiedByStaffId(scriptInstanceData.getLastModifiedByStaffId());
			scriptInstDBData.setName(scriptInstanceData.getName());
			scriptInstDBData.setScriptTypeId(scriptInstanceData.getScriptTypeId());
			scriptInstDBData.setStatus(scriptInstanceData.getStatus());
			
			session.update(scriptInstDBData);
			
			staffData.setAuditId(scriptInstDBData.getAuditUId());
			staffData.setAuditName(scriptInstDBData.getName());
			
			doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_SCRIPT);
			session.flush();
			
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Script , Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update Script , Reason: " + hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update Script , Reason: " + exp.getMessage(), exp);
		}
	}
	
	@Override
	public String deleteByName(String scriptName) throws DataManagerException {
        try{
            Session session = getSession();
            Criteria criteria = session.createCriteria(ScriptInstanceData.class);
        	criteria.add(Restrictions.eq(SCRIPT_INSTANCE_NAME, scriptName));
        	
        	ScriptInstanceData scriptInstanceData = (ScriptInstanceData) criteria.uniqueResult();
            if(scriptInstanceData == null){
            	throw new InvalidValueException("Script Instance Data not found");
            }
            
            session.delete(scriptInstanceData);
            return scriptInstanceData.getName();
    	} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete " + scriptName + ", Reason: " 
					+ EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hExp) {
			hExp.printStackTrace();
            throw new DataManagerException("Failed to delete "+ scriptName +", Reason: "+hExp.getMessage(), hExp);
        } catch(Exception exp) {
        	exp.printStackTrace();
        	throw new DataManagerException("Failed to delete "+ scriptName +", Reason: "+exp.getMessage(), exp);
        }
	}
	
}