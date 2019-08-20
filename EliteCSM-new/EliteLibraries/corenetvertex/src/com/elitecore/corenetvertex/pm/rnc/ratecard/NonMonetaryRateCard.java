package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;

public class NonMonetaryRateCard implements RateCard{

	private static final long serialVersionUID = 1L;
	private static final RateCardType type = RateCardType.NON_MONETARY;

	private String id;
	private String name;
	private String description;
	private Uom pulseUom;
	private Uom timeUom;
	private Long pulse;
	private long time;
	private long event;
	private String rncPackageId;
	private String rncPackageName;
	private String rateCardGroupId;
	private String rateCardGroupName;
	private long timeMinorUnit;
	private long pulseMinorUnit;
	private RenewalIntervalUnit renewalIntervalUnit;
	private int renewalInterval;
	private boolean proration;

	public NonMonetaryRateCard(String id, String name, String description, Uom timeUom, long time, long timeMinorUnit,
							   long event, Uom pulseUom, long pulse, long pulseMinorUnit, String rncPackageId, String rncPackageName,
							   String rateCardGroupId, String rateCardGroupName,int renewalInterval, RenewalIntervalUnit renewalIntervalUnit,boolean proration) {
		this.id = id;
		this.name = name;
		this.description = description;
		this.rncPackageId = rncPackageId;
		this.rncPackageName = rncPackageName;
		this.rateCardGroupId = rateCardGroupId;
		this.rateCardGroupName = rateCardGroupName;
		this.timeUom = timeUom;
		this.time = time;
		this.event = event;
		this.pulseUom = pulseUom;
		this.pulse = pulse;
		this.timeMinorUnit = timeMinorUnit;
		this.pulseMinorUnit = pulseMinorUnit;
		this.renewalIntervalUnit = renewalIntervalUnit;
		this.renewalInterval = renewalInterval;
		this.proration = proration;
	}

	public String getName() {
		return name;
	}

	public Uom getPulseUom() {
		return pulseUom;
	}

	public Uom getTimeUom() {
		return timeUom;
	}

	public Long getPulse() {
		return pulse;
	}

	public long getTime() {
		return time;
	}

	public long getEvent() {
		return event;
	}

	public String getId() {
		return id;
	}

	public String getDescription() {
		return description;
	}

	public String getRncPackageId() {
		return rncPackageId;
	}

	public String getRncPackageName() {
		return rncPackageName;
	}

	public String getRateCardGroupId() {
		return rateCardGroupId;
	}

	public String getRateCardGroupName() {
		return rateCardGroupName;
	}

	public long getTimeMinorUnit() {
		return timeMinorUnit;
	}

	public long getPulseMinorUnit() {
		return pulseMinorUnit;
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {

		builder.append("Name", name);
		builder.append("Description", description);
		builder.append("Type", type.getValue());

        if(time!= CommonConstants.QUOTA_UNDEFINED){
            builder.append("Time Unit", timeUom);
            builder.append("Time", time==CommonConstants.QUOTA_UNLIMITED?CommonConstants.UNLIMITED:time);
        }

        if(event!= CommonConstants.QUOTA_UNDEFINED){
            builder.append("Event", event);
        }
        builder.append("Pulse Unit", pulseUom);
        builder.append("Pulse", pulse);

		builder.append("Renewal Interval", renewalInterval);
        builder.append("Renewal Interval Unit", renewalIntervalUnit);
		builder.append("Pro-ration", proration);

		builder.newline();
	}

	@Override
	public String toString(){
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		toString(builder);
		return builder.toString();
	}

	@Override
	public RateCardType getType() {
		return type;
	}

	public RenewalIntervalUnit getRenewalIntervalUnit() {
		return renewalIntervalUnit;
	}

	public void setRenewalIntervalUnit(RenewalIntervalUnit renewalIntervalUnit) {
		this.renewalIntervalUnit = renewalIntervalUnit;
	}

	public int getRenewalInterval() {
		return renewalInterval;
	}

	public void setRenewalInterval(int renewalInterval) {
		this.renewalInterval = renewalInterval;
	}

	public boolean getProration() {
		return proration;
	}
}
