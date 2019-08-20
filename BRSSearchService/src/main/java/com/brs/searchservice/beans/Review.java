package com.brs.searchservice.beans;

import java.math.BigDecimal;


public class Review {
	private Ratings ratings = null;

	private BigDecimal reviewCount = null;
	private BigDecimal posReviewCount = null;

	private BigDecimal criReviewCount = null;

	private BigDecimal imgReviewCount = null;

	private BigDecimal totalRating = null;

	private BigDecimal approvedImageCount = null;
	private String id = null;
	public Ratings getRatings() {
		return ratings;
	}
	public void setRatings(Ratings ratings) {
		this.ratings = ratings;
	}
	public BigDecimal getReviewCount() {
		return reviewCount;
	}
	public void setReviewCount(BigDecimal reviewCount) {
		this.reviewCount = reviewCount;
	}
	public BigDecimal getPosReviewCount() {
		return posReviewCount;
	}
	public void setPosReviewCount(BigDecimal posReviewCount) {
		this.posReviewCount = posReviewCount;
	}
	public BigDecimal getCriReviewCount() {
		return criReviewCount;
	}
	public void setCriReviewCount(BigDecimal criReviewCount) {
		this.criReviewCount = criReviewCount;
	}
	public BigDecimal getImgReviewCount() {
		return imgReviewCount;
	}
	public void setImgReviewCount(BigDecimal imgReviewCount) {
		this.imgReviewCount = imgReviewCount;
	}
	public BigDecimal getTotalRating() {
		return totalRating;
	}
	public void setTotalRating(BigDecimal totalRating) {
		this.totalRating = totalRating;
	}
	public BigDecimal getApprovedImageCount() {
		return approvedImageCount;
	}
	public void setApprovedImageCount(BigDecimal approvedImageCount) {
		this.approvedImageCount = approvedImageCount;
	}
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}

}
