package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class QuotaProfileValidator implements Validator<QuotaProfileData,PkgData,PkgData> {
	private static final String MODULE = "QUOTA-PROFILE-VALIDATOR";

	@Override
	public List<String> validate(QuotaProfileData quotaProfileImported, final PkgData pkgData, PkgData superObject, String action, SessionProvider session) {

		List<String> subReasons = new ArrayList<String>();
		if(QuotaProfileType.USAGE_METERING_BASED == superObject.getQuotaProfileType()) {
			try {

				String id = quotaProfileImported.getId();
				String quotaProfileName = quotaProfileImported.getName();


				if (Strings.isNullOrBlank(id) && Strings.isNullOrBlank(quotaProfileName)) {
					subReasons.add("Quota Profile Id or name is mandatory");
					return subReasons;
				}
				quotaProfileImported.setCreatedByStaff(pkgData.getCreatedByStaff());
				quotaProfileImported.setCreatedDate(new Timestamp(System.currentTimeMillis()));
				quotaProfileImported.setModifiedByStaff(quotaProfileImported.getCreatedByStaff());
				quotaProfileImported.setModifiedDate(quotaProfileImported.getCreatedDate());
				validateQuotaProfileName(quotaProfileName, subReasons);

				if (Collectionz.isNullOrEmpty(quotaProfileImported.getQuotaProfileDetailDatas())) {
					subReasons.add("Quota Profile detail must be configured with Quota profile " + BasicValidations.printIdAndName(id, quotaProfileName));
					return subReasons;
				}

				if(quotaProfileImported.getRenewalInterval() != null) {
					if ( quotaProfileImported.getRenewalInterval() <1) {
						subReasons.add("Renewal Interval should not be less than 1 " + BasicValidations.printIdAndName(id, quotaProfileName));
						return subReasons;
					}

					if ( quotaProfileImported.getRenewalIntervalUnit() == null || RenewalIntervalUnit.fromRenewalIntervalUnit(quotaProfileImported.getRenewalIntervalUnit())==null) {
						subReasons.add("Invalid Renewal Interval Unit, Possible values are "+RenewalIntervalUnit.getAllNames()+" " + BasicValidations.printIdAndName(id, quotaProfileName));
						return subReasons;
					}
				} else {
					quotaProfileImported.setRenewalIntervalUnit(RenewalIntervalUnit.MONTH.name());
				}

				setDefaultValues(quotaProfileImported);

				QuotaProfileData existingQuotaProfile = null;
				if (Strings.isNullOrBlank(id) == false) {
					existingQuotaProfile = ImportExportCRUDOperationUtil.get(QuotaProfileData.class, id, session);
					if (existingQuotaProfile != null){
						if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingQuotaProfile.getStatus())){
							ImportExportCRUDOperationUtil.deleteQosProfileForQuotaProfile(existingQuotaProfile.getId(), session);
							ImportExportCRUDOperationUtil.deleteUsageMeteringBasedQuotaProfile(existingQuotaProfile.getId(), existingQuotaProfile.getPkgData().getId(), session);
						} else {
							if (Strings.isNullOrBlank(pkgData.getId()) == false
									&& pkgData.getId().equalsIgnoreCase(existingQuotaProfile.getPkgData().getId()) == false
									&& CommonConstants.REPLACE.equalsIgnoreCase(action)) {
								subReasons.add(Discriminators.QUOTA_PROFILE + " " + BasicValidations.printIdAndName(quotaProfileImported.getId(), quotaProfileImported.getName()) + " already exists in different package " + BasicValidations.printIdAndName(existingQuotaProfile.getPkgData().getId(), existingQuotaProfile.getPkgData().getName()));
								return subReasons;
							}
						}
					}

				} else {
					List<QuotaProfileData> quotaProfileList = ImportExportCRUDOperationUtil.getNameBasedOnParentId(QuotaProfileData.class, quotaProfileName, pkgData.getId(), "pkgData.id", session);
					if (Collectionz.isNullOrEmpty(quotaProfileList) == false) {
						existingQuotaProfile = quotaProfileList.get(0);
					}
				}

				if(existingQuotaProfile != null){
					if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingQuotaProfile.getStatus()) == false) {
						validateQuotaProfileBasedOnAction(quotaProfileImported, existingQuotaProfile, pkgData, action, subReasons);
					}
				}

				List<QuotaProfileDetailData> quotaProfileDetails = quotaProfileImported.getQuotaProfileDetailDatas();
				boolean isHSQPresent = false;
				boolean isFUP1Present = false;
				boolean isFUP2Present = false;
				if (Collectionz.isNullOrEmpty(quotaProfileDetails) == false) {
					for (QuotaProfileDetailData quotaProfileDetailData : quotaProfileDetails) {
						if (quotaProfileDetailData.getFupLevel() == null || (quotaProfileDetailData.getFupLevel() != 0 && quotaProfileDetailData.getFupLevel() != 1 && quotaProfileDetailData.getFupLevel() != 2)) {
							subReasons.add("Invalid FUP Level: " + quotaProfileDetailData.getFupLevel() + " configured with Quota Profile Detail id: " + id + " associated with Quota Profile " + BasicValidations.printIdAndName(id, quotaProfileName));
						} else {
							if (quotaProfileDetailData.getFupLevel() == 0) {
								isHSQPresent = true;
							} else if (quotaProfileDetailData.getFupLevel() == 1) {
								isFUP1Present = true;
							} else if (quotaProfileDetailData.getFupLevel() == 2) {
								isFUP2Present = true;
							}
						}
					}
					if (isHSQPresent == false && isFUP1Present == false && isFUP2Present == false) {
						subReasons.add("No Level is configured with Quota profile " + BasicValidations.printIdAndName(id, quotaProfileName) + " associated with package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
					} else {
						if (isHSQPresent == false) {
							subReasons.add("HSQ Level must be configured with Quota profile " + BasicValidations.printIdAndName(id, quotaProfileName) + " associated with package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
						} else if (isFUP1Present == false && isFUP2Present == true) {
							subReasons.add(" FUP1 must be configured with Quota profile " + BasicValidations.printIdAndName(id, quotaProfileName) + " associated with package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()) + " in presence of FUP2");
						}
					}
				}

			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Failed to validate quota profile " + BasicValidations.printIdAndName(quotaProfileImported.getId(), quotaProfileImported.getName()) + " associated with package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()));
				LogManager.getLogger().trace(MODULE, e);
				subReasons.add("Failed to validate Quota Profile" + BasicValidations.printIdAndName(quotaProfileImported.getId(), quotaProfileImported.getName()) + " associated with Package " + BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()) + ". Kindly refer logs for further details");
			}
		}
		return subReasons;
	}

	private void setDefaultValues(QuotaProfileData quotaProfileImported) {
		//UsagePresence
		Integer usagePresence = quotaProfileImported.getUsagePresence();
		if(usagePresence == null){
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Usage Presence value must be configured with Quota Profile "+ BasicValidations.printIdAndName(quotaProfileImported.getId(),quotaProfileImported.getName())
						+ " so taking default value Mandatory(1)");
			}
			quotaProfileImported.setUsagePresence(1);
		}else if(usagePresence != 0 && usagePresence != 1 && usagePresence != 2 && usagePresence != 3){
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Invalid Usage Presence value: " + usagePresence + "is configured with Quota Profile "+ BasicValidations.printIdAndName(quotaProfileImported.getId(), quotaProfileImported.getName())
						+ " so taking default value Mandatory(1)");
			}
			quotaProfileImported.setUsagePresence(1);
		}

		//Balance Level
		BalanceLevel balanceLevel = quotaProfileImported.getBalanceLevel();
		if(balanceLevel == null){
			if(LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Balance level must be configured with Quota Profile " + BasicValidations.printIdAndName(quotaProfileImported.getId(), quotaProfileImported.getName())
						+ " so taking default value " + BalanceLevel.HSQ);
			}
			quotaProfileImported.setBalanceLevel(BalanceLevel.HSQ);
		}

	}

	private void validateQuotaProfileName(String quotaProfileName, List<String> subReasons) {
		BasicValidations.validateName(quotaProfileName, "Quota Profile",subReasons);
	}

	private void validateQuotaProfileBasedOnAction(QuotaProfileData quotaProfileImported, QuotaProfileData existingQuotaProfile,PkgData pkgData, String action, List<String> subReasons){
		if(CommonConstants.FAIL.equalsIgnoreCase(action) && pkgData.getName().equalsIgnoreCase(existingQuotaProfile.getPkgData().getName())){
			subReasons.add("Quota Profile " + BasicValidations.printIdAndName(quotaProfileImported.getId(),quotaProfileImported.getName()) +" already exists in Package " + BasicValidations.printIdAndName(pkgData.getId(),pkgData.getName()));
		}else if(CommonConstants.REPLACE.equalsIgnoreCase(action)){
				quotaProfileImported.setId(existingQuotaProfile.getId());
			}
	}

}

