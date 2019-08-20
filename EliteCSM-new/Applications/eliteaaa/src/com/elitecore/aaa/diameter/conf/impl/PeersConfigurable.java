package com.elitecore.aaa.diameter.conf.impl;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlTransient;

import com.elitecore.aaa.core.EliteAAADBConnectionManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.config.core.Configurable;
import com.elitecore.core.commons.config.core.annotations.DBRead;
import com.elitecore.core.commons.config.core.annotations.DBReload;
import com.elitecore.core.commons.config.core.annotations.PostRead;
import com.elitecore.core.commons.config.core.annotations.PostReload;
import com.elitecore.core.commons.config.core.annotations.PostWrite;
import com.elitecore.diameterapi.diameter.common.data.PeerData;


public abstract class PeersConfigurable extends Configurable{
	
	/*
	 * DB field constants
	 */
	private static final String PEER_UUID = "PEERUUID";
	private static final String PEER_ID = "PEERID";
	private static final String PEER_NAME = "PEERNAME";
	private static final String HOST_IDENTITY = "HOSTIDENTITY";
	private static final String REALM_NAME = "REALMNAME";
	private static final String REMOTE_ADDRESS = "REMOTEADDRESS";
	private static final String LOCAL_ADDRESS = "LOCALADDRESS";
	private static final String PROFILE_NAME = "PROFILENAME";
	private static final String URIFORMAT = "URIFORMAT";
	private static final String RETRANSMISSIONCOUNT = "RETRANSMISSIONCOUNT";
	private static final String REQUESTTIMEOUT = "REQUESTTIMEOUT";
	private static final String SECONDARY_PEER_NAME = "SECONDARYPEER";
	/*
	 * End of DB field constants
	 */
	
	private List<PeerDetail> peerList;

	//Transient fields
	@XmlTransient
	private Map<String,PeerDetail> peersMap;
	
	
	public PeersConfigurable() {
		this.peerList = new ArrayList<PeerDetail>();
		this.peersMap = new HashMap<String, PeerDetail>();
	}
	
	@DBRead
	public void readPeerConfiguration() throws Exception {

		Connection  connection = null;
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;
		try{						
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(getQueryForPeers());
			
			List<PeerDetail> tempPeerDetailList = new ArrayList<PeerDetail>();
			Map<String,PeerDetail> tempPeerDetailMap = new HashMap<String, PeerDetail>();
			
			PeerDetail peerDetail;
			resultSet = preparedStatement.executeQuery();

			while(resultSet.next()){
				peerDetail = readPeerDetail(resultSet);
				tempPeerDetailList.add(peerDetail);
				tempPeerDetailMap.put(peerDetail.getPeerUUID(), peerDetail);
			}
			
			this.peerList = tempPeerDetailList;
			this.peersMap = tempPeerDetailMap;
			
			LogManager.getLogger().info(getModule(), toString());
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
		
	}
	
	
	@DBReload
	public void reloadPeersConfiguration() throws Exception {
		Connection  connection = null;		
		PreparedStatement preparedStatement = null;
		ResultSet resultSet = null;		
		try{						
			connection = EliteAAADBConnectionManager.getInstance().getConnection();
			preparedStatement = connection.prepareStatement(getQueryForPeers());
			
			List<PeerDetail> tempPeerDetailList = new ArrayList<PeerDetail>();
			Map<String,PeerDetail> tempPeerDetailMap = new HashMap<String, PeerDetail>();
			
			PeerDetail peerDetail;
			resultSet = preparedStatement.executeQuery();			
			while(resultSet.next()){
				
				peerDetail = peersMap.get(resultSet.getString(PEER_UUID));
				
				if(peerDetail != null){
					//if the peer already exists then only reload following parameters
					peerDetail.setPeerName(resultSet.getString(PEER_NAME));
					
					String localAddress = resultSet.getString(LOCAL_ADDRESS) ;
					peerDetail.setLocalAddress(localAddress);

					String profileName = resultSet.getString(PROFILE_NAME) ;
					if(profileName!=null && profileName.length()>0){
						peerDetail.setProfileName(profileName);
					}
				}else{
					peerDetail = readPeerDetail(resultSet);
					tempPeerDetailList.add(peerDetail);
					tempPeerDetailMap.put(peerDetail.getPeerUUID(), peerDetail);
				}
			}
			
			peerList.addAll(tempPeerDetailList);
			peersMap.putAll(tempPeerDetailMap);
			
			LogManager.getLogger().info(getModule(), toString());
		}finally{
			DBUtility.closeQuietly(resultSet);
			DBUtility.closeQuietly(preparedStatement);
			DBUtility.closeQuietly(connection);
		}
		

	}
	
	
	private PeerDetail readPeerDetail(ResultSet resultSet) throws SQLException{
		PeerDetail peerDetail = new PeerDetail();
		peerDetail.setPeerUUID(resultSet.getString(PEER_UUID));
		peerDetail.setPeerId(Long.parseLong(resultSet.getString(PEER_ID)));
		peerDetail.setPeerName(resultSet.getString(PEER_NAME));
		peerDetail.setHostIdentity(resultSet.getString(HOST_IDENTITY));

		String secondaryPeerName = resultSet.getString(SECONDARY_PEER_NAME);
		if (Strings.isNullOrBlank(secondaryPeerName) == false) {
			peerDetail.setSecondaryPeerName(secondaryPeerName);
		}
		
		String realmName = resultSet.getString(REALM_NAME);
		if(realmName != null && realmName.trim().length() > 0 ){
			peerDetail.setRealm(realmName);
		}else{
			LogManager.getLogger().debug(getModule(), "Realm Name is  not Defined,while fetching DiameterPeerProfile Details.");
		}

		String remoteAddress = resultSet.getString(REMOTE_ADDRESS); 
		if(remoteAddress!=null && remoteAddress.length()>0){
			peerDetail.setRemoteAddress(resultSet.getString(REMOTE_ADDRESS));
		}

		String localAddress = resultSet.getString(LOCAL_ADDRESS) ;
		peerDetail.setLocalAddress(localAddress);

		String profileName = resultSet.getString(PROFILE_NAME) ;
		if(profileName!=null && profileName.length()>0){
			peerDetail.setProfileName(profileName);
		}
		
		String uriFormat = resultSet.getString(URIFORMAT);
		if (uriFormat != null && uriFormat.trim().length() > 0) {
			peerDetail.setURI(uriFormat);
		} else {
			peerDetail.setURI(PeerData.DEFAULT_URI_FORMAT);
		}
		
		peerDetail.setRetransmissionCount(resultSet.getInt(RETRANSMISSIONCOUNT));
		peerDetail.setRequestTimeout(resultSet.getLong(REQUESTTIMEOUT));
		

		return peerDetail;
	}

	protected abstract String getQueryForPeers();

	protected abstract String getModule();
	
	@PostRead
	public void postReadProcessing() {
		
	}
	@PostWrite
	public void postWriteProcessing(){
		
	}

	@PostReload
	public void postReloadProcessing(){
		
	}
	@XmlElement(name="peer")
	public List<PeerDetail> getPeerList() {
		return peerList;
	}
	public void setPeerList(List<PeerDetail> peerList) {
		this.peerList = peerList;
	}

	@Override
	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		
		for (PeerDetail peerDetail : getPeerList()) {
			out.println(peerDetail);
		}
		return stringBuffer.toString();
	}
	
}
