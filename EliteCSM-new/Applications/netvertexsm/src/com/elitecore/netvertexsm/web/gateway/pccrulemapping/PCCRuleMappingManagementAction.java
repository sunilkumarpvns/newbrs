package com.elitecore.netvertexsm.web.gateway.pccrulemapping;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.gateway.pccrulemapping.RuleMappingBlManager;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.pccrulemapping.data.RuleMappingData;
import com.elitecore.netvertexsm.datamanager.gateway.profile.data.PCCRuleMappingData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.base.ICreateAction;
import com.elitecore.netvertexsm.web.core.base.IDeleteAction;
import com.elitecore.netvertexsm.web.core.base.ISearchAction;
import com.elitecore.netvertexsm.web.core.base.IUpdateAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.netvertexsm.web.gateway.pccrulemapping.form.PCCRuleMappingForm;

public class PCCRuleMappingManagementAction extends BaseWebDispatchAction implements ISearchAction, ICreateAction, IUpdateAction,IDeleteAction {
	private static final String MODULE = PCCRuleMappingManagementAction.class.getSimpleName();
	
	private static final String SEARCH_FORWARD = "searchpccrulemapping";
	private static final String CREATE_PAGE = "createpccrulemapping";
	private static final String VIEW_PAGE = "viewpccrulemapping";
	private static final String EDIT_PAGE = "editpccrulemapping";
	private static final String ACTION_ALIAS = ConfigConstant.CREATE_GATEWAY;
	
	@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		try{
			PCCRuleMappingForm pccRuleMappingForm = (PCCRuleMappingForm) form;
        	RuleMappingBlManager ruleMappingBlManager=new RuleMappingBlManager();
        	
        	RuleMappingData ruleMappingData=new RuleMappingData();
        	String strMappingId = request.getParameter("ruleMappingId");
        	boolean isRequestForDuplicate=Boolean.parseBoolean(request.getParameter("isRequestForDuplicate"));
        	Long mappingId = Long.parseLong(strMappingId);

			if(mappingId != null ){
				ruleMappingData.setRuleMappingId(mappingId);
				ruleMappingData = ruleMappingBlManager.getRuleMappingData(mappingId);
				pccRuleMappingForm.setMappingId(mappingId);
				pccRuleMappingForm.setPccRuleMappingList(ruleMappingData.getPccRuleMappingList());
				if(isRequestForDuplicate){
					pccRuleMappingForm.setName("Copy_of_"+ruleMappingData.getName());
					pccRuleMappingForm.setDescription(getDefaultDescription(request));
					request.setAttribute("pccRuleMappingForm", pccRuleMappingForm);
					request.setAttribute("ruleMappingData", ruleMappingData);
					request.setAttribute("requestForDuplicate", isRequestForDuplicate);
					
					return mapping.findForward(CREATE_PAGE);
				}else {
					pccRuleMappingForm.setName(ruleMappingData.getName());
					pccRuleMappingForm.setDescription(ruleMappingData.getDescription());
				}
			}
			request.setAttribute("pccRuleMappingForm", pccRuleMappingForm);
			request.setAttribute("ruleMappingData", ruleMappingData);
			return mapping.findForward(EDIT_PAGE);
		}catch(Exception e){
			Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("update.init.failed"));
            saveErrors(request, messages);
            return mapping.findForward(FAILURE);
		} 
		
	}

	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form,	HttpServletRequest request, HttpServletResponse response)throws Exception {
		try{
			PCCRuleMappingForm pccRuleMappingForm = (PCCRuleMappingForm) form;
        	RuleMappingBlManager ruleMappingBlManager=new RuleMappingBlManager();
        	
			RuleMappingData ruleMapData = new RuleMappingData();
			ruleMapData.setRuleMappingId(pccRuleMappingForm.getMappingId());
			ruleMapData.setName(pccRuleMappingForm.getName());
			ruleMapData.setDescription(pccRuleMappingForm.getDescription());
			
			ruleMapData.setPccRuleMappingList(getPCCRuleMappingList(request));
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			ruleMappingBlManager.update(ruleMapData,staffData, ACTION_ALIAS);
		    ActionMessage message = new ActionMessage("rulemapping.update.success", pccRuleMappingForm.getName());
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveMessages(request,messages);
			request.setAttribute("responseUrl","/pccRuleManagement.do?method=initSearch");
			return mapping.findForward(SUCCESS);
		}catch (Exception e) {
			Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("rulemapping.update.failure"));
            saveErrors(request, messages);
            
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("rulemapping.error.heading","editing");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	 	            
            return mapping.findForward(FAILURE);
		}  
	}

	@Override
	public ActionForward initCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in init create Method");
		PCCRuleMappingForm pccRuleMappingForm = (PCCRuleMappingForm) form;
		pccRuleMappingForm.setDescription(getDefaultDescription(request));
		return mapping.findForward(CREATE_PAGE);
	}

	@Override
	public ActionForward create(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered create Method");
		try{
			PCCRuleMappingForm pccRuleMappingForm = (PCCRuleMappingForm) form;
			RuleMappingData ruleMappingData=new RuleMappingData();
			ruleMappingData.setName(pccRuleMappingForm.getName());
			ruleMappingData.setDescription(pccRuleMappingForm.getDescription());
			ruleMappingData.setPccRuleMappingList(getPCCRuleMappingList(request));
			RuleMappingBlManager ruleMappingBlManager=new RuleMappingBlManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			ruleMappingBlManager.create(ruleMappingData,staffData, null);
			ActionMessage message = new ActionMessage("rulemapping.create.success", pccRuleMappingForm.getName());
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveMessages(request,messages);
			request.setAttribute("responseUrl","/pccRuleManagement.do?method=initSearch");
			return mapping.findForward(SUCCESS);
		}catch (Exception e) {
			Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("rulemapping.create.failure"));
            saveErrors(request, messages);
            	            
            ActionMessages errorHeadingMessage = new ActionMessages();
            ActionMessage message = new ActionMessage("rulemapping.error.heading","creating");
            errorHeadingMessage.add("errorHeading",message);
            saveMessages(request,errorHeadingMessage); 	 
            return mapping.findForward(FAILURE);
		} 
	}

	@Override
	public ActionForward initSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
			Logger.logDebug(MODULE, "Entered in Init Search Method");
			return search( mapping,  form,  request,  response);
	}

	@Override
	public ActionForward search(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logInfo(MODULE,"Enter into search method");
		
		try{
			PCCRuleMappingForm pccRuleMappingForm = (PCCRuleMappingForm) form;
			RuleMappingBlManager ruleMappingBlManager = new RuleMappingBlManager();
			RuleMappingData ruleMappingData=new RuleMappingData();
			

			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo;
			if(request.getParameter("pageNo") != null){
				requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
			}else{
				requiredPageNo = new Long(pccRuleMappingForm.getPageNumber()).intValue();
			}
			if(requiredPageNo == 0)
				requiredPageNo = 1;
			
			String strName = pccRuleMappingForm.getName();
			if(strName != null && strName.length() > 0){
				ruleMappingData.setName(strName);
			}else{
				pccRuleMappingForm.setName("");
			}
			
			IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
			PageList pageList = ruleMappingBlManager.search(ruleMappingData, requiredPageNo,pageSize, staffData, ACTION_ALIAS);
			pccRuleMappingForm.setRuleMappingList(pageList.getListData());
			pccRuleMappingForm.setPageNumber(pageList.getCurrentPage());
			pccRuleMappingForm.setTotalPages(pageList.getTotalPages());
			pccRuleMappingForm.setTotalRecords(pageList.getTotalItems());
			pccRuleMappingForm.setAction(BaseConstant.LISTACTION);
			request.setAttribute("pccRuleMappingForm", pccRuleMappingForm);

		}catch(Exception e){
			Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
			Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("rulemapping.search.failure"));
			saveErrors(request, messages);
			
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        ActionMessage message = new ActionMessage("rulemapping.error.heading","searching");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
		}
		return mapping.findForward(SEARCH_FORWARD);
	}

	@Override
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logInfo(MODULE,"Enter into delete method");
		try{
			RuleMappingBlManager ruleMappingBlManager = new RuleMappingBlManager();
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			String[] strRuleMappingIds =request.getParameterValues("ruleMappingId");
			Long[] ruleMappingIds = convertStringIdsToLong(strRuleMappingIds);  
			ruleMappingBlManager.delete(ruleMappingIds,staffData,ACTION_ALIAS);
			ActionMessage message = new ActionMessage("rulemapping.delete.success");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveMessages(request,messages);
           	request.setAttribute("responseUrl","/pccRuleManagement.do?method=initSearch");
            return mapping.findForward(SUCCESS);
		}catch(Exception e){
			Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
			Logger.logTrace(MODULE,e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
            ActionMessages messages = new ActionMessages();
            messages.add("information", new ActionMessage("rulemapping.delete.failure"));
            messages.add("information", new ActionMessage("rulemapping.delete.failure.reason" ,"Reason"));
            saveErrors(request, messages);
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        ActionMessage message = new ActionMessage("rulemapping.error.heading","deleting");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
            return mapping.findForward(FAILURE);
        }
	}
	
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in View Method");
			PCCRuleMappingForm pccRuleMappingForm = (PCCRuleMappingForm)form;
			try{
				RuleMappingBlManager ruleMappingBlManager =new RuleMappingBlManager();
				
				String strMappingId = request.getParameter("ruleMappingId");
				Long mappingId = Long.parseLong(strMappingId);
				RuleMappingData ruleMappingData=ruleMappingBlManager.getRuleMappingData(mappingId);
				List<PCCRuleMappingData> pccRuleMapList=ruleMappingData.getPccRuleMappingList();
 			
 				pccRuleMappingForm.setMappingId(ruleMappingData.getRuleMappingId());
 				pccRuleMappingForm.setName(ruleMappingData.getName());
 				pccRuleMappingForm.setDescription(ruleMappingData.getDescription());
 				pccRuleMappingForm.setPccRuleMappingList(ruleMappingData.getPccRuleMappingList());
 				
 				request.setAttribute("ruleMappingData",ruleMappingData);
				request.setAttribute("pccRuleMapList", pccRuleMapList);
				request.setAttribute("pccRuleMappingForm", pccRuleMappingForm);
				return mapping.findForward(VIEW_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("rulemapping.view.failure"));
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("rulemapping.error.heading","viewing");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);	            
	            return mapping.findForward(FAILURE);
	        }
		}	
	
	

	private List<PCCRuleMappingData> getPCCRuleMappingList(HttpServletRequest request){
		List<PCCRuleMappingData> pccRuleMappingList = new ArrayList<PCCRuleMappingData>();
		//		Static PCCRule Mapping
		String[] attributeS = request.getParameterValues("attributeS");
		String[] policyKeyS = request.getParameterValues("policyKeyS");
		String[] defaultValueS = request.getParameterValues("defaultValueS");
		String[] valueMappingS = request.getParameterValues("valueMappingS");

		if(attributeS != null && attributeS.length > 0)
			for(int i=0; i<attributeS.length; i++) {
				if(attributeS[i]!=null && attributeS[i].trim().length()>0 && policyKeyS[i]!=null && policyKeyS[i].trim().length()>0){
					PCCRuleMappingData pccRuleMapping = new PCCRuleMappingData();
					pccRuleMapping.setAttribute(attributeS[i]);
					pccRuleMapping.setPolicyKey(policyKeyS[i]);
					pccRuleMapping.setDefaultValue(defaultValueS[i]);
					pccRuleMapping.setValueMapping(valueMappingS[i]);
					pccRuleMapping.setType("STATIC");
					pccRuleMappingList.add(pccRuleMapping);
				}
			}

		//	Dynamic PCCRule Mapping
		String[] attributeD = request.getParameterValues("attributeD");
		String[] policyKeyD = request.getParameterValues("policyKeyD");
		String[] defaultValueD = request.getParameterValues("defaultValueD");
		String[] valueMappingD = request.getParameterValues("valueMappingD");

		if(attributeD != null && attributeD.length > 0)
			for(int i=0; i<attributeD.length; i++) {
				if(attributeD[i]!=null && attributeD[i].trim().length()>0 && policyKeyD[i]!=null && policyKeyD[i].trim().length()>0){
					PCCRuleMappingData pccRuleMapping = new PCCRuleMappingData();
					pccRuleMapping.setAttribute(attributeD[i]);
					pccRuleMapping.setPolicyKey(policyKeyD[i]);
					pccRuleMapping.setDefaultValue(defaultValueD[i]);
					pccRuleMapping.setValueMapping(valueMappingD[i]);
					pccRuleMapping.setType("DYNAMIC");
					pccRuleMappingList.add(pccRuleMapping);
				}
			}
		return pccRuleMappingList;
	}
}
