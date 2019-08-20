package com.elitecore.test.dependecy.diameter.packet.avps.derived;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.test.dependecy.diameter.DiameterConstants;
import com.elitecore.test.dependecy.diameter.packet.avps.URISyntaxException;
import com.elitecore.test.dependecy.diameter.packet.avps.basic.AvpOctetString;

import java.io.UnsupportedEncodingException;
import java.util.StringTokenizer;

public class AvpDiameterURI extends AvpOctetString{
    private static final String SCHEME_SEPARATOR = "://";
    private static final String PARAMS_SEPARATOR = ";";
    private static final int DEFAULT_PORT = DiameterConstants.DIAMETER_SERVICE_PORT;

    private String strScheme;
    private String strHost;
    private int iPort = -1;
    private String strTransport = "sctp";
    private String strProtocol = "diameter";
	
    public AvpDiameterURI(int intAVPCode,int intVendorId ,byte bAVPFlag,String strAvpId,String strAVPEncryption) {
		super(intAVPCode,intVendorId ,bAVPFlag,strAvpId,strAVPEncryption);
	}
    
	public void setStringValue(String data) throws URISyntaxException {
		if(data == null)
			throw new URISyntaxException();
		parseURI(data);
		super.setStringValue(data);
	}
	
	public String getStringValue() throws URISyntaxException {
		String data ;
		byte []valueBuffer = null;
		valueBuffer = this.getValueBytes();
		try {
			data=new String(valueBuffer,"UTF-8");
		} catch (UnsupportedEncodingException e) {
			LogManager.getLogger().trace("", e);
			data=new String(valueBuffer);
		}
		parseURI(data);
		return data;
	}
	
	public String getFQDN(){
		return strHost;
	}
	public int getPort(){
		return iPort;
	}
	public String getTransport(){
		return strTransport;
	}
	public String getProtocol(){
		return strProtocol;
	}
	public String getScheme(){
		return strScheme;
	}
	public void parseURI(String strURI){
        int schemeStartIndex = strURI.indexOf(SCHEME_SEPARATOR);
        if (schemeStartIndex == -1)
            throw new URISyntaxException("Protocol scheme not found");
        strScheme = strURI.substring(0, schemeStartIndex);
        int schemeEndIndex = schemeStartIndex + 3;
        schemeStartIndex = strURI.indexOf(';', schemeEndIndex);
        if (schemeStartIndex == -1)
            strHost = strURI.substring(schemeEndIndex);
        else
            strHost = strURI.substring(schemeEndIndex, schemeStartIndex);
        int sepIndex = strHost.indexOf(':');
        if (sepIndex != -1) {
            iPort = Integer.parseInt(strHost.substring(sepIndex + 1));
            strHost = strHost.substring(0, sepIndex);
        }else{
        	iPort = DEFAULT_PORT;
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
        if(strTransport.equals("udp")&&strProtocol.equals("diameter")){
        	throw new URISyntaxException("UDP Transport can not be used with Diameter");
        }
    }
/*	public String toString() {
		String strURI =this.getDiameterURI();
		String strStringURI = "Scheme    : " + getScheme() +   
							  "\nHost      : " + getFQDN() +  
							  "\nPort      : " + getPort() +  
							  "\nTransport : " + getTransport() +  
							  "\nProtocol  : " + getProtocol();
		return strStringURI;
	}
*/}
