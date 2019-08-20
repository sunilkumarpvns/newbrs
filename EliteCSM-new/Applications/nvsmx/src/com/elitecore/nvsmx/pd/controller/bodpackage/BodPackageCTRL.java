package com.elitecore.nvsmx.pd.controller.bodpackage;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.bod.BoDQosMultiplierData;
import com.elitecore.corenetvertex.pd.bod.BoDServiceMultiplierData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import javax.annotation.Nonnull;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates.createStaffBelongingPredicate;
import static com.elitecore.nvsmx.policydesigner.controller.util.ProductOfferUtility.doesBelongToGroup;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(NVSMXCommonConstants.REST_PARENT_PKG_PD)
@Namespace("/pd/bodpackage")
@Results({
        @Result(name = SUCCESS,
                type = RestGenericCTRL.REDIRECT_ACTION,
                params = {NVSMXCommonConstants.ACTION_NAME, "bod-package"})
})
public class BodPackageCTRL extends RestGenericCTRL<BoDData> {
    private Set<String> qosProfileNames = Collectionz.newHashSet();
    private static final String MODULE  = "BOD-PKG-CTRL";
    private static final int HSQ_LEVEL = 0;
    private static final int FUP_LEVEL_2 = 2;
    private static final int MINIMUM_QOS_MULTIPLIER = 1;
    private static final int MAXIMUM_QOS_MULTIPLIER = 10;

    private static final Predicate<BoDQosMultiplierData> EMPTY_QOS_MULTIPLIER_DATA = bodQosMultiplierData -> {
        if (bodQosMultiplierData == null) {
            return false;
        }
        return (Objects.isNull(bodQosMultiplierData.getFupLevel())
                || Objects.isNull(bodQosMultiplierData.getMultiplier())) == false;
    };

    public Timestamp getCurrentTime(){
        return new Timestamp(TimeSource.systemTimeSource().currentTimeInMillis());
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.BODPACKAGE;
    }

    @Override
    public BoDData createModel() {
        return new BoDData();
    }


    @Override
    public HttpHeaders index() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called index()");
        }
        try{
            List<BoDData> bodPackageList = CRUDOperationUtil.findAll(BoDData.class);
            Collectionz.filter(bodPackageList,createStaffBelongingPredicate(getStaffBelongingGroups()));
            setList(bodPackageList);
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
    public HttpHeaders create() {
        BoDData model = (BoDData) getModel();
        if(Strings.isNullOrBlank(model.getId()) == false){
            if (isBoDPackageAlreadyExistWithGivenId(model, METHOD_EDITNEW))
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
        }
        setDefaultGroups(model);
        model.setPackageMode(PkgMode.DESIGN.name());
        PkgStatus pkgStatus = PkgStatus.fromVal(model.getStatus());
        if(Objects.isNull(pkgStatus) || PkgStatus.RETIRED == pkgStatus) {
            getLogger().error(getLogModule(), "Invalid status provided");
            addActionError(getText("bod.package.invalid.status"));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        Timestamp currentTime = getCurrentTime();
        if(Objects.isNull(model.getAvailabilityStartDate())){
            model.setAvailabilityStartDate(currentTime);
        }

        if(Objects.nonNull(model.getAvailabilityStartDate())){
            if(model.getAvailabilityStartDate().getTime() < currentTime.getTime()){
                getLogger().error(getLogModule(), "Invalid Availability Start Date Received");
                addActionError(getText("bod.package.availability.mustbe.greater.than.current.time"));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
            }
        }

        if(validateEndDateAvailability(model,currentTime) == false)
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();

        if (validateParamLength(model) == false)
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();


        return super.create();
    }

    private boolean validateEndDateAvailability(BoDData model,Timestamp currentTime) {

        if(Objects.nonNull(model.getAvailabilityEndDate())){
            if(model.getAvailabilityEndDate().getTime() < currentTime.getTime()){
                getLogger().error(getLogModule(), "Invalid Availability End Date Received");
                addActionError(getText("bod.package.availability.enddate.greater.than.current.time"));
                return false;
            }else if(model.getAvailabilityStartDate().getTime() > model.getAvailabilityEndDate().getTime()){
                getLogger().error(getLogModule(), "Invalid Availability End Date Received");
                addActionError(getText("bod.package.availability.enddate.mustbe.greaterthan.startdate"));
                return false;
            }
        }
        return true;
    }


    @Override
    public HttpHeaders update() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling update method");
        }
        BoDData bodData = (BoDData) getModel();
        BoDData bodDataFromDB = CRUDOperationUtil.get(BoDData.class, bodData.getId());
        if (bodDataFromDB == null) {
            addActionError("BoD not found with given id: " + bodData.getId());
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }

        JsonObject jsonOldObject = bodDataFromDB.toJson();

        if(PkgMode.LIVE.name().equalsIgnoreCase(bodDataFromDB.getPackageMode())){
            bodDataFromDB.setStatus(bodData.getStatus());
            bodDataFromDB.setPrice(bodData.getPrice());
            bodDataFromDB.setAvailabilityStartDate(bodData.getAvailabilityStartDate());
            bodDataFromDB.setAvailabilityEndDate(bodData.getAvailabilityEndDate());
            bodDataFromDB.setParam1(bodData.getParam1());
            bodDataFromDB.setParam2(bodData.getParam2());
            setModel(bodDataFromDB);
        }else if(PkgMode.LIVE2.name().equalsIgnoreCase(bodDataFromDB.getPackageMode())){
            bodDataFromDB.setStatus(bodData.getStatus());
            setModel(bodDataFromDB);
        }

        if (PkgMode.getMode(bodData.getPackageMode()) == null) {
            addActionError("Invalid value for packageMode");
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        if (PkgStatus.fromVal(bodData.getStatus()) == null) {
            addActionError("Invalid value for status");
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        if (bodData.getValidityPeriod() != null && ValidityPeriodUnit.fromName(bodData.getValidityPeriodUnit()) == null) {
            addActionError("Invalid value for validity period unit");
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        Timestamp currentTime = getCurrentTime();

        if (validateEndDateAvailability(bodData,currentTime) == false)
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();

        if (validateParamLength(bodData) == false)
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();

        bodData.setModifiedDateAndStaff(getStaffData());
        setDefaultGroups(bodData);
        bodData.setBodQosMultiplierDatas(bodDataFromDB.getBodQosMultiplierDatas());
        HttpHeaders result =  super.update();
        return result;
    }

    private boolean validateParamLength(BoDData bodData) {
        if(Objects.nonNull(bodData.getParam1())  && bodData.getParam1().length() > 100){
            getLogger().error(getLogModule(), "Invalid Size of param1");
            addActionError(getText("bod.package.param1.size"));
            return false;
        }

        if(Objects.nonNull(bodData.getParam2()) && bodData.getParam2().length() > 100){
            getLogger().error(getLogModule(), "Invalid Size of param2");
            addActionError(getText("bod.package.param2.size"));
            return false;
        }
        return true;
    }

    private void setDefaultGroups(BoDData model) {
        if(Strings.isNullOrBlank(model.getGroups())){
            model.setGroups(CommonConstants.DEFAULT_GROUP_ID);
        }
    }

    private boolean isBoDPackageAlreadyExistWithGivenId(BoDData model, String method) {
        PkgData pkgData = CRUDOperationUtil.get(PkgData.class, model.getId());
        if(pkgData != null){
            getLogger().error(getLogModule(),"BoD Package can't be created with given Id: "+model.getPackageMode()+" Reason: Id "+pkgData.getName()+" already exist");
            addActionError("BoD Package "+pkgData.getName()+" already exist with given Id");
            setActionChainUrl(getRedirectURL(method));
            return true;
        }
        return false;
    }

    @Override
    protected boolean prepareAndValidateDestroy(BoDData bodData) {
        if(PkgMode.LIVE2.name().equalsIgnoreCase(bodData.getPackageMode()) || PkgMode.LIVE.name().equalsIgnoreCase(bodData.getPackageMode())){
            addActionError("BoD Package can not be deleted.Reason: "+bodData.getName()+" is in "+bodData.getPackageMode()+" Stage");
            return false;
        }
        return true;
    }

    @SkipValidation
    public HttpHeaders updateMode() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "update mode is called");
        }
        BoDData model = (BoDData) getModel();
        String nextModeVal = getRequest().getParameter(Attributes.PKG_MODE);
        PkgMode nextMode = PkgMode.getMode(nextModeVal);
        if(Strings.isNullOrBlank(nextModeVal) || Objects.isNull(nextMode)){
            addActionError("Package Mode not provided/Invalid mode received to update");
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }
        BoDData bodDataFromDB = CRUDOperationUtil.get(BoDData.class,model.getId());
        if(bodDataFromDB == null){
            addActionError("BoD Package not found with given id:" + model.getId());
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }

        bodDataFromDB.setPackageMode(nextMode.val);

        List<PolicyDetail> policyDetails = DefaultNVSMXContext.getContext().getPolicyRepository().getBoDPackageDetails(bodDataFromDB.getName());
        if(nextMode == PkgMode.LIVE) {
            if (Collectionz.isNullOrEmpty(policyDetails)) {
                getLogger().error(MODULE, "Unable to change BoD package mode. Reason: Policy is not reloaded");
                addActionError("You are recommended to reload policies before updating mode to "+ nextModeVal);
                setActionChainUrl(getRedirectURL(METHOD_SHOW));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
            }

            for (PolicyDetail policyDetail : policyDetails) {
                String remark = policyDetail.getRemark();
                PolicyStatus status = policyDetail.getStatus();
                if ((status != PolicyStatus.SUCCESS || status != PolicyStatus.PARTIAL_SUCCESS) && Strings.isNullOrEmpty(remark) == false) {
                    getLogger().error(MODULE, "Unable to change BoD package mode to "+ nextModeVal +".\n " +
                            "Reason: Policy is failed with status " + status + ", " + remark);
                    addActionError("Unable to change package mode to "+ nextModeVal +".\n " +
                            "Reason: Policy is failed with status " + status + ", " + remark);
                    setActionChainUrl(getRedirectURL(METHOD_SHOW));
                    return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
                }
            }
        }

        setModel(bodDataFromDB);
        return super.update();
    }

    public Set<String> getQosProfileNames() {
        return qosProfileNames;
    }

    public void setQosProfileNames(Set<String> qosProfileNames) {
        this.qosProfileNames = qosProfileNames;
    }

    private List<String> getStaffBelongingGroups() {
        String groups = (String) getRequest().getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
        if (groups == null) {
            groups = "";
        }
        return CommonConstants.COMMA_SPLITTER.split(groups);
    }

    @Override
    public void prepareValuesForSubClass() throws Exception{
        Set<String> pccProfileNames = PolicyManager.getInstance().base().all().stream()
                .filter(basePackage -> basePackage.getStatus() != PolicyStatus.FAILURE)
                .filter(basePackage -> doesBelongToGroup(basePackage.getGroupIds(), getStaffBelongingGroups()))
                .flatMap(basePackage -> basePackage.getQoSProfiles().stream())
                .map(QoSProfile::getName)
                .collect(Collectors.toSet());

        setQosProfileNames(pccProfileNames);
    }

    @SkipValidation
    public HttpHeaders addBoDQosMultipliers() throws Exception {
        authorize();
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling Add QoS Multiplier Method");
        }
        BoDData boDData = (BoDData) getModel();
        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(boDData.getId()));
        setBoDQosMultiplierData(boDData);
        BoDData boDDataInDB = CRUDOperationUtil.get(BoDData.class, boDData.getId());

        if (boDDataInDB == null) {
            getLogger().error(getLogModule(), "Unable to find BoD Data for Given Id");
            addActionError("BoD not found");
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
        }
        BoDQoSMultiplierPredicate boDQoSMultiplierPredicate = new BoDQoSMultiplierPredicate(boDDataInDB.getBodQosMultiplierDatas());
        List<BoDQosMultiplierData> duplicateMappingList = Lists.copy(boDData.getBodQosMultiplierDatas(), boDQoSMultiplierPredicate);
        if (Collectionz.isNullOrEmpty(duplicateMappingList) == false) {
            getLogger().error(getLogModule(), "Duplicate Mapping found for BoD Qos Multiplier");
            addActionError(getText("bod.package.qos.multiplier.duplicate.error"));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        List<Integer> fupLevelList = new ArrayList<>(3);

        for(BoDQosMultiplierData boDQosMultiplierData : boDData.getBodQosMultiplierDatas()){

            if(Objects.isNull(boDQosMultiplierData.getFupLevel())){
                getLogger().error(getLogModule(), "Mandatory parameter fup level or multiplier is missing");
                addActionError(getText("Mandatory parameter fup level or multiplier is missing"));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INPUT_PARAMETER_MISSING.code).disableCaching();
            }

            if(boDQosMultiplierData.getFupLevel() < HSQ_LEVEL || boDQosMultiplierData.getFupLevel() > FUP_LEVEL_2 || boDQosMultiplierData.getMultiplier() < MINIMUM_QOS_MULTIPLIER
                    || boDQosMultiplierData.getMultiplier() > MAXIMUM_QOS_MULTIPLIER) {
                getLogger().error(getLogModule(), "Invalid value provided for fup level or multiplier");
                addActionError(getText("bod.package.invalid.qos.multiplier.value"));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
            }

            if(fupLevelList.contains(boDQosMultiplierData.getFupLevel())){
                getLogger().error(getLogModule(), "Duplicate Mapping found for Fup Level");
                addActionError(getText("Duplicate Mapping found for Fup Level"));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
            }else {
                fupLevelList.add(boDQosMultiplierData.getFupLevel());
            }
            List<BoDServiceMultiplierData> bodServiceMultiplierDatas = boDQosMultiplierData.getBodServiceMultiplierDatas();
            if(Collectionz.isNullOrEmpty(bodServiceMultiplierDatas) == false) {
                for(BoDServiceMultiplierData boDServiceMultiplierData : bodServiceMultiplierDatas) {
                    DataServiceTypeData serviceData = null;
                    if(Objects.nonNull(boDServiceMultiplierData.getServiceTypeData())) {
                        serviceData = CRUDOperationUtil.get(DataServiceTypeData.class, boDServiceMultiplierData.getServiceTypeData().getId());
                    }

                    if (serviceData == null) {
                        getLogger().error(getLogModule(), getText("bod.package.service.not.exist"));
                        addActionError(getText("bod.package.service.not.exist"));
                        addActionMessage(getText("bod.package.service.not.exist"));
                        setActionChainUrl(getRedirectURL(METHOD_INDEX));
                        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
                    } else {
                        boDServiceMultiplierData.setServiceTypeData(serviceData);
                        boDServiceMultiplierData.setBodQosMultiplierData(boDQosMultiplierData);
                    }
                }
            }
        }

        for(BoDQosMultiplierData boDQosMultiplierData : boDData.getBodQosMultiplierDatas()) {
            for (BoDQosMultiplierData boDQosMultiplierDataInDB : boDDataInDB.getBodQosMultiplierDatas()) {
                if(boDQosMultiplierData.getFupLevel().equals(boDQosMultiplierDataInDB.getFupLevel())){
                    getLogger().error(getLogModule(), "Duplicate Mapping found for Fup Level");
                    addActionError(getText("Duplicate Mapping found for Fup Level"));
                    return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
                }
            }
        }

        JsonObject jsonOldObject = boDDataInDB.toJson();
        boDDataInDB.getBodQosMultiplierDatas().addAll(boDData.getBodQosMultiplierDatas());

        boolean isPreviousFuPConfigured = false;
        for(BoDQosMultiplierData boDQosMultiplierData : boDData.getBodQosMultiplierDatas()){
            if(boDQosMultiplierData.getFupLevel() != HSQ_LEVEL) {
                for (BoDQosMultiplierData boDQosMultiplierDataInDB : boDDataInDB.getBodQosMultiplierDatas()) {
                    if (boDQosMultiplierDataInDB.getFupLevel() == boDQosMultiplierData.getFupLevel() - 1) {
                        isPreviousFuPConfigured = true;
                        break;
                    }
                }
            }else{
                isPreviousFuPConfigured = true;
            }

            if(isPreviousFuPConfigured == false){
                getLogger().error(getLogModule(), "Can not configure BoD qos multiplier for fup level " + boDQosMultiplierData.getFupLevel()
                        + " as the previous fup level " + (boDQosMultiplierData.getFupLevel() - 1) + " is not configured");
                addActionError(getText("Can not configure BoD qos multiplier for fup level " + boDQosMultiplierData.getFupLevel()
                        + " as the previous fup level " + (boDQosMultiplierData.getFupLevel() - 1) + " is not configured"));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
            }
        }

        CRUDOperationUtil.merge(boDDataInDB);

        JsonObject jsonNewObject = boDDataInDB.toJson();
        addActionMessage(getModule().getDisplayLabel() + " updated successfully");

        JsonArray diff = ObjectDiffer.diff(jsonOldObject, jsonNewObject);
        String message = getModule().getDisplayLabel() + " <b><i>" + FieldValueConstants.BOD_PACKAGE + "</i></b> " + "Updated";
        CRUDOperationUtil.audit(boDDataInDB, boDDataInDB.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), diff, boDDataInDB.getHierarchy(), message);

        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(boDData.getId()));
        CRUDOperationUtil.flushSession();
        return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }

    private void setBoDQosMultiplierData(BoDData boDData) {

        if (Collectionz.isNullOrEmpty(boDData.getBodQosMultiplierDatas())) {
            return;
        }
        Collectionz.filter(boDData.getBodQosMultiplierDatas(), EMPTY_QOS_MULTIPLIER_DATA);

        boDData.getBodQosMultiplierDatas()
                .forEach(bodQosMultiplierData -> bodQosMultiplierData.setBodData(boDData
                ));
    }

    private static class BoDQoSMultiplierPredicate implements Predicate<BoDQosMultiplierData> {

        private List<BoDQosMultiplierData> boDQosMultiplierDatas;

        private BoDQoSMultiplierPredicate(@Nonnull List<BoDQosMultiplierData> boDQosMultiplierData) {
            this.boDQosMultiplierDatas = boDQosMultiplierData;
        }

        @Override
        public boolean apply(BoDQosMultiplierData boDQosMultiplierData) {
            return boDQosMultiplierDatas.contains(boDQosMultiplierData);
        }
    }
}