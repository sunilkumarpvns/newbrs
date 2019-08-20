package com.elitecore.aaa.core.plugins.conf;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable.PluginType;
import com.elitecore.aaa.core.plugins.conf.PluginDetail.PluginInfo;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Optional;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.plugins.PluginConfiguration;
import com.elitecore.core.commons.plugins.PluginContext;

public abstract class TransactionLoggerConfigurable<T> extends BasePluginConfigurable<T>{

	private static final String DATA_QUERY = "SELECT PLUGINID, TIMEBOUNDRY, LOGFILE, RANGE, PATTERN, GLOBALIZATION FROM TBLMTRANSACTIONLOGGER WHERE PLUGININSTANCEID=?";
	private static final String MAPPINGS_QUERY = "SELECT KEY, FORMAT FROM TBLMFORMATMAPPINGS WHERE PLUGINID=?";
	
	private int timeBoundryRollingUnit = 30;				// roll file every 30 mins
	private String logFileName = "user-statistic";
	private String fileExtension = "inp";
	private String sequenceRange;
	private String sequencePosition;
	private boolean sequenceGlobalization;
	
	private List<FormatMappingData> formatMappings = new ArrayList<FormatMappingData>();
	
	/*
	 * Transient properties
	 */
	private PluginContext pluginContext;
	private Optional<PluginInfo> pluginInfo = Optional.absent();
	
	@XmlElement( name = "time-boundry-rolling-unit")
	public int getTimeBoundryRollingUnit() {
		return timeBoundryRollingUnit;
	}
	public void setTimeBasedRollingUnit(int timeBoundryRollingUnit) {
		this.timeBoundryRollingUnit = timeBoundryRollingUnit;
	}

	@XmlElement( name = "log-file-name")
	public String getLogFileName() {
		return logFileName;
	}
	public void setLogFileName(String logFileName) {
		this.logFileName = logFileName;
	}
	
	@XmlElement( name = "file-extension")
	public String getFileExtension() {
		return fileExtension;
	}
	public void setFileExtension(String fileExtension) {
		this.fileExtension = fileExtension;
	}

	public String getSequenceRange() {
		return sequenceRange;
	}
	public void setSequenceRange(String sequenceRange) {
		this.sequenceRange = sequenceRange;
	}
	public String getSequencePosition() {
		return sequencePosition;
	}
	public void setSequencePosition(String sequencePosition) {
		this.sequencePosition = sequencePosition;
	}
	public boolean isSequenceGlobalization() {
		return sequenceGlobalization;
	}
	public void setSequenceGlobalization(boolean sequenceGlobalization) {
		this.sequenceGlobalization = sequenceGlobalization;
	}
	
	@XmlElementWrapper( name = "format-mappings")
	@XmlElement( name = "format-mapping")
	public List<FormatMappingData> getFormatMappings() {
		return formatMappings;
	}
	public void setFormatMappings(List<FormatMappingData> formatMappings) {
		this.formatMappings = formatMappings;
	}
	
	@DBRead
	@DBReload
	public void readFromDB() throws SQLException {
		
		AAAServerContext context = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resulSet = null;
		PreparedStatement pstmForMappings = null;
		ResultSet resulSetForMappings = null;
		try {
			
			List<FormatMappingData> tmpFormatMappings = new ArrayList<FormatMappingData>();
			
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			Set<PluginInfo> transactionLogger = context.getPluginDetail().getTypeSpecific(getPluginType());
			if (transactionLogger.isEmpty() == false) {
				this.pluginInfo = Optional.of(transactionLogger.iterator().next());
				pstmt = connection.prepareStatement(DATA_QUERY);
				pstmt.setString(1, pluginInfo.get().getId());
				resulSet = pstmt.executeQuery();
				
				resulSet.next();
				
				String formateId = resulSet.getString("PLUGINID");
				this.logFileName = resulSet.getString("LOGFILE");
				this.timeBoundryRollingUnit = resulSet.getInt("TIMEBOUNDRY");
				this.sequenceRange = resulSet.getString("RANGE");
				this.sequencePosition = resulSet.getString("PATTERN");
				this.sequenceGlobalization = Boolean.valueOf(resulSet.getString("GLOBALIZATION"));
				
				pstmForMappings = connection.prepareStatement(MAPPINGS_QUERY);
				pstmForMappings.setString(1, formateId);
				resulSetForMappings = pstmForMappings.executeQuery();
				
				while (resulSetForMappings.next()) {
					tmpFormatMappings.add(new FormatMappingData(resulSetForMappings.getString("KEY"), resulSetForMappings.getString("FORMAT")));
				}
				
				this.formatMappings = tmpFormatMappings;
			}
			
		}finally {
			DBUtility.closeQuietly(resulSet);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(connection);
			DBUtility.closeQuietly(pstmForMappings);
			DBUtility.closeQuietly(resulSetForMappings);
		}

		LogManager.getLogger().info(getMODULE(), "Succesfully read transaction logger configuration");
	}
	
	@PostRead
	@PostReload
	public void postRead() {
		this.pluginContext = new PluginContext() {

			@Override
			public AAAServerContext getServerContext() {
				return ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
			}

			@Override
			public PluginConfiguration getPluginConfiguration(String pluginName) {
				return null;
			}
		};
		
		Set<PluginInfo> transactionLogger = ((AAAConfigurationContext)getConfigurationContext()).getServerContext().getPluginDetail().getTypeSpecific(getPluginType());
		if (transactionLogger.isEmpty() == false) {
			this.pluginInfo = Optional.of(transactionLogger.iterator().next());
		}
	}
	
	protected final PluginContext getPluginContext() {
		return this.pluginContext;
	}
	
	protected final Optional<PluginInfo> getPluginInfo() {
		return this.pluginInfo;
	}
	
	protected abstract String getMODULE();
	protected abstract PluginType getPluginType();
	
	@PostWrite
	public void postWrite() {
		
	}
	
}
