package com.elitecore.coreeap.data.tls;

import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.commons.util.cipher.ICipherProvider;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;

public class TLSSecurityParameters {
	private ProtocolVersion protocolVersion;
	/*
	 * 	PRFAlgorithm was introduced in TLSv1.2
	 * 	For versions lower than TLSv1.2 
	 * 		PRFAlgorithm == MACAlgorithm == hashAlgorithm == CipherSuite specific Algorithm.
	 * 
	 * 	For TLSv1.2, SHA-256 is a replacement for MD5/SHA-1, 
	 * 	And for larger than/equal to SHA-256 hash Cipher Suite 
	 * 	specific Algorithms are used as PRFAlgorithm.
	 * 
	 */
	private String PRFAlgorithm;
	/*
	 * 	MACAlgorithm is always the Algorithm that is specified in Cipher Suites.
	 * 	This Algorithm is same as PRFALgorithm for version less than TLSv1.2 
	 * 
	 */
	private String MACAlgorithm;
	private CipherSuites cipher;
	private int keySize;
	private int keyMaterialLength;
	private boolean isExportable;
	private ICipherProvider cipherProvider;
	
	public ProtocolVersion getProtocolVersion(){
		return protocolVersion;
	}
	public void setProtocolVersion(ProtocolVersion version){
		this.protocolVersion = version;
	}
	public String getPRFAlgorithm(){
		return PRFAlgorithm;
	}
	public void setPRFAlgorithm(String algorithm){
			this.PRFAlgorithm = algorithm;
	}
	public String getMACAlgo(){
		return MACAlgorithm;
	}
	public void setMACAlgo(String MACAlgorithm){
		this.MACAlgorithm = MACAlgorithm;
	}
	public CipherSuites getCipher() {
		return cipher;
	}
	public void setCipher(CipherSuites cipher) {
		this.cipher = cipher;
	}
	public int getKeySize() {
		return keySize;
	}
	public void setKeySize(int keySize) {
		this.keySize = keySize;
	}
	public int getKeyMaterialLength() {
		return keyMaterialLength;
	}
	public void setKeyMaterialLength(int keyMaterialLength) {
		this.keyMaterialLength = keyMaterialLength;
	}
	public boolean isExportable() {
		return isExportable;
	}
	public void setExportable(boolean isExportable) {
		this.isExportable = isExportable;
	}
	public void reset(){
		//cipher=0;
		keySize=0;
		keyMaterialLength=0;
		isExportable=false;
	}
	public ICipherProvider getCipherProvider() {
		return cipherProvider;
	}
	public void setCipherProvider(ICipherProvider cipherProvider) {
		this.cipherProvider = cipherProvider;
	}
}
