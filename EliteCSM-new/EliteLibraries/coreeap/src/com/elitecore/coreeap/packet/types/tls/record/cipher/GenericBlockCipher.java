package com.elitecore.coreeap.packet.types.tls.record.cipher;


public class GenericBlockCipher extends BaseGenericCipher {

	private byte[] padding;
	private int paddingLength;
	
	public byte[] getPadding() {
		return padding;
	}
	public void setPadding(byte[] padding) {
		this.padding = padding;
	}
	public int getPaddingLength() {
		return paddingLength;
	}
	public void setPaddingLength(int paddingLength) {
		this.paddingLength = paddingLength;
	}

}
