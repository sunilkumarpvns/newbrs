package com.elitecore.corenetvertex.pm.pkg;

import com.elitecore.acesstime.TimePeriod;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.pm.pkg.datapackage.conf.GroupManageOrder;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EmergencyPackageFactory {
	
	public static EmergencyPackageBuilder createEmergencyPackage(String id, String name) {
		return new EmergencyPackageBuilder(id, name);
	}
	
	public static class EmergencyPackageBuilder {

		private String id;
		private String name;
		private List<String> groupIds;
		private List<QoSProfile> qosProfiles = new ArrayList<>();
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
		private PolicyStatus policyStatus = PolicyStatus.SUCCESS;
		private String description;
		private Double price;
		private Timestamp availabilityStartDate;
		private Timestamp availabilityEndDate;
		private List<QuotaProfile> quotaProfiles;
		private String param1;
		private String param2;
		private PkgData pkgData;
		private List<GroupManageOrder> pkgGroupOrderConfs;

		public EmergencyPackageBuilder(String id, String name) {
			this.id = id;
			this.name = name;
			this.groupIds = Collectionz.newArrayList();
			this.pkgGroupOrderConfs = Collectionz.newArrayList();
		}

		public EmergencyPackageBuilder withPkgManageOrder(List<GroupManageOrder> asList) {
			for (GroupManageOrder groupOrderConf : asList) {
				withPkgManageOrder(groupOrderConf);
			}
			return this;
		}

		public EmergencyPackageBuilder withPkgManageOrder(GroupManageOrder groupOrderConf) {
			this.pkgGroupOrderConfs.add(groupOrderConf);
			
			if (groupIds.contains(groupOrderConf.getGroupId()) == false) {
				groupIds.add(groupOrderConf.getGroupId());
			}
			
			return this;
		}

		public EmergencyPackageBuilder withGroupIds(List<String> groupIds) {
			this.groupIds = groupIds;
			return this;
		}

		public EmergencyPackageBuilder withPolicyStatus(PolicyStatus policyStatus) {
			this.policyStatus = policyStatus;
			return this;
		}

		public EmergencyPackage build() {
			return new EmergencyPackage(id, name, availabilityStatus, qosProfiles, packageMode, description, price, availabilityStartDate, availabilityEndDate, groupIds, policyStatus, failReason, partialFailReason, param1, param2, pkgGroupOrderConfs);
		}
	}
}
