package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.ignoreTrace;


public class QuotaProfile implements Serializable, ToStringable{

	private static final long serialVersionUID = 1L;

	private final String name;
	private final String pkgName;
	private final String id;
	private final BalanceLevel balanceLevel;
	private final QuotaProfileType quotaProfileType;
	private int renewalInterval;
	private RenewalIntervalUnit renewalIntervalUnit;
	private final Map<String, QuotaProfileDetail> hsqLevelServiceWiseQuotaProfileDetais;
	private final List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetails;
	private Boolean proration;
	private Boolean carryForward;
	public QuotaProfile(String name,
			String pkgName,
			String id,
			BalanceLevel balanceLevel,
			int			renewalInterval,
			RenewalIntervalUnit renewalIntervalUnit,
			QuotaProfileType quotaProfileType,
			List<Map<String, QuotaProfileDetail>> fupLevelserviceWiseQuotaProfileDetais,Boolean proration, Boolean carryForward) {
		super();
		this.name = name;
		this.pkgName = pkgName;
		this.id = id;
		this.balanceLevel = balanceLevel;
		this.renewalInterval=renewalInterval;
		this.renewalIntervalUnit=renewalIntervalUnit;
		this.quotaProfileType = quotaProfileType;
		this.fupLevelserviceWiseQuotaProfileDetails = fupLevelserviceWiseQuotaProfileDetais;
		this.hsqLevelServiceWiseQuotaProfileDetais = fupLevelserviceWiseQuotaProfileDetais.get(0);
		this.proration = proration;
		this.carryForward = carryForward;
	}

	public String getName() {
		return name;
	}

	public String getPkgName() {
		return pkgName;
	}

	public String getId() {
		return id;
	}

	public BalanceLevel getBalanceLevel(){
		return balanceLevel;
	}

	public QuotaProfileType getType() {
		return quotaProfileType;
	}

	public Map<String, QuotaProfileDetail> getServiceWiseQuotaProfileDetails(int level) {

			if(fupLevelserviceWiseQuotaProfileDetails == null) {
				return null;
			}

			if(level < fupLevelserviceWiseQuotaProfileDetails.size()) {
				return fupLevelserviceWiseQuotaProfileDetails.get(level);
			}

			return null;

	}

	public Map<String, QuotaProfileDetail> getHsqLevelServiceWiseQuotaProfileDetails() {
		return hsqLevelServiceWiseQuotaProfileDetais;
	}

	public List<Map<String, QuotaProfileDetail>> getAllLevelServiceWiseQuotaProfileDetails() {
		return fupLevelserviceWiseQuotaProfileDetails;
	}

	@Override
	public String toString() {

		IndentingToStringBuilder toStringBuilder = new IndentingToStringBuilder();

		toString(toStringBuilder);

		return toStringBuilder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.newline()
				.incrementIndentation()
				.append("Name", name)
				.append("Balance Level", balanceLevel.displayVal)
				.append("Quota Profile Type", quotaProfileType.getVal());

		if(renewalInterval!=0){
			builder.append("Renewal Interval", renewalInterval);
			builder.append("Renewal Interval Unit", renewalIntervalUnit.value());
		} else {
			builder.append("Renewal Interval", CommonConstants.NOT_APPLICABLE);
			builder.append("Renewal Interval Unit", CommonConstants.NOT_APPLICABLE);
		}

		builder.append("Proration", proration);

		builder.append("Carry Forward", carryForward);

		builder.newline();
		if (Collectionz.isNullOrEmpty(fupLevelserviceWiseQuotaProfileDetails) == false) {

			for (Map<String, QuotaProfileDetail> quotaProfileDetails : fupLevelserviceWiseQuotaProfileDetails) {
				for (QuotaProfileDetail quotaProfileDetail : quotaProfileDetails.values()) {
					if (quotaProfileDetail instanceof RncProfileDetail) {
						((RncProfileDetail)quotaProfileDetail).setCarryForward(carryForward);
					}
					builder.append("Quota Profile Details" , quotaProfileDetail);
				}
			}
		} else {
			builder.append("Quota Profile Details", "No quota profile detail(s) found");
		}

	}



	public RenewalIntervalUnit getRenewalIntervalUnit() {
		return renewalIntervalUnit;
	}

	public int getRenewalInterval() {
		return renewalInterval;
	}

	@Override
	public int hashCode() {
		return id.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}

		try {
			QuotaProfile other = (QuotaProfile) obj;
			if (id.equals(other.id) == false) {
				return false;
			}
		} catch (ClassCastException e) {
			ignoreTrace(e);
			return false;
		}
		return true;
	}

	public Boolean getProration() {
		return proration;
	}

	public void setProration(Boolean proration) {
		this.proration = proration;
	}

	public Boolean getCarryForward() {
		return carryForward;
	}
}