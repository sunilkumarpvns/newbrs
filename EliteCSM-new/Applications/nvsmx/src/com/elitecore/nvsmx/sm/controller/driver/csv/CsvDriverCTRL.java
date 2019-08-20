package com.elitecore.nvsmx.sm.controller.driver.csv;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicates;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DriverType;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.driver.DriverData;
import com.elitecore.corenetvertex.sm.driver.constants.FileTransferProtocol;
import com.elitecore.corenetvertex.sm.driver.constants.TimeUnits;
import com.elitecore.corenetvertex.sm.driver.csv.CsvDriverData;
import com.elitecore.corenetvertex.sm.driver.csv.CsvDriverFieldMappingData;
import com.elitecore.corenetvertex.sm.driver.csv.CsvDriverStripMappingData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.util.PasswordUtility;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Restrictions;
import org.hibernate.criterion.SimpleExpression;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * @author dhyani.raval
 */
@ParentPackage(value = "sm")
@Namespace("/sm/driver")
@Results({
        @Result(name= SUCCESS, type="redirectAction",params = {"actionName","csv-driver"}),
})
public class CsvDriverCTRL extends RestGenericCTRL<DriverData> {

    private static final int MAXLENGTH_PCRFKEY = 255;
    private static final int MAXLENGTH_SEPERATOR = 1;
    private static final int MAXLENGTH_PATTERN = 8;
    private static final int MAXLENGTH_HEADERFIELD = 255;
    private static final String RESTFIELD_PCRF_KEY = "pcrfKey";
    private static final String RESTFIELD_PATTERN = "pattern";
    private static final String RESTFIELD_SEPARATOR = "separator";
    private static final String DISPLAY_PROPERTY_PCRF_KEY = "csv.mapping.pcrf.key";
    private static final String DISPLAY_PROPERTY_PATTERN = "csv.strip.mapping.pattern";
    private static final String DISPLAY_PROPERTY_SEPARATOR = "csv.strip.mapping.separator";
    private static final String RESTFIELD_HEADERFIELD = "headerField";
    private static final String DISPLAY_PROPERTY_HEADERFIELD = "csv.field.mapping.header";

    private List<CsvDriverFieldMappingData> csvDriverFieldMappingDataList = Collectionz.newArrayList();

    @Override
    protected String getRedirectURL(String method) {
        StringBuilder sb = new StringBuilder();
        sb.append(getModule().getComponent().getUrl()).append(CommonConstants.FORWARD_SLASH)
                .append(getModule().getActionURL()[0]).append(CommonConstants.FORWARD_SLASH).append(method);
        return sb.toString();
    }

    @Override
    @SkipValidation
    public HttpHeaders index() {
        if(LogManager.getLogger().isDebugLogLevel()){
            LogManager.getLogger().debug(getLogModule(),"called index() method");
        }
        List<DriverData> driverDatas = CRUDOperationUtil.findAll(DriverData.class);
        List<DriverData> csvDriverDatas = Collectionz.newArrayList();
        driverDatas.stream().forEach(driverData -> {
            if(DriverType.CSV_DRIVER.name().equals(driverData.getDriverType())) {
                csvDriverDatas.add(driverData);
            }
        });
        setList(csvDriverDatas);
        setActionChainUrl("sm/driver/driver/index");
        getRequest().setAttribute(NVSMXCommonConstants.TYPE, DriverType.CSV_DRIVER.name());

        return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL).disableCaching();
    }

    @Override
    public HttpHeaders create() {
        DriverData driverData = (DriverData) getModel();
        driverData.setDriverType(DriverType.CSV_DRIVER.name());
        driverData.getCsvDriverData().setDriverData(driverData);
        driverData.setDbCdrDriverData(null);

        try {
            String password = driverData.getCsvDriverData().getPassword();
            if(Strings.isNullOrBlank(password) == false) {
                driverData.getCsvDriverData().setPassword(PasswordUtility.getEncryptedPassword(password));
            }
        } catch (Exception e) {
            getLogger().error(getLogModule(),"Error while encrypt password for "+ getModule().getDisplayLabel() +". Reason: "+e.getMessage());
            getLogger().trace(getLogModule(),e);
            addActionError("Error while encrypt password "+ getModule().getDisplayLabel());
        }
        Collectionz.filter(driverData.getCsvDriverData().getCsvDriverFieldMappingDataList(), Predicates.nonNull());
        Collectionz.filter(driverData.getCsvDriverData().getCsvDriverStripMappingDataList(), Predicates.nonNull());
        setOrderNoForFieldMapping(driverData.getCsvDriverData().getCsvDriverFieldMappingDataList());
        return super.create();
    }

    @Override
    public HttpHeaders update() {
        DriverData driverData = (DriverData) getModel();
        if(Strings.isNullOrBlank(driverData.getId())) {
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
        driverData.setDbCdrDriverData(null);
        driverData.setDriverType(DriverType.CSV_DRIVER.name());
        driverData.getCsvDriverData().setDriverData(driverData);
        DriverData driverDataTemp = CRUDOperationUtil.get(DriverData.class, driverData.getId());
        if(driverDataTemp == null) {
            addActionError(getModule().getDisplayLabel()+" Not Found with id: " + driverData.getId());
            getLogger().error(getLogModule(),"Error while updating "+getModule().getDisplayLabel()+" with id: "+ driverData.getId()+". Reason: Not found");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
        }
        driverData.getCsvDriverData().setPassword(driverDataTemp.getCsvDriverData().getPassword());//password can be update from CsvDriverPasswordCTRL
        driverData.getCsvDriverData().setAllocatingProtocol(driverDataTemp.getCsvDriverData().getAllocatingProtocol()); //Allocating protocol can not be update
        Collectionz.filter(driverData.getCsvDriverData().getCsvDriverFieldMappingDataList(), Predicates.nonNull());
        Collectionz.filter(driverData.getCsvDriverData().getCsvDriverStripMappingDataList(), Predicates.nonNull());
        setOrderNoForFieldMapping(driverData.getCsvDriverData().getCsvDriverFieldMappingDataList());
        return super.update();
    }

    @Override
    public ACLModules getModule() {
        return ACLModules.DRIVER;
    }

    @Override
    public DriverData createModel() {
        return new DriverData();
    }

    private void setOrderNoForFieldMapping(List<CsvDriverFieldMappingData> csvDriverFieldMappingDataList) {
        int i = 1;
        for(CsvDriverFieldMappingData csvDriverFieldMappingData : csvDriverFieldMappingDataList) {
            csvDriverFieldMappingData.setOrderNo(i);
            i++;
        }
    }


    public String getCsvFieldMappingAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        DriverData driverData = (DriverData) getModel();
        return gson.toJsonTree(driverData.getCsvDriverData().getCsvDriverFieldMappingDataList(), new TypeToken<List<DriverData>>() {}.getType()).getAsJsonArray().toString();

    }

    public String getCsvStripMappingAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        DriverData driverData = (DriverData) getModel();
        return gson.toJsonTree(driverData.getCsvDriverData().getCsvDriverStripMappingDataList(), new TypeToken<List<DriverData>>() {}.getType()).getAsJsonArray().toString();
    }

    public String getCsvDriverDataAsJson(){
        Gson gson = GsonFactory.defaultInstance();
        JsonArray modelJson = gson.toJsonTree(getList(),new TypeToken<List<DriverData>>() {}.getType()).getAsJsonArray();
        return modelJson.toString();
    }

    @Override
    protected SimpleExpression getAdditionalCriteria() {
        return Restrictions.eq("driverType", DriverType.CSV_DRIVER.name());
    }

    @SkipValidation
    public HttpHeaders initManageOrder(){
        if(getLogger().isDebugLogLevel()){
            getLogger().debug(getLogModule(),"Method called initManageOrder()");
        }
        try{

            DriverData driverData = (DriverData) getModel();
            CsvDriverData csvDriverData = CRUDOperationUtil.get(CsvDriverData.class, driverData.getId());
            setCsvDriverFieldMappingDataList(csvDriverData.getCsvDriverFieldMappingDataList());
            setActionChainUrl(getRedirectURL("field-mapping-manageorder"));
            Collections.sort(csvDriverFieldMappingDataList,new SortFieldMapping());
            driverData.setId(csvDriverData.getDriverData().getId());
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

            DriverData driverData = (DriverData) getModel();
            driverData = CRUDOperationUtil.get(DriverData.class,driverData.getId());
            String[] csvDriverFieldMappingDataIds = getRequest().getParameterValues("csvDriverFieldMappingDataIds");
            int index = 1;
            List<CsvDriverFieldMappingData> csvDriverFieldMappingDatas = Collectionz.newArrayList();
            for(String id: csvDriverFieldMappingDataIds){
                CsvDriverFieldMappingData csvDriverFieldMappingData = CRUDOperationUtil.get(CsvDriverFieldMappingData.class, id);
                int oldOrderNumber = csvDriverFieldMappingData.getOrderNo();

                csvDriverFieldMappingData.setOrderNo(index);
                int newOrderNumber = csvDriverFieldMappingData.getOrderNo();

                csvDriverFieldMappingDatas.add(csvDriverFieldMappingData);
                CRUDOperationUtil.update(csvDriverFieldMappingData);

                if (getLogger().isDebugLogLevel()) {
                    getLogger().debug(getLogModule(), "CSV Driver's Field Mapping order changed to  '" + newOrderNumber + "' from old OrderNumber " + oldOrderNumber);
                }
                index++;
            }
            String message = getModule().getDisplayLabel() +"  "+NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG + driverData.getResourceName() +  NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG  + " Updated";
            CRUDOperationUtil.audit(driverData, driverData.getResourceName(), AuditActions.UPDATE, getStaffData(), getRequest().getRemoteAddr(), driverData.getHierarchy(), message);
            setActionChainUrl(getRedirectURL("../../../csv-driver/"+driverData.getId()));
            addActionMessage("Field Mapping order changed successfully");
            return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_ACTION.getValue());
        }catch(Exception ex){
            getLogger().error(getLogModule(),"Failed to manage order of Field Mapping. Reason: "+ex.getMessage());
            getLogger().trace(getLogModule(),ex);
            return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);
        }
    }

    public List<CsvDriverFieldMappingData> getCsvDriverFieldMappingDataList() {
        return csvDriverFieldMappingDataList;
    }

    public void setCsvDriverFieldMappingDataList(List<CsvDriverFieldMappingData> csvDriverFieldMappingDataList) {
        this.csvDriverFieldMappingDataList = csvDriverFieldMappingDataList;
    }

    @Override
    public HttpHeaders show() {
        DriverData driverData = new DriverData();
        try{
            super.show();
            if(getModel() == null) {
                //Action error message will be came from parent
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.NOT_FOUND.code);
            }
            driverData = (DriverData) getModel();
            Collections.sort(driverData.getCsvDriverData().getCsvDriverFieldMappingDataList(),new SortFieldMapping());
            setActionChainUrl(getRedirectURL(METHOD_SHOW));
            return new DefaultHttpHeaders(NVSMXCommonConstants.REDIRECT_URL);
        } catch (Exception e) {
            addActionError("Fail to view " + getModule().getDisplayLabel() + " for id " + driverData.getId());
            getLogger().error(getLogModule(), "Error while viewing " + getModule().getDisplayLabel() + " for id " + driverData.getId() + " . Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
        }
        return new DefaultHttpHeaders(METHOD_INDEX).disableCaching().withStatus(ResultCode.INTERNAL_ERROR.code);

    }

    private class SortFieldMapping implements Comparator {

        @Override
        public int compare(Object o1, Object o2) {
            CsvDriverFieldMappingData q1 = (CsvDriverFieldMappingData) o1;
            CsvDriverFieldMappingData q2 = (CsvDriverFieldMappingData) o2;
            if(q1.getOrderNo()<q2.getOrderNo()) {
                return -1;
            } else if(q1.getOrderNo()>q2.getOrderNo()){
                return 1;
            }
            return 0;
        }
    }

    @Override
    public void validate() {
        DriverData driverData =(DriverData) getModel();
        CsvDriverData csvDriverData = driverData.getCsvDriverData();

        if(FileTransferProtocol.LOCAL.name().equals(csvDriverData.getAllocatingProtocol()) && Strings.isNullOrBlank(csvDriverData.getPassword()) == false) {
            addFieldError("password","When allocation protocol is LOCAL then there is no use of password. Please remove it.");
        }

        if(csvDriverData.getTimeBoundary() != null && TimeUnits.fromKey(Strings.valueOf(csvDriverData.getTimeBoundary())) == null) {
            addFieldError("timeBoundary",getText("invalid.field.value"));
        }

        checkSequenceRange(csvDriverData.getSequenceRange());
        validateFieldMappings(csvDriverData.getCsvDriverFieldMappingDataList());
        validateStripMappings(csvDriverData.getCsvDriverStripMappingDataList());
        if(Strings.isNullOrBlank(csvDriverData.getUserName()) == false && csvDriverData.getUserName().length() > 64){
                addFieldError("username",getText("validate.max.length", new String[]{"username", "64"}));
        }
        super.validate();
    }

    private void checkSequenceRange(String sequenceRange) {
        if(Strings.isNullOrBlank(sequenceRange) == false){
            if(sequenceRange.contains("-") == true){
                sequenceRange=sequenceRange.replaceAll(" ", "");
                String[] tokens = sequenceRange.split("-");
                if(tokens[0].matches("[0-9]+") == false || tokens[1].matches("[0-9]+") == false) {
                    addFieldError("sequenceRange", getText("invalid.field.value"));
                }
            } else {
                addFieldError("sequenceRange",getText("invalid.field.value"));
            }
        }
    }

    private void validateFieldMappings(List<CsvDriverFieldMappingData> csvDriverFieldMappingDataList) {
        if (csvDriverFieldMappingDataList.isEmpty()) {
            return;
        }

        Set<String> headerSet = new HashSet<>();
        Set<String> pcrfKeySet = new HashSet<>();


        for (CsvDriverFieldMappingData data : csvDriverFieldMappingDataList) {
            if (data == null) {
                continue;
            }
            validateRequiredField(data.getHeaderField(), RESTFIELD_HEADERFIELD, DISPLAY_PROPERTY_HEADERFIELD);
            validateRequiredField(data.getPcrfKey(), RESTFIELD_PCRF_KEY, DISPLAY_PROPERTY_PCRF_KEY);
            validateMaxLength(data.getHeaderField(), RESTFIELD_HEADERFIELD, DISPLAY_PROPERTY_HEADERFIELD, MAXLENGTH_HEADERFIELD);
            validateMaxLength(data.getPcrfKey(), RESTFIELD_PCRF_KEY, DISPLAY_PROPERTY_PCRF_KEY, MAXLENGTH_PCRFKEY);

            if(headerSet.add(data.getHeaderField().toLowerCase()) == false){
                addFieldError(RESTFIELD_HEADERFIELD,"Duplicate Mapping for Header Field '"+data.getHeaderField() +"' in Field Mapping");
            }

            if(pcrfKeySet.add(data.getPcrfKey().toLowerCase()) == false){
                addFieldError(RESTFIELD_PCRF_KEY,"Duplicate Mapping for PCRF Key '"+data.getPcrfKey() +"' in Field Mapping");
            }
        }
    }

    private void validateStripMappings(List<CsvDriverStripMappingData> csvDriverStripMappingDataList) {
        if (csvDriverStripMappingDataList.isEmpty()) {
            return;
        }

        Set<String> pcrfKeys = new HashSet<>();

        for (CsvDriverStripMappingData data : csvDriverStripMappingDataList) {
            if (data == null) {
                continue;
            }
            validateRequiredField(data.getPcrfKey(), RESTFIELD_PCRF_KEY, DISPLAY_PROPERTY_PCRF_KEY);
            validateRequiredField(data.getPattern(), RESTFIELD_PATTERN, DISPLAY_PROPERTY_PATTERN);
            validateRequiredField(data.getSeparator(), RESTFIELD_SEPARATOR, DISPLAY_PROPERTY_SEPARATOR);
            validateMaxLength(data.getPcrfKey(), RESTFIELD_PCRF_KEY, DISPLAY_PROPERTY_PCRF_KEY, MAXLENGTH_PCRFKEY);
            validateMaxLength(data.getPattern(), RESTFIELD_PATTERN, DISPLAY_PROPERTY_PATTERN, MAXLENGTH_PATTERN);
            validateMaxLength(data.getSeparator(), RESTFIELD_SEPARATOR, DISPLAY_PROPERTY_SEPARATOR, MAXLENGTH_SEPERATOR);

            if(pcrfKeys.add(data.getPcrfKey().toLowerCase()) == false){
                addFieldError(RESTFIELD_PCRF_KEY,"Duplicate Mapping for PCRF Key '"+data.getPcrfKey() +"' in Strip Attribute");
            }
        }
    }
}
