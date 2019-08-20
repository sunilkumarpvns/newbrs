package com.elitecore.elitesm.web.core.base;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.action.Action;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.BaseDatasourceException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceConnectionException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceConstraintViolationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceDuplicateMappingException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceGenericException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceIdentifierGenerationException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceLockException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceMappingException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceMultipleObjectWithSameIdentifier;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceNonUniqueResultException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourcePropertyNotFoundException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceQueryException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceSQLGrammarException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceStaleStateException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceTransactionException;
import com.elitecore.elitesm.datamanager.core.exceptions.datasource.DatasourceUncategorizedException;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.InvalidArrguementsException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public abstract class BaseWebAction extends Action {
    
    protected static final String INVALID_ACCESS_FORWARD = "failure";
    protected static final String MODULE                 = "BaseWebAction";
    
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
    
    protected boolean checkAccess( HttpServletRequest request , String subModuleActionAlias ) throws ActionNotPermitedException {
        String userName = null;
        try{
            EliteAssert.notNull(request,"request must be specified.");
            EliteAssert.notNull(subModuleActionAlias,"subModuleActionAlias must be specified.");
            EliteAssert.valiedWebSession(request);
            SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
            EliteAssert.notNull(radiusLoginForm,"radiusLoginForm must be specified in session.");
            userName = radiusLoginForm.getSystemUserName();

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
            userName = radiusLoginForm.getSystemUserName();

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
    protected String getLoggedInUserId(HttpServletRequest request) throws InvalidArrguementsException{
        EliteAssert.notNull(request,"request must be specified.");
        SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
        EliteAssert.notNull(radiusLoginForm,"radiusLoginForm must be specified in session.");
        
        return radiusLoginForm.getUserId();

    }
   protected long getCurrentPageNumberAfterDel(int numOfdelRecord,long pageNumber,long totalPages,long totalRecords)
    {
 	   	   
 	   long currentPageNumber = pageNumber;
 	   Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
 	   
 	   Logger.logDebug(MODULE," TotalRecords         -->"+totalRecords);
 	   Logger.logDebug(MODULE," PageSize             --> "+pageSize);
 	   Logger.logDebug(MODULE," TotalPages           -->"+totalPages);
 	   Logger.logDebug(MODULE," CurrentPageNumber    -->"+currentPageNumber);
 	   Logger.logDebug(MODULE," strSelectedIdsLen    -->"+numOfdelRecord);
 	   Logger.logDebug(MODULE," TotalRecords%pageSize--> "+totalRecords%pageSize);
 	   
 	    if((totalPages+1) == currentPageNumber && (numOfdelRecord == (totalRecords%pageSize) || numOfdelRecord == pageSize) )
 	    {
 	        currentPageNumber--; 	
 	    	
 	    }
 	    return currentPageNumber;
 	   
 	   
    }
    
   protected String getDefaultDescription(String userName){
	    SimpleDateFormat formatter = new SimpleDateFormat(ConfigManager.get(ConfigConstant.DATE_FORMAT));
		return ("Created by " + userName + " on " + formatter.format(new Date()));		    
   }
   
   protected String getDefaultDescription(HttpServletRequest request){
	   String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
	   SimpleDateFormat formatter = new SimpleDateFormat(ConfigManager.get(ConfigConstant.DATE_FORMAT));
	   return ("Created by " + userName + " on " + formatter.format(new Date()));		    
   }
   
   public void doAuditing(IStaffData staffData,String actionAlias)throws DataManagerException{
   		IDataManagerSession session = DataManagerSessionFactory.getInstance().getDataManagerSession();
   		SystemAuditDataManager systemAuditDataManager = getSystemAuditDataManager(session);

		if (systemAuditDataManager == null)
			throw new DataManagerException("Data Manager implementation not found for " + getClass().getName());

		try{
			systemAuditDataManager.updateTbltSystemAudit(staffData, actionAlias);
		}catch(Exception e){
			e.printStackTrace();
			session.rollback();
			throw new DataManagerException("System unable to perform audit, Reason: "+e.getMessage());
		}finally{
			session.close();
		}
   }
   public  SystemAuditDataManager getSystemAuditDataManager(IDataManagerSession  session){
		SystemAuditDataManager gracePolicyDataManager = (SystemAuditDataManager)DataManagerFactory.getInstance().getDataManager(SystemAuditDataManager.class, session);
		return gracePolicyDataManager;
	}
}
