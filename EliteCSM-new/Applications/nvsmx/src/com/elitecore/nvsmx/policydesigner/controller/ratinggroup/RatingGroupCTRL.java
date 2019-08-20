package com.elitecore.nvsmx.policydesigner.controller.ratinggroup;

/**
 * Created by Ishani on 23/3/17.
 */

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupContainer;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupDataExt;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.sm.acl.StaffData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportEntityAccumulator;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportCTRL;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.system.ObjectDiffer;
import com.elitecore.nvsmx.system.constants.Attributes;
import com.elitecore.nvsmx.system.constants.InputConfigConstants;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;
import com.elitecore.nvsmx.system.constants.Results;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.keys.ActionMessageKeys;
import com.elitecore.nvsmx.system.util.NVSMXUtil;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;
import com.opensymphony.xwork2.interceptor.annotations.InputConfig;
import org.apache.struts2.interceptor.validation.SkipValidation;
import org.hibernate.criterion.Order;

import javax.servlet.ServletException;
import javax.xml.bind.JAXBException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.elitecore.commons.logging.LogManager.getLogger;


public class RatingGroupCTRL extends ImportExportCTRL<RatingGroupData> {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = RatingGroupCTRL.class.getSimpleName();
    private RatingGroupData ratingGroupData = new RatingGroupData();
    private static final String ACTION_SEARCH = "policydesigner/ratinggroup/RatingGroup/search";
    private static final String ACTION_VIEW = "policydesigner/ratinggroup/RatingGroup/view";
    private String actionChainUrl;
    private List<GroupData> staffBelongingGroupList = Collectionz.newArrayList();
    private Object[] messageParameter = {Discriminators.RATING_GROUP};
    private static final String RATING_GROUP_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.description,dataList\\[\\d+\\]\\.identifier,dataList\\[\\d+\\]\\.groups";
    private String selectedServiceTypes = new String();
    private List<DataServiceTypeData> allServices = Collectionz.newArrayList();

    @SkipValidation
    public String init() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called init()");
        }
        String ratingGroupId = getRatingGroupId();

        if (Strings.isNullOrBlank(ratingGroupId)) {
        ratingGroupData.setDescription(NVSMXUtil.getDefaultDescription(request));
        setAllServices(CRUDOperationUtil.get(DataServiceTypeData.class, Order.asc("name")));
        return Results.CREATE.getValue();
    }

        try {
            ratingGroupData = CRUDOperationUtil.get(RatingGroupData.class, ratingGroupId);
            if(ratingGroupData == null){
                LogManager.getLogger().error(MODULE, "Error while fetching Rating Group for update operation. Reason: RatingGroup Id not found");
                return redirectError(Discriminators.RATING_GROUP, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
            }
            List<String> selectedServices = Collectionz.newArrayList();
            for (DataServiceTypeData dataServiceTypeData : ratingGroupData.getDataServiceTypeData()) {
                selectedServices.add(dataServiceTypeData.getId());
            }
            setSelectedServiceTypes(Strings.join(",", selectedServices));
            setAllServices(CRUDOperationUtil.get(DataServiceTypeData.class, Order.asc("name")));
            return Results.UPDATE.getValue();

        } catch (Exception e) {
            return generateErrorLogsAndRedirect(e, "Failed to fetch Rating Group.", ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue());
        }
    }

    private String getRatingGroupId() {

        String ratingGroupId = request.getParameter(Attributes.RATING_GROUP_ID);
        if (Strings.isNullOrBlank(ratingGroupId)) {
            ratingGroupId = (String) request.getAttribute(Attributes.RATING_GROUP_ID);
            if (Strings.isNullOrBlank(ratingGroupId)) {
                ratingGroupId = request.getParameter("ratingGroupData.id");
            }
        }
        return ratingGroupId;
    }

    @InputConfig(resultName = InputConfigConstants.CREATE)
    public String create() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called create()");
        }
        try {
            ratingGroupData.setCreatedDateAndStaff(getStaffData());

            if (Strings.isNullOrBlank(getGroupIds())) {
                ratingGroupData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
            } else {
                ratingGroupData.setGroups(getGroupIds());
            }
            setServiceTypeRatingGroupRel(ratingGroupData);
            CRUDOperationUtil.save(ratingGroupData);
            String message = Discriminators.RATING_GROUP + " <b><i>" + ratingGroupData.getNameAndIdentifier() + "</i></b> " + "Created";
            CRUDOperationUtil.audit(ratingGroupData, ratingGroupData.getName(), AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), ratingGroupData.getHierarchy(), message);
            request.setAttribute(Attributes.RATING_GROUP_ID, ratingGroupData.getId());

            MessageFormat messageFormat = new MessageFormat(getText("create.success"));
            addActionMessage(messageFormat.format(messageParameter));

            setActionChainUrl(Results.VIEW.getValue());
            return Results.DISPATCH_VIEW.getValue();
        } catch (Exception e) {
            return generateErrorLogsAndRedirect(e, "Failed to Create Rating Group.", ActionMessageKeys.CREATE_FAILURE.key, Results.LIST.getValue());
        }
    }

    @InputConfig(resultName = InputConfigConstants.UPDATE)
    public String update() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called update()");
        }

        request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
        setActionChainUrl(Results.VIEW.getValue());
        try {

            if (Strings.isNullOrBlank(getGroupIds())) {
                ratingGroupData.setGroups(CommonConstants.DEFAULT_GROUP_ID);
            } else {
                ratingGroupData.setGroups(getGroupIds());
            }
            ratingGroupData.setModifiedDateAndStaff(getStaffData());

            RatingGroupData ratingGroupInDb = CRUDOperationUtil.get(RatingGroupData.class,ratingGroupData.getId());
            JsonObject oldJsonObject = ratingGroupInDb.toJson();

            ratingGroupInDb.getDataServiceTypeData().clear();
            setServiceTypeRatingGroupRel(ratingGroupInDb);
            ratingGroupInDb.setName(ratingGroupData.getName());
            ratingGroupInDb.setDescription(ratingGroupData.getDescription());
            ratingGroupInDb.setIdentifier(ratingGroupData.getIdentifier());
            ratingGroupInDb.setGroups(ratingGroupData.getGroups());
            ratingGroupInDb.setModifiedDateAndStaff(getStaffData());
            CRUDOperationUtil.update(ratingGroupInDb);
            JsonObject jsonObjectForNew= ratingGroupInDb.toJson();
            JsonArray difference = ObjectDiffer.diff(oldJsonObject, jsonObjectForNew);
            String message = Discriminators.RATING_GROUP + " <b><i>" + ratingGroupData.getNameAndIdentifier() + "</i></b> " + "Updated";
            CRUDOperationUtil.audit(ratingGroupInDb,ratingGroupInDb.getName(),AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference ,ratingGroupInDb.getHierarchy(), message);

            request.setAttribute(Attributes.RATING_GROUP_ID, ratingGroupData.getId());

            MessageFormat messageFormat = new MessageFormat(getText("update.success"));
            addActionMessage(messageFormat.format(messageParameter));
            return Results.DISPATCH_VIEW.getValue();

        } catch (Exception e) {
            setActionChainUrl(ACTION_VIEW);
            return generateErrorLogsAndRedirect(e, "Failed to Update Rating Group.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
        }
    }

    private void setServiceTypeRatingGroupRel(RatingGroupData ratingGroup) {
        if(Strings.isNullOrBlank(getSelectedServiceTypes()) == false) {
            List<String> selectedServiceTypeIds = SPLITTER.split(getSelectedServiceTypes());
            if (Collectionz.isNullOrEmpty(selectedServiceTypeIds) == false) {
                for (String serviceTypeId : selectedServiceTypeIds) {
                    DataServiceTypeData dataServiceTypeData = CRUDOperationUtil.get(DataServiceTypeData.class,serviceTypeId);
                    if(dataServiceTypeData != null) {
                        ratingGroup.getDataServiceTypeData().add(dataServiceTypeData);
                    }
                }
            }
        }
    }

    @SkipValidation
    public String view() {

        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called view()");
        }
        try {

            String ratingGroupId = getRatingGroupId();
            if(Strings.isNullOrBlank(ratingGroupId)){
                return redirectError(Discriminators.RATING_GROUP, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
            }
            ratingGroupData = CRUDOperationUtil.get(RatingGroupData.class, ratingGroupId);
            if (Strings.isNullOrBlank(ratingGroupData.getGroups()) == false) {
                ratingGroupData.setGroupNames(GroupDAO.getGroupNames(SPLITTER.split(ratingGroupData.getGroups())));
            }
        } catch (Exception e) {
            return generateErrorLogsAndRedirect(e, "Failed to View Rating Group.", ActionMessageKeys.VIEW_FAILURE.key, Results.LIST.getValue());
        }
        return Results.VIEW.getValue();

    }

    @SkipValidation
    public String search() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called search()");
        }
        return Results.LIST.getValue();
    }

    @SkipValidation
    public String delete() {
        if (getLogger().isDebugLogLevel()) {
            getLogger().debug(MODULE, "Method called delete()");
        }

        request.setAttribute(Attributes.ACTION, ACTION_SEARCH);
        setActionChainUrl(ACTION_SEARCH);
        try {
            String[] ratingGroupIds = request.getParameterValues(Attributes.IDS);
            if (ratingGroupIds != null) {
                for (String ratingGroupId : ratingGroupIds) {
                    ratingGroupData = CRUDOperationUtil.get(RatingGroupData.class, ratingGroupId);

                    final Set<String> pccRuleNames = Collectionz.newHashSet();
                    List<PCCRuleData> pccRules = CRUDOperationUtil.get(PCCRuleData.class, Order.asc("chargingKey"));
                    Collectionz.filter(pccRules, new Predicate<PCCRuleData>() {
                        @Override
                        public boolean apply(PCCRuleData pccRule) {
                            if(pccRule.getChargingKey().equals(ratingGroupData.getId())) {
                                pccRuleNames.add(pccRule.getName());
                                return true;
                            }
                            return false;
                        }
                    });
                    if (Collectionz.isNullOrEmpty(pccRules) == false) {
                        addActionError("Unable to delete '"+ratingGroupData.getName()+"'. <br/>Reason :  "+Discriminators.RATING_GROUP+" is configured with one or more "+Discriminators.PCCRULE);
                        if (getLogger().isDebugLogLevel()) {
                            getLogger().debug(MODULE, Discriminators.RATING_GROUP + ratingGroupData.getName() + " is configured with PCC Rule: " + Strings.join(",", pccRuleNames));
                        }
                        return Results.LIST.getValue();
                        //return redirectError(Discriminators.RATING_GROUP, ,Results.LIST.getValue());
                    }
                    if (Collectionz.isNullOrEmpty(ratingGroupData.getDataServiceTypeData()) == false) {
                        ratingGroupData.getDataServiceTypeData().clear();
                    }


                    String message = Discriminators.RATING_GROUP + " <b><i>" + ratingGroupData.getName() + "</i></b> " + " Deleted";
                    CRUDOperationUtil.audit(ratingGroupData, ratingGroupData.getName(), AuditActions.DELETE, getStaffData(), request.getRemoteAddr(), ratingGroupData.getHierarchy(), message);
                    ratingGroupData.setStatus(CommonConstants.STATUS_DELETED);
                    CRUDOperationUtil.update(ratingGroupData);
                }
            }
        } catch (Exception ex) {
            return generateErrorLogsAndRedirect(ex, "Failed to delete Rating Group.", ActionMessageKeys.DELETE_FAILURE.key, Results.LIST.getValue());
        }
        MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
        addActionMessage(messageFormat.format(messageParameter));

        return Results.REDIRECT_ACTION.getValue();
    }



    @Override
    public RatingGroupData getModel() {
        return ratingGroupData;
    }

    public RatingGroupData getRatingGroupData() {
        return ratingGroupData;
    }

    public void setRatingGroupData(RatingGroupData ratingGroupData) {
        this.ratingGroupData = ratingGroupData;
    }

    public String getActionChainUrl() {
        return actionChainUrl;
    }

    @ActionChain(name = "actionChainUrlMethod")
    public void setActionChainUrl(String actionChainUrl) {
        this.actionChainUrl = actionChainUrl;
    }

    public List<GroupData> getStaffBelongingGroupList() {
        return staffBelongingGroupList;
    }

    public void setStaffBelongingGroupList(List<GroupData> staffBelongingGroups) {
        this.staffBelongingGroupList = staffBelongingGroups;
    }

    @Override
    protected List<RatingGroupData> getSearchResult(String criteriaJson, Class beanType, int startIndex, int maxRecords, String sortColumnName, String sortColumnOrder, String staffBelongingGroups) throws Exception {
        return super.getSearchResult(criteriaJson, beanType, startIndex, maxRecords, sortColumnName, sortColumnOrder, staffBelongingGroups);
    }

    @SkipValidation
    public String exportAll() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called exportAll()");
        }
        String FILENAME = "exportRatingGroup_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + XML;
        BufferedWriter writer = null;
        PrintWriter out = null;
        try {

            if (isImportExportOperationInProgress()) {
                return redirectError(Discriminators.RATING_GROUP, "ratinggroup.importexport.operation", Results.LIST.getValue(), false);
            }
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, true);
            RatingGroupContainer ratingGroupContainer = new RatingGroupContainer();
            List<RatingGroupDataExt> ratingGroupDatas = CRUDOperationUtil.get(RatingGroupDataExt.class, Order.asc("name"));

            importExportUtil.filterBasedOnStaffBelongingGroupIds(ratingGroupDatas, getStaffBelongingGroups());
            if (Collectionz.isNullOrEmpty(ratingGroupDatas)) {
                makeImportExportOperationInProgress(false);
                LogManager.getLogger().warn(MODULE, getText("ratinggroup.importexport.nogroupfound"));
                return redirectError(MODULE,"ratinggroup.importexport.nogroupfound", Results.LIST.getValue(), false);
            }
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Export All operation started for rating groups");
            }

            List<RatingGroupDataExt> ratingGroupToBeExported = new ArrayList<RatingGroupDataExt>();
            Map<String, String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
            for (RatingGroupDataExt ratingGroup : ratingGroupDatas) {
                List<String> groups = SPLITTER.split(ratingGroup.getGroups());
                Reason reason = new Reason(ratingGroup.getName());
                boolean isExportAllowedForGroup = importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, ACLModules.RATINGGROUP, ACLAction.EXPORT.name(), getStaffData().getUserName());
                if (isExportAllowedForGroup) {
                    ratingGroupToBeExported.add(ratingGroup);
                    importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, ratingGroup);
                }
            }

            if (Collectionz.isNullOrEmpty(ratingGroupToBeExported)) {
                makeImportExportOperationInProgress(false);
                LogManager.getLogger().warn(MODULE, getText("ratinggroup.importexport.nogroupfound"));
                return redirectError(MODULE,"ratinggroup.importexport.nogroupfound", Results.LIST.getValue(), false);
            }
            ratingGroupContainer.setRatingGroup(ratingGroupToBeExported);

            StringWriter stringWriter = new StringWriter();
            ConfigUtil.serialize(stringWriter, RatingGroupContainer.class, ratingGroupContainer);
            String ratingGroupInfo = stringWriter.toString();
            if (Strings.isNullOrBlank(ratingGroupInfo)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, "Can not find content for Rating Group.");
                }
                throw new Exception("Can not find data as string for the Rating Group");
            }
            response.setContentType(TEXT_XML);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + FILENAME + "\"");
            out = response.getWriter();
            writer = new BufferedWriter(out);
            writer.write(ratingGroupInfo, 0, ratingGroupInfo.length());
            writer.flush();

            makeImportExportOperationInProgress(false);
            request.getSession().setAttribute(Attributes.LAST_URI, ACTION_SEARCH);
            LogManager.getLogger().debug(MODULE, "Export All operation ended for Rating Group");
            return "EXPORT_COMPLETED";

        } catch (Exception e) {
            request.getServletContext().setAttribute(NVSMXCommonConstants.IS_IMPORT_EXPORT_PROCESSING, false);
            return generateErrorLogsAndRedirect(e, "Error while fetching Rating Group data for export operation.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
    }

    @SkipValidation
    public String export() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called export()");
        }
        String[] ratingGroupIds = request.getParameterValues(Attributes.IDS);
        String FILENAME = "exportRatingGroup_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + XML;
        BufferedWriter writer = null;
        PrintWriter out = null;
        try {

            if (ratingGroupIds == null) {
                makeImportExportOperationInProgress(false);
                return redirectError(Discriminators.RATING_GROUP, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
            }

            Map<String, String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();

            if (isImportExportOperationInProgress()) {
                return redirectError(Discriminators.RATING_GROUP, "ratinggroup.importexport.operation", Results.REDIRECT_ERROR.getValue(), false);
            }
            makeImportExportOperationInProgress(true);

            RatingGroupContainer ratingGroupContainer = new RatingGroupContainer();
            List<RatingGroupDataExt> ratingGroupDatas = CRUDOperationUtil.getAllByIds(RatingGroupDataExt.class, ratingGroupIds);

            if (Collectionz.isNullOrEmpty(ratingGroupDatas)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, "Can not find Rating Group for Ids : " + Arrays.asList(ratingGroupIds));
                }
                //throw new Exception("Can not find Rating Group in Database");
                makeImportExportOperationInProgress(false);
                return redirectError(Discriminators.RATING_GROUP, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);

            }
            for (RatingGroupDataExt ratingGroup : ratingGroupDatas) {

                if (Strings.isNullOrBlank(ratingGroup.getGroups())) {
                    ratingGroup.setGroups(CommonConstants.DEFAULT_GROUP);
                }
                importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, ratingGroup);
            }

            ratingGroupContainer.setRatingGroup(ratingGroupDatas);

            StringWriter stringWriter = new StringWriter();
            ConfigUtil.serialize(stringWriter, RatingGroupContainer.class, ratingGroupContainer);

            String ratingGroupInfo = stringWriter.toString();
            if (Strings.isNullOrBlank(ratingGroupInfo)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, "Can not find content for Rating Group.");
                }
                throw new Exception("Can not find data as string for Rating Group");
            }
            response.setContentType(TEXT_XML);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + FILENAME + "\"");

            out = response.getWriter();
            writer = new BufferedWriter(out);
            writer.write(ratingGroupInfo, 0, ratingGroupInfo.length());
            writer.flush();


            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "All Rating Groups are exported successfully");
            }
            addActionMessage("Rating Group are exported successfully");
            makeImportExportOperationInProgress(false);
            request.getSession().setAttribute(Attributes.LAST_URI, ACTION_SEARCH);
            return "EXPORT_COMPLETED";


        } catch (Exception e) {
            makeImportExportOperationInProgress(false);
            return generateErrorLogsAndRedirect(e, "Error while fetching Rating Group data for export operation.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
    }

    @SkipValidation
    public String importRatingGroup() throws ServletException {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called importRatingGroup()");
        }
        if (isImportExportOperationInProgress()) {
            return redirectError(Discriminators.RATING_GROUP, "ratinggroup.importexport.operation", Results.LIST.getValue(), false);
        }
        if (getImportedFile() == null) {
            LogManager.getLogger().error(MODULE, "Error while importing Rating Group data. Reason: File not found");
            return redirectError(Discriminators.RATING_GROUP, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
        }
        try {
            if (TEXT_XML.equals(getImportedFileContentType()) == false) {
                LogManager.getLogger().error(MODULE, "Error while importing rating group data. Reason: Invalid File type is configured. Only XML_FILE_EXT File is supported for importing Rating Group");
                return redirectError(Discriminators.RATING_GROUP, "ratinggroup.importexport.invalidfiletype", Results.LIST.getValue(), false);
            }
            RatingGroupContainer ratingGroupContainer = ConfigUtil.deserialize(getImportedFile(), RatingGroupContainer.class);
            if (ratingGroupContainer == null) {
                return Results.REDIRECT_ERROR.getValue();
            }

            List<RatingGroupDataExt> ratingGroups = ratingGroupContainer.getRatingGroup();
            skipRatingGroupHaveNullName(ratingGroups);

            request.getSession().setAttribute(Attributes.RATING_GROUPS, ratingGroups);

            Gson gson = GsonFactory.defaultInstance();
            JsonArray importRatingGroupJson = gson.toJsonTree(ratingGroups, new TypeToken<List<RatingGroupDataExt>>() {
            }.getType()).getAsJsonArray();
            request.setAttribute("importedRatingGroups", importRatingGroupJson);
            return Results.IMPORT_RATING_GROUP.getValue();

        } catch (JAXBException e) {
            makeImportExportOperationInProgress(false);
            return generateErrorLogsAndRedirect(e, "Error while importing Rating Group data due to XML_FILE_EXT processing failure..", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
        } catch (Exception e) {
            makeImportExportOperationInProgress(false);
            return generateErrorLogsAndRedirect(e, "Error while importing Rating Group data.", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
        }
    }

    private void skipRatingGroupHaveNullName(List<RatingGroupDataExt> ratingGroupDataExts) {
        /*
        This method is used to remove the RatingGroupData have null name from the list of ratingGroupDataExts.
        It will also set proper message in request attribute(invalidEntityMessage) to display in GUI.
        */

        Predicate predicate = new Predicate<RatingGroupDataExt>() {
            @Override
            public boolean apply(RatingGroupDataExt input) {
                if(Strings.isNullOrBlank(input.getName())){
                    LogManager.getLogger().info(MODULE, "Found RatingGroupData with null name. Skipping Import process for RatingGroupData: " + input);
                    return false;
                }
                return true;
            };
        };

        String message = ImportExportUtil.removeInvalidEntitiesAndGetMessage(ratingGroupDataExts, predicate, Discriminators.RATING_GROUP);
        request.setAttribute(INVALID_ENTITY_MESSAGE,message);
    }

    @SkipValidation
    public String importData() {
        LogManager.getLogger().debug(MODULE, "Method called importData()");
        try {
            String selectedRatingGroupIndexes = request.getParameter(Attributes.SELECTED_INDEXES);
            String action = request.getParameter(Attributes.USER_ACTION);

            if (isImportExportOperationInProgress()) {
                addActionError(Discriminators.RATING_GROUP + " " + getText(ActionMessageKeys.IMPORT_FAILURE.key));
                addActionError(getText("ratinggroup.importexport.operation"));
                return Results.LIST.getValue();

            }

            List<RatingGroupDataExt> ratingGroups = (List<RatingGroupDataExt>) request.getSession().getAttribute(Attributes.RATING_GROUPS);
            List<RatingGroupDataExt> selectedRatingGroups = Collectionz.newArrayList();

            if(Strings.isNullOrBlank(selectedRatingGroupIndexes) == false && Collectionz.isNullOrEmpty(ratingGroups) == false) {
                makeImportExportOperationInProgress(true);
                selectedRatingGroups = new ImportEntityAccumulator<RatingGroupDataExt>(ratingGroups, selectedRatingGroupIndexes).get();
            }

            List<Reason> reasons = importRatingGroups(selectedRatingGroups, action);
            Gson gson = GsonFactory.defaultInstance();
            JsonArray importRatingGroupResultJson = gson.toJsonTree(reasons, new TypeToken<List<Reason>>() {
            }.getType()).getAsJsonArray();
            request.setAttribute("importStatus", importRatingGroupResultJson);
            makeImportExportOperationInProgress(false);
        } catch (Exception e) {
            makeImportExportOperationInProgress(false);
            return generateErrorLogsAndRedirect(e, "Error while importing Rating Group data.", ActionMessageKeys.IMPORT_FAILURE.key, Results.IMPORT_STATUS_REPORT.getValue());
        }
        return Results.IMPORT_STATUS_REPORT.getValue();
    }


    private List<Reason> importRatingGroups(List<RatingGroupDataExt> ratingGroupDatasForImport, String action) {
        List<Reason> reasons = Collectionz.newArrayList();
        for (RatingGroupDataExt importRatingGroup : ratingGroupDatasForImport) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Import Operation started for rating group: " + importRatingGroup.getName());
            }
            Reason reason = new Reason(importRatingGroup.getName());
            try {
                //Setting staff information to the rating group
                importRatingGroup.setCreatedByStaff((StaffData) request.getSession().getAttribute("staffData"));
                importRatingGroup.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                importRatingGroup.setModifiedByStaff(importRatingGroup.getCreatedByStaff());
                importRatingGroup.setModifiedDate(importRatingGroup.getCreatedDate());
                boolean isExistGroup = true;
                List<String> groups = new ArrayList<String>();
                if (Strings.isNullOrBlank(importRatingGroup.getGroupNames())) {
                    importRatingGroup.setGroups(CommonConstants.DEFAULT_GROUP_ID);
                    groups.add(CommonConstants.DEFAULT_GROUP_ID);
                } else {
                    isExistGroup = importExportUtil.getGroupIdsFromName(importRatingGroup, reason, groups);
                }
                if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
                    List<String> staffBelongingGroups = CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
                    importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(), reason, ACLModules.RATINGGROUP, ACLAction.IMPORT.name(), getStaffData().getUserName(), staffBelongingGroups, importRatingGroup, importRatingGroup.getName());

                    boolean flag = importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, ACLModules.RATINGGROUP, ACLAction.IMPORT.name(), getStaffData().getUserName());

                    if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
                        importRatingGroup.setGroups(Strings.join(",", groups));
                        importExportUtil.validateAndImportInformation(importRatingGroup, action, reason);
                        if (LogManager.getLogger().isDebugLogLevel()) {
                            LogManager.getLogger().debug(MODULE, "Import Operation finished for rating group: " + importRatingGroup.getName());
                        }
                    }
                }
                reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
                reasons.add(reason);

            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "Error while importing rating group data: " + importRatingGroup.getName());
                LogManager.getLogger().trace(MODULE, e);
                String errorMessage = "Failed to import rating group due to internal error. Kindly refer logs for further details";
                List<String> errors = new ArrayList<String>(2);
                errors.add(errorMessage);
                reason.setMessages("FAIL");
                reason.addFailReason(errors);
                reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
                reasons.add(reason);
            }
        }

        Collections.sort(reasons, new Comparator<Reason>() {
            @Override
            public int compare(Reason reason1, Reason reason2) {
                return reason1.getMessages().compareTo(reason2.getMessages());
            }
        });
        return reasons;
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

    private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
        return super.generateErrorLogsAndRedirect(Discriminators.RATING_GROUP, e, message, key, result);
    }

    public String getSelectedServiceTypes() {
        return selectedServiceTypes;
    }

    public void setSelectedServiceTypes(String selectedServiceTypes) {
        this.selectedServiceTypes = selectedServiceTypes;
    }

    public List<DataServiceTypeData> getAllServices() {
        return allServices;
    }

    public void setAllServices(List<DataServiceTypeData> allServices) {
        this.allServices = allServices;
    }

    @Override
    public String getIncludeProperties(){
        return RATING_GROUP_INCLUDE_PARAMETERS;
    }

}

