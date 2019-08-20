package com.elitecore.nvsmx.sm.controller.database;

import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.password.PasswordData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Used to change the password for Database Data Sourcess
 * @author dhyani.raval
 */

@ParentPackage(value = "sm")
@Namespace("/sm/database")
@org.apache.struts2.convention.annotation.Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","database-password"}),

})
@InterceptorRef(value = "restStack")
public class DatabasePasswordCTRL extends RestGenericCTRL<PasswordData> {

    public ACLModules getModule() {
        return ACLModules.DATABASE;
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

        setActionChainUrl("sm/database/database-password/edit");
        return NVSMXCommonConstants.REDIRECT_URL;
    }

    @Override
    public HttpHeaders update() {
         if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called update()");
        }
        PasswordData passwordData = (PasswordData) getModel();
        DatabaseData databaseData = CRUDOperationUtil.get(DatabaseData.class,passwordData.getId());

        if(databaseData == null) {
            addActionError(getModule().getDisplayLabel()+" not found with id: " + passwordData.getId());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        try {

            String result = authorize();
            if(result.equals(SUCCESS) == false){
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            String encryptedPassword = PasswordUtility.getEncryptedPassword(passwordData.getOldPassword());

            if (databaseData.getPassword().equals(encryptedPassword) == false ) {
                addActionError(getText("password.not.match"));
                throw new Exception(getText("password.not.match"));
            }

            databaseData.setPassword(PasswordUtility.getEncryptedPassword(passwordData.getNewPassword()));
            databaseData.setModifiedDateAndStaff(getStaffData());
            CRUDOperationUtil.merge(databaseData);

            String message = getModule().getDisplayLabel() + " <b><i>" + databaseData.getResourceName() + "</i></b> " + "Password Updated";
            CRUDOperationUtil.audit(databaseData,databaseData.getResourceName(), AuditActions.UPDATE,getStaffData(),getRequest().getRemoteAddr(),databaseData.getHierarchy(),message);
            addActionMessage(getModule().getDisplayLabel()+" updated successfully");

            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(databaseData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();

        } catch (Exception e) {
            getLogger().error(getLogModule(),"Error while changing " + getModule().getDisplayLabel() + " password. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code);
        }

    }
}
