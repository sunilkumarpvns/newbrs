package com.elitecore.nvsmx.pd.controller.nonmonetaryratecard;


import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.pd.ratecard.NonMonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardScope;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * manage Nonmonetary RateCard related information.
 * Created by ishani.
 */

@ParentPackage(value = "pd")
@Namespace("/pd/nonmonetaryratecard")
@Results({@Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = {NVSMXCommonConstants.ACTION_NAME,
        "non-monetary-rate-card"}),

})
public class NonMonetaryRateCardCTRL extends RestGenericCTRL<RateCardData> {

    private static final long serialVersionUID = -6882470730134447721L;

    private String nonMonetaryRateCardAsJson;
    private String rncPackageId;
    private String chargingType;
    private List<Uom> pulseUnits = Collectionz.newArrayList();

    @Override
    public ACLModules getModule() {
        return ACLModules.NONMONETARYRATECARD;
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
            setPulseUnits();
            setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
            return NVSMXCommonConstants.REDIRECT_URL;
        }catch (Exception e) {
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform create Operation");
            return ERROR;
        }

    }

    private void setPulseUnits() {
        pulseUnits.addAll(Uom.getTimeUoms().stream().filter(uom -> uom.name().equals(Uom.HOUR.name())==false).collect(Collectors.toList()));
    }

    @Override
    public HttpHeaders create() {
        RateCardData rateCardData = (RateCardData) getModel();
        rateCardData.setCurrency(rateCardData.getRncPackageData().getCurrency());
        rateCardData.setGroups(rateCardData.getRncPackageData().getGroups());
        if (Objects.isNull(rateCardData.getScope())) {
            rateCardData.setScope(RateCardScope.LOCAL.name());
        }
        if(Objects.isNull(rateCardData.getChargingType())) {
            rateCardData.setChargingType(rateCardData.getRncPackageData().getChargingType());
        }
        HttpHeaders headers = super.create();
        if(hasActionErrors()){
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
        }
        setActionChainUrl(getRedirectToParentURL(rateCardData.getRncPkgId()));
        return headers;
    }

    @SuppressWarnings("unchecked")
    @Override
    public String edit() {
        RateCardData rateCardData = (RateCardData) getModel();
        RateCardData resourceInDB = CRUDOperationUtil.get((Class<RateCardData>) rateCardData.getClass(),
                rateCardData.getId(), getAdditionalCriteria());
        RncPackageData rncPackageData = CRUDOperationUtil.get(RncPackageData.class, resourceInDB.getRncPkgId());
        resourceInDB.setGroups(rncPackageData.getGroups());
        resourceInDB.setChargingType(rncPackageData.getChargingType());
        resourceInDB.setCurrency(rncPackageData.getCurrency());
        setPulseUnits();
        setModel(resourceInDB);
        return super.edit();
    }

    @Override
    public HttpHeaders update() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called update()");
        }
        RateCardData rateCardData = (RateCardData) getModel();
        if (Strings.isNullOrBlank(rateCardData.getId()) == false) {
            RateCardData rateCardFromDB = CRUDOperationUtil.get(RateCardData.class, rateCardData.getId());
            if (rateCardFromDB == null) {
                addActionError(getModule().getDisplayLabel() + " Not Found with id: " + rateCardData.getId());
                setOldGroupsFromDB(Collectionz.newArrayList());
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
            String nonMonetaryRateCardId = rateCardFromDB.getNonMonetaryRateCardData().getId();
            rateCardData.setGroups(rateCardFromDB.getGroups());

            if (Objects.isNull(rateCardData.getScope())) {
                rateCardData.setScope(RateCardScope.LOCAL.name());
            }
            if(Objects.isNull(rateCardData.getChargingType())) {
                rateCardData.setChargingType(rateCardData.getRncPackageData().getChargingType());
            }

            rateCardData.setCurrency(rateCardData.getRncPackageData().getCurrency());

            NonMonetaryRateCardData nonMonetaryRateCardData = rateCardData.getNonMonetaryRateCardData();
            nonMonetaryRateCardData.setId(nonMonetaryRateCardId);
        }

        HttpHeaders headers = super.update();
        if(hasActionErrors()){
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
        }
        setActionChainUrl(getRedirectToParentURL(rateCardData.getRncPkgId()));
        return headers;
    }

    @Override
    public boolean prepareAndValidateDestroy(RateCardData rateCardData) {
        if(PkgMode.LIVE.name().equalsIgnoreCase(rateCardData.getRncPackageData().getMode()) || PkgMode.LIVE2.name().equalsIgnoreCase(rateCardData.getRncPackageData().getMode())){
            addActionError("Non Monetary Rate Card Configured with Live or Live2 RnC package can not be deleted");
            return false;
        }
        List<RateCardGroupData> rateCardGroups = rateCardData.getRncPackageData().getRateCardGroupData();
        if (Collectionz.isNullOrEmpty(rateCardGroups) == false) {
            for(RateCardGroupData rateCardGroup : rateCardGroups){
                if(rateCardGroup.getPeakRateRateCard() != null && rateCardGroup.getPeakRateRateCard().getId().equals(rateCardData.getId())) {
                    addActionError("Non-Monetary Peak Rate Card is associated with Rate Card Group " + rateCardGroup.getName());
                    getLogger().error(getLogModule(), "Error while deleting non-monetary rate card" + rateCardData.getName() + ".Reason: rate card is associated with " + rateCardGroup.getName() + " Rate Card Group.");
                    return false;
                } else if(rateCardGroup.getOffPeakRateRateCard() != null && rateCardGroup.getOffPeakRateRateCard().getId().equals(rateCardData.getId())) {
                    addActionError("Non-Monetary Off Peak Rate Card is associated with Rate Card Group " + rateCardGroup.getName());
                    getLogger().error(getLogModule(), "Error while deleting non-monetary rate card" + rateCardData.getName() + ".Reason: rate card is associated with " + rateCardGroup.getName() + " Rate Card Group.");
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

    @Override
    public HttpHeaders show(){
        RateCardData model = (RateCardData) getModel();
        RateCardData rateCardData = CRUDOperationUtil.get(RateCardData .class, model.getId());
        if (rateCardData == null) {
            addActionError(getModule().getDisplayLabel() + " Not Found with id: " + model.getId());
            setModel(rateCardData);
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
        RncPackageData rncPackageData = CRUDOperationUtil.get(RncPackageData.class, rateCardData.getRncPkgId());
        setChargingType(rncPackageData.getChargingType());
        return super.show();
    }

    @Override
    public void validate() {
        RateCardData rateCardData = (RateCardData) getModel();
        RncPackageData rncPackageData = CRUDOperationUtil.get(RncPackageData.class, rateCardData.getRncPkgId());
        setChargingType(rncPackageData.getChargingType());
        rateCardData.setType(RateCardType.NON_MONETARY.name());
        rateCardData.getNonMonetaryRateCardData().setRateCardData(rateCardData);
        rateCardData.setMonetaryRateCardData(null);
        boolean isAlreadyExist = CRUDOperationUtil.isDuplicateNameWithInParent(RateCardData.class,getMethodName(),rateCardData.getId(),rateCardData.getResourceName(),rateCardData.getRncPkgId(),"rncPackageData");
        if (isAlreadyExist) {
            addFieldError("name", getText("nonmonetary.ratecard.duplicate.name"));
            return;
        }

        NonMonetaryRateCardData nonMonetaryRateCardData = rateCardData.getNonMonetaryRateCardData();
        if(nonMonetaryRateCardData == null){
            addFieldError("nonMonetaryRateCardData",getText("error.valueRequired"));
            return;
        }

        if(Strings.isNullOrBlank(nonMonetaryRateCardData.getPulseUom())){
            return;
        }

        validateRnCPackage(rateCardData);

        if(getChargingType().equals(ChargingType.SESSION.name())){
            validateChargingTime(nonMonetaryRateCardData);
            nonMonetaryRateCardData.setEvent(null);
        } else {
            validateChargingEvent(nonMonetaryRateCardData);
            nonMonetaryRateCardData.setTime(null);
        }

        validateRenewalInterval(nonMonetaryRateCardData);
    }

    private void validateRenewalInterval(NonMonetaryRateCardData nonMonetaryRateCardData) {
        if(RenewalIntervalUnit.TILL_BILL_DATE.name().equals(nonMonetaryRateCardData.getRenewalIntervalUnit())){
            nonMonetaryRateCardData.setRenewalInterval(null);
        }else{
           if(nonMonetaryRateCardData.getRenewalInterval() != null) {
               if(nonMonetaryRateCardData.getRenewalInterval() <=0 || nonMonetaryRateCardData.getRenewalInterval() > NVSMXCommonConstants.RENEWAL_INTERVAL_MAX_VAL){
                   addFieldError("renewalInterval",getText("nonmonetary.ratecard.renewal.interval.range"));
               }
           }
        }
        if(RenewalIntervalUnit.TILL_BILL_DATE.name().equals(nonMonetaryRateCardData.getRenewalIntervalUnit()) == false &&
                RenewalIntervalUnit.MONTH.name().equals(nonMonetaryRateCardData.getRenewalIntervalUnit())  == false &&
                        RenewalIntervalUnit.MONTH_END.name().equals(nonMonetaryRateCardData.getRenewalIntervalUnit()) == false){
            nonMonetaryRateCardData.setProration(CommonStatusValues.DISABLE.isBooleanValue());
        }
    }

    private void validateChargingTime(NonMonetaryRateCardData nonMonetaryRateCardData) {
        if(nonMonetaryRateCardData.getPulse() == null) {
            addFieldError("nonMonetaryRateCardData", getText("error.required.field", new String[]{getText("nonmonetary.ratecard.pulse")}));
        } else if(Uom.SECOND.name().equals(nonMonetaryRateCardData.getPulseUom()) == false
                && Uom.MINUTE.name().equals(nonMonetaryRateCardData.getPulseUom()) == false){
            addFieldError(getText("nonmonetary.ratecard.pulseUom"), getText("invalid.field.value"));
        }
        if(nonMonetaryRateCardData.getTime() != null) {

            if (nonMonetaryRateCardData.getTime() < 0) {
                addFieldError("nonMonetaryRateCardData", getText("nonmonetary.ratecard.invalid.time"));
                return;
            }
            if (nonMonetaryRateCardData.getTime() > 999999999999999999L) {
                addFieldError("nonMonetaryRateCardData", getText("nonmonetary.ratecard.invalid.time.max"));
                return;
            }
        }

        if (nonMonetaryRateCardData.getTimeUom() == null){
            addFieldError(getText("nonmonetary.ratecard.timeUom"), getText("error.valueRequired"));
        } else if(Uom.SECOND.name().equals(nonMonetaryRateCardData.getTimeUom()) == false
                && Uom.MINUTE.name().equals(nonMonetaryRateCardData.getTimeUom()) == false
                && Uom.HOUR.name().equals(nonMonetaryRateCardData.getTimeUom()) == false){
            addFieldError(getText("nonmonetary.ratecard.timeUom"), getText("invalid.field.value"));
        }
    }

    private void validateChargingEvent(NonMonetaryRateCardData nonMonetaryRateCardData){
        nonMonetaryRateCardData.setPulse(1L);
        if(nonMonetaryRateCardData.getEvent() == null){
            addFieldError(getText("nonmonetary.ratecard.event") ,getText("error.valueRequired"));
        }else {
            if (nonMonetaryRateCardData.getEvent() < 0) {
                addFieldError("nonMonetaryRateCardData", getText("nonmonetary.ratecard.invalid.event"));
                return;
            }
            if (nonMonetaryRateCardData.getEvent() > 999999999999999999L) {
                addFieldError("nonMonetaryRateCardData", getText("nonmonetary.ratecard.invalid.event.max"));
                return;
            }
        }
    }


    private void validateRnCPackage(RateCardData rateCardData) {
        if(com.elitecore.commons.base.Strings.isNullOrEmpty(rateCardData.getRncPkgId())){
            addFieldError("rncPackageId",getText("error.valueRequired"));
        }else{
            RncPackageData rncPackageData = CRUDOperationUtil.get(RncPackageData.class,rateCardData.getRncPkgId());
            if(rncPackageData == null){
                addFieldError("rncPackageId","RnC Package does not exists");
            }else if(PkgMode.LIVE.name().equalsIgnoreCase(rncPackageData.getMode()) || PkgMode.LIVE2.name().equalsIgnoreCase(rncPackageData.getMode())){
                addActionError("Live or Live2 Package does not allow create / update operation on Monetary Rate Card");
            }else if(RnCPkgType.MONETARY_ADDON.name().equals(rncPackageData.getType())){
                addActionError("RnC package of type "+RnCPkgType.MONETARY_ADDON.getVal()+" does not support non monetary rate card");
            }else if(rncPackageData.getChargingType().equals(ChargingType.EVENT.name()) && rateCardData.getNonMonetaryRateCardData().getPulseUom().equals(Uom.EVENT.name()) == false){
                addActionError("RnC package of charging type is EVENT then Non-Monetary pulse unit must be EVENT");
            }
            rateCardData.setRncPackageData(rncPackageData);
        }
    }


    public List<Uom> getPulseUnits() {
        return pulseUnits;
    }

    public void setPulseUnits(List<Uom> pulseUnits) {
        this.pulseUnits = pulseUnits;
    }

    public String getNonMonetaryRateCardAsJson() {
        return nonMonetaryRateCardAsJson;
    }

    public void setNonMonetaryRateCardAsJson(String nonMonetaryRateCardAsJson) {
        this.nonMonetaryRateCardAsJson = nonMonetaryRateCardAsJson;
    }
    public String getChargingType() {
        return chargingType;
    }

    public void setChargingType(String chargingType) {
        this.chargingType = chargingType;
    }


}
