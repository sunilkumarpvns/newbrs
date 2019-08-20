package com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile;


import javax.annotation.Nonnull;

/**
 * 
 * @author Chetan.Sankhala
 */
public class TotalBalance implements Balance {

	@Nonnull private Balance dailyBalance;
	@Nonnull private Balance weeklyBalance;
	@Nonnull private Balance customBalance;
	@Nonnull private Balance billingCycleBalance;
	
	public TotalBalance(@Nonnull Balance dailyBalance,
						@Nonnull Balance weeklyBalance,
						@Nonnull Balance customBalance,
						@Nonnull Balance billingCycleBalance) {
		this.dailyBalance = dailyBalance;
		this.weeklyBalance = weeklyBalance;
		this.customBalance = customBalance;
		this.billingCycleBalance = billingCycleBalance;
	}
	
	public @Nonnull Balance getDailyBalance() {
		return dailyBalance;
	}
	public @Nonnull Balance getWeeklyBalance() {
		return weeklyBalance;
	}
	public @Nonnull Balance getCustomBalance() {
		return customBalance;
	}
	public @Nonnull Balance getBillingCycleBalance() {
		return billingCycleBalance;
	}

	@Override
	public long total() {
		long totalBalance = billingCycleBalance.total();

		if (weeklyBalance.total() < totalBalance) {
			totalBalance = weeklyBalance.total();
		}

		if (dailyBalance.total() < totalBalance) {
			totalBalance = dailyBalance.total();
		}

		if (customBalance.total() < totalBalance) {
			totalBalance = customBalance.total();
		}

		return totalBalance;
	}

	@Override
	public long download() {
		long downloadBalance = billingCycleBalance.download();

		if (weeklyBalance.download() < downloadBalance) {
			downloadBalance = weeklyBalance.download();
		}

		if (dailyBalance.download() < downloadBalance) {
			downloadBalance = dailyBalance.download();
		}

		if (customBalance.download() < downloadBalance) {
			downloadBalance = customBalance.download();
		}

		return downloadBalance;
	}

	@Override
	public long upload() {
		long uploadBalance = billingCycleBalance.upload();

		if (weeklyBalance.upload() < uploadBalance) {
			uploadBalance = weeklyBalance.upload();
		}

		if (dailyBalance.upload() < uploadBalance) {
			uploadBalance = dailyBalance.upload();
		}

		if (customBalance.upload() < uploadBalance) {
			uploadBalance = customBalance.upload();
		}

		return uploadBalance;
	}

	@Override
	public long time() {
		long timeBalance = billingCycleBalance.time();

		if (weeklyBalance.time() < timeBalance) {
			timeBalance = weeklyBalance.time();
		}

		if (dailyBalance.time() < timeBalance) {
			timeBalance = dailyBalance.time();
		}

		if (customBalance.time() < timeBalance) {
			timeBalance = customBalance.time();
		}

		return timeBalance;
	}

	@Override
	public boolean isExist() {
		return billingCycleBalance.isExist() && dailyBalance.isExist() && weeklyBalance.isExist() && customBalance.isExist();
	}

	@Override
	public String toString() {
		return "TotalBalance [dailyBalance=" + dailyBalance + ", weeklyBalance=" + weeklyBalance + ", customBalance=" + customBalance
				+ ", billingCycleBalance=" + billingCycleBalance + "]";
	}

	@Override
	public void subtract(Balance balance) {
		dailyBalance.subtract(balance);
		weeklyBalance.subtract(balance);
		customBalance.subtract(balance);
		billingCycleBalance.subtract(balance);
	}
}
