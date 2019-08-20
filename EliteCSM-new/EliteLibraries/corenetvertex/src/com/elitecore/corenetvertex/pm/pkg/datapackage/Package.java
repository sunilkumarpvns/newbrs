package com.elitecore.corenetvertex.pm.pkg.datapackage;

import com.elitecore.acesstime.AccessTimePolicy;
import com.elitecore.acesstime.TimePeriod;
import com.elitecore.acesstime.TimeSlot;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.annotation.Lazy;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.QoSProfileAction;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.IPCANQoS;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfile;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.QoSProfileDetail;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.pm.pkg.notification.QuotaNotificationScheme;
import com.elitecore.corenetvertex.pm.pkg.notification.UsageNotificationScheme;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

import javax.annotation.Nullable;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.stream.Collectors;

public abstract class Package implements UserPackage {

	private static final long serialVersionUID = 1L;
	private static final long ONE_DAY_TOTAL_SEC = TimeUnit.DAYS.toSeconds(1);
	private final String name;
	private final String id;
	private final List<QoSProfile> qosProfiles;
	private QuotaNotificationScheme quotaNotificationScheme;
	private final Map<String, QoSProfile> idToqosProfile;
	private final Map<String, QuotaProfile> monitoringKeyToQuotaProfile;
	private final Map<String, PCCRule> monitoringKeyToPCCRule;
	private final Map<String, ChargingRuleBaseName> monitoringKeyToChargingRuleBaseName;
	private final List<String> quotaProfileIds;
	private final List<String> dataRateCardIds;
	private final QuotaProfileType quotaProfileType;
	private final PkgStatus availabilityStatus;
	@Lazy private ArrayList<TimePeriod> timeSlots;
	private boolean isQuotaProfileExist;
	private boolean isQoSProfileDetailsExist;
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
	private List<DataRateCard> dataRateCards;
	private Map<String, QuotaProfile> idToQuotaProfiles;
	private Map<String, DataRateCard> idToDataRateCards;
	private List<String> groupIds;
	private String param1;
	private String param2;
	private String currency;

	public Package(String id,
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
				   PolicyStatus status,
				   @Nullable String failReason,
				   @Nullable String partialFailReason,
				   @Nullable String param1,
				   @Nullable String param2, QuotaNotificationScheme quotaNotificationScheme) {
		super();
		this.name = name;
		this.id = id;
		this.quotaProfileType = quotaProfileType;
		this.availabilityStatus = availabilityStatus;
		this.qosProfiles = qosProfiles;
		this.quotaNotificationScheme = quotaNotificationScheme;
		if(Strings.isNullOrBlank(failReason)) {
			this.idToqosProfile = qosProfiles.stream().collect(Collectors.toMap(QoSProfile::getId, Function.identity()));
		} else {
			this.idToqosProfile = Collections.emptyMap();
		}
		this.usageNotificationScheme = usageNotificationScheme;
		this.packageMode = packageMode;
		this.description = description;
		this.price = price;
		this.availabilityStartDate = availabilityStartDate;
		this.availabilityEndDate = availabilityEndDate;
		this.groupIds = groupIds;
		this.status = status;
		this.failReason = failReason;
		this.partialFailReason = partialFailReason;
		this.monitoringKeyToQuotaProfile = new HashMap<>(1, 1);
		this.monitoringKeyToPCCRule = new HashMap<>(1, 1);
		this.monitoringKeyToChargingRuleBaseName = new HashMap<String, ChargingRuleBaseName>(1,1);
		this.quotaProfileIds = new ArrayList<>();
		this.dataRateCardIds = new ArrayList<>();
		this.quotaProfiles = new ArrayList<>();
		this.dataRateCards = new ArrayList<>();
		this.idToQuotaProfiles = new HashMap<>();
		this.idToDataRateCards = new HashMap<>();
		this.param1 = param1;
		this.param2 = param2;
	}

	public Package(String id,
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
                   PolicyStatus status,
                   @Nullable String failReason,
                   @Nullable String partialFailReason,
                   @Nullable String param1,
                   @Nullable String param2, QuotaNotificationScheme quotaNotificationScheme,
				   @Nullable String currency) {
		super();
		this.name = name;
		this.id = id;
		this.quotaProfileType = quotaProfileType;
		this.availabilityStatus = availabilityStatus;
		this.qosProfiles = qosProfiles;
		this.quotaNotificationScheme = quotaNotificationScheme;
		if(Strings.isNullOrBlank(failReason)) {
			this.idToqosProfile = qosProfiles.stream().collect(Collectors.toMap(QoSProfile::getId, Function.identity()));
		} else {
			this.idToqosProfile = Collections.emptyMap();
		}
		this.usageNotificationScheme = usageNotificationScheme;
		this.packageMode = packageMode;
		this.description = description;
		this.price = price;
		this.availabilityStartDate = availabilityStartDate;
		this.availabilityEndDate = availabilityEndDate;
		this.groupIds = groupIds;
		this.status = status;
		this.failReason = failReason;
		this.partialFailReason = partialFailReason;
		this.monitoringKeyToQuotaProfile = new HashMap<>(1, 1);
		this.monitoringKeyToPCCRule = new HashMap<>(1, 1);
		this.monitoringKeyToChargingRuleBaseName = new HashMap<String, ChargingRuleBaseName>(1,1);
		this.quotaProfileIds = new ArrayList<>();
		this.dataRateCardIds = new ArrayList<>();
		this.quotaProfiles = new ArrayList<>();
		this.dataRateCards = new ArrayList<>();
		this.idToQuotaProfiles = new HashMap<>();
		this.idToDataRateCards = new HashMap<>();
		this.param1 = param1;
		this.param2 = param2;
		this.currency=currency;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.newpkg.IPackage#init()
	 */
	public void init() {
		TreeSet<Time> timeSlots = new TreeSet<Time>();
		
		if(status == PolicyStatus.FAILURE){
			return;
		}
		

		for(QoSProfile qosProfile : qosProfiles) {
			
			QuotaProfile quotaProfile = qosProfile.getQuotaProfile();
			
			if(quotaProfile != null) {
				isQuotaProfileExist = true;
				quotaProfileIds.add(quotaProfile.getId());
				List<PCCRule> pccRules = qosProfile.getPCCRules();
				
				for(PCCRule pccRule : pccRules) {
					monitoringKeyToQuotaProfile.put(pccRule.getMonitoringKey(), quotaProfile);
					monitoringKeyToPCCRule.put(pccRule.getMonitoringKey(), pccRule);
				}

				// CRBN BEGIN
				List<ChargingRuleBaseName> chargingRuleBaseNames = qosProfile.getChargingRuleBaseNames();
				if( Collectionz.isNullOrEmpty(chargingRuleBaseNames)==false ) {
					for (ChargingRuleBaseName chargingRuleBaseName : chargingRuleBaseNames) {
						for (String monitorngKey : chargingRuleBaseName.getMonitoringKeyServiceTypeMap().keySet()) {
							monitoringKeyToQuotaProfile.put(monitorngKey, quotaProfile);
							monitoringKeyToChargingRuleBaseName.put(monitorngKey, chargingRuleBaseName);
						}
					}
				}
				// CRBN END

				idToQuotaProfiles.put(quotaProfile.getId(), quotaProfile);
			}

			for(QoSProfile qosProfileForDataRateCard : qosProfiles) {

				DataRateCard dataRateCard = qosProfileForDataRateCard.getDataRateCard();

				if (dataRateCard != null) {
					dataRateCardIds.add(dataRateCard.getId());

					idToDataRateCards.put(dataRateCard.getId(), dataRateCard);
				}
			}
			
			AccessTimePolicy accessTimePolicy = qosProfile.getAccessTimePolicy();
			if(accessTimePolicy != null) {
				for(TimeSlot timSlot : accessTimePolicy.getListTimeSlot()){
					for(TimePeriod period : timSlot.getTimePeriods()) {
						timeSlots.add(new Time(period.getStartHour(), period.getStartMinute(), period.getStartSecond()));
						timeSlots.add(new Time(period.getStopHour(), period.getStopMinute(), period.getStopSecond()));
					}
				}
			}

			if(qosProfile.getHSQLevelQoSDetail()!=null){
				isQoSProfileDetailsExist = true;
			}

		}

		this.quotaProfiles.addAll(idToQuotaProfiles.values());
		this.dataRateCards.addAll(idToDataRateCards.values());
		
		if(timeSlots.isEmpty() == false) {
			this.timeSlots = new ArrayList<>(timeSlots.size());
			Time currTime;
			Time nextTime = timeSlots.pollFirst();
			while(nextTime != null) {
				currTime = nextTime;
				nextTime = timeSlots.higher(currTime);
				if(nextTime != null)
					this.timeSlots.add(new TimePeriod(currTime.getHour(), currTime.getMinute(), currTime.getSecond(), nextTime.getHour(), nextTime.getMinute(), nextTime.getSecond()));
			}
			this.timeSlots.trimToSize();
		}
		
		
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getName()
	 */
	@Override
	public String getName() {
		return name;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getServiceId(java.lang.String)
	 */
	@Override
	public String getServiceId(String monitoringKey) {
		PCCRule pccRule = monitoringKeyToPCCRule.get(monitoringKey);

		if ( pccRule != null ){
				return pccRule.getServiceTypeId();
		} else {
			ChargingRuleBaseName chargingRuleBaseName = monitoringKeyToChargingRuleBaseName.get(monitoringKey);
			if (chargingRuleBaseName != null ) {

				DataServiceType dataServiceType = chargingRuleBaseName.getMonitoringKeyServiceTypeMap().get(monitoringKey);
				if(dataServiceType != null ) {
					return dataServiceType.getDataServiceTypeID();
				}
			}
		}

		return null;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getQuotaProfileByMonitoringKey(java.lang.String)
	 */
	@Override
	public QuotaProfile getQuotaProfileByMonitoringKey(String monitoringKey) {
		return monitoringKeyToQuotaProfile.get(monitoringKey);
	}

	@Override
	public QuotaProfile getQuotaProfile(String id) {
		return idToQuotaProfiles.get(id);
	}

	@Override
	public DataRateCard getDataRateCard(String id) {
		return idToDataRateCards.get(id);
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getTimeSlots()
	 */
	@Override
	public List<TimePeriod> getTimeSlots() {
		return timeSlots;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getQuotaProfileType()
	 */
	@Override
	public QuotaProfileType getQuotaProfileType() {
		return quotaProfileType;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#isQuotaProfileExist()
	 */
	@Override
	public boolean isQuotaProfileExist() {
		return isQuotaProfileExist;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#isQoSProfileDetailsExist()
	 */
	@Override
	public boolean isQoSProfileDetailsExist(){
		return isQoSProfileDetailsExist;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getNextSessionTimeOut(com.elitecore.acesstime.TimePeriod)
	 */
	@Override
	public long getNextSessionTimeOut(TimePeriod currTimePeriod) {
		if (timeSlots == null) {
			return AccessTimePolicy.NO_TIME_OUT;
		}
		
		//	In case 1
		TimePeriod firstTimePeriod = timeSlots.get(0);
		if(firstTimePeriod.compareTo(currTimePeriod) < 0) {
			return (long) firstTimePeriod.getStartTimeInSec() - currTimePeriod.getStopTimeInSec();
		}
			
		
		//	In case 2
		for(TimePeriod timePeriod : timeSlots){
			
			int result = timePeriod.compareTo(currTimePeriod);
			
			if(result == 0){
				
				int diffInSec = timePeriod.getStopTimeInSec() - currTimePeriod.getStopTimeInSec();
				
				if(diffInSec > 0){
					return diffInSec;	
				}
				
			} else if(result < 0) {
				break;
			}
				
		}
			
		//	In case 3
		return (ONE_DAY_TOTAL_SEC + firstTimePeriod.getStartTimeInSec() - currTimePeriod.getStartTimeInSec());
	
		
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getId()
	 */
	@Override
	public String getId() {
		return id;
	}

	public List<DataRateCard> getDataRateCards() {
		return dataRateCards;
	}

	/**
	 * Time class used in sorting TimePeriod between multiple TimeSlot 
	 *
	 */
	private class Time implements Comparable<Time> {
		
		private int hour;
		private int min;
		private int second;
		
		public Time(int hour, int min, int second) {
			this.hour = hour;
			this.min = min;
			this.second = second;
		}
		
		public int getHour() {
			return hour;
		}
		
		public int getMinute() {
			return min;
		}
		
		public int getSecond() {
			return second;
		}

		@Override
		public int compareTo(Time time) {
			int ans = hour - time.getHour();
			if (ans != 0) {
				return ans;
			}

			ans = min - time.getMinute();
			if (ans != 0) {
				return ans;
			}


			return second - time.getSecond();
		}

		@Override
		public boolean equals(Object o) {
			if (this == o) {
				return true;
			}
			if (o == null || getClass() != o.getClass()) {
				return false;
			}
			Time time = (Time) o;
			return hour == time.hour &&
					min == time.min &&
					second == time.second;
		}

		@Override
		public int hashCode() {
			return Objects.hash(hour, min, second);
		}

		@Override
		public String toString() {
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.print(hour + ":" + min + ":" + second);
			out.close();
			return stringWriter.toString();
		}
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getQoSProfiles()
	 */
	@Override
	public List<QoSProfile> getQoSProfiles() {
		return qosProfiles;
	}

    @Override
    public QoSProfile getQoSProfile(String id) {
        return idToqosProfile.get(id);
    }
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getStatus()
	 */
	@Override
	public PolicyStatus getStatus() {
		return status;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getFailReason()
	 */
	@Override
	public String getFailReason() {
		return failReason;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getPartialFailReason()
	 */
	@Override
	public String getPartialFailReason() {
		return partialFailReason;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getAvailabilityStatus()
	 */
	@Override
	public PkgStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getUsageNotificationScheme()
	 */
	@Override
	public @Nullable UsageNotificationScheme getUsageNotificationScheme() {
		return usageNotificationScheme;
	}

	@Override
	public QuotaNotificationScheme getQuotaNotificationScheme() {
		return quotaNotificationScheme;
	}

	protected void createPCCTrace(Map<String, PCCRule> pccRules, IndentingWriter trace) {
		trace.print("PCCRules: ");
		if(pccRules == null || pccRules.isEmpty()) {
			trace.append("No PCCRule");
		} else {			
			for(PCCRule pccRule : pccRules.values()) {			
				trace.append(pccRule.getName() + "[Service:" + pccRule.getServiceName()
						+ ",QCI:" + pccRule.getQCI().stringVal 
						+ ",GBRUL:"  + pccRule.getGBRUL() 
						+ ",GBRDL:" + pccRule.getGBRDL() 
						+ ",MBRUL:" +pccRule.getMBRUL() 
						+ ",MBRDL:" + pccRule.getMBRDL() 
						+"], ");
			}
		}
		
		trace.println();
	}

	protected void createChargingRuleBaseNameTrace(List<ChargingRuleBaseName> chargingRuleBaseNames, IndentingWriter trace) {
		trace.print("Charging Rule Base Names: ");

		if(Collectionz.isNullOrEmpty(chargingRuleBaseNames)) {
			trace.append("No ChargingRuleBaseName");
		} else {
			for(ChargingRuleBaseName chargingRuleBaseName : chargingRuleBaseNames) {
				chargingRuleBaseName.printToQosSelectionSummary(trace);
			}
		}
		trace.println();
	}
	
	protected void createSessionQoSTrace(QoSProfileDetail qoSProfileDetail, IndentingWriter traceWriter) {
		
		traceWriter.print("QoS profile: " + qoSProfileDetail.getName());
		if (qoSProfileDetail.getAction() == QoSProfileAction.REJECT) {
			traceWriter.append("[Action: Reject, RejectCause: " + qoSProfileDetail.getRejectCause() + "]");
		} else {
			
			IPCANQoS sessionQoS = qoSProfileDetail.getSessionQoS();
			traceWriter.append("[QCI:"+ sessionQoS.getQCI() .stringVal
					+ ",AAMBRUL:"+ sessionQoS.getAAMBRULInBytes()
					+ ",AAMBRDL:"+ sessionQoS.getAAMBRDLInBytes()
					+ ",MBRUL:"+ sessionQoS.getMBRULInBytes()
					+ ",MBRDL:"+ sessionQoS.getMBRDLInBytes() + ']');
		}
		
		traceWriter.println();
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#isMonitoringKeyExist(java.lang.String)
	 */
	@Override
	public boolean isMonitoringKeyExist(String monitoringKey) {
		for (QoSProfile qoSProfile : qosProfiles) {
			for (PCCRule pccRule : qoSProfile.getPCCRules()) {
				if (pccRule.getMonitoringKey().equalsIgnoreCase(monitoringKey)) {
					return true;
				}
			}
		}
		return false;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#isQuotaProfileExists(java.lang.String)
	 */
	@Override
	public boolean isQuotaProfileExists(String quotaProfileId) {
		return quotaProfileIds.contains(quotaProfileId);
	}

	@Override
	public boolean isDataRateCardExists(String dataRateCardId) {
		return dataRateCardIds.contains(dataRateCardId);
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#isHsqLevelMonitoringKey(java.lang.String)
	 */
	@Override
	public boolean isHsqLevelMonitoringKey(String monitoringKey) {

		PCCRule pccRule = monitoringKeyToPCCRule.get(monitoringKey);
		if( pccRule != null ) {
			return pccRule.getFupLevel() == 0 ? true : false;
		} else {
			ChargingRuleBaseName chargingRuleBaseName = monitoringKeyToChargingRuleBaseName.get(monitoringKey);
			if(chargingRuleBaseName != null ) {
				return chargingRuleBaseName.getFupLevel() == 0 ? true : false;
			}
			return false;
		}

	}

	
	public List<QuotaProfile> getQuotaProfiles() {
		return quotaProfiles;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getPackageMode()
	 */
	@Override
	public PkgMode getMode() {
		return packageMode;
	}
	
	@Override
	public String getPackageMode() {
		return packageMode.name();
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#setPolicyStatus(com.elitecore.corenetvertex.constants.PolicyStatus)
	 */
	@Override
	public void setPolicyStatus(PolicyStatus status) {
		this.status = status;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#setPartialFailReason(java.lang.String)
	 */
	@Override
	public void setPartialFailReason(String partialFailReason) {
		this.partialFailReason = partialFailReason;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#setFailReason(java.lang.String)
	 */
	@Override
	public void setFailReason(String failReason) {
		this.failReason = failReason;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getDescription()
	 */
	@Override
	public String getDescription() {
		return description;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getPrice()
	 */
	@Override
	public Double getPrice() {
		return price;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getAvailabilityStartDate()
	 */
	@Override
	public Timestamp getAvailabilityStartDate() {
		return availabilityStartDate;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getAvailabilityEndDate()
	 */
	@Override
	public Timestamp getAvailabilityEndDate() {
		return availabilityEndDate;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.corenetvertex.pm.pkg.datapackage.UserPackage#getEligibleGroupIds()
	 */
	@Override
	public List<String> getGroupIds() {
		return groupIds;
	}

	@Nullable
	@Override
	public String getParam1() {
		return param1;
	}

	@Nullable
	@Override
	public String getParam2() {
		return param2;
	}

	@Nullable
	@Override
	public String getCurrency() {
		return currency;
	}

	@Override
	public String toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", getName())
				.append("Type", getPackageType())
				.append("Package Mode", getMode())
				.append("Quota Profile Type", getQuotaProfileType() != null ?  getQuotaProfileType() : "N/A")
				.append("Availibility Start Date", getAvailabilityStartDate())
				.append("Availibility End Date", getAvailabilityEndDate())
				.append("Availability Status", availabilityStatus)
				.append("Param 1",param1)
				.append("Param 2",param2)
				.append("Currency",currency);
		
		if (Collectionz.isNullOrEmpty(groupIds) == false) {
			toStringBuilder.append("Group Ids:");
			for (String groupId : groupIds) {
				toStringBuilder.append(groupId);
			}
		}
		
		toStringBuilder.append("\t");
		if (Collectionz.isNullOrEmpty(getQoSProfiles()) == false) {

			for (QoSProfile qosProfile : getQoSProfiles()) {
				toStringBuilder.append("QoS Profile", qosProfile);
			}
		}

		if (Collectionz.isNullOrEmpty(getQuotaProfiles()) == false) {

			for (QuotaProfile quotaProfile : getQuotaProfiles()) {
				toStringBuilder.append("Quota Profile", quotaProfile);
			}
		}

		if (Collectionz.isNullOrEmpty(getDataRateCards()) == false) {

			for (DataRateCard dataRateCard: getDataRateCards()) {
				toStringBuilder.append("Data Rate Card", dataRateCard);
			}
		}
		
		toStringBuilder.append("Policy Status", getStatus());
		if (Strings.isNullOrBlank(getFailReason()) == false) {
			toStringBuilder.append("Fail Reasons", getFailReason());
		}
		if (Strings.isNullOrBlank(getPartialFailReason()) == false) {
			toStringBuilder.append("Partial Fail Reasons", getPartialFailReason());
		}

		return toStringBuilder.toString();

	}
	
}
