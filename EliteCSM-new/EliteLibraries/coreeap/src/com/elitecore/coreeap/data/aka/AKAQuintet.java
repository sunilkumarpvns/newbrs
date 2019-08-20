package com.elitecore.coreeap.data.aka;

import java.security.GeneralSecurityException;

import com.elitecore.coreeap.util.aka.quintet.IAkaQuintet;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class AKAQuintet {

      private  byte[] rand;
	  private byte[] autn;
	  private byte[] xres;
	  private byte[] ck;
	  private byte[] ik;

	  public AKAQuintet(){
		  
	  }
	  
	  public AKAQuintet(byte[] rand, byte[] autn, byte[] xres, byte[] ck, byte[] ik)	  {
	    this.rand = rand;
	    this.autn = autn;
	    this.xres = xres;
	    this.ck = ck;
	    this.ik = ik;
	  }
	  
	  public AKAQuintet(byte[] buf, int randOffset, int autnOffset, int xresOffset, int xresLength, int ckOffset, int ikOffset)
	  {
	    this.rand = new byte[16];
	    this.autn = new byte[16];
	    this.xres = new byte[xresLength];
	    this.ck = new byte[16];
	    this.ik = new byte[16];
	    System.arraycopy(buf, randOffset, this.rand, 0, 16);
	    System.arraycopy(buf, autnOffset, this.autn, 0, 16);
	    System.arraycopy(buf, xresOffset, this.xres, 0, xresLength);
	    System.arraycopy(buf, ckOffset, this.ck, 0, 16);
	    System.arraycopy(buf, ikOffset, this.ik, 0, 16);
	  }
	  
	  
	  public AKAQuintet(byte[] bytes)
	  {
	    this(bytes, 0, 16, 33, bytes[32], 49, 65);
	  }

	  public AKAQuintet(byte[] key, byte[] rand, byte[] sqn, byte[] amf, IAkaQuintet akaAlgorithm) throws GeneralSecurityException
	  {
	    if (key.length != 16) {
	      throw new IllegalArgumentException("Invalid KEY length " + key.length);
	    }
	    if (rand.length != 16) {
	      throw new IllegalArgumentException("Invalid RAND length " + rand.length);
	    }
	    if (sqn.length != 6) {
	      throw new IllegalArgumentException("Invalid SQN length " + sqn.length);
	    }
	    if (amf.length != 2) {
	      throw new IllegalArgumentException("Invalid AMF length " + amf.length);
	    }
	    this.rand = rand;
	    byte[] ak = new byte[6];
	    byte[] mac = new byte[8];
	    this.ck = new byte[16];
	    this.ik = new byte[16];

	    akaAlgorithm.f1(key, rand, sqn, amf, mac);
	    this.xres = akaAlgorithm.f2345(key, rand, this.ck, this.ik, ak);

	    this.autn = new byte[16];
	    for (int i = 0; i < 6; i++)
	    {
	      this.autn[i] = (byte)(sqn[i] ^ ak[i]);
	    }
	    this.autn[6] = amf[0];
	    this.autn[7] = amf[1];
	    System.arraycopy(mac, 0, this.autn, 8, 8);
	  }
	
	  public byte[] getBytes()
	  {
	    byte[] tmp = new byte[81];
	    System.arraycopy(this.rand, 0, tmp, 0, 16);
	    System.arraycopy(this.autn, 0, tmp, 16, 16);
	    tmp[32] = (byte)this.xres.length;
	    System.arraycopy(this.xres, 0, tmp, 33, this.xres.length);
	    System.arraycopy(this.ck, 0, tmp, 49, 16);
	    System.arraycopy(this.ik, 0, tmp, 65, 16);
	    return tmp;
	  }

	  public String toString()
	  {
	    StringBuilder sb = new StringBuilder();
	    sb.append("rand=").append(TLSUtility.bytesToHex(this.rand));
	    sb.append(", autn=").append(TLSUtility.bytesToHex(this.autn));
	    sb.append(", xres=").append(TLSUtility.bytesToHex(this.xres));
	    sb.append(", ck=").append(TLSUtility.bytesToHex(this.ck));
	    sb.append(", ik=").append(TLSUtility.bytesToHex(this.ik));
	    return sb.toString();
	  }

	public byte[] getRand() {
		return rand;
	}

	public byte[] getAutn() {
		return autn;
	}

	public byte[] getXres() {
		return xres;
	}

	public byte[] getCk() {
		return ck;
	}

	public byte[] getIk() {
		return ik;
	}

}
