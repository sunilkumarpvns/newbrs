package com.elitecore.elitesm.web.servicepolicy.tgpp.data;

import java.util.List;

public interface ProfileDriverDetails {

	public String getDriverScript();

	public List<PrimaryDriverDetail> getPrimaryDriverGroup();

	public List<SecondaryAndCacheDriverDetail> getSecondaryDriverGroup();

	public List<AdditionalDriverDetail> getAdditionalDriverList();

}
