package com.elitecore.diameterapi.diameter.common.util;

import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.avps.URISyntaxException;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterConstants;

public class DiameterURIParser {
	private static final String SCHEME_SEPARATOR = "://";
    private static final String PARAMS_SEPARATOR = ";";
    private static final int DEFAULT_PORT = DiameterConstants.DIAMETER_SERVICE_PORT;
	private static final String MODULE = "DIA-URI-PARSER";
    
	public static URIData parse(String strURI) throws URISyntaxException{
		URIData uriElements = null;
		String scheme;
		String host;
		int port;
		String strTransport = null;
		String strProtocol = null;

		try {
			int schemeStartIndex = strURI.indexOf(SCHEME_SEPARATOR);
			if (schemeStartIndex == -1)
				throw new URISyntaxException("Protocol scheme not found");
			scheme = strURI.substring(0, schemeStartIndex);

			int schemeEndIndex = schemeStartIndex + 3;
			schemeStartIndex = strURI.indexOf(';', schemeEndIndex);
			if (schemeStartIndex == -1)
				host = strURI.substring(schemeEndIndex);
			else
				host = strURI.substring(schemeEndIndex, schemeStartIndex);

			int sepIndex = host.indexOf(':');
			if (sepIndex != -1) {
				try {
					port = Integer.parseInt(host.substring(sepIndex + 1));
				} catch ( NumberFormatException e) {
					throw new URISyntaxException("Invalid port: " + host.substring(sepIndex + 1) + " configured for DiameterURI");
				}
				host = host.substring(0, sepIndex);

			}else{
				port = DEFAULT_PORT;
			}
			if (schemeStartIndex != -1){
				String strRest = strURI.substring(schemeStartIndex + 1);
				StringTokenizer strToken = new StringTokenizer(strRest,PARAMS_SEPARATOR);
				while(strToken.hasMoreTokens()){
					String strNextToken = strToken.nextToken();
					String strValues[] = strNextToken.split("=");
					if(strValues.length==2){
						if(strValues[0].equals("transport")){
							strTransport = strValues[1];
						}else if(strValues[0].equals("protocol")){
							strProtocol = strValues[1];
						}
					}
				}
			}
			if( "udp".equalsIgnoreCase(strTransport) && "diameter".equalsIgnoreCase(strProtocol) ){
				throw new URISyntaxException("UDP Transport can not be used with Diameter Protocol");
			}

			uriElements = new URIData();
			uriElements.setProtocol(strProtocol);
			uriElements.setTransport(strTransport);
			uriElements.setHost(host);
			uriElements.setPort(port);
			uriElements.setScheme(scheme);
		} catch (Exception e) {
			LogManager.getLogger().warn(MODULE, "Invalid DiameterURI. Reason: " + e.getMessage());
		}
		return uriElements;
	}
}
