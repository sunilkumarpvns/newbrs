package com.elitecore.elitesm.web.dashboard.widget.dao.cpuusage;

import java.sql.PreparedStatement;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.base.DBUtility;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.cpuusage.CpuUsageData;
import com.elitecore.elitesm.web.dashboard.widget.model.dynaauthserver.RadiusDynaAuthServerData;
import com.elitecore.elitesm.web.dashboard.widget.model.radauthesistatistics.RadiusAuthESIStatData;

public class CpuUsageDAOImpl extends BaseDAO implements CpuUsageDAO {

	private static final String MODULE=CpuUsageDAOImpl.class.getSimpleName();
	private static final String Query_CPU_USAGE = "SELECT DISTINCT CAST(CREATETIME AS DATE) AS TIME,B.NAME,A.JVMSYSTEMLOADAVERAGE from JVMMEMPOOLTABLE A,TBLMNETSERVERINSTANCE B WHERE A.INSTANCEID=B.NETSERVERID ORDER BY TIME ASC";
	private static final String Query_CPU_USAGE_LIVE="SELECT DISTINCT CAST(CREATETIME AS DATE) AS TIME,B.NAME,A.JVMSYSTEMLOADAVERAGE from JVMMEMPOOLTABLE A,TBLMNETSERVERINSTANCE B WHERE A.INSTANCEID=B.NETSERVERID and A.CREATETIME >=(SYSDATE- 5/1440) ORDER BY TIME ASC";
	private static final String LIVE_CPU_USAGE_QUERY="select A.CREATETIME,B.NAME,A.JVMMEMPOOLNAME,count(A.JVMMEMPOOLNAME),A.JVMMEMPOOLTYPE,A.JVMSYSTEMLOADAVERAGE,A.JVMSYSTEMLOADAVERAGE_MINVAL,A.JVMSYSTEMLOADAVERAGE_MAXVAL from JVMMEMPOOLTABLE A,TBLMNETSERVERINSTANCE B group by A.JVMMEMPOOLNAME,A.JVMMEMPOOLTYPE,A.JVMSYSTEMLOADAVERAGE_MAXVAL,A.JVMSYSTEMLOADAVERAGE,A.JVMSYSTEMLOADAVERAGE_MINVAL, B.NAME,A.CREATETIME order by A.CREATETIME";
	@Override
	public Map<String, List<CpuUsageData>> getCpuUsageData(String serverKey) throws SQLException, ParseException{
		LogManager.getLogger().debug(MODULE, "Execute Query : " + Query_CPU_USAGE);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String,List<CpuUsageData>> memoryMap=null;
		Map<String,List<CpuUsageData>> cpuUsageMap = new  LinkedHashMap<String, List<CpuUsageData>>();
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(Query_CPU_USAGE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			memoryMap=getCpuUsageData(rs);  
			
			if(serverKey != null && serverKey.length() > 0 ){
				String serverDataKeys[] = serverKey.split(",");
							
					for(String serverName : serverDataKeys){
						for (Map.Entry<String,List<CpuUsageData>> entry : memoryMap.entrySet()) {
							serverName=serverName.trim();
							if(entry.getKey().equals(serverName)){
						    	cpuUsageMap.put(entry.getKey(), entry.getValue());
						    }
						}
					}
				return cpuUsageMap;
			}else{
				return memoryMap;
			}
			
			}catch (SQLException e) {
				LogManager.getLogger().error(MODULE, "SQL Error while fetching CPU Usage. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching JVM Memory. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
	   return memoryMap;
	}

	@Override
	public Map<String, List<CpuUsageData>> getCpuUsageData(int interval) throws SQLException, ParseException{
		LogManager.getLogger().debug(MODULE, "Execute Query : " + Query_CPU_USAGE_LIVE);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String,List<CpuUsageData>> memoryMap=null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(Query_CPU_USAGE_LIVE, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			memoryMap=getCpuUsageData(rs);     			
			}catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching CPU Usage. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching JVM Memory. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
	   return memoryMap;
	}
	
	private Map<String,List<CpuUsageData>> getCpuUsageData(ResultSet rs) throws SQLException, ParseException{
		Map<String,List<CpuUsageData>> cpuUsageMap=null;
		while(rs.next()){
			if(cpuUsageMap==null){
				cpuUsageMap=new LinkedHashMap<String, List<CpuUsageData>>();
			}
			CpuUsageData cpuUsageData=new CpuUsageData();
			cpuUsageData.setInstanceID(rs.getString("NAME"));
			cpuUsageData.setCreateTime(rs.getTimestamp("TIME").getTime());
			cpuUsageData.setCpuAverageUsage(rs.getFloat("JVMSYSTEMLOADAVERAGE"));
			if(cpuUsageMap.get(cpuUsageData.getInstanceID())==null){
				List<CpuUsageData> cpuUsageList=new ArrayList<CpuUsageData>();
				cpuUsageMap.put(cpuUsageData.getInstanceID(), cpuUsageList);
			}else{
				cpuUsageMap.get(cpuUsageData.getInstanceID()).add(cpuUsageData);
			}
			
		}
		return cpuUsageMap;
	}

	@Override
	public List<CpuUsageData> getCpuUsageLiveData(String serverKey) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Query : " + LIVE_CPU_USAGE_QUERY);
		List<CpuUsageData>  cpuUsageDataList = new ArrayList<CpuUsageData>();
		List<CpuUsageData> cpuUsageDatasList = new ArrayList<CpuUsageData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(LIVE_CPU_USAGE_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				CpuUsageData cpuUsageData = new CpuUsageData();
				
				cpuUsageData.setInstanceID(rs.getString("NAME"));
				cpuUsageData.setCreateTime(rs.getTimestamp("CREATETIME").getTime());
				cpuUsageData.setCpuAverageUsage(rs.getFloat("JVMSYSTEMLOADAVERAGE"));
				cpuUsageData.setCpuUsageMinVal(rs.getLong("JVMSYSTEMLOADAVERAGE_MINVAL"));
				cpuUsageData.setCpuUsageMaxVal(rs.getLong("JVMSYSTEMLOADAVERAGE_MAXVAL"));
				cpuUsageData.setJvmMemoryPoolName(rs.getString("JVMMEMPOOLNAME"));
				cpuUsageData.setJvmMemoryPoolType(rs.getString("JVMMEMPOOLTYPE"));
				cpuUsageData.setStrCreateTime(rs.getTimestamp("CREATETIME").toString());
				
				cpuUsageDataList.add(cpuUsageData);
			}
			
			if(serverKey != null && serverKey.length() > 0 ){
				String serverDataKeys[] = serverKey.split(",");
							
					for(String serverName : serverDataKeys){
							for(CpuUsageData cpuUsageDataObj : cpuUsageDataList){
									serverName=serverName.trim();
									if(serverName.equals(cpuUsageDataObj.getInstanceID())){
										cpuUsageDatasList.add(cpuUsageDataObj);
									}
								}
							}
				return cpuUsageDatasList;
			}else{
				return cpuUsageDataList;
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
