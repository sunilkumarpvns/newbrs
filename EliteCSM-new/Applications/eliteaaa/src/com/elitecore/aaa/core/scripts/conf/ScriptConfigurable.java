package com.elitecore.aaa.core.scripts.conf;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlEnumValue;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.conf.context.AAAConfigurationState;
import com.elitecore.aaa.core.scripts.ScriptDetail;
import com.elitecore.aaa.core.util.constant.CommonConstants;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.io.ByteStreams;
import com.elitecore.commons.io.Closeables;
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
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.alert.AlertSeverity;


@XmlRootElement(name = "external-scripts")
@ConfigurationProperties(moduleName ="EXTERNAL-SCRIPTS-CONFIGURABLE",synchronizeKey ="", readWith = DBReader.class, reloadWith = DBReader.class, writeWith = XMLWriter.class)
@XMLProperties(schemaDirectories = {"system","schema"} ,configDirectories = {"conf","db","external-scripts"},name = "scripts")

public class ScriptConfigurable extends Configurable{

	public static final String MODULE = "EXTERNAL-SCRIPTS-CONFIGURABLE";

	private List<ScriptDetail> list = new ArrayList<ScriptDetail>();

	//transient fields
	private Map<String, List<File>> routerScript = new HashMap<String, List<File>>();
	private Map<String, List<File>> externalScript = new HashMap<String, List<File>>();

	public Map<String, List<File>> getExternalScript() {
		return externalScript;
	}

	public Map<String, List<File>> getRouterScript() {
		return routerScript;
	}

	@XmlElementWrapper(name = "script-details-list")
	@XmlElement(name="script-data")
	public List<ScriptDetail> getList() {
		return list;
	}

	public void setList(List<ScriptDetail> list) {
		this.list = list;
	}


	@DBRead
	public void readFromDB() throws SQLException {
		Connection connection = null;
		PreparedStatement pstmt = null;
		ResultSet resultSet = null;

		String queryForScriptDetail = "SELECT * FROM TBLMSCRIPTINSTANCE WHERE STATUS='" + CommonConstants.DATABASE_POLICY_STATUS_ACTIVE +"'";
		try {
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			pstmt = connection.prepareStatement(queryForScriptDetail);
			resultSet = pstmt.executeQuery();
			while (resultSet.next()) {

				ScriptDetail scriptDetail = new ScriptDetail();

				String scriptId = resultSet.getString("SCRIPTID");
				String scriptTypeId = resultSet.getString("SCRIPTTYPEID");
				String name = resultSet.getString("NAME");

				scriptDetail.setName(name);
				scriptDetail.setType(ScriptType.from(Integer.valueOf(scriptTypeId)));
				scriptDetail.setDescription(resultSet.getString("DESCRIPTION"));
				scriptDetail.setStatus(resultSet.getString("STATUS"));

				setScriptFileDetail(scriptDetail,scriptId, connection); 
			}
		} finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(connection);
		}
	}

	@DBReload
	public void reloadFromDB() {
		// NO-OP
	}
	private void setScriptFileDetail(ScriptDetail scriptDetail, String scriptId, Connection connection) throws SQLException {
		PreparedStatement pstmt= null;
		ResultSet resultSet = null;
		String queryForScriptFiles = "SELECT * FROM TBLMSCRIPTDATA WHERE SCRIPTID = ?";
		try {
			pstmt= connection.prepareStatement(queryForScriptFiles);
			pstmt.setString(1, scriptId);
			resultSet = pstmt.executeQuery();

			while(resultSet.next()) {
				byte[] scriptContents = resultSet.getBytes("SCRIPTFILE");
				String fileName = resultSet.getString("FILENAME");
				long lastUpdateTime = resultSet.getTimestamp("LASTUPDATETIME").getTime();
				if(scriptContents != null) {
					storeBlobToFile(scriptContents, fileName, lastUpdateTime, scriptDetail);
				}
			}
			if(scriptDetail.getFilenameToFile().isEmpty()) {
				LogManager.getLogger().debug(MODULE, "No Script file is uploaded for " + scriptDetail.getName());
			}else {
				LogManager.getLogger().info(MODULE, "Succesfully read script file(s): " + scriptDetail.getFilenameToFile().keySet()
						+ " For the Script " + scriptDetail.getName());
			}
		}finally {
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(pstmt);
		}
	}

	private void storeBlobToFile(byte[] scriptContents, String fileName, long timestamp , ScriptDetail scriptDetail) {
		ServerContext serverContext = getServerContext();
		ScriptType scriptType = scriptDetail.getType();
		File file = null;
		try {
			if(scriptType.equals(ScriptType.DIAMETER_ROUTER_SCRIPT)) {
				file = createFileInstance(getDiameterScriptHome(serverContext), fileName);
			} else {
				file = createFileInstance(getScriptHome(serverContext), fileName);
			}

			ByteArrayInputStream scriptFileContentBinaryStream = new ByteArrayInputStream(scriptContents);

			if (file.exists()) {
				if (timestamp > file.lastModified()) {
					changeFileContent(file, scriptFileContentBinaryStream);
				} else {
					LogManager.getLogger().info(MODULE, "Using the newer version, found in local repository, kindly update" +
							" central DB repository as per analysis - " + fileName);
					serverContext.generateSystemAlert(AlertSeverity.INFO, Alerts.OTHER_GENERIC, MODULE, 
							"Using the newer version, found in local repository, kindly update" +
									" central DB repository as per analysis - " + fileName);
				}

			} else {
				createFile(file, scriptFileContentBinaryStream);
				if(file.setLastModified(timestamp) == false) {
					LogManager.getLogger().trace(MODULE, "File Updation was not successful" + file.getName());
				}
			}

			loadScriptFiles(file, scriptDetail);

		}catch (IOException e) {
			LogManager.getLogger().warn(MODULE, "Configuration reading for " +file.getName() + " for script " + scriptDetail.getName() +
					" failed, Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}


	private ServerContext getServerContext() {
		AAAConfigurationContext aaaConfigurationContext = (AAAConfigurationContext) getConfigurationContext();
		return aaaConfigurationContext.getServerContext();
	}

	private String getScriptHome(ServerContext serverContext) {
		return  serverContext.getServerHome() + File.separator + "conf" + 
				File.separator + "db" + File.separator + "external-scripts" ;
	}

	private String getDiameterScriptHome(ServerContext serverContext) {
		return  serverContext.getServerHome() + File.separator + "conf" + 
				File.separator + "db" + File.separator + "external-scripts" + File.separator + "diameter" ;
	}

	private File createFileInstance(String scriptHome, String fileName) {
		File directory = new File(scriptHome);
		if (directory.exists() == false) {
			directory.mkdirs();
		}
		return  new File(scriptHome + File.separator + fileName);
	}

	private void loadScriptFiles(File file, ScriptDetail scriptDetail) {
		///FIXME APPLY GROOVY FILTER
		if(file.getName().endsWith(".groovy")){
			scriptDetail.getFilenameToFile().put(file.getName(), file);
			list.add(scriptDetail);
		}
	}

	private void changeFileContent(File file, InputStream binaryStream) throws IOException {
		if(file.delete()) {
			createFile(file, binaryStream);
		}
	}

	private void createFile(File file, InputStream binaryStream) throws IOException {
		FileOutputStream stream = null;
		try {																
			if(file.createNewFile()) {
				stream = new FileOutputStream(file);
				stream.write(ByteStreams.readFully(binaryStream));
			}
		}finally {
			if(stream != null) {
				Closeables.closeQuietly(stream);
			}
		}
	}

	@PostRead
	public void postRead() {
		if (((AAAConfigurationContext)getConfigurationContext()).state() == AAAConfigurationState.FALLBACK_CONFIGURATION) {
			List<ScriptDetail> data =  getList();
			for(ScriptDetail scriptDetail : data ) {
				LogManager.getLogger().info(MODULE, "Succesfully read script file(s): " + scriptDetail.getFilenameToFile().keySet()
						+ "For the Script" + scriptDetail.getName());
			}
		}
		filterScriptFiles();
	}

	private void filterScriptFiles() {
		for(ScriptDetail scriptdetail : list) {
			List<File> files = new ArrayList<File>();
			files.addAll(scriptdetail.getFilenameToFile().values());
			if(scriptdetail.getType() == ScriptType.DIAMETER_ROUTER_SCRIPT) {
				routerScript.put(scriptdetail.getName(), files);
			}else {
				externalScript.put(scriptdetail.getName(), files);
			}
		}
	}

	@PostReload
	public void postReload() {
		// This is to support Configuration Framework 
	}

	@PostWrite
	public void postWrite() {
		// This is to support Configuration Framework
	}

	@XmlEnum
	public enum ScriptType {

		@XmlEnumValue(value = "DRIVER_SCRIPT")
		DRIVER_SCRIPT(1, "DRIVER_SCRIPT"),

		@XmlEnumValue(value = "TRANSLATION_MAPPING_SCRIPT")
		TRANSLATION_MAPPING_SCRIPT(2, "TRANSLATION_MAPPING_SCRIPT"),

		@XmlEnumValue(value = "EXTERNAL_RADIUS_SCRIPT")
		EXTERNAL_RADIUS_SCRIPT(3, "EXTERNAL_RADIUS_SCRIPT"),

		@XmlEnumValue(value = "DIAMETER_ROUTER_SCRIPT")
		DIAMETER_ROUTER_SCRIPT(4, "DIAMETER_ROUTER_SCRIPT");

		private int typeId;
		private String typeName;

		private static Map<Integer, ScriptType> map = Collections.emptyMap();

		ScriptType(int typeId, String typeName) {
			this.typeId = typeId;
			this.typeName = typeName;
		}

		public String getTypeName() {
			return typeName;
		}

		static {
			map = new HashMap<Integer, ScriptType>();
			for (ScriptType type : values()) {
				map.put(type.typeId, type);
			}
		}

		public static ScriptType from(int name) {
			return map.get(name);
		}
	}
}
