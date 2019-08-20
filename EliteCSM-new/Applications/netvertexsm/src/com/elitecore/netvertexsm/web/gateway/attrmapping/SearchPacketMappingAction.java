package com.elitecore.netvertexsm.web.gateway.attrmapping;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.MappingBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.attrmapping.data.PacketMappingData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.attrmapping.form.PacketMappingForm;

public class SearchPacketMappingAction extends BaseWebAction {
	private static final String LIST_FORWARD = "searchMappingList";
	private static final String FAILURE_FORWARD = "failure";
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_PACKET_MAPPING;
	private static final String MODULE = "PACKET-MAPPING";
	
	public ActionForward execute(ActionMapping mapping,ActionForm actionForm,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter Execute method of "+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				PacketMappingForm searchMappingForm = (PacketMappingForm) actionForm;
				MappingBLManager mappingBLManager = new MappingBLManager();
				PacketMappingData mappingSearchData = new PacketMappingData();
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
	            int requiredPageNo;
	            if(request.getParameter("pageNo") != null)
	    			requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
	    		else
	    			requiredPageNo = new Long(searchMappingForm.getPageNumber()).intValue();
	    		
	            if (requiredPageNo == 0)
	                requiredPageNo = 1;								
	            
				String strName = searchMappingForm.getName();
				String strCommProtocol = searchMappingForm.getCommProtocol();
				String strType = searchMappingForm.getType();
				
				if(strName != null && strName.length() > 0) 
					mappingSearchData.setName(strName);
				
				if(strCommProtocol != null && strCommProtocol.length() > 0) {
					mappingSearchData.setCommProtocol(strCommProtocol);
				} else {
					mappingSearchData.setCommProtocol("ALL");
					searchMappingForm.setCommProtocol("ALL");
				}
				
				if(strType != null && strType.length() > 0) {
					mappingSearchData.setType(strType);
				} else {
					mappingSearchData.setType("ALL");
					searchMappingForm.setType("ALL");
				}
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));					
				PageList pageList = mappingBLManager.search(mappingSearchData, requiredPageNo, pageSize,staffData, ACTION_ALIAS);
				
				searchMappingForm.setName(strName);
				searchMappingForm.setPageNumber(pageList.getCurrentPage());
				searchMappingForm.setTotalPages(pageList.getTotalPages());
				searchMappingForm.setTotalRecords(pageList.getTotalItems());
				searchMappingForm.setListSearchPacketMapping(pageList.getListData());
				searchMappingForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchMappingForm", searchMappingForm);
					
				return mapping.findForward(LIST_FORWARD);
			}catch(Exception managerExp){
				Logger.logError(MODULE,"Error during Data Manager operation, reason : "+ managerExp.getMessage());
                Logger.logTrace(MODULE,managerExp);
        		Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
      			request.setAttribute("errorDetails", errorElements);
                ActionMessage message = new ActionMessage("search.failure");
                ActionMessages messages = new ActionMessages();
                messages.add("information", message);
                saveErrors(request, messages);
			}
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("mapping.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	 
		    return mapping.findForward(FAILURE_FORWARD);
		}else{
	        Logger.logWarn(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("mapping.error.heading","searching");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	 
		return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
}
