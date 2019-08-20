package com.elitecore.corenetvertex.spr.data;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.Table;

@Entity(name="com.elitecore.corenetvertex.spr.data.DatabaseDSData")
@Table(name="TBLMDATABASEDS")
public class DatabaseDSData implements Serializable{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Long id;
	private String name;
    private String connectionUrl;
    private String userName;
    private String password;
    private Long minimumPool;
    private Long maximumPool;
    private Long timeout;
    private Long statusCheckDuration;
    
    @Id
    @Column(name="DATABASEDSID")
    @GeneratedValue(generator="sequenceGenerator")
	public Long getId() {
		return id;
	}
	public void setId(Long id) {
		this.id = id;
	}
	
	@Column(name="NAME")
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	
	@Column(name="CONNECTIONURL")
	public String getConnectionUrl() {
		return connectionUrl;
	}
	
	public void setConnectionUrl(String connectionUrl) {
		this.connectionUrl = connectionUrl;
	}
	
	@Column(name="USERNAME")
	public String getUserName() {
		return userName;
	}
	
	public void setUserName(String userName) {
		this.userName = userName;
	}
	
	@Column(name="PASSWORD")
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	@Column(name="MINIMUMPOOL")
	public Long getMinimumPool() {
		return minimumPool;
	}
	
	public void setMinimumPool(Long minimumPool) {
		this.minimumPool = minimumPool;
	}
	
	@Column(name="MAXIMUMPOOL")
	public Long getMaximumPool() {
		return maximumPool;
	}
	
	public void setMaximumPool(Long maximumPool) {
		this.maximumPool = maximumPool;
	}
	
	@Column(name="TIMEOUT")
	public Long getTimeout() {
		return timeout;
	}
	
	public void setTimeout(Long timeout) {
		this.timeout = timeout;
	}
	
	@Column(name="STATUSCHECKDURATION")
	public Long getStatusCheckDuration() {
		return statusCheckDuration;
	}
	public void setStatusCheckDuration(Long statusCheckDuration) {
		this.statusCheckDuration = statusCheckDuration;
	}
}
