package com.elitecore.nvsmx.sm.controller.sessionconfiguration;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.BatchUpdateMode;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.database.DatabaseData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.session.SessionConfigurationData;
import com.elitecore.corenetvertex.sm.session.SessionConfigurationFieldMappingData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.CreateNotSupportedCTRL;
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

/**
 * Used to manage Session Configuration
 * Created by dhyani on 22/8/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/sessionconfiguration")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","session-configuration"}),

})
public class SessionConfigurationCTRL extends CreateNotSupportedCTRL<SessionConfigurationData> {


    private List<DatabaseData> databaseDataList;
    @Override
    public ACLModules getModule() {
        return ACLModules.SESSIONCONFIGURATION;
    }

    @Override
    public SessionConfigurationData createModel() {
        return new SessionConfigurationData();
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setDatabaseDataList(CRUDOperationUtil.findAll(DatabaseData.class));
    }

    public void setDatabaseDataList(List<DatabaseData> databaseDataList) {
        this.databaseDataList = databaseDataList;
    }

    public List<DatabaseData> getDatabaseDataList() {
        return databaseDataList;
    }


    @Override
    public HttpHeaders update() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called update()");
        }
        try {

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            SessionConfigurationData sessionConfigurationData = (SessionConfigurationData) getModel();
            setSessionConfToSessionFieldMapping(sessionConfigurationData);

            sessionConfigurationData.setModifiedDateAndStaff(getStaffData());
            CRUDOperationUtil.merge(sessionConfigurationData);

            String message = getModule().getDisplayLabel() + " <b><i>" + sessionConfigurationData.getResourceName() + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(sessionConfigurationData,sessionConfigurationData.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(),sessionConfigurationData.getHierarchy(), message);

            addActionMessage(getModule().getDisplayLabel()+" updated successfully");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code).disableCaching();

        }catch (Exception e){
            LogManager.getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information. Reason: "+e.getMessage());
            LogManager.getLogger().trace(getLogModule(),e);
            addActionError("Error while performing Update Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

    }

    @Override
    public HttpHeaders index() {
        return show();
    }

    @Override
    public HttpHeaders show() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called show()");
        }

        SessionConfigurationData  sessionConfigurationData = (SessionConfigurationData) getModel();

        if (sessionConfigurationData == null){
            setActionChainUrl(getRedirectURL("show"));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
        }

        if (Strings.isNullOrBlank(sessionConfigurationData.getId()) || "*".equals(sessionConfigurationData.getId())) {
            List all = CRUDOperationUtil.findAll(getModel().getClass());
            if(Collectionz.isNullOrEmpty(all) == false){
                sessionConfigurationData = (SessionConfigurationData) all.get(0);
                setModel(sessionConfigurationData);
            }
        } else {
            setModel(CRUDOperationUtil.get(sessionConfigurationData.getClass(), sessionConfigurationData.getId()));
        }
        setActionChainUrl(getRedirectURL("show"));
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
    }


    private void setSessionConfToSessionFieldMapping(SessionConfigurationData sessionConfigurationData) {
        filterEmptyFieldMapping(sessionConfigurationData.getSessionConfigurationFieldMappingDatas());
        sessionConfigurationData.getSessionConfigurationFieldMappingDatas().forEach(fieldMappingData -> fieldMappingData.setSessionConfigurationData(sessionConfigurationData));
    }

    private void filterEmptyFieldMapping(List<SessionConfigurationFieldMappingData> mappingDatas) {

        Collectionz.filter(mappingDatas, (SessionConfigurationFieldMappingData fieldMappingData) -> {
            if(fieldMappingData == null) {
                return false;
            }

            return fieldMappingData.getFieldName() != null || (fieldMappingData.getReferringAttribute() != null);
        });
    }

    public String getSessionFieldMappingAsJson() {
        Gson gson = GsonFactory.defaultInstance();
        SessionConfigurationData sessionConfigurationData = (SessionConfigurationData) getModel();
        return gson.toJsonTree(sessionConfigurationData.getSessionConfigurationFieldMappingDatas(),new TypeToken<List<SessionConfigurationFieldMappingData>>() {}.getType()).getAsJsonArray().toString();
    }


    @Override
    public void validate() {
        SessionConfigurationData sessionConfigurationData = (SessionConfigurationData) getModel();
        validateBatchParameters(sessionConfigurationData);
        validateFieldMappings(sessionConfigurationData);
        validateDatabaseDataSource(sessionConfigurationData);
    }

    private void validateDatabaseDataSource(SessionConfigurationData sessionConfigurationData) {
        String databaseId = sessionConfigurationData.getDatabaseId();
        if(Strings.isNullOrBlank(databaseId) == false){
            DatabaseData databaseExists = CRUDOperationUtil.get(DatabaseData.class, databaseId);
            if(databaseExists == null){
                addFieldError("databaseId",getText("session.conf.invalid.datasource"));
            }else{
                sessionConfigurationData.setDatabaseData(databaseExists);
            }
        }
    }

    private void validateFieldMappings(SessionConfigurationData sessionConfigurationData) {
        List<SessionConfigurationFieldMappingData> sessionConfigurationFieldMappingDatas = sessionConfigurationData.getSessionConfigurationFieldMappingDatas();
        if(Collectionz.isNullOrEmpty(sessionConfigurationFieldMappingDatas) == false) {
            for (SessionConfigurationFieldMappingData fieldMappingData : sessionConfigurationFieldMappingDatas) {
                if (Strings.isNullOrBlank(fieldMappingData.getFieldName())) {
                    addFieldError("fieldName", getText("session.conf.fieldmapping.required"));
                }
                if (Strings.isNullOrBlank(fieldMappingData.getReferringAttribute())) {
                    addFieldError("referringAttribute", getText("session.conf.referringattr.required"));
                }

            }
        }
    }

    private void validateBatchParameters(SessionConfigurationData sessionConfigurationData) {
        if(sessionConfigurationData.getBatchMode() != BatchUpdateMode.FALSE.getValue()){
            Integer batchSize = sessionConfigurationData.getBatchSize();
            if(batchSize <= 0){
                addFieldError("batchSize",getText("session.conf.invalid.batchsize"));
            }else if(batchSize < CommonConstants.BATCH_SIZE_MIN || batchSize > CommonConstants.MAX_BATCH_SIZE){
                addFieldError("batchSize",getText("session.conf.invalid.batchsize.range"));
            }
            if(sessionConfigurationData.getQueryTimeout() <= 0){
                addFieldError("queryTimeout",getText("session.conf.invalid.querytimeout"));
            }else if(sessionConfigurationData.getQueryTimeout() < CommonConstants.MIN_QUERY_TIMEOUT_IN_SEC || sessionConfigurationData.getQueryTimeout() > CommonConstants.MAX_QUERY_TIMEOUT_IN_SEC){
                addFieldError("queryTimeout",getText("invalid.query.timeout"));
            }

        }

    }
}