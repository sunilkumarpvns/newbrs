package com.elitecore.netvertexsm.web.locationconfig.area;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
import com.elitecore.netvertexsm.datamanager.RoutingTable.network.data.NetworkData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.datamanager.core.util.PageList;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.AreaData;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.CallingStationInfoData;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.LacData;
import com.elitecore.netvertexsm.datamanager.locationconfig.area.data.WiFiCallingStationInfoData;
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
import com.elitecore.netvertexsm.web.locationconfig.area.form.AreaMgmtForm;

public class AreaMgmtAction extends BaseWebDispatchAction implements ICreateAction, IUpdateAction, ISearchAction, IDeleteAction {
	private static final String MODULE 	= "AreaMgmtAction";
	
	private static final String SEARCH_PAGE = "searchAreaPage";
	private static final String CREATE_PAGE = "createAreaPage";
	private static final String VIEW_PAGE 	= "viewAreaPage";
	private static final String EDIT_PAGE 	= "editAreaPage";
	
	
	private static final String SEARCH_ACTION_ALIAS = ConfigConstant.SEARCH_AREA;
	private static final String CREATE_ACTION_ALIAS = ConfigConstant.CREATE_AREA;
	private static final String UPDATE_ACTION_ALIAS = ConfigConstant.UPDATE_AREA;
	private static final String DELETE_ACTION_ALIAS = ConfigConstant.DELETE_AREA;
	private static final String VIEW_ACTION_ALIAS 	= ConfigConstant.VIEW_AREA;

	@Override
	public ActionForward initSearch(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Search Method");
		return search( mapping,  form,  request,  response);
	}

	@SuppressWarnings("unchecked")
	@Override
	public ActionForward search(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Search Method");
		
		if(checkAccess(request, SEARCH_ACTION_ALIAS)) {
			try{
				AreaMgmtForm 		areaMgmtForm 	= (AreaMgmtForm)form;
				AreaBLManager 		areaBLManager 	= new AreaBLManager();				
				AreaData 			areaData 		= new AreaData();
				
				Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
				int requiredPageNo;
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(areaMgmtForm.getPageNumber()).intValue();
				}
				if (requiredPageNo == 0)
					requiredPageNo = 1;
				
	            String area = areaMgmtForm.getArea();	            
	            if(area != null && area.length() > 0) {
	            	areaData.setArea(area);
	            }
	            
	            Long cityId = areaMgmtForm.getCityId();	            
	            if(cityId != null && cityId > 0) {
	            	
	            	CityData cityData =  new CityData();
	            	cityData.setCityId(cityId);	            	
	            	Long regionId = areaMgmtForm.getRegionId();
	            	cityData.setRegionId(regionId);
	            	
	            	RegionData regionData = new RegionData();
	            	Long countryId = areaMgmtForm.getCountryId();	            		            	
	            	regionData.setCountryId(countryId);
	            	cityData.setRegion(regionData);
	            	
	            	areaData.setCityData(cityData);
	            }
	            
				IStaffData 	staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				PageList 	pageList = areaBLManager.search(areaData, requiredPageNo, pageSize,staffData, SEARCH_ACTION_ALIAS);				
				
				NetworkBLManager networkBLManager = new NetworkBLManager();
				List<CountryData> 	listCountryData = networkBLManager.getCountryDataList();
				CityBLManger 		cityBlManeger 	= new CityBLManger();
				List<CityData> 		listCityData 	= cityBlManeger.getCityDataList();
				RegionBLManager 	regionBlManger 	= new RegionBLManager();
				List<RegionData> 	listRegionData 	= regionBlManger.getRegionDataList();
				areaMgmtForm.setListCityData(listCityData);
				areaMgmtForm.setListRegionData(listRegionData);
				areaMgmtForm.setListCountryData(listCountryData);
				
				areaMgmtForm.setAreaDataList(pageList.getListData());
				areaMgmtForm.setPageNumber(pageList.getCurrentPage());
				areaMgmtForm.setTotalPages(pageList.getTotalPages());
				areaMgmtForm.setTotalRecords(pageList.getTotalItems());
				areaMgmtForm.setActionName(BaseConstant.LISTACTION);
			    request.setAttribute("areaMgmtForm", areaMgmtForm);
			    
				return mapping.findForward(SEARCH_PAGE);
				
			}catch(Exception e){
				
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("area.search.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages 	errorHeadingMessage = new ActionMessages();
		        ActionMessage 	message = new ActionMessage("area.search.error.heading","searching");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
			}
			return mapping.findForward(FAILURE);
			
		}else{
            Logger.logWarn(MODULE,"No Access On this Operation.");
	        ActionMessage 	message = new ActionMessage("general.user.restricted");
            ActionMessages 	messages = new ActionMessages();
            messages.add("information", message);
            saveErrors(request, messages);
            
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("area.search.error.heading","searching");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
            return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	
	@Override
	public ActionForward initCreate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Init Create Method");
		
		if(checkAccess(request, CREATE_ACTION_ALIAS)){
			
				AreaMgmtForm areaMasterForm = (AreaMgmtForm)form;
				NetworkBLManager networkBLManager = new NetworkBLManager();
				List<CountryData> listCountryData = networkBLManager.getCountryDataList();
				CityBLManger 	cityBlManeger = new CityBLManger();
				List<CityData> 	listCityData = cityBlManeger.getCityDataList();
				RegionBLManager 	regionBlManger = new RegionBLManager();
				List<RegionData> 	listRegionData = regionBlManger.getRegionDataList();
				List<NetworkData> mccmncCodesDataList = networkBLManager.getNetworkDataList();
				areaMasterForm.setListNetworkData(mccmncCodesDataList);
				areaMasterForm.setListCityData(listCityData);
				areaMasterForm.setListRegionData(listRegionData);
				areaMasterForm.setListCountryData(listCountryData);
				request.setAttribute("areaMasterForm", areaMasterForm);
				return mapping.findForward(CREATE_PAGE);
				
			}else{
				
	            Logger.logWarn(MODULE,"No Access On this Operation.");
		        ActionMessage 	message  = new ActionMessage("general.user.restricted");
	            ActionMessages 	messages = new ActionMessages();
	            messages.add("information", message);
	            saveErrors(request, messages);
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        message = new ActionMessage("area.search.error.heading","creating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);            
	            return mapping.findForward(INVALID_ACCESS_FORWARD);
	            
			}		
	}
	
	@Override
	public ActionForward create(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Create Method");
		
		if((checkAccess(request, CREATE_ACTION_ALIAS))){
			AreaMgmtForm areaMgmtForm = (AreaMgmtForm)form;
			
			try{
				
				AreaBLManager areaBLManager = new AreaBLManager();
				AreaData 		areaMasterData 		= new AreaData();
				
				WiFiCallingStationInfoData 	wificallingStationIdData = new WiFiCallingStationInfoData();
				CallingStationInfoData 		callingStationInfoData 	 = new CallingStationInfoData();
				
				if(areaMgmtForm.getStrWiFiSSIDs()!=null){
					wificallingStationIdData.setSsids(areaMgmtForm.getStrWiFiSSIDs().trim());
				}
				
				if(areaMgmtForm.getStrCallingStationIds()!=null){
					callingStationInfoData.setCallingStaionIds(areaMgmtForm.getStrCallingStationIds().trim());
				}
				
				callingStationInfoData.setAreaData(areaMasterData);
				wificallingStationIdData.setAreaData(areaMasterData);
				areaMasterData.setCallingStationInfoData(callingStationInfoData);
				areaMasterData.setWifiCallingStationInfoData(wificallingStationIdData);				

				convertFormToBean(areaMgmtForm,areaMasterData,request);
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				areaBLManager.create(areaMasterData,staffData, CREATE_ACTION_ALIAS);
				
               	ActionMessage 	message = new ActionMessage("area.create.success",areaMasterData.getArea());
	            ActionMessages 	messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
	            
               	request.setAttribute("responseUrl","/areaManagement.do?method=initSearch");               	
				return mapping.findForward(SUCCESS);
				
			}catch(Exception e){
				
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("area.create.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages 	errorHeadingMessage = new ActionMessages();
		        ActionMessage 	message = new ActionMessage("area.search.error.heading","creating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
	            
	            return mapping.findForward(FAILURE);
	            
	        }
		}else{
			
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage 	message = new ActionMessage("general.user.restricted");
	        ActionMessages 	messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("area.search.error.heading","creating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
			
		}
	}
	
	@Override
	public ActionForward delete(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Delete Method");
		if((checkAccess(request, DELETE_ACTION_ALIAS))){
			try{
				
				AreaBLManager locationMasterBLManager = new AreaBLManager();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String[] 	strAreaIds = (String[])request.getParameterValues("areaId");
				Long[] 		locationMasterIDS 	 = convertStringIdsToLong(strAreaIds);  
				locationMasterBLManager.delete(locationMasterIDS,staffData,DELETE_ACTION_ALIAS);

				ActionMessage 	message 	= new ActionMessage("area.delete.success");
	            ActionMessages 	messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/areaManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
				
			}catch(Exception e){
				
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("area.delete.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("area.search.error.heading","deleting");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
		        
	            return mapping.findForward(FAILURE);
	        }
		}else{
			
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage 	message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);

	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("area.search.error.heading","deleting");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	@Override
	public ActionForward initUpdate(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
//		return mapping.findForward(EDIT_PAGE);
		Logger.logDebug(MODULE, "Entered in Init Update Method");
		if((checkAccess(request,UPDATE_ACTION_ALIAS))){
			AreaMgmtForm areaMgmtForm = (AreaMgmtForm)form;
			
			try{
				
				NetworkBLManager 	networkBLManager = new NetworkBLManager();
				AreaBLManager areaBLManager = new AreaBLManager();	
				AreaData areaData = areaBLManager.getAreaData(areaMgmtForm.getAreaId());
						
				convertBeanToForm(areaMgmtForm, areaData);
				
				List<CountryData> 	listCountryData	= networkBLManager.getCountryDataList();
				CityBLManger 		cityBlManeger	= new CityBLManger();
				List<CityData> 		listCityData	= cityBlManeger.getCityDataList();
				RegionBLManager 	regionBlManger	= new RegionBLManager();
				List<RegionData> 	listRegionData 	= regionBlManger.getRegionDataList();
				List<NetworkData> networkDataList = networkBLManager.getNetworkDataList();
				areaMgmtForm.setListNetworkData(networkDataList);
				areaMgmtForm.setListCityData(listCityData);
				areaMgmtForm.setListRegionData(listRegionData);
				areaMgmtForm.setListCountryData(listCountryData);				
				
				request.setAttribute("areaData", areaData);
				request.setAttribute("areaMgmtForm", areaMgmtForm);
               	return mapping.findForward(EDIT_PAGE);
               	
			}catch(Exception e){
				
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("area.update.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages 	errorHeadingMessage = new ActionMessages();
		        ActionMessage 	message = new ActionMessage("area.search.error.heading","updating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
	            
	            return mapping.findForward(FAILURE);
	        }
		}else{
			
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage 	message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("area.search.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);	        	       
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}		
	}

	@Override
	public ActionForward update(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in Update Method");
		if((checkAccess(request, UPDATE_ACTION_ALIAS))){
			AreaMgmtForm areaMgmtForm = (AreaMgmtForm)form;
			
			try{
				AreaBLManager areaBLManager = new AreaBLManager();
				AreaData 		areaData 		= new AreaData();
				areaData.setAreaId(areaMgmtForm.getAreaId());
				
				WiFiCallingStationInfoData 	wificallingStationIdData = new WiFiCallingStationInfoData();
				wificallingStationIdData.setWifissidinfo_id(Long.parseLong(request.getParameter("wifissidinfoId")));
				wificallingStationIdData.setAreaData(areaData);
				
				if(request.getParameter("strWiFiSSIDs")!=null){
					wificallingStationIdData.setSsids(request.getParameter("strWiFiSSIDs").trim());
				}
				
				areaData.setWifiCallingStationInfoData(wificallingStationIdData);				
				
				CallingStationInfoData 		callingStationInfoData = new CallingStationInfoData();
				callingStationInfoData.setAreaData(areaData);
				callingStationInfoData.setCsId(Long.parseLong(request.getParameter("csId")));
				
				if(request.getParameter("strCallingStationIds")!=null){
					callingStationInfoData.setCallingStaionIds(request.getParameter("strCallingStationIds").trim());
				}
				
				areaData.setCallingStationInfoData(callingStationInfoData);	
								
				convertFormToBean(areaMgmtForm, areaData,request);
				
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				areaBLManager.update(areaData,staffData, UPDATE_ACTION_ALIAS);
               	
               	ActionMessage 	message = new ActionMessage("area.update.success",areaData.getArea());
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", message);
	            saveMessages(request,messages);
               	request.setAttribute("responseUrl","/areaManagement.do?method=initSearch");
               	
				return mapping.findForward(SUCCESS);
				
			}catch(Exception e){
				
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("area.update.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages 	errorHeadingMessage = new ActionMessages();
		        ActionMessage 	message = new ActionMessage("area.search.error.heading","updating");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
	            
	            return mapping.findForward(FAILURE);
	            
	        }
		}else{
			
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage 	message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);

	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("area.search.error.heading","updating");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
	        
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	public ActionForward view(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Entered in View Method");

		if((checkAccess(request, SEARCH_ACTION_ALIAS))){
			AreaMgmtForm areaMgmtForm = (AreaMgmtForm)form;
			
			try{	
				
				AreaBLManager areaBLManager = new AreaBLManager();
				AreaData 		areaData 		= areaBLManager.getAreaData(areaMgmtForm.getAreaId());				
				convertBeanToForm(areaMgmtForm, areaData);
				request.setAttribute("areaData", areaData);
				request.setAttribute("areaMgmtForm", areaMgmtForm);
				return mapping.findForward(VIEW_PAGE);
				
			}catch(Exception e){
				
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
	            ActionMessages messages = new ActionMessages();
	            messages.add("information", new ActionMessage("area.view.failure"));
	            saveErrors(request, messages);
	            
		        ActionMessages errorHeadingMessage = new ActionMessages();
		        ActionMessage message = new ActionMessage("area.search.error.heading","viewing");
		        errorHeadingMessage.add("errorHeading",message);
		        saveMessages(request,errorHeadingMessage);
		        
	            return mapping.findForward(FAILURE);
	        }
		}else{
			
	        Logger.logError(MODULE, "Error during Data Manager operation ");
	        ActionMessage 	message = new ActionMessage("general.user.restricted");
	        ActionMessages messages = new ActionMessages();
	        messages.add("information", message);
	        saveErrors(request, messages);
	        ActionMessages errorHeadingMessage = new ActionMessages();
	        message = new ActionMessage("area.search.error.heading","viewing");
	        errorHeadingMessage.add("errorHeading",message);
	        saveMessages(request,errorHeadingMessage);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
			
		}
 	
	}
	
	private void convertBeanToForm(AreaMgmtForm form,AreaData data){
		
		form.setAreaId(data.getAreaId());
		form.setArea(data.getArea());		
		form.setCountryId(data.getCityData().getRegion().getCountryData().getCountryID());
		form.setRegionId(data.getCityData().getRegion().getRegionId());
		form.setCityId(data.getCityData().getCityId());		
		form.setNetworkId(data.getNetworkId());
		form.setParam1(data.getParam1());
		form.setParam2(data.getParam2());
		form.setParam3(data.getParam3());
		
	}
	
	private void convertFormToBean(AreaMgmtForm areaMgmtForm,AreaData areaData,HttpServletRequest request){
		
		if(areaMgmtForm.getNetworkId()!=null && areaMgmtForm.getNetworkId()==0){
			areaData.setNetworkId(null);
		}else{
			areaData.setNetworkId(areaMgmtForm.getNetworkId());
		}
				
		String[] lacIdAll		=	request.getParameterValues("lacIdALl");		
		String[] listLacCodes	=	request.getParameterValues("lacCode");
		String[] listCi			=	request.getParameterValues("CI");
		String[] listRac		=	request.getParameterValues("RAC");
		String[] listSac		=	request.getParameterValues("SAC");	
		
		List<LacData> deleteLacIdList = new ArrayList<LacData>();			
		if(lacIdAll!=null ){			
			for(String oldId:lacIdAll){								
				LacData lacData = new LacData();
				lacData.setLacId(Long.parseLong(oldId));
				deleteLacIdList.add(lacData);				
			}
		}		
		areaData.setDeleteLacIds(deleteLacIdList);
				
		List<LacData> listLacList	= new ArrayList<LacData>();
		Set<LacData>  lacDataSet	= new HashSet<LacData>();				
		if(listLacCodes != null && areaMgmtForm.getNetworkId()!=null && areaMgmtForm.getNetworkId()!=0){	
			for (int i = 0; i < listLacCodes.length; i++) {	           	
	           	if(listLacCodes[i]!=null && listLacCodes[i].trim().length()>0){
	           			LacData lacData=new LacData();	           		
	           			lacData.setLacCode(Long.parseLong(listLacCodes[i]));
	           			lacData.setStrCellIds(listCi[i]);
	           			lacData.setStrSacs(listSac[i]);
	           			lacData.setStrRacs(listRac[i]);
	           			lacData.setNetworkId(areaMgmtForm.getNetworkId());
	           			listLacList.add(lacData);
	           			lacDataSet.add(lacData);
	           	}
			}
		}
		
		areaData.setLacDataSet(lacDataSet);		
		areaData.setLacDataList(listLacList);
		areaData.setCityId(areaMgmtForm.getCityId());
		if(areaMgmtForm.getArea()!=null){
		areaData.setArea(areaMgmtForm.getArea().trim());
		}areaData.setParam1(areaMgmtForm.getParam1());
		areaData.setParam2(areaMgmtForm.getParam2());
		areaData.setParam3(areaMgmtForm.getParam3());
	}
}