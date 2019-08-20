package com.elitecore.coreradius.util.wimax;

import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.util.eap.tls.TLSUtility;


public class WiMAXUtility {

	static final int MSK_LENGTH = 64;
	static final int EMSK_LENGTH = 64;
	static final int HA_RK_KEY_LENGTH = 20;
	static final int DHCP_RK_KEY_LENGTH = 20;
	static final int MN_HA_MIP4_KEY_LENGTH = 20;
	static final int FA_RK_KEY_LENGTH = 20;
	static final int MIP_KEY_LENGTH = 64;
	static final String PMIP4_MN_HA_LABEL = "PMIP4 MN HA";
	static final String CMIP4_MN_HA_LABEL = "CMIP4 MN HA";
	static final int MIP_SPI_LENGTH = 4;
	static final String FA_RK_LABEL = "FA-RK";
	
	//	MSK(0,63) = TLS-PRF-64(master secret + "client EAP encryption" + random)
	public static byte[] generateMSK(byte[] masterSecret, String label, byte[] clientRandom, byte[] serverRandom){
		byte[] MSK = new byte[MSK_LENGTH];
		
		byte[] random = new byte[clientRandom.length + serverRandom.length];
		
		System.arraycopy(clientRandom, 0, random, 0	, clientRandom.length);
		System.arraycopy(serverRandom, 0, random, clientRandom.length, serverRandom.length);
		try{
			MSK = TLSUtility.TLSPRF(masterSecret, label.getBytes(CommonConstants.UTF8), random, MSK_LENGTH);
		}catch(UnsupportedEncodingException e){
			MSK = TLSUtility.TLSPRF(masterSecret, label.getBytes(), random, MSK_LENGTH);
		}
		
		return MSK;
	}
		
	//	EMSK(0,63) = second 64 octets of: TLS-PRF-128(master secret, "client EAP encryption",random)
	public static byte[] generateEMSK(byte[] masterSecret, String label, byte[] clientRandom, byte[] serverRandom){
		byte[] EMSK = new byte[EMSK_LENGTH];
		
		byte[] random = new byte[clientRandom.length + serverRandom.length];
		
		System.arraycopy(clientRandom, 0, random, 0	, clientRandom.length);
		System.arraycopy(serverRandom, 0, random, clientRandom.length, serverRandom.length);
		
		byte[] tempEMSK = null;
		try{
			tempEMSK = TLSUtility.TLSPRF(masterSecret, label.getBytes(CommonConstants.UTF8), random, EMSK_LENGTH * 2);
		}catch(UnsupportedEncodingException e){
			tempEMSK = TLSUtility.TLSPRF(masterSecret, label.getBytes(), random, EMSK_LENGTH * 2);		
		}
		
		System.arraycopy(tempEMSK, EMSK_LENGTH, EMSK, 0, EMSK_LENGTH);
		
		return EMSK;
	}
	
	//	160-bit random HA-RK-KEY
	public static byte[] generateHA_RK_KEY(){
		byte[] HA_RK_KEY = new byte[HA_RK_KEY_LENGTH];
		
		Random rand = new Random();
		rand.nextBytes(HA_RK_KEY);
				
		return HA_RK_KEY;
	}
	
	//	32-bit HA-RK-SPI
	public static int generateHA_RK_SPI(){
		int HA_RK_SPI =0;		
		Random rand = new Random();
		HA_RK_SPI =  Math.abs(rand.nextInt());	
		
		return HA_RK_SPI;
	}
	
	/**	MIP-RK = MIP-RK-1 | MIP-RK-2 
		MIP-RK-1 = HMAC-SHA256(EMSK , usage-data | 0x01)
		MIP-RK-2 = HMAC-SHA256(EMSK, MIP-RK-1 | usage data | 0x02)
			usage-data = key label + "\0" + length 
				key label = miprk@wimaxforum.org in ASCII 
				length = 0x0200 the length in bits of the MIP-RK expressed as a 2 byte unsigned integer in network order.
								
	*/	
	public static byte[] generateMIP_RK_Key(byte[] EMSK) {
		byte[] MIP_RK = new byte[MIP_KEY_LENGTH];
		
		byte[] MIP_RK_1 = new byte[MIP_KEY_LENGTH/2];
		byte[] MIP_RK_2 = new byte[MIP_KEY_LENGTH/2];
						
		String keyLabel = "miprk@wimaxforum.org" + "\0";		
		int keyLength = 512;
		byte[] keyLabelBytes = null;
		try{
			keyLabelBytes = keyLabel.getBytes(CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			keyLabelBytes = keyLabel.getBytes();
		}
		byte[] usageData = new byte[keyLabelBytes.length + 2];
				
		System.arraycopy(keyLabelBytes, 0, usageData, 0, keyLabelBytes.length);
		usageData[keyLabelBytes.length + 1] = (byte)(keyLength);
		usageData[keyLabelBytes.length] = (byte)(keyLength >>> 8);
						
		byte[] keyMaterial1 = new byte[usageData.length + 1];
		System.arraycopy(usageData, 0, keyMaterial1, 0, usageData.length);
		keyMaterial1[usageData.length] = (byte)1;
		
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKey secretKey = new SecretKeySpec(EMSK, "sha256");
			sha256_HMAC.init(secretKey);
			MIP_RK_1 = sha256_HMAC.doFinal(keyMaterial1);			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		byte[] keyMaterial2 = new byte[MIP_RK_1.length + usageData.length + 1];
		System.arraycopy(MIP_RK_1, 0, keyMaterial2, 0, MIP_RK_1.length);
		System.arraycopy(usageData, 0, keyMaterial2, MIP_RK_1.length, usageData.length);
		keyMaterial2[MIP_RK_1.length + usageData.length] = (byte)2;
		
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKey secretKey = new SecretKeySpec(EMSK, "sha256");
			sha256_HMAC.init(secretKey);
			MIP_RK_2 = sha256_HMAC.doFinal(keyMaterial2);			
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
				
		System.arraycopy(MIP_RK_1, 0, MIP_RK, 0, MIP_RK_1.length);
		System.arraycopy(MIP_RK_2, 0, MIP_RK, MIP_RK_1.length, MIP_RK_2.length);
		
		return MIP_RK;
	}
	
	/**	H(MIP-RK,"PMIP4 MN HA" | HA-IPv4 | MN-NAI)
		H = HMAC-SHA1
		sharedSecret = MIP-RK
		HA-IPv4 is IPv4 address of HA sending this request.
		MN-NAI is the identity received in EAP-response-identity
				
	*/
	//	------------------------ between ASN-GW and HAAA ------------------------
	public static byte[] generateMN_HA_PMIP4_KEY(byte[] MIP_RK, String MN_NAI, byte[] HA_IPv4){
		byte[] MN_HA_PMIP4_KEY = new byte[MN_HA_MIP4_KEY_LENGTH];
						
		byte[] PMIP4_MN_HA_LABELBytes = null;
		try{
			PMIP4_MN_HA_LABELBytes = PMIP4_MN_HA_LABEL.getBytes(CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			PMIP4_MN_HA_LABELBytes = PMIP4_MN_HA_LABEL.getBytes();
		}
		byte[] MN_NAIBytes = null;
		try{
			MN_NAIBytes = MN_NAI.getBytes(CommonConstants.UTF8); 
		}catch(UnsupportedEncodingException e){
			MN_NAIBytes = MN_NAI.getBytes();
		}
		byte[] keyMaterial = new byte[PMIP4_MN_HA_LABELBytes.length + 4 + MN_NAIBytes.length];
//		byte[] HA_IPv4 = new byte[4];
//		Arrays.fill(HA_IPv4, (byte)0);
				
		System.arraycopy(PMIP4_MN_HA_LABELBytes, 0, keyMaterial, 0, PMIP4_MN_HA_LABELBytes.length);
		System.arraycopy(HA_IPv4, 0, keyMaterial, PMIP4_MN_HA_LABELBytes.length, HA_IPv4.length);
		System.arraycopy(MN_NAIBytes, 0, keyMaterial, PMIP4_MN_HA_LABELBytes.length + 4, MN_NAIBytes.length);
		
		MN_HA_PMIP4_KEY = TLSUtility.HMAC(CommonConstants.SHA1, keyMaterial, MIP_RK);
		
		return MN_HA_PMIP4_KEY;
	}
	
	// ------------ between HA and HAAA ------------------
	public static byte[] generateMN_HA_CMIP4_KEY(byte[] MIP_RK, String MN_NAI){
		byte[] MN_HA_CMIP4_KEY = new byte[MN_HA_MIP4_KEY_LENGTH];
		byte[] CMIP4_MN_HA_LABELBytes = null;
		try{
			CMIP4_MN_HA_LABELBytes = CMIP4_MN_HA_LABEL.getBytes(CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			CMIP4_MN_HA_LABELBytes = CMIP4_MN_HA_LABEL.getBytes();
		}
		byte[] MN_NAIBytes = null;
		try{
			MN_NAIBytes = MN_NAI.getBytes(CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			MN_NAIBytes = MN_NAI.getBytes();
		}
		byte[] keyMaterial = new byte[CMIP4_MN_HA_LABELBytes.length + 4 + MN_NAIBytes.length];
		byte[] HA_IPv4 = new byte[4];
		Arrays.fill(HA_IPv4, (byte)0);
				
		System.arraycopy(CMIP4_MN_HA_LABELBytes, 0, keyMaterial, 0, CMIP4_MN_HA_LABELBytes.length);
		System.arraycopy(HA_IPv4, 0, keyMaterial, CMIP4_MN_HA_LABELBytes.length, HA_IPv4.length);
		System.arraycopy(MN_NAIBytes, 0, keyMaterial, CMIP4_MN_HA_LABELBytes.length + 4, MN_NAIBytes.length);
		
		MN_HA_CMIP4_KEY = TLSUtility.HMAC("sha-1", keyMaterial, MIP_RK);
		
		return MN_HA_CMIP4_KEY;
	}
	
	public static byte[] generateFA_RK_KEY(byte[] MIP_RK){
		byte[] FA_RK_KEY = null;
		try{
			FA_RK_KEY = TLSUtility.HMAC("sha-1", FA_RK_LABEL.getBytes(CommonConstants.UTF8), MIP_RK);
		}catch(UnsupportedEncodingException e){
			FA_RK_KEY = TLSUtility.HMAC("sha-1", FA_RK_LABEL.getBytes(), MIP_RK);
		}
		return FA_RK_KEY;
	}
	
	//	------------------------ between ASN-GW and HAAA ------------------------
	public static long generateMN_HA_PMIP_SPI(byte[] MIP_RK){
		long MN_HA_PMIP_SPI = 0;
		
		MN_HA_PMIP_SPI = generateMIP_SPI(MIP_RK) + 1;
		return MN_HA_PMIP_SPI;
	}

	//	 ------------ between HA and HAAA ------------------
	public static long generateMN_HA_CMIP_SPI(byte[] MIP_RK){
		long MN_HA_CMIP_SPI = 0;
		
		MN_HA_CMIP_SPI = generateMIP_SPI(MIP_RK);		
		return MN_HA_CMIP_SPI;
	}
	
	public static long generateMIP_SPI(byte[] MIP_RK) {
		long MIP_SPI;	//4 most significant bytes of HMAC-SHA256(MIP-RK | "SPI CMIP PMIP")
		byte[] spiBytes = null;
		byte[] hmacInput = new byte[MIP_RK.length + 13];
		System.arraycopy(MIP_RK, 0, hmacInput, 0, MIP_RK.length);
		String spiLabel = "SPI CMIP PMIP";
		byte[] spiLabelBytes = null;
		try{
			spiLabelBytes = spiLabel.getBytes(CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			spiLabelBytes = spiLabel.getBytes();
		}
		System.arraycopy(spiLabelBytes, 0, hmacInput, MIP_RK.length, spiLabelBytes.length);
		
		try {
			Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
			SecretKey secretKey = new SecretKeySpec(MIP_RK, "sha256");
			sha256_HMAC.init(secretKey);
			spiBytes = sha256_HMAC.doFinal(hmacInput);	
//			System.out.println("spi bytes: " + TLSUtility.bytesToHex(spiBytes));
		} catch (NoSuchAlgorithmException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (InvalidKeyException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		MIP_SPI = spiBytes[0] & 0xFF;
		MIP_SPI = MIP_SPI << 8;
		MIP_SPI = MIP_SPI | (spiBytes[1] & 0xFF);
		MIP_SPI = MIP_SPI << 16;
		MIP_SPI = MIP_SPI | (spiBytes[2] & 0xFF);
		MIP_SPI = MIP_SPI << 24;
		MIP_SPI = MIP_SPI | (spiBytes[3] & 0xFF);
		
//		System.out.println("mip-spi: " + MIP_SPI);
		
		while ((CommonConstants.UNSIGNED32_MAX_VAL - MIP_SPI) < 3) {
			MIP_SPI += 259;
			MIP_SPI = MIP_SPI & 0xFFFFFFFFL;
		}
		
		if (MIP_SPI < 256) {
			MIP_SPI += 256;
		}
		return MIP_SPI;		
	}
		
	public static byte[] encryptKeyRFC2868(byte[] key, String sharedSecret, byte[] requestAuthenticator, byte[] saltBytes){
		byte[] encrptedKey = null;		
		byte[] plainText = null;
						
		if((key.length+1) % 16 != 0){
			plainText = new byte[(((1 + key.length)/16)+1) * 16];		
			encrptedKey = new byte[plainText.length];
		}else{
			plainText = new byte[((1 + key.length)/16)*16];	
			encrptedKey = new byte[plainText.length];
		}		
		
		plainText[0] = (byte)key.length;
		System.arraycopy(key, 0, plainText, 1, key.length);
		if((key.length+1) % 16 != 0){
			Arrays.fill(plainText, key.length+1, plainText.length-1, (byte)0);
		}			
		
		byte[][] plainKeyBlocks = new byte[plainText.length/16][16];
		byte[][] intermediateValues = new byte[plainText.length/16][16];
		byte[][] cipherBlocks = new byte[plainText.length/16][16];
		
		int itr = plainText.length/16;
		MessageDigest msgDigest = null;
				
		for(int i=0; i<itr; i++){
			System.arraycopy(plainText, i*16, plainKeyBlocks[i], 0, 16);		
			//We know these exceptions will never hit
				msgDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
				try{
					msgDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
				}catch(UnsupportedEncodingException e){
					msgDigest.update(sharedSecret.getBytes());
				}
				if(i == 0){
					msgDigest.update(requestAuthenticator);
					msgDigest.update(saltBytes);
				}else{				
					msgDigest.update(cipherBlocks[i-1]);	
				}
				intermediateValues[i] = msgDigest.digest();
				for(int j=0; j<16; j++){
					cipherBlocks[i][j] = (byte)(plainKeyBlocks[i][j] ^ intermediateValues[i][j]);
				}
						
		}
		
		for(int i=0; i<itr; i++){
			System.arraycopy(cipherBlocks[i], 0, encrptedKey, i*16, cipherBlocks[i].length);
		}
		
		return encrptedKey;
	}		
	
	public static byte[] decryptKeyRFC2868(byte[] encKey, String sharedSecret, byte[] requestAuthenticator, byte[] saltBytes){
		byte[] plainText = null;
		
		byte[][] plainKeyBlocks = new byte[encKey.length/16][16];
		byte[][] intermediateValues = new byte[encKey.length/16][16];
		byte[][] cipherBlocks = new byte[encKey.length/16][16];
		
		int itr = encKey.length/16;
		MessageDigest msgDigest = null;
				
		for(int i=0; i<itr; i++){
			System.arraycopy(encKey, i*16, cipherBlocks[i], 0, 16);
				msgDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
				try{
					msgDigest.update(sharedSecret.getBytes(CommonConstants.UTF8));
				}catch(UnsupportedEncodingException e){
					msgDigest.update(sharedSecret.getBytes());
				}
				
				if(i == 0){				
					msgDigest.update(requestAuthenticator);
					msgDigest.update(saltBytes);																		
				}else{
					msgDigest.update(cipherBlocks[i-1]);																	
				}
				intermediateValues[i] = msgDigest.digest();
				for(int j=0; j<16; j++){
					plainKeyBlocks[i][j] = (byte)(cipherBlocks[i][j] ^ intermediateValues[i][j]);
				}
		}
		plainText = new byte[(encKey.length/16)*16];
		for(int i=0; i<itr; i++){
			System.arraycopy(plainKeyBlocks[i], 0, plainText, i*16, plainKeyBlocks[i].length);
		}
		
		byte[] decryptedKey = new byte[plainText[0]];
		
//		System.out.println("decrypted plain text: " + TLSUtility.bytesToHex(plainText));
		System.arraycopy(plainText, 1, decryptedKey, 0, plainText[0]);		
		return decryptedKey;
	}
	
	// generating DHCP_RK (160 -bit)
	public static byte[] generateDhcp_RK(){
		byte[] DHCP_RK_KEY = new byte[DHCP_RK_KEY_LENGTH];		
		Random rand = new Random();
		rand.nextBytes(DHCP_RK_KEY);			
		return DHCP_RK_KEY;
	}
	
//	32-bits DHCP_RK_KEY_ID
	public static int generateDHCP_RK_KEY_ID(){
		int DHCP_RK_KEY_ID = 0;
		Random rand = new Random();
		DHCP_RK_KEY_ID =  Math.abs(rand.nextInt());				
		return DHCP_RK_KEY_ID;
	}
}
