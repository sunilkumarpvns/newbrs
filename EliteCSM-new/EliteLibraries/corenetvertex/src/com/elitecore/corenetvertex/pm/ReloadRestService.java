package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.config.exception.LoadConfigurationException;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.pm.util.BoDDataPredicates;
import com.elitecore.corenetvertex.pm.util.ProductOfferPredicates;
import com.elitecore.corenetvertex.pm.util.RnCPackagePredicates;
import com.elitecore.corenetvertex.util.GsonFactory;

import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;
/**
 * @author Jay Trivedi
 */

@Path("/reload")
public class ReloadRestService {

	private static final String MODULE = "RELOAD-REST";
	private static final String POLICY = "policy";
	private static final String POLICY_BY_GROUPS = "policyByGroups";
	public static final String RELOAD_DATA_SLICE_CONFIGURATION = "reloadDataSliceConfiguration";
	public static final String SUCCESS = "SUCCESS";

	private PolicyRepository policyRepository;

	public ReloadRestService(PolicyRepository reloadOperation) {
		this.policyRepository = reloadOperation;
	}
	
	@POST
    @Path("/" + POLICY)
	@Produces({MediaType.TEXT_PLAIN})
    public String reload(String packageIds) {
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading packages with Ids: " + packageIds);
		}
		if (Strings.isNullOrBlank(packageIds)) {
			return CommonConstants.EMPTY_STRING;
		}

		try {
			packageIds = URLDecoder.decode(packageIds, CommonConstants.UTF_8);
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogger().error(MODULE,"Unable to decode arguments: "+packageIds+ " . Reason:"+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return CommonConstants.EMPTY_STRING;
		}

		String[] packageIdsArray = CommonConstants.COMMA_SPLITTER.splitToArray(packageIds);


		String responseString = GsonFactory.defaultInstance().toJson(policyRepository.reloadByName(packageIdsArray));

		try {
			return URLEncoder.encode(responseString, CommonConstants.UTF_8);
		}catch (UnsupportedEncodingException e) {
			LogManager.getLogger().error(MODULE,"Unable to encode response: "+responseString+". Reason:"+ e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
		return CommonConstants.EMPTY_STRING;
	}
	
	@GET
    @Path("/" + POLICY)
	@Produces({MediaType.TEXT_PLAIN})
    public String reload() {
		
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading all packages");
		}

		String responseString = GsonFactory.defaultInstance().toJson(policyRepository.reload());
		try {
			return URLEncoder.encode(responseString, CommonConstants.UTF_8);
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogger().error(MODULE,"Unable to encode response: "+responseString+". Reason:"+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return CommonConstants.EMPTY_STRING;
	}


	@POST
	@Path("/" + POLICY_BY_GROUPS)
	@Produces({MediaType.TEXT_PLAIN})
	public String reloadByGroups(String staffGroupBelongingIds) {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading packages for groups: " + staffGroupBelongingIds);
		}
		if (Strings.isNullOrBlank(staffGroupBelongingIds)) {
			return CommonConstants.EMPTY_STRING;
		}

		try {
			staffGroupBelongingIds = URLDecoder.decode(staffGroupBelongingIds, CommonConstants.UTF_8);
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogger().error(MODULE,"Unable to decode arguments: "+ staffGroupBelongingIds + ". Reason:"+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			return CommonConstants.EMPTY_STRING;
		}

		List<String> groupsIds = CommonConstants.COMMA_SPLITTER.split(staffGroupBelongingIds);

		PolicyCacheDetail dataPackagePolicyCacheDetail = policyRepository.reloadDataPackagesOfGroups(groupsIds);
        PolicyCacheDetail imsPackagePolicyCacheDetail = policyRepository.reloadIMSPackagesOfGroups(groupsIds);
        PolicyCacheDetail quotaTopUpPolicyCacheDetail = policyRepository.reloadQuotaTopUpsOfGroups(groupsIds);
        PolicyCacheDetail monetaryRechargePlanPolicyCachDetail = policyRepository.reloadMonetaryRechargePlansOfGroups(groupsIds);
		PolicyCacheDetail productOfferCacheDetail = policyRepository.reloadProductOffers(ProductOfferPredicates.createGroupFilter(groupsIds));
		PolicyCacheDetail rncPackageCacheDetail = policyRepository.reloadRnCPackages(RnCPackagePredicates.createGroupFilter(groupsIds));
		PolicyCacheDetail bodPackageCacheDetail = policyRepository.reloadBoDPackages(BoDDataPredicates.createGroupFilter(groupsIds));
		policyRepository.readServices();

		PolicyCacheDetail reloadResponse = PolicyCacheDetail.merge(dataPackagePolicyCacheDetail,
				imsPackagePolicyCacheDetail,
				quotaTopUpPolicyCacheDetail,
				monetaryRechargePlanPolicyCachDetail,
				productOfferCacheDetail,
				rncPackageCacheDetail,
				bodPackageCacheDetail);
		String responseString = GsonFactory.defaultInstance().toJson(reloadResponse);
		try {
			return URLEncoder.encode(responseString, CommonConstants.UTF_8);
		}catch (UnsupportedEncodingException e) {
			LogManager.getLogger().error(MODULE,"Unable to encode response: "+responseString+". Reason:"+ e.getMessage());
			LogManager.getLogger().trace(MODULE,e);
		}
		return CommonConstants.EMPTY_STRING;
	}

	@GET
	@Path("/" + RELOAD_DATA_SLICE_CONFIGURATION)
	@Produces({MediaType.TEXT_PLAIN})
	public String reloadDataSliceConfiguration() {
		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Reloading data slice configuration");
		}

		try {
			policyRepository.reloadSliceConfiguration();
			return SUCCESS;
		} catch (LoadConfigurationException e) {
			LogManager.getLogger().error(MODULE, "Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return CommonConstants.EMPTY_STRING;
	}
}
