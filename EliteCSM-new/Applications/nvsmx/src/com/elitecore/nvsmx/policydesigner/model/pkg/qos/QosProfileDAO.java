package com.elitecore.nvsmx.policydesigner.model.pkg.qos;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PccRuleWrapper;
import com.elitecore.nvsmx.policydesigner.model.pkg.qos.QosProfileDetailWrapper.QosProfileDetailWrapperBuilder;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.List;
import java.util.Set;

public class QosProfileDAO {
    	private static final String MODULE="QoS-DAO";
		private static final String NAME_PROPERTY = "name";

	public static List<QosProfileDetailWrapper> getQosProfileDetailWrappers(PkgData pkgdata,String sortColumnName, String sortColumnOrder){
		try {
			Session session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(QosProfileData.class).add(Restrictions.eq("pkgData", pkgdata));
			criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));
			if(Strings.isNullOrBlank(sortColumnName)){
				sortColumnName = "orderNo";
			}
			if(sortColumnOrder != null && sortColumnOrder.equalsIgnoreCase("desc")){
			    criteria.addOrder(Order.desc(sortColumnName));
			} else {
			    criteria.addOrder(Order.asc(sortColumnName));
			}

			
			List<QosProfileData> list =  criteria.list();
			
			
			List<QosProfileDetailWrapper> qosProfileDetailWrappers = Collectionz.newArrayList();			
		
			for( QosProfileData qosProfile : list ){
				if(qosProfile.getQuotaProfile() != null){
				     QosProfileDetailWrapper qosProfileDetailWrapper = new QosProfileDetailWrapperBuilder(qosProfile.getId(),qosProfile.getName(), qosProfile.getOrderNo())
				     .withQuotaProfile(qosProfile.getQuotaProfile())
				     .withQosProfileDetails(qosProfile.getQosProfileDetailDataList()).build();
				     qosProfileDetailWrappers.add(qosProfileDetailWrapper);
				}else{
					QosProfileDetailWrapper qosProfileDetailWrapper = new QosProfileDetailWrapperBuilder(qosProfile.getId(),qosProfile.getName(), qosProfile.getOrderNo())
				     .withSyQuotaProfile(qosProfile.getSyQuotaProfileData())
				     .withQosProfileDetails(qosProfile.getQosProfileDetailDataList()).build();
				     qosProfileDetailWrappers.add(qosProfileDetailWrapper);
				}
				
			     
			}
			return qosProfileDetailWrappers;
		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to fetch QoS Profile Detail. Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to fetch QoS Profile Detail. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}
	
	/**
	 * This method will get data of PccRule and set those data in to wrapper to display it into the DataTable.
	 * @param qosProfileDetail
	 * @return List<PccRuleWrapper>
	 * @author Dhyani.raval
	 */
	public static List<PccRuleWrapper> getPccRules(QosProfileDetailData qosProfileDetail){
		PccRuleWrapper pccRuleWrapper = null;
		try {
			QosProfileDetailData qosProfileDetailData = CRUDOperationUtil.get(QosProfileDetailData.class,qosProfileDetail.getId());
			List<PccRuleWrapper> wrapperPccRules = Collectionz.newArrayList();
			if(qosProfileDetailData != null && Collectionz.isNullOrEmpty(qosProfileDetailData.getPccRules())==false) {
			   List<PCCRuleData> pccRules = qosProfileDetailData.getPccRules();
		      	for(PCCRuleData pccRule : pccRules){
						pccRuleWrapper = new PccRuleWrapper.PccRuleWrapperBuilder().withPccRulesDetails(pccRule).build();
						wrapperPccRules.add(pccRuleWrapper);
				}
			}

			if(qosProfileDetailData != null && Collectionz.isNullOrEmpty(qosProfileDetailData.getChargingRuleBaseNames())==false) {
				List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas = qosProfileDetailData.getChargingRuleBaseNames();

				for(ChargingRuleBaseNameData chargingRuleBaseNameData : chargingRuleBaseNameDatas){
					pccRuleWrapper = new PccRuleWrapper.PccRuleWrapperBuilder().withPccRulesDetails(chargingRuleBaseNameData).build();
					wrapperPccRules.add(pccRuleWrapper);
				}
			}

			return wrapperPccRules;
		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to fetch PccRule. Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to fetch PccRule. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	@SuppressWarnings("unchecked")
	public static QosProfileData getQosProfilebByName(String name) {
			QosProfileData qosProfile=null;
			try {
				Session session=HibernateSessionFactory.getSession();
			    Criteria criteria = session.createCriteria(QosProfileData.class)
			    		           .add(Restrictions.eq(NAME_PROPERTY, name));
				List<QosProfileData> list =  criteria.list();
				if(Collectionz.isNullOrEmpty(list)==false){
					qosProfile= list.get(0);
				}
				return qosProfile;
			} catch (HibernateException he) {
				LogManager.getLogger().error(MODULE,"Failed to fetch qos Profile for name: " + name + " .Reason: " + he.getMessage());
				LogManager.getLogger().trace(MODULE, he);
				throw new HibernateDataException(he.getMessage(), he);
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE,"Failed to fetch qos Profile for name: " + name + " .Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
				throw new HibernateDataException(e.getMessage(), e);
			}
	}
	
	public static boolean isDuplicateName(String mode,String id,String name,String pkgId) throws HibernateDataException{
		try{
		   PkgData pkg = new PkgData();
		   pkg.setId(pkgId);
		
			Criteria criteria  = HibernateSessionFactory.getSession().createCriteria(QosProfileData.class);
			ProjectionList proList = Projections.projectionList();
			proList.add(Projections.property(NAME_PROPERTY));
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase(CRUDOperationUtil.MODE_UPDATE)){
				criteria.add(Restrictions.ne("id", id));
			}
			List list = criteria.add(Restrictions.ilike(NAME_PROPERTY,name))
					                .add(Restrictions.eq("pkgData", pkg))
					                .add(Restrictions.ne(CRUDOperationUtil.STATUS_PROPERTY, CommonConstants.STATUS_DELETED))
					                .list();
		   if(Collectionz.isNullOrEmpty(list) == true){
				return false;
			}
		}catch (HibernateException he) {
			LogManager.getLogger().error(MODULE, "Failed to Qos Profile  for name "+name+".Reason: "+he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to Qos Profile  for name "+name+".Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}	
		return true;
		}
	

	/**
	 * Fetch all the unique QosProfiles names which is not deleted  
	 * @return List<String>
	 * @author Dhyani.Raval
	 */
	@SuppressWarnings("unchecked")
	public static Set<String> getQosProfilesNames(String staffBelongingGroupIds) {
		Set<String> qosProfileDatas = Collectionz.newHashSet();
		try {
			Session session=HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(QosProfileData.class);
			criteria.add(Restrictions.ne(CRUDOperationUtil.STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			List<QosProfileData> qosProfiles = criteria.list();
			CRUDOperationUtil.filterpackages(qosProfiles, staffBelongingGroupIds);
			qosProfiles.forEach(qosProfile -> qosProfileDatas.add(qosProfile.getName()));
			return qosProfileDatas;
		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to fetch qos Profiles names: .Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to fetch qos Profiles names: .Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	public static List<DataRateCardData> getDataRateCardDataList(PkgData pkgData){
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(DataRateCardData.class);
		criteria.add(Restrictions.eq("pkgData", pkgData));
		criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));
		return criteria.list();
	}

	
}
