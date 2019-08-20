package com.elitecore.elitesm.datamanager.system.databaseproperties;

import javax.validation.constraints.Pattern;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import org.hibernate.validator.constraints.NotEmpty;

import com.elitecore.commons.base.Differentiable;
import com.elitecore.elitesm.util.constants.RestValidationMessages;

import net.sf.json.JSONObject;

@XmlRootElement(name="database-properties")
@XmlType(propOrder = { "connectionUrl", "dbUsername", "dbPassword", "showSQL", "formatSQL", "dialect", "driverClass" })
public class DatabaseProperties implements Differentiable{
	
	@NotEmpty(message="Connection URL must be specified")
	private String connectionUrl;
	
	@NotEmpty(message="Username must be specified")
	private String dbUsername;
	
	@NotEmpty(message="Password must be specified")
	private String dbPassword;
	
	@NotEmpty(message="Show SQL must be specified")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = "Invalid value of Show SQL. It could be true or false")
	private String showSQL;
	
	@NotEmpty(message="Format SQL must be specified")
	@Pattern(regexp = RestValidationMessages.REGEX_TRUE_FALSE, message = "Invalid value of Format SQL. It could be true or false")
	private String formatSQL;
	
	private String dialect;
	private String driverClass;
	
	@XmlElement(name = "connection-url")
	public String getConnectionUrl() {
		return connectionUrl;
	}
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	@XmlElement(name = "username")
	public String getDbUsername() {
		return dbUsername;
	}
	public void setDbUsername(String dbUsername) {
		this.dbUsername = dbUsername;
	}
	
	@XmlElement(name = "password")
	public String getDbPassword() {
		return dbPassword;
	}
	public void setDbPassword(String dbPassword) {
		this.dbPassword = dbPassword;
	}
	
	@XmlElement(name = "show-sql")
	public String getShowSQL() {
		return showSQL;
	}
	public void setShowSQL(String showSQL) {
		this.showSQL = showSQL.toLowerCase();
	}
	
	@XmlElement(name = "format-sql")
	public String getFormatSQL() {
		return formatSQL;
	}
	public void setFormatSQL(String formatSQL) {
		this.formatSQL = formatSQL.toLowerCase();
	}
	
	@XmlElement(name = "dialect")
	public String getDialect() {
		return dialect;
	}
	public void setDialect(String dialect) {
		this.dialect = dialect;
	}
	
	@XmlElement(name = "driver-class")
	public String getDriverClass() {
		return driverClass;
	}
	public void setDriverClass(String driverClass) {
		this.driverClass = driverClass;
	}
	
	@Override
	public JSONObject toJson() {
		
		JSONObject jsonObject = new JSONObject();
		
		jsonObject.put("Connection URL", connectionUrl);
		jsonObject.put("Username", dbUsername);
		jsonObject.put("Password", dbPassword);
		jsonObject.put("Show SQL", showSQL);
		jsonObject.put("Format SQL", formatSQL);
		jsonObject.put("Dialect", dialect);
		jsonObject.put("Driver Class", driverClass);
		
		return jsonObject;
	}
}
