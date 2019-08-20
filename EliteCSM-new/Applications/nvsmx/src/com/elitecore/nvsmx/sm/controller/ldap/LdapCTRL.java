package com.elitecore.nvsmx.sm.controller.ldap;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.ldap.LdapBaseDn;
import com.elitecore.corenetvertex.ldap.LdapData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.HttpHeaders;

import java.util.List;
import java.util.Objects;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * manage LDAP Datasource related information.
 * Created by dhyani on 23/8/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/ldap")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","ldap"}),

})
public class LdapCTRL extends RestGenericCTRL<LdapData> {

    @Override
    public ACLModules getModule() {
        return ACLModules.LDAP;
    }

    @Override
    public LdapData createModel() {
        return new LdapData();
    }

    @Override
    public HttpHeaders create() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called create()");
        }
        LdapData ldapData = (LdapData) getModel();
        try {
            ldapData.setPassword(PasswordUtility.getEncryptedPassword(ldapData.getPassword()));
        } catch (NoSuchEncryptionException | EncryptionFailedException e) {
            getLogger().error(getLogModule(),"Error while encrypt password for "+ getModule().getDisplayLabel() +". Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while encrypt password "+ getModule().getDisplayLabel());
        }
        setLdapDataToLdapBaseDns(ldapData);
        if(Collectionz.isNullOrEmpty(ldapData.getLdapBaseDns())) {
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information.Reason: At-least one Base DN must be specified.");
            addActionError("At-least one Base DN must be specified.");
            return super.index();
        }
        return super.create();
    }

    @Override
    public HttpHeaders update() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called update()");
        }
        LdapData ldapData = (LdapData) getModel();
        LdapData tempLdapData = CRUDOperationUtil.get(LdapData.class, ldapData.getId());
        if (Objects.isNull(tempLdapData)) {
            addActionError(getText("ldap.datasource.does.not.exist"));
        }else{
            ldapData.setPassword(tempLdapData.getPassword()); //required to set for rest services
        }

        setLdapDataToLdapBaseDns(ldapData);

        if(Collectionz.isNullOrEmpty(ldapData.getLdapBaseDns())) {
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information.Reason: At-least one Base DN must be specified.");
            addActionError("At-least one Base DN must be specified.");
            return super.index();
        }
        return super.update();
    }


    private void setLdapDataToLdapBaseDns(LdapData ldapData) {
        filterEmptyLdapBasedns(ldapData.getLdapBaseDns());
        ldapData.getLdapBaseDns().forEach(ldapBaseDn -> ldapBaseDn.setLdapData(ldapData));
    }

    private void filterEmptyLdapBasedns(List<LdapBaseDn> ldapBaseDns) {

        Collectionz.filter(ldapBaseDns, ldapBaseDn -> {
            if (ldapBaseDn != null) {
                return !Strings.isNullOrBlank(ldapBaseDn.getSearchBaseDn());
            } else {
                return false;
            }
    });
    }

    public String getLdapBaseDnDataAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        LdapData ldapData = (LdapData) getModel();
        return gson.toJsonTree(ldapData.getLdapBaseDns(),new TypeToken<List<LdapBaseDn>>() {}.getType()).getAsJsonArray().toString();
    }

    @Override
    public void validate() {

        LdapData ldapData = (LdapData) getModel();
        String methodName = getMethodName();

        if(CRUDOperationUtil.MODE_CREATE.equalsIgnoreCase(methodName)){
            //PASSWORD IS REQUIRED AT CREATION TIME
            if (Strings.isNullOrBlank(ldapData.getPassword())) {
                addFieldError("password", getText("error.valueRequired"));
            }
        }

        if(ldapData.getMinimumPool() > ldapData.getMaximumPool()) {
            addFieldError("minimumPool", getText("ldap.min.max.pool.value.invalid"));
        }

        if(ldapData.getLdapBaseDns().isEmpty()) {
            addActionError(getText("ldap.base.dn.required"));
        } else {
            for(LdapBaseDn ldapBaseDn : ldapData.getLdapBaseDns()) {
                if (Strings.isNullOrBlank(ldapBaseDn.getSearchBaseDn())) {
                    addActionError(getText("ldap.data.search.base.dn"));
                }
            }
        }
        super.validate();
    }
}
