package com.elitecore.elitesm.web.inmemorydatagrid.form;

import java.util.List;

import com.elitecore.elitesm.datamanager.servermgr.data.NetServerInstanceData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class InMemoryDataGridForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private List<NetServerInstanceData> instanceDataList;
	private String imdgId;
	private String imdgConfig;
	
	public List<NetServerInstanceData> getInstanceDataList() {
		return instanceDataList;
	}
	public void setInstanceDataList(List<NetServerInstanceData> instanceDataList) {
		this.instanceDataList = instanceDataList;
	}
	public String getImdgConfig() {
		return imdgConfig;
	}
	public void setImdgConfig(String imdgConfig) {
		this.imdgConfig = imdgConfig;
	}
	public String getImdgId() {
		return imdgId;
	}
	public void setImdgId(String imdgId) {
		this.imdgId = imdgId;
	}
}
