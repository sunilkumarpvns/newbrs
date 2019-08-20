package com.elitecore.elitesm.web.radius.radiusesigroup;

import java.io.StringReader;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;

import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusEsiGroupConfigurationData;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.externalsystem.ExternalSystemInterfaceBLManager;
import com.elitecore.elitesm.blmanager.history.HistoryBLManager;
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.history.DatabaseHistoryData;
import com.elitecore.elitesm.web.radius.radiusesigroup.form.RadiusESIGroupForm;

public class ViewRadiusESIGroupHistoryAction extends BaseWebAction {
private static final String VIEW_FORWARD 	= "viewRadiuESIGroupHistory";
private static final String FAILURE_FORWARD = "failure";
private static final String ACTION_ALIAS 	= ConfigConstant.VIEW_RADIUS_ESI_GROUP;
private static final String MODULE 			= "ViewRadiusESIGroupHistoryAction";
/**
 * 
 * @author Tejas Shah
 *
 */
public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logInfo(MODULE,"Enter execute method of"+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try{
				RadiusESIGroupForm radiusESIGroupForm = (RadiusESIGroupForm)form;
				String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
				
				RadiusESIGroupBLManager blManager = new RadiusESIGroupBLManager();
				RadiusESIGroupData radiusESIGroupData = new RadiusESIGroupData();
				ExternalSystemInterfaceBLManager externalSystemInterfaceBLManager =new ExternalSystemInterfaceBLManager();
				
				String strESIGroupId = request.getParameter("id");
				String esiGroupId = strESIGroupId;
				if(Strings.isNullOrBlank(esiGroupId) == true){
					esiGroupId = radiusESIGroupForm.getEsiGroupId();
				}
	
				if(Strings.isNullOrBlank(esiGroupId) == false){
					radiusESIGroupData.setId(esiGroupId);
					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					radiusESIGroupData = blManager.getRadiusESIGroupById(esiGroupId);

					convertBeanToFrom(radiusESIGroupData,radiusESIGroupForm);
					HistoryBLManager historyBlManager= new HistoryBLManager();
					
					String strAuditUid = request.getParameter("auditUid");
					String strSytemAuditId=request.getParameter("systemAuditId");
					String esiGroupName=request.getParameter("esiGroupName");
					
					if(strSytemAuditId != null){
						request.setAttribute("systemAuditId", strSytemAuditId);
					}
					
					if(esiGroupId != null && Strings.isNullOrBlank(strAuditUid) == false){
						
						staffData.setAuditName(radiusESIGroupData.getName());
						staffData.setAuditId(radiusESIGroupData.getAuditUId());
						
						List<DatabaseHistoryData> lstDatabaseDSHistoryDatas=historyBlManager.getHistoryData(strAuditUid);
						
						request.setAttribute("esiGroupName", esiGroupName);
						request.setAttribute("lstDatabaseDSHistoryDatas", lstDatabaseDSHistoryDatas);
					}
					request.setAttribute("radiusESIGroup",radiusESIGroupData);
					request.setAttribute("radiusESIGroupForm",radiusESIGroupForm);
				}
	
				return mapping.findForward(VIEW_FORWARD);
	
			}catch(Exception e){
				Logger.logError(MODULE,"Returning error forward from "+getClass().getName());
				Logger.logTrace(MODULE,e);
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
				ActionMessages messages = new ActionMessages();
				ActionMessage message1 = new ActionMessage("radiusesigroup.view.failure");
				messages.add("information",message1);
				saveErrors(request,messages);
			} 
			return mapping.findForward(FAILURE_FORWARD);
		
		}else{
			Logger.logWarn(MODULE, "No Access on this Operation ");
			ActionMessage message = new ActionMessage("general.user.restricted");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}
	}

	private void convertBeanToFrom(RadiusESIGroupData radiusESIGroup, RadiusESIGroupForm radiusESIGroupForm) throws JAXBException {
		radiusESIGroupForm.setDescription(radiusESIGroup.getDescription());
		radiusESIGroupForm.setEsiGroupId(radiusESIGroup.getId());
		radiusESIGroupForm.setEsiGroupName(radiusESIGroup.getName());
		radiusESIGroupForm.setAuditUId(radiusESIGroup.getAuditUId());

		String xmlDatas = new String(radiusESIGroup.getEsiGroupDataXml());
		StringReader stringReader =new StringReader(xmlDatas.trim());

		RadiusEsiGroupConfigurationData esiConfigurationData = ConfigUtil.deserialize(stringReader, RadiusEsiGroupConfigurationData.class);

		radiusESIGroupForm.setRedundancyMode(esiConfigurationData.getRedundancyMode());
		radiusESIGroupForm.setEsiType(esiConfigurationData.getEsiType());
		radiusESIGroupForm.setStickySession(Boolean.getBoolean(esiConfigurationData.getStateful()));
		radiusESIGroupForm.setSwitchBack(Boolean.getBoolean(esiConfigurationData.getSwitchBackEnable()));
		radiusESIGroupForm.setPrimaryEsiValues(esiConfigurationData.getPrimaryEsiList());
		radiusESIGroupForm.setSecondaryEsiValues(esiConfigurationData.getFailOverEsiList());

		radiusESIGroupForm.setAuditUId(radiusESIGroup.getAuditUId());
	}
}