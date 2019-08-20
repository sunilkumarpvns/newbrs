package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.CommonConstants;

/**
 * 
 * @author Chetan.Sankhala
 */
public class BalanceImpl implements Balance {
	
	private long total;
	private long upload;
	private long download;
	private long time;
	private boolean isTotalUndefined;
	private boolean isUploadUndefined;
	private boolean isDownloadUndefined;
	private boolean isTimeUndefined;

	public BalanceImpl(long total, long download, long upload, long time) {
		this.total = total;
		this.download = download;
		this.upload = upload;
		this.time = time;
		this.isTotalUndefined = total == CommonConstants.QUOTA_UNDEFINED ? true : false;
		this.isDownloadUndefined = download == CommonConstants.QUOTA_UNDEFINED ? true : false;
		this.isUploadUndefined = upload == CommonConstants.QUOTA_UNDEFINED ? true : false;
		this.isTimeUndefined = time == CommonConstants.QUOTA_UNDEFINED ? true : false;
	}
	
	@Override
	public long total() {
		return total;
	}

	@Override
	public long upload() {
		return upload;
	}

	@Override
	public long download() {
		return download;
	}

	@Override
	public long time() {
		return time;
	}
	
	public void setTotal(long total) {
		this.total = total;
	}
	
	public void setDownload(long download) {
		this.download = download;
	}

	public void setUpload(long upload) {
		this.upload = upload;
	}

	public void setTime(long time) {
		this.time = time;
	}

	@Override
	public boolean isExist() {
		return isTotalBalanceExist()
				&& isDownloadBalanceExist()
				&& isUploadBalanceExist()
				&& isTimeBalanceExist();
	}

	private boolean isTimeBalanceExist() {
		return isTimeUndefined ? true : time > 0;
	}

	private boolean isUploadBalanceExist() {
		return isUploadUndefined ? true : upload > 0;
	}

	private boolean isDownloadBalanceExist() {
		return isDownloadUndefined ? true : download > 0;
	}

	private boolean isTotalBalanceExist() {
		return isTotalUndefined ? true : total > 0;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o) {
		    return true;
        }

		if (o == null || getClass() != o.getClass()) {
		    return false;
        }

		BalanceImpl balance = (BalanceImpl) o;

		if (total != balance.total) {
		    return false;
        }
		if (upload != balance.upload) {
		    return false;
        }
		if (download != balance.download) {
		    return false;
        }

		return time == balance.time;
	}

	@Override
	public int hashCode() {
		int result = (int) (total ^ (total >>> 32));
		result = 31 * result + (int) (upload ^ (upload >>> 32));
		result = 31 * result + (int) (download ^ (download >>> 32));
		result = 31 * result + (int) (time ^ (time >>> 32));
		return result;
	}

	@Override
	public String toString() {
		return "[total=" + total + ", upload=" + upload + ", download=" + download + ", time=" + time + "]";
	}

	@Override
	public void subtract(Balance balance) {
		this.total -= balance.total();
		this.download -= balance.download();
		this.upload -= balance.upload();
		this.time -= balance.time();
	}
}