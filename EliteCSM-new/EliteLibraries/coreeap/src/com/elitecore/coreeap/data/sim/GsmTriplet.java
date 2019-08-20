package com.elitecore.coreeap.data.sim;

import java.nio.ByteBuffer;
import java.security.GeneralSecurityException;

import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.sim.gsm.GsmA3A8Dictionary;
import com.elitecore.coreeap.util.sim.gsm.IGsmA3A8;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class GsmTriplet implements Cloneable{
	private byte m_rand[];
	private byte m_sres[];
	private byte m_kc[];

	public GsmTriplet(byte rand[], byte sres[], byte kc[]) {
		if(rand.length%16 != 0 && sres.length%4!=0 && kc.length%8!=0 ) {
			throw new IllegalArgumentException("Invalid length of triplet");
		}
		m_rand = new byte[16];
		m_sres = new byte[4];
		m_kc = new byte[8];
		System.arraycopy(rand, 0, m_rand, 0, 16);
		System.arraycopy(sres, 0, m_sres, 0, 4);
		System.arraycopy(kc, 0, m_kc, 0, 8);
	}

	public GsmTriplet(byte buf[], int randOffset, int sresOffset, int kcOffset) {
		m_rand = new byte[16];
		m_sres = new byte[4];
		m_kc = new byte[8];
		System.arraycopy(buf, randOffset, m_rand, 0, 16);
		System.arraycopy(buf, sresOffset, m_sres, 0, 4);
		System.arraycopy(buf, kcOffset, m_kc, 0, 8);
	}

	public GsmTriplet(ByteBuffer byteBuffer, int randOffset, int sresOffset,int kcOffset) {
		m_rand = new byte[16];
		m_sres = new byte[4];
		m_kc = new byte[8];
		byteBuffer.position(randOffset);
		byteBuffer.get(m_rand, 0, 16);
		byteBuffer.position(sresOffset);
		byteBuffer.get(m_sres, 0, 4);
		byteBuffer.position(kcOffset);
		byteBuffer.get(m_kc, 0, 8);
	}

	public static GsmTriplet generate(byte key[], byte rand[], IGsmA3A8 a3a8)
			throws GeneralSecurityException {
		if (key.length != 16)
			throw new IllegalArgumentException((new StringBuilder()).append(
					"Invalid key length ").append(key.length).toString());
		if (rand.length != 16) {
			throw new IllegalArgumentException((new StringBuilder()).append(
					"Invalid rand length ").append(rand.length).toString());
		} else {
			byte output[] = a3a8.compute(rand, key);
			byte sres[] = new byte[4];
			byte kc[] = new byte[8];
			System.arraycopy(output, 0, sres, 0, 4);
			System.arraycopy(output, 4, kc, 0, 8);
			return new GsmTriplet(rand, sres, kc);
		}
	}

	public byte[] getKc() {
		byte tmp[] = new byte[8];
		System.arraycopy(m_kc, 0, tmp, 0, 8);
		return tmp;
	}

	public byte[] getSRES() {
		byte tmp[] = new byte[4];
		System.arraycopy(m_sres, 0, tmp, 0, 4);
		return tmp;
	}

	public byte[] getRAND() {
		byte tmp[] = new byte[16];
		System.arraycopy(m_rand, 0, tmp, 0, 16);
		return tmp;
	}

	public byte[] getBytes() {
		byte tmp[] = new byte[28];
		System.arraycopy(m_rand, 0, tmp, 0, 16);
		System.arraycopy(m_sres, 0, tmp, 16, 4);
		System.arraycopy(m_kc, 0, tmp, 20, 8);
		return tmp;
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append("rand=").append(Utility.bytesToHex(m_rand));
		sb.append(", sres=").append(Utility.bytesToHex(m_sres));
		sb.append(", kc=").append(Utility.bytesToHex(m_kc));
		return sb.toString();
	}

	@Override
	protected Object clone() throws CloneNotSupportedException {
		GsmTriplet gsmTriplet = null;
		gsmTriplet = (GsmTriplet)super.clone();
		if(m_rand != null){
			gsmTriplet.m_rand = new byte[m_rand.length];
			System.arraycopy(m_rand, 0, gsmTriplet.m_rand, 0, m_rand.length);
		}
		if(m_sres != null){
			gsmTriplet.m_sres = new byte[m_sres.length];
			System.arraycopy(m_sres, 0, gsmTriplet.m_sres, 0, m_sres.length);
		}
		if(m_kc != null){
			gsmTriplet.m_kc = new byte[m_kc.length];
			System.arraycopy(m_kc, 0, gsmTriplet.m_kc, 0, m_kc.length);
		}				
		return gsmTriplet;
	}
	
	public static void main(String[] args) {		
		byte[] key = TLSUtility.HexToBytes("31353130383930393637373336343832");
		byte[] rand = TLSUtility.HexToBytes("C5E6113D4A6B4e74BA9115D9C8A5C401");
		
		IGsmA3A8 gsmA3A8 = GsmA3A8Dictionary.getInstance().getGsmA3A8(3);					
		GsmTriplet gsmTriplet = null;
		try {
			gsmTriplet = GsmTriplet.generate(key, rand, gsmA3A8);
		} catch (GeneralSecurityException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
}
		
		System.out.println("GSMTriplet : " + gsmTriplet);
	}
}
