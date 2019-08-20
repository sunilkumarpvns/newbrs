package com.elitecore.aaa.rm.drivers;

public interface IRatingResponseObject {
	
	 public abstract String get(String arg);
	  
	  public abstract String put(String key, String value);	  	  
	  
	  public abstract int getResponseCode();
	  
	  public abstract void setResponseCode(int responseCode);
	  
	  public abstract Object getResponseObject();
	  
	  public abstract void setResponseObject(Object arg0);
	  
	  public abstract String getResponseMessage();
	  	  
	  public abstract void setResponseMessage(String responseMessage);
	  	 
	  public abstract java.util.Set keySet();

}
