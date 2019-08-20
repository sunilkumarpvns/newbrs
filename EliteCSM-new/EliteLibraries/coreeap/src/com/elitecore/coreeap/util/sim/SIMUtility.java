package com.elitecore.coreeap.util.sim;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Random;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

import com.elitecore.coreeap.util.tls.TLSUtility;


public class SIMUtility {
	static int HMAC_SHARED_SECRET_MAX_LENGTH = 64;
	
	public static  byte[] HMAC(String hashFunction, byte[] dataToBeEncrypted,byte[] sharedSecret) {
		
		byte[] resultBytes = null;
		
//		if (hashFunction.equals("MD5")) {
//			resultBytes = new byte[HMAC_MD5_LENGTH]; // The final output result will be stored in this byte array			
//		} else if (hashFunction.equals("SHA-1")) {
//			resultBytes = new byte[HMAC_SHA_LENGTH]; // The final output result will be stored in this byte array			
//		}

		byte sharedSecretBytes[] = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // the shared secret bytes will be stored here
		byte[] ipad = new byte[HMAC_SHARED_SECRET_MAX_LENGTH];
		byte[] opad = new byte[HMAC_SHARED_SECRET_MAX_LENGTH];

		byte[] ipadXORSharedSecret = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // will contain the bytes after XORing ipad bytes with sharedSecret bytes 
		byte[] opadXORSharedSecret = new byte[HMAC_SHARED_SECRET_MAX_LENGTH]; // will contain the bytes after XORing opad bytes with sharedSecret bytes



			Arrays.fill(ipad, (byte) 0x36); // fills the inner pad byte array with 0x36 ( 54 in Decimal )
			Arrays.fill(opad, (byte) 0x5c); // fills the outer pad byte array with 0x5c ( 92 in Decimal )

			if (sharedSecret.length < HMAC_SHARED_SECRET_MAX_LENGTH) { // checks if the length of the sharedSecret is less than 64
				System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0,
						sharedSecret.length); // copies the bytes from shared secret in sharedSecretBytes
				Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00); // pads the rest of the bytes with 0x00 ( 0 in Decimal )

			/*} else if (sharedSecret.length > HMAC_SHARED_SECRET_MAX_LENGTH) { // checks if the length of the sharedSecret is more than 64
				md5MessageDigest.update(sharedSecret);// appply MD5 on the the sharedSecret to convert it into 16 bytes sharedSecret
				sharedSecretBytes = md5MessageDigest.digest();// store the result bytes in sharedSecretBytes
				// pads the rest of the bytes with 0x00
				/*Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);*/
				
				//printBytes("SharedSecret Bytes",sharedSecretBytes);*/
				
			} else if (sharedSecret.length > HMAC_SHARED_SECRET_MAX_LENGTH) { // checks if the length of the sharedSecret is more than 64				
				int[] iSharedSecret = new int[sharedSecret.length];
				System.arraycopy(sharedSecret, 0, iSharedSecret, 0, sharedSecret.length);
				SHA1 sha2 = new SHA1();
				sha2.init();
				sha2.transform(iSharedSecret);
				sharedSecret = sha2.SHA1FinalNoLength();				
				// pads the rest of the bytes with 0x00
				/*Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);*/
				System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0, sharedSecret.length);
				//printBytes("SharedSecret Bytes",sharedSecretBytes);
				Arrays.fill(sharedSecretBytes, sharedSecret.length,
						sharedSecretBytes.length - 1, (byte) 0x00);
			} else {
				System.arraycopy(sharedSecret, 0, sharedSecretBytes, 0,
						sharedSecret.length);
			}

			for (int i = 0; i < sharedSecretBytes.length; i++) {
				ipadXORSharedSecret[i] = (byte) ((sharedSecretBytes[i] & 0xFF) ^ (ipad[i] & 0xFF)); // XORing the sharedSecretBytes with the ipad bytes
				opadXORSharedSecret[i] = (byte) ((sharedSecretBytes[i] & 0xFF) ^ (opad[i] & 0xFF)); // XORing the sharedSecretBytes with the opad bytes
			}

			int[] iPadXORSharedSecret = new int[ipadXORSharedSecret.length];
			for(int i=0 ; i < ipadXORSharedSecret.length ; i++){
				iPadXORSharedSecret[i] = ipadXORSharedSecret[i];
			}			
			int[] oPadXORSharedSecret = new int[opadXORSharedSecret.length];
			for(int i=0 ; i < opadXORSharedSecret.length ; i++){
				oPadXORSharedSecret[i] = opadXORSharedSecret[i];
			}				
			
			int[] iDataToBeEncrypted = new int[dataToBeEncrypted.length];
			for(int i=0 ; i < dataToBeEncrypted.length ; i++){
				iDataToBeEncrypted[i] = dataToBeEncrypted[i];
			}			
			
			int[] phase1 = new int[iPadXORSharedSecret.length + dataToBeEncrypted.length];
			System.arraycopy(iPadXORSharedSecret, 0, phase1, 0, iPadXORSharedSecret.length);
			System.arraycopy(iDataToBeEncrypted, 0, phase1, iPadXORSharedSecret.length, iDataToBeEncrypted.length);
			
			SHA1 sha1 = new SHA1();
			sha1.init();
			sha1.transform(phase1);			
			byte[] tempResult = sha1.SHA1FinalNoLength(); // generating the encrypted value of the bytes set
			int[] phase2 = new int[oPadXORSharedSecret.length + tempResult.length];
			
			
			System.arraycopy(oPadXORSharedSecret, 0, phase2, 0, oPadXORSharedSecret.length);
			
			for(int i= 0; i < tempResult.length ; i++){
				phase2[i+ oPadXORSharedSecret.length] = tempResult[i];
			}		
			sha1.init();
			sha1.transform(phase2);
			resultBytes = sha1.SHA1FinalNoLength();

		return resultBytes;
	}

	
	public static byte[] generateRandom() {
		
		byte[] bServerRandom = new byte[16];
		byte[] randomNumber = new byte[12];
		long gmtTime;
		gmtTime = System.currentTimeMillis() / 1000;				
		bServerRandom[3] = (byte) gmtTime;
		gmtTime = gmtTime >>> 8;
		bServerRandom[2] = (byte) gmtTime;
		gmtTime = gmtTime >>> 8;
		bServerRandom[1] = (byte) gmtTime;
		gmtTime = gmtTime >>> 8;
		bServerRandom[0] = (byte) gmtTime;
		Random rand = new Random();
		rand.nextBytes(randomNumber);
		System.arraycopy(randomNumber, 0, bServerRandom, 4,12) ;
		return (bServerRandom);
	}

	public static byte[] generateRandom(int numberOfTriplets) {
		byte[] rands = null;
		for(int i =0 ; i < numberOfTriplets ; i++){
			rands = TLSUtility.appendBytes(rands, generateRandom());
		}
		return rands;
	}

	public static byte[] HMAC_SHA256(byte[] key, byte[] sBytes) throws NoSuchAlgorithmException, InvalidKeyException {
		Mac sha256_HMAC = Mac.getInstance("HmacSHA256");
		SecretKeySpec secret_key = new SecretKeySpec(key, "HmacSHA256");
		sha256_HMAC.init(secret_key);
		return sha256_HMAC.doFinal(sBytes);
	}
	
	public static byte[] prfPrime(byte[] ckik, byte[] identity, int outputLen) throws InvalidKeyException, NoSuchAlgorithmException, IOException {
		ByteArrayOutputStream masterKey  = new ByteArrayOutputStream(outputLen);
		
		byte[] t = null;
		byte cnt = 1;
		byte[] sBytes = TLSUtility.appendBytes(identity, new byte[] {cnt});
		t = HMAC_SHA256(ckik, sBytes);
		cnt++;
		masterKey.write(t);
		while(true) {
			byte[] t_s_bytes = TLSUtility.appendBytes(t, identity);
			sBytes = TLSUtility.appendBytes(t_s_bytes, new byte[] {cnt});
			t = HMAC_SHA256(ckik, sBytes);
			cnt++;
			if(masterKey.size() >= outputLen) {
				break;
			}
			masterKey.write(t);
		}
		return masterKey.toByteArray();
	}
	
	private static byte[] addBytes(byte[] a, byte[] b ){
		byte[] sum = new byte[20];
		int s ,carry=0;
		for(int i =19;i>=0;i--){
			s = ((a[i]&0xFF) + (b[i]&0xFF) + carry);
			sum[i]= (byte) s;
			carry = s >> 8;
		}
		return sum;
	}
	
	public static byte[] SimPRF(byte [] master_key){
		
		byte finalkey[] = new byte[160];
		byte []xkey = new byte[20];
		byte []xval = new byte[20];
		byte []w0= new byte [20];
		byte []w1=  new byte [20];
		byte []sum=  new byte [20];
		byte []one=  new byte [20];
		one[19]=1;
		byte []zeros = new byte[64];
		xkey=master_key;
		int f=0;

		for(int i=0; i<4; i++) {
			System.arraycopy(xkey, 0, xval, 0, xval.length);
			System.arraycopy(xval, 0,zeros ,0, xval.length);

			SHA1 sha1 = new SHA1();
			sha1.init();
			int totIntBytes[] = new int[16];
			for(int p=0,m=0;p<16;p++,m+=4){
				byte b[]= new byte[4];
				System.arraycopy(zeros, m, b,0,4);
				totIntBytes[p]=byteArrayToInt(b);
			}
			sha1.transform(totIntBytes);
			w0=sha1.SHA1FinalNoLength();
			sum= addBytes(xkey,w0);
			xkey=addBytes(sum,one);

			System.arraycopy(xkey, 0, xval, 0, xval.length);
			
			zeros= new byte[64];
			System.arraycopy(xval, 0,zeros ,0, 20);
			sha1 = new SHA1();
			sha1.init();
			int tIntBytes[] = new int[16];
			for(int p=0,m=0;p<16;p++,m+=4){
				byte b[]= new byte[4];
				System.arraycopy(zeros, m, b,0,4);
				tIntBytes[p]=byteArrayToInt(b);
			}
			sha1.transform(tIntBytes);
			w1=sha1.SHA1FinalNoLength();

			/*   f. XKEY = (1 + XKEY + w_1) mod 2^160 */
			sum= addBytes(xkey,w1);
			xkey=addBytes(sum,one);

			System.arraycopy(w0, 0,finalkey,f, 20);
			f+=20;

			System.arraycopy(w1, 0,finalkey,f, 20);
			f+=20;
		}
		return finalkey;
	} 

/* public static void main(String args[]){
	 byte mk[]= {(byte)0xbd, (byte)0x02, (byte)0x9b, (byte)0xbe, (byte)0x7f, (byte)0x51, (byte)0x96, (byte)0x0b, (byte)0xcf, (byte)0x9e, (byte)0xdb, 
			     (byte)0x2b, (byte)0x61, (byte)0xf0, (byte)0x6f, (byte)0x0f, (byte)0xeb, (byte)0x5a, (byte)0x38, (byte)0xb6 };

	 System.out.println("Mk : 0xbd029bbe7f51960bcf9edb2b61f06f0feb5a38b6");
	 
 }
*/ 
 
 /**
  * Convert the byte array to an int.
  *
  * @param b The byte array
  * @return The integer
  */
 public static int byteArrayToInt(byte[] b) {
     return byteArrayToInt(b, 0);
 }

 /**
  * Convert the byte array to an int starting from the given offset.
  *
  * @param b The byte array
  * @param offset The array offset
  * @return The integer
  */
 public static int byteArrayToInt(byte[] b, int offset) {
     int value = 0;
     for (int i = 0; i < 4; i++) {
         int shift = (4 - 1 - i) * 8;
         value += (b[i + offset] & 0x000000FF) << shift;
     }
     return value;
 }


	public static boolean isPermanentID(String ID, String pseudoIdPrefix, String fastReauthIDPref){
		if (ID == null)
			return false;
		if (ID.startsWith(pseudoIdPrefix) || ID.startsWith(fastReauthIDPref)){
			return false;
		}
		return true;
	}

	public static boolean isFastReauthID(String attrFastReauthID, String fastReauthID){
		if (attrFastReauthID != null && fastReauthID != null && attrFastReauthID.startsWith(fastReauthID)){
			return true;
		}
		return false;
	}

	public static boolean isPseudonymID(String attrPseudonymID, String pseudonymID){
		if (attrPseudonymID != null && pseudonymID != null && attrPseudonymID.startsWith(pseudonymID)){
			return true;
		}
		return false;
	}
	
}

class SHA1 {

	private int state[] = new int[5];
	private int block[] = new int[16];
	int buffer[] = new int[5];

	public SHA1() {
		state = new int[5];
		if (block == null)
			block = new int[16];
	}

	/*
	 * These functions are taken out of #defines in Steve's
	 * code. Java doesn't have a preprocessor so the first
	 * step is to just promote them to real methods.
	 * Later we can optimize them out into inline code,
	 * note that by making them final some compilers will
	 * inline them when given the -O flag.
	 */
	final int rol(int value, int bits) {
		int q = (value << bits) | (value >>> (32 - bits));
		return q;
	}

	final int blk0(int i) {
		return block[i];
	}

	final int blk(int i) {
		block[i&15] = rol(block[(i+13)&15]^block[(i+8)&15]^block[(i+2)&15]^block[i&15], 1);
		return (block[i&15]);
	}

	final void R0(int data[], int v, int w, int x , int y, int z, int i){
		data[z]+=((data[w]&(data[x]^data[y]))^data[y])+blk0(i)+0x5A827999+rol(data[v],5);
		data[w]=rol(data[w], 30);
	}

	final void R1(int data[], int v, int w, int x, int y, int z, int i){
		data[z] += ((data[w] & (data[x] ^ data[y])) ^ data[y]) + blk(i) + 0x5A827999 + rol(data[v] ,5);
		data[w] = rol(data[w], 30);
	}

	final void R2(int data[], int v, int w, int x, int y, int z, int i){
		data[z] += (data[w] ^ data[x] ^ data[y]) + blk(i) + 0x6ED9EBA1 + rol(data[v] ,5);
		data[w] = rol(data[w], 30);
	}

	final void R3(int data[], int v, int w, int x, int y, int z, int i){
		data[z] += (((data[w] | data[x]) & data[y]) | (data[w] & data[x])) + blk(i) + 0x8F1BBCDC + rol(data[v] ,5);
		data[w] = rol(data[w], 30);
	}

	final void R4(int data[], int v, int w, int x, int y, int z, int i){
		data[z] += (data[w] ^ data[x] ^ data[y]) + blk(i) + 0xCA62C1D6 + rol(data[v] ,5);
		data[w] = rol(data[w], 30);
	}



	/**
	 * Hash a single 512-bit block. This is the core of the algorithm.
	 *
	 * Note that working with arrays is very inefficent in Java as it
	 * does a class cast check each time you store into the array.
	 *
	 */

	public void transform(int []blk) {
		block = blk;
		/* Copy context->state[] to working vars */
		buffer[0] = state[0];
		buffer[1] = state[1];
		buffer[2] = state[2];
		buffer[3] = state[3];
		buffer[4] = state[4];
		/* 4 rounds of 20 operations each. Loop unrolled. */
		R0(buffer,0,1,2,3,4, 0);

		R0(buffer,4,0,1,2,3, 1); R0(buffer,3,4,0,1,2, 2);  R0(buffer,2,3,4,0,1, 3); 
		R0(buffer,1,2,3,4,0, 4); R0(buffer,0,1,2,3,4, 5); R0(buffer,4,0,1,2,3, 6);  R0(buffer,3,4,0,1,2, 7);
		R0(buffer,2,3,4,0,1, 8); R0(buffer,1,2,3,4,0, 9); R0(buffer,0,1,2,3,4,10);  R0(buffer,4,0,1,2,3,11);
		R0(buffer,3,4,0,1,2,12); R0(buffer,2,3,4,0,1,13); R0(buffer,1,2,3,4,0,14);  R0(buffer,0,1,2,3,4,15);
		R1(buffer,4,0,1,2,3,16); R1(buffer,3,4,0,1,2,17); R1(buffer,2,3,4,0,1,18);  R1(buffer,1,2,3,4,0,19);
		R2(buffer,0,1,2,3,4,20); R2(buffer,4,0,1,2,3,21); R2(buffer,3,4,0,1,2,22);  R2(buffer,2,3,4,0,1,23);
		R2(buffer,1,2,3,4,0,24); R2(buffer,0,1,2,3,4,25); R2(buffer,4,0,1,2,3,26);  R2(buffer,3,4,0,1,2,27);
		R2(buffer,2,3,4,0,1,28); R2(buffer,1,2,3,4,0,29); R2(buffer,0,1,2,3,4,30);  R2(buffer,4,0,1,2,3,31);
		R2(buffer,3,4,0,1,2,32); R2(buffer,2,3,4,0,1,33); R2(buffer,1,2,3,4,0,34);  R2(buffer,0,1,2,3,4,35);
		R2(buffer,4,0,1,2,3,36); R2(buffer,3,4,0,1,2,37); R2(buffer,2,3,4,0,1,38);  R2(buffer,1,2,3,4,0,39);
		R3(buffer,0,1,2,3,4,40); R3(buffer,4,0,1,2,3,41); R3(buffer,3,4,0,1,2,42);  R3(buffer,2,3,4,0,1,43);
		R3(buffer,1,2,3,4,0,44); R3(buffer,0,1,2,3,4,45); R3(buffer,4,0,1,2,3,46);  R3(buffer,3,4,0,1,2,47);
		R3(buffer,2,3,4,0,1,48); R3(buffer,1,2,3,4,0,49); R3(buffer,0,1,2,3,4,50);  R3(buffer,4,0,1,2,3,51);
		R3(buffer,3,4,0,1,2,52); R3(buffer,2,3,4,0,1,53); R3(buffer,1,2,3,4,0,54);  R3(buffer,0,1,2,3,4,55);
		R3(buffer,4,0,1,2,3,56); R3(buffer,3,4,0,1,2,57); R3(buffer,2,3,4,0,1,58);  R3(buffer,1,2,3,4,0,59);
		R4(buffer,0,1,2,3,4,60); R4(buffer,4,0,1,2,3,61); R4(buffer,3,4,0,1,2,62);  R4(buffer,2,3,4,0,1,63);
		R4(buffer,1,2,3,4,0,64); R4(buffer,0,1,2,3,4,65); R4(buffer,4,0,1,2,3,66);  R4(buffer,3,4,0,1,2,67);
		R4(buffer,2,3,4,0,1,68); R4(buffer,1,2,3,4,0,69); R4(buffer,0,1,2,3,4,70);  R4(buffer,4,0,1,2,3,71);
		R4(buffer,3,4,0,1,2,72); R4(buffer,2,3,4,0,1,73); R4(buffer,1,2,3,4,0,74);  R4(buffer,0,1,2,3,4,75);
		R4(buffer,4,0,1,2,3,76); R4(buffer,3,4,0,1,2,77); R4(buffer,2,3,4,0,1,78);  R4(buffer,1,2,3,4,0,79);

	//	System.out.println(buffer[0]+ " " + buffer[1] + " "+ buffer[2]+ " "+ buffer[3]+" " + buffer[4]);        
		/* Add the working vars back into context.state[] */
		state[0] += buffer[0];
		state[1] += buffer[1];
		state[2] += buffer[2];
		state[3] += buffer[3];
		state[4] += buffer[4];
	}

	/**
	 *
	 * SHA1Init - Initialize new context
	 */
	public void init() {
		/* SHA1 initialization constants */
		state[0] = 0x67452301;
		state[1] = 0xEFCDAB89;
		state[2] = 0x98BADCFE;
		state[3] = 0x10325476;
		state[4] = 0xC3D2E1F0;
	}

	public byte[] SHA1FinalNoLength() {
		byte []digestBits = new byte[20];
		for ( int i = 0; i < 20; i++) {
			digestBits[i] = (byte)((state[i>>2] >> ((3-(i & 3)) * 8) ) & 255);
		}
		return digestBits;
	}


	/**
	 * Add one byte to the digest. When this is implemented
	 * all of the abstract class methods end up calling
	 * this method for types other than bytes.
	 */
	/* public synchronized void update(byte b) {
        int mask = (8 * (blockIndex & 3));

        count += 8;
        block[blockIndex >> 2] &= ~(0xff << mask);
        block[blockIndex >> 2] |= (b & 0xff) << mask;
        blockIndex++;
        if (blockIndex == 64) {
            transform();
            blockIndex = 0;
        }
    }
	 */

	/**
	 * Complete processing on the message digest.
	 */
	/*  public void finish() {
        byte bits[] = new byte[8];
        int i, j;

        for (i = 0; i < 8; i++) {
            bits[i] = (byte)((count >>> (((7 - i) * 8))) & 0xff);
        }

        update((byte) 128);
        while (blockIndex != 56)
            update((byte) 0);
        // This should cause a transform to happen.
        update(bits);
        for (i = 0; i < 20; i++) {
            digestBits[i] = (byte)
                ((state[i>>2] >> ((3-(i & 3)) * 8) ) & 0xff);
        }
        digestValid = true;
    }*/

	/** Return a string that identifies this algorithm */
	//    public String getAlg() { return "SHA1"; }

	/**
	 * Print out the digest in a form that can be easily compared
	 * to the test vectors.
	 */
	/*   private String digout() {
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 20; i++) {
            char c1, c2;

            c1 = (char) ((digestBits[i] >>> 4) & 0xf);
            c2 = (char) (digestBits[i] & 0xf);
            c1 = (char) ((c1 > 9) ? 'A' + (c1 - 10) : '0' + c1);
            c2 = (char) ((c2 > 9) ? 'A' + (c2 - 10) : '0' + c2);
            sb.append(c1);
            sb.append(c2);
            if (((i+1) % 4) == 0)
                sb.append(' ');
        }
        return sb.toString();
    }
	 */

	/**
	 *  Generates 16 bytes random number [ 16 bytes = 4 bytes(unix time) + 12 bytes(random) ]. 
	 * @return
	 */
	
	
	
}

