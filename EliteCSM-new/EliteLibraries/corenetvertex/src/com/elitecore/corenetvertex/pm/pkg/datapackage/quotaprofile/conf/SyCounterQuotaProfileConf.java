package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.conf;

import java.util.ArrayList;
import java.util.List;

public class SyCounterQuotaProfileConf {

	private String quotaProfileId;
	private String quotaProfileName;
	private String serviceId;
	private int fupLevel;
	private List<CouterDetail> syCounters;
	private String packageName;
	private String serviceName;

	public SyCounterQuotaProfileConf(String quotaProfileId,
			String quotaProfileName,
			String packageName,
			String serviceId,
			String serviceName,
			int fupLevel) {
		this.quotaProfileId = quotaProfileId;
		this.quotaProfileName = quotaProfileName;
		this.serviceId = serviceId;
		this.serviceName = serviceName;
		this.fupLevel = fupLevel;
		this.packageName = packageName;
		this.syCounters = new ArrayList<CouterDetail>();
	}

	public SyCounterQuotaProfileConf addCounter(String key, String hsqCounter, boolean isOptional) {
		syCounters.add(new CouterDetail(key, hsqCounter, isOptional));
		
		return this;
	}
	
	
	
	public String getQuotaProfileId() {
		return quotaProfileId;
	}

	public String getQuotaProfileName() {
		return quotaProfileName;
	}

	public String getServiceId() {
		return serviceId;
	}

	public int getFupLevel() {
		return fupLevel;
	}

	public List<CouterDetail> getSyCounters() {
		return syCounters;
	}

	public String getPackageName() {
		return packageName;
	}

	public String getServiceName() {
		return serviceName;
	}

	public static class CouterDetail {
		private String key;
		private String value;
		private boolean isOptional;
		
		
		
		public CouterDetail(String key, String value, boolean isOptional) {
			super();
			this.key = key;
			this.value = value;
			this.isOptional = isOptional;
		}
		public String getKey() {
			return key;
		}
		public String getValue() {
			return value;
		}
		public boolean isOptional() {
			return isOptional;
		}
		
		
	}

	
}
