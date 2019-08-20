package com.elitecore.core.commons.tls;

import java.security.PrivateKey;
import java.security.cert.Certificate;
import java.util.Collection;
import java.util.List;

import com.elitecore.core.commons.tls.constant.PrivateKeyAlgo;


public interface ServerCertificateProfile {
	public String getName();
	public PrivateKey getPrivateKey();
	public String getPrivateKeyPassword();
	public String getPlainTextPrivateKeyPassword();
	public PrivateKeyAlgo getPrivateKeyAlgo();
	public Collection<? extends Certificate > getCertificates();
	public String getCertificatePath();
	public String getPrivateKeyPath();
	public Long getId();
	public String getUUID();
	public String getCertificateName();
	public String getPrivateKeyName() ;
	public byte[] getCertificateFileBytes();
	public byte[] getPrivatekeyFileBytes() ;
	public List<byte[]> getCertificateChainBytes();
}
