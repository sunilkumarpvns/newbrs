package com.elitecore.netvertexsm.web.core.base;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.GroupInfo;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffGroupRoleRelData;
import com.elitecore.netvertexsm.util.EliteAssert;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.servermgr.spr.GroupInfoSelectionUtil;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;
import org.apache.struts.actions.DispatchAction;

import javax.annotation.Nonnull;
import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

public abstract class BaseWebDispatchAction extends DispatchAction {

	protected static final String SUCCESS = "success";
    protected static final String FAILURE = "failure";
    protected static final String STATUS_REPORT = "statusreport";
    
    protected static final String INVALID_ACCESS_FORWARD = "failure";
    protected final String MODULE = this.getClass().getSimpleName();
    
    
    
    protected SimpleDateFormat formatter = new SimpleDateFormat(ConfigManager.get(ConfigConstant.DATE_FORMAT));
    
    
    protected String getDefaultDescription(HttpServletRequest request){
 	   String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserName();
 	   SimpleDateFormat formatter = new SimpleDateFormat(ConfigManager.get(ConfigConstant.DATE_FORMAT));
 	   return ("Created by " + userName + " on " + formatter.format(new Date()));		    
    }
    protected long getCurrentPageNumberAfterDel(int numOfdelRecord,long pageNumber,long totalPages,long totalRecords)
    {
 	   
 	   
 	   long currentPageNumber = pageNumber;
 	   Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
 	   Logger.logDebug(MODULE, "TotalRecords--> "+totalRecords);
 	   Logger.logDebug(MODULE, "PageSize	--> "+pageSize);
 	   Logger.logDebug(MODULE, "totalPages	--> "+totalPages);
 	   Logger.logDebug(MODULE, "currentPageNumber	 --> "+currentPageNumber);
 	   Logger.logDebug(MODULE, "strSelectedIdsLen	 --> "+numOfdelRecord);
 	   Logger.logDebug(MODULE, "totalRecords%pageSize--> "+totalRecords%pageSize);
 	    if((totalPages+1) == currentPageNumber && (numOfdelRecord == (totalRecords%pageSize) || numOfdelRecord == pageSize) )
 	    {
 	        currentPageNumber--; 	
 	    	
 	    }
 	    return currentPageNumber;
    }
    
    protected final IStaffData getStaffObject(SystemLoginForm systemLoginForm) throws DataManagerException{
		
		StaffBLManager staffBLManager = new StaffBLManager();
		String userId = systemLoginForm.getUserId();
		
	    return staffBLManager.getStaffData(userId);

	}
    
    /*protected final IGatewayData getGatewayObject(SystemLoginForm systemLoginForm) throws DataManagerException {
    	GatewayBLManager gatewayBLManager = new GatewayBLManager();
    	String gatewayId = systemLoginForm.
    }*/
    
    protected boolean checkAccess( HttpServletRequest request , String subModuleActionAlias ) throws ActionNotPermitedException {
        String userName = null;
        try{
            EliteAssert.notNull(request,"request must be specified.");
            EliteAssert.notNull(subModuleActionAlias,"subModuleActionAlias must be specified.");
            EliteAssert.valiedWebSession(request);
            SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
            EliteAssert.notNull(radiusLoginForm,"radiusLoginForm must be specified in session.");
            userName = radiusLoginForm.getUserName();

            if (userName != null && (userName.equalsIgnoreCase(BaseConstant.ADMIN_USER_NAME) || userName.equalsIgnoreCase(BaseConstant.PROFILE_USER_NAME))) {
                return true;
            }

            Set<String> actionAliasSet = (Set<String>) request.getSession(false).getAttribute("__action_Alias_Set_");
            EliteAssert.notNull(actionAliasSet,"__action_Alias_Set_ must be specified in session.");

            if(actionAliasSet.contains(subModuleActionAlias))
                return true;

        }catch(Exception e){
            Logger.logTrace(MODULE,e);
            throw new ActionNotPermitedException("[ "+ subModuleActionAlias +" ] is not permitted to [ "+ userName +" ]");
        }
        return false;

    }
   
    protected boolean checkActionPermission( HttpServletRequest request , String subModuleActionAlias ) throws ActionNotPermitedException{
        String userName = null;
        try{
            EliteAssert.notNull(request,"request must be specified.");
            EliteAssert.notNull(subModuleActionAlias,"subModuleActionAlias must be specified.");
            EliteAssert.valiedWebSession(request);
            SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
            EliteAssert.notNull(radiusLoginForm,"radiusLoginForm must be specified in session.");
            userName = radiusLoginForm.getUserName();

            if (userName != null && userName.equalsIgnoreCase(BaseConstant.ADMIN_USER_NAME)) {
                return true;
            }

            Set<String> actionAliasSet = (Set<String>) request.getSession(false).getAttribute("__action_Alias_Set_");
            EliteAssert.notNull(actionAliasSet,"__action_Alias_Set_ must be specified in session.");

            if(actionAliasSet.contains(subModuleActionAlias))
                return true;
            
        }catch(Exception e){
            Logger.logTrace(MODULE,e);
        }
        throw new ActionNotPermitedException("[ "+ subModuleActionAlias +" ] is not permitted to [ "+ userName +" ]");
    }
    
    
    protected void printPermitedActionAlias(HttpServletRequest request){
        try{
            EliteAssert.notNull(request,"request must be specified.");
            Set<String> actionAliasSet = (HashSet<String>) request.getSession(false).getAttribute("__action_Alias_Set_");
            Logger.logDebug(MODULE,"Permitted Actions are :- " + actionAliasSet);
        }
        catch(Exception e){
        }
    }
     
     protected Long[] convertStringIdsToLong(String[] strIds){
    	 Long[] ids = null;
    	 if(strIds!=null && strIds.length>0){
    		 ids =  new Long[strIds.length];
    		 for (int i = 0; i < strIds.length; i++) {
    			 ids[i] = Long.parseLong(strIds[i]);
			}
    	 }
    	 return ids;
     }
     
	 protected Timestamp getCurrentTimeStemp(){
	        return new Timestamp(new Date().getTime());
	 }
	 
	 protected void restrictedAction(HttpServletRequest request){
		 Logger.logError(MODULE, "Error during Data Manager operation ");
		 ActionMessage message = new ActionMessage("general.user.restricted");
		 ActionMessages messages = new ActionMessages();
		 messages.add("information", message);
		 saveErrors(request, messages);

		 ActionMessages errorHeadingMessage = new ActionMessages();
		 message = new ActionMessage("group.error.heading","creating");
		 errorHeadingMessage.add("errorHeading",message);
		 saveMessages(request,errorHeadingMessage);
	 } 

    public boolean isAuthorizedForAnyGroup(List<String> groupIds, Map<String,RoleData> staffBelongingGroupIdRoleMap, String moduleName, String methodName) {
        for (String groupId : groupIds) {
            RoleData roleData = staffBelongingGroupIdRoleMap.get(groupId);
            if (roleData == null) {
                continue;
            }
            Set<RoleModuleActionData> roleModuleActionDatas = roleData.getRoleModuleActionData();
            for (RoleModuleActionData roleModuleActionData : roleModuleActionDatas) {
                if (roleModuleActionData.getModuleName().equalsIgnoreCase(moduleName) && roleModuleActionData.getActions().contains(methodName)) {
                    return true;
                }
            }
        }
        return false;
    }


    protected boolean isAdminUser(String userName) {
        if (userName != null && (userName.equalsIgnoreCase(BaseConstant.ADMIN_USER_NAME) || userName.equalsIgnoreCase(BaseConstant.PROFILE_USER_NAME))) {
            return true;
        }
        return false;
    }

    protected List<String> findNotAllowedGroups(HttpServletRequest request,ACLModules module, String methodName, List<String> staffBelongingGroups, Map<String, RoleData> staffBelongingRoleMap, List<String> entityGroups, List<String> oldGroups) {

        SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
        String userName = radiusLoginForm.getUserName();
        if (isAdminUser(userName)) {
            return null;
        }

           List<String> notAllowedGroups = Collectionz.newArrayList();
           if (Collectionz.isNullOrEmpty(oldGroups) == false) {

                if (isAuthorizedForAnyGroup(oldGroups, staffBelongingRoleMap, module.name(), methodName) == false) {
                    return oldGroups;
                }

                Set<GroupData> staffBelongingGroupDatas =  getStaffBelongingGroups(request);
                notAllowedGroups.addAll(checkOldGroups(staffBelongingGroupDatas, entityGroups, oldGroups));

            }

            List<String> newGroups = Lists.copy(entityGroups,new NewGroupPredicate(oldGroups));

            notAllowedGroups.addAll(isAuthorizedForAllGroup(newGroups, staffBelongingRoleMap, module.name(), methodName));

           return notAllowedGroups;
    }

    public List<String> checkOldGroups(Set<GroupData> staffBelongingGroupDatas, List<String> entityGroups, List<String> oldGroups){

        List<String> notAllowedGroups = Collectionz.newArrayList();
        List<GroupInfo> groupInfoList = GroupInfoSelectionUtil.getCombinedGroupInfoList(staffBelongingGroupDatas, oldGroups);
        for (GroupInfo groupInfo : groupInfoList) {
            if (groupInfo.isLocked() == true && entityGroups.contains(groupInfo.getId()) == false) {
                notAllowedGroups.add(groupInfo.getId());
            }
        }
        return notAllowedGroups;
    }

    public List<String> isAuthorizedForAllGroup(List<String> groupIds, Map<String, RoleData> staffBelongingGroupIdRoleMap, String moduleName, String methodName) {
        List<String> notAllowedGroups = Collectionz.newArrayList();
        for (String groupId : groupIds) {

            RoleData roleData = staffBelongingGroupIdRoleMap.get(groupId);
            if (roleData != null) {
                Set<RoleModuleActionData> roleModuleActionDatas = roleData.getRoleModuleActionData();
                boolean isAllowed = false;
                for (RoleModuleActionData roleModuleActionData : roleModuleActionDatas) {
                    if (roleModuleActionData.getModuleName().equalsIgnoreCase(moduleName) && roleModuleActionData.getActions().contains(methodName)) {
                        isAllowed = true;
                        break;
                    }
                }
                if(isAllowed == false){
                    notAllowedGroups.add(groupId);
                }
            } else {
                notAllowedGroups.add(groupId);
            }
        }
        if(Collectionz.isNullOrEmpty(notAllowedGroups) == false){
            LogManager.getLogger().debug(MODULE, moduleName + " '" + methodName + "' allowed for '" + notAllowedGroups + "' Group");
        }
        return notAllowedGroups;
    }

    protected Set<GroupData> getStaffBelongingGroups(HttpServletRequest request) {
        return (Set<GroupData>) request.getSession().getAttribute(ServermgrConstant.STAFF_BELONGING_GROUPS);
    }

    protected String getStaffBelongingGroupIds(HttpServletRequest request) {
        return (String) request.getSession().getAttribute(ServermgrConstant.STAFF_BELONGING_GROUP_IDS);
    }

    protected List<String> getStaffBelongingGroupIdList(HttpServletRequest request) {
        return  CommonConstants.COMMA_SPLITTER.split(getStaffBelongingGroupIds(request));
    }

    protected Map<String, RoleData> getGroupIdVsStaffGroupRoleRelMap(HttpServletRequest request) {
        return (Map<String, RoleData>) request.getSession().getAttribute(ServermgrConstant.STAFFGROUPSBELONGINGROLES);
    }


    private static final class NewGroupPredicate implements Predicate<String> {

        private List<String> oldGroups;

        public NewGroupPredicate(List<String> oldGroups) {
            this.oldGroups = oldGroups;
        }

        @Override
        public boolean apply(String entityGroup) {
            return oldGroups.contains(entityGroup) == false;
        }
    }

}
