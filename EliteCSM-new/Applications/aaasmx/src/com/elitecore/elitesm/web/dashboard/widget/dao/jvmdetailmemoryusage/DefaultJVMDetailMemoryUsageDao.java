package com.elitecore.elitesm.web.dashboard.widget.dao.jvmdetailmemoryusage;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import com.elitecore.commons.base.DBUtility;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.elitesm.web.dashboard.widget.dao.BaseDAO;
import com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory.JVMDetailMemoryTotalData;
import com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory.JVMDetailMemoryTotalData.JVMDetailMemoryBuilder;
import com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory.JVMDetailMemoryUsageData;
import com.elitecore.elitesm.web.dashboard.widget.model.jvmdetailmemory.JVMMemoryPoolConstant;


public class DefaultJVMDetailMemoryUsageDao extends BaseDAO  implements DetailMemoryUsageDao {
   
	private static final String MODULE=DefaultJVMDetailMemoryUsageDao.class.getSimpleName();
	private static final String Query_Memory_Usage = "SELECT B.NAME,A.CREATETIME,TO_CHAR(A.CREATETIME,'YYYY-MM-DD HH24:MI:SS') AS TIME,A.JVMMEMPOOLINDEX,A.JVMMEMPOOLNAME,A.JVMMEMPOOLUSED   FROM JVMMEMPOOLTABLE A,TBLMNETSERVERINSTANCE B WHERE A.INSTANCEID=B.NETSERVERID order by A.INSTANCEID,A.CREATETIME";
	private static final String Query_Memory_Usage_Live = "SELECT B.NAME,A.CREATETIME,TO_CHAR(A.CREATETIME,'YYYY-MM-DD HH24:MI:SS') AS TIME,A.JVMMEMPOOLINDEX,A.JVMMEMPOOLNAME,A.JVMMEMPOOLUSED   FROM JVMMEMPOOLTABLE A,TBLMNETSERVERINSTANCE B WHERE A.INSTANCEID=B.NETSERVERID  and  A.CREATETIME >=(SYSDATE-7/1440) order by A.INSTANCEID,A.CREATETIME ASC";
	private static final String LIVE_MEMORY_USAGE_DETAIL_QUERY = "SELECT B.NAME,A.CREATETIME,TO_CHAR(A.CREATETIME,'YYYY-MM-DD HH24:MI:SS') AS TIME,A.JVMMEMPOOLINDEX,A.JVMMEMPOOLNAME,A.JVMMEMPOOLUSED,A.JVMSYSTEMLOADAVERAGE,A.JVMSYSTEMLOADAVERAGE_MINVAL,A.JVMSYSTEMLOADAVERAGE_MAXVAL   FROM JVMMEMPOOLTABLE A,TBLMNETSERVERINSTANCE B WHERE A.INSTANCEID=B.NETSERVERID order by A.INSTANCEID,A.CREATETIME desc";

	
	@Override
	public List<JVMDetailMemoryTotalData> getDetailMemoryUsageData() throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Query : " + Query_Memory_Usage);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String,List<JVMDetailMemoryUsageData>> memoryMap=null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(Query_Memory_Usage, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				if(memoryMap==null){
					memoryMap=new LinkedHashMap<String,List<JVMDetailMemoryUsageData>>(8);
				}
				JVMDetailMemoryUsageData memoryData=new JVMDetailMemoryUsageData();
				memoryData.setServerInstance(rs.getString("NAME"));
				memoryData.setCreateTime(rs.getString("TIME"));
				memoryData.setJvmPoolIndex(rs.getInt("JVMMEMPOOLINDEX"));
				memoryData.setJvmPoolName(rs.getString("JVMMEMPOOLNAME"));
				memoryData.setJvmPoolUsed((rs.getLong("JVMMEMPOOLUSED")));
				memoryData.setEpochTime((rs.getTimestamp("CREATETIME")).getTime());
				if(memoryMap.get(memoryData.getCreateTime())==null){
					List<JVMDetailMemoryUsageData> memoryList=new ArrayList<JVMDetailMemoryUsageData>(5);
					memoryList.add(memoryData);
					memoryMap.put(memoryData.getCreateTime(), memoryList);
				}else{
					memoryMap.get(memoryData.getCreateTime()).add(memoryData);
				}
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
		return convertData(memoryMap);
	}

	@Override
	public List<JVMDetailMemoryTotalData> getDetailMemoryUsageData(int interval) throws Exception{
		LogManager.getLogger().debug(MODULE, "Execute Query : " + Query_Memory_Usage_Live);
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		Map<String,List<JVMDetailMemoryUsageData>> memoryMap=null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(Query_Memory_Usage_Live, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			
			while(rs.next()){
				if(memoryMap==null){
					memoryMap=new LinkedHashMap<String,List<JVMDetailMemoryUsageData>>(8);
				}
					
				JVMDetailMemoryUsageData memoryData=new JVMDetailMemoryUsageData();
				memoryData.setServerInstance(rs.getString("NAME"));
				memoryData.setCreateTime(rs.getString("TIME"));
				memoryData.setJvmPoolIndex(rs.getInt("JVMMEMPOOLINDEX"));
				memoryData.setJvmPoolName(rs.getString("JVMMEMPOOLNAME"));
				memoryData.setJvmPoolUsed((rs.getLong("JVMMEMPOOLUSED")));
				memoryData.setEpochTime((rs.getTimestamp("CREATETIME")).getTime());
				if(memoryMap.get(memoryData.getCreateTime())==null){
					List<JVMDetailMemoryUsageData> memoryList=new ArrayList<JVMDetailMemoryUsageData>(5);
					memoryList.add(memoryData);
					memoryMap.put(memoryData.getCreateTime(), memoryList);
				}else{
					memoryMap.get(memoryData.getCreateTime()).add(memoryData);
				}
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
		
		return convertData(memoryMap);
	}
	
   private List<JVMDetailMemoryTotalData> convertData(Map<String,List<JVMDetailMemoryUsageData>> memoryMap){
	        if(memoryMap==null || memoryMap.isEmpty()){
	        	return null;
	        }
	        List<JVMDetailMemoryTotalData> list=new LinkedList<JVMDetailMemoryTotalData>(); 
	        for(List<JVMDetailMemoryUsageData> memoryList:memoryMap.values()){
	        	 JVMDetailMemoryTotalData memoryData=convertMemoryToTotalData(memoryList);
	        	 list.add(memoryData);
	        }
	   return list;
 }
   
   private JVMDetailMemoryTotalData convertMemoryToTotalData(List<JVMDetailMemoryUsageData> memoryList){
	   if(memoryList==null||memoryList.isEmpty()){
		   return null;
	   }   
	   JVMDetailMemoryBuilder memoryBuilder=new JVMDetailMemoryBuilder(memoryList.get(0).getEpochTime(),memoryList.get(0).getServerInstance());
	   for(JVMDetailMemoryUsageData memoryData:memoryList){
		   if(memoryData.getJvmPoolIndex()==JVMMemoryPoolConstant.PS_EDEN_SPACE.poolIndex){
			   memoryBuilder.withEdenSpace(memoryData.getJvmPoolUsed());
		   }else if(memoryData.getJvmPoolIndex()==JVMMemoryPoolConstant.PS_SURVIVOR_SPACE.poolIndex){
			   memoryBuilder.withSurvivorSpace(memoryData.getJvmPoolUsed());
		   }else if(memoryData.getJvmPoolIndex()==JVMMemoryPoolConstant.PS_PERM_GEN.poolIndex){
			   memoryBuilder.withPermGen(memoryData.getJvmPoolUsed());
		   }else if(memoryData.getJvmPoolIndex()==JVMMemoryPoolConstant.PS_OLD_GEN.poolIndex){
			   memoryBuilder.withOldGen(memoryData.getJvmPoolUsed());
		   }
	   }
	   return memoryBuilder.build();
   }

	@Override
	public List<JVMDetailMemoryUsageData> getDetailMemoryUsageDataList(String serverKey) throws Exception {
		LogManager.getLogger().debug(MODULE, "Execute Query : " + LIVE_MEMORY_USAGE_DETAIL_QUERY);
		List<JVMDetailMemoryUsageData>  memoryDataList = new ArrayList<JVMDetailMemoryUsageData>();
		List<JVMDetailMemoryUsageData> memoryDatasList = new ArrayList<JVMDetailMemoryUsageData>();
		Connection conn = null;
		PreparedStatement pstmt = null;
		ResultSet rs = null;
		try {
			conn = getConnection();
			pstmt = conn.prepareStatement(LIVE_MEMORY_USAGE_DETAIL_QUERY, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
			pstmt.setFetchSize(40000);
			rs = pstmt.executeQuery();
			while(rs.next()){
				
				JVMDetailMemoryUsageData memoryData=new JVMDetailMemoryUsageData();
				memoryData.setServerInstance(rs.getString("NAME"));
				memoryData.setCreateTime(rs.getString("TIME"));
				memoryData.setJvmPoolIndex(rs.getInt("JVMMEMPOOLINDEX"));
				memoryData.setJvmPoolName(rs.getString("JVMMEMPOOLNAME"));
				memoryData.setJvmPoolUsed((rs.getLong("JVMMEMPOOLUSED")));
				memoryData.setEpochTime((rs.getTimestamp("CREATETIME")).getTime());
				memoryData.setSystemLoadAvgMin(rs.getLong("JVMSYSTEMLOADAVERAGE_MINVAL"));
				memoryData.setSystemLoadAvgMax(rs.getLong("JVMSYSTEMLOADAVERAGE_MAXVAL"));
				memoryData.setSystemLoadAvg(rs.getFloat("JVMSYSTEMLOADAVERAGE"));
				
				memoryDataList.add(memoryData);
			}
			
			if(serverKey != null && serverKey.length() > 0 ){
				String serverDataKeys[] = serverKey.split(",");
							
					for(String serverName : serverDataKeys){
							for(JVMDetailMemoryUsageData memoryUsageDataObj : memoryDataList){
									serverName=serverName.trim();
									if(serverName.equals(memoryUsageDataObj.getServerInstance())){
										memoryDatasList.add(memoryUsageDataObj);
									}
								}
							}
				return memoryDatasList;
			}else{
				return memoryDataList;
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
