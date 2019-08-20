package com.elitecore.coreeap.data.sim;

import java.security.GeneralSecurityException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.coreeap.util.sim.gsm.GsmA3A8Dictionary;
import com.elitecore.coreeap.util.sim.gsm.IGsmA3A8;
import com.elitecore.coreeap.util.tls.TLSUtility;

public class GsmTriplets {

	private ArrayList<GsmTriplet> gsmTripletList;
	
	public GsmTriplets() {
		gsmTripletList = new ArrayList<GsmTriplet>(4);
	}
	
	public void addGsmTriplet(GsmTriplet gsmTriplet){
		gsmTripletList.add(gsmTriplet);
	}
	
	public void addGsmTriplet(byte[] rand,byte[] key, byte[] operatorVariant, int gsmA3A8Algo) throws GeneralSecurityException {
		IGsmA3A8 gsmA3A8 = GsmA3A8Dictionary.getInstance().getGsmA3A8(gsmA3A8Algo);
		gsmA3A8.configure(TLSUtility.bytesToHex(operatorVariant));			
		gsmTripletList.add(GsmTriplet.generate(key, rand, gsmA3A8)); 
	}
	
	public void addGsmTriplets(byte[] rand,byte[] key, byte[] operatorVariant, int gsmA3A8Algo, int numberOfTriplets) throws GeneralSecurityException{
		if (rand.length%16 != 0 || rand.length/numberOfTriplets != 16) {
			throw new IllegalArgumentException((new StringBuilder()).append(
					"Invalid rand length ").append(rand.length).toString());
		}
		
		for(int i=0 ; i < rand.length ; i+=16){
			byte[] tempRand = new byte[16];
			System.arraycopy(rand, i, tempRand, 0, 16);
			addGsmTriplet(tempRand, key, operatorVariant, gsmA3A8Algo);			
		}
	}
	
	public List<GsmTriplet> getGsmTriplets(){
		return gsmTripletList;
	}

	public byte[] getSres(){
		byte[] sres = null;
		for(GsmTriplet gsmTriplet : gsmTripletList){
			sres = TLSUtility.appendBytes(sres, gsmTriplet.getSRES());
		}
		return sres;
	}
	
	public byte[] getKcs(){
		byte[] kcs = null;
		for(GsmTriplet gsmTriplet : gsmTripletList){
			kcs = TLSUtility.appendBytes(kcs, gsmTriplet.getKc());
		}
		return kcs;
	}

	public byte[] getRands(){
		byte[] rands = null;
		for(GsmTriplet gsmTriplet : gsmTripletList){
			rands = TLSUtility.appendBytes(rands, gsmTriplet.getRAND());
		}
		return rands;
	}

}
