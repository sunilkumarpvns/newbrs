package com.elitecore.nvsmx.sm.controller.staff;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.AuthenticationMode;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.RoleData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.acl.StaffGroupRoleRelData;
import com.elitecore.corenetvertex.sm.acl.StaffProfilePictureData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.base64.Base64;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import javax.annotation.Nullable;
import javax.servlet.http.HttpServletResponse;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by aditya on 8/16/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/staff")
@Results({
        @Result(name = SUCCESS, type = "redirectAction", params = {"actionName", "staff"}),
})
public class StaffCTRL extends RestGenericCTRL<StaffData> {

    private List<RoleData> roleDataList = new ArrayList<>();
    private List<GroupData> groupDataList = new ArrayList<>();
    private static final int MAX_FILE_SIZE = (2 * 1024 * 1024);
    private File profilePictureFile;

    @Override
    public ACLModules getModule() {
        return ACLModules.STAFF;
    }

    @Override
    public StaffData createModel() {
        return new StaffData();
    }


    @Override
    public HttpHeaders create() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method  called create()");
        }

        HttpHeaders httpHeaders = authorizeStaff(METHOD_EDITNEW);
        if (httpHeaders != null) {
            return httpHeaders;
        }

        StaffData staffData = (StaffData) getModel();
        try {
            staffData.setPassword(PasswordUtility.getEncryptedPassword(staffData.getPassword()));
        } catch (NoSuchEncryptionException | EncryptionFailedException e) {
            getLogger().error(getLogModule(), "Error while encrypting password. Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while saving profile");
            addActionError("Reason: " + e.getMessage());
            return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }

        try {
            String profilePictureId = saveOrUpdateProfilePicture(staffData.getProfilePictureId());
            staffData.setProfilePictureId(profilePictureId);
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while saving profile picture. Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while saving profile picture");
            addActionError("Reason: " + e.getMessage());
            return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
        Collectionz.filter(staffData.getStaffGroupRoleRelDataList(),Predicates.nonNull());
        staffData.getStaffGroupRoleRelDataList().forEach(staffGroupRoleRelData -> staffGroupRoleRelData.setStaffData(staffData));
        for(StaffGroupRoleRelData data : staffData.getStaffGroupRoleRelDataList()){
            data.setRoleData(CRUDOperationUtil.get(RoleData.class, data.getRoleId()));
            data.setGroupData(CRUDOperationUtil.get(GroupData.class, data.getGroupId()));
        }
        return super.create();
    }

    private @Nullable
    String saveOrUpdateProfilePicture(@Nullable String profilePictureId) throws Exception {

        StaffProfilePictureData profilePictureData = getProfilePicture();
        if (profilePictureData == null) {
            //case of create where profile picture not selected
            if (Strings.isNullOrBlank(profilePictureId)) {
                return null;
            }
            //case of update where profile picture not selected
            return profilePictureId;
        }
        if (Strings.isNullOrBlank(profilePictureId) == false) {
            profilePictureData.setId(profilePictureId);
        }
        CRUDOperationUtil.saveOrUpdate(profilePictureData);
        return profilePictureData.getId();
    }

    @Override
    public HttpHeaders destroy() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called destroy()");
        }
        getLogger().error(getLogModule(),"Delete operation Not Supported");
        addActionError(getText("method.not.allowed"));
        getResponse().addHeader(ALLOWED_METHOD_HEADER,NON_DESTROYABLE_RESOURCE_ALLOWED_METHOD);
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(HttpServletResponse.SC_METHOD_NOT_ALLOWED);
    }

    private @Nullable
    StaffProfilePictureData getProfilePicture() throws Exception {
        String profilePicture = getRequest().getParameter(Attributes.PROFILE_PICTURE_FIELD);
        StaffProfilePictureData staffProfilePictureData = null;
        if (Strings.isNullOrBlank(profilePicture) == false) {
            String[] profilePictureArray = CommonConstants.COMMA_SPLITTER.splitToArray(profilePicture);
            String encodedString = profilePictureArray[1];
            byte[] decodedBytes = Base64.decode(encodedString);
            ByteArrayInputStream bis = new ByteArrayInputStream(decodedBytes);
            String contentType = URLConnection.guessContentTypeFromStream(bis);
            if (contentType == null || contentType.contains("image") == false) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(getLogModule(), "Invalid Profile Picture format.Profile Picture must be of jpg,jpeg or png format");
                }
                throw new Exception("Invalid Profile Picture format");
            }
            if (decodedBytes.length > MAX_FILE_SIZE) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(getLogModule(), "Invalid Profile Picture size");
                }
                throw new Exception("Invalid Profile Picture size or format");
            }

            staffProfilePictureData = new StaffProfilePictureData();
            staffProfilePictureData.setProfilePicture(decodedBytes);
        }
        return staffProfilePictureData;
    }


    @Override
    public HttpHeaders update() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called update()");
        }

        try {

            HttpHeaders httpHeaders = authorizeStaff(METHOD_EDIT);
            if (httpHeaders != null) {
                return httpHeaders;
            }

            StaffData model = (StaffData) getModel();
            StaffData staffDataInDB = CRUDOperationUtil.get(StaffData.class, model.getId());

            if(staffDataInDB == null){
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }

            JsonObject oldJsonObject= staffDataInDB.toJson();

            String profilePictureId = saveOrUpdateProfilePicture(staffDataInDB.getProfilePictureId());
            staffDataInDB.setProfilePictureId(profilePictureId);
            staffDataInDB.setName(model.getName());
            staffDataInDB.setUserName(model.getUserName());
            staffDataInDB.setEmailAddress(model.getEmailAddress());
            staffDataInDB.setMobile(model.getMobile());
            staffDataInDB.setPhone(model.getPhone());
            staffDataInDB.setGroups(model.getGroups());
            staffDataInDB.setStatus(model.getStatus());
            staffDataInDB.getStaffGroupRoleRelDataList().clear();
            Collectionz.filter(model.getStaffGroupRoleRelDataList(),Predicates.nonNull());
            model.getStaffGroupRoleRelDataList().forEach(staffGroupRoleRelData -> staffGroupRoleRelData.setStaffData(staffDataInDB));
            staffDataInDB.getStaffGroupRoleRelDataList().addAll(model.getStaffGroupRoleRelDataList());

            for(StaffGroupRoleRelData data : model.getStaffGroupRoleRelDataList()){
                data.setRoleData(CRUDOperationUtil.get(RoleData.class, data.getRoleId()));
                data.setGroupData(CRUDOperationUtil.get(GroupData.class, data.getGroupId()));
            }

            CRUDOperationUtil.merge(staffDataInDB);
            String message = getModule().getDisplayLabel() + " <b><i>" + model.getResourceName() + "</i></b> " + "Updated";

            JsonObject newJsonObject= staffDataInDB.toJson();
            JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);

            CRUDOperationUtil.audit(staffDataInDB,	staffDataInDB.getName(),AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference ,	staffDataInDB.getHierarchy(), message);

            addActionMessage(getModule().getDisplayLabel() + " updated successfully");
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(staffDataInDB.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information. Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while performing Update Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    private HttpHeaders authorizeStaff(String url) {
        try {
            String result = authorize();
            if (SUCCESS.equals(result) == false) {
                if(getLogger().isDebugLogLevel()){
                    getLogger().debug(getLogModule(),"Staff is not Authorised for this Action");
                }
                prepareValuesForSubClass();
                addActionError(getText("superadmin.allowed.access"));
                setActionChainUrl(getRedirectURL(url));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error in staff authorization. Reason: " + e.getMessage());
            getLogger().trace(e);
            addActionError("Error in staff authorization");
            addActionError("Reason: " + e.getMessage());
            return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
        return null;
    }

    @Override
    public String authorize() throws Exception {
        StaffData staffData = (StaffData) getRequest().getSession().getAttribute(Attributes.STAFF_DATA);
        if (staffData != null) {
            if (isAdminUser(staffData)) {
                return SUCCESS;
            }
        }
        return INPUT;
    }

    @SkipValidation
    public HttpHeaders updateStaffStatus() {
        try {
            StaffData model = (StaffData) getModel();
            StaffData staffDataInDB = CRUDOperationUtil.get(StaffData.class, model.getId());
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(getLogModule(), "Updating staff: " + model.getId() + " with status:" + model.getStatus());
            }
            if (PkgStatus.INACTIVE.name().equals(staffDataInDB.getStatus())) {
                staffDataInDB.setStatus(PkgStatus.ACTIVE.name());
            } else if (PkgStatus.ACTIVE.name().equals(staffDataInDB.getStatus())) {
                staffDataInDB.setStatus(PkgStatus.INACTIVE.name());
            }
            setModel(staffDataInDB);
            return super.update();
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information. Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while performing update staff staff status ");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    public List<RoleData> getRoleDataList() {
        return roleDataList;
    }

    public void setRoleDataList(List<RoleData> roleDataList) {
        this.roleDataList = roleDataList;
    }

    public List<GroupData> getGroupDataList() {
        return groupDataList;
    }

    public void setGroupDataList(List<GroupData> groupDataList) {
        this.groupDataList = groupDataList;
    }

    public File getProfilePictureFile() {
        return profilePictureFile;
    }

    public void setProfilePictureFile(File profilePictureFile) {
        this.profilePictureFile = profilePictureFile;
    }

    @Override
    public void prepareValuesForSubClass(){
        setRoleDataList(CRUDOperationUtil.findAllWhichIsNotDeleted(RoleData.class));
        setGroupDataList(CRUDOperationUtil.findAllWhichIsNotDeleted(GroupData.class));
    }

    @Override
    public void validate() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called validate()");
        }
        StaffData staffData = (StaffData) getModel();
        String methodName = getMethodName();
        boolean isUserNameExist = false;
        if(CRUDOperationUtil.MODE_CREATE.equalsIgnoreCase(methodName)){
            isUserNameExist = isDuplicateEntity("userName",staffData.getUserName(),CRUDOperationUtil.MODE_CREATE);
            validatePassword(staffData);
        }else if(CRUDOperationUtil.MODE_UPDATE.equalsIgnoreCase(methodName)){
            isUserNameExist = isDuplicateEntity("userName",staffData.getUserName(),CRUDOperationUtil.MODE_UPDATE);
        }
        if(isUserNameExist){
            addFieldError("userName",getText("staff.username.duplicate"));
        }
        validateGroupRoleRelList(staffData.getStaffGroupRoleRelDataList());
    }

    private void validateGroupRoleRelList(List<StaffGroupRoleRelData> staffGroupRoleRelDataList) {

        if (Collectionz.isNullOrEmpty(staffGroupRoleRelDataList)) {
            addActionError(getText("role.group.required"));
        } else {
            Set<String> groupIds = Collectionz.newHashSet();

            for (StaffGroupRoleRelData staffGroupRoleRelData : staffGroupRoleRelDataList) {
                if(Objects.isNull(staffGroupRoleRelData)){
                    continue;
                }
                if (Strings.isNullOrBlank(staffGroupRoleRelData.getGroupId()) || Strings.isNullOrBlank(staffGroupRoleRelData.getRoleId())) {
                    addActionError(getText("role.group.value.required"));
                }else {
                    String roleID = staffGroupRoleRelData.getRoleId();
                    String groupId = staffGroupRoleRelData.getGroupId();

                    if (CRUDOperationUtil.get(RoleData.class, roleID) == null) {
                        addActionError("Invalid role provided");
                    }

                    if (CRUDOperationUtil.get(GroupData.class, groupId) == null) {
                        getLogger().error(getLogModule(), "Failed to create Staff. Invalid Group provided.");
                        addActionError("Invalid Group provided");
                    } else if (groupIds.contains(groupId)) {
                        getLogger().error(getLogModule(), "Failed to create Staff. Duplicate Group found.");
                        addActionError(getText("group.duplicate"));
                    } else {
                        groupIds.add(groupId);
                    }
                }
            }
        }
    }

    private void validatePassword(StaffData staffData) {
        if (Strings.isNullOrBlank(staffData.getPassword()) && AuthenticationMode.LOCAL.name().equals(staffData.getAuthenticationMode())) {
            addFieldError("password", getText("password.required"));
        }
    }

}