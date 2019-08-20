package com.elitecore.elitesm.web.dashboard.widget.dao.radauthserv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthserv.RadiusAuthServData;

public class DefaultRadiusAuthServDAO extends BaseDAO implements RadiusAuthServDAO {
	
	private static final String MODULE = "DefaultRadiusAuthServDAO"; 
	
	private static final String QUERY = "SELECT B.MAX_CREATETIME AS CREATETIME, A.TOTALACCESSREQUESTS AS TOTALACCESSREQUESTS,"  
			+" A.TOTALINVALIDREQUESTS AS TOTALINVALIDREQUESTS, A.TOTALDUPACCESSREQUESTS AS  TOTALDUPACCESSREQUESTS,"
			+" A.TOTALACCESSACCEPTS AS TOTALACCESSACCEPTS,"
			+" A.TOTALACCESSREJECTS AS  TOTALACCESSREJECTS, A.TOTALACCESSCHALLENGES AS TOTALACCESSCHALLENGES," 
			+" A.TOTALMALFORMEDACCESSREQUESTS AS TOTALMALFORMEDACCESSREQUESTS, "
			+" A.TOTALBADAUTHENTICATORS AS TOTALBADAUTHENTICATORS, A.TOTALPACKETSDROPPED AS TOTALPACKETSDROPPED,"  
			+" A.TOTALUNKNOWNTYPES AS TOTALUNKNOWNTYPES,C.NAME AS INSTANCENAME, A.instanceid as INSTANCEIDS,C.NETSERVERID as NETSERVERID FROM RADIUSAUTHSERV A,"
			+" (SELECT INSTANCEID,MAX(CREATETIME) AS MAX_CREATETIME FROM RADIUSAUTHSERV GROUP BY INSTANCEID) B, "
			+" (SELECT NETSERVERID,NAME,NETSERVERCODE FROM tblmnetserverinstance) C"
			+" WHERE A.INSTANCEID = B.INSTANCEID AND A.CREATETIME = B.MAX_CREATETIME AND C.NETSERVERCODE= A.instanceid" ;
	
	private static final String INSTANCE_QUERY= "SELECT NAME FROM TBLMNETSERVERINSTANCE WHERE NETSERVERID = ";
	
	private static final String FETCH_QRY = QUERY + " AND A.CREATETIME >= ((SYSDATE - 1 /1440))"; 
	
	@Override
	public List<RadiusAuthServData> getAuthServDataList(String serverKey) throws Exception {
		List<RadiusAuthServData> radiusAuthServDataList = executeQuery(QUERY);
		List<RadiusAuthServData> radiusAuthServDatas = new ArrayList<RadiusAuthServData>();
		if(serverKey != null && serverKey.length() > 0 ){
			String serverDataKeys[] = serverKey.split(",");
			
			for(String instanceId : serverDataKeys){
				boolean isElementExist = false;
				for(RadiusAuthServData radiusAuthServData : radiusAuthServDataList){
					if(instanceId.equals(radiusAuthServData.getInstanceID())){
						radiusAuthServDatas.add(radiusAuthServData);
						isElementExist = true;
					}
				}
				
				if( !isElementExist ){
					RadiusAuthServData radiusAuthServData = executeInstanceQuery(INSTANCE_QUERY + instanceId);
					radiusAuthServDatas.add(radiusAuthServData);
				}
			}
			
			return radiusAuthServDatas;
		}else{
			return radiusAuthServDataList;
		}
	}

	@Override
	public List<RadiusAuthServData> getAuthServDataList(int interval) throws Exception {
		return executeQuery(FETCH_QRY);
	}
	
	private List<RadiusAuthServData> executeQuery(String query) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Query : " + query);
		List<RadiusAuthServData>  radiusAuthServDataList = new ArrayList<RadiusAuthServData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
					RadiusAuthServData radiusAuthServData = new RadiusAuthServData();
					radiusAuthServData.setRadiusAuthServIdent(rs.getString("INSTANCENAME"));
					radiusAuthServData.setCreatetime(rs.getTimestamp("CREATETIME"));
					radiusAuthServData.setTotalAccessAccepts(rs.getLong("TOTALACCESSACCEPTS"));
					radiusAuthServData.setTotalAccessChallenges(rs.getLong("TOTALACCESSCHALLENGES"));
					radiusAuthServData.setTotalAccessRejects(rs.getLong("TOTALACCESSREJECTS"));
					radiusAuthServData.setTotalAccessRequests(rs.getLong("TOTALACCESSREQUESTS"));
					radiusAuthServData.setTotalBadAuthenticators(rs.getLong("TOTALBADAUTHENTICATORS"));
					radiusAuthServData.setTotalDupAccessRequests(rs.getLong("TOTALDUPACCESSREQUESTS"));
					radiusAuthServData.setTotalInvalidRequests(rs.getLong("TOTALINVALIDREQUESTS"));
					radiusAuthServData.setTotalMalformedAccessRequests(rs.getLong("TOTALMALFORMEDACCESSREQUESTS"));
					radiusAuthServData.setTotalPacketsDropped(rs.getLong("TOTALPACKETSDROPPED"));
					radiusAuthServData.setTotalUnknownTypes(rs.getLong("TOTALUNKNOWNTYPES"));
					radiusAuthServData.setInstanceID(rs.getString("NETSERVERID"));
					radiusAuthServDataList.add(radiusAuthServData);
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
		return radiusAuthServDataList;
	}
	
	private RadiusAuthServData executeInstanceQuery(String query) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Instance Query : " + query);
		RadiusAuthServData radiusAuthServData = new RadiusAuthServData();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				radiusAuthServData = new RadiusAuthServData();
				radiusAuthServData.setRadiusAuthServIdent(rs.getString("NAME"));
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
		return radiusAuthServData;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new DefaultRadiusAuthServDAO().executeQuery(FETCH_QRY));
	}
}
