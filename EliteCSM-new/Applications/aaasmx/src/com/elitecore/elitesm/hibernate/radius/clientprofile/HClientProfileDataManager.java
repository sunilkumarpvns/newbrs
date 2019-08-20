package com.elitecore.elitesm.hibernate.radius.clientprofile;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import net.sf.json.JSONArray;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.type.StandardBasicTypes;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.sqloperationexception.DatabaseConnectionException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.clientprofile.ClientProfileDataManager;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ClientTypeData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.ProfileSuppVendorRelData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.RadiusClientProfileData;
import com.elitecore.elitesm.datamanager.radius.clientprofile.data.VendorData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;


public class HClientProfileDataManager extends HBaseDataManager implements ClientProfileDataManager{

	private static final String CLIENT_PROFILE_NAME = "profileName";
	private static final String CLIENT_PROFILE_ID = "profileId";
	private final static String MODULE="HCLIENTPROFILEDATAMANAGER";

	@Override
	public String create(Object obj) throws DataManagerException{
		
		RadiusClientProfileData clientProfileData = (RadiusClientProfileData) obj;
		EliteAssert.notNull(clientProfileData,"RadiusClientProfileData must not be null.");
		try{
			Logger.logDebug(MODULE, "profile Id is:"+clientProfileData.getProfileId());
			Session session = getSession();
			session.clear();
			
			//fetch next audit id
			String auditId= UUIDGenerator.generate();
			
			clientProfileData.setAuditUId(auditId);
			
			List<ProfileSuppVendorRelData> profileSupportedVendorList = clientProfileData.getSupportedVendorList();
			
			clientProfileData.setSupportedVendorList(null);
			session.save(clientProfileData);
			session.flush();
			session.clear();
			Logger.logDebug(MODULE, "profile Id is:"+clientProfileData.getProfileId());
			
			if(profileSupportedVendorList !=null ){
				for(Iterator<ProfileSuppVendorRelData> iterator = profileSupportedVendorList.iterator();iterator.hasNext();){
					ProfileSuppVendorRelData profileSuppVendorRelData = iterator.next();
					profileSuppVendorRelData.setProfileId(clientProfileData.getProfileId());
					session.save(profileSuppVendorRelData);
					session.flush();
					session.clear();
				}
			}
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE +clientProfileData.getProfileName()+ REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			Logger.logTrace(MODULE, hExp);
			throw new DataManagerException(FAILED_TO_CREATE+clientProfileData.getProfileName()+ REASON + hExp.getMessage(), hExp);
		}catch(Exception exp){
			Logger.logTrace(MODULE, exp);
			throw new DataManagerException(FAILED_TO_CREATE+clientProfileData.getProfileName()+ REASON + exp.getMessage(),exp);
		}
		return clientProfileData.getProfileName();
	}
	
	public List<VendorData> getVendorList() throws DataManagerException {
		List<VendorData> vendorList = null;
		try{
	        Session session = getSession();
	        Criteria criteria = session.createCriteria(VendorData.class);
	        vendorList = criteria.list();
	     
	    }catch(HibernateException hExp){
	    	hExp.printStackTrace();
	    	throw new DataManagerException("Failed to retrive Vendor List, Reason: "+hExp.getMessage(), hExp);
	    }catch(Exception exp){
	    	exp.printStackTrace();
	        throw new DataManagerException("Failed to retrive Vendor List, Reason: "+exp.getMessage(), exp);
	    }
		return vendorList;
	}
	
	public List<ClientTypeData> getClientTypeList() throws DataManagerException{
		List<ClientTypeData> clientTypeList = null;
		try{
	        Session session = getSession();
	        Criteria criteria = session.createCriteria(ClientTypeData.class);
	        criteria.add(Restrictions.eq("status","Y"));
	        clientTypeList = criteria.list();
	        

	    }catch(HibernateException hExp){
	    	hExp.printStackTrace();
	    	throw new DataManagerException("Failed to retrive client type list, Reason: "+hExp.getMessage(), hExp);
	    }catch(Exception exp){
	    	exp.printStackTrace();
	    	throw new DataManagerException("Failed to retrive client type list, Reason: "+exp.getMessage(), exp);
	    }
		return clientTypeList;
	}

	public VendorData getVendorData(Long vendorId) throws DataManagerException{

		VendorData data=null;
		try{

			Session session=getSession();
			Criteria criteria=session.createCriteria(VendorData.class);
			criteria.add(Restrictions.eq("vendorId",vendorId));
			data=(VendorData)criteria.uniqueResult();

		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive vendor, Reason: "+hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive vendor, Reason: "+exp.getMessage(), exp);
		}

		return data;
	}

	public ClientTypeData getClientTypeData(Long clientTypeId) throws DataManagerException{
		
		ClientTypeData clientTypeData=null;
		try{
			Session session=getSession();
			Criteria criteria=session.createCriteria(ClientTypeData.class);
			criteria.add(Restrictions.eq("clientTypeId",clientTypeId));
			clientTypeData=(ClientTypeData)criteria.list().get(0);
			
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to search client type, Reason: "+hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to search client type, Reason: "+exp.getMessage(), exp);
		}
		return clientTypeData;
	}
  
	public PageList search(RadiusClientProfileData radiusClientProfileData,int requiredPageNo, Integer pageSize) throws DataManagerException{
		
		PageList pageList = null;
		try{
				Session session = getSession();
				Criteria criteria = session.createCriteria(RadiusClientProfileData.class);
										  
	            if((radiusClientProfileData.getProfileName() != null && radiusClientProfileData.getProfileName().length()>0 )){
	            	String profileName="%"+radiusClientProfileData.getProfileName()+"%";
	            	criteria.add(Restrictions.ilike(CLIENT_PROFILE_NAME,profileName));
	            }
                long clientTypeId = radiusClientProfileData.getClientTypeId();
	            if(clientTypeId != 0){
	            	criteria.add(Restrictions.eq("clientTypeId",clientTypeId));
	            }
	            String vendorInstanceId=radiusClientProfileData.getVendorInstanceId();
	            if (Strings.isNullOrBlank(vendorInstanceId) == false && "0".equals(vendorInstanceId) == false) {
	            	criteria.add(Restrictions.eq("vendorInstanceId",vendorInstanceId));
	            }
	            
	            int totalItems = criteria.list().size();
            	criteria.setFirstResult(((requiredPageNo-1) * pageSize));
            	
            	if (pageSize > 0 ){
            		criteria.setMaxResults(pageSize);
            	}
            	
            	List clientProfileList = criteria.list();
	            long totalPages = (long)Math.ceil(totalItems/pageSize);
	            if(totalItems%pageSize == 0)
	           	totalPages-=1;
	            
	            pageList = new PageList(clientProfileList, requiredPageNo, totalPages ,totalItems);
	            Logger.logDebug(MODULE,"LIST SIZE IS:"+clientProfileList.size());
	            
       }catch(HibernateException hExp){
    	   hExp.printStackTrace();
    	   throw new DataManagerException("Failed to retrive Trusted Client Profile for pagging operation, Reason: "+ hExp.getMessage(), hExp);
       }catch(Exception exp){
    	   exp.printStackTrace();
    	   throw new DataManagerException("Failed to retrive Trusted Client Profile for pagging operation, Reason: "+ exp.getMessage(), exp);
       }
    return pageList;
		
	}
	
	public List<RadiusClientProfileData> getRadiusClientProfileList() throws DataManagerException {
		List<RadiusClientProfileData> clientProfileList=null;
		try{
			Session session = getSession();

			Criteria criteria = session.createCriteria(RadiusClientProfileData.class).addOrder(Order.asc(CLIENT_PROFILE_NAME));
			clientProfileList = criteria.list();
			return clientProfileList;
			
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to fetch list of Trusted Client Profile, Reason: "+hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}

	@Override
	public String deleteById(String clientProfileId) throws DataManagerException {
		return delete(CLIENT_PROFILE_ID, clientProfileId);
	}
	
	@Override
	public String deleteByName(String clientProfileName) throws DataManagerException {
		return delete(CLIENT_PROFILE_NAME, clientProfileName);
	}
	
	private String delete(String propertyName, Object propertyValue)throws DataManagerException {
		String clientProfileName = (CLIENT_PROFILE_NAME.equals(propertyName)) ? (String) propertyValue : "Client Profile";
		try {

			Session session = getSession();
			Criteria profileCriteria = session.createCriteria(RadiusClientProfileData.class);
			profileCriteria.add(Restrictions.eq(propertyName, propertyValue));
			RadiusClientProfileData clientProfileData = (RadiusClientProfileData) profileCriteria.uniqueResult();

			if (clientProfileData == null) {
				throw new InvalidValueException("Trusted Client Profile not found");
			}
			
			clientProfileName = clientProfileData.getProfileName();
			List<ProfileSuppVendorRelData> profileSuppVendorRelLst = clientProfileData.getSupportedVendorList();

			// iterate supported vendor one by one and delete it(one to many realation is here)
			for (ProfileSuppVendorRelData profileSuppVendorRelData :profileSuppVendorRelLst) {
				if (profileSuppVendorRelData != null) {
					session.delete(profileSuppVendorRelData);
				}
			}
			session.flush();
			if (clientProfileData != null) {
				// delete RadiusClientProfileData (parent table)
				session.delete(clientProfileData);
			}
			session.flush();
			return clientProfileName;
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete "+clientProfileName+", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to delete "+ clientProfileName +", Reason: "+hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to delete "+ clientProfileName +", Reason: "+exp.getMessage(), exp);
		}
	}
	
	public void updateBasicDetails(RadiusClientProfileData radiusClientProfileData,IStaffData staffData,String actionAlias) throws DataManagerException {
		
		Session session = getSession();
		RadiusClientProfileData clientProfileData = null;
		if(radiusClientProfileData != null){
			EliteAssert.notNull(radiusClientProfileData,"Trusted Client Profile Data must not be null.");
			try{
	        	Criteria criteria = session.createCriteria(RadiusClientProfileData.class);
				criteria.add(Restrictions.eq(CLIENT_PROFILE_ID,radiusClientProfileData.getProfileId()));
				
				clientProfileData=(RadiusClientProfileData)criteria.uniqueResult();
				
				JSONArray jsonArray=ObjectDiffer.diff(clientProfileData,radiusClientProfileData);
				
				clientProfileData.setProfileName(radiusClientProfileData.getProfileName());
				clientProfileData.setDescription(radiusClientProfileData.getDescription());
				clientProfileData.setClientTypeId(radiusClientProfileData.getClientTypeId());
				clientProfileData.setVendorInstanceId(radiusClientProfileData.getVendorInstanceId());
				clientProfileData.setLastModifiedByStaffId(radiusClientProfileData.getLastModifiedByStaffId());
				clientProfileData.setLastModifiedDate(radiusClientProfileData.getLastModifiedDate());
				
				if (clientProfileData.getAuditUId() == null) {
					String auditId= UUIDGenerator.generate();
					clientProfileData.setAuditUId(auditId);
					staffData.setAuditId(auditId);
				}
				
				session.update(clientProfileData);
				
				//delete profile supported vendor rel list
				Criteria profileSuppRelCriteria = session.createCriteria(ProfileSuppVendorRelData.class);
				profileSuppRelCriteria.add(Restrictions.eq(CLIENT_PROFILE_ID,radiusClientProfileData.getProfileId()));
				List<ProfileSuppVendorRelData> profileSuppVendorRelList = profileSuppRelCriteria.list();
				for (ProfileSuppVendorRelData profileSuppVendorRelData : profileSuppVendorRelList) {
					session.delete(profileSuppVendorRelData);
				}
				session.flush();
				//add profile supported vendor rel list
				List<ProfileSuppVendorRelData> supportedVendorList = radiusClientProfileData.getSupportedVendorList();
				if(Collectionz.isNullOrEmpty(supportedVendorList) == false){
					for(ProfileSuppVendorRelData profileSuppVendorRelData : supportedVendorList ){
						profileSuppVendorRelData.setProfileId(clientProfileData.getProfileId());
						Logger.logDebug(MODULE, "profileSuppVendorRelData VENDOR INSTANCE ID IS: "+profileSuppVendorRelData.getVendorInstanceId());
						session.save(profileSuppVendorRelData);
						session.flush();
					}
				}
				
				doAuditingJson(jsonArray.toString(),staffData,actionAlias);
			
			} catch (ConstraintViolationException cve) {
				cve.printStackTrace();
				throw new DataManagerException("Failed to update basic detail of Trusted Client Profile, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
			}catch(HibernateException hExp){
				hExp.printStackTrace();
				throw new DataManagerException("Failed to update basic detail of Trusted Client Profile, Reason: "+hExp.getMessage(),hExp);
			}catch(Exception exp){
				exp.printStackTrace();
				throw new DataManagerException("Failed to update basic detail of Trusted Client Profile, Reason: "+exp.getMessage(),exp);
			}
		}else{
			throw new DataManagerException("Update Client Profile Failed");
		}
	}
	
	public void updateAdvanceDetails(RadiusClientProfileData radiusClientProfileData,IStaffData staffData,String actionAlias) throws DataManagerException{
		Session session = getSession();
		RadiusClientProfileData clientProfileData = null;
       if(radiusClientProfileData != null){
		try{
			
        	Criteria criteria = session.createCriteria(RadiusClientProfileData.class);
			criteria.add(Restrictions.eq(CLIENT_PROFILE_ID,radiusClientProfileData.getProfileId()));
			
			clientProfileData=(RadiusClientProfileData)criteria.uniqueResult();
			JSONArray jsonArray=ObjectDiffer.diff(clientProfileData,radiusClientProfileData);
			
			clientProfileData.setDnsList(radiusClientProfileData.getDnsList());
			clientProfileData.setUserIdentities(radiusClientProfileData.getUserIdentities());
			clientProfileData.setPrepaidStandard(radiusClientProfileData.getPrepaidStandard());
			clientProfileData.setClientPolicy(radiusClientProfileData.getClientPolicy());
			clientProfileData.setHotlinePolicy(radiusClientProfileData.getHotlinePolicy());
			//clientProfileData.setFramedPool(radiusClientProfileData.getFramedPool());
			clientProfileData.setDhcpAddress(radiusClientProfileData.getDhcpAddress());
			clientProfileData.setHaAddress(radiusClientProfileData.getHaAddress());
			clientProfileData.setLastModifiedByStaffId(radiusClientProfileData.getLastModifiedByStaffId());
			clientProfileData.setLastModifiedDate(radiusClientProfileData.getLastModifiedDate());
			clientProfileData.setMultipleClassAttribute(radiusClientProfileData.getMultipleClassAttribute());
			clientProfileData.setFilterUnsupportedVsa(radiusClientProfileData.getFilterUnsupportedVsa());
			clientProfileData.setCoaSupportedAttributes(radiusClientProfileData.getCoaSupportedAttributes());
			clientProfileData.setCoaUnsupportedAttributes(radiusClientProfileData.getCoaUnsupportedAttributes());
			clientProfileData.setDmSupportedAttributes(radiusClientProfileData.getDmSupportedAttributes());
			clientProfileData.setDmUnsupportedAttributes(radiusClientProfileData.getDmUnsupportedAttributes());
			clientProfileData.setDynAuthPort(radiusClientProfileData.getDynAuthPort());
			clientProfileData.setAuditUId(radiusClientProfileData.getAuditUId());
			
			if(clientProfileData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				clientProfileData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			session.update(clientProfileData);
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update advance detail of Trusted Client Profile, Reason: "+hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update advance detail of Trusted Client Profile, Reason: "+exp.getMessage(),exp);
		}
		}else{
			throw new DataManagerException("Update client profile failed");
		}
	}
	
	@Override
	public VendorData getVendorData(String vendorId) throws DataManagerException {
		Criteria criteria = null;
		VendorData vendorData = null;
		Session session = getSession();
		criteria = session.createCriteria(VendorData.class);
		List list = criteria.add(Restrictions.eq("vendorInstanceId",vendorId)).list();
		if(list!=null && !list.isEmpty()){
			vendorData = (VendorData)list.get(0);
		}
		return vendorData;
	}

	@Override
	public void createVendor(VendorData vendorData) throws DataManagerException {
		EliteAssert.notNull(vendorData,"Vendor Data must not be null.");
		try{
			Session session = getSession();
			session.save(vendorData);
			session.flush();
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to create Vendor, Reason: "+hExp.getMessage(), hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to create Vendor, Reason: "+exp.getMessage(),exp);
		}
	}
	

	@Override
	public RadiusClientProfileData getClientProfileDataById(String profileID) throws DataManagerException {
		return getClientProfileData(CLIENT_PROFILE_ID, profileID);
	}
	
	@Override
	public RadiusClientProfileData getClientProfileByName(String profileName)
			throws DataManagerException {
		return getClientProfileData(CLIENT_PROFILE_NAME, profileName);
	}
	
	private RadiusClientProfileData getClientProfileData(String properties , Object value) throws DataManagerException{
		RadiusClientProfileData radiusClientProfileData = new RadiusClientProfileData();
		try{
			
			Session session=getSession();
			Criteria criteria=session.createCriteria(RadiusClientProfileData.class);
			criteria.add(Restrictions.eq(properties,value));
			radiusClientProfileData=(RadiusClientProfileData)criteria.uniqueResult();
			
			if (radiusClientProfileData == null) {
				throw new InvalidValueException("Trusted Client Profile data not found");
			}
			// set  supported vendor list to RadiusClientProfile Object
			List<ProfileSuppVendorRelData> supportedVendorList = radiusClientProfileData.getSupportedVendorList();
			List<VendorData> supportedVendorLst=new ArrayList<VendorData>();
			for (ProfileSuppVendorRelData profileSuppVendorRelData : supportedVendorList) {
                criteria=session.createCriteria(VendorData.class);				
				criteria.add(Restrictions.eq("vendorInstanceId",profileSuppVendorRelData.getVendorInstanceId()));
				VendorData data=(VendorData)criteria.uniqueResult();
				supportedVendorLst.add(data);
			}
			
		   radiusClientProfileData.setSupportedVendorLst(supportedVendorLst);
		   
		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrive Trusted Client Profile, Reason: "+hExp.getMessage(), hExp);
	    }catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrive Trusted Client Profile, Reason: "+exp.getMessage(), exp);
	    }
		return radiusClientProfileData;
	}

	@Override
	public void update(RadiusClientProfileData radiusClientProfileData,IStaffData staffData, String actionAlias,String clientProfileName)
			throws DataManagerException {
		Session session = getSession();
		RadiusClientProfileData clientProfileData = null;
		try{
			//get RadiusClientProfileData from database
			Criteria criteria=session.createCriteria(RadiusClientProfileData.class);
			criteria.add(Restrictions.eq(CLIENT_PROFILE_NAME,clientProfileName));
			clientProfileData=(RadiusClientProfileData)criteria.uniqueResult();
			
			if (clientProfileData == null) {
				throw new InvalidValueException("Trusted Client Profile data not found");
			}
			
			//difference between update object and stored object
			JSONArray jsonArray = ObjectDiffer.diff(clientProfileData,radiusClientProfileData);
			
			//for updating basic details
			clientProfileData.setProfileName(radiusClientProfileData.getProfileName());
			clientProfileData.setDescription(radiusClientProfileData.getDescription());
			clientProfileData.setSystemgenerated("N");
			clientProfileData.setClientTypeId(radiusClientProfileData.getClientTypeId());
			clientProfileData.setVendorInstanceId(radiusClientProfileData.getVendorInstanceId());
			clientProfileData.setLastModifiedByStaffId(staffData.getStaffId());
			clientProfileData.setLastModifiedDate(new Timestamp(System.currentTimeMillis()));
			//for updating advance details
			clientProfileData.setDnsList(radiusClientProfileData.getDnsList());
			clientProfileData.setUserIdentities(radiusClientProfileData.getUserIdentities());
			clientProfileData.setPrepaidStandard(radiusClientProfileData.getPrepaidStandard());
			clientProfileData.setClientPolicy(radiusClientProfileData.getClientPolicy());
			clientProfileData.setHotlinePolicy(radiusClientProfileData.getHotlinePolicy());
			clientProfileData.setDhcpAddress(radiusClientProfileData.getDhcpAddress());
			clientProfileData.setHaAddress(radiusClientProfileData.getHaAddress());
			clientProfileData.setMultipleClassAttribute(radiusClientProfileData.getMultipleClassAttribute());
			clientProfileData.setFilterUnsupportedVsa(radiusClientProfileData.getFilterUnsupportedVsa());
			clientProfileData.setCoaSupportedAttributes(radiusClientProfileData.getCoaSupportedAttributes());
			clientProfileData.setCoaUnsupportedAttributes(radiusClientProfileData.getCoaUnsupportedAttributes());
			clientProfileData.setDmSupportedAttributes(radiusClientProfileData.getDmSupportedAttributes());
			clientProfileData.setDmUnsupportedAttributes(radiusClientProfileData.getDmUnsupportedAttributes());
			clientProfileData.setDynAuthPort(radiusClientProfileData.getDynAuthPort());

			session.update(clientProfileData);
			session.flush();
			//delete profile supported vendor rel list
			List<ProfileSuppVendorRelData> profileSuppVendorRelList = clientProfileData.getSupportedVendorList();
			for (ProfileSuppVendorRelData profileSuppVendorRelData : profileSuppVendorRelList) {
				session.delete(profileSuppVendorRelData);
			}
			session.flush();
			Logger.logDebug(MODULE, "supported vendor delete succesfully");
			//add profile supported vendor rel list
			List<ProfileSuppVendorRelData> supportedVendorList = radiusClientProfileData.getSupportedVendorList();
			if(supportedVendorList!=null){
				for(ProfileSuppVendorRelData profileSuppVendorRelData :supportedVendorList){
					profileSuppVendorRelData.setProfileId(clientProfileData.getProfileId());
					Logger.logDebug(MODULE, "profileSuppVendorRelData VENDOR INSTANCE ID IS: "+profileSuppVendorRelData.getVendorInstanceId());
					session.save(profileSuppVendorRelData);
					session.flush();
				}
			}
			
			//Auditing Parameters
			staffData.setAuditName(clientProfileData.getProfileName());
			staffData.setAuditId(clientProfileData.getAuditUId());
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update " +clientProfileName+", Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hExp){
			hExp.printStackTrace();
			throw new DataManagerException("Failed to update "+clientProfileName+", Reason: "+hExp.getMessage(),hExp);
		}catch(Exception exp){
			exp.printStackTrace();
			throw new DataManagerException("Failed to update "+clientProfileName+", Reason: "+exp.getMessage(),exp);
		}
	}

	@Override
	public long getClientTypeIdFromName(String clientType)
			throws DataManagerException {
		Session session = getSession();
		ClientTypeData clientTypeData ;
		try {
			Criteria criteria = session.createCriteria(ClientTypeData.class);
			clientTypeData = (ClientTypeData) criteria.add(Restrictions.eq("clientTypeName",clientType)).uniqueResult();
			if (clientTypeData == null){
				throw new InvalidValueException("No matching client type found for: "+clientType);
			}
			
		} catch (HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Client type id from client type, Reason: "+hbe.getMessage(), hbe);
		}
		return clientTypeData.getClientTypeId();
	}

	@Override
	public String getVendorIdFromName(String vendorName)
			throws DataManagerException {
		Session session = getSession();
		VendorData vendorData ;
		try {
			Criteria criteria = session.createCriteria(VendorData.class);
			vendorData = (VendorData) criteria.add(Restrictions.eq("vendorName",vendorName)).uniqueResult();
			if (vendorData == null){
				throw new InvalidValueException("No matching vendor name found for: "+vendorName);
			}
			
		} catch (HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive vendor id from vendor name, Reason: "+hbe.getMessage(), hbe);
		}
		return vendorData.getVendorInstanceId();
	}	     
}
