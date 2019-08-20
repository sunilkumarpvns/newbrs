package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.acesstime.exception.InvalidTimeSlotException;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Splitter;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CounterPresence;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.qos.TimePeriodData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import org.apache.commons.collections.CollectionUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;

public class QoSProfileFactory {

	private static final Comparator<QoSProfileDetail> QOS_DETAIL_FUP_COMPARATOR = new QosProfileDetailComparator();
	private static final Splitter COMMA_BASE_SPLITTER = Splitter.on(CommonConstants.COMMA).trimTokens();
	private static final long DURATION_MIN_VALUE = CommonConstants.MIN_POSITIVE_INTEGER;
	private static final long DURATION_MAX_VALUE = java.util.concurrent.TimeUnit.DAYS.toMinutes(1);

	private PackageFactory packageFactory;
	private SyBasedQuotaProfileFactory syBasedQuotaProfileFactory;
	private UMBasedQuotaProfileFactory umBasedQuotaProfileFactory;
	private RncProfileFactory rncProfileFactory;
	private DataMonetaryRateCardFactory dataMonetaryRateCardFactory;
	private QoSProfileDetailFactory qoSProfileDetailFactory;

	public QoSProfileFactory(UMBasedQuotaProfileFactory umBasedQuotaProfileFactory,
							 SyBasedQuotaProfileFactory syBasedQuotaProfileFactory,
							 QoSProfileDetailFactory qoSProfileDetailFactory,
							 PackageFactory packageFactory,
							 RncProfileFactory rncProfileFactory,
							 DataMonetaryRateCardFactory dataMonetaryRateCardFactory) {
		this.umBasedQuotaProfileFactory = umBasedQuotaProfileFactory;
		this.syBasedQuotaProfileFactory = syBasedQuotaProfileFactory;
		this.qoSProfileDetailFactory = qoSProfileDetailFactory;
		this.packageFactory = packageFactory;
		this.rncProfileFactory = rncProfileFactory;
		this.dataMonetaryRateCardFactory = dataMonetaryRateCardFactory;
	}

	public QoSProfile createQoSProfile(QosProfileData qosProfileData, List<String> qosProfileFailReasons, List<String> qosProfilePartialFailReasons, List<RatingGroupData> ratingGroups) {

		QuotaProfile quotaProfile = null;

		DataRateCard dataRateCard = null;

		validate(qosProfileData);

		if (qosProfileData.getPkgData().getQuotaProfileType() == null) {
			if (qosProfileData.getQuotaProfile() != null) {
				qosProfileFailReasons.add("Quota Profile (" + qosProfileData.getQuotaProfile().getName()
						+ " parsing fail. Cause by: Value not found for Quota profile type");
			}
		} else {

		    if (PkgType.EMERGENCY.name().equals(qosProfileData.getPkgData().getType()) == false) {
                if (qosProfileData.getPkgData().getQuotaProfileType() == QuotaProfileType.SY_COUNTER_BASED) {
                    if (qosProfileData.getSyQuotaProfileData() != null) {
                        List<String> quotaProfileFailReasons = new ArrayList<String>();

                        quotaProfile = syBasedQuotaProfileFactory.createSyBaseQuotaProfile(qosProfileData.getSyQuotaProfileData().getId(), qosProfileData.getSyQuotaProfileData()
                                .getName(), qosProfileData.getSyQuotaProfileData(), quotaProfileFailReasons);
                        if (quotaProfileFailReasons.isEmpty() == false) {
                            qosProfileFailReasons.add("Quota Profile (" + qosProfileData.getSyQuotaProfileData().getName() + " parsing fail. Cause by:"
                                    + format(quotaProfileFailReasons));
                        }
                    }
                } else if (qosProfileData.getPkgData().getQuotaProfileType() == QuotaProfileType.USAGE_METERING_BASED) {
                    if (qosProfileData.getQuotaProfile() != null) {
                        List<String> quotaProfileFailReasons = new ArrayList<String>();
                        List<String> quotaProfilePartialFailReasons = new ArrayList<String>();

                        quotaProfile = umBasedQuotaProfileFactory.createUMBaseQuotaProfile(qosProfileData.getQuotaProfile(), quotaProfileFailReasons, quotaProfilePartialFailReasons);
                        if (quotaProfileFailReasons.isEmpty() == false) {
                            qosProfileFailReasons.add("Quota Profile (" + qosProfileData.getQuotaProfile().getName() + " parsing fail. Cause by:"
                                    + format(quotaProfileFailReasons));
                        }

                        if (quotaProfilePartialFailReasons.isEmpty() == false) {
                            qosProfilePartialFailReasons.add("Quota Profile (" + qosProfileData.getQuotaProfile().getName() + " parsing partially fail. Cause by:"
                                    + format(qosProfilePartialFailReasons));
                        }
                    }
                } else if (qosProfileData.getPkgData().getQuotaProfileType() == QuotaProfileType.RnC_BASED) {
                    List<String> quotaProfileFailReasons = new ArrayList<String>();

					RncProfileData rncProfileData = qosProfileData.getRncProfileData();
                    if (rncProfileData != null) {

						validateQosAndQuotaProfileBalanceLevels(qosProfileData, qosProfileFailReasons, rncProfileData);

                        quotaProfile = rncProfileFactory.create(rncProfileData, qosProfileData.getName(), quotaProfileFailReasons);

                        if (quotaProfileFailReasons.isEmpty() == false) {
                            qosProfileFailReasons.add("RnC Quota Profile (" + rncProfileData.getName() + " parsing fail. Cause by:"
                                    + format(quotaProfileFailReasons));
                        }

                    } else if (qosProfileData.getRncProfileData() == null && qosProfileData.getRateCardData() != null) {
                        List<String> dataRateCardFailReasons = new ArrayList<String>();

                        dataRateCard = dataMonetaryRateCardFactory.createDataMonetaryRateCard(qosProfileData.getRateCardData(), dataRateCardFailReasons);

                        if (dataRateCardFailReasons.isEmpty() == false) {
                            qosProfileFailReasons.add("Rate Card (" + qosProfileData.getRateCardData().getName() + " parsing fail. Cause by:"
                                    + format(dataRateCardFailReasons));
                        }

                    } else {
                        qosProfileFailReasons.add("Either Quota profile or Rate card is mandatory.");
                    }

                } else {
                    qosProfileFailReasons.add("Quota Profile (" + qosProfileData.getQuotaProfile().getName()
                            + " parsing fail. Cause by: Invalid Quota profile type:" + qosProfileData.getPkgData().getQuotaProfileType());
                }
            }
		}

		QoSProfileDetail hsqLevelQoSDetail = null;
		List<QoSProfileDetail> fupLevelQoSDetails = new ArrayList<QoSProfileDetail>();

		if (qosProfileData.getQosProfileDetailDataList().isEmpty() == false) {

			if (qosProfileData.getRncProfileData() == null && qosProfileData.getRateCardData() != null) {
				hsqLevelQoSDetail = createQoSProfileDetailForRateCard(qosProfileData, qosProfileFailReasons, ratingGroups, quotaProfile, dataRateCard,
						qosProfileData.getQosProfileDetailDataList());
			} else {
				for (QosProfileDetailData qosProfileDetailData : qosProfileData.getQosProfileDetailDataList()) {

					List<String> qosProfileDetailFailReasons = new ArrayList<String>();
					validateQoSProfileDetailData(qosProfileDetailData, qosProfileDetailFailReasons);
					if (qosProfileDetailFailReasons.isEmpty() == false) {
						qosProfileFailReasons.add("QOS profile detail parsing fail. Cause by:" + format(qosProfileDetailFailReasons));
						continue;
					}

					CounterPresence usagePresence = (qosProfileData.getQuotaProfile() == null || qosProfileData.getQuotaProfile().getUsagePresence() == null)
							? CounterPresence.MANDATORY
							: CounterPresence.fromValue(qosProfileData.getQuotaProfile().getUsagePresence());

					QoSProfileDetail qosProfileDetail = qoSProfileDetailFactory.createQoSDetail(qosProfileDetailData, qosProfileData.getName(), quotaProfile, dataRateCard, qosProfileData.getOrderNo(), qosProfileDetailFailReasons, usagePresence, ratingGroups);
					if (qosProfileDetailData.getFupLevel() == 0) {

						if (qosProfileDetailFailReasons.isEmpty() == false) {
							qosProfileFailReasons.add("QOS profile detail(hsq) parsing fail. Cause by:" + format(qosProfileDetailFailReasons));
						} else {
							hsqLevelQoSDetail = qosProfileDetail;
						}

					} else {

						if (qosProfileDetailFailReasons.isEmpty() == false) {
							qosProfileFailReasons.add("QOS profile detail(fup" + qosProfileDetailData.getFupLevel() + ") parsing fail. Cause by:"
									+ format(qosProfileDetailFailReasons));
						} else {
							fupLevelQoSDetails.add(qosProfileDetail);
						}
					}
				}
			}
		}

		// Generating logical expression
		LogicalExpression logicalExpression = null;
		String advancedCondition = qosProfileData.getAdvancedCondition();
		try {
			if (Strings.isNullOrBlank(advancedCondition) == false) {
				logicalExpression = com.elitecore.exprlib.compiler.Compiler.getDefaultCompiler().parseLogicalExpression(advancedCondition);
			}
		} catch (InvalidExpressionException e) {
			qosProfileFailReasons.add("Invalid condition: " + advancedCondition);
			ignoreTrace(e);
		}

		int duration;
		if (qosProfileData.getDuration() == null) {
			duration = 0;
		} else if (isValidDuration(qosProfileData.getDuration()) == false) {
			duration = 0;
			qosProfileFailReasons.add("Invalid duration configured: " + qosProfileData.getDuration());
		} else {
			duration = qosProfileData.getDuration().intValue();
		}

		// Making access time policy
		AccessTimePolicy accessTimePolicy = null;
		List<TimeSlot> timeSlots = null;
		try {
			timeSlots = createTimeSlots(qosProfileData.getTimePeriodDataList());
		} catch (InvalidTimeSlotException e) {
			qosProfileFailReasons.add("Invalid TimeSlot Configuration. Reason: " + e.getMessage());
			ignoreTrace(e);
		}

		if (Collectionz.isNullOrEmpty(timeSlots) == false) {
			accessTimePolicy = new AccessTimePolicy();
			for (TimeSlot timeSlot : timeSlots) {
				accessTimePolicy.addTimeSlot(timeSlot);
			}
		}

		if (qosProfileFailReasons.isEmpty() == false) {
			return null;
		}

		Collections.sort(fupLevelQoSDetails, QOS_DETAIL_FUP_COMPARATOR);
		List<String> accessNetworks = null;
		if (qosProfileData.getAccessNetwork() != null) {
			accessNetworks = COMMA_BASE_SPLITTER.split(qosProfileData.getAccessNetwork());
		}

		return packageFactory.createQoSProfile(qosProfileData.getId(), qosProfileData.getName(),
				qosProfileData.getPkgData().getName(),
				qosProfileData.getPkgData().getId(),
				quotaProfile,
				dataRateCard,
				accessNetworks
				, duration
				, hsqLevelQoSDetail
				, fupLevelQoSDetails
				, logicalExpression
				, advancedCondition
				, accessTimePolicy);

	}

	private void validateQosAndQuotaProfileBalanceLevels(QosProfileData qosProfileData, List<String> qosProfileFailReasons, RncProfileData rncProfileData) {
		List<QosProfileDetailData> qosProfileDetailDatas = qosProfileData.getQosProfileDetailDataList();

		if(CollectionUtils.isEmpty(qosProfileDetailDatas)){
			return;
		}

		int levelsConfigured = qosProfileDetailDatas.size();

		if(levelsConfigured!=qosProfileData.getRncProfileData().getFupCountConfigured()){
			qosProfileFailReasons.add("FUP levels configured for QOS and Quota Profile ("
					+ rncProfileData.getName() + ") do not match");
		}
	}

	private QoSProfileDetail createQoSProfileDetailForRateCard(QosProfileData qosProfileData, List<String> qosProfileFailReasons,
															   List<RatingGroupData> ratingGroups, QuotaProfile quotaProfile, DataRateCard dataRateCard,
															   List<QosProfileDetailData> qosProfileDetailDataList) {

		QosProfileDetailData hsqLevelQoSProfileDetailData = null;

		for (QosProfileDetailData qosProfileDetailData : qosProfileDetailDataList) {
			if (qosProfileDetailData.getFupLevel() == 0) {
				hsqLevelQoSProfileDetailData = qosProfileDetailData;
				break;
			}
		}

		List<String> qosProfileDetailFailReasons = new ArrayList<String>();

		validateQoSProfileDetailData(hsqLevelQoSProfileDetailData, qosProfileDetailFailReasons);

		if (qosProfileDetailFailReasons.isEmpty() == false) {
			qosProfileFailReasons.add("QOS profile detail parsing fail. Cause by:" + format(qosProfileDetailFailReasons));
		}

		CounterPresence usagePresence = (qosProfileData.getQuotaProfile() == null || qosProfileData.getQuotaProfile().getUsagePresence() == null)
				? CounterPresence.MANDATORY
				: CounterPresence.fromValue(qosProfileData.getQuotaProfile().getUsagePresence());

		QoSProfileDetail qosProfileDetail = qoSProfileDetailFactory.createQoSDetail(hsqLevelQoSProfileDetailData, qosProfileData.getName(), quotaProfile, dataRateCard, qosProfileData.getOrderNo(), qosProfileDetailFailReasons, usagePresence, ratingGroups);

		if (qosProfileDetailFailReasons.isEmpty() == false) {
			qosProfileFailReasons.add("QOS profile detail(hsq) parsing fail. Cause by:" + format(qosProfileDetailFailReasons));
		} else {
			return qosProfileDetail;
		}

		return qosProfileDetail;
	}

	private static void validate(QosProfileData qosProfileData) {
		if (qosProfileData.getOrderNo() == null) {
			qosProfileData.setOrderNo(0);
		}
	}


	private static String format(List<String> failReasons) {

		StringBuilder builder = new StringBuilder();

		for (int index = failReasons.size() - 1; index > 0; index--) {
			builder.append(failReasons.get(index));
			builder.append(CommonConstants.COMMA);
		}

		builder.append(failReasons.get(0));

		return builder.toString();
	}

	private static void validateQoSProfileDetailData(QosProfileDetailData qosProfileDetailData, List<String> failReasons) {

		if (qosProfileDetailData.getFupLevel() == null) {
			failReasons.add("FUP Level not defined");
		}

		if (QCI.fromId(qosProfileDetailData.getQci()) == null) {
			failReasons.add("Invalid QCI ID: " + qosProfileDetailData.getQci());
		}

		if (QoSProfileAction.fromValue(qosProfileDetailData.getAction()) == null) {
			failReasons.add("Invalid action configured: " + ((qosProfileDetailData.getAction() == null) ? "" : qosProfileDetailData.getAction()));
		}

		if (PkgType.ADDON.val.equalsIgnoreCase(qosProfileDetailData.getQosProfile().getPkgData().getType())) {

			if (qosProfileDetailData.getAction().equals(QoSProfileAction.REJECT.getId()))
				failReasons.add("AddOn cannot contain QoS profile with " + QoSProfileAction.REJECT + " action");
		}

		if (qosProfileDetailData.getMbrdl() == null) {
			qosProfileDetailData.setMbrdl((long) 0);
		} else if (qosProfileDetailData.getMbrdl() < 0) {
			failReasons.add("MBRDL value is negative(" + qosProfileDetailData.getMbrdl() + ")");
		}

		if (qosProfileDetailData.getMbrul() == null) {
			qosProfileDetailData.setMbrul((long) 0);
		} else if (qosProfileDetailData.getMbrul() < 0) {
			failReasons.add("MBRUL value is negative(" + qosProfileDetailData.getMbrul() + ")");
		}

		if (qosProfileDetailData.getAambrdl() == null) {
			qosProfileDetailData.setAambrdl(0l);
		} else if (qosProfileDetailData.getAambrdl() < 0) {
			failReasons.add("AAMBRDL value is negative(" + qosProfileDetailData.getAambrdl() + ")");
		}

		if (qosProfileDetailData.getAambrul() == null) {
			qosProfileDetailData.setAambrul(0l);
		} else if (qosProfileDetailData.getAambrul() < 0) {
			failReasons.add("AAMBRUL value is negative(" + qosProfileDetailData.getAambrul() + ")");
		}

		if (qosProfileDetailData.getUsageMonitoring() == null) {
			qosProfileDetailData.setUsageMonitoring(false);
		}

		if (qosProfileDetailData.getSliceTotal() == null) {
			qosProfileDetailData.setSliceTotal((long) 0);
		} else if (qosProfileDetailData.getUsageMonitoring() && qosProfileDetailData.getSliceTotal() < 0) {
			failReasons.add("Slice total value is negative(" + qosProfileDetailData.getSliceTotal() + ")");
		}

		if (qosProfileDetailData.getSliceUpload() == null) {
			qosProfileDetailData.setSliceUpload((long) 0);
		} else if (qosProfileDetailData.getUsageMonitoring() && qosProfileDetailData.getSliceUpload() < 0) {
			failReasons.add("Slice upload value is negative(" + qosProfileDetailData.getSliceUpload() + ")");
		}

		if (qosProfileDetailData.getSliceDownload() == null) {
			qosProfileDetailData.setSliceDownload((long) 0);
		} else if (qosProfileDetailData.getUsageMonitoring() && qosProfileDetailData.getSliceDownload() < 0) {
			failReasons.add("Slice download value is negative(" + qosProfileDetailData.getSliceDownload() + ")");
		}

		if (qosProfileDetailData.getSliceTime() == null) {
			qosProfileDetailData.setSliceTime(0l);
		} else if (qosProfileDetailData.getUsageMonitoring() && qosProfileDetailData.getSliceTime() < 0l) {
			failReasons.add("Slice time value is negative(" + qosProfileDetailData.getSliceTime() + ")");
		}

		if (qosProfileDetailData.getUsageMonitoring()) {
			if (qosProfileDetailData.getSliceTotal() <= 0 &&
					qosProfileDetailData.getSliceUpload() <= 0 &&
					qosProfileDetailData.getSliceDownload() <= 0 &&
					qosProfileDetailData.getSliceTime() <= 0) {
				failReasons.add("Slice infomation not provide or configured as 0 or negative");
			}

		}

	}

	private static class QosProfileDetailComparator implements Comparator<QoSProfileDetail> {

		@Override
		public int compare(QoSProfileDetail o1, QoSProfileDetail o2) {

			if (o1.getFUPLevel() < o2.getFUPLevel()) {
				return -1;
			} else if (o1.getFUPLevel() > o2.getFUPLevel()) {
				return 1;
			}
			return 0;
		}

	}

	private static List<TimeSlot> createTimeSlots(List<TimePeriodData> timePeriods) throws InvalidTimeSlotException {

		List<TimeSlot> timeSlots = new ArrayList<TimeSlot>();
		for (TimePeriodData timePeriod : timePeriods) {

			timeSlots.add(TimeSlot.getTimeSlot(timePeriod.getMoy(), timePeriod.getDom(), timePeriod.getDow(), timePeriod.getTimePeriod()));
		}
		return timeSlots;
	}

	private static boolean isValidDuration(Long duration) {
		return DURATION_MIN_VALUE <= duration && duration <= DURATION_MAX_VALUE;
	}

}