package com.elitecore.netvertex.service.offlinernc.ratecard;

import java.math.BigDecimal;

import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;

public class RatingRequest {

	private RnCRequest rncRequest;
	
	private int previousAccountedUsage;
	private BigDecimal slabOneCost;
	private BigDecimal slabOnePulse;
	private BigDecimal slabOneRatePerPulse;
	private BigDecimal chargePerUom;
	private boolean ratingCompleted;


	public RatingRequest(RnCRequest rncRequest) {
		this.rncRequest = rncRequest;
	}

	public RnCRequest getRncRequest() {
		return rncRequest;
	}

	public void setRncRequest(RnCRequest rncRequest) {
		this.rncRequest = rncRequest;
	}

	public int getPreviousAccountedUsage() {
		return previousAccountedUsage;
	}

	public void setPreviousAccountedUsage(int accountedUsage) {
		this.previousAccountedUsage = accountedUsage;
	}

	public BigDecimal getSlabOneCost() {
		return slabOneCost;
	}

	public void setSlabOneCost(BigDecimal slabOneCost) {
		this.slabOneCost = slabOneCost;
	}

	public BigDecimal getSlabOnePulse() {
		return slabOnePulse;
	}

	public void setSlabOnePulse(BigDecimal slabOnePulse) {
		this.slabOnePulse = slabOnePulse;
	}

	public BigDecimal getSlabOneRatePerPulse() {
		return slabOneRatePerPulse;
	}

	public void setSlabOneRatePerPulse(BigDecimal slabOneRatePerPulse) {
		this.slabOneRatePerPulse = slabOneRatePerPulse;
	}

	public void setRatingCompleted(boolean ratingCompleted) {
		this.ratingCompleted = ratingCompleted;
	}

	public boolean isRatingCompleted() {
		return ratingCompleted;
	}
	/**
	 * Only resets various counters.
	 * Doesn't removes RnCRequest.
	 * 
	 */
	public void reset() {
		previousAccountedUsage = 0;
		slabOneCost = null;
		slabOnePulse = null;
		slabOneRatePerPulse = null;
		ratingCompleted = false;
	}

	public void setChargePerUom(BigDecimal chargePerUom) {
		this.chargePerUom = chargePerUom;
	}
	
	public BigDecimal getChargePerUom() {
		return chargePerUom;
	}
}
