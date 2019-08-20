package com.elitecore.nvsmx.pd.controller.sliceconfig;

import java.util.List;
import java.util.stream.Collectors;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pd.sliceconfig.SliceConfigData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.sm.controller.CreateNotSupportedCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.ws.reload.blmanager.ReloadPolicyBlManager;
import com.elitecore.nvsmx.ws.reload.response.ReloadDataSliceConfigurationResponse;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "pd")
@Namespace("/pd/sliceconfig")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","slice-config"})
})
public class SliceConfigCTRL extends CreateNotSupportedCTRL<SliceConfigData> {
    private Long maxSliceTimeValueInMinute=1440L;
    private Boolean timeUnitFlg = true;
    private Boolean volumeUnitFlg = true;
    private Long maxVolume=1048576L;
    private static final ReloadPolicyBlManager reloadPolicyBlManager = new ReloadPolicyBlManager(RMIGroupManager.getInstance(), EndPointManager.getInstance(),DefaultNVSMXContext.getContext().getPolicyRepository());
    private ReloadDataSliceConfigurationResponse reloadDataSliceConfigurationResponse;
    private String unreachableInstances;
    @Override
    public ACLModules getModule() {
        return ACLModules.SLICECONFIGURATION;
    }

    @Override
    public SliceConfigData createModel() {
        return new SliceConfigData();
    }

    @Override
    public HttpHeaders index() {
        return show();
    }

    @Override
    public HttpHeaders show() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called show()");
        }

        SliceConfigData sliceConfigData = (SliceConfigData) getModel();

        if (sliceConfigData == null) {
            setActionChainUrl(getRedirectURL("show"));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
        }

        if (Strings.isNullOrBlank(sliceConfigData.getId()) || "*".equals(sliceConfigData.getId())) {
            List all = CRUDOperationUtil.findAll(getModel().getClass());
            if (Collectionz.isNullOrEmpty(all) == false) {
                sliceConfigData = (SliceConfigData) all.get(0);
                setModel(sliceConfigData);
            }
        } else {
            setModel(CRUDOperationUtil.get(sliceConfigData.getClass(), sliceConfigData.getId()));
        }
        setActionChainUrl(getRedirectURL("show"));
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
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

            SliceConfigData sliceConfigData = (SliceConfigData) getModel();

            sliceConfigData.setModifiedDateAndStaff(getStaffData());
            CRUDOperationUtil.merge(sliceConfigData);

            String message = getModule().getDisplayLabel() + " <b><i>" + sliceConfigData.getResourceName() + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(sliceConfigData,sliceConfigData.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(),sliceConfigData.getHierarchy(), message);

            addActionMessage(getModule().getDisplayLabel()+" updated successfully");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code).disableCaching();

        }catch (Exception e){
            LogManager.getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information. Reason: "+e.getMessage());
            LogManager.getLogger().trace(getLogModule(),e);
            addActionError("Error while performing Update Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

    }

    @SkipValidation
    public String reloadDataSliceConfiguration() {
        reloadDataSliceConfigurationResponse = reloadPolicyBlManager.reloadDataSliceConfiguration();
        if (CommonConstants.SUCCESS.equalsIgnoreCase(reloadDataSliceConfigurationResponse.getResponse()) == false) {
            addActionError("Failed to reload data slice configuration");
        } else {
            if (Collectionz.isNullOrEmpty(reloadDataSliceConfigurationResponse.getRemoteRMIResponsesForDataSlice()) == false) {
                unreachableInstances = reloadDataSliceConfigurationResponse.getRemoteRMIResponsesForDataSlice().stream()
                        .filter(remoteRMIResponse -> ResultCode.INTERNAL_ERROR.code == remoteRMIResponse.getResponseCode() )
                        .map(remoteRMIResponse -> remoteRMIResponse.getInstanceData().getName())
                        .collect(Collectors.joining(","));

                setActionChainUrl(getRedirectURL("reload"));
                return NVSMXCommonConstants.REDIRECT_URL;
            }
        }
        show();
        return NVSMXCommonConstants.REDIRECT_URL;

    }

    @Override
    public void validate() {
        SliceConfigData data = (SliceConfigData)getModel();
        validateVolumeUnit(data.getVolumeMaximumSliceUnit(),"volumeMaximumSliceUnit");
        validateVolumeUnit(data.getVolumeMinimumSliceUnit(),"volumeMinimumSliceUnit");
        validateTimeUnit(data.getTimeMaximumSliceUnit(),"timeMaximumSliceUnit");
        validateTimeUnit(data.getTimeMinimumSliceUnit(),"timeMinimumSliceUnit");
        validateVolume(data.getVolumeMaximumSlice(),"volumeMaximumSlice");
        validateVolume(data.getVolumeMinimumSlice(),"volumeMinimumSlice");
        validateTime(data.getTimeMaximumSlice(),"timeMaximumSlice");
        validateTime(data.getTimeMinimumSlice(),"timeMinimumSlice");
        validateMinMaxVolume(data.getVolumeMinimumSlice(),data.getVolumeMaximumSlice(),"volumeMaximumSlice");
        validateMinMaxTime(data.getTimeMinimumSlice(),data.getTimeMaximumSlice(),"timeMaximumSlice");
    }

    /* To validate entered value is MB for data minSlice/maxSlice*/
    private void validateVolumeUnit(String volumeUnit, String field) {
        if(volumeUnit == null || volumeUnit.equalsIgnoreCase(DataUnit.MB.name())==false) {
            addFieldError(field,"Unit can not be other than MB");
            volumeUnitFlg = false;
        }
    }

    /* To validate entered value is MINUTE for time minSlice/maxSlice*/
    private void validateTimeUnit(String timeUnit, String field) {
        if(timeUnit == null || timeUnit.equalsIgnoreCase(TimeUnit.MINUTE.name() ) == false) {
                addFieldError(field,"Unit can not be other than MINUTE");
                timeUnitFlg = false;
        }
    }

    /* To validate volume should not exceed specified limit*/
    private void validateVolume(Long volumeSlice, String field){
        if(volumeUnitFlg && volumeSlice>maxVolume) {
            addFieldError(field,"max allowed value is "+maxVolume);
        }
        if(volumeUnitFlg && volumeSlice<=0) {
            addFieldError(field,"Value should be greater than zero");
        }
    }

    /* To validate time should not exceed specified limit*/
    private void validateTime(Long timeSlice, String field){
        if(timeUnitFlg && timeSlice>maxSliceTimeValueInMinute){
            addFieldError(field,"max allowed value in MINUTE is "+maxSliceTimeValueInMinute);
        }
        if(timeUnitFlg && timeSlice<=0) {
            addFieldError(field,"Value should be greater than zero");
        }
    }

    /* To validate time min value < max value*/
    private void validateMinMaxVolume(Long dataMinSlice, Long dataMaxSlice, String field){
        if(volumeUnitFlg && dataMinSlice>=dataMaxSlice) {
            addFieldError(field, getText("slice.max.val.greater.than.min.val"));
        }
    }

    /* To validate time min value < max value*/
    private void validateMinMaxTime(Long timeMinimumSlice, Long timeMaximumSlice, String field) {
        if(timeUnitFlg && timeMinimumSlice>=timeMaximumSlice) {
            addFieldError(field, getText("slice.max.val.greater.than.min.val"));
        }
    }

    public ReloadDataSliceConfigurationResponse getReloadDataSliceConfigurationResponse() {
        return reloadDataSliceConfigurationResponse;
    }

    public void setReloadDataSliceConfigurationResponse(ReloadDataSliceConfigurationResponse reloadDataSliceConfigurationResponse) {
        this.reloadDataSliceConfigurationResponse = reloadDataSliceConfigurationResponse;
    }

    public String getUnreachableInstances() {
        return unreachableInstances;
    }

    public void setUnreachableInstances(String unreachableInstances) {
        this.unreachableInstances = unreachableInstances;
    }
}
