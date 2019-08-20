package com.elitecore.netvertex.service.pcrf;

import com.elitecore.corenetvertex.util.IndentingToStringBuilder;
import com.elitecore.corenetvertex.util.ToStringable;

public class TACDetail implements ToStringable{

	private String tac;
	private String brand;
	private String model;
	private String hwType;
	private String os;
	private String year;
	private String additionalInfo;
	
	public TACDetail(String tac, String brand, String model, String hwType, String os, String year, String additionalInfo) {
		this.tac = tac;
		this.brand = brand;
		this.model = model;
		this.hwType = hwType;
		this.os = os;
		this.year = year;
		this.additionalInfo = additionalInfo;
	}

	/**
	 * @return Type Allocation Code of User Equipment
	 */
	public String getTac() {
		return tac;
	}

	/**
	 * @return Brand Name of User Equipment
	 */
	public String getBrand() {
		return brand;
	}

	/**
	 * @return Model of User Equipment
	 */
	public String getModel() {
		return model;
	}

	/**
	 * @return Hardware Type of User Equipment
	 */
	public String getHwType() {
		return hwType;
	}

	/**
	 * @return Operating System of User Equipment
	 */
	public String getOs() {
		return os;
	}

	/**
	 * @return Manufacture/Release Year of User Equipment
	 */
	public String getYear() {
		return year;
	}
	/**
	 * 
	 * @return additional information provided with the User Equipment
	 */
	public String getAdditionalInfo() {
		return additionalInfo;
	}

	@Override
	public String toString() {
		IndentingToStringBuilder builder = new IndentingToStringBuilder();
		builder.appendHeading(" -- TAC Detail -- ");
		toString(builder);
		return builder.toString();
	}

	@Override
	public void toString(IndentingToStringBuilder builder) {
		builder.newline()
				.append("TAC", tac)
				.append("Brand", brand)
				.append("Model", model)
				.append("Hardware Type", hwType)
				.append("OS", os)
				.append("Year", year)
				.append("Additional Information", additionalInfo);
	}
}
