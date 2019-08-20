package com.elitecore.aaa.radius.conf.impl;

import java.util.List;

public interface ProfileDriverDetails {

	public String getDriverScript();

	public List<PrimaryDriverDetail> getPrimaryDriverGroup();

	public List<SecondaryAndCacheDriverDetail> getSecondaryDriverGroup();

	public List<AdditionalDriverDetail> getAdditionalDriverList();

}
