package com.elitecore.core.util.url;

import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;

/**
 * Set of Utility to generate MultiSocketURL
 * 
 * @author malav.desai
 */
public class MultiSocketURLParser {
	
	private static final String MODULE = "MULTISOCKET-URL-PARSER";
	
	/**
	 * 
	 * Generates list of socket details from comma separated String of URL.
	 * Each socket detail contains the IP Address and port on which service need to run.
	 * <pre>
	 * 
	 * 	a) Service address can have multiple comma separated URLs.
	 * 
	 * 	b) In case of multiple URLs invalid URL will not be included 
	 * 		into the returned list.
	 * 
	 * 	c) Port 0 is considered as invalid port so if any URL with port 
	 * 		0 is encountered then that specific URl will be skipped.
	 * 
	 * 	d) If service address doesn't have a single valid URL than it will
	 * 		throw MalformedURLException.
	 * 
	 * Some service addresses and their respective behavior with reason
	 *	 
	 * 	1) 10.106.1.1:100
	 * 		will return single SocketDetail 
	 * 			1. IPAddress: 10.106.1.1 and Port: 100
	 * 
	 * 	2) 10.106.1.1:100, 10.106.1.2:200
	 * 		will return two SocketDetail  
	 * 			1. IPAddress: 10.106.1.1 and Port: 100
	 * 			2. IPAddress: 10.106.1.2 and Port: 200
	 * 
	 * 	3) 10.106.1.1::100, 10.106.1.2:200, 10.106.1.3::300
	 * 		will return single SocketDetail
	 * 			1. IPAddress: 10.106.1.2 and Port: 200
	 * 		Reason:
	 * 			first and third URLs in service address have improper
	 * 			URL so skipped
	 * 
	 * 	4) 10.106.1.1:0, 10.106.1.2:200
	 * 		will return single SocketDetail
	 * 			1. IPAddress: 10.106.1.2 and Port: 200
	 * 		Reason:
	 * 			if any service starts using port 0 then the bound port will
	 * 			be unknown so port 0 is not acceptable in service address
	 * 			so skipped
	 * 
	 * 	5) 10.106.1.1:
	 * 		will throw MalformedURLException
	 * 		Reason:
	 * 			port is not defined so service address have improper
	 * 			URL so skipped and not a single URL is proper to return
	 * 
	 * 	6) 10.106.1.1:, 10.106.1.2:a
	 * 		will throw MalformedURLException
	 * 		Reason:
	 * 			no proper port is available so service address have improper
	 * 			URL so skipped and not a single URL is proper to return
	 * 
	 * 	7) 10.106.1.1::100, 10.106.1.2::200
	 * 		will throw MalformedURLException
	 * 		Reason:
	 * 			each URL in service address has improper URL syntax
	 * 
	 * 	8) 10.106.1.1:0
	 * 		will throw MalformedURLException
	 * 		Reason:
	 * 			if any service starts using port 0 than the bound port will
	 * 			be unknown so port 0 is not acceptable in service address 
	 * </pre>
	 * 		
	 * 
	 * @param serviceAddress comma separated String of multiple URL.
	 * @see SocketDetail
	 * @return list of valid socket detail which contains IPAddress and Port
	 * @throws MalformedURLException If service address doesn't have a single valid URL
	 */
	public static List<SocketDetail> parse(String serviceAddress) throws MalformedURLException {
		List<SocketDetail> socketDataList = new ArrayList<SocketDetail>();
		for (String url : Strings.splitter(',').trimTokens().split(serviceAddress)) {
			try {
				URLData urlData = URLParser.parse(url);
				if ( urlData.getPort() == 0) {
					throw new InvalidURLException("URL port 0 is not allowed");
				}
				socketDataList.add(new SocketDetail(urlData.getHost(), urlData.getPort()));
			} catch (InvalidURLException e) {
				LogManager.getLogger().warn(MODULE, e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		if (socketDataList.isEmpty()) {
			throw new MalformedURLException("No URL is acceptable in Service Address: " + serviceAddress);
		}
		return socketDataList;
	}
	
}
