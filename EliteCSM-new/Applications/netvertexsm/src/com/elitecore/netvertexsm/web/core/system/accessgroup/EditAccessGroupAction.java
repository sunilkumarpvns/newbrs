package com.elitecore.netvertexsm.web.core.system.accessgroup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.netvertexsm.blmanager.core.system.accessgroup.AccessGroupBLManager;
import com.elitecore.netvertexsm.blmanager.core.system.profilemanagement.ProfileBLManager;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.IRoleData;
import com.elitecore.netvertexsm.datamanager.core.system.accessgroup.data.RoleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModelModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModuleSubBISModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleSubBISModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ISubBISModuleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.SubBISModuleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.SubBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.netvertexsm.util.constants.ConfigConstant;
import com.elitecore.netvertexsm.util.exception.EliteExceptionUtils;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.accessgroup.forms.ViewAccessGroupForm;
import com.elitecore.netvertexsm.web.core.system.login.forms.SystemLoginForm;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.apache.struts.action.ActionError;
import org.apache.struts.action.ActionErrors;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.StringTokenizer;


public class EditAccessGroupAction extends BaseWebAction {
	private static final String STATE = "state";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final String EDIT_FORWARD = "editAccessGroup";
	private static final String VIEW_FORWARD = "viewAccessGroup";
	private static final String ACTION_ALIAS = ConfigConstant.UPDATE_ROLE_ACTION;
	private static final String TEXT = "text";
	private static final String NODES = "nodes";
	private static final String CHECKED = "checked";
	public static final String PIPE_SIGN = "|";


	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws Exception{
		Logger.logTrace(MODULE,"Enter execute method of :"+getClass().getName());
		
		if(checkAccess(request, ACTION_ALIAS)){	
			AccessGroupBLManager accessGroupBLManager = new AccessGroupBLManager();
			ProfileBLManager profileBLManager = new ProfileBLManager();
			Long roleId = Long.valueOf(request.getParameter("roleId"));
			ActionErrors errors = new ActionErrors();

			List lstRoleActionList = accessGroupBLManager.getRoleActionRelList(roleId);
			
			try {
				ViewAccessGroupForm viewAccessGroupForm = (ViewAccessGroupForm)form;
				IRoleData roleData = new RoleData();
				List businessModelList = profileBLManager.getBusinessModelList();
				HashMap modelMap = new HashMap();
				HashMap profileMap = new HashMap();
				profileMap.put("name",profileMap);
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

				if(viewAccessGroupForm.getAction() == null){
					if(roleId != null && roleId > 0){
						roleData.setRoleId(Long.valueOf(roleId));
						roleData = accessGroupBLManager.getRoleData(roleData);
						viewAccessGroupForm = convertBeanToForm(roleData);
						viewAccessGroupForm.setListBusinessModel(businessModelList);
						
						
						List<RoleModuleActionData> roleModuleActions = accessGroupBLManager.getRoleModuleActionRelData(roleId);
						StringBuilder stringBuilder = new StringBuilder();
						for(RoleModuleActionData roleModuleActionData : roleModuleActions){
							JsonObject obj = new JsonObject();
							String moduleName = ACLModules.getVal(roleModuleActionData.getModuleName());
							if(Strings.isNullOrBlank(moduleName)){
								LogManager.getLogger().warn(MODULE, "Invalid ACL module name found: "+ moduleName);
							}else{
								obj.addProperty(moduleName, roleModuleActionData.getActions());
								stringBuilder.append(obj.toString());
								stringBuilder.append(PIPE_SIGN);
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
						viewAccessGroupForm.setJsonDataArray(stringBuilder.toString());
						request.setAttribute("viewAccessGroupForm",viewAccessGroupForm);
						roleData = accessGroupBLManager.getRoleData(roleData.getRoleId());
						request.setAttribute("roleData",roleData);
						request.setAttribute("lstRoleActionList",lstRoleActionList);
					}
					return mapping.findForward(EDIT_FORWARD);
				}else if(viewAccessGroupForm.getAction().equalsIgnoreCase("update")){
					//Adding policy designer access rights
				
					List<RoleModuleActionData> roleModuleActionList = Collectionz.newArrayList();
					for(ACLModules aclModule:ACLModules.getRootmodules()){
						String accessGroupJSON = request.getParameter(aclModule.getDisplayLabel());	
						if(accessGroupJSON!=null){
							accessGroupJSON = accessGroupJSON.substring(1, accessGroupJSON.length()-1);
					     	TreeNode node = GsonFactory.defaultInstance().fromJson(accessGroupJSON, TreeNode.class);
						    AccessGroupBLManager.generateList(node, roleModuleActionList);
						}
						
					}
					
					
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
					List lstActionlist = new ArrayList(stringToken.countTokens());
					while(stringToken.hasMoreElements()){
						String token = stringToken.nextToken();
						if(!token.equals("")){
							lstActionlist.add(token);
						}
					}
					IRoleData iroleData = convertFormToBean(viewAccessGroupForm);
					iroleData.setLastModifiedDate(new Timestamp((new Date()).getTime()));



					IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
					String actionAlias = ACTION_ALIAS;
					accessGroupBLManager.updateAccessGroup(iroleData,lstActionlist,roleModuleActionList,staffData,actionAlias);
					lstRoleActionList = lstActionlist;
					request.setAttribute("lstRoleActionList",lstRoleActionList);

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
	
	private ViewAccessGroupForm convertBeanToForm(IRoleData roleData){
		ViewAccessGroupForm viewAccessGroupForm = null;
		if(roleData!=null){
			viewAccessGroupForm = new ViewAccessGroupForm();
			viewAccessGroupForm.setRoleId(roleData.getRoleId());
			viewAccessGroupForm.setName(roleData.getName().trim());
			viewAccessGroupForm.setDescription(roleData.getDescription());
			viewAccessGroupForm.setCreateDate(roleData.getCreateDate());
			viewAccessGroupForm.setCreatedByStaffId(roleData.getCreatedByStaffId());
			viewAccessGroupForm.setLastModifiedDate(roleData.getLastModifiedDate());
			viewAccessGroupForm.setLastModifiedByStaffId(roleData.getLastModifiedByStaffId());
		}
		return viewAccessGroupForm;
	}
	
	private IRoleData convertFormToBean(ViewAccessGroupForm viewAccessGroupForm){
		IRoleData roleData  = null;
		if(viewAccessGroupForm!=null){
			roleData = new RoleData();
			roleData.setRoleId(viewAccessGroupForm.getRoleId());
			roleData.setName(viewAccessGroupForm.getName().trim());
			roleData.setDescription(viewAccessGroupForm.getDescription());
			roleData.setCreateDate(viewAccessGroupForm.getCreateDate());
			roleData.setCreatedByStaffId(viewAccessGroupForm.getCreatedByStaffId());
			roleData.setLastModifiedDate(viewAccessGroupForm.getLastModifiedDate());
			roleData.setLastModifiedByStaffId(viewAccessGroupForm.getLastModifiedByStaffId());
		}
		return roleData;
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
							setOfActions = isModuleExists(jsonEntry.getKey(),roleModuleActions);
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

	private String isModuleExists(String key,List<RoleModuleActionData> roleModuleActions) {
		String setOfAction = null;
		for(RoleModuleActionData roleModuleActionRel : roleModuleActions){
			if(ACLModules.getVal(roleModuleActionRel.getModuleName()) != null && ACLModules.getVal(roleModuleActionRel.getModuleName()).equalsIgnoreCase(key)){
				setOfAction = roleModuleActionRel.getActions();
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
		if(Strings.isNullOrBlank(setOfActions) == false){
			stateProperties.addProperty(CHECKED, true);
		}
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
		
		if (Strings.isNullOrBlank(setOfActions) == false) {
			stateProperties.addProperty(CHECKED, false);
			if (setOfActions.contains(key)) {
				stateProperties.addProperty(CHECKED, true);
			} else if (key.equalsIgnoreCase(ACLAction.UPDATE_BASIC_DETAIL.getVal())
					&& setOfActions.contains(ACLAction.UPDATE.name())) {
				stateProperties.addProperty(CHECKED, true);
			}
			} else {
			stateProperties.addProperty(CHECKED, false);
		}
		treeNode.add(STATE,stateProperties);
		jsonArrayData.add(treeNode);
		return jsonArrayData;
	}
}
