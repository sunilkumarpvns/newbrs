package com.elitecore.coreeap.packet.types.sim.attributes.types;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.packet.types.sim.attributes.BaseSIMAttribute;
import com.elitecore.coreeap.util.constants.sim.attribute.SIMAttributeTypeConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AtEncrData extends BaseSIMAttribute {

	public AtEncrData() {
		// TODO Auto-generated constructor stub
		super(SIMAttributeTypeConstants.AT_ENCR_DATA.Id);
	}
	
	public byte[] getEncrData(){
		return getValueBytes();
	}
	
	@Override
	public String getStringValue() {
		return "[ Encrypted Data = " + TLSUtility.bytesToHex(getEncrData())+"]";
	}
	
	public void encryptAndSetBytesForEncrData(byte[] k_encr, AtIV atIV, byte[] value){
		try {
			SecretKey secretKey = new SecretKeySpec(k_encr, "AES");
			Cipher cipher = Cipher.getInstance("AES/CBC/NoPadding");
			cipher.init(Cipher.ENCRYPT_MODE,secretKey,new IvParameterSpec(atIV.getInitializationVector()));
		
			if ((value.length % 16) != 0){
				AtPadding padding = new AtPadding();
				byte[] tempBytes = new byte[(16 - (value.length % 16))-4];
				for (int i=0 ; i<tempBytes.length ; i++){
					tempBytes[0] = 0;
}
				padding.setValueBytes(tempBytes);
				setValueBytes( cipher.doFinal(TLSUtility.appendBytes(value,padding.getBytes())));
				return;
			}
			setValueBytes(cipher.doFinal(value));
		} catch (Exception e){
			if (LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
				LogManager.getLogger().trace("AT_ENCR_DATA", "Function: EncryptBytesForEncrData. Exception: " + e.getClass().getName());
				LogManager.getLogger().trace("AT_ENCR_DATA", "Function: EncryptBytesForEncrData. Message: " + e.getMessage());
				e.printStackTrace();
			}
		}
	}
}
