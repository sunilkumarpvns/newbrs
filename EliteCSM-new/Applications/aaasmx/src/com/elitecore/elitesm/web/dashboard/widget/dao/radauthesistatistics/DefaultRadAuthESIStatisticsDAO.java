package com.elitecore.elitesm.web.dashboard.widget.dao.radauthesistatistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthesistatistics.RadiusAuthESIStatData;

public class DefaultRadAuthESIStatisticsDAO extends BaseDAO implements RadiusAuthESIStatisticsDAO {
	private static final String MODULE = "DefaultRadAuthESIStatisticsDAO";
    private static final String TABLE="RADIUSAUTHSERVERTABLE";	

	private static final String QUERY_LATEST_ESI_STATISTICS =   "SELECT " +
							                                    "AUTHSERVERNAME AS AUTHSERVERNAME,"+
									                            "RADIUSAUTHCLIENTACCESSACCEPTS AS ACCESSACCESPTS," +
									                            "RADIUSAUTHCLIENTACCESSREJECTS AS ACCESSREJECTS," +
									                            "CLIENTACCESSCHALLENGES AS ACCESSCHALLENGES,"+ 
									                            "RADIUSAUTHCLIENTTIMEOUTS AS REQUESTDROPS, " +
									                            "CREATETIME AS CREATETIME, " +
									                            "RADIUSAUTHSERVERADDRESS AS RADIUSAUTHSERVERADDRESS, "+
									                            "CLIENTSERVERPORTNUMBER AS CLIENTSERVERPORTNUMBER "+
									                            "FROM "+TABLE+"  WHERE CREATETIME IN (SELECT MAX(CREATETIME) FROM  "+TABLE+" GROUP BY AUTHSERVERNAME) ORDER BY AUTHSERVERNAME";
	
	private static final String QUERY_ESI_DETAIL="SELECT "+
												 "AUTHSERVERNAME AS AUTHSERVERNAME,"+
												 "RADIUSAUTHCLIENTACCESSACCEPTS AS ACCESSACCESPTS," +
												 "RADIUSAUTHCLIENTACCESSREJECTS AS ACCESSREJECTS," +
												 "CLIENTACCESSCHALLENGES AS ACCESSCHALLENGES,"+ 
												 "RADIUSAUTHCLIENTTIMEOUTS AS REQUESTDROPS, " +
											     "CREATETIME AS CREATETIME, " +
												 "RADIUSAUTHSERVERADDRESS AS RADIUSAUTHSERVERADDRESS, "+
												 "CLIENTSERVERPORTNUMBER AS CLIENTSERVERPORTNUMBER "+
												 "FROM "+TABLE+" WHERE AUTHSERVERNAME=?  ORDER BY CREATETIME";

	
	private static final String FETCH_ESI_QUERY = "SELECT "+
												 "AUTHSERVERNAME AS AUTHSERVERNAME,"+
												 "RADIUSAUTHCLIENTACCESSACCEPTS AS ACCESSACCESPTS," +
												 "RADIUSAUTHCLIENTACCESSREJECTS AS ACCESSREJECTS," +
												 "CLIENTACCESSCHALLENGES AS ACCESSCHALLENGES,"+ 
												 "RADIUSAUTHCLIENTTIMEOUTS AS REQUESTDROPS, " +
											     "CREATETIME AS CREATETIME, " +
												 "RADIUSAUTHSERVERADDRESS AS RADIUSAUTHSERVERADDRESS, "+
												 "CLIENTSERVERPORTNUMBER AS CLIENTSERVERPORTNUMBER "+
												 "FROM "+TABLE+" WHERE CREATETIME IN (SELECT MAX(CREATETIME) FROM RADIUSAUTHSERVERTABLE GROUP BY AUTHSERVERNAME)  AND CREATETIME >=((SYSDATE - 1 /1440)) ORDER BY AUTHSERVERNAME";
	
	@Override
	public List<RadiusAuthESIStatData> getRadAuthESIDataList(String serverKey)  throws Exception{
		
		LogManager.getLogger().debug(MODULE, "Execute Query : " + QUERY_LATEST_ESI_STATISTICS);
		List<RadiusAuthESIStatData>  radAuthESIDataList = new ArrayList<RadiusAuthESIStatData>();
		List<RadiusAuthESIStatData> radiusAuthESIDatasList = new ArrayList<RadiusAuthESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(QUERY_LATEST_ESI_STATISTICS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			while(rs.next()){
				RadiusAuthESIStatData radAuthESIStatData = new RadiusAuthESIStatData();
				
				radAuthESIStatData.setAuthServerName(rs.getString("AUTHSERVERNAME"));
				radAuthESIStatData.setRadAuthServerAddress(rs.getString("RADIUSAUTHSERVERADDRESS"));
				radAuthESIStatData.setClientServerPortNumber(rs.getInt("CLIENTSERVERPORTNUMBER"));
				radAuthESIStatData.setRadAuthClientAccessAccepts(rs.getInt("ACCESSACCESPTS"));
				radAuthESIStatData.setRadAuthClientAccessRejects(rs.getInt("ACCESSREJECTS"));
				radAuthESIStatData.setClientAccessChallenges(rs.getInt("ACCESSCHALLENGES"));
				radAuthESIStatData.setRadAuthClientTimeouts(rs.getInt("REQUESTDROPS"));
				radAuthESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				
				radAuthESIDataList.add(radAuthESIStatData);
			}
			
			if(serverKey != null && serverKey.length() > 0 ){
				String serverDataKeys[] = serverKey.split(",");
							
					for(String esiServerName : serverDataKeys){
							for(RadiusAuthESIStatData radiusAuthEsiData : radAuthESIDataList){
								esiServerName=esiServerName.trim();
								if(esiServerName.equals(radiusAuthEsiData.getAuthServerName())){
									radiusAuthESIDatasList.add(radiusAuthEsiData);
								}
							}
						}
					
				return radiusAuthESIDatasList;
			}else{
				return radAuthESIDataList;
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
	public List<RadiusAuthESIStatData> getRadAuthESIDataList(int interval)  throws Exception{
		
		LogManager.getLogger().debug(MODULE, "Execute Query : " + QUERY_LATEST_ESI_STATISTICS);
		List<RadiusAuthESIStatData>  radAuthESIDataList = new ArrayList<RadiusAuthESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(FETCH_ESI_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			while(rs.next()){
				RadiusAuthESIStatData radAuthESIStatData = new RadiusAuthESIStatData();
				
				radAuthESIStatData.setAuthServerName(rs.getString("AUTHSERVERNAME"));
				radAuthESIStatData.setRadAuthServerAddress(rs.getString("RADIUSAUTHSERVERADDRESS"));
				radAuthESIStatData.setClientServerPortNumber(rs.getInt("CLIENTSERVERPORTNUMBER"));
				radAuthESIStatData.setRadAuthClientAccessAccepts(rs.getInt("ACCESSACCESPTS"));
				radAuthESIStatData.setRadAuthClientAccessRejects(rs.getInt("ACCESSREJECTS"));
				radAuthESIStatData.setClientAccessChallenges(rs.getInt("ACCESSCHALLENGES"));
				radAuthESIStatData.setRadAuthClientTimeouts(rs.getInt("REQUESTDROPS"));
				radAuthESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				
				radAuthESIDataList.add(radAuthESIStatData);
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
		return radAuthESIDataList;
	}
	
	
	@Override
	public List<RadiusAuthESIStatData> getRadAuthESIStatisticsDetails(RadiusAuthESIStatData criteriaData) throws Exception{
		
		//long startTime1 = System.currentTimeMillis();
		
		
		if(criteriaData == null){
		  LogManager.getLogger().error(MODULE, "Criteria Data Radius Auth ESI Stat data is Null");
		  return null;
		} 
		
		List<RadiusAuthESIStatData>  radAuthESIDataList = new ArrayList<RadiusAuthESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			 conn=getConnection();
			 
			//QUERY_ESI_DETAIL.append(" ORDER BY CREATETIME");
			 
			pstmt = conn.prepareStatement(QUERY_ESI_DETAIL.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1,criteriaData.getRadAuthServerAddress());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				RadiusAuthESIStatData radAuthESIStatData = new RadiusAuthESIStatData();
				
				radAuthESIStatData.setAuthServerName(rs.getString("AUTHSERVERNAME"));
				radAuthESIStatData.setRadAuthServerAddress(rs.getString("RADIUSAUTHSERVERADDRESS"));
				radAuthESIStatData.setClientServerPortNumber(rs.getInt("CLIENTSERVERPORTNUMBER"));
				radAuthESIStatData.setRadAuthClientAccessAccepts(rs.getInt("ACCESSACCESPTS"));
				radAuthESIStatData.setRadAuthClientAccessRejects(rs.getInt("ACCESSREJECTS"));
				radAuthESIStatData.setClientAccessChallenges(rs.getInt("ACCESSCHALLENGES"));
				radAuthESIStatData.setRadAuthClientTimeouts(rs.getInt("REQUESTDROPS"));
				radAuthESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				radAuthESIDataList.add(radAuthESIStatData);
			}
			
			//long endTime1 = System.currentTimeMillis();
			//Logger.logDebug(MODULE,"DB Fetch time-difference :["+(endTime1-startTime1)+"] ms");

		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching Rad Auth ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		}catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching Rad Auth ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
		return radAuthESIDataList;
	}
	
	
	public static void main(String[] args) {
	  /*	List<RadiusAuthESIStatData> executeQuery = new DefaultRadAuthESIStatisticsDAO().getRadAuthESIStatisticsDetails("127.0.0.1");
		//List<RadiusAuthESIStatData> executeQuery = new DefaultRadAuthESIStatisticsDAO().getRadAuthESIDataList();
		ListIterator<RadiusAuthESIStatData> listIterator = executeQuery.listIterator();
		while(listIterator.hasNext()){
			RadiusAuthESIStatData next = listIterator.next();
		}*/
	}


	@Override
	public List<RadiusAuthESIStatData> getRadAuthESIStatisticsDetailsData(String serverKey) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Query : " + FETCH_ESI_QUERY);
		List<RadiusAuthESIStatData>  radAuthESIDataList = new ArrayList<RadiusAuthESIStatData>();
		List<RadiusAuthESIStatData> radiusAuthESIDatasList = new ArrayList<RadiusAuthESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(QUERY_LATEST_ESI_STATISTICS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			//pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			while(rs.next()){
				RadiusAuthESIStatData radAuthESIStatData = new RadiusAuthESIStatData();
				
				radAuthESIStatData.setAuthServerName(rs.getString("AUTHSERVERNAME"));
				radAuthESIStatData.setRadAuthServerAddress(rs.getString("RADIUSAUTHSERVERADDRESS"));
				radAuthESIStatData.setClientServerPortNumber(rs.getInt("CLIENTSERVERPORTNUMBER"));
				radAuthESIStatData.setRadAuthClientAccessAccepts(rs.getInt("ACCESSACCESPTS"));
				radAuthESIStatData.setRadAuthClientAccessRejects(rs.getInt("ACCESSREJECTS"));
				radAuthESIStatData.setClientAccessChallenges(rs.getInt("ACCESSCHALLENGES"));
				radAuthESIStatData.setRadAuthClientTimeouts(rs.getInt("REQUESTDROPS"));
				radAuthESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				
				radAuthESIDataList.add(radAuthESIStatData);
			}
			
			if(serverKey != null && serverKey.length() > 0 ){
				String serverDataKeys[] = serverKey.split(",");
							
					for(String esiServerName : serverDataKeys){
							for(RadiusAuthESIStatData radiusAuthEsiData : radAuthESIDataList){
								esiServerName=esiServerName.trim();
								if(esiServerName.equals(radiusAuthEsiData.getAuthServerName())){
									radiusAuthESIDatasList.add(radiusAuthEsiData);
								}
							}
						}
					
				return radiusAuthESIDatasList;
			}else{
				return radAuthESIDataList;
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

}
