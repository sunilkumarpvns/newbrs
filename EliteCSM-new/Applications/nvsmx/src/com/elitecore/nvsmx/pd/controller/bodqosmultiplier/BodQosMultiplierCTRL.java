package com.elitecore.nvsmx.pd.controller.bodqosmultiplier;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.bod.BoDQosMultiplierData;
import com.elitecore.corenetvertex.pd.bod.BoDServiceMultiplierData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pm.PolicyManager;
import com.elitecore.corenetvertex.pm.bod.BoDQosMultiplier;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.elitecore.nvsmx.policydesigner.controller.util.ProductOfferUtility.doesBelongToGroup;
import static com.opensymphony.xwork2.Action.SUCCESS;

@ParentPackage(NVSMXCommonConstants.REST_PARENT_PKG_PD)
@Namespace("/pd/bodqosmultiplier")
@Results({
        @Result(name = SUCCESS,
                type = RestGenericCTRL.REDIRECT_ACTION,
                params = {NVSMXCommonConstants.ACTION_NAME, "bod-qos-multiplier"})
})
public class BodQosMultiplierCTRL extends RestGenericCTRL<BoDQosMultiplierData> {
    private String bodPackageId;
    private List<DataServiceTypeData> serviceTypeDatas = Collectionz.newArrayList();

    @Override
    @SkipValidation
    public String editNew() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called editNew()");
        }
        try {
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
    public void prepareValuesForSubClass() throws Exception{
        String bodPackageId = getRequest().getParameter(NVSMXCommonConstants.BOD_PACKAGE_ID);
        if (Strings.isNullOrBlank(bodPackageId) == false) {
            BoDQosMultiplierData bodQosMultiplier = (BoDQosMultiplierData) getModel();
            bodQosMultiplier.setBodPackageId(bodPackageId);
            setModel(bodQosMultiplier);
        }
        setServiceTypeDatas(CRUDOperationUtil.findAllWhichIsNotDeleted(DataServiceTypeData.class));
    }

    @Override
    public HttpHeaders create() {
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(), "Method called create()");
        }
        try{
            BoDQosMultiplierData boDQosMultiplierData = (BoDQosMultiplierData) getModel();
            BoDData bodDataFromDB = CRUDOperationUtil.get(BoDData.class, boDQosMultiplierData.getBodPackageId());
            boDQosMultiplierData.setBodData(bodDataFromDB);
            boDQosMultiplierData.setGroups(bodDataFromDB.getGroups());
            filterEmptyserviceMultipliers(boDQosMultiplierData.getBodServiceMultiplierDatas());
            setQosMultiplierInServiceMultiplier(boDQosMultiplierData);
            JsonObject jsonObjectNew = boDQosMultiplierData.toJson();
            JsonArray difference = ObjectDiffer.diff(new JsonObject(), jsonObjectNew);
            HttpHeaders headers = super.create();
            if(hasActionErrors()){
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
            }
            String message = Discriminators.BOD + " <b><i>" + boDQosMultiplierData.getBodData().getName() + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(boDQosMultiplierData.getBodData(),boDQosMultiplierData.getBodData().getName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference , boDQosMultiplierData.getBodData().getHierarchy(), message);
            setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
            return headers;
        } catch(Exception e){
            getLogger().error(getLogModule(),"Error while creating "+ getModule().getDisplayLabel() +" information. Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Fail to perform create Operation");
        }
        return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
    }

    private void filterEmptyserviceMultipliers(List<BoDServiceMultiplierData> serviceMultiplierDatas) {
        Collectionz.filter(serviceMultiplierDatas, new Predicate<BoDServiceMultiplierData>() {
            @Override
            public boolean apply(BoDServiceMultiplierData bodServiceMultiplierData) {
                if (bodServiceMultiplierData == null) {
                    return false;
                }
                if (Strings.isNullOrBlank(bodServiceMultiplierData.getServiceTypeData().getId()) && bodServiceMultiplierData.getMultiplier() == null) {
                    return false;
                }
                return true;
            }
        });
    }

    @Override
    public HttpHeaders update(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(), "Method called update()");
        }
        try{
            BoDQosMultiplierData boDQosMultiplierData = (BoDQosMultiplierData) getModel();
            BoDData bodDataFromDB = CRUDOperationUtil.get(BoDData.class, boDQosMultiplierData.getBodPackageId());

            if(bodDataFromDB == null){
                addActionError("BoD package does not exist with id "+boDQosMultiplierData.getBodPackageId());
                setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
            }

            if(PkgMode.getMode(bodDataFromDB.getPackageMode())==PkgMode.LIVE || PkgMode.getMode(bodDataFromDB.getPackageMode())==PkgMode.LIVE2){
                addActionError("BoD package is in "+bodDataFromDB.getPackageMode()+" mode.");
                setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
            }

            boDQosMultiplierData.setBodData(bodDataFromDB);
            boDQosMultiplierData.setGroups(bodDataFromDB.getGroups());
            BoDQosMultiplierData bodQosMultiplierDataInDb = CRUDOperationUtil.get(BoDQosMultiplierData.class, boDQosMultiplierData.getId());

            if(bodQosMultiplierDataInDb == null){
                addActionError("BoD multiplier does not exist with id "+boDQosMultiplierData.getId());
                setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
            }

            JsonObject jsonObjectOld = bodQosMultiplierDataInDb.toJson();
            filterEmptyserviceMultipliers(boDQosMultiplierData.getBodServiceMultiplierDatas());

                String errorMessage = setQosMultiplierInServiceMultiplier(boDQosMultiplierData);

            if(errorMessage!=null){
                addActionError(errorMessage);
                setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
            }

            boDQosMultiplierData.setFupLevel(bodQosMultiplierDataInDb.getFupLevel());

            HttpHeaders headers = super.update();
            JsonObject jsonObjectNew = boDQosMultiplierData.toJson();
            JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);
            String message = getModule().getDisplayLabel() + " <b><i>" + BalanceLevel.fromVal(bodQosMultiplierDataInDb.getFupLevel()) + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(boDQosMultiplierData.getBodData(),bodQosMultiplierDataInDb.getBodData().getName(),
                    AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference ,
                    boDQosMultiplierData.getBodData().getHierarchy(), message);
            setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
            return headers;
        } catch(Exception e){
            getLogger().error(getLogModule(),"Failed to update "+Discriminators.QOS_MULTIPLIER+". Reason: " + e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError(Discriminators.QOS_MULTIPLIER + " " +getText(ActionMessageKeys.UPDATE_FAILURE.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
        }
        return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
    }

    public String setQosMultiplierInServiceMultiplier(BoDQosMultiplierData boDQosMultiplierData){
        Set<String> serviceIdSet = new HashSet<>();
        for(BoDServiceMultiplierData bodServiceMultiplierData : boDQosMultiplierData.getBodServiceMultiplierDatas()){

            String serviceId = bodServiceMultiplierData.getServiceTypeData().getId();
            if(serviceIdSet.add(serviceId) == false){
                return "Duplicate mapping for Service Id "+serviceId;
            }

            Double multiplier = bodServiceMultiplierData.getMultiplier();
            if(multiplier == null){
                return "Multiplier is null for Service Id "+serviceId;
            }

            if (multiplier < 1 || multiplier > 10) {
                return "Multiplier value for Service Id "+serviceId+" must be between 1.00 and 10.00";
            }

            DataServiceTypeData dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class,serviceId);

            if (dataServiceTypeData == null) {
                return "Service does not exist with Id "+serviceId;
            }
            bodServiceMultiplierData.setServiceTypeData(dataServiceTypeData);
            bodServiceMultiplierData.setBodQosMultiplierData(boDQosMultiplierData);
        }

        return null;
    }

    public HttpHeaders destroy(){
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(getLogModule(), "Method called destroy()");
        }
        try {
            BoDQosMultiplierData boDQosMultiplierData = (BoDQosMultiplierData) getModel();
            BoDData bodDataFromDB = CRUDOperationUtil.get(BoDData.class, boDQosMultiplierData.getBodPackageId());

            if(bodDataFromDB == null){
                addActionError("BoD package does not exist with id "+boDQosMultiplierData.getBodPackageId());
                setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.NOT_FOUND.code).disableCaching();
            }

            if(PkgMode.getMode(bodDataFromDB.getPackageMode())==PkgMode.LIVE || PkgMode.getMode(bodDataFromDB.getPackageMode())==PkgMode.LIVE2){
                addActionError("BoD package is in "+bodDataFromDB.getPackageMode()+" mode.");
                setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
            }

            JsonObject jsonOldObject = bodDataFromDB.toJson();

            Iterator<BoDQosMultiplierData> boDQosMultiplierIterator= bodDataFromDB.getBodQosMultiplierDatas().iterator();
            BoDQosMultiplierData bodQosMultiplierDataInDb = null;
            while(boDQosMultiplierIterator.hasNext()){
                bodQosMultiplierDataInDb = boDQosMultiplierIterator.next();
                if(bodQosMultiplierDataInDb.getId().equals(boDQosMultiplierData.getId())){
                    if(bodQosMultiplierDataInDb.getFupLevel()!=bodDataFromDB.getBodQosMultiplierDatas().size()-1){
                        addActionError("Can not delete intermediate level QOS multiplier until higher level exists");
                        setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
                        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
                    }
                    //Removing object from list so that hibernate allows to delete it in super.destroy()
                    boDQosMultiplierIterator.remove();
                }
            }

            if(bodQosMultiplierDataInDb==null){
                addActionError("BoD package is in "+bodDataFromDB.getPackageMode()+" mode.");
                setActionChainUrl(getRedirectToParentURL(boDQosMultiplierData.getBodData().getId()));
                return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).withStatus(ResultCode.PRECONDITION_FAILED.code).disableCaching();
            }

            JsonObject jsonNewObject = bodDataFromDB.toJson();
            super.destroy();

            JsonArray difference = ObjectDiffer.diff(jsonOldObject, jsonNewObject);
            String message = getModule().getDisplayLabel() + " <b><i>" + BalanceLevel.fromVal(bodQosMultiplierDataInDb.getFupLevel()) + "</i></b> " + "Deleted";

            setActionChainUrl(getRedirectToParentURL(getRequest().getParameter(NVSMXCommonConstants.BOD_PACKAGE_ID)));
            CRUDOperationUtil.audit(bodDataFromDB,bodDataFromDB.getName(),
                    AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), difference ,
                    bodDataFromDB.getHierarchy(), message);
            return new DefaultHttpHeaders(REDIRECT_ACTION).withStatus(ResultCode.SUCCESS.code).disableCaching();
        } catch (Exception e) {
            getLogger().error(getLogModule(), "Failed to update " + Discriminators.QOS_MULTIPLIER + ". Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.QOS_MULTIPLIER + " " + getText(ActionMessageKeys.UPDATE_FAILURE.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
        }
        return new DefaultHttpHeaders(ERROR).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
    }

    @Override
    public void validate() {
        validateIdExistForCreateMode();
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.BODQOSMULTIPLIER;
    }

    @Override
    public BoDQosMultiplierData createModel() {
        return new BoDQosMultiplierData();
    }

    public String getBodPackageId() {
        return bodPackageId;
    }

    public void setBodPackageId(String bodPackageId) {
        this.bodPackageId = bodPackageId;
    }

    public List<DataServiceTypeData> getServiceTypeDatas() {
        return serviceTypeDatas;
    }

    public void setServiceTypeDatas(List<DataServiceTypeData> serviceTypeDatas) {
        this.serviceTypeDatas = serviceTypeDatas;
    }
}
