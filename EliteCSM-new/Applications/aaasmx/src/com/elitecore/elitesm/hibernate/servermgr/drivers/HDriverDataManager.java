package com.elitecore.elitesm.hibernate.servermgr.drivers;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import org.hibernate.Criteria;
import org.hibernate.FetchMode;
import org.hibernate.HibernateException;
import org.hibernate.MultiIdentifierLoadAccess;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.ObjectDiffer;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidValueException;
import com.elitecore.elitesm.datamanager.core.exceptions.server.DuplicateParameterFoundExcpetion;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.diameter.diameterpeer.data.DiameterPeerRelData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverData;
import com.elitecore.elitesm.datamanager.servermanager.drivers.hssdriver.data.HssAuthDriverFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.certificate.data.ServerCertificateData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.DriverDataManager;
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.chargingdriver.data.CrestelChargingDriverPropsData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVAttrRelationData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.csvdriver.data.ClassicCSVStripPattRelData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverTypeData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameDriverRelData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.LogicalNameValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.ProfileFieldValuePoolData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAcctFeildMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverPeerData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverRealmsData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dcdriver.data.DiameterChargingDriverVendorData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAcctDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.detailacctlocal.data.DetailLocalAttrRelationData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.httpdriver.data.HttpAuthDriverFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.ldapdriver.data.LDAPAuthFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.GatewayFieldMapData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.mapgatewaydriver.data.MappingGatewayAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.pcdriver.data.ParleyChargingDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.userfiledriver.data.UserFileAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebMethodKeyMapRelData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.webserviceauthdriver.data.WebServiceAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.service.data.IServiceTypeData;
import com.elitecore.elitesm.datamanager.servermgr.service.data.ServiceTypeData;
import com.elitecore.elitesm.hibernate.core.base.HBaseDataManager;
import com.elitecore.elitesm.hibernate.core.system.util.UUIDGenerator;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.driver.CreateDriverConfig;

import net.sf.json.JSONArray;

public class HDriverDataManager extends HBaseDataManager implements DriverDataManager{
	private static final String CACHEABLE = "cacheable";
	private static final String SERIAL_NUMBER = "serialNumber";
	private static final String STATUS = "status";
	private static final String CRESTEL_CHARGING_DRIVER_ID = "crestelChargingDriverId";
	private static final String MAP_AUTH_ID = "mapAuthId";
	private static final String CLASSIC_CSV_ID = "classicCsvId";
	private static final String OPEN_DB_ACCT_ID = "openDbAcctId";
	private static final String DC_DRIVER_ID = "dcDriverId";
	private static final String DETAIL_LOCAL_ID = "detailLocalId";
	private static final String LDAP_DRIVER_ID = "ldapDriverId";
	private static final String HTTP_AUTH_DRIVER_ID = "httpAuthDriverId";
	private static final String DB_AUTH_ID = "dbAuthId";
	private static final String MODULE = "HDriverDataManager";
	private static final String DRIVER_INSTANCE_ID = "driverInstanceId";
	private static final String DRIVER_INSTANCE_NAME = "name";
	private static final String DRIVER_SERVICE_TYPE_ID = "serviceTypeId"; 
	private static final String DRIVER_TYPE_ID = "driverTypeId"; 		
	private static final Class<?> DRIVER_INSTANCE_DATA = DriverInstanceData.class;
	
	public String createDBDriver(CreateDriverConfig driverConfig) throws DataManagerException {
		
		try{						
			Session session = getSession();
			session.clear();
			DriverInstanceData driverInstanceData = driverConfig.getDriverInstanceData();

			String auditId= UUIDGenerator.generate();
			driverInstanceData.setAuditUId(auditId);
			
			Set<DBAuthDriverData> data = new HashSet<DBAuthDriverData>();
			data.add(driverConfig.getDbAuthDriverData());
			driverInstanceData.setDbdetail(data);
			
			session.save(driverInstanceData);			

			String driverInstanceId = driverInstanceData.getDriverInstanceId();

			DBAuthDriverData dbAuthDriverData = driverConfig.getDbAuthDriverData();			
			dbAuthDriverData.setDriverInstanceId(driverInstanceId);
			
			session.save(dbAuthDriverData);

			String dbAuthId = dbAuthDriverData.getDbAuthId();
			List<DBAuthFieldMapData> tempList = dbAuthDriverData.getDbFieldMapList();

			int orderNumber = 1;
			int size = tempList.size();
			for (int i = 0; i < size; i++) {
				DBAuthFieldMapData fieldMapData = tempList.get(i);
				fieldMapData.setDbAuthId(dbAuthId);
				fieldMapData.setOrderNumber(orderNumber++);
				session.save(fieldMapData);
			}
			//if this property is true then it will not validate after operation performed.
			dbAuthDriverData.setCheckValidate(true);
			session.flush();
			session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON +  hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON +e.getMessage(),e);
		}
		return driverConfig.getDriverInstanceData().getName();
	}
	
	public String createHttpAuthDriver(CreateDriverConfig driverConfig) throws DataManagerException{
		try{						
			Session session = getSession();
			session.clear();
			DriverInstanceData driverInstanceData = driverConfig.getDriverInstanceData();

			HttpAuthDriverData httpAuthDriverData = driverConfig.getHttpAuthDriverData();			

			String auditId= UUIDGenerator.generate();
			
			driverInstanceData.setAuditUId(auditId);
			Set<HttpAuthDriverData> httpAuthDriverFieldMapDatas = new HashSet<HttpAuthDriverData>();
			httpAuthDriverFieldMapDatas.add(driverConfig.getHttpAuthDriverData());
			driverInstanceData.setHttpAuthFieldMapSet(httpAuthDriverFieldMapDatas);
			
			session.save(driverInstanceData);			

			String driverInstanceId = driverInstanceData.getDriverInstanceId();

			httpAuthDriverData.setDriverInstanceId(driverInstanceId);
//			if this property is true then it will not validate after operation performed.
			httpAuthDriverData.setCheckValidate(true);
			session.save(httpAuthDriverData);

			String httpAuthDriverId = httpAuthDriverData.getHttpAuthDriverId();
			
			List<HttpAuthDriverFieldMapData> tempHttpAuthFieldMapDataList = httpAuthDriverData.getHttpFieldMapList();
			if(Collectionz.isNullOrEmpty(tempHttpAuthFieldMapDataList) == false ){
				int size = tempHttpAuthFieldMapDataList.size();
				int orderNumber = 1;
				for (int i = 0; i < size; i++) {
					HttpAuthDriverFieldMapData httpAuthDriverFieldMapData = tempHttpAuthFieldMapDataList.get(i);
					httpAuthDriverFieldMapData.setHttpAuthDriverId(httpAuthDriverId);
					httpAuthDriverFieldMapData.setOrderNumber(orderNumber++);
					session.save(httpAuthDriverFieldMapData);
				}

				session.flush();
				session.clear();
			}
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() +REASON +  hbe.getMessage(),hbe);
		} catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON+e.getMessage(),e);
		}
		return driverConfig.getDriverInstanceData().getName();
	}

	public List<LogicalNameValuePoolData> getLogicalNameValuePoolList() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(LogicalNameValuePoolData.class);
			criteria.addOrder(Order.asc(DRIVER_INSTANCE_NAME));
			List<LogicalNameValuePoolData> nameValuePool = criteria.list();
			return nameValuePool;

		}catch(HibernateException hbe){;
		throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	public List<ProfileFieldValuePoolData> getProfileFieldValuePoolList() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ProfileFieldValuePoolData.class);
			criteria.addOrder(Order.asc(DRIVER_INSTANCE_NAME));
			List<ProfileFieldValuePoolData> nameValuePool = criteria.list();
			return nameValuePool;
		}catch(HibernateException hbe){;
		throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	public String createMAPGatewayAuthDriver(DriverInstanceData driverInstance,MappingGatewayAuthDriverData mappingGatewayAuthDriver) throws DataManagerException {
		try{
			Session session = getSession();
			session.clear();
			String auditId= UUIDGenerator.generate();
			Set<MappingGatewayAuthDriverData> mapGatewaySet = new HashSet<MappingGatewayAuthDriverData>();
			mapGatewaySet.add(mappingGatewayAuthDriver);
			driverInstance.setMapGatewaySet(mapGatewaySet);
			
			driverInstance.setAuditUId(auditId);

			session.save(driverInstance);			

			String driverInstanceId = driverInstance.getDriverInstanceId();
			mappingGatewayAuthDriver.setDriverInstanceId(driverInstanceId);

			session.save(mappingGatewayAuthDriver);

			String mapGWAuthId = mappingGatewayAuthDriver.getMapGWAuthid();
			List<GatewayFieldMapData> tempList = mappingGatewayAuthDriver.getGatewayFieldList();
			if(Collectionz.isNullOrEmpty(tempList) == false){
				int orderNumber = 1;
				int size = tempList.size();
				for(int i=0;i<size;i++){
					GatewayFieldMapData feildMapData = tempList.get(i);
					feildMapData.setMapAuthId(mapGWAuthId);
					feildMapData.setOrderNumber(orderNumber++);
					session.save(feildMapData);
				}
			}
//			if this property is true then it will not validate after operation performed.
			mappingGatewayAuthDriver.setCheckValidate(true);
			session.flush();
			session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstance.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstance.getName() +REASON +  hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstance.getName() + REASON+e.getMessage(),e);
		}
		return driverInstance.getName();
	}

	public void createRadiusPCDriver(DriverInstanceData driverInstanceData,ParleyChargingDriverData parleyChargingDriverData) throws DataManagerException {
		try{
			Session session = getSession();

			boolean isDriverExist = getDriverInstancesByName(driverInstanceData.getName());
			if(isDriverExist){
				throw new DuplicateParameterFoundExcpetion("The driver with this name already exists.");
			}

			String auditId= UUIDGenerator.generate();
			
			driverInstanceData.setAuditUId(auditId);
			
			session.save(driverInstanceData);			
			session.flush();

			String driverInstanceId = driverInstanceData.getDriverInstanceId();
			parleyChargingDriverData.setDriverInstanceId(driverInstanceId);

			session.save(parleyChargingDriverData);
			session.flush();

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	public String createDiameterChargingDriver(DriverInstanceData driverInstanceData,DiameterChargingDriverData diameterChargingDriverData) throws DataManagerException {
		try{
			Session session = getSession();
			session.clear();

			String auditId= UUIDGenerator.generate();
			
			driverInstanceData.setAuditUId(auditId);
			Set<DiameterChargingDriverData> diameterChargingDriverDataSet = new HashSet<DiameterChargingDriverData>();
			diameterChargingDriverDataSet.add(diameterChargingDriverData);
			driverInstanceData.setDiameterChargingDriverSet(diameterChargingDriverDataSet);
			 
			session.save(driverInstanceData);

			String driverInstanceId = driverInstanceData.getDriverInstanceId();
			diameterChargingDriverData.setDriverInstanceId(driverInstanceId);
			
			session.save(diameterChargingDriverData);
			session.flush();
			session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON +  hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON +e.getMessage(),e);
		}
		return driverInstanceData.getName();
	}

	@Override
	public String createLDAPDriver(CreateDriverConfig driverConfig) throws DataManagerException {

		try{
			Session session = getSession();
			session.clear();
			DriverInstanceData driverInstanceData = driverConfig.getDriverInstanceData();

			String auditId= UUIDGenerator.generate();
			
			driverInstanceData.setAuditUId(auditId);

			LDAPAuthDriverData ldapAuthDriverData = driverConfig.getLdapAuthDriverData();

			Set<LDAPAuthDriverData> ldapAuthDriverDataSet = new HashSet<LDAPAuthDriverData>();
			ldapAuthDriverDataSet.add(driverConfig.getLdapAuthDriverData());
			driverInstanceData.setLdapdetail(ldapAuthDriverDataSet);
			
			session.save(driverInstanceData);			

			String driverInstanceId = driverInstanceData.getDriverInstanceId();

			ldapAuthDriverData.setDriverInstanceId(driverInstanceId);
			session.save(ldapAuthDriverData);

			String ldapAuthId = ldapAuthDriverData.getLdapDriverId();
			List<LDAPAuthFieldMapData> tempList = ldapAuthDriverData.getLdapAuthDriverFieldMapList();
			if (Collectionz.isNullOrEmpty(tempList) == false) {
				int orderNumber = 1;
				int size = tempList.size();
				for (int i = 0; i < size; i++) {
					LDAPAuthFieldMapData feildMapData = tempList.get(i);
					feildMapData.setLdapDriverId(ldapAuthId);
					feildMapData.setOrderNumber(orderNumber++);
					session.save(feildMapData);
				}
//				if this property is true then it will not validate after operation performed.
				ldapAuthDriverData.setCheckValidate(true);	
				session.flush();
				session.clear();
			}
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() +REASON +  hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON+e.getMessage(),e);
		}
		return driverConfig.getDriverInstanceData().getName();
	}

	public String createUserFileDriver(CreateDriverConfig driverConfig) throws DataManagerException {
		String driverName = "Driver Instance";
		try{
			Session session = getSession();
			session.clear();
			DriverInstanceData driverInstanceData = driverConfig.getDriverInstanceData();
			driverName = driverInstanceData.getName();
			
			UserFileAuthDriverData userFileAuthDriver = driverConfig.getUserFileAuthDriverData();
			String auditId= UUIDGenerator.generate();
			driverInstanceData.setAuditUId(auditId);

			Set<UserFileAuthDriverData> userFileAuthDriverDataSet = new HashSet<UserFileAuthDriverData>();
			
			userFileAuthDriverDataSet.add(driverConfig.getUserFileAuthDriverData());
			driverInstanceData.setUserFileDetail(userFileAuthDriverDataSet);
			
			session.save(driverInstanceData);	
			
			String driverInstanceId = driverInstanceData.getDriverInstanceId();
			
			userFileAuthDriver.setDriverInstanceId(driverInstanceId);
			session.save(userFileAuthDriver);
			session.flush();
			session.clear();
		}catch(ConstraintViolationException cve){
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE+driverName+REASON
					+EliteExceptionUtils.extractConstraintName(cve.getSQLException()),cve);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE+driverName+REASON+e.getMessage(),e);
		}
		return driverName;
	}
	public List<IServiceTypeData> getListOfAllServices() throws DataManagerException{
		try{			
			Session session = getSession();
			Criteria criteria = session.createCriteria(ServiceTypeData.class);
			List<IServiceTypeData> serviceTypeDataList = criteria.list();

			return serviceTypeDataList;			

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}

	}

	public List<DriverInstanceData> getDriverInstanceList(long serviceTypeId) throws DataManagerException {
		List<DriverInstanceData> driverInstanceDataList = new ArrayList<DriverInstanceData>();
		try{
			Session session = getSession();

			Criteria criteria = session.createCriteria(DriverTypeData.class).add(Restrictions.eq(DRIVER_SERVICE_TYPE_ID, serviceTypeId));
			List<DriverTypeData> list = criteria.list();
			if(list!=null){
				Long driverTypeIds[] = new Long[list.size()];
				for(int i =0;i<list.size();i++){
					driverTypeIds[i] = list.get(i).getDriverTypeId();
				}
				Logger.logDebug(MODULE, "Driver Types:"+Arrays.toString(driverTypeIds));
				if(driverTypeIds != null && driverTypeIds.length > 0){
					driverInstanceDataList = session.createCriteria(DRIVER_INSTANCE_DATA).add(Restrictions.in(DRIVER_TYPE_ID,driverTypeIds))
							.addOrder(Order.asc(DRIVER_INSTANCE_NAME)).list();
				}				
				return driverInstanceDataList;
			}

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return driverInstanceDataList;
	}

	public List<DriverInstanceData> getDriverInstanceByDriverTypeList(Long driverTypeId) throws DataManagerException {
		List<DriverInstanceData> driverInstanceDataList=null ;
		try{
			Session session = getSession();
			driverInstanceDataList = session.createCriteria(DRIVER_INSTANCE_DATA).add(Restrictions.eq(DRIVER_TYPE_ID,driverTypeId)).list();

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return driverInstanceDataList;
	}
	
	public PageList search(DriverInstanceData driverInstanceData, int requiredPageNo, Integer pageSize) throws DataManagerException {

		List<DriverInstanceData> driverList = null;
		PageList pageList = null;
		try{		
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			String name=driverInstanceData.getName();
			Long selectedDriver=driverInstanceData.getDriverTypeId();

			if((name != null && !"".equals(name))){
				name = "%"+name+"%";
				criteria.add(Restrictions.ilike(DRIVER_INSTANCE_NAME,name));
			}

			if(selectedDriver != null && selectedDriver !=0){
				criteria.add(Restrictions.eq(DRIVER_TYPE_ID,selectedDriver));
			}
			int totalItems =  criteria.list().size();
			criteria.setFirstResult(((requiredPageNo-1) * pageSize));

			if (pageSize > 0 ){
				criteria.setMaxResults(pageSize);
			}

			driverList = criteria.list();

			long totalPages = (long)Math.ceil(totalItems/pageSize);
			if(totalItems%pageSize == 0)
				totalPages-=1;

			for(int i =0;i<driverList.size();i++){

				DriverInstanceData tempData = driverList.get(i);
				tempData.setDriverTypeName(tempData.getDriverTypeData().getName());

			}
			pageList = new PageList(driverList, requiredPageNo, totalPages ,totalItems);

		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive "+driverInstanceData.getName()+", Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive "+driverInstanceData.getName()+", Reason: "+e.getMessage(),e);
		}
		return pageList;
	}

	public DBAuthDriverData getDbDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DBAuthDriverData.class);
			DBAuthDriverData authDriverData = (DBAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(authDriverData == null) {
				throw new InvalidValueException("DB Auth Driver not found.");	
			}
			return authDriverData;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);

		}
	}
	
	public HttpAuthDriverData getHttpDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(HttpAuthDriverData.class);
			HttpAuthDriverData httpAuthDriverData = (HttpAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(httpAuthDriverData == null) {
				throw new InvalidValueException("HTTP Auth Driver not found.");	
			}
			return httpAuthDriverData;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Http Auth Driver Data, Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Http Auth Driver Data, Reason: "+e.getMessage(),e);
		}
	}
	@Override
	public void updateHttpAuthDriverById(DriverInstanceData driverInstance,HttpAuthDriverData httpAuthDriverData, IStaffData staffData)
			throws DataManagerException {
		updateHttpAuthDriver(driverInstance, httpAuthDriverData, staffData,DRIVER_INSTANCE_ID,driverInstance.getDriverInstanceId());		
	}

	@Override
	public void updateHttpAuthDriverByName(DriverInstanceData driverInstance,HttpAuthDriverData httpAuthDriverData, IStaffData staffData,
			String name) throws DataManagerException {
		updateHttpAuthDriver(driverInstance, httpAuthDriverData, staffData,DRIVER_INSTANCE_NAME,name);
	}

	private void updateHttpAuthDriver(DriverInstanceData driverInstance,HttpAuthDriverData httpAuthDriverData,IStaffData staffData,
			String propertyName, Object value) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
		
			Set<HttpAuthDriverData> oldhttpAuthFieldMapSet = new HashSet<HttpAuthDriverData>();
			
			
			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(propertyName,value)).uniqueResult();
			
			if(tempDriverInstanceData == null){
				throw new InvalidValueException("Driver Instance not found.");
			}
			
			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();
			oldhttpAuthFieldMapSet.add(httpAuthDriverData);
			driverInstance.setHttpAuthFieldMapSet(oldhttpAuthFieldMapSet);
			
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstance);
		
			criteria = session.createCriteria(HttpAuthDriverData.class);
			HttpAuthDriverData tempHttpAuthDriverData = (HttpAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();
			
			if(tempHttpAuthDriverData == null){
				throw new InvalidValueException("Http Auth Driver not found.");
			}
			String httpAuthDriverId = tempHttpAuthDriverData.getHttpAuthDriverId();

			criteria = session.createCriteria(HttpAuthDriverFieldMapData.class);
			List<HttpAuthDriverFieldMapData> httpAuthFieldMapList = criteria.add(Restrictions.eq(HTTP_AUTH_DRIVER_ID,tempHttpAuthDriverData.getHttpAuthDriverId())).list();
			
			if(Collectionz.isNullOrEmpty(httpAuthFieldMapList) == false){
				int size = httpAuthFieldMapList.size();
				for (int j = 0; j < size; j++) {
					HttpAuthDriverFieldMapData httpAuthDriverFieldMapData = httpAuthFieldMapList.get(j);
					session.delete(httpAuthDriverFieldMapData);
					session.flush();
				}
			}
			
			List<HttpAuthDriverFieldMapData> httpAuthFieldMapDataList = httpAuthDriverData.getHttpFieldMapList();
			
			if(Collectionz.isNullOrEmpty(httpAuthFieldMapDataList) == false){
				int orderNumber = 1;
				Iterator<HttpAuthDriverFieldMapData> httpAuthFieldMapIterator = httpAuthFieldMapDataList.iterator();
				while(httpAuthFieldMapIterator.hasNext()){			
					HttpAuthDriverFieldMapData httpAuthDriverFieldMapData = httpAuthFieldMapIterator.next();
					httpAuthDriverFieldMapData.setHttpAuthDriverId(httpAuthDriverId);
					httpAuthDriverFieldMapData.setOrderNumber(orderNumber++);
					session.save(httpAuthDriverFieldMapData);
				}
			}
			session.flush();

			tempHttpAuthDriverData.setHttp_url(httpAuthDriverData.getHttp_url());
			tempHttpAuthDriverData.setHttpFieldMapList(httpAuthDriverData.getHttpFieldMapList());
			tempHttpAuthDriverData.setDriverInstanceId(httpAuthDriverData.getDriverInstanceId());
			tempHttpAuthDriverData.setMaxQueryTimeoutCount(httpAuthDriverData.getMaxQueryTimeoutCount());
			tempHttpAuthDriverData.setStatusCheckDuration(httpAuthDriverData.getStatusCheckDuration());	
			tempHttpAuthDriverData.setExpiryDateFormat(httpAuthDriverData.getExpiryDateFormat());
			tempHttpAuthDriverData.setUserIdentityAttributes(httpAuthDriverData.getUserIdentityAttributes());
//			if this property is true then it will not validate after operation performed.
			tempHttpAuthDriverData.setCheckValidate(true);
			session.update(tempHttpAuthDriverData);

			// for driver instance 

			tempDriverInstanceData.setName(driverInstance.getName());
			tempDriverInstanceData.setDescription(driverInstance.getDescription());
			tempDriverInstanceData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			tempDriverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
			session.update(tempDriverInstanceData);
			session.flush();
			
			staffData.setAuditId(tempDriverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstance.getName());
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Http Auth Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update Http Auth Driver, Reason: " +  hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update Http Auth Driver, Reason: "+e.getMessage(),e);
		}
	}

	public UserFileAuthDriverData getUserFileDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException {
		try{	
			Session session = getSession();
			Criteria criteria = session.createCriteria(UserFileAuthDriverData.class);
			UserFileAuthDriverData driverData = (UserFileAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(driverData == null) {
				throw new InvalidValueException("User File Auth Driver not found.");	
			}
			return driverData;
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update User File Auth Driver "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update User File Auth Driver "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateUserFileAuthDriverDataByName(DriverInstanceData driverInstance,UserFileAuthDriverData userFileAuthDriverData, IStaffData staffData, String value) throws DataManagerException {
		updateUserFileAuthDriverData(driverInstance, userFileAuthDriverData,staffData,DRIVER_INSTANCE_NAME,value);
	}

	@Override
	public void updateUserFileAuthDriverDataById(DriverInstanceData driverInstance,UserFileAuthDriverData userFileAuthDriverData, IStaffData staffData) throws DataManagerException {
		updateUserFileAuthDriverData(driverInstance, userFileAuthDriverData,staffData,DRIVER_INSTANCE_ID,driverInstance.getDriverInstanceId());
	}
	
	private void updateUserFileAuthDriverData(DriverInstanceData driverInstance,UserFileAuthDriverData userFileAuthDriverData,IStaffData staffData,
			String propertyName, Object value) throws DataManagerException {
			String driverInstanceName = "Driver Instance Name";
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			DriverInstanceData oldData = null;
			
			Set<UserFileAuthDriverData> newUserFileAuthDriverSet = new HashSet<UserFileAuthDriverData>();
			
			oldData =  (DriverInstanceData)criteria.add(Restrictions.eq(propertyName, value)).uniqueResult();
			
			driverInstanceName = driverInstance.getName();
			if(oldData == null){
				throw new InvalidValueException("Driver not found");
			}
			
			newUserFileAuthDriverSet.add(userFileAuthDriverData);
			driverInstance.setUserFileDetail(newUserFileAuthDriverSet);
			JSONArray jsonArray=ObjectDiffer.diff(oldData,driverInstance);
					
			oldData.setName(driverInstance.getName());
			oldData.setDescription(driverInstance.getDescription());
			oldData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			oldData.setLastModifiedDate(driverInstance.getLastModifiedDate());

			// updating userfile auth driver instance related data.
			criteria = session.createCriteria(UserFileAuthDriverData.class);
			UserFileAuthDriverData oldUserfileData = (UserFileAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, oldData.getDriverInstanceId())).uniqueResult();
			if(oldUserfileData == null){
				throw new InvalidValueException("User File Auth Driver Not Found.");
			}
			oldUserfileData.setFileLocations(userFileAuthDriverData.getFileLocations());
			oldUserfileData.setExpiryDateFormat(userFileAuthDriverData.getExpiryDateFormat());
		
			session.update(oldUserfileData);
			session.flush();
			
			staffData.setAuditId(oldData.getAuditUId());
			staffData.setAuditName(oldData.getName());
			session.update(oldData);
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
			
		}catch(ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to update "+driverInstanceName+", Reason: "
					+EliteExceptionUtils.extractConstraintName(cve.getSQLException()),cve);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update "+driverInstanceName+", Reason: "+e.getMessage(),e);
		}
	}

	public LDAPAuthDriverData getLdapAuthDriverDataByDriverInstanceId(String driverInstanceId) throws DataManagerException {

		try{		
			Session session = getSession();
			Criteria criteria = session.createCriteria(LDAPAuthDriverData.class);
			LDAPAuthDriverData driverData = (LDAPAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(driverData == null) {
				throw new InvalidValueException("LDAP File Auth Driver not found.");	
			}
			return driverData;

		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive LDAP Auth driver, Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive LDAP Auth driver, Reason: "+e.getMessage(),e);
		}

	}

	@Override
	public void updateLDAPAuthDriverByName(DriverInstanceData driverInstance,LDAPAuthDriverData updatedLDAPAuthDriverData, IStaffData staffData,String name)
			throws DataManagerException {
			updateLdapAuthDriverData(driverInstance, updatedLDAPAuthDriverData, staffData, DRIVER_INSTANCE_NAME, name);
	}

	@Override
	public void updateLDAPAuthDriverById(DriverInstanceData driverInstance,LDAPAuthDriverData updatedLDAPAuthDriverData, IStaffData staffData)
			throws DataManagerException {
		updateLdapAuthDriverData(driverInstance, updatedLDAPAuthDriverData, staffData, DRIVER_INSTANCE_ID, driverInstance.getDriverInstanceId());
	}

	private void updateLdapAuthDriverData(DriverInstanceData driverInstance,LDAPAuthDriverData ldapAuthDriverData,IStaffData staffData,
			String propertyName, Object value) throws DataManagerException {

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			
			Set<LDAPAuthDriverData> oldLdapAuthFieldMapSet = new HashSet<LDAPAuthDriverData>();
			
			DriverInstanceData driverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(propertyName,value)).uniqueResult();
			if(driverInstanceData ==  null ){
				throw new InvalidValueException("Driver Instance not found.");
			}
			oldLdapAuthFieldMapSet.add(ldapAuthDriverData);
			driverInstance.setLdapdetail(oldLdapAuthFieldMapSet);
			JSONArray jsonArray=ObjectDiffer.diff(driverInstanceData,driverInstance);
			
			criteria = session.createCriteria(LDAPAuthDriverData.class);
			LDAPAuthDriverData ldapAuthDriver = (LDAPAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceData.getDriverInstanceId())).uniqueResult();
			
			if(ldapAuthDriver == null){
				throw new InvalidValueException("LDAP Auth Driver not found.");
			}
			
			criteria = session.createCriteria(LDAPAuthFieldMapData.class);
			List<LDAPAuthFieldMapData> feildDataList = criteria.add(Restrictions.eq(LDAP_DRIVER_ID, ldapAuthDriver.getLdapDriverId())).list();
			if(Collectionz.isNullOrEmpty(feildDataList) == false){
				int size = feildDataList.size();
				for (int i = 0; i < size; i++) {
					LDAPAuthFieldMapData feildData = feildDataList.get(i);
					session.delete(feildData);
					session.flush();
				}
				
			}
			List<LDAPAuthFieldMapData> tempFeildList = ldapAuthDriverData.getLdapAuthDriverFieldMapList();

			////  feild map data related changes
			if(Collectionz.isNullOrEmpty(tempFeildList) == false){
				Iterator<LDAPAuthFieldMapData> itr = tempFeildList.iterator();
				int orderNumber = 1;
				while(itr.hasNext()){
					LDAPAuthFieldMapData temppfeildMapData = itr.next();
					temppfeildMapData.setLdapDriverId(ldapAuthDriver.getLdapDriverId());
					temppfeildMapData.setOrderNumber(orderNumber++);
					session.save(temppfeildMapData);
					session.flush();
				}
			}

			// updating ldap driver
			ldapAuthDriver.setDriverInstanceId(driverInstanceData.getDriverInstanceId());
			//tempData.setDsStatusCheckInterval(ldapAuthDriverData.getDsStatusCheckInterval());
			ldapAuthDriver.setExpiryDatePattern(ldapAuthDriverData.getExpiryDatePattern());
			ldapAuthDriver.setLdapDsId(ldapAuthDriverData.getLdapDsId());
			ldapAuthDriver.setPasswordDecryptType(ldapAuthDriverData.getPasswordDecryptType());
			ldapAuthDriver.setQueryMaxExecTime(ldapAuthDriverData.getQueryMaxExecTime());
			ldapAuthDriver.setMaxQueryTimeoutCount(ldapAuthDriverData.getMaxQueryTimeoutCount());
			ldapAuthDriver.setUserIdentityAttributes(ldapAuthDriverData.getUserIdentityAttributes());
			ldapAuthDriver.setSearchFilter(ldapAuthDriverData.getSearchFilter());
			ldapAuthDriver.setSearchScope(ldapAuthDriverData.getSearchScope());
			ldapAuthDriver.setCheckValidate(true);
			session.update(ldapAuthDriver);

			ldapAuthDriver.setLdapAuthDriverFieldMapList(tempFeildList);
			
			/// updating driverInstance

			driverInstanceData.setDescription(driverInstance.getDescription());
			driverInstanceData.setName(driverInstance.getName());
			driverInstanceData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			driverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
		
			session.update(driverInstanceData);
			session.flush();
			
			staffData.setAuditId(driverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstance.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);

		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update LDAP Auth Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update LDAP Auth Driver, Reason: " +  hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to  update LDAP Auth Driver, Reason: "+e.getMessage(),e);
		}
	}

	public void createDetailLocalAcctDriver(DriverInstanceData driverInstanceData,DetailLocalAcctDriverData detailLocalAcctDriverData)throws DataManagerException {

		try{
			Session session = getSession();
			boolean isDriverExist = getDriverInstancesByName(driverInstanceData.getName());
			if(isDriverExist){
				throw new DuplicateParameterFoundExcpetion("The driver with this name already exists.");
			}

			String auditId= UUIDGenerator.generate();
			
			driverInstanceData.setAuditUId(auditId);
			Set<DetailLocalAcctDriverData> detailLocalAcctDriverDatas = new HashSet<DetailLocalAcctDriverData>();
			detailLocalAcctDriverDatas.add(detailLocalAcctDriverData);
			driverInstanceData.setDetaillocalset(detailLocalAcctDriverDatas);
			
			session.save(driverInstanceData);

			String driverInstanceId = driverInstanceData.getDriverInstanceId();
			Set<DetailLocalAttrRelationData> relationalSet = detailLocalAcctDriverData.getDetailLocalSet();
			detailLocalAcctDriverData.setDetailLocalSet(null);
			detailLocalAcctDriverData.setDriverInstanceId(driverInstanceId);

			session.save(detailLocalAcctDriverData);

			String detailLocalId = detailLocalAcctDriverData.getDetailLocalId();


			Iterator<DetailLocalAttrRelationData> itr = relationalSet.iterator();

			while(itr.hasNext()){

				DetailLocalAttrRelationData data = itr.next();			
				data.setDetailLocalId(detailLocalId);
				session.save(data);

			}

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}


	}

	public String createDBAcctDriver(DriverInstanceData driverInstanceData,DBAcctDriverData dbAcctDriverData) throws DataManagerException {
		try{
			Session session = getSession();
			session.clear();

			String auditId= UUIDGenerator.generate();
			Set<DBAcctDriverData> dbAcctDriverDataSet = new HashSet<DBAcctDriverData>();
			dbAcctDriverDataSet.add(dbAcctDriverData);
			
			driverInstanceData.setDbacctset(dbAcctDriverDataSet);
			driverInstanceData.setAuditUId(auditId);
			
			session.save(driverInstanceData);

			String driverInstanceId = driverInstanceData.getDriverInstanceId();
			dbAcctDriverData.setDriverInstanceId(driverInstanceId);		
			List<DBAcctFeildMapData> tempfeilMapList = dbAcctDriverData.getDbAcctFieldMapList();

			session.save(dbAcctDriverData);

			if(Collectionz.isNullOrEmpty(tempfeilMapList) == false){
				Iterator<DBAcctFeildMapData> itr = tempfeilMapList.iterator();
				int orderNumber = 1;
				while(itr.hasNext()){
					DBAcctFeildMapData fieldMapData = itr.next();
					fieldMapData.setOpenDbAcctId(dbAcctDriverData.getOpenDbAcctId());
					fieldMapData.setOrderNumber(orderNumber++);
					session.save(fieldMapData);
				}
			}
				session.flush();
				session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}
		catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON +  hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON +e.getMessage(),e);
		}
		return driverInstanceData.getName();


	}

	public String createClassicCSVDriverData(ClassicCSVAcctDriverData classicCSVAcctDriverData,DriverInstanceData driverInstanceData) throws DataManagerException {
		String driverName = "Driver Instance";
		try{
			Session session = getSession();
			session.clear();
			driverName = driverInstanceData.getName();
			String auditId= UUIDGenerator.generate();
			
			driverInstanceData.setAuditUId(auditId);
			
			Set<ClassicCSVAcctDriverData> classicCSVAcctDriverDataSet = new HashSet<ClassicCSVAcctDriverData>();
			classicCSVAcctDriverDataSet.add(classicCSVAcctDriverData);
			driverInstanceData.setCsvset(classicCSVAcctDriverDataSet);
			
			session.save(driverInstanceData);

			String driverInstanceId = driverInstanceData.getDriverInstanceId();
			classicCSVAcctDriverData.setDriverInstanceId(driverInstanceId);
			List<ClassicCSVAttrRelationData> tempfeildMapList = classicCSVAcctDriverData.getCsvAttrRelList();
			List<ClassicCSVStripPattRelData> tempPattMapList = classicCSVAcctDriverData.getCsvPattRelList();

			classicCSVAcctDriverData.setCsvAttrRelList(null);
			classicCSVAcctDriverData.setCsvPattRelList(null);

			session.save(classicCSVAcctDriverData);
			session.flush();
			session.clear();

			int orderNumber = 1;
			if(tempfeildMapList != null){
				Iterator<ClassicCSVAttrRelationData> itr = tempfeildMapList.iterator();
				while(itr.hasNext()){
					ClassicCSVAttrRelationData tempData = itr.next();
					tempData.setClassicCsvId(classicCSVAcctDriverData.getClassicCsvId());
					tempData.setOrderNumber(orderNumber++);
					session.save(tempData);
					session.flush();
					session.clear();
				}

			}

			orderNumber = 1;
			if(tempPattMapList != null){
				Iterator<ClassicCSVStripPattRelData> it = tempPattMapList.iterator();
				while (it.hasNext()) {
					ClassicCSVStripPattRelData tempMappingData = it.next();
					tempMappingData.setClassicCsvId(classicCSVAcctDriverData.getClassicCsvId());
					tempMappingData.setOrderNumber(orderNumber++);
					session.save(tempMappingData);
					session.flush();
					session.clear();

				}
			}
			session.flush();
			session.clear();

		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException hbe) {
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON +  hbe.getMessage(),hbe);
		} catch(Exception e) {
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON +e.getMessage(),e);
		}
		return driverName;
	}

	public boolean getDriverInstancesByName(String name) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			List<DriverInstanceData> driverInstanceList = criteria.add(Restrictions.eq(DRIVER_INSTANCE_NAME,name)).list();
			if(driverInstanceList != null && driverInstanceList.size() > 0){
				return true;
			}
			return false;
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

	}

	public DetailLocalAcctDriverData getDetailLocalDriverByDriverInstanceId(String driverInstanceId)throws DataManagerException {
		try{
			Session session = getSession();
			DetailLocalAcctDriverData detailLocalDriverData = null;
			Criteria criteria = session.createCriteria(DetailLocalAcctDriverData.class);
			detailLocalDriverData = (DetailLocalAcctDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			return detailLocalDriverData;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	public void updateDetailLocalDriverData(DriverInstanceData driverInstance,DetailLocalAcctDriverData detailLocalDriverData,IStaffData staffData,String actionAlias)throws DataManagerException {

		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			
			Set<DetailLocalAcctDriverData> detailLocalAcctDriverDataSet=new HashSet<DetailLocalAcctDriverData>();
			
			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstance.getDriverInstanceId())).uniqueResult();
			detailLocalDriverData.setDetailLocalSet(new HashSet<DetailLocalAttrRelationData>(detailLocalDriverData.getMappingList()));
		
			detailLocalAcctDriverDataSet.add(detailLocalDriverData);
			driverInstance.setDetaillocalset(detailLocalAcctDriverDataSet);
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstance);
			
			if(tempDriverInstanceData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				tempDriverInstanceData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();

			criteria = session.createCriteria(DetailLocalAcctDriverData.class);

			DetailLocalAcctDriverData detailLoc = (DetailLocalAcctDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();

			String detailLocId = detailLoc.getDetailLocalId();

			criteria = session.createCriteria(DetailLocalAttrRelationData.class);
			List<DetailLocalAttrRelationData> attrRelDataList = criteria.add(Restrictions.eq(DETAIL_LOCAL_ID,detailLocId)).list();
			Set<DetailLocalAttrRelationData> attrRelSet=new LinkedHashSet<DetailLocalAttrRelationData>();
			if(attrRelDataList != null){
				for(int i=0;i<attrRelDataList.size();i++){
					DetailLocalAttrRelationData atrRelData = attrRelDataList.get(i);
					session.delete(atrRelData);
					session.flush();
				}
			}

			List<DetailLocalAttrRelationData> mainMappingList = detailLocalDriverData.getMappingList();
			for(int i=0;i<mainMappingList.size();i++){
				DetailLocalAttrRelationData relData = mainMappingList.get(i);
				relData.setDetailLocalId(detailLocId);
				attrRelSet.add(relData);
				session.save(relData);
				session.flush();
			}

			// for detaillocal driver 

			detailLoc.setAllocatingProtocol(detailLocalDriverData.getAllocatingProtocol());
			detailLoc.setArchiveLocation(detailLocalDriverData.getArchiveLocation());
			detailLoc.setAvpairSeperator(detailLocalDriverData.getAvpairSeperator());
			detailLoc.setDefaultDirName(detailLocalDriverData.getDefaultDirName());				
			detailLoc.setEventDateFormat(detailLocalDriverData.getEventDateFormat());
			detailLoc.setFailOverTime(detailLocalDriverData.getFailOverTime());
			detailLoc.setFileName(detailLocalDriverData.getFileName());			
			detailLoc.setTimeBoundry(detailLocalDriverData.getTimeBoundry());
			detailLoc.setTimeBasedRollingUnit(detailLocalDriverData.getTimeBasedRollingUnit());
			detailLoc.setRecordBasedRollingUnit(detailLocalDriverData.getRecordBasedRollingUnit());
			detailLoc.setSizeBasedRollingUnit(detailLocalDriverData.getSizeBasedRollingUnit());
			detailLoc.setFolderName(detailLocalDriverData.getFolderName());
			detailLoc.setGlobalization(detailLocalDriverData.getGlobalization());
			detailLoc.setIpaddress(detailLocalDriverData.getIpaddress());
			detailLoc.setLocation(detailLocalDriverData.getLocation());
			detailLoc.setPassword(detailLocalDriverData.getPassword());			
			detailLoc.setPattern(detailLocalDriverData.getPattern());
			detailLoc.setPostOperation(detailLocalDriverData.getPostOperation());
			detailLoc.setPrefixFileName(detailLocalDriverData.getPrefixFileName());
			detailLoc.setRange(detailLocalDriverData.getRange());
			detailLoc.setRemoteLocation(detailLocalDriverData.getRemoteLocation());
			detailLoc.setUseDictionaryValue(detailLocalDriverData.getUseDictionaryValue());
			detailLoc.setUserName(detailLocalDriverData.getUserName());			
			detailLoc.setWriteAttributes(detailLocalDriverData.getWriteAttributes());
			detailLoc.setDetailLocalSet(attrRelSet);
			session.update(detailLoc);
			session.flush();

			tempDriverInstanceData.setName(driverInstance.getName());
			tempDriverInstanceData.setDescription(driverInstance.getDescription());
			tempDriverInstanceData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			tempDriverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
			session.update(tempDriverInstanceData);
			session.flush();
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch (Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}
	}

	@Override
	public void updateDiameterChargingDriverDataByName(DriverInstanceData driverInstance,DiameterChargingDriverData updatedDiameterChargingDriverData,
			StaffData staffData, String name) throws DataManagerException {
		updateDiameterChargingDriverData(driverInstance, updatedDiameterChargingDriverData, staffData, DRIVER_INSTANCE_NAME, name);	
	}

	@Override
	public void updateDiameterChargingDriverDataById(DriverInstanceData driverInstance,DiameterChargingDriverData updatedDiameterChargingDriverData,
			StaffData staffData) throws DataManagerException {
		updateDiameterChargingDriverData(driverInstance, updatedDiameterChargingDriverData, staffData, DRIVER_INSTANCE_ID, driverInstance.getDriverInstanceId());
	}

	private void updateDiameterChargingDriverData(DriverInstanceData driverInstance,DiameterChargingDriverData diameterChargingDriverData,IStaffData staffData,String property, Object value)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			
			Set<DiameterChargingDriverData> dcDriver=new HashSet<DiameterChargingDriverData>();
			
			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(property,value)).uniqueResult();
			if(tempDriverInstanceData == null) {
				throw new InvalidValueException("Driver Instance not found.");
			}
			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();
			dcDriver.add(diameterChargingDriverData);
			driverInstance.setDiameterChargingDriverSet(dcDriver);
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstance);
			
			criteria = session.createCriteria(DiameterChargingDriverData.class);
			DiameterChargingDriverData dcDriverData = (DiameterChargingDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();
			
			if(dcDriverData == null) {
				throw new InvalidValueException("Diameter Charging Driver not found");
			}
			
			String dcDriverId = dcDriverData.getDcDriverId();

			criteria = session.createCriteria(DiameterChargingDriverRealmsData.class);
			List<DiameterChargingDriverRealmsData> dcDriverRealmsDataList = criteria.add(Restrictions.eq(DC_DRIVER_ID,dcDriverId)).list();
			if(Collectionz.isNullOrEmpty(dcDriverRealmsDataList) == false){
				int size = dcDriverRealmsDataList.size();
				for (int i = 0; i < size; i++) {
					DiameterChargingDriverRealmsData diameterChargingDriverRealmsData = dcDriverRealmsDataList.get(i);
					Set<DiameterChargingDriverVendorData> dcDriverVendorSet = diameterChargingDriverRealmsData.getRealmVendorRelSet();
					Iterator<DiameterChargingDriverVendorData> dcDriverVendorDataItr = dcDriverVendorSet.iterator();
					while(dcDriverVendorDataItr.hasNext()){
						DiameterChargingDriverVendorData diameterChargingDriverVendorData = dcDriverVendorDataItr.next();			
						session.delete(diameterChargingDriverVendorData);
						session.flush();
					}
					diameterChargingDriverRealmsData.setRealmVendorRelSet(null);
					
					Set<DiameterChargingDriverPeerData> diameterChargingDriverPeerSet = diameterChargingDriverRealmsData.getRealmPeerRelSet();
					
					if(Collectionz.isNullOrEmpty(diameterChargingDriverPeerSet) == false) {
						Iterator<DiameterChargingDriverPeerData> diameterDCDriverPeerItr = diameterChargingDriverPeerSet.iterator();
						while(diameterDCDriverPeerItr.hasNext()){
							DiameterChargingDriverPeerData diameterChargingDriverPeerData = diameterDCDriverPeerItr.next();			
							session.delete(diameterChargingDriverPeerData);
							session.flush();
						}
						diameterChargingDriverRealmsData.setRealmPeerRelSet(null);
						session.delete(diameterChargingDriverRealmsData);
						session.flush();
					}
				}
			}
			
			tempDriverInstanceData.setName(driverInstance.getName());
			tempDriverInstanceData.setDescription(driverInstance.getDescription());
			tempDriverInstanceData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			tempDriverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
			session.update(tempDriverInstanceData);
			session.flush();
			
			staffData.setAuditId(tempDriverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstance.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Charging Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update Diameter Charging Driver, Reason: " +  hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to  update Diameter Charging Driver, Reason: "+e.getMessage(),e);
		}
	}

	public DBAcctDriverData getDbAcctDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException {
		try{
			Session session = getSession();
			DBAcctDriverData dbAcctDriverData = null;
			Criteria criteria = session.createCriteria(DBAcctDriverData.class);
			dbAcctDriverData = (DBAcctDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(dbAcctDriverData == null) {
				throw new InvalidValueException("DB Acct Driver not found.");	
			}
			return dbAcctDriverData;

		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive DB Account driver, Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive DB Account driver, Reason: "+e.getMessage(),e);
		}

	}

	@Override
	public void updateDBAcctDriverById(DriverInstanceData driverInstance,DBAcctDriverData dbAcctDriverData, IStaffData staffData)
			throws DataManagerException {
		updateDBAcctDriver(driverInstance, dbAcctDriverData, staffData, DRIVER_INSTANCE_ID, driverInstance.getDriverInstanceId());
	}
	
	@Override
	public void updateDBAcctDriverByName(DriverInstanceData driverInstance,DBAcctDriverData updateDBAcctDriverData,IStaffData staffData,String name) throws DataManagerException {
		updateDBAcctDriver(driverInstance, updateDBAcctDriverData, staffData, DRIVER_INSTANCE_NAME, name);
	}
	
	private void updateDBAcctDriver(DriverInstanceData driverInstance, DBAcctDriverData updateDBAcctDriverData, IStaffData staffData ,
			String property, Object value) throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(property, value)).uniqueResult();
			
			if(tempDriverInstanceData == null){
				throw new InvalidValueException("Driver instance not found");				
			}
			
			Set<DBAcctDriverData> dbAcctDriverSet=new HashSet<DBAcctDriverData>();
			dbAcctDriverSet.add(updateDBAcctDriverData);
			driverInstance.setDbacctset(dbAcctDriverSet);
			
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstance);

			criteria = session.createCriteria(DBAcctDriverData.class);
			
			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();
			DBAcctDriverData dbAcctDriver = (DBAcctDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();
			
			if(dbAcctDriver == null){
				throw new InvalidValueException("DB Acct driver not found");				
			}
			
			String openDbAcctId = dbAcctDriver.getOpenDbAcctId();
			criteria = session.createCriteria(DBAcctFeildMapData.class);
			
			List<DBAcctFeildMapData> attrRelDataList = criteria.add(Restrictions.eq(OPEN_DB_ACCT_ID,openDbAcctId)).list();
			
			if(Collectionz.isNullOrEmpty(attrRelDataList) == false){
				int size = attrRelDataList.size();
				for(int i=0;i<size;i++){
					DBAcctFeildMapData atrRelData = attrRelDataList.get(i);
					session.delete(atrRelData);
					session.flush();
				}
			}

			List<DBAcctFeildMapData> mainMappingList = updateDBAcctDriverData.getDbAcctFieldMapList();
			
			if(Collectionz.isNullOrEmpty(mainMappingList) == false){
				int size = mainMappingList.size();
				int orderNumber = 1;
				for (int i = 0; i < size; i++) {
					DBAcctFeildMapData relData = mainMappingList.get(i);
					relData.setOpenDbAcctId(openDbAcctId);
					relData.setOrderNumber(orderNumber++);
					session.save(relData);
					session.flush();
				}
			}
			
			tempDriverInstanceData.setName(driverInstance.getName());
			tempDriverInstanceData.setDescription(driverInstance.getDescription());
			tempDriverInstanceData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			tempDriverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
			session.update(tempDriverInstanceData);
			session.flush();
			
			// for detaillocal driver 
			dbAcctDriver.setCallEndFieldName(updateDBAcctDriverData.getCallEndFieldName());
			dbAcctDriver.setCallStartFieldName(updateDBAcctDriverData.getCallStartFieldName());
			dbAcctDriver.setCdrIdDbField(updateDBAcctDriverData.getCdrIdDbField());
			dbAcctDriver.setCdrIdSeqName(updateDBAcctDriverData.getCdrIdSeqName());
			dbAcctDriver.setCdrTablename(updateDBAcctDriverData.getCdrTablename());
			dbAcctDriver.setCreateDateFieldName(updateDBAcctDriverData.getCreateDateFieldName());		
			dbAcctDriver.setDatabaseId(updateDBAcctDriverData.getDatabaseId());
			//dbAcctDriver.setDatasourceScantime(dbAcctDriverData.getDatasourceScantime());
			dbAcctDriver.setDatasourceType(updateDBAcctDriverData.getDatasourceType());
			dbAcctDriver.setDbDateField(updateDBAcctDriverData.getDbDateField());
			dbAcctDriver.setDbQueryTimeout(updateDBAcctDriverData.getDbQueryTimeout());						
			dbAcctDriver.setEnabled(updateDBAcctDriverData.getEnabled());
			dbAcctDriver.setInterimCdrIdDbField(updateDBAcctDriverData.getInterimCdrIdDbField());
			dbAcctDriver.setInterimCdrIdSeqName(updateDBAcctDriverData.getInterimCdrIdSeqName());
			dbAcctDriver.setInterimTablename(updateDBAcctDriverData.getInterimTablename());
			dbAcctDriver.setLastModifiedDateFieldName(updateDBAcctDriverData.getLastModifiedDateFieldName());
			dbAcctDriver.setMaxQueryTimeoutCount(updateDBAcctDriverData.getMaxQueryTimeoutCount());
			dbAcctDriver.setMultivalDelimeter(updateDBAcctDriverData.getMultivalDelimeter());		
			dbAcctDriver.setRemoveInterimOnStop(updateDBAcctDriverData.getRemoveInterimOnStop());
			dbAcctDriver.setRemoveTunnelLinkStopRec(updateDBAcctDriverData.getRemoveTunnelLinkStopRec());
			dbAcctDriver.setRemoveTunnelStopRec(updateDBAcctDriverData.getRemoveTunnelStopRec());
			dbAcctDriver.setStoreInterimRec(updateDBAcctDriverData.getStoreInterimRec());
			dbAcctDriver.setStoreStopRec(updateDBAcctDriverData.getStoreStopRec());
			dbAcctDriver.setStoreTunnelLinkRejectRec(updateDBAcctDriverData.getStoreTunnelLinkRejectRec());
			dbAcctDriver.setStoreTunnelLinkStartRec(updateDBAcctDriverData.getStoreTunnelLinkStartRec());
			dbAcctDriver.setStoreTunnelLinkStopRec(updateDBAcctDriverData.getStoreTunnelLinkStopRec());
			dbAcctDriver.setStoreTunnelRejectRec(updateDBAcctDriverData.getStoreTunnelRejectRec());
			dbAcctDriver.setStoreTunnelStartRec(updateDBAcctDriverData.getStoreTunnelStartRec());
			dbAcctDriver.setStoreTunnelStopRec(updateDBAcctDriverData.getStoreTunnelStopRec());
			dbAcctDriver.setStoreAllCdr(updateDBAcctDriverData.getStoreAllCdr());
			session.update(dbAcctDriver);
			session.flush();

			staffData.setAuditId(tempDriverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstance.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update DB Acct Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update DB Acct Driver, Reason: " +  hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to  update DB Acct Driver, Reason: "+e.getMessage(),e);
		}
	}
	
	@Override
	public void updateMapGWAuthDriverDataByName(DriverInstanceData driverInstance,MappingGatewayAuthDriverData updatedMappingGatewayAuthDriverData,
			IStaffData staffData, String idOrName) throws DataManagerException {
		
		updateMapGWAuthDriverData(driverInstance,updatedMappingGatewayAuthDriverData, staffData,DRIVER_INSTANCE_NAME, idOrName);
	}

	@Override
	public void updateMapGWAuthDriverDataById(DriverInstanceData driverInstance,MappingGatewayAuthDriverData updatedMappingGatewayAuthDriverData,
			IStaffData staffData) throws DataManagerException {
		
		updateMapGWAuthDriverData(driverInstance,updatedMappingGatewayAuthDriverData, staffData,DRIVER_INSTANCE_ID, driverInstance.getDriverInstanceId());
	}
	private void updateMapGWAuthDriverData(DriverInstanceData driverInstance,MappingGatewayAuthDriverData updatedMappingGatewayAuthDriverData,
			IStaffData staffData, String propertyName, Object value)throws DataManagerException {
		
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(propertyName,value)).uniqueResult();
			
			Set<MappingGatewayAuthDriverData> mappingGatewayAuthDriverDataSet=new HashSet<MappingGatewayAuthDriverData>();
			
			mappingGatewayAuthDriverDataSet.add(updatedMappingGatewayAuthDriverData);
			driverInstance.setMapGatewaySet(mappingGatewayAuthDriverDataSet);
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstance);
			
			if(tempDriverInstanceData == null){
				throw new InvalidValueException("DB Acct driver not found");	
			}
			
			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();
			criteria = session.createCriteria(MappingGatewayAuthDriverData.class);
			MappingGatewayAuthDriverData mappingGatewayData = (MappingGatewayAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();
			String mapGWAuthId = mappingGatewayData.getMapGWAuthid();

			criteria = session.createCriteria(GatewayFieldMapData.class);
			List<GatewayFieldMapData> feildMapList = criteria.add(Restrictions.eq(MAP_AUTH_ID,mappingGatewayData.getMapGWAuthid())).list();
			if(Collectionz.isNullOrEmpty(feildMapList) == false){
				int size = feildMapList.size();
				for(int j=0;j<size;j++){
					GatewayFieldMapData tempDatata = feildMapList.get(j);
					session.delete(tempDatata);
					session.flush();
				}
			}
			List<GatewayFieldMapData> tempFeildlist = updatedMappingGatewayAuthDriverData.getGatewayFieldList();
			if(Collectionz.isNullOrEmpty(tempFeildlist) == false){
				int orderNumber = 1;
				Iterator<GatewayFieldMapData> itr = tempFeildlist.iterator();
				while(itr.hasNext()){			
					GatewayFieldMapData data = itr.next();
					data.setMapAuthId(mapGWAuthId);
					data.setOrderNumber(orderNumber++);
					session.save(data);
					session.flush();
				}
			}

			//For MapGateWay Driver
			mappingGatewayData.setLocalHostId(updatedMappingGatewayAuthDriverData.getLocalHostId());
			mappingGatewayData.setLocalHostIp(updatedMappingGatewayAuthDriverData.getLocalHostIp());
			mappingGatewayData.setLocalHostPort(updatedMappingGatewayAuthDriverData.getLocalHostPort());
			mappingGatewayData.setRemoteHostId(updatedMappingGatewayAuthDriverData.getRemoteHostId());
			mappingGatewayData.setRemoteHostIp(updatedMappingGatewayAuthDriverData.getRemoteHostIp());
			mappingGatewayData.setRemoteHostPort(updatedMappingGatewayAuthDriverData.getRemoteHostPort());
			mappingGatewayData.setMaxQueryTimeoutCount(updatedMappingGatewayAuthDriverData.getMaxQueryTimeoutCount());
			mappingGatewayData.setMapGwConnPoolSize(updatedMappingGatewayAuthDriverData.getMapGwConnPoolSize());
			mappingGatewayData.setRequestTimeout(updatedMappingGatewayAuthDriverData.getRequestTimeout());
			mappingGatewayData.setStatusCheckDuration(updatedMappingGatewayAuthDriverData.getStatusCheckDuration());
			mappingGatewayData.setUserIdentityAttributes(updatedMappingGatewayAuthDriverData.getUserIdentityAttributes());
			mappingGatewayData.setSendAuthInfo(updatedMappingGatewayAuthDriverData.getSendAuthInfo());
			mappingGatewayData.setNumberOfTriplets(updatedMappingGatewayAuthDriverData.getNumberOfTriplets());
			mappingGatewayData.setCheckValidate(true);
			session.update(mappingGatewayData);
			session.flush();

			tempDriverInstanceData.setName(driverInstance.getName());
			tempDriverInstanceData.setDescription(driverInstance.getDescription());
			tempDriverInstanceData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			tempDriverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
			
			session.update(tempDriverInstanceData);
			session.flush();

			staffData.setAuditId(tempDriverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstance.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Map Gateway Auth Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update Map Gateway Auth Driver, Reason: " +  hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update Map Gateway Auth Driver, Reason: "+e.getMessage(),e);
		}
	}

	public void updateDCDriverData(DriverInstanceData driverInstance,DiameterChargingDriverData diameterChargingDriverData,IStaffData staffData,String actionAlias) throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstance.getDriverInstanceId())).uniqueResult();
			
			Set<DiameterChargingDriverData> diameterChargingDriverDataSet=new HashSet<DiameterChargingDriverData>();
			
			diameterChargingDriverDataSet.add(diameterChargingDriverData);
			driverInstance.setDiameterChargingDriverSet(diameterChargingDriverDataSet);
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstance);
			
			if(tempDriverInstanceData.getAuditUId() == null){
				String auditId= UUIDGenerator.generate();
				tempDriverInstanceData.setAuditUId(auditId);
				staffData.setAuditId(auditId);
			}
			
			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();
			criteria = session.createCriteria(DiameterChargingDriverData.class);
			DiameterChargingDriverData diameterChargingData = (DiameterChargingDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();

			//For Diameter Charging Driver

			diameterChargingData.setDisConnectUrl(diameterChargingDriverData.getDisConnectUrl());
			diameterChargingData.setTransMapConfId(diameterChargingDriverData.getTransMapConfId());
			
			session.update(diameterChargingData);
			session.flush();

			tempDriverInstanceData.setName(driverInstance.getName());
			tempDriverInstanceData.setDescription(driverInstance.getDescription());
			tempDriverInstanceData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			tempDriverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
			session.update(tempDriverInstanceData);
			session.flush();
			
			doAuditingJson(jsonArray.toString(),staffData,actionAlias);
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch (Exception e) {
			throw new DataManagerException(e.getMessage(),e);
		}	
	}

	public ClassicCSVAcctDriverData getClassicCsvDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException {

		try{
			Session session = getSession();
			ClassicCSVAcctDriverData classicCSVAcctDriverData = null;
			Criteria criteria = session.createCriteria(ClassicCSVAcctDriverData.class);
			classicCSVAcctDriverData = (ClassicCSVAcctDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(classicCSVAcctDriverData == null) {
				throw new InvalidValueException("Classic CSV Acct Driver not found.");	
			}
			return classicCSVAcctDriverData;

		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive Classic CSV driver data, Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive Classic CSV driver data, Reason: "+e.getMessage(),e);
		}
	}

	public MappingGatewayAuthDriverData getMappingGWDataByDriverInstanceId(String driverInstanceId) throws DataManagerException {
		try{
			Session session = getSession();
			MappingGatewayAuthDriverData mappingGatewayAuthDriverData = null;
			Criteria criteria = session.createCriteria(MappingGatewayAuthDriverData.class);
			mappingGatewayAuthDriverData = (MappingGatewayAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(mappingGatewayAuthDriverData == null) {
				throw new InvalidValueException("Mapping Gateway Auth Driver not found.");	
			}
			return mappingGatewayAuthDriverData;

		}catch(HibernateException hbe){
			throw new DataManagerException("Failed to retrive Map Gateway Auth driver, Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException("Failed to retrive Map Gateway Auth driver, Reason: "+e.getMessage(),e);
		}
	}

	public DiameterChargingDriverData getDiameterChargingDataByDriverInstanceId(String driverInstanceId) throws DataManagerException{
		try{
			Session session = getSession();
			DiameterChargingDriverData diameterChargingDriverData = null;
			Criteria criteria = session.createCriteria(DiameterChargingDriverData.class);
			diameterChargingDriverData = (DiameterChargingDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(diameterChargingDriverData == null) {
				throw new InvalidValueException("Diameter Charging Driver not found.");	
			}
			return diameterChargingDriverData;
		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	public DBAuthDriverData getDBAuthDriverData(Long dbAuthId)throws DataManagerException {
		DBAuthDriverData dbAuthDriverData;
		try{
			Session session = getSession();

			dbAuthDriverData =(DBAuthDriverData) session.createCriteria(DBAuthDriverData.class).add(Restrictions.eq(DB_AUTH_ID,dbAuthId)).uniqueResult();
			if(dbAuthDriverData == null) {
				throw new InvalidValueException("DB Auth Driver not found.");	
			}

		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to retrive DB Auth driver data, Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to retrive DB Auth driver data, Reason: "+e.getMessage(),e);
		}
		return dbAuthDriverData;
	}

	public String createWebServiceAuthDriver(DriverInstanceData driverInstance,WebServiceAuthDriverData webServiceAuthDriverData)throws DataManagerException {
		try{
			Session session = getSession();
			session.clear();

			String auditId= UUIDGenerator.generate();
			
			driverInstance.setAuditUId(auditId);
			
			Set<WebServiceAuthDriverData> webServiceAuthDriverDataSet = new HashSet<WebServiceAuthDriverData>();
			webServiceAuthDriverDataSet.add(webServiceAuthDriverData);
			driverInstance.setWebServiceAuthDriverSet(webServiceAuthDriverDataSet);

			session.save(driverInstance);			

			String driverInstanceId = driverInstance.getDriverInstanceId();
			webServiceAuthDriverData.setDriverInstanceId(driverInstanceId);
			
			session.save(webServiceAuthDriverData);
			List<WebMethodKeyMapRelData> webMethodKeyMapList = webServiceAuthDriverData.getWebMethodKeyDataList();
			
			if( Collectionz.isNullOrEmpty(webMethodKeyMapList) == false ){
				int orderNumber = 1;
				Iterator<WebMethodKeyMapRelData> iterator = webMethodKeyMapList.iterator();
				while(iterator.hasNext()){
					WebMethodKeyMapRelData data  =  iterator.next();
					data.setWsAuthDriverId(webServiceAuthDriverData.getWsAuthDriverId());
					data.setOrderNumber(orderNumber++);
					session.save(data);
				}
			}
//			if this property is true then it will not validate after operation performed.
			webServiceAuthDriverData.setCheckValidate(true);
			session.flush();
			session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstance.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstance.getName() + REASON +  hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstance.getName() + REASON +e.getMessage(),e);
		}
		return driverInstance.getName();
	}

	public WebServiceAuthDriverData getWebServiceDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException {
		try{	

			Session session = getSession();
			Criteria criteria = session.createCriteria(WebServiceAuthDriverData.class);
			WebServiceAuthDriverData driverData = (WebServiceAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(driverData == null) {
				throw new InvalidValueException("Web service Auth Driver not found.");	
			}
			return driverData;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);

		}
	}
	@Override
	public void updateWebServiceAuthDriverDataByName( DriverInstanceData driverInstance, WebServiceAuthDriverData updatedWebServiceAuthDriverData, IStaffData staffData, String name) throws DataManagerException {
		updateWebServiceAuthDriver(driverInstance, updatedWebServiceAuthDriverData, staffData, DRIVER_INSTANCE_NAME, name);
	}

	@Override
	public void updateWebServiceAuthDriverDataById( DriverInstanceData driverInstance, WebServiceAuthDriverData updatedWebServiceAuthDriverData, IStaffData staffData) throws DataManagerException {
		 updateWebServiceAuthDriver(driverInstance, updatedWebServiceAuthDriverData, staffData, DRIVER_INSTANCE_ID, driverInstance.getDriverInstanceId());
	}

	public void updateWebServiceAuthDriver(DriverInstanceData driverInstance, WebServiceAuthDriverData webServiceAuthDriverData, IStaffData staffData, String propertyName, Object value)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);

			/// updating driver instance
			DriverInstanceData driverInstanceData =  (DriverInstanceData)criteria.add(Restrictions.eq(propertyName, value)).uniqueResult();
			
			if(driverInstanceData == null){
				throw new InvalidValueException("Driver Instance not found.");
			}
			String driverInstanceId = driverInstanceData.getDriverInstanceId();
			
			Set<WebServiceAuthDriverData> webServiceAuthDriverDataSet = new HashSet<WebServiceAuthDriverData>();
			webServiceAuthDriverDataSet.add(webServiceAuthDriverData);
			driverInstance.setWebServiceAuthDriverSet(webServiceAuthDriverDataSet);

			JSONArray jsonArray = ObjectDiffer.diff(driverInstanceData,driverInstance);

			criteria = session.createCriteria(WebServiceAuthDriverData.class);
			WebServiceAuthDriverData newWebServiceAuthDriverData = (WebServiceAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();
			
			if(newWebServiceAuthDriverData == null){
				throw new InvalidValueException("Web Service Auth driver not found.");
				
			}
			List<WebMethodKeyMapRelData> webMethodKeyDataList  = newWebServiceAuthDriverData.getWebMethodKeyDataList();
			
			if( Collectionz.isNullOrEmpty(webMethodKeyDataList) == false ){
				Iterator<WebMethodKeyMapRelData> iterator = webMethodKeyDataList.iterator();
				while(iterator.hasNext()){
					session.delete(iterator.next());
				}
			}
			session.flush();
			
			webMethodKeyDataList = webServiceAuthDriverData.getWebMethodKeyDataList();
			
			if( Collectionz.isNullOrEmpty(webMethodKeyDataList) == false ){
				int orderNumber = 1;
				Iterator<WebMethodKeyMapRelData> iterator = webMethodKeyDataList.iterator();
				while(iterator.hasNext()){
					WebMethodKeyMapRelData data = iterator.next();
					data.setWsAuthDriverId(newWebServiceAuthDriverData.getWsAuthDriverId());
					data.setOrderNumber(orderNumber++);
					session.save(data);
				}
			}
			session.flush();
			newWebServiceAuthDriverData.setWebMethodKeyDataList(webMethodKeyDataList);
			
			
			newWebServiceAuthDriverData.setServiceAddress(webServiceAuthDriverData.getServiceAddress());
			newWebServiceAuthDriverData.setImsiAttribute(webServiceAuthDriverData.getImsiAttribute());
			newWebServiceAuthDriverData.setMaxQueryTimeoutCnt(webServiceAuthDriverData.getMaxQueryTimeoutCnt());
			newWebServiceAuthDriverData.setStatusChkDuration(webServiceAuthDriverData.getStatusChkDuration());
			newWebServiceAuthDriverData.setUserIdentityAttributes(webServiceAuthDriverData.getUserIdentityAttributes());
//			if this property is true then it will not validate after operation performed.
			newWebServiceAuthDriverData.setCheckValidate(true);
			session.update(newWebServiceAuthDriverData);
		
			driverInstanceData.setName(driverInstance.getName());
			driverInstanceData.setDescription(driverInstance.getDescription());
			driverInstanceData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			driverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
			session.update(driverInstanceData);

			staffData.setAuditId(driverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstance.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
			session.flush();

		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Web Service Auth Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update Web Service Auth Driver, Reason: "+e.getMessage(),e);
		} catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update Web Service Auth Driver, Reason: "+e.getMessage(),e);
		}
	}

	public String createCrestelChargingDriver(DriverInstanceData driverInstanceData,CrestelChargingDriverData crestelChargingDriverData) throws DataManagerException {
		try{
			Session session = getSession();
			session.clear();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);

			String auditId= UUIDGenerator.generate();
			
			driverInstanceData.setAuditUId(auditId);
			Set<CrestelChargingDriverData> chargingDriverDataSet = new HashSet<CrestelChargingDriverData>();
			chargingDriverDataSet.add(crestelChargingDriverData);
			driverInstanceData.setCrestelChargingSet(chargingDriverDataSet);
			session.save(driverInstanceData);

			String driverInstanceId = driverInstanceData.getDriverInstanceId();
			crestelChargingDriverData.setDriverInstanceId(driverInstanceId);		
			List<CrestelChargingDriverPropsData> tempfeilMapList = crestelChargingDriverData.getJndiPropValMapList();
			crestelChargingDriverData.setJndiPropValMapList(null);

			session.save(crestelChargingDriverData);
			
			if(Collectionz.isNullOrEmpty(tempfeilMapList) == false){
				Iterator<CrestelChargingDriverPropsData> itr = tempfeilMapList.iterator();
				int orderNumber = 1;
				while(itr.hasNext()){
					CrestelChargingDriverPropsData feildMapData = itr.next();
					feildMapData.setCrestelChargingDriverId(crestelChargingDriverData.getCrestelChargingDriverId());
					feildMapData.setOrderNumber(orderNumber++);
					session.save(feildMapData);
					session.flush();
					session.clear();
				}
			}
		} catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() +REASON +  hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverInstanceData.getName() + REASON +e.getMessage(),e);
		}
		return driverInstanceData.getName();
	}

	public CrestelChargingDriverData getCrestelChargingDriverByDriverInstanceId(String driverInstanceId) throws DataManagerException {
		try{
			Session session = getSession();
			CrestelChargingDriverData crestelChargingDriverData = null;
			Criteria criteria = session.createCriteria(CrestelChargingDriverData.class);
			crestelChargingDriverData = (CrestelChargingDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId)).uniqueResult();
			if(crestelChargingDriverData == null) {
				throw new InvalidValueException("Crestel Charging Driver not found.");	
			}
			return crestelChargingDriverData;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}

	@Override
	public void updateCrestelChargingDriverByName(DriverInstanceData driverInstance,CrestelChargingDriverData updatedCrestelChargingDriverData,
			StaffData staffData, String name) throws DataManagerException {
	
			updateCrestelChargingDriver(driverInstance,updatedCrestelChargingDriverData, staffData,DRIVER_INSTANCE_NAME, name);
	}

	@Override
	public void updateCrestelChargingDriverById(DriverInstanceData driverInstance,CrestelChargingDriverData updatedCrestelChargingDriverData,
			StaffData staffData) throws DataManagerException {
			
			updateCrestelChargingDriver(driverInstance,updatedCrestelChargingDriverData, staffData,DRIVER_INSTANCE_ID, driverInstance.getDriverInstanceId());
	}
	
	private void updateCrestelChargingDriver(DriverInstanceData driverInstance,CrestelChargingDriverData chargingDrivedata,IStaffData staffData,String property, Object value) throws DataManagerException {
		try{
			Session session = getSession();
			/*
			 * update Driverinstance Data
			 */
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);

			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(property, value)).uniqueResult();
		
			if(tempDriverInstanceData == null ){
				throw new InvalidValueException("Driver Instance Not found");
			}
			Set<CrestelChargingDriverData> crestelChargingDriverDataSet=new HashSet<CrestelChargingDriverData>();
			
			crestelChargingDriverDataSet.add(chargingDrivedata);
			driverInstance.setCrestelChargingSet(crestelChargingDriverDataSet);
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstance);
			
			tempDriverInstanceData.setName(driverInstance.getName());
			tempDriverInstanceData.setDescription(driverInstance.getDescription());
			tempDriverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
			
			session.update(tempDriverInstanceData);
			session.flush();

			/*
			 * update CrestelRatingDriverData 
			 */
			
			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();
			criteria = session.createCriteria(CrestelChargingDriverData.class);

			CrestelChargingDriverData chargingDriverData = (CrestelChargingDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();
		
			if(chargingDrivedata == null) {
				throw new InvalidValueException("Charging Driver not found.");
			}
			
			chargingDriverData.setTransMapConfId(chargingDrivedata.getTransMapConfId());
			chargingDriverData.setInstanceNumber(chargingDrivedata.getInstanceNumber());
			session.update(chargingDriverData);
			session.flush();

			/*
			 * update RatingDriverPropsData List
			 */

			Criteria chargingDriverPropsCriteria = session.createCriteria(CrestelChargingDriverPropsData.class);
			chargingDriverPropsCriteria.add(Restrictions.eq(CRESTEL_CHARGING_DRIVER_ID,chargingDriverData.getCrestelChargingDriverId()));

			List<CrestelChargingDriverPropsData> chargingDriverPropsList = chargingDriverPropsCriteria.list();
			deleteObjectList(chargingDriverPropsList, session);

			List<CrestelChargingDriverPropsData> tempfeilMapList = chargingDrivedata.getJndiPropValMapList();
			if(Collectionz.isNullOrEmpty(tempfeilMapList) == false){
				Iterator<CrestelChargingDriverPropsData> itr = tempfeilMapList.iterator();
				int orderNumber = 1;
				while(itr.hasNext()){
					CrestelChargingDriverPropsData feildMapData = itr.next();
					feildMapData.setCrestelChargingDriverId(chargingDriverData.getCrestelChargingDriverId());
					feildMapData.setOrderNumber(orderNumber++);
					session.save(feildMapData);
					session.flush();
				}
			}
			
			staffData.setAuditId(tempDriverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstance.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
			
		}catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Crestel Charging Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to update Crestel Charging Driver, Reason: " +  hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update Crestel Charging Driver, Reason: "+e.getMessage(),e);
		}
	}
	
	public List<WebMethodKeyData> getWebMethodKeyDataList() throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(WebMethodKeyData.class);
			List<WebMethodKeyData> webMethodKeyDataList = criteria.add(Restrictions.eq(STATUS,"Y")).addOrder(Order.asc(SERIAL_NUMBER)).list();
			return webMethodKeyDataList;

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}
	}
	
	public List<DriverInstanceData> getCacheableDriverData() throws DataManagerException {
		List<DriverInstanceData> driverInstanceDataList = null;
		try{
			Session session = getSession();
			Criteria  criteria = session.createCriteria(DRIVER_INSTANCE_DATA)
			.add(Restrictions.eq(CACHEABLE,"true"));
			driverInstanceDataList = criteria.list();
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return driverInstanceDataList;	
	}

	@Override
	public List<LogicalNameDriverRelData> getLogicalNameDriverRelData(Long driverTypeId)
			throws DataManagerException {
		List<LogicalNameDriverRelData> driverInstanceDataList = null;
		try{
			Session session = getSession();
			Criteria  criteria = session.createCriteria(LogicalNameDriverRelData.class);
			criteria.add(Restrictions.eq(DRIVER_TYPE_ID,driverTypeId))
			.setFetchMode("logicalNameValuePoolData", FetchMode.JOIN);  
			
			driverInstanceDataList = criteria.list();
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(),he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return driverInstanceDataList;
	}
	
	public DriverTypeData getDriverTypeData(long driverTypeId) throws DataManagerException{
		DriverTypeData driverTypeData = null;
		try{
			Session session = getSession();
			driverTypeData = (DriverTypeData) session.get(DriverTypeData.class, driverTypeId);			
		}catch(HibernateException he){
			throw new DataManagerException(he.getMessage(), he);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(), e);
		}
		return driverTypeData;
	}
	@Override
	public List<ServerCertificateData> getServerCertificateDataList() throws DataManagerException {
		List<ServerCertificateData> lstServerCertificateData;

		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(ServerCertificateData.class);
			criteria.addOrder(Order.asc("serverCertificateId"));
			lstServerCertificateData = criteria.list();
		}catch(HibernateException hExp){
		    throw new DataManagerException(hExp.getMessage(), hExp);
		}catch(Exception exp){
			throw new DataManagerException(exp.getMessage(), exp);
		}
		Logger.logDebug("MODULE", "SUCEESFULL TRANSACTION....");
		
		return lstServerCertificateData;  
	}
	
	@Override
	public List<DriverTypeData> getDriverTypeList(Long serviceTypeId) throws DataManagerException {
		List<DriverTypeData> driverTypeDataList=null ;
		try{
			Session session = getSession();
			driverTypeDataList = session.createCriteria(DriverTypeData.class).add(Restrictions.eq(DRIVER_SERVICE_TYPE_ID,serviceTypeId)).list();

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return driverTypeDataList;
		
	}

	@Override
	public List<DriverTypeData> getAcctDriverTypeList(Long serviceTypeId) throws DataManagerException {
		List<DriverTypeData> driverTypeDataList=null;
		try{
			
			Session session = getSession();
			driverTypeDataList = session.createCriteria(DriverTypeData.class).add(Restrictions.eq(DRIVER_SERVICE_TYPE_ID,serviceTypeId)).add(Restrictions.eq(STATUS,"Y")).list();

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}
		return driverTypeDataList;
	}

	@Override
	public String getDriverNameById(String driverInstanceId) throws DataManagerException {
		String driverName;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA)
					.add(Restrictions.eq(DRIVER_INSTANCE_ID,driverInstanceId))
					.setProjection(Projections.property(DRIVER_INSTANCE_NAME));
			
			Object name = criteria.uniqueResult();
			if(name != null){
				driverName = name.toString();
			}else{
				driverName = "";
			}

		}catch(HibernateException hbe){
			throw new DataManagerException(hbe.getMessage(),hbe);
		}catch(Exception e){
			throw new DataManagerException(e.getMessage(),e);
		}

		return driverName;
	}

	@Override
	public Long getDriverTypeById(String driverInstanceId) throws DataManagerException {
		return getDriverTypeData(DRIVER_INSTANCE_ID, driverInstanceId);
	}
	
	private Long getDriverTypeData(String properties,Object value) throws DataManagerException{
		Long driverType = null;
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA)
					.add(Restrictions.eq(properties,value))
					.setProjection(Projections.property(DRIVER_TYPE_ID));
			
			Object type = criteria.uniqueResult();
			if(type != null){
				driverType = (Long) type;
			}
			return driverType;
		}catch (HibernateException hExp) {
			throw new DataManagerException(hExp.getMessage(), hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	@Override
	public DriverInstanceData getDriverInstanceByName(String name) throws DataManagerException {
			return getDriverInstanceData(DRIVER_INSTANCE_NAME,name);
	}
	
	@Override
	public DriverInstanceData getDriverInstanceById(String driverInstanceId) throws DataManagerException {
		return getDriverInstanceData(DRIVER_INSTANCE_ID, driverInstanceId);
	}
	
	private DriverInstanceData getDriverInstanceData(String properties, Object value) throws DataManagerException {
		String driverInstanceName = (DRIVER_INSTANCE_NAME.equals(properties)) ? (String)value : "Driver Instance";
		DriverInstanceData driverInstanceData = new DriverInstanceData();
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DriverInstanceData.class);
			criteria.add(Restrictions.eq(properties, value));
			driverInstanceData = (DriverInstanceData) criteria.uniqueResult();
			
			if (driverInstanceData == null) {
				throw new InvalidValueException("Driver not found");
			}
			driverInstanceName = driverInstanceData.getName();
			return driverInstanceData;

		} catch (HibernateException hExp) {
			hExp.printStackTrace();
			throw new DataManagerException("Failed to retrieve "+driverInstanceName+", Reason: "+hExp.getMessage(),hExp);
		} catch (Exception exp) {
			exp.printStackTrace();
			throw new DataManagerException("Failed to retrieve "+driverInstanceName+", Reason: "+exp.getMessage(),exp);
		}
	}
	
	@Override
	public List<String> delete(List<String> driverInstanceIds)throws DataManagerException{
		
		String driverName = "Driver Instance";
	
		try{
			
			List<String> deletedDriverLst = new ArrayList<String>();
			Session session = getSession();
			
			MultiIdentifierLoadAccess<DriverInstanceData> multiLoadAccess = session.byMultipleIds(DriverInstanceData.class);
			List<DriverInstanceData> driverInstanceDataLst = multiLoadAccess.multiLoad(driverInstanceIds);
			
			if(Collectionz.isNullOrEmpty(driverInstanceDataLst) == false){
				for(DriverInstanceData driverInstance : driverInstanceDataLst){
					if(driverInstance != null){
						driverName = driverInstance.getName();
						session.delete(driverInstance);
						session.flush();
						deletedDriverLst.add(driverName);
					}
				}
			}
		
			return deletedDriverLst;
		}catch(ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to delete "+driverName+", Reason: "
			+EliteExceptionUtils.extractConstraintName(cve.getSQLException()),cve);
		}catch(HibernateException hbe){
			hbe.printStackTrace();
			throw new DataManagerException("Failed to delete "+driverName+", Reason: "+hbe.getMessage(),hbe);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to delete "+driverName+", Reason: "+e.getMessage(),e);
		}
	}

	@Override
	public void updateDBAuthDriverById(DriverInstanceData driverInstance, DBAuthDriverData dbAuthDriverData,
			IStaffData staffData) throws DataManagerException {
		
		updateDBAuthDriver(driverInstance, dbAuthDriverData, staffData, DRIVER_INSTANCE_ID, driverInstance.getDriverInstanceId());
	}

	@Override
	public void updateDBAuthDriverByName(DriverInstanceData driverInstance, DBAuthDriverData dbAuthDriverData,IStaffData staffData
			,String name) throws DataManagerException {
		updateDBAuthDriver(driverInstance, dbAuthDriverData, staffData, DRIVER_INSTANCE_NAME, name);
	}

	private void updateDBAuthDriver(DriverInstanceData driverInstance, DBAuthDriverData dbAuthDriverData, IStaffData staffData,
			String propertyName, Object value) throws DataManagerException {
		
		try{
			Set<DBAuthDriverData> dbAuthDriverSet=new HashSet<DBAuthDriverData>();
			dbAuthDriverSet.add(dbAuthDriverData);
			driverInstance.setDbdetail(dbAuthDriverSet);
			
			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);	

			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(propertyName, value)).uniqueResult();
			
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstance);
			
			criteria = session.createCriteria(DBAuthDriverData.class);
			DBAuthDriverData tempDbauthDriverData = (DBAuthDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, tempDriverInstanceData.getDriverInstanceId())).uniqueResult();

			if(tempDbauthDriverData == null){
				throw new InvalidValueException("DB Auth driver not found");				
			}
			
			String dbauthId = tempDbauthDriverData.getDbAuthId();
			criteria = session.createCriteria(DBAuthFieldMapData.class);
			
			List<DBAuthFieldMapData> feildMapList = criteria.add(Restrictions.eq(DB_AUTH_ID,tempDbauthDriverData.getDbAuthId())).list();

			for(int j=0;j<feildMapList.size();j++){
				DBAuthFieldMapData tempDatata = feildMapList.get(j);
				session.delete(tempDatata);
			}
			session.flush();
			
			List<DBAuthFieldMapData> tempFeildSet = dbAuthDriverData.getDbFieldMapList();
			Iterator<DBAuthFieldMapData> itr = tempFeildSet.iterator();
			
			int orderNumber = 1;
			while(itr.hasNext()){			
				DBAuthFieldMapData data = itr.next();
				data.setDbAuthId(dbauthId);
				data.setOrderNumber(orderNumber++);
				session.save(data);
			}
			session.flush();

			tempDriverInstanceData.setName(driverInstance.getName());
			tempDriverInstanceData.setDescription(driverInstance.getDescription());
			tempDriverInstanceData.setLastModifiedByStaffId(driverInstance.getLastModifiedByStaffId());
			tempDriverInstanceData.setLastModifiedDate(driverInstance.getLastModifiedDate());
			tempDriverInstanceData.setCacheable(driverInstance.getCacheable());
			
			session.update(tempDriverInstanceData);
			session.flush();
			
			tempDbauthDriverData.setDatabaseId(dbAuthDriverData.getDatabaseId());
			tempDbauthDriverData.setDbQueryTimeout(dbAuthDriverData.getDbQueryTimeout());
			tempDbauthDriverData.setMaxQueryTimeoutCount(dbAuthDriverData.getMaxQueryTimeoutCount());
			tempDbauthDriverData.setTableName(dbAuthDriverData.getTableName());	
			tempDbauthDriverData.setPrimaryKeyColumn(dbAuthDriverData.getPrimaryKeyColumn());
			tempDbauthDriverData.setSequenceName(dbAuthDriverData.getSequenceName());
			tempDbauthDriverData.setUserIdentityAttributes(dbAuthDriverData.getUserIdentityAttributes());
			tempDbauthDriverData.setProfileLookupColumn(dbAuthDriverData.getProfileLookupColumn());

//			if this property is true then it will not validate after operation performed.			
			tempDbauthDriverData.setCheckValidate(true);
			session.update(tempDbauthDriverData);
			session.flush();
			
			staffData.setAuditId(tempDriverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstance.getName());
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
		} catch (ConstraintViolationException cve) {
			cve.printStackTrace();
			throw new DataManagerException("Failed to update DB Auth Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		} catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update DB Auth Driver, Reason: "+e.getMessage(),e);
		} catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update DB Auth Driver, Reason: "+e.getMessage(),e);
		}
	}

	@Override
	public void updateClassicCSVDriverByName(DriverInstanceData driverInstance,ClassicCSVAcctDriverData classicCSVAcctDriverData
			,IStaffData staffData, String idOrName) throws DataManagerException {
		updateClassicCSVDriverData(driverInstance, classicCSVAcctDriverData,staffData, DRIVER_INSTANCE_NAME, idOrName);
	}

	@Override
	public void updateClassicCSVDriverById(DriverInstanceData driverInstance,ClassicCSVAcctDriverData classicCSVAcctDriverData,
			IStaffData staffData) throws DataManagerException {
		updateClassicCSVDriverData(driverInstance, classicCSVAcctDriverData,staffData,DRIVER_INSTANCE_ID , driverInstance.getDriverInstanceId());
	}
	
	private void updateClassicCSVDriverData(DriverInstanceData driverInstanceData,ClassicCSVAcctDriverData classicCSVAcctDriverData,
			IStaffData staffData,String propertyName, Object value) throws DataManagerException {
		try{

			Session session = getSession();
			Criteria criteria = session.createCriteria(DRIVER_INSTANCE_DATA);
			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(propertyName, value)).uniqueResult();

			if(tempDriverInstanceData == null){
				throw new InvalidValueException("Classic CSV Acct Driver not found.");
			}
			
			Set<ClassicCSVAcctDriverData> classicCSVAcctDriverDataSet = new HashSet<ClassicCSVAcctDriverData>();
			classicCSVAcctDriverDataSet.add(classicCSVAcctDriverData);
			driverInstanceData.setCsvset(classicCSVAcctDriverDataSet);
			
			if(Collectionz.isNullOrEmpty(classicCSVAcctDriverData.getMappingList()) == false){
				classicCSVAcctDriverData.setCsvAttrRelList(new ArrayList<ClassicCSVAttrRelationData>(classicCSVAcctDriverData.getMappingList()));
			}
			if(Collectionz.isNullOrEmpty(classicCSVAcctDriverData.getStripMappingList()) == false){
				classicCSVAcctDriverData.setCsvPattRelList(new ArrayList<ClassicCSVStripPattRelData>(classicCSVAcctDriverData.getStripMappingList()));
			}
							
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,driverInstanceData);
			
			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();

			criteria = session.createCriteria(ClassicCSVAcctDriverData.class);

			ClassicCSVAcctDriverData classicCsv = (ClassicCSVAcctDriverData)criteria.add(Restrictions.eq(DRIVER_INSTANCE_ID, driverInstanceId)).uniqueResult();

			String classicCsvId = classicCsv.getClassicCsvId();

			criteria = session.createCriteria(ClassicCSVAttrRelationData.class);
			List<ClassicCSVAttrRelationData> attrRelDataList = criteria.add(Restrictions.eq(CLASSIC_CSV_ID,classicCsvId)).list();

			if(Collectionz.isNullOrEmpty(attrRelDataList) == false){
				int attrRelDataSize = attrRelDataList.size();
				for(int i = 0; i < attrRelDataSize; i++){
					ClassicCSVAttrRelationData atrRelData = attrRelDataList.get(i);
					session.delete(atrRelData);
					session.flush();
				}
			}

			criteria = session.createCriteria(ClassicCSVStripPattRelData.class);
			List<ClassicCSVStripPattRelData> attrPattRelDataList = criteria.add(Restrictions.eq(CLASSIC_CSV_ID,classicCsvId)).list();

			if(Collectionz.isNullOrEmpty(attrPattRelDataList) == false){
				int attrPattRelDataSize = attrPattRelDataList.size();
				for(int i = 0;i < attrPattRelDataSize; i++){
					ClassicCSVStripPattRelData atrRelData = attrPattRelDataList.get(i);
					session.delete(atrRelData);
					session.flush();
				}
			}
			
			int orderNumber = 1;
			if(Collectionz.isNullOrEmpty(classicCSVAcctDriverData.getMappingList()) == false){
				List<ClassicCSVAttrRelationData> mainMappingList = classicCSVAcctDriverData.getMappingList();
				List<ClassicCSVAttrRelationData> oldMainMappingList = new ArrayList<ClassicCSVAttrRelationData>();
				int mappingListSize = mainMappingList.size();
				for(int i = 0; i < mappingListSize; i++){
					ClassicCSVAttrRelationData relData = mainMappingList.get(i);
					relData.setClassicCsvId(classicCsvId);
					relData.setOrderNumber(orderNumber++);
					oldMainMappingList.add(relData);
					session.save(relData);
					session.flush();
					classicCsv.setCsvAttrRelList(oldMainMappingList);
				}	
			}
			
			orderNumber = 1;
			if(Collectionz.isNullOrEmpty(classicCSVAcctDriverData.getStripMappingList()) == false){
				List<ClassicCSVStripPattRelData> mainStripList = classicCSVAcctDriverData.getStripMappingList();
				List<ClassicCSVStripPattRelData> oldMainStripList=new ArrayList<ClassicCSVStripPattRelData>();
				int mainStripListSize = mainStripList.size();
				for(int i = 0;i < mainStripListSize; i++){
					ClassicCSVStripPattRelData relData = mainStripList.get(i);
					relData.setClassicCsvId(classicCsvId);
					relData.setOrderNumber(orderNumber++);
					oldMainStripList.add(relData);
					session.save(relData);
					session.flush();
				}
				classicCsv.setCsvPattRelList(oldMainStripList);
			}
			// for detaillocal driver 

			classicCsv.setAllocatingprotocol(classicCSVAcctDriverData.getAllocatingprotocol());
			classicCsv.setArchivelocation(classicCSVAcctDriverData.getArchivelocation());
			classicCsv.setAvpairseparator(classicCSVAcctDriverData.getAvpairseparator());
			classicCsv.setCreateBlankFile(classicCSVAcctDriverData.getCreateBlankFile());
			classicCsv.setDefaultdirname(classicCSVAcctDriverData.getDefaultdirname());
			classicCsv.setDelimeter(classicCSVAcctDriverData.getDelimeter());
			classicCsv.setEventdateformat(classicCSVAcctDriverData.getEventdateformat());
			classicCsv.setFailovertime(classicCSVAcctDriverData.getFailovertime());
			classicCsv.setFilename(classicCSVAcctDriverData.getFilename());
			classicCsv.setTimeBoundry(classicCSVAcctDriverData.getTimeBoundry());
			classicCsv.setFoldername(classicCSVAcctDriverData.getFoldername());
			classicCsv.setGlobalization(classicCSVAcctDriverData.getGlobalization());
			classicCsv.setHeader(classicCSVAcctDriverData.getHeader());
			classicCsv.setIpaddress(classicCSVAcctDriverData.getIpaddress());
			classicCsv.setLocation(classicCSVAcctDriverData.getLocation());
			classicCsv.setMultivaluedelimeter(classicCSVAcctDriverData.getMultivaluedelimeter());
			classicCsv.setPassword(classicCSVAcctDriverData.getPassword());
			classicCsv.setPattern(classicCSVAcctDriverData.getPattern());
			classicCsv.setPostoperation(classicCSVAcctDriverData.getPostoperation());
			classicCsv.setPrefixfilename(classicCSVAcctDriverData.getPrefixfilename());
			classicCsv.setCdrtimestampFormat(classicCSVAcctDriverData.getCdrtimestampFormat());
			classicCsv.setRange(classicCSVAcctDriverData.getRange());
			classicCsv.setRemotelocation(classicCSVAcctDriverData.getRemotelocation());
			classicCsv.setSizeBasedRollingUnit(classicCSVAcctDriverData.getSizeBasedRollingUnit());
			classicCsv.setRecordBasedRollingUnit(classicCSVAcctDriverData.getRecordBasedRollingUnit());
			classicCsv.setTimeBasedRollingUnit(classicCSVAcctDriverData.getTimeBasedRollingUnit());
			classicCsv.setTimeBoundry(classicCSVAcctDriverData.getTimeBoundry());
			classicCsv.setUsedictionaryvalue(classicCSVAcctDriverData.getUsedictionaryvalue());
			classicCsv.setUsername(classicCSVAcctDriverData.getUsername());
			classicCsv.setEnclosingCharacter(classicCSVAcctDriverData.getEnclosingCharacter());
			classicCsv.setCdrTimestampHeader(classicCSVAcctDriverData.getCdrTimestampHeader());
			classicCsv.setCdrTimestampPosition(classicCSVAcctDriverData.getCdrTimestampPosition());
			
			session.update(classicCsv);
			session.flush();

			tempDriverInstanceData.setName(driverInstanceData.getName());
			tempDriverInstanceData.setDescription(driverInstanceData.getDescription());
			tempDriverInstanceData.setLastModifiedByStaffId(driverInstanceData.getLastModifiedByStaffId());
			tempDriverInstanceData.setLastModifiedDate(driverInstanceData.getLastModifiedDate());
			session.update(tempDriverInstanceData);
			session.flush();
			
			staffData.setAuditId(tempDriverInstanceData.getAuditUId());
			staffData.setAuditName(driverInstanceData.getName());
			
			doAuditingJson(jsonArray.toString(),staffData,ConfigConstant.UPDATE_DRIVER);
			
		}catch(ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to update Classic CSV Acct Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update Classic CSV Acct Driver, Reason: "+e.getMessage(),e);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update Classic CSV Acct Driver, Reason: "+e.getMessage(),e);
		}
	}

	@Override
	public String createHssAuthDriver(CreateDriverConfig driverConfig) throws DataManagerException {
		try{						
			Session session = getSession();
			session.clear();
			DriverInstanceData driverInstanceData = driverConfig.getDriverInstanceData();

			HssAuthDriverData hssAuthDriverData = driverConfig.getHssAuthDriverData();

			String auditId= UUIDGenerator.generate();
			
			driverInstanceData.setAuditUId(auditId);

			Set<HssAuthDriverData> hssAuthDriverDataSet = new HashSet<HssAuthDriverData>();
			hssAuthDriverDataSet.add(driverConfig.getHssAuthDriverData());
			driverInstanceData.setHssDetail(hssAuthDriverDataSet);
			
			session.save(driverInstanceData);			

			String driverInstanceId = driverInstanceData.getDriverInstanceId();

			hssAuthDriverData.setDriverInstanceId(driverInstanceId);
			session.save(hssAuthDriverData);

			String hssAuthId = hssAuthDriverData.getHssauthdriverid();
			List<HssAuthDriverFieldMapData> tempList = hssAuthDriverData.getHssAuthFieldMapList();
			int orderNumber = 1;
			if(Collectionz.isNullOrEmpty(tempList) == false){
				int size = tempList.size();
				for (int i = 0; i < size; i++) {
					HssAuthDriverFieldMapData fieldMapData = tempList.get(i);
					fieldMapData.setHssauthdriverid(hssAuthId);
					fieldMapData.setOrderNumber(orderNumber++);
					session.save(fieldMapData);
				}
			}
			
			List<DiameterPeerRelData> diameterRelDataList = hssAuthDriverData.getDiameterPeerRelDataList();
			
			if(Collectionz.isNullOrEmpty(diameterRelDataList) == false){
				int size = diameterRelDataList.size();
				orderNumber = 1;
				for (int i = 0; i < size; i++) {
					DiameterPeerRelData diameterPeerrelData = diameterRelDataList.get(i);
					diameterPeerrelData.setHssauthdriverid(hssAuthId);
					diameterPeerrelData.setOrderNumber(orderNumber++);
					session.save(diameterPeerrelData);
				}
			}
//			if this property is true then it will not validate after operation performed.
			hssAuthDriverData.setCheckValidate(true);
			session.flush();
			session.clear();
		}catch (ConstraintViolationException cve) {
			Logger.logTrace(MODULE, cve);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException hbe){
			Logger.logTrace(MODULE, hbe);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON +  hbe.getMessage(),hbe);
		}catch(Exception e){
			Logger.logTrace(MODULE, e);
			throw new DataManagerException(FAILED_TO_CREATE + driverConfig.getDriverInstanceData().getName() + REASON +e.getMessage(),e);
		}
		return driverConfig.getDriverInstanceData().getName();
	}

	@Override
	public void updateHSSDataByName(DriverInstanceData updatedDriverInstance, HssAuthDriverData updateHSSAuthDriverData, IStaffData staffData, String name) throws DataManagerException {
			updateHSSAuthDriverData(updatedDriverInstance, updateHSSAuthDriverData, staffData, DRIVER_INSTANCE_NAME, name);
	}
	
	@Override
	public void updateHSSDataById(DriverInstanceData updatedDriverInstance, HssAuthDriverData updateHSSAuthDriverData, IStaffData staffData) throws DataManagerException {
		updateHSSAuthDriverData(updatedDriverInstance, updateHSSAuthDriverData, staffData, DRIVER_INSTANCE_ID, updatedDriverInstance.getDriverInstanceId());
	}

	private void updateHSSAuthDriverData(DriverInstanceData updatedDriverInstance, HssAuthDriverData updateHSSAuthDriverData, IStaffData staffData, String propertyName, Object value) throws DataManagerException {
		try{
			Session session = getSession();
			
			Set<HssAuthDriverData> hssAuthDriverSet = new LinkedHashSet<HssAuthDriverData>();

			/*
			 * update Driverinstance Data
			 */
			Criteria criteria = session.createCriteria(DriverInstanceData.class);
			
			DriverInstanceData tempDriverInstanceData = (DriverInstanceData)criteria.add(Restrictions.eq(propertyName, value)).uniqueResult();
		
			if(tempDriverInstanceData == null ){
				throw new InvalidValueException("Driver Instance Not found");
			}
			
			hssAuthDriverSet.add(updateHSSAuthDriverData);
			updatedDriverInstance.setHssDetail(hssAuthDriverSet);
			
			JSONArray jsonArray=ObjectDiffer.diff(tempDriverInstanceData,updatedDriverInstance);
		
			tempDriverInstanceData.setName(updatedDriverInstance.getName());
			tempDriverInstanceData.setDescription(updatedDriverInstance.getDescription());
			tempDriverInstanceData.setLastModifiedDate(updatedDriverInstance.getLastModifiedDate());
			session.update(tempDriverInstanceData);
			/*
			 * update HssAuthDriverData 
			 */
			String driverInstanceId = tempDriverInstanceData.getDriverInstanceId();
			criteria = session.createCriteria(HssAuthDriverData.class);

			HssAuthDriverData hssAuthDriverDataObj = (HssAuthDriverData)criteria.add(Restrictions.eq("hssauthdriverid", updateHSSAuthDriverData.getHssauthdriverid())).uniqueResult();
			
			if(hssAuthDriverDataObj == null){
				throw new InvalidValueException("HSS Auth Driver not found.");
			}
			
            hssAuthDriverDataObj.setAdditionalAttributes(updateHSSAuthDriverData.getAdditionalAttributes());
            hssAuthDriverDataObj.setApplicationid(updateHSSAuthDriverData.getApplicationid());
            hssAuthDriverDataObj.setCommandCode(updateHSSAuthDriverData.getCommandCode());
            hssAuthDriverDataObj.setDriverInstanceId(updateHSSAuthDriverData.getDriverInstanceId());
            hssAuthDriverDataObj.setHssauthdriverid(updateHSSAuthDriverData.getHssauthdriverid());
            hssAuthDriverDataObj.setNoOfTriplets(updateHSSAuthDriverData.getNoOfTriplets());
            hssAuthDriverDataObj.setRequesttimeout(updateHSSAuthDriverData.getRequesttimeout());
            hssAuthDriverDataObj.setUserIdentityAttributes(updateHSSAuthDriverData.getUserIdentityAttributes());
//			if this property is true then it will not validate after operation performed.
            hssAuthDriverDataObj.setCheckValidate(true);
            session.update(hssAuthDriverDataObj);
            /*
             * update HssAuthDriverFieldMapData List
             */
            
            Criteria hssAuthDriverFieldMapCriteria = session.createCriteria(HssAuthDriverFieldMapData.class);
            hssAuthDriverFieldMapCriteria.add(Restrictions.eq("hssauthdriverid",hssAuthDriverDataObj.getHssauthdriverid()));
       
            List<HssAuthDriverFieldMapData> hssFieldMappingDataList=hssAuthDriverFieldMapCriteria.list();
            deleteObjectList(hssFieldMappingDataList, session);
            
            List<HssAuthDriverFieldMapData> tempfeilMapSet = updateHSSAuthDriverData.getHssAuthFieldMapList();
            Iterator<HssAuthDriverFieldMapData> itr = tempfeilMapSet.iterator();
			
            int orderNumber = 1;
			while(itr.hasNext()){
				HssAuthDriverFieldMapData feildMapData = itr.next();
				feildMapData.setHssauthdriverid(hssAuthDriverDataObj.getHssauthdriverid());
				feildMapData.setOrderNumber(orderNumber++);
				session.save(feildMapData);
			}
			
			 /*
             * update DiameterPeerRelData List
             */
            
			  Criteria diameterPeerRelDataCriteria = session.createCriteria(DiameterPeerRelData.class);
	          
			  diameterPeerRelDataCriteria.add(Restrictions.eq("hssauthdriverid",hssAuthDriverDataObj.getHssauthdriverid()));
	       
	            List<DiameterPeerRelData> hssDiameterPeerRelDataList=diameterPeerRelDataCriteria.list();
	            deleteObjectList(hssDiameterPeerRelDataList, session);
	            
	        	List<DiameterPeerRelData> tempDiameterPeerSet = updateHSSAuthDriverData.getDiameterPeerRelDataList();
	    		
	            Iterator<DiameterPeerRelData> diameterPeerInt = tempDiameterPeerSet.iterator();
	            orderNumber = 1;
				while(diameterPeerInt.hasNext()){
					DiameterPeerRelData feildMapData = diameterPeerInt.next();
					feildMapData.setHssauthdriverid(hssAuthDriverDataObj.getHssauthdriverid());
					feildMapData.setOrderNumber(orderNumber++);
					session.save(feildMapData);
				}
				session.flush();

				staffData.setAuditId(tempDriverInstanceData.getAuditUId());
				staffData.setAuditName(updatedDriverInstance.getName());

				doAuditingJson(jsonArray.toString(), staffData, ConfigConstant.UPDATE_DRIVER);
		}catch(ConstraintViolationException cve){
			cve.printStackTrace();
			throw new DataManagerException("Failed to update HSS Auth Driver, Reason: " + EliteExceptionUtils.extractConstraintName(cve.getSQLException()), cve);
		}catch(HibernateException e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update HSS Auth Driver, Reason: "+e.getMessage(),e);
		}catch(Exception e){
			e.printStackTrace();
			throw new DataManagerException("Failed to update HSS Auth Driver, Reason: "+e.getMessage(),e);
		}	
	}

}