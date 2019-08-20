package com.elitecore.corenetvertex.pm.pkg;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import com.elitecore.acesstime.TimePeriod;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;

public class PromotionalPackageFactory {
	
	public static PromotionalPackageBuilder createPackage(String id, String name) {
		return new PromotionalPackageBuilder(id, name);
	}
	
	public static class PromotionalPackageBuilder {


		private String id;
		private String name;
		private List<String> groupIds;
		private List<QoSProfile> qosProfiles;
		private Map<String, QuotaProfile> monitoringKeyToQuotaProfile;
		private Map<String, PCCRule> monitoringKeyToPCCRule;
		private Map<String, ChargingRuleBaseName> monitoringKeyToChargingRuleBaseName;
		private List<String> quotaProfileIds;
		private QuotaProfileType quotaProfileType;
		private PkgStatus availabilityStatus;
		private ArrayList<TimePeriod> timeSlots;
		private boolean isQuotaProfileExist;
		private UsageNotificationScheme usageNotificationScheme;
		private PkgMode packageMode;
		private String failReason;
		private String partialFailReason;
		private PolicyStatus status = PolicyStatus.SUCCESS;
		private String description;
		private Double price;
		private Timestamp availabilityStartDate;
		private Timestamp availabilityEndDate;
		private List<QuotaProfile> quotaProfiles;
		private String param1;
		private String param2;
		private PkgData pkgData;
		private List<GroupManageOrder> pkgGroupOrderConfs;
		private boolean preferPromotionalQoS;

		public PromotionalPackageBuilder(String id, String name) {
			this.id = id;
			this.name = name;
			this.groupIds = Collectionz.newArrayList();
			this.pkgGroupOrderConfs = Collectionz.newArrayList();
		}

		public PromotionalPackageBuilder withPkgManageOrder(List<GroupManageOrder> asList) {
			for (GroupManageOrder groupOrderConf : asList) {
				withPkgManageOrder(groupOrderConf);
			}
			return this;
		}

		public PromotionalPackageBuilder withPkgManageOrder(GroupManageOrder groupOrderConf) {
			this.pkgGroupOrderConfs.add(groupOrderConf);
			
			if (groupIds.contains(groupOrderConf.getGroupId()) == false) {
				groupIds.add(groupOrderConf.getGroupId());
			}
			
			return this;
		}

		public PromotionalPackageBuilder withGroupIds(List<String> groupIds) {
			this.groupIds = groupIds;
			return this;
		}

		public PromotionalPackage build() {
			return new PromotionalPackage(id, name, quotaProfileType, availabilityStatus, qosProfiles, usageNotificationScheme, packageMode, description, price, availabilityStartDate, availabilityEndDate, groupIds, preferPromotionalQoS, status, failReason, partialFailReason, param1, param2, pkgGroupOrderConfs);
		}
	}
}
