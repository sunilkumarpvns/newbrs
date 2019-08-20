package com.elitecore.coreeap.packet.types.tls.record.cipher;

import com.elitecore.coreeap.packet.types.tls.record.ITLSRecord;

public abstract class BaseGenericCipher implements IGenericCipher {

	private ITLSRecord content;
	private byte[] MAC;

	public ITLSRecord getContent() {
		return content;
	}
	public void setContent(ITLSRecord content) {
		this.content = content;
	}
	public byte[] getMAC() {
		return MAC;
	}
	public void setMAC(byte[] mac) {
		MAC = mac;
	}
	
}
