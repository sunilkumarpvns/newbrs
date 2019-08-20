package com.elitecore.corenetvertex.pm.pkg.factory;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.corenetvertex.pkg.dataservicetype.DefaultServiceDataFlowData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;

public class DataServiceTypeFactory {

	private PackageFactory packageFactory;
	private RatingGroupFactory ratingGroupFactory;
	
	public DataServiceTypeFactory(RatingGroupFactory ratingGroupFactory, PackageFactory packageFactory) {
		this.ratingGroupFactory = ratingGroupFactory;
		this.packageFactory = packageFactory;
	}
	
	public DataServiceType createServiceType(DataServiceTypeData dataServiceTypeData) {

		return packageFactory.createServiceType(dataServiceTypeData.getId(), dataServiceTypeData.getName(),
				dataServiceTypeData.getServiceIdentifier(), createServiceDataFlow(dataServiceTypeData.getDefaultServiceDataFlows()),
				ratingGroupFactory.createRatingGroup(dataServiceTypeData.getRatingGroupDatas()));
	}

	public List<String> createServiceDataFlow(List<DefaultServiceDataFlowData> defaultServiceDataFlowsDatas) {
		List<String> serviceDataFlows = new ArrayList<>();
		for (DefaultServiceDataFlowData defaultServiceDataFlowData : defaultServiceDataFlowsDatas) {

			serviceDataFlows.add(defaultServiceDataFlowData.getFlowAccess()
					+ " "
					+ (defaultServiceDataFlowData.getProtocol() == null ? "ip" : defaultServiceDataFlowData.getProtocol()) + " from "
					+ (defaultServiceDataFlowData.getSourceIP() == null ? "any" : defaultServiceDataFlowData.getSourceIP())
					+ (defaultServiceDataFlowData.getSourcePort() == null ? "" : ":" + defaultServiceDataFlowData.getSourcePort())
					+ " to "
					+ (defaultServiceDataFlowData.getDestinationIP() == null ? "any" : defaultServiceDataFlowData.getDestinationIP())
					+ (defaultServiceDataFlowData.getDestinationPort() == null ? "" : ":" + defaultServiceDataFlowData.getDestinationPort())
					);

		}
		return serviceDataFlows;
	}
}
