package com.elitecore.nvsmx.policydesigner.controller.pccrule;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.annotation.ActionChain;
import com.elitecore.corenetvertex.audit.AuditActions;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.core.validator.Reason;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.constants.ACLAction;
import com.elitecore.corenetvertex.pkg.constants.ACLModules;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DefaultServiceDataFlowData;
import com.elitecore.corenetvertex.pkg.dataservicetype.ServiceDataFlowData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleScope;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.sm.acl.GroupData;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.commons.model.acl.GroupDAO;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportEntityAccumulator;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportCTRL;
import com.elitecore.nvsmx.policydesigner.controller.util.ImportExportUtil;
import com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PCCRuleContainer;
import com.elitecore.nvsmx.policydesigner.model.pkg.pccrule.PccRuleDAO;
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
import org.apache.struts2.interceptor.ServletResponseAware;
import org.apache.struts2.interceptor.validation.SkipValidation;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletResponse;
import javax.xml.bind.JAXBException;
import java.io.BufferedWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.elitecore.corenetvertex.core.validator.BasicValidations.isPositiveNumber;

/**
 * @author Dhyani.Raval
 */
public class PccRuleCTRL extends ImportExportCTRL<PCCRuleData> implements ServletResponseAware {

    private static final long serialVersionUID = 1L;
    private static final String MODULE = "PCCRULE-CTRL";
    private static final String ERROR_POSTIVE_NUMERIC = "error.postive.numeric";
    private static final String ACTION_PCC_RULE_VIEW = "policydesigner/pccrule/PccRule/view";
    private static final String ACTION_PCC_RULE_SEARCH = "policydesigner/pccrule/PccRule/search";


    public static final String TYPE = "type";
    private static final Comparator<Reason> REASON_COMPARATOR = (reason1, reason2) -> reason1.getMessages().compareTo(reason2.getMessages());

    private PCCRuleData pccRule = new PCCRuleData();
    private List<RatingGroupData> ratingGroups = new ArrayList<RatingGroupData>();
    private JsonArray serviceDataFlowjson = null;
    private String actionChainUrl;
    Object[] messageParameter = {Discriminators.PCCRULE};
    private List<GroupData> staffBelongingGroupList = Collectionz.newArrayList();
    private List<String> showSelectedGroupsForUpdate = Collectionz.newArrayList();
    public static final String PCC_INCLUDE_PARAMETERS = ",dataList\\[\\d+\\]\\.id,dataList\\[\\d+\\]\\.name,dataList\\[\\d+\\]\\.type,dataList\\[\\d+\\]\\.monitoringKey,dataList\\[\\d+\\]\\.dataServiceTypeData,dataList\\[\\d+\\]\\.dataServiceTypeData.name,dataList\\[\\d+\\]\\.dataServiceTypeData.id,dataList\\[\\d+\\]\\.groups";

    private String serviceTypeRatingGroupMapJson;
    private String staffBelongingRatingGroupJson;
    private String selectedChargingKey;
    private String selectedChargingKeyId;

    private JsonArray qosProfileDetailsjson = null;
    private List<DataServiceTypeData> serviceTypeDataList = Collectionz.newArrayList();


    public String view() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called view()");
        }

        String typeValue = request.getParameter(TYPE);
        if(CommonConstants.CRBN.equalsIgnoreCase(typeValue)) {
            String crbnViewUrl = "policydesigner/chargingrulebasename/ChargingRuleBaseName/view?"+Attributes.CHARGING_RULE_BASE_NAME_ID+"="+getPccRuleId();
            setActionChainUrl(crbnViewUrl);
            return Results.REDIRECT_ACTION.getValue();
        }

        String pccRuleId = null;
        setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
        boolean requestFromQosView = Strings.toBoolean(request.getParameter("requestFromQosView"));
        String qosProfileId = getQosProfileId();
        String qosProfileDetailId = getQosProfileDetailId();

        try {
            pccRuleId = getPccRuleId();
            if (Strings.isNullOrBlank(pccRuleId) == true) {
                return redirectError(Discriminators.PCCRULE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
            }
            pccRule = CRUDOperationUtil.get(PCCRuleData.class, pccRuleId);
            if (PCCRuleScope.LOCAL.equals(pccRule.getScope())) {
                String belongingGroups = pccRule.getQosProfileDetails().get(0).getQosProfile().getGroupNames();
                if (Strings.isNullOrBlank(belongingGroups)) {
                    belongingGroups = GroupDAO.getGroupNames(SPLITTER.split(pccRule.getQosProfileDetails().get(0).getQosProfile().getGroups()));
                }
                pccRule.setGroupNames(belongingGroups);
            } else {
                String belonginsGroups = GroupDAO.getGroupNames(SPLITTER.split(pccRule.getGroups()));
                pccRule.setGroupNames(belonginsGroups);
            }
            request.setAttribute(Attributes.PCC_RULE_ID, pccRule.getId());
            if (pccRule == null) {
                return redirectError(Discriminators.PCCRULE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
            }
            String displayChargingKey = "";

            for (RatingGroupData ratingGroupData : getRatingGroupsFromDB()) {

                if (pccRule.getChargingKey().equalsIgnoreCase(ratingGroupData.getId())) {
                    displayChargingKey = ratingGroupData.getName() + "  (" + ratingGroupData.getIdentifier() + ") ";
                    setSelectedChargingKeyId(ratingGroupData.getId());
                    break;
                }
            }

            if (checkAuthorizationForLocal() == false) {
                setActionChainUrl("genericSearch/policydesigner/util/EliteGeneric/search");
                String unauthorizeMessages = Discriminators.PCCRULE + "'VIEW' not allowed";
                request.getSession().setAttribute(Attributes.UNAUTHORIZED_USER, unauthorizeMessages);
                return Results.REDIRECT_ERROR.getValue();
            }

            request.setAttribute(Attributes.PCCRULE_CHARGING_KEY, displayChargingKey);
            Gson gson = GsonFactory.defaultInstance();
            serviceDataFlowjson = gson.toJsonTree(pccRule.getServiceDataFlowList(), new TypeToken<List<ServiceDataFlowData>>() {
            }.getType()).getAsJsonArray();

            if (isGlobalPccRule(pccRule) == true) {
                createPccRuleAssociationList(pccRule);
            }
            request.setAttribute("requestFromQosView", requestFromQosView);
            if (Strings.isNullOrBlank(qosProfileId) == false) {
                QosProfileData qosProfileData = CRUDOperationUtil.get(QosProfileData.class, qosProfileId);
                if (qosProfileData != null) {
                    request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfileId);
                    request.setAttribute(Attributes.PKG_ID, qosProfileData.getPkgData().getId());
                    request.setAttribute(Attributes.QOS_PROFILE_DETAIL_ID, qosProfileDetailId);
                    request.setAttribute(Attributes.PKG_MODE, qosProfileData.getPkgData().getPackageMode());
                }
            }

            request.setAttribute("serviceDataFlow", serviceDataFlowjson);
            return Results.DETAIL.getValue();
        } catch (Exception e) {
            return generateErrorLogsAndRedirect(e, "Failed to view PccRule: " + pccRuleId + ".", ActionMessageKeys.VIEW_FAILURE.key, Results.REDIRECT_ERROR.getValue());
        }
    }

    private boolean checkAuthorizationForLocal() {
        if (PCCRuleScope.GLOBAL.equals(pccRule.getScope())) {
            return true;
        }

        List<String> dataPkgGroupsAsList = SPLITTER.split(pccRule.getQosProfileDetails().get(0).getQosProfile().getPkgData().getGroups());
        String groupNames = GroupDAO.getGroupNames(dataPkgGroupsAsList);
        pccRule.setGroupNames(groupNames);
        String staffBelongingGroups = (String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
        List<String> staffBelongingGroupsAsList = SPLITTER.split(staffBelongingGroups);

        for (String groupId : dataPkgGroupsAsList) {
            if (staffBelongingGroupsAsList.contains(groupId)) {
                return true;
            }
        }
        return false;
    }

    /**
     * Delete method will be performed in following way
     * <p> Check PCC Rule scope</p>
     * <pre>
     * LOCAL
     * Remove Relation & update as 'Deleted' Status<br/>
     * GLOBAL
     *     IF request is from QoS profile view page then remove only relation
     *     ELSE Check whether this PCC rule attach to any QoS profile
     *      IF YES return with error message of associated QoS profile
     *      ELSE update with 'Deleted' status
     *   </pre>
     */
    public String delete() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called delete()");
        }

        String typeValue = request.getParameter(TYPE);
        if(CommonConstants.CRBN.equalsIgnoreCase(typeValue)) {
            String crbnRemoveUrl = "policydesigner/qos/QosProfile/removeChargingRuleBaseName?"+Attributes.CHARGING_RULE_BASE_NAME_ID+"="+getPccRuleId()+"&"+Attributes.QOS_PROFILE_DETAIL_ID+"="+request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID)+"&"+Attributes.ENTITY_OLD_GROUPS +"="+request.getParameter(Attributes.GROUPIDS)+"&"+Attributes.GROUPIDS +"="+request.getParameter(Attributes.GROUPIDS);
            setActionChainUrl(crbnRemoveUrl);
            return Results.REDIRECT_ACTION.getValue();
        }

        String[] pccRuleIds = request.getParameterValues("ids");
        if (Arrayz.isNullOrEmpty(pccRuleIds)) {
            pccRuleIds = request.getParameterValues(Attributes.PCC_RULE_ID);
        }
        String qosProfileDetailId = request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID);
        String qosProfileId = request.getParameter(Attributes.QOS_PROFILE_ID);

        String isPccDeleteFromQoSProfileView = request.getParameter("fromQoSProfileView");
        if (isGlobalPccRule(pccRule) && Strings.isNullOrBlank(isPccDeleteFromQoSProfileView)) {
            setActionChainUrl(ACTION_PCC_RULE_SEARCH);
        } else {
            setParentIdKey(Attributes.QOS_PROFILE_ID);
            setParentIdValue(qosProfileId);
            if (PkgType.EMERGENCY.name().equalsIgnoreCase(getPkgType())) {
                setActionChainUrl(NVSMXCommonConstants.ACTION_EMERGENCY_QOS_PROFILE_VIEW);
            } else {
                setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
            }
        }

        try {
            if (Arrayz.isNullOrEmpty(pccRuleIds) == true) {
                return redirectError(Discriminators.PCCRULE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
            }

            for (String pccRuleId : pccRuleIds) {
                pccRule = CRUDOperationUtil.get(PCCRuleData.class, pccRuleId);
                if (pccRule == null) {
                    return redirectError(Discriminators.PCCRULE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(), false);
                }
                if (isGlobalPccRule(pccRule) == false) {
                    String message = Discriminators.PCCRULE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG+ pccRule.getName() + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + "Deleted";
                    CRUDOperationUtil.audit(pccRule, getAuditableResourceName(pccRule), AuditActions.DELETE, getStaffData(), request.getRemoteAddr(), pccRule.getHierarchy(), message);
                    pccRule.setStatus(CommonConstants.STATUS_DELETED);
                    // to remove the relation from TBLM_QOS_PCC_RELATION
                    if (Collectionz.isNullOrEmpty(pccRule.getQosProfileDetails()) == false) {
                        pccRule.getQosProfileDetails().remove(0);
                    }
                    CRUDOperationUtil.update(pccRule);
                } else if (isGlobalPccRule(pccRule) == true && Strings.isNullOrBlank(qosProfileDetailId) == false) {
                    QosProfileDetailData qosProfileDetailData = CRUDOperationUtil.get(QosProfileDetailData.class, qosProfileDetailId);
                    JsonObject jsonObjectOld = qosProfileDetailData.getQosProfile().toJson();

                    int index = qosProfileDetailData.getPccRules().indexOf(pccRule);
                    qosProfileDetailData.getPccRules().remove(index);

                    JsonObject jsonObjectNew = qosProfileDetailData.getQosProfile().toJson();
                    JsonArray difference = ObjectDiffer.diff(jsonObjectOld, jsonObjectNew);

                    QosProfileData qosProfileData = qosProfileDetailData.getQosProfile();
                    String message = Discriminators.QOS_PROFILE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG+ pccRule.getName() + qosProfileData.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG+ pccRule.getName() + "Updated";
                    CRUDOperationUtil.audit(qosProfileData, qosProfileData.getPkgData().getName(), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), difference, qosProfileData.getHierarchy(), message);
                    CRUDOperationUtil.update(qosProfileDetailData);
                    if (Strings.isNullOrBlank(qosProfileId) == true) {
                        qosProfileId = findQosProfileToRedirect(pccRule, qosProfileDetailId);
                    }
                } else if (isGlobalPccRule(pccRule) == true) {
                    setActionChainUrl(ACTION_PCC_RULE_SEARCH);
                    createPccRuleAssociationList(pccRule);
                    if (getQosProfileDetailsjson().size() != 0) {
                        addActionError(Discriminators.PCCRULE + " " + getText(ActionMessageKeys.DELETE_FAILURE.key));
                        addActionError("PCCRule is Configured with packages " + getAttachPkgList());
                        return Results.LIST.getValue();
                    }
                    String message = Discriminators.PCCRULE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG+ pccRule.getName() + pccRule.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG+ pccRule.getName() + "Deleted";
                    CRUDOperationUtil.audit(pccRule, getAuditableResourceName(pccRule), AuditActions.DELETE, getStaffData(), request.getRemoteAddr(), pccRule.getHierarchy(), message);
                    pccRule.setStatus(CommonConstants.STATUS_DELETED);
                    CRUDOperationUtil.update(pccRule);
                }

            }

            MessageFormat messageFormat = new MessageFormat(getText("delete.success"));
            addActionMessage(messageFormat.format(messageParameter));
            return Results.REDIRECT_TO_PARENT.getValue();
        } catch (Exception e) {
            if (isGlobalPccRule(pccRule) == true && Strings.isNullOrBlank(isPccDeleteFromQoSProfileView)) {
                return generateErrorLogsAndRedirect(e, "Error while fetching PccRule data for delete operation.", ActionMessageKeys.DELETE_FAILURE.key, Results.LIST.getValue());
            }
            return generateErrorLogsAndRedirect(e, "Error while fetching PccRule data for delete operation.", ActionMessageKeys.DELETE_FAILURE.key, Results.REDIRECT_TO_PARENT.getValue());
        }
    }


    private String findQosProfileToRedirect(PCCRuleData pccRuleData, String qosProfileDetailId) {
        String qosProfileId = null;
        if (Collectionz.isNullOrEmpty(pccRuleData.getQosProfileDetails()) == false) {
            for (QosProfileDetailData qosProfileDetail : pccRuleData.getQosProfileDetails()) {
                if (qosProfileDetailId.equalsIgnoreCase(qosProfileDetail.getId())) {
                    qosProfileId = qosProfileDetail.getQosProfile().getId();
                    break;
                }
            }
        }
        return qosProfileId;
    }

    private String getAttachPkgList() {
        String attachPackages = null;
        if ((getQosProfileDetailsjson().isJsonNull() == false
                && getQosProfileDetailsjson().size() != 0)) {
            attachPackages = Strings.join(",", getQosProfileDetailsjson(), (Function<Object, String>) input -> ((JsonObject) input).get("Package").getAsString());
        }
        return attachPackages;
    }


    private String getAuditableResourceName(PCCRuleData pccRuleData) {
        if (isGlobalPccRule(pccRuleData) == true) {
            return pccRuleData.getName();
        }
        return pccRuleData.getQosProfileDetails().get(0).getQosProfile().getPkgData().getName();

    }

    public String init() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called init()");
        }

        String pccRuleId = getPccRuleId();

        List<String> staffBelongingGroups = CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
        setServiceTypeRatingGroupMapJson(PccRuleDAO.getStaffBelongingServiceTypeJson(staffBelongingGroups));
        setStaffBelongingRatingGroupJson(PccRuleDAO.getStaffBelongingRatingGroupJson(staffBelongingGroups));

        if (Strings.isNullOrBlank(pccRuleId)) {

            if (pccRule.getScope() == null) {
                pccRule.setScope(PCCRuleScope.LOCAL);
            }

            String qosProfileDetailId = request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID);
            if (PCCRuleScope.LOCAL.equals(pccRule.getScope()) && Strings.isNullOrBlank(qosProfileDetailId) == false) {
                QosProfileDetailData qosProfileDetailData = CRUDOperationUtil.get(QosProfileDetailData.class, qosProfileDetailId);
                pccRule.getQosProfileDetails().add(qosProfileDetailData);
            }

            String qosProfileId = request.getParameter(Attributes.QOS_PROFILE_ID);
            String pkgId = request.getParameter(Attributes.PKG_ID);
            request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfileId);
            request.setAttribute(Attributes.PKG_ID, pkgId);
            request.setAttribute(Attributes.QOS_PROFILE_DETAIL_ID, qosProfileDetailId);
            request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfileId);
            setRatingGroups(getRatingGroupsFromDB());
            return Results.CREATE.getValue();
        }

        String typeValue = request.getParameter(TYPE);
        if (CommonConstants.CRBN.equalsIgnoreCase(typeValue)) {
            String crbnGroupIds = null;
            ChargingRuleBaseNameData chargingRuleBaseNameData = CRUDOperationUtil.get(ChargingRuleBaseNameData.class, getPccRuleId());
            if (chargingRuleBaseNameData != null) {
                crbnGroupIds = chargingRuleBaseNameData.getGroups();
            }
            String crbnUpdateUrl = "policydesigner/chargingrulebasename/ChargingRuleBaseName/init?" + Attributes.CHARGING_RULE_BASE_NAME_ID + "=" + getPccRuleId() + "&requestfromQosProfileView=true&" + Attributes.GROUPIDS + "=" + crbnGroupIds;
            setActionChainUrl(crbnUpdateUrl);
            return Results.REDIRECT_ACTION.getValue();
    }

        String qosProfileId = request.getParameter(Attributes.QOS_PROFILE_ID);
        boolean requestFromQosProfileView = Strings.toBoolean(request.getParameter("requestFromQosProfileView"));

        setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);

        try {

            request.setAttribute(Attributes.PCC_RULE_ID, pccRuleId);
        if (isGlobalPccRule(pccRule)) {
            setActionChainUrl(ACTION_PCC_RULE_VIEW);

        } else {
                setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
            }

            final PCCRuleData pccRule = CRUDOperationUtil.get(PCCRuleData.class, pccRuleId);
            request.getSession().setAttribute(Attributes.SCOPE, pccRule.getScope().name());
            if (PCCRuleScope.LOCAL.equals(pccRule.getScope())) {
                pccRule.setGroups(pccRule.getQosProfileDetails().get(0).getQosProfile().getGroups());
        }

            pccRule.setServiceTypeId(pccRule.getDataServiceTypeData().getId());
            RatingGroupData selectedRG = CRUDOperationUtil.get(RatingGroupData.class,pccRule.getChargingKey());
            setSelectedChargingKey(GsonFactory.defaultInstance().toJson(selectedRG));

            setPccRule(pccRule);
            if (isGlobalPccRule(pccRule) == false || requestFromQosProfileView) {
                if (Strings.isNullOrBlank(qosProfileId) == false) {
                    QosProfileData qosProfileData = CRUDOperationUtil.get(QosProfileData.class, qosProfileId);
                    request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfileData.getId());
                    request.setAttribute(Attributes.PKG_ID, qosProfileData.getPkgData().getId());
                }
            }
            request.setAttribute("requestFromQosProfileView", requestFromQosProfileView);
            this.ratingGroups = getRatingGroupsFromDB();
            Collectionz.filter(ratingGroups, ratingGroup -> {
                if (pccRule.getDataServiceTypeData().getRatingGroupDatas().contains(ratingGroup)) {
                    return false;
                }
                return true;
            });
            return Results.UPDATE.getValue();
        } catch (Exception e) {
            return generateErrorLogsAndRedirect(e, "Error while fetching PCC Rule for update operation.", ActionMessageKeys.UPDATE_FAILURE.key, Results.REDIRECT_ERROR.getValue());
        }

    }

    private String getQosProfileDetailId() {

        String qosProfileDetailId = request.getParameter(Attributes.QOS_PROFILE_DETAIL_ID);
        if (Strings.isNullOrBlank(qosProfileDetailId)) {
            qosProfileDetailId = (String) request.getAttribute(Attributes.QOS_PROFILE_DETAIL_ID);
            if (Strings.isNullOrBlank(qosProfileDetailId)) {
                qosProfileDetailId = request.getParameter("quotoProfile.id");
            }
        }
        return qosProfileDetailId;
    }

    private String getQosProfileId() {

        String qosProfileId = request.getParameter(Attributes.QOS_PROFILE_ID);
        if (Strings.isNullOrBlank(qosProfileId)) {
            qosProfileId = (String) request.getAttribute(Attributes.QOS_PROFILE_ID);
            if (Strings.isNullOrBlank(qosProfileId)) {
                qosProfileId = request.getParameter("quotaProfile.pkgData.id");
            }
        }
        return qosProfileId;
    }

    private String getPccRuleId() {
        String qosProfileId = request.getParameter(Attributes.PCC_RULE_ID);
        if (Strings.isNullOrBlank(qosProfileId)) {
            qosProfileId = (String) request.getAttribute(Attributes.PCC_RULE_ID);
            if (Strings.isNullOrBlank(qosProfileId)) {
                qosProfileId = request.getParameter("pccRule.id");
            }
        }
        return qosProfileId;
    }


    @InputConfig(resultName = InputConfigConstants.CREATE)
    public String create() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called Create()");
        }
        setActionURLAndParentValue(pccRule);
        try {
            String qosProfileDetailId = getQosProfileDetailId();
            String qosProfileId = getQosProfileId();

            if (Strings.isNullOrBlank(qosProfileDetailId) == false) {
                QosProfileDetailData qosProfileDetail = CRUDOperationUtil.get(QosProfileDetailData.class, qosProfileDetailId);
                pccRule.getQosProfileDetails().add(qosProfileDetail);
            }

            setDataServiceTypes();

            setServiceDataFlow();

            pccRule.setCreatedDateAndStaff(getStaffData());

            if (Strings.isNullOrBlank(getGroupIds())) {
                pccRule.setGroups(CommonConstants.DEFAULT_GROUP_ID);
            } else {
                pccRule.setGroups(getGroupIds());
            }
            CRUDOperationUtil.save(pccRule);
            String message = Discriminators.PCCRULE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG+ pccRule.getName() + pccRule.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG+ pccRule.getName() + "Created";
            CRUDOperationUtil.audit(pccRule, getAuditableResourceName(pccRule), AuditActions.CREATE, getStaffData(), request.getRemoteAddr(), pccRule.getHierarchy(), message);
            request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfileId);
            request.setAttribute(Attributes.PCC_RULE_ID, pccRule.getId());

            MessageFormat messageFormat = new MessageFormat(getText("create.success"));
            addActionMessage(messageFormat.format(messageParameter));
            if(isGlobalPccRule(pccRule)) {
                return Results.REDIRECT_ACTION.getValue();
            }
            return Results.REDIRECT_TO_PARENT.getValue();
        } catch (Exception e) {
            if(isGlobalPccRule(pccRule) == true || Strings.isNullOrBlank(pccRule.getId())) {
                return generateErrorLogsAndRedirect(e, "Error while fetching PccRule data for create operation.", ActionMessageKeys.CREATE_FAILURE.key, Results.LIST.getValue());
            }
            return generateErrorLogsAndRedirect(e, "Error while fetching PccRule data for create operation.", ActionMessageKeys.CREATE_FAILURE.key, Results.DISPATCH_VIEW.getValue());
        }
    }

    private void setActionURLAndParentValue(PCCRuleData pccRule) {
        if (PkgType.EMERGENCY.name().equalsIgnoreCase(getPkgType())) {
            setActionChainUrl(NVSMXCommonConstants.ACTION_EMERGENCY_QOS_PROFILE_VIEW);
            setParentIdKey(Attributes.QOS_PROFILE_ID);
            setParentIdValue(getQosProfileId());
        } else if(isGlobalPccRule(pccRule)) {
            setActionChainUrl(ACTION_PCC_RULE_SEARCH);

        } else {
            setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
            setParentIdKey(Attributes.QOS_PROFILE_ID);
            setParentIdValue(getQosProfileId());
        }
    }

    private void setServiceDataFlow() {
        filterEmptyServiceDataFlowDatas(pccRule.getServiceDataFlowList());
        for (ServiceDataFlowData serviceDataFlowData : pccRule.getServiceDataFlowList()) {
            serviceDataFlowData.setPccRule(pccRule);
        }
    }

    /**
     * Filter empty or null data from the list
     *
     * @param serviceDataFlowDatas
     */
    private void filterEmptyServiceDataFlowDatas(List<ServiceDataFlowData> serviceDataFlowDatas) {
        Collectionz.filter(serviceDataFlowDatas, serviceDataFlowData -> {
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
        });
    }


    @SkipValidation
    public String initUpdate() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called initUpdate()");
        }


        String typeValue = request.getParameter(TYPE);
        if(CommonConstants.CRBN.equalsIgnoreCase(typeValue)) {
            String crbnUpdateUrl = "policydesigner/chargingrulebasename/ChargingRuleBaseName/initUpdate?"+Attributes.CHARGING_RULE_BASE_NAME_ID+"="+getPccRuleId()+"&requestfromQosProfileView=true";
            setActionChainUrl(crbnUpdateUrl);
            return Results.REDIRECT_ACTION.getValue();
        }

        String pccRuleId = request.getParameter(Attributes.PCC_RULE_ID);
        String qosProfileId = request.getParameter(Attributes.QOS_PROFILE_ID);
        boolean requestFromQosProfileView = Strings.toBoolean(request.getParameter("requestFromQosProfileView"));

        List<GroupData> groupDatas = (List<GroupData>) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP);
        staffBelongingGroupList.addAll(groupDatas);

        setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
        try {
            if (Strings.isNullOrBlank(pccRuleId)) {
                pccRuleId = (String) request.getSession().getAttribute(Attributes.PCC_RULE_ID);
                if (Strings.isNullOrBlank(pccRuleId)) {
                    return redirectError(Discriminators.PCCRULE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
                }

            } else {
                request.getSession().setAttribute(Attributes.PCC_RULE_ID, pccRuleId);
                if (isGlobalPccRule(pccRule)) {
                    setActionChainUrl(ACTION_PCC_RULE_VIEW);

                } else {
                    setActionChainUrl(NVSMXCommonConstants.ACTION_QOS_PROFILE_VIEW);
                }
            }

            final PCCRuleData pccRule = CRUDOperationUtil.get(PCCRuleData.class, pccRuleId);
            request.getSession().setAttribute(Attributes.SCOPE, pccRule.getScope().name());
            if (PCCRuleScope.LOCAL.equals(pccRule.getScope())) {
                pccRule.setGroups(pccRule.getQosProfileDetails().get(0).getQosProfile().getGroups());
            } else {
                setShowSelectedGroupsForUpdate(Strings.splitter(',').trimTokens().split(pccRule.getGroups()));
            }
            pccRule.setServiceTypeId(pccRule.getDataServiceTypeData().getId());
            setPccRule(pccRule);
            if (isGlobalPccRule(pccRule) == false || requestFromQosProfileView) {
                if (Strings.isNullOrBlank(qosProfileId) == false) {
                    QosProfileData qosProfileData = CRUDOperationUtil.get(QosProfileData.class, qosProfileId);
                    request.getSession().setAttribute(Attributes.QOS_PROFILE_DETAIL_ID, qosProfileData.getId());
                    request.getSession().setAttribute(Attributes.QOS_PROFILE_ID, qosProfileData.getId());
                    request.setAttribute(Attributes.PKG_ID, qosProfileData.getPkgData().getId());
                }
            }
            request.setAttribute("requestFromQosProfileView", requestFromQosProfileView);
            this.ratingGroups = getRatingGroupsFromDB();
            Collectionz.filter(ratingGroups, ratingGroup -> {
                if (pccRule.getDataServiceTypeData().getRatingGroupDatas().contains(ratingGroup)) {
                    return false;
                }
                return true;
            });
            return Results.UPDATE.getValue();
        } catch (Exception e) {
            if(isGlobalPccRule(pccRule) == true) {
                return generateErrorLogsAndRedirect(e, "Error while fetching PCC Rule for update operation.", ActionMessageKeys.UPDATE_FAILURE.key, Results.LIST.getValue());
            }
            return generateErrorLogsAndRedirect(e, "Error while fetching PCC Rule for update operation.", ActionMessageKeys.UPDATE_FAILURE.key, Results.UPDATE.getValue());

        }

    }

    @InputConfig(resultName = InputConfigConstants.UPDATE)
    public String update() {

        LogManager.getLogger().debug(MODULE, "Method called update()");
        setActionURLAndParentValue(pccRule);

        try {
            String qosProfileId = getQosProfileId();
            PCCRuleData pccRuleInDb = CRUDOperationUtil.get(PCCRuleData.class, pccRule.getId());

            List<QosProfileDetailData> qosProfileDetailDataList = pccRuleInDb.getQosProfileDetails();
            pccRule.getQosProfileDetails().clear();
            pccRule.getQosProfileDetails().addAll(qosProfileDetailDataList);

            setDataServiceTypes();
            setServiceDataFlow();
            pccRule.setModifiedDateAndStaff(getStaffData());
            if (Strings.isNullOrBlank(getGroupIds())) {
                pccRule.setGroups(CommonConstants.DEFAULT_GROUP_ID);
            } else {
                pccRule.setGroups(getGroupIds());
            }
            CRUDOperationUtil.merge(pccRule);
            String message = Discriminators.PCCRULE + NVSMXCommonConstants.START_BOLD_TEXT_TAG + NVSMXCommonConstants.START_ITALIC_TEXT_TAG+ pccRule.getName() + pccRule.getName() + NVSMXCommonConstants.CLOSE_BOLD_TEXT_TAG + NVSMXCommonConstants.CLOSE_ITALIC_TEXT_TAG+ pccRule.getName() + "Updated";
            CRUDOperationUtil.audit(pccRule, getAuditableResourceName(pccRule), AuditActions.UPDATE, getStaffData(), request.getRemoteAddr(), pccRule.getHierarchy(), message);
            request.setAttribute(Attributes.PCC_RULE_ID, pccRule.getId());
            request.setAttribute(Attributes.QOS_PROFILE_ID, qosProfileId);
            MessageFormat messageFormat = new MessageFormat(getText("update.success"));
            addActionMessage(messageFormat.format(messageParameter));
            if(isGlobalPccRule(pccRule) == true) {
                return Results.REDIRECT_ACTION.getValue();
            }
            return Results.REDIRECT_TO_PARENT.getValue();

        } catch (Exception e) {
            if(isGlobalPccRule(pccRule) == true) {
                return generateErrorLogsAndRedirect(e, "Error while updating PCC Rule  of name(ID): " + pccRule.getName() + "(" + pccRule.getId() + ").", ActionMessageKeys.UPDATE_FAILURE.key, Results.LIST.getValue());
            }
            return generateErrorLogsAndRedirect(e, "Error while updating PCC Rule  of name(ID): " + pccRule.getName() + "(" + pccRule.getId() + ").", ActionMessageKeys.UPDATE_FAILURE.key, Results.DISPATCH_VIEW.getValue());
        }
    }

    private void setDataServiceTypes() {
        if (Strings.isNullOrEmpty(getPccRule().getServiceTypeId()) == false) {
            DataServiceTypeData serviceType = CRUDOperationUtil.get(DataServiceTypeData.class, getPccRule().getServiceTypeId());
            getPccRule().setDataServiceTypeData(serviceType);
        }
        setServiceTypeDataList(CRUDOperationUtil.findAllWhichIsNotDeleted(DataServiceTypeData.class));

    }


    public List<DataServiceTypeData> getServiceTypeDataList() {
        return serviceTypeDataList;
    }
    public void setServiceTypeDataList(List<DataServiceTypeData> serviceTypeDataList) {
        this.serviceTypeDataList = serviceTypeDataList;
    }

    public PCCRuleData getPccRule() {
        return pccRule;
    }

    public void setPccRule(PCCRuleData pccRule) {
        this.pccRule = pccRule;
    }


    public String getActionChainUrl() {
        return actionChainUrl;
    }

    @ActionChain(name = "actionChainUrlMethod")
    public void setActionChainUrl(String actionChainUrl) {
        this.actionChainUrl = actionChainUrl;
    }


    /**
     * get the all DefaultServiceDataFlow's data from database
     */
    public void getDefaultServiceFlow() {
        response.setContentType("application/json");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            String serviceId = request.getParameter("serviceId");
            DataServiceTypeData serviceType = new DataServiceTypeData();
            serviceType.setId(serviceId);
            List<DefaultServiceDataFlowData> defaultServiceDataFlows = PccRuleDAO.getDefaultServiceDataFlows(serviceType);
            Gson gson = GsonFactory.defaultInstance();
            JsonArray jsonArray = gson.toJsonTree(defaultServiceDataFlows).getAsJsonArray();
            out.println(jsonArray);
            out.flush();
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Error while fetching DefaultServiceDataFlow data for delete operation. Reason: " + e.getMessage());
            LogManager.getLogger().trace(MODULE, e);
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }
    @Override
    public PCCRuleData getModel() {
        return pccRule;
    }

    @Override
    public void setServletResponse(HttpServletResponse response) {
        this.response = response;

    }



    public List<RatingGroupData> getRatingGroups() {
        return ratingGroups;
    }

    public void setRatingGroups(List<RatingGroupData> ratingGroups) {
        this.ratingGroups = ratingGroups;
    }

    private List<RatingGroupData> getRatingGroupsFromDB() {
        return CRUDOperationUtil.findAllWhichIsNotDeleted(RatingGroupData.class);
    }

    public enum PCCRuleType {
        STATIC("Static"),
        DYNAMIC("Dynamic");

        public final String value;

        private PCCRuleType(String value) {
            this.value = value;
        }

        public String getValue() {
            return value;
        }
    }


    @Override
    public String search() {
        LogManager.getLogger().debug(MODULE, "Method called search()");
        setDataServiceTypes();
        return Results.LIST.getValue();
    }

    public String searchCriteria() {
        LogManager.getLogger().debug(MODULE, "Method called searchCriteria()");
        return Results.LIST.getValue();
    }


    @Override
    public void validate() {

        if (pccRule.getMbrdl() == null && pccRule.getGbrdl() == null) {
            if (pccRule.getQci() < 5) {
                addFieldError("pccRule.mbrdl", getText("pccrule.qos.error"));
                addFieldError("pccRule.gbrdl", getText("pccrule.qos.error"));
            } else {
                addFieldError("pccRule.mbrdl", getText("pccrule.qos.mbrdl.error"));
            }
            return;
        }


        if (pccRule.getMbrdl() != null && isPositiveNumber(pccRule.getMbrdl()) == false) {
            addFieldError("pccRule.mbrdl", getText(ERROR_POSTIVE_NUMERIC));
            return;
        }
        if (isValidQos("pccRule.mbrdl", pccRule.getMbrdl(), pccRule.getMbrdlUnit()) == false) {
            return;
        }
        if (pccRule.getMbrul() != null && isPositiveNumber(pccRule.getMbrul()) == false) {
            addFieldError("pccRule.mbrul", getText(ERROR_POSTIVE_NUMERIC));
            return;
        }
        if (isValidQos("pccRule.mbrul", pccRule.getMbrul(), pccRule.getMbrulUnit()) == false) {
            return;
        }
        if (pccRule.getGbrdl() != null && isPositiveNumber(pccRule.getGbrdl()) == false) {
            addFieldError("pccRule.gbrdl", getText(ERROR_POSTIVE_NUMERIC));
            return;
        }
        if (isValidQos("pccRule.gbrdl", pccRule.getGbrdl(), pccRule.getGbrdlUnit()) == false) {
            return;
        }
        if (pccRule.getGbrul() != null && isPositiveNumber(pccRule.getGbrul()) == false) {
            addFieldError("pccRule.gbrul", getText(ERROR_POSTIVE_NUMERIC));
            return;
        }
        if (isValidQos("pccRule.gbrul", pccRule.getGbrul(), pccRule.getGbrulUnit()) == false) {
            return;
        }
        if (pccRule.getSliceTotal() == null && pccRule.getSliceDownload() == null && pccRule.getSliceUpload() == null && pccRule.getSliceTime() == null && pccRule.getUsageMonitoring() == true) {
            addFieldError("pccRule.sliceTime", getText("pccrule.usagemonitoring.validation"));
            addFieldError("pccRule.sliceTotal", "");
            addFieldError("pccRule.sliceDownload", "");
            addFieldError("pccRule.sliceUpload", "");
            return;
        }
        getSliceTotalValidation(pccRule);

    }


    private void getSliceTotalValidation(PCCRuleData pccRule) {
        Long sliceTotal = pccRule.getSliceTotal();
        Long sliceDownload = pccRule.getSliceDownload();
        Long sliceUpload = pccRule.getSliceUpload();
        Long sliceTime = pccRule.getSliceTime();
        if (sliceTotal != null
                && isPositiveNumber(sliceTotal) == false) {
            addFieldError("pccRule.sliceTotal", getText(ERROR_POSTIVE_NUMERIC));
            return;
        }
        if (isValidQuota("pccRule.sliceTotal", sliceTotal, pccRule.getSliceTotalUnit()) == false) {
            return;
        }
        if (sliceDownload != null
                && isPositiveNumber(sliceDownload) == false) {
            addFieldError("pccRule.sliceDownload", getText(ERROR_POSTIVE_NUMERIC));
            return;
        }
        if (isValidQuota("pccRule.sliceDownload", sliceDownload, pccRule.getSliceDownloadUnit()) == false) {
            return;
        }
        if (sliceUpload != null
                && isPositiveNumber(sliceUpload) == false) {
            addFieldError("pccRule.sliceUpload", getText(ERROR_POSTIVE_NUMERIC));
            return;
        }
        if (isValidQuota("pccRule.sliceUpload", sliceUpload, pccRule.getSliceUploadUnit()) == false) {
            return;
        }
        if (sliceTime != null
                && isPositiveNumber(sliceTime) == false) {
            addFieldError("pccRule.sliceTime", getText(ERROR_POSTIVE_NUMERIC));
            return;
        }
        isValidSliceTime("pccRule.sliceTime", sliceTime, pccRule.getSliceTimeUnit());

    }

    private boolean isValidSliceTime(String fieldId, Long value, String unit) {
        if (value == null) {
            return true;
        }

        boolean isValidSliceTime = true;
        Long maxSliceTimeValueInSecond = 86400L;
        Long maxSliceTimeValueInMinute = 1440L;
        Long maxSliceTimeValueInHour = 24L;
        if (unit.equalsIgnoreCase(TimeUnit.HOUR.name())) {
            isValidSliceTime = (value > maxSliceTimeValueInHour) ? false : true;
            if (isValidSliceTime == false) {
                addFieldError(fieldId, "Max allowed value in HOUR is " + maxSliceTimeValueInHour);
            }
        } else if (unit.equalsIgnoreCase(TimeUnit.MINUTE.name())) {
            isValidSliceTime = (value > maxSliceTimeValueInMinute) ? false : true;
            if (isValidSliceTime == false) {
                addFieldError(fieldId, "Max allowed value in MINUTE is " + maxSliceTimeValueInMinute);
            }
        } else if (unit.equalsIgnoreCase(TimeUnit.SECOND.name())) {
            isValidSliceTime = (value > maxSliceTimeValueInSecond) ? false : true;
            if (isValidSliceTime == false) {
                addFieldError(fieldId, "Max allowed value in SECOND is " + maxSliceTimeValueInSecond);
            }
        } else {
            isValidSliceTime = false;
        }
        return isValidSliceTime;
    }


    private boolean isValidQuota(String fieldId, Long value, String unit) {
        if (value == null) {
            return true;
        }

        DataUnit dataUnit = DataUnit.valueOf(unit);
        if (dataUnit == null) {
            addFieldError(fieldId, "Invalid unit is configured");
            return false;
        } else if (dataUnit.isInRange(value) == false) {
            addFieldError(fieldId, "Max value in " + dataUnit.name() + " is " + dataUnit.maxValueForSlice);
            return false;
        }
        return true;
    }

    private boolean isValidQos(String fieldId, Long value, String unit) {
        if (value == null) {
            return true;
        }
        QoSUnit qosUnit = QoSUnit.valueOf(unit);
        if (qosUnit == null) {
            addFieldError(fieldId, "Invalid unit is configured");
            return false;
        } else if (qosUnit.isInRange(value) == false) {
            addFieldError(fieldId, "Max Value in " + qosUnit.name() + " is " + qosUnit.maxValueForQoS);
            return false;
        }
        return true;
    }

    private boolean isGlobalPccRule(PCCRuleData pccRuleData) {
        return (PCCRuleScope.GLOBAL == pccRuleData.getScope());
    }

    @Override
    public void prepareForSubClass() throws Exception {
        setDataServiceTypes();
    }


    private String getPkgType() {
        return (String) request.getSession().getAttribute("pkgType");
    }

    @Override
    protected List<PCCRuleData> getSearchResult(String criteriaJson, Class beanType, int startIndex, int maxRecords, String sortColumnName, String sortColumnOrder, String staffBelongingGroups) throws Exception {
        String scope = request.getParameter("scope");

        String qosProfileDetailid = request.getParameter("qosProfileDetailId");
        boolean isCallFromModal = Strings.toBoolean(request.getParameter("isCallFromModal"));

        PCCRuleScope pccRuleScope = PCCRuleScope.fromValue(scope);

        if (pccRuleScope == null) {
            pccRuleScope = PCCRuleScope.LOCAL;
        }

        if (qosProfileDetailid != null && isCallFromModal) {
            return PccRuleDAO.getAvailablePCCRuleList(qosProfileDetailid, pccRuleScope, staffBelongingGroups);
        }

        if (Strings.isNullOrBlank(criteriaJson) == false) {
            Gson gson = GsonFactory.defaultInstance();
            PCCRuleData pccRuleData = gson.fromJson(criteriaJson, PCCRuleData.class);
            pccRuleData.setScope(pccRuleScope);

            return PccRuleDAO.searchByCriteria(pccRuleData, startIndex, maxRecords, sortColumnName, sortColumnOrder, staffBelongingGroups);
        } else {
            PCCRuleData pccRuleData = new PCCRuleData();
            pccRuleData.setScope(pccRuleScope);
            return PccRuleDAO.searchByCriteria(pccRuleData, startIndex, maxRecords, sortColumnName, sortColumnOrder, staffBelongingGroups);
        }

    }

    @Override
    public String getIncludeProperties(){
        return PCC_INCLUDE_PARAMETERS;
    }

    private void createPccRuleAssociationList(PCCRuleData pccRuleData) {
        qosProfileDetailsjson = new JsonArray();
        JsonObject jsonObject = null;
        for (QosProfileDetailData qosProfileDetailData : pccRuleData.getQosProfileDetails()) {
            if ((qosProfileDetailData.getQosProfile().getStatus().equalsIgnoreCase(CommonConstants.STATUS_DELETED)) == false) {
                jsonObject = new JsonObject();
                jsonObject.addProperty("Qos Profile", qosProfileDetailData.getQosProfile().getName());
                jsonObject.addProperty("qosProfileId", qosProfileDetailData.getQosProfile().getId());
                jsonObject.addProperty("Package", qosProfileDetailData.getQosProfile().getPkgData().getName());
                jsonObject.addProperty("pkgId", qosProfileDetailData.getQosProfile().getPkgData().getId());
                if (qosProfileDetailsjson.contains(jsonObject) == false) {
                    qosProfileDetailsjson.add(jsonObject);
                }
            }
        }
    }

    public JsonArray getQosProfileDetailsjson() {
        return qosProfileDetailsjson;
    }

    public void setQosProfileDetailsjson(JsonArray qosProfileDetailsjson) {
        this.qosProfileDetailsjson = qosProfileDetailsjson;
    }


    public String importPCCRule() throws ServletException {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called importPCCRule()");
        }

        if (isImportExportOperationInProgress() == true) {
            return redirectError(Discriminators.PCCRULE, "pccrule.importexport.operation", Results.LIST.getValue(), false);
        }
        if (getImportedFile() == null) {
            LogManager.getLogger().error(MODULE, "Error while importing PCC Rules. Reason: File not found");
            return redirectError(Discriminators.PCCRULE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.LIST.getValue(), false);
        }
        try {
            if (CommonConstants.TEXT_XML_TYPE.equals(getImportedFileContentType()) == false) {
                LogManager.getLogger().error(MODULE, "Error while importing PCC Rule. Reason: Invalid File type is configured. Only XML_FILE_EXT File is supported for importing PCC Rule");
                makeImportExportOperationInProgress(false);
                return redirectError(Discriminators.PCCRULE, "pccrule.importexport.invalidfiletype", Results.LIST.getValue(), false);

            }
            PCCRuleContainer pccRuleContainer = ConfigUtil.deserialize(getImportedFile(), PCCRuleContainer.class);
            if (pccRuleContainer == null) {
                return Results.REDIRECT_ERROR.getValue();
            }

            List<PCCRuleData> pccRules = pccRuleContainer.getPccRules();

            skipPCCRuleHaveNullName(pccRules);

            request.getSession().setAttribute(Attributes.PCCRULES, pccRules);

            Gson gson = GsonFactory.defaultInstance();
            JsonArray importPCCRuleJson = gson.toJsonTree(pccRules, new TypeToken<List<PCCRuleData>>() {
            }.getType()).getAsJsonArray();

            request.setAttribute("importedPCCRules", importPCCRuleJson);

            return Results.IMPORT_PCC_RULE.getValue();

        } catch (JAXBException e) {
            makeImportExportOperationInProgress(false);
            return generateErrorLogsAndRedirect(e, "Error while importing PCC Rule due to XML_FILE_EXT processing failure.", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
        } catch (Exception e) {
            makeImportExportOperationInProgress(false);
            return generateErrorLogsAndRedirect(e, "Error while importing PCC Rule.", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
        }
    }

    private void skipPCCRuleHaveNullName(List<PCCRuleData> pccRules) {
        /*
        This method is used to remove the PCCRule have null name from the list of pccRules.
        It will also set proper message in request attribute(invalidEntityMessage) to display in GUI.
        */

        Predicate<PCCRuleData> predicate = pccRuleData -> {
            if (Strings.isNullOrBlank(pccRuleData.getName())) {
                if (LogManager.getLogger().isInfoLogLevel()) {
                    LogManager.getLogger().info(MODULE, "Found PCCRule with null name. Skipping Import process for PCCRule: " + pccRuleData);
                }
                return false;
            }
            return true;
        };

        String message = ImportExportUtil.removeInvalidEntitiesAndGetMessage(pccRules, predicate, Discriminators.PCCRULE);
        request.setAttribute(INVALID_ENTITY_MESSAGE,message);
    }


    public String importData() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called importData()");
        }
        try {
            String selectedPCCRuleIndexes = request.getParameter(Attributes.SELECTED_INDEXES);
            String action = request.getParameter(Attributes.USER_ACTION);

            if (isImportExportOperationInProgress()) {
                return redirectError(Discriminators.PCCRULE , "pccrule.importexport.operation", Results.LIST.getValue(), false);
            }

            List<PCCRuleData> pccRuleDatas = (List<PCCRuleData>) request.getSession().getAttribute(Attributes.PCCRULES);

            List<PCCRuleData> pccRules = Collectionz.newArrayList();

            if (Strings.isNullOrBlank(selectedPCCRuleIndexes) == false && Collectionz.isNullOrEmpty(pccRuleDatas) == false) {
                makeImportExportOperationInProgress(true);
                pccRules = new ImportEntityAccumulator<PCCRuleData>(pccRuleDatas, selectedPCCRuleIndexes).get();
                                }

            List<Reason> reasons = importPccRules(pccRules, action);

            Gson gson = GsonFactory.defaultInstance();
            JsonArray importPCCRulesResultJson = gson.toJsonTree(reasons, new TypeToken<List<Reason>>() {
            }.getType()).getAsJsonArray();
            makeImportExportOperationInProgress(false);
            request.setAttribute("importStatus", importPCCRulesResultJson);
        } catch (Exception e) {
            makeImportExportOperationInProgress(false);
            return generateErrorLogsAndRedirect(e, "Error while importing PccRule Data.", ActionMessageKeys.IMPORT_FAILURE.key, Results.LIST.getValue());
        }
        return Results.IMPORT_STATUS_REPORT.getValue();
    }


    private List<Reason> importPccRules(List<PCCRuleData> pccRulesForImport, String action) {

        List<Reason> reasons = Collectionz.newArrayList();
        if (Collectionz.isNullOrEmpty(pccRulesForImport)) {
            return reasons;
        }
        for (PCCRuleData importPCCRule : pccRulesForImport) {
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Import Operation started for PCC Rule: " + importPCCRule.getName());
            }
            Reason reason = new Reason(importPCCRule.getName());
            try {

                importPCCRule.setCreatedByStaff(getStaffData());
                importPCCRule.setCreatedDate(new Timestamp(System.currentTimeMillis()));
                importPCCRule.setModifiedByStaff(importPCCRule.getCreatedByStaff());
                importPCCRule.setModifiedDate(importPCCRule.getCreatedDate());
                importPCCRule.setScope(PCCRuleScope.GLOBAL);

                boolean isExistGroup = true;
                List<String> groups = new ArrayList<String>();
                if (Strings.isNullOrBlank(importPCCRule.getGroupNames())) {
                    importPCCRule.setGroups(CommonConstants.DEFAULT_GROUP_ID);
                    groups.add(CommonConstants.DEFAULT_GROUP_ID);
                } else {
                    isExistGroup = importExportUtil.getGroupIdsFromName(importPCCRule, reason, groups);
                }
                if (isExistGroup == true && CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
                    List<String> staffBelongingGroups = CommonConstants.COMMA_SPLITTER.split((String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS));
                    importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(), reason, ACLModules.GLOBALPCCRULE, ACLAction.IMPORT.name(), getStaffData().getUserName(), staffBelongingGroups, importPCCRule, importPCCRule.getName());

                    importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, ACLModules.GLOBALPCCRULE, ACLAction.IMPORT.name(), getStaffData().getUserName());

                    if (CommonConstants.FAIL.equalsIgnoreCase(reason.getMessages()) == false) {
                        importPCCRule.setGroups(Strings.join(",", groups));
                        importExportUtil.validateAndImportInformation(importPCCRule, action, reason);
                        if (LogManager.getLogger().isDebugLogLevel()) {
                            LogManager.getLogger().debug(MODULE, "Import Operation finished for pcc rule: " + importPCCRule.getName());
                        }
                    }
                }
                reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
                reasons.add(reason);

            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "Error while importing pcc rules : " + importPCCRule.getName() + " Reason: " + e.getMessage());
                LogManager.getLogger().trace(MODULE, e);
                String errorMessage = "Failed to import pcc rule due to internal error. Kindly refer logs for further details";
                List<String> errors = new ArrayList<String>(2);
                errors.add(errorMessage);
                reason.setMessages("FAIL");
                reason.addFailReason(errors);
                reason.setRemarks(getRemarksFromSubReasons(reason.getSubReasons()));
                reasons.add(reason);
            }
        }

        Collections.sort(reasons, REASON_COMPARATOR);
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

    public String export() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called export()");
        }
        List<PCCRuleData> pccRuleDatas;
        String[] ids = request.getParameterValues("ids");
        String fileName = "exportGlobalPCCRule_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + CommonConstants.XML;
        BufferedWriter writer = null;
        PrintWriter out = null;
        try {
            Map<String, String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
            if (isImportExportOperationInProgress()) {
                return redirectError(Discriminators.PCCRULE, "importexport.operation.inprogress.error", Results.LIST.getValue(),false);
            }
            makeImportExportOperationInProgress(true);

            if (Arrayz.isNullOrEmpty(ids) == true) {
                makeImportExportOperationInProgress(false);
                return redirectError(Discriminators.PCCRULE, ActionMessageKeys.DATA_NOT_FOUND.key, Results.REDIRECT_ERROR.getValue(),false);
            }

            PCCRuleContainer pccRuleContainer = new PCCRuleContainer();
            pccRuleDatas = CRUDOperationUtil.getAllByIds(PCCRuleData.class, ids);

            setGroupsInPCCRules(pccRuleDatas, groupNamesBasedOnId);

            pccRuleContainer.setPccRules(pccRuleDatas);
            StringWriter stringWriter = new StringWriter();
            ConfigUtil.serialize(stringWriter, PCCRuleContainer.class, pccRuleContainer);

            String pccRuleInfo = stringWriter.toString();
            if (Strings.isNullOrBlank(pccRuleInfo)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, "Can not find content for the PCC Rules");
                }
                throw new Exception("Can not find data as string for the PCC Rule");
            }
            response.setContentType(CommonConstants.TEXT_XML_TYPE);
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");

            out = response.getWriter();
            writer = new BufferedWriter(out);
            writer.write(pccRuleInfo, 0, pccRuleInfo.length());
            writer.flush();


            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "All the PCC Rules are exported successfully");
            }
            addActionMessage("PCCRules are exported successfully");
            makeImportExportOperationInProgress(false);
            setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_SEARCH);
            return "EXPORT_COMPLETED";

        } catch (Exception e) {
            makeImportExportOperationInProgress(false);
            return generateErrorLogsAndRedirect(e, "Error while fetching PCC Rule data for export operation.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
    }

    private void setGroupsInPCCRules(List<PCCRuleData> pccRuleDatas, Map<String, String> groupNamesBasedOnId) {
        for (PCCRuleData pccRuleData : pccRuleDatas) {
            if (Strings.isNullOrBlank(pccRuleData.getGroups())) {
                pccRuleData.setGroups(CommonConstants.DEFAULT_GROUP);
            }
            importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, pccRuleData);
        }
    }

    public String exportAll() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Method called exportAll()");
        }
        List<PCCRuleData> pccRuleDatas;
        String fileName = "exportGlobalPCCRule_" + NVSMXUtil.simpleDateFormatPool.get().format(new Date()) + CommonConstants.XML;
        BufferedWriter writer = null;
        PrintWriter out = null;
        try {
            Map<String, String> groupNamesBasedOnId = importExportUtil.getGroupsByIdAndName();
            if (isImportExportOperationInProgress()) {
                return redirectError(Discriminators.PCCRULE, "importexport.operation.inprogress.error", Results.LIST.getValue(),false);
            }

            String staffBelongingGroups = (String) request.getSession().getAttribute(Attributes.STAFF_BELONGING_GROUP_IDS);
            makeImportExportOperationInProgress(true);
            PCCRuleContainer pccRuleContainer = new PCCRuleContainer();
            PCCRuleData pccRuleExampleInstance = new PCCRuleData();
            pccRuleExampleInstance.setScope(PCCRuleScope.GLOBAL);

            pccRuleDatas = PccRuleDAO.searchByCriteria(pccRuleExampleInstance, -1, -1, "name", "asc", staffBelongingGroups);

            importExportUtil.filterBasedOnStaffBelongingGroupIds(pccRuleDatas, getStaffBelongingGroups());
            if (Collectionz.isNullOrEmpty(pccRuleDatas)) {
                makeImportExportOperationInProgress(false);
                LogManager.getLogger().warn(MODULE, getText("pccrule.importexport.noglobalpccrulefound"));
                return redirectError(MODULE,"pccrule.importexport.noglobalpccrulefound", Results.LIST.getValue(), false);
            }
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "Export All operation started for Global PCC Rules");
            }

            List<PCCRuleData> pccRuleToBeExported = new ArrayList<PCCRuleData>();
            for (PCCRuleData pccRuleData : pccRuleDatas) {
                List<String> groups = SPLITTER.split(pccRuleData.getGroups());
                Reason reason = new Reason(pccRuleData.getName());
                boolean isExportAllowedForGroup = importExportUtil.filterStaffBelongingGroupsAndRoles(groups, getStaffBelogingRoleMap(),getStaffBelongingGroupDataSet(), reason, ACLModules.GLOBALPCCRULE, ACLAction.EXPORT.name(), getStaffData().getUserName());
                if (isExportAllowedForGroup) {
                    pccRuleToBeExported.add(pccRuleData);
                    importExportUtil.setGroupNamesBasedOnId(groupNamesBasedOnId, pccRuleData);
                }
            }
            if (Collectionz.isNullOrEmpty(pccRuleToBeExported)) {
                makeImportExportOperationInProgress(false);
                LogManager.getLogger().warn(MODULE, getText("pccrule.importexport.noglobalpccrulefound"));
                return redirectError(Discriminators.PCCRULE, "pccrule.importexport.noglobalpccrulefound", Results.LIST.getValue(), false);
            }
            pccRuleContainer.setPccRules(pccRuleToBeExported);
            LogManager.getLogger().debug(MODULE, "Started exporting pcc Rules");
            StringWriter stringWriter = new StringWriter();
            ConfigUtil.serialize(stringWriter, PCCRuleContainer.class, pccRuleContainer);

            String pccRuleInfo = stringWriter.toString();

            if (Strings.isNullOrBlank(pccRuleInfo)) {
                if (LogManager.getLogger().isWarnLogLevel()) {
                    LogManager.getLogger().warn(MODULE, "Can't find content for the PCC rules.");
                }
                throw new Exception("Can not find data as string for the PCC Rule");
            }
            response.setContentType("text/xml");
            response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + "\"");
            out = response.getWriter();
            writer = new BufferedWriter(out);
            writer.write(pccRuleInfo, 0, pccRuleInfo.length());
            writer.flush();

            makeImportExportOperationInProgress(false);
            setActionChainUrl(NVSMXCommonConstants.ACTION_PKG_SEARCH);
            if (LogManager.getLogger().isDebugLogLevel()) {
                LogManager.getLogger().debug(MODULE, "End of operation for exporting pcc rules");
            }
            return "EXPORT_COMPLETED";

        } catch (Exception e) {
            makeImportExportOperationInProgress(false);
            return generateErrorLogsAndRedirect(e, "Error while fetching PCC Rule data for export operation.", ActionMessageKeys.EXPORT_FAILURE.key, Results.LIST.getValue());
        } finally {
            Closeables.closeQuietly(writer);
            Closeables.closeQuietly(out);
        }
    }

    private String generateErrorLogsAndRedirect(Exception e, String message, String key, String result) {
        return super.generateErrorLogsAndRedirect(Discriminators.PCCRULE, e, message, key, result);
    }

    public List<GroupData> getStaffBelongingGroupList() {
        return staffBelongingGroupList;
    }

    public void setStaffBelongingGroupList(List<GroupData> staffBelongingGroupList) {
        this.staffBelongingGroupList = staffBelongingGroupList;
    }

    public List<String> getShowSelectedGroupsForUpdate() {
        return showSelectedGroupsForUpdate;
    }

    public void setShowSelectedGroupsForUpdate(List<String> showSelectedGroupsForUpdate) {
        this.showSelectedGroupsForUpdate = showSelectedGroupsForUpdate;
    }

    public String getServiceTypeRatingGroupMapJson() {
        return serviceTypeRatingGroupMapJson;
    }
    public void setServiceTypeRatingGroupMapJson(String serviceTypeRatingGroupMapJson) {
        this.serviceTypeRatingGroupMapJson = serviceTypeRatingGroupMapJson;
    }

    public String getStaffBelongingRatingGroupJson() {
        return staffBelongingRatingGroupJson;
    }

    public void setStaffBelongingRatingGroupJson(String staffBelongingRatingGroupJson) {
        this.staffBelongingRatingGroupJson = staffBelongingRatingGroupJson;
    }


    public String getSelectedChargingKey() {
        return selectedChargingKey;
    }

    public void setSelectedChargingKey(String selectedChargingKey) {
        this.selectedChargingKey = selectedChargingKey;
    }

    public String getSelectedChargingKeyId() {
        return selectedChargingKeyId;
    }

    public void setSelectedChargingKeyId(String selectedChargingKeyId) {
        this.selectedChargingKeyId = selectedChargingKeyId;
    }


}