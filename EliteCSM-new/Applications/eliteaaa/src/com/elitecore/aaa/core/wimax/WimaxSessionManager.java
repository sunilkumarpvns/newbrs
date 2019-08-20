package com.elitecore.aaa.core.wimax;



import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.Predicate;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;

public class WimaxSessionManager implements Serializable {
	
	private static final long serialVersionUID = 1L;
	
	private static final String MODULE = "WIMAX-SESSION-MGR";
	public static final int WIMAX_CALLING_STATION_ID = 0;
	public static final int WIMAX_USERNAME = 1;
	public static final int WIMAX_AAA_SESSION_ID = 2;
	private static final int WIMAX_SESSION_CLEANUP_INTERVAL = 24*60*60*1000; // milliseconds corresponding to 24 hours	
	private static final String WIMAX_SESSION_FILE_NAME = "_sys.wimaxsession";
	private static final String SYSTEM_FOLDER = "system";
	
	private Map<String, WimaxSessionData> wimaxCallingStationIdSessionMap;		
	private Map<String, WimaxSessionData> wimaxUsernameSessionMap;
	private Map<String, WimaxSessionData> wimaxAAASessionIDSessionMap;
	
	private transient String serverHome;
	
	public WimaxSessionManager() {
		wimaxCallingStationIdSessionMap = new ConcurrentHashMap<String, WimaxSessionData>();
		wimaxUsernameSessionMap = new ConcurrentHashMap<String, WimaxSessionData>();        
		wimaxAAASessionIDSessionMap = new ConcurrentHashMap<String, WimaxSessionData>();    
	}
	
	public void init(ServerContext serverContext){
		this.serverHome = serverContext.getServerHome();
		WimaxSessionCleanupTask wimaxCleanupTask = new WimaxSessionCleanupTask(WIMAX_SESSION_CLEANUP_INTERVAL,WIMAX_SESSION_CLEANUP_INTERVAL);
		serverContext.getTaskScheduler().scheduleIntervalBasedTask(wimaxCleanupTask);
		
		File sessionDataFile = new File(getSessionDataFileLocation());
		
		if (sessionDataFile.exists()) {
			loadWimaxSessions();
		}
	}

	
	public Map<String, WimaxSessionData> getActiveWimaxSessions(){
		return wimaxAAASessionIDSessionMap;
	}
	
	public WimaxSessionData getWimaxSession(int key,String value) {
		
		WimaxSessionData wimaxSessionData = null;
		
		if(key == WIMAX_AAA_SESSION_ID){
			wimaxSessionData = wimaxAAASessionIDSessionMap.get(value);
		}else if(key == WIMAX_CALLING_STATION_ID){
			wimaxSessionData = wimaxCallingStationIdSessionMap.get(value);
		}else if(key == WIMAX_USERNAME){
			wimaxSessionData = wimaxUsernameSessionMap.get(value);
		}
		
		if (wimaxSessionData != null) {
			try {
				return (WimaxSessionData) wimaxSessionData.clone();
			} catch (CloneNotSupportedException ex) {
				throw new AssertionError("Wimax session data is cloneable but got clone not supported");
			}
		} else {
			return null;
		}
	}
	
	public WimaxSessionData createWimaxSession(int id,String value){
		WimaxSessionDataImpl wimaxSessionData = new WimaxSessionDataImpl();
		switch(id){
			case WIMAX_CALLING_STATION_ID :wimaxSessionData.setCallingStationId(value);
									   wimaxCallingStationIdSessionMap.put(value, wimaxSessionData);
									   break;
			case WIMAX_AAA_SESSION_ID :wimaxSessionData.setAaaSessionId(value);
								   wimaxAAASessionIDSessionMap.put(value, wimaxSessionData);
								   break;
			case WIMAX_USERNAME :wimaxSessionData.setUsername(value);
			   					 wimaxUsernameSessionMap.put(value, wimaxSessionData);
			   					break;					   	
			
		}
		return wimaxSessionData;
	}
	
	public WimaxSessionData updateWimaxSessionData(WimaxSessionData wimaxSessionData){
		
		if(wimaxSessionData.getUsername() != null){
			wimaxUsernameSessionMap.put(wimaxSessionData.getUsername(),wimaxSessionData);
		}
		if(wimaxSessionData.getAAASessionId()!= null){
			wimaxAAASessionIDSessionMap.put(wimaxSessionData.getAAASessionId(),wimaxSessionData);
		}
		if(wimaxSessionData.getCallingStationId() != null){
			wimaxCallingStationIdSessionMap.put(wimaxSessionData.getCallingStationId(), wimaxSessionData);
		}
		if (wimaxSessionData.getCUI() != null) {
			wimaxUsernameSessionMap.put(wimaxSessionData.getCUI(), wimaxSessionData);
		}
		return wimaxSessionData;
	}
	
	public void removeSession(@Nonnull WimaxSessionData wimaxSessionData) {
		
		if (LogManager.getLogger().isInfoLogLevel()) {
			LogManager.getLogger().info(MODULE, "Removing Wimax-Session: " + wimaxSessionData);
		}
		
		String strCallingStationId = wimaxSessionData.getCallingStationId();
		if(strCallingStationId != null){
			synchronized(wimaxCallingStationIdSessionMap){
				wimaxCallingStationIdSessionMap.remove(strCallingStationId);
			}
		}
		String strUsername = wimaxSessionData.getUsername();
		if(strUsername != null){
			synchronized(wimaxUsernameSessionMap){
				wimaxUsernameSessionMap.remove(strUsername);
			}
		}
		String strAAASessionId = wimaxSessionData.getAAASessionId();
		if(strAAASessionId != null){
			synchronized(wimaxAAASessionIDSessionMap){
				wimaxAAASessionIDSessionMap.remove(strAAASessionId);
			}
		}
		String cui = wimaxSessionData.getCUI();
		if (cui != null) {
			synchronized(wimaxUsernameSessionMap){
				wimaxUsernameSessionMap.remove(cui);
			}
		}
	}
	
	private WimaxSessionData removeSession(Map<String, WimaxSessionData> map, String key) {
		WimaxSessionData sessionData = map.remove(key);
		if (sessionData == null) {
			return null;
		}
		
		removeSession(sessionData);
		return sessionData;
	}
	
	/**
	 * Removes WiMAX session based on calling station ID and returns the session removed.
	 * Returns null if no session was mapped for calling station ID.
	 *   
	 * @param callingStationID a non-null string
	 */
	public @Nullable WimaxSessionData removeSessionByCallingStationID(String callingStationID) {
		return removeSession(wimaxCallingStationIdSessionMap, callingStationID);
	}
	
	/**
	 * Removes WiMAX session based on username and returns the session removed.
	 * Returns null if no session was mapped for username.
	 * 
	 * @param username a non-null string
	 */
	public @Nullable WimaxSessionData removeSessionByUsername(String username) {
		return removeSession(wimaxUsernameSessionMap, username);
	}
	
	/**
	 * Removes WiMAX session based on CUI and returns the session removed.
	 * Returns null if no session was mapped for CUI.
	 *   
	 * @param cui a non-null string
	 */
	public @Nullable WimaxSessionData removeSessionByCUI(String cui) {
		return removeSession(wimaxUsernameSessionMap, cui);
	}
	
	/**
	 * Removes WiMAX session based on AAA Session ID and returns the session removed.
	 * Returns null if no session was mapped for AAA Session ID.
	 *   
	 * @param aaaSessionID a non-null string
	 */
	public @Nullable WimaxSessionData removeSessionByAAASessionID(String aaaSessionID) {
		return removeSession(wimaxAAASessionIDSessionMap, aaaSessionID);
	}
	
	/**
	 * A bulk removal call that removes all sessions for which the predicate is satisfied.
	 * <br/>
	 * <b> NOTE: This is a heavy call as it iterates through all the sessions.</b>
	 * This method should be used sparsely. 
	 * 
	 * @param sessionPredicate
	 * @return number of sessions deleted. Returns 0 if no session is deleted.
	 */
	public int removeSessions(Predicate<WimaxSessionData> sessionPredicate) {
		int removeCount = 0;
		Iterator<WimaxSessionData> wimaxSessionIterator = wimaxAAASessionIDSessionMap.values().iterator();
		WimaxSessionData sessionData;
		
		while (wimaxSessionIterator.hasNext()) {
			sessionData = wimaxSessionIterator.next();
			if (sessionPredicate.apply(sessionData)) {
				removeSession(sessionData);
				removeCount++;
			}
		}
		return removeCount;
	}
	
	/**
	 * Returns a new predicate which does exact match of provided NAS IP Address with
	 * NAS IP in WiMAX session. Returns false if no NAS IP is present in WiMAX session.
	 */
	public static Predicate<WimaxSessionData> havingNasIPAddress(final String nasIPAddress) {
		return new Predicate<WimaxSessionData>() {

			@Override
			public boolean apply(WimaxSessionData session) {
				if (session.getNasIP() == null) {
					return false;
				}
				return session.getNasIP().equals(nasIPAddress);
			}
		};
	}
	
	/**
	 * Returns a new predicate which does exact match of provided NAS Identifer with
	 * NAS Identifier in WiMAX session. Returns false if no NAS Identifier is present in WiMAX session.
	 */
	public static Predicate<WimaxSessionData> havingNasIdentifier(final String nasIdentifier) {
		return new Predicate<WimaxSessionData>() {

			@Override
			public boolean apply(WimaxSessionData session) {
				if (session.getNasIdentifier() == null) {
					return false;
				}
				return session.getNasIdentifier().equals(nasIdentifier);
			}
		};
	}
	
	class WimaxSessionCleanupTask extends BaseIntervalBasedTask{
		private static final String MODULE = "WIMAX SESSION CLEANUP";
		private long initialDelay;
		private long intervalSeconds;
		public WimaxSessionCleanupTask(long initialDelay,long intervalSeconds){
			this.initialDelay= initialDelay;
			this.intervalSeconds = intervalSeconds;
		}
		
		@Override
		public long getInitialDelay() {
			return initialDelay;
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}

		@Override
		public long getInterval() {
			return intervalSeconds;
		}

		@Override
		public void execute(AsyncTaskContext context) {
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Executing WiMAX session cleanup task");
			}
			Set<String> callingStationIdSet = wimaxCallingStationIdSessionMap.keySet();
			long currentTime = System.currentTimeMillis();
			Iterator<String> itr = callingStationIdSet.iterator();
			
			while(itr.hasNext()){
				WimaxSessionData wimaxSessionData = wimaxCallingStationIdSessionMap.get(itr.next());
				long timeElapsed = currentTime - wimaxSessionData.getLastAccessTime();
				if(timeElapsed > wimaxSessionData.getSessionLifetime()){ 
					try{
						wimaxCallingStationIdSessionMap.remove(wimaxSessionData);
						wimaxAAASessionIDSessionMap.remove(wimaxSessionData.getAAASessionId());
						wimaxUsernameSessionMap.remove(wimaxSessionData.getUsername());
					}catch(Exception e){
						//print logger 
					}
				}
			}
			// TODO print logs regarding how many session removed and how many remaining.
			
		}
		
	}

	public void stop() {
		storeWimaxSessions();
	}
	
	private String getSessionDataFileLocation() {
		return this.serverHome + File.separator + SYSTEM_FOLDER + File.separator + WIMAX_SESSION_FILE_NAME;
	}
	
	private void storeWimaxSessions() {
			
		FileOutputStream fos = null;
		ObjectOutputStream oos = null;
		try {
			
			fos = new FileOutputStream(new File(getSessionDataFileLocation()));
			oos = new ObjectOutputStream(fos);
			oos.writeObject(this);
			
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "WiMAX sessions stored successfully in file: "
						+ WIMAX_SESSION_FILE_NAME + ", located at: " + getSessionDataFileLocation());
			}
		} catch (IOException e) {
			if (LogManager.getLogger().isErrorLogLevel()) {
				LogManager.getLogger().error(MODULE, "Failed to store WiMAX sessions in file, Reason: " + e.getMessage());
			}
			LogManager.getLogger().trace(e);
		} finally {
			Closeables.closeQuietly(oos);
			Closeables.closeQuietly(fos);
		}
	}
	
	private void loadWimaxSessions() {
		
		FileInputStream fis = null; 
		ObjectInputStream ois = null;
		try{
			fis = new FileInputStream(getSessionDataFileLocation());
			ois = new ObjectInputStream(fis);

			WimaxSessionManager tmpWimaxSessionManager = (WimaxSessionManager) ois.readObject();
			this.wimaxAAASessionIDSessionMap = tmpWimaxSessionManager.wimaxAAASessionIDSessionMap;
			this.wimaxCallingStationIdSessionMap = tmpWimaxSessionManager.wimaxCallingStationIdSessionMap;
			this.wimaxUsernameSessionMap = tmpWimaxSessionManager.wimaxUsernameSessionMap;
			
			LogManager.getLogger().info(MODULE, "Successfully loaded WiMAX sessions from file: " + WIMAX_SESSION_FILE_NAME);
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Error while loading WiMAX sessions from file: " 
					+ getSessionDataFileLocation() + ", Reason: " + e.getMessage() + ". No sessions will be added in memory.");
			LogManager.getLogger().trace(e);

		} catch (ClassNotFoundException e) {
			LogManager.getLogger().error(MODULE, "Error while loading WiMAX sessions from file: " 
					+ getSessionDataFileLocation() + ", Reason: " + e.getMessage() + ". No sessions will be added in memory.");
			LogManager.getLogger().trace(e);
		} finally {
			Closeables.closeQuietly(ois);
			Closeables.closeQuietly(fis);
		}
	}
}