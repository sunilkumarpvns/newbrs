package com.elitecore.netvertexsm.web.core.base;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import com.elitecore.corenetvertex.pkg.acl.GroupData;
import com.elitecore.netvertexsm.blmanager.core.system.group.GroupDataBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import org.apache.struts.action.Action;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.netvertexsm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.netvertexsm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.auhorization.UnidentifiedServerInstanceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.communication.CommunicationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.BaseDatasourceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceConnectionException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceConstraintViolationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceDuplicateMappingException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceGenericException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceIdentifierGenerationException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceLockException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceMappingException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceMultipleObjectWithSameIdentifier;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceNonUniqueResultException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourcePropertyNotFoundException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceQueryException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceSQLGrammarException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceStaleStateException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceTransactionException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datasource.DatasourceUncategorizedException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.datavalidation.InvalidArrguementsException;
import com.elitecore.netvertexsm.datamanager.core.exceptions.opererationfailed.InitializationFailedException;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.netvertexsm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.netvertexsm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.netvertexsm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.netvertexsm.util.EliteAssert;
import com.elitecore.netvertexsm.util.communicationmanager.CommunicationManagerFactory;
import com.elitecore.netvertexsm.util.communicationmanager.remotecommunication.IRemoteCommunicationManager;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.CommunicationConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.ServermgrConstant;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;

public abstract class BaseWebAction extends Action {
    
    protected static final String INVALID_ACCESS_FORWARD = "failure";
    protected final String MODULE = this.getClass().getSimpleName();
    
    protected static final String SUCCESS = "success";
    protected static final String FAILURE = "failure";
    protected static final String POPUPSUCCESS = "popupsuccess";
    protected static final String POPUPFAILURE = "popupfailure";
    
    
    protected final IStaffData getStaffObject(SystemLoginForm systemLoginForm) throws DataManagerException{
		
		StaffBLManager staffBLManager = new StaffBLManager();
		String userId = systemLoginForm.getUserId();
		
		IStaffData staffData = new StaffData();
		staffData = staffBLManager.getStaffData(userId);
		return staffData; 		
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
    
    
    protected void indetifyUserDefineDatasourceException(BaseDatasourceException e, ActionMessages messages) {
        
        // General Datasource Exception
        if(e instanceof DatasourceIdentifierGenerationException) {
            ActionMessage message = new ActionMessage("datasource.identifier.generation.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceTransactionException) {
            ActionMessage message = new ActionMessage("datasource.transaction.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceNonUniqueResultException) {
            ActionMessage message = new ActionMessage("datasource.non.unique.result.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceMultipleObjectWithSameIdentifier) {
            ActionMessage message = new ActionMessage("datasource.multiple.object.with.same.identifier");
            messages.add("information",message);
        }
        
        // JDBC Exception
        if(e instanceof DatasourceSQLGrammarException) {
            ActionMessage message = new ActionMessage("datasource.sql.grammar.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceConstraintViolationException) {
            ActionMessage message = new ActionMessage("datasource.constraint.violation.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceConnectionException) {
            ActionMessage message = new ActionMessage("datasource.connection.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceGenericException) {
            ActionMessage message = new ActionMessage("datasource.generic.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceLockException) {
            ActionMessage message = new ActionMessage("datasource.lock.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceException) {
            ActionMessage message = new ActionMessage("datasource.exception");
            messages.add("information",message);
        }
        
        
        // Hibernate Mapping Exception
        if(e instanceof DatasourceDuplicateMappingException) {
            ActionMessage message = new ActionMessage("datasource.duplicate.mapping.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourcePropertyNotFoundException) {
            ActionMessage message = new ActionMessage("datasource.property.not.found.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceMappingException) {
            ActionMessage message = new ActionMessage("datasource.mapping.exception");
            messages.add("information",message);
        }
        
        // General Datasource Exception
        if(e instanceof DatasourceQueryException) {
            ActionMessage message = new ActionMessage("datasource.query.exception");
            messages.add("information",message);
        }
        if(e instanceof DatasourceStaleStateException) {
            ActionMessage message = new ActionMessage("datasource.stale.state.exception");
            messages.add("information",message);
        }
        
        // Datasource Uncategorized Exception
        if(e instanceof DatasourceUncategorizedException) {
            ActionMessage message = new ActionMessage("datasource.uncategorized.exception");
            messages.add("information",message);
        }
    }
    
    /*
     * @author kaushikVira
     * @Return it give System CurrentTime TimeStemp
     */
    protected Timestamp getCurrentTimeStemp(){
        return new Timestamp(new Date().getTime());
    }
    
    /*
     * @author kaushikVira
     * @Return userId of Currently LoggedIn user.
     */
    protected Long getLoggedInUserId(HttpServletRequest request) throws InvalidArrguementsException{
        EliteAssert.notNull(request,"request must be specified.");
        SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
        EliteAssert.notNull(radiusLoginForm,"radiusLoginForm must be specified in session.");
        Long staffId = Long.parseLong(radiusLoginForm.getUserId());
        return staffId;

    }
   protected long getCurrentPageNumberAfterDel(int numOfdelRecord,long pageNumber,long totalPages,long totalRecords)
    {
 	   
 	   
 	   long currentPageNumber = pageNumber;
 	   Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
 	   Logger.logDebug(MODULE, "TotalRecords-->"+totalRecords);
 	   Logger.logDebug(MODULE,"PageSize--> "+pageSize);
 	   Logger.logDebug(MODULE,"totalPages-->"+totalPages);
 	   Logger.logDebug(MODULE,"currentPageNumber-->"+currentPageNumber);
 	   Logger.logDebug(MODULE," strSelectedIdsLen-->"+numOfdelRecord);
 	   Logger.logDebug(MODULE," totalRecords%pageSize--> "+totalRecords%pageSize);
 	    if((totalPages+1) == currentPageNumber && (numOfdelRecord == (totalRecords%pageSize) || numOfdelRecord == pageSize) )
 	    {
 	        currentPageNumber--; 	
 	    	
 	    }
 	    return currentPageNumber;
 	   
 	   
    }
   protected String getDefaultDescription(HttpServletRequest request){
	   String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getUserName();
	   SimpleDateFormat formatter = new SimpleDateFormat(ConfigManager.get(ConfigConstant.DATE_FORMAT));
	   return ("Created by " + userName + " on " + formatter.format(new Date()));		    
   }
   
   
   public void doAuditing(IStaffData staffData,String actionAlias)throws DataManagerException{
   		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
   		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			session.beginTransaction();	
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
			session.commit();
		    session.close();
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			session.close();
			throw new DataManagerException("Action failed :"+e.getMessage());
		}
		
   }
   public  SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession  session){
		SystemAuditDataManager gracePolicyDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return gracePolicyDataManager;
	}
   
   
   public ActionForward actionNotPermittedFailure(ActionMapping mapping,HttpServletRequest request){
	   	printPermitedActionAlias(request);
		ActionMessages messages = new ActionMessages();
		messages.add("information", new ActionMessage("general.user.restricted"));
		saveErrors(request, messages);
		return mapping.findForward(INVALID_ACCESS_FORWARD);
}

    protected Map<String, RoleData> getGroupIdVsStaffGroupRoleRelMap(HttpServletRequest request) {
        return (Map<String, RoleData>) request.getSession().getAttribute(ServermgrConstant.STAFFGROUPSBELONGINGROLES);
    }

    protected String getUserName(HttpServletRequest request) throws InvalidArrguementsException {
        SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
        EliteAssert.notNull(radiusLoginForm, "radiusLoginForm must be specified in session.");
        return radiusLoginForm.getUserName();
    }

}
