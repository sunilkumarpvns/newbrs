package com.elitecore.nvsmx.sm.controller;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.Replicable;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.NewGroupPredicate;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.interceptor.SingleOperationAuthorizedHandler;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;
import com.opensymphony.xwork2.Preparable;
import com.opensymphony.xwork2.Validateable;
import com.thoughtworks.xstream.security.ExplicitTypePermission;
import com.thoughtworks.xstream.security.TypePermission;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.apache.struts2.rest.handler.XStreamPermissionProvider;
import org.hibernate.criterion.SimpleExpression;
import org.hibernate.exception.ConstraintViolationException;
import org.hibernate.exception.GenericJDBCException;


import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_ITALIC_TEXT_TAG;
import static com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil.ID_PROPERTY;
import static com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil.MODE_CREATE;

/**
 * A class that will perform Create, Update, Delete, view and audit of the Struts and Rest request. Extend this class for
 * providing struts and rest configuration for the specific module
 * Created by Ishani on 16/8/17.
 */

@InterceptorRef(value = "restStack")
public abstract class RestGenericCTRL<T extends ResourceData> extends ActionSupport implements ModelDriven<Object>,Validateable, ServletRequestAware, ServletResponseAware, Preparable, XStreamPermissionProvider {


    public static final String METHOD_EDIT = "edit";
    public static final  String METHOD_SHOW = "show";
    public static final  String METHOD_INDEX = "index";
    public static final  String METHOD_EDITNEW = "editNew";
    public static final String REDIRECT_ACTION = "redirectAction";
    public static final String UPDATED = " Updated";
    public static final String CREATED = " Created";
    private static final String ADMIN = "admin";
    protected static final String ASTERISK = "*";
    protected static final String NON_DESTROYABLE_RESOURCE_ALLOWED_METHOD = "GET,POST,PUT";
    protected static final String NON_CREATEABLE_RESOURCE_ALLOWED_METHOD = "GET,PUT";
    protected static final String ALLOWED_METHOD_HEADER = "Allow";
    public static final int UNIQUE_CONSTRAINT_ERRORCODE = 1;
    public static final String DELETED = " Deleted";

    private Collection<T> list;

    private T model = createModel();
    private String actionChainUrl;
    private HttpServletRequest request;
    private HttpServletResponse response;
    private List<String> oldGroupsFromDB = Collectionz.newArrayList();
    private String[] ids;

    @SuppressWarnings("unchecked")
    @SkipValidation
    public HttpHeaders index() { // list or search
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called index()");
        }
        try{
            list = CRUDOperationUtil.findAll((Class<T>) model.getClass());
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while fetching "+getModule().getDisplayLabel()+" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform Search Operation");
        }
        return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
    }

    @SkipValidation
    public String editNew() { // initCreate
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called editNew()");
        }
        if(model != null) {
            model = createModel();
        }
        setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
        return NVSMXCommonConstants.REDIRECT_URL;
    }

    // POST /sm/module/action
    public HttpHeaders create() { // create
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Creating "+getModule().name()+" with name: "+model.getResourceName());
        }
        try {
            String result = authorize();
            if(result.equals(SUCCESS) == false){
                setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
            if (model != null) {
                StaffData staffData = getStaffData();
                model.setCreatedDateAndStaff(staffData);
            }
            String groups = model.getGroupNames();
            model = CRUDOperationUtil.merge(model);
            model.setGroupNames(groups);
            String message = getAuditMessage(model,CREATED);
            CRUDOperationUtil.audit(model,model.getResourceName(),AuditActions.CREATE,getStaffData(),request.getRemoteAddr(),model.getHierarchy(),message);
            addActionMessage(getModule().getDisplayLabel()+" created successfully");
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(model.getId()));
            CRUDOperationUtil.flushSession();
            return new DefaultHttpHeaders(REDIRECT_ACTION).setLocationId(model.getId());
        }catch (ConstraintViolationException cve){
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information. Reason: "+cve.getMessage());
            getLogger().trace(getLogModule(),cve);
            addActionError("Fail to perform create Operation.Reason: constraint "+cve.getConstraintName()+" violated");
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform create Operation");
        }
        return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
    }

    @SkipValidation
    public HttpHeaders copymodel() { // create
        if (getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "copyModel() Called for: " + model.getResourceName());
        }

        try {

            if (Strings.isNullOrBlank(model.getId())) {
                getLogger().error(getLogModule(), "Error while creating " + getModule().getDisplayLabel() + " with id: " + model.getId() + ". Reason: Not found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
            String resourceName = model.getResourceName();//store update name
            model = CRUDOperationUtil.get((Class<T>) model.getClass(), model.getId());
            if (model == null) {
                getLogger().error(getLogModule(), "Error while creating " + getModule().getDisplayLabel() + " with id: " + model.getId() + ". Reason: Not found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }

            if((model instanceof Replicable) == false) {
                getLogger().error(getLogModule(), "Error while creating " + getModule().getDisplayLabel() + ". Reason: copy operation not supported");
                addActionError("Fail to perform copy Operation. Reason: copy operation not supported");
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);
            }

            T newEntity = ((Replicable)model).copyModel();
            if(Objects.isNull(newEntity)){
                getLogger().error(getLogModule(), "Error while creating " + getModule().getDisplayLabel() + ". Reason: copy operation not supported");
                addActionError("Fail to perform copy Operation. Reason: copy operation not supported");
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);
            }
            newEntity.setResourceName(resourceName);
            setModel(newEntity);
            String authorizationResult = authorize();
            if (authorizationResult.equals(SUCCESS) == false) {
                return new DefaultHttpHeaders(SUCCESS).disableCaching();
            }

            if (newEntity != null) {
                StaffData staffData = getStaffData();
                newEntity.setCreatedDateAndStaff(staffData);
            }
            CRUDOperationUtil.save(newEntity);
            String message = getAuditMessage(newEntity,CREATED);
            CRUDOperationUtil.audit(newEntity, newEntity.getResourceName(), AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), newEntity.getHierarchy(), message);
            CRUDOperationUtil.flushSession();
            addActionMessage(getModule().getDisplayLabel() + " created successfully");
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(newEntity.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).setLocationId(newEntity.getId());
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while creating " + getModule().getDisplayLabel() + " information. Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Fail to perform create Operation");
        }
        return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
    }

    public String edit() { // initUpdate
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called edit()");
        }
        try {
            if(Strings.isNullOrBlank(model.getId()) == false) {
                model = CRUDOperationUtil.get((Class<T>)model.getClass(), model.getId());
            }
            setActionChainUrl(getRedirectURL(METHOD_EDIT));
            return NVSMXCommonConstants.REDIRECT_URL;
        }catch(Exception e){
            getLogger().error(getLogModule(),"Error while updating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform Update Operation");
            return ERROR;
        }

    }
    // PUT /sm/module/action/id
    public HttpHeaders update() { // update
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"updating "+getModule().getDisplayLabel()+" with id: "+ model.getId());
        }
        try {

            String result = authorize();
            if(result.equals(SUCCESS) == false){
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            if (isEntityExists(model.getId()) == false) {
                getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ model.getId()+". Reason: Not found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
            String groups = model.getGroupNames();
            model.setModifiedDateAndStaff(getStaffData());
            model=CRUDOperationUtil.merge(model);
            model.setGroupNames(groups);
            String message = getAuditMessage(model, UPDATED);
            CRUDOperationUtil.audit(model,model.getResourceName(),AuditActions.UPDATE,getStaffData(),request.getRemoteAddr(),model.getHierarchy(),message);
            addActionMessage(getModule().getDisplayLabel()+" updated successfully");
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(model.getId()));
            CRUDOperationUtil.flushSession();
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }catch (ConstraintViolationException cve){
            getLogger().error(getLogModule(),"Error while updating "+ getModule().getDisplayLabel() +" information. Reason: "+cve.getMessage());
            getLogger().trace(getLogModule(),cve);
            addActionError("Fail to perform update Operation.Reason: constraint "+cve.getConstraintName()+" violated");
        }catch (GenericJDBCException e){
            getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform Update Operation. Reason: " + e.getSQLException().getMessage());
        } catch (Exception e){
            getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform Update Operation. Reason: " + e.getMessage());
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

    }

    protected String getAuditMessage(T model, String action) {
        return getModule().getDisplayLabel() + "  " + START_BOLD_TEXT_TAG + START_ITALIC_TEXT_TAG + model.getResourceName() + CLOSE_BOLD_TEXT_TAG + CLOSE_ITALIC_TEXT_TAG + action;
    }

    protected boolean prepareAndValidateDestroy(T resourceData){
        return true;
    }


    // DELETE /sm/module/action/id
    /*Destroy method will perform the delete operation for the entities flow will be like this
    * if multiple ids are passed then it will perform the delete operation one by one
    *   else
    * if multiple ids are not passed than it will check the id that is passed in url
    * & perform the delete operation for that id
    *
    *
    * */
    @SkipValidation
    public HttpHeaders destroy() { // delete
        try {
            if (Arrayz.isNullOrEmpty(getIds()) && (Strings.isNullOrBlank(model.getId()) || ASTERISK.equals(model.getId()))) {
                getLogger().error(getLogModule(), "Can not perform delete operation. Reason: Id not found");
                addActionError(getModule().getDisplayLabel() + " Id Not Found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
            DefaultHttpHeaders httpHeaders = null;
            if (Arrayz.isNullOrEmpty(getIds()) == false) {
                httpHeaders = performAuthorizationAndDelete(getIds());
            }else if (Strings.isNullOrBlank(model.getId()) == false) {
                httpHeaders = performAuthorizationAndDelete(model.getId());
            }
            if (httpHeaders != null) {
                return httpHeaders;
            }
            addActionMessage(getModule().getDisplayLabel() + " removed successfully");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);
        } catch (ConstraintViolationException e){
            addActionError("Can not perform delete operation. Reason: " + model.getResourceName() +" is associated to some other entity");
            getLogger().error(getLogModule(), "Error while " + getModule().getDisplayLabel() + " for id " + model.getId() + " . Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        } catch (Exception e) {
            addActionError("Can not perform delete operation. Reason:" + e.getMessage());
            getLogger().error(getLogModule(), "Error while " + getModule().getDisplayLabel() + " for id " + model.getId() + " . Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    private DefaultHttpHeaders performAuthorizationAndDelete(String... modelIds) throws Exception {
        for (String modelId : modelIds) {
            if (Strings.isNullOrBlank(modelId)) {
                getLogger().error(getLogModule(), "Can not perform delete operation. Reason: Id not found");
                addActionError(getModule().getDisplayLabel() + " Id Not Found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(getLogModule(), "Deleting " + getModule().getDisplayLabel() + " with Id: " + modelId);
            }
            model = getModelFromId(modelId);
            if (model == null) {
                getLogger().error(getLogModule(), "Can not perform delete operation. Reason:"+getModule().getDisplayLabel()+" not found with id: "+modelId);
                addActionError(getModule().getDisplayLabel() + " Not Found with Id: " + modelId);
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                return new DefaultHttpHeaders(SUCCESS).withStatus(401);
            }
            if (prepareAndValidateDestroy(model) == false) {
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
            String message = getAuditMessage(model, DELETED);
            CRUDOperationUtil.audit(model, model.getResourceName(), AuditActions.DELETE, getStaffData(), request.getRemoteAddr(), model.getHierarchy(), message);
            CRUDOperationUtil.delete(model);
            CRUDOperationUtil.flushSession();
        }
        return null;
    }

    private T getModelFromId(String id) {
        return CRUDOperationUtil.get((Class<T>) this.model.getClass(), id, getAdditionalCriteria());
    }

    public HttpHeaders show() { // View
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called show()");

        }
        try {
            T resourceInDB = CRUDOperationUtil.get((Class<T>)model.getClass(),model.getId(),getAdditionalCriteria());
            if (resourceInDB == null) {
                addActionError(getModule().getDisplayLabel()+" Not Found with id: " + model.getId());
                setModel(resourceInDB);
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
            if (resourceInDB.getGroups() != null) {
                String belongingsGroups = GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(resourceInDB.getGroups()));
                resourceInDB.setGroupNames(belongingsGroups);
            }
            setModel(resourceInDB);
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
        } catch (Exception e) {
            addActionError("Fail to view " + getModule().getDisplayLabel() + " for id ");
            getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + " for id " + model.getId() + " . Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);

    }

    @SkipValidation
    public String authorize() throws Exception {
        String userName = getStaffData().getUserName();
        if(LogManager.getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Performing authorization for user: " + userName);
        }
        // validate admin user
        String result = SUCCESS;
        ResourceData resource = (ResourceData) getModel();
        if (Strings.isNullOrBlank(resource.getGroups())) {
            resource.setGroups(com.elitecore.corenetvertex.constants.CommonConstants.DEFAULT_GROUP_ID);
        }
        List<String> entityGroups = CommonConstants.COMMA_SPLITTER.split(resource.getGroups());
        //validate group ids from database
        List<String> nonExistingGroups = findNonExistingGroupsFromDB(entityGroups);
        if (Collectionz.isNullOrEmpty(nonExistingGroups) == false) {
            addActionError("Groups with Id: " + nonExistingGroups + " does not exist");
            result = INPUT;
        } else if (userName != null && userName.equalsIgnoreCase(ADMIN) == false) {
                //validate staff group role relation for action
            result = validateUserBasedOnStaffGroupRoleAction(entityGroups);

        }
        if(SUCCESS.equals(result)){
            if (resource.getGroups() != null) {
                String belongingsGroups = GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(resource.getGroups()));
                resource.setGroupNames(belongingsGroups);
            }
        }
        if(INPUT.equals(result)){
            /* If returned result is INPUT, it means authorization is failed, then we need prepare the prerequisite values again for respective module respective operation.
            * So, calling the prepareValuesForSubClass() method for INPUT result.*/
            prepareValuesForSubClass();
        }
        return result;
    }

    private String validateUserBasedOnStaffGroupRoleAction(List<String> entityGroups) {
        List<String> oldGroups = getOldGroupsFromDB();
        String methodName = getMethodName();

        List<GroupData> staffBelongingGroups = Collectionz.newArrayList();
        staffBelongingGroups.addAll(getStaffData().getGroupDatas());
        List<String> notAllowedGroups = findNotAllowedGroups(getModule(), methodName, staffBelongingGroups,entityGroups,oldGroups);
        if (Collectionz.isNullOrEmpty(notAllowedGroups) == false) {
            String groupNames = GroupDAO.getGroupNames(notAllowedGroups);
            String message = getModule().getDisplayLabel() + " '" + methodName + "' not allowed for '" + groupNames + "' Group";
            LogManager.getLogger().warn(getLogModule(), message);
            addActionError(message);
            return INPUT;
        }
        return SUCCESS.trim();
    }


    /**
     * Override this method in order to set the data for create and update pages
     * @throws Exception
     */
    @SkipValidation
    public void prepareValuesForSubClass() throws Exception{

    }

    public void validate() {
        if(isDuplicateEntity("name",model.getResourceName() ,getMethodName())){
            addFieldError("name","Name already exists");
        }
        validateIdExistForCreateMode();
    }

    public void validateIdExistForCreateMode() {
		if(MODE_CREATE.equalsIgnoreCase(getMethodName()) && isIdExist()){
			addFieldError("id","Id already exists");
		}
	}

    protected String getMethodName() {
        String tempMethod = request.getParameter("_method");
        ACLAction action = null;
        if (Strings.isNullOrBlank(tempMethod) == false) {
            action = ACLAction.fromName(tempMethod.toUpperCase());
        }
        if (action == null) {
            LogManager.getLogger().debug(getLogModule(),"Request : " + request.getMethod());
            action = ACLAction.fromName(request.getMethod());
        }
        if(action != null) {
            return action.getVal();
        }
        return "";
    }

    protected boolean isDuplicateEntity(String propertyName, String value, String mode){
        try {
            return CRUDOperationUtil.isDuplicateProperty(mode, model.getClass(), model.getId(), value, propertyName);
        } catch (Exception e) {
            addActionError("Fail to perform duplicate entity check");
            getLogger().error(getLogModule(),"Fail to perform duplicate entity check.Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        return false;
    }

    protected boolean isIdExist(){
        try {
            return CRUDOperationUtil.isExist(model.getClass(), ID_PROPERTY, model.getId());
        } catch (Exception e) {
            addActionError("Fail to perform duplicate entity check with property: " + ID_PROPERTY);
            getLogger().error(getLogModule(),"Fail to perform duplicate entity check with property: " + ID_PROPERTY + ". Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        return false;
    }

    protected boolean isAdminUser(StaffData staffData){
        return NVSMXCommonConstants.ADMIN_STAFF_ID.equals(staffData.getId());
    }

    public abstract ACLModules getModule();

    public abstract T createModel();

    public String getLogModule(){
        return getModule().name() + "-CTRL";
    }



    @Override
    public Object getModel() {
        return list == null ? model : list;
    }

    public void setModel(T o) {
        this.model = o;
    }

    public Collection<T> getList() {
        return list;
    }

    public void setList(Collection<T> list) {
        this.list = list;
    }


    public String getActionChainUrl() {
        return actionChainUrl;
    }

    @ActionChain(name = "actionChainUrlMethod")
    public void   setActionChainUrl(String actionChainUrl) {
        this.actionChainUrl = actionChainUrl;
    }


    public String getDataListAsJson() {
            Gson gson = GsonFactory.defaultInstance();
        JsonArray modelJson = gson.toJsonTree(list,new TypeToken<List<T>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    @Override
    public void setServletRequest(HttpServletRequest httpServletRequest) {
        this.request = httpServletRequest;
    }

    public HttpServletRequest getRequest() {
        return this.request;
    }

    public HttpServletResponse getResponse() {
        return response;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    public StaffData getStaffData(){
        return (StaffData) getRequest().getSession().getAttribute(Attributes.STAFF_DATA);
    }

    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[0]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    public String getRedirectToParentURL(String parentId) {
        StringBuilder sb = new StringBuilder();
        sb.append(CommonConstants.FORWARD_SLASH + getModule().getComponent().getUrl() + CommonConstants.FORWARD_SLASH +
                getModule().getParentModule().getActionURL()[0] + CommonConstants.FORWARD_SLASH + parentId);
        return sb.toString();
    }

    private Map<String,RoleData> getStaffBelongingRoleMap(){
        return getStaffData().getGroupIdRoleDataMap();
    }
    public List<String> getGroupValuesForUpdate() {
        return CommonConstants.COMMA_SPLITTER.split(model.getGroups());
    }
    private List<String> findNotAllowedGroups(ACLModules module, String methodName, List<GroupData> staffBelongingGroups,List<String> entityGroups,List<String> oldGroups) {

        List<String> notAllowedGroups = Collectionz.newArrayList();
        SingleOperationAuthorizedHandler authorizedHandler = SingleOperationAuthorizedHandler.getHandler();

        if (Collectionz.isNullOrEmpty(oldGroups) == false) {
            /* If it find the oldGroups, it means this call is for Update operation */

            if (authorizedHandler.isAuthorizedForAnyGroup(oldGroups, getStaffBelongingRoleMap(), module.name(), methodName) == false) {
                return oldGroups;
            }
            notAllowedGroups.addAll(authorizedHandler.checkOldGroups(staffBelongingGroups, entityGroups, oldGroups));

        }

        List<String> newGroups = Lists.copy(entityGroups, new NewGroupPredicate(oldGroups));
        notAllowedGroups.addAll(authorizedHandler.isAuthorizedForAllGroup(newGroups, getStaffBelongingRoleMap(), module.name(), methodName));

        return notAllowedGroups;
    }

    private List<String> findNonExistingGroupsFromDB(List<String> entityGroups) throws Exception {
        List<GroupData> groups = CRUDOperationUtil.getAllByIds(GroupData.class, entityGroups.toArray(new String[0]));
        List<String> notExistGroups = Collectionz.newArrayList();
        if(Collectionz.isNullOrEmpty(groups)){
          notExistGroups.addAll(entityGroups);
        }else{
            entityGroups.forEach(groupData -> {
                boolean result = groups.stream().anyMatch(group -> group.getId().equals(groupData));
                if (result == false) {
                    notExistGroups.add(groupData);
                }
            });
        }
        return notExistGroups;

    }

    public List<String> getOldGroupsFromDB() {
       return oldGroupsFromDB;
    }

    public void setOldGroupsFromDB(List<String> oldGroupsFromDB) {
        this.oldGroupsFromDB = oldGroupsFromDB;
    }

    protected Boolean isEntityExists(String id) {
        ResourceData resourceInDB = CRUDOperationUtil.get((Class<T>) model.getClass(),id,getAdditionalCriteria());
        if(resourceInDB == null){
            addActionError(getModule().getDisplayLabel()+" Not Found with id: " + id);
            setOldGroupsFromDB(Collectionz.newArrayList());
            return false;
        }
        setOldGroupsFromDB(CommonConstants.COMMA_SPLITTER.split(resourceInDB.getGroups()));
        return true;
    }
    protected SimpleExpression getAdditionalCriteria(){
        return null;
    }

    public void prepareEditNew() throws Exception {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called prepareEditNew()");
        }
        prepareValuesForSubClass();
    }

    public void prepareEdit() throws Exception {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called prepareEdit()");
        }
        prepareValuesForSubClass();
    }
    public void prepare() throws Exception {

    }

    @Override
    public Collection<TypePermission> getTypePermissions() {
        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called getTypePermissions()");
        }
        Collection<TypePermission> typePermitted = Collectionz.newArrayList();
        typePermitted.add(new ExplicitTypePermission(new Class[]{String.class}));
        return typePermitted;
    }


    public String[] getIds() {
        return ids;
    }

    public void setIds(String[] ids) {
        this.ids = ids;
    }

    protected void validateMaxLength(String field, String restFieldName, String displayFieldName, int maxLength) {
        if (hasValidLength(field, maxLength)) {
            return;
        }

        addFieldError(restFieldName,
                getText("validate.max.length", Arrays.asList(getText(displayFieldName), maxLength)));
    }

    protected void validateRequiredField(String field, String restField, String displayProperties) {
        if (Strings.isNullOrBlank(field) == false) {
            return;
        }

        addFieldError(restField,
                getText("error.required.field", Arrays.asList(getText(displayProperties))));
    }

    private boolean hasValidLength(String field, int maxLength) {
        if (Strings.isNullOrBlank(field)) {
            return true;
        }

        return field.length() <= maxLength;
    }

    protected List<String> getStaffBelongingGroupsIds() {
        return CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
    }

}
