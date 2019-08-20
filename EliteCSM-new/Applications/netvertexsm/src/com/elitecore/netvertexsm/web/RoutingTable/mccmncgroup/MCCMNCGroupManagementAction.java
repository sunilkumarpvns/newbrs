package com.elitecore.netvertexsm.web.RoutingTable.mccmncgroup;

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
import com.elitecore.netvertexsm.blmanager.RoutingTable.network.NetworkBLManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCCodeGroupRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncgroup.data.MCCMNCGroupData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.mccmncroutingtable.data.RoutingTableData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.BrandOperatorRelData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.OperatorData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.util.constants.BaseConstant;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.RoutingTable.mccmncgroup.form.MCCMNCGroupManagementForm;
import com.elitecore.netvertexsm.web.core.base.BaseWebDispatchAction;
import com.elitecore.netvertexsm.web.core.base.ICreateAction;
import com.elitecore.netvertexsm.web.core.base.IDeleteAction;
import com.elitecore.netvertexsm.web.core.base.ISearchAction;
import com.elitecore.netvertexsm.web.core.base.IUpdateAction;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;

public class MCCMNCGroupManagementAction extends BaseWebDispatchAction implements ISearchAction,ICreateAction,IDeleteAction,IUpdateAction {
	private static final String MODULE = "MCC-MNC-GROUP-MANAGEMENT";
	
	private static final String LIST_FORWARD = "searchmccmncGroup";
	private static final String CREATE_PAGE = "createmccmncGroup";
	private static final String VIEW_PAGE = "viewmccmncGroup";
	private static final String EDIT_PAGE = "editmccmncGroup";
	
	
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_MCCMNC_GROUP;
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_MCCMNC_GROUP;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_MCCMNC_GROUP;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_MCCMNC_GROUP;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_MCCMNC_GROUP;

	@Override
	public ActionForward initSearch(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Search Method");
		return search( mapping,  form,  request,  response);
	}

	public ActionForward search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter search method of "+getClass().getName());

			try{
				MCCMNCGroupManagementForm searchMCCMNCGroupForm = (MCCMNCGroupManagementForm) form;
				MCCMNCGroupBLManager mccmncGroupBLManager = new MCCMNCGroupBLManager();
				NetworkBLManager mccMNCMgmtBLManager = new NetworkBLManager();
				
				MCCMNCGroupData mccmncGroupData = new MCCMNCGroupData();
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(searchMCCMNCGroupForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo = 1;

				String strName = searchMCCMNCGroupForm.getName();
				if(strName != null && strName.length() > 0){
					mccmncGroupData.setName(strName);
				}else{
					searchMCCMNCGroupForm.setName("");
				}
				Long brandId=searchMCCMNCGroupForm.getBrandID();
				List<BrandOperatorRelData> brandOperaList=mccMNCMgmtBLManager.getBrandOperatorDataRelList();
				if(brandId!=null&&brandId>0){
			    mccmncGroupData.setBrandId(brandId);
				}	
				searchMCCMNCGroupForm.setBrandOperatorRelList(brandOperaList);
				String actionAlias = SEARCH_ACTION_ALIAS;
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = mccmncGroupBLManager.search(mccmncGroupData, requiredPageNo,pageSize, staffData, actionAlias);
				searchMCCMNCGroupForm.setMccmncGroupDataList(pageList.getListData());
				searchMCCMNCGroupForm.setPageNumber(pageList.getCurrentPage());
				searchMCCMNCGroupForm.setTotalPages(pageList.getTotalPages());
				searchMCCMNCGroupForm.setTotalRecords(pageList.getTotalItems());
				searchMCCMNCGroupForm.setName(searchMCCMNCGroupForm.getName());
				searchMCCMNCGroupForm.setAction(BaseConstant.LISTACTION);
				request.setAttribute("searchMCCMNCGroupForm", searchMCCMNCGroupForm);

			}catch(Exception e){
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("mccmncgroup.search.failure"));
				saveErrors(request, messages);
				
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("mccmncgroup.error.heading","searching");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
			}
			return mapping.findForward(LIST_FORWARD);
	}


	@Override
	public ActionForward initCreate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
					throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Create Method");		
		if(checkAccess(request, CREATE_ACTION_ALIAS)){
			MCCMNCGroupManagementForm mccMNCGroupManagmentForm = (MCCMNCGroupManagementForm)form;
			NetworkBLManager mccMNCMgmtBLManager = new NetworkBLManager();
			List<OperatorData> operatorDataList = mccMNCMgmtBLManager.getOperatorDataList();
			List<BrandData> brandDataList=mccMNCMgmtBLManager.getBrandDataList();
			List<BrandOperatorRelData> brandOperatorRelDataList=mccMNCMgmtBLManager.getBrandOperatorDataRelList();
			String restrictionSqlQuery="NETWORKID NOT IN (select NETWORKID From TBLMMCCMNCCODEGROUPREL)";
			List<NetworkData> mccmncCodesDataList=mccMNCMgmtBLManager.getNetworkDataList(restrictionSqlQuery);
			mccMNCGroupManagmentForm.setBrandOperatorRelList(brandOperatorRelDataList);
			mccMNCGroupManagmentForm.setOperatorDataList(operatorDataList);
			mccMNCGroupManagmentForm.setBrandDataList(brandDataList);
			mccMNCGroupManagmentForm.setMccmncCodesDataList(mccmncCodesDataList);
			mccMNCGroupManagmentForm.setDescription(getDefaultDescription(request));
			request.setAttribute("mccMNCGroupManagmentForm", mccMNCGroupManagmentForm);
			return mapping.findForward(CREATE_PAGE);
		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("mccmncgroup.error.heading","creating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);

		}

	}

	@Override
	public ActionForward create(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in  Create Method");
		if(checkAccess(request,CREATE_ACTION_ALIAS)){
			try {
				MCCMNCGroupManagementForm mccmncGroupManagmentForm = (MCCMNCGroupManagementForm) form;
				MCCMNCGroupData mccmncGroup = new MCCMNCGroupData();
				MCCMNCGroupBLManager mccmncGroupBLManager = new MCCMNCGroupBLManager();
				convertFormToBean(mccmncGroupManagmentForm, mccmncGroup);
				List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList=new ArrayList<MCCMNCCodeGroupRelData>();
				
				String[] strmccmcnCodeId = request.getParameterValues("mccmncCode");
				if(strmccmcnCodeId != null) {
					for(int i=0; i<strmccmcnCodeId.length; i++) {
						MCCMNCCodeGroupRelData CodeGroupMapData=new MCCMNCCodeGroupRelData();
						CodeGroupMapData.setMccMNCID(Long.parseLong(strmccmcnCodeId[i]));	
						mccmncCodeGroupRelDataList.add(CodeGroupMapData);
					}
				}    
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
	            mccmncGroup.setMccmncCodeGroupRelDataList(mccmncCodeGroupRelDataList);  		
				mccmncGroupBLManager.create(mccmncGroup, staffData,CREATE_ACTION_ALIAS);
				ActionMessage message = new ActionMessage("mccmncgroup.create.success",mccmncGroup.getName());
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				request.setAttribute("responseUrl","/mccmncGroupManagement.do?method=initSearch");
				return mapping.findForward(SUCCESS);
			} catch (Exception e) {
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("mccmncgroup.create.failure"));
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("mccmncgroup.error.heading","creating");
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
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("mccmncgroup.error.heading","creating");
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
				MCCMNCGroupBLManager mccmncGroupBLManager = new MCCMNCGroupBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String[] strMccMncGroupIds =request.getParameterValues("mccmncGroupId");
				Long[] mccmncGroupIDS = convertStringIdsToLong(strMccMncGroupIds);  
				mccmncGroupBLManager.delete(mccmncGroupIDS,staffData,DELETE_ACTION_ALIAS);

				ActionMessage message = new ActionMessage("mccmncgroup.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","mccmncGroupManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("mccmncgroup.delete.failure"));
	            messages.add("information", new ActionMessage("mccmncgroup.delete.failure.reason"));
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("mccmncgroup.error.heading","deleting");
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
	        message = new ActionMessage("mccmncgroup.error.heading","deleting");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Update Method");
		if((checkAccess(request,UPDATE_ACTION_ALIAS))){
			MCCMNCGroupManagementForm mccMNCGroupManagmentForm = (MCCMNCGroupManagementForm)form;
			try{
				MCCMNCGroupBLManager mccmncGroupBLManager = new MCCMNCGroupBLManager();
				MCCMNCGroupData mccmncGroupData=mccmncGroupBLManager.getMCCMNCGroupData(mccMNCGroupManagmentForm.getMccmncGroupId());
				List<MCCMNCCodeGroupRelData> mccmncCodeRelDataList=mccmncGroupBLManager.getMCCMNCCodeGroupRelDataList(mccmncGroupData.getMccmncGroupId());
				
				mccmncGroupData.setMccmncCodeGroupRelDataList(mccmncCodeRelDataList);
				NetworkBLManager mccMNCMgmtBLManager = new NetworkBLManager();
				String restrictionSqlQuery="NETWORKID NOT IN (select NETWORKID From TBLMMCCMNCCODEGROUPREL)";
				List<NetworkData> mccmncCodeDataList=mccMNCMgmtBLManager.getNetworkDataList(restrictionSqlQuery);
				for(MCCMNCCodeGroupRelData tempRel:mccmncCodeRelDataList){
					mccmncCodeDataList.add(tempRel.getMccmncCodeData());
				}
				List<BrandOperatorRelData> brandOperatorRelDataList=mccMNCMgmtBLManager.getBrandOperatorDataRelList();
				mccMNCGroupManagmentForm.setBrandOperatorRelList(brandOperatorRelDataList);
				List<BrandData> brandDataList = mccMNCMgmtBLManager.getBrandDataList();
				List<OperatorData> operatorDataList = mccMNCMgmtBLManager.getOperatorDataList();
				mccMNCGroupManagmentForm.setMccmncCodesDataList(mccmncCodeDataList);
				mccMNCGroupManagmentForm.setBrandDataList(brandDataList);
				mccMNCGroupManagmentForm.setOperatorDataList(operatorDataList);
				convertBeanToForm(mccMNCGroupManagmentForm, mccmncGroupData);
				request.setAttribute("mccmncGroupData",mccmncGroupData );
				request.setAttribute("mccMNCGroupManagmentForm", mccMNCGroupManagmentForm);
				request.setAttribute("hideTable", false);
               	return mapping.findForward(EDIT_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("mccmncgroup.update.failure"));
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("mccmncgroup.error.heading","updating");
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
	        message = new ActionMessage("mccmncgroup.error.heading","updating");
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
			MCCMNCGroupManagementForm mccMNCGroupManagmentForm = (MCCMNCGroupManagementForm)form;
			
			try{
				MCCMNCGroupBLManager mccmncGroupBLManager = new MCCMNCGroupBLManager();
				MCCMNCGroupData mccmncGroupData=new MCCMNCGroupData();
				String[]  mccmncCodeIds=request.getParameterValues("mccmncCode");
				List<MCCMNCCodeGroupRelData> mccmnCodeGroupRelDataList=new ArrayList<MCCMNCCodeGroupRelData>();
				if(mccmncCodeIds!=null){
					for( String temp:mccmncCodeIds){
						MCCMNCCodeGroupRelData codeGroupRelData=new MCCMNCCodeGroupRelData();
						codeGroupRelData.setMccMNCID(Long.parseLong(temp));
						codeGroupRelData.setMccmncGroupId(mccMNCGroupManagmentForm.getMccmncGroupId());
						mccmnCodeGroupRelDataList.add(codeGroupRelData);
					}
				}
				mccmncGroupData.setMccmncCodeGroupRelDataList(mccmnCodeGroupRelDataList);
				convertFormToBean(mccMNCGroupManagmentForm, mccmncGroupData);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				mccmncGroupBLManager.update(mccmncGroupData,staffData, UPDATE_ACTION_ALIAS);
               	ActionMessage message = new ActionMessage("mccmncgroup.update.success",mccmncGroupData.getName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","mccmncGroupManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("mccmncgroup.update.failure"));
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("mccmncgroup.error.heading","updating");
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
	        message = new ActionMessage("mccmncgroup.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in View Method");
			MCCMNCGroupManagementForm mccMNCGroupManagmentForm = (MCCMNCGroupManagementForm)form;
			try{
				MCCMNCGroupBLManager mccmncGroupBLManager = new MCCMNCGroupBLManager();
				NetworkBLManager mccmncMgmtBLManager=new NetworkBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				MCCMNCGroupData mccmncGroupData=mccmncGroupBLManager.getMCCMNCGroupData(mccMNCGroupManagmentForm.getMccmncGroupId(),staffData,VIEW_ACTION_ALIAS);
				List<MCCMNCCodeGroupRelData> mccmncCodeGroupRelDataList=mccmncGroupBLManager.getMCCMNCCodeGroupRelDataList(mccmncGroupData.getMccmncGroupId());	
				List<BrandOperatorRelData> brandOperatorRelDataList=mccmncMgmtBLManager.getBrandOperatorDataRelList();
				mccmncGroupData.setMccmncCodeGroupRelDataList(mccmncCodeGroupRelDataList);
 				convertBeanToForm(mccMNCGroupManagmentForm, mccmncGroupData);
 				
 				MCCMNCRoutingTableBLManager mccmncRoutingTableBLManager=new MCCMNCRoutingTableBLManager();
 			    RoutingTableData routingTableData=mccmncRoutingTableBLManager.getRoutingTableDataByMCCMNCGroup(mccmncGroupData.getMccmncGroupId());
 				
 				request.setAttribute("routingTableData",routingTableData);
				request.setAttribute("mccmncGroupData", mccmncGroupData);
				request.setAttribute("mccMNCGroupManagmentForm", mccMNCGroupManagmentForm);
				return mapping.findForward(VIEW_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("mccmncgroup.view.failure"));
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("mccmncgroup.error.heading","viewing");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);	            
	            return mapping.findForward(FAILURE);
	        }
	}
	
	
	private void convertBeanToForm(
			MCCMNCGroupManagementForm form,
			MCCMNCGroupData data) {
			form.setMccmncGroupId(data.getMccmncGroupId());
			form.setName(data.getName());
			form.setBrandID(data.getBrandId());
			form.setDescription(data.getDescription());
    }
	
	private void convertFormToBean(MCCMNCGroupManagementForm form,MCCMNCGroupData data){
	     data.setName(form.getName());
	     data.setDescription(form.getDescription());
	     data.setBrandId(form.getBrandID());
		 data.setMccmncGroupId(form.getMccmncGroupId());
		 data.setDescription(form.getDescription());
		}
	
}
