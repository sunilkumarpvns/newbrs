package com.elitecore.corenetvertex.core.validator;

import java.util.ArrayList;
import java.util.List;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.FieldValueConstants;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

public class QosProfileDetailValidator implements Validator<QosProfileDetailData,QosProfileData,ResourceData> {

	private static final String MODULE = "QOS-PROFILE-DETAIL-VALIDATION";

	@Override
	public List<String> validate(QosProfileDetailData qosProfileDetailImported, QosProfileData qosProfile, ResourceData superObject, String action, SessionProvider session) {

        List<String> subReasons = new ArrayList<String>();
		try {
			String id = qosProfileDetailImported.getId();
			if (Strings.isNullOrBlank(id) == false) {
				QosProfileDetailData existingQosProfileDetail = ImportExportCRUDOperationUtil.get(qosProfileDetailImported.getClass(), id,session);
				if (existingQosProfileDetail != null) {
					if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
						subReasons.add("Qos Profile Detail id ( "+id +") already exists for QoS Profile " + BasicValidations.printIdAndName(qosProfile.getId(), qosProfile.getName()));
					}
				}
			}
			setDefaultValues(qosProfileDetailImported, qosProfile);
			validateQuotaAndQosRelatedParameters(qosProfileDetailImported,qosProfile,subReasons);
			setDefaultValuesForQosParameter(qosProfileDetailImported);

		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to validate qos profile detail with id ("+qosProfileDetailImported.getId() +") for QoS Profile " + BasicValidations.printIdAndName(qosProfile.getId(), qosProfile.getName()));
			LogManager.getLogger().trace(MODULE, e);
			subReasons.add("Failed to validate QoS Profile Detail with id ("+qosProfileDetailImported.getId() +") for QoS Profile " + BasicValidations.printIdAndName(qosProfile.getId(), qosProfile.getName()));
		}
		return subReasons;
	}

	private void setDefaultValuesForQosParameter(QosProfileDetailData qosProfileDetailImported) {
		if(Strings.isNullOrBlank(qosProfileDetailImported.getMbrdlUnit())){
			qosProfileDetailImported.setMbrdlUnit(QoSUnit.Kbps.name());
		}
		if(Strings.isNullOrBlank(qosProfileDetailImported.getMbrulUnit())){
			qosProfileDetailImported.setMbrulUnit(QoSUnit.Kbps.name());
		}
		if(Strings.isNullOrBlank(qosProfileDetailImported.getAambrdlUnit())){
			qosProfileDetailImported.setAambrdlUnit(QoSUnit.Kbps.name());
		}
		if(Strings.isNullOrBlank(qosProfileDetailImported.getAambrulUnit())){
			qosProfileDetailImported.setAambrulUnit(QoSUnit.Kbps.name());
		}

		// set usage metering parameter
		if(Strings.isNullOrBlank(qosProfileDetailImported.getSliceDownloadUnit())){
			qosProfileDetailImported.setSliceDownloadUnit(DataUnit.MB.name());
		}
		if(Strings.isNullOrBlank(qosProfileDetailImported.getSliceUploadUnit())){
			qosProfileDetailImported.setSliceUploadUnit(DataUnit.MB.name());
		}
		if(Strings.isNullOrBlank(qosProfileDetailImported.getSliceTotalUnit())){
			qosProfileDetailImported.setSliceTotalUnit(DataUnit.MB.name());
		}
		if(Strings.isNullOrBlank(qosProfileDetailImported.getSliceTimeUnit())){
			qosProfileDetailImported.setSliceTimeUnit(TimeUnit.MINUTE.name());
		}

	}

	private void setDefaultValues(QosProfileDetailData qosProfileDetailImported, QosProfileData qosProfile) throws Exception {
		String id = qosProfileDetailImported.getId();
		//validate usage monitoring
		if(qosProfileDetailImported.getUsageMonitoring() == null || CommonStatusValues.fromBooleanValue(qosProfileDetailImported.getUsageMonitoring()) == null){
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "Invalid Usage Monitoring: " + qosProfileDetailImported.getUsageMonitoring() + " configured for Qos Profile detail(" + id + ") associated with Qos Profile " + BasicValidations.printIdAndName(qosProfile.getId(),qosProfile.getName()) + ". So taking default usage metering type as DISABLE(0)");
			}
			qosProfileDetailImported.setUsageMonitoring(CommonStatusValues.DISABLE.isBooleanValue());
		}

		//validate pre-emption capability and Vulnerability
		if(qosProfileDetailImported.getPreCapability() == null || CommonStatusValues.fromBooleanValue(qosProfileDetailImported.getPreCapability()) == null){
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "Invalid Preemption capability: " + qosProfileDetailImported.getPreCapability() + " configured for Qos Profile detail(" + id + ") associated with Qos Profile " + BasicValidations.printIdAndName(qosProfile.getId(),qosProfile.getName()) +". So taking default Preemption capability as DISABLE(0)");
			}
			qosProfileDetailImported.setPreCapability(CommonStatusValues.DISABLE.isBooleanValue());
		}

		if(qosProfileDetailImported.getPreVulnerability() == null || CommonStatusValues.fromBooleanValue(qosProfileDetailImported.getPreVulnerability()) == null){
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "Invalid Pre-Vulnerability : " + qosProfileDetailImported.getPreVulnerability() + " configured for Qos Profile detail(" + id + ") associated with Qos Profile " + BasicValidations.printIdAndName(qosProfile.getId(),qosProfile.getName()) + ". So taking default Preemption vulnerability as ENABLE(1)");
			}
			qosProfileDetailImported.setPreVulnerability(CommonStatusValues.ENABLE.isBooleanValue());
		}

		//validating priority level
		if(qosProfileDetailImported.getPriorityLevel() < 1 || qosProfileDetailImported.getPriorityLevel() > 15){
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE,"Invalid Priority is configured for Qos Profile detail(" + id + ") associated with Qos Profile " + BasicValidations.printIdAndName(qosProfile.getId(),qosProfile.getName()) +". So taking default priority as 15");
			}
			qosProfileDetailImported.setPriorityLevel((byte)15);
		}

		//validate qci
		if(qosProfileDetailImported.getQci() < 1 || qosProfileDetailImported.getQci() > 9 ){
			if(LogManager.getLogger().isDebugLogLevel()){
				LogManager.getLogger().debug(MODULE, "Invalid QCI: " + qosProfileDetailImported.getQci() +" is configured with Qos Profile detail(" + id + ") associated with Qos Profile " + BasicValidations.printIdAndName(qosProfile.getId(),qosProfile.getName()) +".  So taking default value as " + QCI.getDefault().getQci());
			}
			qosProfileDetailImported.setQci(QCI.getDefault().getQci());
		}

	}
	public void validateQuotaAndQosRelatedParameters(QosProfileDetailData qosProfileDetailImported,QosProfileData qosProfileData, List<String> subReasons) {
		if(qosProfileDetailImported.getAction() == null){
			subReasons.add("Action must be configured with Qos Profile " + BasicValidations.printIdAndName(qosProfileData.getId(),qosProfileData.getName()));
			return;
		}else if(qosProfileDetailImported.getAction() != 0 && qosProfileDetailImported.getAction() != 1){
			subReasons.add("Invalid Action: " + qosProfileDetailImported.getAction() + " is configured with Qos Profile " + BasicValidations.printIdAndName(qosProfileData.getId(),qosProfileData.getName()));
			return;
		}
		if(qosProfileDetailImported.getAction() == 1){
			if(Strings.isNullOrBlank(qosProfileDetailImported.getRejectCause())){
				subReasons.add("Reject Cause is mandatory for Action Reject(1) configured with Qos Profile " + BasicValidations.printIdAndName(qosProfileData.getId(),qosProfileData.getName()));
				return;
			}
		}
		if (qosProfileDetailImported.getAction() == 0 && qosProfileDetailImported.getMbrdl() == null && qosProfileDetailImported.getAambrdl() == null) {
				subReasons.add(FieldValueConstants.MBRDL + " / " +FieldValueConstants.AAMBRDL + " is mandatory for Qos Profile " + BasicValidations.printIdAndName(qosProfileData.getId(),qosProfileData.getName()));
		}
		validateQosParameters(qosProfileDetailImported, subReasons, qosProfileData.getName());

		BasicValidations.validateSliceInformation(qosProfileDetailImported.getUsageMonitoring(), qosProfileDetailImported.getSliceTotal(), qosProfileDetailImported.getSliceTotalUnit(), qosProfileDetailImported.getSliceDownload(), qosProfileDetailImported.getSliceDownloadUnit(),
				qosProfileDetailImported.getSliceUpload(), qosProfileDetailImported.getSliceUploadUnit(), qosProfileDetailImported.getSliceTime(), qosProfileDetailImported.getSliceTimeUnit(), subReasons, qosProfileData.getName());

	}

	private void validateQosParameters(QosProfileDetailData qosProfileDetailImported, List<String> subReasons,String entityName){
		if (qosProfileDetailImported.getMbrdl() != null && BasicValidations.isPositiveNumber(qosProfileDetailImported.getMbrdl()) == false) {
			subReasons.add(FieldValueConstants.MBRDL + " must be positive numeric associated with Qos Profile: " + entityName);
		}
		BasicValidations.checkForValidQos(FieldValueConstants.MBRDL, qosProfileDetailImported.getMbrdl(), qosProfileDetailImported.getMbrdlUnit(), subReasons, entityName);


		if (qosProfileDetailImported.getMbrul() != null && BasicValidations.isPositiveNumber(qosProfileDetailImported.getMbrul()) == false) {
			subReasons.add(FieldValueConstants.MBRDL + " must be positive numeric associated with Qos Profile: " + entityName);
		}
		BasicValidations.checkForValidQos(FieldValueConstants.MBRUL, qosProfileDetailImported.getMbrul(), qosProfileDetailImported.getMbrulUnit(), subReasons, entityName);

		if (qosProfileDetailImported.getAambrdl() != null && BasicValidations.isPositiveNumber(qosProfileDetailImported.getAambrdl()) == false) {
			subReasons.add(FieldValueConstants.AAMBRDL + " must be positive numeric associated with Qos Profile: " + entityName);
		}
		BasicValidations.checkForValidQos(FieldValueConstants.AAMBRDL, qosProfileDetailImported.getAambrdl(), qosProfileDetailImported.getAambrdlUnit(), subReasons, entityName);

		if (qosProfileDetailImported.getAambrul() != null && BasicValidations.isPositiveNumber(qosProfileDetailImported.getAambrul() ) == false) {
			subReasons.add(FieldValueConstants.AAMBRUL + " must be positive numeric associated with Qos Profile: " + entityName);
		}
		BasicValidations.checkForValidQos(FieldValueConstants.AAMBRUL, qosProfileDetailImported.getAambrul() , qosProfileDetailImported.getAambrulUnit() , subReasons, entityName);

		if(Strings.isNullOrEmpty(qosProfileDetailImported.getRedirectUrl()) == false && qosProfileDetailImported.getRedirectUrl().trim().length() > 4000){
			subReasons.add("Length of " +FieldValueConstants.REDIRECT_URL + " should be between 0 to 4000 for " + entityName);
		}
	}

}

