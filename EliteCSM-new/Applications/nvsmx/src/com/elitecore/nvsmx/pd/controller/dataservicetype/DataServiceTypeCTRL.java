package com.elitecore.nvsmx.pd.controller.dataservicetype;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.pd.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pd.dataservicetype.DefaultServiceDataFlowData;
import com.elitecore.corenetvertex.pd.dataservicetype.SDFFlowAccess;
import com.elitecore.corenetvertex.pd.dataservicetype.SDFProtocols;
import com.elitecore.corenetvertex.pd.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeContainer;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeDataExt;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.pd.controller.util.ImportExportCTRL;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportEntityAccumulator;
import com.elitecore.nvsmx.policydesigner.model.pkg.dataservicetype.DataServiceTypeDAO;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
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
import org.hibernate.criterion.Order;

import javax.xml.bind.JAXBException;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static com.elitecore.commons.logging.LogManager.getLogger;
import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * @author jaidiptrivedi
 */
@ParentPackage(value = "pd")
@Namespace("/pd/dataservicetype")
@Results({
        @Result(name = SUCCESS, type = "redirectAction", params = {"actionName", "data-service-type"}),
})
public class DataServiceTypeCTRL extends ImportExportCTRL<DataServiceTypeData> {

    private static final String EXPORT_DATA_SERVICE_TYPE = "exportDataServiceType_";
    private List<RatingGroupData> allRatingGroups = Collectionz.newArrayList();

    private static final Predicate<DefaultServiceDataFlowData> DEFAULT_SERVICE_DATA_FLOW_DATA_FILTER = serviceDataFlowData -> {
        if (serviceDataFlowData == null) {
            return false;
        }
        if (Strings.isNullOrBlank(serviceDataFlowData.getFlowAccess())
                && Strings.isNullOrBlank(serviceDataFlowData.getProtocol())
                && Strings.isNullOrBlank(serviceDataFlowData.getSourceIP())
                && Strings.isNullOrBlank(serviceDataFlowData.getSourcePort())
                && Strings.isNullOrBlank(serviceDataFlowData.getDestinationIP())
                && Strings.isNullOrBlank(serviceDataFlowData.getDestinationPort())) {
            return false;
        }
        return true;
    };

    /*
    This predicate is used to remove the DataServiceTypeData have null name from the list of dataServiceTypeDataExts.
    */
    private final Predicate<DataServiceTypeDataExt> NULL_NAME_PREDICATE = dataServiceTypeDataExt -> {
        if (Strings.isNullOrBlank(dataServiceTypeDataExt.getName())) {
            LogManager.getLogger().info(getLogModule(), "Found DataServiceTypeData with null name. Skipping Import process for DataServiceTypeData: " + dataServiceTypeDataExt);
            return false;
        }
        return true;
    };

    @Override
    public ACLModules getModule() {
        return ACLModules.DATASERVICETYPE;
    }

    @Override
    public DataServiceTypeData createModel() {
        return new DataServiceTypeData();
    }

    @Override
    public HttpHeaders create() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called create()");
        }
        setDefaultSDF();

        return super.create();
    }

    @Override
    public HttpHeaders update() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called update()");
        }
        setDefaultSDF();

        return super.update();
    }

    @Override
    public boolean prepareAndValidateDestroy(DataServiceTypeData dataServiceTypeData) {

        try {
            String attachedPackages = DataServiceTypeDAO.AttachedWithPackages(dataServiceTypeData.getId());
            if (Strings.isNullOrBlank(attachedPackages) == false) {
                addActionError("Unable to delete '" + dataServiceTypeData.getName() + "'. <br/>Reason :  " + ACLModules.DATASERVICETYPE.getDisplayLabel() + " is configured with one or more " + ACLModules.DATAPKG.getDisplayLabel());
                LogManager.getLogger().error(getLogModule(), "Unable to delete " + dataServiceTypeData.getName() + " DataServiceType is configured with packages: " + attachedPackages);
                return false;
            }

            String attachedPccRules = DataServiceTypeDAO.getAttachPccRules(dataServiceTypeData.getId());
            if (Strings.isNullOrBlank(attachedPccRules) == false) {
                addActionError("Unable to delete '" + dataServiceTypeData.getName() + "'. <br/>Reason :  " + ACLModules.DATASERVICETYPE.getDisplayLabel() + " is configured with one or more " + ACLModules.GLOBALPCCRULE.getDisplayLabel());
                LogManager.getLogger().error(getLogModule(), "Unable to delete " + dataServiceTypeData.getName() + " DataServiceType is configured with global pcc rules:  " + attachedPccRules);
                return false;
            }

            String attachedChargingRuleBaseName = DataServiceTypeDAO.getAttachChargingRuleBaseName(dataServiceTypeData.getId());
            if (Strings.isNullOrBlank(attachedChargingRuleBaseName) == false) {
                addActionError("Unable to delete '" + dataServiceTypeData.getName() + "'. <br/>Reason :  " + ACLModules.DATASERVICETYPE.getDisplayLabel() + " is configured with one or more " + ACLModules.CHARGINGRULEBASENAME.getDisplayLabel());
                LogManager.getLogger().error(getLogModule(), "Unable to delete " + dataServiceTypeData.getName() + " DataServiceType is configured with charging rule base name:  " + attachedChargingRuleBaseName);
                return false;
            }

        } catch (Exception e) {
            addActionError("Can not perform delete operation. Reason:" + e.getMessage());
            getLogger().error(getLogModule(), "Error while " + getModule().getDisplayLabel() + " for id " + dataServiceTypeData.getId() + " . Reason: " + e.getMessage());
            getLogger().trace(getLogModule(), e);
            return false;
        }

        dataServiceTypeData.getRatingGroupDatas().clear();
        return true;
    }

    @SkipValidation
    public HttpHeaders export() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called export()");
        }
        String[] serviceTypeIds = getRequest().getParameterValues("ids");
        String fileName = EXPORT_DATA_SERVICE_TYPE + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + XML;
        BufferedWriter writer = null;
        PrintWriter out = null;
        try {
            Map<String, String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();

            if (serviceTypeIds == null) {
                addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
                addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
                makeImportExportOperationInProgress(false);
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            if (isImportExportOperationInProgress()) {
                addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.EXPORT_FAILURE.key));
                addActionError(getText("dataservicetype.importexport.operation"));
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
            makeImportExportOperationInProgress(true);

            DataServiceTypeContainer dataServiceTypeContainer = new DataServiceTypeContainer();
            List<DataServiceTypeDataExt> serviceTypeDatas = CRUDOperationUtil.getAllByIds(DataServiceTypeDataExt.class, serviceTypeIds);
            for (DataServiceTypeDataExt dataServiceType : serviceTypeDatas) {

                if (Strings.isNullOrBlank(dataServiceType.getGroups())) {
                    dataServiceType.setGroups(CommonConstants.DEFAULT_GROUP);
                }
                importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, dataServiceType);
            }

            dataServiceTypeContainer.setserviceType(serviceTypeDatas);

            StringWriter stringWriter = new StringWriter();
            ConfigUtil.serialize(stringWriter, DataServiceTypeContainer.class, dataServiceTypeContainer);

            String serviceTypeInfo = stringWriter.toString();
            if (Strings.isNullOrBlank(serviceTypeInfo)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(getLogModule(), "Can not find content for Data Service Type.");
                }
                throw new Exception("Can not find data as string for Data Service Type");
            }
            getResponse().setContentType("text/xml");
            getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            out = getResponse().getWriter();
            writer = new BufferedWriter(out);
            writer.write(serviceTypeInfo, 0, serviceTypeInfo.length());
            writer.flush();

            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "All Data Service Types are exported successfully");
            }

            addActionMessage("Data Service Type are exported successfully");
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);

        } catch (IllegalArgumentException e) {
            LogManager.getLogger().error(getLogModule(), "Error while fetching Data Service Type data for export operation. Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while fetching Data Service Type data for export operation. Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
    }

    @SkipValidation
    public HttpHeaders exportAll() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called exportAll()");
        }

        String fileName = EXPORT_DATA_SERVICE_TYPE + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + XML;
        BufferedWriter writer = null;
        PrintWriter out = null;
        try {

            if (isImportExportOperationInProgress()) {
                addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.EXPORT_FAILURE.key));
                addActionError(getText("dataservicetype.importexport.operation"));
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            Map<String, String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
            makeImportExportOperationInProgress(true);
            DataServiceTypeContainer dataServiceTypeContainer = new DataServiceTypeContainer();
            List<DataServiceTypeDataExt> dataServiceTypeDatas = CRUDOperationUtil.get(DataServiceTypeDataExt.class, Order.asc("name"));

            if (Collectionz.isNullOrEmpty(dataServiceTypeDatas)) {
                LogManager.getLogger().warn(getLogModule(), "No Data Service Type Data found for Export ALL operation");
            }
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "Export All operation started for data packages");
            }

            List<DataServiceTypeDataExt> dataServiceTypeToBeExported = new ArrayList<DataServiceTypeDataExt>();
            for (DataServiceTypeDataExt dataServiceTypeData : dataServiceTypeDatas) {
                dataServiceTypeToBeExported.add(dataServiceTypeData);
                importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, dataServiceTypeData);
            }

            if (Collectionz.isNullOrEmpty(dataServiceTypeToBeExported)) {
                LogManager.getLogger().warn(getLogModule(), "No Data Service Type found for Export ALL operation");
            }

            dataServiceTypeContainer.setserviceType(dataServiceTypeToBeExported);

            StringWriter stringWriter = new StringWriter();
            ConfigUtil.serialize(stringWriter, DataServiceTypeContainer.class, dataServiceTypeContainer);
            String serviceTypeInfo = stringWriter.toString();
            if (Strings.isNullOrBlank(serviceTypeInfo)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(getLogModule(), "Can not find content for Data Service Type.");
                }
                throw new Exception("Can not find data as string for the Data Service Type");
            }
            getResponse().setContentType("text/xml");
            getResponse().setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            out = getResponse().getWriter();
            writer = new BufferedWriter(out);
            writer.write(serviceTypeInfo, 0, serviceTypeInfo.length());
            writer.flush();
            makeImportExportOperationInProgress(false);
            LogManager.getLogger().debug(getLogModule(), "Export All operation ended for Data Service Type");
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);

        } catch (IllegalArgumentException e) {
            LogManager.getLogger().error(getLogModule(), "Error while fetching Data Service Type data for export operation. Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while fetching Data Service Type data for export operation. Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
    }

    private List<Reason> importDataServices(List<DataServiceTypeDataExt> dataServiceTypeDatasForImport, String action) {

        List<Reason> reasons = Collectionz.newArrayList();
        if (Collectionz.isNullOrEmpty(dataServiceTypeDatasForImport)) {
            return reasons;
        }
        for (DataServiceTypeDataExt importService : dataServiceTypeDatasForImport) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "Import Operation started for data service type: " + importService.getName());
            }
            Reason reason = new Reason(importService.getName());
            try {
                setStaffInfo(importService);
                boolean isExistGroup = true;
                List<String> groups = new ArrayList<>();
                if (Strings.isNullOrBlank(importService.getGroupNames())) {
                    importService.setGroups(CommonConstants.DEFAULT_GROUP_ID);
                    groups.add(CommonConstants.DEFAULT_GROUP_ID);
                } else {
                    isExistGroup = importExportUtil.getGroupIdsFromName(importService, reason, groups);
                }
                if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {

                    /**
                     * this will import services based on staff belonging groups
                     */
                    if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
                        importService.setGroups(Strings.join(",", groups));
                        importExportUtil.validateAndImportInformation(importService, action, reason);
                        if (LogManager.getLogger().isDebugLogLevel()) {
                            LogManager.getLogger().debug(getLogModule(), "Import Operation finished for data service type: " + importService.getName());
                        }
                    }
                }
                reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
                reasons.add(reason);

            } catch (Exception e) {
                LogManager.getLogger().error(getLogModule(), "Error while importing data service type data: " + importService.getName());
                LogManager.getLogger().trace(getLogModule(), e);
                String errorMessage = "Failed to import data service type due to internal error. Kindly refer logs for further details";
                List<String> errors = new ArrayList<String>(2);
                errors.add(errorMessage);
                reason.setMessages("FAIL");
                reason.addFailReason(errors);
                reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
                reasons.add(reason);
            }
        }

        reasons.sort(Comparator.comparing(Reason::getMessages));
        return reasons;
    }


    @SkipValidation
    public String importDataServiceType() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called importDataServiceType()");
        }
        if (isImportExportOperationInProgress()) {
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
            addActionError(getText("dataservicetype.importexport.operation"));
            return com.elitecore.nvsmx.system.constants.Results.LIST.getValue();

        }
        if (getImportedFile() == null) {
            LogManager.getLogger().error(getLogModule(), "Error while importing Data Service Type data. Reason: File not found");
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            return com.elitecore.nvsmx.system.constants.Results.LIST.getValue();
        }
        try {
            if (TEXT_XML.equals(getImportedFileContentType()) == false) {
                LogManager.getLogger().error(getLogModule(), "Error while importing data service type data. Reason: Invalid File type is configured. Only XML_FILE_EXT File is supported for importing service type");
                addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
                addActionError("Invalid File type for import");
                makeImportExportOperationInProgress(false);
                return com.elitecore.nvsmx.system.constants.Results.LIST.getValue();
            }
            DataServiceTypeContainer dataServiceTypeContainer = ConfigUtil.deserialize(getImportedFile(), DataServiceTypeContainer.class);
            if (dataServiceTypeContainer == null) {
                return com.elitecore.nvsmx.system.constants.Results.REDIRECT_ERROR.getValue();
            }
            List<DataServiceTypeDataExt> serviceTypes = dataServiceTypeContainer.getserviceType();
            serviceTypes.stream().filter(NULL_NAME_PREDICATE).collect(Collectors.toList());
            getRequest().getSession().setAttribute("serviceTypes", serviceTypes);

            Gson gson = GsonFactory.defaultInstance();
            JsonArray importDataServiceTypeJson = gson.toJsonTree(serviceTypes, new TypeToken<List<DataServiceTypeDataExt>>() {
            }.getType()).getAsJsonArray();
            getRequest().setAttribute("importedDataServiceTypes", importDataServiceTypeJson);
            setActionChainUrl(getRedirectURL("import"));

            return "redirectURL";

        } catch (IOException e) {
            LogManager.getLogger().error(getLogModule(), "Error while importing Data Service Type data. Reason: File not found");
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            makeImportExportOperationInProgress(false);
            return com.elitecore.nvsmx.system.constants.Results.LIST.getValue();
        } catch (JAXBException e) {
            LogManager.getLogger().error(getLogModule(), "Error while importing Data Service Type data due to XML_FILE_EXT processing failure. Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
            addActionError(getText(ActionMessageKeys.IMPORT_FAILURE.key));
            makeImportExportOperationInProgress(false);
            return com.elitecore.nvsmx.system.constants.Results.LIST.getValue();
        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while importing Data Service Type data. Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
            addActionError(getText(ActionMessageKeys.IMPORT_FAILURE.key));
            makeImportExportOperationInProgress(false);
            return com.elitecore.nvsmx.system.constants.Results.LIST.getValue();
        }
    }


    @SkipValidation
    public String importData() {
        LogManager.getLogger().debug(getLogModule(), "Method called importData()");
        try {
            String selectedServiceTypeIndexes = getRequest().getParameter(Attributes.SELECTED_INDEXES);
            String action = getRequest().getParameter(Attributes.USER_ACTION);

            if (isImportExportOperationInProgress() == true) {
                addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
                addActionError(getText("dataservicetype.importexport.operation"));
                return com.elitecore.nvsmx.system.constants.Results.LIST.getValue();
            }

            List<DataServiceTypeDataExt> serviceTypes = (List<DataServiceTypeDataExt>) getRequest().getSession().getAttribute(Attributes.SERVICE_TYPES);
            List<DataServiceTypeDataExt> dataServiceTypeDataExts = Collectionz.newArrayList();

            if (Strings.isNullOrBlank(selectedServiceTypeIndexes) == false && Collectionz.isNullOrEmpty(serviceTypes) == false) {
                makeImportExportOperationInProgress(true);
                dataServiceTypeDataExts = new ImportEntityAccumulator<>(serviceTypes, selectedServiceTypeIndexes).get();
            }

            List<Reason> reasons = importDataServices(dataServiceTypeDataExts, action);
            Gson gson = GsonFactory.defaultInstance();
            JsonArray importServiceTypeResultJson = gson.toJsonTree(reasons, new TypeToken<List<Reason>>() {
            }.getType()).getAsJsonArray();
            getRequest().setAttribute("importStatus", importServiceTypeResultJson);
            makeImportExportOperationInProgress(false);

        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while importing Data Service Type data");
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.DATA_SERVICE_TYPE + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
            addActionError(getText(ActionMessageKeys.IMPORT_FAILURE.key));
            makeImportExportOperationInProgress(false);
            return com.elitecore.nvsmx.system.constants.Results.LIST.getValue();
        }

        setActionChainUrl(NVSMXCommonConstants.IMPORT_STATUS_REPORT);
        return NVSMXCommonConstants.REDIRECT_URL;
    }


    private String getRemarksFromSubReasons(List<String> subReasons) {
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

    private void setDefaultSDF() {
        DataServiceTypeData dataServiceTypeData = (DataServiceTypeData) getModel();
        dataServiceTypeData.getDefaultServiceDataFlows().stream().filter(DEFAULT_SERVICE_DATA_FLOW_DATA_FILTER).collect(Collectors.toList());
        for (DefaultServiceDataFlowData defaultServiceDataFlowData : dataServiceTypeData.getDefaultServiceDataFlows()) {
            defaultServiceDataFlowData.setDataServiceTypeData(dataServiceTypeData);
        }
    }

    @Override
    public String authorize() throws Exception {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called authorize()");
        }
        StaffData staffData = (StaffData) getRequest().getSession().getAttribute(Attributes.STAFF_DATA);
        if (staffData != null) {
            if (isAdminUser(staffData)) {
                return SUCCESS;
            }
        }
        addActionError("Only admin can create / modify Data Service Type");
        return INPUT;
    }

    @Override
    public void validate() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called validate()");
        }

        DataServiceTypeData dataServiceTypeData = (DataServiceTypeData) getModel();
        if (dataServiceTypeData != null) {
            if (Collectionz.isNullOrEmpty(dataServiceTypeData.getRatingGroupDatas()) == false) {
                validateRatingGroups(dataServiceTypeData);
            }
            if (Collectionz.isNullOrEmpty(dataServiceTypeData.getDefaultServiceDataFlows()) == false) {
                validateSDF(dataServiceTypeData.getDefaultServiceDataFlows());
            }
        }

        super.validate();
    }

    private void validateRatingGroups(DataServiceTypeData dataServiceTypeData) {

        List<RatingGroupData> ratingGroupDataListFromDB = Collectionz.newArrayList();
        int i = 0;
        for (RatingGroupData ratingGroupData : dataServiceTypeData.getRatingGroupDatas()) {
            if (ratingGroupData == null) {
                addFieldError("ratingGroupDatas[" + i + "]", getText("ratinggroup.invalid"));
            } else {
                RatingGroupData ratingGroupDataFromDb = CRUDOperationUtil.get(RatingGroupData.class, ratingGroupData.getId());
                if (ratingGroupDataFromDb == null) {
                    addFieldError("ratingGroupDatas[" + i + "]", getText("ratinggroup.invalid"));
                } else {
                    ratingGroupDataListFromDB.add(ratingGroupDataFromDb);
                }
            }
            i++;
        }
        dataServiceTypeData.setRatingGroupDatas(ratingGroupDataListFromDB);
    }

    private void validateSDF(List<DefaultServiceDataFlowData> defaultServiceDataFlowDataList) {

        int i = 0;
        for (DefaultServiceDataFlowData defaultServiceDataFlowData : defaultServiceDataFlowDataList) {
            if (SDFFlowAccess.fromVal(defaultServiceDataFlowData.getFlowAccess()) == null) {
                addFieldError("defaultServiceDataFlows[" + i + "].flowAccess", getText("flowaccess.invalid"));
            }
            if (SDFProtocols.fromVal(defaultServiceDataFlowData.getProtocol()) == null) {
                addFieldError("defaultServiceDataFlows[" + i + "].protocol", getText("protocol.invalid"));
            }
            i++;
        }
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setAllRatingGroups(CRUDOperationUtil.findAll(RatingGroupData.class));
    }

    public List<RatingGroupData> getAllRatingGroups() {
        return allRatingGroups;
    }

    public void setAllRatingGroups(List<RatingGroupData> allRatingGroups) {
        this.allRatingGroups = allRatingGroups;
    }

}
