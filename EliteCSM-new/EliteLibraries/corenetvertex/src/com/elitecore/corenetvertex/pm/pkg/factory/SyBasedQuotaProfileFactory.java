package com.elitecore.corenetvertex.pm.pkg.factory;

import static com.elitecore.commons.base.Collectionz.isNullOrEmpty;
import static com.elitecore.commons.logging.LogManager.getLogger;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CounterPresence;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileData;
import com.elitecore.corenetvertex.pkg.syquota.SyQuotaProfileDetailData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.conf.SyCounterQuotaProfileConf;

public class SyBasedQuotaProfileFactory {

	private static final String MODULE = "UM-QUOTA-FCTRY";
	private PackageFactory packageFactory;
	
	public SyBasedQuotaProfileFactory(PackageFactory packageFactory) {
		
		this.packageFactory = packageFactory;
	}

	public QuotaProfile createSyBaseQuotaProfile(String quotaProfileId, String quotaProfileName,
			SyQuotaProfileData quotaProfileData, List<String> quotaProfileFailReasons) {

		if (isNullOrEmpty(quotaProfileData.getSyQuotaProfileDetailDatas())) {
			quotaProfileFailReasons.add("No quota profile details configured for quota profile: " + quotaProfileData.getName());
			return null;
		}

		List<HashMap<String, SyCounterQuotaProfileConf>> configs = new ArrayList<HashMap<String, SyCounterQuotaProfileConf>>();
		HashMap<String, SyCounterQuotaProfileConf> hsqServiceWiseConfig = new HashMap<String, SyCounterQuotaProfileConf>();
		HashMap<String, SyCounterQuotaProfileConf> fup1ServiceWiseConfig = new HashMap<String, SyCounterQuotaProfileConf>();
		HashMap<String, SyCounterQuotaProfileConf> fup2ServiceWiseConfig = new HashMap<String, SyCounterQuotaProfileConf>();
		configs.add(hsqServiceWiseConfig);
		configs.add(fup1ServiceWiseConfig);
		configs.add(fup2ServiceWiseConfig);

		for (SyQuotaProfileDetailData quotaProfileDetailData : quotaProfileData.getSyQuotaProfileDetailDatas()) {

			List<String> failReasons = new ArrayList<String>();

			if (failReasons.isEmpty() == false) {
				quotaProfileFailReasons.add("Quota Profile Detail(" + quotaProfileDetailData.getId() + " parsing fail. Cause by:"
						+ FactoryUtils.format(failReasons));
				continue;
			}

			String serviceId = quotaProfileDetailData.getDataServiceTypeData().getId();
			String serviceName = quotaProfileDetailData.getDataServiceTypeData().getName();
			SyCounterQuotaProfileConf hsqConfig = hsqServiceWiseConfig.get(serviceId);

			if (hsqConfig == null) {
				hsqConfig = new SyCounterQuotaProfileConf(quotaProfileId, quotaProfileName, quotaProfileData.getPkgData().getName(), serviceId, serviceName, 0);
				hsqServiceWiseConfig.put(serviceId, hsqConfig);
			}

			boolean isHSQCounterPresence = true;
			if (quotaProfileDetailData.getCounterPresent() == CounterPresence.OPTIONAL_HSQ.getVal()) {
				isHSQCounterPresence = false;
			}
			if (Strings.isNullOrBlank(quotaProfileDetailData.getHsqValue()) == false) {
				hsqConfig.addCounter(quotaProfileDetailData.getCounterName(), quotaProfileDetailData.getHsqValue(), isHSQCounterPresence);
			}

			// /FUP 1 CREATION
			boolean isFUP1CounterPresence = true;
			if (quotaProfileDetailData.getCounterPresent() == CounterPresence.OPTIONAL_FUP1.getVal()) {
				isFUP1CounterPresence = false;
			}
			SyCounterQuotaProfileConf fup1Builder = fup1ServiceWiseConfig.get(serviceId);

			if (fup1Builder == null) {
				fup1Builder = new SyCounterQuotaProfileConf(quotaProfileId, quotaProfileName, quotaProfileData.getPkgData().getName(), serviceId, serviceName, 1);
				fup1ServiceWiseConfig.put(serviceId, fup1Builder);
			}

			if (Strings.isNullOrBlank(quotaProfileDetailData.getFup1Value()) == false) {
				fup1Builder.addCounter(quotaProfileDetailData.getCounterName(), quotaProfileDetailData.getFup1Value(), isFUP1CounterPresence);
			}

			// /FUP 2 CREATION

			boolean isFUP2CounterPresence = true;
			if (quotaProfileDetailData.getCounterPresent() == CounterPresence.OPTIONAL_FUP2.getVal()) {
				isFUP2CounterPresence = false;
			}
			SyCounterQuotaProfileConf fup2Builder = fup2ServiceWiseConfig.get(serviceId);

			if (fup2Builder == null) {
				fup2Builder = new SyCounterQuotaProfileConf(quotaProfileId, quotaProfileName, quotaProfileData.getPkgData().getName(), serviceId, serviceName, 2);
				fup2ServiceWiseConfig.put(serviceId, fup2Builder);
			}

			if (Strings.isNullOrBlank(quotaProfileDetailData.getFup2Value()) == false) {
				fup2Builder.addCounter(quotaProfileDetailData.getCounterName(), quotaProfileDetailData.getFup2Value(), isFUP2CounterPresence);
			}

		}

		if (quotaProfileFailReasons.isEmpty() == false) {
			getLogger().info(MODULE, "Skip to create Quota profie for :" + quotaProfileName + ". Reason: " + FactoryUtils.format(quotaProfileFailReasons));
			return null;
		}

		ArrayList<Map<String, QuotaProfileDetail>> quotaProfileDetails = new ArrayList<Map<String, QuotaProfileDetail>>();
		for (int fupLevel = 0; fupLevel < configs.size(); fupLevel++) {

			HashMap<String, QuotaProfileDetail> serviceToQuotaProfileDetails = new HashMap<String, QuotaProfileDetail>();
			quotaProfileDetails.add(serviceToQuotaProfileDetails);

			for (Entry<String, SyCounterQuotaProfileConf> serviceTobuilder : configs.get(fupLevel).entrySet()) {
				SyCounterBaseQuotaProfileDetail quotaProfileDetail = packageFactory.createSyBasedQuotaProfileDetail(serviceTobuilder.getValue());
				serviceToQuotaProfileDetails.put(serviceTobuilder.getKey(), quotaProfileDetail);
			}
		}

		return packageFactory.createSyBaseQuotaProfile(quotaProfileData.getName(), quotaProfileData.getPkgData().getName()
				, quotaProfileData.getId(), QuotaProfileType.SY_COUNTER_BASED, quotaProfileDetails);
	}
	
}
