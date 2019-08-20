package com.elitecore.nvsmx.pd.controller.dataratecard;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion;
import com.elitecore.corenetvertex.pd.revenuedetail.RevenueDetailData;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.constants.OperationType;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionDetailData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionRelationData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.BigDecimalToStringGsonAdapter;
import com.elitecore.corenetvertex.util.commons.gson.adaptor.LongToStringGsonAdapter;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.collections.CollectionUtils;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * @author dhyani.raval
 */
@ParentPackage(value = "pd")
@Namespace("/pd/dataratecard")
@Results({ @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = { NVSMXCommonConstants.ACTION_NAME, "data-rate-card" }),

})
public class DataRateCardCTRL extends RestGenericCTRL<DataRateCardData> {

    private Map<String, JsonArray> rateCardVersionDetailMap = new HashMap<>();
    private String rateCardVersionDetailAsJson;
    private List<DataRateCardVersionDetailData> dataRateCardVersionDetailDataList = Collectionz.newArrayList();
    private List<Uom> uomListForPulse = Collectionz.newArrayList();
    private static final String REGEX_PATTERN = "^\\d{0,14}\\.*\\d{0,6}$";
    private BigDecimal bulkUpdateRate;
    private OperationType rateUnitOperation;
    private List<RevenueDetailData> revenueDetails;

    @Override
    public ACLModules getModule() {
        return ACLModules.DATARATECARD;
    }

    @Override
    public DataRateCardData createModel() {
        return new DataRateCardData();
    }


    @Override
    public String editNew() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called editNew()");
        }
        try {
            String pkgId = getRequest().getParameter(Attributes.PKG_ID);
            if (Strings.isNullOrBlank(pkgId) == false) {
                PkgData pkgData = CRUDOperationUtil.get(PkgData.class,pkgId);
                if(pkgData != null) {
                    DataRateCardData dataRateCardData = (DataRateCardData) getModel();
                    dataRateCardData.setPkgData(pkgData);
                    setModel(dataRateCardData);
                }
            }
            setActionChainUrl(getRedirectURL(METHOD_EDITNEW));
            return NVSMXCommonConstants.REDIRECT_URL;
        }catch (Exception e) {
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform create Operation");
            return ERROR;
        }

    }

    @Override
    public void prepare() {
        List<RevenueDetailData> revenueDetailDataList = CRUDOperationUtil.findAll(RevenueDetailData.class);
        setRevenueDetails(revenueDetailDataList);
    }

    @Override
    public String edit() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called edit()");
        }

        DataRateCardData dataRateCardData = (DataRateCardData) getModel();
        try {
            if(Strings.isNullOrBlank(dataRateCardData.getId()) == false) {
                dataRateCardData = CRUDOperationUtil.get(DataRateCardData.class, dataRateCardData.getId());
                String pkgId = getRequest().getParameter(Attributes.PKG_ID);
                if(Strings.isNullOrBlank(pkgId) == false) {
                    PkgData pkgData = new PkgData();
                    pkgData.setId(pkgId);
                    pkgData.setCurrency(dataRateCardData.getPkgData().getCurrency());
                    dataRateCardData.setPkgData(pkgData);
                }
                setModel(dataRateCardData);
            }
            setActionChainUrl(getRedirectURL(METHOD_EDIT));
            return NVSMXCommonConstants.REDIRECT_URL;
        }catch(Exception e){
            getLogger().error(getLogModule(),"Error while updating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform Update Operation");
            return ERROR;
        }
    }

    @Override
    public HttpHeaders create() {
        StringBuffer sb = new StringBuffer();
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called create()");
        }

        DataRateCardData dataRateCardData = (DataRateCardData) getModel();

        filterEmptyRows(dataRateCardVersionDetailDataList);
        if(Collectionz.isNullOrEmpty(dataRateCardVersionDetailDataList) ==false) {

            DataRateCardVersionRelationData dataRateCardVersionRelationData = new DataRateCardVersionRelationData();
            dataRateCardVersionRelationData.setVersionName("1");
            dataRateCardVersionRelationData.setEffectiveFromDate(new Timestamp(System.currentTimeMillis()));
            dataRateCardVersionRelationData.setDataRateCardData(dataRateCardData);


            for (DataRateCardVersionDetailData dataRateCardVersionDetailData : dataRateCardVersionDetailDataList) {
                dataRateCardVersionDetailData.setDataRateCardVersionRelationData(dataRateCardVersionRelationData);
                if(Strings.isNullOrBlank(dataRateCardVersionDetailData.getRevenueDetail().getId())){
                    dataRateCardVersionDetailData.setRevenueDetail(null);
                }
                dataRateCardVersionRelationData.getDataRateCardVersionDetailDataList().add(dataRateCardVersionDetailData);
            }

            dataRateCardData.getDataRateCardVersionRelationData().add(dataRateCardVersionRelationData);
        }

        HttpHeaders headers = super.create();
        if(hasActionErrors()){
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
        }
        setActionChainUrl(sb.append(CommonConstants.FORWARD_SLASH).append(NVSMXCommonConstants.ACTION_PKG_VIEW).append(CommonConstants.QUESTION_MARK)
                .append(Attributes.PKG_ID).append(CommonConstants.EQUAL).append(dataRateCardData.getPkgId()).toString());
        return headers;
    }

    private void filterEmptyRows(List<DataRateCardVersionDetailData> dataRateCardVersionDetailDatas){
        Collectionz.filter(dataRateCardVersionDetailDatas, dataRateCardVersionDetailData -> {
            if(dataRateCardVersionDetailData == null){
                return false;
            }
            return !(Strings.isNullOrBlank(dataRateCardVersionDetailData.getLabelKey1())
                    && Strings.isNullOrBlank(dataRateCardVersionDetailData.getLabelKey2())
                    && (dataRateCardVersionDetailData.getPulse1() == null)
                    && (dataRateCardVersionDetailData.getRate1() == null));
        });
    }


    @Override
    public HttpHeaders update() {
        StringBuffer sb = new StringBuffer();
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called update()");
        }
        try {

            DataRateCardData dataRateCardData = (DataRateCardData) getModel();
            if (Strings.isNullOrBlank(dataRateCardData.getId())) {
                addActionError(getModule().getDisplayLabel() + " Not Found with id: " + dataRateCardData.getId());
                setOldGroupsFromDB(Collectionz.newArrayList());
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code);
            }
            DataRateCardData dataRateCardDataDB = CRUDOperationUtil.get(DataRateCardData.class, dataRateCardData.getId());
            if (dataRateCardDataDB == null) {
                addActionError(getModule().getDisplayLabel() + " Not Found with id: " + dataRateCardDataDB.getId());
                setOldGroupsFromDB(Collectionz.newArrayList());
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }

            JsonObject oldJsonObject = dataRateCardDataDB.toJson();
            String result = authorize();
            if(result.equals(SUCCESS) == false){
                setActionChainUrl(getRedirectURL(METHOD_EDIT));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
         
            filterEmptyRows(dataRateCardVersionDetailDataList);

            if(Collectionz.isNullOrEmpty(dataRateCardVersionDetailDataList) ==false) {

                DataRateCardVersionRelationData dataRateCardVersionRelationData = dataRateCardData.getDataRateCardVersionRelationData().get(0);
                for (DataRateCardVersionDetailData dataRateCardVersionDetailData : dataRateCardVersionDetailDataList) {
                    dataRateCardVersionDetailData.setDataRateCardVersionRelationData(dataRateCardVersionRelationData);
                    if(Strings.isNullOrBlank(dataRateCardVersionDetailData.getRevenueDetail().getId())){
                        dataRateCardVersionDetailData.setRevenueDetail(null);
                    }
                    dataRateCardVersionRelationData.getDataRateCardVersionDetailDataList().add(dataRateCardVersionDetailData);
                }
                dataRateCardVersionRelationData.setDataRateCardData(dataRateCardData);
            }

            dataRateCardData.setModifiedDateAndStaff(getStaffData());
            dataRateCardData = (DataRateCardData) CRUDOperationUtil.merge(dataRateCardData);
            JsonObject newJsonObject = dataRateCardData.toJson();
            JsonArray diff = ObjectDiffer.diff(oldJsonObject, newJsonObject);
            String message = getModule().getDisplayLabel() + " <b><i>" + dataRateCardData.getResourceName() + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(dataRateCardData,dataRateCardData.getResourceName(), AuditActions.UPDATE,getStaffData(),getRequest().getRemoteAddr(),diff,dataRateCardData.getHierarchy(),message);
            addActionMessage(getModule().getDisplayLabel()+" updated successfully");
            setActionChainUrl(sb.append(CommonConstants.FORWARD_SLASH).append(NVSMXCommonConstants.ACTION_PKG_VIEW).append(CommonConstants.QUESTION_MARK)
                    .append(Attributes.PKG_ID).append(CommonConstants.EQUAL).append(dataRateCardData.getPkgId()).toString());
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
    public HttpHeaders destroy() {

        DataRateCardData dataRateCard = (DataRateCardData) getModel();

        try {
            String pkgId = getRequest().getParameter(Attributes.PKG_ID);
            if(Strings.isNullOrBlank(pkgId)) {
                setActionChainUrl("/policydesigner/pkg/Pkg/search");
            } else {
                setActionChainUrl("/policydesigner/pkg/Pkg/view?pkgId="+ pkgId);
            }
            super.destroy();
        } catch (Exception e) {
            getLogger().error(getLogModule(),"Error while fetching Data Rate Card data for delete operation. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
    }

    @Override
    public boolean prepareAndValidateDestroy(DataRateCardData dataRateCardData) {

        if(PkgMode.LIVE2.name().equalsIgnoreCase(dataRateCardData.getPkgData().getPackageMode()) || PkgMode.LIVE.name().equalsIgnoreCase(dataRateCardData.getPkgData().getPackageMode())){
            addActionError("Rate Card can't be deleted.Reason: "+dataRateCardData.getPkgData().getName()+" is in "+dataRateCardData.getPkgData().getPackageMode()+" Stage");
            return false;
        }

        if(Collectionz.isNullOrEmpty(dataRateCardData.getQoSProfiles()) == false) {
            for(QosProfileData qosProfileData : dataRateCardData.getQoSProfiles()){
                if(CommonConstants.STATUS_DELETED.equals(qosProfileData.getStatus()) == false) {
                    addActionError(ACLModules.DATARATECARD.getDisplayLabel() + " " + getText(ActionMessageKeys.DELETE_FAILURE.key));
                    addActionError("QoS Profile is Configured with '" + dataRateCardData.getName() + "' " + ACLModules.DATARATECARD.getDisplayLabel());
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public HttpHeaders show() {

        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called show()");
        }

        DataRateCardData dataRateCardData = (DataRateCardData) getModel();

        try {
            dataRateCardData = CRUDOperationUtil.get(DataRateCardData.class, dataRateCardData.getId(), getAdditionalCriteria());
            if (dataRateCardData == null) {
                addActionError(getModule().getDisplayLabel()+" Not Found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
            if (dataRateCardData.getGroups() != null) {
                String belongingsGroups = GroupDAO.getGroupNames(CommonConstants.COMMA_SPLITTER.split(dataRateCardData.getGroups()));
                dataRateCardData.setGroupNames(belongingsGroups);
            }
            if(Collectionz.isNullOrEmpty(dataRateCardData.getDataRateCardVersionRelationData()) ==false) {
                setDataRateCardInJsonMap(dataRateCardData.getDataRateCardVersionRelationData().get(0));
            }
            setModel(dataRateCardData);
        } catch (Exception e) {
            addActionError("Fail to view " + getModule().getDisplayLabel() + " for id ");
            getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + " . Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        setActionChainUrl(getRedirectURL(METHOD_SHOW));
        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);


    }

    @SkipValidation
    public HttpHeaders bulkUpdateDataRate(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called saveVersionConfiguration()");
        }
        getLogger().debug(getLogModule(),"rateUnitOperation = "  + rateUnitOperation);

        try {
            DataRateCardData dataRateCardData = (DataRateCardData) getModel();

            DataRateCardData dataRateCardDataDB = CRUDOperationUtil.get(DataRateCardData.class, dataRateCardData.getId());
            if(Strings.isNullOrBlank(dataRateCardDataDB.getId())){
                getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ dataRateCardData.getId()+". Reason: Not found");
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);

            }
            if(Arrayz.isNullOrEmpty(getIds())) {
                getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " with id: Reason: Version Configuration not selected");
                addActionError(getModule().getDisplayLabel()+" records not selected");
                setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(dataRateCardData.getId()));
                return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.INPUT_PARAMETER_MISSING.code).disableCaching();
            }
            List<String> bulkUpdateCheckList = Arrays.asList(getIds());
            if(Collectionz.isNullOrEmpty(bulkUpdateCheckList) == false) {

                DataRateCardVersionRelationData dataRateCardVersionRelationData = new DataRateCardVersionRelationData();
                List<DataRateCardVersionDetailData> dataRateCardVersionDetailDataDBList = Collectionz.newArrayList();
                if ((Collectionz.isNullOrEmpty(dataRateCardDataDB.getDataRateCardVersionRelationData())) == false){
                    dataRateCardVersionRelationData = dataRateCardDataDB.getDataRateCardVersionRelationData().get(0);
                    dataRateCardVersionDetailDataDBList = dataRateCardVersionRelationData.getDataRateCardVersionDetailDataList();
                }
                for (DataRateCardVersionDetailData dataRateCardVersionDetailData: dataRateCardVersionDetailDataDBList) {
                    if(bulkUpdateCheckList.contains(dataRateCardVersionDetailData.getId())){    // Set Rate
                        BigDecimal rate1 = dataRateCardVersionDetailData.getRate1();
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
                            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(dataRateCardDataDB.getId()));
                            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
                        }

                        if(newRate.doubleValue() > CommonConstants.MONETARY_VALUE_LIMIT ) {
                            getLogger().error(getLogModule(), "Error while updating " + getModule().getDisplayLabel() + " with id: Reason: Rate values exceeding maximum limit "+CommonConstants.MAX_MONETARY_VALUE);
                            addActionError("Fail to perform Update Operation");
                            addActionError("Reason: "+getModule().getDisplayLabel()+" updated Rate value should not be greater than "+ CommonConstants.MAX_MONETARY_VALUE);
                            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(dataRateCardDataDB.getId()));
                            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
                        }

                        dataRateCardVersionDetailData.setRate1(newRate);
                    }
                }
                dataRateCardDataDB.getDataRateCardVersionRelationData().add(dataRateCardVersionRelationData);
            }
            dataRateCardDataDB.setModifiedDateAndStaff(getStaffData());
            CRUDOperationUtil.merge(dataRateCardDataDB);
            String message = getModule().getDisplayLabel() + " <b><i>" + dataRateCardDataDB.getResourceName() + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(dataRateCardDataDB,dataRateCardDataDB.getResourceName(), AuditActions.UPDATE,getStaffData(),getRequest().getRemoteAddr(),dataRateCardDataDB.getHierarchy(),message);
            addActionMessage(getModule().getDisplayLabel()+" updated successfully");
            setActionChainUrl(CommonConstants.FORWARD_SLASH + getRedirectURL(dataRateCardData.getId()));
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

    private void setDataRateCardInJsonMap(DataRateCardVersionRelationData versionRelationData) {
        if(versionRelationData == null) {
            return;
        }
        Gson gson = new GsonBuilder().registerTypeAdapter(BigDecimal.class, new BigDecimalToStringGsonAdapter()).registerTypeAdapter(Long.class, new LongToStringGsonAdapter()).create();
        rateCardVersionDetailMap = new HashMap<>();

        JsonArray jsonArray = gson.toJsonTree(versionRelationData.getDataRateCardVersionDetailDataList(), new TypeToken<List<DataRateCardVersionDetailData>>() {}.getType()).getAsJsonArray();
        setRateCardVersionDetailAsJson(jsonArray.toString());
        rateCardVersionDetailMap.put(versionRelationData.getVersionName(),jsonArray);
    }

    public Map<String, JsonArray> getRateCardVersionDetailMap() {
        return rateCardVersionDetailMap;
    }

    public void setRateCardVersionDetailMap(Map<String, JsonArray> rateCardVersionDetailMap) {
        this.rateCardVersionDetailMap = rateCardVersionDetailMap;
    }

    public List<DataRateCardVersionDetailData> getDataRateCardVersionDetailDataList() {
        return dataRateCardVersionDetailDataList;
    }

    public void setDataRateCardVersionDetailDataList(List<DataRateCardVersionDetailData> dataRateCardVersionDetailDataList) {
        this.dataRateCardVersionDetailDataList = dataRateCardVersionDetailDataList;
    }


    public List<Uom> getUomListForPulse() {
        return uomListForPulse;
    }

    public void setUomListForPulse(List<Uom> uomListForPulse) {
        this.uomListForPulse = uomListForPulse;
    }


    @Override
    public void prepareValuesForSubClass() throws Exception {
        getUomListForPulse().addAll(Uom.getTimeUoms());
        getUomListForPulse().addAll(Uom.getVolumeUoms());
    }

    @Override
    protected boolean isDuplicateEntity(String propertyName, String value, String mode) {
     DataRateCardData dataRateCardData = (DataRateCardData) getModel();
     return CRUDOperationUtil.isDuplicateNameWithInParent(DataRateCardData.class, mode, dataRateCardData.getId(), value, dataRateCardData.getPkgData().getId(), "pkgData");
    }

    private void validatePackage(DataRateCardData rateCardData) {
        if(Strings.isNullOrEmpty(rateCardData.getPkgData().getId())){
            addFieldError("pkgId",getText("error.valueRequired"));
        }else{
            PkgData pkgData = CRUDOperationUtil.get(PkgData.class,rateCardData.getPkgData().getId());
            if(pkgData == null){
                addFieldError("pkgId","Pkg Data does not exists");
            }else if(PkgMode.LIVE.name().equalsIgnoreCase(pkgData.getPackageMode()) || PkgMode.LIVE2.name().equalsIgnoreCase(pkgData.getPackageMode())){
                addActionError("Live or Live2 Package does not allow create / update operation on Monetary Rate Card");
            }else{
                rateCardData.setPkgData(pkgData);
            }
        }
    }

    @Override
    public void validate() {
        DataRateCardData dataRateCardData = (DataRateCardData) getModel();
        validatePackage(dataRateCardData);
        validateRevenueDetail(dataRateCardData);
        super.validate();
    }

    private void validateRevenueDetail(DataRateCardData dataRateCardData) {
        List<RevenueDetailData> revenueDetailData = CRUDOperationUtil.findAll(RevenueDetailData.class);
        List<String> revenueCode = revenueDetailData.stream().map(RevenueDetailData::getName).collect(Collectors.toList());
        if(CollectionUtils.isEmpty(dataRateCardData.getDataRateCardVersionRelationData()) == false) {
            for (DataRateCardVersionRelationData dataRateCardVersionRelationData : dataRateCardData.getDataRateCardVersionRelationData()) {
                if(CollectionUtils.isNotEmpty(dataRateCardVersionRelationData.getDataRateCardVersionDetailDataList())) {
                    for (DataRateCardVersionDetailData dataRateCardVersionDetailData : dataRateCardVersionRelationData.getDataRateCardVersionDetailDataList()) {
                        if (Strings.isNullOrEmpty(dataRateCardVersionDetailData.getRevenueDetail().getName()) == false && revenueCode.contains(dataRateCardVersionDetailData.getRevenueDetail()) == false) {
                            addActionError("Revenue code : " + dataRateCardVersionDetailData.getRevenueDetail() + " not configured");
                        }
                    }
                }
            }
        }
    }


    private boolean isRateCardVersionDetailPresent(MonetaryRateCardData monetaryRateCardData) {
        for (MonetaryRateCardVersion rateCardVersionRelation : monetaryRateCardData.getMonetaryRateCardVersions()) {
            if (Collectionz.isNullOrEmpty(rateCardVersionRelation.getMonetaryRateCardVersionDetail())) {
                return true;
            }
        }
        return false;
    }

    public BigDecimal getBulkUpdateRate() {
        return bulkUpdateRate;
    }

    public void setBulkUpdateRate(BigDecimal bulkUpdateRate) {
        this.bulkUpdateRate = bulkUpdateRate;
    }

    public String getRateCardVersionDetailAsJson() {
        return rateCardVersionDetailAsJson;
    }

    public void setRateCardVersionDetailAsJson(String rateCardVersionDetailAsJson) {
        this.rateCardVersionDetailAsJson = rateCardVersionDetailAsJson;
    }

    public OperationType getRateUnitOperation() {
        return rateUnitOperation;
    }

    public void setRateUnitOperation(OperationType rateUnitOperation) {
        this.rateUnitOperation = rateUnitOperation;
    }

    public List<RevenueDetailData> getRevenueDetails() {
        return revenueDetails;
    }

    public void setRevenueDetails(List<RevenueDetailData> revenueDetails) {
        this.revenueDetails = revenueDetails;
    }
}
