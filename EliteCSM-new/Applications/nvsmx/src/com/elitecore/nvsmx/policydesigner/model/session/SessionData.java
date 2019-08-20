package com.elitecore.nvsmx.policydesigner.model.session;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Function;
import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.QCI;
import com.elitecore.corenetvertex.pkg.ims.MediaTypeData;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl;
import com.elitecore.corenetvertex.session.SessionInformation;
import com.elitecore.corenetvertex.session.SessionRuleData;
import com.elitecore.corenetvertex.sm.servergroup.ServerGroupData;
import com.elitecore.corenetvertex.sm.serverinstance.ServerInstanceData;
import com.elitecore.corenetvertex.util.ActivePCCRuleParser;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.nvsmx.policydesigner.model.pkg.chargingrulebasename.ChargingRuleBaseName;
import java.io.Serializable;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Used to display session information.
 * @author Dhyani.Raval
 *
 */
public class SessionData implements Serializable{

	private String schemaName;
	private String sessionId;
	private String  creationTime;
	private String lastUpdateTime;
	private Map<String,String> sessionInfo = new HashMap<String, String>();
	private List<PCCRuleImpl> activePccrules = Collectionz.newArrayList();
	private List<ChargingRuleBaseName> activeChargingRuleBaseNames = Collectionz.newArrayList();
	private ServerGroupData instanceGroupData;
    private ServerInstanceData instanceData;
	private List<PCCRuleImpl> afActivePccrules = Collectionz.newArrayList();
	private String subSessionsIdentifier;
	private String sessionType;
	
	public String getSchemaName() {
		return schemaName;
	}
	public void setSchemaName(String schemaName) {
		this.schemaName = schemaName;
	}
	public String getSessionId() {
		return sessionId;
	}
	public void setSessionId(String sessionId) {
		this.sessionId = sessionId;
	}
	public String getCreationTime() {
		return creationTime;
	}
	public void setCreationTime(String creationTime) {
		this.creationTime = creationTime;
	}
	public String getLastUpdateTime() {
		return lastUpdateTime;
	}
	public void setLastUpdateTime(String lastUpdateTime) {
		this.lastUpdateTime = lastUpdateTime;
	}
	public Map<String, String> getSessionInfo() {
		return sessionInfo;
	}
	public void setSessionInfo(Map<String, String> sessionInfo) {
		this.sessionInfo = sessionInfo;
	}
	public List<PCCRuleImpl> getActivePccrules() {
		return activePccrules;
	}
	public void setActivePccrules(List<PCCRuleImpl> activePccrules) {
		this.activePccrules = activePccrules;
	}
	public ServerGroupData getInstanceGroupData() {
		return instanceGroupData;
	}
	public void setInstanceGroupData(ServerGroupData instanceGroupData) {
		this.instanceGroupData = instanceGroupData;
	}
	public ServerInstanceData getInstanceData() {
		return instanceData;
	}
	public void setInstanceData(ServerInstanceData instanceData) {
		this.instanceData = instanceData;
	}

	public List<ChargingRuleBaseName> getActiveChargingRuleBaseNames() {
		return activeChargingRuleBaseNames;
	}

	public void setActiveChargingRuleBaseNames(List<ChargingRuleBaseName> activeChargingRuleBaseNames) {
		this.activeChargingRuleBaseNames = activeChargingRuleBaseNames;
	}

	public List<PCCRuleImpl> getAfActivePccrules() {
		return afActivePccrules;
	}

	public void setAfActivePccrules(List<PCCRuleImpl> afActivePccrules) {
		this.afActivePccrules = afActivePccrules;
	}

	public String getSubSessionsIdentifier() {
		return subSessionsIdentifier;
	}

	public void setSubSessionsIdentifier(String subSessionsIdentifier) {
		this.subSessionsIdentifier = subSessionsIdentifier;
	}

	public String getSessionType() {
		return sessionType;
	}

	public void setSessionType(String sessionType) {
		this.sessionType = sessionType;
	}

}
