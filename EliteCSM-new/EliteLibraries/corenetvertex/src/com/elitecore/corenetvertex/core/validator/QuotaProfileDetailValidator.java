package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileData;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

public class QuotaProfileDetailValidator implements Validator<QuotaProfileDetailData,QuotaProfileData,PkgData> {

	private static final String MODULE = "QUOTA-PROFILE-DETAIL-VALIDATOR";

	@Override
	public List<String> validate(QuotaProfileDetailData quotaProfileDetailImported, QuotaProfileData quotaProfileData, PkgData pkgData, String action, SessionProvider session) {
		List<String> subReasons = new ArrayList<String>();
		try {
			String id = quotaProfileDetailImported.getId();
			if (Strings.isNullOrBlank(id) == false) {
				QuotaProfileDetailData detailData = ImportExportCRUDOperationUtil.get(QuotaProfileDetailData.class, id, session);
				if (detailData != null) {
					if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
						subReasons.add("Quota Profile Detail id(" + id + ") already exists for Quota Profile " + BasicValidations.printIdAndName(quotaProfileData.getId(),quotaProfileData.getName()));
					}
				}
			}
			if(quotaProfileDetailImported.getDataServiceTypeData() == null){
				subReasons.add("Data Service Type must be configured with Quota Profile " + BasicValidations.printIdAndName(quotaProfileData.getId(),quotaProfileData.getName()));
			}
			if(Strings.isNullOrBlank(quotaProfileDetailImported.getAggregationKey())){
				subReasons.add("Aggregation Key is mandatory with Quota Profile Detail id: " + id + " associated with Quota Profile " + BasicValidations.printIdAndName(quotaProfileData.getId(),quotaProfileData.getName()));
			}else if(quotaProfileDetailImported.getAggregationKey().equals(AggregationKey.BILLING_CYCLE.name()) == false && quotaProfileDetailImported.getAggregationKey().equals(AggregationKey.CUSTOM.name()) == false
					&& quotaProfileDetailImported.getAggregationKey().equals(AggregationKey.DAILY.name()) == false && quotaProfileDetailImported.getAggregationKey().equals(AggregationKey.WEEKLY.name()) == false){
				subReasons.add("Invalid Aggregation key " + quotaProfileDetailImported.getAggregationKey() +" is configured with Quota Profile Detail id: " + id + " associated with Quota Profile " + BasicValidations.printIdAndName(quotaProfileData.getId(),quotaProfileData.getName()));
			}


			if(quotaProfileDetailImported.getTotal() == null && Strings.isNullOrBlank(quotaProfileDetailImported.getTotalUnit())
					&& quotaProfileDetailImported.getDownload() == null && Strings.isNullOrBlank(quotaProfileDetailImported.getDownloadUnit())
					&& quotaProfileDetailImported.getUpload() == null && Strings.isNullOrBlank(quotaProfileDetailImported.getUploadUnit())
					&& quotaProfileDetailImported.getTime() == null && Strings.isNullOrBlank(quotaProfileDetailImported.getTimeUnit())){
				subReasons.add("At least (Total/Upload/Download/Time) Quota and Unit must be configured with Quota Profile Detail id: " + id + " associated with Quota Profile " + BasicValidations.printIdAndName(quotaProfileData.getId(), quotaProfileData.getName()));
			}
			setDefaultUnitsForQuotaProfiles(quotaProfileDetailImported,subReasons);

		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Failed to validate quota profile detail with id (" + quotaProfileDetailImported.getId() + ") for quota profile "+BasicValidations.printIdAndName(quotaProfileData.getId(),quotaProfileData.getName())+ " associated with package " +BasicValidations.printIdAndName(pkgData.getId(),pkgData.getName()));
					LogManager.getLogger().trace(MODULE, e);
			subReasons.add("Failed to validate Quota Profile Detail with id ("+quotaProfileDetailImported.getId() +") for Quota profile " + BasicValidations.printIdAndName(quotaProfileData.getId(),quotaProfileData.getName())+" associated with package " +BasicValidations.printIdAndName(pkgData.getId(), pkgData.getName()) + "). Kindly refer logs for further details");
		}
		return subReasons;
	}

	private void setDefaultUnitsForQuotaProfiles(QuotaProfileDetailData quotaProfileDetailImported,List<String> subReasons) {
		if(quotaProfileDetailImported.getTotal() == null && Strings.isNullOrBlank(quotaProfileDetailImported.getTotalUnit())){
			quotaProfileDetailImported.setTotalUnit(DataUnit.MB.name());
		}else if(quotaProfileDetailImported.getTotal() != null && Strings.isNullOrBlank(quotaProfileDetailImported.getTotalUnit())){
			subReasons.add("Value for Total Unit must be configured with Quota Profile Detail id: " + quotaProfileDetailImported.getId());
		}else if(DataUnit.fromName(quotaProfileDetailImported.getTotalUnit()) == null){
			subReasons.add("Invalid value for Total Unit " + quotaProfileDetailImported.getTotalUnit() + " is configured with Quota Profile Detail id: " + quotaProfileDetailImported.getId());
		}

		if(quotaProfileDetailImported.getDownload() == null && Strings.isNullOrBlank(quotaProfileDetailImported.getDownloadUnit())){
			quotaProfileDetailImported.setDownloadUnit(DataUnit.MB.name());
		}else if(quotaProfileDetailImported.getDownload() != null && Strings.isNullOrBlank(quotaProfileDetailImported.getDownloadUnit())){
			subReasons.add("Value for Download Unit must be configured with Quota Profile Detail id: " + quotaProfileDetailImported.getId());
		}else if(DataUnit.fromName(quotaProfileDetailImported.getDownloadUnit()) == null){
			subReasons.add("Invalid value for Download Unit " + quotaProfileDetailImported.getDownloadUnit() + " is configured with Quota Profile Detail id: " + quotaProfileDetailImported.getId());
		}
		if(quotaProfileDetailImported.getUpload() == null && Strings.isNullOrBlank(quotaProfileDetailImported.getUploadUnit())){
			quotaProfileDetailImported.setUploadUnit(DataUnit.MB.name());
		}else if(quotaProfileDetailImported.getUpload() != null && Strings.isNullOrBlank(quotaProfileDetailImported.getUploadUnit())){
			subReasons.add("Value for Upload Unit must be configured with Quota Profile Detail id: " + quotaProfileDetailImported.getId());
		}else if(DataUnit.fromName(quotaProfileDetailImported.getUploadUnit()) == null){
			subReasons.add("Invalid value for Upload Unit " + quotaProfileDetailImported.getUploadUnit() + " is configured with Quota Profile Detail id: " + quotaProfileDetailImported.getId());
		}
		if(quotaProfileDetailImported.getTime() == null && Strings.isNullOrBlank(quotaProfileDetailImported.getTimeUnit())){
			quotaProfileDetailImported.setTimeUnit(TimeUnit.MINUTE.name());
		}else if(quotaProfileDetailImported.getTime() != null && Strings.isNullOrBlank(quotaProfileDetailImported.getTimeUnit())){
			subReasons.add("Value for Time Unit must be configured with Quota Profile Detail id: " + quotaProfileDetailImported.getId());
		}else if(TimeUnit.fromVal(quotaProfileDetailImported.getTimeUnit()) == null){
			subReasons.add("Invalid value for Time Unit " + quotaProfileDetailImported.getTimeUnit() + " is configured with Quota Profile Detail id: " + quotaProfileDetailImported.getId());
		}
	}

}
