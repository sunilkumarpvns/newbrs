package com.elitecore.coreradius.util.digest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.util.Arrays;
import java.util.Random;

import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;

public class DigestUtil {

	public static String generateDigestNonce(int nonceLength) {
		byte[] digestNoncebytes = new byte[nonceLength];
		Random random = new Random();
		random.nextBytes(digestNoncebytes);
		String nonceInHex = bytesToHex(digestNoncebytes);
		String digestNonce = null;
		try{
			digestNonce = new String(digestNoncebytes,CommonConstants.UTF8);
		}catch(UnsupportedEncodingException e){
			digestNonce = new String(digestNoncebytes);
		}
		digestNonce = nonceInHex;
		return digestNonce;
	}

	public static byte[] getHA1(String username,String realm,String password,String digestAlgorithm,String nonce,String cNonce) throws IOException{
		byte[] hA1=null;
		
			MessageDigest md5MessageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
			md5MessageDigest.update(stringToBytes_8859(username));
            md5MessageDigest.update(stringToBytes_8859(":"));
            md5MessageDigest.update(stringToBytes_8859(realm));
            md5MessageDigest.update(stringToBytes_8859(":"));
            md5MessageDigest.update(stringToBytes_8859(password));
            hA1 = md5MessageDigest.digest();
            if(digestAlgorithm != null && !digestAlgorithm.equalsIgnoreCase("MD5")){
            	String nonce1 = nonce;
            	String cNonce1 = cNonce;
            	if(nonce1 == null)
            		nonce1 = "";
            	if(cNonce1 == null)
            		cNonce1 = "";
            	if(digestAlgorithm.equalsIgnoreCase("MD5-sess")){
				md5MessageDigest.reset();
				md5MessageDigest.update(DigestUtil.stringToBytes_8859(DigestUtil.bytesToHex(hA1)));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));					
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(nonce1));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(cNonce1));	            
            	}else if(!digestAlgorithm.equalsIgnoreCase("AKAv1-MD5")){		        
					if(digestAlgorithm.equalsIgnoreCase("AKAv1-MD5-sess")){
//						String nonce = (String)this.digestSession.getParameter(DigestConstants.DIGEST_NONCE);
						md5MessageDigest.reset();
						md5MessageDigest.update(DigestUtil.stringToBytes_8859(DigestUtil.bytesToHex(hA1)));
			            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));					
			            md5MessageDigest.update(DigestUtil.stringToBytes_8859(nonce1));
			            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
			            md5MessageDigest.update(DigestUtil.stringToBytes_8859(cNonce1));		            
		            }else{
		                throw new IOException("Unsupported algorithm: " + digestAlgorithm);
		            }
		        }
            	hA1 = md5MessageDigest.digest();
            }
		return(hA1);
	}

	public static byte[] getDigest(String ha1Hex,String strQoP,String strDigestMethod,String strDigestURI,String strNonceCount,String strNonce,String strCNonce,String strDigestEntityBodyHash )throws IOException{
	    byte digest[] = new byte[16];	    
	    String ha1HexToUse = ha1Hex;
	    String strDigestMethodToUse = strDigestMethod;
	    String strDigestURIToUse = strDigestURI;
	    String strNonceCountToUse = strNonceCount;
	    String strNonceToUse = strNonce;
	    String strCNonceToUse = strCNonce;
	    String strDigestEntityBodyHashToUse = strDigestEntityBodyHash;
			if(ha1Hex == null)
				ha1HexToUse = "";
			if( strDigestMethod == null)
				strDigestMethodToUse = "";
			if( strDigestURI == null)
				strDigestURIToUse = "";
			if(strNonceCount == null)
				strNonceCountToUse = "";
			if(strNonce == null)
				strNonceToUse = "";
			if(strCNonce == null)
				strCNonceToUse = "";
			if(strDigestEntityBodyHash == null)
				strDigestEntityBodyHashToUse = "";
				
			MessageDigest md5MessageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);
		    byte ha2[];	
		    if(strQoP != null && strQoP.equals("auth")){
		    	md5MessageDigest.reset();
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strDigestMethodToUse));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));	            	
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strDigestURIToUse));
		        ha2 = md5MessageDigest.digest();
		    } else if(strQoP != null && strQoP.equalsIgnoreCase("auth-int")){
		    	md5MessageDigest.reset();
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strDigestMethodToUse));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));	            	            	
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strDigestURIToUse));	            
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strDigestEntityBodyHashToUse));		    	
		        ha2 = md5MessageDigest.digest();
		    } else if(strQoP == null){
		    	/***
		    	 * Through the IOT with NextGE, we found that QoP is not mandatory so if it is not
		    	 * passed in the request, then we have to consider defualt one.
		    	 */
		    	md5MessageDigest.reset();
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strDigestMethodToUse));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));	            	
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strDigestURIToUse));
		        ha2 = md5MessageDigest.digest();	    	
		    	
		    }else{
		    	throw new IOException((new StringBuilder()).append("Unsupported Qop: ").append(strQoP).toString());		    	
		    }
		    
		    String ha2Hex = DigestUtil.bytesToHex(ha2);
		    md5MessageDigest.reset();
            md5MessageDigest.update(DigestUtil.stringToBytes_8859(ha1HexToUse));
            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strNonceToUse));	    	
            
		    if(strQoP != null){
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strNonceCountToUse));	    	

	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strCNonceToUse));	    	

	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strQoP));	    	
		    }
		    md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
            md5MessageDigest.update(DigestUtil.stringToBytes_8859(ha2Hex));   		    
		    
		    digest =  md5MessageDigest.digest();	
		return(digest);
    }

    public static byte[] getResponseAuth(String ha1Hex,String strQoP,String strUri, String strEntityBodyHash,String strNonceCount,String strCNonce,String strNonce ) throws IOException
	{
	    byte authResponse[] = new byte[16];
	    String strUri1 = strUri;
	    if(strUri == null){
	    	strUri = "";
	    }
	    //String nonce = (String)this.digestSession.getParameter(DigestConstants.DIGEST_NONCE);
	    if(ha1Hex == null)
	        throw new IOException("Call getDigest() first");
	    	MessageDigest md5MessageDigest = RadiusUtility.getMessageDigest(CommonConstants.MD5);					    
		    
	    	byte ha2[]= null;
		    if(strQoP == null || strQoP.equalsIgnoreCase("auth")){
		    	md5MessageDigest.reset();
		    	md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
		    	md5MessageDigest.update(DigestUtil.stringToBytes_8859(strUri1));
		        ha2 = md5MessageDigest.digest();
		    } else if(strQoP != null && strQoP.equalsIgnoreCase("auth-int")){
		    	md5MessageDigest.reset();
		    	md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
		    	md5MessageDigest.update(DigestUtil.stringToBytes_8859(strUri1));
		        md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
	            md5MessageDigest.update(DigestUtil.stringToBytes_8859(strEntityBodyHash));
		        ha2 = md5MessageDigest.digest();
		    } else if(strQoP != null){
		        throw new IOException((new StringBuilder()).append("Unsupported Qop: ").append(strQoP).toString());
		    }
		    String ha2Hex = DigestUtil.bytesToHex(ha2);
		    md5MessageDigest.reset();
		    md5MessageDigest.update(DigestUtil.stringToBytes_8859(ha1Hex));
	    	md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));		    
	    	md5MessageDigest.update(DigestUtil.stringToBytes_8859(strNonce));
		    
		    if(strQoP != null && strQoP.length() > 0){
			    md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));		    
			    md5MessageDigest.update(DigestUtil.stringToBytes_8859(strNonceCount));
			    md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));		    
			    md5MessageDigest.update(DigestUtil.stringToBytes_8859(strCNonce));
			    md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));		    
			    md5MessageDigest.update(DigestUtil.stringToBytes_8859(strQoP));
		    }
		    
		    md5MessageDigest.update(DigestUtil.stringToBytes_8859(":"));
		    md5MessageDigest.update(DigestUtil.stringToBytes_8859(ha2Hex));		    
		    authResponse = 	md5MessageDigest.digest();
		return(authResponse);
	}

    public static String bytesToHex(byte buf[]){
        int length = buf.length;
        StringBuilder hexbuf = new StringBuilder(length << 1);
        for(int i = 0; i < length; i++){
            hexbuf.append(LHEX[buf[i] >> 4 & 0xf]);
            hexbuf.append(LHEX[buf[i] & 0xf]);
        }

        return hexbuf.toString();
    }
    private static final char LHEX[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'a', 'b', 'c', 'd', 'e', 'f'
    };
    
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
	/***
	 * appends bytes of newArrayBytes to the oldArrayBytes. 
	 * if the oldArrayBytes is null, then it will assign newArrayBytes to the OldArrayBytes
	 * if the newArrayBytes is null, then it will do nothing, simply return OldArrayBytes 
	 * @param oldArrayBytes
	 * @param newArrayBytes
	 * @return
	 */
	public static byte[] appendBytes(byte[] oldArrayBytes,byte[] newArrayBytes){
		if(oldArrayBytes == null)
			return(newArrayBytes);
		
		byte[] tempArrayBytes = oldArrayBytes;
		if(newArrayBytes != null){
			tempArrayBytes = new byte[oldArrayBytes.length + newArrayBytes.length];
			System.arraycopy(oldArrayBytes,0,tempArrayBytes,0,oldArrayBytes.length);
			System.arraycopy(newArrayBytes,0,tempArrayBytes,oldArrayBytes.length,newArrayBytes.length);
		}
		return(tempArrayBytes);
	}

	public static byte[] stringToBytes_8859(String str){
		byte[]  buffer = new byte[str.length()];		
		for(int iCounter =0 ;iCounter < str.length();iCounter ++){
			buffer[iCounter] = (byte)str.charAt(iCounter);
		}
		return(buffer);
	}

}
