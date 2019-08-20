package com.elitecore.elitesm.web.dashboard.widget.dao.radacctclient;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctclient.RadiusAcctClientData;

public class DefaultRadiusAcctClientDAO extends BaseDAO implements RadiusAcctClientDAO {
	private static final String MODULE = "DefaultRadiusAuthClientDAO"; 

	private static final String QUERY = "SELECT B.RADIUSACCCLIENTADDRESS AS RADIUSACCCLIENTADDRESS, B.MAX_CREATETIME AS CREATETIME, A.RADIUSACCSERVPACKETSDROPPED AS RADIUSACCSERVPACKETSDROPPED,"   
		+ " A.RADIUSACCSERVDUPREQUESTS AS RADIUSACCSERVDUPREQUESTS, A.RADIUSACCSERVRESPONSES AS RADIUSACCSERVRESPONSES, A.RADIUSACCSERVBADAUTHENTICATORS AS  RADIUSACCSERVBADAUTHENTICATORS, A.RADIUSACCSERVREQUESTS As RADIUSACCSERVREQUESTS,"
		+ " A.RADIUSACCSERVMALFORMEDREQUESTS AS RADIUSACCSERVMALFORMEDREQUESTS, A.RADIUSACCSERVNORECORDS AS RADIUSACCSERVNORECORDS, A.RADIUSACCSERVUNKNOWNTYPES AS RADIUSACCSERVUNKNOWNTYPES FROM RADIUSACCCLIENTTABLE A,"
		+ " (SELECT RADIUSACCCLIENTADDRESS,MAX(CREATETIME) AS MAX_CREATETIME FROM RADIUSACCCLIENTTABLE GROUP BY RADIUSACCCLIENTADDRESS) B"
		+ " WHERE A.RADIUSACCCLIENTADDRESS = B.RADIUSACCCLIENTADDRESS AND A.CREATETIME = B.MAX_CREATETIME";

	private static final String FETCH_QRY = QUERY + " AND A.CREATETIME >= ((SYSDATE - 1 /1440))"; 

	private static final String INSTANCE_QUERY= "SELECT RADIUSACCCLIENTADDRESS FROM RADIUSACCCLIENTTABLE WHERE RADIUSACCCLIENTADDRESS = ";
	
	@Override
	public List<RadiusAcctClientData> getRadiusAcctClientDataList(String clientsKey) throws Exception {
		List<RadiusAcctClientData> radiusAcctClientDataList = executeQuery(QUERY);
		List<RadiusAcctClientData> radiusAcctClientDatas = new ArrayList<RadiusAcctClientData>();
		if(clientsKey != null && clientsKey.length() > 0 ){
			String clientDataKeys[] = clientsKey.split(",");
			
			for(String clientIp : clientDataKeys){
				boolean isElementExist = false;
				for(RadiusAcctClientData radiusAcctClientData : radiusAcctClientDataList){
					clientIp=clientIp.trim();
					if(clientIp.equals(radiusAcctClientData.getRadiusAccClientAddress())){
						radiusAcctClientDatas.add(radiusAcctClientData);
						isElementExist = true;
					}
				}
				
				if( !isElementExist ){
					RadiusAcctClientData radiusAcctClientData = executeInstanceQuery(INSTANCE_QUERY + "'" +clientIp + "'" +"group by RADIUSACCCLIENTADDRESS");
					radiusAcctClientData.setRadiusAccClientAddress(clientIp + " (Invalid Client)");
					radiusAcctClientDatas.add(radiusAcctClientData);
				}
			}
			
			return radiusAcctClientDatas;
		}else{
			return radiusAcctClientDataList;
		}
	}

	@Override
	public List<RadiusAcctClientData> getRadiusAcctClientDataList(int interval) throws Exception  {
		return executeQuery(FETCH_QRY);
	}

	public List<RadiusAcctClientData> executeQuery(String query) throws Exception  {
		LogManager.getLogger().debug(MODULE, "Execute Query : " + query);
		List<RadiusAcctClientData>  radiusAcctClientDataList = new ArrayList<RadiusAcctClientData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				RadiusAcctClientData radiusAcctClientData = new RadiusAcctClientData();
				radiusAcctClientData.setRadiusAccClientAddress(rs.getString("RADIUSACCCLIENTADDRESS"));
				radiusAcctClientData.setCreateTime(rs.getTimestamp("CREATETIME"));
				radiusAcctClientData.setRadiusAccServPacketsDropped(rs.getLong("RADIUSACCSERVPACKETSDROPPED"));
				radiusAcctClientData.setRadiusAccServRequests(rs.getLong("RADIUSACCSERVREQUESTS"));
				radiusAcctClientData.setRadiusAccServDupRequests(rs.getLong("RADIUSACCSERVDUPREQUESTS"));
				radiusAcctClientData.setRadiusAccServResponses(rs.getLong("RADIUSACCSERVRESPONSES"));
				radiusAcctClientData.setRadiusAccServBadAuthenticators(rs.getLong("RADIUSACCSERVBADAUTHENTICATORS"));
				radiusAcctClientData.setRadiusAccServMalformedRequests(rs.getLong("RADIUSACCSERVMALFORMEDREQUESTS"));
				radiusAcctClientData.setRadiusAccServNoRecords(rs.getLong("RADIUSACCSERVNORECORDS"));
				radiusAcctClientData.setRadiusAccServUnknownTypes(rs.getLong("RADIUSACCSERVUNKNOWNTYPES"));
				radiusAcctClientDataList.add(radiusAcctClientData);
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
		return radiusAcctClientDataList;
	}
	
	private RadiusAcctClientData executeInstanceQuery(String query) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Instance Query : " + query);
		RadiusAcctClientData radiusAcctClientData = new RadiusAcctClientData();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				radiusAcctClientData = new RadiusAcctClientData();
				radiusAcctClientData.setRadiusAccClientAddress(rs.getString("RADIUSACCCLIENTADDRESS"));
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
		return radiusAcctClientData;
	}

	
	public static void main(String[] args) throws Exception {
		System.out.println(new DefaultRadiusAcctClientDAO().executeQuery(FETCH_QRY));
	}

}
