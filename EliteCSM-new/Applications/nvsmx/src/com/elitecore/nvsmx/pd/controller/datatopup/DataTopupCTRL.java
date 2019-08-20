package com.elitecore.nvsmx.pd.controller.datatopup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.topup.DataTopUpData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.elitecore.nvsmx.system.util.ResourceDataGroupPredicate;
import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates.createStaffBelongingPredicate;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(NVSMXCommonConstants.REST_PARENT_PKG_PD)
@Namespace("/pd/datatopup")
@Results({
        @Result(name = SUCCESS,
                type = RestGenericCTRL.REDIRECT_ACTION,
                params = {NVSMXCommonConstants.ACTION_NAME, "data-topup"})
})
public class DataTopupCTRL extends RestGenericCTRL<DataTopUpData> {
    private static final String MODULE  = "DATA-TOPUP-CTRL";
    private static final Predicate<NotificationTemplateData> EMAIL_TEMPLATE_PREDICATE = notificationTemplateData -> NotificationTemplateType.EMAIL.equals(notificationTemplateData.getTemplateType());
    private static final Predicate<NotificationTemplateData> SMS_TEMPLATE_PREDICATE = notificationTemplateData -> NotificationTemplateType.SMS.equals(notificationTemplateData.getTemplateType());
    private List<NotificationTemplateData> emailTemplates = new ArrayList<>();
    private List<NotificationTemplateData> smsTemplates = new ArrayList<>();
    private Set<String> pccProfileNames;


    @Override
    public HttpHeaders index() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called index()");
        }
        try{
            List<DataTopUpData> dataTopUps = CRUDOperationUtil.findAll(DataTopUpData.class);
            Collectionz.filter(dataTopUps,createStaffBelongingPredicate(getStaffBelongingGroupsIds()));
            setList(dataTopUps);
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while fetching "+getModule().getDisplayLabel()+" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform Search Operation");
        }
        return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.DATATOPUP;
    }

    @Override
    public DataTopUpData createModel() {
        return new DataTopUpData();
    }


    public void prepareShow(){
        ResourceDataGroupPredicate resourceDataGroupPredicate = ResourceDataGroupPredicate.create(getStaffBelongingGroupsIds());
        List<NotificationTemplateData> notificationTemplates = CRUDOperationUtil.findAllWhichIsNotDeleted(NotificationTemplateData.class);
        List<NotificationTemplateData> emailTemplates = notificationTemplates.stream()
                .filter(resourceDataGroupPredicate)
                .filter(EMAIL_TEMPLATE_PREDICATE)
                .collect(Collectors.toList());
        List<NotificationTemplateData> smsTemplates = notificationTemplates.stream()
                .filter(resourceDataGroupPredicate)
                .filter(SMS_TEMPLATE_PREDICATE)
                .collect(Collectors.toList());
        setEmailTemplates(emailTemplates);
        setSmsTemplates(smsTemplates);
    }

    public List<NotificationTemplateData> getEmailTemplates() {
        return emailTemplates;
    }

    public void setEmailTemplates(List<NotificationTemplateData> emailTemplates) {
        this.emailTemplates = emailTemplates;
    }

    public List<NotificationTemplateData> getSmsTemplates() {
        return smsTemplates;
    }

    public void setSmsTemplates(List<NotificationTemplateData> smsTemplates) {
        this.smsTemplates = smsTemplates;
    }

    @Override
    public HttpHeaders create() {
        DataTopUpData model = (DataTopUpData) getModel();
        if(Strings.isNullOrBlank(model.getId()) == false){
            if (isPkgAlreadyExistWithGivenId(model, METHOD_EDITNEW))
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
        }
        setDefaultGroups(model);
        model.setPackageMode(PkgMode.DESIGN.name());
        if(Collectionz.isNullOrEmpty(model.getTopUpNotificationList()) == false){
            model.getTopUpNotificationList().clear();
        }



        return super.create();
    }

    @Override
    public HttpHeaders update() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling update method");
        }
        DataTopUpData model = (DataTopUpData) getModel();
        if(Strings.isNullOrBlank(model.getId()) == false){
            if (isPkgAlreadyExistWithGivenId(model,METHOD_EDIT))
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
        }

        DataTopUpData dataTopUpDataFromDB = CRUDOperationUtil.get(DataTopUpData.class,model.getId());
        if(dataTopUpDataFromDB == null){
            addActionError("Data Top-Up not found with given id: " + model.getId());
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }
        setDefaultGroups(model);
        if(PkgMode.LIVE2.name().equalsIgnoreCase(dataTopUpDataFromDB.getPackageMode())){
           model.setName(dataTopUpDataFromDB.getName());
           model.setDescription(dataTopUpDataFromDB.getDescription());
           model.setGroups(dataTopUpDataFromDB.getGroups());
           model.setMultipleSubscription(dataTopUpDataFromDB.getMultipleSubscription());
           model.setAvailabilityEndDate(dataTopUpDataFromDB.getAvailabilityEndDate());
           model.setAvailabilityStartDate(dataTopUpDataFromDB.getAvailabilityStartDate());
           model.setValidityPeriod(dataTopUpDataFromDB.getValidityPeriod());
           model.setValidityPeriodUnit(dataTopUpDataFromDB.getValidityPeriodUnit());
           model.setPrice(dataTopUpDataFromDB.getPrice());
           model.setParam1(dataTopUpDataFromDB.getParam1());
           model.setParam2(dataTopUpDataFromDB.getParam2());
           model.setApplicablePCCProfiles(dataTopUpDataFromDB.getApplicablePCCProfiles());
        }
        if(PkgMode.LIVE.name().equalsIgnoreCase(dataTopUpDataFromDB.getPackageMode())
                || PkgMode.LIVE2.name().equalsIgnoreCase(dataTopUpDataFromDB.getPackageMode())){
            copyQuotaValueToModelFromDB(model, dataTopUpDataFromDB);
        }
        model.setTopupType(dataTopUpDataFromDB.getTopupType());
        model.setPackageMode(dataTopUpDataFromDB.getPackageMode());
        model.setTopUpNotificationList(dataTopUpDataFromDB.getTopUpNotificationList());
        return super.update();
    }

    private void copyQuotaValueToModelFromDB(DataTopUpData model, DataTopUpData dataTopUpDataFromDB) {
        model.setQuotaType(dataTopUpDataFromDB.getQuotaType());
        model.setUnitType(dataTopUpDataFromDB.getUnitType());
        model.setVolumeBalance(dataTopUpDataFromDB.getVolumeBalance());
        model.setVolumeBalanceUnit(dataTopUpDataFromDB.getVolumeBalanceUnit());
        model.setTimeBalance(dataTopUpDataFromDB.getTimeBalance());
        model.setTimeBalanceUnit(dataTopUpDataFromDB.getTimeBalanceUnit());
    }

    private void setDefaultGroups(DataTopUpData model) {
        if(Strings.isNullOrBlank(model.getGroups())){
            model.setGroups(CommonConstants.DEFAULT_GROUP_ID);
        }
    }

    private boolean isPkgAlreadyExistWithGivenId(DataTopUpData model, String method) {
        PkgData pkgData = CRUDOperationUtil.get(PkgData.class, model.getId());
        if(pkgData != null){
            getLogger().error(getLogModule(),"Data Top-Up can't be created with given Id: "+model.getPackageMode()+" Reason: Package "+pkgData.getName()+" already exist with given Id");
            addActionError("Package "+pkgData.getName()+" already exist with given Id");
            setActionChainUrl(getRedirectURL(method));
            return true;
        }
        return false;
    }

    @Override
    protected boolean prepareAndValidateDestroy(DataTopUpData dataTopUpData) {
        if(PkgMode.LIVE2.name().equalsIgnoreCase(dataTopUpData.getPackageMode())){
            addActionError("Data Top-Up can't up deleted.Reason: "+dataTopUpData.getName()+" is in LIVE2 Stage");
            return false;
        }
        return true;
    }



    @SkipValidation
    public HttpHeaders updateMode() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "update mode is called");
        }
        DataTopUpData model = (DataTopUpData) getModel();
        String pkgModeVal = getRequest().getParameter(Attributes.PKG_MODE);
        PkgMode pkgMode = PkgMode.getMode(pkgModeVal);
        if(Strings.isNullOrBlank(pkgModeVal) || pkgMode == null){
            addActionError("Package Mode not provided/Invalid mode received to update");
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }
        DataTopUpData dataTopUpDataFromDB = CRUDOperationUtil.get(DataTopUpData.class,model.getId());
        if(dataTopUpDataFromDB == null){
            addActionError("Data Top-Up not found with given id:" + model.getId());
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }
        PkgMode pkgNextMode = pkgMode.getNextMode();
        if(pkgNextMode!=null){
            dataTopUpDataFromDB.setPackageMode(pkgNextMode.val);
        }

        List<PolicyDetail> policyDetails = DefaultNVSMXContext.getContext().getPolicyRepository().getPolicyDetail(dataTopUpDataFromDB.getName());
        if(pkgNextMode == PkgMode.LIVE) {
            if (Collectionz.isNullOrEmpty(policyDetails)) {
                getLogger().error(MODULE, "Unable to change package mode. Reason: Policy is not reloaded");
                addActionError("You are recommended to reload policies before updating mode to "+ pkgNextMode);
                setActionChainUrl(getRedirectURL(METHOD_SHOW));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
            }

            for (PolicyDetail policyDetail : policyDetails) {
                String remark = policyDetail.getRemark();
                PolicyStatus status = policyDetail.getStatus();
                if ((status != PolicyStatus.SUCCESS || status != PolicyStatus.PARTIAL_SUCCESS) && Strings.isNullOrEmpty(remark) == false) {
                    getLogger().error(MODULE, "Unable to change package mode to "+ pkgNextMode +".\n " +
                            "Reason: Policy is failed with status " + status + ", " + remark);
                    addActionError("Unable to change package mode to "+ pkgNextMode +".\n " +
                            "Reason: Policy is failed with status " + status + ", " + remark);
                    setActionChainUrl(getRedirectURL(METHOD_SHOW));
                    return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
                }
            }
        }

        setModel(dataTopUpDataFromDB);
        return super.update();
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {

        Set<String> pccProfileNames = PolicyManager.getInstance().base().all().stream()
                .filter(basePackage -> basePackage.getStatus() != PolicyStatus.FAILURE
                        && basePackage.getQuotaProfileType() == QuotaProfileType.RnC_BASED)
                .flatMap(basePackage -> basePackage.getQoSProfiles().stream())
                .map(QoSProfile::getName)
                .collect(Collectors.toSet());

               setPccProfileNames(pccProfileNames);

    }

    public void setPccProfileNames(Set<String> pccProfileNames) {
        this.pccProfileNames = pccProfileNames;
    }

    public Set<String> getPccProfileNames() {
        return pccProfileNames;
    }

    public List<String> getPccProfileNamesForUpdate() {
        DataTopUpData model = (DataTopUpData) getModel();
        if (Objects.isNull(model)) {
            return null;
        }
        return CommonConstants.COMMA_SPLITTER.split(model.getApplicablePCCProfiles());
    }


    private List<String> validatePCCProfiles(DataTopUpData dataTopUpData){

        if(Objects.isNull(dataTopUpData)){
            return null;
        }
        if(StringUtils.isBlank(dataTopUpData.getApplicablePCCProfiles())){
            return null;
        }

        List<String> pccProfiles = CommonConstants.COMMA_SPLITTER.split(dataTopUpData.getApplicablePCCProfiles());
        List<String> inValidPccProfiles = new ArrayList<>();
        for(String pccProfileName : pccProfiles){
            if(isPCCProfileExist(pccProfileName) == false){
                inValidPccProfiles.add(pccProfileName);
            }
        }
        return inValidPccProfiles;

    }
    private boolean isPCCProfileExist(String pccProfileName){
        DetachedCriteria pccProfileCriteria = DetachedCriteria.forClass(QosProfileData.class);
        pccProfileCriteria.createAlias("pkgData", "pkg");
        pccProfileCriteria.add(Restrictions.eq("pkg.quotaProfileType", QuotaProfileType.RnC_BASED));
        pccProfileCriteria.add(Restrictions.eq("pkg.type", PkgType.BASE.name()));
        pccProfileCriteria.setProjection(Projections.rowCount());
        pccProfileCriteria.add(Restrictions.eq("name", pccProfileName));
        List<Long> existingPccProfiles = CRUDOperationUtil.findAllByDetachedCriteria(pccProfileCriteria);
        if (CollectionUtils.isNotEmpty(existingPccProfiles) && existingPccProfiles.get(0) > 0) {
            return true;
        }
        return false;
    }

    @Override
    public void validate() {
        super.validate();
        DataTopUpData dataTopUpData = (DataTopUpData) getModel();
        if (ACLAction.CREATE.name().equalsIgnoreCase(getMethodName())) {
            if (PkgStatus.RETIRED.name().equalsIgnoreCase(dataTopUpData.getStatus())) {
                addActionError("RETIRED status not allowed");
            }
        }
        List<String> invalidPccProfiles= validatePCCProfiles(dataTopUpData);
        if(CollectionUtils.isNotEmpty(invalidPccProfiles)){
            String errorMessage = "Invalid applicable PCC Profiles: "+StringUtils.join(invalidPccProfiles,",")+" configured";
            getLogger().error(MODULE,errorMessage);
            addActionError(errorMessage);
        }
    }
}