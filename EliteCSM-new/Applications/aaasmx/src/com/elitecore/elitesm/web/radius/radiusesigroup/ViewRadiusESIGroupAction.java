package com.elitecore.elitesm.web.radius.radiusesigroup;

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
import com.elitecore.elitesm.blmanager.radius.radiusesigroup.RadiusESIGroupBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.radius.radiusesigroup.data.RadiusESIGroupData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.radius.radiusesigroup.form.RadiusESIGroupForm;

import java.io.StringReader;

/**
 * 
 * @author Tejas Shah
 *
 */
public class ViewRadiusESIGroupAction extends BaseWebAction{
	protected static final String MODULE = "ViewRadiusESIGroupAction";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_RADIUS_ESI_GROUP;
	private static final String VIEW_FORWARD = "viewRadiusESIGroup";
	public ActionForward execute( ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response) throws Exception{
		
		RadiusESIGroupForm radiusESIGroupForm = (RadiusESIGroupForm)form;
		String userName = ((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")).getSystemUserName();
		
		RadiusESIGroupBLManager blManager = new RadiusESIGroupBLManager();
		RadiusESIGroupData radiusESIGroupData = new RadiusESIGroupData();
			if(checkActionPermission(request, ACTION_ALIAS)){
			try {
				Logger.logTrace(MODULE, "Enter execute method of" + getClass().getName());
	
				String strRadiusESIGroupId = request.getParameter("id");
				Logger.getLogger().info(MODULE, "ESI Group Id is : " + strRadiusESIGroupId);
				
				String esiGroupId = "";
				if(Strings.isNullOrBlank(strRadiusESIGroupId) == false){
					esiGroupId = strRadiusESIGroupId;
				}
				
				if(Strings.isNullOrBlank(esiGroupId) == false){
					radiusESIGroupData.setId(esiGroupId);
					
					radiusESIGroupData = blManager.getRadiusESIGroupById(esiGroupId);
					
					convertBeanToFrom(radiusESIGroupData, radiusESIGroupForm);
					
					request.setAttribute("radiusESIGroup",radiusESIGroupData);
					request.setAttribute("radiusESIGroupForm",radiusESIGroupForm);
				}
				return mapping.findForward(VIEW_FORWARD);
			}catch (DataManagerException managerExp) {
				Logger.logError(MODULE, "Error during Data Manager operation , reason : " + managerExp.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(managerExp);
				request.setAttribute("errorDetails", errorElements);

				ActionMessage message = new ActionMessage("radiusesigroup.view.failure");
				ActionMessages messages = new ActionMessages();
				messages.add("information", message);
				saveErrors(request, messages);
				return mapping.findForward(FAILURE);
			}
		
		}else{
			Logger.logError(MODULE, "Error during Data Manager operation ");
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
		radiusESIGroupForm.setStickySession(Boolean.parseBoolean(esiConfigurationData.getStateful()));
		radiusESIGroupForm.setSwitchBack(Boolean.parseBoolean(esiConfigurationData.getSwitchBackEnable()));
		radiusESIGroupForm.setPrimaryEsiValues(esiConfigurationData.getPrimaryEsiList());
		radiusESIGroupForm.setSecondaryEsiValues(esiConfigurationData.getFailOverEsiList());
		radiusESIGroupForm.setActivePassiveEsiList(esiConfigurationData.getActivePassiveEsiList());

		radiusESIGroupForm.setAuditUId(radiusESIGroup.getAuditUId());
	}
}
