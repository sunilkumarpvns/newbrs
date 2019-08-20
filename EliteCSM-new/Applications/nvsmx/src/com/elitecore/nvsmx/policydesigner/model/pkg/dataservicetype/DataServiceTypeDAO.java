package com.elitecore.nvsmx.policydesigner.model.pkg.dataservicetype;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleDataServiceTypeData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.notification.QuotaNotificationData;
import com.elitecore.corenetvertex.pkg.notification.UsageNotificationData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileDetailData;
import com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename.ChargingRuleBaseNameDAO;
import com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PccRuleDAO;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;


/**
 * contain methods related to hibernate operations for {@code DataServiceTypeData}
 * 
 * @author Dhyani.Raval
 *
 */

public class DataServiceTypeDAO {
	
	private static final String MODULE="Service-Type-DAO";
	private static final String NAME_PROPERTY="name";
    private static final String ID_PROPERTY = "id";
    public static final String  MODE_UPDATE = "update";
    public static final String  MODE_CREATE = "create";
    public static final String STATUS_PROPERTY = "status";
	
	/**
	 * Gives comma separated package names for given service type    
	 * @param id
	 * @return {@link String}
	 * 
	 */
	public static String AttachedWithPackages(String id){
		
		Set<String> packageNames = Collectionz.newHashSet();
		try{
			List<PkgData> pkgDatas = CRUDOperationUtil.findAllWhichIsNotDeleted(PkgData.class);
			
			if(getLogger().isDebugLogLevel()){
				getLogger().debug(MODULE, "Read All Packages Successfully...");
			}
			
			for(PkgData pkgData : pkgDatas){
				if(QuotaProfileType.USAGE_METERING_BASED.equals(pkgData.getQuotaProfileType())){
					for(QuotaProfileData quotaProfileData : pkgData.getQuotaProfiles()){
						for(QuotaProfileDetailData quotaProfileDetailData : quotaProfileData.getQuotaProfileDetailDatas()){
							if(quotaProfileDetailData.getDataServiceTypeData() != null){
								if(quotaProfileDetailData.getDataServiceTypeData().getId().equals(id)){
									packageNames.add(quotaProfileData.getPkgData().getName());
								}
							}
						}
					}
					for(UsageNotificationData usageNotificationData : pkgData.getUsageNotificationDatas()){
						if(usageNotificationData.getDataServiceTypeData() != null){
							if(usageNotificationData.getDataServiceTypeData().getId().equals(id)){
								packageNames.add(pkgData.getName());
							}
						}
					}

					for(QuotaNotificationData quotaNotificationData : pkgData.getQuotaNotificationDatas()){
						if(quotaNotificationData.getDataServiceTypeData() != null){
							if(quotaNotificationData.getDataServiceTypeData().getId().equals(id)){
								packageNames.add(pkgData.getName());
							}
						}
					}
				}else{
					for(SyQuotaProfileData syQuotaProfileData : pkgData.getSyQuotaProfileDatas()){
						for(SyQuotaProfileDetailData syQuotaProfileDetailData : syQuotaProfileData.getSyQuotaProfileDetailDatas()){
							if(syQuotaProfileDetailData.getDataServiceTypeData() != null){
								if(syQuotaProfileDetailData.getDataServiceTypeData().getId().equals(id)){
									packageNames.add(pkgData.getName());
								}
							}
						}
					}
				}


				for(QosProfileData qosProfileData : pkgData.getQosProfiles()){
					for(QosProfileDetailData qosProfileDetailData : qosProfileData.getQosProfileDetailDataList()){
						for(PCCRuleData pccRuleData : qosProfileDetailData.getPccRules()){
							if(pccRuleData.getDataServiceTypeData() != null){
								if(pccRuleData.getDataServiceTypeData().getId().equals(id)){
									packageNames.add(pccRuleData.getQosProfileDetails().get(0).getQosProfile().getPkgData().getName());
								}
							}
						}
					}
				}
				
			}
			return Strings.join(",", packageNames);
		}catch(HibernateDataException hex){
			getLogger().error(MODULE, "Failed to get packages for DataServiceTypeData. Reason: "+hex.getMessage());
			getLogger().trace(MODULE, hex);
			throw new HibernateDataException(hex.getMessage(), hex);
			
		}catch(Exception e){
			getLogger().error(MODULE, "Failed to get packages for DataServiceTypeData. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
		
	}

	public static String getAttachPccRules(String id) throws  Exception{
		Set<String> pccRuleNames =null;
		PCCRuleData pccRuleData = new PCCRuleData();
		pccRuleData.setScope(PCCRuleScope.GLOBAL);
		DataServiceTypeData dataServiceTypeData = new DataServiceTypeData();
		dataServiceTypeData.setId(id);
		pccRuleData.setDataServiceTypeData(dataServiceTypeData);

		List<PCCRuleData> pccRuleDatas = PccRuleDAO.searchByCriteria(pccRuleData, 0, Integer.MAX_VALUE, null, null, null);
		if(Collectionz.isNullOrEmpty(pccRuleDatas)==false){
			pccRuleNames = Collectionz.newHashSet();
			for(PCCRuleData pccRule : pccRuleDatas){
				pccRuleNames.add(pccRule.getName());
			}
              return Strings.join(",",pccRuleNames);
		}
           return null;
	}

	public static String getAttachChargingRuleBaseName(String id) throws  Exception{

		ChargingRuleBaseNameData chargingRuleBaseNameData = new ChargingRuleBaseNameData();
		List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas = ChargingRuleBaseNameDAO.searchByCriteria(chargingRuleBaseNameData, 0, Integer.MAX_VALUE, null, null, null);
		Set<String> chargingRuleBaseNames = Collectionz.newHashSet();
		for(ChargingRuleBaseNameData chargingRuleBaseName : chargingRuleBaseNameDatas){
			List<ChargingRuleDataServiceTypeData> chargingRuleDataServiceTypeDatas = chargingRuleBaseName.getChargingRuleDataServiceTypeDatas();
			for(ChargingRuleDataServiceTypeData chargingRuleDataServiceTypeData : chargingRuleDataServiceTypeDatas){
				DataServiceTypeData serviceType = chargingRuleDataServiceTypeData.getDataServiceTypeData();
				if(serviceType != null && serviceType.getId().equalsIgnoreCase(id)){
					chargingRuleBaseNames.add(chargingRuleBaseName.getName());
				}
			}
		}

		return Strings.join(",", chargingRuleBaseNames);
	}
	
	@SuppressWarnings("unchecked")
	public static <T> boolean isDuplicateValue(String mode, Class<T> type,String id, String name,String property) throws Exception{
		try{
			Criteria criteria  = HibernateSessionFactory.getSession().createCriteria(type);
			ProjectionList proList = Projections.projectionList();
			if(Strings.isNullOrBlank(property)==true){
				criteria.add(Restrictions.ilike(NAME_PROPERTY,name));
			    proList.add(Projections.property(NAME_PROPERTY));
			}else{
				if(property.equals("serviceIdentifier")){
					criteria.add(Restrictions.eq(property,Long.valueOf(name)));
				}else{
					criteria.add(Restrictions.eq(property,name));
				}
				proList.add(Projections.property(property));
			}
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase(MODE_UPDATE)){
				criteria.add(Restrictions.ne(ID_PROPERTY, id));
			}
			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			List<T> list = criteria.list();
			if(Collectionz.isNullOrEmpty(list) == true){
				return false;
			}
 		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to search entity "+ type.getName() +" for name "+name+". Reason: "+e.getMessage());
			throw e;
		}	
		return true;
	}

	public static <T> boolean isDuplicateIdentifierForRatingGroup(String mode, Class<T> type,String id, String name,String property) throws Exception{
		try{
			Criteria criteria  = HibernateSessionFactory.getSession().createCriteria(type);
			ProjectionList proList = Projections.projectionList();
			if(Strings.isNullOrBlank(property)==true){
				criteria.add(Restrictions.ilike(NAME_PROPERTY,name));
				proList.add(Projections.property(NAME_PROPERTY));
			}else{
				if(property.equals("identifier")){
					criteria.add(Restrictions.eq(property,Long.valueOf(name)));
				}else{
					criteria.add(Restrictions.eq(property,name));
				}
				proList.add(Projections.property(property));
			}
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase(MODE_UPDATE)){
				criteria.add(Restrictions.ne(ID_PROPERTY, id));
			}
			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			List<T> list = criteria.list();
			if(Collectionz.isNullOrEmpty(list) == true){
				return false;
			}
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to search entity "+ type.getName() +" for name "+name+". Reason: "+e.getMessage());
			throw e;
		}
		return true;
	}

}



