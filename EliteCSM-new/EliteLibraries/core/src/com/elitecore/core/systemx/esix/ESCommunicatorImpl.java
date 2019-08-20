package com.elitecore.core.systemx.esix;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.locks.ReentrantLock;

import javax.annotation.Nullable;

import org.apache.commons.collections.buffer.CircularFifoBuffer;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.alert.AlertListener;
import com.elitecore.core.commons.alert.Events;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.base.BaseIntervalBasedTask;
import com.elitecore.core.systemx.esix.statistics.ESIStatistics;
import com.elitecore.core.util.cli.TableFormatter;

public abstract class ESCommunicatorImpl implements ESCommunicator{
	public static final String MODULE = "ES-COMM";
	private final List<ESIEventListener<ESCommunicator>> eventListnerList;
	private boolean alive = true;
	private StatusScannerTask statusScanner;
	@Nullable private final TaskScheduler taskScheduler;
	private boolean isInitialized = false;

	//ESI Statistics
	private ESIStatisticsImpl esiStatistics;

	private List<AlertListener> alertListeners;

	private final int[] alignment  = new int[]{TableFormatter.LEFT,TableFormatter.LEFT};
	@Nullable private Future<?> statusScannerFuture;

	public ESCommunicatorImpl(@Nullable TaskScheduler scheduler) {
		eventListnerList = new CopyOnWriteArrayList<ESIEventListener<ESCommunicator>>();
		taskScheduler = scheduler;
		esiStatistics = createESIStatistics();
		alertListeners = new ArrayList<AlertListener>();
	}

	@Override
	public void init() throws InitializationFailedException {
		if (isInitialized) {
			return;
		}
		scheduleStatusScannerTask();
		initESIStatistics();
		isInitialized = true;
	}

	@Override
	public void addESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
		eventListnerList.add(eventListener);
		if (isAlive() == false) {
			eventListener.dead(this);
		}
	}

	@Override
	public boolean isAlive() {
		return alive;
	}

	@Override
	public final ESIStatistics getStatistics() {
		return esiStatistics;
	}

	public synchronized void markDead(){
		if(isAlive() == false){
			if(getLogger().isInfoLogLevel()){
				getLogger().info(MODULE, getName() + " is already dead");
			}
			return;
		}

		long currentTimeInMillies = System.currentTimeMillis();
		if(currentTimeInMillies - esiStatistics.getLastMarkDeadTimestamp() <= 10000){
			if(getLogger().isWarnLogLevel()){
				getLogger().warn(MODULE, "Skipping markDead operation. Reason:"
						+getName() + " markDead is called more than one time in less than 10sec, last markDead Time "
						+ new Date(esiStatistics.getLastMarkDeadTimestamp()).toString());
			}
			return;
		}


		updateLastMarkDeadTimestamp();

		boolean isAliveForFallback = checkForFallback();
		if(isAliveForFallback){
			if(getLogger().isWarnLogLevel()){
				getLogger().warn(MODULE, "Check for fallback is true. Marking "+getName()+ " as Alive");
			}
			markAlive();
			return;
		}


		incrementTotalDeadCount();
		updateLastDeadTimestamp();

		alive = false;
		generateDownAlert();
		for(ESIEventListener<ESCommunicator> eventListner : eventListnerList) {
			eventListner.dead(this);
		}
		if(getLogger().isErrorLogLevel()){
			getLogger().error(MODULE, "Marking "+getName()+ " as Dead");
		}
	}

	public synchronized void markAlive(){
		if (isAlive()) {
			if(getLogger().isInfoLogLevel()){
				getLogger().info(MODULE, getName() + " is already alive");
			}
			return;
		}

		alive = true;
		generateUpAlert();

		for (ESIEventListener<ESCommunicator> eventListner : eventListnerList) {
			eventListner.alive(this);
		}

		if (getLogger().isWarnLogLevel()) {
			getLogger().warn(MODULE, "Marked " + getName() + " as Alive");
		}
	}

	@Override
	public void removeESIEventListener(ESIEventListener<ESCommunicator> eventListener) {
		eventListnerList.remove(eventListener);
	}

	private class StatusScannerTask extends BaseIntervalBasedTask{

		@Override
		public void execute(AsyncTaskContext context) {
			if(ESCommunicatorImpl.this.isAlive() == false){
				if(getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Status Scanner Thread started for ESI");
				}
				if(getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Scanning for aliveness");
				}


				scan();
			}
			updateLastStatusScanTimestamp();
		}

		@Override
		public long getInterval() {
			return getStatusCheckDuration();
		}

		@Override
		public long getInitialDelay() {
			return 60;
		}

		@Override
		public boolean isFixedDelay() {
			return true;
		}
	}

	private void scheduleStatusScannerTask(){
		if(getStatusCheckDuration() <= NO_SCANNER_THREAD || taskScheduler == null)
			return;
		statusScanner = new StatusScannerTask();
		statusScannerFuture = taskScheduler.scheduleIntervalBasedTask(statusScanner);
	}

	protected TaskScheduler getTaskScheduler() {
		return taskScheduler;
	}

	private void initESIStatistics(){
		esiStatistics.init();
	}

	protected abstract int getStatusCheckDuration();

	public void stop(){

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Stopping "+getName()+" communicator");
		}
		if(statusScannerFuture != null) {
			statusScannerFuture.cancel(false);
		}
		esiStatistics.stop();
	}

	/* ------- Start of protected/private methods for updating the statistics -------- */
	protected void updateAverageResponseTime(long value){
		esiStatistics.updateAverageResponseTime(value);
	}

	protected void incrementTotalRequests(){
		esiStatistics.incrementTotalRequests();
	}

	protected void incrementTotalSuccess(){
		esiStatistics.incrementTotalSuccess();
	}

	protected void incrementTotalErrorResponses(){
		esiStatistics.incrementTotalErrorResponses();
	}

	protected void incrementTotalTimedoutResponses(){
		esiStatistics.incrementTotalTimedoutResponses();
	}

	private void updateLastDeadTimestamp(){
		esiStatistics.updateLastDeadTimestamp();
	}

	private void updateLastMarkDeadTimestamp(){
		esiStatistics.updateLastMarkDeadTimestamp();
	}

	private void incrementTotalDeadCount(){
		esiStatistics.incrementTotalDeadCount();
	}

	private void updateLastStatusScanTimestamp(){
		esiStatistics.updateLastStatusScanTimestamp();
	}
	/* ------- End of protected/private methods for updating the statistics -------- */


	protected ESIStatisticsImpl createESIStatistics(){
		return new ESIStatisticsImpl();
	}

	/* ------------------- BASIC ESI STATISTICS CONTAINER ---------------------- */
	/**
	 * Any subclass can extend basic counters and give its own specific counters
	 * @see ESCommunicatorImpl#createESIStatistics()
	 *
	 */
	protected class ESIStatisticsImpl implements ESIStatistics{

		protected SimpleDateFormat dateFormat = new SimpleDateFormat("E M dd HH:mm:ss yyyy");

		/*these both entities are not atomic long as they are not incremented and just
		 * assigned new values every time. So just using volatile for visibility
		 */
		private volatile long lastStatusScanTimestamp = 0L;
		private volatile long lastDeadTimestamp = 0L;
		private volatile long lastMarkDeadDeadTimestamp = 0L;

		private AtomicLong deadCount = new AtomicLong(0);
		private AtomicLong totalRequests = new AtomicLong(0);
		private AtomicLong totalSuccessResponse = new AtomicLong(0);
		private AtomicLong totalErrorResponse = new AtomicLong(0);
		private AtomicLong totalTimeOuts = new AtomicLong(0);

		/* ----------- COUNTERS FOR AVERAGE RESPONSE TIME -------- */

		//this lock protects the average response time calculation entities.
		//Note: Before accessing any of average time values acquire this lock
		private ReentrantLock averageLock = new ReentrantLock();

		//this represents the actual response time fresh of last minute
		private float lastMinAverageResponseTime = 0.0F;

		private float lastTenMinAverageResponseTime = 0.0F;

		private float lastHourAverageResponseTime = 0.0F;

		//this represents calculation going on now
		private float newAverageResponseTime = 0;

		//this represents the requests served till last refresh of counters
		private long lastMinRequestsReceivedCount = 0;
		private Future<?> averageResTimeCalcTaskFuture;


		private void init(){

			if( taskScheduler == null){
				if (getLogger().isWarnLogLevel()) {
					getLogger().warn(MODULE, "Skipping initializing statistics calculation of "+getName()+". Reason: Task scheduler is null");
				}
				return;
			}
			AverageResponseTimeCalculator averageResTimeCalcTask = new AverageResponseTimeCalculator();
			averageResTimeCalcTaskFuture = taskScheduler.scheduleIntervalBasedTask(averageResTimeCalcTask);
		}

		/* ------- Public methods for accessing the statistics -------- */
		@Override
		public String currentStatus(){
			return (isAlive() ? ALIVE : DEAD);
		}

		@Override
		public float getLastMinAvgResponseTime(){
			return lastMinAverageResponseTime;
		}

		@Override
		public long getLastScanTimestamp(){
			return lastStatusScanTimestamp;
		}

		@Override
		public long getLastDeadTimestamp(){
			return lastDeadTimestamp;
		}

		@Override
		public long getLastMarkDeadTimestamp(){
			return lastMarkDeadDeadTimestamp;
		}

		public void stop() {
			if (getLogger().isWarnLogLevel()) {
				getLogger().warn(MODULE, "Stopping statistics calculation of "+getName());
			}
			if(averageResTimeCalcTaskFuture != null ) {
				averageResTimeCalcTaskFuture.cancel(false);
			}
		}

		@Override
		public long getTotalTimedouts(){
			return totalTimeOuts.get();
		}

		@Override
		public long getTotalRequests(){
			return totalRequests.get();
		}

		@Override
		public long getTotalSuccesses(){
			return totalSuccessResponse.get();
		}

		@Override
		public long getTotalErrors(){
			return totalErrorResponse.get();
		}

		@Override
		public String getName() {
			return ESCommunicatorImpl.this.getName();
		}

		@Override
		public String getTypeName() {
			return ESCommunicatorImpl.this.getTypeName();
		}

		@Override
		public long getDeadCount() {
			return deadCount.get();
		}

		@Override
		public float getLastHourAvgResponseTime() {
			return lastHourAverageResponseTime;
		}

		@Override
		public float getLastTenMinAvgResponseTime() {
			return lastTenMinAverageResponseTime;
		}
		/* ------- End of public methods for accessing the statistics -------- */


		/* ------- Start of protected/private methods for updating the statistics -------- */
		protected void updateAverageResponseTime(double value){
			averageLock.lock();
			newAverageResponseTime += value;
			lastMinRequestsReceivedCount++;
			averageLock.unlock();
		}

		protected void incrementTotalRequests(){
			totalRequests.incrementAndGet();
		}

		protected void incrementTotalSuccess(){
			totalSuccessResponse.incrementAndGet();
		}

		protected void incrementTotalErrorResponses(){
			this.totalErrorResponse.incrementAndGet();
		}

		protected void incrementTotalTimedoutResponses(){
			this.totalTimeOuts.incrementAndGet();
		}

		private void updateLastDeadTimestamp(){
			lastDeadTimestamp = System.currentTimeMillis();
		}

		private void updateLastMarkDeadTimestamp(){
			lastMarkDeadDeadTimestamp = System.currentTimeMillis();
		}

		private void updateLastStatusScanTimestamp(){
			lastStatusScanTimestamp = System.currentTimeMillis();
		}

		private void incrementTotalDeadCount(){
			deadCount.incrementAndGet();
		}
		/* ------- End of protected/private methods for updating the statistics -------- */

		@Override
		public String toString(){

			int[] width= {35,30};

			String[] header={};

			TableFormatter esiStatsTableFormatter = new TableFormatter(header, width,TableFormatter.ONLY_HEADER_LINE);
			esiStatsTableFormatter.addRecord(new String[]{"ESI Name",":"+getName()},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"ESI Type",":"+getTypeName()},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"ESI Status",":"+currentStatus()},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"ESI Name",":"+getName()},alignment);

			esiStatsTableFormatter.addRecord(new String[]{"Total Requests", ":"+String.valueOf(totalRequests)},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"Total Error Response", ":"+String.valueOf(totalErrorResponse)},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"Total Success Response", ":"+String.valueOf(totalSuccessResponse)},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"Total Timed out Response", ":"+String.valueOf(totalTimeOuts)},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"Total Dead Count", ":"+String.valueOf(deadCount)},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"Last Scan Timestamp", ":"+dateFormat.format(new Date(lastStatusScanTimestamp))},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"Last Dead Timestamp", ":"+dateFormat.format(new Date(lastDeadTimestamp))},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"Last Minute Avg. Response Time",":"+String.valueOf(lastMinAverageResponseTime)},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"Last 10 Minute Avg. Response Time", ":"+String.valueOf(lastTenMinAverageResponseTime)},alignment);
			esiStatsTableFormatter.addRecord(new String[]{"Last Hour Avg. Response Time", ":"+String.valueOf(lastHourAverageResponseTime)},alignment);

			return esiStatsTableFormatter.getFormattedValues();
		}

		private class AverageResponseTimeCalculator extends BaseIntervalBasedTask{

			/* Stores average response time of last ten minutes */
			private CircularFifoBuffer tenMinsAvgBuffer = new CircularFifoBuffer(10);

			/* Stores average response time of last hour */
			private CircularFifoBuffer hourAvgBuffer = new CircularFifoBuffer(60);

			@Override
			public long getInterval() {
				return 60;
			}

			@Override
			public long getInitialDelay() {
				return 60;
			}

			@Override
			public boolean isFixedDelay() {
				return true;
			}

			@Override
			public TimeUnit getTimeUnit() {
				return TimeUnit.SECONDS;
			}

			@Override
			public void execute(AsyncTaskContext context) {
				averageLock.lock();

				refreshPerMinuteAvgResponseStatitics();

				tenMinsAvgBuffer.add(lastMinAverageResponseTime);
				hourAvgBuffer.add(lastMinAverageResponseTime);

				refreshTenMinsAvgResponseStatistics();

				refreshHourAvgResponseStatistics();

				averageLock.unlock();
			}

			private void refreshPerMinuteAvgResponseStatitics(){
				//this is necessary as 0.0/0.0 becomes NaN in float
				if(newAverageResponseTime > 0 && lastMinRequestsReceivedCount > 0)
					lastMinAverageResponseTime = newAverageResponseTime / lastMinRequestsReceivedCount;
				else
					lastMinAverageResponseTime = 0.0F;

				newAverageResponseTime = 0;
				lastMinRequestsReceivedCount = 0;
			}

			private void refreshTenMinsAvgResponseStatistics(){
				float totalAverageRespTime = 0.0F;
				for(Object f : tenMinsAvgBuffer){
					totalAverageRespTime += (Float)f;
				}

				lastTenMinAverageResponseTime = totalAverageRespTime/tenMinsAvgBuffer.size();
			}

			private void refreshHourAvgResponseStatistics(){
				float totalAverageRespTime = 0.0F;
				for(Object f : hourAvgBuffer){
					totalAverageRespTime += (Float)f;
				}

				lastHourAverageResponseTime = totalAverageRespTime/hourAvgBuffer.size();
			}
		}
	}

	@Override
	public void reInit()throws InitializationFailedException {

	}

	protected boolean checkForFallback() {
		return false;
	}

	public void generateUpAlert() {

	}

	public void generateDownAlert() {

	}

	@Override
	public void registerAlertListener(AlertListener alertListener){
		alertListeners.add(alertListener);
	}

	protected void notifyAlertListeners(Events event, String message){
		for (AlertListener alertListener : alertListeners) {
			alertListener.generateAlert(event, message);
		}
	}

}