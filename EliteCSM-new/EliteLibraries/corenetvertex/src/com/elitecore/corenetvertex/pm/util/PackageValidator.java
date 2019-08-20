package com.elitecore.corenetvertex.pm.util;

import java.util.List;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;

public class PackageValidator {

	public static boolean isValidQuotaProfileTypeWithDeploymentMode(DeploymentMode deploymentMode,
																	QuotaProfileType quotaProfileType) {

		if (DeploymentMode.PCC == deploymentMode
				&& QuotaProfileType.RnC_BASED != quotaProfileType) {
			return false;
		}

		if (DeploymentMode.PCRF == deploymentMode
				&& QuotaProfileType.USAGE_METERING_BASED != quotaProfileType
				&& QuotaProfileType.SY_COUNTER_BASED != quotaProfileType) {
			return false;
		}

		return (DeploymentMode.OCS == deploymentMode
				&& QuotaProfileType.RnC_BASED != quotaProfileType) == false;
	}

	public static void validPCCProfileWithDeploymentMode(DeploymentMode deploymentMode,
															QosProfileData qosProfileData,
															List<String> qosProfileFailReasons) {

		if (DeploymentMode.PCC == deploymentMode) {

			addFailReasonIfQoSDetailNotConfigured(deploymentMode, qosProfileData, qosProfileFailReasons);

			if (qosProfileData.getRncProfileData() == null && qosProfileData.getRateCardData() == null) {
				qosProfileFailReasons.add("RnC Quota Profile/Rate Card is not attached with PCC profile. Deployment mode is " + deploymentMode);
			}
		} else if (DeploymentMode.PCRF == deploymentMode) {
			addFailReasonIfQoSDetailNotConfigured(deploymentMode, qosProfileData, qosProfileFailReasons);
		} else if (DeploymentMode.OCS == deploymentMode) {
			if (qosProfileData.getQosProfileDetailDataList().isEmpty() == false) {
				qosProfileFailReasons.add("QoS detail is configured. Deployment mode is " + deploymentMode);
			}
		}
	}

	private static void addFailReasonIfQoSDetailNotConfigured(DeploymentMode deploymentMode, QosProfileData qosProfileData, List<String> qosProfileFailReasons) {
		if (qosProfileData.getQosProfileDetailDataList().isEmpty()) {
			qosProfileFailReasons.add("QoS detail is not configured. Deployment mode is " + deploymentMode);
		}
	}
}
