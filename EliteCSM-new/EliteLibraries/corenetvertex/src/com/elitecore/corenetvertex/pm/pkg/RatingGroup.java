package com.elitecore.corenetvertex.pm.pkg;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;

public class RatingGroup implements Serializable{

	private static final long serialVersionUID = 1L;
	private String ratingGroupId;
	private String name;
	private String description;
	private long identifier;
	
	public RatingGroup(String ratingGroupId, String name, String description, long identifier) {
		this.ratingGroupId = ratingGroupId;
		this.name = name;
		this.description = description;
		this.identifier = identifier;
	}

	public String getRatingGroupId() {
		return ratingGroupId;
	}

	public String getName() {
		return name;
	}

	public String getDescription() {
		return description;
	}

	public long getIdentifier() {
		return identifier;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.print(this.name);
		out.print(" (");
		out.print(this.identifier);
		out.print(") ");
		out.close();
		return stringBuffer.toString();
	}
}
