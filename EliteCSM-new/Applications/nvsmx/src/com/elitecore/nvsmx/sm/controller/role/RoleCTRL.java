package com.elitecore.nvsmx.sm.controller.role;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.constants.Component;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.RoleModuleActionData;
import com.elitecore.corenetvertex.sm.acl.RoleType;
import com.elitecore.corenetvertex.sm.acl.StaffGroupRoleRelData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.model.role.TreeNode;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Restrictions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by aditya on 8/22/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/role")
@Results({
        @Result(name = SUCCESS, type = "redirectAction", params = {"actionName", "role"}),

})
public class RoleCTRL extends RestGenericCTRL<RoleData> {


    private static final String ICON = "checkedIcon";
    private static final String TEXT = "text";
    private static final String NODES = "nodes";
    private static final String CHECKED = "checked";
    private static final String STATE = "state";
    private JsonArray jsonArray = ACLModules.getJsonDataFromMap();

    private Map<String, String> actionJsonRelationForPD = new HashMap<>();

    private Map<String, String> actionJsonRelationForSM = new HashMap<>();

    private String jsonDataArray;

    @Override
    public ACLModules getModule() {
        return ACLModules.ROLE;
    }

    @Override
    public RoleData createModel() {
        return new RoleData();
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setActionJsonRelationForSM(generateModuleToACLMap(ACLModules.getJsonDataFromMap(Component.SM)));
        setActionJsonRelationForPD(generateModuleToACLMap(ACLModules.getJsonDataFromMap(Component.PD)));
    }

    @Override
    public HttpHeaders create() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling create method");
        }
        try {

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            RoleData roleData = (RoleData) getModel();

            roleData.setRoleType(RoleType.STANDARD.name());
            roleData.setSystemGenerated("N");
            List<RoleModuleActionData> roleModuleActionList = roleData.getRoleModuleActionData();
            if (Collectionz.isNullOrEmpty(roleModuleActionList)) {
                roleModuleActionList = generateModuleWiseAclList();
            }

            roleModuleActionList.forEach(roleModuleActionData -> roleModuleActionData.setRoleData(roleData));
            roleData.setRoleModuleActionData(roleModuleActionList);
            return super.create();

        } catch (Exception ex) {
            addActionError("Failed to create Role");
            getLogger().error(getLogModule(), "Error while creating Role. Reason: " + ex.getMessage());
            getLogger().trace(getLogModule(), ex);
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    @Override
    public String edit() { // initUpdate
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called edit()");
        }
        try {
            RoleData model = (RoleData) getModel();
            RoleData roleData = CRUDOperationUtil.get(RoleData.class, model.getId());
            setActionJsonRelationForPD(generateAclModuleToActionsMap(roleData.getRoleModuleActionData(), ACLModules.getJsonDataFromMap(Component.PD)));
            setActionJsonRelationForSM(generateAclModuleToActionsMap(roleData.getRoleModuleActionData(), ACLModules.getJsonDataFromMap(Component.SM)));
            setJsonDataArray(getModuleWiseActionsString(roleData));
            return super.edit();
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information.Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while performing Update Operation");
            return ERROR;
        }
    }


    @Override
    public HttpHeaders show() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called show()");
        }
        RoleData model = (RoleData) getModel();
        if(Strings.isNullOrBlank(model.getId())) {
            addActionError(getModule().getDisplayLabel()+" Not Found with id: " + model.getId());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        RoleData roleData = CRUDOperationUtil.get(RoleData.class, model.getId());

        if (roleData == null) {
            addActionError(getModule().getDisplayLabel()+" Not Found with id: " + model.getId());
            setModel(roleData);
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        setActionJsonRelationForPD(generateAclModuleToActionsMap(roleData.getRoleModuleActionData(), ACLModules.getJsonDataFromMap(Component.PD)));
        setActionJsonRelationForSM(generateAclModuleToActionsMap(roleData.getRoleModuleActionData(), ACLModules.getJsonDataFromMap(Component.SM)));
        setJsonDataArray(getModuleWiseActionsString(roleData));
        return super.show();
    }

    private String getModuleWiseActionsString(RoleData roleData) {
        StringBuilder stringBuilder = new StringBuilder();
        for (RoleModuleActionData roleModuleActionData : roleData.getRoleModuleActionData()) {
            JsonObject obj = new JsonObject();
            String moduleName = ACLModules.getVal(roleModuleActionData.getModuleName());
            if (Strings.isNullOrBlank(moduleName)) {
                LogManager.getLogger().warn(getLogModule(), "Invalid ACL module name found: " + moduleName);
            } else {
                obj.addProperty(moduleName, roleModuleActionData.getActions());
                stringBuilder.append(obj.toString());
                stringBuilder.append(CommonConstants.PIPE);
            }
        }
        return stringBuilder.toString();
    }

    private Map<String, String> generateAclModuleToActionsMap(List<RoleModuleActionData> roleModuleActions, JsonArray jsonArray) {
        Map<String, String> actionJsonRelation = new HashMap<>();

        for (JsonElement arr : jsonArray) {
            JsonArray jsonArrayData = new JsonArray();
            getJsonTree(arr, jsonArrayData, roleModuleActions, null);
            JsonArray elements = arr.getAsJsonArray();
            for (JsonElement jsonElemenent : elements) {
                if (jsonElemenent.isJsonObject()) {
                    JsonObject jsonObjectData = (JsonObject) jsonElemenent;
                    Set<Map.Entry<String, JsonElement>> jsonEntries = jsonObjectData.entrySet();
                    if (jsonEntries != null) {
                        for (Map.Entry<String, JsonElement> jsonEntry : jsonEntries) {
                            actionJsonRelation.put(jsonEntry.getKey(), jsonArrayData.toString());
                        }
                    }
                }
            }
        }
        return actionJsonRelation;
    }


    @Override
    public HttpHeaders update() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called update()");
        }
        try {

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            RoleData model = (RoleData) getModel();
            model.setSystemGenerated("N");
            model.setRoleType(RoleType.STANDARD.name());
            List<RoleModuleActionData> roleModuleActionList = model.getRoleModuleActionData();
            if (Collectionz.isNullOrEmpty(roleModuleActionList)) {
                roleModuleActionList = generateModuleWiseAclList();
            }

            roleModuleActionList.forEach(roleModuleActionData -> roleModuleActionData.setRoleData(model));
            model.setRoleModuleActionData(roleModuleActionList);
            return super.update();
        } catch (Exception ex) {
            addActionError("Failed to Update Role");
            getLogger().error(getLogModule(), "Error while updating Role. Reason: " + ex.getMessage());
            getLogger().trace(getLogModule(), ex);
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    @Override
    @SkipValidation
    public HttpHeaders destroy() { // delete
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called destroy()");
        }

        RoleData model = null;
        try{
            String result = authorize();
            if(result.equals(SUCCESS) == false){
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.OPERATION_NOT_SUPPORTED.code);
            }

            model = (RoleData)getModel();
            if(Strings.isNullOrBlank(model.getId()) == false){
                model = CRUDOperationUtil.get(model.getClass(),model.getId(),getAdditionalCriteria());
                if(model == null){
                    addActionError(getModule().getDisplayLabel()+" Not Found");
                    return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
                }

               org.hibernate.criterion.Criterion roleIdCriterion = Restrictions.eq("roleData.id",model.getId());
               List<StaffGroupRoleRelData> list = CRUDOperationUtil.findAll(StaffGroupRoleRelData.class, roleIdCriterion);
               if(!Collectionz.isNullOrEmpty(list)) {
                   StringBuilder staffNames = new StringBuilder();
                   for (StaffGroupRoleRelData data : list) {
                           if(staffNames.toString().isEmpty()){
                               staffNames.append(data.getStaffData().getUserName());
                           }else{
                               staffNames.append(", ");
                               staffNames.append(data.getStaffData().getUserName());
                           }
                   }
                   addActionError("Role is already associated with '"+staffNames.toString()+"' Staff(s)");
                   return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
               }
            }

            String message = getModule().getDisplayLabel() + " <b><i>" + model.getResourceName() + "</i></b> " + " Deleted";
            CRUDOperationUtil.audit(model,model.getResourceName(), AuditActions.DELETE,getStaffData(),getRequest().getRemoteAddr(),model.getHierarchy(),message);
            CRUDOperationUtil.delete(model);
            HibernateSessionFactory.getSession().flush();
            addActionMessage(getModule().getDisplayLabel()+" removed successfully");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);
        }catch (Exception e) {
            addActionError("Can not perform delete operation. Reason:" + e.getMessage());
            getLogger().error(getLogModule(), "Error while deleting " + getModule().getDisplayLabel() + " for id " + ( model==null ? "" : model.getId()) + ". Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
    }


    private @Nonnull
    List<RoleModuleActionData> generateModuleWiseAclList() {
        List<RoleModuleActionData> roleModuleActionList = new ArrayList<>();
        for (ACLModules aclModule : ACLModules.getRootmodules()) {
            String moduleWiseAccessJson = getRequest().getParameter(aclModule.getDisplayLabel());
            if (moduleWiseAccessJson != null) {
                moduleWiseAccessJson = moduleWiseAccessJson.substring(1, moduleWiseAccessJson.length() - 1);
                TreeNode node = GsonFactory.defaultInstance().fromJson(moduleWiseAccessJson, TreeNode.class);
                generateList(node, roleModuleActionList);
            }
        }
        return roleModuleActionList;
    }

    public Map<String, String> getActionJsonRelationForPD() {
        return actionJsonRelationForPD;
    }

    public void setActionJsonRelationForPD(Map<String, String> actionJsonRelationForPD) {
        this.actionJsonRelationForPD = actionJsonRelationForPD;
    }

    public JsonArray getJsonArray() {
        return jsonArray;
    }

    public void setJsonArray(JsonArray jsonArray) {
        this.jsonArray = jsonArray;
    }


    public String getJsonDataArray() {
        return jsonDataArray;
    }

    public void setJsonDataArray(String jsonDataArray) {
        this.jsonDataArray = jsonDataArray;
    }

    public String getRoleModuleActionDetailsForPD() {
        Gson gson = GsonFactory.defaultInstance();
        RoleData roleData = (RoleData) getModel();
        return gson.toJsonTree(roleData.getRoleModuleActionData(), new TypeToken<List<RoleModuleActionData>>() {
        }.getType()).getAsJsonArray().toString();
    }


    public Map<String, String> getActionJsonRelationForSM() {
        return actionJsonRelationForSM;
    }

    public void setActionJsonRelationForSM(Map<String, String> actionJsonRelationForSM) {
        this.actionJsonRelationForSM = actionJsonRelationForSM;
    }







    public boolean generateList(TreeNode node, List<RoleModuleActionData> roleModuleActionList){
        boolean isUpdateBasicDetail = false;
        if(node.getState().isChecked()==false){
            return false;
        }

        StringBuilder allowedActions = new StringBuilder();
        if(Collectionz.isNullOrEmpty(node.getNodes())==false){
            for(TreeNode subNode: node.getNodes()){
                if(subNode.getState().isChecked()==false){
                    continue;
                }
                ACLAction action = ACLAction.fromName(subNode.getText());
                if(action == null){
                    generateList(subNode, roleModuleActionList);
                    continue;
                }
                if(action == ACLAction.UPDATE){
                    if(Collectionz.isNullOrEmpty(subNode.getNodes()))
                        allowedActions.append(subNode.getText()).append(",");
                    else{
                        isUpdateBasicDetail = generateList(subNode, roleModuleActionList);
                        if(isUpdateBasicDetail == true){
                            allowedActions.append(ACLAction.UPDATE.name()).append(",");
                            allowedActions.append(ACLAction.UPDATE_BASIC_DETAIL.name()).append(",");
                        }
                    }
                }else if(action == ACLAction.UPDATE_BASIC_DETAIL){
                    isUpdateBasicDetail = true;
                }else{
                    allowedActions.append(subNode.getText()).append(",");
                }
            }
        }
        if(node.getText().equals(ACLAction.UPDATE.getVal()) == false){
            if(Strings.isNullOrBlank(allowedActions.toString())) {

                LogManager.getLogger().warn(getLogModule(), "No Action is configured with Module: " + node.getText() + " .So skipping further processing");
                return isUpdateBasicDetail;
            }

            RoleModuleActionData roleModuleActionData = new RoleModuleActionData();
            roleModuleActionData.setModuleName(ACLModules.fromVal(node.getText()).name());
            roleModuleActionData.setActions(allowedActions.toString());
            roleModuleActionList.add(roleModuleActionData);
        }
        return isUpdateBasicDetail;
    }


    /*
     * This method will create the TreeNode for BootStrap tree view as json
     * @param key defines the name of the TreeNode
     * @param jsonArrayData is used to define the sub children of Parent TreeNode
     * @return
     */
    private static JsonArray setTreeNodeProperty(String key, JsonArray jsonArrayData) {
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

    private static JsonArray setTreeNodePropertyForLeafNodes(String key, JsonArray jsonArrayData) {
        JsonObject treeNode = new JsonObject();
        treeNode.addProperty(TEXT, key);
        treeNode.addProperty(ICON, "glyphicon glyphicon-check");
        treeNode.addProperty("multiSelect", true);
        JsonObject states = new JsonObject();
        states.addProperty("disabled", false);
        jsonArrayData.add(treeNode);
        return jsonArrayData;
    }


    /* * This method is used to generate the Json String as per BootStrap's tree view structure.
     * From parent-child relation json data, it will generate the TreeView specific json.
     * @param jsonElement
     * @param jsonArrayData
     */
    private  void getJsonTree(JsonElement jsonElement, JsonArray jsonArrayData) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObjectData = (JsonObject) jsonElement;
            Set<Map.Entry<String, JsonElement>> jsonEntries = jsonObjectData.entrySet();
            if (jsonEntries != null) {
                for (Map.Entry<String, JsonElement> jsonEntry : jsonEntries) {
                    JsonArray jsonArray;
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
            JsonArray jsonArr = jsonElement.getAsJsonArray();
            for (JsonElement je : jsonArr) {
                getJsonTree(je,jsonArrayData);

            }
        }
    }

    private   Map<String, String> generateModuleToACLMap(JsonArray jsonArray) {
        Map<String,String> actionJsonRelation = new HashMap<>();
        for(JsonElement arr :  jsonArray){
            JsonArray jsonArrayData = new JsonArray();
            getJsonTree(arr,jsonArrayData);
            JsonArray elements = arr.getAsJsonArray();
            for(JsonElement jsonElement : elements){
                if(jsonElement.isJsonObject()){
                    JsonObject jsonObjectData = (JsonObject) jsonElement;
                    Set<Map.Entry<String, JsonElement>> jsonEntries = jsonObjectData.entrySet();
                    if (jsonEntries != null) {
                        for (Map.Entry<String, JsonElement> jsonEntry : jsonEntries) {
                            actionJsonRelation.put(jsonEntry.getKey(), jsonArrayData.toString());
                        }
                    }
                }
            }
        }
        return actionJsonRelation;
    }


    private void getJsonTree(JsonElement jsonElement,JsonArray jsonArrayData,List<RoleModuleActionData> roleModuleActions,String setOfActions) {
        if (jsonElement.isJsonObject()) {
            JsonObject jsonObjectData = (JsonObject) jsonElement;
            Set<Map.Entry<String, JsonElement>> jsonEntries = jsonObjectData.entrySet();
            if (jsonEntries != null) {
                for (Map.Entry<String, JsonElement> jsonEntry : jsonEntries) {
                    JsonArray jsonArray;
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

    private JsonArray setTreeNodeProperty(String key, JsonArray jsonArrayData, String setOfActions) {
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

    private JsonArray setTreeNodePropertyForLeafNodes(String key, JsonArray jsonArrayData, String setOfActions) {
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

    private String isModuleExists(String key,List<RoleModuleActionData> roleModuleActions) {
        String setOfAction = null;
        for(RoleModuleActionData roleModuleActionRel : roleModuleActions){
            if(ACLModules.getVal(roleModuleActionRel.getModuleName()) != null && ACLModules.getVal(roleModuleActionRel.getModuleName()).equalsIgnoreCase(key)){
                setOfAction = roleModuleActionRel.getActions();
            }
        }
        return setOfAction;
    }

}
