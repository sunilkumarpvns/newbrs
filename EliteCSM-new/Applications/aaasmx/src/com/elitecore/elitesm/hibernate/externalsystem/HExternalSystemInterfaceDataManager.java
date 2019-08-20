package com.elitecore.elitesm.hibernate.externalsystem;

import java.io.StringReader;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.ActivePassiveCommunicatorData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.CommunicatorData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import com.elitecore.elitesm.web.radius.radiusesigroup.RedundancyMode;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.constraintviolation.DuplicateInstanceNameFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.externalsystem.ExternalSystemInterfaceDataManager;
import com.elitecore.elitesm.datamanager.externalsystem.data.ESITypeAndInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.externalsystem.data.ExternalSystemInterfaceTypeData;
import com.elitecore.elitesm.datamanager.externalsystem.data.IExternalSystemInterfaceInstanceData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.constants.ExternalSystemConstants;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.servicepolicy.diameter.tgppaaapolicy.TGPPAAAPolicyConstant;

import net.sf.json.JSONArray;
public class HExternalSystemInterfaceDataManager extends HBaseDataManager implements ExternalSystemInterfaceDataManager{

	private static final String ESI_INSTANCE_ID = "esiInstanceId";
	private static final String ESI_NAME = "name";
	private static final String MODULE = "HExternalSystemInterfaceDataManager";

	public List<ExternalSystemInterfaceTypeData> getListOfESITypes() throws DataManagerException {
			
		List<ExternalSystemInterfaceTypeData> eSITypeList = null;
		try{
			String status = "Y";
			Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceTypeData.class);
			eSITypeList = criteria.add(Restrictions.eq("status",status)).list();
			
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to search list of ESI type: Reason: "+hbe.getMessage(),hbe);
		}		
		return eSITypeList;
	}
	
	@Override
	public String create(Object obj) throws DataManagerException{
		IExternalSystemInterfaceInstanceData esiInstanceData = (IExternalSystemInterfaceInstanceData) obj;
		try{
			Session session = getSession();
			session.clear();
			String auditId= UUIDGenerator.generate();
			
			esiInstanceData.setAuditUId(auditId);
			
			session.save(esiInstanceData);
			session.flush();
			session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE +esiInstanceData.getName()+ REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE +esiInstanceData.getName()+ REASON +hbe.getMessage(), hbe);
		}
		return esiInstanceData.getName();
	}

	public List getESIInstanceAndType(String name, long esiTypeId) throws DataManagerException {
		
		try{
			Session session = getSession();
			Criteria criteria ;
			List<ESITypeAndInstanceData> finalResultList = new ArrayList();
			ESITypeAndInstanceData esiTypeAndInstanceData;
			List<ExternalSystemInterfaceInstanceData> initialList = null;
		
			if((name == null || name.equals("")) && esiTypeId == 0){
			
				criteria= session.createCriteria(ExternalSystemInterfaceInstanceData.class);
				initialList = criteria.list();
			
			}else if((name == null || name.equals("")) && esiTypeId != 0){
					// retrieve on basis of type id. 
			   criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			   initialList = criteria.add(Restrictions.eq("esiTypeId",esiTypeId)).list();
			   
			}else if(esiTypeId == 0 && (name!= null || !name.equals(""))){
					//retrieve on basis of name 
				 criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
				 initialList = criteria.add(Restrictions.eq(ESI_NAME,name)).list();
				
			}else if((name!= null || name.equals("")) && esiTypeId != 0){
			
					// retrieve on basis of both ..
				 criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
				 initialList = criteria.add(Restrictions.eq(ESI_NAME,name)).add(Restrictions.eq("esiTypeId", esiTypeId)).list();
				
			}		
			
			for(int i=0; i<initialList.size();i++){
				ExternalSystemInterfaceInstanceData tempData = initialList.get(i);
				criteria = session.createCriteria(ExternalSystemInterfaceTypeData.class);
				ExternalSystemInterfaceTypeData typeData = (ExternalSystemInterfaceTypeData) criteria.add(Restrictions.eq("esiTypeId",tempData.getEsiTypeId())).uniqueResult();
				
				esiTypeAndInstanceData = new ESITypeAndInstanceData();
				esiTypeAndInstanceData.setName(tempData.getName());
				esiTypeAndInstanceData.setEsiTypeId(tempData.getEsiTypeId());
				esiTypeAndInstanceData.setEsiTypeName(tempData.getName());
				esiTypeAndInstanceData.setAddress(tempData.getAddress());
				
				esiTypeAndInstanceData.setMinLocalPort(tempData.getMinLocalPort());
				esiTypeAndInstanceData.setTimeout(tempData.getTimeout());
				esiTypeAndInstanceData.setExpiredRequestLimitCount(tempData.getExpiredRequestLimitCount());
				
				esiTypeAndInstanceData.setEsiTypeName(typeData.getName());
				esiTypeAndInstanceData.setEsiInstanceId(tempData.getEsiInstanceId());
				finalResultList.add(esiTypeAndInstanceData);
			}
			return finalResultList;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive list of ESI instace and type details, Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive list of ESI instace and type details, Reason: "+e.getMessage(),e);
	  }
	}

	public ESITypeAndInstanceData getESIInstanceDetail(String esiInstanceId) {
		
		try{
			
			Session session = getSession();
			ESITypeAndInstanceData esiTypeInstanceData = new ESITypeAndInstanceData();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			ExternalSystemInterfaceInstanceData esiData = (ExternalSystemInterfaceInstanceData)criteria.add(Restrictions.eq(ESI_INSTANCE_ID, esiInstanceId)).uniqueResult();
			
			esiTypeInstanceData.setDescription(esiData.getDescription());
			esiTypeInstanceData.setEsiInstanceId(esiData.getEsiInstanceId());
			esiTypeInstanceData.setEsiTypeId(esiData.getEsiTypeId());
			esiTypeInstanceData.setRealmNames(esiData.getRealmNames());
			esiTypeInstanceData.setAddress(esiData.getAddress());
			esiTypeInstanceData.setName(esiData.getName());
			esiTypeInstanceData.setTimeout(esiData.getTimeout());
			esiTypeInstanceData.setMinLocalPort(esiData.getMinLocalPort());
			esiTypeInstanceData.setExpiredRequestLimitCount(esiData.getExpiredRequestLimitCount());
			esiTypeInstanceData.setSharedSecret(esiData.getSharedSecret());
			esiTypeInstanceData.setRetryLimit(esiData.getRetryLimit());
			esiTypeInstanceData.setStatusCheckDuration(esiData.getStatusCheckDuration());
			esiTypeInstanceData.setSupportedAttribute(esiData.getSupportedAttribute());
			esiTypeInstanceData.setUnSupportedAttribute(esiData.getUnSupportedAttribute());
			esiTypeInstanceData.setStatusCheckMethod(esiData.getStatusCheckMethod());
			esiTypeInstanceData.setPacketBytes(esiData.getPacketBytes());
			esiTypeInstanceData.setAuditUId(esiData.getAuditUId());
			
			criteria = session.createCriteria(ExternalSystemInterfaceTypeData.class);		
			ExternalSystemInterfaceTypeData esiTypeData = (ExternalSystemInterfaceTypeData)criteria.add(Restrictions.eq("esiTypeId",esiData.getEsiTypeId())).uniqueResult();
			esiTypeInstanceData.setEsiTypeName(esiTypeData.getName());
			
			return esiTypeInstanceData;
			
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new HibernateException("Failed to search ESI instance type data, Reason: "+hbe.getMessage(),hbe);
		}
	}
	
	@Override
	public void updateByInstanceName(ExternalSystemInterfaceInstanceData esiInstanceData,IStaffData staffData, String esiName)
			throws DataManagerException{
		update(esiInstanceData, staffData, ESI_NAME, esiName);
	}
	
	@Override
	public void updateByInstanceId(ExternalSystemInterfaceInstanceData esiInstanceData,IStaffData staffData,String esiInstanceId) throws DataManagerException{
		update(esiInstanceData, staffData, ESI_INSTANCE_ID, esiInstanceId);
	}
	
	private void update(ExternalSystemInterfaceInstanceData esiInstanceData,IStaffData staffData,String propertyName,Object value) throws DataManagerException{
		try{			
			Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			ExternalSystemInterfaceInstanceData tempData = (ExternalSystemInterfaceInstanceData)criteria.add(Restrictions.eq(propertyName,value)).uniqueResult();
			
			if(tempData == null){
				throw new InvalidValueException("External System Interface not found");
			}
			
			if(tempData.getEsiTypeId() != esiInstanceData.getEsiTypeId()){
				throw new DataValidationException("ESI Type is different");
			}
			
			JSONArray jsonArray = ObjectDiffer.diff(tempData,esiInstanceData);    
			
			tempData.setName(esiInstanceData.getName());
			tempData.setDescription(esiInstanceData.getDescription());
			tempData.setAddress(esiInstanceData.getAddress());
			tempData.setRealmNames(esiInstanceData.getRealmNames());
			tempData.setSharedSecret(esiInstanceData.getSharedSecret());
			tempData.setTimeout(esiInstanceData.getTimeout());
			tempData.setMinLocalPort(esiInstanceData.getMinLocalPort());
			tempData.setExpiredRequestLimitCount(esiInstanceData.getExpiredRequestLimitCount());
			tempData.setRetryLimit(esiInstanceData.getRetryLimit());
			tempData.setStatusCheckDuration(esiInstanceData.getStatusCheckDuration());
			tempData.setSupportedAttribute(esiInstanceData.getSupportedAttribute());
			tempData.setUnSupportedAttribute(esiInstanceData.getUnSupportedAttribute());
			tempData.setStatusCheckMethod(esiInstanceData.getStatusCheckMethod());
			tempData.setPacketBytes(esiInstanceData.getPacketBytes());
			tempData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			tempData.setLastModifiedByStaffId(staffData.getStaffId());
			
			session.update(tempData);
			session.flush();
			
			//Audit Paramters
			staffData.setAuditName(esiInstanceData.getName());
			staffData.setAuditId(tempData.getAuditUId());
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_EXTERNAL_SYSTEM);
			
		}catch(DataManagerException de){
			de.printStackTrace();
			throw new DataManagerException("Failed to update External System Interface, Reason: " + de.getMessage() , de);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update External System Interface, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update External System Interface, Reason: "+hbe.getMessage(),hbe);
		}
	}

	public List<ExternalSystemInterfaceInstanceData> getExternalSystemInstanceDataList(long esiTypeId) throws DataManagerException {
		List<ExternalSystemInterfaceInstanceData> externalSystemInstList;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			externalSystemInstList= criteria.add(Restrictions.eq("esiTypeId", esiTypeId)).list();
			session.flush();
			session.clear();
			return externalSystemInstList;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive list of External System Interface, Reason: "+hbe.getMessage(),hbe);
		}
	}
	
	public PageList search(ESITypeAndInstanceData esiInstanceData ,int pageNo, int pageSize) throws DataManagerException {

		Session session = getSession();
		Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
		PageList pageList = null;
		List<ESITypeAndInstanceData> finalResultList = new ArrayList();
		ESITypeAndInstanceData esiTypeAndInstanceData;
		
		try{
            
            if((esiInstanceData.getName() != null && esiInstanceData.getName().length()>0 )){
            	criteria.add(Restrictions.ilike(ESI_NAME,"%"+esiInstanceData.getName()+"%"));
            }

            if(esiInstanceData.getEsiTypeId() != 0){
            	criteria.add(Restrictions.eq("esiTypeId",esiInstanceData.getEsiTypeId()));
            }
            
			int totalItems = criteria.list().size();
			criteria.setFirstResult(((pageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			List policyList = criteria.list();
			
			for(int i=0; i<policyList.size();i++){
				ExternalSystemInterfaceInstanceData tempData = (ExternalSystemInterfaceInstanceData)policyList.get(i);
				criteria = session.createCriteria(ExternalSystemInterfaceTypeData.class);
				ExternalSystemInterfaceTypeData typeData = (ExternalSystemInterfaceTypeData) criteria.add(Restrictions.eq("esiTypeId",tempData.getEsiTypeId())).uniqueResult();
				
				esiTypeAndInstanceData = new ESITypeAndInstanceData();
				esiTypeAndInstanceData.setName(tempData.getName());
				esiTypeAndInstanceData.setEsiTypeId(tempData.getEsiTypeId());
				esiTypeAndInstanceData.setEsiTypeName(tempData.getName());
				esiTypeAndInstanceData.setAddress(tempData.getAddress());
				
				esiTypeAndInstanceData.setMinLocalPort(tempData.getMinLocalPort());
				esiTypeAndInstanceData.setTimeout(tempData.getTimeout());
				esiTypeAndInstanceData.setExpiredRequestLimitCount(tempData.getExpiredRequestLimitCount());
				
				esiTypeAndInstanceData.setEsiTypeName(typeData.getName());
				esiTypeAndInstanceData.setEsiInstanceId(tempData.getEsiInstanceId());
				finalResultList.add(esiTypeAndInstanceData);
			}
			
			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			pageList = new PageList(finalResultList, pageNo, totalPages ,totalItems);
			return  pageList;

		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to search list of ESI instace, Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to search list of ESI instace, Reason: "+e.getMessage(),e);
		}
	}
	
	@Override
	public List<ExternalSystemInterfaceInstanceData> getExternalSystemInterfaceInstanceDataList()throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			PageList pageList = null;
			List<ExternalSystemInterfaceInstanceData> policyList = criteria.list();
			session.flush();
			session.clear();
			return policyList;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive External System Interface List, Reason: "+hbe.getMessage(),hbe);
		}
	}

	@Override
	public List<ExternalSystemInterfaceInstanceData> getAcctFlowExternalSystemInstanceDataList(long authProxy) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class).add(Restrictions.ne("esiTypeId",authProxy));
			PageList pageList = null;
			List<ExternalSystemInterfaceInstanceData> policyList = criteria.list();
			session.flush();
			session.clear();
			return policyList;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive  Acct Flow External System Interface List, Reason: "+hbe.getMessage(),hbe);
		}
	}

	@Override
	public List<ExternalSystemInterfaceInstanceData> getRadiusTypeExternalInterfaceInstanceDataList() throws DataManagerException {
		try{
			Session session = getSession();
		
			Disjunction disjunction = Restrictions.disjunction();
			disjunction.add(Restrictions.eq("esiTypeId", TGPPAAAPolicyConstant.RADAUTH_ESI));
			disjunction.add(Restrictions.eq("esiTypeId", TGPPAAAPolicyConstant.RADACCT_ESI));
			disjunction.add(Restrictions.eq("esiTypeId", TGPPAAAPolicyConstant.IP_POOL_SERVER_ESI));
			disjunction.add(Restrictions.eq("esiTypeId", TGPPAAAPolicyConstant.CHARGING_GATEWAY_ESI));
			disjunction.add(Restrictions.eq("esiTypeId", TGPPAAAPolicyConstant.NAS_ESI));
			
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class).add(disjunction);
			
			List<ExternalSystemInterfaceInstanceData> policyList = criteria.list();
			session.flush();
			session.clear();
			return policyList;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Radius type External System Interface List, Reason: "+hbe.getMessage(),hbe);
		}
	}
	
	@Override
	public ExternalSystemInterfaceInstanceData getExternalSystemInterfaceInstanceDataById(String esiInstanceId) throws DataManagerException {
		return getExternalSystemInterfaceInstanceData(ESI_INSTANCE_ID, esiInstanceId);
	}
	
	@Override
	public ExternalSystemInterfaceInstanceData getExternalSystemInterfaceInstanceDataByName(String esiInstanceName) throws DataManagerException {
		return getExternalSystemInterfaceInstanceData(ESI_NAME, esiInstanceName);
	}
	
	private ExternalSystemInterfaceInstanceData getExternalSystemInterfaceInstanceData(String esiProperty,Object value) throws DataManagerException{
		ExternalSystemInterfaceInstanceData esiInstanceData;
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			esiInstanceData = (ExternalSystemInterfaceInstanceData) criteria.add(Restrictions.eq(esiProperty,value)).uniqueResult();
			
			if (esiInstanceData == null){
				throw new InvalidValueException("External System Interface data not found");
			}
			
			return esiInstanceData;
		}catch(DataManagerException de){
			de.printStackTrace();
			throw new DataManagerException("Failed to retrive External System Interface, Reason: "+de.getMessage(), de);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive External System Interface, Reason: "+hbe.getMessage(),hbe);
		}
	}
	
	@Override
	public String deleteByInstaceId(String esiInstanceId) throws DataManagerException {
		return delete(ESI_INSTANCE_ID, esiInstanceId);
	}

	@Override
	public String deleteByInstaceName(String esiInstanceName)throws DataManagerException {
		return delete(ESI_NAME, esiInstanceName);
	}

	private String delete(String propertyName, Object value) throws DataManagerException{
		Session session = getSession();
		String esiInstanceName = (ESI_NAME.equals(propertyName)) ? (String) value : "External System Interface";
		try{
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			ExternalSystemInterfaceInstanceData esiInstanceData = (ExternalSystemInterfaceInstanceData)criteria.add(Restrictions.eq(propertyName,value)).uniqueResult();
			
			if (esiInstanceData == null){
				throw new InvalidValueException("External System Interface not found");
			}

			esiInstanceName = esiInstanceData.getName();

			Criteria radEsiData = session.createCriteria(RadiusESIGroupData.class);
			List<RadiusESIGroupData> radEsiDataList = radEsiData.list();

			//Validate If Esi is Binded in Radius Esi Group
			verifyRadiusEsiIsBinded(radEsiDataList,esiInstanceName);

			session.delete(esiInstanceData);
			session.flush();
			
			return esiInstanceName;
		} catch (ConstraintViolationException ce) {
			ce.printStackTrace();
			throw new DataManagerException("Failed to delete "+esiInstanceName+", Reason: " + EliteExceptionUtils.extractConstraintName(ce.getSQLException()), ce);
		} catch (HibernateException hbe) {
			hbe.printStackTrace();
			throw new DataManagerException("Failed to delete "+ esiInstanceName +", Reason: "+hbe.getMessage(), hbe);
		}catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete "+ esiInstanceName +", Reason: "+exp.getMessage(), exp);
		}
	}

	@Override
	public String getRadiusESIGroupNameById(String esiId)throws DataManagerException {
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			ExternalSystemInterfaceInstanceData externalSystemInterfaceInstanceData = (ExternalSystemInterfaceInstanceData)criteria.add(Restrictions.eq("esiInstanceId",esiId)).uniqueResult();
			
			if(externalSystemInterfaceInstanceData != null){
				return externalSystemInterfaceInstanceData.getName();
			}
			else{
				return "";
			}
			
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Diameter Peer name by Id, Reason"+e.getMessage(),e);
		}		
	}

	@Override
	public String getESIInstanceIdByESITypeId(Long esiTypeId) throws DataManagerException {
		String esiInstanceId = "";
		try{
			org.hibernate.Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			esiInstanceId = (String)criteria.add(Restrictions.eq("esiTypeId",esiTypeId)).uniqueResult();
			
			return esiInstanceId;
		}catch(HibernateException he){
			he.printStackTrace();
			throw new DataManagerException(he.getMessage(),he);
		}
	}

	@Override
	public Long getESITypeIdFromName(String esiName)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceTypeData.class);
			ExternalSystemInterfaceTypeData externalSystemInterfaceInstanceTypeData = (ExternalSystemInterfaceTypeData)criteria.add(Restrictions.eq("displayName",esiName.trim())).uniqueResult();
			
			if(externalSystemInterfaceInstanceTypeData == null){
				throw new InvalidValueException("No matching ESI Type found for: "+esiName);
			}
			
			return externalSystemInterfaceInstanceTypeData.getEsiTypeId();
		}catch(DataManagerException de){
			de.printStackTrace();
			throw new DataManagerException("Failed to retrive ESI Type, Reason:"+de.getMessage(), de);
		}catch(HibernateException he){
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive ESI Type, Reason:"+he.getMessage(), he);
		}
	}
	
	@Override
	public String getESITypeNameFromId(Long esiTypeId)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceTypeData.class);
			ExternalSystemInterfaceTypeData externalSystemInterfaceInstanceTypeData = (ExternalSystemInterfaceTypeData)criteria.add(Restrictions.eq("esiTypeId",esiTypeId)).uniqueResult();
			
			if(externalSystemInterfaceInstanceTypeData == null){
				throw new InvalidValueException("No matching ESI Type found");
			}
			
			return externalSystemInterfaceInstanceTypeData.getDisplayName();
		}catch(DataManagerException de){
			de.printStackTrace();
			throw new DataManagerException("Failed to retrive ESI Type, Reason: "+de.getMessage(), de);
		}catch(HibernateException he){
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive ESI Type, Reason: "+he.getMessage(), he);
		}
	}

	@Override
	public String getRadiusESIIdByName(String radiusESIName) throws DataManagerException {

		try {

			Session session = getSession();

			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class).add(Restrictions.eq("name", radiusESIName));
			ExternalSystemInterfaceInstanceData externalSystemInterfaceInstanceData = (ExternalSystemInterfaceInstanceData) criteria.uniqueResult();

			if (externalSystemInterfaceInstanceData == null) {
				throw new InvalidValueException("External System Interface not found");
			}

			return externalSystemInterfaceInstanceData.getEsiInstanceId();

		} catch (HibernateException he) {
			he.printStackTrace();
			throw new DataManagerException("Failed to retrive ESI " + radiusESIName + ", Reason: " + he.getMessage(), he);
		} catch (Exception e) {
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive ESI " + radiusESIName + ", Reason: " + e.getMessage(), e);
		}
	}

	@Override
	public ExternalSystemInterfaceInstanceData getAcctExternalSystemInterfaceInstanceData(String esiName) throws DataManagerException {
		ExternalSystemInterfaceInstanceData esiInstanceData;
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ExternalSystemInterfaceInstanceData.class);
			esiInstanceData = (ExternalSystemInterfaceInstanceData) criteria.add(Restrictions.eq("name",esiName)).add(Restrictions.ne("esiTypeId", ExternalSystemConstants.AUTH_PROXY)).uniqueResult();
			
			if (esiInstanceData == null){
				throw new InvalidValueException("External System Interface data not found");
			}
			
			return esiInstanceData;
		}catch(DataManagerException de){
			de.printStackTrace();
			throw new DataManagerException("Failed to retrive External System Interface, Reason: "+de.getMessage(), de);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive External System Interface, Reason: "+hbe.getMessage(),hbe);
		}
	}
	
	
	
}
