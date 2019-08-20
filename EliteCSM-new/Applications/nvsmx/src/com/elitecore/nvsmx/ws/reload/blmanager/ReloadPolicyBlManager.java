package com.elitecore.nvsmx.ws.reload.blmanager;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.EntityType;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.util.BoDDataPredicates;
import com.elitecore.corenetvertex.pm.util.ProductOfferPredicates;
import com.elitecore.corenetvertex.pm.util.RnCPackagePredicates;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.nvsmx.remotecommunications.BroadCastCompletionResult;
import com.elitecore.nvsmx.remotecommunications.EndPoint;
import com.elitecore.nvsmx.remotecommunications.EndPointManager;
import com.elitecore.nvsmx.remotecommunications.RMIGroupManager;
import com.elitecore.nvsmx.remotecommunications.RMIResponse;
import com.elitecore.nvsmx.remotecommunications.RemoteMessageCommunicator;
import com.elitecore.nvsmx.remotecommunications.RemoteMethod;
import com.elitecore.nvsmx.remotecommunications.RemoteMethodConstant;
import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;
import com.elitecore.nvsmx.ws.reload.response.ReloadDataSliceConfigurationResponse;
import com.elitecore.nvsmx.ws.reload.response.ReloadPolicyResponse;
import com.elitecore.nvsmx.ws.reload.response.RemoteRMIResponse;
import com.elitecore.nvsmx.ws.reload.response.RemoteRMIResponseForDataSlice;

import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * Created by aditya on 4/20/17.
 */
public class ReloadPolicyBlManager {
    private static final String MODULE = "RELOAD-POLICY-BL-MNGR";
    public static final String CALLING_RELOAD_POLICY = "Calling reload Policy";
    public static final String CALLING_RELOAD_DATA_SLICE_CONFIGURATION = "Calling reload Data Slice Configuration";
    public static final String SUCCESS = "SUCCESS";
    public static final String FAILURE = "FAILURE";
    private final RMIGroupManager rmiGroupManager;
    private final EndPointManager endPointManager;
    private final PolicyRepository policyRepository;
    private final Map<String,PolicyCacheDetail> pkgTypeWisePolicyStatistics =  Arrays.stream(EntityType.values())
            .collect(Collectors.toMap(EntityType::getValue, k -> new PolicyCacheDetail.PolicyCacheDetailBuilder().build(), (e1, e2) -> e2, LinkedHashMap::new));

    public ReloadPolicyBlManager(RMIGroupManager rmiGroupManager, EndPointManager endPointManager, PolicyRepository policyRepository) {
        this.rmiGroupManager = rmiGroupManager;
        this.endPointManager = endPointManager;
        this.policyRepository = policyRepository;
    }

    /**
     * To broadcast reload call to all instances as well as reload all
     * policies to own instance
     *
     * @return
     */
    public ReloadPolicyResponse reloadALLPolicy() {

        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, CALLING_RELOAD_POLICY);
        }

        //reload instance before make a call
       reloadRemoteCommunicationInterface();


        List<RemoteRMIResponse> remoteRMIResponses = Collectionz.newArrayList();
        RemoteMethod netVertexReloadRemoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_POLICY_REST_BASE_URI_PATH, RemoteMethodConstant.RELOAD_POLICY, "", HTTPMethodType.GET);
        RemoteMethod nvsmxReloadRemoteMethod = new RemoteMethod(RemoteMethodConstant.NVSMX_PRIVATE_REST_BASE_URI_PATH, RemoteMethodConstant.NVSMX_RELOAD_ALL_OWN_POLICY, "", HTTPMethodType.GET);
        callToEndPoint(remoteRMIResponses, netVertexReloadRemoteMethod, endPointManager.getAllNetvertexEndPoint());
        callToEndPoint(remoteRMIResponses, nvsmxReloadRemoteMethod, endPointManager.getALLNvsmxEndPoints());

        return new ReloadPolicyResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name,
                reloadAllOwnPolicy(), remoteRMIResponses, null, null);

    }

    /**
     * To broadcast reload call to all instances as well as own instance for names
     * * @param names policy names
     *
     * @return
     */
    public ReloadPolicyResponse reloadPolicies(String names) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, CALLING_RELOAD_POLICY);
        }

        if (Strings.isNullOrEmpty(names)) {
            return new ReloadPolicyResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Reason: packages not found", null, null, null, null);
        }

        //reload instance before make a call
        reloadRemoteCommunicationInterface();

        List<RemoteRMIResponse> remoteRMIResponses = Collectionz.newArrayList();

        RemoteMethod netvertexReloadRemoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_POLICY_REST_BASE_URI_PATH, RemoteMethodConstant.RELOAD_POLICIES, names, HTTPMethodType.POST);
        callToEndPoint(remoteRMIResponses, netvertexReloadRemoteMethod, endPointManager.getAllNetvertexEndPoint());
        RemoteMethod nvsmxReloadRemoteMethod = new RemoteMethod(RemoteMethodConstant.NVSMX_PRIVATE_REST_BASE_URI_PATH, RemoteMethodConstant.NVSMX_RELOAD_OWN_POLICIES, names, HTTPMethodType.POST);
        callToEndPoint(remoteRMIResponses, nvsmxReloadRemoteMethod, endPointManager.getALLNvsmxEndPoints());


        return new ReloadPolicyResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name,
                reloadOwnPoliciesByNames(names), remoteRMIResponses, null, null);

    }


    /**
     * To broadcast reload call to all instances as well as own instance for staffBelogingGroups
     * * @param names policy names
     *
     * @return
     */
    public ReloadPolicyResponse reloadPoliciesByGroups(String staffGroupIds) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, CALLING_RELOAD_POLICY);
        }

        if (Strings.isNullOrEmpty(staffGroupIds)) {
            return new ReloadPolicyResponse(ResultCode.INPUT_PARAMETER_MISSING.code, "Reason: staffGroupIds not found", null, null, null, null);
        }

        //reload instance before make a call
       reloadRemoteCommunicationInterface();

        List<RemoteRMIResponse> remoteRMIResponses = Collectionz.newArrayList();

        RemoteMethod netvertexReloadRemoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_POLICY_REST_BASE_URI_PATH, RemoteMethodConstant.RELOAD_POLICIES_BY_GROUPS, staffGroupIds, HTTPMethodType.POST);
        callToEndPoint(remoteRMIResponses, netvertexReloadRemoteMethod, endPointManager.getAllNetvertexEndPoint());
        RemoteMethod nvsmxReloadRemoteMethod = new RemoteMethod(RemoteMethodConstant.NVSMX_PRIVATE_REST_BASE_URI_PATH, RemoteMethodConstant.NVSMX_RELOAD_OWN_POLICIES_BY_GROUPS, staffGroupIds, HTTPMethodType.POST);
        callToEndPoint(remoteRMIResponses, nvsmxReloadRemoteMethod, endPointManager.getALLNvsmxEndPoints());


        return new ReloadPolicyResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name,
                reloadOwnPoliciesByGroups(staffGroupIds), remoteRMIResponses, null, null);

    }

    public ReloadDataSliceConfigurationResponse reloadDataSliceConfiguration() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, CALLING_RELOAD_DATA_SLICE_CONFIGURATION);
        }

        //reload instance before make a call
        reloadRemoteCommunicationInterface();

        RemoteMethod netvertexReloadRemoteMethod = new RemoteMethod(RemoteMethodConstant.NETVERTEX_POLICY_REST_BASE_URI_PATH,
                RemoteMethodConstant.RELOAD_DATA_SLICE_CONFIGURATION, "", HTTPMethodType.GET);
        RemoteMethod nvsmxReloadRemoteMethod = new RemoteMethod(RemoteMethodConstant.NVSMX_PRIVATE_REST_BASE_URI_PATH,
                RemoteMethodConstant.NVSMX_RELOAD_OWN_DATA_SLICE_CONFIGURATION, "", HTTPMethodType.GET);

        List<RemoteRMIResponseForDataSlice> remoteRMIResponsesForDataSlice = Collectionz.newArrayList();
        callToEndPointForDataSliceConfiguration(remoteRMIResponsesForDataSlice, netvertexReloadRemoteMethod, endPointManager.getAllNetvertexEndPoint());
        callToEndPointForDataSliceConfiguration(remoteRMIResponsesForDataSlice, nvsmxReloadRemoteMethod, endPointManager.getALLNvsmxEndPoints());


        return new ReloadDataSliceConfigurationResponse(ResultCode.SUCCESS.code, ResultCode.SUCCESS.name, reloadOwnDataSliceConfiguration()
                , remoteRMIResponsesForDataSlice);
    }

    public String reloadOwnDataSliceConfiguration() {
        try {
            policyRepository.reloadSliceConfiguration();
            return SUCCESS;
        } catch (LoadConfigurationException e) {
            LogManager.getLogger().error(MODULE,"Error while reloading data slice configuration. Reason: "+e.getMessage());
            LogManager.getLogger().trace(MODULE,e);
            return FAILURE;
        }
    }


    /**
     * To reload ALL own policy
     *
     * @return
     */
    public com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail reloadAllOwnPolicy() {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Calling reloadAllOwnPolicy");
        }
        return com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail.from(policyRepository.reload());


    }


    /**
     * To reload own policies for specific names
     *
     * @param names comma separated names of packages
     * @return
     */
    public com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail reloadOwnPoliciesByNames(String names) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Calling reloadOwnPolicies");
        }
        if (Strings.isNullOrEmpty(names)) {
            return null;
        }
        String[] nameArray = CommonConstants.COMMA_SPLITTER.splitToArray(names);
        return com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail.from(policyRepository.reloadByName(nameArray));
    }



    /**
     * To reload own policies for specific names
     *
     * @param staffGroupIds comma separated names of packages
     * @return
     */
    public com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail reloadOwnPoliciesByGroups(String staffGroupIds) {
        if (LogManager.getLogger().isDebugLogLevel()) {
            LogManager.getLogger().debug(MODULE, "Calling reloadOwnPoliciesByGroups");
        }
        if (Strings.isNullOrEmpty(staffGroupIds)) {
            return null;
        }
        List<String> staffGroups = CommonConstants.COMMA_SPLITTER.split(staffGroupIds);

        PolicyCacheDetail dataPolicyCacheDetail = policyRepository.reloadDataPackagesOfGroups(staffGroups);
        if(dataPolicyCacheDetail != null){
            pkgTypeWisePolicyStatistics.replace(EntityType.EMERGENCY.getValue(), policyRepository.getDataPkgTypeWisePolicyStatistics(PkgType.EMERGENCY.name()));
            pkgTypeWisePolicyStatistics.replace(EntityType.PROMOTIONAL.getValue(), policyRepository.getDataPkgTypeWisePolicyStatistics(PkgType.PROMOTIONAL.name()));
            PolicyCacheDetail dataPolicyCacheDetails = PolicyCacheDetail.merge(policyRepository.getDataPkgTypeWisePolicyStatistics(PkgType.BASE.name()), policyRepository.getDataPkgTypeWisePolicyStatistics(PkgType.ADDON.name()));
            pkgTypeWisePolicyStatistics.replace(EntityType.DATA.getValue(), dataPolicyCacheDetails);
        }
        PolicyCacheDetail  imsPolicyCacheDetail = policyRepository.reloadIMSPackagesOfGroups(staffGroups);
        pkgTypeWisePolicyStatistics.replace(EntityType.IMS.getValue(), imsPolicyCacheDetail);
        PolicyCacheDetail quotaTopUpPolicyCacheDetail = policyRepository.reloadQuotaTopUpsOfGroups(staffGroups);
        pkgTypeWisePolicyStatistics.replace(EntityType.TOPUP.getValue(), quotaTopUpPolicyCacheDetail);
        PolicyCacheDetail  offerCacheDetail = policyRepository.reloadProductOffers(ProductOfferPredicates.createGroupFilter(staffGroups));
        pkgTypeWisePolicyStatistics.replace(EntityType.OFFER.getValue(), offerCacheDetail);

        PolicyCacheDetail  rncPackageCacheDetail = policyRepository.reloadRnCPackages(RnCPackagePredicates.createGroupFilter(staffGroups));
        pkgTypeWisePolicyStatistics.replace(EntityType.RNC.getValue(), rncPackageCacheDetail);

        PolicyCacheDetail boDPackageCacheDetail = policyRepository.reloadBoDPackages(BoDDataPredicates.createGroupFilter(staffGroups));
        pkgTypeWisePolicyStatistics.replace(EntityType.BOD.getValue(), boDPackageCacheDetail);

        PolicyCacheDetail monetaryRechargePlanCacheDetail = policyRepository.reloadMonetaryRechargePlansOfGroups(staffGroups);
        pkgTypeWisePolicyStatistics.replace(EntityType.MONETARYRECHARGEPLAN.getValue(), monetaryRechargePlanCacheDetail);
		PolicyCacheDetail localDataPolicyCacheDetail = PolicyCacheDetail.merge(dataPolicyCacheDetail, imsPolicyCacheDetail,quotaTopUpPolicyCacheDetail, offerCacheDetail,rncPackageCacheDetail);
		policyRepository.readServices();
        return com.elitecore.nvsmx.ws.reload.data.PolicyCacheDetail.from(localDataPolicyCacheDetail);
    }

    private List<RemoteRMIResponse> convertRMIResponseList(Collection<RMIResponse<PolicyCacheDetail>> rmiResponses) {
        List<RemoteRMIResponse> remoteRMIResponses = Collectionz.newArrayList();
        for (RMIResponse<PolicyCacheDetail> rmiResponse : rmiResponses) {
            remoteRMIResponses.add(RemoteRMIResponse.from(rmiResponse));
        }
        return remoteRMIResponses;
    }

    private List<RemoteRMIResponseForDataSlice> convertRMIResponseListDataSlice(Collection<RMIResponse<String>> rmiResponses) {
        List<RemoteRMIResponseForDataSlice> remoteRMIResponses = Collectionz.newArrayList();
        for (RMIResponse<String> response : rmiResponses) {
            if (response.isSuccess() == false) {
                remoteRMIResponses.add(RemoteRMIResponseForDataSlice.from(response));
            }
        }
        return remoteRMIResponses;
    }


    private void callToEndPoint(List<RemoteRMIResponse> remoteRMIResponses, RemoteMethod remoteMethod, List<? extends EndPoint> endPoints) {

        if (Collectionz.isNullOrEmpty(endPoints)) {
            return;
        }

        BroadCastCompletionResult<PolicyCacheDetail> broadcastResult = RemoteMessageCommunicator.broadcast(endPoints, remoteMethod);

        Collection<RMIResponse<PolicyCacheDetail>> reloadPolicyDetails = broadcastResult.getAll(3, TimeUnit.SECONDS);
        if (Collectionz.isNullOrEmpty(reloadPolicyDetails) == false) {
            remoteRMIResponses.addAll(convertRMIResponseList(reloadPolicyDetails));
        }

    }

    private void callToEndPointForDataSliceConfiguration(List<RemoteRMIResponseForDataSlice> remoteRMIResponses, RemoteMethod remoteMethod, List<? extends EndPoint> endPoints) {
        if (Collectionz.isNullOrEmpty(endPoints)) {
            return;
        }

        BroadCastCompletionResult<String> broadcastResult = RemoteMessageCommunicator.broadcast(endPoints, remoteMethod);
        Collection<RMIResponse<String>> reloadPolicyDetails = broadcastResult.getAll(3, TimeUnit.SECONDS);
        if (Collectionz.isNullOrEmpty(reloadPolicyDetails) == false) {
            remoteRMIResponses.addAll(convertRMIResponseListDataSlice(reloadPolicyDetails));
        }
    }

    private void reloadRemoteCommunicationInterface() {
        endPointManager.reload();
        rmiGroupManager.reload();
    }

    public Map<String, PolicyCacheDetail> getPkgTypeWisePolicyStatistics() {
        return pkgTypeWisePolicyStatistics;
    }
}
