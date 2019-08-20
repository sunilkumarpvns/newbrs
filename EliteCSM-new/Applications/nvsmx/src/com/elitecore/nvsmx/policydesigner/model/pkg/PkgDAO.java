package com.elitecore.nvsmx.policydesigner.model.pkg;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgGroupOrderData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.SQLQuery;
import org.hibernate.Session;
import org.hibernate.criterion.*;

import javax.annotation.Nonnull;
import java.util.*;


/**
 * This class contains method related hibernate operation for {@code PkgData}
 * 
 * @author aditya
 *
 */

public class PkgDAO {

	private static final String MODULE = "PKG-DAO";

	/* property constants for criteria */
	private static final String STATUS_PROPERTY = "status";
	private static final String DELETE_PKG_GROUP_QUERY = "DELETE FROM TBLM_PKG_GROUP_ORDER WHERE PKG_ID=:pkgId";
	private static final Comparator<String> DEFAULT_GROUP_COMPARATOR = (o1, o2) -> {

        if (o1.equalsIgnoreCase(CommonConstants.DEFAULT_GROUP) && o2.equalsIgnoreCase(CommonConstants.DEFAULT_GROUP)) {
            return 0;
        } else if (o1.equalsIgnoreCase(CommonConstants.DEFAULT_GROUP)) {
            return -1;
        } else if (o2.equalsIgnoreCase(CommonConstants.DEFAULT_GROUP)) {
            return 1;
        } else {
            return o1.compareTo(o2);
        }
    };

	public static List<PkgData> findPackages(String staffBelongingGroupIds, String pkgType){
		return findPackages(-1, -1, null, null,staffBelongingGroupIds, pkgType);
	}

	/**
	 * This method is used to fetch list of {@code PkgDatawrapper} which is custom object for PkgData having set of properties
	 * rather than whole object useful for SearchPage
	 *
	 * @param firstResult
	 * @param maxResults
	 * @param sortColumnName
	 * @param sortColumnOrder
	 * @return
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static List<PkgData> findPackages(int firstResult,
											 int maxResults,
											 String sortColumnName,
											 String sortColumnOrder,
											 String staffBelongingGroupIds,
											 String type) throws HibernateDataException {
		List<PkgData> pkgDatas = Collectionz.newArrayList();
		try {
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(PkgData.class);

			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));

			addPkgTypeRestriction(type, criteria);

			if (Strings.isNullOrBlank(sortColumnName) == false && Strings.isNullOrBlank(sortColumnOrder) == false) {
				if ("desc".equalsIgnoreCase(sortColumnOrder)) {
					criteria.addOrder(Order.desc(sortColumnName));
				} else {
					criteria.addOrder(Order.asc(sortColumnName));
				}
			} else {
				criteria.addOrder(Order.asc("name"));
			}
			if(sortColumnName != null && sortColumnName.trim().length()>0){
			    if(sortColumnOrder != null && "desc".equalsIgnoreCase(sortColumnOrder)){
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
			pkgDatas = criteria.list();

			if (Strings.isNullOrBlank(staffBelongingGroupIds) == false) {
				CRUDOperationUtil.filterpackages(pkgDatas, staffBelongingGroupIds);
			}

		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to fetch package list. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return pkgDatas;
	}

	private static void addPkgTypeRestriction(String type, Criteria criteria) {
		if (PkgType.BASE.name().equalsIgnoreCase(type)) {
			criteria.add(Restrictions.in("type", new String[]{PkgType.BASE.name(),PkgType.ADDON.name()}));

		} else if (PkgType.EMERGENCY.name().equalsIgnoreCase(type)) {
			criteria.add(Restrictions.eq("type", PkgType.EMERGENCY.name()));
		} else if (PkgType.PROMOTIONAL.name().equalsIgnoreCase(type)) {
			criteria.add(Restrictions.eq("type", PkgType.PROMOTIONAL.name()));
		}
	}

	@SuppressWarnings("unchecked")
	public static List<PkgData> searchByCriteria(PkgData pkgData, int firstResult, int maxResults, String sortColumnName, String sortColumnOrder, final String staffBelongingGroupIds) throws HibernateDataException {

		List<PkgData> pkgDatas = Collectionz.newArrayList();
		try {
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(PkgData.class);

			if (Strings.isNullOrBlank(pkgData.getStatus()) == false) {
				criteria.add(Restrictions.eq(STATUS_PROPERTY, pkgData.getStatus()));
			} else {
				criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			}

			if (Strings.isNullOrBlank(pkgData.getName()) == false) {
				criteria.add(Restrictions.ilike("name", pkgData.getName(), MatchMode.ANYWHERE));
			}

			if (pkgData.getPrice() != null) {
				criteria.add(Restrictions.eq("price", pkgData.getPrice()));
			}

			if (Strings.isNullOrBlank(pkgData.getType()) == false) {
				criteria.add(Restrictions.like("type", pkgData.getType()));
			}
			criteria.add(Restrictions.ne("type", PkgType.EMERGENCY.name()));
			criteria.add(Restrictions.ne("type", PkgType.PROMOTIONAL.name()));

			if (Strings.isNullOrBlank(pkgData.getPackageMode()) == false) {
				criteria.add(Restrictions.eq("packageMode", pkgData.getPackageMode()));
			}

			if (Strings.isNullOrBlank(sortColumnName) == false && Strings.isNullOrBlank(sortColumnOrder) == false) {
				if ("desc".equalsIgnoreCase(sortColumnOrder)) {
					criteria.addOrder(Order.desc(sortColumnName));
				} else {
					criteria.addOrder(Order.asc(sortColumnName));
				}
			}

			if(firstResult >=0 && maxResults >= 0) {
				criteria.setFirstResult(firstResult);
				criteria.setMaxResults(maxResults);
			}
			pkgDatas = criteria.list();

			if (Strings.isNullOrBlank(staffBelongingGroupIds) == false) {
				CRUDOperationUtil.filterpackages(pkgDatas, staffBelongingGroupIds);
			}
		} catch (HibernateException he) {
			LogManager.getLogger().error(MODULE, "Failed to search entity " + pkgData.getClass().getName() + ".Reason: " + he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to search entity " + pkgData.getClass().getName() + ".Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
		return pkgDatas;
	}

	public static Integer getMaxOrderNumber(PkgType pkgType) {

		try {
			Session session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(PkgData.class);
			criteria.setProjection(Projections.max("orderNumber"));
			Criterion typeCriterion = Restrictions.eq("type", pkgType.name());
			Criterion statusCriterion = Restrictions.ne("status", "DELETED");
			criteria.add(typeCriterion);
			criteria.add(statusCriterion);
			Integer maxOrderNumber = (Integer) criteria.uniqueResult();

			if (maxOrderNumber == null) {
				maxOrderNumber = 0;
			}

			return maxOrderNumber;
		} catch (Exception ex) {
			LogManager.getLogger().error(MODULE, "Failed get max order number. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return 0;
		}
	}

	public static int getGroupWiseMaxOrder(String groupId,String pkgType){
		int maxOrder = 0;
		Session session = HibernateSessionFactory.getSession();
		Criteria criteria = session.createCriteria(PkgGroupOrderData.class);
		criteria.setProjection(Projections.property("orderNumber"));
		criteria.add(Restrictions.and(Restrictions.eq("type", pkgType),Restrictions.eq("groupId",groupId)));
		criteria.addOrder(Order.desc("orderNumber"));
		List<Integer> list = criteria.list();
		if (Collectionz.isNullOrEmpty(list) == false) {
			maxOrder = list.get(0) ;
		}
		return maxOrder;
	}

	public static Integer getNoOfPackagesOfType(PkgType pkgType) {

		Session session = null;
		try {
			session = HibernateSessionFactory.getSession();
			Criteria criteria = session.createCriteria(PkgData.class);
			Criterion typeCriterion = Restrictions.eq("type", pkgType.name());
			Criterion statusCriterion = Restrictions.ne("status", "DELETED");
			criteria.add(typeCriterion);
			criteria.add(statusCriterion);
			return criteria.list().size();

		} catch (Exception ex) {
			LogManager.getLogger().error(MODULE, "Failed get max order number. Reason: " + ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return 0;

		}
	}

	//this method is used for Global Plan group wise limit check purposes only
	public static Map<String,List<PkgData>> fetchGroupWisePkgMap(String pkgId, String mode, PkgType pkgType) {
		List<PkgData> packages = findListOfPackages(pkgId,mode,pkgType);

		if (Collectionz.isNullOrEmpty(packages) == true) {
			return null;
		}

		Map<String, List<PkgData>> groupWisePackageMap = Maps.newHashMap();
		for (PkgData pkg : packages) {
			//if mode is update then skip current package
			if(CRUDOperationUtil.MODE_UPDATE.equalsIgnoreCase(mode) == true && pkg.getId().equals(pkgId) == true ){
				continue;
			}
			//if package has only one group & that is DEFAULT GROUP
			if (CommonConstants.DEFAULT_GROUP_ID.equalsIgnoreCase(pkg.getGroups()) == true) {
				addToGroupWiseMap(groupWisePackageMap, pkg, CommonConstants.DEFAULT_GROUP_ID);
				continue;
			}

			final List<String> groupIds = CommonConstants.COMMA_SPLITTER.split(pkg.getGroups());

			// if package has multiple group but one of the group is DEFAULT GROUP than that will be consider in DEFAULT GROUP LIMIT
			if (groupIds.contains(CommonConstants.DEFAULT_GROUP_ID)) {
				addToGroupWiseMap(groupWisePackageMap, pkg, CommonConstants.DEFAULT_GROUP_ID);
				continue;
			}

			//Consider individual GROUP wise limit
			for (String groupId : groupIds) {
				addToGroupWiseMap(groupWisePackageMap, pkg, groupId);
			}
		}
		return groupWisePackageMap;
	}


	public static Map<String, List<PkgGroupOrderData>> fetchGroupOrderWisePkgMap(PkgType pkgType, String staffBelongingGroupIds) {

		List<String> staffGroupIds = CommonConstants.COMMA_SPLITTER.split(staffBelongingGroupIds);

		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(PkgGroupOrderData.class);
		criteria.add(Restrictions.eq("type", pkgType.name()));
		criteria.add(Restrictions.in("groupId", staffGroupIds));
		criteria.createAlias("pkgData","pkg");
		criteria.add(Restrictions.ne("pkg.status",CommonConstants.STATUS_DELETED));

		List<PkgGroupOrderData> packages = criteria.list();


		if (Collectionz.isNullOrEmpty(packages) == true) {
			return null;
		}

		Collections.sort(packages);

		Map<String, List<PkgGroupOrderData>> groupWisePackageMap = new TreeMap<String, List<PkgGroupOrderData>>(DEFAULT_GROUP_COMPARATOR);


		List<GroupData> staffGroups = GroupDAO.getGroups(staffGroupIds);

		Map<String, GroupData> staffGroupDataMap = GroupDAO.getGroupMapFromGroupList(staffGroups);

		for (PkgGroupOrderData pkg : packages) {
			addToGroupWiseMap(groupWisePackageMap,pkg,staffGroupDataMap.get(pkg.getGroupId()).getName());
		}

		return groupWisePackageMap;
	}

	public static void deletePkgGroupOrderEntries(String pkgId){
		Session session = HibernateSessionFactory.getSession();
		SQLQuery sqlQuery = session.createSQLQuery(DELETE_PKG_GROUP_QUERY);
		sqlQuery.setParameter("pkgId", pkgId);
		sqlQuery.executeUpdate();
	}


	private static void addToGroupWiseMap(Map<String, List<PkgGroupOrderData>> groupWisePackageMap, PkgGroupOrderData pkg, String group) {
		if(Collectionz.isNullOrEmpty(groupWisePackageMap.get(group)) == true){
			List<PkgGroupOrderData> pkgList = Collectionz.newArrayList();
			pkgList.add(pkg);
			groupWisePackageMap.put(group,pkgList);
			return;
		}
		groupWisePackageMap.get(group).add(pkg);
	}

	private static void addToGroupWiseMap(Map<String, List<PkgData>> groupWisePackageMap, PkgData pkg,String group) {
		if(Collectionz.isNullOrEmpty(groupWisePackageMap.get(group)) == true){
			List<PkgData> pkgList = Collectionz.newArrayList();
			pkgList.add(pkg);
			groupWisePackageMap.put(group,pkgList);
			return;
		}
		groupWisePackageMap.get(group).add(pkg);
	}

	private static List<PkgData> findListOfPackages(String pkgId, String mode, PkgType pkgType){
		List<PkgData> pkgDatas = Collectionz.newArrayList();
		try {
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(PkgData.class);

			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));

			criteria.add(Restrictions.eq("type", pkgType.name()));
			if(CRUDOperationUtil.MODE_UPDATE.equalsIgnoreCase(mode)){
				criteria.add(Restrictions.ne("id",pkgId));
			}
			pkgDatas = criteria.list();
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to fetch package list. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return pkgDatas;
	}


	public static @Nonnull List<String> getListPkgGroupOrderString(@Nonnull PkgData pkgData){
		List<String> pkgGroupOrders = Collectionz.newArrayList();
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(PkgGroupOrderData.class);
			criteria.add(Restrictions.eq("pkgData", pkgData));
			Projection groupId = Projections.property("groupId");
			criteria.setProjection(groupId);
			pkgGroupOrders = criteria.list();
		}catch (Exception e ){
			LogManager.getLogger().error(MODULE, "Failed to fetch pkg Group Order list. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);

		}
		return pkgGroupOrders;
	}

	public static void deletePkgGroupOrderEntries(@Nonnull PkgData pkgData,@Nonnull List<String> groupIds){
		try {
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(PkgGroupOrderData.class);
			criteria.add(Restrictions.eq("pkgData", pkgData));
			criteria.add(Restrictions.in("groupId", groupIds));
			List<PkgGroupOrderData> list = criteria.list();
			for (PkgGroupOrderData pkgGroupOrderData : list) {
				CRUDOperationUtil.delete(pkgGroupOrderData);
			}
		}catch (Exception e){
			LogManager.getLogger().error(MODULE,"Error while deleting pkg group orders.Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
	}
}