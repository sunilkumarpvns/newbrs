package com.elitecore.nvsmx.pd.controller.rncpackage;

import java.math.BigDecimal;
import java.util.*;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardScope;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.rncpackage.notification.RncNotificationData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.BigDecimalToStringGsonAdapter;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.LongToStringGsonAdapter;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.pd.model.rncpackage.RncPackageDetailsWrapper;
import com.elitecore.nvsmx.policydesigner.controller.util.ProductOfferUtility;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.Restrictions;
import org.hibernate.exception.ConstraintViolationException;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates.createStaffBelongingPredicate;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(value = "pd")
@Namespace("/pd/rncpackage")
@Results({ @Result(name = SUCCESS, type = "redirectAction", params = { "actionName", "rnc-package" }),

})
public class RncPackageCTRL extends RestGenericCTRL<RncPackageData> {

	private static final String MODULE  = "RNC-PACKAGE-CTRL";
	private static final long serialVersionUID = 5527430558612297879L;

	private static final Predicate<RateCardData> MONETARY_RATE_CARD_PREDICATE = rateCardData-> RateCardType.MONETARY.name().equals(rateCardData.getType());
	private static final Predicate<RateCardData> NON_MONETARY_RATE_CARD_PREDICATE = rateCardData-> RateCardType.NON_MONETARY.name().equals(rateCardData.getType());
	public static final String SCOPE = "scope";
	public static final String GLOBAL = "GLOBAL";

	private String rateCardGroupAsJson;
	private String monetaryRateCardListAsjson;
	private String nonMonetaryRateCardListAsjson;
	private String rncNotificationListAsJson;
	private static final Comparator<RateCardGroupData> BYORDER = Comparator.comparingInt(RateCardGroupData::getOrderNo);

	private List<RncPackageDetailsWrapper> rncPackageDetailsWrapperData ;
	private String groupIds;
	private List<RateCardData> rateCardList = Collectionz.newArrayList();
	private List<NotificationTemplateData> emailTemplateList = Collectionz.newArrayList();
	private List<NotificationTemplateData> smsTemplateList = Collectionz.newArrayList();
	private boolean globalRateCardExists = false;
    private boolean isCurrencyUpdateAllowed;

	private static final Criterion NON_MONETARY_RATE_CARD_CRITERIA = Restrictions.eq("type", RateCardType.NON_MONETARY.name());
	private static final NotificationComparator NOTIFICATION_COMPARATOR = new NotificationComparator();

	public List<RncPackageDetailsWrapper> getRncPackageDetailsWrapperData() {
		return rncPackageDetailsWrapperData;
	}

	public void setRncPackageDetailsWrapperData(List<RncPackageDetailsWrapper> rncPackageDetailsWrapperData) {
		this.rncPackageDetailsWrapperData = rncPackageDetailsWrapperData;
	}

	@Override
	public ACLModules getModule() {
		return ACLModules.RNCPACKAGE;
	}

	@Override
	public RncPackageData createModel() {
		return new RncPackageData();
	}
	
	@SkipValidation	
    @Override
	public void prepareValuesForSubClass() throws Exception {
		RncPackageData rncPackageData = (RncPackageData)getModel();
        if(Strings.isNullOrEmpty(rncPackageData.getId()) == false){
        	getRequest().setAttribute(NVSMXCommonConstants.RNC_PACKAGE_ID, rncPackageData.getId());
        }		
		super.prepareValuesForSubClass();
	}

	@Override
	public void validate() {
		super.validate();

		RncPackageData rncPackageData = (RncPackageData) getModel();

		rncPackageData.setRateCardData(new ArrayList<>());
		rncPackageData.setRateCardGroupData(new ArrayList<>());
		rncPackageData.setRncNotifications(new ArrayList<>());

        validateCurrency();
	}

	private boolean validateCurrency(){
        boolean isValid = true;
        RncPackageData rncPackageData = (RncPackageData) getModel();

        if(Strings.isNullOrBlank(rncPackageData.getCurrency()) ) {
            addFieldError("rncpackage.currency",getText("error.rncpackage.currency.required"));
            isValid = false;

        }else if (SystemParameterDAO.isMultiCurrencyEnable()) {
            if (SystemParameter.CURRENCY.validate(rncPackageData.getCurrency()) == false) {
                addFieldError("rncpackage.currency", getText("error.rncpackage.invalid.currency"));
                isValid = false;
            }
        } else if (SystemParameterDAO.getCurrency().equals(rncPackageData.getCurrency()) == false) {
                addFieldError("rncpackage.currency", getText("error.rncpackage.currency.systemOnly"));
                isValid = false;
        }

        return isValid;
    }

	@Override
	public HttpHeaders index() {
		HttpHeaders result = super.index();

		List<RncPackageData> dataList = (List<RncPackageData>)getList();
		Collectionz.filter(dataList, createStaffBelongingPredicate(getStaffBelongingGroups()));
		for(RncPackageData rncPackageData : dataList){
			rncPackageData.setRateCardData(null);
			rncPackageData.setRateCardGroupData(null);
			rncPackageData.setRncNotifications(null);
		}

		return result;
	}

    @Override
    public String edit() {  // initUpdate
        String result;
        RncPackageData rncPackageData;

        result = super.edit();

        rncPackageData = (RncPackageData) getModel();
        setCurrencyUpdateAllowed(checkRncPackageCurrencyUpdate(rncPackageData, false));
        return result;
    }


    @Override
	public HttpHeaders update() { // update
        String authorizedResult;
        RncPackageData rncPackageData;
        RncPackageData rncPackageDataFromDB;

		if(LogManager.getLogger().isDebugLogLevel()){
			LogManager.getLogger().debug(getLogModule(),"Method called update()");
		}

		try {

            authorizedResult = authorize();
			if(SUCCESS.equals(authorizedResult)==false){
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            rncPackageData = (RncPackageData) getModel();
            if (Strings.isNullOrBlank(rncPackageData.getId())) {
                addActionError(getModule().getDisplayLabel() + " Not Found with id: " + rncPackageData.getId());
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }

            rncPackageDataFromDB = CRUDOperationUtil.get(RncPackageData.class,rncPackageData.getId());
			if(rncPackageDataFromDB == null){
				addActionError(getModule().getDisplayLabel()+" Not Found with id: " + rncPackageData.getId());
				getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ rncPackageData.getId()+". Reason: Not found");
				return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
			}

			if(rncPackageDataFromDB.getChargingType().equals(rncPackageData.getChargingType()) == false){
				addActionError(getModule().getDisplayLabel()+" Charging Type Cannot be changed: " + rncPackageData.getId());
				getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ rncPackageData.getId()+". Reason: ChargingType cannot be changed.");
				return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
			}

            if(rncPackageDataFromDB.getCurrency().equals(rncPackageData.getCurrency())==false && checkRncPackageCurrencyUpdate(rncPackageDataFromDB, true)==false){
                getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ rncPackageData.getId()+". Reason: Currency cannot be changed.");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }


			rncPackageData.setMode(rncPackageDataFromDB.getMode());
			rncPackageData.setType(rncPackageDataFromDB.getType());
			if(PkgMode.LIVE2.name().equalsIgnoreCase(rncPackageDataFromDB.getMode())){
				rncPackageData.setName(rncPackageDataFromDB.getName());
				rncPackageData.setDescription(rncPackageDataFromDB.getDescription());
				rncPackageData.setGroups(rncPackageDataFromDB.getGroups());
				rncPackageData.setGroupNames(rncPackageDataFromDB.getGroupNames());
			}
			rncPackageData.setModifiedDateAndStaff(getStaffData());
			rncPackageData.setRateCardData(rncPackageDataFromDB.getRateCardData());
			rncPackageData.setRateCardGroupData(rncPackageDataFromDB.getRateCardGroupData());
			rncPackageData.setRncNotifications(rncPackageDataFromDB.getRncNotifications());

            List<RateCardData> rateCardDataList = rncPackageData.getRateCardData();
			if(Collectionz.isNullOrEmpty(rateCardDataList)==false){
			    for(RateCardData rateCardData : rateCardDataList){
                    rateCardData.setCurrency(rncPackageData.getCurrency());
                }
            }

			CRUDOperationUtil.merge(rncPackageData);
			String auditMessage = getAuditMessage(rncPackageData, UPDATED);
			CRUDOperationUtil.audit(rncPackageData,rncPackageData.getResourceName(),AuditActions.UPDATE,getStaffData(),getRequest().getRemoteAddr(),rncPackageData.getHierarchy(), auditMessage);
			addActionMessage(getModule().getDisplayLabel()+" updated successfully");
			setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(rncPackageData.getId()));
			CRUDOperationUtil.flushSession();
			return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
		}catch (ConstraintViolationException cve){
			getLogger().error(getLogModule(),"Error while updating "+ getModule().getDisplayLabel() +" information. Reason: "+cve.getMessage());
			getLogger().trace(getLogModule(),cve);
			addActionError("Fail to perform update Operation.Reason: constraint "+cve.getConstraintName()+" violated");
		}catch (Exception e){
			getLogger().error(getLogModule(),"Error while updating " + getModule().getDisplayLabel() + " information. Reason: "+e.getMessage());
			getLogger().trace(getLogModule(),e);
			addActionError("Fail to perform Update Operation");
		}
		return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();

	}

	@Override
	public HttpHeaders show() { // View
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called show()");
        }
        RncPackageData rncPackageData =(RncPackageData) getModel();
        try {
        	RncPackageData rncPackageFromDB = CRUDOperationUtil.get(rncPackageData.getClass(),rncPackageData.getId(),getAdditionalCriteria());
            if (rncPackageFromDB == null) {
                addActionError(getModule().getDisplayLabel()+" Not Found with id " + rncPackageData.getId());
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
            if (rncPackageFromDB.getGroups() != null) {
                String belongingsGroups = GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(rncPackageFromDB.getGroups()));
                rncPackageFromDB.setGroupNames(belongingsGroups);
            }
            
            getRequest().setAttribute(NVSMXCommonConstants.RNC_PACKAGE_ID, rncPackageData.getId());
            setRateCardWrappers(rncPackageFromDB.getRateCardData());
			Collections.sort(rncPackageFromDB.getRateCardGroupData(), BYORDER);

			Criterion criterion = Restrictions.and(Restrictions.eq(SCOPE, RateCardScope.GLOBAL.name()),
					Restrictions.eq("chargingType", rncPackageFromDB.getChargingType()));
			List<RateCardData> globalRateCardData = CRUDOperationUtil.findAll(RateCardData.class, criterion);
			Collectionz.filter(globalRateCardData, createStaffBelongingPredicate(getStaffBelongingGroups()));
			globalRateCardData = globalRateCardData.stream().filter(e -> ProductOfferUtility.doesBelongToGroup(CommonConstants.COMMA_SPLITTER.split(rncPackageFromDB.getGroups()),
					CommonConstants.COMMA_SPLITTER.split(e.getGroups()))).collect(Collectors.toList());
			setGlobalRateCardExists(globalRateCardData.isEmpty()==false);

			setRateCardGroupWrappers(rncPackageFromDB.getRateCardGroupData());

			if(RnCPkgType.MONETARY_ADDON.name().equals(rncPackageFromDB.getType())){
				rncPackageFromDB.setRncNotifications(null);
			} else {
				setRncNotificationList(rncPackageFromDB.getRncNotifications());
				setRateCardAndNotificationTemplateList(rncPackageFromDB.getId());
			}

			setModel(rncPackageFromDB);

        } catch (Exception e) {
            addActionError("Fail to view " + getModule().getDisplayLabel() + " for id ");
            getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + " for id " + rncPackageData.getId() + " . Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        setActionChainUrl(getRedirectURL(METHOD_SHOW));
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);

    }

	private void setRateCardAndNotificationTemplateList(String rncPackageId) throws Exception {
		List<RateCardData> nonMonetaryRateCards = CRUDOperationUtil.findAll(RateCardData.class,NON_MONETARY_RATE_CARD_CRITERIA);
		if (Strings.isNullOrEmpty(rncPackageId) == false) {
			for (RateCardData rateCardData : nonMonetaryRateCards) {
				if (rncPackageId.equals(rateCardData.getRncPackageData().getId())) {
					getRateCardList().add(rateCardData);
				}
			}
		}
		List<NotificationTemplateData> templateDatas = CRUDOperationUtil.findAllByStatus(NotificationTemplateData.class, null, null, null, null);
		RncPackageData rncPackageData = CRUDOperationUtil.get(RncPackageData.class,rncPackageId);
		CRUDOperationUtil.filterpackages(templateDatas, rncPackageData.getGroups());
		for (NotificationTemplateData notificationTemplateData : templateDatas) {
			if (NotificationTemplateType.EMAIL == notificationTemplateData.getTemplateType()) {
				getEmailTemplateList().add(notificationTemplateData);
			} else if (NotificationTemplateType.SMS == notificationTemplateData.getTemplateType()) {
				getSmsTemplateList().add(notificationTemplateData);
			}
		}
	}

	@SkipValidation
	public HttpHeaders updateMode() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(getLogModule(), "method called updateMode()");
		}
		RncPackageData rncPackageData = (RncPackageData) getModel();
		String nextModeVal = getRequest().getParameter(Attributes.PKG_MODE);
		PkgMode nextMode = PkgMode.getMode(nextModeVal);
		if(nextMode == null){
			addActionError("RnC Package Mode not provided/Invalid mode received to update");
			setActionChainUrl(getRedirectURL(METHOD_SHOW));
			return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
		}
		RncPackageData rncPackageFromDB = CRUDOperationUtil.get(RncPackageData.class,rncPackageData.getId());
		if(rncPackageFromDB == null){
			addActionError("RnC Package not found with given id:" + rncPackageData.getId());
			setActionChainUrl(getRedirectURL(METHOD_SHOW));
			return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
		}
		String existingPkgMode = rncPackageFromDB.getMode();
		PkgMode existingMode = PkgMode.getMode(existingPkgMode);
		int result = PkgMode.compare(existingMode,nextMode);
		if(result > 0){
			addActionError("RnC Package can not be updated to previous mode(s). Life Cycle can be DESIGN --> TEST --> LIVE --> LIVE2");
			setActionChainUrl(getRedirectURL(METHOD_SHOW));
			return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
		}

		List<PolicyDetail> policyDetails = DefaultNVSMXContext.getContext().getPolicyRepository().getRnCDetails(rncPackageFromDB.getName());
		if(nextMode == PkgMode.LIVE) {
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

		rncPackageFromDB.setMode(nextMode.val);
		setModel(rncPackageFromDB);
		return new DefaultHttpHeaders(SUCCESS);
	}

	@Override
	protected boolean prepareAndValidateDestroy(RncPackageData rncPackageData) {
		if(PkgMode.LIVE2.name().equalsIgnoreCase(rncPackageData.getMode())){
			addActionError("RnC Package can't be deleted.Reason: "+rncPackageData.getName()+" is in LIVE2 Stage");
			return false;
		}

		Criteria criteria = HibernateSessionFactory.getSession().createCriteria(ProductOfferServicePkgRelData.class);
		criteria.add(Restrictions.eq("rncPackageData.id", rncPackageData.getId()));
		List<ProductOfferServicePkgRelData> productOfferServicePkgRelData = criteria.list();

		if(productOfferServicePkgRelData.isEmpty()==false){
			addActionError("RnC Package can't be deleted.Reason: "+rncPackageData.getName()+" has reference in offer("+getOfferNames(productOfferServicePkgRelData)+")");
			return false;
		}

		return true;
	}

	private String getOfferNames(List<ProductOfferServicePkgRelData> productOfferServicePkgRelDataList){
		StringBuilder names = new StringBuilder();

		for(ProductOfferServicePkgRelData data: productOfferServicePkgRelDataList){
			if(data.getProductOfferData()!=null){
				names.append(data.getProductOfferData().getName()+", ");
			}
		}

		return names.substring(0,names.length()-2);
	}

	public String getGroupIds() {
		return groupIds;
	}

	public void setGroupIds(String groupIds) {
		this.groupIds = groupIds;
	}

	public String getRateCardGroupAsJson() {
		return rateCardGroupAsJson;
	}

	public void setRateCardGroupAsJson(String rateCardGroupAsJson) {
		this.rateCardGroupAsJson = rateCardGroupAsJson;
	}

	public String getMonetaryRateCardListAsjson() {
		return monetaryRateCardListAsjson;
	}

	public void setMonetaryRateCardListAsjson(String monetaryRateCardListAsjson) {
		this.monetaryRateCardListAsjson = monetaryRateCardListAsjson;
	}

	public String getNonMonetaryRateCardListAsjson() {
		return nonMonetaryRateCardListAsjson;
	}

	public void setNonMonetaryRateCardListAsjson(String nonMonetaryRateCardListAsjson) {
		this.nonMonetaryRateCardListAsjson = nonMonetaryRateCardListAsjson;
	}
	
	private void setRateCardWrappers(List<RateCardData> rateCardDataList) {
		Gson gson = new GsonBuilder().registerTypeAdapter(BigDecimal.class, new BigDecimalToStringGsonAdapter()).registerTypeAdapter(Long.class, new LongToStringGsonAdapter()).create();
		setMonetaryRateCardListAsjson(gson.toJsonTree(rateCardDataList.stream().filter(MONETARY_RATE_CARD_PREDICATE).collect(Collectors.toList())).getAsJsonArray().toString());
		setNonMonetaryRateCardListAsjson(gson.toJsonTree(rateCardDataList.stream().filter(NON_MONETARY_RATE_CARD_PREDICATE).collect(Collectors.toList())).getAsJsonArray().toString());
	}
	
	private void setRateCardGroupWrappers(List<RateCardGroupData> rateCardGroupDatas) {
		Gson gson = GsonFactory.defaultInstance();
		setRateCardGroupAsJson(gson.toJsonTree(rateCardGroupDatas).getAsJsonArray().toString());
	}

	@Override
	public String getDataListAsJson() {
		Gson gson = GsonFactory.defaultInstance();

		for (RncPackageData rncPackageData : getList()){
			RnCPkgType rnCPkgType = RnCPkgType.fromName(rncPackageData.getType());
			rncPackageData.setType(rnCPkgType==null?null:rnCPkgType.getVal());
		}

		JsonArray modelJson = gson.toJsonTree(getList(),new TypeToken<List<RncPackageData>>() {}.getType()).getAsJsonArray();
		return modelJson.toString();
	}

	@SkipValidation
	public String viewDetails(){
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(getLogModule(), "Method called viewDetails()");
		}
		String tableId = getRequest().getParameter(Attributes.TABLE_ID);
		String rowData = getRequest().getParameter(Attributes.ROW_DATA+tableId);
		
	    Gson gson = GsonFactory.defaultInstance();
	    
	    RncPackageData rncPackageData = gson.fromJson(rowData, RncPackageData.class);
	  
        rncPackageData = CRUDOperationUtil.get(RncPackageData.class, rncPackageData.getId());
       
        List<RncPackageDetailsWrapper> rncPackageDetailsWrappers = createRncPackageWrapper(rncPackageData.getRateCardGroupData());
        setRncPackageDetailsWrapperData(rncPackageDetailsWrappers);
        setActionChainUrl("/WEB-INF/content/pd/rncpackage/rnc-package-view-sub-details.jsp");
        
        return NVSMXCommonConstants.SUBTABLEURL;
		
	}

	private boolean checkRncPackageCurrencyUpdate(RncPackageData rncPackageData, boolean addErrorMsg){
	    boolean isCurrencyUpdateAllowed = false;
        if(SystemParameterDAO.isMultiCurrencyEnable()){
            PkgMode rncPkgMode = PkgMode.getMode(rncPackageData.getMode());
            if (PkgMode.DESIGN==rncPkgMode || PkgMode.TEST==rncPkgMode){
                isCurrencyUpdateAllowed = true;

                List<RateCardGroupData> rncPackageRateCardGroupDataList = rncPackageData.getRateCardGroupData();
                if(Collectionz.isNullOrEmpty(rncPackageRateCardGroupDataList)){
                    if (getLogger().isDebugLogLevel()) {
                        getLogger().debug(getLogModule(), "Currency can be modified. Reason: " + rncPackageData.getName() + " does not have any global Rate Card association.");
                    }

                }else{
                    boolean isGlobalFound = rncPackageRateCardGroupDataList.stream().anyMatch(c-> ((c.getPeakRateRateCard()!=null && RateCardScope.GLOBAL.name().equals(c.getPeakRateRateCard().getScope()))
                            ||(c.getOffPeakRateRateCard()!=null && RateCardScope.GLOBAL.name().equals(c.getOffPeakRateRateCard().getScope()))));

                    if(isGlobalFound){
                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(getLogModule(), "Currency cannot be modified. Reason: " + rncPackageData.getName() + " has global Rate Card associated with it.");
                        }
                        if(addErrorMsg) {
                            addActionError(getModule().getDisplayLabel() + "Currency cannot be modified. Reason: " + rncPackageData.getName() + " has global Rate Card associated with it.");
                        }
                        isCurrencyUpdateAllowed = false;
                    }

                }

                if (PkgMode.TEST==rncPkgMode){
                    Criterion rncPackageFilterCriteria = Restrictions.eq("rncPackageData", rncPackageData);
                    List<ProductOfferServicePkgRelData> productOfferServicePkgRelDataList = CRUDOperationUtil.findAll(ProductOfferServicePkgRelData.class, rncPackageFilterCriteria);

                    if(Collectionz.isNullOrEmpty(productOfferServicePkgRelDataList)){
                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(getLogModule(), "Currency can be modified. Reason: " + rncPackageData.getName() + " is not associated with product offers.");
                        }
                    }else{
                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(getLogModule(), "Currency cannot be modified. Reason: " + rncPackageData.getName() + " is already associated with product offers.");
                        }
                        if(addErrorMsg) {
                            addActionError(getModule().getDisplayLabel() + "Currency cannot be modified. Reason: " + rncPackageData.getName() + " is already associated with product offers.");
                        }
                        isCurrencyUpdateAllowed = false;
                    }

                }
            } else  if (PkgMode.LIVE==rncPkgMode){
                addActionError(getModule().getDisplayLabel() + "Currency cannot be modified. Reason: " + rncPackageData.getName() + " is already in LIVE Mode.");

            } else  if (PkgMode.LIVE2==rncPkgMode){
                addActionError(getModule().getDisplayLabel() + "Currency cannot be modified. Reason: " + rncPackageData.getName() + " is already in LIVE2 Mode.");

            }
        }

        return isCurrencyUpdateAllowed;
    }

	public List<RateCardData> getRateCardList() {
		return rateCardList;
	}

	public void setRateCardList(List<RateCardData> rateCardList) {
		this.rateCardList = rateCardList;
	}

	public List<NotificationTemplateData> getEmailTemplateList() {
		return emailTemplateList;
	}

	public void setEmailTemplateList(List<NotificationTemplateData> emailTemplateList) {
		this.emailTemplateList = emailTemplateList;
	}

	public List<NotificationTemplateData> getSmsTemplateList() {
		return smsTemplateList;
	}

	public void setSmsTemplateList(List<NotificationTemplateData> smsTemplateList) {
		this.smsTemplateList = smsTemplateList;
	}


	private List<RncPackageDetailsWrapper> createRncPackageWrapper(List<RateCardGroupData>rateCardGroupDatas){
			List<RncPackageDetailsWrapper> rncPackageDetailsWrapper = Collectionz.newArrayList();

			for(RateCardGroupData rateCardGroupData : rateCardGroupDatas){
				RncPackageDetailsWrapper wrapper = new RncPackageDetailsWrapper.RncPackageDetailWrapperBuilder(rateCardGroupData.getId()).withRateCardGroupdetail(rateCardGroupData).build();
				rncPackageDetailsWrapper.add(wrapper);
			}
	        return  rncPackageDetailsWrapper;
		}

	public String getRncNotificationListAsJson() {
		return rncNotificationListAsJson;
	}

	public void setRncNotificationListAsJson(String rncNotificationListAsJson) {
		this.rncNotificationListAsJson = rncNotificationListAsJson;
	}

	public void setRncNotificationList(List<RncNotificationData> rncNotificationList) {
		sortByRateCardAndThreshold(rncNotificationList);
		Gson gson = GsonFactory.defaultInstance();
		setRncNotificationListAsJson(gson.toJsonTree(rncNotificationList).getAsJsonArray().toString());
	}

	private void sortByRateCardAndThreshold(List<RncNotificationData> rncNotificationList) {
		Collections.sort(rncNotificationList, NOTIFICATION_COMPARATOR);
	}

	private List<String> getStaffBelongingGroups() {
		String groups = (String) getRequest().getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
		if (groups == null) {

			groups = "";
		}

		return CommonConstants.COMMA_SPLITTER.split(groups);
	}

	private static class NotificationComparator implements Comparator<RncNotificationData> {

		@Override
		public int compare(RncNotificationData data1, RncNotificationData data2) {

			int result = data1.getRateCardData().getName().compareTo(data2.getRateCardData().getName());
			if (result != 0) {
				return result;
			}

			result = data1.getThreshold().compareTo(data2.getThreshold());
			if (result != 0) {
				return result;
			}

			return 0;
		}
	}

	public boolean getGlobalRateCardExists() {
		return globalRateCardExists;
	}

	public void setGlobalRateCardExists(boolean globalRateCardExists) {
		this.globalRateCardExists = globalRateCardExists;
	}

    public boolean getCurrencyUpdateAllowed(){
        return isCurrencyUpdateAllowed;
    }

    private void setCurrencyUpdateAllowed(boolean isCurrencyUpdateAllowed){ this.isCurrencyUpdateAllowed = isCurrencyUpdateAllowed; }
}
