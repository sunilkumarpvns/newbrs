package com.elitecore.netvertex.cli.data;

import java.util.List;

import com.elitecore.core.servicex.ServiceDescription;

public interface ServiceDataProvider {
	
	public List<ServiceDescription> getServiceDescriptionList();
	
}
