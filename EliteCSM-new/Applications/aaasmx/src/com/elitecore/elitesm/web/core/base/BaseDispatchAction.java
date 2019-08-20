package com.elitecore.elitesm.web.core.base;

/*import java.sql.Date;
import java.sql.Timestamp;*/
import java.util.HashSet;
import java.util.Set;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts.actions.DispatchAction;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerFactory;
import com.elitecore.elitesm.blmanager.core.system.util.DataManagerSessionFactory;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.datamanager.core.system.util.IDataManagerSession;
import com.elitecore.elitesm.datamanager.systemaudit.SystemAuditDataManager;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public abstract class BaseDispatchAction extends DispatchAction {

	protected static final String MODULE = "BaseDispatchAction";
	protected static final String INVALID_ACCESS_FORWARD = "failure";

	protected final IStaffData getStaffObject(SystemLoginForm systemLoginForm) throws DataManagerException{

		StaffBLManager staffBLManager = new StaffBLManager();
		String userId = systemLoginForm.getUserId();

		IStaffData staffData = new StaffData();
		staffData = staffBLManager.getStaffData(userId);
		return staffData; 
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
	  
	  protected Timestamp getCurrentTimeStemp(){
	        return new Timestamp(new Date().getTime());
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
	  
	  protected boolean checkAccess( HttpServletRequest request , String subModuleActionAlias ) throws ActionNotPermitedException {
	        String userName = null;
	        try{
	            EliteAssert.notNull(request,"request must be specified.");
	            EliteAssert.notNull(subModuleActionAlias,"subModuleActionAlias must be specified.");
	            EliteAssert.valiedWebSession(request);
	            SystemLoginForm radiusLoginForm = (SystemLoginForm) request.getSession(false).getAttribute("radiusLoginForm");
	            EliteAssert.notNull(radiusLoginForm,"radiusLoginForm must be specified in session.");
	            userName = radiusLoginForm.getSystemUserName();

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
	  protected String getDefaultDescription(String userName){
		    SimpleDateFormat formatter = new SimpleDateFormat(ConfigManager.get(ConfigConstant.DATE_FORMAT));
			return ("Created by " + userName + " on " + formatter.format(new Date()));		    
	   }
	   
}
