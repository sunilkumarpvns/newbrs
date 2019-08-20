package com.elitecore.nvsmx.system.jmx;

import static com.elitecore.commons.logging.LogManager.getLogger;
import com.elitecore.commons.base.Stopwatch;
import com.elitecore.commons.base.Strings;
import com.elitecore.commons.io.Closeables;
import com.elitecore.commons.threads.EliteThreadFactory;
import com.elitecore.core.commons.utilx.mbean.BaseMBeanController;
import com.elitecore.core.commons.utilx.mbean.MBeanConstants;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.core.jmx.SubscriberImportStatistics;
import com.elitecore.corenetvertex.core.jmx.SubscriberImportStatisticsImpl;
import com.elitecore.corenetvertex.core.mbean.SubscriberImportControllerMXBean;
import com.elitecore.corenetvertex.spr.data.SubscriberInfo;
import com.elitecore.corenetvertex.spr.data.SubscriptionDetail;
import com.elitecore.corenetvertex.subscriberimport.InputType;
import com.elitecore.corenetvertex.subscriberimport.SubscriberImportParameters;
import com.elitecore.corenetvertex.util.LeakyBucket;
import com.elitecore.nvsmx.policydesigner.model.subscriber.SubscriberDAO;
import com.elitecore.nvsmx.system.util.migrate.NV648SubscriberInfo;
import com.elitecore.nvsmx.system.util.migrate.PackageNameMapping;
import com.elitecore.nvsmx.system.util.migrate.SPRColumnConfigurationProvider;
import com.elitecore.nvsmx.system.util.migrate.SubscriberCSVDataParser;
import com.elitecore.nvsmx.system.util.migrate.SubscriberDataParser;
import com.elitecore.nvsmx.system.util.migrate.SubscriberImportResult;
import com.elitecore.nvsmx.system.util.migrate.SubscriberJSONDataParser;
import com.elitecore.nvsmx.system.util.migrate.SubscriberTransformer;
import com.elitecore.nvsmx.system.util.migrate.SubscriptionCSVParser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.CompletionService;
import java.util.concurrent.ExecutorCompletionService;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class SubscriberImportController extends BaseMBeanController implements SubscriberImportControllerMXBean {

	private static final String MODULE = "SUBSCRIBER-IMPORT-MBEAN";
	private static final String CDR_EXTENTION = ".cdr";
	private static final String CDR_FILE_PREFIX = "migrationCDR-";
	private static final String LOGS_PATH = "logs";
	private static final String SYSTEM_PATH = "system";
	private static final String THREAD_NAME_IMPORT_TASK_SUBMITTOR = "IMPORT-TASK-SUBMITTOR";
	private static final String DEFAUL_PACKAGE_NAME_MAPPING_FILE = "package-name-mapping.xml";
	private static final String CDR = "cdr";
	
	private ExecutorService importTaskSubmittorExecutorService;
	private String serverHome;
	private Future<?> future;
	private volatile boolean isMigrationInProgress;
	private LeakyBucket<SubscriberImportStatistics> statisticsHistory;
	private SubscriberImportStatistics runningTaskStatistcs;

	public SubscriberImportController(String homePath) {
		this.serverHome = homePath;
		this.importTaskSubmittorExecutorService = new ThreadPoolExecutor(1, 1,
                1L, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>(),
                new EliteThreadFactory(THREAD_NAME_IMPORT_TASK_SUBMITTOR, THREAD_NAME_IMPORT_TASK_SUBMITTOR, 7));
		this.isMigrationInProgress = false;
		this.statisticsHistory = new LeakyBucket<SubscriberImportStatistics>(5);
		Runtime.getRuntime().addShutdownHook(new ShutdownHook(importTaskSubmittorExecutorService));
	}

	@Override
	public synchronized String start(SubscriberImportParameters subscriberImportParameters) {
		
		InputType inputType = subscriberImportParameters.getInputType();
		String inputFilePath = subscriberImportParameters.getInputFilePath();
		String packageNameMappingFileName = subscriberImportParameters.getPackageMappingFilePath();

		if(isMigrationInProgress == false) {
			
			if(future != null && future.isCancelled() == false) {
				future.cancel(true);
			}
			
			File jsonFile = new File(inputFilePath);
			
			if (jsonFile.exists() == false) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Input file not exist for file: " + inputFilePath);
				}
				return ResonseMessageEnum.FAIL.messsage + ". Input file not exist: " + inputFilePath;
			}
			
			if (jsonFile.canRead() == false) {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Read access denied for file: " + inputFilePath);
				}
				return ResonseMessageEnum.FAIL.messsage + ". Read access denied for input File: " + inputFilePath;
			}
			
			if (getLogger().isDebugLogLevel()) {
				getLogger().debug(MODULE, "Input file found: " + inputFilePath);
			}
			
			PackageNameMapping packageNameMapping;
			if (Strings.isNullOrBlank(packageNameMappingFileName)) {
				packageNameMapping = new PackageNameMapping(serverHome + SYSTEM_PATH + File.separator +  DEFAUL_PACKAGE_NAME_MAPPING_FILE);
			} else {
				packageNameMapping = new PackageNameMapping(packageNameMappingFileName);
			}
			
			packageNameMapping.init();
			
			SPRColumnConfigurationProvider  columnConfigurationProvider = new SPRColumnConfigurationProvider(serverHome);
			columnConfigurationProvider.init();
			
			SubscriberTransformer subscriberTransformer = new SubscriberTransformer(packageNameMapping, columnConfigurationProvider);
			
			SubscriberDataParser subscriberDataParser;
			
			switch (inputType) {
				case CSV_SUBSCRIBER:
					subscriberDataParser = new SubscriberCSVDataParser(columnConfigurationProvider.getColumnHeaders());
					break;
				case CSV_SUBSCRIPTION:
					subscriberDataParser = new SubscriptionCSVParser(columnConfigurationProvider.getSubscriptionCSVHeaders(), columnConfigurationProvider.getDateFormat());
					break;
				case JSON:
					subscriberDataParser = new SubscriberJSONDataParser(columnConfigurationProvider.getDateFormat());
					break;
				default:
					return ResonseMessageEnum.FAIL.messsage + ". Reason: Invalid input type: " + inputType;
			}
			
			final String cdrFilePath = new StringBuilder(serverHome).append(File.separator)
										.append(LOGS_PATH).append(File.separator)
										.append(CDR).append(File.separator)
										.append(CDR_FILE_PREFIX).append(jsonFile.getName())
										.append(CommonConstants.DASH).append(new Date().toString()).append(CDR_EXTENTION).toString();
			File cdrFile = new File(cdrFilePath);
			cdrFile.getParentFile().mkdirs();
			try {
				cdrFile.createNewFile();
			} catch (IOException e) {
				return ResonseMessageEnum.FAIL.messsage + ". CDR file creation failed at location: " + cdrFilePath;
			}
			
			//TODO CHETAN err file creation, will do after CLI commit
			isMigrationInProgress = true;
			SubscriberImportStatisticsImpl subscriberImportStatistics = new SubscriberImportStatisticsImpl();
			this.statisticsHistory.add(subscriberImportStatistics);
			this.runningTaskStatistcs = subscriberImportStatistics;
			
			
			future = importTaskSubmittorExecutorService.submit(new ImportTaskSubmitor(jsonFile, cdrFile, subscriberTransformer, 
					subscriberImportStatistics, subscriberDataParser));
			return ResonseMessageEnum.MIGRATION_IS_STARTED.messsage + ". Migration CDR is being generated at location: " + cdrFilePath;
			
		} else {
			return ResonseMessageEnum.MIGRATION_IS_IN_PROGRESS.messsage + "\n" + getFormattedStatistics(runningTaskStatistcs);
		}
	}
	
	private String getFormattedStatistics(SubscriberImportStatistics statistics) {
		if (statistics == null) {
			return "";
		}

		return statistics.toString();
	}
	
	public Iterator<SubscriberImportStatistics> status() {
		return statisticsHistory.iterator();
	}

	@Override
	public String stop() {

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Cancel operation started for subscriber import process");
		}

		String responseMessage;

		if (future != null) {
			future.cancel(true);
			//TODO should wait till task complete
			future = null;
			responseMessage = ResonseMessageEnum.SUCCESS.messsage;
		} else {
			responseMessage = ResonseMessageEnum.NO_TASK_RUNNING.messsage;
		}

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Response message: " + responseMessage);
		}

		isMigrationInProgress = false;

		if (getLogger().isDebugLogLevel()) {
			getLogger().debug(MODULE, "Cancel operation completed for subscriber import process");
		}

		return responseMessage;
	}

	@Override
	public String getName() {
		return MBeanConstants.ELITEAAA_MBEAN + com.elitecore.corenetvertex.core.mbean.MBeanConstants.IMPORT_648_SUBSCRIBER;
	}

	private class ImportTaskSubmitor implements Runnable {

		private static final String THREAD_NAME_SUBSCRIBER_IMPORT_TASK = "SUBSCRIBER-IMPORT-TASK";
		private static final String THREAD_NAME_CDR_GEN_TASK = "CDR-GEN-TASK";
		private File inputFile;
		private File cdrFile;
		private SubscriberTransformer subscriberTransformer;
		private CompletionService<SubscriberImportResult> subscriberImportTaskCompletionService;
		private ExecutorService cdrGeneratingTaskExecutorService;
		private SubscriberImportStatisticsImpl subscriberImportStatistics;
		private ThreadPoolExecutor workerThreadPool;
		private SubscriberDataParser subscriberDataParser;
		
		public ImportTaskSubmitor(File inputFile, File cdrFile,
				SubscriberTransformer subscriberTransformer, 
				SubscriberImportStatisticsImpl subscriberImportStatistics,
				SubscriberDataParser subscriberDataParser) {
			this.inputFile = inputFile;
			this.cdrFile = cdrFile;
			this.subscriberTransformer = subscriberTransformer;
			this.subscriberImportStatistics = subscriberImportStatistics;
			this.subscriberDataParser = subscriberDataParser;
			this.cdrGeneratingTaskExecutorService = new ThreadPoolExecutor(1, 1,
                                                    1, TimeUnit.SECONDS,
                                                    new LinkedBlockingQueue<Runnable>(),
                                                    new EliteThreadFactory(THREAD_NAME_CDR_GEN_TASK, THREAD_NAME_CDR_GEN_TASK, 7));
			this.workerThreadPool = new ThreadPoolExecutor(10, 10,
                                                    1, TimeUnit.SECONDS,
                                                    new LinkedBlockingQueue<Runnable>(),
                                                    new EliteThreadFactory(THREAD_NAME_SUBSCRIBER_IMPORT_TASK, THREAD_NAME_SUBSCRIBER_IMPORT_TASK, 7));
			this.subscriberImportTaskCompletionService = new ExecutorCompletionService<SubscriberImportResult>(workerThreadPool);
			Runtime.getRuntime().addShutdownHook(new ShutdownHook(cdrGeneratingTaskExecutorService));
			Runtime.getRuntime().addShutdownHook(new ShutdownHook(workerThreadPool));
		}

		@Override
		public void run() {

			Stopwatch stopwatch = new Stopwatch();
			stopwatch.start();
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Subscriber import started.");
			}
			
			BufferedReader brForInputFile = null;
			PrintWriter cdrPrintWriter = null;
			try {
				FileReader fr = new FileReader(inputFile);
				brForInputFile = new BufferedReader(fr);
				FileWriter fw = new FileWriter(cdrFile);
				cdrPrintWriter = new PrintWriter(fw);

				
				int index = 0;
				while (true) {
					
					if (Thread.currentThread().isInterrupted()) {
						break;
					}
					
					String subscriberDataString = brForInputFile.readLine();
					
					if (subscriberDataString == null) {
						getLogger().info(MODULE, "subscriber dump file is completed");
						break;
					}

					if(subscriberDataString.trim().isEmpty()) {
						continue;
					}
					subscriberImportTaskCompletionService.submit(new SubscriberImportTask(index + 1, subscriberDataString, subscriberTransformer, subscriberDataParser));
					subscriberImportStatistics.incrementSubmittedTaskCount();
					++index;
				}				

				Future<Boolean> cdrGenretionTaskFuture = cdrGeneratingTaskExecutorService.submit(new CDRGeneratingTask(cdrPrintWriter,subscriberImportTaskCompletionService, index, subscriberImportStatistics));
							
				/*
				 *  THIS CALL IS ADDED TO WAIT TILL ALL CDR GENERATED. 
				 */
				cdrGenretionTaskFuture.get();
				
			} catch (Exception e) {
				getLogger().error(MODULE, "Error while importing subscribers. Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				
				// write message in ERR file. print imported subscriber count
			} finally {
				Closeables.closeQuietly(brForInputFile);
				Closeables.closeQuietly(cdrPrintWriter);
				isMigrationInProgress = false;
				this.workerThreadPool.shutdown();
				this.cdrGeneratingTaskExecutorService.shutdown();
			}
			
			stopwatch.stop();
			if (getLogger().isInfoLogLevel()) {
				getLogger().info(MODULE, "Subscriber import completed.");
				getLogger().info(MODULE, "Time spent on subscriber import is " + stopwatch.elapsedTime(TimeUnit.MILLISECONDS) + " (ms)" );
			}
		}
	}
	
	
	/**
	 * 
	 * This task does following task for one subscriber
	 * 
	 * > convert from JSON
	 * > import subscriber
	 * > generate one CDR
	 *
	 */
	private class SubscriberImportTask implements Callable<SubscriberImportResult> {
		
	
		private final int index;
		private final String nv648SubscriberDataString;
		private final SubscriberTransformer subscriberTransformer;
		private SubscriberDataParser subscriberDataParser;
		
		public SubscriberImportTask(int index, String subscriberDataString,
				SubscriberTransformer subscriberTransformer, 
				SubscriberDataParser subscriberDataParser) {
			this.index = index;
			this.nv648SubscriberDataString = subscriberDataString;
			this.subscriberTransformer = subscriberTransformer;
			this.subscriberDataParser = subscriberDataParser;
		}

		@Override
		public SubscriberImportResult call() throws Exception {
			
			SubscriberImportResult result = new SubscriberImportResult(index);
			
			try {
				
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Parsing subscriber data: " + nv648SubscriberDataString);
				}
				
				NV648SubscriberInfo nv648Subscriber = subscriberDataParser.parse(nv648SubscriberDataString);

				String subscriberId = null;
				if (InputType.CSV_SUBSCRIPTION == subscriberDataParser.getInputType()) {
					List<SubscriptionDetail> subscriptionDetails = subscriberTransformer.createSubscriptionsDetails(nv648Subscriber.getSubscriptionInfos());
					
					subscriberId = subscriptionDetails.iterator().next().getAddOnSubscription().getSubscriberIdentity();
					String strippedSubscriberIdentity = SubscriberDAO.getInstance().getStrippedSubscriberIdentity(subscriberId);
					for (SubscriptionDetail subscriptionDetail : subscriptionDetails) {
						subscriptionDetail.getAddOnSubscription().setSubscriberIdentity(strippedSubscriberIdentity);
					}
					
					SubscriberDAO.getInstance().importSubscriptions(subscriberId, subscriptionDetails);
					
				} else {
					SubscriberInfo subscriberInfo = subscriberTransformer.transformTo680Subscriber(nv648Subscriber);
					subscriberId = subscriberInfo.getSprInfo().getSubscriberIdentity();
					SubscriberDAO.getInstance().importSubscriber(subscriberInfo);
				}				
				
				result.setSubscriberId(subscriberId);
 				result.setResult(true);
				
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error while importing subscriber: " + result.getSubscriberId() + ". Reason: " + e.getMessage());
				getLogger().trace(MODULE, e);
				
				result.setResult(false);
				result.setMessage(e.getMessage());
			}

			return result;
		}
	}
	
	private class ShutdownHook extends Thread {
		private ExecutorService executor;

		public ShutdownHook(ExecutorService executor) {
			this.executor = executor;
		}

		public void run() {

			try {
				executor.shutdown();
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Waiting for subscriber import level scheduled async task executor to complete execution");
				}
				if (executor.awaitTermination(5, TimeUnit.SECONDS) == false) {
					if (getLogger().isWarnLogLevel()) {
						getLogger().warn(MODULE, "Shutting down subscriber import level level scheduled async task executor forcefully." +
								" Reason: Async task taking more than 5 second to complete");
					}
					executor.shutdownNow();
				}
				if (getLogger().isInfoLogLevel()) {
					getLogger().info(MODULE, "Subscriber import level scheduled async task completed");
				}
			} catch (Exception ex) {
				try {
					executor.shutdownNow();
				} catch (Exception e) { getLogger().trace(MODULE, e);}
			}
		}
	}
	
	private class CDRGeneratingTask implements Callable<Boolean> {
		
		private PrintWriter cdrPrintWriter;
		private CompletionService<SubscriberImportResult> subscriberImportTaskCompletionService;
		private int noOfTaskSubmitted;
		private SubscriberImportStatisticsImpl subscriberImportStatistics;
		
		public CDRGeneratingTask(PrintWriter cdrPrintWriter, 
				CompletionService<SubscriberImportResult> subscriberImportTaskCompletionService, 
				int noOfTaskSubmitted, 
				SubscriberImportStatisticsImpl subscriberImportStatistics) {
			this.subscriberImportTaskCompletionService = subscriberImportTaskCompletionService;
			this.cdrPrintWriter = cdrPrintWriter;
			this.noOfTaskSubmitted = noOfTaskSubmitted;
			this.subscriberImportStatistics = subscriberImportStatistics;	
		}

		@Override
		public Boolean call() throws Exception {
			
			try {
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "Started CDR generation for imported subscribers");
				}
				
				for (int taskIndex = 0; taskIndex < noOfTaskSubmitted; taskIndex++) {
					SubscriberImportResult subscriberImportResult = subscriberImportTaskCompletionService.poll(30, TimeUnit.MINUTES).get();
					cdrPrintWriter.println(subscriberImportResult.getCDRRecord());
					if (subscriberImportResult.isSuccess()) {
						subscriberImportStatistics.incrementSuccessCount();
					} else {
						subscriberImportStatistics.incrementFailCount();
					}
				}
				
				if (getLogger().isDebugLogLevel()) {
					getLogger().debug(MODULE, "CDR generation for imported subscribers completed ");
				}
				
			} catch (Throwable e) {
				getLogger().error(MODULE, "Error while generation cdr. Reason: " + e.getMessage());
				getLogger().trace(e);
				return false;
			}
			return true;
		}
	}


}
