package com.elitecore.nvsmx.pd.controller.monetaryratecard;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersionDetail;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardScope;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.constants.OperationType;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.BigDecimalToStringGsonAdapter;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.LongToStringGsonAdapter;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.math.NumberUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.exception.ConstraintViolationException;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.regex.Pattern;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_ITALIC_TEXT_TAG;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * manage Monetary RateCard related information. Created by Saket on 17/12/17.
 */

@ParentPackage(value = "pd")
@Namespace("/pd/monetaryratecard")
@Results({ @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = { NVSMXCommonConstants.ACTION_NAME,
        "monetary-rate-card" }),

})
public class MonetaryRateCardCTRL extends RestGenericCTRL<RateCardData> {

    private static final long serialVersionUID = -6882470730134447721L;

    private String monetaryRateCardAsJson;
    private String rncPackageId;
    private String chargingType;
    private static final String REGEX_PATTERN = "^((\\d{0,9}(\\.\\d{0,6})?))$";
    private List<Uom> pulseUnits = Collectionz.newArrayList();

    private String monetaryRateCardVersionsAsJson;
    private BigDecimal bulkUpdateRate;
    private OperationType rateUnitOperation;

    private List<RevenueDetailData> revenueDetails;

    @Override
    public ACLModules getModule() {
        return ACLModules.MONETARYRATECARD;
    }

    @Override
    public RateCardData createModel() {
        return new RateCardData();
    }

    public String getRncPackageId() {
        return rncPackageId;
    }

    public void setRncPackageId(String rncPackageId) {
        this.rncPackageId = rncPackageId;
    }

    @SkipValidation
    @Override
    public void prepareValuesForSubClass() throws Exception {
        List<RevenueDetailData> revenueDetailDataList = CRUDOperationUtil.findAll(RevenueDetailData.class);
        setRevenueDetails(revenueDetailDataList);
    }

    @Override
    public String editNew() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called editNew()");
        }
        try {
            String rncPkgId = getRequest().getParameter(NVSMXCommonConstants.RNC_PACKAGE_ID);
            if (Strings.isNullOrBlank(rncPkgId) == false) {
                RateCardData dataRateCardData = (RateCardData) getModel();
                dataRateCardData.setRncPkgId(rncPkgId);
                setModel(dataRateCardData);
                RncPackageData rncPackageData = CRUDOperationUtil.get(RncPackageData.class, rncPkgId);
                dataRateCardData.setChargingType(rncPackageData.getChargingType());
                dataRateCardData.setCurrency(rncPackageData.getCurrency());

            }
            setRateAndPulseUnits();
            setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
            return NVSMXCommonConstants.REDIRECT_URL;
        }catch (Exception e) {
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform create Operation");
            return ERROR;
        }

    }

    private void setRateAndPulseUnits() {
        pulseUnits.addAll(Uom.getTimeUoms());
    }

    @Override
    public HttpHeaders create() {
            if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called create()");
        }

        RateCardData rateCardData = (RateCardData) getModel();
        if(rateCardData.getScope().equalsIgnoreCase(RateCardScope.GLOBAL.name()) == false) {
            MonetaryRateCardData monetaryRateCard = rateCardData.getMonetaryRateCardData();
            setRateCardDataToRateCardVersionRelation(monetaryRateCard);
            setRateCardVersionRelationToRateCardVersionDetail(monetaryRateCard);

            rateCardData.setGroups(rateCardData.getRncPackageData().getGroups());

            if (Objects.isNull(rateCardData.getScope())) {
                rateCardData.setScope(RateCardScope.LOCAL.name());
            }
            if (Objects.isNull(rateCardData.getChargingType())) {
                rateCardData.setChargingType(rateCardData.getRncPackageData().getChargingType());
            }
        }

        HttpHeaders headers = super.create();
        if(hasActionErrors()){
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
        }

        if(rateCardData.getScope().equalsIgnoreCase(RateCardScope.GLOBAL.name()) == false) {
            setActionChainUrl(getRedirectToParentURL(rateCardData.getRncPkgId()));
        }

        return headers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String edit() {
        RateCardData rateCardData = (RateCardData) getModel();
        RateCardData resourceInDB = CRUDOperationUtil.get((Class<RateCardData>) rateCardData.getClass(),
                rateCardData.getId(), getAdditionalCriteria());

        if(Objects.isNull(resourceInDB.getScope()) == false && resourceInDB.getScope().equalsIgnoreCase(RateCardScope.LOCAL.name()) ) {
            RncPackageData rncPackageData = CRUDOperationUtil.get(RncPackageData.class, resourceInDB.getRncPkgId());
            resourceInDB.setChargingType(rncPackageData.getChargingType());
        }

        MonetaryRateCardData monetaryRateCardDataFromDB = resourceInDB.getMonetaryRateCardData();
        Gson gson = GsonFactory.defaultInstance();
        JsonArray modelJson = gson
                .toJsonTree(monetaryRateCardDataFromDB.getMonetaryRateCardVersions(), new TypeToken<List<MonetaryRateCardVersion>>() {
                }.getType()).getAsJsonArray();
        setMonetaryRateCardAsJson(modelJson.toString());
        setRateAndPulseUnits();
        return super.edit();
    }

    @Override
    public HttpHeaders update() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called update()");
        }
        try {
            RateCardData rateCardData = (RateCardData) getModel();
            if (Strings.isNullOrBlank(rateCardData.getId())) {
                addActionError(getModule().getDisplayLabel() + " Not Found with id: " + rateCardData.getId());
                setOldGroupsFromDB(Collectionz.newArrayList());
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }
            RateCardData rateCardFromDB = CRUDOperationUtil.get(RateCardData.class, rateCardData.getId());
            rateCardData.setGroups(rateCardData.getRncPackageData().getGroups());
            if (rateCardFromDB == null) {
                addActionError(getModule().getDisplayLabel() + " Not Found with id: " + rateCardData.getId());
                setOldGroupsFromDB(Collectionz.newArrayList());
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            JsonObject oldJsonObject = rateCardFromDB.toJson();

            String monetaryRateCardId = rateCardFromDB.getMonetaryRateCardData().getId();
            MonetaryRateCardData monetaryRateCardData = rateCardData.getMonetaryRateCardData();
            monetaryRateCardData.setId(monetaryRateCardId);
            setRateCardDataToRateCardVersionRelation(monetaryRateCardData);
            setRateCardVersionRelationToRateCardVersionDetail(monetaryRateCardData);
            rateCardData.setModifiedByStaff(getStaffData());

            if (Objects.isNull(rateCardData.getScope())) {
                rateCardData.setScope(RateCardScope.LOCAL.name());
            }
            if(Objects.isNull(rateCardData.getChargingType())) {
                rateCardData.setChargingType(rateCardData.getRncPackageData().getChargingType());
            }

            rateCardData = CRUDOperationUtil.merge(rateCardData);
            CRUDOperationUtil.flushSession();
            setModel(rateCardData);
            JsonObject newJsonObject = rateCardData.toJson();
            JsonArray diff = ObjectDiffer.diff(oldJsonObject, newJsonObject);

            String message = getModule().getDisplayLabel() + "  " + START_BOLD_TEXT_TAG + START_ITALIC_TEXT_TAG + rateCardData.getResourceName() + CLOSE_BOLD_TEXT_TAG + CLOSE_ITALIC_TEXT_TAG + " Updated";
            CRUDOperationUtil.audit(rateCardData, rateCardData.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), diff, rateCardData.getHierarchy(), message);
            addActionMessage(getModule().getDisplayLabel() + " updated successfully");
            setActionChainUrl(getRedirectToParentURL(rateCardData.getRncPkgId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        } catch (ConstraintViolationException cve) {
            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information. Reason: " + cve.getMessage());
            getLogger().trace(getLogModule(), cve);
            addActionError("Fail to perform update Operation.Reason: constraint " + cve.getConstraintName() + " violated");
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " information. Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Fail to perform Update Operation");
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }
    @Override
    public HttpHeaders show() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called create()");
        }

        RateCardData rateCardData = (RateCardData) getModel();
        RateCardData resourceInDB = CRUDOperationUtil.get((Class<RateCardData>) rateCardData.getClass(),
                rateCardData.getId(), getAdditionalCriteria());
        if (resourceInDB == null) {
            addActionError(getModule().getDisplayLabel()+" Not Found with id: " + rateCardData.getId());
            setModel(resourceInDB);
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
        MonetaryRateCardData monetaryRateCardDataFromDB = resourceInDB.getMonetaryRateCardData();


        Gson gson = new GsonBuilder().registerTypeAdapter(BigDecimal.class, new BigDecimalToStringGsonAdapter()).registerTypeAdapter(Long.class, new LongToStringGsonAdapter()).create();
        JsonArray modelJson = gson
                .toJsonTree(monetaryRateCardDataFromDB.getMonetaryRateCardVersions().get(0).getMonetaryRateCardVersionDetail(), new TypeToken<List<MonetaryRateCardVersionDetail>>() {
                }.getType()).getAsJsonArray();
        setMonetaryRateCardVersionsAsJson(modelJson.toString());
        return super.show();
    }

    @Override
    public boolean prepareAndValidateDestroy(RateCardData rateCardData) {
        if(PkgMode.LIVE.name().equalsIgnoreCase(rateCardData.getRncPackageData().getMode()) || PkgMode.LIVE2.name().equalsIgnoreCase(rateCardData.getRncPackageData().getMode())){
            addActionError("Monetary Rate Card Configured with Live or Live2 RnC package can not be deleted");
            return false;
        }
        List<RateCardGroupData> rateCardGroups = rateCardData.getRncPackageData().getRateCardGroupData();
        if (Collectionz.isNullOrEmpty(rateCardGroups) == false) {
            for(RateCardGroupData rateCardGroup : rateCardGroups){
                if(rateCardGroup.getPeakRateRateCard() != null && rateCardGroup.getPeakRateRateCard().getId().equals(rateCardData.getId())) {
                    addActionError("Monetary Rate Card is associated with Rate Card Group " + rateCardGroup.getName());
                    getLogger().error(getLogModule(), "Error while deleting monetary rate card" + rateCardData.getName() + ".Reason: rate card is associated with " + rateCardGroup.getName() + " Rate Card Group.");
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public HttpHeaders destroy(){
        super.destroy();
        setActionChainUrl(CommonConstants.FORWARD_SLASH + "pd/rncpackage/rnc-package/"
                + (getRequest().getParameter(NVSMXCommonConstants.RNC_PACKAGE_ID)));
        return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }




    protected void setRateCardVersionRelationToRateCardVersionDetail(MonetaryRateCardData monetaryRateCardData) {
        if (Collectionz.isNullOrEmpty(monetaryRateCardData.getMonetaryRateCardVersions()) == false) {
            for (MonetaryRateCardVersion rateCardVersionRelData : monetaryRateCardData.getMonetaryRateCardVersions()) {
                if (rateCardVersionRelData != null && Collectionz.isNullOrEmpty(rateCardVersionRelData.getMonetaryRateCardVersionDetail()) == false) {
                    rateCardVersionRelData.setEffectiveFromDate(new Timestamp(System.currentTimeMillis()));
                }
            }
        }

        for(MonetaryRateCardVersion monetaryRateCardVersion: monetaryRateCardData.getMonetaryRateCardVersions()){
            filterEmptyRows(monetaryRateCardVersion.getMonetaryRateCardVersionDetail());
            for(MonetaryRateCardVersionDetail monetaryRateCardVersionDetail: monetaryRateCardVersion.getMonetaryRateCardVersionDetail()){
                monetaryRateCardVersionDetail.setMonetaryRateCardVersion(monetaryRateCardVersion);
            }
        }

    }

    private void filterEmptyRows(List<MonetaryRateCardVersionDetail> dataRateCardVersionDetailDatas){
        Collectionz.filter(dataRateCardVersionDetailDatas, dataRateCardVersionDetailData -> {
            if(dataRateCardVersionDetailData == null){
                return false;
            }
            return !(Strings.isNullOrBlank(dataRateCardVersionDetailData.getLabel1())
                    && Strings.isNullOrBlank(dataRateCardVersionDetailData.getLabel2())
                    && (dataRateCardVersionDetailData.getPulse1() == null)
                    && (dataRateCardVersionDetailData.getRate1() == null));
        });
    }

    protected void setRateCardDataToRateCardVersionRelation(MonetaryRateCardData rateCardData) {
        rateCardData.getMonetaryRateCardVersions().forEach(rateCardDatas -> {
            rateCardDatas.setName("Default Version");
            rateCardDatas.setMonetaryRateCardData(rateCardData);
            });
    }

    public String getMonetaryRateCardAsJson() {
        return monetaryRateCardAsJson;
    }

    public void setMonetaryRateCardAsJson(String monetaryRateCardAsJson) {
        this.monetaryRateCardAsJson = monetaryRateCardAsJson;
    }

    @Override
    public void validate() {
        RateCardData rateCardData = (RateCardData) getModel();
        rateCardData.setType(RateCardType.MONETARY.name());
        rateCardData.getMonetaryRateCardData().setRateCardData(rateCardData);
        rateCardData.setNonMonetaryRateCardData(null);
        boolean isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(RateCardData.class,getMethodName(),rateCardData.getId(),rateCardData.getResourceName(),rateCardData.getRncPkgId(),"rncPackageData");
        if (isAlreadyExist) {
            addFieldError("name", getText("ratecard.duplicate.name"));
            return;
        }
        MonetaryRateCardData monetaryRateCardData = rateCardData.getMonetaryRateCardData();
        if(monetaryRateCardData == null){
            addFieldError("monetaryRateCardData",getText("error.valueRequired"));
            return;
        }

        if(Strings.isNullOrBlank(monetaryRateCardData.getPulseUnit()) == true || Strings.isNullOrBlank(monetaryRateCardData.getRateUnit()) == true){
            return;
        }

        validateRnCPackage(rateCardData);
        validateScope(rateCardData);

        validatePulseAndRateUnit(monetaryRateCardData);
        List<MonetaryRateCardVersion> monetaryRateCardVersions = monetaryRateCardData.getMonetaryRateCardVersions();
        if (Collectionz.isNullOrEmpty(monetaryRateCardVersions)) {
            addActionError(getText("ratecard.version.error"));
            return;

        }

        validateVersionDetail(monetaryRateCardData, monetaryRateCardVersions);
    }

    private void validateScope(RateCardData data) {
        if(Strings.isNullOrEmpty(data.getScope())) {
            data.setScope(RateCardScope.LOCAL.name());
        } else if(RateCardScope.LOCAL.name().equalsIgnoreCase(data.getScope()) == false) {
            addFieldError("scope",getText("invalid.ratecard.scope.local"));
        }
    }

    protected void validateVersionDetail(MonetaryRateCardData monetaryRateCardData, List<MonetaryRateCardVersion> monetaryRateCardVersions) {
        monetaryRateCardVersions.removeAll(Collections.singleton(null));
        boolean isRateCardVersionDetailNotPresent = isRateCardVersionDetailNotPresent(monetaryRateCardData);
        if (isRateCardVersionDetailNotPresent) {
            addActionError(getText("ratecard.version.detail.error"));
            return;
        }
        for (MonetaryRateCardVersion rateCardVersionRelation : monetaryRateCardVersions) {
            List<MonetaryRateCardVersionDetail> monetaryRateCardVersionDetail = rateCardVersionRelation.getMonetaryRateCardVersionDetail();
            for (MonetaryRateCardVersionDetail rateCardVersionDetail : monetaryRateCardVersionDetail) {
                if (rateCardVersionDetail == null) {
                    continue;
                }
                setErrorMessage(rateCardVersionDetail.getPulse1(), "pulse1");
                validateRate(rateCardVersionDetail.getRate1(), "rate1");
                validateDiscount(rateCardVersionDetail.getDiscount(), "discount");
                revenueDetailCheck(rateCardVersionDetail);
            }
        }

    }

    private void revenueDetailCheck(MonetaryRateCardVersionDetail rateCardVersionDetail) {
        if (Objects.nonNull(rateCardVersionDetail.getRevenueDetail())) {
            String revenueDetailId = rateCardVersionDetail.getRevenueDetail().getId();
            if (StringUtils.isEmpty(revenueDetailId)) {
                rateCardVersionDetail.setRevenueDetail(null);
            } else {
                validateRevenueDetail(revenueDetailId, "revenueDetail");
            }

        }
    }

    private void validateRevenueDetail(String revenueDetailId, String fieldName) {
        boolean validRevenueDetail = false;
        List<RevenueDetailData> revenueDetailDataList = CRUDOperationUtil.findAll(RevenueDetailData.class);
        if(Strings.isNullOrBlank(revenueDetailId) == false){
            for(RevenueDetailData revenueDetailData: revenueDetailDataList){
                if(revenueDetailData.getId().equalsIgnoreCase(revenueDetailId)){
                    validRevenueDetail = true;
                }
            }
            if(false == validRevenueDetail){
                addFieldError(fieldName,getText("ratecard.version.detail.revenuedetail"));
            }
        }
    }

    private void validateDiscount(Integer discount, String fieldName) {
        if(discount == null){
            return ;
        }
         if(discount < 0 || discount > 100){
            addFieldError(fieldName,getText("ratecard.version.detail.discount"));
         }

    }

    private void validatePulseAndRateUnit(MonetaryRateCardData monetaryRateCardData) {
        String pulseUnit = monetaryRateCardData.getPulseUnit();
        String rateUnit = monetaryRateCardData.getRateUnit();
        List<Uom> unitsOfSameType = Uom.getUomsOfSameTypeFromName(pulseUnit);
        if(Uom.PERPULSE.name().equals(rateUnit) || pulseUnit.equals(ChargingType.EVENT.name())){
            return;
        }
        if(Collectionz.isNullOrEmpty(unitsOfSameType) == false && unitsOfSameType.contains(Uom.valueOf(rateUnit)) == false) {
            addFieldError("rateUnit", "Pulse Unit is of type " + Uom.valueOf(pulseUnit).getUnitType() + " And Rate Unit is of type " + Uom.valueOf(rateUnit).getUnitType() + ". They must be of same type");
        }
    }

    private void validateRnCPackage(RateCardData rateCardData) {
        if(Strings.isNullOrEmpty(rateCardData.getRncPkgId())){
            addFieldError("rncPackageId",getText("error.valueRequired"));
        }else {
            RncPackageData rncPackageData = CRUDOperationUtil.get(RncPackageData.class, rateCardData.getRncPkgId());
            if (rncPackageData == null) {
                addFieldError("rncPackageId", "RnC Package does not exists");
            } else if (PkgMode.LIVE.name().equalsIgnoreCase(rncPackageData.getMode()) || PkgMode.LIVE2.name().equalsIgnoreCase(rncPackageData.getMode())) {
                addActionError("Live or Live2 Package does not allow create / update operation on Monetary Rate Card");
            } else if(RnCPkgType.NON_MONETARY_ADDON.name().equals(rncPackageData.getType())){
                addActionError("RnC package of type "+RnCPkgType.NON_MONETARY_ADDON.getVal()+" does not support monetary rate card");
            }else if(rncPackageData.getChargingType().equals(ChargingType.EVENT.name()) && (rateCardData.getMonetaryRateCardData().getPulseUnit().equals(Uom.EVENT.name()) == false
                        || rateCardData.getMonetaryRateCardData().getRateUnit().equals(Uom.EVENT.name()) == false)){
                    addActionError("RnC package of charging type is EVENT then Monetary pulse unit and rate unit must be EVENT");
            }
            rateCardData.setRncPackageData(rncPackageData);
        }
    }

    private boolean isRateCardVersionDetailNotPresent(MonetaryRateCardData monetaryRateCardData) {
        for (MonetaryRateCardVersion rateCardVersionRelation : monetaryRateCardData.getMonetaryRateCardVersions()) {
            if (Collectionz.isNullOrEmpty(rateCardVersionRelation.getMonetaryRateCardVersionDetail())) {
                return true;
            }
        }
        return false;
    }


    public void setErrorMessage(String fieldValue, String fieldName) {

        if (Strings.isNullOrEmpty(fieldValue)) {
            addFieldError(fieldName, fieldName +" "+ getText("ratecard.version.value.required"));
        }
    }

    public void validateRate(BigDecimal fieldValue, String fieldName) {
        if (fieldValue == null) {
            addFieldError(fieldName, fieldName + " "+getText("ratecard.version.value.required"));
        } else {
            if (NumberUtils.isNumber(String.valueOf(fieldValue)) == false) {
                addFieldError(fieldName, fieldName +" "+ getText("ratecard.version.double.length"));
            } else if(fieldValue.compareTo(BigDecimal.ZERO) < 0){
                addFieldError(fieldName,getText("message.mustBeGreaterThanZero"));
            }
            else if (Pattern.matches(REGEX_PATTERN,fieldValue.toPlainString()) == false) {
                addFieldError(fieldName, fieldName +" "+  getText("ratecard.version.double.length"));
            }
        }
    }

    public void setErrorMessage(Long fieldValue, String fieldName) {
        if (fieldValue == null) {
            addFieldError(fieldName, fieldName + " " + getText("ratecard.version.value.required"));
        } else {
            if (fieldValue < 1 || fieldValue > NVSMXCommonConstants.LONG_MAX_VALUE) {
                addFieldError(fieldName, "Value must be between 1 to " + NVSMXCommonConstants.LONG_MAX_VALUE);
            }
        }
    }

    public List<Uom> getPulseUnits() {
        return pulseUnits;
    }

    public void setPulseUnits(List<Uom> pulseUnits) {
        this.pulseUnits = pulseUnits;
    }

    public String getMonetaryRateCardVersionsAsJson() {
        return monetaryRateCardVersionsAsJson;
    }

    public void setMonetaryRateCardVersionsAsJson(String monetaryRateCardVersionsAsJson) {
        this.monetaryRateCardVersionsAsJson = monetaryRateCardVersionsAsJson;
    }

    @SkipValidation
    public HttpHeaders bulkUpdateMonetaryRate(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called saveVersionConfiguration()");
        }
        getLogger().debug(getLogModule(),"rateUnitOperation = "  + rateUnitOperation);

        try {
            RateCardData rateCardData = (RateCardData) getModel();
            RateCardData resourceInDB = CRUDOperationUtil.get((Class<RateCardData>) rateCardData.getClass(),rateCardData.getId());
            if(Strings.isNullOrBlank(resourceInDB.getId())){
                getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ resourceInDB.getId()+". Reason: Not found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);

            }

            if(Arrayz.isNullOrEmpty(getIds())) {
                getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " with id: Reason: Version Configuration not selected");
                addActionError(getModule().getDisplayLabel()+" records not selected");
                setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(resourceInDB.getId()));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INPUT_PARAMETER_MISSING.code).disableCaching();
            }

            MonetaryRateCardData monetaryRateCardData = resourceInDB.getMonetaryRateCardData();
            List<String> bulkUpdateCheckList = Arrays.asList(getIds());

            for(MonetaryRateCardVersion monetaryRateCardVersion: monetaryRateCardData.getMonetaryRateCardVersions()){
                filterEmptyRows(monetaryRateCardVersion.getMonetaryRateCardVersionDetail());
                for(MonetaryRateCardVersionDetail monetaryRateCardVersionDetail: monetaryRateCardVersion.getMonetaryRateCardVersionDetail()){
                    if(bulkUpdateCheckList.contains(monetaryRateCardVersionDetail.getId())){    // Set Rate
                        BigDecimal rate1 = monetaryRateCardVersionDetail.getRate1();
                        BigDecimal newRate;
                        if(rateUnitOperation == OperationType.ABSOLUTE){ //Absolute
                            newRate = rate1.add(getBulkUpdateRate());
                        } else {    //Percentage
                            newRate = rate1.add( (rate1.multiply(getBulkUpdateRate()).divide( BigDecimal.valueOf(100) )));
                        }

                        if(newRate.compareTo(new BigDecimal(0)) < 0){
                            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " with id: Reason: Rate values going to be in minus");
                            addActionError("Fail to perform Update Operation");
                            addActionError("Reason: "+getModule().getDisplayLabel()+" updated Rate value should be positive");
                            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(resourceInDB.getId()));
                            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
                        }

                        if(newRate.doubleValue() > CommonConstants.MONETARY_VALUE_LIMIT ) {
                            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " with id: Reason: Rate values exceeding maximum limit "+CommonConstants.MAX_MONETARY_VALUE);
                            addActionError("Fail to perform Update Operation");
                            addActionError("Reason: "+getModule().getDisplayLabel()+" updated Rate value should not be greater than "+ CommonConstants.MAX_MONETARY_VALUE);
                            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(resourceInDB.getId()));
                            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
                        }

                        monetaryRateCardVersionDetail.setRate1(newRate);
                    }
                    monetaryRateCardVersionDetail.setMonetaryRateCardVersion(monetaryRateCardVersion);
                }
            }
            resourceInDB.setMonetaryRateCardData(monetaryRateCardData);
            resourceInDB.setModifiedDateAndStaff(getStaffData());
            CRUDOperationUtil.merge(resourceInDB);
            resourceInDB.setGroupNames(GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(resourceInDB.getGroups())));
            String message = getModule().getDisplayLabel() + " <b><i>" + resourceInDB.getResourceName() + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(resourceInDB,resourceInDB.getResourceName(), AuditActions.UPDATE,getStaffData(),getRequest().getRemoteAddr(),resourceInDB.getHierarchy(),message);
            addActionMessage(getModule().getDisplayLabel()+" updated successfully");
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(resourceInDB.getId()));
            CRUDOperationUtil.flushSession();
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform Update Operation "+e.getMessage());
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    public OperationType getRateUnitOperation() {
        return rateUnitOperation;
    }

    public void setRateUnitOperation(OperationType rateUnitOperation) {
        this.rateUnitOperation = rateUnitOperation;
    }

    public BigDecimal getBulkUpdateRate() {
        return bulkUpdateRate;
    }

    public void setBulkUpdateRate(BigDecimal bulkUpdateRate) {
        this.bulkUpdateRate = bulkUpdateRate;
    }

    public String getChargingType() {
        return chargingType;
    }

    public void setChargingType(String chargingType) {
        this.chargingType = chargingType;
    }

    public List<RevenueDetailData> getRevenueDetails() {
        return revenueDetails;
    }

    public void setRevenueDetails(List<RevenueDetailData> revenueDetails) {
        this.revenueDetails = revenueDetails;
    }
}



