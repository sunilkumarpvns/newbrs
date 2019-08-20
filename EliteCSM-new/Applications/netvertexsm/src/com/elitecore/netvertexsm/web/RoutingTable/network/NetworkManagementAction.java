package com.elitecore.netvertexsm.web.RoutingTable.network;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.RoutingTable.mccmncgroup.MCCMNCGroupBLManager;
import com.elitecore.netvertexsm.blmanager.RoutingTable.network.NetworkBLManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCCodeGroupRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandOperatorRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.RoutingTable.network.form.NetworkManagementForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.base.ICreateAction;
import com.elitecore.netvertexsm.web.core.base.IDeleteAction;
import com.elitecore.netvertexsm.web.core.base.ISearchAction;
import com.elitecore.netvertexsm.web.core.base.IUpdateAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class NetworkManagementAction extends BaseWebDispatchAction implements ICreateAction, IUpdateAction, ISearchAction, IDeleteAction{
	private static final String MODULE = "NetworkManagementAction";
	
	private static final String SEARCH_PAGE = "searchNetworkPage";
	private static final String CREATE_PAGE = "createNetworkPage";
	private static final String VIEW_PAGE = "viewNetworkPage";
	private static final String EDIT_PAGE = "editNetworkPage";
	
	
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_NETWORK;
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_NETWORK;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_NETWORK;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_NETWORK;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_NETWORK;

	@Override
	public ActionForward initSearch(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Search Method");
		return search( mapping,  form,  request,  response);
	}

	@Override
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Search Method");
			try{
				NetworkManagementForm networkManagementForm = (NetworkManagementForm)form;
				NetworkBLManager networkBLManger = new NetworkBLManager();				
				NetworkData networkData = new NetworkData();
				
				List<CountryData> countryDataList = networkBLManger.getCountryDataList();
				List<OperatorData> operatorDataList = networkBLManger.getOperatorDataList();
				networkManagementForm.setCountryDataList(countryDataList);
				networkManagementForm.setOperatorDataList(operatorDataList);
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(networkManagementForm.getPageNumber()).intValue();
				}
				if (requiredPageNo == 0)
					requiredPageNo = 1;		

				convertFormToBean(networkManagementForm, networkData);
	           
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = networkBLManger.search(networkData, requiredPageNo, pageSize,staffData, SEARCH_ACTION_ALIAS);
				
				networkManagementForm.setMccmncCodesDataList(pageList.getListData());
				networkManagementForm.setPageNumber(pageList.getCurrentPage());
				networkManagementForm.setTotalPages(pageList.getTotalPages());
				networkManagementForm.setTotalRecords(pageList.getTotalItems());
				networkManagementForm.setActionName(BaseConstant.LISTACTION);
			    request.setAttribute("networkManagementForm", networkManagementForm);
			    
				return mapping.findForward(SEARCH_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("network.management.search.failure"));
	            saveErrors(request, messages);
	            
		         ActionMessages errorHeadingMessage = new ActionMessages();
		         ActionMessage message = new ActionMessage("network.management.error.heading","searching");
		         errorHeadingMessage.add("errorHeading",message);
		         saveMessages(request,errorHeadingMessage);
			}
			return mapping.findForward(FAILURE);
	}

	
	@Override
	public ActionForward initCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Create Method");		
		if(checkAccess(request, CREATE_ACTION_ALIAS)){
			NetworkManagementForm networkManagementForm = (NetworkManagementForm)form;
			NetworkBLManager networkBLManger = new NetworkBLManager();
			List<CountryData> countryDataList = networkBLManger.getCountryDataList();
			List<OperatorData> operatorDataList = networkBLManger.getOperatorDataList();
			List<BrandData> brandDataList = networkBLManger.getBrandDataList();
			List<BrandOperatorRelData> brandOperatorRelDataList = networkBLManger.getBrandOperatorDataRelList();
			networkManagementForm.setBrandDataList(brandDataList);
			networkManagementForm.setBrandOperatorRelDataList(brandOperatorRelDataList);
			networkManagementForm.setCountryDataList(countryDataList);
			networkManagementForm.setOperatorDataList(operatorDataList);
			
			request.setAttribute("networkManagementForm", networkManagementForm);
			return mapping.findForward(CREATE_PAGE);
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage message = new ActionMessage("general.user.restricted");
            ActionMessages messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
	         ActionMessages errorHeadingMessage = new ActionMessages();
	         message = new ActionMessage("network.management.error.heading","creating");
	         errorHeadingMessage.add("errorHeading",message);
	         saveMessages(request,errorHeadingMessage);            
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
		
	}

	@Override
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Create Method");
		if((checkAccess(request, CREATE_ACTION_ALIAS))){
			NetworkManagementForm networkManagementForm = (NetworkManagementForm)form;
			
			try{
				NetworkBLManager mccmncMgmtBLManager = new NetworkBLManager();
				NetworkData networkData = new NetworkData();
				convertFormToBean(networkManagementForm, networkData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				mccmncMgmtBLManager.create(networkData,staffData, CREATE_ACTION_ALIAS);
               	
               	ActionMessage message = new ActionMessage("network.management.create.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/networkManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("network.management.create.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("network.management.error.heading","creating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
	            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("network.management.error.heading","creating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	@Override
	public ActionForward delete(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Delete Method");
		if((checkAccess(request, DELETE_ACTION_ALIAS))){
			try{
				NetworkBLManager networkBLManager = new NetworkBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String[] strNetworkIDs = (String[])request.getParameterValues("networkID");
				Long[] networkIDs = convertStringIdsToLong(strNetworkIDs);  
				networkBLManager.delete(networkIDs,staffData,DELETE_ACTION_ALIAS);

				ActionMessage message = new ActionMessage("network.management.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/networkManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("network.management.delete.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("network.management.error.heading","deleting");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
		        
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);

	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("network.management.error.heading","deleting");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Update Method");
		if((checkAccess(request,UPDATE_ACTION_ALIAS))){
			NetworkManagementForm networkManagementForm = (NetworkManagementForm)form;
			
			try{
				NetworkBLManager mccmncMgmtBLManager = new NetworkBLManager();
				NetworkData networkData = mccmncMgmtBLManager.getNetworkDetailData(networkManagementForm.getNetworkID());
				List<CountryData> countryDataList = mccmncMgmtBLManager.getCountryDataList();
				List<OperatorData> operatorDataList = mccmncMgmtBLManager.getOperatorDataList();
				List<BrandData> brandDataList = mccmncMgmtBLManager.getBrandDataList();
				List<BrandOperatorRelData> brandOperatorRelDataList = mccmncMgmtBLManager.getBrandOperatorDataRelList();
				networkManagementForm.setBrandDataList(brandDataList);
				networkManagementForm.setBrandOperatorRelDataList(brandOperatorRelDataList);
				networkManagementForm.setCountryDataList(countryDataList);
				networkManagementForm.setOperatorDataList(operatorDataList);
				convertBeanToForm(networkManagementForm, networkData);
				request.setAttribute("networkData", networkData);
				request.setAttribute("networkManagementForm", networkManagementForm);
               	return mapping.findForward(EDIT_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("network.management.update.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("network.management.error.heading","updating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
	            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("network.management.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
	        
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
		
	}

	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Update Method");
		if((checkAccess(request, UPDATE_ACTION_ALIAS))){
			NetworkManagementForm networkManagementForm = (NetworkManagementForm)form;
			
			try{
				NetworkBLManager mccmncMgmtBLManager = new NetworkBLManager();
				NetworkData networkData = new NetworkData();
				convertFormToBean(networkManagementForm, networkData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				mccmncMgmtBLManager.update(networkData,staffData, UPDATE_ACTION_ALIAS);
               	
               	ActionMessage message = new ActionMessage("network.management.update.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/networkManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("network.management.update.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("network.management.error.heading","updating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
	            
	            return mapping.findForward(FAILURE);
	        }
		}else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("network.management.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in View Method");
			NetworkManagementForm networkManagementForm = (NetworkManagementForm)form;
			try{
				NetworkBLManager mccmncMgmtBLManager = new NetworkBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				NetworkData networkData = mccmncMgmtBLManager.getNetworkDetailData(networkManagementForm.getNetworkID(),staffData,VIEW_ACTION_ALIAS);
				
				MCCMNCGroupBLManager mccmncGroupBLManager=new MCCMNCGroupBLManager();
				MCCMNCCodeGroupRelData mccmncCodeGroupRelData=new MCCMNCCodeGroupRelData();
				mccmncCodeGroupRelData.setMccMNCID(networkManagementForm.getNetworkID());
				MCCMNCGroupData mccmncGroupData=mccmncGroupBLManager.getMCCMNCGroupByNetwork(mccmncCodeGroupRelData);
				
				convertBeanToForm(networkManagementForm, networkData);
				request.setAttribute("networkData", networkData);
				request.setAttribute("mccmncGroupData",mccmncGroupData);
			
				request.setAttribute("networkManagementForm", networkManagementForm);
				return mapping.findForward(VIEW_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("network.management.view.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("network.management.error.heading","viewing");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
		        
	            return mapping.findForward(FAILURE);
	        }
	}
	
	private void convertBeanToForm(NetworkManagementForm form,NetworkData data){
		form.setNetworkID(data.getNetworkID());
		form.setOperatorID(data.getOperatorID());
		form.setCountryID(data.getCountryID());
		form.setNetworkName(data.getNetworkName());
		form.setMcc(data.getMcc());
		form.setMnc(data.getMnc());
		form.setBrandID(data.getBrandID());
		form.setTechnology(data.getTechnology());				 
	}
	
	private void convertFormToBean(NetworkManagementForm form,NetworkData data){
		if(form.getNetworkName()!=null){
		data.setNetworkName(form.getNetworkName().trim());
		}
		data.setMcc(form.getMcc());
		data.setMnc(form.getMnc());		
		
		if(form.getTechnology() != null && !form.getTechnology().equalsIgnoreCase("0")){
			data.setTechnology(form.getTechnology());
		}
		
		if(form.getCountryID()!=null && form.getCountryID().longValue() > 0){
			data.setCountryID(form.getCountryID());
		}
		
		if(form.getOperatorID()!=null && form.getOperatorID().longValue() > 0){
			data.setOperatorID(form.getOperatorID());
		}
		
		if(form.getBrandID()!=null && form.getBrandID().longValue() > 0){
			data.setBrandID(form.getBrandID());
		}
		
		if(form.getNetworkID()!=null && form.getNetworkID().longValue() > 0){
			data.setNetworkID(form.getNetworkID());
		}
	}
}