package com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers;


public class MSCHAPv1Handler {

	public static final String MODULE = "MsChapv1 Handler";
	public static byte[] getMsChapResponse(byte[] password, byte[] challenge,byte chapId){
		
		byte[] msChapv1Response = new byte[50];
		byte[] lmResponse = new byte[24]; //lmChallengeResponse has been deprecated; peers SHOULD NOT generate it, and sub-field SHOULD be zero-filled.
		byte[] ntResponse = ntChallengeResponse(challenge, convertToUnicode(password));
		System.arraycopy(lmResponse, 0, msChapv1Response, 2, 24);
		System.arraycopy(ntResponse, 0, msChapv1Response, 26, 24);
		msChapv1Response[0] = chapId;
		msChapv1Response[1] = 0x01; /* Since the use of LAN Manager authentication has been deprecated, 
	        						 this flag SHOULD always be set (1) and the LAN Manager
	        						 compatible challenge response field SHOULD be zero-filled.*/
		return msChapv1Response;
	}

	public static byte[] convertToUnicode(byte[] inputBytes){
		byte unicodeBytes[] = null;
		if(inputBytes!=null && inputBytes.length > 0){
			unicodeBytes= new byte[inputBytes.length * 2];
			for (int i = 0; i < inputBytes.length; i++){
				unicodeBytes[2*i] = inputBytes[i];
			}
		}
		return unicodeBytes;
	}

	private static byte[] ntPasswordHash(byte[] password){
		byte passwordHash[] = new byte[16];
		MD4.getDigest(password, 0, password.length, passwordHash,0);
		return passwordHash;
	}

	private static byte[] challengeResponse(final byte[] challenge, final byte[] passwordHash){
		byte response[] = new byte[24];
		byte zPasswordHash[] = new byte[21];
		byte[] key = new byte[7];
		byte[] tmpResp = new byte[8];

		System.arraycopy(passwordHash, 0, zPasswordHash, 0, 16);
		
		System.arraycopy(response, 0, tmpResp, 0,8);
		System.arraycopy(zPasswordHash, 0, key, 0, 7);
		desEncrypt(challenge, key,tmpResp);
		System.arraycopy(tmpResp, 0, response, 0, 8);

		System.arraycopy(response,8, tmpResp,0,8);
		System.arraycopy(zPasswordHash, 7, key, 0, 7);
		desEncrypt(challenge, key,tmpResp);
		System.arraycopy(tmpResp, 0, response,8, 8);

		System.arraycopy(response, 16, tmpResp, 0,8);
		System.arraycopy(zPasswordHash, 14, key, 0, 7);
		desEncrypt(challenge, key,tmpResp);
		System.arraycopy(tmpResp, 0, response,16, 8);

		return response;
	}

	private static byte[] ntChallengeResponse(byte[] challenge, byte[] password){
		byte[] passwordHash = ntPasswordHash(password);
		return challengeResponse(challenge, passwordHash);
	}

	private static void desEncrypt(byte[] clear, byte[] key, byte[] cypher){
		byte key64[] = new byte[8];
		int pkey[] = new int[32];

		DES.addParity(key, key64);
		DES.permuteKey(key64, pkey);
		DES.encrypt(clear, 0, cypher, 0, 8, pkey);
	}

/*	public static void DesHash(byte[] clear, byte[] cypher){
		String stdText = "KGS!@#$%";
		desEncrypt(stdText.getBytes(),clear, cypher);
	}*/

}

