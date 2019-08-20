/**
 * 
 */
package com.elitecore.aaa.rm.conf.impl;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.rm.conf.GTPPrimeClientConfiguration;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientData;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientDataImpl;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.configuration.LoadConfigurationException;
import com.elitecore.core.serverx.ServerContext;

/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimeClientConfigurationImpl implements	GTPPrimeClientConfiguration {

	private static final String MODULE = "GTP'-Client-Configuration-Impl";
	private AAAServerContext serverContext;
	private List<GTPPrimeClientData> lstClients;
	private Map<InetAddress, GTPPrimeClientData> addrClientMap;

	public GTPPrimeClientConfigurationImpl(AAAServerContext serverContext) {
		this.serverContext = serverContext;
		lstClients = new ArrayList<GTPPrimeClientData>();
		addrClientMap = new HashMap<InetAddress , GTPPrimeClientData>();
	}
	@Override
	public List<GTPPrimeClientData> getClientList(){
		return lstClients;
	}
	@Override
	public void readClientConfiguration(String systemRedirectionIP) {
		String clientIP = "0.0.0.0";
		int clientPort = 3386;
		boolean nodeAliveRequest=false;
		int echoRequestInterval=0;
		int requestExpiryTime=8000;
		int requestRetry = 3;
		String redirectionIP = null;
		String fileName = "defaultFile.gtpp";
		boolean isFileSequenceRequired = true;
		int minSequence = 0;
		int maxSequence = Integer.MAX_VALUE;
		String fileLocation = getServerContext().getServerHome();
		int rollingType = 1;
		int rollingUnit = 5;
		String strSeqRange;
		try {

			File clientConf = new File (getFileName());
			if (!clientConf.exists()){
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "GTP' client configuration file not found");
				}
				throw new LoadConfigurationException("GTP' client config failed");
			}

			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setIgnoringComments(true);
			factory.setIgnoringElementContentWhitespace(true);
			factory.setValidating(false);

			DocumentBuilder builder = factory.newDocumentBuilder();

			Document document = builder.parse(clientConf); 

			Node rootNode = document.getElementsByTagName("gtp-prime-service").item(0);

			if (rootNode!=null && rootNode.getChildNodes().getLength()>0){

				NodeList nodeList = rootNode.getChildNodes();
				for (int i=0 ; i<nodeList.getLength() ; i++){
					Node subNode = nodeList.item(i);
					if (subNode.getNodeName().equalsIgnoreCase("clients") && subNode.getTextContent().trim().length()>0){

						NodeList clientsNode = subNode.getChildNodes();
						for ( int j=0 ; j<clientsNode.getLength() ; j++){
							Node clientSubNode = subNode.getChildNodes().item(j);
							clientIP = "0.0.0.0";
							clientPort = 3386;
							nodeAliveRequest=false;
							echoRequestInterval=0;
							requestExpiryTime=8000;
							fileName = "defaultFile.gtpp";
							isFileSequenceRequired = true;
							minSequence = 0;
							maxSequence = Integer.MAX_VALUE;
							fileLocation = getServerContext().getServerHome();
							requestRetry = 3;
							redirectionIP = "0.0.0.0";
							rollingType = 1;
							rollingUnit = 5;
							if (clientSubNode.getNodeName().equalsIgnoreCase("client") && clientSubNode.getTextContent().trim().length()>0){
								NodeList clientInfoList = clientSubNode.getChildNodes();
								for (int k=0 ; k<clientInfoList.getLength(); k++){
									Node clientInfo = clientInfoList.item(k);
									if (clientInfo.getNodeName().equalsIgnoreCase("client-ip") && clientInfo.getTextContent().trim().length()>0){
										clientIP = clientInfo.getTextContent().trim();
										try{
											InetAddress.getByName(clientIP);
										} catch (UnknownHostException e){
											throw new ParserConfigurationException("Client IP found invalid: " + clientIP);
										}
									}else if (clientInfo.getNodeName().equalsIgnoreCase("client-port") && clientInfo.getTextContent().trim().length()>0){
										try {
											clientPort = stringToInteger(clientInfo.getTextContent().trim(), 3386);
										} catch (Exception e) {
											throw new ParserConfigurationException("Client port invalid: " + clientPort + " for client: " + clientIP);
										}
									} else if (clientInfo.getNodeName().equalsIgnoreCase("node-alive-request") && clientInfo.getTextContent().trim().length()>0){
										nodeAliveRequest = Boolean.parseBoolean(clientInfo.getTextContent().trim());
									}else if (clientInfo.getNodeName().equalsIgnoreCase("echo-request") && clientInfo.getTextContent().trim().length()>0){
										echoRequestInterval = Integer.parseInt(clientInfo.getTextContent().trim());
									}else if (clientInfo.getNodeName().equalsIgnoreCase("request-expiry-time") && clientInfo.getTextContent().trim().length()>0){
										try {
											requestExpiryTime = stringToInteger(clientInfo.getTextContent().trim(),requestExpiryTime);
										} catch (Exception e) {
											throw new ParserConfigurationException("Request expiry time invalid: " + requestExpiryTime);
										}
									}else if (clientInfo.getNodeName().equalsIgnoreCase("request-retry") && clientInfo.getTextContent().trim().length()>0){
										try {
											requestRetry = stringToInteger(clientInfo.getTextContent().trim(),requestExpiryTime);
										} catch (Exception e) {
											throw new ParserConfigurationException("Request retry invalid: " + requestRetry);
										}
									}else if (clientInfo.getNodeName().equalsIgnoreCase("redirection-ip")){
										if ( clientInfo.getTextContent().trim().length()>0){
											redirectionIP = clientInfo.getTextContent().trim();
										}
										else{
											redirectionIP = systemRedirectionIP;
										}

									}else if (clientInfo.getNodeName().equalsIgnoreCase("file-location") && clientInfo.getTextContent().trim().length()>0){
										fileLocation = clientInfo.getTextContent().trim();
									} else if (clientInfo.getNodeName().equalsIgnoreCase("file-name") && clientInfo.getTextContent().trim().length()>0){
										fileName = clientInfo.getTextContent().trim();
									} else if (clientInfo.getNodeName().equalsIgnoreCase("file-sequence") && clientInfo.getTextContent().trim().length()>0){
										isFileSequenceRequired = Boolean.parseBoolean(clientInfo.getTextContent().trim());
									}else if (clientInfo.getNodeName().equalsIgnoreCase("sequence-range") && clientInfo.getTextContent().trim().length()>0){
										
										strSeqRange = clientInfo.getTextContent().trim();
										if (strSeqRange.indexOf('-')!= -1){
											try {
											minSequence = Integer.parseInt(strSeqRange.substring(0, strSeqRange.indexOf('-')));
											maxSequence = Integer.parseInt(strSeqRange.substring(strSeqRange.indexOf('-') + 1, strSeqRange.length() ));
											} catch (NumberFormatException e){
												LogManager.getLogger().info(MODULE, "Invalid sequence range. Default range will be used. 0-"+Integer.MAX_VALUE);
												minSequence = 0;
												maxSequence = Integer.MAX_VALUE;
											}
										} else {
											LogManager.getLogger().info(MODULE, "Invalid sequence range. Default range will be used. 0-"+Integer.MAX_VALUE);
										}
									}else if (clientInfo.getNodeName().equalsIgnoreCase("rolling-type") && clientInfo.getTextContent().trim().length()>0){
										try {
											rollingType = stringToInteger(clientInfo.getTextContent().trim(),rollingType);
											/*if (!(rollingType == 1 || rollingType == 2)){
												throw new ParserConfigurationException("Rolling type invalid.");
											}*/
										} catch (Exception e) {
											throw new ParserConfigurationException("Rolling type invalid: " + rollingType);
										}
									}else if (clientInfo.getNodeName().equalsIgnoreCase("rolling-unit") && clientInfo.getTextContent().trim().length()>0){
										try {
											rollingUnit = stringToInteger(clientInfo.getTextContent().trim(),rollingUnit);
										}  catch (Exception e) {
											throw new ParserConfigurationException("Rolling unit invalid: " + rollingUnit);
										}
									}
								}
								
								GTPPrimeClientData gtpClientData = new GTPPrimeClientDataImpl();
								gtpClientData.setClientIP(clientIP);
								gtpClientData.setClientPort(clientPort);
								gtpClientData.setNodeAliveRequest(nodeAliveRequest);
								gtpClientData.setEchoRequestInterval(echoRequestInterval);
								gtpClientData.setRequestExpiryTime(requestExpiryTime);
								gtpClientData.setRequestRetry(requestRetry);
								gtpClientData.setRedirectionIP(redirectionIP);
								gtpClientData.setFileName(fileName);
								gtpClientData.setIsFileSequenceRequired(isFileSequenceRequired);
								gtpClientData.setMinSequenceRange(minSequence);
								gtpClientData.setMaxSequenceRange(maxSequence);
								gtpClientData.setFileLocation(fileLocation);
								gtpClientData.setRollingType(rollingType);
								gtpClientData.setRollingUnit(rollingUnit);
								lstClients.add(gtpClientData);
								addrClientMap.put(InetAddress.getByName(clientIP), gtpClientData);
							}
						}
					}
				}
			}

		} catch (ParserConfigurationException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, e.getMessage());
			}
		} catch (SAXException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, e.getMessage());
			}
		} catch (IOException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, e.getMessage());
			}
		} catch (LoadConfigurationException e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, e.getMessage());
			}
		} catch (Exception e) {
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, e.getMessage());
			}
		} 
	}

	private String getFileName() {
		return getServerContext().getServerHome() + File.separator + "conf" + File.separator + "services" + File.separator + "gtp-prime.xml";
	}

	private ServerContext getServerContext() {
		return serverContext;
	}
	private int stringToInteger(String originalString,int defaultValue) throws Exception{
		int resultValue = defaultValue;
		resultValue = Integer.parseInt(originalString);
		return resultValue;

	}
	@Override
	public GTPPrimeClientData getClient(InetAddress addr) {
		return addrClientMap.get(addr);
	}
	public void reloadConfiguration( String systemRedirectionIP) {
		addrClientMap.clear();
		lstClients.clear();
		readClientConfiguration(systemRedirectionIP);
	}
	
	@Override
	public String toString(){
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		GTPPrimeClientData client = null;
		out.println("\tClient Details: ");
		for (int i=0 ; i<lstClients.size() ; i++){
			client = lstClients.get(i);
			out.println("\tClient Address: " + client.getClientIP() + ":" + client.getClientPort());
			out.println("\t\tNode Alive request: " + client.getNodeAliveRequest());
			out.println("\t\tEcho request Interval: " + client.getEchoRequestInterval());
			out.println("\t\tRequest Expiry time: " + client.getRequestExpiryTime());
			out.println("\t\tRequest retry: " + client.getRequestRetry());
			out.println("\t\tRedirection IP: " + client.getRedirectionIP());
			out.println("\t\tCDR File Name: " + client.getFileName());
			out.println("\t\tFile Location for CDR: " + client.getFileLocation());
			out.println("\t\tFile sequence Enabled: " + client.getIsFileSequenceRequired());
			out.println("\t\tSequence Range: " + client.getMinSequenceRange() + "-" + client.getMaxSequenceRange());
			out.println("\t\tRolling type: " + client.getRollingType());
			out.println("\t\tRolling Unit: " + client.getRollingUnit());
		}
		return stringWriter.toString();
	}
}
