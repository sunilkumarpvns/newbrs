package com.elitecore.aaa.radius.conf;

import java.util.List;

import com.elitecore.aaa.core.data.AttributeDetails;

public interface RadBWListConfiguration {
	public List<AttributeDetails> getAttributeDetailList();
	public boolean isInitialized();
}
