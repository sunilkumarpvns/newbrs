package com.elitecore.corenetvertex.core.validator;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Ishani on 29/7/16.
 */

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.sm.ResourceData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.TimePeriodData;
import com.elitecore.corenetvertex.util.SessionProvider;
import com.elitecore.corenetvertex.util.commons.ImportExportCRUDOperationUtil;


public class TimePeriodDataValidator implements Validator<TimePeriodData,QosProfileData,ResourceData> {

    private static final String MODULE = "TIME-PERIOD-VALIDATOR";

    @Override
    public List<String> validate(TimePeriodData timePeriodData, QosProfileData qosProfileData, ResourceData pkgData, String action, SessionProvider session) {
        List<String> subReasons = new ArrayList<String>();
        try {
            String id = timePeriodData.getId();
            if (Strings.isNullOrBlank(id) == false) {
                TimePeriodData existingTimePeriod = ImportExportCRUDOperationUtil.get(TimePeriodData.class, id, session);
                if (existingTimePeriod != null && CommonConstants.STATUS_DELETED.equalsIgnoreCase(qosProfileData.getStatus()) == false) {
                    if(existingTimePeriod.getQosProfile().getName().equalsIgnoreCase(qosProfileData.getName()) == false
                            || existingTimePeriod.getQosProfile().getId().equalsIgnoreCase(qosProfileData.getId()) == false){
                        subReasons.add("Time Period data with id(" +id+") already exists in other qos profile "+BasicValidations.printIdAndName(qosProfileData.getId(),qosProfileData.getName()));
                    }
                    if (CommonConstants.FAIL.equalsIgnoreCase(action)) {
                        subReasons.add("Time Period with id(" + id + ") already exists for Qos Profile " + BasicValidations.printIdAndName(qosProfileData.getId(), qosProfileData.getName()));
                    }
                }
            }
        } catch (Exception e) {
            LogManager.getLogger().error(MODULE, "Failed to validate time period detail with id (" + timePeriodData.getId() + ") for qos profile "+BasicValidations.printIdAndName(qosProfileData.getId(),qosProfileData.getName())+ " associated with package " +BasicValidations.printIdAndName(pkgData.getId(),null));
            LogManager.getLogger().trace(MODULE, e);
            subReasons.add("Failed to validate Time Period with id ("+timePeriodData.getId() +") for Qos profile " + BasicValidations.printIdAndName(qosProfileData.getId(),qosProfileData.getName())+" associated with package " +BasicValidations.printIdAndName(pkgData.getId(), null) + "). Kindly refer logs for further details");
        }
        return subReasons;
    }

}

