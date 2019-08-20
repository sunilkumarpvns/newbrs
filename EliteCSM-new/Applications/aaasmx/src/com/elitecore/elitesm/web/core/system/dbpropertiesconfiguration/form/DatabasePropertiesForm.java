package com.elitecore.elitesm.web.core.system.dbpropertiesconfiguration.form;

import com.elitecore.elitesm.web.core.base.forms.BaseWebForm;

public class DatabasePropertiesForm extends BaseWebForm {

	private static final long serialVersionUID = 1L;
	private String connectionUrl;
	private String dbUsername;
	private String dbPassword;
	private String showSQL;
	private String formatSQL;
	private String dialect;
	private String driverClass;
	
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	public String getDbUsername() {
		return dbUsername;
	}
	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	public String getShowSQL() {
		return showSQL;
	}
	public void setShowSQL(String showSQL) {
		this.showSQL = showSQL;
	}
	public String getFormatSQL() {
		return formatSQL;
	}
	public void setFormatSQL(String formatSQL) {
		this.formatSQL = formatSQL;
	}
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
}
