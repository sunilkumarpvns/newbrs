package com.elitecore.elitesm.web.dashboard.widget.dao.nasesistatistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.nasesistatistics.NasESIStatData;

public class DefaultNASESIStatisticsDAO extends BaseDAO implements NASESIStatisticsDAO {
	private static final String MODULE = "DefaultNASESIStatisticsDAO";
    private static final String TABLE="RADIUSDYNAUTHSERVERTABLE";	

	private static final String QUERY_LATEST_ESI_STATISTICS =   "SELECT " +
							                                    "DYNAUTHSERVERNAME  AS DYNAUTHSERVERNAME,"+
									                            "CLIENTDISCONREQUESTS AS DISCONNECTREQ," +
									                            "RADIUSDYNAUTHCLIENTDISCONACKS AS DISCONNECTACKS," +
									                            "RADIUSDYNAUTHCLIENTDISCONNAKS AS DISCONNECTNAKS,"+ 
									                            "CLIENTDISCONTIMEOUTS AS DISCONNECTTIMEOUT, " +
									                            "RADIUSDYNAUTHCLIENTCOAREQUESTS AS COAREQ, "+
									                            "RADIUSDYNAUTHCLIENTCOAACKS AS COAACKS, "+
									                            "RADIUSDYNAUTHCLIENTCOANAKS  AS COANAKS, "+
									                            "RADIUSDYNAUTHCLIENTCOATIMEOUTS AS COATIMEOUT, "+
									                            "RADIUSDYNAUTHSERVERADDRESS AS RADIUSDYNAUTHSERVERADDRESS, "+
									                            "CLIENTPORTNUMBER AS CLIENTPORTNUMBER, "+
									                            "CREATETIME AS CREATETIME " +
									                            "FROM "+TABLE+"  WHERE CREATETIME IN (SELECT MAX(CREATETIME) FROM  "+TABLE+" GROUP BY DYNAUTHSERVERNAME) ORDER BY DYNAUTHSERVERNAME";
	
	private static final String QUERY_ESI_DETAIL="SELECT "+
												 "DYNAUTHSERVERNAME AS DYNAUTHSERVERNAME,"+
												 "CLIENTDISCONREQUESTS AS DISCONNECTREQ," +
						                         "RADIUSDYNAUTHCLIENTDISCONACKS AS DISCONNECTACKS," +
						                         "RADIUSDYNAUTHCLIENTDISCONNAKS AS DISCONNECTNAKS,"+ 
						                         "CLIENTDISCONTIMEOUTS AS DISCONNECTTIMEOUT, " +
						                         "RADIUSDYNAUTHCLIENTCOAREQUESTS AS COAREQ, "+
						                         "RADIUSDYNAUTHCLIENTCOAACKS AS COAACKS, "+
						                         "RADIUSDYNAUTHCLIENTCOANAKS  AS COANAKS, "+
						                         "RADIUSDYNAUTHCLIENTCOATIMEOUTS AS COATIMEOUT, "+
						                         "RADIUSDYNAUTHSERVERADDRESS AS RADIUSDYNAUTHSERVERADDRESS, "+
						                         "CLIENTPORTNUMBER AS CLIENTPORTNUMBER, "+
						                         "CREATETIME AS CREATETIME " +
												 "FROM "+TABLE+" WHERE DYNAUTHSERVERNAME=?  ORDER BY CREATETIME";
	
	private static final String FETCH_ESI_QUERY="SELECT "+
												 "DYNAUTHSERVERNAME AS DYNAUTHSERVERNAME,"+
												 "CLIENTDISCONREQUESTS AS DISCONNECTREQ," +
									            "RADIUSDYNAUTHCLIENTDISCONACKS AS DISCONNECTACKS," +
									            "RADIUSDYNAUTHCLIENTDISCONNAKS AS DISCONNECTNAKS,"+ 
									            "CLIENTDISCONTIMEOUTS AS DISCONNECTTIMEOUT, " +
									            "RADIUSDYNAUTHCLIENTCOAREQUESTS AS COAREQ, "+
									            "RADIUSDYNAUTHCLIENTCOAACKS AS COAACKS, "+
									            "RADIUSDYNAUTHCLIENTCOANAKS  AS COANAKS, "+
									            "RADIUSDYNAUTHCLIENTCOATIMEOUTS AS COATIMEOUT, "+
									            "RADIUSDYNAUTHSERVERADDRESS AS RADIUSDYNAUTHSERVERADDRESS, "+
					                            "CLIENTPORTNUMBER AS CLIENTPORTNUMBER, "+
									            "CREATETIME AS CREATETIME " +
									            "FROM "+TABLE+" WHERE CREATETIME IN (SELECT MAX(CREATETIME) FROM RADIUSDYNAUTHSERVERTABLE GROUP BY DYNAUTHSERVERNAME)  AND CREATETIME >=((SYSDATE - 1 /1440)) ORDER BY DYNAUTHSERVERNAME";


	@Override
	public List<NasESIStatData> getNasESIDataList(String serverKey)  throws Exception{
		
		LogManager.getLogger().debug(MODULE, "Execute Query : " + QUERY_LATEST_ESI_STATISTICS);
		List<NasESIStatData>  radNasESIDataList = new ArrayList<NasESIStatData>();
		List<NasESIStatData> nasESIDatasList = new ArrayList<NasESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(QUERY_LATEST_ESI_STATISTICS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				NasESIStatData nasESIStatData = new NasESIStatData();
				
				nasESIStatData.setDynaAuthServerName(rs.getString("DYNAUTHSERVERNAME"));
				nasESIStatData.setRadiusDynAuthServerAddress(rs.getString("RADIUSDYNAUTHSERVERADDRESS"));
				nasESIStatData.setDynaAuthServerPort(rs.getInt("CLIENTPORTNUMBER"));
				nasESIStatData.setClientDisconRequests(rs.getInt("DISCONNECTREQ"));
				nasESIStatData.setRadiusDynAuthClientDisconAcks(rs.getInt("DISCONNECTACKS"));
				nasESIStatData.setRadiusDynAuthClientDisconNaks(rs.getInt("DISCONNECTNAKS"));
				nasESIStatData.setClientDisconTimeouts(rs.getInt("DISCONNECTTIMEOUT"));
				nasESIStatData.setRadiusDynAuthClientCoARequests(rs.getInt("COAREQ"));
				nasESIStatData.setRadiusDynAuthClientCoAAcks(rs.getInt("COAACKS"));
				nasESIStatData.setRadiusDynAuthClientCoANaks(rs.getInt("COANAKS"));
				nasESIStatData.setRadiusDynAuthClientCoATimeouts(rs.getInt("COATIMEOUT"));
				nasESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				
				radNasESIDataList.add(nasESIStatData);
			}
			
			if(serverKey != null && serverKey.length() > 0 ){
				String serverDataKeys[] = serverKey.split(",");
							
					for(String esiServerName : serverDataKeys){
							for(NasESIStatData nasEsiData : radNasESIDataList){
								esiServerName=esiServerName.trim();
								if(esiServerName.equals(nasEsiData.getDynaAuthServerName())){
									nasESIDatasList.add(nasEsiData);
								}
							}
						}
					
				return nasESIDatasList;
			}else{
				return radNasESIDataList;
			}

		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching Rad Auth ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching Rad Auth ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
	}
	
	@Override
	public List<NasESIStatData> getNasESIDataList(int interval)  throws Exception{
		
		LogManager.getLogger().debug(MODULE, "Execute Query : " + FETCH_ESI_QUERY);
		List<NasESIStatData>  radNasESIDataList = new ArrayList<NasESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(FETCH_ESI_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				NasESIStatData nasESIStatData = new NasESIStatData();
				
				nasESIStatData.setDynaAuthServerName(rs.getString("DYNAUTHSERVERNAME"));
				nasESIStatData.setRadiusDynAuthServerAddress(rs.getString("RADIUSDYNAUTHSERVERADDRESS"));
				nasESIStatData.setDynaAuthServerPort(rs.getInt("CLIENTPORTNUMBER"));
				nasESIStatData.setClientDisconRequests(rs.getInt("DISCONNECTREQ"));
				nasESIStatData.setRadiusDynAuthClientDisconAcks(rs.getInt("DISCONNECTACKS"));
				nasESIStatData.setRadiusDynAuthClientDisconNaks(rs.getInt("DISCONNECTNAKS"));
				nasESIStatData.setClientDisconTimeouts(rs.getInt("DISCONNECTTIMEOUT"));
				nasESIStatData.setRadiusDynAuthClientCoARequests(rs.getInt("COAREQ"));
				nasESIStatData.setRadiusDynAuthClientCoAAcks(rs.getInt("COAACKS"));
				nasESIStatData.setRadiusDynAuthClientCoANaks(rs.getInt("COANAKS"));
				nasESIStatData.setRadiusDynAuthClientCoATimeouts(rs.getInt("COATIMEOUT"));
				nasESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				
				radNasESIDataList.add(nasESIStatData);
			}
			
		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching Rad Auth ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching Rad Auth ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
		
		return radNasESIDataList;
	}
	
	
	@Override
	public List<NasESIStatData> getNasESIStatisticsDetails(NasESIStatData criteriaData) throws Exception{
		
		if(criteriaData == null){
		  LogManager.getLogger().error(MODULE, "Criteria Data Radius Auth ESI Stat data is Null");
		  return null;
		} 
		
		List<NasESIStatData>  nasESIDataList = new ArrayList<NasESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			 conn=getConnection();
			 
			pstmt = conn.prepareStatement(QUERY_ESI_DETAIL.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1,criteriaData.getRadiusDynAuthServerAddress());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				NasESIStatData nasESIStatData = new NasESIStatData();
				
				nasESIStatData.setDynaAuthServerName(rs.getString("DYNAUTHSERVERNAME"));
				nasESIStatData.setRadiusDynAuthServerAddress(rs.getString("RADIUSDYNAUTHSERVERADDRESS"));
				nasESIStatData.setDynaAuthServerPort(rs.getInt("CLIENTPORTNUMBER"));
				nasESIStatData.setClientDisconRequests(rs.getInt("DISCONNECTREQ"));
				nasESIStatData.setRadiusDynAuthClientDisconAcks(rs.getInt("DISCONNECTACKS"));
				nasESIStatData.setRadiusDynAuthClientDisconNaks(rs.getInt("DISCONNECTNAKS"));
				nasESIStatData.setClientDisconTimeouts(rs.getInt("DISCONNECTTIMEOUT"));
				nasESIStatData.setRadiusDynAuthClientCoARequests(rs.getInt("COAREQ"));
				nasESIStatData.setRadiusDynAuthClientCoAAcks(rs.getInt("COAACKS"));
				nasESIStatData.setRadiusDynAuthClientCoANaks(rs.getInt("COANAKS"));
				nasESIStatData.setRadiusDynAuthClientCoATimeouts(rs.getInt("COATIMEOUT"));
				nasESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				nasESIDataList.add(nasESIStatData);
			}

		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching NAS ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		}catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching NAS ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
		return nasESIDataList;
	}
	
	
	public static void main(String[] args) {
	 
	}

}
