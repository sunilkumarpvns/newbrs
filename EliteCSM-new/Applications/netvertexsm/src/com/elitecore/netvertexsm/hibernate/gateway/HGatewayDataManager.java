package com.elitecore.netvertexsm.hibernate.gateway;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Conjunction;
import org.hibernate.criterion.Disjunction;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.constants.ConversionType;
import com.elitecore.corenetvertex.constants.SupportedStandard;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
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
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterAttributeMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.DiameterValueMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfilePacketMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GatewayProfileRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.GroovyScriptData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.RadiusAttributeMapData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.RadiusProfileData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.VendorData;
import com.elitecore.netvertexsm.hibernate.core.base.HBaseDataManager;

public class HGatewayDataManager extends HBaseDataManager implements GatewayDataManager {
	
	//private static final String MODULE = "GATEWAY-DATA-MANAGER";
	
	/**
	 * This Method is generated to create Gateway.
	 * @author Manjil Purohit
	 * @param gatewayData
	 * @throws DataManagerException
	 */
	public void create(GatewayData gatewayData) throws DataManagerException {
		try{
			Session session = getSession();						
			session.save(gatewayData);
			session.flush();
			if(gatewayData.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.RADIUS.id)) {
				gatewayData.getRadiusGatewayData().setGatewayId(gatewayData.getGatewayId());
				session.save(gatewayData.getRadiusGatewayData());
			}else if(gatewayData.getCommProtocol().equalsIgnoreCase(CommunicationProtocol.DIAMETER.id)) {
				gatewayData.getDiameterGatewayData().setGatewayId(gatewayData.getGatewayId());
				session.save(gatewayData.getDiameterGatewayData());
			}			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
	/**
	 * This Method is generated to create Gateway Profile.
	 * @author Manjil Purohit
	 * @param gatewayProfileData
	 * @throws DataManagerException
	 */
	public void create(GatewayProfileData gatewayProfileData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(gatewayProfileData);
			session.flush();
			
			if(CommunicationProtocol.RADIUS.id.equalsIgnoreCase(gatewayProfileData.getCommProtocolId())) {
				
				for(GatewayProfilePacketMapData packetMapData : gatewayProfileData.getRadiusProfileData().getGwProfilePacketMapList()) {
					packetMapData.setProfileId(gatewayProfileData.getProfileId());
					session.save(packetMapData);
				}
				
				for(PCCRuleMappingData mappingData : gatewayProfileData.getRadiusProfileData().getPccRuleMappingDataList()) {
					mappingData.setProfileId(gatewayProfileData.getProfileId());
					session.save(mappingData);
				}
				
				for(GatewayProfileRuleMappingData profileRuleMappingData : gatewayProfileData.getRadiusProfileData().getGatewayProfileRuleMappingList()) {
					profileRuleMappingData.setProfileId(gatewayProfileData.getProfileId());
					session.save(profileRuleMappingData);
				}
			
				
				for(GroovyScriptData groovyScriptData : gatewayProfileData.getRadiusProfileData().getGroovyScriptsList()) {
					groovyScriptData.setProfileId(gatewayProfileData.getProfileId());
					session.save(groovyScriptData);
				}				
			}else if(CommunicationProtocol.DIAMETER.id.equalsIgnoreCase(gatewayProfileData.getCommProtocolId())) {
				gatewayProfileData.getDiameterProfileData().setProfileId(gatewayProfileData.getProfileId());
				gatewayProfileData.getDiameterProfileData().setSupportedStandard(gatewayProfileData.getSupportedStandard());
				session.save(gatewayProfileData.getDiameterProfileData());
				
				if(gatewayProfileData.getSupportedStandard() != SupportedStandard.CISCOSCE.getId()){
					for(PCCRuleMappingData mappingData : gatewayProfileData.getDiameterProfileData().getPccRuleMappingList()) {
						mappingData.setProfileId(gatewayProfileData.getProfileId());
						session.save(mappingData);
					}
				}
				for(GatewayProfilePacketMapData packetMapData : gatewayProfileData.getDiameterProfileData().getGwProfilePacketMapList()) {
					packetMapData.setProfileId(gatewayProfileData.getProfileId());
					session.save(packetMapData);
				}
				
				for(GatewayProfileRuleMappingData profileRuleMappingData : gatewayProfileData.getDiameterProfileData().getGatewayProfileRuleMappingList()) {
					profileRuleMappingData.setProfileId(gatewayProfileData.getProfileId());
					session.save(profileRuleMappingData);
				}
				
				for(GroovyScriptData groovyScriptData : gatewayProfileData.getDiameterProfileData().getGroovyScriptsList()) {
					groovyScriptData.setProfileId(gatewayProfileData.getProfileId());
					session.save(groovyScriptData);
				}	
			}			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
	/**
	 * This method will return the unique result provided by criteria.
	 * @author Manjil Purohit
	 * @param gatewayData
	 * @return gatewayData
	 */
	public GatewayData getGatewayData(IGatewayData gatewayData) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(GatewayData.class);
		
		if(gatewayData.getGatewayId()>0) {
			criteria.add(Restrictions.eq("gatewayId", gatewayData.getGatewayId()));
		}		
		return (GatewayData) criteria.uniqueResult();
	}
	

	public GatewayProfileData getGatewayProfileData(long gatewayProfileId){
		Session session = getSession();
		Criteria criteria = session.createCriteria(GatewayProfileData.class);
		criteria.add(Restrictions.eq("profileId", gatewayProfileId));
		GatewayProfileData gatewayProfile = (GatewayProfileData)criteria.uniqueResult();
		if(CommunicationProtocol.RADIUS.id.equalsIgnoreCase(gatewayProfile.getCommProtocolId())){
			gatewayProfile.setRadiusProfileData(getRadiusProfileData(gatewayProfile.getProfileId()));
		}else if(CommunicationProtocol.DIAMETER.id.equalsIgnoreCase(gatewayProfile.getCommProtocolId())){
			gatewayProfile.setDiameterProfileData(getDiameterProfileData(gatewayProfile.getProfileId()));
		}	
		return gatewayProfile;
	}
	
	
	/**
	 * Return radius profile data
	 * @param profileId
	 * @return<code>IRadiusProfileData</code>
	 */
	private RadiusProfileData getRadiusProfileData(long profileId){
		Session session = getSession();
		Criteria criteria = session.createCriteria(RadiusProfileData.class);
		
		if(profileId > 0){
			criteria.add(Restrictions.eq("profileId", profileId));
		}
		
		return (RadiusProfileData) criteria.uniqueResult();
	}
	
	/**
	 * Return diameter profile
	 * @param profileId
	 * @return<code>IDiameterProfileData</code>
	 */
	private DiameterProfileData getDiameterProfileData(long profileId){
		Session session = getSession();
		Criteria criteria = session.createCriteria(DiameterProfileData.class);
		
		if(profileId > 0){
			criteria.add(Restrictions.eq("profileId", profileId));
		}
		
		return (DiameterProfileData) criteria.uniqueResult();
	}
	
	/**
	 * This method will return the unique result provided by criteria.
	 * @author Manjil Purohit
	 * @param ciscoSceGatewayData
	 * @return ciscoSceGatewayData
	 */
	
	public GatewayData getGatewayDetail(GatewayData gatewayData) throws DataManagerException{
		Session session = getSession();
		Criteria criteria = session.createCriteria(GatewayData.class);
		
		if(gatewayData.getGatewayId() > 0){
			criteria.add(Restrictions.eq("gatewayId", gatewayData.getGatewayId()));
		}
		
		GatewayData gateway = (GatewayData)criteria.uniqueResult();
		if(CommunicationProtocol.RADIUS.id.equalsIgnoreCase(gateway.getCommProtocol())){
			gateway.setRadiusGatewayData(getRadiusGatewayData(gateway.getGatewayId()));
		}else if(CommunicationProtocol.DIAMETER.id.equalsIgnoreCase(gateway.getCommProtocol())){
			gateway.setDiameterGatewayData(getDiameterGatewayData(gateway.getGatewayId()));
		}else {
			gatewayData.setRadiusGatewayData(null);
			gatewayData.setDiameterGatewayData(null);
		}
	
		return gateway;
	}
	
	/**
	 * This method will return the unique result provided by criteria.
	 * @author Manjil Purohit
	 * @param radiusGatewayData
	 * @return radiusGatewayData
	 */
	public RadiusGatewayData getRadiusGatewayData(IRadiusGatewayData radiusGatewayData) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(RadiusGatewayData.class);
		
		if(radiusGatewayData.getGatewayId()>0) {
			criteria.add(Restrictions.eq("gatewayId", radiusGatewayData.getGatewayId()));
		}
		List list = criteria.list();
		RadiusGatewayData radiusData = null;
		if(list!=null && !list.isEmpty()){
			radiusData = (RadiusGatewayData)list.get(0);
		}
		
		return radiusData;
	} 
	
	private RadiusGatewayData getRadiusGatewayData(long gatewayId){
		Session session = getSession();
		Criteria criteria = session.createCriteria(RadiusGatewayData.class);
		
		if(gatewayId > 0){
			criteria.add(Restrictions.eq("gatewayId", gatewayId));
		}
		List list = criteria.list();
		RadiusGatewayData radiusGatewayData = null;
		if(list!=null && !list.isEmpty()){
			radiusGatewayData = (RadiusGatewayData)list.get(0);
		}
		return radiusGatewayData;
	}
	/**
	 * This method will return the unique result provided by criteria.
	 * @author Manjil Purohit
	 * @param diameterGatewayData
	 * @return diameterGatewayData
	 */
	public DiameterGatewayData getDiameterGatewayData(IDiameterGatewayData diameterGatewayData) throws DataManagerException {
		Session session = getSession();
		Criteria criteria = session.createCriteria(DiameterGatewayData.class);
		
		if(diameterGatewayData.getGatewayId()>0) {
			criteria.add(Restrictions.eq("gatewayId", diameterGatewayData.getGatewayId()));
		}		
		List list = criteria.list();
		DiameterGatewayData diameterData = null;
		if(list!=null && !list.isEmpty()){
			diameterData = (DiameterGatewayData)list.get(0);
		}
		return diameterData;
		
	}
	
	private DiameterGatewayData getDiameterGatewayData(long gatewayId){
		Session session = getSession();
		Criteria criteria = session.createCriteria(DiameterGatewayData.class);
		
		if(gatewayId > 0){
			criteria.add(Restrictions.eq("gatewayId", gatewayId));
		}
		List list = criteria.list();
		DiameterGatewayData diameterData = null;
		if(list!=null && !list.isEmpty()){
			diameterData = (DiameterGatewayData)list.get(0);
		}
		return diameterData;
	}

	

	public List<IGatewayData> getGatewayData(String gatewayName)
			throws DataManagerException {
		return null;
	}
	
	/**
     * This method returns all the Gateway.
     * All the not null fields will be compared for equality.
     * @author Manjil Purohit
     * @return locationList
	 * @throws DataManagerException 
     */
	public List<GatewayData> getGatewayList() throws DataManagerException {
		List<GatewayData> gatewayList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayData.class);
			gatewayList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return gatewayList;
	}
	
	/**
     * This method returns all the Location.
     * All the not null fields will be compared for equality.
     * @author Manjil Purohit
     * @return locationList
	 * @throws DataManagerException 
     */
	public List<GatewayLocationData> getLocationList() throws DataManagerException {
		List<GatewayLocationData> locationList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayLocationData.class);
			locationList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return locationList;
	}
	
	/**
     * This method returns all the Vendor.
     * All the not null fields will be compared for equality.
     * @author Manjil Purohit
     * @return vendorList
	 * @throws DataManagerException 
     */
	public List<VendorData> getVendorList() throws DataManagerException {
		List<VendorData> vendorList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(VendorData.class);
			vendorList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return vendorList;
	}

	public List getList(IGatewayData gatewayData) throws DataManagerException {
		return null;
	}	
	public void updateStatus(long gatewayId, String status,
			Timestamp statusChangeDate) throws DataManagerException {
		
	}


	/**
     * This method returns all the profiles.
     * @author Manjil Purohit
     * @return profileList
	 * @throws DataManagerException 
     */
	public List<GatewayProfileData> getProfileList() throws DataManagerException {
		List<GatewayProfileData> profileList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfileData.class);
			profileList = criteria.list();		
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return profileList;
	}
	
	/**
     * This method returns all the profiles.
     * @author Manjil Purohit
     * @param commProtocolId
     * @return profileList
	 * @throws DataManagerException 
     */
	public List<GatewayProfileData> getProfileList(String commProtocolId) throws DataManagerException {
		List<GatewayProfileData> profileList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfileData.class);
			criteria.add(Restrictions.eq("commProtocolId", commProtocolId));
			profileList = criteria.list();	
			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return profileList;
	}
	
	public List<GatewayData> getDiameterGatewayDataList(String commProtocolId) throws DataManagerException {
		List<GatewayData> diameterGatewayDataList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayData.class);
			criteria.add(Restrictions.eq("commProtocol", commProtocolId));
			criteria.addOrder(Order.asc("gatewayName"));
			diameterGatewayDataList = criteria.list();			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return diameterGatewayDataList;
	}
	
	public List<GatewayData> getDiameterGatewayDataAssociation(String commProtocolId,long gatewayId) throws DataManagerException {
		List<GatewayData> diameterGatewayDataList = null;
		try {
			Session session = getSession();
			
			Criteria criteria = session.createCriteria(GatewayData.class)
				    .createCriteria("diameterGatewayDataSet")
				        .add( Restrictions.eq("alternateHostId", gatewayId));
			 diameterGatewayDataList = criteria.list();
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return diameterGatewayDataList;
	}
	
	
	public List<GatewayData> getUpdatedDiameterGatewayDataList(String commProtocolId,long gatewayId) throws DataManagerException {
		List<GatewayData> diameterGatewayDataList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayData.class);
			criteria.add(Restrictions.eq("commProtocol", commProtocolId));
			criteria.add(Restrictions.ne("gatewayId", gatewayId));
			criteria.addOrder(Order.asc("gatewayName"));
			diameterGatewayDataList = criteria.list();			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return diameterGatewayDataList;
	}

	public List<GroovyScriptData> getGroovyScriptDataList(long profileId) throws DataManagerException {
		List<GroovyScriptData> groovyScriptDataList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroovyScriptData.class).addOrder(Order.asc("orderNumber"));;
			criteria.add(Restrictions.eq("profileId", profileId));
			groovyScriptDataList = criteria.list();				
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return groovyScriptDataList;
	}	
	
	public List<PCCRuleMappingData> getPCCRuleMapList(long profileId) throws DataManagerException {
		List<PCCRuleMappingData> pccRuleMapList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCCRuleMappingData.class);
			criteria.add(Restrictions.eq("profileId", profileId));
			pccRuleMapList = criteria.list();	
			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return pccRuleMapList;
	}
	
	public List<GatewayProfilePacketMapData> getProfilePacketMapList(long gatewayProfileId) throws DataManagerException {
		List<GatewayProfilePacketMapData> diameterPacketMapList = new ArrayList<GatewayProfilePacketMapData>();
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class);
			if(gatewayProfileId > 0) {
				criteria.add(Restrictions.eq("profileId", gatewayProfileId));
				diameterPacketMapList = criteria.list();
			}	
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return diameterPacketMapList;
	}
	
	public List<DiameterValueMapData> getDiameterValueMapData(long attributeId) throws DataManagerException {
		List<DiameterValueMapData> diameterValueMapDataList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterValueMapData.class);
			if(attributeId>0) {
				criteria.add(Restrictions.eq("diameterAttributeId", attributeId));
			}	
			diameterValueMapDataList = criteria.list();			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return diameterValueMapDataList;
	}
	
	/**
	 * This method will return the search result for Gateway.
	 * All the not null fields will be compared for equality.
	 * @author Manjil Purohit
	 * @param gatewayData
	 * @return pageList
	 */
	public PageList search(GatewayData gatewayData, int pageNo, int pageSize) throws DataManagerException {		
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayData.class).addOrder(Order.asc("commProtocol")).addOrder(Order.asc("gatewayName"));
			if(gatewayData.getGatewayName()!=null && gatewayData.getGatewayName().length()>0) {				
				criteria.add(Restrictions.ilike("gatewayName", gatewayData.getGatewayName(),MatchMode.ANYWHERE));
			}
			if(gatewayData.getCommProtocol()!=null && gatewayData.getCommProtocol().length()>0) {
				criteria.add(Restrictions.ilike("commProtocol", gatewayData.getCommProtocol(), MatchMode.ANYWHERE));
			}
			if(gatewayData.getConnectionUrl()!=null && gatewayData.getConnectionUrl().length()>0) {				
				criteria.add(Restrictions.ilike("connectionUrl", gatewayData.getConnectionUrl(),MatchMode.ANYWHERE));
			}
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			
			if(pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			List gatewayList = criteria.list();
	        long totalPages = (long)Math.ceil(totalItems/pageSize);
	        if(totalItems%pageSize == 0)
				totalPages-=1;
	        
	        pageList = new PageList(gatewayList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}
	
	/**
	 * This method will return the search result.
	 * All the not null fields will be compared for equality.
	 * @author Manjil Purohit
	 * @param profileData
	 * @return pageList
	 */
	public PageList search(GatewayProfileData profileData, int pageNo, int pageSize) throws DataManagerException {
		PageList pageList = null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfileData.class).addOrder(Order.asc("profileName"));			
			
			if(profileData.getProfileName()!=null) {
				criteria.add(Restrictions.ilike("profileName", profileData.getProfileName(), MatchMode.ANYWHERE));
			}			
			
			int totalItems = criteria.list().size();		
			criteria.setFirstResult(((pageNo-1) * pageSize));
			
			if(pageSize > 0) {
				criteria.setMaxResults(pageSize);
			}
			List gatewayList = criteria.list();
	        long totalPages = (long)Math.ceil(totalItems/pageSize);
	        if(totalItems%pageSize == 0)
				totalPages-=1;
	        
	        pageList = new PageList(gatewayList, pageNo, totalPages ,totalItems);
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return pageList;
	}

	public List<GatewayProfileData> getProfileData(String profileName)	throws DataManagerException {
		return null;
	}	
	
	
	/**
	 * Update Gateway Profile
	 */
	public void updateGatewayProfile(GatewayProfileData gatewayProfileData) throws DataManagerException{
		try{
			Session session = getSession();
			GatewayProfileData dbGateWayProfileData=(GatewayProfileData) session.load(GatewayProfileData.class, gatewayProfileData.getProfileId());
			
			dbGateWayProfileData.setProfileName(gatewayProfileData.getProfileName());
			dbGateWayProfileData.setGatewayType(gatewayProfileData.getGatewayType());
			dbGateWayProfileData.setCommProtocolId(gatewayProfileData.getCommProtocolId());
			dbGateWayProfileData.setVedorId(gatewayProfileData.getVedorId());
			dbGateWayProfileData.setFirmware(gatewayProfileData.getFirmware());
			dbGateWayProfileData.setMaxThroughput(gatewayProfileData.getMaxThroughput());
			dbGateWayProfileData.setBufferBW(gatewayProfileData.getBufferBW());
			dbGateWayProfileData.setMaxIPCANSession(gatewayProfileData.getMaxIPCANSession());
			dbGateWayProfileData.setDescription(gatewayProfileData.getDescription());
			dbGateWayProfileData.setUsageReportingTime(gatewayProfileData.getUsageReportingTime());
			dbGateWayProfileData.setRevalidationMode(gatewayProfileData.getRevalidationMode());
			setUpdateAuditDetail(dbGateWayProfileData);
			session.update(dbGateWayProfileData);
			session.flush();
			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		
	}
	
	public void updateRadiusGatewayProfile(GatewayProfileData gatewayProfileData) throws DataManagerException{
		try{
			Session session = getSession();
            
		//	deletePCCRuleMapData(gatewayProfileData.getProfileId());				
			for(PCCRuleMappingData mappingData : gatewayProfileData.getRadiusProfileData().getPccRuleMappingDataList()) {
				mappingData.setProfileId(gatewayProfileData.getProfileId());
				session.save(mappingData);
			}
			deleteGatewayProfileRuleMappingRelData(gatewayProfileData.getProfileId());
			for(GatewayProfileRuleMappingData profileRuleMappingData : gatewayProfileData.getRadiusProfileData().getGatewayProfileRuleMappingList()) {
				profileRuleMappingData.setProfileId(gatewayProfileData.getProfileId());
				session.save(profileRuleMappingData);
			}
            
			deleteGWPacketMapData(gatewayProfileData.getProfileId());				
			for(GatewayProfilePacketMapData packetMapData : gatewayProfileData.getRadiusProfileData().getGwProfilePacketMapList()) {
				packetMapData.setProfileId(gatewayProfileData.getProfileId());
				session.save(packetMapData);
			}
			deleteGroovyScriptsData(gatewayProfileData.getProfileId());
			for(GroovyScriptData groovyScriptData : gatewayProfileData.getRadiusProfileData().getGroovyScriptsList()) {
				groovyScriptData.setProfileId(gatewayProfileData.getProfileId());
				session.save(groovyScriptData);
			}
			setUpdateAuditDetail(gatewayProfileData);
			session.update(gatewayProfileData);
			session.flush();
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}

	}
	public void updateGatewayProfileFieldMap(GatewayProfileData gatewayProfileData) throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterProfileData.class);
			long profileid =  gatewayProfileData.getProfileId();
			criteria.add(Restrictions.eq("profileId",profileid));
			gatewayProfileData.getDiameterProfileData().setProfileId(gatewayProfileData.getProfileId());
			
			DiameterProfileData dbDiameterProfileData = (DiameterProfileData)criteria.uniqueResult(); 
			DiameterProfileData diameterProfileData = gatewayProfileData.getDiameterProfileData();
			dbDiameterProfileData.setDwInterval(diameterProfileData.getDwInterval());
			dbDiameterProfileData.setGxApplicationId(diameterProfileData.getGxApplicationId());
			dbDiameterProfileData.setGyApplicationId(diameterProfileData.getGyApplicationId());
			dbDiameterProfileData.setRxApplicationId(diameterProfileData.getRxApplicationId());
			dbDiameterProfileData.setS9ApplicationId(diameterProfileData.getS9ApplicationId());
			dbDiameterProfileData.setSyApplicationId(diameterProfileData.getSyApplicationId());
			//dbDiameterProfileData.setInitConnection(diameterProfileData.getInitConnection());
			dbDiameterProfileData.setIsCustomGxAppId(diameterProfileData.getIsCustomGxAppId());
			dbDiameterProfileData.setIsCustomGyAppId(diameterProfileData.getIsCustomGyAppId());
			dbDiameterProfileData.setIsCustomRxAppId(diameterProfileData.getIsCustomRxAppId());
			dbDiameterProfileData.setIsCustomS9AppId(diameterProfileData.getIsCustomS9AppId());
			dbDiameterProfileData.setIsCustomSyAppId(diameterProfileData.getIsCustomSyAppId());
			dbDiameterProfileData.setIsDWGatewayLevel(diameterProfileData.getIsDWGatewayLevel());
			dbDiameterProfileData.setRetransmissionCnt(diameterProfileData.getRetransmissionCnt());
			dbDiameterProfileData.setTlsEnable(diameterProfileData.getTlsEnable());
			dbDiameterProfileData.setTimeout(diameterProfileData.getTimeout());
			dbDiameterProfileData.setPccProvision(diameterProfileData.getPccProvision());
			dbDiameterProfileData.setSupportedVendorList(diameterProfileData.getSupportedVendorList());
			dbDiameterProfileData.setMultiChargingRuleEnabled(diameterProfileData.getMultiChargingRuleEnabled());
			dbDiameterProfileData.setSupportedStandard(diameterProfileData.getSupportedStandard());
			dbDiameterProfileData.setUmStandard(diameterProfileData.getUmStandard());
			dbDiameterProfileData.setSessionCleanUpCER(diameterProfileData.getSessionCleanUpCER());
			dbDiameterProfileData.setSessionCleanUpDPR(diameterProfileData.getSessionCleanUpDPR());
			dbDiameterProfileData.setCerAvps(diameterProfileData.getCerAvps());
			dbDiameterProfileData.setDprAvps(diameterProfileData.getDprAvps());
			dbDiameterProfileData.setTransportProtocol(diameterProfileData.getTransportProtocol());
			dbDiameterProfileData.setSocketReceiveBufferSize(diameterProfileData.getSocketReceiveBufferSize());
			dbDiameterProfileData.setSendDPRCloseEvent(diameterProfileData.getSendDPRCloseEvent());
			dbDiameterProfileData.setDwrAvps(diameterProfileData.getDwrAvps());
			dbDiameterProfileData.setSocketSendBufferSize(diameterProfileData.getSocketSendBufferSize());
			dbDiameterProfileData.setSocketReceiveBufferSize(diameterProfileData.getSocketReceiveBufferSize());
			dbDiameterProfileData.setTcpNagleAlgorithm(diameterProfileData.getTcpNagleAlgorithm());
			//dbDiameterProfileData.setDwrDuration(diameterProfileData.getDwrDuration());
			dbDiameterProfileData.setInitConnectionDuration(diameterProfileData.getInitConnectionDuration());
			if(diameterProfileData.getSessionLookUpKey()!=null){
				dbDiameterProfileData.setSessionLookUpKey(diameterProfileData.getSessionLookUpKey());
			}
			session.update(dbDiameterProfileData);
			session.flush();
			
			deleteGWPacketMapData(gatewayProfileData.getProfileId());
			deleteGatewayProfileRuleMappingRelData(gatewayProfileData.getProfileId());
			
			if(gatewayProfileData.getSupportedStandard() != SupportedStandard.CISCOSCE.getId()){
				//deletePCCRuleMapData(gatewayProfileData.getProfileId());
				
				for(PCCRuleMappingData mappingData : gatewayProfileData.getDiameterProfileData().getPccRuleMappingList()) {
					mappingData.setProfileId(gatewayProfileData.getProfileId());
					session.save(mappingData);
				}
				//deleteGatewayProfileRuleMappingRelData(gatewayProfileData.getProfileId());
				for(GatewayProfileRuleMappingData profileRuleMappingData : gatewayProfileData.getDiameterProfileData().getGatewayProfileRuleMappingList()) {
					profileRuleMappingData.setProfileId(gatewayProfileData.getProfileId());
					session.save(profileRuleMappingData);
				}
			}
			for(GatewayProfilePacketMapData packetMapData : gatewayProfileData.getDiameterProfileData().getGwProfilePacketMapList()) {
				packetMapData.setProfileId(gatewayProfileData.getProfileId());
				session.save(packetMapData);
			}
			
			deleteGroovyScriptsData(gatewayProfileData.getProfileId());
			for(GroovyScriptData groovyScriptData : gatewayProfileData.getDiameterProfileData().getGroovyScriptsList()) {
				groovyScriptData.setProfileId(gatewayProfileData.getProfileId());
				session.save(groovyScriptData);
			}			
			
			GatewayProfileData dbGateWayProfileData=(GatewayProfileData) session.load(GatewayProfileData.class,gatewayProfileData.getProfileId());
			setUpdateAuditDetail(dbGateWayProfileData);
			session.update(dbGateWayProfileData);
			session.flush();
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}

	}
	
	/*public void deletePCCRuleMapData(Long profileId)throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(PCCRuleMappingData.class);
			criteria.add(Restrictions.eq("profileId", profileId));
			List<PCCRuleMappingData> pccRuleMapList = criteria.list();
			
			for(PCCRuleMappingData pccRuleMapData : pccRuleMapList){
				pccRuleMapData = (PCCRuleMappingData) session.load(PCCRuleMappingData.class, pccRuleMapData.getPccRuleMapId());
				session.delete(pccRuleMapData);
			}
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}*/
	public void deleteGroovyScriptsData(Long profileId)throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GroovyScriptData.class);
			criteria.add(Restrictions.eq("profileId", profileId));
			List<GroovyScriptData> groovyScriptsDataList = criteria.list();
			
			for(GroovyScriptData groovyScriptData : groovyScriptsDataList){
				groovyScriptData = (GroovyScriptData) session.load(GroovyScriptData.class, groovyScriptData.getGroovySctiptId());
				session.delete(groovyScriptData);
			}
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}	
	public void deleteGWPacketMapData(Long profileId)throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class);
			criteria.add(Restrictions.eq("profileId", profileId));
			List<GatewayProfilePacketMapData> profilePacketMapList = criteria.list();
			
			for(GatewayProfilePacketMapData packetMapData : profilePacketMapList){
				GatewayProfilePacketMapData packetData = (GatewayProfilePacketMapData) session.load(GatewayProfilePacketMapData.class, packetMapData.getGppmId());
				session.delete(packetData);
			}
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
	private void deleteGatewayProfileRuleMappingRelData(Long profileId)throws DataManagerException {
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfileRuleMappingData.class);
			criteria.add(Restrictions.eq("profileId", profileId));
			List<GatewayProfileRuleMappingData> profilePacketMapList = criteria.list();
			
			for(GatewayProfileRuleMappingData packetMapData : profilePacketMapList){
				GatewayProfileRuleMappingData packetData = (GatewayProfileRuleMappingData) session.load(GatewayProfileRuleMappingData.class, packetMapData.getProfileRuleMappingId());
				session.delete(packetData);
			}
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
//
//			if(oldCommProtocolId.equalsIgnoreCase(GatewayConstants.CISCO_GATEWAY_ID)){
//				CiscoSceProfileData ciscoSceProfileData  = new CiscoSceProfileData();
//				ciscoSceProfileData.setProfileId(profileId);
//				session.delete(ciscoSceProfileData);
//			}else if(oldCommProtocolId.equalsIgnoreCase(GatewayConstants.RADIUS_GATEWAY_ID)){
//				RadiusProfileData radiusProfileData = new RadiusProfileData();
//				radiusProfileData.setProfileId(profileId);
//				session.delete(radiusProfileData);
//			}else if(oldCommProtocolId.equalsIgnoreCase(GatewayConstants.DIAMETER_GATEWAY_ID)){
//				DiameterProfileData diameterProfileData = new DiameterProfileData();
//				diameterProfileData.setProfileId(profileId);
//				session.delete(diameterProfileData);
//			}
//		}catch (HibernateException hExe) {
//			throw new DataManagerException(hExe.getMessage(), hExe);
//		}catch (Exception exp) {
//			throw new DataManagerException(exp.getMessage(), exp);
//		}
//	}
//	
//
	/**
	 * Update GATEWAY DATA
	 */
//	public void updateGatewayData(IGatewayData gatewayData,String oldCommProtocolId) throws DataManagerException{
//		try{
//			Session session = getSession();
//			Criteria criteria = session.createCriteria(GatewayData.class);
//			criteria.add(Restrictions.eq("gatewayId", gatewayData.getGatewayId()));
//			GatewayData netGatewayData = (GatewayData)criteria.uniqueResult();
//			
//			deleteObjectSet(netGatewayData.getRadiusGatewayDataSet(),session);
//			deleteObjectSet(netGatewayData.getCiscoSceGatewayDataSet(),session);
//			deleteObjectSet(netGatewayData.getDiameterGatewayDataSet(),session);
//						
//			netGatewayData.setGatewayId(gatewayData.getGatewayId());
//			netGatewayData.setAreaName(gatewayData.getAreaName());
//			netGatewayData.setGatewayType(gatewayData.getGatewayType());
//			netGatewayData.setPccProvision(gatewayData.getPccProvision());
//			netGatewayData.setCommProtocol(gatewayData.getCommProtocol());
//			netGatewayData.setConnectionUrl(gatewayData.getConnectionUrl());
//			netGatewayData.setLocationId(gatewayData.getLocationId());
//			netGatewayData.setDescription(gatewayData.getDescription());
//			netGatewayData.setProfileId(gatewayData.getProfileId());
//			netGatewayData.setHostId(gatewayData.getHostId());
//			netGatewayData.setModifiedDate(gatewayData.getModifiedDate());
//			netGatewayData.setModifiedByStaffId(gatewayData.getModifiedByStaffId());
//			
//			session.update(netGatewayData);
//			session.flush();
//			
//			if(gatewayData.getCommProtocol().equalsIgnoreCase(GatewayConstants.CISCO_GATEWAY_ID)){
//				session.save(gatewayData.getCiscoSceGatewayData());
//			}else if(gatewayData.getCommProtocol().equalsIgnoreCase(GatewayConstants.RADIUS_GATEWAY_ID)){
//				session.save(gatewayData.getRadiusGatewayData());
//			}else if(gatewayData.getCommProtocol().equalsIgnoreCase(GatewayConstants.DIAMETER_GATEWAY_ID)){
//				session.save(gatewayData.getDiameterGatewayData());
//			}
//			
//		}catch (HibernateException hExe) {
//			throw new DataManagerException(hExe.getMessage(), hExe);
//		}catch (Exception exp) {
//			throw new DataManagerException(exp.getMessage(), exp);
//		}
//	}
	public void updateGatewayBasicDetail(IGatewayData gatewayData) throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayData.class);
			criteria.add(Restrictions.eq("gatewayId", gatewayData.getGatewayId()));
			GatewayData netGatewayData = (GatewayData)criteria.uniqueResult();
			
			netGatewayData.setGatewayId(gatewayData.getGatewayId());
			netGatewayData.setAreaName(gatewayData.getAreaName());
			netGatewayData.setCommProtocol(gatewayData.getCommProtocol());
			netGatewayData.setConnectionUrl(netGatewayData.getConnectionUrl());
			netGatewayData.setGatewayName(gatewayData.getGatewayName());
			if(gatewayData.getLocationId()!=null && gatewayData.getLocationId()!=0){
				netGatewayData.setLocationId(gatewayData.getLocationId());
			}else{
				netGatewayData.setLocationId(null);
			}
			if(gatewayData.getDiameterGatewayData()!=null){
				netGatewayData.setPolicyEnforcementMethodName(gatewayData.getPolicyEnforcementMethodName());
				netGatewayData.setProfileId(gatewayData.getProfileId());
				netGatewayData.setConnectionUrl(gatewayData.getConnectionUrl());
			}
			if(gatewayData.getRadiusGatewayData()	!=null){
				netGatewayData.setPolicyEnforcementMethodName(gatewayData.getPolicyEnforcementMethodName());
				netGatewayData.setProfileId(gatewayData.getProfileId());
				netGatewayData.setConnectionUrl(gatewayData.getConnectionUrl());
			}			
			netGatewayData.setDescription(gatewayData.getDescription());			
			netGatewayData.setHostId(gatewayData.getHostId());
			
			setUpdateAuditDetail(netGatewayData);
			session.update(netGatewayData);
			session.flush();
			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	public void updateGatewayData(IGatewayData gatewayData) throws DataManagerException{
		try{
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayData.class);
			criteria.add(Restrictions.eq("gatewayId", gatewayData.getGatewayId()));
			GatewayData netGatewayData = (GatewayData)criteria.uniqueResult();
			
			setUpdateAuditDetail(netGatewayData);
			session.update(netGatewayData);
			session.flush();
			
			if(CommunicationProtocol.RADIUS.id.equalsIgnoreCase(netGatewayData.getCommProtocol())){
				List list =  session.createCriteria(RadiusGatewayData.class).add(Restrictions.eq("gatewayId", gatewayData.getGatewayId())).list();
				RadiusGatewayData newRadiusGatewayData = gatewayData.getRadiusGatewayData();
				RadiusGatewayData oldRadiusGatewayData = null;
				if(list!=null && !list.isEmpty()){
					oldRadiusGatewayData  = (RadiusGatewayData)list.get(0);
				}
				if(oldRadiusGatewayData!=null){
					oldRadiusGatewayData.setSharedSecret(newRadiusGatewayData.getSharedSecret());
					oldRadiusGatewayData.setMinLocalPort(newRadiusGatewayData.getMinLocalPort());
					session.update(oldRadiusGatewayData);
				}else{
					newRadiusGatewayData.setGatewayId(gatewayData.getGatewayId());
					session.save(newRadiusGatewayData);
				}
			}else if(CommunicationProtocol.DIAMETER.id.equalsIgnoreCase(netGatewayData.getCommProtocol())){
				List list =  session.createCriteria(DiameterGatewayData.class).add(Restrictions.eq("gatewayId", gatewayData.getGatewayId())).list();
				DiameterGatewayData newDiameterGatewayData = gatewayData.getDiameterGatewayData();
				DiameterGatewayData oldDiameterGatewayData = null;				
				if(list!=null && !list.isEmpty()){
					oldDiameterGatewayData  = (DiameterGatewayData)list.get(0);					
				}
				if(oldDiameterGatewayData!=null){					
					oldDiameterGatewayData.setHostId(newDiameterGatewayData.getHostId());
					oldDiameterGatewayData.setRealm(newDiameterGatewayData.getRealm());
					oldDiameterGatewayData.setLocalAddress(newDiameterGatewayData.getLocalAddress());
					oldDiameterGatewayData.setRequestTimeout(newDiameterGatewayData.getRequestTimeout());
					oldDiameterGatewayData.setRetransmissionCount(newDiameterGatewayData.getRetransmissionCount());
					if(newDiameterGatewayData.getAlternateHostId() != null && newDiameterGatewayData.getAlternateHostId() > 0){
						oldDiameterGatewayData.setAlternateHostId(newDiameterGatewayData.getAlternateHostId());
					}else{
						oldDiameterGatewayData.setAlternateHostId(null);
					}
					session.update(oldDiameterGatewayData);
				}else{					
					newDiameterGatewayData.setGatewayId(gatewayData.getGatewayId());
					session.save(newDiameterGatewayData);
				}
					
			}
			
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
	}
	
	
	public void deleteProfile(List<Long> profileIdList, String actionAlias) throws DataManagerException {
		try{
			Session session = getSession();			
			for(Long profileId:profileIdList){
				deleteGWPacketMapData(profileId);
				//deletePCCRuleMapData(profileId);
				deleteGroovyScriptsData(profileId);
				deleteGatewayProfileRuleMappingRelData(profileId);
				GatewayProfileData profileData = (GatewayProfileData) session.load(GatewayProfileData.class, profileId);
				session.delete(profileData);
			}
		}catch(HibernateException hExp){
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public List<DatabaseDSData> getDataSourceList() throws DataManagerException {
		List<DatabaseDSData> datasourceList;
		try
		{
			Session session = getSession();
			Criteria criteria = session.createCriteria(DatabaseDSData.class);
			datasourceList = criteria.list();
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}
		return datasourceList;
	}


	/**
     * @author MilanPaliwal
     * @param packageData
     * @param actionAlias
     * @throws DataManagerException
     */
	public void delete(List<Long> gatewayIdList)throws DataManagerException {
		try{
			Session session = getSession();
			Criteria criteria=session.createCriteria(GatewayData.class).add(Restrictions.in("gatewayId", gatewayIdList.toArray()));
			List<GatewayData> list = criteria.list();
			if(list!=null && !list.isEmpty()){
				for (Iterator iterator = list.iterator(); iterator.hasNext();) {
					GatewayData gatewayData = (GatewayData) iterator.next();
					deleteObjectSet(gatewayData.getRadiusGatewayDataSet(),session);
					deleteObjectSet(gatewayData.getDiameterGatewayDataSet(),session);
					session.delete(gatewayData);
				}
			}
			session.flush();
		}catch(ConstraintViolationException ce){
			throw new ConstraintViolationException(ce.getMessage(), ce.getSQLException(), ce.getConstraintName());
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
	

	public void deleteRadiusAttributeMapData(Long profileId)throws DataManagerException {
		try{
			Session session = getSession();
		
			Criteria criteria=session.createCriteria(RadiusAttributeMapData.class);
			criteria.add(Restrictions.eq("gatewayProfileId", profileId));
			List<RadiusAttributeMapData> listRadiusAttributeMapData=null;
			listRadiusAttributeMapData=criteria.list();
			
			for(RadiusAttributeMapData radiusattributeMapData:listRadiusAttributeMapData){
				session.delete(radiusattributeMapData);
			}
			session.flush();
			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	public void create(RadiusAttributeMapData radiusAttributeMapData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(radiusAttributeMapData);
			if(radiusAttributeMapData.getRadiusValueMap()!=null){
				for(int i=0;i<radiusAttributeMapData.getRadiusValueMap().size();i++){
					
					radiusAttributeMapData.getRadiusValueMap().get(i).setRadiusAttributeId(radiusAttributeMapData.getRadiusAttributeId());
					session.save(radiusAttributeMapData.getRadiusValueMap().get(i));
				}
			}
			session.flush();
			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}
	
	public void create(DiameterAttributeMapData diameterAttributeMapData) throws DataManagerException {
		try{
			Session session = getSession();
			session.save(diameterAttributeMapData);
			if(diameterAttributeMapData.getDiameterValueMapList()!=null){
			for(int i=0;i<diameterAttributeMapData.getDiameterValueMapList().size();i++){
				
				diameterAttributeMapData.getDiameterValueMapList().get(i).setDiameterAttributeId(diameterAttributeMapData.getDiameterAttributeId());
				session.save(diameterAttributeMapData.getDiameterValueMapList().get(i));
			}
			}
			
			session.flush();
			
		}catch(HibernateException hExp){
	       	hExp.printStackTrace();
	        throw new DataManagerException(hExp.getMessage(), hExp);
	    }catch(Exception exp){
	      	exp.printStackTrace();
	      	throw new DataManagerException(exp.getMessage(),exp);
	    }
	}

	@Override
	public List<DefaultAttributeMappingData> getDefaultDiameterMapping(String type)throws DataManagerException {
		List<DefaultAttributeMappingData> defaultAttributeMappingDataList=null;
		
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(DefaultAttributeMappingData.class);
			criteria.add(Restrictions.eq("parameterUsageType",type));
			defaultAttributeMappingDataList=criteria.list();
					
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}		
		return defaultAttributeMappingDataList;
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<GatewayProfilePacketMapData> getDiameterToPCRFProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		List<GatewayProfilePacketMapData> diameterToPCRFProfilePacketMapList=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class);
			if(gatewayProfileData.getProfileId() > 0) {
				 criteria.add(Restrictions.eq("profileId", gatewayProfileData.getProfileId()));
				 Criteria packetCriteria = criteria.createCriteria("packetMappingData");
				 packetCriteria.add(Restrictions.eq("commProtocol","DIAMETER"));
				 packetCriteria.add(Restrictions.eq("type",(ConversionType.GATEWAY_TO_PCRF.getConversionType())));
				 Conjunction c1=Restrictions.conjunction();
		         c1.add(Restrictions.eq("type",ConversionType.GATEWAY_TO_PCRF.getConversionType()));
		         Disjunction disjunction=Restrictions.disjunction();
				 disjunction.add(c1);
				 packetCriteria.add(disjunction);
				 diameterToPCRFProfilePacketMapList=criteria.addOrder(Order.asc("orderNumber")).list();
			}	
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}	
		return diameterToPCRFProfilePacketMapList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GatewayProfilePacketMapData> getPCRFToDiameterProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		List<GatewayProfilePacketMapData> pcrfToDiameterProfilePacketMapList=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class);
			if(gatewayProfileData.getProfileId() > 0) {
				criteria.add(Restrictions.eq("profileId", gatewayProfileData.getProfileId()));
				Criteria packetCriteria = criteria.createCriteria("packetMappingData");
				packetCriteria.add(Restrictions.eq("commProtocol","DIAMETER"));
				packetCriteria.add(Restrictions.eq("type",ConversionType.PCRF_TO_GATEWAY.getConversionType()));
				pcrfToDiameterProfilePacketMapList=criteria.addOrder(Order.asc("orderNumber")).list();
			}	
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}	
		return pcrfToDiameterProfilePacketMapList;
	}
	@SuppressWarnings("unchecked")
	@Override
	public List<GatewayProfilePacketMapData> getRadiusToPCRFProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		List<GatewayProfilePacketMapData> radiusToPCRFProfilePacketMapList=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class);
			if(gatewayProfileData.getProfileId() > 0) {
				 criteria.add(Restrictions.eq("profileId", gatewayProfileData.getProfileId()));
				 Criteria packetCriteria = criteria.createCriteria("packetMappingData");
				 packetCriteria.add(Restrictions.eq("commProtocol","RADIUS"));
				 packetCriteria.add(Restrictions.eq("type",ConversionType.GATEWAY_TO_PCRF.getConversionType()));				                        
				 radiusToPCRFProfilePacketMapList=criteria.addOrder(Order.asc("orderNumber")).list();
			}	
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}	
		return radiusToPCRFProfilePacketMapList;
	}
	
	@SuppressWarnings("unchecked")
	@Override
	public List<GatewayProfilePacketMapData> getPCRFToRadiusProfilePacketMapList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		List<GatewayProfilePacketMapData> pcrfToRadiusProfilePacketMapList=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfilePacketMapData.class);
			if(gatewayProfileData.getProfileId() > 0) {
				criteria.add(Restrictions.eq("profileId", gatewayProfileData.getProfileId()));
				Criteria packetCriteria = criteria.createCriteria("packetMappingData");
				packetCriteria.add(Restrictions.eq("commProtocol","RADIUS"));
				packetCriteria.add(Restrictions.eq("type",ConversionType.PCRF_TO_GATEWAY.getConversionType()));
				pcrfToRadiusProfilePacketMapList=criteria.addOrder(Order.asc("orderNumber")).list();
			}	
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}	
		return pcrfToRadiusProfilePacketMapList;
	}
   
	@SuppressWarnings("unchecked")
	@Override
	public List<GatewayProfileRuleMappingData> getProfileRuleMappingRelList(GatewayProfileData gatewayProfileData) throws DataManagerException {
		List<GatewayProfileRuleMappingData> gatewayProfileRuleMappingList=null;
		try {
			Session session = getSession();
			Criteria criteria = session.createCriteria(GatewayProfileRuleMappingData.class);
			if(gatewayProfileData.getProfileId() > 0) {
				 criteria.add(Restrictions.eq("profileId", gatewayProfileData.getProfileId()));
				 gatewayProfileRuleMappingList=criteria.addOrder(Order.asc("orderNumber")).list();
			}	
		}catch (HibernateException hExe) {
			throw new DataManagerException(hExe.getMessage(), hExe);
		}catch (Exception exp) {
			throw new DataManagerException(exp.getMessage(), exp);
		}	
		return gatewayProfileRuleMappingList;
	}
	
	
	@Override
	public List<DiameterProfileData> getDiameterProfileDataList() throws DataManagerException {				
			Session session = getSession();
			Criteria criteria = session.createCriteria(DiameterProfileData.class);				
			return criteria.list();		
		
	}

	
	public List<GatewayData> getGatewayListbyProfileId(Long profileId)throws DataManagerException {
			List<GatewayData> gatewayList = null;
			try {
				Session session = getSession();
				Criteria criteria = session.createCriteria(GatewayData.class).add(Restrictions.eq("profileId",profileId));
				gatewayList = criteria.list();		
			}catch (HibernateException hExe) {
				throw new DataManagerException(hExe.getMessage(), hExe);
			}catch (Exception exp) {
				throw new DataManagerException(exp.getMessage(), exp);
			}		
			return gatewayList;
		}
		
	}




