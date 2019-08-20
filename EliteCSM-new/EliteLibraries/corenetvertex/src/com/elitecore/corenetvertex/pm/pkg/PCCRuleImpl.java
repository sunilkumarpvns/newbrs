package com.elitecore.corenetvertex.pm.pkg;

import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.PriorityLevel;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class PCCRuleImpl implements PCCRule {

    public static final String MODULE = "PCC-RULE";

    private String id;
    private String name;
    private int precedence;
    private String ratingGroupId;
    private long chargingKey;
    private String appServiceProviderId;
    private String sponsorIdentity;
    private UsageMetering usageMetering;
    private QCI qci;

    @SerializedName("GBRDL")
    private long gbrdlInBytes;
    @SerializedName("GBRUL")
    private long gbrulInBytes;
    @SerializedName("MBRDL")
    private long mbrdlInBytes;
    @SerializedName("MBRUL")
    private long mbrulInBytes;
    private long gbrdl;
    private long gbrul;
    private long mbrdl;
    private long mbrul;
    private QoSUnit gbrdlUnit = QoSUnit.Bps;
    private QoSUnit gbrulUnit = QoSUnit.Bps;
    private QoSUnit mbrdlUnit = QoSUnit.Bps;
    private QoSUnit mbrulUnit = QoSUnit.Bps;
    private PriorityLevel arp;
    private boolean peCapability;
    private boolean peVulnerability;
    private List<String> serviceDataFlows;
    private ChargingModes chargingMode;
    private boolean dynamic;
    private FlowStatus flowStatus;
    private String serviceName;
    private long serviceIdentifier;
    private String monitoringKey;
    private PolicyStatus status;
    private SliceInformation sliceInformation;
    private String serviceTypeId;
    private int fupLevel = 0;

    public PCCRuleImpl(String pccRuleId, //NOSONAR A long parameter list can indicate that a new structure should be created to wrap the numerous parameters or that the function is doing too many things. Noncompliant Code Example
                       String pccRuleName,
                       int precedence,
                       long chargingKey,
                       String ratingGroupId,
                       String appServiceProviderId,
                       String sponsorIdentity,
                       UsageMetering usageMetering,
                       QCI qci,
                       long gbrdlInBytes,
                       long gbrulInBytes,
                       long mbrdlInBytes,
                       long mbrulInBytes,
                       QoSUnit gbrdlUnit,
                       QoSUnit gbrulUnit,
                       QoSUnit mbrdlUnit,
                       QoSUnit mbrulUnit,
                       long gbrdl,
                       long gbrul,
                       long mbrdl,
                       long mbrul,
                       PriorityLevel arp,
                       boolean peCapability,
                       boolean peVulnerability,
                       boolean dynamic,
                       List<String> serviceDataFlows,
                       ChargingModes chargingMode,
                       FlowStatus flowStatus,
                       String monitoringKey,
                       long serviceIdentifier,
                       String serviceName,
                       String serviceTypeId,
                       SliceInformation sliceInformation,
                       int fupLevel) {

        this.id = pccRuleId;
        this.name = pccRuleName;
        this.precedence = precedence;
        this.chargingKey = chargingKey;
        this.ratingGroupId = ratingGroupId;
        this.appServiceProviderId = appServiceProviderId;
        this.sponsorIdentity = sponsorIdentity;
        this.usageMetering = usageMetering;
        this.qci = qci;
        this.gbrdlInBytes = gbrdlInBytes;
        this.gbrulInBytes = gbrulInBytes;
        this.mbrdlInBytes = mbrdlInBytes;
        this.mbrulInBytes = mbrulInBytes;
        this.gbrdlUnit = gbrdlUnit;
        this.gbrulUnit = gbrulUnit;
        this.mbrdlUnit = mbrdlUnit;
        this.mbrulUnit = mbrulUnit;
        this.gbrdl = gbrdl;
        this.gbrul = gbrul;
        this.mbrdl = mbrdl;
        this.mbrul = mbrul;
        this.arp = arp;
        this.peCapability = peCapability;
        this.peVulnerability = peVulnerability;
        this.serviceDataFlows = serviceDataFlows;
        this.chargingMode = chargingMode;
        this.dynamic = dynamic;
        this.flowStatus = flowStatus;
        this.monitoringKey = monitoringKey;
        this.serviceIdentifier = serviceIdentifier;
        this.serviceName = serviceName;
        this.serviceTypeId = serviceTypeId;
        this.sliceInformation = sliceInformation;
        this.fupLevel = fupLevel;
    }

    public PCCRuleImpl(String id, String name) {
        this.id = id;
        this.name = name;
    }

    public static String getModule() {
        return MODULE;
    }


    @Override
    public int getPrecedence() {
        return precedence;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public long getChargingKey() {
        return chargingKey;
    }

    @Override
    public String getRatingGroupId() {
        return ratingGroupId;
    }

    @Override
    public String getAppServiceProviderId() {
        return appServiceProviderId;
    }

    @Override
    public String getSponsorIdentity() {
        return sponsorIdentity;
    }

    @Override
    public long getServiceIdentifier() {
        return serviceIdentifier;
    }

    @Override
    public UsageMetering getUsageMetering() {
        return usageMetering;
    }

    @Override
    public QCI getQCI() {
        return qci;
    }

    @Override
    public PriorityLevel getPriorityLevel() {
        return arp;
    }

    @Override
    public boolean getPeCapability() {
        return peCapability;
    }

    @Override
    public boolean getPeVulnerability() {
        return peVulnerability;
    }

    @Override
    public List<String> getServiceDataFlows() {
        return serviceDataFlows;
    }

    @Override
    public long getGBRDL() {
        return gbrdlInBytes;
    }

    @Override
    public long getGBRUL() {
        return gbrulInBytes;
    }

    @Override
    public long getMBRDL() {
        return mbrdlInBytes;
    }

    @Override
    public long getMBRUL() {
        return mbrulInBytes;
    }

    @Override
    public String getServiceName() {
        return serviceName;
    }

    @Override
    public ChargingModes getChargingMode() {
        return chargingMode;
    }

    @Override
    public boolean isPredifine() {
        return dynamic == false;
    }

    public boolean isDynamic() {
        return dynamic;
    }

    @Override
    public FlowStatus getFlowStatus() {
        return flowStatus;
    }

    @Override
    public String getMonitoringKey() {
        return monitoringKey;
    }

    public PolicyStatus getStatus() {
        return status;
    }

    @Override
    public int getFupLevel() {
        return fupLevel;
    }

    @Override
    public SliceInformation getSliceInformation() {
        return sliceInformation;
    }

    public void setSliceInformation(SliceInformation sliceInformation) {
        this.sliceInformation = sliceInformation;
    }

    @Override
    public void setQCI(QCI qci) {
        this.qci = qci;
    }

    @Override
    public void setMBRUL(long val) {
        this.mbrulInBytes = val;
    }

    @Override
    public void setMBRDL(long val) {
        this.mbrdlInBytes = val;
    }

    @Override
    public void setGBRUL(long val) {
        this.gbrulInBytes = val;
    }

    @Override
    public void setGBRDL(long val) {
        this.gbrdlInBytes = val;
    }


    public static class PCCRuleBuilder {

        private PCCRuleImpl pccRule;

        public PCCRuleBuilder(String id, String name) {
            pccRule = new PCCRuleImpl(id, name);
        }

        public PCCRuleImpl build() {
            return pccRule;
        }

        public PCCRuleBuilder withId(String id) {
            pccRule.id = id;
            return this;
        }

        public PCCRuleBuilder withFUPLevel(int fupLevel) {
            pccRule.fupLevel = fupLevel;
            return this;
        }

        public PCCRuleBuilder withName(String name) {
            pccRule.name = name;
            return this;
        }

        public PCCRuleBuilder withStatus(PolicyStatus status) {
            pccRule.status = status;
            return this;
        }

        public PCCRuleBuilder withPrecedence(int precedence) {
            pccRule.precedence = precedence;
            return this;
        }

        public PCCRuleBuilder withChargingKey(long chargingKey) {
            pccRule.chargingKey = chargingKey;
            return this;
        }

        public PCCRuleBuilder withRatingGroupId(String ratingGroupId) {
            pccRule.ratingGroupId = ratingGroupId;
            return this;
        }

        public PCCRuleBuilder withRatingGroup(RatingGroup ratingGroup) {
            pccRule.chargingKey = ratingGroup.getIdentifier();
            pccRule.ratingGroupId = ratingGroup.getRatingGroupId();
            return this;
        }

        public PCCRuleBuilder withAppServiceProviderId(String appServiceProviderId) {
            pccRule.appServiceProviderId = appServiceProviderId;
            return this;
        }

        public PCCRuleBuilder withUsageMetering(UsageMetering usageMetering) {
            pccRule.usageMetering = usageMetering;
            return this;
        }

        public PCCRuleBuilder withQci(QCI qci) {
            pccRule.qci = qci;
            return this;
        }

        public PCCRuleBuilder withGbrdl(long gbrdl) {
            pccRule.gbrdlInBytes = gbrdl;
            return this;
        }

        public PCCRuleBuilder withGbrul(long gbrul) {
            pccRule.gbrulInBytes = gbrul;
            return this;
        }

        public PCCRuleBuilder withMbrdl(long mbrdl) {
            pccRule.mbrdlInBytes = mbrdl;
            return this;
        }

        public PCCRuleBuilder withMbrul(long mbrul) {
            pccRule.mbrulInBytes = mbrul;
            return this;
        }

        public PCCRuleBuilder withPriorityLevel(PriorityLevel arp) {
            pccRule.arp = arp;
            return this;
        }

        public PCCRuleBuilder withPeCapability(boolean peCapability) {
            pccRule.peCapability = peCapability;
            return this;
        }

        public PCCRuleBuilder withPeVulnerability(boolean peVulnerability) {
            pccRule.peVulnerability = peVulnerability;
            return this;
        }

        public PCCRuleBuilder withServiceDataFlows(List<String> serviceDataFlows) {
            pccRule.serviceDataFlows = serviceDataFlows;
            return this;
        }

        public PCCRuleBuilder withChargingMode(ChargingModes chargingMode) {
            pccRule.chargingMode = chargingMode;
            return this;
        }

        public PCCRuleBuilder withDynamic(boolean predefine) {
            pccRule.dynamic = predefine;
            return this;
        }

        public PCCRuleBuilder withFlowStatus(FlowStatus flowStatus) {
            pccRule.flowStatus = flowStatus;
            return this;
        }

        public PCCRuleBuilder withServiceType(DataServiceType serviceType) {
            pccRule.serviceName = serviceType.getName();
            pccRule.serviceTypeId = serviceType.getDataServiceTypeID();
            pccRule.serviceIdentifier = serviceType.getServiceIdentifier();
            pccRule.serviceTypeId = serviceType.getDataServiceTypeID();
            return this;
        }

        public PCCRuleBuilder withMonitoringKey(String monitoringKey) {
            pccRule.monitoringKey = monitoringKey;
            return this;
        }

        public PCCRuleBuilder withServiceName(String serviceName) {
            pccRule.serviceName = serviceName;
            return this;
        }

        public PCCRuleBuilder withServiceIdentifier(long identifier) {
            pccRule.serviceIdentifier = identifier;
            return this;
        }

        public PCCRuleBuilder withServiceTypeId(String serviceTypeId) {
            pccRule.serviceTypeId = serviceTypeId;
            return this;
        }
    }

    @Override
    public int compareTo(PCCRule selectedPCCRule) {
        return pccRuleQoSBaseComparator.compare(this, selectedPCCRule);
    }

    @Override
    public void setPrecedence(int precedence) {
        this.precedence = precedence;
    }

    @Override
    public void setChargingMode(ChargingModes chargingModes) {
        this.chargingMode = chargingModes;
    }


    @Override
    public void setAppServiceProviderId(String appServiceProviderId) {
        this.appServiceProviderId = appServiceProviderId;
    }

    @Override
    public void setSponsorIdentity(String sponsorIdentity) {
        this.sponsorIdentity = sponsorIdentity;
    }

    @Override
    public void setPriorityLevel(PriorityLevel arp) {
        this.arp = arp;
    }

    @Override
    public void setPeCapability(boolean peCapability) {
        this.peCapability = peCapability;
    }

    @Override
    public void setPeVulnerability(boolean peVulnerability) {
        this.peVulnerability = peVulnerability;
    }

    @Override
    public void setChargingKey(long chargingKey) {
        this.chargingKey = chargingKey;
    }

    @Override
    public void setFlowStatus(FlowStatus flowStatus) {
        this.flowStatus = flowStatus;
    }

    @Override
    public String getServiceTypeId() {
        return serviceTypeId;
    }

    @Override
    public String toString() {

        return toString(PCC_RULE_DATA_TO_STRING_STYLE);
    }

    public String toString(ToStringStyle toStringStyle) {

        ToStringBuilder builder = new ToStringBuilder(this, toStringStyle).append("Name", name)
                .append("QCI", qci);

        if (gbrdl > 0) {
            builder.append("GBRDL", gbrdl + " " + gbrdlUnit);
        }

        if (gbrul > 0) {
            builder.append("GBRUL", gbrul + " " + gbrulUnit);
        }

        if (mbrdl > 0) {
            builder.append("MBRDL", mbrdl + " " + mbrdlUnit);
        }

        if (mbrul > 0) {
            builder.append("MBRUL", mbrul + " " + mbrulUnit);
        }

        builder.append("Dynamic PCC: ", dynamic)
                .append("Monitoring Key", monitoringKey).append("")
                .append("Service Name", serviceName)
                .append("Charging Key", chargingKey).toString();

        return builder.toString();
    }

}
