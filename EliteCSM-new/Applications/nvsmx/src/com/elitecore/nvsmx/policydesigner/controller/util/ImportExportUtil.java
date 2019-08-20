package com.elitecore.nvsmx.policydesigner.controller.util;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.config.util.ReflectionUtil;
import com.elitecore.corenetvertex.annotation.Import;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.imports.ImportOperation;
import com.elitecore.corenetvertex.core.imports.exception.ImportOperationFailedException;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.core.validator.Validator;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.interceptor.SingleOperationAuthorizedHandler;
import org.hibernate.Criteria;
import org.hibernate.criterion.Restrictions;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.ENTITY_IMPORT_FAIL;

public class ImportExportUtil {
	
	private static final String MODULE = "IMP-EXP-UTIL";
	private static SessionProvider sessionProvider;

	public final Splitter SPLITTER = Strings.splitter(',').trimTokens();
	public ImportExportUtil(SessionProvider session){
		this.sessionProvider = session;
		
	}
	
	public void validateAndImportInformation(Object objForImport, String action, Reason reason) throws Exception {
		ImportScope importScope = new ImportScope();
		sessionProvider.getSession().getTransaction().setTimeout(60);
		if( sessionProvider.getSession().getTransaction().isActive() == false){
			sessionProvider.getSession().beginTransaction();
		}

		Class clazz = ReflectionUtil.getClassAnnotatedWith(objForImport.getClass(), Import.class);
		Import importAnnotation = (Import) (clazz != null ? clazz.getAnnotation(Import.class) : null);

		validateInformation(objForImport, null, objForImport, reason, action, importAnnotation.validatorClass(),importScope);
		//flushing & clearing session to sync it with DB
		sessionProvider.getSession().flush();
		sessionProvider.getSession().clear();
		if (Results.SUCCESS.getValue().equalsIgnoreCase(reason.getMessages()) && Collectionz.isNullOrEmpty(reason.getSubReasons())) {
			try {
				importInformation(objForImport, null, objForImport, reason, importAnnotation.importClass(), importScope);

				if (sessionProvider.getSession().getTransaction() != null && sessionProvider.getSession().getTransaction().isActive()) {
					sessionProvider.getSession().getTransaction().commit();
				}
			} catch (Exception e) {
				sessionProvider.getSession().getTransaction().rollback();
				throw new Exception("Failed to import data: " + e.getMessage(), e);
			}

		}else{
			reason.setMessages(ENTITY_IMPORT_FAIL);
			LogManager.getLogger().warn(MODULE,"Failed to import data: " + reason.getName());
			sessionProvider.getSession().getTransaction().rollback();
		}
	}


	private void validateInformation(Object objForImport, @Nullable Object parentObject, @Nullable Object superObject, Reason reason, String action,Class validatorClassType,ImportScope importScope) throws Exception {
		Validator validator = importScope.getOrCreateValidator(objForImport.getClass(),validatorClassType);
		List<String> subReason = validator.validate(objForImport, parentObject, superObject, action, sessionProvider);

		if (Collectionz.isNullOrEmpty(subReason) == false) {
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.getSubReasons().addAll(subReason);
		}


		List<Method> methods = ReflectionUtil.getAllMethodsAnnotatedWith(objForImport.getClass(), Import.class);
		for (Method method : methods) {
			Import importAnnotation = method.getAnnotation(Import.class);
			Object childObject = method.invoke(objForImport, null);

			if (childObject == null) {
				continue;
			}

			if (childObject instanceof Collection) {
				for (Object obj : (Collection) childObject) {
					validateInformation(obj, objForImport, superObject, reason, action, importAnnotation.validatorClass(), importScope);
				}
			} else {
				validateInformation(childObject, objForImport, superObject, reason, action, importAnnotation.validatorClass(), importScope);
			}
		}

	}


	private void importInformation(@Nonnull Object objForImport, @Nullable Object parentObject, @Nullable Object superObject, Reason reason,Class importOperationClass,ImportScope importScope) throws Exception{
		ImportOperation importOperation = importScope.getOrCreateImportOperation(objForImport.getClass(), importOperationClass);
		try {

			importOperation.importData(objForImport, null, superObject,sessionProvider);
		}catch(ImportOperationFailedException ioe){
			LogManager.getLogger().error(MODULE,"Error while importing data. Reason: " + ioe.getMessage());
			LogManager.getLogger().trace(MODULE,ioe);
			reason.setMessages(ENTITY_IMPORT_FAIL);
			reason.getSubReasons().add(ioe.getMessage());
		}

	}


	public Map<String, String> getGroupsByIdAndName() throws Exception{
		Map<String, String> groupsByIdAndName = new HashMap<String, String>();
		List<GroupData> groups = getGroups();
		if (Collectionz.isNullOrEmpty(groups) == false) {
			for (GroupData group : groups) {
				groupsByIdAndName.put(group.getId(), group.getName());
			}
		}
		return groupsByIdAndName;
	}

	public void setGroupNamesBasedOnId(Map<String,String> groupNamesBasedOnId,ResourceData resourceData){
		StringBuilder groupNames = new StringBuilder();
		List<String> groupIds = SPLITTER.split(resourceData.getGroups());
		for(String groupId : groupIds){
			String groupName = groupNamesBasedOnId.get(groupId);
			if(Strings.isNullOrBlank(groupName) == false){
				groupNames.append(groupName);
				groupNames.append(CommonConstants.COMMA);
			}
		}
		if (Strings.isNullOrBlank(groupNames.toString()) == false) {
			groupNames.deleteCharAt(groupNames.lastIndexOf(","));
			resourceData.setGroupNames(groupNames.toString());
		}

	}

	public boolean getGroupIdsFromName(ResourceData importPkg, Reason reason, List<String> groups) throws Exception {
		List<String> groupNames = SPLITTER.split(importPkg.getGroupNames());
		if (Collectionz.isNullOrEmpty(groupNames) == false) {
			for (String group : groupNames) {
				List<GroupData> groupDataList = getByName(GroupData.class, group);
				if (Collectionz.isNullOrEmpty(groupDataList)) {
					reason.setMessages(ENTITY_IMPORT_FAIL);
					reason.getSubReasons().add("Group Name: " + group + " does not exist");
					LogManager.getLogger().warn(MODULE, "Group Name: " + group + " does not exist");
					return false;
				} else {
					groups.add(groupDataList.get(0).getId());
				}
			}
		}
		return true;
	}



	public void filterBasedOnStaffBelongingGroupIds(List<? extends ResourceData> resourceDatas, final String staffBelongingGroupIds){
		if(Collectionz.isNullOrEmpty(resourceDatas)){
			return;
		}

		final List<String> groupDatas = Strings.splitter(',').trimTokens().split(staffBelongingGroupIds);

		Collectionz.filter(resourceDatas, new Predicate<ResourceData>() {
			@Override
			public boolean apply(ResourceData resourceData) {
				if(Strings.isNullOrBlank(resourceData.getGroups())){
					resourceData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
				}
				List<String> groups = Strings.splitter(',').trimTokens().split(resourceData.getGroups());
				for(String groupId:groupDatas){
					if(groups.contains(groupId)){
						return true;
					}
				}
				return false;
			}
		});
	}

	public boolean filterStaffBelongingGroupsAndRoles(List<String> groupList, Map<String, RoleData> staffGroupRoleMap,Set<GroupData> groupDataSet, Reason reason, ACLModules module, String method, String userName) throws Exception{
		if("admin".equals(userName)){
			return true;
		}
		Map<String, String> notAllowedGroupNames = new HashMap<String, String>();
		for (String groupId : groupList) {
			if (staffGroupRoleMap != null && staffGroupRoleMap.size() > 0) {
				RoleData roleData = staffGroupRoleMap.get(groupId);
				if (roleData != null) {
					setGroupIdAndName(notAllowedGroupNames, groupId, groupDataSet);
					List<RoleModuleActionData> roleModuleActionDatas = roleData.getRoleModuleActionData();
					for (RoleModuleActionData roleModuleActionData : roleModuleActionDatas) {
						if (roleModuleActionData.getModuleName().equalsIgnoreCase(module.name()) && roleModuleActionData.getActions().contains(method)) {
							return true;

						}
					}

				} else {
					Map<String, String> groups = getGroupsByIdAndName();
					notAllowedGroupNames.put(groupId, groups.get(groupId));
				}

			}
		}
		String message = module.getDisplayLabel() + "(" + reason.getName() + ") "  + method + " is not allowed for " + notAllowedGroupNames.values() + " Group";
		LogManager.getLogger().warn(MODULE, message);
		reason.setMessages(ENTITY_IMPORT_FAIL);
		reason.getSubReasons().add(message);
		return false;
	}

	public boolean filterStaffBelongingGroupsAndRoles(List<String> entityGroups, Map<String, RoleData> staffGroupRoleMap, Reason reason, ACLModules module, String methodName, String userName, List<String> staffBelongingGroups, ResourceData entity, String entityName) throws Exception {
		if(NVSMXCommonConstants.ADMIN.equals(userName)){
			return true;
		}
		try {
			if (sessionProvider.getSession().getTransaction().isActive() == false) {
				sessionProvider.getSession().beginTransaction();
			}
			ResourceData existingEntity = ImportExportCRUDOperationUtil.getExistingEntity(entity.getId(), entityName, entity, sessionProvider);
			List<String> oldGroups = Collectionz.newArrayList();

			if (existingEntity != null) {
				oldGroups = CommonConstants.COMMA_SPLITTER.split(existingEntity.getGroups());
			}

			if (isAuthorized(entityGroups, staffGroupRoleMap, module, methodName, staffBelongingGroups, oldGroups) == false) {
				String message = "You are not authorized to perform action. Please refer logs for more detail";
				LogManager.getLogger().warn(MODULE, message);
				reason.setMessages(ENTITY_IMPORT_FAIL);
				reason.getSubReasons().add(message);
				return false;
			}

		} catch (Exception e) {
			sessionProvider.getSession().getTransaction().rollback();
			throw new Exception("Failed to import data: " + e.getMessage(), e);
		} finally {
			sessionProvider.getSession().close();
		}

		return true;
	}

	private boolean isAuthorized(List<String> entityGroups, Map<String, RoleData> staffGroupRoleMap, ACLModules module, String methodName, List<String> staffBelongingGroups, List<String> oldGroups) {

		List<String> notAllowedGroups = Collectionz.newArrayList();
		SingleOperationAuthorizedHandler authorizedHandler = SingleOperationAuthorizedHandler.getHandler();

		if (Collectionz.isNullOrEmpty(oldGroups) == false) {

			if (authorizedHandler.isAuthorizedForAnyGroup(oldGroups, staffGroupRoleMap, module.name(), methodName) == false) {
				LogManager.getLogger().error(MODULE,"Import operation not allowed for groups: " + GroupDAO.getGroupNames(oldGroups));
				return false;
			}
			List<GroupData> staffBelongingGroupDatas = GroupDAO.getGroups(staffBelongingGroups);
			notAllowedGroups.addAll(authorizedHandler.checkOldGroups(staffBelongingGroupDatas, entityGroups, oldGroups));
			if(Collectionz.isNullOrEmpty(notAllowedGroups) == false){
				LogManager.getLogger().error(MODULE,"You are not authorized to remove groups: " + GroupDAO.getGroupNames(notAllowedGroups));
				return false;
			}

		}

		List<String> newGroups = Lists.copy(entityGroups, new NewGroupPredicate(oldGroups));
		notAllowedGroups.addAll(authorizedHandler.isAuthorizedForAllGroup(newGroups, staffGroupRoleMap, module.name(), methodName));

		if(Collectionz.isNullOrEmpty(notAllowedGroups) == false){
			LogManager.getLogger().error(MODULE,"You are not authorized to add groups: " + GroupDAO.getGroupNames(notAllowedGroups));
			return false;
		}
		return true;
	}

	private void setGroupIdAndName(Map<String, String> notAllowedGroupNames, String groupId, Set<GroupData> groups) {
		for (GroupData group : groups) {
			if (group.getId().equalsIgnoreCase(groupId)) {
				notAllowedGroupNames.put(group.getId(), group.getName());
			}
		}
	}

	public List<GroupData> getGroups() throws Exception {
			if (sessionProvider.getSession().getTransaction().isActive() == false) {
				sessionProvider.getSession().beginTransaction();
			}
			Criteria criteria = sessionProvider.getSession().createCriteria(GroupData.class);
			return (List<GroupData>) criteria.list();

	}

	public static <T> List<T> getByName(Class<T> type , String name) throws Exception{
		try {
			if (sessionProvider.getSession().getTransaction().isActive() == false) {
				sessionProvider.getSession().beginTransaction();
			}
			Criteria criteria = sessionProvider.getSession().createCriteria(type);
			criteria.add(Restrictions.eq("name", name));
			if (type != GroupData.class) {
				criteria.add(Restrictions.ne("STATUS", CommonConstants.STATUS_DELETED));
			}
			return ((List<T>) criteria.list());
		}finally{
			sessionProvider.getSession().close();
		}
	}

	public static <E> String removeInvalidEntitiesAndGetMessage(Collection<E> collection, Predicate<? super E> predicate, String entityName){
		int totalPCCRules = collection.size();
		Collectionz.filter(collection, predicate);
		int availablePccRules = collection.size();
		StringBuilder message = new StringBuilder();
		if(totalPCCRules>availablePccRules){
			message.append("Found ")
					.append((totalPCCRules-availablePccRules))
					.append(" invalid ")
					.append(entityName)
					.append(". Skipping import process for invalid ")
					.append(entityName)
					.append(". Kindly refer logs for more Details.");
		}
		return message.toString();
	}



}
