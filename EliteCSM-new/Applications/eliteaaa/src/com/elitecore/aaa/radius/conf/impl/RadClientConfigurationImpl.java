package com.elitecore.aaa.radius.conf.impl;

import java.io.BufferedOutputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.TreeMap;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.core.conf.context.AAAConfigurationContext;
import com.elitecore.aaa.core.data.ClientTypeConstant;
import com.elitecore.aaa.core.data.Vendor;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.conf.impl.ClientsConfigurable.ClientDetail;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.data.RadiusClientDataImpl;
import com.elitecore.aaa.radius.data.RadiusClientProfile;
import com.elitecore.commons.annotations.VisibleForTesting;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.CompositeConfigurable;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.Configuration;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.core.commons.config.core.annotations.ReadOrder;
import com.elitecore.core.commons.configuration.ReadConfigurationFailedException;
import com.elitecore.core.commons.configuration.UpdateConfigurationFailedException;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.util.mbean.data.config.EliteNetConfigurationData;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.license.base.commons.LicenseNameConstants;

@ReadOrder(order = { "vendorConfigurable"
					,"clientsProfileConfigurable"
					,"clientsConfigurable"})
public class RadClientConfigurationImpl extends CompositeConfigurable implements RadClientConfiguration{

	private static final String MODULE = "RAD-CLIENT-CONF-IMPL";
	private static final String KEY = "CLIENTS";
	
	private Map<String,RadClientData> clientsMap = null;
	/*
	 * This map will contain the clients for which the client is configured with 
	 * Netmask
	 */
	private Map<String,RadClientData> networkClientsMap = null;
	private Map<String, ClientDetail> clientDetailMap;
	private Map <String,RadClientData> unsupportedClients;
	private Map <String,RadClientData> clientsNosExceededThanLicenseTaken;
	private List<String> lstClientAddresses;
	protected static int DEFAULT_QUERY_TIMEOUT = 100;

	//added for the change of XML reading
	@Configuration private VendorConfigurable vendorConfigurable;
	@Configuration private ClientsConfigurable clientsConfigurable;
	@Configuration private ClientProfileConfigurable clientsProfileConfigurable;
	
	public RadClientConfigurationImpl() {
		this.clientsMap = new TreeMap<String,RadClientData>(String.CASE_INSENSITIVE_ORDER);
		this.unsupportedClients = new TreeMap<String,RadClientData>(String.CASE_INSENSITIVE_ORDER);
		this.clientsNosExceededThanLicenseTaken = new TreeMap<String,RadClientData>(String.CASE_INSENSITIVE_ORDER);
		this.lstClientAddresses = new ArrayList<String>();
		this.networkClientsMap = new TreeMap<String, RadClientData>(String.CASE_INSENSITIVE_ORDER);
		this.clientDetailMap = new TreeMap<String, ClientDetail>(String.CASE_INSENSITIVE_ORDER);
	}
	
	/***
	 * This method is used by MIB counters to initialize.
	 * 
	 * @return List of ClientAddresses (lstClientAddresses)
	 */
	@Override
	public List<String>getClientAddresses(){
		return lstClientAddresses;		
	}

	public RadClientData getClientData(String clientIp) {
		if (clientIp == null) {
			return null;
		}

		if (clientsMap != null) {
			RadClientData radClientData = clientsMap.get(clientIp);
			if (radClientData != null) {
				return radClientData;
			}
		}
		return getNetworkedClientData(clientIp);
		/*
		 *  At the time validating request for client detail we should use Map of valid clients.
		 *   
		else if(unsupportedClients!=null && unsupportedClients.get(clientIp)!=null){
			return unsupportedClients.get(clientIp);
		}else if(clientsNosExceededThanLicenseTaken!=null && clientsNosExceededThanLicenseTaken.get(clientIp)!=null){
			return clientsNosExceededThanLicenseTaken.get(clientIp);
		}
		 */
	}



	/* Private Methods */
	private RadClientData getNetworkedClientData(String clientIP){
		try{
			byte[] clientAddress = InetAddress.getByName(clientIP).getAddress();
			Long longClientIP = RadiusUtility.bytesToLong(clientAddress);
			if(longClientIP != null){
				for(RadClientData clientData : networkClientsMap.values()){
					if(checkForNetorkBelongingness(clientData,longClientIP)){
						return clientData;
					}
				}
			}
		}catch(UnknownHostException ex){
			LogManager.getLogger().debug(MODULE, "No IP Address found for host:" + clientIP);
			LogManager.getLogger().trace(ex);
		}catch(SecurityException ex){
			LogManager.getLogger().trace(ex);
		}
		return null;
	}

	private boolean checkForNetorkBelongingness(RadClientData clientData,Long clientIp){
		if(clientData.getStartAddress() != null && clientData.getEndAddress() != null){
			if(clientIp >= clientData.getStartAddress() && clientIp <= clientData.getEndAddress())
				return true;
		}
		return false;
	}

	/**
	 * Load RadiusClients
	 * @return
	 */
	private String getFileName(){
		AAAServerContext serverContext = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		return serverContext.getServerHome() + File.separator + "conf" + File.separator +"clients.xml";
	}

	
	@PostRead	
	public void postReadProcessing(){
		AAAServerContext serverContext = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		
		if(getClientProfileConfigurable().countRadiusClientProfiles() == 0){
			return;
		}
		
		/**
		 * Do processing required  after reading of Client Data
		 */
		
		StringBuilder strVendorNotSupportedIpList= new StringBuilder();
		StringBuilder strLicenseExceededIpList= new StringBuilder();

		
		List<ClientDetail> clientList = getClientsConfigurable().getClients();
		if(clientList==null)
			return;
		
		Map<String, RadClientData> tempClientsMap = new TreeMap<String, RadClientData>(String.CASE_INSENSITIVE_ORDER);
		Map<String, RadClientData> tempNetworkClientsMap = new TreeMap<String, RadClientData>(String.CASE_INSENSITIVE_ORDER);
		Map<String, ClientDetail> tempClientDetailMap = new TreeMap<String, ClientDetail>(String.CASE_INSENSITIVE_ORDER);
		List<String> tmpClientAddresses = new ArrayList<String>();
		
		int numOfClientsConfigured =clientList.size();
		int addedClientsCount = 0;
		outer:
			for(int k = 0; k <numOfClientsConfigured ; k++){
				RadiusClientProfile profile = null ;
				ClientDetail client = clientList.get(k);
				profile = getClientProfileConfigurable().getRadiusClientProfileForName(client.getProfileName());
				
				if(profile == null){
					LogManager.getLogger().error(MODULE, "Error adding client " + client.getStrClientIp() + ", Reason: Client Profile(" + client.getProfileName() + ") not found");
					continue outer;
				}

				List<RadClientData> clients = RadiusClientDataImpl.parseClientIP(client, profile);
				for (RadClientData clientData : clients){
					//ClientTypeConstant.isLicenseRequired(clientData.getVendorType()) check added for EliteAAA-1761 for bypassing the license check for NAS,RADIUSX and CUSTOMX clients 
					if( ClientTypeConstant.isLicenseRequired(clientData.getVendorType()) && !serverContext.isLicenseValid(LicenseNameConstants.SYSTEM_VENDOR_TYPE,String.valueOf(ClientTypeConstant.getClientType(clientData.getVendorType())))){											
						addUnsupportedVendorsClients(clientData);
						strVendorNotSupportedIpList.append(clientData.getClientIp()+", ");
					}else if(!serverContext.isLicenseValid(LicenseNameConstants.SYSTEM_CLIENTS, String.valueOf(addedClientsCount + 1))){
						addLicenseExceededClients(clientData);
						strLicenseExceededIpList.append(clientData.getClientIp()+", ");
					}else{
						if(clientData.containsNetworkAddress()){
							tempNetworkClientsMap.put(clientData.getClientIp(),clientData);
						}else{
							tempClientsMap.put(clientData.getClientIp(),clientData);
						}
						tmpClientAddresses.add(clientData.getClientIp());
						addedClientsCount++;
					}
					tempClientDetailMap.put(clientData.getClientIp(), client);
				}
			}
		lstClientAddresses = tmpClientAddresses;
		clientsMap = tempClientsMap;
		networkClientsMap = tempNetworkClientsMap;
		clientDetailMap = tempClientDetailMap;
		// generating the alert if no of clients exceed a limit
		if(lstClientAddresses.size() > 1000)
			serverContext.generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Too many clients configured. This will affect system performance.", 0,
					"Too many clients configured. This will affect system performance.");
		
	}

	@PostWrite
	public void postWriteProcessing() {

	}
	
	@PostReload
	public void postReloadProcessing() {
		postReadProcessing();
	}
	
	private List<RadiusClientProfile> removeUnknownVendorFromProfile(List<RadiusClientProfile> radiusClientProfiles) {
		int numOFProfile = radiusClientProfiles.size();
		RadiusClientProfile radiusClientProfile;
		for(int i=0;i<numOFProfile;i++){
			radiusClientProfile = radiusClientProfiles.get(i);
			List<Vendor> tempSupportedVendors = new ArrayList<Vendor>();
			List<Vendor> supportedVendors = radiusClientProfile.getSupportedVendorsList();
			if(supportedVendors!=null){
				int noOfVendor = supportedVendors.size();
				for(int j=0;j<noOfVendor;j++){
					if(getVendorConfigurable().getVendorForID(supportedVendors.get(j).getVendorID()) != null){
						tempSupportedVendors.add(supportedVendors.get(j));
					}
				}
			}
			radiusClientProfile.setSupportedVendorsList(tempSupportedVendors);
		}
		
		return radiusClientProfiles;
	}

	public boolean isValidClient(String clientIP) {
		if(clientIP == null)
			return false;
		
		
		if(clientsMap.containsKey(clientIP)){
			return true;
		}else{
			try{
				byte[] clientAddress = InetAddress.getByName(clientIP).getAddress();
				Long longClientIP = RadiusUtility.bytesToLong(clientAddress);
				for(RadClientData clientData : networkClientsMap.values()){
					if(checkForNetorkBelongingness(clientData,longClientIP)){
						return true;
					}
				}
			}catch(UnknownHostException ex){
				
			}catch(SecurityException ex){
				
			}
		}
		return false;
	}


	public void removeClient(String ip) {
		clientsMap.remove(ip);	
		networkClientsMap.remove(ip);
	}


	public String getClientVendorID(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getVendorId().toString();
		
		return null;
	}


	public String getClientVendorType(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getVendorType().toString();
		
		return null;
	}
	
	@Override
	public boolean isPortalTypeClient(String clientIp) {
		String clientType = getClientVendorType(clientIp);
		return "portal".equalsIgnoreCase(clientType) || "wimaxportal".equalsIgnoreCase(clientType);
	}


	public String getClientSharedSecret(String strClientIp, int packetType) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getSharedSecret(packetType);
		return null;
	}
	
	public long getClientRequestExpiryTime(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getTimeout();
		
		return -1;
	}

	public String getClientPolicie(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getClientPolicy();
		return null;

	}

	public List<Vendor> getSupportedVendorList(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getSupportedVendorsList();
		return null;
	}

	public boolean isSupportedVendorId(String strClientIp, long vendorId) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.isSupportedVendorId(vendorId);
		return false;
	}

	public boolean isSupportedVendorId(String strClientIp, String vendorId) {	
		if(strClientIp == null)
			return false;
		
		try{
			return isSupportedVendorId(strClientIp,Long.parseLong(vendorId));
		}catch(NumberFormatException ex){
			return false;
		}
	}

	public List<byte[]> getDNSList(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getDnsList();
		return null;
	}

	public String getHA_IPAddress(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getHAAddress();
		return null;
	}

	public List<String> getClientList() {
		ArrayList<String> clientList = new ArrayList<String>();
		if(clientsMap!=null){
			clientList.addAll(clientsMap.keySet());
		}
		if(networkClientsMap!=null){
			clientList.addAll(networkClientsMap.keySet());
		}
		else
			clientList = null;
		return clientList;		
	}

	public String getNasHotlinePolicy(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getHotlinePolicy();
		return null;
	}

	public Map<String, List<String>> getUserIdentityOfClients() {
		Map<String,List<String>> userIdentityOfClients = new HashMap<String,List<String>>();
		for(RadClientData clientData : clientsMap.values()){
			userIdentityOfClients.put(clientData.getClientIp(), clientData.getUserIdentities());
		}
		for(RadClientData clientData : networkClientsMap.values()){
			userIdentityOfClients.put(clientData.getClientIp(), clientData.getUserIdentities());
		}
		return userIdentityOfClients;	
	}

	public List<String> getUserIdentitiesOfClient(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getUserIdentities();
		return null;
	}

	public Map<String, List<byte[]>> getDNSIPListForClients() {
		Map<String,List<byte[]>> dnsListForClients = new HashMap<String,List<byte[]>>();
		for(RadClientData clientData : clientsMap.values()){
			dnsListForClients.put(clientData.getClientIp(), clientData.getDnsList());
		}
		for(RadClientData clientData : networkClientsMap.values()){
			dnsListForClients.put(clientData.getClientIp(), clientData.getDnsList());
		}
		return dnsListForClients;		
	}

	public List<byte[]> getDNSIPListForClient(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getDnsList();
		
		return null;
	}

	public String getFramedPool(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getFramedPoolName();
		return null;
	}

	public int getSupportedClientCount() {
		return clientsMap.size() + networkClientsMap.size();
	}

	public String getPrepaidStandard(String strClientIp) {
		RadClientData radClientData = clientsMap.get(strClientIp);
		if(radClientData == null)
			radClientData = getNetworkedClientData(strClientIp);
		
		if(radClientData != null)
			return radClientData.getPrepaidStandard();
		return null;
	}

	public boolean updateConfiguration(List lstConfiguration) throws UpdateConfigurationFailedException {
		AAAServerContext serverContext = ((AAAConfigurationContext)getConfigurationContext()).getServerContext();
		boolean bSuccess = true;
		String strReturn = "";
		//strReturn = getServerContext().getServerHome() + File.separator + "conf";
		strReturn = serverContext.getServerHome() + File.separator + "tempconf";
		Iterator iterator = lstConfiguration.iterator();
		while(iterator.hasNext()) {
			EliteNetConfigurationData eliteNetConfigurationData = (EliteNetConfigurationData)iterator.next();

			String configurationKey = eliteNetConfigurationData.getNetConfigurationKey();

			if(configurationKey != null && configurationKey.equalsIgnoreCase(KEY)) {				
				try {
					bSuccess = write(strReturn, "clients.xml", eliteNetConfigurationData.getNetConfigurationData());
				} catch (IOException e) {
					bSuccess = false;
					LogManager.getLogger().warn(MODULE, "Error while writing file. Reason:" + e.getMessage());
					LogManager.getLogger().trace(MODULE,e);
					throw new UpdateConfigurationFailedException("Update Elite AAA Server configuration failed. Reason: " + e.getMessage(),e);
				}
			}				
		}

		return bSuccess;
	}

	
	public String getKey(){
		return KEY;
	}

	@Override
	public String getAllClients() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		int srNo = 1;		
		out.println();

		if((clientsMap == null || clientsMap.size() == 0) && (networkClientsMap == null || networkClientsMap.size() == 0)){
			out.println(" No Active Clients found.");
		}
		
		if(clientsMap != null && clientsMap.size() > 0){
			out.println(getDottedLine());
			out.println("                                 Active Clients");
			out.println(getDottedLine());
			for(Entry<String, RadClientData> entry: clientsMap.entrySet()){
				out.println(" " + srNo++ + " ) " + entry.getKey());
			}
		}
		if(networkClientsMap != null && networkClientsMap.size() > 0){
			out.println(getDottedLine());
			out.println("                          Active Network Type Clients");
			out.println(getDottedLine());
			for(Entry<String, RadClientData> entry: networkClientsMap.entrySet()){
				out.println(" " + srNo++ + " ) " + entry.getKey() + "/" + entry.getValue().getSubnetMask());
			}
		}
		
	
		srNo=1;
		if(unsupportedClients != null && unsupportedClients.size() > 0){
			out.println(getDottedLine());
			out.println("               Inactive Clients - Unsupported Vendors Clients");
			out.println(getDottedLine());
			for(Entry<String, RadClientData> entry: unsupportedClients.entrySet())
				out.println(" " + srNo++ + " ) " + entry.getKey());		
		}
		srNo=1;
		if(clientsNosExceededThanLicenseTaken != null && clientsNosExceededThanLicenseTaken.size() > 0){
			out.println(getDottedLine());
			out.println("           Inactive Clients - License Exceeded for No. of Clients");
			out.println(getDottedLine());
			for(Entry<String, RadClientData> entry: clientsNosExceededThanLicenseTaken.entrySet())
				out.println(" " + srNo++ + " ) " + entry.getKey());		
		}
	
		return stringBuffer.toString();
	}

	private void addLicenseExceededClients(RadClientData clientData){
		if(clientsNosExceededThanLicenseTaken!=null){
			clientsNosExceededThanLicenseTaken.put(clientData.getClientIp(), clientData);
		}else{
			clientsNosExceededThanLicenseTaken = new TreeMap<String, RadClientData>(String.CASE_INSENSITIVE_ORDER);
			clientsNosExceededThanLicenseTaken.put(clientData.getClientIp(), clientData);
		}
	}

	private void addUnsupportedVendorsClients(RadClientData clientData){
		if(unsupportedClients!=null){
			unsupportedClients.put(clientData.getClientIp(), clientData);
		}else{
			unsupportedClients = new TreeMap<String, RadClientData>(String.CASE_INSENSITIVE_ORDER);
			unsupportedClients.put(clientData.getClientIp(), clientData);
		}
	}

	public RadClientData getUnsupportedVendorClientData(String strIP) {
		return unsupportedClients.get(strIP);
	}

	public RadClientData getLicenseExceededClientsData(String strIP) {
		return clientsNosExceededThanLicenseTaken.get(strIP);
	}

	public List<String> getUnsupportedVendorClientList(){
		if(unsupportedClients!=null)
			return new ArrayList<String>(unsupportedClients.keySet());
		else
			return null;
	}

	public List<String> getAddedClientExceededLicenseList(){
		if(clientsNosExceededThanLicenseTaken!=null)
			return new ArrayList<String>(clientsNosExceededThanLicenseTaken.keySet());
		else
			return null;
	}

	private String getDottedLine(){
		return "--------------------------------------------------------------------------------";			   
	}

	
	public EliteNetConfigurationData getNetConfigurationData(){
		try {
			return read(getFileName(),MODULE, getKey());
		} catch (ReadConfigurationFailedException e) {
			LogManager.getLogger().error(MODULE, e.getMessage());
		}		
		return null;
	}


	@SuppressWarnings("unchecked")
	@Override
	public Map<String, ArrayList<HashMap<String, String>>> clientDetails() {
		Map<String, ArrayList<HashMap<String, String>>> resultClientMap = new HashMap<String, ArrayList<HashMap<String,String>>>();

		ArrayList<HashMap<String, String>> tempClientDataList = new ArrayList<HashMap<String,String>>();;
		HashMap<String, String> clientDataMap = null;
		RadClientData clientData = null;
		if(clientsMap != null && clientsMap.size() > 0){
			Map<String, RadClientData> tempClientMap = (TreeMap<String, RadClientData>)((TreeMap<String, RadClientData>) clientsMap).clone();	
			for(Entry<String, RadClientData> entry: tempClientMap.entrySet()){
				clientData = entry.getValue();
				clientDataMap = new HashMap<String, String>();
				clientDataMap.put("IP", clientData.getClientIp());
				clientDataMap.put("EXPIRY_TIME", String.valueOf(clientData.getTimeout()));
				clientDataMap.put("PROFILE_NAME", clientData.getProfileName());
				clientDataMap.put("SHARED_SECRET", clientDetailMap.get(clientData.getClientIp()).getStrSharedSecret());
				tempClientDataList.add(clientDataMap);
			}
		}

		//adding the networked clients list in the client details
		if(networkClientsMap != null && networkClientsMap.size() > 0){
			Map<String, RadClientData> tempClientMap = (TreeMap<String, RadClientData>)((TreeMap<String, RadClientData>)networkClientsMap).clone();	
			for(Entry<String, RadClientData> entry: tempClientMap.entrySet()){
				clientData = entry.getValue();
				clientDataMap = new HashMap<String, String>();
				clientDataMap.put("IP", clientData.getClientIp());
				clientDataMap.put("EXPIRY_TIME", String.valueOf(clientData.getTimeout()));
				clientDataMap.put("PROFILE_NAME", clientData.getProfileName());
				clientDataMap.put("SHARED_SECRET", clientDetailMap.get(clientData.getClientIp()).getStrSharedSecret());
				tempClientDataList.add(clientDataMap);
			}
		}
		resultClientMap.put("ACTIVE", tempClientDataList);
		
		if(unsupportedClients != null && unsupportedClients.size() > 0){
			tempClientDataList = new ArrayList<HashMap<String,String>>();
			Map<String, RadClientData> tempUnsupportedClients = (TreeMap<String, RadClientData>)((TreeMap<String, RadClientData>)unsupportedClients).clone();
			for(Entry<String, RadClientData> entry: tempUnsupportedClients.entrySet()){
				clientData = entry.getValue();
				clientDataMap = new HashMap<String, String>();  
				clientDataMap.put("IP", clientData.getClientIp());
				clientDataMap.put("EXPIRY_TIME", String.valueOf(clientData.getTimeout()));
				clientDataMap.put("PROFILE_NAME", clientData.getProfileName());
				clientDataMap.put("SHARED_SECRET", clientDetailMap.get(clientData.getClientIp()).getStrSharedSecret());
				tempClientDataList.add(clientDataMap);
			}
			resultClientMap.put("UNSUPPORTED_VENDORS", tempClientDataList);
		}

		if(clientsNosExceededThanLicenseTaken != null && clientsNosExceededThanLicenseTaken.size() > 0){
			tempClientDataList = new ArrayList<HashMap<String,String>>();
			Map<String, RadClientData> tempClientNosExceededMap = (TreeMap<String, RadClientData>)((TreeMap<String, RadClientData>)clientsNosExceededThanLicenseTaken).clone(); 	
			for(Entry<String, RadClientData> entry: tempClientNosExceededMap.entrySet()){
				clientData = entry.getValue();
				clientDataMap = new HashMap<String, String>();	
				clientDataMap.put("IP", clientData .getClientIp());
				clientDataMap.put("EXPIRY_TIME", String.valueOf(clientData .getTimeout()));
				clientDataMap.put("PROFILE_NAME", clientData .getProfileName());
				clientDataMap.put("SHARED_SECRET", clientDetailMap.get(clientData.getClientIp()).getStrSharedSecret());
				tempClientDataList.add(clientDataMap);
			}
			resultClientMap.put("LICENSE_EXCEEDED", tempClientDataList);
		}


		return resultClientMap;
	}

	protected final EliteNetConfigurationData read(String strFile,String moduleName,String confKey) throws ReadConfigurationFailedException{
		EliteNetConfigurationData configurationData = new EliteNetConfigurationData();
		StreamResult result = null;
		Document document = null;
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		factory.setIgnoringComments(true);
		factory.setIgnoringElementContentWhitespace(true);
		factory.setValidating(false);
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		try {			
			File file = new File(strFile);
			DocumentBuilder documentBuilder = factory.newDocumentBuilder();
			document = documentBuilder.parse(file);
			TransformerFactory tFactory =TransformerFactory.newInstance();
	        Transformer transformer = tFactory.newTransformer();
	        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
	        DOMSource source = new DOMSource(document);
	        result = new StreamResult(outputStream);
	        transformer.transform(source, result);	     
	        configurationData.setNetConfigurationKey(confKey);
	        configurationData.setNetConfigurationData(outputStream.toByteArray());	        
	        LogManager.getLogger().info(MODULE, "Reading configuration from " +  file.getAbsoluteFile());
		}catch(Exception e) {			
			LogManager.getLogger().trace(MODULE, "Error occured while reading configuration reason, " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e.getMessage());			
			throw new ReadConfigurationFailedException("Error occured while reading configuration for " + moduleName);
		}finally {
			if(outputStream != null) {
				try {
					outputStream.close();
				}catch(Exception exp) {					
					LogManager.getLogger().warn(MODULE, "Error occured while closing stream for " + moduleName);
				}
			}
		}
		return configurationData;
	}
	
	public final boolean write(String base,String filename, byte[] fileContent)throws UpdateConfigurationFailedException, IOException {
		boolean bSuccess = true;
		boolean bFileCreated = true;
		boolean bReadWritePermission=  false;
		File file = new File(base);
		if(!file.exists()) {
			bFileCreated = file.mkdirs();
			LogManager.getLogger().info(MODULE,"Creating a directory : " + file.getAbsoluteFile());
		}	
		File destFile = new File(file,filename);
		if(destFile!= null && !destFile.exists() && bFileCreated) {
			bFileCreated = destFile.createNewFile();
			LogManager.getLogger().info(MODULE, "Creating configuration file : " +  destFile.getAbsoluteFile());
		}
		bReadWritePermission = destFile.canRead() & destFile.canWrite();
		if(bFileCreated && bReadWritePermission) {
			BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(destFile));
			stream.write(fileContent);
			stream.close();
			LogManager.getLogger().info(MODULE, "Writing updated configuration in " +  destFile.getAbsoluteFile());
		}else{			
			LogManager.getLogger().warn(MODULE, "Configuration file creation failed or read-write accees not allowed.");
			bSuccess = false; 
		}
		return bSuccess;
	}

	@Override
	public boolean isEligible(Class<? extends Configurable> configurableClass) {
		// TODO Auto-generated method stub
		return false;
	}
	
	@VisibleForTesting
	protected VendorConfigurable getVendorConfigurable() {
		return vendorConfigurable;
	}
	
	@VisibleForTesting
	protected ClientsConfigurable getClientsConfigurable() {
		return clientsConfigurable;
	}
	
	@VisibleForTesting
	protected ClientProfileConfigurable getClientProfileConfigurable() {
		return clientsProfileConfigurable;
	}
}
