package com.elitecore.elitesm.web.dashboard.widget.dao.radacctesistatistics;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctesistatistics.RadiusAcctESIStatData;

public class DefaultRadAcctESIStatisticsDAO extends BaseDAO implements RadiusAcctESIStatisticsDAO {
	private static final String MODULE = "DefaultRadAcctESIStatisticsDAO";
    private static final String TABLE="RADIUSACCSERVERTABLE";	

	private static final String QUERY_LATEST_ESI_STATISTICS =   "SELECT " +
							                                    "ACCTSERVERNAME AS ACCTSERVERNAME,"+
									                            "RADIUSACCCLIENTREQUESTS AS ACCOUNTINGREQ," +
									                            "RADIUSACCCLIENTRESPONSES AS ACCOUNTINGRES," +
									                            "RADIUSACCCLIENTRETRANSMISSIONS AS RETRANSMISSION,"+ 
									                            "RADIUSACCCLIENTTIMEOUTS AS REQUESTDROPS, " +
									                            "RADIUSACCSERVERADDRESS AS RADIUSACCSERVERADDRESS, "+
									                            "CLIENTSERVERPORTNUMBER AS CLIENTSERVERPORTNUMBER, "+
									                            "CREATETIME AS CREATETIME " +
									                            "FROM "+TABLE+"  WHERE CREATETIME IN (SELECT MAX(CREATETIME) FROM  "+TABLE+" GROUP BY ACCTSERVERNAME) ORDER BY ACCTSERVERNAME";
	
	private static final String QUERY_ESI_DETAIL="SELECT "+
												 "ACCTSERVERNAME AS ACCTSERVERNAME,"+
												 "RADIUSACCCLIENTREQUESTS AS ACCOUNTINGREQ," +
												 "RADIUSACCCLIENTRESPONSES AS ACCOUNTINGRES," +
												 "RADIUSACCCLIENTRETRANSMISSIONS AS RETRANSMISSION,"+ 
												 "RADIUSACCCLIENTTIMEOUTS AS REQUESTDROPS, " +
												 "RADIUSACCSERVERADDRESS AS RADIUSACCSERVERADDRESS, "+
						                         "CLIENTSERVERPORTNUMBER AS CLIENTSERVERPORTNUMBER, "+
											     "CREATETIME AS CREATETIME " +
												 "FROM "+TABLE+" WHERE ACCTSERVERNAME=?  ORDER BY CREATETIME";
	

	private static final String FETCH_ESI_QUERY="SELECT "+
												 "ACCTSERVERNAME AS ACCTSERVERNAME,"+
												 "RADIUSACCCLIENTREQUESTS AS ACCOUNTINGREQ," +
												 "RADIUSACCCLIENTRESPONSES AS ACCOUNTINGRES," +
												 "RADIUSACCCLIENTRETRANSMISSIONS AS RETRANSMISSION,"+ 
												 "RADIUSACCCLIENTTIMEOUTS AS REQUESTDROPS, " +
												 "RADIUSACCSERVERADDRESS AS RADIUSACCSERVERADDRESS, "+
						                         "CLIENTSERVERPORTNUMBER AS CLIENTSERVERPORTNUMBER, "+
											     "CREATETIME AS CREATETIME " +
												 "FROM "+TABLE+" WHERE CREATETIME IN (SELECT MAX(CREATETIME) FROM RADIUSACCSERVERTABLE GROUP BY ACCTSERVERNAME)  AND CREATETIME >=((SYSDATE - 1 /1440)) ORDER BY ACCTSERVERNAME";

	@Override
	public List<RadiusAcctESIStatData> getRadAuthESIDataList(String serverKey)  throws Exception{
		
		LogManager.getLogger().debug(MODULE, "Execute Query : " + QUERY_LATEST_ESI_STATISTICS);
		List<RadiusAcctESIStatData>  radAcctESIDataList = new ArrayList<RadiusAcctESIStatData>();
		List<RadiusAcctESIStatData> radiusAcctESIDatasList = new ArrayList<RadiusAcctESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(QUERY_LATEST_ESI_STATISTICS, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				RadiusAcctESIStatData radAcctESIStatData = new RadiusAcctESIStatData();
				
				radAcctESIStatData.setAcctServerName(rs.getString("ACCTSERVERNAME"));
				radAcctESIStatData.setRadiusAccServerAddress(rs.getString("RADIUSACCSERVERADDRESS"));
				radAcctESIStatData.setClientServerPortNumber(rs.getInt("CLIENTSERVERPORTNUMBER"));
				radAcctESIStatData.setRadiusAccClientRequests(rs.getInt("ACCOUNTINGREQ"));
				radAcctESIStatData.setRadiusAccClientResponses(rs.getInt("ACCOUNTINGRES"));
				radAcctESIStatData.setRadiusAccClientRetransmissions(rs.getInt("RETRANSMISSION"));
				radAcctESIStatData.setRadiusAccClientTimeouts(rs.getInt("REQUESTDROPS"));
				radAcctESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				
				radAcctESIDataList.add(radAcctESIStatData);
			}

			if(serverKey != null && serverKey.length() > 0 ){
				String serverDataKeys[] = serverKey.split(",");
							
					for(String esiServerName : serverDataKeys){
							for(RadiusAcctESIStatData radiusAcctEsiData : radAcctESIDataList){
								esiServerName=esiServerName.trim();
								if(esiServerName.equals(radiusAcctEsiData.getAcctServerName())){
									radiusAcctESIDatasList.add(radiusAcctEsiData);
								}
							}
									
						}
						
				return radiusAcctESIDatasList;
			}else{
				return radAcctESIDataList;
			}
			
		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching Rad Acct ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching Rad Acct ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
	}
	
	@Override
	public List<RadiusAcctESIStatData> getRadAuthESIDataList(int interval)  throws Exception{
		
		LogManager.getLogger().debug(MODULE, "Execute Query : " + QUERY_LATEST_ESI_STATISTICS);
		List<RadiusAcctESIStatData>  radAcctESIDataList = new ArrayList<RadiusAcctESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(FETCH_ESI_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				RadiusAcctESIStatData radAcctESIStatData = new RadiusAcctESIStatData();
				
				radAcctESIStatData.setAcctServerName(rs.getString("ACCTSERVERNAME"));
				radAcctESIStatData.setRadiusAccServerAddress(rs.getString("RADIUSACCSERVERADDRESS"));
				radAcctESIStatData.setClientServerPortNumber(rs.getInt("CLIENTSERVERPORTNUMBER"));
				radAcctESIStatData.setRadiusAccClientRequests(rs.getInt("ACCOUNTINGREQ"));
				radAcctESIStatData.setRadiusAccClientResponses(rs.getInt("ACCOUNTINGRES"));
				radAcctESIStatData.setRadiusAccClientRetransmissions(rs.getInt("RETRANSMISSION"));
				radAcctESIStatData.setRadiusAccClientTimeouts(rs.getInt("REQUESTDROPS"));
				radAcctESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				
				radAcctESIDataList.add(radAcctESIStatData);
			}
			
		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching Rad Acct ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching Rad Acct ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
		return radAcctESIDataList;
	}
	
	
	@Override
	public List<RadiusAcctESIStatData> getRadAuthESIStatisticsDetails(RadiusAcctESIStatData criteriaData) throws Exception{
		
		if(criteriaData == null){
		  LogManager.getLogger().error(MODULE, "Criteria Data Radius Acct ESI Stat data is Null");
		  return null;
		} 
		
		List<RadiusAcctESIStatData>  radAcctESIDataList = new ArrayList<RadiusAcctESIStatData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn=getConnection();
			 
			pstmt = conn.prepareStatement(QUERY_ESI_DETAIL.toString(),ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setString(1,criteriaData.getRadiusAccServerAddress());
			
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				RadiusAcctESIStatData radAcctESIStatData = new RadiusAcctESIStatData();
				
				radAcctESIStatData.setAcctServerName(rs.getString("ACCTSERVERNAME"));
				radAcctESIStatData.setRadiusAccServerAddress(rs.getString("RADIUSACCSERVERADDRESS"));
				radAcctESIStatData.setClientServerPortNumber(rs.getInt("CLIENTSERVERPORTNUMBER"));
				radAcctESIStatData.setRadiusAccClientRequests(rs.getInt("ACCOUNTINGREQ"));
				radAcctESIStatData.setRadiusAccClientResponses(rs.getInt("ACCOUNTINGRES"));
				radAcctESIStatData.setRadiusAccClientRetransmissions(rs.getInt("RETRANSMISSION"));
				radAcctESIStatData.setRadiusAccClientTimeouts(rs.getInt("REQUESTDROPS"));
				radAcctESIStatData.setCreateTime(rs.getTimestamp("CREATETIME"));
				radAcctESIDataList.add(radAcctESIStatData);
			}

		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching Rad Acct ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		}catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching Rad Acct ESI. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
		return radAcctESIDataList;
	}
	
	
	public static void main(String[] args) {
	 
	}

}
