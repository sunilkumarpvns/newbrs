package com.elitecore.aaa.diameter.conf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.DBReader;
import com.elitecore.core.commons.config.core.writerimpl.XMLWriter;
import com.elitecore.diameterapi.core.common.transport.priority.Priority;
import com.elitecore.diameterapi.core.common.transport.priority.PriorityEntry.DiameterSessionTypes;

@XmlType(propOrder = {})
@XmlRootElement(name = "priority-table")
@ConfigurationProperties(moduleName = "PRIORITY-TBL-CONFIGURABLE", readWith = DBReader.class, writeWith = XMLWriter.class,synchronizeKey ="")
@XMLProperties(name = "priority-table", schemaDirectories = {"system", "schema"}, configDirectories = {"conf", "diameter"})
public class PriorityConfigurable extends Configurable {
	private static final String MODULE = "PRIORITY-TBL-CONF";
	private List<PriorityEntryConfigurable> priorityEntryConfigurables;

	public PriorityConfigurable() {
		priorityEntryConfigurables = new ArrayList<PriorityEntryConfigurable>();
	}
	
	
	@DBRead
	public void readFromDB() throws Exception {
		Connection connection = null;
		String query = "";
		PreparedStatement psForPriority = null;
		ResultSet rsForPriority =  null;
		List<PriorityEntryConfigurable> priorityEntryConfigurables = new ArrayList<PriorityEntryConfigurable>();
		
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			query = getQueryForPriorityTables();
			psForPriority = connection.prepareStatement(query);
			rsForPriority = psForPriority.executeQuery();
			PriorityEntryConfigurable priorityEntryConfigurable = null;
			String applicationIds, commandCodes, ipAddresses;
			int priorityVal, diamterSessionTypeVal;
			String priorityId;
			
			while (rsForPriority.next()) {
				priorityId = rsForPriority.getString("PRIORITYTABLEID");
				applicationIds = rsForPriority.getString("APPLICATIONID");
				commandCodes = rsForPriority.getString("COMMANDCODE");
				ipAddresses = rsForPriority.getString("IPADDRESS");
				diamterSessionTypeVal = rsForPriority.getInt("DIAMETERSESSIONTYPE");
				priorityVal = rsForPriority.getInt("PRIORITY");
				
				DiameterSessionTypes diameterSessionType = DiameterSessionTypes.fromVal(diamterSessionTypeVal);
				if(diameterSessionType == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Skipping Priority: " + priorityId + ". Reason: Invalid diameter session type value: " + diamterSessionTypeVal);
					}
					continue;
				}
				
				Priority priority = Priority.fromPriority(priorityVal);
				if(priority == null) {
					if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
						LogManager.getLogger().warn(MODULE, "Skipping Priority: " + priorityId + ". Reason: Invalid priority value: " + priorityVal);
					}
					continue;
				}
				
				
				priorityEntryConfigurable = new PriorityEntryConfigurable();
				priorityEntryConfigurable.setPriorityId(priorityId);
				priorityEntryConfigurable.setApplicationIds(applicationIds);
				priorityEntryConfigurable.setCommandCodes(commandCodes);
				priorityEntryConfigurable.setIpAddresses(ipAddresses);
				priorityEntryConfigurable.setdiameterSessionType(diameterSessionType);
				priorityEntryConfigurable.setPriority(priority);
				priorityEntryConfigurables.add(priorityEntryConfigurable);
			}
			this.priorityEntryConfigurables = priorityEntryConfigurables;
		} finally {
			DBUtility.closeQuietly(rsForPriority);
			DBUtility.closeQuietly(psForPriority);
			DBUtility.closeQuietly(connection);
		}
	}
	
	@PostWrite
	public void postWriteProcessing(){
		
	}
	
	@PostRead
	public void postReadProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}

	@DBReload
	public void dbReload(){
		
	}
	
	private String getQueryForPriorityTables() {
		return "select * from TBLMPRIORITYTABLE";
	}

	@XmlElement(name="priority-entry")
	public List<PriorityEntryConfigurable> getPriorityEntryConfigurables() {
		return priorityEntryConfigurables;
	}
}
