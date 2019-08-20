package com.elitecore.coreeap.util.sim.gsm;

public class GsmXor extends BaseGSMA3A8 {
	  
	public void configure(String s)
      {
      }

      public byte[] compute(byte rand[], byte key[])
      {
          byte output[] = new byte[12];
          for(int i = 0; i < 12; i++){
              output[i] = (byte)(key[i] ^ rand[i]);
          }
          return output;
      }
      
    public Object clone() throws CloneNotSupportedException {
    	GsmXor gsmXor = null;
    	gsmXor = (GsmXor)super.clone();    	
    	return gsmXor;
    }
}
