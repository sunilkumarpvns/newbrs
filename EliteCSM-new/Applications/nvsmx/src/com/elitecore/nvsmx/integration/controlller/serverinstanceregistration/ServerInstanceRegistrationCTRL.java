package com.elitecore.nvsmx.integration.controlller.serverinstanceregistration;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.commons.util.db.DatabaseTypeNotSupportedException;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupServerInstanceRelData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceRegistrationRequest;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.controller.serverinstance.ServerInstanceWeightage;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.db.NVSMXDBConnectionManager;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.passwordutil.EncryptionFailedException;
import com.elitecore.passwordutil.NoSuchEncryptionException;
import com.elitecore.passwordutil.PasswordEncryption;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.apache.tomcat.dbcp.dbcp2.BasicDataSource;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;

@ParentPackage(value = "integration")
@Namespace("/integration/serverinstanceregistration")
public class ServerInstanceRegistrationCTRL extends RestGenericCTRL<ServerInstanceRegistrationRequest> {

    private static final String COLON = ":";
    private static final int DEFAULT_REST_PORT = 9000;
    private static final String DIAMETER_URL="0.0.0.0:3868";
    private static final String SNMP_URL="0.0.0.0:1161";
    private static final String RADIUS_URL="0.0.0.0:2813";

    @Override
    public ACLModules getModule() {
        return ACLModules.SERVER_INSTANCE_INTEGRATION;
    }

    @Override
    public ServerInstanceRegistrationRequest createModel() {
        return new ServerInstanceRegistrationRequest();
    }

    @SkipValidation
    public HttpHeaders registerServerInstance() {
        ServerInstanceRegistrationRequest serverData = (ServerInstanceRegistrationRequest) getModel();
        try {
            List<ServerGroupData> serverGroupData = CRUDOperationUtil.searchByCriteria(ServerGroupData.class, org.hibernate.criterion.Restrictions.eq("name", serverData.getServerGroupName()));
            if (Collectionz.isNullOrEmpty(serverGroupData)) {
                prepareFailServerResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Server Group does not found with name: " + serverData.getServerGroupName(), serverData);
                return new DefaultHttpHeaders(Results.ERROR.getValue());
            }

            if (!isValidSeverGroupAssociations(serverGroupData.get(0))) {
                    return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }
            List<ServerInstanceData> serverInstanceDatas = CRUDOperationUtil.searchByCriteria(ServerInstanceData.class, org.hibernate.criterion.Restrictions.eq("name", serverData.getServerName()));
            if (Collectionz.isNullOrEmpty(serverInstanceDatas)) {
                ServerInstanceData serverInstanceData = new ServerInstanceData();
                serverInstanceData.setId(UUID.randomUUID().toString());
                serverInstanceData.setName(serverData.getServerName());

                serverInstanceData.setServerGroupId(serverGroupData.get(0).getId());
                serverInstanceData.setJmxUrl(getRequest().getRemoteAddr() + COLON + serverData.getJmxPort());
                serverInstanceData.setRestApiUrl(getRequest().getRemoteAddr() + COLON + DEFAULT_REST_PORT);
                serverInstanceData.setDiameterUrl(DIAMETER_URL);
                serverInstanceData.setDiameterEnabled(true);
                serverInstanceData.setDiameterOriginHost(serverData.getOriginHost());
                serverInstanceData.setDiameterOriginRealm(serverData.getOriginRealm());
                serverInstanceData.setSnmpUrl(SNMP_URL);
                serverInstanceData.setRadiusUrl(RADIUS_URL);
                serverInstanceData.setServerHome(serverData.getServerHome());
                serverInstanceData.setJavaHome(serverData.getJavaHome());
                serverInstanceData.setStatus(NVSMXCommonConstants.ACTIVE);

                StaffData staffData = getStaffData();
                serverInstanceData.setCreatedDateAndStaff(staffData);

                ServerGroupServerInstanceRelData serverGroupServerInstanceRelData = new ServerGroupServerInstanceRelData();
                serverGroupServerInstanceRelData.setServerGroupData(serverGroupData.get(0));
                serverGroupServerInstanceRelData.setServerInstanceData(serverInstanceData);

                if (Collectionz.isNullOrEmpty(serverGroupData.get(0).getServerInstances())) {
                    serverGroupServerInstanceRelData.setServerWeightage(ServerInstanceWeightage.PRIMARY.val);
                } else {
                    serverGroupServerInstanceRelData.setServerWeightage(ServerInstanceWeightage.SECONDARY.val);
                }
                serverInstanceData.setServerGroupServerInstanceRelData(serverGroupServerInstanceRelData);

                CRUDOperationUtil.save(serverInstanceData);
                CRUDOperationUtil.flushSession();
                reload();
                prepareSuccessServerResponse(ResultCode.SUCCESS.code, null, serverData, serverInstanceData);
                return new DefaultHttpHeaders(Results.SUCCESS.getValue());
            } else if (Objects.equals(serverInstanceDatas.get(0).getId(), serverData.getServerInstanceId())
                    || Objects.equals(serverInstanceDatas.get(0).getName(), serverData.getServerName())) {
                prepareSuccessServerResponse(ResultCode.SUCCESS.code, null, serverData, serverInstanceDatas.get(0));
                serverInstanceDatas.get(0).setRestApiUrl(getRequest().getRemoteAddr() + COLON + DEFAULT_REST_PORT);
                serverInstanceDatas.get(0).setJmxUrl(getRequest().getRemoteAddr() + COLON + serverData.getJmxPort());
                CRUDOperationUtil.merge(serverInstanceDatas.get(0));
                CRUDOperationUtil.flushSession();
                reload();
                return new DefaultHttpHeaders(Results.SUCCESS.getValue());
            } else  {
                prepareFailServerResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Server instance already exists with name : " + serverData.getServerName(), serverData);
                return new DefaultHttpHeaders(Results.ERROR.getValue());
            }
        } catch (Exception e) {
            getLogger().error(getLogModule(),"Request failed. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(e.getMessage()+ " Reason:"+e.getCause());
            prepareFailServerResponse(ResultCode.INVALID_INPUT_PARAMETER.code, "Server instance already exists with name : " + serverData.getServerName(), serverData);
            return new DefaultHttpHeaders(Results.ERROR.getValue()).disableCaching().withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
        }
    }

    private static void prepareFailServerResponse(int resultCode, String message, ServerInstanceRegistrationRequest serverData){
        if(serverData!=null){
            serverData.setServerGroupName(null);
            serverData.setServerInstanceId(null);
            serverData.setServerName(null);
            serverData.setMessageCode(resultCode);
            serverData.setMessage(message);
        }
    }

    private static void prepareSuccessServerResponse(int resultCode, String message, ServerInstanceRegistrationRequest serverData, ServerInstanceData serverInstanceData) throws  NoSuchEncryptionException, EncryptionFailedException {
        if(serverData!=null){

            BasicDataSource basicDataSource = NVSMXDBConnectionManager.getInstance().getBasicDataSource();
            serverData.setServerInstanceId(serverInstanceData.getId());
            serverData.setServerName(serverInstanceData.getName());
            serverData.setUserName(basicDataSource.getUsername());
            serverData.setPassword(PasswordEncryption.getInstance().crypt(basicDataSource.getPassword(),PasswordEncryption.ELITE_PASSWORD_CRYPT));
            serverData.setConnectionUrl(basicDataSource.getUrl());
            serverData.setDriverClassName(basicDataSource.getDriverClassName());
            serverData.setMaxIdle(basicDataSource.getMaxIdle());
            serverData.setMaxTotal(basicDataSource.getMaxTotal());
            serverData.setQueryTimeout(basicDataSource.getValidationQueryTimeout());
            serverData.setStatusCheckDuration(serverInstanceData.getServerGroupServerInstanceRelData().getServerGroupData().getDatabaseData().getStatusCheckDuration());
            serverData.setMessageCode(resultCode);
            serverData.setMessage(message);
        }
    }

    private boolean isValidSeverGroupAssociations(ServerGroupData serverGroupData){
        /*  This method is for checking the provided serverGroupId in REST call has already two ServerInstances associated or not.
            If not then serverInstance can be associated with the provided serverGroupId. */

        if (serverGroupData.getServerInstances() != null && serverGroupData.getServerInstances().size() == 2) {
            addActionError("Maximum two ServerInstances can be Associated with one ServerGroup");
            return false;

        }

        return true;
    }

    public void reload() {

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Reload server group cache started");
        }

        try {
            EndPointManager.getInstance().reload();
            RMIGroupManager.getInstance().reload();
        }catch (Exception ex){
            getLogger().error(getLogModule(),"Error while reloading the server group cache. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
        }

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Server group caching completed successfully");
        }

    }
}
