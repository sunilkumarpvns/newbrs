package com.elitecore.elitesm.web.radius.radiusesigroup;

import java.io.StringReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.core.util.PageList;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.util.constants.BaseConstant;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radiusesigroup.form.RadiusESIGroupForm;

/**
 * 
 * @author Tejas Shah
 *
 */
public class SearchRadiusESIGroupAction extends BaseWebAction{

	private static final String FAILURE_FORWARD = "failure";               
	private static final String MODULE ="RADIUS-ESI-GROUP";
	private static final String SEARCH_RADIUS_ESI_GROUP = "searchRadiusESIGroup"; 
	private static final String ACTION_ALIAS = ConfigConstant.SEARCH_RADIUS_ESI_GROUP;
	private static final String DELETE_ALIAS = ConfigConstant.DELETE_RADIUS_ESI_GROUP;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		RadiusESIGroupForm radiusESIGroupForm = (RadiusESIGroupForm)form;
		RadiusESIGroupBLManager blManager = new RadiusESIGroupBLManager();
		try{
			
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			
			Integer pageSize = Integer.parseInt(ConfigManager.get("TOTAL_ROW"));
			int requiredPageNo;
			String requestAction = request.getParameter("action");
			if( requestAction != null && requestAction.equals("deleteESIGroup")){
				try{
					checkActionPermission(request, DELETE_ALIAS);
					String[] strSelectedIds = request.getParameterValues("select");
					
					blManager.deleteRadiusESIGroupById(Arrays.asList(strSelectedIds), staffData);
					  
					request.setAttribute("responseUrl", "/searchRadiusESIGroup");
					ActionMessage message = new ActionMessage("radiusesigroup.delete.success");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveMessages(request, messages);
					return mapping.findForward(SUCCESS);
				}catch(ActionNotPermitedException e){
					Logger.logError(MODULE,"Error :-" + e.getMessage());
					printPermitedActionAlias(request);
					ActionMessages messages = new ActionMessages();
					messages.add("information", new ActionMessage("general.user.restricted"));
					saveErrors(request, messages);
					return mapping.findForward(INVALID_ACCESS_FORWARD);
				}catch(Exception exp){
					Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());                                 
					ActionMessage message = new ActionMessage("radiusesigroup.delete.failure");                                                         
					ActionMessages messages = new ActionMessages();                                                                                 
					messages.add("information", message);                                                                                           
					saveErrors(request, messages); 
					return mapping.findForward(FAILURE_FORWARD); 
				}
			}else{
				if(request.getParameter("pageNo") != null){
					requiredPageNo = Integer.parseInt(String.valueOf(request.getParameter("pageNo")));
				}else{
					requiredPageNo = new Long(radiusESIGroupForm.getPageNumber()).intValue();
				}
				if(requiredPageNo == 0)
					requiredPageNo =1;
				RadiusESIGroupData radiusESIGroupData = new RadiusESIGroupData();
				String strESIGroupName = radiusESIGroupForm.getEsiGroupName();
				if(strESIGroupName!=null)
					radiusESIGroupData.setName(strESIGroupName);
				else
					radiusESIGroupData.setName("");
				
				String peerGroupId = radiusESIGroupForm.getEsiGroupId();
				if(Strings.isNullOrBlank(peerGroupId) == false)
					radiusESIGroupData.setId(peerGroupId);
				
				PageList pageList = blManager.searchRadiusESIGroup(radiusESIGroupData, staffData, requiredPageNo, pageSize);
				
			    radiusESIGroupForm.setRadiusESIGroupDataList(pageList.getListData());
			    radiusESIGroupForm.setAction(BaseConstant.LISTACTION);
			    radiusESIGroupForm.setPageNumber(pageList.getCurrentPage());
			    radiusESIGroupForm.setTotalPages(pageList.getTotalPages());
			    radiusESIGroupForm.setTotalRecords(pageList.getTotalItems());
			    radiusESIGroupForm.setEsiGroupName(radiusESIGroupData.getName());
				convertBeanToForm(radiusESIGroupForm);
				request.setAttribute("radiusESIGroupForm", radiusESIGroupForm);
			}
			
			
		}catch(Exception e){
			e.printStackTrace();
		}
		
		return mapping.findForward(SEARCH_RADIUS_ESI_GROUP); 
	}

	private void convertBeanToForm(RadiusESIGroupForm radiusESIGroupForm) throws JAXBException {

		if(Collectionz.isNullOrEmpty(radiusESIGroupForm.getRadiusESIGroupDataList()) == false){

			List<RadiusEsiGroupConfigurationData> esiDatas = new ArrayList<>();
			for (RadiusESIGroupData esiData:radiusESIGroupForm.getRadiusESIGroupDataList()) {

				String xmlDatas = new String(esiData.getEsiGroupDataXml());
				StringReader stringReader =new StringReader(xmlDatas.trim());

				RadiusEsiGroupConfigurationData esiConfigurationData = ConfigUtil.deserialize(stringReader, RadiusEsiGroupConfigurationData.class);
				esiConfigurationData.setId(esiData.getId());
				esiDatas.add(esiConfigurationData);
			}
			radiusESIGroupForm.setDeserializedRadiusEsiGroupDataList(esiDatas);
		}
	}
}
