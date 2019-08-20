package com.elitecore.nvsmx.sm.controller.ldapauthconf;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.LdapAuthConfigurationData;
import com.elitecore.corenetvertex.sm.acl.LdapAuthConfigurationFieldMappingData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.util.List;

import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "sm")
@Namespace("/sm/ldapauthconf")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","ldap-auth-configuration"}),

})
public class LdapAuthConfigurationCTRL extends RestGenericCTRL<LdapAuthConfigurationData> {

    private List<LdapData> ldapDataList = Collectionz.newArrayList();

    @Override
    public ACLModules getModule() {
        return ACLModules.LDAP_AUTH_CONFIGURATION;
    }

    @Override
    public LdapAuthConfigurationData createModel() {
        return new LdapAuthConfigurationData();
    }

    @Override
    public HttpHeaders create() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called create()");
        }
        setActionChainUrl(getRedirectURL("show"));
        addActionError("Unauthorized Action");
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INPUT_PARAMETER_MISSING.code);
    }

    @Override
    public HttpHeaders index() {
        return show();
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setLdapDataList(CRUDOperationUtil.findAll(LdapData.class));

    }

    @Override
    public HttpHeaders destroy() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called destroy()");
        }
        setActionChainUrl(getRedirectURL("show"));
        addActionError("Unauthorized Action");
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INPUT_PARAMETER_MISSING.code);
    }

    /**
     * Override because don't want to check duplicate entry
     * @return
     */
    @Override
    public HttpHeaders update() {

        if(LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(),"Method called update()");
        }
        try {
            String result = authorize();
            if(result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
            LdapAuthConfigurationData ldapAuthConfigurationData = (LdapAuthConfigurationData) getModel();

            ldapAuthConfigurationData.setLdapData(CRUDOperationUtil.get(LdapData.class,ldapAuthConfigurationData.getLdapData().getId()));

            ldapAuthConfigurationData.setModifiedDateAndStaff(getStaffData());
            filterEmptyDdfSprRelDataList(ldapAuthConfigurationData.getLdapAuthConfigurationFieldMappingDataList());
            CRUDOperationUtil.merge(ldapAuthConfigurationData);

            String message = getModule().getDisplayLabel() + " <b><i>" + ldapAuthConfigurationData.getResourceName() + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(ldapAuthConfigurationData,ldapAuthConfigurationData.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(),ldapAuthConfigurationData.getHierarchy(), message);

            addActionMessage(getModule().getDisplayLabel()+" updated successfully");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code).disableCaching();

        }catch (Exception e) {
            LogManager.getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information.Reason: "+e.getMessage());
            LogManager.getLogger().trace(getLogModule(),e);
            addActionError("Failed to perform update operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    @Override
    public HttpHeaders show() {
        if(LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(),"Method called show()");
        }

        LdapAuthConfigurationData ldapAuthConfigurationData = (LdapAuthConfigurationData) getModel();

        if (ldapAuthConfigurationData == null) {
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
        }

        if (Strings.isNullOrBlank(ldapAuthConfigurationData.getId()) || "*".equals(ldapAuthConfigurationData.getId())) {
            List all = CRUDOperationUtil.findAll(getModel().getClass());
            if(Collectionz.isNullOrEmpty(all) == false){
                ldapAuthConfigurationData = (LdapAuthConfigurationData) all.get(0);
                setModel(ldapAuthConfigurationData);
            }
        } else {
            setModel(CRUDOperationUtil.get(ldapAuthConfigurationData.getClass(), ldapAuthConfigurationData.getId()));
        }
        setActionChainUrl(getRedirectURL(METHOD_SHOW));
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
    }
    public List<LdapData> getLdapDataList() {
        return ldapDataList;
    }

    public void setLdapDataList(List<LdapData> ldapDataList) {
        this.ldapDataList = ldapDataList;
    }

    @Override
    public void validate() {
        //
    }

    private void filterEmptyDdfSprRelDataList(List<LdapAuthConfigurationFieldMappingData> ldapAuthConfigurationFieldMappingDataList) {

        Collectionz.filter(ldapAuthConfigurationFieldMappingDataList, ldapAuthConfigurationFieldMappingData -> ldapAuthConfigurationFieldMappingData == null ? false : true);
    }

    public String getLdapAuthConfFieldMappingDataAsJsonString() {
        Gson gson = GsonFactory.defaultInstance();
        LdapAuthConfigurationData ldapAuthConfigurationData = (LdapAuthConfigurationData) getModel();
        return gson.toJsonTree(ldapAuthConfigurationData.getLdapAuthConfigurationFieldMappingDataList(),new TypeToken<List<LdapAuthConfigurationFieldMappingData>>() {}.getType()).getAsJsonArray().toString();
    }

}
