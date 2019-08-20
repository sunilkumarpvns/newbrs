package com.elitecore.elitesm.web.servicepolicy.radiusservicepolicy;

import java.util.List;

public class CDRGenerationHandler {
	
	private Long cdrGenHandlerId;
	private List<CDRGeneration> cdrGenerationMappingList;
	
	public Long getCdrGenHandlerId() {
		return cdrGenHandlerId;
	}
	public void setCdrGenHandlerId(Long cdrGenHandlerId) {
		this.cdrGenHandlerId = cdrGenHandlerId;
	}
	public List<CDRGeneration> getCdrGenerationMappingList() {
		return cdrGenerationMappingList;
	}
	public void setCdrGenerationMappingList(
			List<CDRGeneration> cdrGenerationMappingList) {
		this.cdrGenerationMappingList = cdrGenerationMappingList;
	}
}
