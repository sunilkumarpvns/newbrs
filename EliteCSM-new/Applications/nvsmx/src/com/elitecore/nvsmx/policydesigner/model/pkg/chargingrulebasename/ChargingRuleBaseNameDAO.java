package com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.hibernate.Criteria;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

public class ChargingRuleBaseNameDAO {
	
	private static final String MODULE="ChargingRuleBaseName-DAO";


	public static   List<ChargingRuleBaseNameData> searchByCriteria(ChargingRuleBaseNameData chargingRuleBaseNameData,int firstResult,int maxResults, String sortColumnName, String sortColumnOrder,final String staffBelongingGroupIds) throws Exception{
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(chargingRuleBaseNameData.getClass());
		  	criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));


			if(Strings.isNullOrBlank(chargingRuleBaseNameData.getName()) == false){
				criteria.add(Restrictions.ilike("name", chargingRuleBaseNameData.getName(), MatchMode.ANYWHERE));
			}

			if(Strings.isNullOrBlank(sortColumnName) == false && Strings.isNullOrBlank(sortColumnOrder) == false){
				if(sortColumnOrder.equalsIgnoreCase("desc")){
					criteria.addOrder(Order.desc(sortColumnName));
				} else {
					criteria.addOrder(Order.asc(sortColumnName));
				}
			}

			if(firstResult >=0 && maxResults  >= 0 ) {
				criteria.setFirstResult(firstResult);
				criteria.setMaxResults(maxResults);

			}
			List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas = criteria.list();
			if (Strings.isNullOrBlank(staffBelongingGroupIds) == false) {
				CRUDOperationUtil.filterpackages(chargingRuleBaseNameDatas, staffBelongingGroupIds);
			}
			return chargingRuleBaseNameDatas;

		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to search entity "+ chargingRuleBaseNameData.getClass().getName()+".Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw e;
		}
	}

}