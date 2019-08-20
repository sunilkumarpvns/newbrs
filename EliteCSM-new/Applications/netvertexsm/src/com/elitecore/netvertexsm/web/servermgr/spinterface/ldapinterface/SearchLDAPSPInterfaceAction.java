package com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.datasource.LDAPDatasourceBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.drivers.DriverBLManager;
import com.elitecore.netvertexsm.blmanager.servermgr.spinterface.SPInterfaceBLManager;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.datasource.ldap.data.LDAPDatasourceData;
import com.elitecore.netvertexsm.datamanager.servermgr.drivers.data.DriverInstanceData;
import com.elitecore.netvertexsm.datamanager.servermgr.spinterface.ldapinterface.data.LDAPSPInterfaceData;
import com.elitecore.netvertexsm.util.constants.ActionMessageConstant;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.servermgr.spinterface.ldapinterface.form.SearchLDAPSPInterfaceForm;

public class SearchLDAPSPInterfaceAction extends BaseWebAction {
	
	private static final String LIST_FORWARD = "searchLDAPList";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_SP_INTERFACE;
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
            
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				SearchLDAPSPInterfaceForm searchLDAPSPInterfaceForm = (SearchLDAPSPInterfaceForm) actionForm;
				DriverBLManager driverBLManager = new DriverBLManager();
				SPInterfaceBLManager spInterfaceDriverBLManager = new SPInterfaceBLManager();
				LDAPSPInterfaceData ldapSpInterfaceData = new LDAPSPInterfaceData();
				
				int requiredPageNo = Integer.parseInt(String.valueOf(searchLDAPSPInterfaceForm.getPageNumber()));
				if(requiredPageNo == 0)
					requiredPageNo=1;												
				
				Long strLDAPDsId = searchLDAPSPInterfaceForm.getLdapDsId();
				Long strDriverInstanceId = searchLDAPSPInterfaceForm.getDriverInstanceId();
				
				if(strLDAPDsId>0) {
					ldapSpInterfaceData.setLdapDsId(strLDAPDsId);
				}else if(strDriverInstanceId>0) {
					ldapSpInterfaceData.setDriverInstanceId(strDriverInstanceId);
				}else {
					ldapSpInterfaceData.setLdapDsId(null);
					ldapSpInterfaceData.setDriverInstanceId(null);
				}																
									
				String actionAlias = ACTION_ALIAS;
				PageList pageList = spInterfaceDriverBLManager.search(ldapSpInterfaceData, requiredPageNo, 10, actionAlias);
				
				LDAPDatasourceBLManager ldapDatasourceBLManager = new LDAPDatasourceBLManager();
				List<LDAPDatasourceData> ldapDSList = ldapDatasourceBLManager.getLDAPDSList();
				List<DriverInstanceData> driverInstanceList = driverBLManager.getDriverInstanceList();
				
				searchLDAPSPInterfaceForm.setLdapDsList(ldapDSList);
				searchLDAPSPInterfaceForm.setDriverInstanceList(driverInstanceList);
				
				searchLDAPSPInterfaceForm.setLdapDsId(strLDAPDsId);
				searchLDAPSPInterfaceForm.setDriverInstanceId(strDriverInstanceId);				
				searchLDAPSPInterfaceForm.setPageNumber(pageList.getCurrentPage());
				searchLDAPSPInterfaceForm.setTotalPages(pageList.getTotalPages());
				searchLDAPSPInterfaceForm.setTotalRecords(pageList.getTotalItems());
				searchLDAPSPInterfaceForm.setListSearchLDAPDriver(pageList.getListData());
				searchLDAPSPInterfaceForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchLDAPSPInterfaceForm", searchLDAPSPInterfaceForm);
					
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
					
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute(ActionMessageConstant.ERROR_DETAILS, errorElements);
                ActionMessage message = new ActionMessage("spinterface.search.failure");
                ActionMessages messages = new ActionMessages();
                messages.add(ActionMessageConstant.INFORMATION , message);
                saveErrors(request, messages);
			}
		    return mapping.findForward(FAILURE);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add(ActionMessageConstant.INFORMATION , message);
	        saveErrors(request, messages);
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
