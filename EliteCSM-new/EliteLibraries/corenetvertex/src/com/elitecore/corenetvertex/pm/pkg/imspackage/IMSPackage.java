package com.elitecore.corenetvertex.pm.pkg.imspackage;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.Maps;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.util.ToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringStyle;

public class IMSPackage implements Serializable{

	private static final long serialVersionUID = 1L;
	private final String id;
	private final String name;
	private final Map<Long, List<IMSServiceTable>> serviceIdentfierToServiceTables;

	private PolicyStatus policyStatus;
	private String failedReson;
	private PkgMode packageMode;
	private String partialFailReason;
	private PkgStatus availabilityStatus;
	private List<String> groupIds;
	private Double price;
	
	public IMSPackage(String id, String name, Map<Long, List<IMSServiceTable>> serviceIdentifierToServiceTables,
                      PkgMode packageMode, PkgStatus availabilityStatus, Double price, List<String> groupIds) {
		this.id = id;
		this.name = name;
		this.serviceIdentfierToServiceTables = serviceIdentifierToServiceTables;
		this.packageMode = packageMode;
		this.policyStatus = PolicyStatus.SUCCESS;
		this.failedReson = null;
		this.availabilityStatus = availabilityStatus;
		this.price = price;
		this.groupIds = groupIds;
	}

	public IMSPackage(String id, String name, PkgMode packageMode, PkgStatus availabilityStatus
			, PolicyStatus policyStatus, String failReason, String partialFailReason, Double price, List<String> groupIds) {
		this.id = id;
		this.name = name;
		this.packageMode = packageMode;
		this.partialFailReason = partialFailReason;
		this.groupIds = groupIds;
		this.serviceIdentfierToServiceTables = null;
		this.policyStatus = policyStatus;
		this.failedReson = failReason;
		this.availabilityStatus = availabilityStatus;
		this.price = price;
	}

	public String getName() {
		return name;
	}

	public PolicyStatus getStatus() {
		return policyStatus;
	}

	public String getFailReason() {
		return failedReson;
	}

	public String getId() {
		return id;
	}

	public Map<Long, List<IMSServiceTable>> getServiceIdentfierToServiceTables() {
		return serviceIdentfierToServiceTables;
	}

	public PkgMode getMode() {
		return packageMode;
	}

	public String getPackageMode() {
		return packageMode.name();
	}
	
	public String getPartialFailReason() {
		return partialFailReason;
	}

	public void setPolicyStatus(PolicyStatus status) {
		this.policyStatus = status;
		
	}
	
	public void setFailedReson(String failedReson) {
		this.failedReson = failedReson;
	}

	public void setPartialFailReason(String partialFailReason) {
		this.partialFailReason = partialFailReason;
	}

	public PkgStatus getAvailabilityStatus() {
		return availabilityStatus;
	}

	public List<String> getGroupIds() {
		return groupIds;
	}

	@Override
	public String toString() {

		ToStringBuilder toStringBuilder = new ToStringBuilder(this,
				ToStringStyle.CUSTOM_TO_STRING_STYLE).append("Name", name)
				.append("Type", PkgType.BASE.name())
				.append("Package Mode", packageMode)
				.append("Price", price == null ? "N/A" : price);


		toStringBuilder.append("\t");
		if (Maps.isNullOrEmpty(serviceIdentfierToServiceTables) == false) {
			for (List<IMSServiceTable> imsPkgServiceTables : serviceIdentfierToServiceTables.values()) {
				for (IMSServiceTable imsPkgServiceTable : imsPkgServiceTables) {
					toStringBuilder.append("Service", imsPkgServiceTable);
				}
			}
		}

		toStringBuilder.append("Policy Status", policyStatus);
		if (failedReson != null) {
			toStringBuilder.append("Fail Reasons", failedReson);
		}
		if (partialFailReason != null) {
			toStringBuilder.append("Partial Fail Reasons", partialFailReason);
		}

		return toStringBuilder.toString();

	}
	
}
