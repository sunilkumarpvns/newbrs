package com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.handlers;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.tls.HashAlgorithm;

public class MSCHAP2Handler {
	public static final String MODULE = "MSCHAP2 HANDLER";
    public static byte[] getMsChap2Response(int chapId, byte peerChallenge[], byte challenge[], String username, String password)
    {
        byte msChap2Response[] = new byte[50];
        msChap2Response[0] = (byte)chapId;
        System.arraycopy(peerChallenge, 0, msChap2Response, 2, 16);                           
        

			MessageDigest sha1MessageDigest = Utility.getMessageDigest(HashAlgorithm.SHA1.getIdentifier());
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
				LogManager.getLogger().trace(MODULE, "Peer Challenge : " + bytesToHex(peerChallenge) + " Length : " + peerChallenge.length);
				LogManager.getLogger().trace(MODULE, "Challenge : " + bytesToHex(challenge) + " Length : " + challenge.length);
				LogManager.getLogger().trace(MODULE, "username : " + username);
			}
			sha1MessageDigest.update(peerChallenge);
			sha1MessageDigest.update(challenge);
			sha1MessageDigest.update(stringToLatin1Bytes(username));
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "User name in Latin1 Bytes: " + bytesToHex(stringToLatin1Bytes(username)));
			byte challengeHash[]  = sha1MessageDigest.digest();
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "Challenge hash generated: " + bytesToHex(challengeHash));
			ntChallengeReponse(challengeHash, password, msChap2Response, 26);
			
		return(msChap2Response);
    }

    public static byte[] stringToLatin1Bytes(String s)
    {
        if(s == null)
            return null;
        else
            return stringToLatin1Bytes(s, 0, s.length());
    }
    
    public static byte[] stringToLatin1Bytes(String s, int offset, int length)
    {
        byte bytes[] = new byte[length];
        stringToLatin1Bytes(s, offset, length + offset, bytes, 0);
        return bytes;
    }

    public static int stringToLatin1Bytes(String string, int sOffset, int sLimit, byte bytes[], int bOffset)
    {
        for(int i = sOffset; i < sLimit; i++)
        {
            char ch = string.charAt(i);
            if(ch > '\377')
                throw new IllegalArgumentException((new StringBuilder()).append("Non-Latin1 character in string: ").append(quoteString(string.substring(sOffset, sLimit), true)).toString());
            bytes[bOffset++] = (byte)ch;
        }

        return sLimit - sOffset;
    }
    
    public static String quoteString(String in, boolean forceQuotes)
    {
        if(in == null)
            return "null";
        if(!forceQuotes)
        {
            boolean ok = true;
            int i = 0;
            do
            {
                if(i >= in.length())
                    break;
                char c = in.charAt(i);
                String mask = i != 0 ? "_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789" : "_ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz";
                if(mask.lastIndexOf(c) < 0)
                {
                    ok = false;
                    break;
                }
                i++;
            } while(true);
            if(ok && in.length() > 0)
                return in;
        }
        return (new StringBuilder()).append('"').append(escapeString(in)).append('"').toString();
    }
    
    public static String escapeString(String in)
    {
        return escapeString(in, null);
    }

    public static String escapeString(String in, String extra)
    {
        StringBuilder buf = new StringBuilder();
        for(int i = 0; i < in.length(); i++)
        {
            int c = in.charAt(i);
            switch(c)
            {
            case 7: // '\007'
                buf.append("\\a");
                break;

            case 8: // '\b'
                buf.append("\\b");
                break;

            case 12: // '\f'
                buf.append("\\f");
                break;

            case 10: // '\n'
                buf.append("\\n");
                break;

            case 13: // '\r'
                buf.append("\\r");
                break;

            case 9: // '\t'
                buf.append("\\t");
                break;

            case 11: // '\013'
                buf.append("\\v");
                break;

            case 34: // '"'
                buf.append("\\\"");
                break;

            case 92: // '\\'
                buf.append("\\\\");
                break;

            default:
                if(c >= 0 && c <= 31 || c >= 127 && c <= 255)
                {
                    buf.append('\\');
                    String octal = Integer.toOctalString(c);
                    for(int count = 3 - octal.length(); count-- > 0;){
                        buf.append('0');
                    }
                    buf.append(octal);
                    break;
                }
                if(c > 255)
                {
                    buf.append("\\u");
                    String hex = Integer.toHexString(c);
                    for(int count = 4 - hex.length(); count-- > 0;){
                        buf.append('0');
                    }
                    buf.append(hex);
                    break;
                }
                if(extra != null && extra.indexOf(c) != -1)
                    buf.append('\\').append((char)c);
                else
                    buf.append((char)c);
                break;
            }
        }

        return buf.toString();
    }
    public static void ntChallengeReponse(byte challenge[], String password, byte msChapResponse[], int offset)
    {
        try
        {
            byte zpPasswdHash[] = new byte[21];
            byte bytes[] = password.getBytes("UTF-16LE");
            MD4.getDigest(bytes, 0, bytes.length, zpPasswdHash, 0);
            challengeReponse(challenge, zpPasswdHash, msChapResponse, offset);
        }
        catch(UnsupportedEncodingException e)
        {
            throw new RuntimeException(e);
        }
    }
    
    public static void challengeReponse(byte challenge[], byte zpPasswdHash[], byte msChapResponse[], int offset)
    {
        byte key56[] = new byte[7];
        byte key64[] = new byte[8];
        int pkey[] = new int[32];
        System.arraycopy(zpPasswdHash, 0, key56, 0, 7);
        DES.addParity(key56, key64);
        DES.permuteKey(key64, pkey);
        DES.encrypt(challenge, 0, msChapResponse, offset, 8, pkey);
        System.arraycopy(zpPasswdHash, 7, key56, 0, 7);
        DES.addParity(key56, key64);
        DES.permuteKey(key64, pkey);
        DES.encrypt(challenge, 0, msChapResponse, offset + 8, 8, pkey);
        System.arraycopy(zpPasswdHash, 14, key56, 0, 7);
        DES.addParity(key56, key64);
        DES.permuteKey(key64, pkey);
        DES.encrypt(challenge, 0, msChapResponse, offset + 16, 8, pkey);
    }

    public static String getMsChap2SuccessValue(byte msChap2Response[], byte challengeHash[], byte passwordHashHash[])
    {
        byte authResp[] = getAuthenticatorResponse(msChap2Response, 26, challengeHash, passwordHashHash);
//        return (new StringBuilder()).append((char)(msChap2Response[0] & 0xff)).append("S=").append(TLSUtility.bytesToHex(authResp)).toString();
//        return (new StringBuilder()).append((char)(msChap2Response[0] & 0xff)).append("S=").append(MSCHAP2Handler.bytesToHex(authResp)).toString();
        return (new StringBuilder()).append("S=").append(bytesToHex(authResp)).toString();        
    }

    public static byte[] getAuthenticatorResponse(byte data[], int offset, byte challengeHash[], byte passwordHashHash[])
    {
        final byte Magic1[] = {
            77, 97, 103, 105, 99, 32, 115, 101, 114, 118, 
            101, 114, 32, 116, 111, 32, 99, 108, 105, 101, 
            110, 116, 32, 115, 105, 103, 110, 105, 110, 103, 
            32, 99, 111, 110, 115, 116, 97, 110, 116
        };
        final byte Magic2[] = {
            80, 97, 100, 32, 116, 111, 32, 109, 97, 107, 
            101, 32, 105, 116, 32, 100, 111, 32, 109, 111, 
            114, 101, 32, 116, 104, 97, 110, 32, 111, 110, 
            101, 32, 105, 116, 101, 114, 97, 116, 105, 111, 
            110
        };
        
        byte[] resultBytes = null;

			MessageDigest sha1MessageDigest = Utility.getMessageDigest(HashAlgorithm.SHA1.getIdentifier());

			sha1MessageDigest.update(passwordHashHash);
			sha1MessageDigest.update(data, offset, 24);
			sha1MessageDigest.update(Magic1);
			byte[] dataBytes = new byte[24];
			System.arraycopy(data, offset, dataBytes, 0, 24);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(MODULE, "NT Response : " + bytesToHex(dataBytes));
			resultBytes = sha1MessageDigest.digest();

			sha1MessageDigest = Utility.getMessageDigest(HashAlgorithm.SHA1.getIdentifier());			
			
			sha1MessageDigest.update(resultBytes);
			sha1MessageDigest.update(challengeHash, 0, 8);
			sha1MessageDigest.update(Magic2);

			resultBytes = sha1MessageDigest.digest();
		return(resultBytes);
    }  

    public static String bytesToHex(byte buf[])
    {
        if(buf == null)
            return "";
        else
            return bytesToHex(buf, 0, buf.length);
    }
    
    public static String bytesToHex(byte buf[], int offset, int limit)
    {
        StringBuilder hexbuf = new StringBuilder((limit - offset) * 2);
        for(int i = offset; i < limit; i++)
        {
            hexbuf.append(HEX[buf[i] >> 4 & 0xf]);
            hexbuf.append(HEX[buf[i] & 0xf]);
        }

        return hexbuf.toString();
    }

    private static final char HEX[] = {
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F'
    };
}

