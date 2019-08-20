package com.elitecore.nvsmx.policydesigner.model.pkg.pccrule;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DefaultServiceDataFlowData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.util.ResourceDataGroupPredicate;
import com.google.gson.Gson;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class PccRuleDAO {
	
	private static final String MODULE="PCCRule-DAO";


	/**
	 * 
	 * @param serviceType
	 * @return List<DefaultServiceDataFlow>
	 * @author Dhyani.raval
	 */
	public static List<DefaultServiceDataFlowData> getDefaultServiceDataFlows(DataServiceTypeData serviceType){
		try {
			Session session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(DefaultServiceDataFlowData.class);
			criteria.add(Restrictions.eq("dataServiceTypeData", serviceType));
			return criteria.list();
		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to fetch DefaultServiceDataFlow. Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to fetch DefaultServiceDataFlow. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	public static List<PCCRuleData> getAvailablePCCRuleList(String qosProfileDetailId,PCCRuleScope scope,String staffBelongingGroupIds){
		try {
			Session session = HibernateSessionFactory.getSession();
			QosProfileDetailData qosProfileDetailData = (QosProfileDetailData) session.get(QosProfileDetailData.class,qosProfileDetailId);


			List<PCCRuleData> alreadyAttachPccRules=Collectionz.newArrayList();
			if(qosProfileDetailData !=null) {
				alreadyAttachPccRules = qosProfileDetailData.getPccRules();
			}
			Criteria criteria = session.createCriteria(PCCRuleData.class);
			criteria.add(Restrictions.eq("scope", scope));
			criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));
			List<PCCRuleData> pccRules = criteria.list();
			if (Strings.isNullOrBlank(staffBelongingGroupIds) == false) {
				CRUDOperationUtil.filterpackages(pccRules, staffBelongingGroupIds);
			}
			Iterator<PCCRuleData> iterator = pccRules.iterator();
			while(iterator.hasNext()){
				if(alreadyAttachPccRules.contains(iterator.next())==true){
					iterator.remove();
				}
			}
			return pccRules;
		}catch (HibernateException he) {
			LogManager.getLogger().error(MODULE,"Failed to fetch PCC Rule List. Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE,"Failed to fetch PCC Rule List. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	public static   List<PCCRuleData> searchByCriteria(PCCRuleData pccRuleData,int firstResult,int maxResults, String sortColumnName, String sortColumnOrder,final String staffBelongingGroupIds) throws Exception{
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(pccRuleData.getClass());
		  	criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));


			if(Strings.isNullOrBlank(pccRuleData.getName()) == false){
				criteria.add(Restrictions.ilike("name", pccRuleData.getName(), MatchMode.ANYWHERE));
			}
            if(Strings.isNullOrBlank(pccRuleData.getType()) == false){
				criteria.add(Restrictions.like("type", pccRuleData.getType()));
			}
			if(pccRuleData.getScope() !=null){
				criteria.add(Restrictions.eq("scope", pccRuleData.getScope()));
			}

			if(Strings.isNullOrBlank(sortColumnName) == false && Strings.isNullOrBlank(sortColumnOrder) == false){
				if(sortColumnOrder.equalsIgnoreCase("desc")){
					criteria.addOrder(Order.desc(sortColumnName));
				} else {
					criteria.addOrder(Order.asc(sortColumnName));
				}
			}
			if(pccRuleData.getDataServiceTypeData()!=null && Strings.isNullOrBlank(pccRuleData.getDataServiceTypeData().getId())==false){
				DataServiceTypeData dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class, pccRuleData.getDataServiceTypeData().getId());
				criteria.add(Restrictions.eq("dataServiceTypeData", dataServiceTypeData));
			}

			if(firstResult >=0 && maxResults  >= 0 ) {
				criteria.setFirstResult(firstResult);
				criteria.setMaxResults(maxResults);

			}
			List<PCCRuleData> pccRuleDatas = criteria.list();
			if (Strings.isNullOrBlank(staffBelongingGroupIds) == false) {
				CRUDOperationUtil.filterpackages(pccRuleDatas, staffBelongingGroupIds);
			}
			return pccRuleDatas;
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to search entity "+ pccRuleData.getClass().getName()+".Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw e;
		}
	}

	public static String getStaffBelongingServiceTypeJson(List<String> staffBelongingGroups) {

		List<DataServiceTypeData> dataServiceTypeDataList = CRUDOperationUtil.findAllWhichIsNotDeleted(DataServiceTypeData.class);
		Map<String, List<RatingGroupData>> ratingGroupDataMap = Maps.newHashMap();
		ResourceDataGroupPredicate resourceDataGroupPredicate = ResourceDataGroupPredicate.create(staffBelongingGroups);

		for (DataServiceTypeData dataServiceTypeData : dataServiceTypeDataList) {
			List<RatingGroupData> ratingGroups =  dataServiceTypeData.getRatingGroupDatas().stream().filter(resourceDataGroupPredicate).collect(Collectors.toList());
			ratingGroupDataMap.put(dataServiceTypeData.getId(), ratingGroups);
		}

		Gson gson = GsonFactory.defaultInstance();
		return gson.toJson(ratingGroupDataMap);
	}

	public static String getStaffBelongingRatingGroupJson(List<String> staffBelongingGroups) {
		List<RatingGroupData> ratingGroupDatas = CRUDOperationUtil.findAllWhichIsNotDeleted(RatingGroupData.class);
		List<RatingGroupData> filteredRatingGroups = ratingGroupDatas.stream().filter(ResourceDataGroupPredicate.create(staffBelongingGroups)).collect(Collectors.toList());

		Gson gson = GsonFactory.defaultInstance();
		return gson.toJson(filteredRatingGroups);
	}

}