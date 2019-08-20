package com.elitecore.elitesm.web.driver.subscriberprofile.database;

import java.util.Arrays;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.elitesm.blmanager.servermgr.drivers.subscriberprofile.database.DatabaseSubscriberProfileBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.dbdriver.data.DBAuthDriverData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.DatasourceSchemaData;
import com.elitecore.elitesm.datamanager.servermgr.drivers.subscriberprofile.database.data.IDatasourceSchemaData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.MiscSearchDatabaseSubscriberProfileForm;
import com.elitecore.elitesm.web.driver.subscriberprofile.database.forms.SearchDatabaseSubscriberProfileDataForm;
import com.elitecore.elitesm.web.radius.base.BaseDictionaryAction;

public class MiscSearchDatabaseSubscriberProfileAction extends BaseWebAction {
    
    private static final String SUCCESS_FORWARD = "success";
    private static final String LIST_FORWARD    = "searchDatabaseSubscriberProfileDataList";
    private static final String FAILURE_FORWARD = "failure";
    private static final String ACTION_ALIAS    = ConfigConstant.DELETE_SUBSCRIBE_PROFILE;
    
    public ActionForward execute( ActionMapping mapping , ActionForm actionForm , HttpServletRequest request , HttpServletResponse response ) {
        Logger.logTrace(MODULE, "Enter execute method of " + getClass().getName());
        
        try {
        	if (!checkAccess(request, ACTION_ALIAS))
        		throw new ActionNotPermitedException(ACTION_ALIAS);
        	
        	long currentPageNumber = 0;
        	MiscSearchDatabaseSubscriberProfileForm miscSearchDatabaseSubscriberProfileForm = (MiscSearchDatabaseSubscriberProfileForm) actionForm;
        	
        	if (miscSearchDatabaseSubscriberProfileForm.getMiscAction() != null) {

        		String[] strSelectedIds = request.getParameterValues("select");
        		String fieldName = miscSearchDatabaseSubscriberProfileForm.getFieldName();
        		String dbAuthId = miscSearchDatabaseSubscriberProfileForm.getDbAuthId();
        		String driverInstanceId =miscSearchDatabaseSubscriberProfileForm.getDriverInstanceId();
        		DriverBLManager driverBLManager  = new DriverBLManager();
        		
				DriverInstanceData  driverInstanceData = driverBLManager.getDriverInstanceByDriverInstanceId(driverInstanceId);
				DBAuthDriverData dbAuthDriverData = driverBLManager.getDBDriverByDriverInstanceId(driverInstanceId);
				request.setAttribute("driverInstanceData", driverInstanceData);

        		DatabaseSubscriberProfileBLManager blManager = new DatabaseSubscriberProfileBLManager();
        		
        		if (strSelectedIds != null) {

        			if (miscSearchDatabaseSubscriberProfileForm.getMiscAction().equalsIgnoreCase("delete")) {

        				if (dbAuthDriverData != null) {

        					IDatasourceSchemaData datasourceSchemaData = new DatasourceSchemaData();
        					datasourceSchemaData.setFieldName(fieldName);
        					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
        					blManager.deleteSubscriberProfileRecords(dbAuthDriverData, Arrays.asList(strSelectedIds), datasourceSchemaData);
        					int strSelectedIdsLen = strSelectedIds.length;
        					currentPageNumber = getCurrentPageNumberAfterDel(strSelectedIdsLen,Long.parseLong(miscSearchDatabaseSubscriberProfileForm.getPageNo()),Long.parseLong(miscSearchDatabaseSubscriberProfileForm.getTotalPage()),Long.parseLong(miscSearchDatabaseSubscriberProfileForm.getTotalNumberOfRecord()));
        					doAuditing(staffData, ACTION_ALIAS);        					
        					request.setAttribute("responseUrl","/searchDatabaseSubscriberProfileData.do?action=paging&pageNo=" +currentPageNumber+ "&driverInstanceId="+miscSearchDatabaseSubscriberProfileForm.getDriverInstanceId()+"&dbAuthId="+miscSearchDatabaseSubscriberProfileForm.getDbAuthId()+"&firstFieldName="+miscSearchDatabaseSubscriberProfileForm.getFirstFieldName()+"&firstFieldData="+miscSearchDatabaseSubscriberProfileForm.getFirstFieldData()+"&idFieldName="+miscSearchDatabaseSubscriberProfileForm.getIdFieldName());
        					ActionMessage message = new ActionMessage("database.datasource.delete.datasourcedatadeletesuccess");
        					ActionMessages messages1 = new ActionMessages();
        					messages1.add("information",message);
        					saveMessages(request,messages1);
        					return mapping.findForward(SUCCESS_FORWARD);
        				}
        			}
        		}

        		SearchDatabaseSubscriberProfileDataForm searchDatabaseSubscriberProfileDataForm = new SearchDatabaseSubscriberProfileDataForm();
        		searchDatabaseSubscriberProfileDataForm.setFirstFieldName(miscSearchDatabaseSubscriberProfileForm.getFieldName());
        		searchDatabaseSubscriberProfileDataForm.setFirstFieldData(miscSearchDatabaseSubscriberProfileForm.getFirstFieldData());
        		searchDatabaseSubscriberProfileDataForm.setIdFieldName(miscSearchDatabaseSubscriberProfileForm.getIdFieldName());
        		searchDatabaseSubscriberProfileDataForm.setAction("paging");
        		searchDatabaseSubscriberProfileDataForm.setCurrentPage((int)currentPageNumber);

        		request.setAttribute("searchDatabaseDatasourceDataForm", searchDatabaseSubscriberProfileDataForm);
        		request.setAttribute("pageNo", miscSearchDatabaseSubscriberProfileForm.getPageNo());
        		request.setAttribute("pageNo", currentPageNumber);
        		request.setAttribute("totalNumberOfRecord", miscSearchDatabaseSubscriberProfileForm.getTotalNumberOfRecord());
        		request.setAttribute("totalPage", miscSearchDatabaseSubscriberProfileForm.getTotalPage());

        		return mapping.findForward(LIST_FORWARD);

        	}
        	

        }
        catch (ActionNotPermitedException e) {
            Logger.logError(MODULE, "Error during Data Manager operation ");
            Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
        }
        catch (DataManagerException e) {
            Logger.logTrace(MODULE, "Execute method of :" + getClass().getName());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);

            ActionMessage message = new ActionMessage("database.datasource.delete.datasourcedatadeletefailure");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
        }
        catch (Exception managerExp) {
            managerExp.printStackTrace();
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
			request.setAttribute("errorDetails", errorElements);

            Logger.logTrace(MODULE, "Error during Data Manager operation, reason :" + managerExp.getMessage());
        }
        
        return mapping.findForward(FAILURE_FORWARD);
    }
}
