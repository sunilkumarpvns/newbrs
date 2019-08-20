package com.elitecore.netvertexsm.web.locationconfig.region;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.netvertexsm.blmanager.RoutingTable.network.NetworkBLManager;
import com.elitecore.netvertexsm.blmanager.locationconfig.city.CityBLManger;
import com.elitecore.netvertexsm.blmanager.locationconfig.region.RegionBLManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.locationconfig.city.data.CityData;
import com.elitecore.netvertexsm.datamanager.locationconfig.region.data.RegionData;
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
import com.elitecore.netvertexsm.web.locationconfig.region.form.RegionMgmtForm;



public class RegionMgmtAction extends BaseWebDispatchAction implements ISearchAction,ICreateAction,IDeleteAction,IUpdateAction {
	
		private static final String MODULE = "REGION-MANAGEMENT";
		
		private static final String LIST_FORWARD = "searchRegionPage";
		private static final String CREATE_PAGE = "createRegionPage";
		private static final String VIEW_PAGE = "viewRegionPage";
		private static final String EDIT_PAGE = "editRegionPage";
		
		
		private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_REGION;
		private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_REGION;
		private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_REGION;
		private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_REGION;
		private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_REGION;

		@Override
		public ActionForward initSearch(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response)
				throws Exception {
			Logger.logDebug(MODULE, "Entered in Init Search Method");
			return search( mapping,  form,  request,  response);
		}

		public ActionForward search(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
			Logger.logInfo(MODULE,"Enter search method of "+getClass().getName());

			if(checkAccess(request, SEARCH_ACTION_ALIAS)){
				try{
					RegionMgmtForm searchRegionForm = (RegionMgmtForm) form;
					RegionData	regionData	=new RegionData();
					Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
					int requiredPageNo;
					if(request.getParameter("pageNo") != null){
						requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
					}else{
						requiredPageNo = new Long(searchRegionForm.getPageNumber()).intValue();
					}
					if(requiredPageNo == 0)
						requiredPageNo = 1;

					String strName = searchRegionForm.getRegionName();
					if(strName != null && strName.length() > 0){
						regionData.setRegionName(strName);
					}else{
						searchRegionForm.setRegionName("");
					}
					Long countryId=searchRegionForm.getCountryId();
					if(countryId!=null&&countryId>0){
				    regionData.setCountryId(countryId);
					}
					NetworkBLManager mccmncBlManager=new NetworkBLManager();
					List<CountryData> countryList=mccmncBlManager.getCountryDataList();
					searchRegionForm.setCountryList(countryList);
					String actionAlias = SEARCH_ACTION_ALIAS;
					IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
					RegionBLManager regionBlManager=new RegionBLManager();
					PageList pageList = regionBlManager.search(regionData, requiredPageNo,pageSize, staffData, actionAlias);
					searchRegionForm.setRegionDataList(pageList.getListData());
					searchRegionForm.setPageNumber(pageList.getCurrentPage());
					searchRegionForm.setTotalPages(pageList.getTotalPages());
					searchRegionForm.setTotalRecords(pageList.getTotalItems());
					searchRegionForm.setRegionName(searchRegionForm.getRegionName());
					searchRegionForm.setAction(BaseConstant.LISTACTION);
					request.setAttribute("searchRegionForm", searchRegionForm);

				}catch(Exception e){
					Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
					Logger.logTrace(MODULE, e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
					ActionMessages messages = new ActionMessages();
					messages.add("information", new ActionMessage("region.search.failure"));
					saveErrors(request, messages);
					
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        ActionMessage message = new ActionMessage("region.error.heading","searching");
			        errorHeadingMessage.add("errorHeading",message);
			        saveMessages(request,errorHeadingMessage);
				}
				return mapping.findForward(LIST_FORWARD);
			}else{
				Logger.logWarn(MODULE, "No Access On this Operation.");
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("region.error.heading","searching");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);			
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
		}


		@Override
		public ActionForward initCreate(ActionMapping mapping, ActionForm form,
				HttpServletRequest request, HttpServletResponse response)
						throws Exception {
			Logger.logDebug(MODULE, "Entered in Init Create Method");		
			if(checkAccess(request, CREATE_ACTION_ALIAS)){
				RegionMgmtForm regionMgmtForm = (RegionMgmtForm)form;
				NetworkBLManager mccMNCMgmtBLManager = new NetworkBLManager();
				List<CountryData> countryList=mccMNCMgmtBLManager.getCountryDataList();
				regionMgmtForm.setCountryList(countryList);
				request.setAttribute("regionMgmtForm", regionMgmtForm);
				return mapping.findForward(CREATE_PAGE);
			}else{
				Logger.logWarn(MODULE,"No Access On this Operation.");
				ActionMessage message = new ActionMessage("general.user.restricted");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("region.error.heading","creating");
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
					RegionMgmtForm regionMgmtForm = (RegionMgmtForm)form;
					long countryId=regionMgmtForm.getCountryId();
					String[]   strRegionNames=request.getParameterValues("region");
					RegionBLManager regionBLManager=new RegionBLManager();
					List<RegionData> regionDataList=new ArrayList<RegionData>();
					if( strRegionNames != null) {
						for(int i=0; i<strRegionNames.length; i++) {
							RegionData regionData=new RegionData();
							regionData.setRegionName(strRegionNames[i].trim());
							regionData.setCountryId(countryId);
							regionDataList.add(regionData);
						}
					}    
					IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
					regionBLManager.createRegionByList(regionDataList,staffData,CREATE_ACTION_ALIAS);
					ActionMessage message = new ActionMessage("region.create.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information", message);
					saveMessages(request, messages);
					request.setAttribute("responseUrl","/regionManagement.do?method=initSearch");
					return mapping.findForward(SUCCESS);
				} catch (Exception e) {
					Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", new ActionMessage("region.create.failure"));
		            saveErrors(request, messages);
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        ActionMessage message = new ActionMessage("region.error.heading","creating");
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
		        message = new ActionMessage("region.error.heading","creating");
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
					RegionBLManager regionBlManager=new RegionBLManager();
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String[] strRegionIds =request.getParameterValues("regionId");
					Long[] regionIds = convertStringIdsToLong(strRegionIds);  
					regionBlManager.delete(regionIds,staffData,DELETE_ACTION_ALIAS);
					ActionMessage message = new ActionMessage("region.delete.success");
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", message);
		            saveMessages(request,messages);
	               	request.setAttribute("responseUrl","regionManagement.do?method=initSearch");
	               	
					return mapping.findForward(SUCCESS);
				}catch(Exception e){
					Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", new ActionMessage("region.delete.failure"));
		            messages.add("information", new ActionMessage("region.associated.with.city"));		            
		            saveErrors(request, messages);
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        ActionMessage message = new ActionMessage("region.error.heading","deleting");
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
		        message = new ActionMessage("region.error.heading","deleting");
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
				RegionMgmtForm regionMgmtForm = (RegionMgmtForm)form;
				try{
					RegionBLManager regionBLManager=new RegionBLManager();
					RegionData  regionData=regionBLManager.getRegionData(regionMgmtForm.getRegionId());
					CityBLManger cityBlManger=new CityBLManger();
					List<CityData> cityList=cityBlManger.getCityDataList(regionData.getRegionId());
					regionData.setCityDataList(cityList);
					regionMgmtForm.setRegionId(regionData.getRegionId());
				    regionMgmtForm.setRegionName(regionData.getRegionName());
				    regionMgmtForm.setCountryId(regionData.getCountryId());
			    
					request.setAttribute("regionData",regionData );
					request.setAttribute("regionMgmtForm", regionMgmtForm);
					request.setAttribute("hideTable", false);
	               	return mapping.findForward(EDIT_PAGE);
				}catch(Exception e){
					Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", new ActionMessage("region.update.failure"));
		            saveErrors(request, messages);
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        ActionMessage message = new ActionMessage("region.error.heading","updating");
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
		        message = new ActionMessage("region.error.heading","updating");
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
				RegionMgmtForm regionMgmtForm = (RegionMgmtForm)form;
					try{
					RegionBLManager regionBlManager=new RegionBLManager();
					RegionData regionData=new RegionData();
					regionData.setRegionName(regionMgmtForm.getRegionName().trim());
					regionData.setRegionId(regionMgmtForm.getRegionId());
					regionData.setCountryId(regionMgmtForm.getCountryId());
					CountryData countryData = new CountryData();
					countryData.setCountryID(regionMgmtForm.getCountryId());
					regionData.setCountryData(countryData);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					regionBlManager.update(regionData,staffData, UPDATE_ACTION_ALIAS);
	               	ActionMessage message = new ActionMessage("region.update.success",regionData.getRegionName());
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", message);
		            saveMessages(request,messages);
	               	request.setAttribute("responseUrl","regionManagement.do?method=initSearch");
					return mapping.findForward(SUCCESS);
				}catch(Exception e){
					Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", new ActionMessage("region.update.failure"));
		            saveErrors(request, messages);
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        ActionMessage message = new ActionMessage("region.error.heading","updating");
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
		        message = new ActionMessage("region.error.heading","updating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
		}
		
		
		public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
			Logger.logDebug(MODULE, "Entered in View Method");
			if((checkAccess(request, SEARCH_ACTION_ALIAS))){
				RegionMgmtForm regionManagementForm = (RegionMgmtForm)form;
				try{
					RegionBLManager regionBlManager=new RegionBLManager();
					CityBLManger cityBlManager=new CityBLManger();
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					RegionData regionData=regionBlManager.getRegionData(regionManagementForm.getRegionId());
					List<CityData> cityDataList=cityBlManager.getCityDataList(regionData.getRegionId());
					regionData.setCityDataList(cityDataList);
					request.setAttribute("regionData", regionData);
					request.setAttribute("cityDataList", cityDataList);
					request.setAttribute("regionManagementForm", regionManagementForm);
					return mapping.findForward(VIEW_PAGE);
				}catch(Exception e){
					Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", new ActionMessage("region.view.failure"));
		            saveErrors(request, messages);
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        ActionMessage message = new ActionMessage("region.error.heading","viewing");
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
		        message = new ActionMessage("region.error.heading","viewing");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);	            
		        
				return mapping.findForward(INVALID_ACCESS_FORWARD);
			}
		}
		
		
		/*private void convertBeanToForm(MCCMNCGroupManagementForm form,MCCMNCGroupData data) {
				form.setMccmncGroupId(data.getMccmncGroupId());
				form.setName(data.getName());
				form.setBrandID(data.getBrandId());
				form.setDescription(data.getDescription());
	    }*/
		
		/*private void convertFormToBean(MCCMNCGroupManagementForm form,MCCMNCGroupData data){
		     data.setName(form.getName());
		     data.setDescription(form.getDescription());
		     data.setBrandId(form.getBrandID());
			 data.setMccmncGroupId(form.getMccmncGroupId());
			 data.setDescription(form.getDescription());
			}
		*/
	}


