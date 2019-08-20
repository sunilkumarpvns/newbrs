package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Objects;
import javax.annotation.Nullable;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Maps;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CounterPresence;
import com.elitecore.corenetvertex.constants.PriorityLevel;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgData;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.chargingrulebasename.ChargingRuleBaseNameData;
import com.elitecore.corenetvertex.pkg.pccrule.PCCRuleData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.SliceInformation;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail;

public class QoSProfileDetailFactory {

	private static final Comparator<PCCRuleData> PCC_RULE__DATA_BASE_COMPAROTOR = new PCCRuleCreateTimeBaseComparator();
	private static final Comparator<ChargingRuleBaseNameData> CHARGING_RULE_BASE_NAME_DATA_COMPARATOR = new ChargingRuleBaseNameCreateTimeBaseComparator();

	private PackageFactory packageFactory;
	private PCCRuleFactory pccRuleFactory;
	private ChargingRuleBaseNameFactory chargingRuleBaseNameFactory;

	public QoSProfileDetailFactory(PCCRuleFactory pccRuleFactory, ChargingRuleBaseNameFactory chargingRuleBaseNameFactory, PackageFactory packageFactory) {
		this.pccRuleFactory = pccRuleFactory;
		this.chargingRuleBaseNameFactory = chargingRuleBaseNameFactory;
		this.packageFactory = packageFactory;
	}

	@Nullable
	public QoSProfileDetail createQoSDetail(QosProfileDetailData qosProfileDetailData,
											String qosProfileName,
											QuotaProfile quotaProfile,
											DataRateCard dataRateCard,
											Integer orderNo,
											List<String> qosProfileDetailFailReasons,
											CounterPresence usagePresence,
											List<RatingGroupData> ratingGroups) {

		List<QuotaProfileDetail> quotaProfileDetails = this.createQuotaProfileDetail(quotaProfile, qosProfileDetailData);

		List<PCCRule> pccRules = this.createPCCRule(qosProfileDetailData, qosProfileDetailFailReasons, ratingGroups);

		SliceInformation sliceInformation = null;
		if (qosProfileDetailData.getUsageMonitoring()) {
			sliceInformation = new SliceInformation(FactoryUtils.getDataInBytes(qosProfileDetailData.getSliceTotal(), qosProfileDetailData.getSliceTotalUnit())
					, FactoryUtils.getDataInBytes(qosProfileDetailData.getSliceUpload(), qosProfileDetailData.getSliceUploadUnit())
					, FactoryUtils.getDataInBytes(qosProfileDetailData.getSliceDownload(), qosProfileDetailData.getSliceDownloadUnit())
					, FactoryUtils.getTimeInSeconds(qosProfileDetailData.getSliceTime(), qosProfileDetailData.getSliceTimeUnit()));
		}

		List<ChargingRuleBaseName> chargingRuleBaseNames = this.createChargingRuleBaseNames(qosProfileDetailData, qosProfileDetailFailReasons);

		QoSProfileAction qosProfileAction = qosProfileDetailData.getAction() == 1 ? QoSProfileAction.REJECT : QoSProfileAction.ACCEPT;
		IPCANQoS sessionQoS = this.createIPCANQos(qosProfileDetailData, qosProfileDetailFailReasons, qosProfileAction);
		Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail = this.createServiceToQuotaProfileDetail(qosProfileDetailFailReasons, quotaProfileDetails);

		QuotaProfileDetail allServiceQuotaProfileDetail = this.getAllServiceQuotaProfileDetail(qosProfileDetailFailReasons, quotaProfileDetails);


		boolean isQuotaProfileAttached = quotaProfile != null;

		boolean isApplicableOnUsageUnavailability;

		// Quota profile details
		if (isQuotaProfileAttached) {
			isApplicableOnUsageUnavailability = CounterPresence
					.isApplicableOnUsageUnavailability(qosProfileDetailData.getFupLevel(), usagePresence);
		} else {
			isApplicableOnUsageUnavailability = true;
		}


		if (qosProfileDetailFailReasons.isEmpty() == false) {
			return null;
		}
		/// Creation of QoSProfileDetailFactory with action Reject
		if (qosProfileAction == QoSProfileAction.REJECT) {
			return this.createQoSProfileDetailWithActionReject(qosProfileAction,
					qosProfileDetailData,
					dataRateCard,
					qosProfileDetailFailReasons,
					serviceToQuotaProfileDetail,
					qosProfileName,
					allServiceQuotaProfileDetail,
					orderNo,
					isQuotaProfileAttached,
					isApplicableOnUsageUnavailability);
		} else {
			return this.createQoSProfileDetailWithActionAccept(qosProfileAction,
					sessionQoS,
					pccRules,
					chargingRuleBaseNames,
					isQuotaProfileAttached,
					isApplicableOnUsageUnavailability,
					orderNo,
					qosProfileDetailData,
					dataRateCard,
					serviceToQuotaProfileDetail,
					qosProfileName,
					sliceInformation,
					allServiceQuotaProfileDetail);
		}

	}

	private List<QuotaProfileDetail> createQuotaProfileDetail(QuotaProfile quotaProfile, QosProfileDetailData qosProfileDetailData){
		Map<String, QuotaProfileDetail> quotaProfileDetails = Collections.emptyMap();

		if (quotaProfile != null) {
			if (qosProfileDetailData.getFupLevel() == 0) {
				quotaProfileDetails = quotaProfile.getHsqLevelServiceWiseQuotaProfileDetails();
			} else {
				quotaProfileDetails = quotaProfile.getServiceWiseQuotaProfileDetails(qosProfileDetailData.getFupLevel());
			}

		}

		List<QuotaProfileDetail> quotaProfileDetailsList = null;
		if (Maps.isNullOrEmpty(quotaProfileDetails) == false) {
			quotaProfileDetailsList = new ArrayList<>();
			quotaProfileDetails.values().forEach(quotaProfileDetailsList::add);
		}
		return quotaProfileDetailsList;
	}

	private List<PCCRule> createPCCRule(QosProfileDetailData qosProfileDetailData, List<String> qosProfileDetailFailReasons, List<RatingGroupData> ratingGroups){
		List<PCCRule> pccRules = null;
		List<PCCRuleData> pccRuleDatas = qosProfileDetailData.getPccRules();
		if (Collectionz.isNullOrEmpty(pccRuleDatas) == false) {
			List<String> serviceIds = new ArrayList<>();

			Collections.sort(pccRuleDatas, PCC_RULE__DATA_BASE_COMPAROTOR);
			pccRules = new ArrayList<>();
			for (PCCRuleData pccRuleData : pccRuleDatas) {

				List<String> failReasons = new ArrayList<>();
				if (serviceIds.contains(pccRuleData.getDataServiceTypeData().getId())) {
					qosProfileDetailFailReasons.add("One level of qos profile cannot have more than one pcc rule with same service("
							+ pccRuleData.getDataServiceTypeData().getId() + ")");
					continue;
				}

				PCCRule pccRule = pccRuleFactory.createPCCRule(pccRuleData, qosProfileDetailData.getFupLevel(), failReasons, ratingGroups);
				if (failReasons.isEmpty() == false) {
					qosProfileDetailFailReasons.add("PCC rule(" + pccRuleData.getName() + ") parsing fail. Cause by:" + FactoryUtils.format(failReasons));
				} else {
					pccRules.add(pccRule);
				}

				serviceIds.add(pccRuleData.getDataServiceTypeData().getId());
			}

		}
		return pccRules;
	}

	private List<ChargingRuleBaseName> createChargingRuleBaseNames(QosProfileDetailData qosProfileDetailData, List<String> qosProfileDetailFailReasons){
		List<ChargingRuleBaseName> chargingRuleBaseNames = null;
		List<ChargingRuleBaseNameData> chargingRuleBaseNameDatas = qosProfileDetailData.getChargingRuleBaseNames();
		if (Collectionz.isNullOrEmpty(chargingRuleBaseNameDatas) == false) {

			Collections.sort(chargingRuleBaseNameDatas, CHARGING_RULE_BASE_NAME_DATA_COMPARATOR);
			chargingRuleBaseNames = new ArrayList<>();
			for (ChargingRuleBaseNameData chargingRuleBaseNameData : chargingRuleBaseNameDatas) {

				List<String> failReasons = new ArrayList<>();

				ChargingRuleBaseName chargingRuleBaseName = chargingRuleBaseNameFactory.createChargingRuleBaseName(chargingRuleBaseNameData, qosProfileDetailData.getFupLevel(), failReasons);
				if (failReasons.isEmpty() == false) {
					qosProfileDetailFailReasons.add("Charging Rule Base Name (" + chargingRuleBaseNameData.getName()
							+ ") parsing fail. Cause by:" + FactoryUtils.format(failReasons));
				} else {
					chargingRuleBaseNames.add(chargingRuleBaseName);
				}

			}

		}
		return chargingRuleBaseNames;
	}

	private IPCANQoS createIPCANQos(QosProfileDetailData qosProfileDetailData,
									List<String> qosProfileDetailFailReasons,
									QoSProfileAction qosProfileAction){
		IPCANQoS sessionQoS = null;
		if (qosProfileAction != QoSProfileAction.REJECT) {
			boolean fail = false;
			QCI qci = QCI.fromId(qosProfileDetailData.getQci());
			if (qci == null) {
				fail = true;
				qosProfileDetailFailReasons.add("Invalid QCI value:" + qosProfileDetailData.getQci());
			}

			if (qosProfileDetailData.getAambrdl() <= 0
					&& qosProfileDetailData.getAambrul() <= 0
					&& qosProfileDetailData.getMbrdl() <= 0
					&& qosProfileDetailData.getMbrul() <= 0) {

				fail = true;
				qosProfileDetailFailReasons.add("AAMBUL,AAMBRDL,MBRUL,MBRDL not configured");
			}

			PriorityLevel priorityLevel = PriorityLevel.fromVal(qosProfileDetailData.getPriorityLevel());
			if (priorityLevel == null) {
				fail = true;
				qosProfileDetailFailReasons.add("Invalid priority level value:" + qosProfileDetailData.getPriorityLevel());
			}

			if(fail) {
				return null;
			}


			sessionQoS = new IPCANQoS(
					qci,
					priorityLevel,
					qosProfileDetailData.getPreCapability(),
					qosProfileDetailData.getPreVulnerability(),
					qosProfileDetailData.getMbrdl(), QoSUnit.fromVal(qosProfileDetailData.getMbrdlUnit()),
					qosProfileDetailData.getMbrul(), QoSUnit.fromVal(qosProfileDetailData.getMbrulUnit()),
					qosProfileDetailData.getAambrdl(), QoSUnit.fromVal(qosProfileDetailData.getAambrdlUnit()),
					qosProfileDetailData.getAambrul(), QoSUnit.fromVal(qosProfileDetailData.getAambrulUnit()));
		}
		return sessionQoS;
	}

	private Map<String, QuotaProfileDetail> createServiceToQuotaProfileDetail(List<String> qosProfileDetailFailReasons, Collection<QuotaProfileDetail> quotaProfileDetailsList){


		Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail = null;

		if (Collectionz.isNullOrEmpty(quotaProfileDetailsList) == false) {
			for (QuotaProfileDetail quotaProfileDetail : quotaProfileDetailsList) {
				if (quotaProfileDetail.getServiceId() != null) {
					if (quotaProfileDetail.getServiceId().equals(CommonConstants.ALL_SERVICE_ID)) {
						continue;
					}

					if (serviceToQuotaProfileDetail == null) {
						serviceToQuotaProfileDetail = new HashMap<>();
					}
					serviceToQuotaProfileDetail.put(quotaProfileDetail.getServiceId(), quotaProfileDetail);
				} else {
					qosProfileDetailFailReasons.add("Service Id not found quota profile detail");
				}
			}
		}
		return serviceToQuotaProfileDetail;
	}



	private QuotaProfileDetail getAllServiceQuotaProfileDetail(List<String> qosProfileDetailFailReasons, Collection<QuotaProfileDetail> quotaProfileDetailsList){

		if (Collectionz.isNullOrEmpty(quotaProfileDetailsList) == false) {
			for (QuotaProfileDetail quotaProfileDetail : quotaProfileDetailsList) {
				if (quotaProfileDetail.getServiceId() != null && quotaProfileDetail.getServiceId().equals(CommonConstants.ALL_SERVICE_ID)) {
					return quotaProfileDetail;
				}
			}

			qosProfileDetailFailReasons.add("ALL service quota profile detail not defined");

		}
		return null;
	}

	private QoSProfileDetail createQoSProfileDetailWithActionReject(QoSProfileAction qosProfileAction,
																	QosProfileDetailData qosProfileDetailData,
																	DataRateCard dataRateCard,
																	List<String> qosProfileDetailFailReasons,
																	Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
																	String qosProfileName,
																	QuotaProfileDetail allServiceQuotaProfileDetail,
																	Integer orderNo,
																	boolean isQuotaProfileAttached,
																	boolean isApplicableOnUsageUnavailability){

		if (Strings.isNullOrBlank(qosProfileDetailData.getRejectCause())) {
			qosProfileDetailFailReasons.add("No reason defined for reject action");
			return null;
		}

		boolean usageRequired = isUsageRequired(qosProfileDetailData.getQosProfile().getPkgData());
			if (qosProfileDetailData.getQosProfile().getPkgData().getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED) {

				Map<String, QuotaProfileDetail> serviceToSyQuotaProfileDetail = null;
				if (serviceToQuotaProfileDetail != null) {
					serviceToSyQuotaProfileDetail = new HashMap<>();
					for (Entry<String, QuotaProfileDetail> entry : serviceToQuotaProfileDetail.entrySet()) {
						serviceToSyQuotaProfileDetail
								.put(entry.getKey(), (com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.SyCounterBaseQuotaProfileDetail) entry
										.getValue());
					}
				}

				return packageFactory.createSyCounterBaseQoSProfileDetail(qosProfileName,
						qosProfileDetailData.getQosProfile().getPkgData().getName(),
						qosProfileAction,
						qosProfileDetailData.getRejectCause(),
						(SyCounterBaseQuotaProfileDetail) allServiceQuotaProfileDetail,
						serviceToSyQuotaProfileDetail,
						qosProfileDetailData.getFupLevel(), orderNo, qosProfileDetailData.getRedirectUrl());

			} else if(qosProfileDetailData.getQosProfile().getPkgData().getQuotaProfileType() == QuotaProfileType.RnC_BASED) {

				if (dataRateCard != null) {
					return packageFactory.createRateCardBaseQoSProfileDetailForActionReject(qosProfileName,
							qosProfileDetailData.getQosProfile().getPkgData().getName(),
							qosProfileAction,
							qosProfileDetailData.getRejectCause(),
							dataRateCard,
							qosProfileDetailData.getRedirectUrl());
				}


				return packageFactory.createRnCBaseQoSProfileDetailForActionReject(qosProfileName,
						qosProfileDetailData.getQosProfile().getPkgData().getName(),
						qosProfileAction,
						qosProfileDetailData.getRejectCause(),
						(RncProfileDetail) allServiceQuotaProfileDetail,
						serviceToQuotaProfileDetail,
						qosProfileDetailData.getFupLevel(),
						orderNo,
						true,
						qosProfileDetailData.getRedirectUrl());
			}  else {

				if (isQuotaProfileAttached && allServiceQuotaProfileDetail == null) {
					return packageFactory.createFailedUMBaseQoSProfileDetail(qosProfileName,
							qosProfileDetailData.getQosProfile().getPkgData().getName(),
							qosProfileAction,
							qosProfileDetailData.getRejectCause().trim(),
							qosProfileDetailData.getFupLevel(), orderNo, qosProfileDetailData.getRedirectUrl());
				} else {

					Map<String, QuotaProfileDetail> serviceToUMQuotaProfileDetail = null;
					if (serviceToQuotaProfileDetail != null) {
						serviceToUMQuotaProfileDetail = new HashMap<>();
						for (Entry<String, QuotaProfileDetail> entry : serviceToQuotaProfileDetail.entrySet()) {
							serviceToUMQuotaProfileDetail
									.put(entry.getKey(), (com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.UMBaseQuotaProfileDetail) entry
											.getValue());
						}
					}



					return packageFactory.createUMBaseQoSProfileDetailForActionReject(qosProfileName,
							qosProfileDetailData.getQosProfile().getPkgData().getName(),
							qosProfileAction,
							qosProfileDetailData.getRejectCause().trim(),
							(UMBaseQuotaProfileDetail) allServiceQuotaProfileDetail,
							serviceToUMQuotaProfileDetail,
							usageRequired,
							qosProfileDetailData.getFupLevel(),
							orderNo, isApplicableOnUsageUnavailability, qosProfileDetailData.getRedirectUrl());
				}

			}
	}

	private boolean isUsageRequired(PkgData pkgData) {

		PkgType pkgType = PkgType.valueOf(pkgData.getType());

		return pkgType == PkgType.ADDON;
	}

	private QoSProfileDetail createQoSProfileDetailWithActionAccept(QoSProfileAction qosProfileAction,
																	IPCANQoS sessionQoS,
																	List<PCCRule> pccRules,
																	List<ChargingRuleBaseName> chargingRuleBaseNames,
																	boolean isQuotaProfileAttached,
																	boolean isApplicableOnUsageUnavailability,
																	Integer orderNo,
																	QosProfileDetailData qosProfileDetailData,
																	DataRateCard dataRateCard, Map<String, QuotaProfileDetail> serviceToQuotaProfileDetail,
																	String qosProfileName,
																	SliceInformation sliceInformation,
																	QuotaProfileDetail allServiceQuotaProfileDetail) {


		boolean usageRequired = isUsageRequired(qosProfileDetailData.getQosProfile().getPkgData());

		String pkgName = qosProfileDetailData.getQosProfile().getPkgData().getName();
		boolean usageMonitoring = qosProfileDetailData.getUsageMonitoring();
		int fupLevel = qosProfileDetailData.getFupLevel();
		@Nullable
		String redirectURL = qosProfileDetailData.getRedirectUrl();
		if (qosProfileDetailData.getQosProfile().getPkgData().getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED) {

			Map<String, QuotaProfileDetail> serviceToSyQuotaProfileDetail = null;
			if (serviceToQuotaProfileDetail != null) {
				serviceToSyQuotaProfileDetail = new HashMap<>();
				for (Entry<String, QuotaProfileDetail> entry : serviceToQuotaProfileDetail.entrySet()) {
					serviceToSyQuotaProfileDetail
							.put(entry.getKey(), entry
									.getValue());
				}
			}

			return packageFactory.createSyCounterBaseQoSProfileDetail(qosProfileName,
					pkgName,
					qosProfileAction,
					null,
					fupLevel,
					(SyCounterBaseQuotaProfileDetail) allServiceQuotaProfileDetail,
					serviceToSyQuotaProfileDetail,
					sessionQoS,
					pccRules,
					usageMonitoring,
					sliceInformation, orderNo, redirectURL, chargingRuleBaseNames);

		} else if (qosProfileDetailData.getQosProfile().getPkgData().getQuotaProfileType() == QuotaProfileType.RnC_BASED) {

			if (Objects.isNull(allServiceQuotaProfileDetail) && Objects.isNull(serviceToQuotaProfileDetail) && Objects.nonNull(dataRateCard) ) {
				return packageFactory.createRateCardBaseQoSProfileDetail(qosProfileName, pkgName,
						dataRateCard,
						sessionQoS,
						pccRules,
						redirectURL,
						chargingRuleBaseNames);
			}

			return packageFactory.createRnCBaseQoSProfileDetail(qosProfileName, pkgName,
					fupLevel,
					(RncProfileDetail) allServiceQuotaProfileDetail,
					serviceToQuotaProfileDetail,
					sessionQoS, pccRules, orderNo,
					redirectURL, true, chargingRuleBaseNames);
		} else {

			if (isQuotaProfileAttached && allServiceQuotaProfileDetail == null) {
				return packageFactory.createFailedUMBaseQoSProfileDetail(qosProfileName,
						pkgName,
						qosProfileAction,
						null,
						fupLevel,
						sessionQoS,
						pccRules,
						usageMonitoring,
						sliceInformation, redirectURL, chargingRuleBaseNames);
			} else {


				return packageFactory.createUMBaseQoSProfileDetail(qosProfileName, pkgName,
						qosProfileAction, null, fupLevel,
						(UMBaseQuotaProfileDetail) allServiceQuotaProfileDetail,
						serviceToQuotaProfileDetail,
						usageRequired,
						sessionQoS, pccRules, usageMonitoring, sliceInformation, orderNo, isApplicableOnUsageUnavailability,
						redirectURL, chargingRuleBaseNames);
			}

		}

	}

	private static class PCCRuleCreateTimeBaseComparator implements Comparator<PCCRuleData> {

		@Override
		public int compare(PCCRuleData o1, PCCRuleData o2) {
			return o1.getCreatedDate().compareTo(o2.getCreatedDate());
		}

	}



	private static class ChargingRuleBaseNameCreateTimeBaseComparator implements Comparator<ChargingRuleBaseNameData> {

		@Override
		public int compare(ChargingRuleBaseNameData o1, ChargingRuleBaseNameData o2) {
			return o1.getCreatedDate().compareTo(o2.getCreatedDate());
		}

	}




}