package com.elitecore.diameterapi.diameter.common.util.constant;

public enum RedirectHostAVPFormat {
	DIAMETERURI("DIAMETERURI"),
	HOSTIDENTITY("HOST IDENTITY"),
	IP("IP");


	/**
	 * get RedirectHostAVPFormat with the help of its String value
	 * 
	 * @param redirectHostAVPFormatStr is String value of RedirectHostAVPFormat
	 * @return RedirectHostAVPFormat
	 */
	private String name;
	private RedirectHostAVPFormat(String name) {
		this.name = name;
	}
	
	public static RedirectHostAVPFormat fromRedirectHostAVPFormat(String redirectHostAVPFormatStr) {
		if(DIAMETERURI.name.equalsIgnoreCase(redirectHostAVPFormatStr)){
			return RedirectHostAVPFormat.DIAMETERURI;
		}else if(HOSTIDENTITY.name.equalsIgnoreCase(redirectHostAVPFormatStr)){
			return RedirectHostAVPFormat.HOSTIDENTITY;
		}else if(IP.name.equalsIgnoreCase(redirectHostAVPFormatStr)){
			return RedirectHostAVPFormat.IP;
		}
		return null;

	}
	
	public String getStrFormat() {
		return this.name;
	}

}
