package com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile;


import com.elitecore.commons.base.Numbers;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PolicyViewConstant;
import com.elitecore.corenetvertex.constants.PriorityLevel;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.constants.QoSUnit;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;
import com.google.gson.Gson;
import com.google.gson.annotations.SerializedName;
import org.apache.commons.lang.SystemUtils;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.io.Serializable;

public class IPCANQoS implements Serializable {

	private static final String MODULE = "IP-CAN-QOS";
	public static final ToStringStyle IP_CAN_QOS_TO_STRING_STYLE = new IPCanQoSToStringStyle();
	private @SerializedName(PolicyViewConstant.IPCAN_QOS_AAMBRDL)long aambrdlInBytes;
	private @SerializedName(PolicyViewConstant.IPCAN_QOS_AAMBRUL)long aambrulInBytes;
	private @SerializedName(PolicyViewConstant.QCI)QCI qci;
	private @SerializedName(PolicyViewConstant.IPCAN_QOS_PRIORITY_LEVEL)PriorityLevel priorityLevel;
	private @SerializedName(PolicyViewConstant.IPCAN_QOS_PE_CAPABILITIY)boolean peCapability = false;
	private @SerializedName(PolicyViewConstant.IPCAN_QOS_PE_VULNERABILITY)boolean peVulnerability = true;
	private @SerializedName(PolicyViewConstant.QOS_UPGRADE) boolean qosUpgrade;
	private @SerializedName(PolicyViewConstant.MBRDL) long mbrdlInBytes;
	private @SerializedName(PolicyViewConstant.MBRUL) long mbrulInBytes;
	private @SerializedName(PolicyViewConstant.GBRDL) long gbrdlInBytes;
	private @SerializedName(PolicyViewConstant.GBRUL) long gbrulInBytes;

	//Used for policy -view
	private long mbrdl;
	private long mbrul;
	private long gbrdl;
	private long gbrul;
	private String mbrdlUnit;
	private String mbrulUnit;
	private String gbrdlUnit;
	private String gbrulUnit;
	private long aambrdl;
	private long aambrul;
	private String aambrdlUnit;
	private String aambrulUnit;


	private IPCANQoS() {
		qosUpgrade =  true;
	}



	public IPCANQoS(long aambrdl, long aambrul, QCI qci, PriorityLevel priorityLevel,
					boolean peCapability, boolean peVulnerability,boolean qosUpgrade) {
		this.aambrdlInBytes = aambrdl;
		this.aambrulInBytes = aambrul;
		this.qci = qci;
		this.priorityLevel = priorityLevel;
		this.peCapability = peCapability;
		this.peVulnerability = peVulnerability;
		this.qosUpgrade = qosUpgrade;
	}

	public IPCANQoS(long aambrdlInBytes, long aambrulInBytes, QCI qci, PriorityLevel priorityLevel, boolean peCapability, boolean peVulnerability
			, long mbrdlInBytes, long mbrulInBytes
			, long mbrdl, String mbrdlUnit
			, long mbrul, String mbrulUnit
			, long aambrdl, String aambrdlUnit
			, long aambrul, String aambrulUnit) {
		this(aambrdlInBytes, aambrulInBytes, qci, priorityLevel,peCapability, peVulnerability, true);
		this.mbrdlInBytes = mbrdlInBytes;
		this.mbrulInBytes = mbrulInBytes;
		this.mbrdl = mbrdl;
		this.mbrdlUnit = mbrdlUnit;
		this.mbrul = mbrul;
		this.mbrulUnit = mbrulUnit;
		this.aambrdl = aambrdl;
		this.aambrdlUnit = aambrdlUnit;
		this.aambrul = aambrul;
		this.aambrulUnit = aambrulUnit;
	}

	public IPCANQoS(@Nonnull QCI qci,
					@Nonnull PriorityLevel priorityLevel,
					boolean peCapability,
					boolean peVulnerability
			, long mbrdl, @Nonnull QoSUnit mbrdlUnit
			, long mbrul, @Nonnull QoSUnit mbrulUnit
			, long aambrdl, @Nonnull QoSUnit aambrdlUnit
			, long aambrul, @Nonnull QoSUnit aambrulUnit) {
		this.aambrdlInBytes = aambrdlUnit.toBps(aambrdl);
		this.aambrulInBytes = aambrulUnit.toBps(aambrul);
		this.qci = qci;
		this.priorityLevel = priorityLevel;
		this.peCapability = peCapability;
		this.peVulnerability = peVulnerability;
		this.qosUpgrade = qosUpgrade;
		this.mbrdlInBytes = mbrdlUnit.toBps(mbrdl);
		this.mbrulInBytes = mbrulUnit.toBps(mbrul);
		this.mbrdl = mbrdl;
		this.mbrdlUnit = mbrdlUnit.name();
		this.mbrul = mbrul;
		this.mbrulUnit = mbrulUnit.name();
		this.aambrdl = aambrdl;
		this.aambrdlUnit = aambrdlUnit.name();
		this.aambrul = aambrul;
		this.aambrulUnit = aambrulUnit.name();
	}

	@Override
	public String toString() {
		final ToStringBuilder toStringBuilder = new ToStringBuilder(this, IP_CAN_QOS_TO_STRING_STYLE);
		toString(toStringBuilder);
		return  toStringBuilder.toString();
	}



	public void toString(ToStringBuilder toStringBuilder) {

		toStringBuilder.append("QCI", qci);
		toStringBuilder.append("Priority Level", priorityLevel);
		toStringBuilder.append("Pre-emption capability", peCapability);
		toStringBuilder.append("Pre-emption vulnerability", peVulnerability);


		if (aambrdlUnit != null) {
			toStringBuilder.append("AAMBRDL", aambrdl + " " + aambrdlUnit);
		}
		if (aambrulUnit != null) {
			toStringBuilder.append("AAMBRUL", aambrul + " " + aambrulUnit);
		}
		if (mbrdlUnit != null) {
			toStringBuilder.append("MBRDL", mbrdl + " " + mbrdlUnit);
		}
		if (mbrulUnit != null) {
			toStringBuilder.append("MBRUL", mbrul + " " + mbrulUnit);
		}


	}

	public static class IPCANQoSBuilder{

		private IPCANQoS ipcanQoS;

		public IPCANQoSBuilder() {
			ipcanQoS = new IPCANQoS();

		}

		public IPCANQoSBuilder withAAMBRDL(long aambrdl){
			ipcanQoS.aambrdlInBytes = aambrdl;
			return this;
		}

		public IPCANQoSBuilder withAAMBRUL(long aambrul){
			ipcanQoS.aambrulInBytes = aambrul;
			return this;
		}

		public IPCANQoSBuilder withMBRDL(long mbrdl){
			ipcanQoS.mbrdlInBytes = mbrdl;
			return this;
		}

		public IPCANQoSBuilder withMBRUL(long mbrul){
			ipcanQoS.mbrulInBytes = mbrul;
			return this;
		}

		public IPCANQoSBuilder withGBRDL(long gbrdl){
			ipcanQoS.gbrdlInBytes = gbrdl;
			return this;
		}

		public IPCANQoSBuilder withGBRUL(long gbrul){
			ipcanQoS.gbrulInBytes = gbrul;
			return this;
		}

		public IPCANQoSBuilder withPriorityLevel(PriorityLevel priorityLevel) {
			if(priorityLevel != null) {
				ipcanQoS.priorityLevel = priorityLevel;
			}
			return this;
		}


		public IPCANQoSBuilder withPriorityLevel(int priorityLevel){
			return withPriorityLevel(PriorityLevel.fromVal(priorityLevel));
		}

		public IPCANQoSBuilder withAAMBRDL(String aambrdl){
			if(aambrdl != null) {
				ipcanQoS.aambrdlInBytes = Numbers.parseLong(aambrdl, ipcanQoS.aambrdlInBytes);
			}
			return this;
		}

		public IPCANQoSBuilder withAAMBRUL(String aambrul){
			if(aambrul != null) {
				ipcanQoS.aambrulInBytes = Numbers.parseLong(aambrul, ipcanQoS.aambrulInBytes);
			}
			return this;
		}

		public IPCANQoSBuilder withMBRDL(String mbrdl){
			if(mbrdl != null) {
				ipcanQoS.mbrdlInBytes = Numbers.parseLong(mbrdl, ipcanQoS.mbrdlInBytes);
			}
			return this;
		}

		public IPCANQoSBuilder withMBRUL(String mbrul){
			if(mbrul != null) {
				ipcanQoS.mbrulInBytes = Numbers.parseLong(mbrul, ipcanQoS.mbrulInBytes);
			}
			return this;
		}

		public IPCANQoSBuilder withGBRDL(String gbrdl){
			if(gbrdl != null) {
				ipcanQoS.gbrdlInBytes = Numbers.parseLong(gbrdl, ipcanQoS.gbrdlInBytes);
			}
			return this;
		}

		public IPCANQoSBuilder withGBRUL(String gbrul){
			if(gbrul !=  null) {
				ipcanQoS.gbrulInBytes = Numbers.parseLong(gbrul, ipcanQoS.gbrulInBytes);
			}
			return this;
		}


		public IPCANQoSBuilder withPriorityLevel(String priorityLevel){
			if(priorityLevel != null) {
				withPriorityLevel(Numbers.parseInt(priorityLevel, 0));
			}
			return this;
		}

		public IPCANQoSBuilder withQCI(String qci){
			if(qci != null) {
				withQCI(Numbers.parseInt(qci, 0));
			}
			return this;
		}

		public IPCANQoSBuilder withQCI(int qciVal){
			return withQCI(QCI.fromId(qciVal));
		}

		public IPCANQoSBuilder withQCI(QCI qci) {
			if(qci != null) {
				ipcanQoS.qci =  qci;
			}
			return this;
		}

		public IPCANQoSBuilder withPreEmptionCapability(String preEmptionCapability){
			if(preEmptionCapability != null) {
				ipcanQoS.peCapability =  preEmptionCapability.equals(PCRFKeyValueConstants.REQ_PREEMPTION_CAPABILITY_ENABLE.val) ? true : false;
			}
			return this;
		}

		public IPCANQoSBuilder withPreEmptionCapability(boolean preEmptionCapability){
			ipcanQoS.peCapability =  preEmptionCapability;
			return this;
		}

		public IPCANQoSBuilder withPreEmptionValnerability(String preEmptionValnerability){
			if(preEmptionValnerability != null) {
				ipcanQoS.peVulnerability = preEmptionValnerability.equals(PCRFKeyValueConstants.REQ_PREEMPTION_VULNERABILITY_ENABLE.val) ? true : false;
			}
			return this;
		}

		public IPCANQoSBuilder withPreEmptionValnerability(boolean preEmptionValnerability){
			ipcanQoS.peVulnerability = preEmptionValnerability;
			return this;
		}

		public IPCANQoSBuilder withQoSUpgrade(boolean qosUpgrade){
			ipcanQoS.qosUpgrade =  qosUpgrade;
			return this;
		}

		public IPCANQoSBuilder withJson(String jsonString){
			try{
				Gson gson = GsonFactory.defaultInstance();
				IPCANQoS ipcanQoS = gson.fromJson(jsonString, IPCANQoS.class);
				if(ipcanQoS != null) {
					this.ipcanQoS = ipcanQoS;
				}
			}catch(Exception ex){
				LogManager.getLogger().error(MODULE, "Error while creating IPCAN QoS from json string : " + jsonString + " from session. Reason: " + ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			}

			return this;

		}

		public @Nullable IPCANQoS build(){
			if(ipcanQoS.aambrdlInBytes > 0 || ipcanQoS.aambrulInBytes > 0 || ipcanQoS.mbrdlInBytes > 0 || ipcanQoS.mbrulInBytes > 0){
				return ipcanQoS;
			}
			return null;
		}




	}



	public long getAAMBRDInByt() {
		return aambrdl;
	}

	public long getAAMBRULInByt() {
		return aambrul;
	}

	public PriorityLevel getPriorityLevel() {
		return priorityLevel;
	}

	public void setPriorityLevel(PriorityLevel priorityLevel) {
		this.priorityLevel = priorityLevel;
	}

	public long getAAMBRDLInBytes() {
		return aambrdlInBytes;
	}

	public long getAAMBRULInBytes() {
		return aambrulInBytes;
	}

	public String getAambrdlUnit() {
		return aambrdlUnit;
	}

	public String getAambrulUnit() {
		return aambrulUnit;
	}

	public QCI getQCI() {
		return qci;
	}

	public boolean getPreEmptionCapability() {
		return peCapability;
	}

	public boolean getPreEmptionVulnerability() {
		return peVulnerability;
	}

	public long getMBRDL() {
		return mbrdl;
	}

	public long getMBRUL() {
		return mbrul;
	}

	public long getGbrdlInBytes() {
		return gbrdlInBytes;
	}

	public long getGbrulInBytes() {
		return gbrulInBytes;
	}

	public long getMBRDLInBytes() {
		return mbrdlInBytes;
	}

	public long getMBRULInBytes() {
		return mbrulInBytes;
	}

	public long getGbrdl() {
		return gbrdl;
	}

	public long getGbrul() {
		return gbrul;
	}

	public String getMbrdlUnit() {
		return mbrdlUnit;
	}

	public String getMbrulUnit() {
		return mbrulUnit;
	}

	public String getGbrdlUnit() {
		return gbrdlUnit;
	}

	public String getGbrulUnit() {
		return gbrulUnit;
	}

	public boolean isQosUpgrade() {
		return qosUpgrade;
	}

	static class IPCanQoSToStringStyle extends ToStringStyle.CustomToStringStyle {

		private static final long serialVersionUID = 1L;

		IPCanQoSToStringStyle() {
			super();
			this.setContentStart(SystemUtils.LINE_SEPARATOR);
			this.setFieldSeparator(SystemUtils.LINE_SEPARATOR + getSpaces(6)
					+ getTabs(2));
		}
	}

}
