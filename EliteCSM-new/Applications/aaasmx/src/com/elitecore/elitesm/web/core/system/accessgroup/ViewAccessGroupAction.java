package com.elitecore.elitesm.web.core.system.accessgroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.blmanager.core.system.profilemanagement.ProfileBLManager;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleSubBISModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IConfigurationProfileData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleActionRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ProfileBISModelRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.SubBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.accessgroup.forms.ViewAccessGroupForm;
import com.elitecore.elitesm.web.core.system.cache.ConfigManager;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;

public class ViewAccessGroupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "viewAccessGroup";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "VIEW ACCESS GROUP ACTION ";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_ACCESS_GROUP_ACTION;
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try {
				AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
				ProfileBLManager profileBLManager = new ProfileBLManager();
				ViewAccessGroupForm viewAccessGroupForm = (ViewAccessGroupForm)form;
				IGroupData groupData = new GroupData();
				IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
				
				String strGroupId = request.getParameter("groupid");
				String groupId;
				if(strGroupId == null){
					groupId = viewAccessGroupForm.getGroupId();
				}else{
					groupId = request.getParameter("groupid");
				}
				List lstGroupActionList = accessGroupBLManager.getGroupActionRelList(groupId);


				if(Strings.isNullOrBlank(groupId) == false){
					groupData.setGroupId(groupId);
					groupData = accessGroupBLManager.getGroupData(groupData.getGroupId());
					request.setAttribute("groupData",groupData);

					String profileName = ConfigManager.get(ConfigConstant.PROFILE);
					IConfigurationProfileData configurationProfileData = profileBLManager.getConfigurationProfileData(profileName);
					List businessModelList = profileBLManager.getBusinessModelList();
					viewAccessGroupForm.setListBusinessModel(businessModelList);
					HashMap profileMap = new HashMap();
					
					profileMap.put("name",profileMap);
					HashMap modelMap = new HashMap();
					for(int i=0;i<businessModelList.size();i++){
						IBISModelData bisModelData = (IBISModelData)businessModelList.get(i);
						IBISModuleData businessModuleData = new BISModuleData();
						List businessModelModuleRelList = new ArrayList();
						HashMap modelObjectMap = new HashMap();
						modelObjectMap.put("businessModelId",bisModelData.getBusinessModelId());
						modelObjectMap.put("businessModelName",bisModelData.getName());
						modelObjectMap.put("businessModelStatus",bisModelData.getStatus());
						businessModelModuleRelList = profileBLManager.getBISModelModuleRelList(modelObjectMap.get("businessModelId").toString());
						HashMap moduleMap = new HashMap();

						for(int j=0;j<businessModelModuleRelList.size();j++){
							HashMap moduleObjectMap = new HashMap();
							List businessModuleSubBISModuleRelList = new ArrayList();
							ISubBISModuleData subBusinessModuleData = new SubBISModuleData();
							businessModuleData = profileBLManager.getBISModule(((IBISModelModuleRelData)businessModelModuleRelList.get(j)).getBusinessModuleId());
							moduleObjectMap.put("businessModuleId",businessModuleData.getBusinessModuleId());
							moduleObjectMap.put("businessModuleName",businessModuleData.getName());
							moduleObjectMap.put("businessModuleStatus",businessModuleData.getStatus());
							businessModuleSubBISModuleRelList = profileBLManager.getBISModuleSubBISModuleRelList(moduleObjectMap.get("businessModuleId").toString());
							HashMap subModuleMap = new HashMap();

							for(int k=0;k<businessModuleSubBISModuleRelList.size();k++){
								HashMap subModuleObjectMap = new HashMap();
								List subBusinessModuleActionRelList = new ArrayList();
								IActionData actionData = new ActionData();
								subBusinessModuleData = profileBLManager.getSubBISModule(((IBISModuleSubBISModuleRelData)businessModuleSubBISModuleRelList.get(k)).getSubBusinessModuleId());
								subModuleObjectMap.put("subBusinessModuleId",subBusinessModuleData.getSubBusinessModuleId());
								subModuleObjectMap.put("subBusinessModuleName",subBusinessModuleData.getName());
								subModuleObjectMap.put("subBusinessModuleStatus",subBusinessModuleData.getStatus());
								subBusinessModuleActionRelList = profileBLManager.getSubBISModuleActionRelList(subModuleObjectMap.get("subBusinessModuleId").toString());
								HashMap actionMap = new HashMap();

								for(int l=0;l<subBusinessModuleActionRelList.size();l++){
									HashMap actionObjectMap = new HashMap();
									actionData = profileBLManager.getActionData(((ISubBISModuleActionRelData)subBusinessModuleActionRelList.get(l)).getActionId());
									if(actionData.getStatus()!=null && actionData.getStatus().equals("D")){
										continue;
									}
									actionObjectMap.put("actionId",actionData.getActionId());
									actionObjectMap.put("actionName",actionData.getName());
									actionObjectMap.put("actionStatus",actionData.getStatus());
									actionMap.put(actionData.getActionId(),actionObjectMap);
								}
								subModuleObjectMap.put("actionMap",actionMap);
								subModuleMap.put(subBusinessModuleData.getSubBusinessModuleId(),subModuleObjectMap);
							}
							moduleObjectMap.put("subModuleMap",subModuleMap);
							moduleMap.put(businessModuleData.getBusinessModuleId(),moduleObjectMap);
						}
						modelObjectMap.put("moduleMap",moduleMap);
						modelMap.put(bisModelData.getBusinessModelId(),modelObjectMap);
					}
					profileMap.put("modelMap",modelMap);
					request.setAttribute("profileMap",profileMap);

					request.setAttribute("lstGroupActionList",lstGroupActionList);
					doAuditing(staffData, ACTION_ALIAS);
					return mapping.findForward(SUCCESS_FORWARD);
				}else{
					Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
					ActionMessage message = new ActionMessage("accessgroup.view.failure");
					ActionMessages messages = new ActionMessages();
					messages.add("information",message);
					saveErrors(request,messages);
					return mapping.findForward(FAILURE_FORWARD);
				}

			} catch (Exception e) {
				Logger.logTrace(MODULE,e.getMessage());
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			}
			Logger.logTrace(MODULE,"Returning error forward from "+ getClass().getName());
			ActionMessage message = new ActionMessage("accessgroup.view.failure");
			ActionMessages messages = new ActionMessages();
			messages.add("information",message);
			saveErrors(request,messages);
			return mapping.findForward(FAILURE_FORWARD);
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
}
