package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;

import com.elitecore.corenetvertex.constants.CommonConstants;

import java.io.Serializable;


/**
 *  
 * @author Chetan.Sankhala
 */
public interface Balance extends Serializable{
	long total();
	long upload();
	long download();
	long time();
	boolean isExist();
	void subtract(Balance balance);

	default boolean isTotalBalanceLessThan(long totalBalance) {
		return totalBalance > total();
	}

	default boolean isDownloadBalanceLessThan(long downloadBalance) {
		return downloadBalance > download();
	}

	default boolean isUploadBalanceLessThan(long uploadBalance) {
		return uploadBalance > upload();
	}

	default boolean isTimeBalanceLessThan(long timeBalance) {
		return timeBalance > time();
	}

	default boolean isTotalBalanceLessThanEqualTo(long totalBalance) {
		return totalBalance >= total();
	}

	default boolean isDownloadBalanceLessThanEqualTo(long downloadBalance) {
		return downloadBalance >= download();
	}

	default boolean isUploadBalanceLessThanEqualTo(long uploadBalance) {
		return uploadBalance >= upload();
	}

	default boolean isTimeBalanceLessThanEqualTo(long timeBalance) {
		return timeBalance >= time();
	}
	
	Balance ZERO = new Balance() {

		@Override
		public long total() {
			return 0;
		}

		@Override
		public long upload() {
			return 0;
		}

		@Override
		public long download() {
			return 0;
		}

		@Override
		public long time() {
			return 0;
		}

		@Override
		public boolean isExist() {
			return false;
		}

		@Override
		public void subtract(Balance balance) {
			throw new UnsupportedOperationException("Cannot subtract from zero balance");
		}
	};
	
	Balance UNLIMITED = new Balance() {
		
		@Override
		public long upload() {
			return CommonConstants.QUOTA_UNLIMITED;
		}
		
		@Override
		public long total() {
			return CommonConstants.QUOTA_UNLIMITED;
		}
		
		@Override
		public long time() {
			return CommonConstants.QUOTA_UNLIMITED;
		}
		
		@Override
		public long download() {
			return CommonConstants.QUOTA_UNLIMITED;
		}

		@Override
		public boolean isExist() {
			return true;
		}

		@Override
        public boolean isTotalBalanceLessThan(long totalBalance) {
			return false;
		}

		@Override
        public boolean isDownloadBalanceLessThan(long downloadBalance) {
			return false;
		}

		@Override
        public boolean isUploadBalanceLessThan(long uploadBalance) {
			return false;
		}

		@Override
        public boolean isTimeBalanceLessThan(long timeBalance) {
			return false;
		}

		@Override
		public boolean isTotalBalanceLessThanEqualTo(long totalBalance) {
			return false;
		}

		@Override
		public boolean isDownloadBalanceLessThanEqualTo(long downloadBalance) {
			return false;
		}

		@Override
		public boolean isUploadBalanceLessThanEqualTo(long uploadBalance) {
			return false;
		}

		@Override
		public boolean isTimeBalanceLessThanEqualTo(long timeBalance) {
			return false;
		}
		
		public String toString() {
			return "UNLIMITED";
		}

		@Override
		public void subtract(Balance balance) {
			// Subtracting from unlimited balance has no effect.
		}
	}; 
}
