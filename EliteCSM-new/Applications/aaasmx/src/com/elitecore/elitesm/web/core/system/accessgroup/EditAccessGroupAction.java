package com.elitecore.elitesm.web.core.system.accessgroup;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.elitesm.blmanager.core.system.profilemanagement.ProfileBLManager;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupActionRelData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.GroupData;
import com.elitecore.elitesm.datamanager.core.system.accessgroup.data.IGroupData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModelModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.BISModuleSubBISModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModelModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.IBISModuleSubBISModuleRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleActionRelData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.elitesm.datamanager.core.system.profilemanagement.data.SubBISModuleActionRelData;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseWebAction;
import com.elitecore.elitesm.web.core.system.accessgroup.forms.ViewAccessGroupForm;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;


public class EditAccessGroupAction extends BaseWebAction {
	private static final String GROUP_ID = "groupId";
	private static final String FAILURE_FORWARD = "failure";
	private static final String EDIT_FORWARD = "editAccessGroup";
	private static final String VIEW_FORWARD = "viewAccessGroup";
	private static final String ACTION_ALIAS = "UPDATE_ACCESS_GROUP_ACTION";
	
	@Override
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){	
			AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
			ProfileBLManager profileBLManager = new ProfileBLManager();
			String groupId = request.getParameter(GROUP_ID);
			ActionErrors errors = new ActionErrors();

			List lstGroupActionList = accessGroupBLManager.getGroupActionRelList(groupId);
			
			try {
				ViewAccessGroupForm viewAccessGroupForm = (ViewAccessGroupForm)form;
				IGroupData groupData = new GroupData();
				List businessModelList = profileBLManager.getBusinessModelList();
				HashMap modelMap = new HashMap();
				HashMap profileMap = new HashMap();
				profileMap.put("name",profileMap);
				if(Collectionz.isNullOrEmpty(businessModelList) == false) {
					for(int i=0;i<businessModelList.size();i++){
						IBISModelData bisModelData = (IBISModelData)businessModelList.get(i);
						List businessModelModuleRelList = null;
						HashMap modelObjectMap = new HashMap();
						modelObjectMap.put("businessModelId",bisModelData.getBusinessModelId());
						modelObjectMap.put("businessModelName",bisModelData.getName());
						modelObjectMap.put("businessModelStatus",bisModelData.getStatus());
						businessModelModuleRelList = profileBLManager.getBISModelModuleRelList(modelObjectMap.get("businessModelId").toString());
						HashMap moduleMap = new HashMap();
						
						for(int j=0;j<businessModelModuleRelList.size();j++){
							HashMap moduleObjectMap = new HashMap();
							List businessModuleSubBISModuleRelList = new ArrayList();
							IBISModuleData businessModuleData = profileBLManager.getBISModule(((IBISModelModuleRelData)businessModelModuleRelList.get(j)).getBusinessModuleId());
							moduleObjectMap.put("businessModuleId",businessModuleData.getBusinessModuleId());
							moduleObjectMap.put("businessModuleName",businessModuleData.getName());
							moduleObjectMap.put("businessModuleStatus",businessModuleData.getStatus());
							businessModuleSubBISModuleRelList = profileBLManager.getBISModuleSubBISModuleRelList(moduleObjectMap.get("businessModuleId").toString());
							HashMap subModuleMap = new HashMap();
							
							for(int k=0;k<businessModuleSubBISModuleRelList.size();k++){
								HashMap subModuleObjectMap = new HashMap();
								ISubBISModuleData subBusinessModuleData = profileBLManager.getSubBISModule(((IBISModuleSubBISModuleRelData)businessModuleSubBISModuleRelList.get(k)).getSubBusinessModuleId());
								subModuleObjectMap.put("subBusinessModuleId",subBusinessModuleData.getSubBusinessModuleId());
								subModuleObjectMap.put("subBusinessModuleName",subBusinessModuleData.getName());
								subModuleObjectMap.put("subBusinessModuleStatus",subBusinessModuleData.getStatus());
								List subBusinessModuleActionRelList = profileBLManager.getSubBISModuleActionRelList(subModuleObjectMap.get("subBusinessModuleId").toString());
								HashMap actionMap = new HashMap();
								
								for(int l=0;l<subBusinessModuleActionRelList.size();l++){
									HashMap actionObjectMap = new HashMap();
									IActionData actionData = profileBLManager.getActionData(((ISubBISModuleActionRelData)subBusinessModuleActionRelList.get(l)).getActionId());
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
					
				}
				profileMap.put("modelMap",modelMap);
				request.setAttribute("profileMap",profileMap);

				if(viewAccessGroupForm != null && viewAccessGroupForm.getAction() == null){
					if(Strings.isNullOrBlank(groupId) == false){
						groupData.setGroupId(groupId);
						groupData = accessGroupBLManager.getGroupData(groupData);
						viewAccessGroupForm = convertBeanToForm(groupData);
						viewAccessGroupForm.setListBusinessModel(businessModelList);
						request.setAttribute("viewAccessGroupForm",viewAccessGroupForm);
						groupData = accessGroupBLManager.getGroupData(groupData.getGroupId());
						request.getSession().setAttribute("groupData",groupData);
						request.setAttribute("lstGroupActionList",lstGroupActionList);
					}
					request.setAttribute(GROUP_ID, groupData.getGroupId());
					return mapping.findForward(EDIT_FORWARD);
				}else if(viewAccessGroupForm.getAction().equalsIgnoreCase("update")){
					List lstBISModelModuleRelList = null;
					viewAccessGroupForm.setListBusinessModel(businessModelList);
					lstBISModelModuleRelList = profileBLManager.getBISModelModuleRelList(viewAccessGroupForm.getChkID());
					for(int i=0;i<lstBISModelModuleRelList.size();i++){
						String businessModuleId = ((BISModelModuleRelData)lstBISModelModuleRelList.get(i)).getBusinessModuleId();
						IBISModuleData bisModuleData = profileBLManager.getBISModule(businessModuleId);
						List lstBISModuleSubBISModuleRelList = profileBLManager.getBISModuleSubBISModuleRelList(bisModuleData.getBusinessModuleId());
						for(int j=0;j<lstBISModuleSubBISModuleRelList.size();j++){
							String subBusinessModuleId = ((BISModuleSubBISModuleRelData)lstBISModuleSubBISModuleRelList.get(j)).getSubBusinessModuleId();
							ISubBISModuleData subBISModuleData = profileBLManager.getSubBISModule(subBusinessModuleId);
							List lstSubBISModuleActionRelList = profileBLManager.getSubBISModuleActionRelList(subBISModuleData.getSubBusinessModuleId());
							for(int k=0;k<lstSubBISModuleActionRelList.size();k++){
								String actionId = ((SubBISModuleActionRelData)lstSubBISModuleActionRelList.get(k)).getActionId();
								IActionData actionData = profileBLManager.getActionData(actionId);
							}
						}
					}
					String checkedAction = viewAccessGroupForm.getC_strCheckedIDs();
					StringTokenizer stringToken = new StringTokenizer(checkedAction,"$");
					List<String> lstActionlist = new ArrayList(stringToken.countTokens());
					while(stringToken.hasMoreElements()){
						String token = stringToken.nextToken();
						if(!token.equals("")){
							lstActionlist.add(token);
						}
					}
					
					IGroupData igroupData = convertFormToBean(viewAccessGroupForm);
					igroupData.setLastModifiedDate(new Timestamp((new Date()).getTime()));


					List<GroupActionRelData> actionGroupRelDataList = new ArrayList<GroupActionRelData>();
					for(String actionId: lstActionlist){
						
						IActionData actionData = profileBLManager.getActionData(actionId);
						GroupActionRelData data = new GroupActionRelData();

						data.setGroupId(igroupData.getGroupId());
						data.setActionId(actionId);
						data.setActionData(actionData);
						
						actionGroupRelDataList.add(data);
						
					}

					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					
					accessGroupBLManager.updateAccessGroup(igroupData,lstActionlist,staffData,actionAlias);
					lstGroupActionList = lstActionlist;
					request.setAttribute("lstGroupActionList",lstGroupActionList);
					
					request.setAttribute(GROUP_ID, igroupData.getGroupId());
					return mapping.findForward(VIEW_FORWARD);
				}	
			} catch (Exception e) {
				e.printStackTrace();
				Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
				request.setAttribute("errorDetails", errorElements);
			}
			errors.add("fatal", new ActionError("accessgroup.update.failure")); 
			saveErrors(request,errors);
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
	
	private ViewAccessGroupForm convertBeanToForm(IGroupData groupData){
		ViewAccessGroupForm viewAccessGroupForm = null;
		viewAccessGroupForm = new ViewAccessGroupForm();
		viewAccessGroupForm.setGroupId(groupData.getGroupId());
		viewAccessGroupForm.setName(groupData.getName());
		viewAccessGroupForm.setDescription(groupData.getDescription());
		viewAccessGroupForm.setCreateDate(groupData.getCreateDate());
		viewAccessGroupForm.setCreatedByStaffId(groupData.getCreatedByStaffId());
		viewAccessGroupForm.setLastModifiedDate(groupData.getLastModifiedDate());
		viewAccessGroupForm.setLastModifiedByStaffId(groupData.getLastModifiedByStaffId());
		return viewAccessGroupForm;
	}
	
	private IGroupData convertFormToBean(ViewAccessGroupForm viewAccessGroupForm){
		IGroupData groupData  = null;
		if(viewAccessGroupForm!=null){
			groupData = new GroupData();
			groupData.setGroupId(viewAccessGroupForm.getGroupId());
			groupData.setName(viewAccessGroupForm.getName());
			groupData.setDescription(viewAccessGroupForm.getDescription());
			groupData.setCreateDate(viewAccessGroupForm.getCreateDate());
			groupData.setCreatedByStaffId(viewAccessGroupForm.getCreatedByStaffId());
			groupData.setLastModifiedDate(viewAccessGroupForm.getLastModifiedDate());
			groupData.setLastModifiedByStaffId(viewAccessGroupForm.getLastModifiedByStaffId());
		}
		return groupData;
	}
}
