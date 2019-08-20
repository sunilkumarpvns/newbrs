package com.elitecore.elitesm.web.core.system.systeminformation;



import java.lang.management.GarbageCollectorMXBean;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.management.MemoryPoolMXBean;
import java.lang.management.MemoryType;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.lang.management.ThreadMXBean;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import java.util.TreeSet;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.systeminformation.form.SystemInformationForm;
import com.elitecore.elitesm.web.dashboard.memoryusage.GarbageCollectorData;
import com.elitecore.elitesm.web.dashboard.memoryusage.HeapMemoryUsageData;
import com.elitecore.elitesm.web.dashboard.memoryusage.NonHeapMemoryUsage;
import com.elitecore.elitesm.web.dashboard.memoryusage.ThreadInformationData;
import com.elitecore.elitesm.web.servermgr.server.graph.PieChartData;
import com.google.gson.Gson;

public class SystemInformationAction extends BaseDispatchAction {

	private static final String ACTION_ALIAS = ConfigConstant.CREATE_DRIVER;
	private static final String MODULE = SystemInformationAction.class.getSimpleName();
	private static final String VIEW_SYSTEM_CONFIGURATION = "systemInformation";
	private static final String SUCCESS_FORWARD = "success";
	private static final String FAILURE_FORWARD = "failure";
	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;
	private static final int DAY = 24 * HOUR;
	
	public ActionForward getSystemConfiguration(ActionMapping mapping, ActionForm form,HttpServletRequest request, HttpServletResponse response)throws Exception {
		Logger.logInfo(MODULE, "Entered getSystemConfiguration method of "+ getClass().getName());
		try {
				SystemInformationForm systemInformationForm = (SystemInformationForm) form;

				MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
				MemoryUsage heapmemoryUsage = mbean.getHeapMemoryUsage();
				MemoryUsage nonHeapmemoryUsage = mbean.getNonHeapMemoryUsage();
				
				List<HeapMemoryUsageData> heapMemoryList=new ArrayList<HeapMemoryUsageData>();
				List<NonHeapMemoryUsage> nonHeapMemoryList=new ArrayList<NonHeapMemoryUsage>();
				
				//Get Heap Memory Info
				
				systemInformationForm.setHeapMemoryUsed(toMB(heapmemoryUsage.getUsed()));
				systemInformationForm.setHeapMax(toMB(heapmemoryUsage.getMax()) );
				systemInformationForm.setHeapUsage(((heapmemoryUsage.getUsed()*100)/heapmemoryUsage.getMax()));
				systemInformationForm.setFreeHeapMemory((toMB(heapmemoryUsage.getMax())) - (toMB(heapmemoryUsage.getUsed())));
				
				
				List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
				for(MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans){
					if(memoryPoolMXBean.getType() == MemoryType.HEAP){
						HeapMemoryUsageData heapMemoryUsageData=new HeapMemoryUsageData();
						MemoryUsage usage = memoryPoolMXBean.getUsage();
						MemoryUsage peakUsage = memoryPoolMXBean.getPeakUsage();
						
						heapMemoryUsageData.setName(memoryPoolMXBean.getName());
						heapMemoryUsageData.setUsed(toMB(usage.getUsed()));
						heapMemoryUsageData.setMax(toMB(usage.getMax()));
						heapMemoryUsageData.setUsage(((usage.getUsed()*100)/usage.getMax()));
						heapMemoryUsageData.setPeakused(toMB(peakUsage.getUsed()));
						heapMemoryUsageData.setPeakmax(toMB(peakUsage.getMax()));
						
						heapMemoryList.add(heapMemoryUsageData);
					}
				}
				
				//Get Non Heap Memory Info
				
				systemInformationForm.setNonHeapMemoryUsed(toMB(nonHeapmemoryUsage.getUsed()));
				systemInformationForm.setNonHeapMax(toMB(nonHeapmemoryUsage.getMax()));
				systemInformationForm.setNonHeapUsage(((nonHeapmemoryUsage.getUsed()*100)/nonHeapmemoryUsage.getMax()));
				systemInformationForm.setFreeNonHeapMemory((toMB(nonHeapmemoryUsage.getMax())) - (toMB(nonHeapmemoryUsage.getUsed())));
				
				
				for(MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans){
					if(memoryPoolMXBean.getType() == MemoryType.NON_HEAP){
						NonHeapMemoryUsage nonMemoryUsage=new NonHeapMemoryUsage();
						MemoryUsage usage = memoryPoolMXBean.getUsage();
						MemoryUsage peakUsage = memoryPoolMXBean.getPeakUsage();
						nonMemoryUsage.setName(memoryPoolMXBean.getName());
						nonMemoryUsage.setUsed(toMB(usage.getUsed()));
						nonMemoryUsage.setMax(toMB(usage.getMax()));
						nonMemoryUsage.setUsage(((usage.getUsed()*100)/usage.getMax()));
						nonMemoryUsage.setPeakmax(toMB(peakUsage.getMax()));
						nonMemoryUsage.setPeakused(toMB(peakUsage.getUsed()));
						
						nonHeapMemoryList.add(nonMemoryUsage);
					}
				}
				
				//Get Garbage Collector Info
				List<GarbageCollectorData> garbageCollectorList=new ArrayList<GarbageCollectorData>();
				List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
				for(GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans){			
					GarbageCollectorData garbageCollectorData=new GarbageCollectorData();
					garbageCollectorData.setName(garbageCollectorMXBean.getName());
					garbageCollectorData.setCollectionCount(garbageCollectorMXBean.getCollectionCount());
					garbageCollectorData.setCollectionTime(garbageCollectorMXBean.getCollectionTime());
					
					garbageCollectorList.add(garbageCollectorData);
				}

				systemInformationForm.setGarbageCollectorDataList(garbageCollectorList);
				systemInformationForm.setServerUpTime(toString(ManagementFactory.getRuntimeMXBean().getUptime()));
				systemInformationForm.setHeapMemoryList(heapMemoryList);
				systemInformationForm.setNonHeapMemoryList(nonHeapMemoryList);
				
				//get Thread Info
				
				List<ThreadInformationData> threadInformationDataList=new ArrayList<ThreadInformationData>();
				
				ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();		
				for(ThreadInfo info :threadMXBean.dumpAllThreads(true, true)){			
					ThreadInformationData threadInformationData= new ThreadInformationData();
					threadInformationData.setThreadName(info.getThreadName());
					threadInformationData.setThreadState(info.getThreadState().toString());
					threadInformationData.setBlockedCount(info.getBlockedCount());
					threadInformationData.setTotalBlockedTime(toString(info.getBlockedTime()));
					threadInformationData.setWaitedCount(info.getWaitedCount());
					threadInformationData.setTotalWaitedTime(toString(info.getWaitedTime()));
					
					StringBuilder sb = new StringBuilder();
	                   
					sb.append("\n"+info.getThreadName() + "[" + info.getThreadState() + "] Lock : " + info.getLockName() + " Owner : " + info.getLockOwnerName());
                    for(StackTraceElement element :  info.getStackTrace()){
                    	sb.append("\n" + element.getClassName() + "." + element.getMethodName() + " at Line : " + element.getLineNumber());                      
                    }
                    
                    if(info.getLockedMonitors().length > 0){
                    	sb.append("\n"+"Monitor Info : ");  
                    	for(MonitorInfo moninfo :  info.getLockedMonitors()){
                         	sb.append("\nClass Name" + moninfo.getClassName() +"IdentityHashCode : "+moninfo.getIdentityHashCode()+"LockedStackDepth : "+moninfo.getLockedStackDepth());                            
                        }
                    }
                   
                    if(info.getLockedSynchronizers().length > 0){
                    	sb.append("\n"+"Lock Info : ");
                        for(LockInfo lockinfo : info.getLockedSynchronizers()){
                           	sb.append("\nClass Name" + lockinfo.getClassName() +"IdentityHashCode : "+lockinfo.getIdentityHashCode());                            
                        }
                    }
                    
					threadInformationData.setStatckTraceInfo(sb.toString());
					
					threadInformationDataList.add(threadInformationData);
				} 
				
				systemInformationForm.setThreadInfoDataList(threadInformationDataList);
				
				Map<String,String> systemPropertiesMap= new HashMap<String, String>();
				
				Properties pr = System.getProperties();
				TreeSet propKeys = new TreeSet(pr.keySet());  // TreeSet sorts keys
				for (Iterator it = propKeys.iterator(); it.hasNext(); ) {
				    String key = (String)it.next();
				    systemPropertiesMap.put(key, (String) pr.get(key));
				}  
				systemInformationForm.setSystemPropertiesMap(systemPropertiesMap);
				request.setAttribute("systemInformationForm", systemInformationForm);
		        
				return mapping.findForward(VIEW_SYSTEM_CONFIGURATION); 
		
		} catch (Exception e) {
			Logger.logError(MODULE,"Error during Data Manager operation , reason : "+ e.getMessage());
			// Logger.logTrace(MODULE, e);
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(e);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
		}
		return mapping.findForward(FAILURE_FORWARD);
	}
	
	public ActionForward getPieChartData(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called getPieChartData of "+ getClass().getName());
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
			MemoryUsage heapmemoryUsage = mbean.getHeapMemoryUsage();
			MemoryUsage nonHeapmemoryUsage = mbean.getNonHeapMemoryUsage();
			PieChartData pieChartData =new  PieChartData();
			
			try {
				pieChartData.setFreeHeapMemory((toMB(heapmemoryUsage.getMax())) - (toMB(heapmemoryUsage.getUsed())));
				pieChartData.setHeapUsed(toMB(heapmemoryUsage.getUsed()));
			
				pieChartData.setFreeNonHeapMemory((toMB(nonHeapmemoryUsage.getMax())) - (toMB(nonHeapmemoryUsage.getUsed())));
				pieChartData.setNonHeapused(toMB(nonHeapmemoryUsage.getUsed()));
				
			} catch (Exception e) {
				e.printStackTrace();
			}
			
			String json = new Gson().toJson(pieChartData);
			response.getWriter().write(json);
		
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting pie charts information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	
	public ActionForward getGarbageInfo(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called getGarbageInfo of "+ getClass().getName());
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				List<GarbageCollectorData> garbageCollectorList=new ArrayList<GarbageCollectorData>();
				List<GarbageCollectorMXBean> garbageCollectorMXBeans = ManagementFactory.getGarbageCollectorMXBeans();
				for(GarbageCollectorMXBean garbageCollectorMXBean : garbageCollectorMXBeans){			
					GarbageCollectorData garbageCollectorData=new GarbageCollectorData();
					garbageCollectorData.setName(garbageCollectorMXBean.getName());
					garbageCollectorData.setCollectionCount(garbageCollectorMXBean.getCollectionCount());
					garbageCollectorData.setCollectionTime(garbageCollectorMXBean.getCollectionTime());
					
					garbageCollectorList.add(garbageCollectorData);
				}
				String json = new Gson().toJson(garbageCollectorList);
				response.getWriter().write(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting garbage collection information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	public ActionForward getJVMHeapMemoryData(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called getJVMHeapMemoryData of "+ getClass().getName());
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				MemoryMXBean mbean = ManagementFactory.getMemoryMXBean();
				
				List<HeapMemoryUsageData> heapMemoryList=new ArrayList<HeapMemoryUsageData>();
				
				//Get Heap Memory Info
				
				List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
				for(MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans){
					if(memoryPoolMXBean.getType() == MemoryType.HEAP){
						HeapMemoryUsageData heapMemoryUsageData=new HeapMemoryUsageData();
						MemoryUsage usage = memoryPoolMXBean.getUsage();
						MemoryUsage peakUsage = memoryPoolMXBean.getPeakUsage();
						
						heapMemoryUsageData.setName(memoryPoolMXBean.getName());
						heapMemoryUsageData.setUsed(toMB(usage.getUsed()));
						heapMemoryUsageData.setMax(toMB(usage.getMax()));
						heapMemoryUsageData.setUsage(((usage.getUsed()*100)/usage.getMax()));
						heapMemoryUsageData.setPeakused(toMB(peakUsage.getUsed()));
						heapMemoryUsageData.setPeakmax(toMB(peakUsage.getMax()));
						
						heapMemoryList.add(heapMemoryUsageData);
					}
				}
				
				String json = new Gson().toJson(heapMemoryList);
				response.getWriter().write(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting JVM heap memory data information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	
	public ActionForward getJVMNonHeapMemoryData(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called getJVMNonHeapMemoryData of "+ getClass().getName());
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				List<NonHeapMemoryUsage> nonHeapMemoryList=new ArrayList<NonHeapMemoryUsage>();
								
				//Get Heap Memory Info
				List<MemoryPoolMXBean> memoryPoolMXBeans = ManagementFactory.getMemoryPoolMXBeans();
				
				for(MemoryPoolMXBean memoryPoolMXBean : memoryPoolMXBeans){
					if(memoryPoolMXBean.getType() == MemoryType.NON_HEAP){
						NonHeapMemoryUsage nonMemoryUsage=new NonHeapMemoryUsage();
						MemoryUsage usage = memoryPoolMXBean.getUsage();
						MemoryUsage peakUsage = memoryPoolMXBean.getPeakUsage();
						nonMemoryUsage.setName(memoryPoolMXBean.getName());
						nonMemoryUsage.setUsed(toMB(usage.getUsed()));
						nonMemoryUsage.setMax(toMB(usage.getMax()));
						nonMemoryUsage.setUsage(((usage.getUsed()*100)/usage.getMax()));
						nonMemoryUsage.setPeakmax(toMB(peakUsage.getMax()));
						nonMemoryUsage.setPeakused(toMB(peakUsage.getUsed()));
						
						nonHeapMemoryList.add(nonMemoryUsage);
					}
				}
				
				String json = new Gson().toJson(nonHeapMemoryList);
				response.getWriter().write(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting JVM non heap memory information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	public ActionForward getThreadDetails(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called getThreadDetails of "+ getClass().getName());
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			try {
				List<ThreadInformationData> threadInformationDataList=new ArrayList<ThreadInformationData>();
				
				ThreadMXBean threadMXBean = ManagementFactory.getThreadMXBean();		
				for(ThreadInfo info :threadMXBean.dumpAllThreads(true, true)){			
					ThreadInformationData threadInformationData= new ThreadInformationData();
					threadInformationData.setThreadName(info.getThreadName());
					threadInformationData.setThreadState(info.getThreadState().toString());
					threadInformationData.setBlockedCount(info.getBlockedCount());
					threadInformationData.setTotalBlockedTime(toString(info.getBlockedTime()));
					threadInformationData.setWaitedCount(info.getWaitedCount());
					threadInformationData.setTotalWaitedTime(toString(info.getWaitedTime()));
					
					StringBuilder sb = new StringBuilder();
	                   
					sb.append("\n"+info.getThreadName() + "[" + info.getThreadState() + "] Lock : " + info.getLockName() + " Owner : " + info.getLockOwnerName());
                    for(StackTraceElement element :  info.getStackTrace()){
                    	sb.append("\n" + element.getClassName() + "." + element.getMethodName() + " at Line : " + element.getLineNumber());                      
                    }
                    
                    if(info.getLockedMonitors().length > 0){
                    	sb.append("\n"+"Monitor Info : ");  
                    	for(MonitorInfo moninfo :  info.getLockedMonitors()){
                         	sb.append("\nClass Name" + moninfo.getClassName() +"IdentityHashCode : "+moninfo.getIdentityHashCode()+"LockedStackDepth : "+moninfo.getLockedStackDepth());                            
                        }
                    }
                   
                    if(info.getLockedSynchronizers().length > 0){
                    	sb.append("\n"+"Lock Info : ");
                        for(LockInfo lockinfo : info.getLockedSynchronizers()){
                           	sb.append("\nClass Name" + lockinfo.getClassName() +"IdentityHashCode : "+lockinfo.getIdentityHashCode());                            
                        }
                    }
                    
					threadInformationData.setStatckTraceInfo(sb.toString());
					
					threadInformationDataList.add(threadInformationData);
				}
				String json = new Gson().toJson(threadInformationDataList);
				response.getWriter().write(json);
			} catch (Exception e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting thread details. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	
	
	public static long toMB(long l){
		return Math.round((l/1024)/1024); 
	}
	
	public static String toString(long uptime){		
		StringBuffer text = new StringBuffer("");
		if(uptime < 0){
			return "";
		}
		if (uptime > DAY) {
		  text.append(uptime / DAY).append(" days ");
		  uptime %= DAY;
		}
		if (uptime > HOUR) {
		  text.append(uptime / HOUR).append(" hours ");
		  uptime %= HOUR;
		}
		if (uptime > MINUTE) {
		  text.append(uptime / MINUTE).append(" minutes ");
		  uptime %= MINUTE;
		}
		if (uptime > SECOND) {
		  text.append(uptime / SECOND).append(" seconds ");
		  uptime %= SECOND;
		}
		text.append(uptime + " ms");			
		return text.toString();
	}

}
