package com.elitecore.elitesm.ws.rest.serverconfig.server.services;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.validation.ConstraintValidatorContext;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.elitesm.blmanager.core.base.GenericBLManager;
import com.elitecore.elitesm.blmanager.servermgr.plugins.PluginBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.base.data.BaseData;
import com.elitecore.elitesm.datamanager.core.exceptions.datavalidation.DataValidationException;
import com.elitecore.elitesm.datamanager.servermgr.plugins.data.PluginInstData;
import com.elitecore.elitesm.util.constants.RestValidationMessages;
import com.elitecore.elitesm.ws.rest.serverconfig.server.configurations.elitecsmserver.diameterstack.data.PluginEntryDetailData;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.data.DiameterPluginsDetail;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.data.PluginEntryDetail;
import com.elitecore.elitesm.ws.rest.serverconfig.server.services.data.PluginsDetail;
import com.elitecore.elitesm.ws.rest.util.RestUtitlity;

public class ServiceRestUtility {

	public static final String ALL_WITH_HYPHEN = "-ALL-";
	public static final String ALL = "ALL";
	public static final String RAD_AUTH = "RAD-AUTH";
	public static final String RAD_ACCT = "RAD-ACCT";
	public static final String RAD_DYNAUTH = "RAD-DYNAUTH";
	public static final String DIAMETER_NAS = "DIAMETER-NAS";
	public static final String DIAMETER_CC = "DIAMETER-CC";
	public static final String DIAMETER_EAP = "DIAMETER-EAP";
	public static final String DIAMETER_TGPP = "3GPP-AAA-SERVER";
	
	public static final String RM_CHARGING_SERVICE = "RM-CHARGING-SERVICE";
	public static final String RM_IPPOOL_SERVICE = "IPPOOL-SERVICE";
	
	public static final String ERROR_OCCUR = "Error Occur";

	public static String getRollingType(String rollingType) throws DataManagerException {

		if  (RestValidationMessages.TIME_BASED_ROLLING_TYPE.equals(rollingType)) {
			return RestValidationMessages.TIME_BASED;
		} else if (RestValidationMessages.SIZE_BASED_ROLLING_TYPE.equals(rollingType)) {
			return RestValidationMessages.SIZE_BASED;
		} else  {
			throw new DataValidationException("Invalid Rolling Type. It can be Time-Based or Size-Based");
		}
	}
	
	public static void getPluginName(PluginsDetail pluginDetail) {
		if (Collectionz.isNullOrEmpty(pluginDetail.getPrePlugins().getPrePlugins()) == false) {
			for (PluginEntryDetail pluginEntryDetail : pluginDetail.getPrePlugins().getPrePlugins()) {
				if (Strings.isNullOrEmpty(pluginEntryDetail.getPluginName())) {
					pluginEntryDetail.setPluginName(RestValidationMessages.NONE_WITH_HYPHEN);
				}

			}

		}
		
		if (Collectionz.isNullOrEmpty(pluginDetail.getPostPlugins().getPostPlugins()) == false) {
			for (PluginEntryDetail pluginEntryDetail : pluginDetail.getPostPlugins().getPostPlugins()) {
				if (Strings.isNullOrEmpty(pluginEntryDetail.getPluginName())) {
					pluginEntryDetail.setPluginName(RestValidationMessages.NONE_WITH_HYPHEN);
				}

			}

		}

	}

	public static String setRollingType(String rollingType) throws DataManagerException {
		if  (RestValidationMessages.TIME_BASED.equals(rollingType)) {
			return RestValidationMessages.TIME_BASED_ROLLING_TYPE;
		} else if (RestValidationMessages.SIZE_BASED.equals(rollingType)) {
			return RestValidationMessages.SIZE_BASED_ROLLING_TYPE;
		} else  {
			throw new DataValidationException("Invalid Rolling Type");
		}
	}

	public static List<String> getServicePolicy(List<String> servicePolicies) {
		if (Collectionz.isNullOrEmpty(servicePolicies) == false) {
			if (servicePolicies.size() == 1 && Strings.isNullOrEmpty(servicePolicies.get(0))) {
				servicePolicies.remove(0);
				servicePolicies.add(ALL);
			} else if (servicePolicies.size() == 1 && ALL_WITH_HYPHEN.equals(servicePolicies.get(0))) {
				servicePolicies.remove(0);
				servicePolicies.add(ALL);
			}
		}
		return servicePolicies;
	}

	public static String getSyslogFacility(String facility) {

		if (Strings.isNullOrEmpty(facility)) {
			return RestValidationMessages.NONE_WITH_HYPHEN;
		} else {
		return facility;
		}
	}

	public static String setSyslogFacility(String facility) {
		if (RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(facility) || RestValidationMessages.NONE.equalsIgnoreCase(facility)) {
			return "";
		} else {
		return facility;
		}
	}
	
	public static boolean validateDiameterPlugin(ConstraintValidatorContext context, DiameterPluginsDetail diameterPluginsDetail, String policyName, Boolean isValid) {
		PluginBLManager pluginBLManager = new PluginBLManager();
		try {
			List<PluginInstData> pluginInstDataList = pluginBLManager.getPluginInstanceDataList(policyName);
			List<String> pluginNameList = new ArrayList<String>();
			if (Collectionz.isNullOrEmpty(pluginInstDataList) == false) {
				for (PluginInstData pluginData : pluginInstDataList) {
					pluginNameList.add(pluginData.getName());
				}
			}
			List<PluginEntryDetailData> prePluginsDetails = diameterPluginsDetail.getInPlugins().getInPluginList();
			if (Collectionz.isNullOrEmpty(prePluginsDetails) == false) {
				for (PluginEntryDetailData pluginDetail : prePluginsDetails) {
					if (Strings.isNullOrBlank(pluginDetail.getPluginName()) == false) {
						if (RestValidationMessages.NONE.equalsIgnoreCase(pluginDetail.getPluginName()) || RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(pluginDetail.getPluginName())) {
							pluginDetail.setPluginName(null);
						} else if (pluginNameList.contains(pluginDetail.getPluginName()) == false) {
							RestUtitlity.setValidationMessage(context, "Configured " + pluginDetail.getPluginName()+ " In plugin does not exist");
							isValid = false;
						}
					} else {
						RestUtitlity.setValidationMessage(context, " In Plugin Name must be specified. It can be name of any diameter plugin or 'None'.");
						isValid = false;
					}
				}
			}
			List<PluginEntryDetailData> postPluginsDetails = diameterPluginsDetail.getOutPlugins().getOutPluginList();
			if (Collectionz.isNullOrEmpty(postPluginsDetails) == false) {
				for (PluginEntryDetailData pluginDetail : postPluginsDetails) {
					if (Strings.isNullOrBlank(pluginDetail.getPluginName()) == false) {
						if (RestValidationMessages.NONE.equalsIgnoreCase(pluginDetail.getPluginName()) || RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(pluginDetail.getPluginName())) {
							pluginDetail.setPluginName(null);
						} else if (pluginNameList.contains(pluginDetail.getPluginName()) == false) {
							RestUtitlity.setValidationMessage(context, "Configured " + pluginDetail.getPluginName()+ " Out plugin does not exist");
							isValid = false;
						} 
					} else {
						RestUtitlity.setValidationMessage(context, " Out Plugin Name must be specified. It can be name of any diameter plugin or 'None'.");
						isValid = false;
					}
				}
			}

		} catch (DataManagerException e) {
			RestUtitlity.setValidationMessage(context, "Unable to retrive Plugin Detail");
			isValid = false;
			e.printStackTrace();
		}
		return isValid;
	}
	
	public static boolean validateRadiusPlugin(ConstraintValidatorContext context, PluginsDetail pluginsDetails, String policyName, boolean isValid) {
		boolean validate = isValid;
		PluginBLManager pluginBLManager = new PluginBLManager();
		try {
			List<PluginInstData> pluginInstDataList = pluginBLManager.getPluginInstanceDataList(policyName);
			List<String> pluginNameList = new ArrayList<String>();
			if (Collectionz.isNullOrEmpty(pluginInstDataList) == false) {
				for (PluginInstData pluginData : pluginInstDataList) {
					pluginNameList.add(pluginData.getName());
				}
			}
			List<PluginEntryDetail> prePluginsDetails = pluginsDetails.getPrePlugins().getPrePlugins();
			if (Collectionz.isNullOrEmpty(prePluginsDetails) == false) {
				for (PluginEntryDetail pluginDetail : prePluginsDetails) {
					if (Strings.isNullOrBlank(pluginDetail.getPluginName()) == false) {
						if (RestValidationMessages.NONE.equalsIgnoreCase(pluginDetail.getPluginName()) || RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(pluginDetail.getPluginName())) {
							pluginDetail.setPluginName(null);
						} else if (pluginNameList.contains(pluginDetail.getPluginName()) == false) {
							validate = false;
							RestUtitlity.setValidationMessage(context, "Configured " + pluginDetail.getPluginName()+ " pre plugin does not exist.");
							return validate;
						}
					} else {
						validate = false;
						RestUtitlity.setValidationMessage(context, " Pre Plugin Name must be specified. It can be name of any radius plugin or 'None'.");
						return validate;
					}
				}
			}
			List<PluginEntryDetail> postPluginsDetails = pluginsDetails.getPostPlugins().getPostPlugins();
			if (Collectionz.isNullOrEmpty(postPluginsDetails) == false) {
				for (PluginEntryDetail pluginDetail : postPluginsDetails) {
					if (Strings.isNullOrBlank(pluginDetail.getPluginName()) == false) {
						if (RestValidationMessages.NONE.equalsIgnoreCase(pluginDetail.getPluginName()) || RestValidationMessages.NONE_WITH_HYPHEN.equalsIgnoreCase(pluginDetail.getPluginName())) {
							pluginDetail.setPluginName(null);
						} else if (pluginNameList.contains(pluginDetail.getPluginName()) == false) {
							validate = false;
							RestUtitlity.setValidationMessage(context, "Configured " + pluginDetail.getPluginName()+ " post plugin does not exist.");
							return validate;
						} 
					} else {
						validate = false;
						RestUtitlity.setValidationMessage(context, " Post Plugin Name must be specified. It can be name of any radius plugin or 'None'.");
						return validate;
					}
				}
			}

		} catch (DataManagerException e) {
			RestUtitlity.setValidationMessage(context, "Unable to retrive Plugin Detail");
			validate = false;
			e.printStackTrace();
		}

		return validate;
	}

	public static boolean validateServicePolicy(ConstraintValidatorContext context, List<String> servicePolicyList, List<String> configuredPolicies, String policyType) {
		boolean validate = true;

		Set<String> findDuplicates = findDuplicates(configuredPolicies);
		if (Collectionz.isNullOrEmpty(findDuplicates) == false) {
			RestUtitlity.setValidationMessage(context, "Service Policy " + findDuplicates + " given multiple times, Service policy name should be unique.");
			validate = false;
		}
		
		for (String servicePolicyName : configuredPolicies) {
			if (Strings.isNullOrEmpty(servicePolicyName) == false) {

				if (ServiceRestUtility.ALL.equals(servicePolicyName) == false) {
					if (servicePolicyList.contains(servicePolicyName) == false) {
						RestUtitlity.setValidationMessage(context, "Configured " + servicePolicyName+ " service policy does not exist");
						validate  = false;
					}
				}
			} else  {
				RestUtitlity.setValidationMessage(context, "Service policy can not be empty. It can be 'ALL' or name of valid " + policyType);
				validate = false;
			}

		}
		
		if (Collectionz.isNullOrEmpty(configuredPolicies) == false) {
			if (configuredPolicies.size() == 1 && ALL.equals(configuredPolicies.get(0))) {
				configuredPolicies.remove(0);
				configuredPolicies.add(ALL_WITH_HYPHEN);
			}
		}
		
		return validate;
	}
	
	public static Set<String> findDuplicates(List<String> listContainingDuplicates) {

		final Set<String> setToReturn = new HashSet<String>();
		final Set<String> uniqueName = new HashSet<String>();

		for (String name : listContainingDuplicates) {
			if (!uniqueName.add(name)) {
				setToReturn.add(name);
			}
		}
		return setToReturn;
	}
	
	 private static <T> List<String> getServicePolicyMap(Class<?> instanceClass) {

		List<String>servicePolicyList = new ArrayList<String>();
		GenericBLManager genericBLManager = new GenericBLManager();
		try {
			@SuppressWarnings("unchecked")
			List<T> policyList = (List<T>) genericBLManager.getAllRecords(instanceClass, "status", "name", true);
			for(T ccPolicyInstData : policyList){
				servicePolicyList.add(((BaseData) ccPolicyInstData).getName());
			}
		} catch (DataManagerException e) {
			servicePolicyList.add(ServiceRestUtility.ERROR_OCCUR);
			e.printStackTrace();
		}
		return servicePolicyList;
	}

	public static Boolean validateDiameterService(
			ConstraintValidatorContext context, List<String> servicePolicies,
			DiameterPluginsDetail diameterPluginsDetail, Class<?> instanceName, String policyName,
			String policyMessage, Boolean isValid) {
		
		List<String> servicePolicyList = getServicePolicyMap(instanceName);
		
		if (servicePolicyList.contains(ServiceRestUtility.ERROR_OCCUR)) {
			RestUtitlity.setValidationMessage(context, "Unable to retrive service policy");
			isValid = false;
		} else {
			isValid = ServiceRestUtility.validateServicePolicy(context, servicePolicyList, servicePolicies, policyMessage);
		}
		isValid = ServiceRestUtility.validateDiameterPlugin(context, diameterPluginsDetail, policyName, isValid);
		
		return isValid;
	}

	public static Boolean validateRadiusService(
			ConstraintValidatorContext context, List<String> servicePolicies,
			PluginsDetail pluginsDetails,
			Class<?> instanceName, String policyName,
			String policyMessage, Boolean isValid) {
		
		List<String> servicePolicyList = getServicePolicyMap(instanceName);
		
		if (servicePolicyList.contains(ServiceRestUtility.ERROR_OCCUR)) {
			RestUtitlity.setValidationMessage(context, "Unable to retrive service policy");
			isValid = false;
		} else {
			isValid = ServiceRestUtility.validateServicePolicy(context, servicePolicyList, servicePolicies, policyMessage);
		}
		isValid = ServiceRestUtility.validateRadiusPlugin(context, pluginsDetails, policyName, isValid);
		
		return isValid;
	}
}
