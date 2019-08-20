package com.elitecore.netvertexsm.web.locationconfig.city;

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
import com.elitecore.netvertexsm.blmanager.locationconfig.area.AreaBLManager;
import com.elitecore.netvertexsm.blmanager.locationconfig.city.CityBLManger;
import com.elitecore.netvertexsm.blmanager.locationconfig.region.RegionBLManager;
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.CountryData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData;
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
import com.elitecore.netvertexsm.web.locationconfig.city.form.CityMgmtForm;

public class CityMgmtAction extends   BaseWebDispatchAction implements ICreateAction, IUpdateAction, ISearchAction, IDeleteAction  {
 
	private static final String MODULE = "CITY-MANAGEMENT";
	
	private static final String LIST_FORWARD = "searchCityPage";
	private static final String CREATE_PAGE = "createCityPage";
	private static final String VIEW_PAGE = "viewCityPage";
	private static final String EDIT_PAGE = "editCityPage";
	
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_CITY;;
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_CITY;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_CITY;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_CITY;
	private static final String VIEW_ACTION_ALIAS = ConfigConstant.VIEW_CITY;
	
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
		Logger.logInfo(MODULE,"Entered in search method of "+getClass().getName());

		if(checkAccess(request, SEARCH_ACTION_ALIAS)){
			try{
				CityMgmtForm searchCityForm = (CityMgmtForm) form;
				CityData cityData=new CityData();
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(searchCityForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo = 1;
               
				String strName = searchCityForm.getCityName();
				if(strName != null && strName.length() > 0){
					cityData.setCityName(strName);
				}else{
					searchCityForm.setCityName("");
				}
				
				Long regionId=searchCityForm.getRegionId();				
				if(regionId!=null&&regionId>0){
					cityData.setRegionId(regionId);
				}

				Long countyId=searchCityForm.getCountryId();
				RegionData regionData = new RegionData(); 
				if(countyId!=null&&countyId>0){
					regionData.setCountryId(countyId);
					cityData.setRegion(regionData);
				}
				
				NetworkBLManager mccmncBlManager=new NetworkBLManager();
				RegionBLManager regionBlManager=new RegionBLManager();
				CityBLManger cityBlManager=new CityBLManger();
				List<CountryData> countryList=mccmncBlManager.getCountryDataList();
				searchCityForm.setCountryList(countryList);
				List<RegionData> regionList=regionBlManager.getRegionDataList();
				searchCityForm.setRegionList(regionList);
				String actionAlias = SEARCH_ACTION_ALIAS;
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				PageList pageList = cityBlManager.search(cityData, requiredPageNo,pageSize, staffData, actionAlias);
				searchCityForm.setCityList(pageList.getListData());
				searchCityForm.setRegionList(regionList);
				searchCityForm.setPageNumber(pageList.getCurrentPage());
				searchCityForm.setTotalPages(pageList.getTotalPages());
				searchCityForm.setTotalRecords(pageList.getTotalItems());
				searchCityForm.setCityName(searchCityForm.getCityName());
				searchCityForm.setAction(BaseConstant.LISTACTION);
				
				request.setAttribute("searchCityForm", searchCityForm);

			}catch(Exception e){
				Logger.logError(MODULE, "Returning error forward from " + getClass().getName());
				Logger.logTrace(MODULE, e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				messages.add("information", new ActionMessage("city.search.failure"));
				saveErrors(request, messages);
				
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("city.error.heading","searching");
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
	        message = new ActionMessage("city.error.heading","searching");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);			
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}


	
	@Override
	public ActionForward delete(ActionMapping mapping, ActionForm form,
			HttpServletRequest request, HttpServletResponse response)
			throws Exception {
		Logger.logDebug(MODULE, "Entered in Delete Method");
		if((checkAccess(request, DELETE_ACTION_ALIAS))){
			try{
				CityBLManger cityBlManager=new CityBLManger();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String[] strCityIds =request.getParameterValues("cityId");
				Long[] cityIds = convertStringIdsToLong(strCityIds);  
				cityBlManager.delete(cityIds,staffData,DELETE_ACTION_ALIAS);

				ActionMessage message = new ActionMessage("city.delete.success");
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","cityManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("city.delete.failure"));
	            messages.add("information", new ActionMessage("city.associated.with.location"));	            
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("city.error.heading","deleting");
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
	        message = new ActionMessage("city.error.heading","deleting");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}



		@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
				Logger.logDebug(MODULE, "Entered in Init Update Method");
			
			if((checkAccess(request,UPDATE_ACTION_ALIAS))){
				CityMgmtForm cityManagementForm = (CityMgmtForm)form;
				try{
					NetworkBLManager mccMNCMgmtBLManager = new NetworkBLManager();
					RegionBLManager  regionBlManager=new RegionBLManager();
					List<RegionData> regionList=regionBlManager.getRegionDataList();
					List<CountryData> countryList=mccMNCMgmtBLManager.getCountryDataList();
					cityManagementForm.setCountryList(countryList);
					cityManagementForm.setRegionList(regionList);
					CityBLManger 	cityBlManger = new CityBLManger();
					CityData 		cityData	 = cityBlManger.getCityData(cityManagementForm.getCityId());
					
					cityManagementForm.setCityId(cityData.getCityId());
					cityManagementForm.setRegionId(cityData.getRegion().getRegionId());
				    cityManagementForm.setCityName(cityData.getCityName());
				    cityManagementForm.setCountryId(cityData.getRegion().getCountryId());
					request.setAttribute("cityData",cityData );
					request.setAttribute("cityManagementForm", cityManagementForm);
					request.setAttribute("hideTable", false);
					
	               	return mapping.findForward(EDIT_PAGE);
	               	
				}catch(Exception e){
					
					Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
					Logger.logTrace(MODULE,e);
					Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
					request.setAttribute("errorDetails", errorElements);
		            ActionMessages messages = new ActionMessages();
		            messages.add("information", new ActionMessage("city.update.failure"));
		            saveErrors(request, messages);
			        ActionMessages errorHeadingMessage = new ActionMessages();
			        ActionMessage message = new ActionMessage("city.error.heading","updating");
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
		        message = new ActionMessage("city.error.heading","updating");
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
				CityMgmtForm cityManagementForm = (CityMgmtForm)form;
				try{
				CityBLManger cityBlManager=new CityBLManger();
				CityData cityData=new CityData();	
				cityData.setCityName(cityManagementForm.getCityName().trim());
				cityData.setCityId(cityManagementForm.getCityId());
				RegionData regionData = new RegionData();
				regionData.setRegionId(cityManagementForm.getRegionId());
				cityData.setRegionId(cityManagementForm.getRegionId());				
				cityData.setRegion(regionData);
				cityData.setRegionId(cityManagementForm.getRegionId());
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				cityBlManager.update(cityData,staffData, UPDATE_ACTION_ALIAS);
               	ActionMessage message = new ActionMessage("city.update.success",cityData.getCityName());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","cityManagement.do?method=initSearch");
				return mapping.findForward(SUCCESS);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("city.update.failure"));
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("city.error.heading","updating");
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
	        message = new ActionMessage("city.error.heading","updating");
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
			CityMgmtForm cityManagementForm = (CityMgmtForm)form;
			NetworkBLManager mccMNCMgmtBLManager = new NetworkBLManager();
			RegionBLManager  regionBlManager=new RegionBLManager();
			List<RegionData> regionList=regionBlManager.getRegionDataList();
			List<CountryData> countryList=mccMNCMgmtBLManager.getCountryDataList();
			cityManagementForm.setCountryList(countryList);
			cityManagementForm.setRegionList(regionList);
			request.setAttribute("cityManagementForm", cityManagementForm);
			return mapping.findForward(CREATE_PAGE);
		}else{
			Logger.logWarn(MODULE,"No Access On this Operation.");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("city.error.heading","creating");
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
				CityMgmtForm cityManagementForm = (CityMgmtForm)form;
				long regionId=cityManagementForm.getRegionId();
				String[]   strCityNames=request.getParameterValues("city");
				CityBLManger cityBlManager=new CityBLManger();
				List<CityData> cityDataList=new ArrayList<CityData>();
				if( strCityNames != null) {
					for(int i=0; i<strCityNames.length; i++) {
						CityData city=new CityData();
						city.setCityName(strCityNames[i].trim());
						city.setRegionId(regionId);
						cityDataList.add(city);
					}
				}    
				IStaffData staffData = getStaffObject(((SystemLoginForm) request.getSession().getAttribute("radiusLoginForm")));
				cityBlManager.createCityByList(cityDataList,staffData,CREATE_ACTION_ALIAS);
				ActionMessage message = new ActionMessage("city.create.success");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveMessages(request, messages);
				request.setAttribute("responseUrl","/cityManagement.do?method=initSearch");
				return mapping.findForward(SUCCESS);
			} catch (Exception e) {
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("city.create.failure"));
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("city.error.heading","creating");
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
	        message = new ActionMessage("city.error.heading","creating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	
	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in View Method");
		if((checkAccess(request, SEARCH_ACTION_ALIAS))){
			CityMgmtForm cityManagementForm = (CityMgmtForm)form;
			try{
				CityBLManger cityBlManager=new CityBLManger();
				CityData cityData=cityBlManager.getCityData(cityManagementForm.getCityId());
				AreaBLManager locationBlManager=new AreaBLManager();
				List<AreaData> areaList=locationBlManager.getAreaByCity(cityData.getCityId());
				cityData.setAreaList(areaList);
				request.setAttribute("cityData", cityData);
				request.setAttribute("areaList", areaList);				
				request.setAttribute("cityManagementForm", cityManagementForm);
				return mapping.findForward(VIEW_PAGE);
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("city.view.failure"));
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("city.error.heading","viewing");
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
	        message = new ActionMessage("city.error.heading","viewing");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);	            
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}
	

}
