package com.elitecore.nvsmx.system.hibernate;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.db.DBConnectionFactory;
import com.elitecore.core.commons.util.db.datasource.IConnectionDataSource;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.audit.AuditData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.exception.HibernateDataException;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.commons.collections.CollectionUtils;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Example;
import org.hibernate.criterion.MatchMode;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Projection;
import org.hibernate.criterion.ProjectionList;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.io.Serializable;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.ResultSet;
import java.sql.Timestamp;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 *  This Class provide the functionality for basic CRUD
 *  C-create,R-read,U-update,D-delete  operations
 *  @author aditya
 *
 */
public class CRUDOperationUtil {

	private static final String MODULE = "CRUD-OPRS";
    private static final String NAME_PROPERTY="name";
    public static final String ID_PROPERTY = "id";
    public static final String  MODE_UPDATE = "update";
    public static final String  MODE_CREATE = "create";
    public static final String STATUS_PROPERTY = "status";
    private static final String DESC_ORDER = "desc";
    public static final JsonObject EMPTY_JSON_OBJECT = new JsonObject();

    private CRUDOperationUtil(){}
    
	/**This method is used to save transient object  
	 * 
	 * @param transientObject
	 * @throws HibernateDataException
	 */
	public static void save(Object transientObject) {

			HibernateSessionFactory.getSession().save(transientObject);
			LogManager.getLogger().debug(MODULE, "saved successfully");

	}

	/**
	 * This method is used to save and audit resource data when base resource(audit resource) and actual resource are different e.g. pccRuleData
	 *
	 * @param baseResourceData Audit resource, in which the actual resource resides
	 * @param actualResourceData Actual resource on which the audit operation will be performed
	 * @param staffData Related staff who made the changes
	 * @param remoteAddress Client address, from which request is arrived
	 */

	/**
	 * This method is used to update transient object
	 * @param transientObject
	 * @throws HibernateDataException
	 */
	public static void update(Object transientObject) {

			HibernateSessionFactory.getSession().update(transientObject);
			LogManager.getLogger().debug(MODULE, "updated successfully");

	}

	/**
	 * Either save(Object) or update(Object) the given instance
	 * @param transientObject
	 * @throws HibernateDataException
	 * @author Dhyani.Raval
	 */
	public static void saveOrUpdate(Object transientObject) {

			Session session = HibernateSessionFactory.getSession();
			session.clear();
			session.saveOrUpdate(transientObject);
			LogManager.getLogger().debug(MODULE, "save or updated successfully");
 	}


	/**
	 * This method is used to delete transient object
	 * @param transientObject
	 * @throws HibernateDataException
	 */
	public static void delete(Object transientObject) {
			HibernateSessionFactory.getSession().delete(transientObject);
			LogManager.getLogger().debug(MODULE,"deleted successfully");
	}

	/**
	 * This method is used to get entity by id
	 * @param type
	 * @param id
	 * @return entity of type
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> type,String id) {
			return (T)HibernateSessionFactory.getSession().get(type, id);
	}
	public static <T> T get(Class<T> type,Serializable id) {
		return (T)HibernateSessionFactory.getSession().get(type, id);
	}

	/**
	 * This method is used to get entity by id and addition condition on which information will be fetch
	 * @param type
	 * @param id
	 * @param additionalCondition
	 * @return entity of type
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static <T> T get(Class<T> type,String id,SimpleExpression additionalCondition) {
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
		criteria.add(Restrictions.eq(ID_PROPERTY, id));
		if(additionalCondition != null){
			criteria.add(additionalCondition);
		}
		List<T> lst = criteria.list();
		if(Collectionz.isNullOrEmpty(lst) == false){
			return lst.get(0);
		}
		return null;
	}

	@SuppressWarnings("unchecked")
	public static <T> T getNotDeleted(Class<T> type,String id)   {
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
		criteria.add(Restrictions.eq(ID_PROPERTY, id));
		criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
		List<T> lst = criteria.list();
		if(Collectionz.isNullOrEmpty(lst) == false){
			return lst.get(0);
		}
		return null;
	}

	/**
	 * This method is used to get entity present in DB
	 * @param type
	 * @param order
	 * @return entity of type
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> get(Class<T> type,Order order)  {
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
		criteria.add(Restrictions.ne("status", CommonConstants.STATUS_DELETED));
		criteria.addOrder(order);
		return criteria.list();
	}

	/**
	 * Get all the entity by its ids
	 * @param type
	 * @param ids
	 * @return List of entity
	 * @throws HibernateDataException
	 * @author Dhyani.Raval
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getAllByIds(Class<T> type , String [] ids) throws Exception{

			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			criteria.add(Restrictions.in(ID_PROPERTY, ids));
			return ((List<T>)criteria.list());
	}

	/**
	 * Get all the entity by its collection of ids
	 * @param type
	 * @param ids
	 * @return List of entity
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> getAllByIds(Class<T> type , Collection ids) throws Exception{

		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
		criteria.add(Restrictions.in(ID_PROPERTY, ids));
		return ((List<T>)criteria.list());
	}

	/**
	 * This method is used to fetch List of Entity
	 * @param type
	 * @return List of entity
	 * @throws HibernateDataException
	 */

	@SuppressWarnings("unchecked")
	public static <T> List<T> findAll(Class<T> type,int firstResult,int maxResults, String sortColumnName, String sortColumnOrder) throws Exception{
		try{

			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			if(sortColumnName != null && sortColumnName.trim().length()>0){
			    if(sortColumnOrder != null && DESC_ORDER.equalsIgnoreCase(sortColumnOrder)){
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
			return ((List<T>)criteria.list());

 		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to get list of type "+ type.getName() +".Reason: "+e.getMessage());
			throw  e;
		}

	}

	/**
	 * This method is used to fetch List of Entity which is not deleted
	 * @param type
	 * @return List of entity
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> findAllByStatus(Class<T> type,Integer firstResult,Integer maxResults, String sortColumnName, String sortColumnOrder) {
		try{

			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			if(sortColumnName != null && sortColumnName.trim().length()>0){
			    if(sortColumnOrder != null && DESC_ORDER.equalsIgnoreCase(sortColumnOrder)){
				criteria.addOrder(Order.desc(sortColumnName));
			    } else {
				criteria.addOrder(Order.asc(sortColumnName));
			    }
			}else {
			    criteria.addOrder(Order.asc("name"));
			}
			if(firstResult!=null){
			 criteria.setFirstResult(firstResult);
			}
			if(maxResults!=null){
			 criteria.setMaxResults(maxResults);
			}
			return ((List<T>)criteria.list());

 		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to get list of type "+ type.getName() +".Reason: "+e.getMessage());
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	/**
	 * This method is used to fetch List of Entity which is not deleted
	 * @param type
	 * @param firstResult
	 * @param maxResults
	 * @param sortColumnName
	 * @param sortColumnOrder
	 * @param staffBelongingGroupIds
	 * @return List of Entity
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> findAllWhichIsNotDeleted(Class<T> type,Integer firstResult,Integer maxResults, String sortColumnName, String sortColumnOrder, String staffBelongingGroupIds) {


			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			if(sortColumnName != null && sortColumnName.trim().length()>0){
			    if(sortColumnOrder != null && DESC_ORDER.equalsIgnoreCase(sortColumnOrder)){
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

			if(Strings.isNullOrBlank(staffBelongingGroupIds)){
				return ((List<T>) criteria.list());
			}
			List<ResourceData> resourceDatas = criteria.list();
			filterpackages(resourceDatas, staffBelongingGroupIds);
		    if(Collectionz.isNullOrEmpty(resourceDatas)){
		    	return Collections.emptyList();

		    }
			return ((List<T>)resourceDatas);

	}



	@SuppressWarnings("unchecked")
	public static <T> List<T> findAll(Class<T> type, int maxResults) throws Exception{
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			if(maxResults >= 0) {
				criteria.setMaxResults(maxResults);
			}
			return ((List<T>)criteria.list());

		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to get list of type "+ type.getName() +". Reason: "+e.getMessage());
			throw e;
		}

	}

	public static <T> List<T> findAll(Class<T> type, String orderByColumnName){
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			criteria.addOrder(Order.asc(orderByColumnName));
			return ((List<T>)criteria.list());
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE,"Failed to get list of type"+type.getName()+". Reason: "+ex.getMessage());
			throw ex;
		}
	}


	@SuppressWarnings("unchecked")
	public static <T> List<T> findAll(Class<T> type) {
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			return ((List<T>)criteria.list());

		}catch (HibernateException he) {
			LogManager.getLogger().error(MODULE, "Failed to get list of type "+ type.getName() +". Reason: "+he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to get list of type "+ type.getName() +". Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}

	}

	@SuppressWarnings("unchecked")
	public static <T> List<T> findAllWhichIsNotDeleted(Class<T> type) {
		try{

			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			return ((List<T>)criteria.list());

		}catch (HibernateException he) {
			LogManager.getLogger().error(MODULE, "Failed to get list of type "+ type.getName() +". Reason: "+he.getMessage());
			LogManager.getLogger().trace(MODULE, he);
			throw new HibernateDataException(he.getMessage(), he);
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to get list of type "+ type.getName() +". Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}

	}


	/**
	 * Method is used to fetch the list of enity by example. i.e. pass the entity by setting some of the values of it.
	 * Using example hibernate create the query like <br/> "SELECT * FROM ENTITY WHERE ENTITY.PROPERTY = ? ....."
	 * @param exampleInstance
	 * @param firstResult
	 * @param maxResults
	 * @param sortColumnName
	 * @param sortColumnOrder
	 * @param staffBelongingGroupIds
	 * @return List of Entity
	 * @throws HibernateDataException
	 */
	@SuppressWarnings("unchecked")
	public static   <T> List<T> searchByCriteria(T exampleInstance,int firstResult,int maxResults, String sortColumnName, String sortColumnOrder,String staffBelongingGroupIds) throws Exception{
		Session session = null;
	    try{
	    	session = HibernateSessionFactory.getSession();
	    	Criteria criteria = session.createCriteria(exampleInstance.getClass());
	    	criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
	    	Example example=Example.create(exampleInstance).enableLike(MatchMode.ANYWHERE).ignoreCase();
	    	criteria.add(example);
	    	if(sortColumnName != null && sortColumnName.trim().length()>0){
			    if(sortColumnOrder != null && DESC_ORDER.equalsIgnoreCase(sortColumnOrder)){
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


			if(Strings.isNullOrBlank(staffBelongingGroupIds)){
				return criteria.list();
			}
			List<ResourceData> resourceDatas = criteria.list();
			CRUDOperationUtil.filterpackages(resourceDatas, staffBelongingGroupIds);
		    if(Collectionz.isNullOrEmpty(resourceDatas)==true){
		    	return Collections.emptyList();

		    }
		    return ((List<T>)resourceDatas);

 		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to search entity "+ exampleInstance.getClass().getName()+".Reason: "+e.getMessage());
			throw e;
		}
	}

	
	
	@SuppressWarnings("unchecked")
	public static   <T> List<T> searchByCriteria(Class<T> type,SimpleExpression additionalCondition) throws Exception{
		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
		if(additionalCondition != null){
			criteria.add(additionalCondition);
		}
		return criteria.list();
	}

	
	
	

	@SuppressWarnings("unchecked")
	public static <T> boolean isDuplicateName(String mode, Class<T> type,String id, String name,String property) throws Exception{
		try{
			Criteria criteria  = HibernateSessionFactory.getSession().createCriteria(type);
			ProjectionList proList = Projections.projectionList();
			if(Strings.isNullOrBlank(property)==true){
				criteria.add(Restrictions.ilike(NAME_PROPERTY,name));
			    proList.add(Projections.property(NAME_PROPERTY));
			}else{

				criteria.add(Restrictions.eq(property,name));

				proList.add(Projections.property(property));
			}
			criteria.setProjection(proList);
			if(mode.equalsIgnoreCase(MODE_UPDATE)){
				criteria.add(Restrictions.ne(ID_PROPERTY, id));
			}
			criteria.add(Restrictions.ne(STATUS_PROPERTY, CommonConstants.STATUS_DELETED));
			List<T> list = criteria.list();
			if(Collectionz.isNullOrEmpty(list) ){
				return false;
			}

		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to search entity "+ type.getName() +" for name "+name+".Reason: "+e.getMessage());
			throw e;
		}
		return true;
	}

	@SuppressWarnings("unchecked")
	public static <T> boolean isDuplicateProperty(String mode, Class<T> type, String id, Object name, String property) throws Exception {
        try {
            Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
            ProjectionList proList = Projections.projectionList();

            if (Strings.isNullOrBlank(property)) {
				criteria.add(Restrictions.ilike(NAME_PROPERTY,name));
                proList.add(Projections.property(NAME_PROPERTY));
            } else {
				criteria.add(Restrictions.eq(property, name));
                proList.add(Projections.property(property));
            }

            criteria.setProjection(proList);
			if(mode.equalsIgnoreCase(MODE_UPDATE))
			{
				criteria.add(Restrictions.ne(ID_PROPERTY, id));
			}
            List list = criteria.list();
            if (Collectionz.isNullOrEmpty(list)) {
                return false;
            }

        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to search entity " + type.getName() + " for name " + name + ".Reason: " + e.getMessage());
            throw e;
        }
        return true;
    }

    @SuppressWarnings("unchecked")
    public static <T> boolean isExist(Class<T> type, String property, String propertyVal) {

		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
		criteria.add(Restrictions.eq(property, propertyVal));
		criteria.setProjection(Projections.count(property));

		return (Long) criteria.uniqueResult() > 0;
	}

	/***
	 * This method check the duplicate name with parent resource
	 * e.g quota profile name within pkg,area with in city...
	 * @param type
	 * @param mode
	 * @param id
	 * @param name
	 * @param parentId
	 * @param parentPropertyName
	 * @return
	 */

	public static boolean isDuplicateNameWithInParent(Class type, String mode, String id, String name, String parentId,String parentPropertyName) {
		try{
			DetachedCriteria criteria = DetachedCriteria.forClass(type);
			DetachedCriteria parentCriteria = criteria.createCriteria(parentPropertyName);
			parentCriteria.add(Restrictions.eq("id",parentId));
			criteria.add(Restrictions.ilike("name",name));
			criteria.setProjection(Projections.property("name"));
			if(mode.equalsIgnoreCase(CRUDOperationUtil.MODE_UPDATE)){
				criteria.add(Restrictions.ne("id", id));
			}
			if(Collectionz.isNullOrEmpty(findAllByDetachedCriteria(criteria))){
				return false;
			}
			return true;
		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to validate the name "+name+".Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new HibernateDataException(e.getMessage(), e);
		}
	}

	/**This method is used to merge transient object
	 *
	 * @param transientObject
	 * @throws HibernateDataException
	 */
	public static  <T> T merge(T transientObject)  {
		if (getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Merging entity");
		}
		return (T) HibernateSessionFactory.getSession().merge(transientObject);
	}

	/**
	 * This method is used for audit transaction
	 * @param actualResourceData
	 * @param auditableResourceName
	 * @param auditAction
	 * @param staffData
	 * @param remoteAddr
	 * @param hierarchy
	 * @param message
	 * @throws HibernateDataException
	 */

	public static void audit(ResourceData actualResourceData, String auditableResourceName, AuditActions auditAction,
                             StaffData staffData, String remoteAddr , String hierarchy , String message) throws Exception{
		JsonArray difference = findDifference(actualResourceData, auditAction);
		audit(actualResourceData, auditableResourceName, auditAction, staffData, remoteAddr, difference, hierarchy, message);

	}

	private static JsonArray findDifference(ResourceData actualResourceData, AuditActions auditAction) throws Exception {
		JsonArray difference = null;
		try {
		if (auditAction == AuditActions.UPDATE) {
			ResourceData dataFromDB = getDataFromDB(actualResourceData.getClass(), actualResourceData.getId());
			if(Strings.isNullOrBlank(dataFromDB.getGroups()) == false){
				List<String> groupIds = CommonConstants.COMMA_SPLITTER.split(dataFromDB.getGroups());
				dataFromDB.setGroupNames(GroupDAO.getGroupNames(groupIds));
			}
			difference  = ObjectDiffer.diff(dataFromDB, actualResourceData);

		}else if(auditAction == AuditActions.CREATE){
				// for create action old object will be empty
				difference = ObjectDiffer.diff(EMPTY_JSON_OBJECT, actualResourceData.toJson());

		}else if(auditAction == AuditActions.DELETE){
					// for delete action new object will be empty
				difference = ObjectDiffer.diff(actualResourceData.toJson(),EMPTY_JSON_OBJECT);
		}
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Audit failed. Reason: "+e.getMessage());
			throw e;
		}
		return difference;
	}


	/**
	 * This method is used for audit transaction
	 * @param actualResourceData
	 * @param auditableResourceName
	 * @param auditAction
	 * @param staffData
	 * @param remoteAddress
	 * @param difference
	 * @param hierarchy
	 * @param message
	 * @throws HibernateDataException
	 */
	public static void audit(ResourceData actualResourceData, String auditableResourceName, AuditActions auditAction, StaffData staffData, String remoteAddress, JsonArray difference, String hierarchy, String message)  {

		AuditData auditData = new AuditData();
		
		auditData.setResourceId(actualResourceData.getAuditableResource().getId());
		auditData.setResourceClass(actualResourceData.getAuditableResource().getClass().getName());
		auditData.setAuditableResourceName(auditableResourceName);

		auditData.setActualResourceId(actualResourceData.getId());
		auditData.setActualResourceClass(actualResourceData.getClass().getName());
		
		auditData.setActionId(auditAction);
		auditData.setAuditDate((new Timestamp(System.currentTimeMillis())));
		auditData.setClientIp(remoteAddress);
		auditData.setStaffData(staffData);
		auditData.setHierarchy(hierarchy);
		auditData.setMessage(message);
		if(difference.size() != 0){
			auditData.setDifference(difference.toString().getBytes());
		}
		save(auditData);
	}


	public static <T> ResourceData getDataFromDB(Class<T> type, String id) throws Exception {
		
		Session newSession =null;
		ResourceData resourceFromDB = null;
		try{
			newSession = HibernateSessionFactory.getNewSession();
			resourceFromDB = (ResourceData) newSession.get(type,id);
		}catch(Exception e){
			LogManager.getLogger().error(MODULE, "Failed to get data from Database. Reason: "+e.getMessage());
			throw e;
		}finally{
			HibernateSessionUtil.closeSession(newSession);
		}
		return resourceFromDB;
	}
	
	
	public static String getProperty(Class className,String propertyName) {
	    LogManager.getLogger().debug(MODULE, "Method called getProperty()");
	    
	    Criteria criteria =  HibernateSessionFactory.getSession().createCriteria(className);
	    
	    Projection groupName = Projections.property(propertyName);
	    criteria.setProjection(groupName);
	    
	    List dataList = criteria.list();
	    StringBuilder values = new StringBuilder();	    
	    
	    for(Object property : dataList){
		values.append(property).append(",");
	    }
	    
	    if(values.toString().contains(",")){
		values = values.deleteCharAt(values.lastIndexOf(","));
	    }
 	    return values.toString(); 
	}
	/***
	 * Method to filter packages based on groups
	 * @param resourceDatas
	 * @param staffBelongingGroupIds
	 */
	
	public static void filterpackages(List<? extends ResourceData> resourceDatas, String staffBelongingGroupIds){
		if(Collectionz.isNullOrEmpty(resourceDatas)){
			return;
		}
		final List<String> groupDatas = Strings.splitter(',').trimTokens().split(staffBelongingGroupIds);

		Collectionz.filter(resourceDatas, (Predicate<ResourceData>) resourceData -> {
            List<String> groups = Strings.splitter(',').trimTokens().split(resourceData.getGroups());
            if(Strings.isNullOrBlank(resourceData.getGroups())){
                return true;
            }
            for(String groupId:groupDatas){
                if(groups.contains(groupId)){
                    return true;
                }
            }
            return false;
        });
	}
	




	/**
	 * This method will use to search entity by its name and status
	 * @param beanType
	 * @param criteriaJson
	 * @param startIndex
	 * @param maxRecords
	 * @param sortColumnName
	 * @param sortColumnOrder
	 * @param staffBelongingGroups
	 * @return
	 * @throws Exception
	 * @author Dhyani.Raval
	 */
	@SuppressWarnings("unchecked")
	public static <T> List<T> searchByNameAndStatus(Class<T> beanType, String criteriaJson, int startIndex,int maxRecords, String sortColumnName, String sortColumnOrder,String staffBelongingGroups) throws Exception{
		try {
			
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(beanType);
			Gson gson = GsonFactory.defaultInstance();
			JsonObject jsonObject = gson.fromJson(criteriaJson, JsonObject.class);
			
			String name = jsonObject.get("Name").getAsString();
			String status = jsonObject.get("Status").getAsString();
			
			if(Strings.isNullOrBlank(status) == false){
	    		criteria.add(Restrictions.eq(STATUS_PROPERTY,status));
	    	}else{
	    		criteria.add(Restrictions.ne(STATUS_PROPERTY,CommonConstants.STATUS_DELETED));
	    	}
			
	    	if(Strings.isNullOrBlank(name) == false){
	    		criteria.add(Restrictions.ilike(NAME_PROPERTY,name,MatchMode.ANYWHERE));
	    	}
	    	if(sortColumnName != null && sortColumnName.trim().length()>0){
			    if(sortColumnOrder != null && DESC_ORDER.equalsIgnoreCase(sortColumnOrder)){
			    	criteria.addOrder(Order.desc(sortColumnName));
			    } else {
			    	criteria.addOrder(Order.asc(sortColumnName));
			    }
			}else {
			    criteria.addOrder(Order.asc(NAME_PROPERTY));			    
			}
	    	criteria.setFirstResult(startIndex);
			criteria.setMaxResults(maxRecords);
			
			
			if(Strings.isNullOrBlank(staffBelongingGroups)){
				return criteria.list();
			}
			List<ResourceData> resourceDatas = criteria.list();
			CRUDOperationUtil.filterpackages(resourceDatas, staffBelongingGroups);
		    if(Collectionz.isNullOrEmpty(resourceDatas)){
		    	return Collections.emptyList();
		    }
		    return ((List<T>)resourceDatas);
			
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to search entity "+ beanType.getName()+" from generic search. Reason: "+e.getMessage());
			throw e;
		}
	}

	public static <T> Integer getMaxValueForProperty(Class<T> type, String property){
		try {
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			return (Integer) criteria.setProjection(Projections.max(property)).uniqueResult();
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE,"Failed to count total records for '"+type.getSimpleName()+"'. Reason: "+ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			return 0;
		}
	}

	public static <T> List<T> findAll(Class<T> type, Criterion... criterions){
		try{
			Criteria criteria = HibernateSessionFactory.getSession().createCriteria(type);
			for(Criterion criterion : criterions) {
				criteria.add(criterion);
			}
			return criteria.list();
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Failed to get list of type "+ type.getName() +". Reason: "+ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			throw ex;
		}
	}

	public static <T> List<T> findAllByDetachedCriteria(DetachedCriteria criteria){
		try{
            return (List<T>)criteria.getExecutableCriteria(HibernateSessionFactory.getSession()).list();
		}catch(Exception ex){
			LogManager.getLogger().error(MODULE, "Failed to fetch Data. Reason: "+ex.getMessage());
			LogManager.getLogger().trace(MODULE, ex);
			throw ex;
		}
	}

	public static void flushSession(){
		HibernateSessionFactory.getSession().flush();
		HibernateSessionFactory.getSession().clear();
	}

	public static Set<String> fetchTableNameOrColumnNameByDatabase(DatabaseData databaseData, String tableName) {
        Connection connection = null;
        ResultSet resultSet = null;
        Set<String> stringArrayList = Collectionz.newHashSet();
        try {

            IConnectionDataSource dataBaseConnection = new DBConnectionFactory().createDataBaseConnection(databaseData.getConnectionUrl(), databaseData.getUserName(), PasswordUtility.getDecryptedPassword(databaseData.getPassword()), 1, 1, new HashMap<>());
            dataBaseConnection.init();
            connection = dataBaseConnection.getConnection();
            DatabaseMetaData metaData = connection.getMetaData();

            if(Strings.isNullOrBlank(tableName)) {

                //Fetch list of table Names
                resultSet = metaData.getTables(null, null, null, new String[]{"TABLE"});
                while (resultSet.next()){
                    stringArrayList.add(resultSet.getString(3));
}
            } else {
		
                //fetch list of columns by table name
                resultSet = metaData.getColumns(null,null,tableName,null);
                while (resultSet.next()){
                    stringArrayList.add(resultSet.getString(4));
                }
            }

        } catch (Exception e) {
            getLogger().error(MODULE,"Error while trying connect with database. Reason: " +e.getMessage());
            getLogger().trace(MODULE,e);
        } finally {
            DBUtility.closeQuietly(resultSet);
            DBUtility.closeQuietly(connection);
        }
        return stringArrayList;
    }

	/**
	 * it will check weather staff is exist or not while verify the session factory
	 * @return
	 */
	public static boolean isStaffTableExist() {

		try {
			StaffData staffData = (StaffData) getDataFromDB(StaffData.class, CommonConstants.ADMIN_STAFF_ID);
			if(staffData != null) {
				return true;
			}
		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to get data from Database. Reason: "+e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return false;

	}

	public static <T> T findByName(Class<T> type,String name) {
		Criteria nameCriteria = HibernateSessionFactory.getSession().createCriteria(type);
		nameCriteria.add(Restrictions.eq(NAME_PROPERTY, name));
		List<T> list = nameCriteria.list();
		if (CollectionUtils.isNotEmpty(list)) {
			return list.get(0);
		}
		return null;
	}
}