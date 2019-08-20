package com.elitecore.elitesm.web.systemstartup.dbsetup.form;

import java.util.Properties;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class EliteAAAStartupDBSetupForm extends BaseWebForm {
	
	private static final long serialVersionUID = 1L;
	private String jdbcDriver = "oracle.jdbc.driver.OracleDriver";
	private String connectionUrl="jdbc:oracle:thin:@127.0.0.1:1521:eliteaaa";
	private String userName = "ELITEAAA";
	private String passWord;
	private String dialect = "org.hibernate.dialect.Oracle9Dialect";
	private String showSql = "false";
	private String formatSql = "false";
	
	/*internal purpose*/
	private String action;
	private Properties dbProperties;
	private boolean isInvalidParamters;
	private String errorMsg;
	
	private String newUserName;
	private String newPassword;
	private String dbfFileLocation;
	
	private String schemaSql;
	
	private String encryptedPasswordData;
	
	private String dbAdminUsername;
	private String dbAdminPassword;
	private boolean createNewUserSelected ;
	
	private boolean sqlExecuted;
	private boolean environmentSetupDone;
	
	public boolean getEnvironmentSetupDone() {
		return environmentSetupDone;
	}
	public void setEnvironmentSetupDone(boolean environmentSetupDone) {
		this.environmentSetupDone = environmentSetupDone;
	}
	public boolean getSqlExecuted() {
		return sqlExecuted;
	}
	public void setSqlExecuted(boolean sqlExecuted) {
		this.sqlExecuted = sqlExecuted;
	}
	public String getDbAdminUsername() {
		return dbAdminUsername;
	}
	public void setDbAdminUsername(String dbAdminUsername) {
		this.dbAdminUsername = dbAdminUsername;
	}
	public String getDbAdminPassword() {
		return dbAdminPassword;
	}
	public void setDbAdminPassword(String dbAdminPassword) {
		this.dbAdminPassword = dbAdminPassword;
	}
	public boolean getCreateNewUserSelected() {
		return createNewUserSelected;
	}
	public void setCreateNewUserSelected(boolean createNewUserSelected) {
		this.createNewUserSelected = createNewUserSelected;
	}
	
	public String getNewUserName() {
		return newUserName;
	}
	public void setNewUserName(String newUserName) {
		this.newUserName = newUserName;
	}
	public String getNewPassword() {
		return newPassword;
	}
	public void setNewPassword(String newPassword) {
		this.newPassword = newPassword;
	}
	
	public String getSchemaSql() {
		return schemaSql;
	}
	public void setSchemaSql(String schemaSql) {
		this.schemaSql = schemaSql;
	}
	public String getAction() {
		return action;
	}
	public void setAction(String action) {
		this.action = action;
	}

	public String getJdbcDriver() {
		return jdbcDriver;
	}
	public String getErrorMsg() {
		return errorMsg;
	}
	public void setErrorMsg(String errorMsg) {
		this.errorMsg = errorMsg;
	}
	public void setJdbcDriver(String jdbcDriver) {
		this.jdbcDriver = jdbcDriver;
	}
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getPassWord() {
		return passWord;
	}
	public void setPassWord(String passWord) {
		this.passWord = passWord;
	}
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	public String getShowSql() {
		return showSql;
	}
	public void setShowSql(String showSql) {
		this.showSql = showSql;
	}
	public String getFormatSql() {
		return formatSql;
	}
	public void setFormatSql(String formatSql) {
		this.formatSql = formatSql;
	}
	
	public boolean isInvalidParamters() {
		return isInvalidParamters;
	}
	public void setInvalidParamters(boolean isInvalidParamters) {
		this.isInvalidParamters = isInvalidParamters;
	}
	public Properties getDbProperties() {
		return dbProperties;
	}
	public void setDbProperties(Properties dbProperties) {
		this.dbProperties = dbProperties;
	}
	public String getEncryptedPasswordData() {
		return encryptedPasswordData;
	}
	public void setEncryptedPasswordData(String encryptedPasswordData) {
		this.encryptedPasswordData = encryptedPasswordData;
	}
	public String getDbfFileLocation() {
		return dbfFileLocation;
	}
	public void setDbfFileLocation(String dbfFileLocation) {
		this.dbfFileLocation = dbfFileLocation;
	}
	
}
