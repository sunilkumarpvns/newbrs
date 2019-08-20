package com.elitecore.nvsmx.policydesigner.model.pkg.quota;

import java.io.Serializable;
import java.util.List;

import com.elitecore.corenetvertex.constants.AggregationKey;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.pkg.quota.QuotaProfileDetailData;
import com.elitecore.nvsmx.system.constants.NVSMXCommonConstants;

public class QuotaProfileWrapper implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	

	private String id;
	private String name;
	private String total;
	private String daily;
	private String weekly;
	private String custom;
	private String fupLevel;

	public QuotaProfileWrapper(String id, String name) {
		this.id = id;
		this.name = name;
	}

	public static class QuotaProfileWrapperBuilder {
		private QuotaProfileWrapper quotaProfileWrapper;

		public QuotaProfileWrapperBuilder(String id, String name) {
			quotaProfileWrapper = new QuotaProfileWrapper(id, name);

		}

		public QuotaProfileWrapper build() {
			return quotaProfileWrapper;
		}

		public QuotaProfileWrapperBuilder withQuotaProfileDetails(
				List<QuotaProfileDetailData> quotaProfileDetailDatas, int fupLevel) {

			setTotalData(quotaProfileDetailDatas, fupLevel, CommonConstants.ALL_SERVICE_ID);
			return this;
		}

		private void setTotalData(List<QuotaProfileDetailData> quotaProfileDetailDatas,
				int fupLevel, String serviceType) {
			for (QuotaProfileDetailData quotaProfileDetail : quotaProfileDetailDatas) {
				if (fupLevel != quotaProfileDetail.getFupLevel()) {
					continue;
				}
				if (serviceType.equalsIgnoreCase(quotaProfileDetail.getDataServiceTypeData().getId()) == false) {
					continue;
				}
				
				quotaProfileWrapper.fupLevel = String.valueOf(quotaProfileDetail.getFupLevel());
				 
				if (AggregationKey.BILLING_CYCLE.toString().equalsIgnoreCase(quotaProfileDetail.getAggregationKey())) {
					quotaProfileWrapper.total = getTotalQuota(quotaProfileDetail);
				} else if (AggregationKey.DAILY.toString().equalsIgnoreCase(quotaProfileDetail.getAggregationKey())) {
					quotaProfileWrapper.daily = getTotalQuota(quotaProfileDetail);
				} else if (AggregationKey.WEEKLY.toString().equalsIgnoreCase(quotaProfileDetail.getAggregationKey())) {
					quotaProfileWrapper.weekly = getTotalQuota(quotaProfileDetail);
				} else if (AggregationKey.CUSTOM.toString().equalsIgnoreCase(quotaProfileDetail.getAggregationKey())) {
					quotaProfileWrapper.custom = getTotalQuota(quotaProfileDetail);
				}
			}

		}

		private String getTotalQuota(QuotaProfileDetailData quotaProfileDetail) {
			StringBuilder totalQuota = new StringBuilder();

			if(quotaProfileDetail.getTotal() == null && quotaProfileDetail.getUpload() == null && quotaProfileDetail.getDownload() == null){
				totalQuota.append(NVSMXCommonConstants.UNLIMITED_QUOTA_STRING);
			}else{
				String total = quotaProfileDetail.getTotal() == null ?"" : String.valueOf(quotaProfileDetail.getTotal()) + " " + quotaProfileDetail.getTotalUnit()+ NVSMXCommonConstants.TOTAL;
				String upload = quotaProfileDetail.getUpload() == null ? "": String.valueOf(quotaProfileDetail.getUpload()) + " "+ quotaProfileDetail.getUploadUnit()+ NVSMXCommonConstants.UPLOAD_STRING;
				String download = quotaProfileDetail.getDownload() == null ? "": String.valueOf(quotaProfileDetail.getDownload()) + " "+ quotaProfileDetail.getDownloadUnit()+ NVSMXCommonConstants.DOWNLOAD_STRING;
				totalQuota.append(total).append(upload).append(download);
			}
			return totalQuota.toString();
		}
	}

	public String getId() {
		return id;
	}

	public String getName() {
		return name;
	}

	public String getTotal() {
		return total;
	}

	public String getDaily() {
		return daily;
	}

	public String getWeekly() {
		return weekly;
	}

	public String getCustom() {
		return custom;
	}

	public String getFupLevel() {
		return fupLevel;
	}

	@Override
	public String toString() {
		return String
				.format("QuotaProfileWrapper [id=%s, name=%s, total=%s, daily=%s, weekly=%s, custom=%s ,fuplevel = %s]",
						id, name, total, daily, weekly, custom ,fupLevel);
	}

}
