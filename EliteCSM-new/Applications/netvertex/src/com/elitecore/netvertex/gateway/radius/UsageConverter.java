package com.elitecore.netvertex.gateway.radius;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.ILogger;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.util.GsonFactory;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.usagemetering.ServiceUnit;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

/**
 * @author harsh patel
 *
 */
public class UsageConverter {
	
	private static final String MODULE = "USG-CNVRTR";
	private static final int INITIAL_CAPACITY = 256;
	
	//FIXME, Use PartitioningCache here in TRUNK
	private ConcurrentHashMap<String, ServiceUnit> l1Cache;
	private ConcurrentHashMap<String, ServiceUnit> l2Cache;
	private RadiusGatewayControllerContext radiusGatewayControllerContext;
	
	public static final String IN_USAGE = "in";
	public static final String OUT_USAGE = "out";
	public static final String TIME_USAGE = "time";


	public UsageConverter(RadiusGatewayControllerContext radiusGatewayControllerContext) {
		this.radiusGatewayControllerContext = radiusGatewayControllerContext;
		int maxThread = radiusGatewayControllerContext.getServerContext().getServerConfiguration().getRadiusGatewayEventListenerConfiguration().getMaximumThread();
		
		int concurrencyLevel = maxThread < 2 ? maxThread : maxThread / 2 ;
		this.l1Cache = new ConcurrentHashMap<String, ServiceUnit>(INITIAL_CAPACITY, CommonConstants.DEFAULT_LOAD_FACTOR,concurrencyLevel);
		this.l2Cache = new ConcurrentHashMap<String, ServiceUnit>(INITIAL_CAPACITY, CommonConstants.DEFAULT_LOAD_FACTOR, concurrencyLevel);
		
		radiusGatewayControllerContext.getServerContext().getTaskScheduler().scheduleIntervalBasedTask(new CacheCleaner());
	}
	
	public UsageConverter(RadiusGatewayControllerContext radiusGatewayControllerContext,Map<String, ServiceUnit> previousUsage) {
		this(radiusGatewayControllerContext);
		l1Cache.putAll(previousUsage);
		
	}

	public void convert(PCRFRequest request) {
		try {

			String coreSessionId = request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
			if (coreSessionId == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Cannot set non-cumulative usage. Reason: core session id not found");
				return;
			}

			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Converting cumulative usage to non-cumulative usage");

			convertFromCumulativeMetering(request, coreSessionId);

			if (request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP) || request.getPCRFEvents().contains(PCRFEvent.SESSION_RESET)) {

				if (getLogger().isLogLevel(LogLevel.DEBUG))
					getLogger().debug(MODULE, "Removing cached cumulative usage monitoring information."
							+ " Reason: PCRFRequest is session stop request");
				ServiceUnit serviceUnitInCache = l1Cache.remove(coreSessionId);
				if (serviceUnitInCache == null) {
					l2Cache.remove(coreSessionId);
				}
			}

			setMSCCUnits(request);

		} catch (Exception e) {
			LogManager.getLogger().error(MODULE, "Error while processing cumulative usage. Reason: " + e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
	}

	

	/**
	 * This method forms cumulative metering usage into non-cumulative metering usage
	 * @param request
	 * @param coreSessionId
	 */
	private void convertFromCumulativeMetering(PCRFRequest request, String coreSessionId) {
		
		List<UsageMonitoringInfo> reportedMonitoringInfoList = request.getReportedUsageInfoList();
		ServiceUnit previousUsage = getPreviousUsage(request);
		
		if(reportedMonitoringInfoList == null || reportedMonitoringInfoList.isEmpty()) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
				LogManager.getLogger().debug(MODULE, "Reported usage monitoring information not found");

			if(previousUsage == null) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Creating zero usage. Reason: Previous usage and reported usage not found");
				}
				setCurrentUsageInSessionAttribute(request, new ServiceUnit());
			} else {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Skipping cumulative usage conversion");
				}
			}

			return;
		}
		
		UsageMonitoringInfo reportedUsage = reportedMonitoringInfoList.get(0);

		long sessionTimeSec = 0;
		if(request.getSessionStartTime() != null) {
			sessionTimeSec = (currentTimeInMillis() - request.getSessionStartTime().getTime()) / 1000;
		}
		
		if(reportedUsage.getUsedServiceUnit().getTime() == 0) {
			reportedUsage.getUsedServiceUnit().setTime(sessionTimeSec);
		}

		if(previousUsage != null) {
			updateServiceUnits(request, reportedUsage.getUsedServiceUnit(), previousUsage);
		}
		
		if(request.getPCRFEvents().contains(PCRFEvent.SESSION_STOP) == false && request.getPCRFEvents().contains(PCRFEvent.SESSION_RESET) == false){
			
			ServiceUnit serviceUnit;
			
			if(previousUsage != null){
				l2Cache.remove(coreSessionId);
				serviceUnit = previousUsage;
			} else {

				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Considering reported usage as currentUsage and removing reported usage "
							+ "monitoring information from request. Reason: previous usage not found");
				}

				request.setReportedUsageInfoList(null);
				serviceUnit = reportedUsage.getUsedServiceUnit();
			}

			setCurrentUsageInSessionAttribute(request, serviceUnit);
		}
		
		if (getLogger().isDebugLogLevel()) {
			if (request.getReportedUsageInfoList() != null) {
				getLogger().debug(MODULE, "Updated non-cumulative usage: " + printUpdatedUsage(reportedMonitoringInfoList));
			}
		}
	}

	private void setCurrentUsageInSessionAttribute(PCRFRequest request, ServiceUnit serviceUnit) {
		l1Cache.put(request.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val), serviceUnit);
		String usageInJson = createJsonForUsage(serviceUnit);

		request.setAttribute(PCRFKeyConstants.CS_SESSION_USAGE.val, usageInJson);
	}

	private String createJsonForUsage(ServiceUnit serviceUnit) {
		
		
		String doubleQuotes = com.elitecore.corenetvertex.constants.CommonConstants.DOUBLE_QUOTES;
		
		StringBuilder jsonString = new StringBuilder("{");
		jsonString.append(doubleQuotes).append("in").append(doubleQuotes).append(':').append(doubleQuotes).append(serviceUnit.getInputOctets()).append(doubleQuotes);
		jsonString.append(',').append(doubleQuotes).append("out").append(doubleQuotes).append(':').append(doubleQuotes).append(serviceUnit.getOutputOctets()).append(doubleQuotes);
		jsonString.append(',').append(doubleQuotes).append("time").append(doubleQuotes).append(':').append(doubleQuotes).append(serviceUnit.getTime()).append(doubleQuotes);
		jsonString.append('}');
		
		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "Total Usage: " + jsonString.toString());
		
		return jsonString.toString();
	
	}





	private String printUpdatedUsage(List<UsageMonitoringInfo> usageMonitoringInfoList) {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println();
		for(UsageMonitoringInfo monitoringInfo : usageMonitoringInfoList){
			out.println("        Monitoring Key = " + monitoringInfo.getMonitoringKey());
			out.println("        Monitoring Level = " + monitoringInfo.getUsageMonitoringLevel());
			if(monitoringInfo.getUsageMonitoringSupport() != null)
				out.println("        Monitoring Support = " + monitoringInfo.getUsageMonitoringSupport());
			if(monitoringInfo.getUsageMonitoringReport() != null)
				out.println("        Monitoring Report = " + monitoringInfo.getUsageMonitoringReport());
			if(monitoringInfo.getUsedServiceUnit() != null){
				out.println("        Used Service Units : ");
				out.println(monitoringInfo.getUsedServiceUnit());
			}
		}
		return stringBuffer.toString();
	}



	private void updateServiceUnits(PCRFRequest pcrfRequest, ServiceUnit reportedUnits, ServiceUnit previousUsageUnits) {

		long inputOctets = reportedUnits.getInputOctets();
		long outputOctets  = reportedUnits.getOutputOctets();
		long totalOctets = reportedUnits.getTotalOctets();
		long time = reportedUnits.getTime();

		boolean negativeDelta = false;
		// ---- Updating Reported Service Units to Non-Cumulative Usage.

		final long nonCumulativeInputOctets = inputOctets - previousUsageUnits.getInputOctets();
		final long nonCumulativeOutputOctets = outputOctets - previousUsageUnits.getOutputOctets();
		final long nonCumulativeTotalOctets = totalOctets - previousUsageUnits.getTotalOctets();
		long nonCumulativeTime = 0;
		//if time reported
		if(time > 0){
			nonCumulativeTime = time - previousUsageUnits.getTime();
		} else {
			reportedUnits.setTime(0);
		}

		if(nonCumulativeInputOctets < 0 || nonCumulativeOutputOctets < 0 || nonCumulativeTotalOctets < 0 || nonCumulativeTime < 0) {
			negativeDelta = true;
			reportedUnits.setInputOctets(0);
			reportedUnits.setOutputOctets(0);
			reportedUnits.setTotalOctets(0);
			reportedUnits.setTime(0);
			pcrfRequest.setReportedUsageInfoList(null);
		} else {
			reportedUnits.setInputOctets(nonCumulativeInputOctets);
			reportedUnits.setOutputOctets(nonCumulativeOutputOctets);
			reportedUnits.setTotalOctets(nonCumulativeTotalOctets);
			//if time reported
			if(time > 0) {
				reportedUnits.setTime(nonCumulativeTime);
			}
		}



		if(negativeDelta) {
			final ServiceUnit previousUsageFromCache = getPreviousUsageFromCache(pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val));

			String previousUsageFromCacheMessage;
			if(previousUsageFromCache != null) {
				previousUsageFromCacheMessage = "Previous cumulative usage from cache (input " + previousUsageUnits.getInputOctets()
						+", output:" + previousUsageUnits.getOutputOctets()
						+ ", total:"+ previousUsageUnits.getTotalOctets()
						+ ", time:" + previousUsageUnits.getTime() + ")";
			} else {
				previousUsageFromCacheMessage = "Previous cumulative usage from cache not found";
			}


			String previousUsageFromSessionMessage;

			String previousCumulativeUsageFromSession = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_USAGE.val);
			if(previousCumulativeUsageFromSession != null) {
				previousUsageFromSessionMessage = "Previous cumulative usage from session ("+  previousCumulativeUsageFromSession +")";
			} else {
				previousUsageFromSessionMessage = "Previous cumulative usage from session not found";
			}

			String subscriberAndSessionIdMessage = null;
			String subscriberIdentity = pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val);
			if(subscriberIdentity != null) {
				subscriberAndSessionIdMessage = "subscriber: " + pcrfRequest.getAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val) + ", for core-sessionID:" + pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
			} else {
				subscriberAndSessionIdMessage = "core-sessionID:" + pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
			}


			getLogger().error(MODULE, "USAGE_ALERT, Reported usage is less than previous usage for " + subscriberAndSessionIdMessage
					+ ". Reported usage(input:"+ inputOctets +", output:" + outputOctets + ", total:"+ totalOctets + ", time:" + time
					+ ")." + previousUsageFromCacheMessage + "." + previousUsageFromSessionMessage + ". ");
		}

		// ---- Updating Session Service Units with Reported Usage.
		previousUsageUnits.setInputOctets(inputOctets);
		previousUsageUnits.setOutputOctets(outputOctets);
		previousUsageUnits.setTotalOctets(totalOctets);
		if (time > 0 ){
			previousUsageUnits.setTime(time);
		}

		if(reportedUnits.getInputOctets() == 0 && reportedUnits.getOutputOctets() == 0 && reportedUnits.getTotalOctets() == 0 && reportedUnits.getTime() == 0) {
			pcrfRequest.setReportedUsageInfoList(null);
		}

	}
	
	private ILogger getLogger() {
		return LogManager.getLogger();
	}
	
	protected ServiceUnit getPreviousUsageFromCache(String coreSessionId){
			ServiceUnit monitoringInfos = l1Cache.get(coreSessionId);
			return monitoringInfos != null ? monitoringInfos : l2Cache.get(coreSessionId);
	}
	
	
	protected ServiceUnit getPreviousUsage(PCRFRequest pcrfRequest) {
		
		String coreSessionId = pcrfRequest.getAttribute(PCRFKeyConstants.CS_CORESESSION_ID.val);
		String currentUsageJsonInSession = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_USAGE.val);

		ServiceUnit previousInCache = getPreviousUsageFromCache(coreSessionId);
		
		if(previousInCache == null){
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Usage not found from cache");
		} else {
			if(getLogger().isLogLevel(LogLevel.DEBUG)){
				getLogger().debug(MODULE, "Usage:in="+previousInCache.getInputOctets()
												+",out="+previousInCache.getOutputOctets()
												+",time="+previousInCache.getTime()
												+" found from cache");
			}
				
		}
		
		if(Strings.isNullOrBlank(currentUsageJsonInSession)){
			if(getLogger().isLogLevel(LogLevel.DEBUG))
				getLogger().debug(MODULE, "Usage not found from session");
			return previousInCache;
		}

		if(getLogger().isLogLevel(LogLevel.DEBUG))
			getLogger().debug(MODULE, "Usage: "+ currentUsageJsonInSession + " found from session");
		 
		
		
		long inUsage = 0;
		long outUsage = 0;
		long usageTime = 0;
		
		try{
			JsonElement jsonElement = GsonFactory.defaultInstance().fromJson(currentUsageJsonInSession.trim(), JsonElement.class);
			JsonObject jsonObject = jsonElement.getAsJsonObject();
			for(Entry<String,JsonElement> entry: jsonObject.entrySet()){
				if(IN_USAGE.equalsIgnoreCase(entry.getKey())){
					inUsage = entry.getValue().getAsLong();
				} else if(OUT_USAGE.equalsIgnoreCase(entry.getKey())) {
					outUsage = entry.getValue().getAsLong();
				} else if(TIME_USAGE.equalsIgnoreCase(entry.getKey())) {
					usageTime = entry.getValue().getAsLong();
				}
			}
			
			
			
			// comparing total and whichever is high will be set
			long totalUsageInSession = inUsage + outUsage;
			
			if(totalUsageInSession > 0){
				if (previousInCache == null) {
					previousInCache = new ServiceUnit(0, inUsage, outUsage, totalUsageInSession);
				} else {
					
					if(getLogger().isLogLevel(LogLevel.DEBUG))
						getLogger().debug(MODULE, "Comparing total usage:"+ totalUsageInSession + " found from session with cached total usage:"
									+ previousInCache.getTotalOctets()+
								", which ever is higher is selected");
					if(totalUsageInSession > previousInCache.getTotalOctets()){
						previousInCache.setInputOctets(inUsage);
						previousInCache.setOutputOctets(outUsage);
						previousInCache.setTotalOctets(totalUsageInSession);
					}
				}
			}

			if (usageTime > 0) {
				if (previousInCache == null) {
					previousInCache = new ServiceUnit(usageTime, 0l, 0l, 0l);
				} else {
					if (getLogger().isLogLevel(LogLevel.DEBUG))
						getLogger().debug(MODULE, "Comparing time:" + usageTime + " found from session with cached time:"
								+ previousInCache.getTime() + ", which ever is higher is selected");
					if (usageTime > previousInCache.getTime()) {
						previousInCache.setTime(usageTime);
					}
				}

			}
			
			
			return previousInCache;
			
		}catch(Exception ex){
			getLogger().error(MODULE, "Using cached usage for core-session id: " + coreSessionId  + ". Reason: Error while parsing json string: "+currentUsageJsonInSession);
			getLogger().trace(MODULE, ex);
		}
		
		return previousInCache;
	}

	/*
		all calculation regarding cumulative to non cumulative is done in UsageMonitoringInfo
		So blindly setting usage units into MSCC
	 */
	public void setMSCCUnits(PCRFRequest request) {

		List<UsageMonitoringInfo> reportedUsageInfos= request.getReportedUsageInfoList();
		if (reportedUsageInfos == null) {
			request.setReportedMSCCs(null);
			return;
		}

		if (reportedUsageInfos.isEmpty()) {
			request.setReportedMSCCs(Collectionz.newArrayList());
			return;
		}

		UsageMonitoringInfo usageMonitoringInfo = reportedUsageInfos.get(0);
		List<MSCC> reportedMSCCs = request.getReportedMSCCs();
		MSCC mscc = reportedMSCCs.get(0);
		GyServiceUnits usedServiceUnits = mscc.getUsedServiceUnits();
		usedServiceUnits.setVolume(usageMonitoringInfo.getUsedServiceUnit().getTotalOctets());
		usedServiceUnits.setTime(usageMonitoringInfo.getUsedServiceUnit().getTime());
	}


	private class CacheCleaner extends BaseIntervalBasedTask {

		private long intervalInMinutes = 360; 	//	Interval in Minutes
		
		
		public CacheCleaner(){
			String interval = System.getProperty("um.cleanup.interval");
			
			if(interval != null){
				try{
					intervalInMinutes = Long.parseLong(interval.trim()) / 2;
					LogManager.getLogger().info(MODULE, "Set "+ intervalInMinutes + " Minutes for UM cleanup interval");
				}catch(Exception ex){
						LogManager.getLogger().warn(MODULE, "Invalid value: " + interval +" configured in \"um.cleanup.interval\", " +
								"Set Default value: " + intervalInMinutes  + " Minutes for UM cleanup interval");
						LogManager.ignoreTrace(ex);
				}
			} else{
				intervalInMinutes = 360;
			}
			
		}

		@Override
		public void execute(AsyncTaskContext context) {
			try {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(MODULE, "Clearing usage metering cache");
				
				
				int maxThread = radiusGatewayControllerContext.getServerContext().getServerConfiguration().getRadiusGatewayEventListenerConfiguration().getMaximumThread();
				
				int concurrencyLevel = maxThread < 2 ? maxThread : maxThread / 2 ;
				l2Cache = l1Cache;
				l1Cache = new ConcurrentHashMap<String, ServiceUnit>(INITIAL_CAPACITY, CommonConstants.DEFAULT_LOAD_FACTOR,concurrencyLevel);
				
				
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error while clearing cache. Reason: " + e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		
		@Override
		public long getInitialDelay() {
			return intervalInMinutes;
		}
		
		@Override
		public long getInterval() {
			return intervalInMinutes;
		}
		
		@Override
		public TimeUnit getTimeUnit() {
			return TimeUnit.MINUTES;
		}

	}
	
	protected long currentTimeInMillis(){
		return System.currentTimeMillis();
	}
	
	
	

}
