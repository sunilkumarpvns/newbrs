package com.elitecore.nvsmx.sm.controller.driver.csv;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.corenetvertex.sm.driver.constants.FileTransferProtocol;
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

import java.util.Optional;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Used to change password for Csv Driver
 * @author dhyani.raval
 */

@ParentPackage(value = "sm")
@Namespace("/sm/driver")
@org.apache.struts2.convention.annotation.Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","csv-driver-password"}),

})
@InterceptorRef(value = "restStack")
public class CsvDriverPasswordCTRL extends RestGenericCTRL<PasswordData> {

    public ACLModules getModule() {
        return ACLModules.DRIVER;
    }

    @Override
    public PasswordData createModel() {
        return new PasswordData();
    }

    @Override
    public String edit() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called edit");
        }

        setActionChainUrl("sm/driver/csv-driver-password/edit");
        return NVSMXCommonConstants.REDIRECT_URL;
    }

    @Override
    public HttpHeaders update() {

        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(),"Method called update()");
        }
        PasswordData passwordData = (PasswordData) getModel();
        DriverData driverData = CRUDOperationUtil.get(DriverData.class,passwordData.getId());

        if(driverData == null) {
            addActionError(getModule().getDisplayLabel()+" not found with id: " + passwordData.getId());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        try {

            if(FileTransferProtocol.LOCAL.name().equals(driverData.getCsvDriverData().getAllocatingProtocol())) {
                setActionChainUrl(getRedirectURL(METHOD_SHOW));
                getLogger().error(getLogModule(),"Change Password operation not supported for allocating protocol type 'LOCAL' ");
                addActionError("Change Password operation not supported for allocating protocol type 'LOCAL'");
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);

            }

            String result = authorize();
            if(result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }



            String encryptedPassword = null;
            if (Strings.isNullOrBlank(passwordData.getOldPassword()) == false) {
                encryptedPassword = PasswordUtility.getEncryptedPassword(passwordData.getOldPassword());
            }

            String existingDbPassword = driverData.getCsvDriverData().getPassword();
            if ((Strings.isNullOrBlank(existingDbPassword) && Strings.isNullOrBlank(encryptedPassword)) || Optional.ofNullable(existingDbPassword).equals(encryptedPassword)) {

                driverData.getCsvDriverData().setPassword(PasswordUtility.getEncryptedPassword(passwordData.getNewPassword()));
                driverData.setModifiedDateAndStaff(getStaffData());
                CRUDOperationUtil.merge(driverData);

                String message = getModule().getDisplayLabel() + " <b><i>" + driverData.getResourceName() + "</i></b> " + "Password Updated";
                CRUDOperationUtil.audit(driverData,driverData.getResourceName(), AuditActions.UPDATE,getStaffData(),getRequest().getRemoteAddr(),driverData.getHierarchy(),message);
                addActionMessage(getModule().getDisplayLabel()+" updated successfully");
                setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(driverData.getId()));

                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();

            } else {
                throw new Exception(getText("password.not.match"));
            }
        } catch (Exception e) {
            getLogger().error(getLogModule(),"Error while changing " + getModule().getDisplayLabel() + " password. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while changing " + getModule().getDisplayLabel() + " password. Reason: "+e.getMessage());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code);
        }

    }
}
