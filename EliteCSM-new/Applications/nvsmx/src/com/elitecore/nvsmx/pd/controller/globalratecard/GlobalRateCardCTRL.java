package com.elitecore.nvsmx.pd.controller.globalratecard;

import java.util.List;
import java.util.Objects;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.HttpMethod;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardScope;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.pd.controller.monetaryratecard.MonetaryRateCardCTRL;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.struts2.ServletActionContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates.createStaffBelongingPredicate;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_BOLD_TEXT_TAG;
import static com.elitecore.nvsmx.system.constants.NVSMXCommonConstants.START_ITALIC_TEXT_TAG;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "pd")
@Namespace("/pd/globalratecard")
@Results({
        @Result(name = SUCCESS, type = "redirectAction", params = {NVSMXCommonConstants.ACTION_NAME, "global-rate-card"}),
})
public class GlobalRateCardCTRL extends MonetaryRateCardCTRL {

    public static final String SCOPE = "scope";
    public static final String GLOBAL = "GLOBAL";
    public static final String INDEX_METHOD = "";

    private List<Uom> pulseUnits = Collectionz.newArrayList();
    private String monetaryRateCardListAsjson;

    private boolean isCurrencyUpdateAllowed;

    @Override
    public ACLModules getModule() {
        return ACLModules.GLOBALRATECARD;
    }

    @Override
    public String editNew() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called editNew()");
        }
        setRateAndPulseUnitsRateCard();
        setSystemCurrency();
        return super.editNew();
    }
    private List<RevenueDetailData> revenueDetails;

    @SkipValidation
    @Override
    public void prepareValuesForSubClass() throws Exception {
        List<RevenueDetailData> revenueDetailDataList = CRUDOperationUtil.findAll(RevenueDetailData.class);
        setRevenueDetails(revenueDetailDataList);
    }

    private void setRateAndPulseUnitsRateCard() {
        pulseUnits.addAll(Uom.getTimeUoms());
    }

    private void setSystemCurrency(){
        RateCardData data = (RateCardData) getModel();
        data.setCurrency(SystemParameterDAO.getCurrency());
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Setting system currency " + SystemParameterDAO.getCurrency() +" as default.");
        }
    }

    @Override
    public HttpHeaders bulkUpdateMonetaryRate() {
        HttpHeaders headers = super.bulkUpdateMonetaryRate();
        if (headers.getResultCode().equals(ERROR)) {
            return headers;
        }
        RateCardData data = (RateCardData) getModel();
        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(data.getId()));
        return headers;
    }

    @Override
    public String edit() {
        String result = super.edit();
        setRateAndPulseUnitsRateCard();
        RateCardData data = (RateCardData) getModel();
        setCurrencyUpdateAllowed(checkRatecardCurrencyUpdate(data));
        return result;
    }

    private boolean checkRatecardCurrencyUpdate(RateCardData data){
        boolean isRateCurrencyUpdateAllowed = false;
        if(SystemParameterDAO.isMultiCurrencyEnable()){
            isRateCurrencyUpdateAllowed = true;
            Criterion peakRateFilterCriteria = Restrictions.eq("peakRateRateCard",data);
            List<RateCardGroupData> peakRateCardGroupDataList = CRUDOperationUtil.findAll(RateCardGroupData.class, peakRateFilterCriteria);
            if(Collectionz.isNullOrEmpty(peakRateCardGroupDataList)){
                Criterion offPeakRateFilterCriteria = Restrictions.eq("offPeakRateRateCard", data);
                List<RateCardGroupData> offPeakRateCardGroupDataList = CRUDOperationUtil.findAll(RateCardGroupData.class, offPeakRateFilterCriteria);
                if(Collectionz.isNullOrEmpty(offPeakRateCardGroupDataList)){
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(getLogModule(), "Currency update is allowed as " + data.getName() + " is not associated with any packages.");
                    }
                }else{
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(getLogModule(), "Currency update is not allowed. " + data.getName() + " is already associated as offpeak ratecard in Rnc package.");
                    }
                    isRateCurrencyUpdateAllowed = false;
                }
            }else{
                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(getLogModule(), "Currency update is not allowed. " + data.getName() + " is already associated as peak ratecard in Rnc package.");
                }
                isRateCurrencyUpdateAllowed = false;
            }
        }

        return isRateCurrencyUpdateAllowed;
    }


    @Override
    public HttpHeaders index() {
        Gson gson = GsonFactory.defaultInstance();
        List<RateCardData> rateCardDataList;

        Criterion criterion = Restrictions.eq(SCOPE, GLOBAL);
        rateCardDataList = CRUDOperationUtil.findAll(RateCardData.class, criterion);
        Collectionz.filter(rateCardDataList, createStaffBelongingPredicate(getStaffBelongingGroups()));
        setMonetaryRateCardListAsjson(gson.toJsonTree(rateCardDataList).getAsJsonArray().toString());
        setList(rateCardDataList);
        setActionChainUrl(getRedirectURL(METHOD_INDEX));

        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }

    public void setMonetaryRateCardListAsjson(String monetaryRateCardListAsjson) {
        this.monetaryRateCardListAsjson = monetaryRateCardListAsjson;
    }

    public String getMonetaryRateCardListAsjson() {
        return monetaryRateCardListAsjson;
    }

    @Override
    public void validate() {
        validateName();
        validateRateCardType();
        validatePulseAndRateUnit();
        validateScope();
        validateChargingType();
        boolean bValidCurrency = validateCurrency();

        RateCardData rateCardData = (RateCardData) getModel();

        HttpServletRequest request = ServletActionContext.getRequest();

        if (request.getMethod().equalsIgnoreCase(HttpMethod.PUT)) {
            RateCardData rateCardFromDB = CRUDOperationUtil.get(RateCardData.class, rateCardData.getId());
            if (rateCardFromDB.getChargingType().equalsIgnoreCase(rateCardData.getChargingType()) == false) {
                addActionError(getText("error.ratecard.modify.chargingtype"));
                return;
            }
            if (bValidCurrency &&  rateCardFromDB.getCurrency().equals(rateCardData.getCurrency()) == false) {
                boolean bCurrencyUpdateAllowed = checkRatecardCurrencyUpdate(rateCardFromDB);
                if (!bCurrencyUpdateAllowed) {
                    addActionError(getText("error.ratecard.modify.currency"));
                    return;
                }
            }

        }
        List<MonetaryRateCardVersion> monetaryRateCardVersions = ((RateCardData) getModel()).getMonetaryRateCardData().getMonetaryRateCardVersions();
        if (Collectionz.isNullOrEmpty(monetaryRateCardVersions)) {
            addActionError(getText("ratecard.version.error"));
            return;
        }
        validateVersionDetail(rateCardData.getMonetaryRateCardData(), rateCardData.getMonetaryRateCardData().getMonetaryRateCardVersions());
        rateCardData.getMonetaryRateCardData().setRateCardData(rateCardData);
        rateCardData.setNonMonetaryRateCardData(null);
    }

    private void validateName() {
        RateCardData data = (RateCardData) getModel();
        if (isDuplicateEntity("name", data.getResourceName(), getMethodName())) {
            addFieldError("name", "Name already exists");
        }
        validateIdExistForCreateMode();
    }

    private void validateRateCardType() {
        RateCardData data = (RateCardData) getModel();
        if(data.getType().equalsIgnoreCase(RateCardType.MONETARY.name()) == false) {
            addFieldError("type",getText("ratecard.monetary.configure"));
        }
    }

    private void validatePulseAndRateUnit() {
        RateCardData data = (RateCardData) getModel();
        if(Objects.isNull(data.getMonetaryRateCardData().getPulseUnit()) ) {
            addFieldError("monetaryRateCardData.pulseUnit",getText("error.ratecard.pulseunit.required"));
        }
        if(Objects.isNull(data.getMonetaryRateCardData().getRateUnit()) ) {
            addFieldError("monetaryRateCardData.rateUnit",getText("error.ratecard.rateunit.required"));
        }
        if(Objects.isNull(data.getMonetaryRateCardData().getRateUnit()) == false && Objects.isNull(data.getMonetaryRateCardData().getPulseUnit()) == false
                && ChargingType.EVENT.name().equalsIgnoreCase(data.getChargingType()) ) {
            if(ChargingType.EVENT.name().equalsIgnoreCase(data.getMonetaryRateCardData().getPulseUnit()) == false) {
                addFieldError("monetaryRateCardData.pulseUnit",getText("invalid.ratecard.pulseunit.eventonly"));
            }
            if(ChargingType.EVENT.name().equalsIgnoreCase(data.getMonetaryRateCardData().getRateUnit()) == false) {
                addFieldError("monetaryRateCardData.rateUnit",getText("invalid.ratecard.rateunit.eventonly"));
            }
        }

        if(Objects.isNull(data.getMonetaryRateCardData().getRateUnit()) == false && Objects.isNull(data.getMonetaryRateCardData().getPulseUnit()) == false
                && ChargingType.EVENT.name().equalsIgnoreCase(data.getChargingType()) == false) {
            if( (data.getMonetaryRateCardData().getPulseUnit().equalsIgnoreCase(Uom.SECOND.name())
                    || data.getMonetaryRateCardData().getPulseUnit().equalsIgnoreCase(Uom.MINUTE.name())
                    || data.getMonetaryRateCardData().getPulseUnit().equalsIgnoreCase(Uom.HOUR.name())) == false) {
                addFieldError("monetaryRateCardData.pulseUnit",getText("invalid.ratecard.pulseunit.sessiononly"));
            }
            if( (data.getMonetaryRateCardData().getRateUnit().equalsIgnoreCase(Uom.SECOND.name())
                    || data.getMonetaryRateCardData().getRateUnit().equalsIgnoreCase(Uom.MINUTE.name())
                    || data.getMonetaryRateCardData().getRateUnit().equalsIgnoreCase(Uom.HOUR.name())
                    || data.getMonetaryRateCardData().getRateUnit().equalsIgnoreCase(Uom.PERPULSE.name())) == false) {
                addFieldError("monetaryRateCardData.rateUnit", getText("invalid.ratecard.rateunit.sessiononly"));
            }
        }
    }

    private void validateScope() {
        RateCardData data = (RateCardData) getModel();
        if (Strings.isNullOrEmpty(data.getScope())) {
            data.setScope(RateCardScope.GLOBAL.name());
        } else if (RateCardScope.GLOBAL.name().equalsIgnoreCase(data.getScope()) == false) {
            addFieldError("scope", getText("invalid.ratecard.scope.global"));
        }
    }

    private void validateChargingType() {
        RateCardData data = (RateCardData) getModel();
        if ((ChargingType.SESSION.name().equalsIgnoreCase(data.getChargingType()) || ChargingType.EVENT.name().equalsIgnoreCase(data.getChargingType())) == false) {
            addFieldError("chargingType", getText("invalid.ratecard.chargingtype.sessionorevent"));
        }
    }

    private boolean validateCurrency(){
        boolean bValid = true;
        RateCardData data = (RateCardData) getModel();
        if(Objects.isNull(data.getCurrency()) ) {
            addFieldError("ratecard.currency",getText("error.ratecard.currency.required"));
            bValid = false;
        }else {
            if (SystemParameterDAO.isMultiCurrencyEnable() == false) {
                if (SystemParameterDAO.getCurrency().equals(data.getCurrency()) == false) {
                    addFieldError("ratecard.currency", getText("invalid.ratecard.currency.systemonly"));
                    bValid = false;
                }
            } else {
                if (SystemParameter.CURRENCY.validate(data.getCurrency()) == false) {
                    addFieldError("ratecard.currency", getText("invalid.ratecard.currency.value"));
                    bValid = false;
                }
            }
        }
        return bValid;
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
            rateCardData.setGroups(rateCardData.getGroups());
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
            rateCardFromDB.setGroupNames(GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(rateCardFromDB.getGroups())));
            JsonObject oldJsonObject = rateCardFromDB.toJson();

            String monetaryRateCardId = rateCardFromDB.getMonetaryRateCardData().getId();
            MonetaryRateCardData monetaryRateCardData = rateCardData.getMonetaryRateCardData();
            monetaryRateCardData.setId(monetaryRateCardId);
            setRateCardDataToRateCardVersionRelation(monetaryRateCardData);
            setRateCardVersionRelationToRateCardVersionDetail(monetaryRateCardData);
            rateCardData.setModifiedByStaff(getStaffData());

            if (Objects.isNull(rateCardData.getChargingType())) {
                rateCardData.setChargingType(rateCardData.getRncPackageData().getChargingType());
            }
            String groups = rateCardData.getGroupNames();
            rateCardData = CRUDOperationUtil.merge(rateCardData);
            rateCardData.setGroupNames(groups);
            CRUDOperationUtil.flushSession();
            setModel(rateCardData);
            JsonObject newJsonObject = rateCardData.toJson();
            JsonArray diff = ObjectDiffer.diff(oldJsonObject, newJsonObject);

            String message = getModule().getDisplayLabel() + "  " + START_BOLD_TEXT_TAG + START_ITALIC_TEXT_TAG + rateCardData.getResourceName() + CLOSE_BOLD_TEXT_TAG + CLOSE_ITALIC_TEXT_TAG + " Updated";
            CRUDOperationUtil.audit(rateCardData, rateCardData.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), diff, rateCardData.getHierarchy(), message);
            addActionMessage(getModule().getDisplayLabel() + " updated successfully");
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(INDEX_METHOD));
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
    public HttpHeaders create() {
        RateCardData data = (RateCardData) getModel();

        MonetaryRateCardData monetaryRateCard = data.getMonetaryRateCardData();
        setRateCardDataToRateCardVersionRelation(monetaryRateCard);
        setRateCardVersionRelationToRateCardVersionDetail(monetaryRateCard);
        HttpHeaders headers = super.create();
        if (hasActionErrors()) {
            if (ChargingType.SESSION.name().equalsIgnoreCase(data.getChargingType())) {
                setRateAndPulseUnitsRateCard();
            }
            pulseUnits.add(Uom.EVENT);
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
        }
        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(INDEX_METHOD));
        return headers;
    }

    @Override
    public boolean prepareAndValidateDestroy(RateCardData rateCardData) {
        List<RateCardGroupData> list = CRUDOperationUtil.findAll(RateCardGroupData.class, Restrictions.or(Restrictions.eq("offPeakRateRateCard.id", rateCardData.getId()),
                Restrictions.eq("peakRateRateCard.id", rateCardData.getId())));
        if (list.isEmpty() == false) {
            addActionError("Global Monetary Rate Card " + rateCardData.getName() + " is associated with Rate Card Group");
            return false;
        }
        return true;
    }

    @Override
    public HttpHeaders destroy() {
        super.destroy();
        setActionChainUrl(getRedirectURL(METHOD_INDEX));
        return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);
    }

    private List<String> getStaffBelongingGroups() {
        String groups = (String) getRequest().getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
        if (groups == null) {
            groups = "";
        }
        return CommonConstants.COMMA_SPLITTER.split(groups);
    }

    @Override
    public List<Uom> getPulseUnits() {
        return pulseUnits;
    }

    @Override
    public void setPulseUnits(List<Uom> pulseUnits) {
        this.pulseUnits = pulseUnits;
    }


    public boolean getCurrencyUpdateAllowed(){
        return isCurrencyUpdateAllowed;
    }

    public void setCurrencyUpdateAllowed(boolean isCurrencyUpdateAllowed) {
        this.isCurrencyUpdateAllowed = isCurrencyUpdateAllowed;
    }

    @Override
    public List<RevenueDetailData> getRevenueDetails() {
        return revenueDetails;
    }

    @Override
    public void setRevenueDetails(List<RevenueDetailData> revenueDetails) {
        this.revenueDetails = revenueDetails;
    }
}
