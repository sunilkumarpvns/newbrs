package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.MAPGWAuthDriverConfiguration;
import com.elitecore.aaa.core.data.AccountDataFieldMapping;

@XmlType(propOrder = {})
public abstract class MAPGWAuthDriverConfigurationImpl  implements MAPGWAuthDriverConfiguration{

	private String driverInstanceId;
	private String mapGWAuthID;
	private String driverName  = "Default Driver Name";
	private String remoteHostId = "";
	private String remoteHostPort =""; 
	private String userIdentity;
	private String remoteHostIP ="";
	private String localHostId ="";
	private String localHostPort ="";
	private String localHostIP ="";
	private int mapGWConnPoolSize=30;
	private long requestTimeout = 1000;
	private int statusCheckDuration=120;
	private int maxQueryTimeoutCount = 100;
	private List<String> userIdentityAttribute = null;
	private AccountDataFieldMapping accountDataFieldMapping;
	private GSMParamsDetail gsmParamsDetail;
	
	public MAPGWAuthDriverConfigurationImpl(){
		gsmParamsDetail = new GSMParamsDetail();
		accountDataFieldMapping = new AccountDataFieldMapping();
		userIdentityAttribute = new ArrayList<String>();
	}	
	
	
	@XmlElement(name ="mapgateway-auth-id",type = String.class)
	public String getMapGWAuthID() {
		return mapGWAuthID;
	}

	public void setMapGWAuthID(String mapGWAuthID) {
		this.mapGWAuthID = mapGWAuthID;
	}

	
	@XmlElement(name = "user-identity-attributes")
	public String getUserIdentity() {
		return userIdentity;
	}

	public void setUserIdentity(String userIdentity) {
		this.userIdentity = userIdentity;
	}

	@XmlElement(name ="remote-host-port",type = String.class,defaultValue ="")
	public String getRemoteHostPort() {
		return remoteHostPort;
	}

	public void setRemoteHostPort(String remoteHostPort) {
		this.remoteHostPort = remoteHostPort;
	}
	
	@XmlElement(name ="remote-host-ip",type = String.class,defaultValue ="")
	public String getRemoteHostIP() {
		return remoteHostIP;
	}

	public void setRemoteHostIP(String remoteHostIP) {
		this.remoteHostIP = remoteHostIP;
	}
	@XmlElement(name ="local-host-port",type = String.class,defaultValue ="")
	public String getLocalHostPort() {
		return localHostPort;
	}

	public void setLocalHostPort(String localHostPort) {
		this.localHostPort = localHostPort;
	}

	@XmlElement(name ="local-host-ip",type = String.class,defaultValue ="")
	public String getLocalHostIP() {
		return localHostIP;
	}

	public void setLocalHostIP(String localHostIP) {
		this.localHostIP = localHostIP;
	}
	
	@XmlElement(name = "gsm-parameters")
	public GSMParamsDetail getGsmParamsDetail() {
		return gsmParamsDetail;
	}

	public void setGsmParamsDetail(GSMParamsDetail gsmParams) {
		this.gsmParamsDetail = gsmParams;
	}
	

	
	@Override
	@XmlTransient
	public String getDriverInstanceId() {
		return this.driverInstanceId;
	}
	
	public void setDriverInstanceId(String driverInstanceId){
		this.driverInstanceId = driverInstanceId;
	}
	
	@Override
	@XmlElement(name = "name",type = String.class,defaultValue ="Default Driver Name")
	public String getDriverName() {
		return this.driverName;
	}

	public void setDriverName(String driverName){
		this.driverName = driverName;
	}
	@Override
	@XmlElement(name = "remote-host-id",type = String.class,defaultValue="")
	public String getRemoteHost(){
		return remoteHostId;
	}
	
	public void setRemoteHost(String remoteHostId){
		this.remoteHostId = remoteHostId;
	}
	

	@Override
	@XmlElement(name = "local-host-id",type = String.class,defaultValue ="")
	public String getLocalHost(){
		return localHostId;
	}
	public void setLocalHost(String localHostId){
		this.localHostId = localHostId;
	}
	
	@XmlElement(name = "connection-pool-size",type =int.class,defaultValue ="30")
	public int getNoOfMAPGWConnections(){
		return mapGWConnPoolSize;
	}
	
	public void setNoOfMAPGWConnections(int mapGWconnPool){
		this.mapGWConnPoolSize = mapGWconnPool;
	}
	
	
	@Override
	@XmlElement(name = "mapgw-field-mapping-list")
	public AccountDataFieldMapping getAccountDataFieldMapping(){
		return this.accountDataFieldMapping;
	}
	public void setAccountDataFieldMapping(AccountDataFieldMapping accountFieldMapping){
		this.accountDataFieldMapping = accountFieldMapping;
	}

	@Override
	@XmlElement(name = "mapgw-query-timeout",type = long.class,defaultValue ="1000")
	public long getRequestTimeout() {
		return requestTimeout;
	}

	
	public void setRequestTimeout(long requestTimeOut){
		this.requestTimeout = requestTimeOut;
	}
	@Override
	@XmlElement(name = "maximum-query-timeout-count",type = int.class,defaultValue ="100")
	public int getMaxQueryTimeoutCount() {
		return maxQueryTimeoutCount;
	}
	
	public void setMaxQueryTimeoutCount(int maxQueryTimeOutCount){
		this.maxQueryTimeoutCount = maxQueryTimeOutCount;
	}

	@Override
	@XmlElement(name = "status-check-duration",type = int.class,defaultValue ="120")
	public int getStatusCheckDuration() {
		return this.statusCheckDuration;
	}
	
	public void setStatusCheckDuration(int statusCheckDuration){
		this.statusCheckDuration = statusCheckDuration;
	}
	
	@Override
	@XmlTransient
	public List<String> getUserIdentityAttributes() {
		return userIdentityAttribute;
	}
	public void setUserIdentityAttributes(List<String> userIdentity){
		this.userIdentityAttribute = userIdentity;
	}
	
	
	@Override
	@XmlTransient
	public boolean getIsSAIEnabled() {
		return gsmParamsDetail.getIsSAIEnabled();
	};
	
	public void setIsSAIEnabled(boolean isSAIEnabled) {
		gsmParamsDetail.setIsSAIEnabled(isSAIEnabled);
	}

	@Override
	@XmlTransient
	public int getNumberOfSIMTriplets() {
		return gsmParamsDetail.getNumberOfSIMTriplets();
	}
	public void setNumberOfSIMTriplets(int noOfTriplets){
		gsmParamsDetail.setNumberOfSIMTriplets(noOfTriplets);
	}
	
	@Override
	@XmlTransient
	public boolean isRestoreDataEnabled() {
		return getAccountDataFieldMapping().getFieldMapping().size() > 0;
	}
	
	public String toString(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println();		
		out.println("--  Map GW Configuration --");
		out.println("    Driver Name                  : " + getDriverName());
		out.println("    Remote Host ID               : " + getRemoteHost());
		out.println("    Local Host ID                : " + getLocalHost());
		out.println("    Map Gateway No of Connections: " + getNoOfMAPGWConnections());
		out.println("    Request Timeout              : " + getRequestTimeout());
		out.println("    Maximum Query Timeout Count  : " + getMaxQueryTimeoutCount());
		out.println("    User Identity Attributes     : " + ((userIdentityAttribute != null) ? userIdentityAttribute : ""));
		out.println();
		out.println(" -- Field Mapping ( Logical Field Name = MAP GW Field Name) -- ");
		out.println(getAccountDataFieldMapping());
		out.println();
		return stringWriter.toString();
	}
	
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof MAPGWAuthDriverConfigurationImpl))
			return false;
		
		MAPGWAuthDriverConfigurationImpl other = (MAPGWAuthDriverConfigurationImpl) obj;
		return this.driverInstanceId == other.driverInstanceId;
	}
}
@XmlType(propOrder = {})
class GSMParamsDetail{
	
	private int noOfTriplrts = 3;
	private boolean isSAIEnabled = false;
	
	public GSMParamsDetail(){
		//requied by Jaxb.
	}
	
	@XmlElement(name = "number-of-triplets",type = int.class,defaultValue ="3")
	public int getNumberOfSIMTriplets() {
		return noOfTriplrts;
	}
	public void setNumberOfSIMTriplets(int noOfTriplets){
		this.noOfTriplrts = noOfTriplets;
	}
	
	@XmlElement(name = "send-auth-info",type = boolean.class,defaultValue ="false")
	public boolean getIsSAIEnabled() {
		return isSAIEnabled;
	};
	
	public void setIsSAIEnabled(boolean isSAIEnabled) {
		this.isSAIEnabled = isSAIEnabled;
	}
	
}

