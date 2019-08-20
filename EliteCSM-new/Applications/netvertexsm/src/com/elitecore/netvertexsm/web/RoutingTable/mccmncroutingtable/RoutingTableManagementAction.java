package com.elitecore.netvertexsm.web.RoutingTable.mccmncroutingtable;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.RoutingTable.mccmncgroup.MCCMNCGroupBLManager;
import com.elitecore.netvertexsm.blmanager.RoutingTable.mccmncroutingtable.MCCMNCRoutingTableBLManager;
import com.elitecore.netvertexsm.blmanager.gateway.GatewayBLManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableGatewayRelData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.gateway.gateway.data.GatewayData;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.constants.RoutingEntryConstants;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.RoutingTable.mccmncroutingtable.form.RoutingTableManagementForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.base.ICreateAction;
import com.elitecore.netvertexsm.web.core.base.IDeleteAction;
import com.elitecore.netvertexsm.web.core.base.ISearchAction;
import com.elitecore.netvertexsm.web.core.base.IUpdateAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class RoutingTableManagementAction extends BaseWebDispatchAction implements ICreateAction, IUpdateAction, ISearchAction, IDeleteAction {

private static final String MODULE = "Routing-Table-MANAGEMENT";

	private static final String LIST_FORWARD = "searchRoutingTable";
	private static final String SEARCH_PAGE = "searchRoutingTable";
	private static final String CREATE_PAGE = "createRoutingTable";
	private static final String VIEW_PAGE = "viewRoutingTable";
	private static final String EDIT_PAGE = "editRoutingTable";
	private static final String MANAGE_ORDER = "manageRoutingEntryOrder";
	
	
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_ROUTING_ENTRY;
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_ROUTING_ENTRY;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_ROUTING_ENTRY;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_ROUTING_ENTRY;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_ROUTING_ENTRY;
	private static final String MANAGE_ROUTING_ENTRY_ORDER_ALIAS = ConfigConstant.MANAGE_ROUTING_ENTRY_ORDER;
	
	private static int i=0;
	

	@Override
	public ActionForward initSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Search Method");
		return search( mapping,  form,  request,  response);
	}

	@Override
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Logger.logInfo(MODULE,"Enter search method of "+getClass().getName());

			try{
				RoutingTableManagementForm routingTableMgmtForm = (RoutingTableManagementForm)form;
				RoutingTableData routingData=new RoutingTableData();
				MCCMNCRoutingTableBLManager routingBlManager=new MCCMNCRoutingTableBLManager();
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(routingTableMgmtForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo = 1;

				String strName = routingTableMgmtForm.getName();
				if(strName != null && strName.length() > 0){
					routingData.setName(strName);
				}else{
					routingTableMgmtForm.setName("");
				}
				String actionAlias = SEARCH_ACTION_ALIAS;
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = routingBlManager.search(routingData, requiredPageNo,pageSize, staffData, actionAlias);
				routingTableMgmtForm.setRoutingTableDataList(pageList.getListData());
				routingTableMgmtForm.setPageNumber(pageList.getCurrentPage());
				routingTableMgmtForm.setTotalPages(pageList.getTotalPages());
				routingTableMgmtForm.setTotalRecords(pageList.getTotalItems());
				routingTableMgmtForm.setName(routingTableMgmtForm.getName());
				routingTableMgmtForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchRoutingTableForm", routingTableMgmtForm);

			}catch(Exception e){
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("routingentry.search.failure"));
				saveErrors(request, messages);
				
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("routingentry.error.heading","searching");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
				
			}
			return mapping.findForward(LIST_FORWARD);
	}


	
	@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
	Logger.logDebug(MODULE, "Entered in Init Update Method");
		if((checkAccess(request,UPDATE_ACTION_ALIAS))){
			RoutingTableManagementForm routingTableMgmtForm = (RoutingTableManagementForm)form;
			try{
 			    MCCMNCRoutingTableBLManager routingBLManager = new MCCMNCRoutingTableBLManager();
 			   GatewayBLManager gatewayBLManager=new GatewayBLManager();
 			    RoutingTableData routingData=routingBLManager.getRoutingTableData(routingTableMgmtForm.getRoutingTableId());
				List<RoutingTableGatewayRelData> routingGatewayRelDataList=routingBLManager.getRoutingGateWayRelList(routingTableMgmtForm.getRoutingTableId());
				routingData.setRoutingTableGatewayRelData(routingGatewayRelDataList);
				if(routingData.getType().equals(RoutingEntryConstants.ROUTING_TYPE_MCCMNC)){
				MCCMNCGroupBLManager mccmncGroupBLManager = new MCCMNCGroupBLManager();
				List<MCCMNCGroupData> mccmncGroupList=mccmncGroupBLManager.getMCCMNCGroupDataList();
				routingTableMgmtForm.setMccmncGroupDataList(mccmncGroupList);
				}
				List<GatewayData> gatewayList=gatewayBLManager.getGatewayList();
				routingTableMgmtForm.setGatewayList(gatewayList);
				convertBeanToForm(routingTableMgmtForm, routingData);
				request.setAttribute("routingTableManagementForm", routingTableMgmtForm);
				request.setAttribute("hideTable", false);
				request.setAttribute("routingTableData",routingData);
               	return mapping.findForward(EDIT_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("routingentry.update.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("routingentry.error.heading","updating");
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
	        message = new ActionMessage("routingentry.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);	       
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}

		}

	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Update Method");
		if((checkAccess(request, UPDATE_ACTION_ALIAS))){
			RoutingTableManagementForm routingTableMgmtForm = (RoutingTableManagementForm)form;
			
			try{
				MCCMNCRoutingTableBLManager routingBLManager=new MCCMNCRoutingTableBLManager();
				RoutingTableData  routingTableData=new RoutingTableData();
				String[]  gatewayIds=request.getParameterValues("gatewayId");
				List<RoutingTableGatewayRelData> routingTableGatewayRelList=new ArrayList<RoutingTableGatewayRelData>();
				if(gatewayIds!=null){
					for( String temp:gatewayIds){
						RoutingTableGatewayRelData routingGatewayRel=new RoutingTableGatewayRelData();
						routingGatewayRel.setGatewayId(Long.parseLong(temp));
						routingGatewayRel.setRoutingTableId(routingTableMgmtForm.getRoutingTableId());
						routingTableGatewayRelList.add(routingGatewayRel);
					}
				}
				String[] weightage = request.getParameterValues("weightage");
				if(weightage!=null){
					int i=0;
					for(String weight:weightage){
						routingTableGatewayRelList.get(i).setWeightage(Long.parseLong(weight));
					i++;	
					}
				}
				routingTableData.setRoutingTableGatewayRelData(routingTableGatewayRelList);
				convertFormToBean(routingTableData,routingTableMgmtForm);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				routingBLManager.update(routingTableData,staffData, UPDATE_ACTION_ALIAS);
               	ActionMessage message = new ActionMessage("routingentry.update.success",routingTableMgmtForm.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
	            request.setAttribute("responseUrl","/routingTableManagement.do?method=initSearch");
              	return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("routingentry.update.failure"));
	            saveErrors(request, messages);

	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("routingentry.error.heading","updating");
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
            message = new ActionMessage("routingentry.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	@Override
	public ActionForward initCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Create Method");
		
		if(checkAccess(request, CREATE_ACTION_ALIAS)){
			RoutingTableManagementForm routingTableMgmtForm = (RoutingTableManagementForm)form;
			MCCMNCGroupBLManager mccMNCGroupBLManager = new MCCMNCGroupBLManager();
			List<MCCMNCGroupData> mccmncgroupList=mccMNCGroupBLManager.getMCCMNCGroupDataList();
			GatewayBLManager gatewayBLManager=new GatewayBLManager();
			List<GatewayData> gatewayList=gatewayBLManager.getGatewayList();
			routingTableMgmtForm.setGatewayList(gatewayList);
			routingTableMgmtForm.setMccmncGroupDataList(mccmncgroupList);
			request.setAttribute("routingTableManagementForm", routingTableMgmtForm);
			return mapping.findForward(CREATE_PAGE);
		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
            ActionMessages errorHeadingMessage = new ActionMessages();
            message = new ActionMessage("routingentry.error.heading","creating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
			
			return mapping.findForward(INVALID_ACCESS_FORWARD);

		}
	}

	
	public ActionForward initManageOrder(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
			Logger.logDebug(MODULE, "Entered in Init Manage Order Method");
		
		if(checkAccess(request, MANAGE_ROUTING_ENTRY_ORDER_ALIAS)){
			RoutingTableManagementForm routingTableMgmtForm = (RoutingTableManagementForm)form;
			MCCMNCRoutingTableBLManager routingBLManager=new MCCMNCRoutingTableBLManager();
			List<RoutingTableData> routingTableDataList=routingBLManager.getRoutingTableDataList();
			routingTableMgmtForm.setRoutingTableDataList(routingTableDataList);
			request.setAttribute("routingTableManagementForm", routingTableMgmtForm);
			return mapping.findForward(MANAGE_ORDER);
		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);

		}
	}

	public ActionForward manageOrder(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response) throws Exception {
			Logger.logDebug(MODULE, "Entered in manageoOrder Method");
			if(checkAccess(request, MANAGE_ROUTING_ENTRY_ORDER_ALIAS)){
				try{
					RoutingTableManagementForm routingTableMgmtForm = (RoutingTableManagementForm) form;
					String[] routingEntries = request.getParameterValues("routingEntryId");
					MCCMNCRoutingTableBLManager routingBLManager = new MCCMNCRoutingTableBLManager();
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					routingBLManager.changeRoutingEntryOrder(routingEntries,staffData,MANAGE_ROUTING_ENTRY_ORDER_ALIAS);
					List<RoutingTableData> routingTableDataList = routingBLManager.getRoutingTableDataList();
					
					routingTableMgmtForm.setRoutingTableDataList(routingTableDataList);
					request.setAttribute("routingTableManagementForm", routingTableMgmtForm);
					ActionMessage message = new ActionMessage("routingentry.reorder.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					 request.setAttribute("responseUrl","/routingTableManagement.do?method=initSearch");
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				} catch (Exception e) {
					Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", new ActionMessage("routingentry.reorder.faliure"));
		            saveErrors(request, messages);
		            return mapping.findForward(FAILURE);
				}
			}else{
				Logger.logWarn(MODULE,"No Access On this Operation.");
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
	}
	
	@Override
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in  Create Method");
		if(checkAccess(request,CREATE_ACTION_ALIAS)){
			try {
				RoutingTableManagementForm routingTableMgmtForm = (RoutingTableManagementForm)form;
				RoutingTableData routingTableData = new RoutingTableData();
				MCCMNCRoutingTableBLManager routingTableBlManager=new MCCMNCRoutingTableBLManager();
				convertFormToBean(routingTableData, routingTableMgmtForm);
				List<RoutingTableGatewayRelData> routingGatewayRelDataList=new ArrayList<RoutingTableGatewayRelData>();
				String[] strgatewayIds = request.getParameterValues("gatewayId");
				if(routingTableMgmtForm.getAction().equals(RoutingEntryConstants.ROUTING_ACTION_PROXY)){//for local routing action need to skip gateway entry 0=LOCAL & 2=PROXY
					if(strgatewayIds != null) {
						for(int i=0; i<strgatewayIds.length; i++) {
							RoutingTableGatewayRelData rotuingTableGatewayRel=new RoutingTableGatewayRelData();
							rotuingTableGatewayRel.setGatewayId(Long.parseLong(strgatewayIds[i]));	
							routingGatewayRelDataList.add(rotuingTableGatewayRel);
						}
					}
					String[] weightage = request.getParameterValues("weightage");
					if(weightage!=null){
						int i=0;
						for(String weight:weightage){
							routingGatewayRelDataList.get(i).setWeightage(Long.parseLong(weight));
							i++;	
						}
					}
				}
			    routingTableData.setOrderNumber(1);
			    routingTableData.setRoutingTableGatewayRelData(routingGatewayRelDataList);
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				routingTableBlManager.create(routingTableData, staffData, CREATE_ACTION_ALIAS);
				ActionMessage message = new ActionMessage("routingentry.create.success",routingTableMgmtForm.getName());
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				request.setAttribute("responseUrl","/routingTableManagement.do?method=initSearch");
				return mapping.findForward(SUCCESS);
			} catch (Exception e) {
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("routingentry.create.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("routingentry.error.heading","creating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
	            
	            return mapping.findForward(FAILURE);
			}
		}
		else{
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	
	@Override
	public ActionForward delete(ActionMapping mapping, ActionForm form, HttpServletRequest request,
			HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Delete Method");
		if((checkAccess(request, DELETE_ACTION_ALIAS))){
			try{
				MCCMNCRoutingTableBLManager routingBLManager = new MCCMNCRoutingTableBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				String[] strRoutingEntries =request.getParameterValues("routingTableId");
				Long[] routingEntries = convertStringIdsToLong(strRoutingEntries);  
				routingBLManager.delete(routingEntries,staffData,DELETE_ACTION_ALIAS);
				ActionMessage message = new ActionMessage("routingentry.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/routingTableManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("routingentry.delete.failure"));
	            saveErrors(request, messages);
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("routingentry.error.heading","deleting");
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
	        message = new ActionMessage("routingentry.error.heading","deleting");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);

			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in View Method");
			RoutingTableManagementForm routingTableMgmtForm = (RoutingTableManagementForm)form;
			
			try{
				MCCMNCRoutingTableBLManager routingBLManager=new MCCMNCRoutingTableBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				RoutingTableData routingData=routingBLManager.getRoutingTableData(routingTableMgmtForm.getRoutingTableId());
				List<RoutingTableGatewayRelData> routingGatewayRelList=routingBLManager.getRoutingGateWayRelList(routingData.getRoutingTableId());	
				routingData.setRoutingTableGatewayRelData(routingGatewayRelList);
 				convertBeanToForm(routingTableMgmtForm, routingData);
 				request.setAttribute("routingTableData", routingData);
				request.setAttribute("routingTableMgmtForm", routingTableMgmtForm);
				return mapping.findForward(VIEW_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("routingentry.view.failure"));
	            saveErrors(request, messages);
	            
	            ActionMessages errorHeadingMessage = new ActionMessages();
	            ActionMessage message = new ActionMessage("routingentry.error.heading","viewing");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
	            
	            return mapping.findForward(FAILURE);
	        }
	}

	private void convertBeanToForm(RoutingTableManagementForm routingTableMgmtForm,
			RoutingTableData routingData) {
                  routingTableMgmtForm.setRoutingTableId(routingData.getRoutingTableId());
                  routingTableMgmtForm.setName(routingData.getName());
                  routingTableMgmtForm.setType(routingData.getType());
                  routingTableMgmtForm.setAction(Long.toString(routingData.getRoutingAction()));
                  routingTableMgmtForm.setRealmcondition(routingData.getRealmCondition());
                  routingTableMgmtForm.setMccmncGroupId(routingData.getMccmncGroupId());
                  routingTableMgmtForm.setRoutingTableGatewayRelData(routingData.getRoutingTableGatewayRelData());
                
                  
		
	}
	
	private void convertFormToBean(RoutingTableData routingTableData,RoutingTableManagementForm routingTableMgmtForm){
		routingTableData.setRoutingTableId(routingTableMgmtForm.getRoutingTableId());
		routingTableData.setName(routingTableMgmtForm.getName());
		routingTableData.setType(routingTableMgmtForm.getType());
		if(routingTableData.getType().equals(RoutingEntryConstants.ROUTING_TYPE_MCCMNC)){
		routingTableData.setMccmncGroupId(routingTableMgmtForm.getMccmncGroupId());
		}
		if(routingTableData.getType().equals(RoutingEntryConstants.ROUTING_TYPE_REALM)){
		routingTableData.setRealmCondition(routingTableMgmtForm.getRealmcondition());
		}
		routingTableData.setRoutingAction(Long.valueOf(routingTableMgmtForm.getAction()));
	}
	
	
	
}
