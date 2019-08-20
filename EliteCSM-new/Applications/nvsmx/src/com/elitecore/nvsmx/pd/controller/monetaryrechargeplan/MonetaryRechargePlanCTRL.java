package com.elitecore.nvsmx.pd.controller.monetaryrechargeplan;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ResourceStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.monetaryrechargeplan.MonetaryRechargePlanData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Pattern;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates.createStaffBelongingPredicate;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(NVSMXCommonConstants.REST_PARENT_PKG_PD)
@Namespace("/pd/monetaryrechargeplan")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","monetary-recharge-plan"}),
})
public class MonetaryRechargePlanCTRL extends RestGenericCTRL<MonetaryRechargePlanData> {

    private static final String MODULE = "MONETARY-RECHARGE-PLAN";
    private static final String REGEX_PATTERN = "^((\\d{0,9}(\\.\\d{0,6})?))$";
    public static final String PRICE = "price";

    @Override
    public ACLModules getModule() {
        return ACLModules.MONETARYRECHARGEPLAN;
    }

    @Override
    public MonetaryRechargePlanData createModel() {
        return new MonetaryRechargePlanData();
    }

    @Override
    public void validate() {
        super.validate();
        validateUniquenessOnPrice();
        validateAmount();
        validateValidationPeriodUnit();
        validateStatus();
        validateAmountAndValidity();
    }
    private void validateAmountAndValidity() {
        MonetaryRechargePlanData data = (MonetaryRechargePlanData) getModel();
        if((data.getAmount()==null || data.getAmount().equals(BigDecimal.ZERO)) && (data.getValidity()==null || data.getValidity() == 0)) {
            addFieldError("amount",getText("plan.amountvalidity.provided"));
            addFieldError("validity",getText("plan.amountvalidity.provided"));
        }
    }

    private void validateAmount() {
        MonetaryRechargePlanData data = (MonetaryRechargePlanData) getModel();
        if(data.getPrice() != null  && Pattern.matches(REGEX_PATTERN,data.getPrice().toPlainString()) == false) {
            addFieldError(PRICE,getText("plan.price.precision"));
        }
        if(data.getAmount() != null && Pattern.matches(REGEX_PATTERN,data.getAmount().toPlainString()) == false) {
            addFieldError("amount",getText("plan.price.precision"));
        }
    }

    private void validateValidationPeriodUnit() {
        MonetaryRechargePlanData data = (MonetaryRechargePlanData) getModel();
        if((data.getValidityPeriodUnit().equals(ValidityPeriodUnit.DAY) || data.getValidityPeriodUnit().equals(ValidityPeriodUnit.MID_NIGHT) ||
                data.getValidityPeriodUnit().equals(ValidityPeriodUnit.HOUR) || data.getValidityPeriodUnit().equals(ValidityPeriodUnit.MINUTE)) == false) {
            addFieldError("validityPeriodUnit","Validity Period Unit can not be other than DAY, HOUR, MID_NIGHT or MINUTE");
        }
    }

    private void validateStatus() {
        MonetaryRechargePlanData data = (MonetaryRechargePlanData) getModel();
        if(Strings.isNullOrEmpty(data.getStatus())) {
            addFieldError("status","Status cannot be null");
        } else {
            if((ResourceStatus.ACTIVE.name().equalsIgnoreCase(data.getStatus()) || ResourceStatus.INACTIVE.name().equalsIgnoreCase(data.getStatus())) == false) {
                addFieldError("status","Status can not be other than ACTIVE or INACTIVE");
            }
        }
    }

    private void validateUniquenessOnPrice() {
        MonetaryRechargePlanData data = (MonetaryRechargePlanData) getModel();
        String mode = "create";
        if(Strings.isNullOrEmpty(data.getId()) == false) {
            mode = "update";
        }
        try {
            if(CRUDOperationUtil.isDuplicateProperty(mode, MonetaryRechargePlanData.class, data.getId(), data.getPrice(), PRICE)) {
                addFieldError(PRICE, "Price already exist");
            }
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while validating price uniqueness" + getModule().getDisplayLabel()
                    + " . Reason: " + e.getMessage());
            LogManager.ignoreTrace(e);
        }
    }

    @Override
    public HttpHeaders index() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called index()");
        }
        try{
            List<MonetaryRechargePlanData> monetaryRechargePlanDataList = CRUDOperationUtil.findAll(MonetaryRechargePlanData.class);
            Collectionz.filter(monetaryRechargePlanDataList,createStaffBelongingPredicate(getStaffBelongingGroups()));
            setList(monetaryRechargePlanDataList);
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch (Exception e){
            getLogger().error(getLogModule(),"Error while fetching "+getModule().getDisplayLabel()+" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform Search Operation");
        }
        return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);

    }

    private List<String> getStaffBelongingGroups() {
        String groups = (String) getRequest().getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
        if (groups == null) {

            groups = "";
        }

        return CommonConstants.COMMA_SPLITTER.split(groups);
    }

    @Override
    public HttpHeaders create() {
        MonetaryRechargePlanData model = (MonetaryRechargePlanData) getModel();
        model.setPackageMode(PkgMode.DESIGN.name());
        return super.create();
    }

    @Override
    public HttpHeaders update() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling update method");
        }
        MonetaryRechargePlanData model = (MonetaryRechargePlanData) getModel();

        MonetaryRechargePlanData planFromDB = CRUDOperationUtil.get(MonetaryRechargePlanData.class,model.getId());
        if(planFromDB == null){
            addActionError("Monetary recharge plan not found with given id: " + model.getId());
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }
        if(PkgMode.LIVE2.name().equalsIgnoreCase(planFromDB.getPackageMode()) || PkgMode.LIVE.name().equalsIgnoreCase(planFromDB.getPackageMode())){
            model.setName(planFromDB.getName());
            model.setDescription(planFromDB.getDescription());
            model.setGroups(planFromDB.getGroups());
            model.setValidity(planFromDB.getValidity());
            model.setPrice(planFromDB.getPrice());
            model.setValidityPeriodUnit(planFromDB.getValidityPeriodUnit());
            model.setAmount(planFromDB.getAmount());
        }

        model.setPackageMode(planFromDB.getPackageMode());
        return super.update();
    }

    @Override
    protected boolean prepareAndValidateDestroy(MonetaryRechargePlanData monetaryRechargePlanData) {
        PkgMode pkgMode = PkgMode.valueOf(monetaryRechargePlanData.getPackageMode());
        if(pkgMode.getOrder() >= PkgMode.LIVE.getOrder()) {
            addActionError(getText("pkg.delete.live.mode", Arrays.asList(getModule().getDisplayLabel())));
            return false;
        }
        return true;
    }

    @SkipValidation
    public HttpHeaders updateMode() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "update mode is called");
        }

        MonetaryRechargePlanData model = (MonetaryRechargePlanData) getModel();
        String nextModeVal = getRequest().getParameter(Attributes.PKG_MODE);
		PkgMode nextMode = PkgMode.getMode(nextModeVal);

		if (nextMode == null) {
			addActionError("Package Mode not provided/Invalid mode received to update");
			setActionChainUrl(getRedirectURL(METHOD_SHOW));
			return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
		}

        MonetaryRechargePlanData monetaryRechargePlanDataFromDB = CRUDOperationUtil.get(MonetaryRechargePlanData.class,model.getId());
        if(monetaryRechargePlanDataFromDB == null){
            addActionError("Monetary recharge plan not found with given id:" + model.getId());
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }

		String existingPkgMode = monetaryRechargePlanDataFromDB.getPackageMode();
		PkgMode existingMode = PkgMode.getMode(existingPkgMode);
		int result = PkgMode.compare(existingMode, nextMode);
		if (result > 0) {
			addActionError("Monetary recharge plan can not be updated to previous mode(s). Life Cycle can be DESIGN --> TEST --> LIVE --> LIVE2");
			setActionChainUrl(getRedirectURL(METHOD_SHOW));
			return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
		}

        monetaryRechargePlanDataFromDB.setPackageMode(nextMode.val);

        if(nextMode == PkgMode.LIVE || nextMode == PkgMode.LIVE2) {
			List<PolicyDetail> policyDetails = DefaultNVSMXContext.getContext().getPolicyRepository().getPolicyDetail(monetaryRechargePlanDataFromDB.getName());
			if (Collectionz.isNullOrEmpty(policyDetails)) {
                getLogger().error(MODULE, "Unable to change package mode. Reason: Policy is not reloaded");
                addActionError("You are recommended to reload policies before updating mode to "+ nextMode);
                setActionChainUrl(getRedirectURL(METHOD_SHOW));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
            }

            for (PolicyDetail policyDetail : policyDetails) {
                String remark = policyDetail.getRemark();
                PolicyStatus status = policyDetail.getStatus();
                if ((status != PolicyStatus.SUCCESS || status != PolicyStatus.PARTIAL_SUCCESS) && Strings.isNullOrEmpty(remark) == false) {
                    getLogger().error(MODULE, "Unable to change package mode to "+ nextMode +".\n " +
                            "Reason: Policy is failed with status " + status + ", " + remark);
                    addActionError("Unable to change package mode to "+ nextMode +".\n " +
                            "Reason: Policy is failed with status " + status + ", " + remark);
                    setActionChainUrl(getRedirectURL(METHOD_SHOW));
                    return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
                }
            }
        }

        setModel(monetaryRechargePlanDataFromDB);
        return super.update();
    }
}
