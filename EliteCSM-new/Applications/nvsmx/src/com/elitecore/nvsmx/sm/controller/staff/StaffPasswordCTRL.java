package com.elitecore.nvsmx.sm.controller.staff;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.systemparameter.PasswordPolicyConfigData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.password.PasswordData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.exprlib.parser.exception.InvalidSymbolException;
import com.elitecore.exprlib.scanner.Scanner;
import com.elitecore.exprlib.scanner.Symbol;
import com.elitecore.exprlib.scanner.impl.ScannerImpl;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.policy.PasswordPolicyDAO;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.sql.Timestamp;
import java.util.Date;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;


@ParentPackage(value = "sm")
@Namespace("/sm/staff")
@org.apache.struts2.convention.annotation.Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","staff-password"}),

})
public class StaffPasswordCTRL extends RestGenericCTRL<PasswordData> {

    private static Scanner scanner ;

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

    public ACLModules getModule() {
        return ACLModules.STAFF;
    }

    @Override
    public PasswordData createModel() {
        return new PasswordData();
    }

    @Override
    public String edit() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called edit()");
        }

        setActionChainUrl("sm/staff/staff-password/edit");
        return NVSMXCommonConstants.REDIRECT_URL;
    }

    @Override
    public HttpHeaders update() {
         if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called update()");
        }
        PasswordData passwordData = (PasswordData) getModel();
        StaffData staffData = CRUDOperationUtil.get(StaffData.class,passwordData.getId());

        if(staffData == null) {
            addActionError(getModule().getDisplayLabel()+" not found with id: " + passwordData.getId());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        try {

            String result = authorize();
            if(result.equals(SUCCESS) == false){
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }


            if(passwordData.getOldPassword().equals(passwordData.getNewPassword())) {
                addActionError(getText("password.old.new.same"));
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }

            PasswordPolicyConfigData passwordPolicySelectionData = PasswordPolicyDAO.getPasswordSelectionPolicy();

            String encryptedRecentPassword = prepareRecentPasswords(passwordData.getOldPassword(), passwordData.getNewPassword(), staffData, new Date(), passwordPolicySelectionData);

            if (PasswordPolicyDAO.validatePasswordWithPasswordPolicy(passwordData.getNewPassword(),passwordPolicySelectionData) == false) {
                addActionError(getText("password.policy.not.match"));
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }
            staffData.setPassword(PasswordUtility.getEncryptedPassword(passwordData.getNewPassword()));
            staffData.setModifiedDateAndStaff(getStaffData());
            staffData.setPasswordChangeDate(new Timestamp(new Date().getTime()));
            staffData.setRecentPasswords(encryptedRecentPassword);

            CRUDOperationUtil.merge(staffData);
            String message = getModule().getDisplayLabel() + " <b><i>" + staffData.getResourceName() + "</i></b> " + "Password Updated";
            CRUDOperationUtil.audit(staffData,staffData.getResourceName(), AuditActions.UPDATE,getStaffData(),getRequest().getRemoteAddr(),staffData.getHierarchy(),message);
            addActionMessage(getModule().getDisplayLabel()+" updated successfully");


            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(staffData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();

        } catch (Exception e) {
            getLogger().error(getLogModule(),"Error while changing " + getModule().getDisplayLabel() + " password. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Failed to change password. Reason: "+e.getMessage());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code);
        }

    }

    private String prepareRecentPasswords(String oldPassword, String newPassword, StaffData staffData, Date currentDate , PasswordPolicyConfigData passwordPolicySelectionData) throws Exception {
        StringBuilder newRecentPasswords = new StringBuilder();
        Integer historicalPasswords = passwordPolicySelectionData.getTotalHistoricalPasswords();
        String recentPasswordsStr = staffData.getRecentPasswords();
        String tempOldPassword = PasswordUtility.getEncryptedPassword(oldPassword);
        tempOldPassword = tempOldPassword.replace(String.valueOf(CommonConstants.BACKSLASH),"\\\\");
        newRecentPasswords.append(tempOldPassword.replace(NVSMXCommonConstants.COMMA,"\\,"));

        if( Strings.isNullOrBlank(recentPasswordsStr) == false ){

            List<String> oldPassList = Collectionz.newArrayList();
            try {
                List<Symbol> oldPasswords = scanner.getSymbols(recentPasswordsStr);
                for(Symbol pass : oldPasswords){
                    if(pass.getName()!=null && NVSMXCommonConstants.COMMA.equalsIgnoreCase(pass.getName())==false){
                        oldPassList.add(pass.getName());
                    }
                }
            } catch (InvalidSymbolException e) {
                throw new Exception("Error while separating recent passwords, Reason: "+e.getMessage(), e);
            }

            for (int i = 0; i < historicalPasswords; i++) {
                if (oldPassList.size() > i) {
                    String tempPassword = oldPassList.get(i);

                    if(Strings.isNullOrBlank(tempPassword)) {
                        return "";
                    }
                    if (tempPassword.equals(PasswordUtility.getEncryptedPassword(newPassword))) {
                        addActionError(getText("password.new.historical.same")+" : "+historicalPasswords);
                        throw new Exception(getText("password.new.historical.same")+" : "+historicalPasswords);
                    } else {
                        if (i < historicalPasswords-1) {
                            newRecentPasswords.append(",");
                            tempPassword = tempPassword.replace(String.valueOf(CommonConstants.BACKSLASH),"\\\\");
                            newRecentPasswords.append(tempPassword.replace(NVSMXCommonConstants.COMMA, "\\,"));
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
            addActionError(getText("password.not.match"));
            throw new Exception(getText("password.not.match"));

        }
        return newRecentPasswords.toString();
    }
}
