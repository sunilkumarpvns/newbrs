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

import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.netvertexsm.blmanager.core.system.profilemanagement.ProfileBLManager;
import com.elitecore.netvertexsm.datamanager.DataManagerException;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.BISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IActionData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModelModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.IBISModuleSubBISModuleRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ISubBISModuleActionRelData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.ISubBISModuleData;
import com.elitecore.netvertexsm.datamanager.core.system.profilemanagement.data.SubBISModuleData;
import com.elitecore.netvertexsm.util.logger.Logger;
import com.elitecore.netvertexsm.web.core.base.BaseWebAction;
import com.elitecore.netvertexsm.web.core.system.accessgroup.forms.CreateAccessGroupForm;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class InitCreateAccessGroupAction extends BaseWebAction{
	private static final String SUCCESS_FORWARD = "initCreateAccessGroup";
	private static final String MODULE = "Create Access Group Action";
	private static final String ICON = "checkedIcon";
	private static final String TEXT = "text";
	private static final String NODES = "nodes";
	
	public ActionForward execute(ActionMapping mapping,ActionForm form,HttpServletRequest request,HttpServletResponse response) throws DataManagerException{
		Logger.logTrace(MODULE,"Enter execute method of "+getClass().getName());
		
		try {
			ProfileBLManager profileBLManager = new ProfileBLManager();
			CreateAccessGroupForm createAccessGroupForm = (CreateAccessGroupForm)form;			
			createAccessGroupForm.setDescription(getDefaultDescription(request));
			List businessModelList = profileBLManager.getBusinessModelList();
			JsonArray jsonArray = ACLModules.getJsonDataFromMap();
			Map<String,String> actionJsonRelation = new HashMap<String, String>();
			for(JsonElement arr :  jsonArray){
				JsonArray jsonArrayData = new JsonArray();
				getJsonTree(arr,jsonArrayData);
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
			createAccessGroupForm.setActionJsonRelationMap(actionJsonRelation);
			
			createAccessGroupForm.setListBusinessModel(businessModelList);
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
							actionObjectMap.put("actionId",actionData.getActionId());
							actionObjectMap.put("actionName",actionData.getName());
							actionObjectMap.put("actionStatus",actionData.getStatus());
							actionMap.put(((ISubBISModuleActionRelData)subBusinessModuleActionRelList.get(l)).getActionId(),actionObjectMap);
						}
						subModuleObjectMap.put("actionMap",actionMap);
						subModuleMap.put(((IBISModuleSubBISModuleRelData)businessModuleSubBISModuleRelList.get(k)).getSubBusinessModuleId(),subModuleObjectMap);
					}
					moduleObjectMap.put("subModuleMap",subModuleMap);
					moduleMap.put(((IBISModelModuleRelData)businessModelModuleRelList.get(j)).getBusinessModuleId(),moduleObjectMap);
				}
				modelObjectMap.put("moduleMap",moduleMap);
				modelMap.put(((IBISModelData)businessModelList.get(i)).getBusinessModelId(),modelObjectMap);
			}
			profileMap.put("modelMap",modelMap);
			request.setAttribute("profileMap",profileMap);
			return mapping.findForward(SUCCESS_FORWARD);
		} catch (DataManagerException hExp) {
			hExp.printStackTrace();
			Logger.logError(MODULE,"Error during data Manager operation, reason : "+hExp.getMessage());
		}
		return mapping.findForward(SUCCESS_FORWARD);
	}
	/**
	 * This method is used to generate the Json String as per BootStrap's tree view structure. 
	 * From parent-child relation json data, it will generate the TreeView specific json. 
	 * @param jsonElement
	 * @param jsonArrayData
	 */
	public void getJsonTree(JsonElement jsonElement,JsonArray jsonArrayData) {
		if (jsonElement.isJsonObject()) {
			JsonObject jsonObjectData = (JsonObject) jsonElement;
			Set<Entry<String, JsonElement>> jsonEntries = jsonObjectData.entrySet();
			if (jsonEntries != null) {
				for (Entry<String, JsonElement> jsonEntry : jsonEntries) {
					JsonArray jsonArray = null;
					if(jsonEntry.getValue().isJsonArray()){
						jsonArray = setTreeNodeProperty(jsonEntry.getKey(),jsonArrayData);
					}else{
						jsonArray = setTreeNodePropertyForLeafNodes(jsonEntry.getKey(),jsonArrayData);
					}
					getJsonTree(jsonEntry.getValue(), jsonArray);
				}
			}
		}
		else if (jsonElement.isJsonArray()) {
			JsonArray jarr = jsonElement.getAsJsonArray();
			for (JsonElement je : jarr) {
				getJsonTree(je,jsonArrayData);
				
			}
		}
	}
	/**
	 * This method will create the TreeNode for BootStrap tree view as json
	 * @param key defines the name of the TreeNode
	 * @param jsonArrayData is used to define the sub children of Parent TreeNode
	 * @return
	 */
	private JsonArray setTreeNodeProperty(String key,JsonArray jsonArrayData) {
		JsonObject treeNode = new JsonObject();
		treeNode.addProperty(TEXT, key);
		treeNode.addProperty(ICON, "glyphicon glyphicon-check");
		treeNode.addProperty("multiSelect", true);
		JsonObject states = new JsonObject();
		states.addProperty("disabled", false);
		treeNode.add("state", states);
		JsonArray childNodes = new JsonArray();
		treeNode.add(NODES,childNodes);
		jsonArrayData.add(treeNode);
		return childNodes;
	}
	
	private JsonArray setTreeNodePropertyForLeafNodes(String key,JsonArray jsonArrayData) {
		JsonObject treeNode = new JsonObject();
		treeNode.addProperty(TEXT, key);
		treeNode.addProperty(ICON, "glyphicon glyphicon-check");
		treeNode.addProperty("multiSelect", true);
		JsonObject states = new JsonObject();
		states.addProperty("disabled", false);
		jsonArrayData.add(treeNode);
		return jsonArrayData;
	}
	
	
		
}
