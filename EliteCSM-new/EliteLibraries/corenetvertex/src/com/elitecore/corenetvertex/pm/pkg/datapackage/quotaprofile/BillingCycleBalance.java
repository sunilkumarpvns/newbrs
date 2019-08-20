package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.CommonConstants;

public class BillingCycleBalance implements Balance {

    private long total;
    private long upload;
    private long download;
    private long time;
    private boolean isTotalUndefined;
    private boolean isUploadUndefined;
    private boolean isDownloadUndefined;
    private boolean isTimeUndefined;

    public BillingCycleBalance(long total, long download, long upload, long time) {
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

    @Override
    public boolean isExist() {
        return (isTotalUndefined ? true : total > 0)
                && (isDownloadUndefined ? true : download > 0)
                && (isUploadUndefined ? true : upload > 0)
                && (isTimeUndefined ? true : time > 0);
    }

    public boolean isTotalUndefined() {
        return isTotalUndefined;
    }

    public boolean isDownloadUndefined() {
        return isDownloadUndefined;
    }

    public boolean isUploadUndefined() {
        return isUploadUndefined;
    }

    public boolean isTimeUndefined() {
        return isTimeUndefined;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }

        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        BillingCycleBalance balance = (BillingCycleBalance) o;

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
	public void subtract(Balance balance) {
    	if (isTotalUndefined() == false) {
    		this.total -= balance.total();
    	}
    	if (isDownloadUndefined() == false) {
    		this.download -= balance.download();
    	}
    	if (isUploadUndefined() == false) {
    		this.upload -= balance.upload();
    	}
    	if (isTimeUndefined() == false) {
    		this.time -= balance.time();
    	}
	}
}
