package com.elitecore.elitesm.web.core.base;

import com.elitecore.elitesm.blmanager.core.system.staff.StaffBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.StaffData;
import com.elitecore.elitesm.util.EliteAssert;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import org.apache.struts.actions.MappingDispatchAction;

import javax.servlet.http.HttpServletRequest;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

public abstract class BaseMappingDispatchAction extends MappingDispatchAction{

    protected static final String MODULE = "BaseMappingDispatchAction";

    protected IStaffData getStaffObject(SystemLoginForm systemLoginForm) throws DataManagerException {

        StaffBLManager staffBLManager = new StaffBLManager();
        String userId = systemLoginForm.getUserId();

        IStaffData staffData = new StaffData();
        staffData = staffBLManager.getStaffData(userId);
        return staffData;

    }

    protected long getCurrentPageNumberAfterDel(int numOfdelRecord,long pageNumber,long totalPages,long totalRecords){
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

    protected boolean checkActionPermission(HttpServletRequest request , String subModuleActionAlias ) throws ActionNotPermitedException {
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

    protected String getDefaultDescription(String userName){
        SimpleDateFormat formatter = new SimpleDateFormat(ConfigManager.get(ConfigConstant.DATE_FORMAT));
        return ("Created by " + userName + " on " + formatter.format(new Date()));
    }
}
