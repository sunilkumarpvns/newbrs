package com.elitecore.elitesm.web.servermgr.copypacket.forms;

import com.elitecore.elitesm.datamanager.servermgr.copypacket.data.CopyPacketTranslationConfData;
import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class UpdateCopyPacketMappingConfigDetailForm extends BaseWebForm{
	
	private static final long serialVersionUID = 1L;
	private String action;
	private String copyPacketMapConfId;
	private CopyPacketTranslationConfData copyPacketMappingConfData;
	
	public CopyPacketTranslationConfData getCopyPacketMappingConfData() {
		return copyPacketMappingConfData;
	}
	public void setCopyPacketMappingConfData(
			CopyPacketTranslationConfData copyPacketMappingConfData) {
		this.copyPacketMappingConfData = copyPacketMappingConfData;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}
	public String getCopyPacketMapConfId() {
		return copyPacketMapConfId;
	}
	public void setCopyPacketMapConfId(String copyPacketMapConfId) {
		this.copyPacketMapConfId = copyPacketMapConfId;
	}
}
