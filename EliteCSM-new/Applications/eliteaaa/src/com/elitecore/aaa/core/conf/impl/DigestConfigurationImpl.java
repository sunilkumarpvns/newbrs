package com.elitecore.aaa.core.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;

import com.elitecore.aaa.core.conf.DigestConfiguration;
import com.elitecore.core.serverx.ServerContext;

@XmlType(propOrder = {})
public class DigestConfigurationImpl implements DigestConfiguration{

	public static final String MODULE= "DIGEST-CONF";
	private String confId;
	private String name ="Default" ;
	private String description = "Default Description";
	private String realm = "elitecore.com";
	private String defaultQOP = "auth";
	private String defaultAlgorithm = "MD5";
	private String opaque = "elitecore";
	private String defaultNonce = "digest";
	private int defaultNonceLength = 4;
	private boolean draftAAASIPEnable=true;

	public DigestConfigurationImpl(ServerContext serverContext,String confId){
		this.confId = confId;
	}
	public DigestConfigurationImpl(){
		//required By Jaxb.
	}
	
	@XmlElement(name = "id",type = String.class)
	public String getConfId() {
		return confId;
	}

	public void setConfId(String confId){
		this.confId = confId;
	}
	public void setRealm(String realm) {
		this.realm = realm;
	}

	@XmlElement(name = "realm",type = String.class,defaultValue ="elitecore.com")
	public String getRealm() {
		return realm;
	}

	public void setDefaultQOP(String defaultQOP) {
		this.defaultQOP = defaultQOP;
	}

	@XmlElement(name = "default-qop",type = String.class,defaultValue ="auth")
	public String getDefaultQOP() {
		return defaultQOP;
	}

	public void setDefaultAlgorithm(String defaultAlgorithm) {
		this.defaultAlgorithm = defaultAlgorithm;
	}

	@XmlElement(name = "default-digest-algorithm",type = String.class,defaultValue ="MD5")
	public String getDefaultAlgorithm() {
		return defaultAlgorithm;
	}

	public void setOpaque(String opaque) {
		this.opaque = opaque;
	}
	@XmlElement(name = "opaque",type = String.class,defaultValue ="elitecore")
	public String getOpaque() {
		return opaque;
	}

	public void setDefaultNonce(String defaultNonce) {
		this.defaultNonce = defaultNonce;
	}

	@XmlElement(name = "default-nonce",type = String.class)
	public String getDefaultNonce() {
		return defaultNonce;
	}

	public void setDefaultNonceLength(int defaultNonceLength) {
		this.defaultNonceLength = defaultNonceLength;
	}
	@XmlElement(name = "default-nonce-length",type = int.class,defaultValue ="4")
	public int getDefaultNonceLength() {
		return defaultNonceLength;
	}

	public void setIsDraftAAASIPEnable(boolean draftAAASIPEnable) {
		this.draftAAASIPEnable = draftAAASIPEnable;
	}

	@XmlElement(name = "draft-sterman-aaa-sip",type = boolean.class,defaultValue ="true")
	public boolean getIsDraftAAASIPEnable() {
		return draftAAASIPEnable;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "name",type = String.class,defaultValue ="Default")
	public String getName() {
		return name;
	}

	public void setDescription(String description) {
		this.description = description;
	}
	@XmlTransient
	public String getDescription() {
		return description;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		out.println("    	 	-- Digest Configuration -- ");
		out.println();
		out.println("    	    	Digest Name = " + name);
		out.println("    	    	Digest Description = " + description);
		out.println("    	    	Digest Realm = " + realm);
		out.println("    	    	Default Digest Quality of Protection = " + defaultQOP);
		out.println("    	    	Default Digest Algorithm = " + defaultAlgorithm);
		out.println("    	    	Digest Opaque = " + opaque);
		out.println("    	    	Default Digest Nonce = " + defaultNonce);
		out.println("    	    	Default Digest Nonce Length = " + defaultNonceLength);
		out.println("    	    	Draft AAA SIP Enable = " + draftAAASIPEnable);
		out.println("    ");
		out.close();
		return stringBuffer.toString();
	}
	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof DigestConfigurationImpl))
			return false;
		
		DigestConfigurationImpl other = (DigestConfigurationImpl) obj;
		return this.confId == other.confId;
	}
}
