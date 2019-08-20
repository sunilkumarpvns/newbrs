package com.elitecore.elitesm.web.dashboard.widget.dao.memoryusage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.memoryusage.MemoryUsageData;


public class DefaultMemoryUsageDAO extends BaseDAO implements MemoryUsageDAO{

	private static final String MODULE=DefaultMemoryUsageDAO.class.getSimpleName();
	private static final String Query_Memory_Usage = "SELECT INSTANCE.NAME,CREATETIME,JVMMEMORYHEAPUSED from JVMMEMORY,TBLMNETSERVERINSTANCE INSTANCE WHERE JVMMEMORY.INSTANCEID=INSTANCE.NETSERVERID ORDER BY INSTANCEID,CREATETIME asc";
	private static final String Query_Memory_Usage_Live = "SELECT INSTANCE.NAME NAME,CREATETIME,JVMMEMORYHEAPUSED from JVMMEMORY,TBLMNETSERVERINSTANCE INSTANCE Where JVMMEMORY.INSTANCEID=INSTANCE.NETSERVERID and  JVMMEMORY.CREATETIME >=(SYSDATE-10/1440) ORDER BY INSTANCEID, CREATETIME asc";
	private static final String LIVE_MEMORY_USAGE_QUERY="SELECT INSTANCE.NAME,CREATETIME,JVMMEMORYHEAPUSED,JVMMEMORYHEAPUSED_MINVAL,JVMMEMORYHEAPUSED_MAXVAL,JVMMEMORYNONHEAPUSED,JVMMEMORYNONHEAPUSED_MINVAL,JVMMEMORYNONHEAPUSED_MAXVAL from JVMMEMORY,TBLMNETSERVERINSTANCE INSTANCE Where JVMMEMORY.INSTANCEID=INSTANCE.NETSERVERID ORDER BY INSTANCEID, CREATETIME asc";
		
	@Override
	public List<MemoryUsageData> getMemoryUsageData(String serverKey) throws Exception{
		LogManager.getLogger().debug(MODULE, "Execute Query : " + Query_Memory_Usage);
		List<MemoryUsageData>  memoryUsageDataList = new ArrayList<MemoryUsageData>();
		List<MemoryUsageData> memoryUsageDatasList = new ArrayList<MemoryUsageData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(Query_Memory_Usage, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			while(rs.next()){
				MemoryUsageData memoryUsageData=new MemoryUsageData();
				
				memoryUsageData.setInstanceId(rs.getString("NAME"));
                memoryUsageData.setTimestamp(rs.getTimestamp("CREATETIME"));
            	memoryUsageData.setMemoryUsage(rs.getLong("JVMMEMORYHEAPUSED"));
				memoryUsageDataList.add(memoryUsageData);
			}
			
			if(serverKey != null && serverKey.length() > 0 ){
				String serverDataKeys[] = serverKey.split(",");
							
					for(String serverName : serverDataKeys){
							for(MemoryUsageData cpuUsageDataObj : memoryUsageDataList){
									serverName=serverName.trim();
									if(serverName.equals(cpuUsageDataObj.getInstanceId())){
										memoryUsageDatasList.add(cpuUsageDataObj);
									}
								}
							}
				return memoryUsageDatasList;
			}else{
				return memoryUsageDataList;
			}

		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching JVM Memory. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching JVM Memory. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
	}

	@Override
	public List<MemoryUsageData> getMemoryUsageData(int interval) throws Exception{
		LogManager.getLogger().debug(MODULE, "Execute Query : " + Query_Memory_Usage);
		List<MemoryUsageData>  memoryUsageDataList = new ArrayList<MemoryUsageData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(Query_Memory_Usage_Live, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			while(rs.next()){
				MemoryUsageData memoryUsageData=new MemoryUsageData();
				
				memoryUsageData.setInstanceId(rs.getString("NAME"));
				memoryUsageData.setTimestamp(rs.getTimestamp("CREATETIME"));
				memoryUsageData.setMemoryUsage(rs.getLong("JVMMEMORYHEAPUSED"));
				memoryUsageDataList.add(memoryUsageData);
			}

		} catch (SQLException e) {
			LogManager.getLogger().error(MODULE, "SQL Error while fetching JVM Memory. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} catch (Exception e){
			LogManager.getLogger().error(MODULE, "Error while fetching JVM Memory. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
			throw new Exception(e);
		} finally{
			DBUtility.closeQuietly(rs);
			DBUtility.closeQuietly(pstmt);
			DBUtility.closeQuietly(conn);
		}
		return memoryUsageDataList;
	}
	
	@Override
	public List<MemoryUsageData> getMemoryUsageLiveData(String serverKey) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Query : " + LIVE_MEMORY_USAGE_QUERY);
		List<MemoryUsageData>  memoryUsageDataList = new ArrayList<MemoryUsageData>();
		List<MemoryUsageData> memoryUsageDatasList = new ArrayList<MemoryUsageData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(LIVE_MEMORY_USAGE_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			rs = pstmt.executeQuery();
			while(rs.next()){
				MemoryUsageData memoryUsageData = new MemoryUsageData();
				
				memoryUsageData.setInstanceId(rs.getString("NAME"));
				memoryUsageData.setTimestamp(rs.getTimestamp("CREATETIME"));
				memoryUsageData.setHeapUsed(rs.getLong("JVMMEMORYHEAPUSED"));
				memoryUsageData.setHeapUsedMinVal(rs.getLong("JVMMEMORYHEAPUSED_MINVAL"));
				memoryUsageData.setHeapUsedMaxVal(rs.getLong("JVMMEMORYHEAPUSED_MAXVAL"));
				memoryUsageData.setNonHeapUsed(rs.getLong("JVMMEMORYNONHEAPUSED"));
				memoryUsageData.setNonHeapMinVal(rs.getLong("JVMMEMORYNONHEAPUSED_MINVAL"));
				memoryUsageData.setNonHeapMaxVal(rs.getLong("JVMMEMORYNONHEAPUSED_MAXVAL"));
				memoryUsageData.setStrCreateTime(rs.getTimestamp("CREATETIME").toString());
				memoryUsageDataList.add(memoryUsageData);
			}
			
			if(serverKey != null && serverKey.length() > 0 ){
				String serverDataKeys[] = serverKey.split(",");
							
					for(String serverName : serverDataKeys){
							for(MemoryUsageData cpuUsageDataObj : memoryUsageDataList){
									serverName=serverName.trim();
									if(serverName.equals(cpuUsageDataObj.getInstanceId())){
										memoryUsageDatasList.add(cpuUsageDataObj);
									}
								}
							}
				return memoryUsageDatasList;
			}else{
				return memoryUsageDataList;
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
