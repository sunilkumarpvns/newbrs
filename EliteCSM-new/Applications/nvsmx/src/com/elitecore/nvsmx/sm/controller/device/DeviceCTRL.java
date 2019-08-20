package com.elitecore.nvsmx.sm.controller.device;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.util.csv.CSVData;
import com.elitecore.core.util.csv.CSVRecordData;
import com.elitecore.core.util.csv.EliteCSVUtility;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.device.DeviceData;
import com.elitecore.corenetvertex.sm.device.DeviceDataField;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.criterion.Restrictions;

import javax.servlet.http.HttpServletResponse;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.ERROR;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * Created by vikas on 21/8/17.
 */

@ParentPackage(value = "sm")
@Namespace("/sm/device")
@Results({
        @Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = {"actionName", "device"}),
        @Result(name = ERROR, type = RestGenericCTRL.REDIRECT_ACTION, params = {"actionName", "device"}),

})
public class DeviceCTRL extends RestGenericCTRL<DeviceData> implements ServletResponseAware {

    private static final String EXPORT_DEVICE = "exportDevice_";
    private File uploadedFile;
    private String uploadedFileContentType;
    private String uploadedFileFileName;
    private HttpServletResponse response;

    @Override
    public ACLModules getModule() {
        return ACLModules.DEVICE;
    }

    @Override
    public DeviceData createModel() {
        return new DeviceData();
    }

    @SkipValidation
    public HttpHeaders upload() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called upload()");
        }
        try {

            String fileExtension = uploadedFileFileName.substring(uploadedFileFileName.lastIndexOf(CommonConstants.DOT) + 1);
            if (CommonConstants.CSV_TYPE.equals(fileExtension) == false) {
                return redirectError("Invalid file type");
            }

            CSVData csvData = EliteCSVUtility.parseCSVExt(new FileInputStream(uploadedFile), ",");
            if (csvData == null) {
                return redirectError("Error while uploading File");
            }

            List<Reason> reasons = uploadDeviceDatas(csvData);
            Gson gson = GsonFactory.defaultInstance();
            JsonArray uploadDataResultJson = gson.toJsonTree(reasons, new TypeToken<List<Reason>>() {
            }.getType()).getAsJsonArray();
            getRequest().setAttribute("uploadStatus", uploadDataResultJson);

        } catch (Exception e) {
            getLogger().error(getLogModule(), "Error while uploading " + getModule().getDisplayLabel() + " information.Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            addActionError("Error while performing Upload Operation");
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
        }

        setActionChainUrl(getRedirectURL(com.elitecore.nvsmx.system.constants.Results.UPLOAD_REPORT.getValue()));
        return new DefaultHttpHeaders(com.elitecore.nvsmx.system.constants.Results.REDIRECT_UPLOAD_REPORT.getValue());
    }

    @SkipValidation
    public HttpHeaders export() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called export()");
        }

        BufferedWriter writer = null;
        PrintWriter out = null;

        try {
            if (isImportExportOperationInProgress()) {
                addActionError(ACLModules.DEVICE.getDisplayLabel() + " " + getText(ActionMessageKeys.EXPORT_FAILURE.key));
                addActionError(getText("device.importexport.operation"));
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
            }
            String[] deviceIds = getRequest().getParameterValues("ids");
            makeImportExportOperationInProgress(true);

            if (Arrayz.isNullOrEmpty(deviceIds)) {
                addActionError(ACLModules.DEVICE.getDisplayLabel() + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
                addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
                makeImportExportOperationInProgress(false);
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
            }

            List<DeviceData> deviceDatas = CRUDOperationUtil.getAllByIds(DeviceData.class, deviceIds);
            response.setContentType("application/ms-excel");
            String fileName = EXPORT_DEVICE + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + CommonConstants.DOT + CommonConstants.CSV_TYPE;
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            out = response.getWriter();
            writer = new BufferedWriter(out);

            writer.write(getCSVHeader());
            for (DeviceData tacDetailData : deviceDatas) {
                writer.write(tacDetailData.generateCSVData());
            }
            writer.flush();

            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "All the Device Data are exported successfully");
            }
            addActionMessage("Device Data are exported successfully");
            makeImportExportOperationInProgress(false);

        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while fetching Device data for export operation. Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
        makeImportExportOperationInProgress(false);
        return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }

    @SkipValidation
    public HttpHeaders exportAll() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called exportAll()");
        }

        BufferedWriter writer = null;
        PrintWriter out = null;

        try {
            if (isImportExportOperationInProgress() == true) {
                addActionError(ACLModules.DEVICE.getDisplayLabel() + " " + getText(ActionMessageKeys.EXPORT_FAILURE.key));
                addActionError(getText("device.importexport.operation"));
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
            }

            makeImportExportOperationInProgress(true);
            List<DeviceData> deviceDatas = CRUDOperationUtil.findAll(DeviceData.class);
            if (Collectionz.isNullOrEmpty(deviceDatas)) {
                addActionError(ACLModules.DEVICE.getDisplayLabel() + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
                addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
                makeImportExportOperationInProgress(false);
                return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
            }

            response.setContentType("application/ms-excel");
            String fileName = EXPORT_DEVICE + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + CommonConstants.DOT + CommonConstants.CSV_TYPE;
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            out = response.getWriter();
            writer = new BufferedWriter(out);

            writer.write(getCSVHeader());
            for (DeviceData tacDetailData : deviceDatas) {
                writer.write(tacDetailData.generateCSVData());
            }
            writer.flush();

            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "All the Device Data are exported successfully");
            }
            addActionMessage("Device Data are exported successfully");
            makeImportExportOperationInProgress(false);

        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while fetching Device data for export operation. Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INTERNAL_ERROR.code).disableCaching();
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
        makeImportExportOperationInProgress(false);
        return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code).disableCaching();
    }

    private boolean isImportExportOperationInProgress() {
        return (Boolean) getRequest().getServletContext().getAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING);
    }

    private void makeImportExportOperationInProgress(boolean inProgress) {
        getRequest().getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, inProgress);
    }

    private List<Reason> uploadDeviceDatas(CSVData csvData) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called uploadDeviceDatas()");
        }

        List<Reason> reasons = Collectionz.newArrayList();
        List<CSVRecordData> csvRecordList = csvData.getRecords();

        if (Collectionz.isNullOrEmpty(csvRecordList) == false) {
            StaffData staffData = getStaffData();
            for (CSVRecordData csvRecordData : csvRecordList) {
                reasons.add(validateAndUploadRecord(csvRecordData, staffData));
            }
        }

        return reasons;
    }

    private Reason validateAndUploadRecord(CSVRecordData csvRecordData, StaffData staffData) {

        DeviceData deviceData = new DeviceData();
        String[] fieldValues = csvRecordData.getRecord();
        Reason reason;

        String tacValue = fieldValues[DeviceDataField.TAC.getIndex()];
        if (Strings.isNullOrBlank(tacValue)) {
            reason = new Reason();
            reason.setMessages(NVSMXCommonConstants.ENTITY_IMPORT_FAIL);
            reason.setRemarks("TAC value is missing");
            return reason;
        }

        reason = new Reason(tacValue);

        List<String> failedReasons = validateRecord(deviceData, fieldValues);

        if (Collectionz.isNullOrEmpty(failedReasons) == false) {
            reason.setMessages(NVSMXCommonConstants.ENTITY_IMPORT_FAIL);
            reason.setRemarks(getRemarksFromSubReasons(failedReasons));
        } else {
            saveOrUpdateDeviceData(deviceData, staffData, reason);
            reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
        }

        return reason;
    }

    private List<String> validateRecord(DeviceData deviceData, String[] fieldValues) {

        List<String> failedReasons = new ArrayList<>();
        Arrays.stream(DeviceDataField.values()).limit(fieldValues.length).forEach(field -> {
            String fieldValue = fieldValues[field.getIndex()];
            if (field.validate(fieldValue, failedReasons)) {
                field.setValue(deviceData, fieldValue);
            }
        });

        return failedReasons;
    }

    private HttpHeaders redirectError(String message) {
        getLogger().error(getLogModule(), message);
        addActionError(message);
        setActionChainUrl(getRedirectURL(METHOD_INDEX));
        return new DefaultHttpHeaders(ERROR).withStatus(ResultCode.INVALID_INPUT_PARAMETER.code).disableCaching();
    }

    private static String getRemarksFromSubReasons(List<String> subReasons) {
        StringBuilder sb = new StringBuilder();
        if (Collectionz.isNullOrEmpty(subReasons)) {
            sb.append(" ---- ");
        } else {
            for (String str : subReasons) {
                sb.append(str);
                sb.append("<br/>");
            }
        }
        return sb.toString();
    }

    private String getCSVHeader() {
        StringWriter stringWriter = new StringWriter();
        PrintWriter out = new PrintWriter(stringWriter);
        out.print(FieldValueConstants.TAC + CommonConstants.COMMA);
        out.print(FieldValueConstants.BRAND + CommonConstants.COMMA);
        out.print(FieldValueConstants.DEVICE_MODEL + CommonConstants.COMMA);
        out.print(FieldValueConstants.HARDWARE_TYPE + CommonConstants.COMMA);
        out.print(FieldValueConstants.OS + CommonConstants.COMMA);
        out.print(FieldValueConstants.YEAR + CommonConstants.COMMA);
        out.print(FieldValueConstants.ADDITIONAL_INFO + CommonConstants.COMMA);
        out.println();

        return stringWriter.toString();
    }


    public File getUploadedFile() {
        return uploadedFile;
    }

    public void setUploadedFile(File uploadedFile) {
        this.uploadedFile = uploadedFile;
    }

    public String getUploadedFileContentType() {
        return uploadedFileContentType;
    }

    public void setUploadedFileContentType(String uploadedFileContentType) {
        this.uploadedFileContentType = uploadedFileContentType;
    }

    public String getUploadedFileFileName() {
        return uploadedFileFileName;
    }

    public void setUploadedFileFileName(String uploadedFileFileName) {
        this.uploadedFileFileName = uploadedFileFileName;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;
    }

    @Override
    public void validate() {
        DeviceData deviceData = (DeviceData) getModel();
        boolean isAlreadyExist = isDuplicateEntity("tac",deviceData.getTac() ,getMethodName());
        if(isAlreadyExist){
            addFieldError("tac","TAC already exists");
        }

    }
	
    private void saveOrUpdateDeviceData(DeviceData deviceData, StaffData staffData, Reason reason){

        try {

            DeviceData existingDeviceData = null;
            List<DeviceData> lst = CRUDOperationUtil.findAll(DeviceData.class, Restrictions.eq("tac", deviceData.getTac()));
            if(Collectionz.isNullOrEmpty(lst) == false){
                existingDeviceData =  lst.get(0);
            }

            if (existingDeviceData == null) {
                deviceData.setCreatedDateAndStaff(staffData);
                CRUDOperationUtil.save(deviceData);
                reason.setMessages(NVSMXCommonConstants.IMPORT_ENTITY_CREATED);
            } else {
                existingDeviceData.setModifiedDateAndStaff(staffData);
                existingDeviceData.setBrand(deviceData.getBrand());
                existingDeviceData.setAdditionalInformation(deviceData.getAdditionalInformation());
                existingDeviceData.setYear(deviceData.getYear());
                existingDeviceData.setOs(deviceData.getOs());
                existingDeviceData.setHardwareType(deviceData.getHardwareType());
                existingDeviceData.setDeviceModel(deviceData.getDeviceModel());
                CRUDOperationUtil.update(existingDeviceData);
                reason.setMessages(NVSMXCommonConstants.IMPORT_ENTITY_UPDATED);
            }

        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while uploading device data: " + deviceData.getName());
            LogManager.getLogger().trace(getLogModule(), e);
            String errorMessage = "Failed to upload device data due to internal error. Kindly refer logs for further details";
            List<String> errors = new ArrayList<String>();
            errors.add(errorMessage);
            reason.setMessages(NVSMXCommonConstants.ENTITY_IMPORT_FAIL);
            reason.addFailReason(errors);
        }
    }
}