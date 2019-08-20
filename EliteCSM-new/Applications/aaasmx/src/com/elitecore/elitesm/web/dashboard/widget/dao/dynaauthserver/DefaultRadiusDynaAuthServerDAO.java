package com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthserver.RadiusDynaAuthServerData;

public class DefaultRadiusDynaAuthServerDAO extends BaseDAO implements RadiusDynaAuthServerDAO {
	private static final String MODULE = "DefaultRadiusDynaAuthClientDAO"; 

	private static final String QUERY = "SELECT B.DYNAUTHSERVERNAME AS DYNAUTHSERVERNAME, B.MAX_CREATETIME AS CREATETIME, A.RADIUSDYNAUTHSERVERADDRESS AS RADIUSDYNAUTHSERVERADDRESS, A.CLIENTROUNDTRIPTIME AS CLIENTROUNDTRIPTIME, A.CLIENTDISCONREQUESTS AS CLIENTDISCONREQUESTS, A.CLIENTDISCONAUTHONLYREQUESTS AS CLIENTDISCONAUTHONLYREQUESTS, A.CLIENTDISCONRETRANSMISSIONS As CLIENTDISCONRETRANSMISSIONS, A.RADIUSDYNAUTHCLIENTDISCONACKS As RADIUSDYNAUTHCLIENTDISCONACKS,"
										 +" A.RADIUSDYNAUTHCLIENTDISCONNAKS As RADIUSDYNAUTHCLIENTDISCONNAKS, A.CLIENTDISCONNAKAUTHONLYREQUEST AS CLIENTDISCONNAKAUTHONLYREQUEST,  A.CLIENTDISCONNAKSESSNOCONTEXT AS CLIENTDISCONNAKSESSNOCONTEXT, A.CLIENTMALFORMEDDISCONRESPONSES AS CLIENTMALFORMEDDISCONRESPONSES,"
										 +" A.CLIENTDISCONBADAUTHENTICATORS AS CLIENTDISCONBADAUTHENTICATORS, A.CLIENTDISCONPENDINGREQUESTS AS CLIENTDISCONPENDINGREQUESTS, A.CLIENTDISCONTIMEOUTS AS CLIENTDISCONTIMEOUTS, A.CLIENTDISCONPACKETSDROPPED AS CLIENTDISCONPACKETSDROPPED, A.RADIUSDYNAUTHCLIENTCOAREQUESTS AS RADIUSDYNAUTHCLIENTCOAREQUESTS,"
										 +" A.CLIENTCOAAUTHONLYREQUEST AS CLIENTCOAAUTHONLYREQUEST, A.CLIENTCOARETRANSMISSIONS AS CLIENTCOARETRANSMISSIONS, A.RADIUSDYNAUTHCLIENTCOAACKS AS RADIUSDYNAUTHCLIENTCOAACKS, A.RADIUSDYNAUTHCLIENTCOANAKS AS RADIUSDYNAUTHCLIENTCOANAKS, A.CLIENTCOANAKAUTHONLYREQUEST AS CLIENTCOANAKAUTHONLYREQUEST,"
										 +" A.CLIENTCOANAKSESSNOCONTEXT AS CLIENTCOANAKSESSNOCONTEXT, A.CLIENTMALFORMEDCOARESPONSES AS CLIENTMALFORMEDCOARESPONSES, A.CLIENTCOABADAUTHENTICATORS AS CLIENTCOABADAUTHENTICATORS, A.CLIENTCOAPENDINGREQUESTS AS CLIENTCOAPENDINGREQUESTS, A.RADIUSDYNAUTHCLIENTCOATIMEOUTS AS RADIUSDYNAUTHCLIENTCOATIMEOUTS,"
										 +" A.CLIENTCOAPACKETSDROPPED AS CLIENTCOAPACKETSDROPPED, A.CLIENTUNKNOWNTYPES AS CLIENTUNKNOWNTYPES FROM RADIUSDYNAUTHSERVERTABLE  A," 
										 +" (SELECT DYNAUTHSERVERNAME,MAX(CREATETIME) AS MAX_CREATETIME FROM RADIUSDYNAUTHSERVERTABLE GROUP BY DYNAUTHSERVERNAME) B" 
										 +" WHERE A.DYNAUTHSERVERNAME = B.DYNAUTHSERVERNAME AND A.CREATETIME = B.MAX_CREATETIME";

	private static final String FETCH_QRY = QUERY + " AND A.CREATETIME >= ((SYSDATE - 1 /1440))"; 
	
	private static final String INSTANCE_QUERY= "SELECT DYNAUTHSERVERNAME FROM RADIUSDYNAUTHSERVERTABLE WHERE DYNAUTHSERVERNAME = ";
	
	
	@Override
	public List<RadiusDynaAuthServerData> getRadiusDynaAuthServerDataList(String clientsKey) throws Exception {
		List<RadiusDynaAuthServerData> radiusDynaAuthServerDataList = executeQuery(QUERY);
		List<RadiusDynaAuthServerData> radiusDynaAuthServerDatas = new ArrayList<RadiusDynaAuthServerData>();
		if(clientsKey != null && clientsKey.length() > 0 ){
			String clientDataKeys[] = clientsKey.split(",");
			
			for(String clientIp : clientDataKeys){
				boolean isElementExist = false;
				for(RadiusDynaAuthServerData radiusDynaAuthServerData : radiusDynaAuthServerDataList){
					clientIp=clientIp.trim();
					if(clientIp.equals(radiusDynaAuthServerData.getDynauthservername())){
						radiusDynaAuthServerDatas.add(radiusDynaAuthServerData);
						isElementExist = true;
					}
				}
				
				if( !isElementExist ){
					RadiusDynaAuthServerData radiusDynaAuthServerData = executeInstanceQuery(INSTANCE_QUERY + "'" +clientIp + "'" +"group by DYNAUTHSERVERNAME");
					radiusDynaAuthServerData.setDynauthservername(clientIp + " (Invalid Server)");
					radiusDynaAuthServerDatas.add(radiusDynaAuthServerData);
				}
			}
			
			return radiusDynaAuthServerDatas;
		}else{
			return radiusDynaAuthServerDataList;
		}
	}

	@Override
	public List<RadiusDynaAuthServerData> getRadiusDynaAuthServerDataList(int interval) throws Exception  {
		return executeQuery(FETCH_QRY);
	}

	public List<RadiusDynaAuthServerData> executeQuery(String query) throws Exception  {
		LogManager.getLogger().debug(MODULE, "Execute Query : " + query);
		List<RadiusDynaAuthServerData>  radiusDynaAuthServerDataList = new ArrayList<RadiusDynaAuthServerData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				RadiusDynaAuthServerData radiusDynaAuthServerData = new RadiusDynaAuthServerData();
				radiusDynaAuthServerData.setDynauthservername(rs.getString("DYNAUTHSERVERNAME"));
				radiusDynaAuthServerData.setCreateTime(rs.getTimestamp("CREATETIME"));
				radiusDynaAuthServerData.setRadiusDynAuthServerAddress(rs.getString("RADIUSDYNAUTHSERVERADDRESS"));
				radiusDynaAuthServerData.setClientRoundTripTime(rs.getLong("CLIENTROUNDTRIPTIME"));
				radiusDynaAuthServerData.setClientDisconRequests(rs.getLong("CLIENTDISCONREQUESTS")); 
				radiusDynaAuthServerData.setClientDisconAuthOnlyRequests(rs.getLong("CLIENTDISCONAUTHONLYREQUESTS"));
				radiusDynaAuthServerData.setClientDisconRetransmissions(rs.getLong("CLIENTDISCONRETRANSMISSIONS"));
				radiusDynaAuthServerData.setRadiusDynAuthClientDisconAcks(rs.getLong("RADIUSDYNAUTHCLIENTDISCONACKS"));
				radiusDynaAuthServerData.setRadiusDynAuthClientDisconNaks(rs.getLong("RADIUSDYNAUTHCLIENTDISCONNAKS"));
				radiusDynaAuthServerData.setClientDisconNakAuthOnlyRequest(rs.getLong("CLIENTDISCONNAKAUTHONLYREQUEST"));
				radiusDynaAuthServerData.setClientDisconNakSessNoContext(rs.getLong("CLIENTDISCONNAKSESSNOCONTEXT"));
				radiusDynaAuthServerData.setClientMalformedDisconResponses(rs.getLong("CLIENTMALFORMEDDISCONRESPONSES"));				
				radiusDynaAuthServerData.setClientDisconBadAuthenticators(rs.getLong("CLIENTDISCONBADAUTHENTICATORS"));				
				radiusDynaAuthServerData.setClientDisconPendingRequests(rs.getLong("CLIENTDISCONPENDINGREQUESTS"));				
				radiusDynaAuthServerData.setClientDisconTimeouts(rs.getLong("CLIENTDISCONTIMEOUTS"));
				radiusDynaAuthServerData.setClientDisconPacketsDropped(rs.getLong("CLIENTDISCONPACKETSDROPPED"));
				radiusDynaAuthServerData.setRadiusDynAuthClientCoARequests(rs.getLong("RADIUSDYNAUTHCLIENTCOAREQUESTS"));
				radiusDynaAuthServerData.setClientCoAAuthOnlyRequest(rs.getLong("CLIENTCOAAUTHONLYREQUEST"));
				radiusDynaAuthServerData.setClientCoARetransmissions(rs.getLong("CLIENTCOARETRANSMISSIONS"));
				radiusDynaAuthServerData.setRadiusDynAuthClientCoAAcks(rs.getLong("RADIUSDYNAUTHCLIENTCOAACKS"));
				radiusDynaAuthServerData.setRadiusDynAuthClientCoANaks(rs.getLong("RADIUSDYNAUTHCLIENTCOANAKS"));
				radiusDynaAuthServerData.setClientCoANakAuthOnlyRequest(rs.getLong("CLIENTCOANAKAUTHONLYREQUEST"));
				radiusDynaAuthServerData.setClientCoANakSessNoContext(rs.getLong("CLIENTCOANAKSESSNOCONTEXT"));
				radiusDynaAuthServerData.setClientMalformedCoAResponses(rs.getLong("CLIENTMALFORMEDCOARESPONSES"));
				radiusDynaAuthServerData.setClientCoABadAuthenticators(rs.getLong("CLIENTCOABADAUTHENTICATORS"));
				radiusDynaAuthServerData.setClientCoAPendingRequests(rs.getLong("CLIENTCOAPENDINGREQUESTS"));
				radiusDynaAuthServerData.setRadiusDynAuthClientCoATimeouts(rs.getLong("RADIUSDYNAUTHCLIENTCOATIMEOUTS"));
				radiusDynaAuthServerData.setClientCoAPacketsDropped(rs.getLong("CLIENTCOAPACKETSDROPPED"));
				radiusDynaAuthServerData.setClientUnknownTypes(rs.getLong("CLIENTUNKNOWNTYPES"));
				
				radiusDynaAuthServerDataList.add(radiusDynaAuthServerData);
			}

		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetch Radius Client Data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetch Radius Client Data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
		return radiusDynaAuthServerDataList;
	}
	
	private RadiusDynaAuthServerData executeInstanceQuery(String query) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Instance Query : " + query);
		RadiusDynaAuthServerData radiusDynaAuthServerData = new RadiusDynaAuthServerData();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				radiusDynaAuthServerData = new RadiusDynaAuthServerData();
				radiusDynaAuthServerData.setDynauthservername(rs.getString("DYNAUTHSERVERNAME"));
			}
				
		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetch Radius Auth Server Data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetch Radius Auth Server Data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} 
		finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
		return radiusDynaAuthServerData;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new DefaultRadiusDynaAuthServerDAO().executeQuery(FETCH_QRY));
	}

}
