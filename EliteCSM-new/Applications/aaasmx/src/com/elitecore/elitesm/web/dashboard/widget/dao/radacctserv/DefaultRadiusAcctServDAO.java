package com.elitecore.elitesm.web.dashboard.widget.dao.radacctserv;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.radacctserv.RadiusAcctServData;

public class DefaultRadiusAcctServDAO extends BaseDAO implements RadiusAcctServDAO {
	

	private static final String MODULE = "DEFAULTRADIUSACCSERVDAO"; 
	
	
	private static final String QUERY = "SELECT B.MAX_CREATETIME AS CREATETIME, A.RADIUSACCSERVTOTALNORECORDS AS RADIUSACCSERVTOTALNORECORDS,"
									+"A.TOTALINVALIDREQUESTS AS TOTALINVALIDREQUESTS, A.RADIUSACCSERVTOTALREQUESTS AS RADIUSACCSERVTOTALREQUESTS,"
									+"A.RADIUSACCSERVTOTALDUPREQUESTS AS  RADIUSACCSERVTOTALDUPREQUESTS, A.RADIUSACCSERVTOTALRESPONSES AS "
									+"RADIUSACCSERVTOTALRESPONSES, A.TOTALMALFORMEDREQUESTS AS TOTALMALFORMEDREQUESTS , A.TOTALBADAUTHENTICATORS "
									+"AS TOTALBADAUTHENTICATORS, A.TOTALPACKETSDROPPED AS TOTALPACKETSDROPPED,  A.RADIUSACCSERVTOTALUNKNOWNTYPES AS "
									+"RADIUSACCSERVTOTALUNKNOWNTYPES,C.NAME AS INSTANCENAME, A.instanceid as INSTANCEIDS,C.NETSERVERID as NETSERVERID "
									+"FROM RADIUSACCSERV A, (SELECT INSTANCEID,MAX(CREATETIME) AS MAX_CREATETIME FROM RADIUSACCSERV GROUP BY INSTANCEID) B, "
									+"(SELECT NETSERVERID,NAME,NETSERVERCODE FROM tblmnetserverinstance) C WHERE A.INSTANCEID = B.INSTANCEID "
									+"AND A.CREATETIME = B.MAX_CREATETIME AND C.NETSERVERCODE= A.instanceid ";
	
	private static final String INSTANCE_QUERY= "SELECT NAME FROM TBLMNETSERVERINSTANCE WHERE NETSERVERID = ";
	
	private static final String FETCH_QRY = QUERY + " AND A.CREATETIME >= ((SYSDATE - 1 /1440))"; 
	
	@Override
	public List<RadiusAcctServData> getAuthServDataList(String serverKey) throws Exception{
		List<RadiusAcctServData> radiusAcctServDataList = executeQuery(QUERY);
		List<RadiusAcctServData> radiusAcctServDatas = new ArrayList<RadiusAcctServData>();
		if(serverKey != null && serverKey.length() > 0 ){
			String serverDataKeys[] = serverKey.split(",");
			
			for(String instanceId : serverDataKeys){
				boolean isElementExist = false;
				for(RadiusAcctServData radiServData :  radiusAcctServDataList){
					if(instanceId.equals(radiServData.getInstanceID())){
						radiusAcctServDatas.add(radiServData);
						isElementExist = true;
					}
				}
				
				if( !isElementExist ){
					RadiusAcctServData radiusAcctServData = executeInstanceQuery(INSTANCE_QUERY + instanceId);
					radiusAcctServDatas.add(radiusAcctServData);
				}
			}
			
			return radiusAcctServDatas;
		}else{
			return radiusAcctServDataList;
		}
	}

	@Override
	public List<RadiusAcctServData> getAuthServDataList(int interval) throws Exception{
		return executeQuery(FETCH_QRY);
	}
	
	private List<RadiusAcctServData> executeQuery(String query) throws Exception{
		LogManager.getLogger().debug(MODULE, "Execute Query : " + query);
		List<RadiusAcctServData>  radiusAcctServDataList = new ArrayList<RadiusAcctServData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
					RadiusAcctServData radiusAcctServData = new RadiusAcctServData();
					radiusAcctServData.setRadiusAccServIdent(rs.getString("INSTANCENAME"));
					radiusAcctServData.setCreatetime(rs.getTimestamp("CREATETIME"));
					radiusAcctServData.setRadiusAccServTotalRequests(rs.getLong("RADIUSACCSERVTOTALREQUESTS"));
					radiusAcctServData.setRadiusAccServTotalResponses(rs.getLong("RADIUSACCSERVTOTALRESPONSES"));
					radiusAcctServData.setRadiusAccServTotalDupRequests(rs.getLong("RADIUSACCSERVTOTALDUPREQUESTS"));
					radiusAcctServData.setRadiusAccServTotalNoRecords(rs.getLong("RADIUSACCSERVTOTALNORECORDS"));
					radiusAcctServData.setRadiusAccServTotalUnknownTypes(rs.getLong("RADIUSACCSERVTOTALUNKNOWNTYPES"));
					radiusAcctServData.setTotalPacketsDropped(rs.getLong("TOTALPACKETSDROPPED"));
					radiusAcctServData.setTotalBadAuthenticators(rs.getLong("TOTALBADAUTHENTICATORS"));
					radiusAcctServData.setTotalInvalidRequests(rs.getLong("TOTALINVALIDREQUESTS"));
					radiusAcctServData.setTotalMalformedRequests(rs.getLong("TOTALMALFORMEDREQUESTS"));
					radiusAcctServData.setInstanceID(rs.getString("NETSERVERID"));
					radiusAcctServDataList.add(radiusAcctServData);
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
		return radiusAcctServDataList;
	}
	
	private RadiusAcctServData executeInstanceQuery(String query) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Instance Query : " + query);
		RadiusAcctServData radiusAcctServData = new RadiusAcctServData();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(query, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				radiusAcctServData = new RadiusAcctServData();
				radiusAcctServData.setRadiusAccServIdent(rs.getString("NAME"));
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
		return radiusAcctServData;
	}
	
	public static void main(String[] args) throws Exception {
		System.out.println(new DefaultRadiusAcctServDAO().executeQuery(FETCH_QRY));
	}

}
