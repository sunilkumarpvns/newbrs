package com.elitecore.corenetvertex.pm.pkg;

import java.io.PrintWriter;
import java.io.Serializable;
import java.io.StringWriter;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PolicyViewConstant;
import com.google.gson.annotations.SerializedName;

public class DataServiceType implements Serializable{

	private static final long serialVersionUID = 1L;
	private @SerializedName(PolicyViewConstant.DATA_SERVICE_TYPE_ID)String dataServiceTypeID;
	private @SerializedName(PolicyViewConstant.SERVICE_TYPE_NAME)String name;
	private long serviceIdentifier;
	private List<String> serviceDataFlowList;
	private List<RatingGroup> ratingGroupList;
	
	public DataServiceType(String dataServiceTypeID, String name, long serviceIdentifier,
						   List<String> serviceDataFlowList,
						   List<RatingGroup> ratingGroupList) {
		this.dataServiceTypeID = dataServiceTypeID;
		this.name = name;
		this.serviceIdentifier = serviceIdentifier;
		this.serviceDataFlowList = serviceDataFlowList;
		this.ratingGroupList = ratingGroupList;
	}
	
	public String getDataServiceTypeID() {
		return dataServiceTypeID;
	}

	public String getName() {
		return name;
	}

	public long getServiceIdentifier() {
		return serviceIdentifier;
	}

	public List<String> getServiceDataFlowList() {
		return serviceDataFlowList;
	}

	public List<RatingGroup> getRatingGroupList() {
		return ratingGroupList;
	}

	@Override
	public String toString() {

		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.print(name);
		out.print(" (");
		out.print(dataServiceTypeID);
		if(Collectionz.isNullOrEmpty(ratingGroupList) == false) {
			out.print(ratingGroupList);
		}
		out.print(") ");
		out.close();
		return stringBuffer.toString();
	}

}
