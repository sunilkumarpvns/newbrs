package com.elitecore.nvsmx.policydesigner.model.pkg.ims;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.ims.IMSPkgData;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;

public class IMSPkgDAO {

	public static final String STATUS_PROPERTY = "status";
	private static final String MODULE="IMS-PKG-DAO";

	/*
	 * This method is used to fetch List of IMSPkgData which is not deleted
	 * @param firstResult
	 * @param maxResults
	 * @param sortColumnName
	 * @param sortColumnOrder
	 * @return List<IMSPkgData> 
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static  List<IMSPkgData> findAllImsPackagesByStatus(int firstResult,int maxResults, String sortColumnName, String sortColumnOrder) throws HibernateDataException{
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(IMSPkgData.class);
			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			if(sortColumnName != null && sortColumnName.trim().length()>0){
				if(sortColumnOrder != null && sortColumnOrder.equalsIgnoreCase("desc")){
					criteria.addOrder(Order.desc(sortColumnName));
				} else {
					criteria.addOrder(Order.asc(sortColumnName));
				}
			}else {
				criteria.addOrder(Order.asc("name"));			    
			}

			if(firstResult >=0 && maxResults >= 0) {
				criteria.setFirstResult(firstResult);
				criteria.setMaxResults(maxResults);
			}
			List<IMSPkgData> dataList = criteria.list(); 
			/*for(IMSPkgData imsPkgData : dataList){
				String groupNames = CRUDOperationUtil.getGroupNames(imsPkgData.getResourceGroupRelationDatas());
				imsPkgData.setGroupNames(groupNames);
			}*/

			return dataList;

		}catch (HibernateException he) {
			getLogger().error(MODULE, "Failed to get list of ims packages. Reason: "+he.getMessage());
			getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			getLogger().error(MODULE, "Failed to get list of ims packages. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}

	} 
	
	@SuppressWarnings("unchecked")
	public static   List<IMSPkgData> searchByCriteria(IMSPkgData imsPkgData,int firstResult,int maxResults, String sortColumnName, String sortColumnOrder,final String staffBelongingGroupIds) throws HibernateDataException{
		
	    try{
	    	
	    	StringBuilder QUERY = new StringBuilder();
	    	
	    	QUERY.append("  from IMSPkgData pkg "+" where ( "+getLikeQuery(staffBelongingGroupIds)+" )");
	    	
	    	if(Strings.isNullOrBlank(imsPkgData.getName()) == false){
	    		QUERY.append("AND ( lower(pkg.name) like lower('%"+imsPkgData.getName()+"%' ))");
	    	}
	    	
	    	if(imsPkgData.getPrice() != null ){
	    		QUERY.append("AND ( pkg.price =  "+imsPkgData.getPrice()+" )");
	    	}
	    	
	    	if(Strings.isNullOrBlank(imsPkgData.getType()) == false){
	    		QUERY.append("AND ( pkg.type like '%"+imsPkgData.getType()+"%' )");
	    	}
	    	
	    	if(Strings.isNullOrBlank(imsPkgData.getPackageMode()) == false){
	    		QUERY.append("AND ( pkg.packageMode = '"+imsPkgData.getPackageMode()+"' )");
	    	}
	    	
	    	if(Strings.isNullOrBlank(imsPkgData.getStatus()) == false){
	    		QUERY.append("AND ( pkg.status like '"+imsPkgData.getStatus()+"' )");
	    	}else{
	    		QUERY.append("AND ( pkg.status != '"+CommonConstants.STATUS_DELETED+"' )");
	    	}
	    	
	    	if(Strings.isNullOrBlank(sortColumnName) == false && Strings.isNullOrBlank(sortColumnOrder) == false){
	    		QUERY.append("order by pkg."+sortColumnName+" "+sortColumnOrder);
	    	}
	    	Session session =  HibernateSessionFactory.getSession();
			Query query = session.createQuery(QUERY.toString());
			query.setFirstResult(firstResult);
			query.setMaxResults(maxResults);
			return query.list();
		}catch (HibernateException he) {
			LogManager.getLogger().error(MODULE, "Failed to search entity "+ imsPkgData.getClass().getName()+". Reason: "+he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to search entity "+ imsPkgData.getClass().getName()+". Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}	
	}

	private static String getLikeQuery(String staffBelongingGroupIds){
		List<String> groups = Strings.splitter(',').trimTokens().split(staffBelongingGroupIds);
		StringBuilder groupLike = new StringBuilder("pkg.groups like '%");
		groupLike.append(groups.get(0)).append("%' ");
		for (int i = 1; i < groups.size(); i++) {
			groupLike.append("or pkg.groups like '%")
			.append(groups.get(i))
			.append("%' ");
		}
		return groupLike.toString();
	}

	public static MediaTypeData getMediaTypeBy(String mediaTypeIdentifier) {

		try{
			MediaTypeData mediaTypeData = new MediaTypeData();
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(MediaTypeData.class);
			criteria.add(Restrictions.eq("mediaIdentifier", Long.valueOf(mediaTypeIdentifier)));

			List<MediaTypeData> mediaTypeDatas = criteria.list();
			if(Collectionz.isNullOrEmpty(mediaTypeDatas) == false) {
				mediaTypeData = mediaTypeDatas.get(0);
			}
			return  mediaTypeData;

		} catch (Exception e) {
			getLogger().error(MODULE, "Failed to fetch Media Types. Reason: "+e.getMessage());
			getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}

	}
}
