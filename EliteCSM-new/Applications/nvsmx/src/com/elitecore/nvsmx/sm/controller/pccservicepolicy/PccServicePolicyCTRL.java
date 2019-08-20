package com.elitecore.nvsmx.sm.controller.pccservicepolicy;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.DriverType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.UnknownUserAction;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.pkg.datapackage.Package;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateCardVersion;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.RateSlab;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.VersionDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.corenetvertex.sm.driver.constants.ReportingType;
import com.elitecore.corenetvertex.sm.gateway.DiameterGatewayData;
import com.elitecore.corenetvertex.sm.pccservicepolicy.PccServicePolicyData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang.StringUtils;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.Criteria;
import org.hibernate.criterion.Criterion;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Restrictions;

import javax.servlet.http.HttpServletResponse;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage( value = NVSMXCommonConstants.REST_PARENT_PKG_SM)
@Namespace("/sm/pccservicepolicy")
@Results({@Result(name=SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = {NVSMXCommonConstants.ACTION_NAME,"pcc-service-policy"})})
public class PccServicePolicyCTRL extends RestGenericCTRL<PccServicePolicyData> implements ServletResponseAware {

    private List<ProductOffer> productOffers = Collections.emptyList();
    private List<DiameterGatewayData> syGateways = Collections.emptyList();
    private List<DriverData> dbCdrDrivers = Collections.emptyList();
    private List<DriverData> policyCDRDrivers = new ArrayList<>();
    private List<PccServicePolicyData> list;
    private HttpServletResponse response;

    @SkipValidation
    public HttpHeaders initManageOrder(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called initManageOrder()");
        }
        try{
            list = CRUDOperationUtil.findAll(PccServicePolicyData.class,"orderNumber");
            setList(list);
            setActionChainUrl(getRedirectURL("manageorder"));
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

            String[] pccServicePoliciesIds = getRequest().getParameterValues("pccServicePoliciesIds");
            int index = 0;
            for(String id: pccServicePoliciesIds){
                PccServicePolicyData pccServicePolicyData = CRUDOperationUtil.get(PccServicePolicyData.class,id);
                JsonObject oldJsonObject = pccServicePolicyData.toJson();
                int oldOrderNumber = pccServicePolicyData.getOrderNumber();

                pccServicePolicyData.setOrderNumber(++index);
                int newOrderNumber = pccServicePolicyData.getOrderNumber();
                JsonObject newJsonObject = pccServicePolicyData.toJson();

                CRUDOperationUtil.update(pccServicePolicyData);

                JsonArray difference = ObjectDiffer.diff(oldJsonObject, newJsonObject);
                String message = Discriminators.PACKAGE + " <b><i>" + pccServicePolicyData.getName() + "</i></b> " + "Updated";
                CRUDOperationUtil.audit(pccServicePolicyData, pccServicePolicyData.getName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference, pccServicePolicyData.getHierarchy(), message);
                if( getLogger().isDebugLogLevel() ){
                    getLogger().debug(getLogModule(), "PCC Service Policy OrderNumber changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
            }

            setActionChainUrl(getRedirectURL("../../../pcc-service-policy"));
            addActionMessage("PCC Service Policies Order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage Order of PCC Service Policies. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    @Override
    public HttpHeaders create(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called create()");
        }

        try{

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            Integer totalRecords = CRUDOperationUtil.getMaxValueForProperty(PccServicePolicyData.class, "orderNumber");
            if(totalRecords==null){
                totalRecords = 0;
            }

            PccServicePolicyData pccServicePolicyData = (PccServicePolicyData) getModel();
            pccServicePolicyData.setOrderNumber(++totalRecords);
            setModel(pccServicePolicyData);

            return super.create();
        }catch(Exception ex){
            addActionError("Failed to create PCC Service Policy");
            getLogger().error(getLogModule(),"Error while creating PCC Service Policy. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    @Override
    public HttpHeaders update(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called update()");
        }

        try{

            String result = authorize();
            if (result.equals(SUCCESS) == false) {
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            PccServicePolicyData pccServicePolicyData = (PccServicePolicyData) getModel();
            //check pcc service policy with id exists or not
            if (isEntityExists(pccServicePolicyData.getId()) == false) {
                getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ pccServicePolicyData.getId()+". Reason: Not found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }

            PccServicePolicyData olddata = CRUDOperationUtil.get(PccServicePolicyData.class,pccServicePolicyData.getId());
            pccServicePolicyData.setOrderNumber(olddata.getOrderNumber());

            setModel(pccServicePolicyData);

            return super.update();
        }catch(Exception ex){
            addActionError("Failed to create PCC Service Policy");
            getLogger().error(getLogModule(),"Error while creating PCC Service Policy. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
        }
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
    }

    @Override
    public void validate() {

        PccServicePolicyData pccServicePolicyData = (PccServicePolicyData)getModel();
        boolean isValidData = true;
        DriverData policyCdrDriver = null;
        if(!Strings.isNullOrBlank(pccServicePolicyData.getPolicyCdrDriverId())) {
            policyCdrDriver = CRUDOperationUtil.get(DriverData.class, pccServicePolicyData.getPolicyCdrDriverId());
            if(policyCdrDriver==null){
                addFieldError("policyCdrDriverId","Policy CDR Driver does not exist for Id: "+pccServicePolicyData.getPolicyCdrDriverId());
                isValidData = false;
            }else {
                if(!(policyCdrDriver.getDriverType().equals(DriverType.DB_CDR_DRIVER.name()) || policyCdrDriver.getDriverType().equals(DriverType.CSV_DRIVER.name()))){
                    addFieldError("policyCdrDriverId","Invalid type Policy CDR Driver found for Id: "+pccServicePolicyData.getPolicyCdrDriverId());
                    isValidData = false;
                }
            }
        }

        if(!Strings.isNullOrBlank(pccServicePolicyData.getSyGatewayId())){
            DiameterGatewayData syGatewayData = CRUDOperationUtil.get(DiameterGatewayData.class, pccServicePolicyData.getSyGatewayId());
            if(syGatewayData == null){
                addFieldError("syGatewayId","Sy Gateway does not exist for Id: "+pccServicePolicyData.getSyGatewayId());
                isValidData = false;
            }else{
                String gatewayType = syGatewayData.getDiameterGatewayProfileData().getGatewayType();
                if(!"OCS".equals(gatewayType)){
                    addFieldError("syGatewayId","Invalid Gateway found with Id: "+pccServicePolicyData.getSyGatewayId());
                    isValidData = false;
                }
            }
        }

        DriverData chargingCdrDriver = null;
       if(Strings.isNullOrBlank(pccServicePolicyData.getChargingCdrDriverId()) == false){
            chargingCdrDriver = CRUDOperationUtil.get(DriverData.class, pccServicePolicyData.getChargingCdrDriverId());
            if(chargingCdrDriver==null){
                addFieldError("chargingCdrDriverId","Charging CDR Driver does not exist for Id: "+pccServicePolicyData.getChargingCdrDriverId());
                isValidData = false;
            }else {
                if(!(chargingCdrDriver.getDriverType().equals(DriverType.CSV_DRIVER.name()) || chargingCdrDriver.getCsvDriverData().getReportingType().equals(ReportingType.CHARGING_CDR.name()))){
                    addFieldError("chargingCdrDriverId","Invalid type Charging CDR Driver found for Id: "+pccServicePolicyData.getChargingCdrDriverId());
                    isValidData = false;
                }
            }
        }
        ProductOfferData productOfferData = null;
        if(pccServicePolicyData.getUnknownUserAction().equals(UnknownUserAction.ALLOW_UNKNOWN_USER.name())){
            if(Strings.isNullOrBlank(pccServicePolicyData.getUnknownUserPkgId())) {
                isValidData = false;
                addFieldError("unknownUserPkgId","Unknown User Offer is Required for Unknown User Action 'Allow Unknown User'");
            } else {
                productOfferData = CRUDOperationUtil.get(ProductOfferData.class, pccServicePolicyData.getUnknownUserPkgId());
                if (productOfferData == null) {
                    addFieldError("unknownUserPkgId","Unknown User Offer does not exist for Id: " + pccServicePolicyData.getUnknownUserPkgId());
                    isValidData = false;
                }else {
                    ProductOffer productOffer = PolicyManager.getInstance().getProductOffer().byId(pccServicePolicyData.getUnknownUserPkgId());
                    if (Objects.isNull(productOffer)) {
                        addFieldError("unknownUserPkgId", "Unknown User Offer does not exist for Id: " + pccServicePolicyData.getUnknownUserPkgId());
                        isValidData = false;
                    } else if (productOffer.getPolicyStatus() != PolicyStatus.SUCCESS
                            && productOffer.getPolicyStatus() != PolicyStatus.LAST_KNOWN_GOOD) {
                        addFieldError("unknownUserPkgId", "Unknown User Offer is in FAILURE / PARTIAL_SUCCESS state");
                        isValidData = false;
                    } else if (productOffer.getPackageMode().getOrder() < PkgMode.LIVE.getOrder()) {
                        addFieldError("unknownUserPkgId", "Only LIVE / LIVE2 Offers are configured with Unknown User");
                        isValidData = false;
                    } else if (productOffer.getDataServicePkgData() != null && productOffer.getDataServicePkgData().getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
                        isValidData = isFreeQuotaConfigure(productOffer);
                        if (isValidData == false) {
                            addFieldError("unknownUserPkgId", "Unknown User Offer contains Rate Configuration either in Quota Profile or Data Rate Card");
                        }
                    }
                }
            }
        }else{
            if(!Strings.isNullOrBlank(pccServicePolicyData.getUnknownUserPkgId())) {
                addFieldError("unknownUserPkgId","Unknown User Package is not allowed for Unknown User Action '"+pccServicePolicyData.getUnknownUserAction()+"'");
                isValidData = false;
            }else{
                pccServicePolicyData.setUnknownUserPkgId(null);
            }
        }

        if(!Strings.isNullOrBlank(pccServicePolicyData.getRuleset())){
            try{
                Compiler.getDefaultCompiler().parseLogicalExpression(pccServicePolicyData.getRuleset());
            }catch(Exception ex){
                getLogger().error(getLogModule(),"Error while parsing ruleset expression. Reason: "+ex.getMessage());
                getLogger().trace(getLogModule(),ex);
                isValidData = false;
                addFieldError("ruleset","Invalid expression in Ruleset");
            }
        }

        if(isValidData) {
            pccServicePolicyData.setUnknownUserProductOffer(productOfferData);
            pccServicePolicyData.setChargingCdrDriver(chargingCdrDriver);
            pccServicePolicyData.setPolicyCdrDriver(policyCdrDriver);
            if(!Strings.isNullOrBlank(pccServicePolicyData.getSyGatewayId())) {
                DiameterGatewayData syGatewayData = CRUDOperationUtil.get(DiameterGatewayData.class, pccServicePolicyData.getSyGatewayId());
                pccServicePolicyData.setSyGateway(syGatewayData);
            }
        }

        super.validate();
    }

    @Override
    @SkipValidation
    public void prepareValuesForSubClass() throws Exception{
        productOffers = getLiveActiveProductOffers();
        syGateways = getSyGatewaysOnly();
        dbCdrDrivers = getDbCdrDriversOnly();
        policyCDRDrivers = getPolicyCDRDriversList();
    }




    private List<DiameterGatewayData> getSyGatewaysOnly() {
        Criteria criteria = HibernateSessionFactory.getSession().createCriteria(DiameterGatewayData.class);
        criteria.createAlias("diameterGatewayProfileData", "profileData");
        criteria.add(Restrictions.eq("profileData.gatewayType", "OCS").ignoreCase());
        return criteria.list();
    }

    public List<DriverData> getDbCdrDrivers() {
        return dbCdrDrivers;
    }

    public List<ProductOffer> getProductOffers() {
        return productOffers;
    }

    public List<DiameterGatewayData> getSyGateways() {
        return syGateways;
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.PCC_SERVICE_POLICY;
    }

    @Override
    public PccServicePolicyData createModel() {
        return new PccServicePolicyData();
    }

    public List<DriverData> getDbCdrDriversOnly() {
        DetachedCriteria driverCriteria = DetachedCriteria.forClass(DriverData.class);
        Criterion c1 =  Restrictions.eq("driverType", DriverType.CSV_DRIVER.name()).ignoreCase();
        driverCriteria.createAlias("csvDriverData","csvDriver");
        Criterion c2 =  Restrictions.eq("csvDriver.reportingType", ReportingType.CHARGING_CDR.name()).ignoreCase();
        driverCriteria.add(c1);
        driverCriteria.add(c2);
        return CRUDOperationUtil.findAllByDetachedCriteria(driverCriteria);
    }

    private List<DriverData> getPolicyCDRDriversList() {
        DetachedCriteria driverCriteria = DetachedCriteria.forClass(DriverData.class);
        List<DriverData> driverDataList = CRUDOperationUtil.findAllByDetachedCriteria(driverCriteria);
        Collectionz.filter(driverDataList, driverData -> {
            if (DriverType.DB_CDR_DRIVER.name().equalsIgnoreCase(driverData.getDriverType())) {
                return true;
            }
            return ((DriverType.CSV_DRIVER.name().equalsIgnoreCase(driverData.getDriverType()))
                    && ReportingType.UM.name().equalsIgnoreCase(driverData.getCsvDriverData().getReportingType())) ? true : false;
        });
        return driverDataList;
    }

    public List<ProductOffer> getLiveActiveProductOffers() {
        List<ProductOffer> productOffers = PolicyManager.getInstance().getProductOffer().all()
                .stream()
                .filter(productOffer -> productOffer.getStatus() == PkgStatus.ACTIVE)
                .filter(productOffer -> productOffer.getPolicyStatus() == PolicyStatus.SUCCESS || productOffer.getPolicyStatus() == PolicyStatus.LAST_KNOWN_GOOD)
                .filter(productOffer -> productOffer.getType() == PkgType.BASE)
                .filter(productOffer -> productOffer.getPackageMode().getOrder() >= PkgMode.LIVE.getOrder())
                .collect(Collectors.toList());
        Collectionz.filter(productOffers, productOffer -> {
            if (productOffer.getDataServicePkgData() != null && productOffer.getDataServicePkgData().getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
                return isFreeQuotaConfigure(productOffer);
            }
            return true;
        });
        return productOffers;
    }

    private boolean isFreeQuotaConfigure(ProductOffer productOffer) {
        if (isRateConfiguredWithQuotaProfile(productOffer)) {
            return false;
        }
        if (isRateConfiguredWithDataRateCard(productOffer)) {
            return false;
        }
        return true;
    }

    private boolean isRateConfiguredWithQuotaProfile(ProductOffer productOffer) {
        List<QuotaProfile> quotaProfiles = productOffer.getDataServicePkgData().getQuotaProfiles();
        for (QuotaProfile quotaProfile : quotaProfiles) {
            boolean isRateConfigured = isRateConfiguredForQuota(quotaProfile.getServiceWiseQuotaProfileDetails(0));
            if (isRateConfigured) {
                return true;
            }
            isRateConfigured = isRateConfiguredForQuota(quotaProfile.getServiceWiseQuotaProfileDetails(1));
            if (isRateConfigured) {
                return true;
            }
            isRateConfigured = isRateConfiguredForQuota(quotaProfile.getServiceWiseQuotaProfileDetails(2));
            if (isRateConfigured) {
                return true;
            }
        }
        return false;
    }

    private boolean isRateConfiguredWithDataRateCard(ProductOffer productOffer) {
        List<DataRateCard> dataRateCards = ((Package)productOffer.getDataServicePkgData()).getDataRateCards();
        for (DataRateCard dataRateCard : dataRateCards) {
            for(RateCardVersion rateCardVersion : dataRateCard.getRateCardVersions()){
                for(VersionDetail versionDetail : rateCardVersion.getVersionDetails()){
                    if (isRateConfiguredWithRateSlab(versionDetail)) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    private boolean isRateConfiguredWithRateSlab(VersionDetail versionDetail) {
        for(RateSlab rateSlab : versionDetail.getSlabs()){
            if(rateSlab.isFree() == false){
                return true;
            }
        }
        return false;
    }

    private boolean isRateConfiguredForQuota(Map<String, QuotaProfileDetail> quotaProfileDetails) {
        if(Maps.isNullOrEmpty(quotaProfileDetails)) {
            return false;
        }
        for (QuotaProfileDetail quotaProfileDetail : quotaProfileDetails.values()) {
            RncProfileDetail rncProfile  = (RncProfileDetail) quotaProfileDetail;
            if (rncProfile.isRateConfigured()) {
                return true;
            }
        }
        return false;
    }

    @SkipValidation
    public void validateRuleset() throws Exception {
        if(getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called validateRuleset()");
        }
        String invalid = "INVALID";
        String valid = "VALID";
        PrintWriter writer = null;
        try {
            String rulesetValue = getRequest().getParameter("rulesetValue");
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(getLogModule(), "Ruleset: " + rulesetValue);
            }
            writer = response.getWriter();
            if (!Strings.isNullOrBlank(rulesetValue)) {
                Compiler.getDefaultCompiler().parseLogicalExpression(rulesetValue);
            }
            if (getLogger().isDebugLogLevel()) {
                getLogger().debug(getLogModule(), "Received ruleset is Valid");
            }
            writer.print(valid);
            writer.flush();
        } catch ( InvalidExpressionException ive){
            getLogger().error(getLogModule(),"Invalid Ruleset. Reason: "+ive.getMessage());
            getLogger().trace(getLogModule(),ive);
            writer.print(invalid);
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Error while validating ruleset. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            if(writer!=null) {
                writer.print(invalid);
            }
        }finally {
            Closeables.closeQuietly(writer);
        }
    }

    public List<DriverData> getPolicyCDRDrivers() {
        return policyCDRDrivers;
    }

    public void setPolicyCDRDrivers(List<DriverData> policyCDRDrivers) {
        this.policyCDRDrivers = policyCDRDrivers;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public String getDataListAsJson() {
        Gson gson = GsonFactory.defaultInstance();
        JsonArray modelJson = gson.toJsonTree(getModel(),new TypeToken<List<PccServicePolicyData>>() {}.getType()).getAsJsonArray();
        String pccServicePolicyAsJson = modelJson.toString();
        if(StringUtils.isEmpty(pccServicePolicyAsJson)){
            return pccServicePolicyAsJson;
        }
            return pccServicePolicyAsJson.replaceAll("\\\\\"", "\\\\\\\\\\\\\"");

    }
}