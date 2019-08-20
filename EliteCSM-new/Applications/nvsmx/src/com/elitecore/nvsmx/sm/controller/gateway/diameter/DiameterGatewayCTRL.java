package com.elitecore.nvsmx.sm.controller.gateway.diameter;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayProfileData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.LogicalExpression;
import org.hibernate.criterion.Order;
import org.hibernate.criterion.Restrictions;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by Ishani on 23/8/17.
 */
@ParentPackage(value = "sm")
@Namespace("/sm/gateway")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","diameter-gateway"}),
})
public class DiameterGatewayCTRL extends RestGenericCTRL<DiameterGatewayData> {
    private static final Criterion ALTERNATE_HOST_NOT_NULL = Restrictions.isNotNull("alternateHost");
    private static final String ALTERNATE_HOST_ID = "alternateHost.id";
    private List<DiameterGatewayData> alternateHosts = Collectionz.newArrayList();
    private List<DiameterGatewayProfileData> diameterGatewayProfiles = Collectionz.newArrayList();

    @Override
    public ACLModules getModule() {
        return ACLModules.GATEWAY;
    }

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[0]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }
    @Override
    public DiameterGatewayData createModel() {
        return new DiameterGatewayData();
    }

    @Override
    @SkipValidation
    public HttpHeaders index() {
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, CommunicationProtocol.DIAMETER.name());
        HttpHeaders result = super.index();
        setActionChainUrl("sm/gateway/gateway/index");
        return result;
    }

    @Override
    public HttpHeaders create(){
        try {
            setDiameterProfileAndAlternateHostBasedOnId();
            return super.create();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while creating " + getModule().getDisplayLabel() + " information.Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while performing Create Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

    }

    @Override
    public String edit() { // initUpdate
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called edit()");
        }
        try {
             DiameterGatewayData diameterGatewayData = (DiameterGatewayData) getModel();
            if(Strings.isNullOrBlank(diameterGatewayData.getId()) == false) {
                diameterGatewayData = CRUDOperationUtil.get(DiameterGatewayData.class, diameterGatewayData.getId());
                setAlternateHosts(getAlternateHostList());
                setModel(diameterGatewayData);
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

    @Override
    public HttpHeaders update() { // update
        try {
            setDiameterProfileAndAlternateHostBasedOnId();
            return super.update();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information.Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while performing Update Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

    }


    @Override
    protected boolean prepareAndValidateDestroy(DiameterGatewayData diameterGatewayData) {

        List<DiameterGatewayData> alternateHosts = CRUDOperationUtil.findAll(DiameterGatewayData.class, getAlternateHostRestriction(diameterGatewayData.getId()));
        if (Collectionz.isNullOrEmpty(alternateHosts) == false) {
            addActionError("Diameter Gateway " + diameterGatewayData.getName() + " is associated as Alternate Host");
            String alternateHostNames = Strings.join(Character.toString(CommonConstants.COMMA), alternateHosts, DiameterGatewayData::getName);
            getLogger().error(getLogModule(), "Error while deleting Diameter Gateway " + diameterGatewayData.getName() + " . Reason: gateway is associated as Alternate Host for " + alternateHostNames + " Gateway");
            return false;
        }
        return true;
    }

    private LogicalExpression getAlternateHostRestriction(String diameterGatewayId) {
        return Restrictions.and(ALTERNATE_HOST_NOT_NULL, Restrictions.eq(ALTERNATE_HOST_ID, diameterGatewayId));
    }

    @SkipValidation
    public List<DiameterGatewayData> getAlternateHostList() {
        DiameterGatewayData gatewayData = (DiameterGatewayData) getModel();
        List<DiameterGatewayData> gatewayDatas = CRUDOperationUtil.findAll(DiameterGatewayData.class);
        if (Strings.isNullOrBlank(gatewayData.getId()) == false) {
            Collectionz.filter(gatewayDatas, diameterGw -> {
                if (diameterGw.getId().equals(gatewayData.getId())) {
                    return false;
                } else {
                    return true;
                }
            });
        }
        return gatewayDatas;
    }

    @Override
    public void validate(){
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"Method called validate()");
        }
        super.validate();
        DiameterGatewayData diameterGatewayData = (DiameterGatewayData) getModel();
        if(Strings.isNullOrBlank(diameterGatewayData.getHostIdentity()) == false){
            boolean isAlreadyExists;
            if(Strings.isNullOrBlank(diameterGatewayData.getId())){
                isAlreadyExists = isDuplicateEntity("hostIdentity",diameterGatewayData.getHostIdentity(),"create");
            }else{
                isAlreadyExists = isDuplicateEntity("hostIdentity",diameterGatewayData.getHostIdentity(),"update");
            }
            if(isAlreadyExists){
                addActionError("Host Identity already exists");
            }
        }
        if(Strings.isNullOrBlank(diameterGatewayData.getAlternateHostId()) == false){
            DiameterGatewayData alternateHostExists = CRUDOperationUtil.get(DiameterGatewayData.class,diameterGatewayData.getAlternateHostId());
            if(alternateHostExists == null){
                addActionError("Alternate Host id does not exist");
            }else if(Strings.isNullOrBlank(diameterGatewayData.getId()) == false && diameterGatewayData.getAlternateHostId().equals(diameterGatewayData.getId())) {
                addActionError("Alternate Host id and Diameter Gateway Id must not be same");
            }else{
                DiameterGatewayData alternateHostData = new DiameterGatewayData();
                alternateHostData.setId(diameterGatewayData.getAlternateHostId());
                diameterGatewayData.setAlternateHost(alternateHostData);
            }
        }
        if(Strings.isNullOrBlank(diameterGatewayData.getDiameterGatewayProfileId()) == false){
            DiameterGatewayProfileData diameterGatewayProfileData = CRUDOperationUtil.get(DiameterGatewayProfileData.class,diameterGatewayData.getDiameterGatewayProfileId());
            if(diameterGatewayProfileData == null){
                addActionError("Diameter Gateway Profile Id does not exist");
            }else{
                DiameterGatewayProfileData diameterGatewayProfile = new DiameterGatewayProfileData();
                diameterGatewayProfile.setId(diameterGatewayData.getDiameterGatewayProfileId());
                diameterGatewayData.setDiameterGatewayProfileData(diameterGatewayProfile);
            }
        }
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
            setAlternateHosts(getAlternateHostList());
            setDiameterGatewayProfiles(CRUDOperationUtil.get(DiameterGatewayProfileData.class, Order.asc("name")));
    }

    private void setDiameterProfileAndAlternateHostBasedOnId() {
        DiameterGatewayData diameterGatewayData = (DiameterGatewayData) getModel();
        if (Strings.isNullOrBlank(diameterGatewayData.getAlternateHostId()) == false) {
            DiameterGatewayData alternateHostData = CRUDOperationUtil.get(DiameterGatewayData.class, diameterGatewayData.getAlternateHostId());
            diameterGatewayData.setAlternateHost(alternateHostData);
        }
        DiameterGatewayProfileData profileData = CRUDOperationUtil.get(DiameterGatewayProfileData.class, diameterGatewayData.getDiameterGatewayProfileId());
        diameterGatewayData.setDiameterGatewayProfileData(profileData);
    }

    public List<DiameterGatewayData> getAlternateHosts() {
        return alternateHosts;
    }

    public void setAlternateHosts(List<DiameterGatewayData> alternateHosts) {
        this.alternateHosts = alternateHosts;
    }

    public List<DiameterGatewayProfileData> getDiameterGatewayProfiles() {
        return diameterGatewayProfiles;
    }

    public void setDiameterGatewayProfiles(List<DiameterGatewayProfileData> diameterGatewayProfiles) {
        this.diameterGatewayProfiles = diameterGatewayProfiles;
    }
}
