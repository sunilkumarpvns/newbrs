package com.elitecore.netvertexsm.web.servermgr.spr.form;

import java.util.List;

import org.apache.struts.action.ActionForm;

import com.elitecore.corenetvertex.spr.data.SubscriberRepositoryData;
import com.elitecore.corenetvertex.spr.ddf.DDFEntryData;

public class DDFTableDataForm extends ActionForm {

	private static final long serialVersionUID = 1L;

	private String id;
	private String defaultSprId;
	private SubscriberRepositoryData defaultSubscriberRepository;
	private String stripPrefixes;
	private List<DDFEntryData> ddfEntries;
	
	private List<SubscriberRepositoryData> subscriberRepositoryDatas;
	
	
	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
	}
	public String getDefaultSprId() {
		return defaultSprId;
	}
	public void setDefaultSprId(String defaultSprId) {
		this.defaultSprId = defaultSprId;
	}
	public String getStripPrefixes() {
		return stripPrefixes;
	}
	public void setStripPrefixes(String stripPrefixes) {
		this.stripPrefixes = stripPrefixes;
	}
	public List<DDFEntryData> getDdfEntries() {
		return ddfEntries;
	}
	public void setDdfEntries(List<DDFEntryData> ddfEntries) {
		this.ddfEntries = ddfEntries;
	}
	public List<SubscriberRepositoryData> getSubscriberRepositoryDatas() {
		return subscriberRepositoryDatas;
	}
	public void setSubscriberRepositoryDatas(List<SubscriberRepositoryData> subscriberRepositoryDatas) {
		this.subscriberRepositoryDatas = subscriberRepositoryDatas;
	}
	public SubscriberRepositoryData getDefaultSubscriberRepository() {
		return defaultSubscriberRepository;
	}
	public void setDefaultSubscriberRepository(SubscriberRepositoryData defaultSubscriberRepository) {
		this.defaultSubscriberRepository = defaultSubscriberRepository;
	}

}
