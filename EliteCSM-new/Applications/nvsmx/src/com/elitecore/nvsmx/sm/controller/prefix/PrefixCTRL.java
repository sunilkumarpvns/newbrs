package com.elitecore.nvsmx.sm.controller.prefix;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.pd.prefix.PrefixData;
import com.elitecore.corenetvertex.pd.prefix.PrefixDataExt;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.sm.routing.network.CountryData;
import com.elitecore.corenetvertex.sm.routing.network.NetworkData;
import com.elitecore.corenetvertex.sm.routing.network.OperatorData;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.pd.controller.util.ImportExportCTRL;
import com.elitecore.nvsmx.pd.importexportutility.prefix.PrefixImportExportUtility;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportEntityAccumulator;
import com.elitecore.nvsmx.sm.controller.RestGenericCTRL;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import org.apache.struts2.convention.annotation.Namespace;
import org.apache.struts2.convention.annotation.ParentPackage;
import org.apache.struts2.convention.annotation.Result;
import org.apache.struts2.convention.annotation.Results;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.apache.struts2.rest.DefaultHttpHeaders;
import org.apache.struts2.rest.HttpHeaders;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.criterion.Order;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.*;

import static com.opensymphony.xwork2.Action.SUCCESS;

/**
 * @author saloni.shah
 */
@ParentPackage(value = "sm")
@Namespace("/sm/prefix")
@Results({@Result(name = SUCCESS, type = RestGenericCTRL.REDIRECT_ACTION, params = {NVSMXCommonConstants.ACTION_NAME, "prefix"}),
})
public class PrefixCTRL extends ImportExportCTRL<PrefixData> {

    private static final long serialVersionUID = -1342976327159618815L;
    private List<CountryData> countryList = new ArrayList<>();
    private List<NetworkData> networkList = new ArrayList<>();
    private List<OperatorData> operatorList = new ArrayList<>();
    private String networkRelations;
    private String exportFileName = NVSMXCommonConstants.EXPORT_PREFIX + PrefixImportExportUtility.getTime() + NVSMXCommonConstants.CSV;
    private static final String REASON = "reason";
    private static final String IMPORTDATAPREFIXEXTS = "importDataPrefixEXTS";
    private static final String CANNOT_FIND_PREFIX = "Can not find content for Prefix.";
    private static final String ERROR_FETCHING_PREFIX_DATA = "Error while fetching Prefix data for export operation. Reason: ";
    private static final String CANNOT_FIND_DATA_STRING_PREFIX="Can not find data as string for Prefix";
    private static final String CONTENT_DISPOSITION = "Content-Disposition";
    private static final String ATTACHMENT_FILENAME = "attachment; filename=\"";

    @Override
    public ACLModules getModule() {

        return ACLModules.PREFIX;
    }

    @Override
    public PrefixData createModel() {

        return new PrefixData();
    }

    @Override
    public void prepareValuesForSubClass() throws Exception {
        setCountryList(CRUDOperationUtil.findAll(CountryData.class));
        setOperatorList(CRUDOperationUtil.findAll(OperatorData.class));
        setNetworkList(CRUDOperationUtil.findAll(NetworkData.class));
        prepareOperationNetworkRelation();
    }

    @Override
    public void validate() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called validate()");
        }
        PrefixData prefixData = (PrefixData) getModel();

        boolean isAlreadyExist = isDuplicateEntity("prefix", prefixData.getResourceName(), getMethodName());
        if (isAlreadyExist) {
            addFieldError("prefix", "Prefix already exists");
        }

        if (Strings.isNullOrBlank(prefixData.getNetworkId())) {
            addFieldError("networkId", getText("prefix.network.required"));
        } else {
            NetworkData networkData = CRUDOperationUtil.get(NetworkData.class, prefixData.getNetworkId());
            if (networkData == null) {
                addFieldError("networkId", getText("prefix.network.not.exist"));
            } else {
                prefixData.setNetworkData(networkData);
            }
        }

        if (Strings.isNullOrBlank(prefixData.getOperatorId())) {
            addFieldError("operatorId", getText("prefix.operator.required"));
        } else {
            OperatorData operatorData = CRUDOperationUtil.get(OperatorData.class, prefixData.getOperatorId());
            if (operatorData == null) {
                addFieldError("operatorId", getText("prefix.operator.not.exist"));
            } else {
                prefixData.setOperatorData(operatorData);
            }
        }

        if (Strings.isNullOrBlank(prefixData.getCountryId())) {
            addFieldError("countryId", getText("prefix.country.required"));
        } else {
            CountryData countryData = CRUDOperationUtil.get(CountryData.class, prefixData.getCountryId());
            if (countryData == null) {
                addFieldError("countryId", getText("prefix.country.not.exist"));
            } else {
                prefixData.setCountryData(countryData);
            }
        }
    }

    public List<CountryData> getCountryList() {
        return countryList;
    }

    public void setCountryList(List<CountryData> countryList) {
        this.countryList = countryList;
    }

    public List<NetworkData> getNetworkList() {
        return networkList;
    }

    public void setNetworkList(List<NetworkData> networkList) {
        this.networkList = networkList;
    }

    public List<OperatorData> getOperatorList() {
        return operatorList;
    }

    public void setOperatorList(List<OperatorData> operatorList) {
        this.operatorList = operatorList;
    }

    public String getNetworkRelations() {
        return networkRelations;
    }

    public void setNetworkRelations(String networkRelations) {
        this.networkRelations = networkRelations;
    }

    private void prepareOperationNetworkRelation() {
        setNetworkBelongingToOperatorJson();
    }

    public void setNetworkBelongingToOperatorJson() {

        List<NetworkData> networkDataList = CRUDOperationUtil.findAllWhichIsNotDeleted(NetworkData.class);
        Map<String, List<Map<String, List<NetworkData>>>> networkDataMap = Maps.newHashMap();

        for (NetworkData networkData : networkDataList) {
            OperatorData operatorData = networkData.getOperatorData();
            String operatorId = operatorData.getId();

            CountryData countryData = networkData.getCountryData();
            String countryId = countryData.getId();

            Map<String, List<NetworkData>> operatorIdToNetworkData = Maps.newHashMap();
            if (operatorIdToNetworkData.containsKey(operatorId)) {
                operatorIdToNetworkData.get(operatorId).add(networkData);
            } else {
                List<NetworkData> networkDataListForOperatorID = new ArrayList<>();
                networkDataListForOperatorID.add(networkData);
                operatorIdToNetworkData.put(operatorId, networkDataListForOperatorID);
            }

            if (networkDataMap.containsKey(countryId)) {
                networkDataMap.get(countryId).add(operatorIdToNetworkData);
            } else {
                List<Map<String, List<NetworkData>>> countryIdToNetworkData = new ArrayList<>();
                countryIdToNetworkData.add(operatorIdToNetworkData);
                networkDataMap.put(countryId, countryIdToNetworkData);
            }
        }
        Gson gson = GsonFactory.defaultInstance();
        this.networkRelations = gson.toJson(networkDataMap);
    }


    @SkipValidation
    public HttpHeaders exportUsingText() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called exportUsingText()");
        }

        String searchedColumn = getRequest().getParameter("exportDataSelect");
        String searchWord = getRequest().getParameter("exportDataInput");

        BufferedWriter writer = null;
        PrintWriter out = null;

        if (searchWord == null) {
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        }

        if (isImportExportOperationInProgress()) {
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.EXPORT_FAILURE.key));
            addActionError(getText("prefix.importexport.operation"));
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        }
        makeImportExportOperationInProgress(true);

        List<PrefixData> prefixDatas = CRUDOperationUtil.get(PrefixData.class, Order.asc("prefix"));
        StringWriter stringWriter = new StringWriter();

        stringWriter = compareWord(prefixDatas, stringWriter, searchWord, searchedColumn);
        try {
            String prefixInfo = stringWriter.toString();
            if (prefixInfo.equals(NVSMXCommonConstants.PREFIX_HEADER)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(getLogModule(), CANNOT_FIND_PREFIX);
                }
                throw new IllegalArgumentException(CANNOT_FIND_DATA_STRING_PREFIX);
            }
            if (Strings.isNullOrBlank(prefixInfo)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(getLogModule(), CANNOT_FIND_PREFIX);
                }
                throw new NullPointerException(CANNOT_FIND_DATA_STRING_PREFIX);
            }
            getResponse().setContentType(TEXT_CSV);
            getResponse().setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + exportFileName + "\"");

            out = getResponse().getWriter();
            writer = new BufferedWriter(out);
            writer.write(prefixInfo, 0, prefixInfo.length());
            writer.flush();

            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "All Prefix are exported successfully");
            }

            addActionMessage("Prefix are exported successfully");
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);

        } catch (IllegalArgumentException e) {
            LogManager.getLogger().error(getLogModule(), ERROR_FETCHING_PREFIX_DATA + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), ERROR_FETCHING_PREFIX_DATA + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
    }

    private StringWriter compareWord(List<PrefixData> prefixDatas, StringWriter stringWriter, String searchWord, String searchedColumn) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called compareWord()");
        }
        stringWriter.write(NVSMXCommonConstants.PREFIX_HEADER);
        for (PrefixData prefixData : prefixDatas) {
            if (searchedColumn.equalsIgnoreCase("prefix") && prefixData.getPrefix().equalsIgnoreCase(searchWord)) {
                stringWriter.write(getDetail(prefixData));
            } else if (searchedColumn.equalsIgnoreCase("country") && prefixData.getCountryData().getName().equalsIgnoreCase(searchWord)) {
                stringWriter.write(getDetail(prefixData));
            } else if (searchedColumn.equalsIgnoreCase("operator") && prefixData.getOperatorData().getName().equalsIgnoreCase(searchWord)) {
                stringWriter.write(getDetail(prefixData));
            } else if (searchedColumn.equalsIgnoreCase("network") && prefixData.getNetworkData().getName().equalsIgnoreCase(searchWord)) {
                stringWriter.write(getDetail(prefixData));
            }
        }
        return stringWriter;
    }

    private String getDetail(PrefixData prefixData) {
        return "\n" + prefixData.getPrefix() + "," + prefixData.getCountryData().getName() + "," + prefixData.getOperatorData().getName() + "," + prefixData.getNetworkData().getName();
    }

    @SkipValidation
    public HttpHeaders export() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called export()");
        }
        String[] prefixIds = getRequest().getParameterValues("ids");
        BufferedWriter writer = null;
        PrintWriter out = null;
        try {
            if (prefixIds == null) {
                addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
                addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
                makeImportExportOperationInProgress(false);
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            if (isImportExportOperationInProgress()) {
                addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.EXPORT_FAILURE.key));
                addActionError(getText("prefix.importexport.operation"));
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
            makeImportExportOperationInProgress(true);
            List<PrefixDataExt> prefixDatas = CRUDOperationUtil.getAllByIds(PrefixDataExt.class, prefixIds);
            if (prefixDatas.isEmpty() || prefixDatas == null) {
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
            StringWriter stringWriter = new StringWriter();
            stringWriter.write(NVSMXCommonConstants.PREFIX_HEADER);
            for (PrefixDataExt prefixData : prefixDatas) {
                stringWriter.write("\n");
                stringWriter.write(prefixData.getPrefix() + "," + prefixData.getCountryData().getName() + "," + prefixData.getOperatorData().getName() + "," + prefixData.getNetworkData().getName());
            }
            String prefixInfo = stringWriter.toString();
            if (Strings.isNullOrBlank(prefixInfo)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(getLogModule(), CANNOT_FIND_PREFIX);
                }
                throw new NullPointerException(CANNOT_FIND_DATA_STRING_PREFIX);
            }
            getResponse().setContentType(TEXT_CSV);
            getResponse().setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + exportFileName + "\"");

            out = getResponse().getWriter();
            writer = new BufferedWriter(out);
            writer.write(prefixInfo, 0, prefixInfo.length());
            writer.flush();

            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "All Prefix data are exported successfully");
            }

            addActionMessage(getText("prefix.export.success"));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);

        } catch (IllegalArgumentException e) {
            LogManager.getLogger().error(getLogModule(), ERROR_FETCHING_PREFIX_DATA + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), ERROR_FETCHING_PREFIX_DATA + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
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
        BufferedWriter writer = null;
        PrintWriter out = null;
        try {
            if (isImportExportOperationInProgress()) {
                addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.EXPORT_FAILURE.key));
                addActionError(getText("prefix.importexport.operation"));
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }
            makeImportExportOperationInProgress(true);

            String prefixInfo = PrefixImportExportUtility.prefixExportData().toString();
            if (Strings.isNullOrBlank(prefixInfo)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(getLogModule(), CANNOT_FIND_PREFIX);
                }
                throw new NullPointerException(CANNOT_FIND_DATA_STRING_PREFIX);
            }
            if (prefixInfo.equals(NVSMXCommonConstants.PREFIX_HEADER)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(getLogModule(), CANNOT_FIND_PREFIX);
                }
                addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
                return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
            }

            getResponse().setContentType(TEXT_CSV);
            getResponse().setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + exportFileName + "\"");

            out = getResponse().getWriter();
            writer = new BufferedWriter(out);
            writer.write(prefixInfo, 0, prefixInfo.length());
            writer.flush();

            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "All Prefix data are exported successfully");
            }

            addActionMessage("Prefix Data are exported successfully");
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);

        } catch (IllegalArgumentException e) {
            LogManager.getLogger().error(getLogModule(), ERROR_FETCHING_PREFIX_DATA + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            addActionError(getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), ERROR_FETCHING_PREFIX_DATA + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            addActionError(getText(ActionMessageKeys.UNKNOWN_INTERNAL_ERROR.key));
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.INTERNAL_ERROR.code);
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
    }

    @SkipValidation
    public String importPrefix() {
        InputStream in = null;
        BufferedReader bufferedReaderTemp = null;
        try {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "Method called importPrefix()");
            }
            if (getImportedFile() == null) {
                LogManager.getLogger().error(getLogModule(), "Error while importing Prefix data. Reason: File not found");
                addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
                return com.elitecore.nvsmx.system.constants.Results.FAIL.getValue();
            }
            in = new FileInputStream(getImportedFile());
            PrefixImportExportUtility prefixImportExportUtility = new PrefixImportExportUtility();
            bufferedReaderTemp = new BufferedReader(new FileReader(getImportedFile()));
            String header = bufferedReaderTemp.readLine();
            if (!((header.equalsIgnoreCase(NVSMXCommonConstants.PREFIX_HEADER_WITH_REMARKS)) || (header.equalsIgnoreCase(NVSMXCommonConstants.PREFIX_HEADER)))) {
                LogManager.getLogger().error(getLogModule(), "Error while importing Prefix data. Reason: Select file with proper header. Expected Header (Prefix,Country,Operator,Network)");
                addActionError(Discriminators.PREFIX + " " + "Select file with proper header. Expected Header (Prefix,Country,Operator,Network)");
                return com.elitecore.nvsmx.system.constants.Results.FAIL.getValue();
            }
            getRequest().getSession().setAttribute(Attributes.PREFIX_TYPES, prefixImportExportUtility.importPrefixExtended(in));
            Gson gson = GsonFactory.defaultInstance();
            JsonArray importDataAll = gson.toJsonTree(getRequest().getSession().getAttribute(Attributes.PREFIX_TYPES), new TypeToken<List<PrefixData>>() {
            }.getType()).getAsJsonArray();

            getRequest().setAttribute("importedDataPrefix", importDataAll);
            getRequest().getSession().setAttribute("importDataPrefixSession", importDataAll);

            setActionChainUrl(getRedirectURL("import"));
            return NVSMXCommonConstants.REDIRECT_URL;
        } catch (IOException e) {
            LogManager.getLogger().error("Prefix", "Error while importing Prefix data. Reason: File not found");
            LogManager.getLogger().trace("Prefix", e);
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.DATA_NOT_FOUND.key));
            makeImportExportOperationInProgress(false);
            return com.elitecore.nvsmx.system.constants.Results.FAIL.getValue();
        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while importing Prefix data. Reason: " + e.getMessage());
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
            addActionError(getText(ActionMessageKeys.IMPORT_FAILURE.key));
            makeImportExportOperationInProgress(false);
            return com.elitecore.nvsmx.system.constants.Results.FAIL.getValue();
        } finally {
            Closeables.closeQuietly(in);
            Closeables.closeQuietly(bufferedReaderTemp);
        }
    }

    private String getRemarksFromSubReasons(List<String> subReasons) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called getRemarksFromSubReasons()");
        }
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

    public List<Reason> importPrefixReason(List<PrefixDataExt> prefixDatasForImport, String action, String strURL, String staffID, HttpServletRequest request) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called importPrefixReason()");
        }
        List<Reason> reasons = Collectionz.newArrayList();
        List<PrefixDataExt> prefixDataExtList = Collectionz.newArrayList();
        if (Collectionz.isNullOrEmpty(prefixDatasForImport)) {
            return reasons;
        }
        long startTime = System.nanoTime();
        StaffData staffData = CRUDOperationUtil.get(StaffData.class, staffID);
        for (PrefixDataExt importService : prefixDatasForImport) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(getLogModule(), "Import Operation started for prefix data: " + importService.getPrefix());
            }
            Reason reason = new Reason(importService.getPrefix());
            Session session = null;
            Transaction transaction = null;
            try {

                if (strURL.equalsIgnoreCase("prefix")) {
                    setStaffInfoAPI(importService, staffData);
                } else {
                    setStaffInfo(importService);
                }
                boolean isExistGroup = true;
                List<String> groups = new ArrayList<>();
                if (Strings.isNullOrBlank(importService.getGroupNames())) {
                    importService.setGroups(CommonConstants.DEFAULT_GROUP_ID);
                    groups.add(CommonConstants.DEFAULT_GROUP_ID);
                } else {
                    isExistGroup = importExportUtil.getGroupIdsFromName(importService, reason, groups);
                }
                if ((isExistGroup) && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
                    /**
                     * this will import services based on staff belonging groups
                     */
                    if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
                        importService.setGroups(Strings.join(",", groups));
                        //logic for save
                        importExportUtil.validateAndImportInformation(importService, action, reason);
                    }
                    if (LogManager.getLogger().isDebugLogLevel()) {
                        LogManager.getLogger().debug(getLogModule(), "Import Operation finished for prefix: " + importService.getPrefix());
                    }
                }
                prefixDataExtList.add(importService);
                reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
                reasons.add(reason);
            } catch (Exception e) {
                if (transaction != null)
                {
                    transaction.rollback();
                }
                LogManager.getLogger().error(getLogModule(), "Error while importing prefix: " + importService.getPrefix());
                LogManager.getLogger().trace(getLogModule(), e);
                String errorMessage = "Failed to import prefix due to internal error. Kindly refer logs for further details";
                List<String> errors = new ArrayList<>(2);
                errors.add(errorMessage);
            } finally {
                HibernateSessionUtil.closeSession(session);
            }
        }
        long endTime = System.nanoTime();
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Total Execution time : " + (endTime - startTime));
        }
        Gson gson = GsonFactory.defaultInstance();
        JsonArray importDataAll = gson.toJsonTree(reasons, new TypeToken<List<PrefixData>>() {
        }.getType()).getAsJsonArray();
        JsonArray importDataPrefix = gson.toJsonTree(prefixDataExtList, new TypeToken<List<PrefixData>>() {
        }.getType()).getAsJsonArray();
        if (strURL.equalsIgnoreCase("prefix")) {
            HttpSession tempSession = request.getSession();
            tempSession.setAttribute(REASON, importDataAll);
            tempSession.setAttribute(IMPORTDATAPREFIXEXTS, importDataPrefix);
        } else {
            getRequest().getSession().setAttribute(REASON, importDataAll);
            getRequest().getSession().setAttribute(IMPORTDATAPREFIXEXTS, importDataPrefix);
        }

        Collections.sort(reasons, new Comparator<Reason>() {
            @Override
            public int compare(Reason reason1, Reason reason2) {
                return reason1.getMessages().compareTo(reason2.getMessages());
            }
        });
        return reasons;
    }


    @SkipValidation
    public String importData() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called importData()");
        }
        try {
            String selectedPrefixIndexes = getRequest().getParameter(Attributes.SELECTED_INDEXES);
            String action = getRequest().getParameter(Attributes.USER_ACTION);
            if (Strings.isNullOrBlank(selectedPrefixIndexes) == false && Collectionz.isNullOrEmpty((List<PrefixDataExt>) getRequest().getSession().getAttribute(Attributes.PREFIX_TYPES)) == false) {
                makeImportExportOperationInProgress(true);
            }
            String[] splitURL = getRequest().getRequestURL().toString().split("/");
            JsonArray importServiceTypeResultJson = importDataExtended(selectedPrefixIndexes, action, (List<PrefixDataExt>) getRequest().getSession().getAttribute(Attributes.PREFIX_TYPES), splitURL[splitURL.length - 1], "", getRequest());
            getRequest().getSession().setAttribute("importStatusSession", importServiceTypeResultJson);
            getRequest().setAttribute("importStatus", importServiceTypeResultJson);
            getRequest().setAttribute("Sender", "prefix");
            getRequest().setAttribute("SenderPackage", "sm");

            makeImportExportOperationInProgress(false);
        } catch (Exception e) {
            LogManager.getLogger().error(getLogModule(), "Error while importing Prefix data");
            LogManager.getLogger().trace(getLogModule(), e);
            addActionError(Discriminators.PREFIX + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
            addActionError(getText(ActionMessageKeys.IMPORT_FAILURE.key));
            makeImportExportOperationInProgress(false);
            return com.elitecore.nvsmx.system.constants.Results.FAIL.getValue();
        }

        setActionChainUrl(NVSMXCommonConstants.IMPORT_STATUS_REPORT);
        return NVSMXCommonConstants.REDIRECT_URL;
    }


    @SkipValidation
    public HttpHeaders mergeValuesAndStatusJSP() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called mergeValuesAndStatusJSP()");
        }
        JsonArray prefixAllDataJson = (JsonArray) getRequest().getSession().getAttribute(IMPORTDATAPREFIXEXTS);
        JsonArray remarks = (JsonArray) getRequest().getSession().getAttribute(REASON);
        StringBuilder sb = new StringBuilder();
        String fileName = NVSMXCommonConstants.IMPORT_PREFIX_ERROR + PrefixImportExportUtility.getTime() + NVSMXCommonConstants.CSV;
        sb.append(NVSMXCommonConstants.PREFIX_HEADER_WITH_REMARKS + "\n");
        for (int i = 0; i < prefixAllDataJson.size(); i++) {
            JsonObject objectPrefix = prefixAllDataJson.get(i).getAsJsonObject();
            JsonObject objectRemarks = remarks.get(i).getAsJsonObject();
            if (!objectRemarks.get("messages").getAsString().equals("SUCCESS")) {
                PrefixImportExportUtility.setErrorData(objectPrefix, sb, objectRemarks);
                sb.append("\n");
            }

        }
        if (!sb.toString().equalsIgnoreCase(NVSMXCommonConstants.PREFIX_HEADER_WITH_REMARKS + "\n")) {
            getResponse().setContentType(TEXT_CSV);
            getResponse().setHeader(CONTENT_DISPOSITION, ATTACHMENT_FILENAME + fileName + "\"");
            try {
                PrintWriter out = getResponse().getWriter();
                BufferedWriter writer = new BufferedWriter(out);
                writer.write(sb.toString(), 0, sb.toString().length());
                writer.flush();
            } catch (IOException e) {
                LogManager.getLogger().error(getLogModule(), "Error while downloading Prefix error data");
                LogManager.getLogger().trace(getLogModule(), e);
            }
        } else {
            addActionError("Nothing to download. Every record imported Successfully.");
            makeImportExportOperationInProgress(false);
            return new DefaultHttpHeaders(SUCCESS).withStatus(ResultCode.SUCCESS.code);
        }
        return null;
    }

    //Do not edit this method if it is not required becuase it is linked with import API
    @SkipValidation
    public JsonArray importDataExtended(String selectedPrefixIndexes, String action, List<PrefixDataExt> prefixPara, String strURL, String staffID, HttpServletRequest request) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(getLogModule(), "Method called importDataExtended()");
        }
        List<PrefixDataExt> prefixes = prefixPara;

        List<PrefixDataExt> prefixDataExtList = new ImportEntityAccumulator<>(prefixes, selectedPrefixIndexes).get();

        List<Reason> reasons;
        reasons = importPrefixReason(prefixDataExtList, action, strURL, staffID, request);
        Gson gson = GsonFactory.defaultInstance();
        return gson.toJsonTree(reasons, new TypeToken<List<Reason>>() {
        }.getType()).getAsJsonArray();
    }

}
