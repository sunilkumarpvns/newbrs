package com.elitecore.aaa.core.conf;

public interface DigestConfiguration {
	public String getName(); 
	public String getDescription(); 
	public String getConfId();
	public String getRealm(); 
	public String getDefaultQOP(); 
	public String getDefaultAlgorithm(); 
	public String getOpaque();
	public String getDefaultNonce();
	public int getDefaultNonceLength(); 
	public boolean getIsDraftAAASIPEnable();
}
