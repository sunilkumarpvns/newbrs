package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.Discriminators;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

public class SyQuotaProfileValidator implements Validator<SyQuotaProfileData,PkgData,PkgData> {
	private static final String MODULE = "SY-QUOTA-PROFILE-VALIDATOR";

	@Override
	public List<String> validate(SyQuotaProfileData syQuotaProfileImported, final PkgData pkgData, PkgData superObject, String action, SessionProvider session) {
		List<String> subReasons = new ArrayList<String>();
		try {
			String id = syQuotaProfileImported.getId();
			String quotaProfileName = syQuotaProfileImported.getName();


			if(Strings.isNullOrBlank(id) && Strings.isNullOrBlank(quotaProfileName)){
				subReasons.add("Sy Quota Profile Id or name is mandatory");
				return subReasons;
			}

			validateQuotaProfileName(quotaProfileName, subReasons);

			syQuotaProfileImported.setCreatedByStaff(pkgData.getCreatedByStaff());
			syQuotaProfileImported.setCreatedDate(new Timestamp(System.currentTimeMillis()));
			syQuotaProfileImported.setModifiedByStaff(syQuotaProfileImported.getCreatedByStaff());
			syQuotaProfileImported.setModifiedDate(syQuotaProfileImported.getCreatedDate());

			SyQuotaProfileData existingSyQuotaProfile = null;
			if (Strings.isNullOrBlank(id) == false) {
				existingSyQuotaProfile = ImportExportCRUDOperationUtil.get(SyQuotaProfileData.class, id, session);
				if (existingSyQuotaProfile != null){
					if(CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingSyQuotaProfile.getStatus())){
						ImportExportCRUDOperationUtil.deleteQosProfileForQuotaProfile(existingSyQuotaProfile.getId(), session);
						ImportExportCRUDOperationUtil.deleteSyBasedQuotaProfileData(existingSyQuotaProfile.getId(), session);
					} else {
						if (Strings.isNullOrBlank(pkgData.getId()) == false
								&& pkgData.getId().equalsIgnoreCase(existingSyQuotaProfile.getPkgData().getId()) == false
								&& CommonConstants.REPLACE.equalsIgnoreCase(action)) {
							subReasons.add(Discriminators.SY_QUOTA_PROFILE + " " + BasicValidations.printIdAndName(syQuotaProfileImported.getId(), syQuotaProfileImported.getName()) + " already exists in different package " + BasicValidations.printIdAndName(existingSyQuotaProfile.getPkgData().getId(), existingSyQuotaProfile.getPkgData().getName()));
							return subReasons;
						}
					}
				}

			} else {
				List<SyQuotaProfileData> syQuotaProfileList = ImportExportCRUDOperationUtil.getNameBasedOnParentId(SyQuotaProfileData.class, quotaProfileName, pkgData.getId(), "pkgData.id", session);
				if (Collectionz.isNullOrEmpty(syQuotaProfileList) == false) {
					existingSyQuotaProfile = syQuotaProfileList.get(0);
				}
			}

			if(existingSyQuotaProfile != null){
				if (CommonConstants.STATUS_DELETED.equalsIgnoreCase(existingSyQuotaProfile.getStatus()) == false) {
					validateQuotaProfileBasedOnAction(syQuotaProfileImported, existingSyQuotaProfile, action, subReasons);
				}
			}

		}catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to validate sy quota profile "+ BasicValidations.printIdAndName(syQuotaProfileImported.getId(), syQuotaProfileImported.getName())+") associated with package "+ BasicValidations.printIdAndName(pkgData.getId(),pkgData.getName()));
			LogManager.getLogger().trace(MODULE, e);
			subReasons.add("Failed to validate Sy Quota Profile"+ BasicValidations.printIdAndName(syQuotaProfileImported.getId(), syQuotaProfileImported.getName())+") associated with Package "+ BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()) + ". Kindly refer logs for further details");
		}
		return subReasons;
	}

	private void validateQuotaProfileName(String quotaProfileName, List<String> subReasons) {
		BasicValidations.validateName(quotaProfileName, "Sy Quota Profile",subReasons);
	}

	private void validateQuotaProfileBasedOnAction(SyQuotaProfileData syQuotaProfileImported, SyQuotaProfileData existingSyQuotaProfile, String action, List<String> subReasons){
		if(CommonConstants.FAIL.equalsIgnoreCase(action)){
			subReasons.add("Sy Quota Profile with " + BasicValidations.printIdAndName(syQuotaProfileImported.getId(),syQuotaProfileImported.getName()) + " already exists with package " + BasicValidations.printIdAndName(syQuotaProfileImported.getPkgData().getId(), syQuotaProfileImported.getPkgData().getName()));
		}else if (CommonConstants.REPLACE.equalsIgnoreCase(action)) {
				syQuotaProfileImported.setId(existingSyQuotaProfile.getId());
		}
	}
}
