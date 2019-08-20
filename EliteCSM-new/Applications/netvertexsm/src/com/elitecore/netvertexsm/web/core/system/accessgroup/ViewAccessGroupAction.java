package com.elitecore.netvertexsm.web.core.system.accessgroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.netvertexsm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.profilemanagement.ProfileBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleSubBISModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IConfigurationProfileData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ISubBISModuleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.SubBISModuleData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.accessgroup.forms.ViewAccessGroupForm;
import com.elitecore.netvertexsm.web.core.system.cache.ConfigManager;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class ViewAccessGroupAction extends BaseWebAction{
	private static final String STATE = "state";
	private static final String GLYPHICON_GLYPHICON_OK = "glyphicon glyphicon-ok";
	private static final String GLYPHICON_GLYPHICON_REMOVE = "glyphicon glyphicon-remove";
	private static final String SUCCESS_FORWARD = "viewAccessGroup";
	private static final String FAILURE_FORWARD = "failure";
	private static final String MODULE = "VIEW ACCESS GROUP ACTION ";
	private static final String ACTION_ALIAS = ConfigConstant.VIEW_ROLE_ACTION;
	private static final String TEXT = "text";
	private static final String NODES = "nodes";
	private static final String ICON = "icon";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		if(checkAccess(request, ACTION_ALIAS)){
			try {
				AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
				ProfileBLManager profileBLManager = new ProfileBLManager();
				ViewAccessGroupForm viewAccessGroupForm = (ViewAccessGroupForm)form;
				IRoleData roleData = new RoleData();

				String strRoleId = request.getParameter("roleId");
				long roleId;
				if(strRoleId == null){
					roleId = viewAccessGroupForm.getRoleId();
				}else{
					roleId = Long.parseLong(request.getParameter("roleId"));
				}
				List lstGroupActionList = accessGroupBLManager.getRoleActionRelList(roleId);


				if(roleId > 0){
					roleData.setRoleId(roleId);
					roleData = accessGroupBLManager.getRoleData(roleData.getRoleId());
					request.setAttribute("roleData",roleData);

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
					
					List<RoleModuleActionData> roleModuleActions = accessGroupBLManager.getRoleModuleActionRelData(roleId);
					StringBuilder stringBuilder = new StringBuilder();
					for(RoleModuleActionData roleModuleActionData : roleModuleActions){
						JsonObject obj = new JsonObject();
						String aclModuleName = ACLModules.getVal(roleModuleActionData.getModuleName());
						if (aclModuleName == null) {
							Logger.logWarn(MODULE, "Invalid ACL module name found: " + roleModuleActionData.getModuleName());
						} else {
							obj.addProperty(aclModuleName, roleModuleActionData.getActions());
							stringBuilder.append(obj.toString());
							stringBuilder.append("|");
						}
					}
					
					
					
					JsonArray jsonArray = ACLModules.getJsonDataFromMap();
					Map<String,String> actionJsonRelation = new HashMap<String, String>();
					for(JsonElement arr :  jsonArray){
						JsonArray jsonArrayData = new JsonArray();
						getJsonTree(arr,jsonArrayData,roleModuleActions,null);
						JsonArray elements = arr.getAsJsonArray();
						for(JsonElement jsonElemenent : elements){
							if(jsonElemenent.isJsonObject()){
								JsonObject jsonObjectData = (JsonObject) jsonElemenent;
								Set<Entry<String, JsonElement>> jsonEntries = jsonObjectData.entrySet();
								if (jsonEntries != null) {
									for (Entry<String, JsonElement> jsonEntry : jsonEntries) {
										actionJsonRelation.put(jsonEntry.getKey(), jsonArrayData.toString());
									}
								}
							}
						}
					}
					viewAccessGroupForm.setActionJsonRelationMap(actionJsonRelation);

					request.setAttribute("lstRoleActionList",lstGroupActionList);
					request.setAttribute("viewAccessGroupForm",viewAccessGroupForm);
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
	
	/**
	 * This method is used to generate the Json String as per BootStrap's tree view structure. 
	 * From parent-child relation json data, it will generate the TreeView specific json. 
	 * @param jsonElement
	 * @param jsonArrayData
	 */
	public void getJsonTree(JsonElement jsonElement,JsonArray jsonArrayData,List<RoleModuleActionData> roleModuleActions,String setOfActions) {
		if (jsonElement.isJsonObject()) {
			JsonObject jsonObjectData = (JsonObject) jsonElement;
			Set<Entry<String, JsonElement>> jsonEntries = jsonObjectData.entrySet();
			if (jsonEntries != null) {
				for (Entry<String, JsonElement> jsonEntry : jsonEntries) {
					JsonArray jsonArray = null;
					if(jsonEntry.getValue().isJsonArray()){
						if(jsonEntry.getKey().equalsIgnoreCase(ACLAction.UPDATE.name()) == false){
							setOfActions = findActionFromModuleName(jsonEntry.getKey(), roleModuleActions);
						}
						jsonArray = setTreeNodeProperty(jsonEntry.getKey(),jsonArrayData,setOfActions);
					}else{
						jsonArray = setTreeNodePropertyForLeafNodes(jsonEntry.getKey(),jsonArrayData,setOfActions);
					}
					getJsonTree(jsonEntry.getValue(), jsonArray,roleModuleActions,setOfActions);
				}
			}
		}
		else if (jsonElement.isJsonArray()) {
			JsonArray jarr = jsonElement.getAsJsonArray();
			for (JsonElement je : jarr) {
				getJsonTree(je,jsonArrayData,roleModuleActions,setOfActions);
				
			}
		}
	}

	private String findActionFromModuleName(String moduleName, List<RoleModuleActionData> roleModuleActions) {
		String setOfAction = null;
		for(RoleModuleActionData roleModuleActionRel : roleModuleActions){
			String aclModuleName = ACLModules.getVal(roleModuleActionRel.getModuleName());
			if (Strings.isNullOrEmpty(aclModuleName)) {
				Logger.logWarn(MODULE, "Invalid ACL module name found: " + roleModuleActionRel.getModuleName());
			} else {
				if (aclModuleName.equalsIgnoreCase(moduleName)) {
					setOfAction = roleModuleActionRel.getActions();
					break;
				}
			}
		}
		return setOfAction;
	}

	/**
	 * This method will create the TreeNode for BootStrap tree view as json
	 * @param key defines the name of the TreeNode
	 * @param jsonArrayData is used to define the sub children of Parent TreeNode
	 * @return
	 */
	private JsonArray setTreeNodeProperty(String key,JsonArray jsonArrayData,String setOfActions) {
		JsonObject treeNode = new JsonObject();
		treeNode.addProperty(TEXT, key);
		JsonObject stateProperties = new JsonObject();
		treeNode.add(STATE, stateProperties);
		JsonArray childNodes = new JsonArray();
		treeNode.add(NODES,childNodes);
		jsonArrayData.add(treeNode);
		return childNodes;
	}
	
	private JsonArray setTreeNodePropertyForLeafNodes(String key,JsonArray jsonArrayData,String setOfActions) {
		JsonObject treeNode = new JsonObject();
		treeNode.addProperty(TEXT, key);
		JsonObject stateProperties = new JsonObject();
		if(Strings.isNullOrBlank(setOfActions) == false){
			treeNode.addProperty(ICON, GLYPHICON_GLYPHICON_REMOVE);
			if (setOfActions.contains(key)) {
				treeNode.addProperty(ICON, GLYPHICON_GLYPHICON_OK);
			} else if (key.equalsIgnoreCase(ACLAction.UPDATE_BASIC_DETAIL.getVal())
					&& setOfActions.contains(ACLAction.UPDATE.name())) {
				treeNode.addProperty(ICON, GLYPHICON_GLYPHICON_OK);
			}
		}else{
			treeNode.addProperty(ICON, GLYPHICON_GLYPHICON_REMOVE);
		}
		treeNode.add(STATE,stateProperties);
		jsonArrayData.add(treeNode);
		return jsonArrayData;
	}
}
