package com.elitecore.elitesm.web.servermgr.server;

import java.io.IOException;
import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryUsage;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.List;

import javax.management.AttributeNotFoundException;
import javax.management.InstanceNotFoundException;
import javax.management.MBeanException;
import javax.management.MBeanServer;
import javax.management.MBeanServerConnection;
import javax.management.MalformedObjectNameException;
import javax.management.ObjectName;
import javax.management.ReflectionException;
import javax.management.openmbean.CompositeDataSupport;
import javax.management.remote.JMXConnector;
import javax.management.remote.JMXConnectorFactory;
import javax.management.remote.JMXServiceURL;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;
import org.apache.struts.action.ActionMessage;
import org.apache.struts.action.ActionMessages;

import com.elitecore.elitesm.blmanager.servermgr.server.NetServerBLManager;
import com.elitecore.elitesm.datamanager.DataManagerException;
import com.elitecore.elitesm.datamanager.core.exceptions.auhorization.ActionNotPermitedException;
import com.elitecore.elitesm.datamanager.core.system.staff.data.IStaffData;
import com.elitecore.elitesm.datamanager.servermgr.data.INetServerInstanceData;
import com.elitecore.elitesm.util.constants.ConfigConstant;
import com.elitecore.elitesm.util.exception.EliteExceptionUtils;
import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.elitecore.elitesm.web.core.system.login.forms.SystemLoginForm;
import com.elitecore.elitesm.web.dashboard.memoryusage.GarbageCollectorData;
import com.elitecore.elitesm.web.dashboard.memoryusage.HeapMemoryUsageData;
import com.elitecore.elitesm.web.dashboard.memoryusage.NonHeapMemoryUsage;
import com.elitecore.elitesm.web.dashboard.memoryusage.ThreadInformationData;
import com.elitecore.elitesm.web.servermgr.server.forms.ViewNetServerGraphForm;
import com.elitecore.elitesm.web.servermgr.server.graph.MemoryThreadInformation;
import com.elitecore.elitesm.web.servermgr.server.graph.PieChartData;
import com.google.gson.Gson;

public class InitViewNetServerGraphAction extends BaseDispatchAction {
	private static String VIEW_SERVER_GRAPH ="viewNetServerGraph";
	private static String ACTION_ALIAS =ConfigConstant.VIEW_SERVER_GRAPH;
	private static final int SECOND = 1000;
	private static final int MINUTE = 60 * SECOND;
	private static final int HOUR = 60 * MINUTE;
	private static final int DAY = 24 * HOUR;
	private static final String FAILURE = "failure";
	private static String netServerId="";
	
	public ActionForward initView(ActionMapping mapping, ActionForm form, HttpServletRequest request, HttpServletResponse response) {
		Logger.logDebug(MODULE, "Called initView of "+ getClass().getName());
		ViewNetServerGraphForm viewNetServerGraphForm = (ViewNetServerGraphForm)form;
		String strNetServerId = request.getParameter("netserverid");
		netServerId=request.getParameter("netserverid");
		try {
			checkAccess(request, ACTION_ALIAS);
			IStaffData staffData = getStaffObject(((SystemLoginForm)request.getSession().getAttribute("radiusLoginForm")));
			String netServerId=null;
			if(strNetServerId != null){
				netServerId = strNetServerId;
			}
			
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
			List netServerTypeList = netServerBLManager.getNetServerTypeList();
			
			String hostName = netServerInstanceData.getAdminHost();		
			int portNum = netServerInstanceData.getAdminPort();

			try {
				JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostName + ":" + portNum +  "/jmxrmi");
				JMXConnector remoteConnection = JMXConnectorFactory.connect(url);
				MBeanServerConnection serverConnection = remoteConnection.getMBeanServerConnection();
				try {
					
					long uptime = (Long) serverConnection.getAttribute(new ObjectName(ManagementFactory.RUNTIME_MXBEAN_NAME), "Uptime");
					viewNetServerGraphForm.setServerUpTime(toString(uptime));
					
					List<HeapMemoryUsageData> heapMemoryList=new ArrayList<HeapMemoryUsageData>();
					List<NonHeapMemoryUsage> nonHeapMemoryList=new ArrayList<NonHeapMemoryUsage>();
					
					CompositeDataSupport compositeDataSupport = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME), "HeapMemoryUsage");
					MemoryUsage heapmemoryUsage =  MemoryUsage.from(compositeDataSupport);
					
					//Get Heap Memory Info
					
					viewNetServerGraphForm.setHeapMemoryUsed(toMB(heapmemoryUsage.getUsed()));
					viewNetServerGraphForm.setHeapMax(toMB(heapmemoryUsage.getMax()) );
					viewNetServerGraphForm.setHeapUsage(((heapmemoryUsage.getUsed()*100)/heapmemoryUsage.getMax()));
					viewNetServerGraphForm.setFreeHeapMemory((toMB(heapmemoryUsage.getMax())) - (toMB(heapmemoryUsage.getUsed())));
					
					CompositeDataSupport compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=PS Eden Space"), "Usage");				
					MemoryUsage memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);

					HeapMemoryUsageData heapMemoryUsageData=new  HeapMemoryUsageData();
					heapMemoryUsageData.setName("PS Eden Space");
					heapMemoryUsageData.setUsed(toMB(memoryPoolUsage.getUsed()));
					heapMemoryUsageData.setMax(toMB(memoryPoolUsage.getMax()));
					heapMemoryUsageData.setUsage(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax()));
					heapMemoryUsageData.setPeakused(toMB(memoryPoolUsage.getUsed()));
					heapMemoryUsageData.setPeakmax(toMB(memoryPoolUsage.getMax()));
					
					heapMemoryList.add(heapMemoryUsageData);
					
					compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=PS Survivor Space"), "Usage");				
					memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);

					heapMemoryUsageData=new  HeapMemoryUsageData();
					heapMemoryUsageData.setName("PS Survivor Space");
					heapMemoryUsageData.setUsed(toMB(memoryPoolUsage.getUsed()));
					heapMemoryUsageData.setMax(toMB(memoryPoolUsage.getMax()));
					heapMemoryUsageData.setUsage(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax()));
					heapMemoryUsageData.setPeakused(toMB(memoryPoolUsage.getUsed()));
					heapMemoryUsageData.setPeakmax(toMB(memoryPoolUsage.getMax()));
					
					heapMemoryList.add(heapMemoryUsageData);
					
					compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=PS Old Gen"), "Usage");				
					memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);

					heapMemoryUsageData=new  HeapMemoryUsageData();
					heapMemoryUsageData.setName("PS Old Gen");
					heapMemoryUsageData.setUsed(toMB(memoryPoolUsage.getUsed()));
					heapMemoryUsageData.setMax(toMB(memoryPoolUsage.getMax()));
					heapMemoryUsageData.setUsage(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax()));
					heapMemoryUsageData.setPeakused(toMB(memoryPoolUsage.getUsed()));
					heapMemoryUsageData.setPeakmax(toMB(memoryPoolUsage.getMax()));
					
					heapMemoryList.add(heapMemoryUsageData);
					
					viewNetServerGraphForm.setHeapMemoryList(heapMemoryList);
					
					compositeDataSupport = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME), "NonHeapMemoryUsage");
					heapmemoryUsage =  MemoryUsage.from(compositeDataSupport);
					
					//Get Non Heap Memory Info
					
					viewNetServerGraphForm.setNonHeapMemoryUsed(toMB(heapmemoryUsage.getUsed()));
					viewNetServerGraphForm.setNonHeapMax(toMB(heapmemoryUsage.getMax()));
					viewNetServerGraphForm.setNonHeapUsage(((heapmemoryUsage.getUsed()*100)/heapmemoryUsage.getMax()));
					viewNetServerGraphForm.setFreeNonHeapMemory((toMB(heapmemoryUsage.getMax())) - (toMB(heapmemoryUsage.getUsed())));
					
					NonHeapMemoryUsage nonHeapMemoryUsageData=new  NonHeapMemoryUsage();
					
					compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=PS Perm Gen"), "Usage");				
					memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);

					nonHeapMemoryUsageData.setName("PS Perm Gen");
					nonHeapMemoryUsageData.setUsed(toMB(memoryPoolUsage.getUsed()));
					nonHeapMemoryUsageData.setMax(toMB(memoryPoolUsage.getMax()));
					nonHeapMemoryUsageData.setUsage(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax()));
					nonHeapMemoryUsageData.setPeakused(toMB(memoryPoolUsage.getUsed()));
					nonHeapMemoryUsageData.setPeakmax(toMB(memoryPoolUsage.getMax()));
					
					nonHeapMemoryList.add(nonHeapMemoryUsageData);	
					
					compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=Code Cache"), "Usage");				
					memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);

					nonHeapMemoryUsageData=new NonHeapMemoryUsage();
					nonHeapMemoryUsageData.setName("Code Cache");
					nonHeapMemoryUsageData.setUsed(toMB(memoryPoolUsage.getUsed()));
					nonHeapMemoryUsageData.setMax(toMB(memoryPoolUsage.getMax()));
					nonHeapMemoryUsageData.setUsage(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax()));
					nonHeapMemoryUsageData.setPeakused(toMB(memoryPoolUsage.getUsed()));
					nonHeapMemoryUsageData.setPeakmax(toMB(memoryPoolUsage.getMax()));
					
					nonHeapMemoryList.add(nonHeapMemoryUsageData);	
					
					viewNetServerGraphForm.setNonHeapMemoryList(nonHeapMemoryList);
					
					// get Grabage Colletor data
					List<GarbageCollectorData> garbageCollectorList=new ArrayList<GarbageCollectorData>();
					
					GarbageCollectorData garbageCollectorData=new  GarbageCollectorData();
					
					String garbageCollectorMXBean = (String) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS MarkSweep"), "Name");
					Long collectionCount = (Long) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS MarkSweep"), "CollectionCount");
					Long collectionTime = (Long) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS MarkSweep"), "CollectionTime");
					
					garbageCollectorData.setName(garbageCollectorMXBean);
					garbageCollectorData.setCollectionCount(collectionCount);
					garbageCollectorData.setCollectionTime(collectionTime);
					
					garbageCollectorList.add(garbageCollectorData);
						
					garbageCollectorData=new GarbageCollectorData();
					
					garbageCollectorMXBean = (String) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS Scavenge"), "Name");
					collectionCount = (Long) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS Scavenge"), "CollectionCount");
					collectionTime = (Long) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS Scavenge"), "CollectionTime");
					
					garbageCollectorData.setName(garbageCollectorMXBean);
					garbageCollectorData.setCollectionCount(collectionCount);
					garbageCollectorData.setCollectionTime(collectionTime);
					
					garbageCollectorList.add(garbageCollectorData);
					
					viewNetServerGraphForm.setGarbageCollectorDataList(garbageCollectorList);
					
					//get Thread Info
					
					List<ThreadInformationData> threadInformationDataList=new ArrayList<ThreadInformationData>();
					
					long[] threadID = (long[]) serverConnection.getAttribute(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "AllThreadIds");
					
					String[] signature = new String [] {"long","int"};
					for (int i = 0; i < threadID.length; i++) {
						Object[] params = new Object [] {threadID[i],Integer.MAX_VALUE};
						CompositeDataSupport compositeDataSupportThreadInfo = (CompositeDataSupport)serverConnection.invoke(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "getThreadInfo",params, signature);
						ThreadInfo threadInfo = ThreadInfo.from(compositeDataSupportThreadInfo);
						
						if(threadInfo != null){
							
							ThreadInformationData threadInformationData= new ThreadInformationData();
							threadInformationData.setThreadName((threadInfo.getThreadName() == null)? "" :threadInfo.getThreadName());
							threadInformationData.setThreadState(threadInfo.getThreadState().toString());
							threadInformationData.setBlockedCount(threadInfo.getBlockedCount());
							threadInformationData.setTotalBlockedTime(toString(threadInfo.getBlockedTime()));
							threadInformationData.setWaitedCount(threadInfo.getWaitedCount());
							threadInformationData.setTotalWaitedTime(toString(threadInfo.getWaitedTime()));
							StringBuilder sb = new StringBuilder();
							                   
							sb.append("\n"+threadInfo.getThreadName() + "[" + threadInfo.getThreadState() + "] Lock : " + threadInfo.getLockName() + " Owner : " + threadInfo.getLockOwnerName());
	                        for(StackTraceElement element :  threadInfo.getStackTrace()){
	                        	sb.append("\n" + element.getClassName() + "." + element.getMethodName() + " at Line : " + element.getLineNumber());                      
	                        }
	                        
	                        if(threadInfo.getLockedMonitors().length > 0){
	                        	sb.append("\n"+"Monitor Info : ");  
	                        	for(MonitorInfo moninfo :  threadInfo.getLockedMonitors()){
	                             	sb.append("\nClass Name" + moninfo.getClassName() +"IdentityHashCode : "+moninfo.getIdentityHashCode()+"LockedStackDepth : "+moninfo.getLockedStackDepth());                            
	                            }
	                        }
	                       
	                        if(threadInfo.getLockedSynchronizers().length > 0){
	                        	sb.append("\n"+"Lock Info : ");
	                            for(LockInfo lockinfo : threadInfo.getLockedSynchronizers()){
	                               	sb.append("\nClass Name" + lockinfo.getClassName() +"IdentityHashCode : "+lockinfo.getIdentityHashCode());                            
	                            }
	                        }
	                        
							threadInformationData.setStatckTraceInfo(sb.toString());
							
							threadInformationDataList.add(threadInformationData);
						}
					}
					viewNetServerGraphForm.setThreadInfoDataList(threadInformationDataList);
					
				} catch (AttributeNotFoundException e) {
					e.printStackTrace();
				} catch (InstanceNotFoundException e) {
					e.printStackTrace();
				} catch (MalformedObjectNameException e) {
					e.printStackTrace();
				} catch (MBeanException e) {
					e.printStackTrace();
				} catch (ReflectionException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}finally{
					remoteConnection.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			request.setAttribute("netServerInstanceData",netServerInstanceData);
		    request.setAttribute("netServerTypeList",netServerTypeList);
			request.setAttribute("viewNetServerGraph",viewNetServerGraphForm);
			doAuditing(staffData, ACTION_ALIAS);
			return mapping.findForward(VIEW_SERVER_GRAPH);
			
		}catch(ActionNotPermitedException e){
			Logger.logError(MODULE,"Error :-" + e.getMessage());
			printPermitedActionAlias(request);
			ActionMessages messages = new ActionMessages();
			messages.add("information", new ActionMessage("general.user.restricted"));
			saveErrors(request, messages);
			return mapping.findForward(INVALID_ACCESS_FORWARD);
		}catch(DataManagerException exp){
			Logger.logError(MODULE, "Error during Data Manager operation , reason : " + exp.getMessage());
			Object errorElements[] = EliteExceptionUtils.getFullStackTraceAsArray(exp);
			request.setAttribute("errorDetails", errorElements);
			ActionMessage message = new ActionMessage("general.error");
			ActionMessages messages = new ActionMessages();
			messages.add("information", message);
			saveErrors(request, messages);
			
			return mapping.findForward(FAILURE);
		}
	}
	
	public ActionForward getMemoryThreadInfo(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called getMemoryThreadInfo of "+ getClass().getName());
		try {
			long heapMemoryUsed=0,heapMemoryPeakUsage=0;
			int totalThreads = 0 ,peakThreads = 0;
			
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
			MemoryThreadInformation memoryThreadDataObj = new  MemoryThreadInformation();
			
			String hostName = netServerInstanceData.getAdminHost();		
			int portNum = netServerInstanceData.getAdminPort();
			Long psMarkSweepcollectionCount=null,psMarkSweepcollectionTime = null,psScavengeCollectionTime=null,psScavengeCollectionCount=null;
			try {
				JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostName + ":" + portNum +  "/jmxrmi");
				JMXConnector remoteConnection = JMXConnectorFactory.connect(url);
				MBeanServerConnection serverConnection = remoteConnection.getMBeanServerConnection();
				try {
					
					CompositeDataSupport compositeDataSupport = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME), "HeapMemoryUsage");
					MemoryUsage heapmemoryUsage =  MemoryUsage.from(compositeDataSupport);
						
					heapMemoryUsed=toMB(heapmemoryUsage.getUsed());					
					heapMemoryPeakUsage=toMB((Math.round(heapmemoryUsage.getMax()*0.75)));
					
					peakThreads= (Integer) serverConnection.getAttribute(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "PeakThreadCount");
					totalThreads =  (Integer) serverConnection.getAttribute(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "ThreadCount");
				
					psMarkSweepcollectionCount = (Long) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS MarkSweep"), "CollectionCount");
					psMarkSweepcollectionTime = (Long) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS MarkSweep"), "CollectionTime");
					
					psScavengeCollectionCount = (Long) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS Scavenge"), "CollectionCount");
					psScavengeCollectionTime = (Long) serverConnection.getAttribute(new ObjectName(ManagementFactory.GARBAGE_COLLECTOR_MXBEAN_DOMAIN_TYPE + ",name=PS Scavenge"), "CollectionTime");
				
					CompositeDataSupport compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=PS Eden Space"), "Usage");				
					MemoryUsage memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);

					memoryThreadDataObj.setPsEdenused(String.valueOf(toMB(memoryPoolUsage.getUsed())) +"MB");
					memoryThreadDataObj.setPsEdenmax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					memoryThreadDataObj.setPsEdenusage(String.valueOf(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax())));
					memoryThreadDataObj.setPsEdenpeakused(String.valueOf(toMB(memoryPoolUsage.getUsed()))+"MB");
					memoryThreadDataObj.setPsEdenpeakmax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					
					compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=PS Survivor Space"), "Usage");				
					memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);
					
					memoryThreadDataObj.setPsSurvivorused(String.valueOf(toMB(memoryPoolUsage.getUsed())) +"MB");
					memoryThreadDataObj.setPsSurvivormax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					memoryThreadDataObj.setPsSurvivorusage(String.valueOf(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax())));
					memoryThreadDataObj.setPsSurvivorpeakused(String.valueOf(toMB(memoryPoolUsage.getUsed()))+"MB");
					memoryThreadDataObj.setPsSurvivorpeakmax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					
					compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=PS Old Gen"), "Usage");				
					memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);

					memoryThreadDataObj.setOldgenused(String.valueOf(toMB(memoryPoolUsage.getUsed())) +"MB");
					memoryThreadDataObj.setOldgenmax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					memoryThreadDataObj.setOldgenusage(String.valueOf(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax())));
					memoryThreadDataObj.setOldgenpeakused(String.valueOf(toMB(memoryPoolUsage.getUsed()))+"MB");
					memoryThreadDataObj.setOldgenpeakmax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					
					compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=PS Perm Gen"), "Usage");				
					memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);

					memoryThreadDataObj.setPermgenused(String.valueOf(toMB(memoryPoolUsage.getUsed())) +"MB");
					memoryThreadDataObj.setPermgenmax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					memoryThreadDataObj.setPermgenusage(String.valueOf(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax())));
					memoryThreadDataObj.setPermgenpeakused(String.valueOf(toMB(memoryPoolUsage.getUsed()))+"MB");
					memoryThreadDataObj.setPermgenpeakmax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					
					compositeDataSupportMemoryPool = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_POOL_MXBEAN_DOMAIN_TYPE + ",name=Code Cache"), "Usage");				
					memoryPoolUsage =  MemoryUsage.from(compositeDataSupportMemoryPool);

					memoryThreadDataObj.setCodecacheused(String.valueOf(toMB(memoryPoolUsage.getUsed())) +"MB");
					memoryThreadDataObj.setCodecachemax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					memoryThreadDataObj.setCodecacheusage(String.valueOf(((memoryPoolUsage.getUsed()*100)/memoryPoolUsage.getMax())));
					memoryThreadDataObj.setCodecachepeakused(String.valueOf(toMB(memoryPoolUsage.getUsed()))+"MB");
					memoryThreadDataObj.setCodecachepeakmax(String.valueOf(toMB(memoryPoolUsage.getMax()))+"MB");
					
					List<ThreadInformationData> threadInformationDataList=new ArrayList<ThreadInformationData>();
					
					long[] threadID = (long[]) serverConnection.getAttribute(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "AllThreadIds");
					
					String[] signature = new String [] {"long","int"};
					for (int i = 0; i < threadID.length; i++) {
						Object[] params = new Object [] {threadID[i],Integer.MAX_VALUE};
						CompositeDataSupport compositeDataSupportThreadInfo = (CompositeDataSupport)serverConnection.invoke(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "getThreadInfo",params, signature);
						ThreadInfo threadInfo = ThreadInfo.from(compositeDataSupportThreadInfo);
						
						
						ThreadInformationData threadInformationData= new ThreadInformationData();
						
						if(threadInfo != null){
							threadInformationData.setThreadName(( threadInfo.getThreadName() == null ) ? "" :threadInfo.getThreadName());
							threadInformationData.setThreadState(threadInfo.getThreadState().toString());
							threadInformationData.setBlockedCount(threadInfo.getBlockedCount());
							threadInformationData.setTotalBlockedTime(toString(threadInfo.getBlockedTime()));
							threadInformationData.setWaitedCount(threadInfo.getWaitedCount());
							threadInformationData.setTotalWaitedTime(toString(threadInfo.getWaitedTime()));
							StringBuilder sb = new StringBuilder();
							                   
							sb.append("\n"+threadInfo.getThreadName() + "[" + threadInfo.getThreadState() + "] Lock : " + threadInfo.getLockName() + " Owner : " + threadInfo.getLockOwnerName());
	                        for(StackTraceElement element :  threadInfo.getStackTrace()){
	                        	sb.append("\n" + element.getClassName() + "." + element.getMethodName() + " at Line : " + element.getLineNumber());                      
	                        }
	                        
	                        if(threadInfo.getLockedMonitors().length > 0){
	                        	sb.append("\n"+"Monitor Info : ");  
	                        	for(MonitorInfo moninfo :  threadInfo.getLockedMonitors()){
	                             	sb.append("\nClass Name" + moninfo.getClassName() +"IdentityHashCode : "+moninfo.getIdentityHashCode()+"LockedStackDepth : "+moninfo.getLockedStackDepth());                            
	                            }
	                        }
	                       
	                        if(threadInfo.getLockedSynchronizers().length > 0){
	                        	sb.append("\n"+"Lock Info : ");
	                            for(LockInfo lockinfo : threadInfo.getLockedSynchronizers()){
	                               	sb.append("\nClass Name" + lockinfo.getClassName() +"IdentityHashCode : "+lockinfo.getIdentityHashCode());                            
	                            }
	                        }
	                        
							threadInformationData.setStatckTraceInfo(sb.toString());
							threadInformationDataList.add(threadInformationData);
						}
					}
					memoryThreadDataObj.setThreadInfoDataList(threadInformationDataList);
				} catch (AttributeNotFoundException e) {
					e.printStackTrace();
				} catch (InstanceNotFoundException e) {
					e.printStackTrace();
				} catch (MalformedObjectNameException e) {
					e.printStackTrace();
				} catch (MBeanException e) {
					e.printStackTrace();
				} catch (ReflectionException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}finally{
					 remoteConnection.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
			
			//get System current Time in Millis
			long millis = System.currentTimeMillis();

			memoryThreadDataObj.setCurrentTime(millis);
			memoryThreadDataObj.setHeapMemoryPeakUsage(heapMemoryPeakUsage);
			memoryThreadDataObj.setHeapMemoryUsage(heapMemoryUsed);
			memoryThreadDataObj.setPeakThread(peakThreads);
			memoryThreadDataObj.setTotalThread(totalThreads);
			memoryThreadDataObj.setPsMarkSweepCount(psMarkSweepcollectionCount);
			memoryThreadDataObj.setPsMarkSweepTime(psMarkSweepcollectionTime);
			memoryThreadDataObj.setPsScavengeCount(psScavengeCollectionCount);
			memoryThreadDataObj.setPsScavengeTime(psScavengeCollectionTime);
			
			String json = new Gson().toJson(memoryThreadDataObj);
			response.getWriter().write(json);
		
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting memory thread information. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	

	public ActionForward getThreadData(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called getThreadData of "+ getClass().getName());
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
			
			String hostName = netServerInstanceData.getAdminHost();		
			int portNum = netServerInstanceData.getAdminPort();
			try {
				JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostName + ":" + portNum +  "/jmxrmi");
				JMXConnector remoteConnection = JMXConnectorFactory.connect(url);
				MBeanServerConnection serverConnection = remoteConnection.getMBeanServerConnection();
				try {
					List<ThreadInformationData> threadInformationDataList=new ArrayList<ThreadInformationData>();
					
					long[] threadID = (long[]) serverConnection.getAttribute(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "AllThreadIds");
					
					String[] signature = new String [] {"long","int"};
					for (int i = 0; i < threadID.length; i++) {
						Object[] params = new Object [] {threadID[i],Integer.MAX_VALUE};
						CompositeDataSupport compositeDataSupportThreadInfo = (CompositeDataSupport)serverConnection.invoke(new ObjectName(ManagementFactory.THREAD_MXBEAN_NAME), "getThreadInfo",params, signature);
						ThreadInfo threadInfo = ThreadInfo.from(compositeDataSupportThreadInfo);
						
						if(threadInfo != null){
							ThreadInformationData threadInformationData= new ThreadInformationData();
							threadInformationData.setThreadName((threadInfo.getThreadName() == null) ? "" :threadInfo.getThreadName());
							threadInformationData.setThreadState(threadInfo.getThreadState().toString());
							threadInformationData.setBlockedCount(threadInfo.getBlockedCount());
							threadInformationData.setTotalBlockedTime(toString(threadInfo.getBlockedTime()));
							threadInformationData.setWaitedCount(threadInfo.getWaitedCount());
							threadInformationData.setTotalWaitedTime(toString(threadInfo.getWaitedTime()));
							StringBuilder sb = new StringBuilder();
							                   
							sb.append("\n"+threadInfo.getThreadName() + "[" + threadInfo.getThreadState() + "] Lock : " + threadInfo.getLockName() + " Owner : " + threadInfo.getLockOwnerName());
	                        for(StackTraceElement element :  threadInfo.getStackTrace()){
	                        	sb.append("\n" + element.getClassName() + "." + element.getMethodName() + " at Line : " + element.getLineNumber());                      
	                        }
	                        
	                        if(threadInfo.getLockedMonitors().length > 0){
	                        	sb.append("\n"+"Monitor Info : ");  
	                        	for(MonitorInfo moninfo :  threadInfo.getLockedMonitors()){
	                             	sb.append("\nClass Name" + moninfo.getClassName() +"IdentityHashCode : "+moninfo.getIdentityHashCode()+"LockedStackDepth : "+moninfo.getLockedStackDepth());                            
	                            }
	                        }
	                       
	                        if(threadInfo.getLockedSynchronizers().length > 0){
	                        	sb.append("\n"+"Lock Info : ");
	                            for(LockInfo lockinfo : threadInfo.getLockedSynchronizers()){
	                               	sb.append("\nClass Name" + lockinfo.getClassName() +"IdentityHashCode : "+lockinfo.getIdentityHashCode());                            
	                            }
	                        }
	                        
							threadInformationData.setStatckTraceInfo(sb.toString());
							
							threadInformationDataList.add(threadInformationData);
						}
					}
					String json = new Gson().toJson(threadInformationDataList);
					response.getWriter().write(json);
				} catch (AttributeNotFoundException e) {
					e.printStackTrace();
				} catch (InstanceNotFoundException e) {
					e.printStackTrace();
				} catch (MalformedObjectNameException e) {
					e.printStackTrace();
				} catch (MBeanException e) {
					e.printStackTrace();
				} catch (ReflectionException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}finally{
					remoteConnection.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting thread data. Reason: "+ e.getMessage());
			Logger.logTrace(MODULE, e);
		} 
		return null;
	}
	
	
	public ActionForward getPieChartData(ActionMapping mapping,ActionForm form, HttpServletRequest request,HttpServletResponse response) throws Exception {
		Logger.logDebug(MODULE, "Called getPieChartData of "+ getClass().getName());
		try {
			response.setContentType("application/json");
			response.setCharacterEncoding("UTF-8");
			
			NetServerBLManager netServerBLManager = new NetServerBLManager();
			INetServerInstanceData netServerInstanceData = netServerBLManager.getNetServerInstance(netServerId);
			PieChartData pieChartData =new  PieChartData();
			
			String hostName = netServerInstanceData.getAdminHost();		
			int portNum = netServerInstanceData.getAdminPort();
			try {
				JMXServiceURL url = new JMXServiceURL("service:jmx:rmi:///jndi/rmi://" + hostName + ":" + portNum +  "/jmxrmi");
				JMXConnector remoteConnection = JMXConnectorFactory.connect(url);
				MBeanServerConnection serverConnection = remoteConnection.getMBeanServerConnection();
				try {
					
					CompositeDataSupport compositeDataSupport = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME), "HeapMemoryUsage");
					MemoryUsage heapmemoryUsage =  MemoryUsage.from(compositeDataSupport);
					
					//Get Heap Memory Info
					
					pieChartData.setFreeHeapMemory((toMB(heapmemoryUsage.getMax())) - (toMB(heapmemoryUsage.getUsed())));
					pieChartData.setHeapUsed(toMB(heapmemoryUsage.getUsed()));
					
					compositeDataSupport = (CompositeDataSupport)serverConnection.getAttribute(new ObjectName(ManagementFactory.MEMORY_MXBEAN_NAME), "NonHeapMemoryUsage");
					heapmemoryUsage =  MemoryUsage.from(compositeDataSupport);
					
					//Get Non Heap Memory Info
					pieChartData.setFreeNonHeapMemory((toMB(heapmemoryUsage.getMax())) - (toMB(heapmemoryUsage.getUsed())));
					pieChartData.setNonHeapused(toMB(heapmemoryUsage.getUsed()));
					
				} catch (AttributeNotFoundException e) {
					e.printStackTrace();
				} catch (InstanceNotFoundException e) {
					e.printStackTrace();
				} catch (MalformedObjectNameException e) {
					e.printStackTrace();
				} catch (MBeanException e) {
					e.printStackTrace();
				} catch (ReflectionException e) {
					e.printStackTrace();
				} catch (NullPointerException e) {
					e.printStackTrace();
				}finally{
					remoteConnection.close();
				}
			} catch (MalformedURLException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			}finally{
				
			}
			
			String json = new Gson().toJson(pieChartData);
			response.getWriter().write(json);
		
		} catch (Exception e) {
			Logger.logError(MODULE,"Error while getting pie chart data. Reason: "+ e.getMessage());
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
