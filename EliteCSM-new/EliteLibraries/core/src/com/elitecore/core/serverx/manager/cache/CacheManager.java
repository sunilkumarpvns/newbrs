package com.elitecore.core.serverx.manager.cache;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.StringUtility;

public class CacheManager {
	private static final String MODULE = "CACHE-MGR";
	private List<Cacheable> cacheableObjs;
	private Map<String,CacheStatistics> cacheStatisticsMap;
	public CacheManager(){
		this.cacheableObjs = new ArrayList<Cacheable>();		
		this.cacheStatisticsMap = new HashMap<String, CacheStatistics>();
	}

	public void load(Cacheable cacheableObj){
		if(cacheableObj != null && !this.cacheableObjs.contains(cacheableObj)){
			this.cacheableObjs.add(cacheableObj);
			CacheDetailProvider cacheDetail = new CacheDetailProvider();
			cacheDetail.setName(cacheableObj.getName());
			cacheDetail.setResultCode(CacheConstants.SUCCESS);			
			CacheStatistics cacheStatistic = createStatistics(cacheDetail);
			cacheStatisticsMap.put(cacheStatistic.getName(), cacheStatistic);
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(MODULE,cacheableObj.getName() + " cached successfully");
		}else{	
			if(cacheableObj == null){
				if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
					LogManager.getLogger().info(MODULE,"Null object can not be cached");
			}				
		}
					
	}
	
	public void reload(){
		Map<String,CacheStatistics> cacheStatistics = new HashMap<String, CacheStatistics>();
		
		final int size = this.cacheableObjs.size();		
		for(int i=0;i<size;i++){			
			CacheDetail cacheDetail = this.cacheableObjs.get(i).reloadCache();
			if(cacheDetail != null){
				CacheStatistics cacheStatistic = createStatistics(cacheDetail);
				cacheStatistics.put(cacheStatistic.getName(), cacheStatistic);				
			}
		}
		
		this.cacheStatisticsMap = cacheStatistics;		
	}
	
	/**
	 * 
	 * @param cacheableObjNames : Names which are stored in cache statistics map.  
	 */
	
	public void reload(String... cacheableObjNames) {
		for (String cacheableObjName : cacheableObjNames) {
			reload(cacheableObjName);
		}

	}
	
	public void reload(String cacheableObjName) {

		for (Cacheable cacheable : cacheableObjs) {
			
			if (cacheable.getName().equals(cacheableObjName) == false) {
				continue;
			} else {

				CacheDetail cacheDetail = cacheable.reloadCache();

				if (cacheDetail != null) {
					CacheStatistics cacheStatistic = createStatistics(cacheDetail);
					this.cacheStatisticsMap.put(cacheStatistic.getName(), cacheStatistic);
				}
			}

		}

	}
	
	
	private CacheStatistics createStatistics(CacheDetail cacheDetail){
		
		CacheStatisticsProvider cacheStatistic = (CacheStatisticsProvider)this.cacheStatisticsMap.get(cacheDetail.getName());

		if(cacheStatistic != null){
			cacheStatistic.setCacheDetail(cacheDetail);
		}else{					
			cacheStatistic = new CacheStatisticsProvider(cacheDetail);			
		}
		
		cacheStatistic.setLastRefreshDate(new Date());
		
		if(cacheDetail.getResultCode() == CacheConstants.SUCCESS){
			cacheStatistic.setLastSuccessfulRefreshDate(new Date());
		}		
		return cacheStatistic;
	}

	public Map<String,CacheStatistics>getDetailedStatistics(){
		return cacheStatisticsMap;	
	}
	
	
	public List<Map<String,String>> getRegisteredCacheObjectList(){
		
		List<Map<String,String>> cacheDetailsList = new ArrayList<Map<String,String>>();		
		SimpleDateFormat formatter = new SimpleDateFormat("dd-MM-yyyy HH:mm:ss");
		String strDate = null;		
		Map<String,String> cacheDetailMap=null;
		CacheStatistics cacheStatistic = null;
		for(Entry<String, CacheStatistics> entry : this.cacheStatisticsMap.entrySet()){
			cacheStatistic = entry.getValue();			
			cacheDetailMap=new HashMap<String,String>();
			cacheDetailMap.put("NAME",cacheStatistic.getName());
			cacheDetailMap.put("SOURCE",cacheStatistic.getSource());

			if((cacheStatistic.getLastSuccessfulRefreshDate())!=null)
				strDate=formatter.format(cacheStatistic.getLastSuccessfulRefreshDate());
			else
				strDate="NOT CACHED";
			
			cacheDetailMap.put("UPDATEDTIME",strDate);
			
			if(cacheStatistic.getLastRefreshDate()!=null)
				strDate=formatter.format(cacheStatistic.getLastRefreshDate());
			else
				strDate="----------";
			
			cacheDetailMap.put("LASTRELOADATTEMPTTIME",strDate);
			cacheDetailsList.add(cacheDetailMap);	
		}	 	
		return cacheDetailsList;	
	}

	
	private String getResultString(int resultCode){
		
		switch(resultCode){
		case CacheConstants.SUCCESS:
			return "SUCCESS";
		
		case CacheConstants.FAIL:
			return "FAIL";
		
		case CacheConstants.INTRIM:
			return "INTRIM";
		default: 
			return "UNDEFINED";
		}
		
	}
		
	public String getCacheStatistics() {

		StringBuilder sb = createHeader();

		for (Entry<String, CacheStatistics> entry : this.cacheStatisticsMap.entrySet()) {
			sb.append(createCacheStatistcs(entry.getValue()));
		}
		sb.append(StringUtility.fillChar("", 53, '-') + "\n");
		return sb.toString();
	}
	
	public String getCacheStatistics(String... cacheableObjNames) {

		StringBuilder sb = createHeader();

		for (Entry<String, CacheStatistics> entry : this.cacheStatisticsMap.entrySet()) {

			for (String cacheableObjName : cacheableObjNames) {

				if (entry.getKey().equals(cacheableObjName)) {
					sb.append(createCacheStatistcs(entry.getValue()));
				}
			}
		}
		sb.append(StringUtility.fillChar("", 53, '-') + "\n");
		return sb.toString();

	}

	private StringBuilder createHeader() {
		StringBuilder sb = new StringBuilder();
		sb.append(StringUtility.fillChar("", 53, '-') + "\n");
		sb.append(StringUtility.fillChar("Display Name", 35) + "|"
				+ StringUtility.fillChar("Remarks", 15) + "|" + "\n");
		sb.append(StringUtility.fillChar("", 53, '-') + "\n");
		return sb;
	}

	private StringBuilder createCacheStatistcs(CacheStatistics cacheStatistics) {
		StringBuilder sb = new StringBuilder();
		CacheStatistics cacheStatistic = cacheStatistics;
		sb.append(StringUtility.fillChar(cacheStatistic.getName(), 35) + "|");
		sb.append(StringUtility.fillChar(getResultString(cacheStatistic.getResultCode()), 15) + "|" + "\n");
		return sb;
	}


}
