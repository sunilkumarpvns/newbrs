package com.elitecore.nvsmx.commons.controller.login;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.pkg.systemparameter.PasswordPolicyConfigData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.exprlib.parser.exception.InvalidSymbolException;
import com.elitecore.exprlib.scanner.Scanner;
import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.scanner.impl.ScannerImpl;
import com.elitecore.nvsmx.commons.model.login.ChangePasswordData;
import com.elitecore.nvsmx.commons.model.staff.StaffDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.policy.PasswordPolicyDAO;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.opensymphony.xwork2.ActionSupport;
import org.apache.struts2.dispatcher.SessionMap;
import org.apache.struts2.interceptor.ServletRequestAware;
import org.apache.struts2.interceptor.SessionAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.http.HttpServletRequest;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

/**
 * This will be used to change password of policy designer.
 * Created by dhyani on 22/12/16.
 */
public class ChangePasswordCTRL extends ActionSupport implements ServletRequestAware, SessionAware{

    public static final String MODULE = ChangePasswordCTRL.class.getSimpleName();
    private static final String COMMA = ",";
    private ChangePasswordData changePasswordData = new ChangePasswordData();
    private HttpServletRequest request;
    public static final String SAME_OLD_AND_NEW_PASSWORDS = "Old Password and New Password should not be same";
    public static final String SAME_NEW_AND_HISTORICAL_PASSWORD = "New password same as historical password";
    public static final String OLD_PASSWORD_DB_AND_GUI_DONT_MATCH="Old Password(DB) and Old Password(GUI) are not same";
    private SessionMap<String, Object> session;
    private String actionChainUrl;
    private static Scanner scanner ;

    @SkipValidation
    public String redirectToChangePasswordPage() {

        if(getLogger().isDebugLogLevel()) {
            getLogger().info(MODULE, "Method called redirectToChangePasswordPage()");
        }

        String userName = (String) request.getSession().getAttribute(Attributes.STAFF_USERNAME);
        changePasswordData.setStaffList(Arrays.asList(StaffDAO.getStaffByUserName(userName)));
        try {
            changePasswordData.setPasswordPolicyConfigData(PasswordPolicyDAO.getPasswordSelectionPolicy());
        } catch (Exception e) {
            addActionError(e.getMessage());
            getLogger().error(MODULE, "Error while changing password. Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
        }

        String isManualPasswordChange = request.getParameter(Attributes.IS_MANUAL_PASSWORD_CHANGE);
        if(Strings.isNullOrBlank(isManualPasswordChange)) {
            isManualPasswordChange = String.valueOf(request.getSession().getAttribute(Attributes.IS_MANUAL_PASSWORD_CHANGE));
        }
        if(isManualPasswordChange.equalsIgnoreCase("false")){
            request.setAttribute(Attributes.IS_MANUAL_PASSWORD_CHANGE,false);
            return Results.COMPULSARY_CHANGE.getValue();
        }else{
            request.setAttribute(Attributes.IS_MANUAL_PASSWORD_CHANGE,true);
            return Results.MANUAL_CHANGE.getValue();
        }
    }

    public String changePassword() {

        if(getLogger().isDebugLogLevel()) {
            getLogger().info(MODULE, "Method called changePassword()");
        }
        StaffData staffData;

        try {

            staffData =  StaffDAO.getStaffById(changePasswordData.getUserId());
            changePasswordData.setStaffList(Arrays.asList(staffData));

            PasswordPolicyConfigData passwordPolicySelectionData = PasswordPolicyDAO.getPasswordSelectionPolicy();
            changePasswordData.setPasswordPolicyConfigData(passwordPolicySelectionData);

            if(changePasswordData.getOldPassword().equals(changePasswordData.getNewPassword())) {
                throw new Exception(SAME_OLD_AND_NEW_PASSWORDS);
            }

            String encryptedRecentPassword = prepareRecentPasswords(changePasswordData.getOldPassword(), changePasswordData.getNewPassword(), staffData, new Date(), passwordPolicySelectionData);
            StaffDAO.changePassword(changePasswordData.getUserId(), changePasswordData.getOldPassword(), changePasswordData.getNewPassword(), encryptedRecentPassword);

            if (staffData.getUserName().equals(NVSMXCommonConstants.ADMIN )) {

                List<GroupData> allGroups = CRUDOperationUtil.findAll(GroupData.class);
                request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP_IDS, StaffDAO.getGroupIds(allGroups));
                request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP,allGroups);

            } else {

                request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP, new ArrayList<GroupData>(staffData.getGroupDatas()));
                request.getSession().setAttribute(Attributes.STAFF_BELONGING_GROUP_IDS, StaffDAO.getStaffBelongingsGroup(staffData));
                request.getSession().setAttribute(Attributes.STAFF_GROUP_BELONGING_ROLES_MAP, staffData.getGroupIdRoleDataMap());
                request.getSession().setAttribute(Attributes.STAFF_ROLES_SET, staffData.getRoleDatas());
            }

            staffData.setACLChecker(StaffDAO.getStaffAclCheckerForSPROperations(staffData));
            staffData.setLastLoginTime(new Timestamp(System.currentTimeMillis()));
            CRUDOperationUtil.update(staffData);
            session.put(Attributes.STAFF_NAME, staffData.getName());
            session.put(Attributes.STAFF_USERNAME,staffData.getUserName());
            session.put(Attributes.STAFF_DATA, staffData);
            session.put(Attributes.STAFF_ID, staffData.getId());
            addActionMessage("Password Changed Successfully");
            setActionChainUrl(NVSMXCommonConstants.WELCOME_URL);
            request.getSession().removeAttribute(Attributes.IS_MANUAL_PASSWORD_CHANGE);
            request.removeAttribute(Attributes.IS_MANUAL_PASSWORD_CHANGE);
            return Results.REDIRECT_ACTION.getValue();

        } catch (Exception e) {
            addActionError(e.getMessage());
            getLogger().error(MODULE, "Error while changing password. Reason: "+e.getMessage());
            getLogger().trace(MODULE,e);
            Boolean isManualPasswordChange = Boolean.valueOf(request.getParameter(Attributes.IS_MANUAL_PASSWORD_CHANGE));
            request.getSession().setAttribute(Attributes.IS_MANUAL_PASSWORD_CHANGE,isManualPasswordChange);
            setActionChainUrl(NVSMXCommonConstants.MANUAL_PASSWORD_PAGE_URL);
            return Results.REDIRECT_ACTION.getValue();
        }
    }


    private String prepareRecentPasswords(String oldPassword, String newPassword, StaffData staffData, Date currentDate , PasswordPolicyConfigData passwordPolicySelectionData) throws Exception {
        StringBuilder newRecentPasswords = new StringBuilder();
        Integer historicalPasswords = passwordPolicySelectionData.getTotalHistoricalPasswords();
        String recentPasswordsStr = staffData.getRecentPasswords();
        String tempOldPassword = PasswordUtility.getEncryptedPassword(oldPassword);
        tempOldPassword = tempOldPassword.replace("\\","\\\\");
        newRecentPasswords.append(tempOldPassword.replace(",","\\,"));

        if( recentPasswordsStr != null ){

            List<String> oldPassList =  new ArrayList<String>();
            try {
                List<Symbol> oldPasswords = scanner.getSymbols(recentPasswordsStr);
                for(Symbol pass : oldPasswords){
                    if(pass.getName()!=null && COMMA.equalsIgnoreCase(pass.getName()) == false){
                        oldPassList.add(pass.getName());
                    }
                }
            } catch (InvalidSymbolException e) {
                throw new Exception("Error while separating recents passwords, Reason: "+e.getMessage(), e);
            }

            for(int i=0; i<historicalPasswords; i++){
                if(oldPassList.size()>i){
                    String tempPassword = oldPassList.get(i);
                    if(tempPassword!=null && tempPassword.length()>0){
                        if (tempPassword.equals(PasswordUtility.getEncryptedPassword(newPassword))){
                            throw new Exception(SAME_NEW_AND_HISTORICAL_PASSWORD+" : "+historicalPasswords);
                        }else{
                            if(i<historicalPasswords-1){
                                newRecentPasswords.append(",");
                                tempPassword = tempPassword.replace("\\","\\\\");
                                newRecentPasswords.append(tempPassword.replace(",", "\\,"));
                            }
                        }
                    }
                }
            }
        }
        if(staffData.getPassword().equals(PasswordUtility.getEncryptedPassword(oldPassword)) || (staffData.getPasswordChangeDate() == null && staffData.getPassword().equals(oldPassword))) {
            staffData.setPassword(newPassword);
            staffData.setModifiedDate(new Timestamp(currentDate.getTime()));
            staffData.setPasswordChangeDate(new Timestamp(currentDate.getTime()));
        } else {
            throw new Exception(OLD_PASSWORD_DB_AND_GUI_DONT_MATCH);
        }
        return newRecentPasswords.toString();

    }

    @SkipValidation
    public String initResetPassword() {

        if(getLogger().isDebugLogLevel()) {
            getLogger().info(MODULE, "Method called initResetPassword()");
        }

        List<StaffData> staffDataList =  StaffDAO.getAllStaffMembersName();
        String loggedInUserName = (String) request.getSession().getAttribute(Attributes.STAFF_USERNAME);
        String loggedInUserId = (String) request.getSession().getAttribute(Attributes.STAFF_ID);

        if(loggedInUserId != null && Strings.isNullOrBlank(loggedInUserName) == false && loggedInUserName.equals(NVSMXCommonConstants.ADMIN)) {
            StaffData loggedInUser = new StaffData();
            loggedInUser.setId(loggedInUserId);
            loggedInUser.setUserName(loggedInUserName);
            staffDataList.remove(loggedInUser);
        }

        changePasswordData.setStaffList(staffDataList);


        return  Results.RESET_PASSWORD.getValue();
    }

    @SkipValidation
    public String resetPassword() {

        if(getLogger().isDebugLogLevel()) {
            getLogger().info(MODULE, "Method called resetPassword()");
        }
        try {
            if (changePasswordData == null) {
                throw new Exception("password detail not found");
            }
            if(Strings.isNullOrBlank(changePasswordData.getUserId()) && Strings.isNullOrBlank(changePasswordData.getNewPassword())){
                throw new Exception("staff id & new password not found");
            }
            StaffDAO.resetPassword(changePasswordData.getUserId(), changePasswordData.getNewPassword());
            setActionChainUrl(NVSMXCommonConstants.WELCOME_URL);
            addActionMessage("Password Reset Successfully");
        } catch (Exception e) {
            addActionError(e.getMessage());
            getLogger().error(MODULE, "Error while reset password. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
            setActionChainUrl(NVSMXCommonConstants.RESET_PASSWORD_PAGE_URL);
        }
        return  Results.REDIRECT_ACTION.getValue();
    }



    static {
        scanner = new ScannerImpl(){
            @Override
            protected boolean isOperator(char ch) {
                return( ch==',' );
            }

            @Override
            protected boolean isWhitespace(char ch) {
                return false;
            }
        };
    }
    public ChangePasswordData getChangePasswordData() {
        return changePasswordData;
    }

    public void setChangePasswordData(ChangePasswordData changePasswordData) {
        this.changePasswordData = changePasswordData;
    }

    @Override
    public void setServletRequest(HttpServletRequest request) {
        this.request = request;
    }

    @Override
    public void setSession(Map<String, Object> session) {
        this.session = (SessionMap<String, Object>) session;
    }

    public String getActionChainUrl() {
        return actionChainUrl;
    }

    public void setActionChainUrl(String actionChainUrl) {
        this.actionChainUrl = actionChainUrl;
    }
}
