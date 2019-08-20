
package com.elitecore.nvsmx.pd.controller.productoffer;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyType;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferAutoSubscriptionRelData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pd.service.ServiceData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateData;
import com.elitecore.corenetvertex.pkg.notification.NotificationTemplateType;
import com.elitecore.corenetvertex.pm.HibernateReader;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.sm.systemparameter.SystemParameter;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.commons.collection.Lists;
import com.elitecore.nvsmx.remotecommunications.NotPredicate;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.sm.model.systemparameter.SystemParameterDAO;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.listeners.DefaultNVSMXContext;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
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

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.corenetvertex.pm.pkg.ResourceDataPredicates.createStaffBelongingPredicate;
import static com.elitecore.nvsmx.policydesigner.controller.util.ProductOfferUtility.doesBelongToGroup;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(NVSMXCommonConstants.REST_PARENT_PKG_PD)
@Namespace("/pd/productoffer")
@Results({
        @Result(name = SUCCESS,
                type = RestGenericCTRL.REDIRECT_ACTION,
                params = {NVSMXCommonConstants.ACTION_NAME, "product-offer"})
})
public class ProductOfferCTRL extends RestGenericCTRL<ProductOfferData> {

    private static final long serialVersionUID = -1520099473142841844L;
    private List<ServiceData> serviceDataList = new ArrayList<>();
    private List<ProductOffer> addOnProductOfferForSuggestions = new ArrayList<>();
    private List<RnCPackage> rncPkgDataList = new ArrayList<>();
    private List<RnCPackage> rncMonetaryAddOnList = Collectionz.newArrayList();
    private List<RnCPackage> rncNonMonetaryAddOnList = Collectionz.newArrayList();
    private List<Package> pkgDataList = new ArrayList<>();
    private String[] servicePkgRelationIds;
    private String[] autoSubscriptionRelIds;
    private static final String MODULE = "PRODUCT-OFFER-CTRL";
    private List<NotificationTemplateData> emailTemplateList = Collectionz.newArrayList();
    private List<NotificationTemplateData> smsTemplateList = Collectionz.newArrayList();
    private boolean isCurrencyUpdateAllowed;

    private List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionList = Collectionz.newArrayList();

    private String productOfferServicePkgRelListAsJson;
    private String autoSubscriptionsAsJson;
    private List<String> productOfferGroupList;
    private static final Predicate<ProductOfferServicePkgRelData> EMPTY_PRODUCT_SPEC_SRV_DATA = productSpecServicePkgRelData -> {
        if (productSpecServicePkgRelData == null) {
            return false;
        }
        return (Strings.isNullOrBlank(productSpecServicePkgRelData.getRncPackageId())
                || Strings.isNullOrBlank(productSpecServicePkgRelData.getServiceId())) == false;
    };

    private List<String> getProductOfferGroupList() {
        return productOfferGroupList;
    }

    private void setProductOfferGroupList(List<String> productOfferGroupList) {
        this.productOfferGroupList = productOfferGroupList;
    }


    @Override
    public ACLModules getModule() {
        return ACLModules.PRODUCTOFFER;
    }

    @Override
    public ProductOfferData createModel() {
        return new ProductOfferData();
    }


    @Override
    public HttpHeaders index() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called index()");
        }
        try{
            List<ProductOfferData> productOfferDataList = CRUDOperationUtil.findAll(ProductOfferData.class);
            Collectionz.filter(productOfferDataList,createStaffBelongingPredicate(getStaffBelongingGroups()));
            setList(productOfferDataList);
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
        ProductOfferData productOfferData = (ProductOfferData) getModel();
        productOfferData.setPackageMode(PkgMode.DESIGN.name());
        setGroups(productOfferData);
        // validate and associate product offer service relation list
        setProductSpecSrvRelData(productOfferData);

        if (findDuplicateEntriesOfServiceFromReq(productOfferData)) {
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }
        for (ProductOfferServicePkgRelData relData : productOfferData.getProductOfferServicePkgRelDataList()) {
            HttpHeaders result = validateProductOfferServiceRel(productOfferData,relData);
            if (result != null) {
                return result;
            }
        }
        //validate Auto Subscription associated with Product Offer
        if (isMaximumAutoSubscriptionReached(productOfferData)) {
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRelDatas = productOfferData.getProductOfferAutoSubscriptionRelDatas();
        if (productOfferAutoSubscriptionRelDatas.isEmpty() == false) {
            if(findDuplicateEntriesFromReq(productOfferData)){
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
            }
            Collectionz.filter(productOfferAutoSubscriptionRelDatas, input -> Strings.isNullOrBlank(input.getAddOnProductOfferId()) == false);
            if(Collectionz.isNullOrEmpty(productOfferAutoSubscriptionRelDatas)) {
                getLogger().error(MODULE, "Unable to add auto subscription. Reason: AddOn product offer id not received in request");
                addActionError(getText("product.offer.auto.sub.id.not.provided"));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
            }

        }

        /* We are skipping logic of checking highest order number while creating product offer at one go.
           we need to consider it in update scenario.
        * */

        Integer orderNo = 1;
        for(ProductOfferAutoSubscriptionRelData relData: productOfferData.getProductOfferAutoSubscriptionRelDatas()){
            relData.setOrderNo(orderNo);
            HttpHeaders result = validateAutoSubscriptionsAssociatedWithProductOffer(productOfferData, relData);
            if (result != null) {
                return result;
            }
            orderNo++;
        }
        return super.create();
    }

    private List<String> getStaffBelongingGroups() {
        String groups = (String) getRequest().getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
        if (groups == null) {

            groups = "";
        }

        return CommonConstants.COMMA_SPLITTER.split(groups);
    }

    @Override
    public void validate() {
        super.validate();

        ProductOfferData productOfferData = (ProductOfferData) getModel();
        if (ACLAction.CREATE.name().equalsIgnoreCase(getMethodName())) {
            if (StringUtils.isNotEmpty(productOfferData.getPackageMode())) {
                if (PkgMode.DESIGN.name().equalsIgnoreCase(productOfferData.getPackageMode()) == false) {
                    addActionError("Invalid input parameter provided. Product offer cant be created in "+productOfferData.getPackageMode()+" mode");
                }
            }
        }

        if (Strings.isNullOrBlank(productOfferData.getDataServicePkgId()) == false) {
            PkgData dataPackage = CRUDOperationUtil.getNotDeleted(PkgData.class, productOfferData.getDataServicePkgId());
            if (dataPackage == null) {
                addFieldError("dataPackageId", getText("product.offer.data.package.not.exist"));
            } else if(PolicyManager.getInstance().getPkgDataById(dataPackage.getId()) == null){
                addFieldError("dataPackageId", getText("product.offer.data.package.improper.state"));
            }else if(PolicyManager.getInstance().getPkgDataById(dataPackage.getId()).getStatus() == PolicyStatus.FAILURE){
                addFieldError("dataPackageId",getText("product.offer.data.package.failed.state"));
            } else if (productOfferData.getType().equalsIgnoreCase(dataPackage.getType()) == false) {
                addFieldError("dataPackageId", getText("product.offer.data.package.invalid.type"));
            } else if (PkgMode.DESIGN.name().equalsIgnoreCase(dataPackage.getPackageMode())) {
                addFieldError("dataPackageId", getText("product.offer.live.data.package.required"));
            } else {
                productOfferData.setDataServicePkgData(dataPackage);
            }

            List<String> productOfferGroups;
            if(StringUtils.isBlank(productOfferData.getGroups())) {
                productOfferGroups = Arrays.asList(CommonConstants.DEFAULT_GROUP_ID);
            } else {
                productOfferGroups = CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups());
            }

            if(dataPackage != null) {
                List<String> dataPkgGroups = CommonConstants.COMMA_SPLITTER.split(dataPackage.getGroups());
                boolean hasCommonGroups = dataPkgGroups.stream().filter( productOfferGroups::contains).findFirst().isPresent();
                if (hasCommonGroups == false) {
                    String errorMessage = getText("product.offer.data.pkg.diff.groups");
                    getLogger().error(MODULE, "Unable to add data package in product offer " + dataPackage.getName() + ".Reason:" + errorMessage
                            + ", Product offer Groups(" + productOfferGroups.stream().collect(Collectors.joining(",")) + ")"
                            + ", Data Package Groups(" + dataPkgGroups.stream().collect(Collectors.joining(",")) + ")");
                    addFieldError("dataPackageId", errorMessage);
                }
            }


        }

        validateCurrency(productOfferData);

        if (PkgType.fromName(productOfferData.getType()) != PkgType.BASE) {
            if (productOfferData.getValidityPeriod() == null) {
                addFieldError("validityPeriod", getText("error.valueRequired"));
            }
            if (productOfferData.getValidityPeriod() != null && productOfferData.getValidityPeriod() <= 0) {
                addFieldError("validityPeriod", getText("product.offer.validity.period.negative"));
            }
            if (ValidityPeriodUnit.DAY.name().equalsIgnoreCase(productOfferData.getValidityPeriodUnit()) == false &&
                    ValidityPeriodUnit.HOUR.name().equalsIgnoreCase(productOfferData.getValidityPeriodUnit()) == false &&
                    ValidityPeriodUnit.MID_NIGHT.name().equalsIgnoreCase(productOfferData.getValidityPeriodUnit()) == false &&
                    ValidityPeriodUnit.MINUTE.name().equalsIgnoreCase(productOfferData.getValidityPeriodUnit()) == false) {
                addFieldError("validityPeriodUnit", getText("invalid.field.value"));
            }
        }
        if(PkgType.fromName(productOfferData.getType()) == PkgType.BASE){
            if (productOfferData.getCreditBalance() != null) {
                if (productOfferData.getCreditBalance() < 0) {
                    addFieldError("creditBalance", getText("product.offer.credit.balance.negative"));
                }
                if (productOfferData.getCreditBalance() > 999999) {
                    addFieldError("creditBalance", getText("product.offer.credit.balance.invalid"));
                }
            }

        }

        if (productOfferData.getSubscriptionPrice() != null && productOfferData.getSubscriptionPrice() < 0) {
                getLogger().error(getLogModule(), getText("product.offer.price.negative"));
                addFieldError("subscriptionPrice", getText("product.offer.price.negative"));
        }

        if(productOfferData.getValidityPeriodUnit()==null){
            productOfferData.setValidityPeriodUnit(ValidityPeriodUnit.DAY.name());
        }
        if(PkgType.ADDON.name().equalsIgnoreCase(productOfferData.getType())){
            if(Strings.isNullOrBlank(productOfferData.getEmailTemplateId()) == false){
                NotificationTemplateData emailTemplateData = CRUDOperationUtil.get(NotificationTemplateData.class,productOfferData.getEmailTemplateId());
                if(emailTemplateData == null){
                    addFieldError("emailTemplateId",getText("product.offer.email.not.exist"));
                } else if(emailTemplateData.getTemplateType() != NotificationTemplateType.EMAIL){
                    addFieldError("emailTemplateId",getText("product.offer.email.not.exist"));
                }else{
                    productOfferData.setEmailNotificationTemplateData(emailTemplateData);
                }
            }
            if(Strings.isNullOrBlank(productOfferData.getSmsTemplateId()) == false){
                NotificationTemplateData smsTemplateData = CRUDOperationUtil.get(NotificationTemplateData.class,productOfferData.getSmsTemplateId());
                if(smsTemplateData == null){
                    addFieldError("smsTemplateId",getText("product.offer.sms.not.exist"));
                } else if(smsTemplateData.getTemplateType() != NotificationTemplateType.SMS){
                    addFieldError("smsTemplateId",getText("product.offer.sms.not.exist"));
                }else{
                    productOfferData.setSmsNotificationTemplateData(smsTemplateData);
                }
            }
        }

    }

    private boolean validateCurrency(ProductOfferData productOfferData){
        boolean bValid = true;
        if(Objects.isNull(productOfferData.getCurrency()) ) {
            addFieldError(getText("product.offer.currency"),getText("product.offer.error.currency.required"));
            bValid = false;
        }else {
            if (!SystemParameterDAO.isMultiCurrencyEnable()) {
                if (!SystemParameterDAO.getCurrency().equals(productOfferData.getCurrency())) {
                    addFieldError(getText("product.offer.currency"), getText("product.offer.invalid.currency.systemonly"));
                    bValid = false;
                }
            } else {
                if (!SystemParameter.CURRENCY.validate(productOfferData.getCurrency())) {
                    addFieldError(getText("product.offer.currency"), getText("product.offer.invalid.currency.value"));
                    bValid = false;
                }
                PkgData pkgData=productOfferData.getDataServicePkgData();
                if(pkgData!=null && pkgData.getCurrency()!=null){
                    if(!pkgData.getCurrency().equals(productOfferData.getCurrency())){
                        addFieldError(getText("product.offer.currency"), getText("product.offer.data.package.different.currency"));
                        bValid = false;
                    }
                }
            }
        }
        return bValid;
    }

    @Override
    public String edit() {
        String result = super.edit();
        ProductOfferData productOfferData = (ProductOfferData) getModel();
        setProductOfferGroupList(CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups()));
        Map<String, PkgData> packages = getIdToPackagesFromDb();
        if (Objects.equals(PkgType.ADDON.name(), ((ProductOfferData) getModel()).getType())) {
            setRncMonetaryAddOnList(PolicyManager.getInstance().getRnCPackage().all().stream()
                    .filter(rncPkg -> doesBelongToGroup(rncPkg.getGroupIds(), getStaffBelongingGroups())
                            && rncPkg.getPackageMode().getOrder() >= PkgMode.TEST.getOrder()
                            && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                            && (rncPkg.getPkgType() == RnCPkgType.MONETARY_ADDON)).collect(Collectors.toList()));
            setRncNonMonetaryAddOnList(PolicyManager.getInstance().getRnCPackage().all().stream()
                    .filter(rncPkg -> doesBelongToGroup(rncPkg.getGroupIds(), getStaffBelongingGroups())
                            && rncPkg.getPackageMode().getOrder() >= PkgMode.LIVE.getOrder()
                            && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                            && (rncPkg.getPkgType() == RnCPkgType.NON_MONETARY_ADDON)).collect(Collectors.toList()));
            setPkgDataList(PolicyManager.getInstance().getAllPackageDatas().stream()
                    .filter(dataPkg -> PkgType.ADDON.name().equals(dataPkg.getType())
                            && doesBelongToGroup(dataPkg.getGroupIds(), getStaffBelongingGroups())
                            && dataPkg.getMode().getOrder() >= PkgMode.TEST.getOrder()
                            && dataPkg.getStatus() != PolicyStatus.FAILURE
                            && isPackageDeleted(packages.get(dataPkg.getId()))==false).collect(Collectors.toList()));
        } else {
            setRncPkgDataList(PolicyManager.getInstance().getRnCPackage().all().stream()
                    .filter(rncPkg -> doesBelongToGroup(rncPkg.getGroupIds(), getStaffBelongingGroups())
                            && rncPkg.getPackageMode().getOrder() >= PkgMode.TEST.getOrder()
                            && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                            && rncPkg.getPkgType() == RnCPkgType.BASE)
                    .filter(rnCPackage -> doesBelongToGroup(rnCPackage.getGroupIds(), getProductOfferGroupList()))
                    .collect(Collectors.toList()));
            setPkgDataList(PolicyManager.getInstance().getAllPackageDatas().stream()
                    .filter(dataPkg -> PkgType.BASE.name().equals(dataPkg.getType())
                            && doesBelongToGroup(dataPkg.getGroupIds(), getStaffBelongingGroups())
                            && dataPkg.getMode().getOrder() >= PkgMode.TEST.getOrder()
                            && dataPkg.getStatus() != PolicyStatus.FAILURE
                            && isPackageDeleted(packages.get(dataPkg.getId()))==false)
                    .filter(dataPkg -> doesBelongToGroup(dataPkg.getGroupIds(), getProductOfferGroupList()))
                    .collect(Collectors.toList()));
        }

        setServiceDataList(CRUDOperationUtil.findAll(ServiceData.class));
        List<NotificationTemplateData> templateDatas = CRUDOperationUtil.findAllByStatus(NotificationTemplateData.class, null, null, null, null);
        for (NotificationTemplateData notificationTemplateData : templateDatas) {
            if (NotificationTemplateType.EMAIL == notificationTemplateData.getTemplateType()) {
                getEmailTemplateList().add(notificationTemplateData);
            } else if (NotificationTemplateType.SMS == notificationTemplateData.getTemplateType()) {
                getSmsTemplateList().add(notificationTemplateData);
            }
        }

        setCurrencyUpdateAllowed(productOfferData);

        return result;


    }

    private void setCurrencyUpdateAllowed(ProductOfferData productOfferData){
        if((productOfferData.getDataServicePkgData()==null) && (productOfferData.getProductOfferServicePkgRelDataList()==null || productOfferData.getProductOfferServicePkgRelDataList().isEmpty()) && (productOfferData.getProductOfferAutoSubscriptionRelDatas()==null || productOfferData.getProductOfferAutoSubscriptionRelDatas().isEmpty())){
            this.isCurrencyUpdateAllowed = true;
        }
    }

    private Map<String, PkgData> getIdToPackagesFromDb(){
        List<PkgData> dataPackages = HibernateReader.readAll(PkgData.class, HibernateSessionFactory.getSession());
        return dataPackages.stream().collect(
                Collectors.toMap(PkgData::getId, x-> x));
    }

    private boolean isPackageDeleted(PkgData pkgData){

        if(pkgData == null){
            return true;
        }

        return CommonConstants.STATUS_DELETED.equals(pkgData.getStatus());
    }

    @Override
    public HttpHeaders update() {
        ProductOfferData productOfferData = (ProductOfferData) getModel();
        if (Strings.isNullOrBlank(productOfferData.getId())) {
            getLogger().error(MODULE, "Product offer Id Not found");
            addActionError(getModule().getDisplayLabel() + " Id Not Found ");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
        setGroups(productOfferData);
        ProductOfferData productOfferDataInDB = CRUDOperationUtil.get(ProductOfferData.class, productOfferData.getId());

        if (Objects.isNull(productOfferDataInDB)) {
            getLogger().error(MODULE, "Product offer Not found with Given Id: " + productOfferData.getId());
            addActionError(getModule().getDisplayLabel() + " Not Found with given id: " + productOfferData.getId());
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        //Currency validation
        setCurrencyUpdateAllowed(productOfferDataInDB);

        if(!(productOfferDataInDB.getCurrency().equals(productOfferData.getCurrency()))){
            if (PkgMode.getMode(productOfferDataInDB.getPackageMode()) == PkgMode.LIVE) {
                getLogger().error(MODULE, "Currency cannot be modified. Reason: Product offer in LIVE mode.");
                addActionError(getText("product.offer.modify.currency.error.in.live.mode"));
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }else if (PkgMode.getMode(productOfferDataInDB.getPackageMode()) == PkgMode.LIVE2) {
                getLogger().error(MODULE, "Currency cannot be modified. Reason: Product offer in LIVE2 mode.");
                addActionError(getText("product.offer.modify.currency.error.in.live2.mode"));
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }else if(!getCurrencyUpdateAllowed()){
                getLogger().error(MODULE, "Currency cannot be modified. Reason: Data Package or Rnc Package or Auto Subscription Add-on Product Offer is already associated with the product offer.");
                addActionError(getText("product.offer.modify.currency.error"));
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
        }

        productOfferData.setProductOfferServicePkgRelDataList(productOfferDataInDB.getProductOfferServicePkgRelDataList());
        productOfferData.setProductOfferAutoSubscriptionRelDatas(productOfferDataInDB.getProductOfferAutoSubscriptionRelDatas());
        productOfferData.setPackageMode(productOfferDataInDB.getPackageMode());

        if (PkgMode.getMode(productOfferDataInDB.getPackageMode()) == PkgMode.LIVE) {
            productOfferDataInDB.setSubscriptionPrice(productOfferData.getSubscriptionPrice());
            productOfferDataInDB.setParam1(productOfferData.getParam1());
            productOfferDataInDB.setParam2(productOfferData.getParam2());
            productOfferDataInDB.setValidityPeriod(productOfferData.getValidityPeriod());
            productOfferDataInDB.setValidityPeriodUnit(productOfferData.getValidityPeriodUnit());
            productOfferDataInDB.setStatus(productOfferData.getStatus());
            productOfferDataInDB.setEmailNotificationTemplateData(productOfferData.getEmailNotificationTemplateData());
            productOfferDataInDB.setSmsNotificationTemplateData(productOfferData.getSmsNotificationTemplateData());
            productOfferDataInDB.setCreditBalance(productOfferData.getCreditBalance());
            setModel(productOfferDataInDB);
        } else if (PkgMode.getMode(productOfferDataInDB.getPackageMode()) == PkgMode.LIVE2) {
            productOfferDataInDB.setStatus(productOfferData.getStatus());
            setModel(productOfferDataInDB);
        }
        return super.update();
    }

    @SkipValidation
    public HttpHeaders initManageOrder(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called initManageOrder()");
        }
        try{

            ProductOfferData productOfferData = (ProductOfferData) getModel();
            ProductOfferData productOfferDataFromDB = CRUDOperationUtil.get(ProductOfferData.class, productOfferData.getId());
            List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRels = productOfferDataFromDB.getProductOfferAutoSubscriptionRelDatas();
            setProductOfferAutoSubscriptionList(productOfferAutoSubscriptionRels);
            setActionChainUrl(getRedirectURL("auto-subscription-manageorder"));
            productOfferAutoSubscriptionList.sort(Comparator.comparing(ProductOfferAutoSubscriptionRelData::getOrderNo));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Error while going to Manage Order view. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.NOT_FOUND.code);
        }
    }

    @SkipValidation
    public HttpHeaders manageOrder(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called manageOrder()");
        }
        try{

            ProductOfferData productOfferData = (ProductOfferData) getModel();
            productOfferData = CRUDOperationUtil.get(ProductOfferData.class,productOfferData.getId());
            String[] productOfferAutoSubscriptionIds = getRequest().getParameterValues("productOfferAutoSubscriptionIds");
            int index = 1;
            for(String id: productOfferAutoSubscriptionIds){
                ProductOfferAutoSubscriptionRelData productOfferAutoSubscriptionRelData = CRUDOperationUtil.get(ProductOfferAutoSubscriptionRelData.class,id);
                JsonObject oldJsonObject = productOfferAutoSubscriptionRelData.toJson();
                int oldOrderNumber = productOfferAutoSubscriptionRelData.getOrderNo();

                productOfferAutoSubscriptionRelData.setOrderNo(index);
                int newOrderNumber = productOfferAutoSubscriptionRelData.getOrderNo();
                JsonObject newJsonObject = productOfferAutoSubscriptionRelData.toJson();

                CRUDOperationUtil.update(productOfferAutoSubscriptionRelData);

                JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);
                String message = getModule().getDisplayLabel() + " <b><i>" + productOfferAutoSubscriptionRelData.getAddOnProductOfferData().getName() + "</i></b> " + "Updated";
                CRUDOperationUtil.audit(productOfferData, productOfferAutoSubscriptionRelData.getAddOnProductOfferData().getName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, productOfferData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "Product Offer's Auto Subscription order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }

            setActionChainUrl(getRedirectURL("../../../product-offer/"+productOfferData.getId()));
            addActionMessage("Auto Subscription order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage order of Auto Subscriptions. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }


    private void setProductSpecSrvRelData(ProductOfferData productOfferData) {

        if (Collectionz.isNullOrEmpty(productOfferData.getProductOfferServicePkgRelDataList())) {
            return;
        }
        Collectionz.filter(productOfferData.getProductOfferServicePkgRelDataList(), EMPTY_PRODUCT_SPEC_SRV_DATA);

        productOfferData.getProductOfferServicePkgRelDataList()
                .forEach(productSpecServicePkgRelData -> productSpecServicePkgRelData.setProductOfferData(productOfferData
                ));

    }

    private void setGroups(ProductOfferData productOfferData) {
        if (Strings.isNullOrBlank(productOfferData.getGroups())) {
            productOfferData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
        }
    }

    public List<ServiceData> getServiceDataList() {
        return serviceDataList;
    }

    public void setServiceDataList(List<ServiceData> serviceDataList) {
        this.serviceDataList = serviceDataList;
    }

    public List<RnCPackage> getRncPkgDataList() {
        return rncPkgDataList;
    }

    public void setRncPkgDataList(List<RnCPackage> rncPkgDataList) {
        this.rncPkgDataList = rncPkgDataList;
    }

    public List<Package> getPkgDataList() {
        return pkgDataList;
    }

    public void setPkgDataList(List<Package> pkgDataList) {
        this.pkgDataList = pkgDataList;
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        Map<String, PkgData> packages = getIdToPackagesFromDb();
        if (Objects.equals(PkgType.ADDON.name(), ((ProductOfferData) getModel()).getType())) {
            setRncMonetaryAddOnList(PolicyManager.getInstance().getRnCPackage().all().stream()
                    .filter(rncPkg -> doesBelongToGroup(rncPkg.getGroupIds(), getStaffBelongingGroups())
                            && rncPkg.getPackageMode().getOrder() >= PkgMode.TEST.getOrder()
                            && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                            && (rncPkg.getPkgType() == RnCPkgType.MONETARY_ADDON)).collect(Collectors.toList()));
            setRncNonMonetaryAddOnList(PolicyManager.getInstance().getRnCPackage().all().stream()
                    .filter(rncPkg -> doesBelongToGroup(rncPkg.getGroupIds(), getStaffBelongingGroups())
                            && rncPkg.getPackageMode().getOrder() >= PkgMode.LIVE.getOrder()
                            && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                            && (rncPkg.getPkgType() == RnCPkgType.NON_MONETARY_ADDON)).collect(Collectors.toList()));
            setPkgDataList(PolicyManager.getInstance().getAllPackageDatas().stream()
                    .filter(dataPkg -> PkgType.ADDON.name().equals(dataPkg.getType())
                            && doesBelongToGroup(dataPkg.getGroupIds(), getStaffBelongingGroups())
                            && dataPkg.getMode().getOrder() >= PkgMode.TEST.getOrder()
                            && dataPkg.getStatus() != PolicyStatus.FAILURE
                            && isPackageDeleted(packages.get(dataPkg.getId())) == false).collect(Collectors.toList()));
        } else {
            setRncPkgDataList(PolicyManager.getInstance().getRnCPackage().all().stream()
                    .filter(rncPkg -> doesBelongToGroup(rncPkg.getGroupIds(), getStaffBelongingGroups())
                            && rncPkg.getPackageMode().getOrder() >= PkgMode.TEST.getOrder()
                            && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                            && rncPkg.getPkgType() == RnCPkgType.BASE).collect(Collectors.toList()));
            setPkgDataList(PolicyManager.getInstance().getAllPackageDatas().stream()
                    .filter(dataPkg -> PkgType.BASE.name().equals(dataPkg.getType())
                            && doesBelongToGroup(dataPkg.getGroupIds(), getStaffBelongingGroups())
                            && dataPkg.getMode().getOrder() >= PkgMode.TEST.getOrder()
                            && dataPkg.getStatus() != PolicyStatus.FAILURE
                            && isPackageDeleted(packages.get(dataPkg.getId())) == false).collect(Collectors.toList()));
        }
        setServiceDataList(CRUDOperationUtil.findAll(ServiceData.class));
        List<NotificationTemplateData> templateDatas = CRUDOperationUtil.findAllByStatus(NotificationTemplateData.class, null, null, null, null);
        for (NotificationTemplateData notificationTemplateData : templateDatas) {
            if (NotificationTemplateType.EMAIL == notificationTemplateData.getTemplateType()) {
                getEmailTemplateList().add(notificationTemplateData);
            } else if (NotificationTemplateType.SMS == notificationTemplateData.getTemplateType()) {
                getSmsTemplateList().add(notificationTemplateData);
            }
        }
    }

    @Override
    public HttpHeaders show() {
        HttpHeaders result = super.show();

        if (result.getResultCode().equals(ERROR)) {
            return result;
        }

        ProductOfferData productOfferData = (ProductOfferData) getModel();
        setServiceRelations(productOfferData);


        final PkgMode productOfferMode = PkgMode.getMode(productOfferData.getPackageMode());

        Gson gson = GsonFactory.defaultInstance();

        setProductOfferGroupList(CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups()));

        setProductOfferServicePkgRelListAsJson(gson.toJsonTree(productOfferData.getProductOfferServicePkgRelDataList()).getAsJsonArray().toString());

        setAutoSubscriptionsAsJson(gson.toJsonTree(productOfferData.getProductOfferAutoSubscriptionRelDatas()).getAsJsonArray().toString());

        if (Objects.equals(PkgType.ADDON.name(), ((ProductOfferData) getModel()).getType())) {
            setRncMonetaryAddOnList(PolicyManager.getInstance().getRnCPackage().all().stream()
                    .filter(rncPkg -> doesBelongToGroup(rncPkg.getGroupIds(), getStaffBelongingGroups())
                            && rncPkg.getCurrency().equals(productOfferData.getCurrency())
                            && rncPkg.getPackageMode().getOrder() >= productOfferMode.getOrder()
                            && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                            && (rncPkg.getPkgType() == RnCPkgType.MONETARY_ADDON))
                    .filter(rnCPackage -> doesBelongToGroup(rnCPackage.getGroupIds(), getProductOfferGroupList())).collect(Collectors.toList()));
            setRncNonMonetaryAddOnList(PolicyManager.getInstance().getRnCPackage().all().stream()
                    .filter(rncPkg -> doesBelongToGroup(rncPkg.getGroupIds(), getStaffBelongingGroups())
                            && rncPkg.getCurrency().equals(productOfferData.getCurrency())
                            && rncPkg.getPackageMode().getOrder() >= productOfferMode.getOrder()
                            && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                            && (rncPkg.getPkgType() == RnCPkgType.NON_MONETARY_ADDON))
                    .filter(rnCPackage -> doesBelongToGroup(rnCPackage.getGroupIds(), getProductOfferGroupList())).collect(Collectors.toList()));
        } else {
            setRncPkgDataList(PolicyManager.getInstance().getRnCPackage().all().stream()
                    .filter(rncPkg -> doesBelongToGroup(rncPkg.getGroupIds(), getStaffBelongingGroups())
                            && rncPkg.getCurrency().equals(productOfferData.getCurrency())
                            && rncPkg.getPackageMode().getOrder() >= productOfferMode.getOrder()
                            && rncPkg.getPolicyStatus() != PolicyStatus.FAILURE
                            && rncPkg.getPkgType() == RnCPkgType.BASE)
                    .filter(rnCPackage -> doesBelongToGroup(rnCPackage.getGroupIds(), getProductOfferGroupList())).collect(Collectors.toList()));

            List<ProductOffer> addOnProductOfferForSuggestions = PolicyManager.getInstance().getProductOffer().all().stream()
                    .filter(productOffer -> productOffer.getPolicyStatus() != PolicyStatus.FAILURE)
                    .filter(productOffer -> productOffer.getType() == PkgType.ADDON)
                    .filter(productOffer -> productOffer.getCurrency().equals(productOfferData.getCurrency()))
                    .filter(productOffer -> productOffer.getPackageMode().getOrder() >= productOfferMode.getOrder())
                    .filter(productOffer -> doesBelongToGroup(productOffer.getGroups(), getStaffBelongingGroups()))
                    .filter(productOffer -> doesBelongToGroup(productOffer.getGroups(),getProductOfferGroupList()))
                    .sorted(Comparator.comparing(ProductOffer::getName))
                    .collect(Collectors.toList());

            setAddOnProductOfferForSuggestions(addOnProductOfferForSuggestions);
        }
        setServiceDataList(CRUDOperationUtil.findAll(ServiceData.class));

        return result;

    }

    @Override
    public String getDataListAsJson() {
        Gson gson = GsonFactory.defaultInstance();

        for (ProductOfferData productOfferData : getList()) {
            PkgType pkgType = PkgType.fromName(productOfferData.getType());
            productOfferData.setType(Objects.isNull(pkgType) ? null : pkgType.val);
        }

        JsonArray modelJson = gson.toJsonTree(getList(), new TypeToken<List<RncPackageData>>() {
        }.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    private void setServiceRelations(ProductOfferData productOfferData) {
        for (ProductOfferServicePkgRelData serviceDataRelations : productOfferData.getProductOfferServicePkgRelDataList()) {
            serviceDataRelations.setRncPackageName(serviceDataRelations.getRncPackageData().getName());
            serviceDataRelations.setDisplayPackageId(serviceDataRelations.getRncPackageData().getId());
            serviceDataRelations.setServiceName(serviceDataRelations.getServiceData().getName());
        }
    }

    @SkipValidation
    public HttpHeaders addServicePkgRelations() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling Add Relation Method");
        }
        ProductOfferData productOfferData = (ProductOfferData) getModel();
        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
        setProductSpecSrvRelData(productOfferData);
        ProductOfferData productOfferDataInDB = CRUDOperationUtil.get(ProductOfferData.class, productOfferData.getId());

        if (productOfferDataInDB == null) {
            getLogger().error(getLogModule(), "Unable to find Product Offer Data for Given Id");
            addActionError("Product Offer not found");
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
        }
        ProductSpecServicePkgRelPredicate productSpecServicePkgRelPredicate = new ProductSpecServicePkgRelPredicate(productOfferDataInDB.getProductOfferServicePkgRelDataList());
        List<ProductOfferServicePkgRelData> duplicateMappingList = Lists.copy(productOfferData.getProductOfferServicePkgRelDataList(), productSpecServicePkgRelPredicate);
        if (Collectionz.isNullOrEmpty(duplicateMappingList) == false) {
            getLogger().error(getLogModule(), "Duplicate Mapping found for Service Package Relation");
            addActionError(getText("product.offer.service.relation.duplicate.error"));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        for (ProductOfferServicePkgRelData relData : productOfferData.getProductOfferServicePkgRelDataList()) {
            HttpHeaders result = validateProductOfferServiceRel(productOfferDataInDB, relData);
            if (result != null) {
                return result;
            }
        }

        JsonObject jsonOldObject = productOfferDataInDB.toJson();

        productOfferDataInDB.getProductOfferServicePkgRelDataList().addAll(productOfferData.getProductOfferServicePkgRelDataList());
        CRUDOperationUtil.merge(productOfferDataInDB);

        JsonObject jsonNewObject = productOfferDataInDB.toJson();

        addActionMessage(getModule().getDisplayLabel() + " updated successfully");

        JsonArray diff = ObjectDiffer.diff(jsonOldObject, jsonNewObject);
        String message = getModule().getDisplayLabel() + " <b><i>" + FieldValueConstants.RNC_PACKAGE + "</i></b> " + "Updated";
        CRUDOperationUtil.audit(productOfferDataInDB, productOfferDataInDB.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), diff, productOfferDataInDB.getHierarchy(), message);

        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
        CRUDOperationUtil.flushSession();
        return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }

    private HttpHeaders validateProductOfferServiceRel(ProductOfferData productOfferData, ProductOfferServicePkgRelData relData) {
        RncPackageData rncPackageData = CRUDOperationUtil.get(RncPackageData.class, relData.getRncPackageId());
        if (rncPackageData == null) {
            getLogger().error(getLogModule(), getText("product.offer.rnc.package.not.exist"));
            addActionError(getText("product.offer.rnc.package.not.exist"));
            addActionMessage(getText("product.offer.rnc.package.not.exist"));
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        } else if(PolicyManager.getInstance().getRnCPackage().byId(rncPackageData.getId()) == null){
            addFieldError("rncPackageId", getText("product.offer.rnc.package.improper.state"));
        }else if(PolicyManager.getInstance().getRnCPackage().byId(rncPackageData.getId()).getPolicyStatus() == PolicyStatus.FAILURE){
            addFieldError("rncPackageId",getText("product.offer.rnc.package.failed.state"));
        }else if (!rncPackageData.getCurrency().equals(productOfferData.getCurrency())){
            getLogger().error(getLogModule(), getText("product.offer.rnc.package.different.currency"));
            addActionError(getText("product.offer.rnc.package.different.currency"));
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }else {
            PkgMode productOfferMode = PkgMode.getMode(productOfferData.getPackageMode());
            if (rncPackageData.getType().contains(productOfferData.getType()) == false) {
                getLogger().error(getLogModule(), getText("product.offer.rnc.package.invalid.type"));
                addActionError(getText("product.offer.rnc.package.invalid.type"));
                addActionMessage(getText("product.offer.rnc.package.invalid.type"));
                setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
            } else if (PkgMode.getMode(rncPackageData.getMode()).getOrder() >= productOfferMode.getOrder() == false) {
                getLogger().error(getLogModule(), getText("product.offer.live.data.package.required"));
                addActionError(getText("product.offer.live.data.package.required"));
                setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
            }

            List<String> productOfferGroups = CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups());
            List<String> rncPkgDataGroups = CommonConstants.COMMA_SPLITTER.split(rncPackageData.getGroups());
            boolean hasCommonGroups = rncPkgDataGroups.stream().filter(productOfferGroups::contains).findFirst().isPresent();
            if(hasCommonGroups == false) {
                String errorMessage = getText("product.offer.rnc.diff.group");
                getLogger().error(MODULE, "Unable to add RnC package in product offer " + rncPackageData.getName() + ".Reason:" + errorMessage
                        + ", Product offer Groups(" + productOfferGroups.stream().collect(Collectors.joining(","))+ ")"
                        + ", RnC Package Groups(" + rncPkgDataGroups.stream().collect(Collectors.joining(","))+ ")");
                addActionError(errorMessage);
                setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
            }

            relData.setRncPackageData(rncPackageData);
        }

        ServiceData serviceData = CRUDOperationUtil.get(ServiceData.class, relData.getServiceId());
        if (serviceData == null) {
            getLogger().error(getLogModule(), getText("product.offer.service.not.exist"));
            addActionError(getText("product.offer.service.not.exist"));
            addActionMessage(getText("product.offer.service.not.exist"));
            setActionChainUrl(getRedirectURL(METHOD_INDEX));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
        } else {
            relData.setServiceData(serviceData);
        }
        return null;
    }

    @SkipValidation
    public HttpHeaders addAutoSubscription() throws Exception {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling Add Auto Subscription");
        }
        ProductOfferData productOfferData = (ProductOfferData) getModel();
        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
        if (Collectionz.isNullOrEmpty(productOfferData.getProductOfferAutoSubscriptionRelDatas())) {
            getLogger().error(getLogModule(), "Unable to find Auto Subscription Configuration");
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        ProductOfferData productOfferDataInDB = CRUDOperationUtil.get(ProductOfferData.class, productOfferData.getId());

        if (productOfferDataInDB == null) {
            getLogger().error(getLogModule(), "Unable to find Product Offer Data for Given Id");
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
        }

        if(PkgMode.valueOf(productOfferDataInDB.getPackageMode()).getOrder() >= PkgMode.LIVE.getOrder()) {
            getLogger().error(getLogModule(),"Auto Subscription can not be added in " + productOfferDataInDB.getPackageMode() + " mode");
            addActionError(getText("product.offer.auto.sub.add.live.mode.error"));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.OPERATION_NOT_SUPPORTED.code).disableCaching();
        }

        if (isMaximumAutoSubscriptionReached(productOfferData)) {
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        Set<String> productOfferAutoSubscriptionIdsFromDB = productOfferDataInDB.getProductOfferAutoSubscriptionRelDatas().stream().map(ProductOfferAutoSubscriptionRelData::getAddOnProductOfferId).collect(Collectors.toSet());

        if (findDuplicateEntries(productOfferData, productOfferAutoSubscriptionIdsFromDB)) {
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        if (productOfferData.getProductOfferAutoSubscriptionRelDatas().size() > 1 && findDuplicateEntriesFromReq(productOfferData)) {
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        Collectionz.filter(productOfferData.getProductOfferAutoSubscriptionRelDatas(), input -> Strings.isNullOrBlank(input.getAddOnProductOfferId()) == false);

        if(Collectionz.isNullOrEmpty(productOfferData.getProductOfferAutoSubscriptionRelDatas())) {
            getLogger().error(MODULE, "Unable to add auto subscription. Reason: AddOn product offer id not received in request");
            addActionError(getText("product.offer.auto.sub.id.not.provided"));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }
        Integer highestOrderNumber = getHighestOrderNumber(productOfferDataInDB);
        if(highestOrderNumber==null){
            highestOrderNumber = 0;
        }
        Integer orderNo = highestOrderNumber + 1;
        for(ProductOfferAutoSubscriptionRelData relData: productOfferData.getProductOfferAutoSubscriptionRelDatas()){
            relData.setOrderNo(orderNo);
            HttpHeaders result = validateAutoSubscriptionsAssociatedWithProductOffer(productOfferDataInDB, relData);
            if (result != null) {
                return result;
            }
            orderNo++;
        }

        JsonObject jsonOldObject = productOfferDataInDB.toJson();

        productOfferDataInDB.getProductOfferAutoSubscriptionRelDatas().addAll(productOfferData.getProductOfferAutoSubscriptionRelDatas());
        CRUDOperationUtil.merge(productOfferDataInDB);

        JsonObject jsonNewObject = productOfferDataInDB.toJson();

        addActionMessage(getModule().getDisplayLabel() + " updated successfully");

        JsonArray diff = ObjectDiffer.diff(jsonOldObject, jsonNewObject);
        String message = getModule().getDisplayLabel() + " <b><i>" + FieldValueConstants.AUTO_SUBSCRIPTION+ "</i></b> " + "Updated";
        CRUDOperationUtil.audit(productOfferDataInDB,productOfferDataInDB.getResourceName(),AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), diff , productOfferDataInDB.getHierarchy(), message);

        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
        CRUDOperationUtil.flushSession();
        return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }

    private boolean isMaximumAutoSubscriptionReached(ProductOfferData productOfferData) {
        if(productOfferData.getProductOfferAutoSubscriptionRelDatas().size() > CommonConstants.MAX_AUTO_SUBSCRIPTION_COUNT) {
            String errorMessage = getText("product.offer.auto.sub.max.allowed.subscription.reached");
            getLogger().error(MODULE, "Unable to add auto subscription. Reason:" + errorMessage);
            addActionError(errorMessage);
            return true;
        }
        return false;
    }

    private HttpHeaders validateAutoSubscriptionsAssociatedWithProductOffer(ProductOfferData productOfferData, ProductOfferAutoSubscriptionRelData relData) {
        ProductOfferData addOnProductOfferData = CRUDOperationUtil.get(ProductOfferData.class, relData.getAddOnProductOfferId());

        if(Objects.isNull(addOnProductOfferData)){
            getLogger().error(getLogModule(),getText("product.offer.not.exist"));
            addActionError(getText("product.offer.not.exist"));
            addActionMessage(getText("product.offer.not.exist"));
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }

        if(PkgType.valueOf(addOnProductOfferData.getType()) != PkgType.ADDON){
            getLogger().error(getLogModule(),getText("product.offer.auto.sub.type.addon.required"));
            addActionError(getText("product.offer.auto.sub.type.addon.required"));
            addActionMessage(getText("product.offer.auto.sub.type.addon.required"));
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }

        if(!addOnProductOfferData.getCurrency().equals(productOfferData.getCurrency())){
            getLogger().error(getLogModule(),getText("product.offer.auto.subscription.product.offer.different.currency"));
            addActionError(getText("product.offer.auto.subscription.product.offer.different.currency"));
            addActionMessage(getText("product.offer.auto.subscription.product.offer.different.currency"));
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }

        ProductOffer cachedAddOnProductOffer = PolicyManager.getInstance().getProductOffer().byId(addOnProductOfferData.getId());

        if (Objects.isNull(cachedAddOnProductOffer)) {
            String errorMessage = getText("product.offer.auto.sub.addon.not.cache");
            getLogger().error(getLogModule(), "Unable to add auto subscription for addOn Product Offer " + addOnProductOfferData.getName() + ".Reason:" + errorMessage);
            addActionError(errorMessage);
            addActionMessage(errorMessage);
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }

        if (cachedAddOnProductOffer.getPolicyStatus() != PolicyStatus.SUCCESS
                && cachedAddOnProductOffer.getPolicyStatus() != PolicyStatus.LAST_KNOWN_GOOD) {
            String errorMessage = getText("product.offer.auto.sub.addon.not.success.state");
            getLogger().error(getLogModule(), "Unable to add auto subscription for addOn Product Offer " + addOnProductOfferData.getName() + ".Reason:" + errorMessage);
            addActionError(errorMessage);
            addActionMessage(errorMessage);
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }

        PkgMode productOfferMode = PkgMode.valueOf(productOfferData.getPackageMode());
        PkgMode addOnProductOfferMode = cachedAddOnProductOffer.getPackageMode();
        if(productOfferMode.getOrder() > addOnProductOfferMode.getOrder() ){
            List<PkgMode> pkgModes = Arrays.asList(productOfferMode, addOnProductOfferMode);
            String errorMessage = getText("product.offer.auto.sub.incompatible.product.offer", pkgModes);
            getLogger().error(MODULE, "Unable to add auto subscription for addOn Product Offer " + addOnProductOfferData.getName() + ".Reason:" + errorMessage);
            addActionError(errorMessage);
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }
        List<String> parentProductOfferGroups = CommonConstants.COMMA_SPLITTER.split(productOfferData.getGroups());
        List<String> addOnProductOfferGroups = CommonConstants.COMMA_SPLITTER.split(addOnProductOfferData.getGroups());
        boolean hasCommonGroups = addOnProductOfferGroups.stream().filter( parentProductOfferGroups::contains).findFirst().isPresent();
        if(hasCommonGroups == false) {
            String errorMessage = getText("product.offer.auto.sub.diff.groups");
            getLogger().error(MODULE, "Unable to add auto subscription for addOn Product Offer " + addOnProductOfferData.getName() + ".Reason:" + errorMessage
                    + ", Parent Product offer Groups(" + parentProductOfferGroups.stream().collect(Collectors.joining(","))+ ")"
                    + ", AddOn Product offer Groups(" + addOnProductOfferGroups.stream().collect(Collectors.joining(","))+ ")");
            addActionError(errorMessage);
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        }
        relData.setAddOnProductOfferData(addOnProductOfferData);
        relData.setParentProductOfferData(productOfferData);
        return null;
    }

    private boolean findDuplicateEntries(ProductOfferData productOfferData, Set<String> productOfferAutoSubscriptionIds) {
        boolean isDuplicate = productOfferData.getProductOfferAutoSubscriptionRelDatas().stream()
                .map(ProductOfferAutoSubscriptionRelData::getAddOnProductOfferId)
                .filter( productOfferAutoSubscriptionIds::contains)
                .findFirst().isPresent();


        if(isDuplicate) {
                String errorMessage = getText("product.offer.auto.sub.duplicate.error");
                getLogger().error(MODULE, "Unable to add auto subscription. Reason:" + errorMessage);
                addActionError(errorMessage);
                return true;
            }
        return false;
    }

    private boolean findDuplicateEntriesFromReq(ProductOfferData productOfferData) {
        Set<String> productOfferAutoSubscriptionIds = Collectionz.newHashSet();
        for(ProductOfferAutoSubscriptionRelData productOfferAutoSubscriptionRelData : productOfferData.getProductOfferAutoSubscriptionRelDatas()){
            boolean isUniqueAddOnSubscription = productOfferAutoSubscriptionIds.add(productOfferAutoSubscriptionRelData.getAddOnProductOfferId());
            if (isUniqueAddOnSubscription == false) {
                String errorMessage = getText("product.offer.auto.sub.duplicate.error");
                getLogger().error(MODULE, "Unable to add auto subscription. Reason:" + errorMessage);
                addActionError(errorMessage);
                return true;
            }
        }
        return false;
    }

    private boolean findDuplicateEntriesOfServiceFromReq(ProductOfferData productOfferData) {
        Set<String> serviceIds = Collectionz.newHashSet();
        for (ProductOfferServicePkgRelData productOfferAutoSubscriptionRelData : productOfferData.getProductOfferServicePkgRelDataList()) {
            boolean isUniqueAddOnSubscription = serviceIds.add(productOfferAutoSubscriptionRelData.getServiceId());
            if (isUniqueAddOnSubscription == false) {
                getLogger().error(getLogModule(), "Duplicate Mapping found for Service Package Relation");
                addActionError(getText("product.offer.service.relation.duplicate.error"));
                return true;
            }
        }
        return false;
    }

    @SkipValidation
    public HttpHeaders removeAutoSubscription() throws Exception {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling remove Auto Subscription");
        }

        ProductOfferData productOfferData = (ProductOfferData) getModel();
        if (Arrayz.isNullOrEmpty(getAutoSubscriptionRelIds())) {
            getLogger().debug(getLogModule(), "No auto subscription found to remove");
            addActionError(getModule().getDisplayLabel() + " Not Found");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
        productOfferData = CRUDOperationUtil.get(ProductOfferData.class, productOfferData.getId());

        if(PkgMode.valueOf(productOfferData.getPackageMode()).getOrder() >= PkgMode.LIVE.getOrder()) {
            getLogger().error(getLogModule(),"Auto Subscription can not be deleted in " + productOfferData.getPackageMode() + " mode");
            addActionError(getText("product.offer.auto.sub.delete.live.mode.error"));
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.OPERATION_NOT_SUPPORTED.code).disableCaching();
        }

        JsonObject jsonOldObject = productOfferData.toJson();

        List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRelDatas = CRUDOperationUtil.getAllByIds(ProductOfferAutoSubscriptionRelData.class, getAutoSubscriptionRelIds());
        if(Collectionz.isNullOrEmpty(productOfferAutoSubscriptionRelDatas)){
            getLogger().debug(getLogModule(), "No service package relation found to remove");
            addActionError(getModule().getDisplayLabel() + " Not Found");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }

        List<String> ids = Arrays.asList(getAutoSubscriptionRelIds());

        Set<String> productOfferAutoSubscriptionIds = productOfferData.getProductOfferAutoSubscriptionRelDatas().stream().map(ProductOfferAutoSubscriptionRelData::getId).collect(Collectors.toSet());


        String idsNotBineded = ids.stream()
                .filter(id -> productOfferAutoSubscriptionIds.contains(id) == false).distinct()
                .collect(Collectors.joining(","));

        if(StringUtils.isNotEmpty(idsNotBineded)) {
            getLogger().debug(getLogModule(), idsNotBineded + " not bind with product offer");
            addActionError(idsNotBineded + " not bind with product offer");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }


        Collectionz.filter(productOfferData.getProductOfferAutoSubscriptionRelDatas(), data -> ids.contains(data.getId()) == false);

        CRUDOperationUtil.merge(productOfferData);

        addActionMessage(getModule().getDisplayLabel() + " updated successfully");
        JsonObject jsonNewObject = productOfferData.toJson();

        getLogger().debug(MODULE, jsonOldObject.toString());
        getLogger().debug(MODULE, jsonNewObject.toString());
        JsonArray diff = ObjectDiffer.diff(jsonOldObject, jsonNewObject);
        String message = getModule().getDisplayLabel() + " <b><i>" + FieldValueConstants.AUTO_SUBSCRIPTION + "</i></b> " + "Updated";
        CRUDOperationUtil.audit(productOfferData,productOfferData.getResourceName(),AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), diff , productOfferData.getHierarchy(), message);

        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
        CRUDOperationUtil.flushSession();
        return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }

    @Override
    protected boolean prepareAndValidateDestroy(ProductOfferData productOffer) {

        boolean validation  = super.prepareAndValidateDestroy(productOffer);

        if(validation == false) {
            return false;
        }


        PkgMode pkgMode = PkgMode.valueOf(productOffer.getPackageMode());
        if(pkgMode.getOrder() >= PkgMode.LIVE.getOrder()) {
            addActionError(getText("pkg.delete.live.mode", Arrays.asList(getModule().getDisplayLabel())));
            getLogger().error(getLogModule(), "Error while deleting product offer "
                    + productOffer.getName() +"("+ productOffer.getId()
                    +").Reason: Product offer " + productOffer.getName() +"("+ productOffer.getId() +") is in " + pkgMode + " mode");
            return false;
        }


        if(PkgType.valueOf(productOffer.getType()) != PkgType.ADDON) {
            return true;
        }


        List<String> baseProductOffers = findAssociatedAutoSubscriptionAddOns(productOffer);
        if (Collectionz.isNullOrEmpty(baseProductOffers) == false) {
            addActionError("Product offer " + productOffer.getName() +"("+ productOffer.getId() +") is associated with other base product offer");
            getLogger().error(getLogModule(), "Error while deleting product offer "
                    + productOffer.getName() +"("+ productOffer.getId()
                    +").Reason: Product offer is associated with " + baseProductOffers.stream().collect(Collectors.joining(",")));
            return false;
        }

        return true;


    }

    @SkipValidation
    public HttpHeaders removeServicePkgRelations() throws Exception {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Calling remove Relation Method");
        }

        ProductOfferData productOfferData = (ProductOfferData) getModel();
        if (Arrayz.isNullOrEmpty(getServicePkgRelationIds())) {
            getLogger().debug(getLogModule(), "No service package relation found to remove");
            addActionError(getModule().getDisplayLabel() + " Not Found");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
        productOfferData = CRUDOperationUtil.get(ProductOfferData.class, productOfferData.getId());

        JsonObject jsonOldObject = productOfferData.toJson();

        List<ProductOfferServicePkgRelData> productOfferServicePkgRelDataList = CRUDOperationUtil.getAllByIds(ProductOfferServicePkgRelData.class, getServicePkgRelationIds());
        if (Collectionz.isNullOrEmpty(productOfferServicePkgRelDataList)) {
            getLogger().debug(getLogModule(), "No service package relation found to remove");
            addActionError(getModule().getDisplayLabel() + " Not Found");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
        ProductSpecServicePkgRelPredicate relationsInDBContainingPredicate = new ProductSpecServicePkgRelPredicate(productOfferServicePkgRelDataList);
        Collectionz.filter(productOfferData.getProductOfferServicePkgRelDataList(), NotPredicate.of(relationsInDBContainingPredicate));
        CRUDOperationUtil.merge(productOfferData);
        addActionMessage(getModule().getDisplayLabel() + " updated successfully");

        JsonObject jsonNewObject = productOfferData.toJson();

        JsonArray diff = ObjectDiffer.diff(jsonOldObject, jsonNewObject);
        String message = getModule().getDisplayLabel() + " <b><i>" + FieldValueConstants.RNC_PACKAGE + "</i></b> " + "Updated";
        CRUDOperationUtil.audit(productOfferData, productOfferData.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), diff, productOfferData.getHierarchy(), message);

        setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(productOfferData.getId()));
        CRUDOperationUtil.flushSession();
        return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }

    @SkipValidation
    public HttpHeaders updateMode() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called updateMode()");
        }

        ProductOfferData productOfferData = (ProductOfferData) getModel();
        String nextModeVal = getRequest().getParameter(Attributes.PKG_MODE);
        PkgMode nextMode = PkgMode.getMode(nextModeVal);

        if (nextMode == null) {
            addActionError("Product Offer Mode not provided/Invalid mode received to update");
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }


        ProductOfferData productOfferDataFromDB = CRUDOperationUtil.get(ProductOfferData.class, productOfferData.getId());
        if (Objects.isNull(productOfferDataFromDB)) {
            addActionError("Product Offer not found with given id:" + productOfferData.getId());
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
        }

        String existingPkgMode = productOfferDataFromDB.getPackageMode();
        PkgMode existingMode = PkgMode.getMode(existingPkgMode);
        int result = PkgMode.compare(existingMode, nextMode);
        if (result > 0) {
            addActionError("Product Offer can not be updated to previous mode(s). Life Cycle can be DESIGN --> TEST --> LIVE --> LIVE2");
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        if(Objects.isNull(productOfferDataFromDB.getDataServicePkgData()) && Collectionz.isNullOrEmpty(productOfferDataFromDB.getProductOfferServicePkgRelDataList())){
                getLogger().error(getLogModule(), "Please configure Data package or RnC package in Product Offer");
                addActionError(getText("product.offer.empty.data.rnc.packages"));
                setActionChainUrl(getRedirectURL(METHOD_SHOW));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
        }

        PkgData pkgData = productOfferDataFromDB.getDataServicePkgData();
        if (Objects.nonNull(pkgData)) {
            PkgMode dataServicePkgMode = PkgMode.getMode(pkgData.getPackageMode());
            if (validateChildPackageMode(dataServicePkgMode, nextMode) == false) {
                getLogger().error(getLogModule(), "Unable to change offer mode. Reason: Data Service Package(" + pkgData.getName() + ") is in " + dataServicePkgMode.name() + " mode.");
                addActionError("Data Service Package(" + pkgData.getName() + ") is in " + dataServicePkgMode.name() + " mode.");
                setActionChainUrl(getRedirectURL(METHOD_SHOW));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
            }
        }

        if (Collectionz.isNullOrEmpty(productOfferDataFromDB.getProductOfferServicePkgRelDataList()) == false) {
            for (ProductOfferServicePkgRelData productOfferServicePkgRelData : productOfferDataFromDB.getProductOfferServicePkgRelDataList()) {
                RncPackageData rncPackageData = productOfferServicePkgRelData.getRncPackageData();
                PkgMode servicePkgMode = PkgMode.getMode(rncPackageData.getMode());
                if (validateChildPackageMode(servicePkgMode, nextMode) == false) {
                    getLogger().error(getLogModule(), "Unable to change offer mode. Reason: Rnc Package (" + rncPackageData.getName() + ") is in " + servicePkgMode + " mode.");
                    addActionError("RnC Package(" + rncPackageData.getName() + ") is in " + servicePkgMode + " mode.");
                    setActionChainUrl(getRedirectURL(METHOD_SHOW));
                    return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
                }
            }
        }

        if (CollectionUtils.isNotEmpty(productOfferDataFromDB.getProductOfferAutoSubscriptionRelDatas())) {
            for (ProductOfferAutoSubscriptionRelData productOfferServicePkgRelData : productOfferDataFromDB.getProductOfferAutoSubscriptionRelDatas()) {
                ProductOfferData addOnProductOfferData = productOfferServicePkgRelData.getAddOnProductOfferData();
                PkgMode addOnProductOfferMode = PkgMode.getMode(addOnProductOfferData.getPackageMode());
                if (validateChildPackageMode(addOnProductOfferMode, nextMode) == false) {
                    getLogger().error(getLogModule(), "Unable to change offer mode. Reason: Auto Subscription AddOn Product Offer(" + addOnProductOfferData.getName() + ") is in " + addOnProductOfferMode + " mode.");
                    addActionError("Auto Subscription AddOn Product Offer(" + addOnProductOfferData.getName() + ") is in " + addOnProductOfferMode + " mode.");
                    return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
                }
            }
        }

        List<PolicyDetail> policyDetails = DefaultNVSMXContext.getContext().getPolicyRepository().getOfferDetails(productOfferDataFromDB.getName());
        if(nextMode == PkgMode.LIVE) {
            if (Collectionz.isNullOrEmpty(policyDetails)) {
                getLogger().error(MODULE, "Unable to change offer mode "+ nextMode +". Reason: Policy is not reloaded");
                addActionError("You are recommended to reload policies before updating mode to "+ nextMode);
                setActionChainUrl(getRedirectURL(METHOD_SHOW));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
            }

            for (PolicyDetail policyDetail : policyDetails) {
                String remark = policyDetail.getRemark();
                PolicyStatus status = policyDetail.getStatus();
                if ((status != PolicyStatus.SUCCESS || status != PolicyStatus.PARTIAL_SUCCESS) && Strings.isNullOrEmpty(remark) == false) {
                    getLogger().error(MODULE, "Unable to change offer mode to "+ nextMode +".\n " +
                            "Reason: Policy is failed with status " + status + ", " + remark);
                    addActionError("Unable to change offer mode to "+ nextMode +".\n " +
                            "Reason: Policy is failed with status " + status + ", " + remark);
                    setActionChainUrl(getRedirectURL(METHOD_SHOW));
                    return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
                }
            }
        }

        productOfferDataFromDB.setPackageMode(nextMode.name());
        setModel(productOfferDataFromDB);
        return super.update();
    }

    public Integer getHighestOrderNumber(ProductOfferData productOfferData){
        List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionRels = productOfferData.getProductOfferAutoSubscriptionRelDatas();
        if(Collectionz.isNullOrEmpty(productOfferAutoSubscriptionRels)){
            return 0;
        }

        Collections.reverse(productOfferAutoSubscriptionRels);
        return productOfferAutoSubscriptionRels.get(0).getOrderNo();
    }



    private boolean validateChildPackageMode(PkgMode childPackageMode, PkgMode nextMode) {
        return childPackageMode.getOrder() >= nextMode.getOrder();
    }

    public String[] getServicePkgRelationIds() {
        return servicePkgRelationIds;
    }

    public void setServicePkgRelationIds(String[] servicePkgRelationIds) {
        this.servicePkgRelationIds = servicePkgRelationIds;
    }


    public List<RnCPackage> getRncMonetaryAddOnList() {
        return rncMonetaryAddOnList;
    }

    public void setRncMonetaryAddOnList(List<RnCPackage> rncMonetaryAddOnList) {
        this.rncMonetaryAddOnList = rncMonetaryAddOnList;
    }

    public List<RnCPackage> getRncNonMonetaryAddOnList() {
        return rncNonMonetaryAddOnList;
    }

    public void setRncNonMonetaryAddOnList(List<RnCPackage> rncNonMonetaryAddOnList) {
        this.rncNonMonetaryAddOnList = rncNonMonetaryAddOnList;
    }

    public List<ProductOffer> getAddOnProductOfferForSuggestions() {
        return addOnProductOfferForSuggestions;
    }

    public void setAddOnProductOfferForSuggestions(List<ProductOffer> addOnProductOfferForSuggestions) {
        this.addOnProductOfferForSuggestions = addOnProductOfferForSuggestions;
    }

    public String getAutoSubscriptionsAsJson() {
        return autoSubscriptionsAsJson;
    }

    public void setAutoSubscriptionsAsJson(String autoSubscriptionsAsJson) {
        if(StringUtils.isNotEmpty(autoSubscriptionsAsJson)) {
            this.autoSubscriptionsAsJson = autoSubscriptionsAsJson.replaceAll("\\\\\"", "\\\\\\\\\\\\\"");
        } else {
            this.autoSubscriptionsAsJson = autoSubscriptionsAsJson;
        }

    }

    public String[] getAutoSubscriptionRelIds() {
        return autoSubscriptionRelIds;
    }

    public void setAutoSubscriptionRelIds(String[] autoSubscriptionRelIds) {
        this.autoSubscriptionRelIds = autoSubscriptionRelIds;
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

    private static class ProductSpecServicePkgRelPredicate implements Predicate<ProductOfferServicePkgRelData> {

        private List<ProductOfferServicePkgRelData> productOfferServicePkgRelDataList;

        private ProductSpecServicePkgRelPredicate(@Nonnull List<ProductOfferServicePkgRelData> productOfferServicePkgRelDataList) {
            this.productOfferServicePkgRelDataList = productOfferServicePkgRelDataList;
        }

        @Override
        public boolean apply(ProductOfferServicePkgRelData productOfferServicePkgRelData) {
            return productOfferServicePkgRelDataList.contains(productOfferServicePkgRelData);
        }


    }

    public String getProductOfferServicePkgRelListAsJson() {
        return productOfferServicePkgRelListAsJson;
    }

    public void setProductOfferServicePkgRelListAsJson(String productOfferServicePkgRelListAsJson) {
        this.productOfferServicePkgRelListAsJson = productOfferServicePkgRelListAsJson;
    }

    public List<String> findAssociatedAutoSubscriptionAddOns(ProductOfferData productOffer) {
        try {
            DetachedCriteria areaData = DetachedCriteria.forClass(ProductOfferAutoSubscriptionRelData.class);
            areaData.add(Restrictions.eq("addOnProductOfferData", productOffer));
            areaData.setProjection(Projections.property("parentProductOfferData"));
            return CRUDOperationUtil.<ProductOfferData>findAllByDetachedCriteria(areaData)
                    .stream().map(ProductOfferData::getName).collect(Collectors.toList());
        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while fetching associated of product offer " + productOffer.getName() +"("+ productOffer.getId() +") .Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
        }
        return null;
    }

    public String getAdvanceConditionAsJson() {
        Gson gson = GsonFactory.defaultInstance();
        List<PCRFKeyConstants> pcrfKeyConstants = PCRFKeyConstants.values(PCRFKeyType.RULE);
        String[] autoSuggestion = new String[pcrfKeyConstants.size()];
        short index = 0;
        for (PCRFKeyConstants keyConstants : pcrfKeyConstants) {
            autoSuggestion[index] = keyConstants.getVal();
            index++;
        }
        return gson.toJson(autoSuggestion);
    }

    public List<ProductOfferAutoSubscriptionRelData> getProductOfferAutoSubscriptionList() {
        return productOfferAutoSubscriptionList;
    }

    public void setProductOfferAutoSubscriptionList(List<ProductOfferAutoSubscriptionRelData> productOfferAutoSubscriptionList) {
        this.productOfferAutoSubscriptionList = productOfferAutoSubscriptionList;
    }

    public boolean getCurrencyUpdateAllowed(){
        return isCurrencyUpdateAllowed;
    }
}

