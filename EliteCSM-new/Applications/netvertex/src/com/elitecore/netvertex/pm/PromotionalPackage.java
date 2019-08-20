package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;

import java.sql.Timestamp;
import java.util.List;

public class PromotionalPackage extends com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage {

    private static final long serialVersionUID = 1L;

    public PromotionalPackage(String id,
                              String name,
                              QuotaProfileType quotaProfileType,
                              PkgStatus availabilityStatus,
                              List<QoSProfile> qosProfiles,
                              UsageNotificationScheme usageNotificationScheme,
                              PkgMode packageMode,
                              String description,
                              Double price,
                              Timestamp availabilityStartDate,
                              Timestamp availabilityEndDate,
                              List<String> groupIds,
                              boolean preferPromotionalQoS,
                              PolicyStatus status,
                              String failReason,
                              String partialFailReason,
                              String param1,
                              String param2,
                              List<GroupManageOrder> groupOrderConfs) {
        super(id, name, quotaProfileType, availabilityStatus, qosProfiles, usageNotificationScheme, packageMode, description,
                price, availabilityStartDate, availabilityEndDate, groupIds, preferPromotionalQoS, status, failReason,
                partialFailReason, param1, param2, groupOrderConfs);
    }
}
