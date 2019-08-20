package com.elitecore.aaa.radius.conf.impl;


import static java.lang.String.format;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.UserDefined;
import com.elitecore.core.commons.config.core.annotations.ConfigurationProperties;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.Reloadable;
import com.elitecore.core.commons.config.core.annotations.XMLProperties;
import com.elitecore.core.commons.config.core.readerimpl.XMLReader;

@XmlType(propOrder = {})
@XmlRootElement(name = "clients")
@ConfigurationProperties(moduleName ="CLIENTS_CONFIGURABLE",readWith = XMLReader.class, reloadWith = XMLReader.class, synchronizeKey ="CLIENTS")
@XMLProperties(name = "clients", schemaDirectories = {"system","schema"}, configDirectories = {"conf"})
@Reloadable(type = ClientsConfigurable.class)
public class ClientsConfigurable extends Configurable{
	
	private static final String MODULE = "CLIENTS-CONFIGURABLE";
	private List<ClientDetail> clientsList;

	public ClientsConfigurable(){
		clientsList = new ArrayList<ClientDetail>();
	}
	
	@XmlElement(name = "client")
	@Reloadable(type = ClientDetail.class)
	public List<ClientDetail> getClients() {
		return clientsList;
	}

	public void setClients(List<ClientDetail> clients) {
		this.clientsList = clients;
	}

	@PostRead
	public void doProcessing() {
		if (LogManager.getLogger().isDebugLogLevel()) {
			LogManager.getLogger().debug(MODULE, "Configured Clients:\n" + clientsList);
		}
	}

	@PostWrite
	public void postWriteProcessing(){
		//This method is blank right now
	}
	
	@PostReload
	public void postReloadProcessing(){
		doProcessing();
	}

	@XmlType(propOrder = {})
	public static class ClientDetail implements UserDefined{
		private String strClientIp = null;
		private String strSharedSecret = null;;
		private String strFramedPoolName=null;
		private Long lTimeout = 8000L;
		private String profileName;
		
		@XmlElement(name = "profile-name", type = String.class)
		public String getProfileName() {
			return profileName;
		}
		public void setProfileName(String profileName) {
			this.profileName = profileName;
		}
		
		@XmlElement(name = "client-ip", type = String.class)
		public String getStrClientIp() {
			return strClientIp;
		}
		public void setStrClientIp(String strClientIp) {
			this.strClientIp = strClientIp;
		}
		
		@XmlElement(name = "shared-secret", type = String.class)
		public String getStrSharedSecret() {
			return strSharedSecret;
		}
		public void setStrSharedSecret(String strSharedSecret) {
			this.strSharedSecret = strSharedSecret;
		}
		
		@XmlElement(name = "framed-pool-name", type = String.class)
		public String getStrFramedPoolName() {
			return strFramedPoolName;
		}
		public void setStrFramedPoolName(String strFramedPoolName) {
			this.strFramedPoolName = strFramedPoolName;
		}
		
		@XmlElement(name = "request-expiry-time", type = Long.class)
		@Reloadable(type = Long.class)
		public Long getlTimeout() {
			return lTimeout;
		}
		public void setlTimeout(Long lTimeout) {
			this.lTimeout = lTimeout;
		}
		
		@Override
		public boolean equals(Object obj) {
			if(obj == null)
				return false;
			
			if(!(obj instanceof ClientDetail))
				return false;
			
			ClientDetail otherClientDetail = (ClientDetail) obj;
			
			if(this.strClientIp != null){
				return this.strClientIp.equals(otherClientDetail.strClientIp);
			}else{
				return false;
			}
		}
		
		@Override
		public String toString() {
			StringWriter writer = new StringWriter();
			PrintWriter out = new PrintWriter(writer);
			out.println(format("%-30s | %s", "Client IP", strClientIp));
			out.println(format("%-30s | %s", "Client Profile", profileName));
			out.println(format("%-30s | %s", "Shared Secret", strSharedSecret));
			out.println(format("%-30s | %s", "Request Expiry Time", lTimeout));
			out.println(format("%-30s | %s", "Framed Pool Name", Strings.isNullOrBlank(strFramedPoolName) ? "" : strFramedPoolName));
			return writer.toString();
		}
	}
}

