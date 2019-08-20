package com.elitecore.elitesm.web.dashboard.widget.dao.dynaauthclient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthclient.RadiusDynaAuthClientData;

public class DefaultRadiusDynaAuthClientDAO extends BaseDAO implements RadiusDynaAuthClientDAO {
	private static final String MODULE = "DefaultRadiusDynaAuthClientDAO"; 

	private static final String QUERY = "SELECT B.RADIUSDYNAUTHCLIENTADDRESS AS RADIUSDYNAUTHCLIENTADDRESS, B.MAX_CREATETIME AS CREATETIME, "
										+" A.RADIUSDYNAUTHSERVCOAREQUESTS AS RADIUSDYNAUTHSERVCOAREQUESTS,A.SERVDISCONREQUESTS AS SERVDISCONREQUESTS, A.RADIUSDYNAUTHSERVCOAACKS AS RADIUSDYNAUTHSERVCOAACKS, A.RADIUSDYNAUTHSERVCOANAKS AS  RADIUSDYNAUTHSERVCOANAKS,A.RADIUSDYNAUTHSERVDISCONACKS As RADIUSDYNAUTHSERVDISCONACKS,"
										+" A.RADIUSDYNAUTHSERVDISCONNAKS As RADIUSDYNAUTHSERVDISCONNAKS,A.SERVCOAPACKETSDROPPED As SERVCOAPACKETSDROPPED,A.SERVDISCONPACKETSDROPPED AS SERVDISCONPACKETSDROPPED,"
										+" A.SERVDUPCOAREQUESTS AS SERVDUPCOAREQUESTS,A.SERVDUPDISCONREQUESTS AS SERVDUPDISCONREQUESTS,A.RADIUSDYNAUTHSERVUNKNOWNTYPES AS RADIUSDYNAUTHSERVUNKNOWNTYPES,"
										+" A.SERVMALFORMEDCOAREQUESTS AS SERVMALFORMEDCOAREQUESTS,A.SERVMALFORMEDDISCONREQUESTS AS SERVMALFORMEDDISCONREQUESTS,"
										+" A.SERVCOABADAUTHENTICATORS AS SERVCOABADAUTHENTICATORS,A.SERVDISCONBADAUTHENTICATORS AS SERVDISCONBADAUTHENTICATORS FROM RADIUSDYNAUTHCLIENTTABLE A,"
										+" (SELECT RADIUSDYNAUTHCLIENTADDRESS,MAX(CREATETIME) AS MAX_CREATETIME FROM RADIUSDYNAUTHCLIENTTABLE GROUP BY RADIUSDYNAUTHCLIENTADDRESS) B"
										+" WHERE A.RADIUSDYNAUTHCLIENTADDRESS = B.RADIUSDYNAUTHCLIENTADDRESS AND A.CREATETIME = B.MAX_CREATETIME";

	private static final String FETCH_QRY = QUERY + " AND A.CREATETIME >= ((SYSDATE - 1 /1440))"; 

	private static final String INSTANCE_QUERY= "SELECT RADIUSDYNAUTHCLIENTADDRESS FROM RADIUSDYNAUTHCLIENTTABLE WHERE RADIUSDYNAUTHCLIENTADDRESS = ";
	
	@Override
	public List<RadiusDynaAuthClientData> getRadiusDynaAuthClientDataList(String clientsKey) throws Exception {
		List<RadiusDynaAuthClientData> radiusDynaAuthClientDataList = executeQuery(QUERY);
		List<RadiusDynaAuthClientData> radiusDynaAuthClientDatas = new ArrayList<RadiusDynaAuthClientData>();
		if(clientsKey != null && clientsKey.length() > 0 ){
			String clientDataKeys[] = clientsKey.split(",");
			
			for(String clientIp : clientDataKeys){
				boolean isElementExist = false;
				for(RadiusDynaAuthClientData radiusDynaAuthClientData : radiusDynaAuthClientDataList){
					clientIp=clientIp.trim();
					if(clientIp.equals(radiusDynaAuthClientData.getRadiusDynAuthClientAddress())){
						radiusDynaAuthClientDatas.add(radiusDynaAuthClientData);
						isElementExist = true;
					}
				}
				
				if( !isElementExist ){
					RadiusDynaAuthClientData radiusDynaAuthClientData = executeInstanceQuery(INSTANCE_QUERY + "'" +clientIp + "'" +"group by RADIUSDYNAUTHCLIENTADDRESS");
					radiusDynaAuthClientData.setRadiusDynAuthClientAddress(clientIp + " (Invalid Client)");
					radiusDynaAuthClientDatas.add(radiusDynaAuthClientData);
				}
			}
			
			return radiusDynaAuthClientDatas;
		}else{
			return radiusDynaAuthClientDataList;
		}
	}

	private RadiusDynaAuthClientData executeInstanceQuery(String query) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Instance Query : " + query);
		RadiusDynaAuthClientData radiusDynaAuthClientData = new RadiusDynaAuthClientData();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				radiusDynaAuthClientData = new RadiusDynaAuthClientData();
				radiusDynaAuthClientData.setRadiusDynAuthClientAddress(rs.getString("RADIUSDYNAUTHCLIENTADDRESS"));
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
		return radiusDynaAuthClientData;
	}
	
	@Override
	public List<RadiusDynaAuthClientData> getRadiusDynaAuthClientDataList(int interval) throws Exception  {
		return executeQuery(FETCH_QRY);
	}

	public List<RadiusDynaAuthClientData> executeQuery(String query) throws Exception  {
		LogManager.getLogger().debug(MODULE, "Execute Query : " + query);
		List<RadiusDynaAuthClientData>  radiusDynaAuthClientDataList = new ArrayList<RadiusDynaAuthClientData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				RadiusDynaAuthClientData radiusDynaAuthClientData = new RadiusDynaAuthClientData();
				radiusDynaAuthClientData.setRadiusDynAuthClientAddress(rs.getString("RADIUSDYNAUTHCLIENTADDRESS"));
				radiusDynaAuthClientData.setCreateTime(rs.getTimestamp("CREATETIME"));
				radiusDynaAuthClientData.setRadiusDynAuthServCoARequests(rs.getLong("RADIUSDYNAUTHSERVCOAREQUESTS"));
				radiusDynaAuthClientData.setServDisconRequests(rs.getLong("SERVDISCONREQUESTS"));
				radiusDynaAuthClientData.setRadiusDynAuthServCoAAcks(rs.getLong("RADIUSDYNAUTHSERVCOAACKS"));
				radiusDynaAuthClientData.setRadiusDynAuthServCoANaks(rs.getLong("RADIUSDYNAUTHSERVCOANAKS"));
				radiusDynaAuthClientData.setRadiusDynAuthServDisconAcks(rs.getLong("RADIUSDYNAUTHSERVDISCONACKS"));
				radiusDynaAuthClientData.setRadiusDynAuthServDisconNaks(rs.getLong("RADIUSDYNAUTHSERVDISCONNAKS"));
				radiusDynaAuthClientData.setServCoAPacketsDropped(rs.getLong("SERVCOAPACKETSDROPPED"));
				radiusDynaAuthClientData.setServDisconPacketsDropped(rs.getLong("SERVDISCONPACKETSDROPPED"));
				radiusDynaAuthClientData.setServDupCoARequests(rs.getLong("SERVDUPCOAREQUESTS"));
				radiusDynaAuthClientData.setServDupDisconRequests(rs.getLong("SERVDUPDISCONREQUESTS"));
				radiusDynaAuthClientData.setRadiusDynAuthServUnknownTypes(rs.getLong("RADIUSDYNAUTHSERVUNKNOWNTYPES"));
				radiusDynaAuthClientData.setServMalformedCoARequests(rs.getLong("SERVMALFORMEDCOAREQUESTS"));
				radiusDynaAuthClientData.setServMalformedDisconRequests(rs.getLong("SERVMALFORMEDDISCONREQUESTS"));
				radiusDynaAuthClientData.setServCoABadAuthenticators(rs.getLong("SERVCOABADAUTHENTICATORS"));
				radiusDynaAuthClientData.setServDisconBadAuthenticators(rs.getLong("SERVDISCONBADAUTHENTICATORS"));
				
				radiusDynaAuthClientDataList.add(radiusDynaAuthClientData);
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
		return radiusDynaAuthClientDataList;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new DefaultRadiusDynaAuthClientDAO().executeQuery(FETCH_QRY));
	}

}
