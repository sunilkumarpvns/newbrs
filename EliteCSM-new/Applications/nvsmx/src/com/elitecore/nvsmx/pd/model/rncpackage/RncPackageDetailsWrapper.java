package com.elitecore.nvsmx.pd.model.rncpackage;

import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;

import java.io.Serializable;


public class RncPackageDetailsWrapper implements Serializable {

	private static final long serialVersionUID = 7777561179851760439L;
	
	private String id;
	private String name;
	private String peakRateCard;
	private String offPeakRateCard;
	private String weekRateCard;
	private String specialRateCard;

	public RncPackageDetailsWrapper(String id) {
		this.id = id;
	}

	public static class RncPackageDetailWrapperBuilder {
		private RncPackageDetailsWrapper rncPackageDetailsWrapper;

		public RncPackageDetailWrapperBuilder(String id) {
			rncPackageDetailsWrapper = new RncPackageDetailsWrapper( id);
		}

		public RncPackageDetailsWrapper build() {
			return rncPackageDetailsWrapper;
		}

		public RncPackageDetailWrapperBuilder withRateCardGroupdetail(
				RateCardGroupData rateCardGroupData) {

		     rncPackageDetailsWrapper.name = rateCardGroupData.getName();
			 if(rateCardGroupData.getSpecialDayRateCard() != null){
				 rncPackageDetailsWrapper.specialRateCard = rateCardGroupData.getSpecialDayRateCard().getName();
			 }
			 
			 if(rateCardGroupData.getWeekendRateRateCard() != null){
				 rncPackageDetailsWrapper.weekRateCard = rateCardGroupData.getWeekendRateRateCard().getName();	 
			 }
			 if(rateCardGroupData.getOffPeakRateRateCard() != null){
				 rncPackageDetailsWrapper.offPeakRateCard = rateCardGroupData.getOffPeakRateRateCard().getName();
			 }
			
			if(rateCardGroupData.getPeakRateRateCard() != null){
				 rncPackageDetailsWrapper.peakRateCard = rateCardGroupData.getPeakRateRateCard().getName();
			 }
			return this;
		}

	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getPeakRateCard() {
		return peakRateCard;
	}

	public void setPeakRateCard(String peakRateCard) {
		this.peakRateCard = peakRateCard;
	}

	public String getOffPeakRateCard() {
		return offPeakRateCard;
	}

	public void setOffPeakRateCard(String offPeakRateCard) {
		this.offPeakRateCard = offPeakRateCard;
	}

	public String getWeekRateCard() {
		return weekRateCard;
	}

	public void setWeekRateCard(String weekRateCard) {
		this.weekRateCard = weekRateCard;
	}

	public String getSpecialRateCard() {
		return specialRateCard;
	}

	public void setSpecialRateCard(String specialRateCard) {
		this.specialRateCard = specialRateCard;
	}

}
