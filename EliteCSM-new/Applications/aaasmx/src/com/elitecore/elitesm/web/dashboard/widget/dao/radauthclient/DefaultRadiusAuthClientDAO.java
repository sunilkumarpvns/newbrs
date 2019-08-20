package com.elitecore.elitesm.web.dashboard.widget.dao.radauthclient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthclient.RadiusAuthClientData;

public class DefaultRadiusAuthClientDAO extends BaseDAO implements RadiusAuthClientDAO {
	private static final String MODULE = "DefaultRadiusAuthClientDAO"; 
	
	private static final String QUERY = "SELECT B.RADIUSAUTHCLIENTADDRESS AS RADIUSAUTHCLIENTADDRESS, B.MAX_CREATETIME AS CREATETIME, A.RADIUSAUTHSERVACCESSREQUESTS AS RADIUSAUTHSERVACCESSREQUESTS,"   
    + " A.DUPACCESSREQUESTS DUPACCESSREQUESTS, A.RADIUSAUTHSERVACCESSACCEPTS RADIUSAUTHSERVACCESSACCEPTS, A.RADIUSAUTHSERVACCESSREJECTS RADIUSAUTHSERVACCESSREJECTS,"
    + " A.RADIUSAUTHSERVACCESSCHALLENGES RADIUSAUTHSERVACCESSCHALLENGES, A.MALFORMEDACCESSREQUESTS MALFORMEDACCESSREQUESTS, A.BADAUTHENTICATORS BADAUTHENTICATORS,"
    + " A.RADIUSAUTHSERVPACKETSDROPPED RADIUSAUTHSERVPACKETSDROPPED, A.RADIUSAUTHSERVUNKNOWNTYPES RADIUSAUTHSERVUNKNOWNTYPES FROM RADIUSAUTHCLIENTTABLE A,"
    + " (SELECT RADIUSAUTHCLIENTADDRESS,MAX(CREATETIME) AS MAX_CREATETIME FROM RADIUSAUTHCLIENTTABLE GROUP BY RADIUSAUTHCLIENTADDRESS) B"
    + " WHERE A.RADIUSAUTHCLIENTADDRESS = B.RADIUSAUTHCLIENTADDRESS AND A.CREATETIME = B.MAX_CREATETIME";
	
	private static final String FETCH_QRY = QUERY + " AND A.CREATETIME >= ((SYSDATE - 1 /1440))"; 

	private static final String INSTANCE_QUERY= "SELECT RADIUSAUTHCLIENTADDRESS FROM RADIUSAUTHCLIENTTABLE WHERE RADIUSAUTHCLIENTADDRESS = ";
	
	@Override
	public List<RadiusAuthClientData> getRadiusAuthClientDataList(String clientsKey) throws Exception{
		
		List<RadiusAuthClientData> radiusAuthClientDataList = executeQuery(QUERY);
		List<RadiusAuthClientData> radiusAuthClientDatas = new ArrayList<RadiusAuthClientData>();
		if(clientsKey != null && clientsKey.length() > 0 ){
			String clientDataKeys[] = clientsKey.split(",");
			
			for(String clientIp : clientDataKeys){
				boolean isElementExist = false;
				for(RadiusAuthClientData radiusAuthClientData : radiusAuthClientDataList){
					clientIp=clientIp.trim();
					if(clientIp.equals(radiusAuthClientData.getRadiusAuthClientAddress())){
						radiusAuthClientDatas.add(radiusAuthClientData);
						isElementExist = true;
					}
				}
				
				if( !isElementExist ){
					RadiusAuthClientData radiusAuthClientData = executeInstanceQuery(INSTANCE_QUERY + "'" +clientIp + "'" +"group by RADIUSAUTHCLIENTADDRESS");
					radiusAuthClientData.setRadiusAuthClientAddress(clientIp + " (Invalid Client)");
					radiusAuthClientDatas.add(radiusAuthClientData);
				}
			}
			
			return radiusAuthClientDatas;
		}else{
			return radiusAuthClientDataList;
		}
	}
	
	@Override
	public List<RadiusAuthClientData> getRadiusAuthClientDataList(int interval) throws Exception {
		return executeQuery(FETCH_QRY);
	}
	
	public List<RadiusAuthClientData> executeQuery(String query) throws Exception{
		LogManager.getLogger().debug(MODULE, "Execute Query : " + query);
		List<RadiusAuthClientData>  radiusAuthClientDataList = new ArrayList<RadiusAuthClientData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
					RadiusAuthClientData radiusAuthClientData = new RadiusAuthClientData();
					radiusAuthClientData.setRadiusAuthClientAddress(rs.getString("RADIUSAUTHCLIENTADDRESS"));
					radiusAuthClientData.setCreateTime(rs.getTimestamp("CREATETIME"));
					radiusAuthClientData.setRadiusAuthServAccessRequests(rs.getLong("RADIUSAUTHSERVACCESSREQUESTS"));
					radiusAuthClientData.setDupAccessRequests(rs.getLong("DUPACCESSREQUESTS"));
					radiusAuthClientData.setRadiusAuthServAccessAccepts(rs.getLong("RADIUSAUTHSERVACCESSACCEPTS"));
					radiusAuthClientData.setRadiusAuthServAccessRejects(rs.getLong("RADIUSAUTHSERVACCESSREJECTS"));
					radiusAuthClientData.setRadiusAuthServAccessChallenges(rs.getLong("RADIUSAUTHSERVACCESSCHALLENGES"));
					radiusAuthClientData.setMalformedAccessRequests(rs.getLong("MALFORMEDACCESSREQUESTS"));
					radiusAuthClientData.setBadAuthenticators(rs.getLong("BADAUTHENTICATORS"));
					radiusAuthClientData.setRadiusAuthServPacketsDropped(rs.getLong("RADIUSAUTHSERVPACKETSDROPPED"));
					radiusAuthClientData.setRadiusAuthServUnknownTypes(rs.getLong("RADIUSAUTHSERVUNKNOWNTYPES"));
					radiusAuthClientDataList.add(radiusAuthClientData);
			
			} 
		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetch Radius Account Server data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetch Radius Account Server data. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
		return radiusAuthClientDataList;
	}
	private RadiusAuthClientData executeInstanceQuery(String query) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Instance Query : " + query);
		RadiusAuthClientData radiusAuthClientData = new RadiusAuthClientData();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				radiusAuthClientData = new RadiusAuthClientData();
				radiusAuthClientData.setRadiusAuthClientAddress(rs.getString("RADIUSAUTHCLIENTADDRESS"));
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
		return radiusAuthClientData;
	}

	
	/*private ProjectionList getProjectionList() {
		ProjectionList projectionList = Projections.projectionList();
		projectionList.add(Projections.groupProperty("radiusAuthClientAddress"), "radiusAuthClientAddress");
		projectionList.add(Projections.max("radiusAuthServAccessRequests"), "radiusAuthServAccessRequests");
		projectionList.add(Projections.max("dupAccessRequests"), "dupAccessRequests");
		projectionList.add(Projections.max("radiusAuthServAccessAccepts"), "radiusAuthServAccessAccepts");
		projectionList.add(Projections.max("radiusAuthServAccessRejects"), "radiusAuthServAccessRejects");
		projectionList.add(Projections.max("radiusAuthServAccessChallenges"), "radiusAuthServAccessChallenges");
		projectionList.add(Projections.max("malformedAccessRequests"), "malformedAccessRequests");
		projectionList.add(Projections.max("badAuthenticators"), "badAuthenticators");
		projectionList.add(Projections.max("radiusAuthServPacketsDropped"), "radiusAuthServPacketsDropped");
		projectionList.add(Projections.max("radiusAuthServUnknownTypes"), "radiusAuthServUnknownTypes");
		return projectionList;
	}*/
	
	public static void main(String[] args) throws Exception {
		System.out.println(new DefaultRadiusAuthClientDAO().executeQuery(QUERY)); 
	}

}
