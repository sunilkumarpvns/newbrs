package com.elitecore.nvsmx.sm.controller.gateway.radius;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommunicationProtocol;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayData;
import com.elitecore.corenetvertex.sm.gateway.RadiusGatewayProfileData;
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
import org.hibernate.criterion.Order;

import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by Ishani on 23/8/17.
 */
@ParentPackage(value = "sm")
@Namespace("/sm/gateway")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","radius-gateway"}),
})
public class RadiusGatewayCTRL extends RestGenericCTRL<RadiusGatewayData> {

    List<RadiusGatewayProfileData> radiusGatewayProfiles = Collectionz.newArrayList();

    @Override
    public ACLModules getModule() {
        return ACLModules.GATEWAY;
    }

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[1]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    @Override
    @SkipValidation
    public HttpHeaders index() {
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, CommunicationProtocol.RADIUS.name());
        HttpHeaders result = super.index();
        setActionChainUrl("sm/gateway/gateway/index");
        return result;
    }

    @Override
    public HttpHeaders create(){
        try {
            setRadiusProfileBasedOnId();
            return super.create();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while creating " + getModule().getDisplayLabel() + " information.Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while performing Create Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

    }
    public HttpHeaders update() { // update
        try {
            setRadiusProfileBasedOnId();
            return super.update();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information.Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while performing Update Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

    }

    @Override
    public RadiusGatewayData createModel() {
        return new RadiusGatewayData();
    }

    public void validate() {
        super.validate();
        RadiusGatewayData radiusGatewayData = (RadiusGatewayData) getModel();
        if(Strings.isNullOrBlank(radiusGatewayData.getRadiusGatewayProfileId()) == false){
            RadiusGatewayProfileData radiusGatewayProfileData = CRUDOperationUtil.get(RadiusGatewayProfileData.class,radiusGatewayData.getRadiusGatewayProfileId());
            if(radiusGatewayProfileData == null){
                addActionError("RADIUS Gateway Profile Id does not exist");
            }else{
                RadiusGatewayProfileData radiusGatewayProfile = new RadiusGatewayProfileData();
                radiusGatewayProfile.setId(radiusGatewayData.getRadiusGatewayProfileId());
                radiusGatewayData.setRadiusGatewayProfileData(radiusGatewayProfile);
            }
        }
    }

    private void setRadiusProfileBasedOnId() {
        RadiusGatewayData radiusGatewayData = (RadiusGatewayData) getModel();
        RadiusGatewayProfileData profileData = CRUDOperationUtil.get(RadiusGatewayProfileData.class, radiusGatewayData.getRadiusGatewayProfileId());
        radiusGatewayData.setRadiusGatewayProfileData(profileData);
    }


    public List<RadiusGatewayProfileData> getRadiusGatewayProfiles() {
        return radiusGatewayProfiles;
    }

    public void setRadiusGatewayProfiles(List<RadiusGatewayProfileData> RadiusGatewayProfiles) {
        this.radiusGatewayProfiles = RadiusGatewayProfiles;
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setRadiusGatewayProfiles(CRUDOperationUtil.get(RadiusGatewayProfileData.class,Order.asc("name")));
    }
}
