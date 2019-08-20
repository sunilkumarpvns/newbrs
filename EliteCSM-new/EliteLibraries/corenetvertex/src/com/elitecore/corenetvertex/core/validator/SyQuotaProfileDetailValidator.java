package com.elitecore.corenetvertex.core.validator;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileDetailData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;

import java.util.ArrayList;
import java.util.List;

public class SyQuotaProfileDetailValidator implements Validator<SyQuotaProfileDetailData, SyQuotaProfileData, PkgData> {

    private static final String MODULE = "SY-QUOTA-PROFILE-DETAIL-VALIDATOR";
    private List<String> subReasons;

    @Override
    public List<String> validate(SyQuotaProfileDetailData syQuotaProfileDetailImported, SyQuotaProfileData syQuotaProfileData, PkgData superObject, String action, SessionProvider session) {
        subReasons = new ArrayList<String>();
        try {
            String id = syQuotaProfileDetailImported.getId();
            if (Strings.isNullOrBlank(id) == false) {
                SyQuotaProfileDetailData detailData = ImportExportCRUDOperationUtil.get(SyQuotaProfileDetailData.class, id,session);
                if (detailData != null) {
                    if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                        subReasons.add("Sy Quota profile detail with id (" + id +") already exists for Sy Quota Profile " + BasicValidations.printIdAndName(syQuotaProfileData.getId(),syQuotaProfileData.getName()));

                    }
                }
            }
            syQuotaProfileDetailImported.setSyQuotaProfileData(syQuotaProfileData);
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate sy quota profile detail ("+ syQuotaProfileDetailImported.getId() +") for sy quota profile " +BasicValidations.printIdAndName(syQuotaProfileData.getId(),syQuotaProfileData.getName()) +" associated with package " + BasicValidations.printIdAndName(superObject.getId(),superObject.getName()));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate Sy Quota Profile Detail with id ("+ syQuotaProfileDetailImported.getId() +") for Sy Quota Profile " +BasicValidations.printIdAndName(syQuotaProfileData.getId(),syQuotaProfileData.getName()) +" associated with Package " + BasicValidations.printIdAndName(superObject.getId(),superObject.getName()) + ". Kindly refer logs for further details");
        }
        return subReasons;
    }

}
