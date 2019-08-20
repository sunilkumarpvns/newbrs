package com.elitecore.netvertex.gateway.file;

import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.AVG_TPS;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.CURRENT_TPS;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.DURATION_IN_MILLIS;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.DURATION_IN_MILLIS_LICENSE;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.PLUGIN_INFO;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_FAILED_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_FAILED_RECORDS;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_INPROCESS_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_MALFORM_RECORDS;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_PACKETS;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_PARTIALLY_SUCCESS_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_PENDING_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_RECORDS_RECIEVED;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_RECORDS_RECIEVED_LICENSE;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_SUCCESS_FILES;
import static com.elitecore.netvertex.gateway.file.FileGatewayConstants.TOTAL_SUCCESS_RECORDS;

import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.netvertex.gateway.file.parsing.ParsingPluginCounter;
import com.sun.management.snmp.SnmpStatusException;

public class FileGatewayControllerCounter {

	private static final String MODULE = "CRESTEL MEDIATION PARSING SERVICE COUNTER";
	
	/** The i index. */
	protected int iIndex;

	/** The service id. */
	protected String serviceId;

	/** The last counter reset time. */
	protected long lastCounterResetTime;
	
	/** The last counter reset time from license agent. */
	protected long lastCounterResetTimeLicense;

	/** The last configuration reload time. */
	protected long lastConfigReloadTime;

	/** The client wise counter. */
//	protected volatile HashMap<String,CrestelMediationBaseCollectionServiceClientWiseCounter> clientWiseCounter=new HashMap<>();

	/** The consolidation type wise counter. */
//	protected volatile HashMap<String,HashMap<String,CrestelMediationConsServiceConsolidationCounter>> consolidationTypeWiseCounter = new HashMap<>();

	/** The Crestel mediation driver wise counter. */
//	protected volatile HashMap<String,HashMap<String,HashMap<String,CrestelMediationDriverCounter>>> crestelMediationDriverWiseCounter = new HashMap<>();

	/** The Crestel mediation Parsing service plugin wise counter. */
	protected volatile HashMap<String,HashMap<String,ParsingPluginCounter>> crestelMediationPluginWiseCounter = new HashMap<>();

	/** The total success files. */
	protected volatile AtomicLong totalSuccessFiles;

	/** The total failure files. */
	protected volatile AtomicLong totalFailureFiles;

	/** The total pending files. */
	protected volatile AtomicLong totalPendingFiles;

	/** The total in process files. */
	protected volatile AtomicLong totalInprocessFiles;

	/** The total files. */
	protected volatile AtomicLong totalFiles;

	/** The total records. */
	protected volatile AtomicLong totalRecords;
	
	/** Total records for license purpose */
	protected volatile AtomicLong totalRecordsLicense;

	/** The total success records. */
	protected volatile AtomicLong totalSuccessRecords;

	/** The total failure records. */
	protected volatile AtomicLong totalFailureRecords;

	protected volatile AtomicLong totalMalformRecords;

	protected volatile AtomicLong totalPartiallySuccessFiles;

	/** The total invalid records. */
	protected volatile AtomicLong totalInvalidRecords;

	/** The total clone records. */
	protected volatile AtomicLong totalCloneRecords;

	/** The total filter records. */
	protected volatile AtomicLong totalFilterRecords;

	/** The total duplicate records. */
	protected volatile AtomicLong totalDuplicateRecords;

	/** The average transaction per second. */
	protected volatile AtomicLong avgTPS;

	/** The total received packets. */
	protected volatile AtomicLong totalReceivedPackets;

	/** The total received requests. */
	protected volatile AtomicLong totalReceivedRequests;
	
	/** The total received requests. */
	protected volatile AtomicLong totalReceivedRequestsLicense;

	/** The total received template requests. */
	protected volatile AtomicLong totalReceivedTemplateRequests;

	/** The total received mal form requests. */
	protected volatile AtomicLong totalReceivedMalformRequests;

	/** The total received option template requests. */
	protected volatile AtomicLong totalReceivedOptionTemplateRequests;

	/** The total received flow data requests. */
	protected volatile AtomicLong totalReceivedFlowDataRequests;

	/** The total buffered packets. */
	protected volatile AtomicLong totalBufferedPackets;

	/** The total buffered request pending to write. */
	protected volatile AtomicLong totalBufferedRequestPendingToWrite;

	/** The total processed packets. */
	protected volatile AtomicLong totalProcessedPackets;

	/** The total dropped packets. */
	protected volatile AtomicLong totalDroppedPackets;

	/** The prime total request received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalRequestReceived;

	/** The prime total request received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalEchoRequestReceived;

	/** The prime total node alive request received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalNodeAliveRequestReceived;

	/** The prime total redirection request received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalRedirectionRequestReceived;

	/** The prime total data record transfer request received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalDataRecordTransferRequestReceived;

	/** The prime total data record transfer record received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalDataRecordTransferRecordsReceived;
	
	/** The prime total data record transfer record received for GPRS tunneling protocol for license. */
	protected volatile AtomicLong gtpPrimeTotalDataRecordTransferRecordsReceivedLicense;

	/** The  prime total mal formed request packet received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalMalformedRequestPacketReceived;

	/** The prime total redirection response sent for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalRedirectionResponseSent;

	/** The prime total response sent for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalEchoResponseSent;

	/** The  prime total node alive response sent for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalNodeAliveResponseSent;

	/** The prime total data record transfer response sent for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalDataRecordTransferResponseSent;

	/** The prime total version not supported response sent for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalVersionNotSupportedResponseSent;

	/** The prime total invalid client request received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalInvalidClientRequestReceived;

	/** The prime service up time for GPRS tunneling protocol. */
	protected volatile Date gtpPrimeServiceUpTime;

	/** The prime total dropped request for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalDroppedRequest;

	/** The prime total request dropped for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalEchoRequestDropped;

	/** The prime total node alive request dropped for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalNodeAliveRequestDropped;

	/** The prime total redirection request dropped for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalRedirectionRequestDropped;

	/** The  prime total data record transfer request dropped for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalDataRecordTransferRequestDropped;

	/** The prime total data record transfer failure response sent for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalDataRecordTransferFailureResponseSent;

	/** The prime total redirection failure response sent for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalRedirectionFailureResponseSent;

	/** The prime total request retry for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalEchoRequestRetry;

	/** The prime total node alive request retry for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalNodeAliveRequestRetry;

	/** The prime total request sent for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalEchoRequestSent;

	/** The prime total response received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalEchoResponseReceived;

	/** The prime total node alive request sent for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalNodeAliveRequestSent;

	/** The prime total node alive response received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalNodeAliveResponseReceived;

	/** The prime total mal formed echo response received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalMalformedEchoResponseReceived;

	/** The prime total mal formed node alive response received for GPRS tunneling protocol. */
	protected volatile AtomicLong gtpPrimeTotalMalformedNodeAliveResponseReceived;

	/** The prime total invalid client request received for GPRS tunneling protocol. */
	protected volatile AtomicLong totalReceivedRadiusInvalidClientRequests;

	/** The total received authentication requests. */
	protected volatile AtomicLong totalReceivedAuthenticationRequests;

	/** The total received accounting start requests. */
	protected volatile AtomicLong totalReceivedAccountingStartRequests;

	/** The total received accounting stop requests. */
	protected volatile AtomicLong 	totalReceivedAccountingStopRequests;

	/** The total received accounting update requests. */
	protected volatile AtomicLong 	totalReceivedAccountingUpdateRequests;

	/** The total received accounting on requests. */
	protected volatile AtomicLong 	totalReceivedAccountingOnRequests;

	/** The total received accounting off requests. */
	protected volatile AtomicLong 	totalReceivedAccountingOffRequests;

	/** The total received accounting other requests. */
	protected volatile AtomicLong 	totalReceivedAccountingOtherRequests;

	/** The total response sent. */
	protected volatile AtomicLong 	totalResponseSent;

	/** The total authentication response. */
	protected volatile AtomicLong 	totalAuthenticationResponse;

	/** The total accounting start response. */
	protected volatile AtomicLong 	totalAccountingStartResponse;

	/** The total accounting stop response. */
	protected volatile AtomicLong 	totalAccountingStopResponse;

	/** The total accounting update response. */
	protected volatile AtomicLong 	totalAccountingUpdateResponse;

	/** The total accounting on response. */
	protected volatile AtomicLong 	totalAccountingOnResponse;

	/** The total accounting off response. */
	protected volatile AtomicLong 	totalAccountingOffResponse;

	/** The total accounting other response. */
	protected volatile AtomicLong 	totalAccountingOtherResponse;

	/** The total accounting other response. */
	protected volatile AtomicLong 	currentTPS;
	
	protected volatile AtomicLong lastTotalRecordsProcessed;
	
	/** The header for the packet Statistics. */
	protected String header;
	
	/**
	 * Instantiates a new crestel mediation parsing service counter.
	 *
	 * @param index the index
	 * @param serviceId the service id
	 */
	public FileGatewayControllerCounter(int index , String serviceId){
		iIndex = index;
		this.serviceId = serviceId;

		lastCounterResetTime = System.currentTimeMillis();
		lastCounterResetTimeLicense = System.currentTimeMillis();
		totalSuccessFiles = new AtomicLong(0);
		totalFailureFiles = new AtomicLong(0);
		totalPendingFiles = new AtomicLong(0);
		totalInprocessFiles = new AtomicLong(0);
		totalFiles = new AtomicLong(0);
		totalRecords = new AtomicLong(0);
		totalRecordsLicense = new AtomicLong(0);
		avgTPS = new AtomicLong(0);
		totalSuccessRecords = new AtomicLong(0);
		totalFailureRecords = new AtomicLong(0);
		totalMalformRecords = new AtomicLong(0);
		totalPartiallySuccessFiles = new AtomicLong(0);
		totalInvalidRecords = new AtomicLong(0);
		totalFilterRecords = new AtomicLong(0);
		totalCloneRecords= new AtomicLong(0);
		totalDuplicateRecords = new AtomicLong(0);
		totalReceivedRequests = new AtomicLong(0);
		totalReceivedPackets = new AtomicLong(0);
		totalReceivedTemplateRequests = new AtomicLong(0);
		totalReceivedMalformRequests = new AtomicLong(0);
		totalReceivedOptionTemplateRequests = new AtomicLong(0);
		totalReceivedFlowDataRequests = new AtomicLong(0);
		totalBufferedPackets = new AtomicLong(0);
		totalBufferedRequestPendingToWrite = new AtomicLong(0);
		totalDroppedPackets = new AtomicLong(0);
		totalProcessedPackets = new AtomicLong(0);


		gtpPrimeTotalDroppedRequest = new AtomicLong(0);
		gtpPrimeTotalInvalidClientRequestReceived = new AtomicLong(0);
		gtpPrimeServiceUpTime = new Date();
		gtpPrimeTotalRequestReceived = new AtomicLong(0);
		gtpPrimeTotalEchoRequestReceived = new AtomicLong(0);
		gtpPrimeTotalNodeAliveRequestReceived = new AtomicLong(0);
		gtpPrimeTotalRedirectionRequestReceived = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferRequestReceived = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferRecordsReceived = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferRecordsReceivedLicense = new AtomicLong(0);
		gtpPrimeTotalRedirectionResponseSent = new AtomicLong(0);
		gtpPrimeTotalEchoResponseSent = new AtomicLong(0);
		gtpPrimeTotalNodeAliveResponseSent = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferResponseSent = new AtomicLong(0);
		gtpPrimeTotalVersionNotSupportedResponseSent = new AtomicLong(0);
		gtpPrimeTotalMalformedRequestPacketReceived = new AtomicLong(0);
		gtpPrimeTotalEchoRequestDropped = new AtomicLong(0);
		gtpPrimeTotalNodeAliveRequestDropped = new AtomicLong(0);
		gtpPrimeTotalRedirectionRequestDropped = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferRequestDropped = new AtomicLong(0);
		gtpPrimeTotalDataRecordTransferFailureResponseSent = new AtomicLong(0);
		gtpPrimeTotalRedirectionFailureResponseSent = new AtomicLong(0);
		gtpPrimeTotalEchoRequestSent = new AtomicLong(0);
		gtpPrimeTotalEchoResponseReceived = new AtomicLong(0);
		gtpPrimeTotalNodeAliveRequestSent = new AtomicLong(0);
		gtpPrimeTotalNodeAliveResponseReceived = new AtomicLong(0);
		gtpPrimeTotalEchoRequestRetry = new AtomicLong(0);
		gtpPrimeTotalNodeAliveRequestRetry = new AtomicLong(0);
		gtpPrimeTotalMalformedEchoResponseReceived = new AtomicLong(0);
		gtpPrimeTotalMalformedNodeAliveResponseReceived = new AtomicLong(0);
		totalReceivedRadiusInvalidClientRequests = new AtomicLong(0);

		totalReceivedAuthenticationRequests = new AtomicLong(0);
		totalReceivedAccountingStartRequests = new AtomicLong(0);
		totalReceivedAccountingStopRequests = new AtomicLong(0);
		totalReceivedAccountingUpdateRequests = new AtomicLong(0);
		totalReceivedAccountingOnRequests = new AtomicLong(0);
		totalReceivedAccountingOffRequests = new AtomicLong(0);
		totalReceivedAccountingOtherRequests = new AtomicLong(0);

		totalResponseSent = new AtomicLong(0);
		totalAuthenticationResponse = new AtomicLong(0);
		totalAccountingStartResponse = new AtomicLong(0);
		totalAccountingStopResponse = new AtomicLong(0);
		totalAccountingUpdateResponse = new AtomicLong(0);
		totalAccountingOnResponse = new AtomicLong(0);
		totalAccountingOffResponse = new AtomicLong(0);
		totalAccountingOtherResponse = new AtomicLong(0);
		currentTPS = new AtomicLong(0);
		lastTotalRecordsProcessed = new AtomicLong(0);
	}

	public boolean resetCounter() {

		totalSuccessFiles.set(0);
		totalFailureFiles.set(0);
		totalPendingFiles.set(0);
		totalInprocessFiles.set(0);
		totalFiles.set(0);
		totalRecords.set(0);
		totalSuccessRecords.set(0);
		totalFailureRecords.set(0);
		totalMalformRecords.set(0);
		totalPartiallySuccessFiles.set(0);
		totalInvalidRecords.set(0);
		totalFilterRecords.set(0);
		totalCloneRecords.set(0);
		totalDuplicateRecords.set(0);
		avgTPS.set(0);
		totalReceivedPackets.set(0);
		totalReceivedRequests.set(0);
		totalReceivedTemplateRequests.set(0);
		totalReceivedMalformRequests.set(0);
		totalReceivedOptionTemplateRequests.set(0);
		totalReceivedFlowDataRequests.set(0);
		// MED-2993 Prevented buffered packets from setting 0 at the event of Reset Counter 
		//totalBufferedPackets.set(0);
		//totalBufferedRequestPendingToWrite.set(0);
		totalProcessedPackets.set(0);
		totalDroppedPackets.set(0);

		gtpPrimeTotalEchoRequestSent.set(0);
		gtpPrimeTotalEchoResponseReceived.set(0);
		gtpPrimeTotalNodeAliveRequestSent.set(0);
		gtpPrimeTotalNodeAliveResponseReceived.set(0);
		gtpPrimeTotalEchoRequestDropped.set(0);
		gtpPrimeTotalNodeAliveRequestDropped.set(0);
		gtpPrimeTotalRedirectionRequestDropped.set(0);
		gtpPrimeTotalDataRecordTransferRequestDropped.set(0);
		gtpPrimeTotalDataRecordTransferFailureResponseSent.set(0);
		gtpPrimeTotalRedirectionFailureResponseSent.set(0);
		gtpPrimeTotalMalformedRequestPacketReceived.set(0);
		gtpPrimeTotalDroppedRequest.set(0);
		gtpPrimeTotalEchoRequestReceived.set(0);
		gtpPrimeTotalRequestReceived.set(0);
		gtpPrimeTotalNodeAliveRequestReceived.set(0);
		gtpPrimeTotalRedirectionRequestReceived.set(0);
		gtpPrimeTotalDataRecordTransferRequestReceived.set(0);
		gtpPrimeTotalDataRecordTransferRecordsReceived.set(0);
		gtpPrimeTotalRedirectionResponseSent.set(0);
		gtpPrimeTotalEchoResponseSent.set(0);
		gtpPrimeTotalNodeAliveResponseSent.set(0);
		gtpPrimeTotalDataRecordTransferResponseSent.set(0);
		gtpPrimeTotalVersionNotSupportedResponseSent.set(0);
		gtpPrimeTotalInvalidClientRequestReceived.set(0);
		gtpPrimeTotalEchoRequestRetry.set(0);
		gtpPrimeTotalNodeAliveRequestRetry.set(0);
		gtpPrimeTotalMalformedEchoResponseReceived.set(0);
		gtpPrimeTotalMalformedNodeAliveResponseReceived.set(0);
		lastCounterResetTime = System.currentTimeMillis();

		totalReceivedRadiusInvalidClientRequests.set(0);
		totalReceivedAuthenticationRequests.set(0);
		totalReceivedAccountingStartRequests.set(0);
		totalReceivedAccountingStopRequests.set(0);
		totalReceivedAccountingUpdateRequests.set(0);
		totalReceivedAccountingOnRequests.set(0);
		totalReceivedAccountingOffRequests.set(0);

		totalResponseSent.set(0);
		totalAuthenticationResponse.set(0);
		totalAccountingStartResponse.set(0);
		totalAccountingStopResponse.set(0);
		totalAccountingUpdateResponse.set(0);
		totalAccountingOnResponse.set(0);
		totalAccountingOffResponse.set(0);
		lastTotalRecordsProcessed.set(0);
		currentTPS.set(0);

		/*Iterator<String> iterator =clientWiseCounter.keySet().iterator();
		while(iterator.hasNext()){
			String client=iterator.next();
			CrestelMediationBaseCollectionServiceClientWiseCounter collCounter=clientWiseCounter.get(client);

			//collCounter.getTotalBufferedPackets().set(0);
			collCounter.getTotalDroppedPackets().set(0);
			collCounter.getTotalProcessedPackets().set(0);
			collCounter.getTotalReceivedFlowDataRequests().set(0);
			collCounter.getTotalReceivedMalformRequests().set(0);
			collCounter.getTotalReceivedOptionTemplateRequests().set(0);
			collCounter.getTotalReceivedPackets().set(0);
			collCounter.getTotalReceivedRequests().set(0);
			collCounter.getTotalReceivedTemplateRequests().set(0);

			collCounter.totalReceivedRadiusInvalidClientRequests.set(0);
			collCounter.totalReceivedAuthenticationRequests.set(0);
			collCounter.totalReceivedAccountingStartRequests.set(0);
			collCounter.totalReceivedAccountingStopRequests.set(0);
			collCounter.totalReceivedAccountingUpdateRequests.set(0);
			collCounter.totalReceivedAccountingOnRequests.set(0);
			collCounter.totalReceivedAccountingOffRequests.set(0);

			collCounter.totalResponseSent.set(0);
			collCounter.totalAuthenticationResponse.set(0);
			collCounter.totalAccountingStartResponse.set(0);
			collCounter.totalAccountingStopResponse.set(0);
			collCounter.totalAccountingUpdateResponse.set(0);
			collCounter.totalAccountingOnResponse.set(0);
			collCounter.totalAccountingOffResponse.set(0);

		}

		Iterator<String> iter = consolidationTypeWiseCounter.keySet().iterator();
		while(iter.hasNext()){
			String sourcePath = iter.next();
			Map<String,CrestelMediationConsServiceConsolidationCounter> consolidationCounterMap = consolidationTypeWiseCounter.get(sourcePath);
			if(consolidationCounterMap != null){
				Iterator<String> iter2 = consolidationCounterMap.keySet().iterator();
				while(iter2.hasNext()){
					String consolidationType = iter2.next();
					CrestelMediationConsServiceConsolidationCounter consolidationCounter = consolidationCounterMap.get(consolidationType);

					consolidationCounter.getTotalFiles().set(0);
					consolidationCounter.getTotalRecords().set(0);
					consolidationCounter.getTotalSuccessFiles().set(0);
					consolidationCounter.getTotalFailureFiles().set(0);
					consolidationCounter.getTotalSuccessRecords().set(0);
					consolidationCounter.getTotalFailureRecords().set(0);

				}
			}
		}

		Iterator<String> driveriter = crestelMediationDriverWiseCounter.keySet().iterator();
		while(driveriter.hasNext()){
			String driverID = driveriter.next();
			HashMap<String, HashMap<String, CrestelMediationDriverCounter>> driverMap = crestelMediationDriverWiseCounter.get(driverID);
			Iterator<String> iter2 = driverMap.keySet().iterator();
			driverMap.get(driverID).get(driverID).getTotalFiles().set(0);
			driverMap.get(driverID).get(driverID).getTotalPendingFiles().set(0);
			driverMap.get(driverID).get(driverID).getTotalInprocessFiles().set(0);
			driverMap.get(driverID).get(driverID).getTotalSuccessFiles().set(0);
			driverMap.get(driverID).get(driverID).getTotalFailureFiles().set(0);
			driverMap.get(driverID).get(driverID).getTotalRecords().set(0);
			driverMap.get(driverID).get(driverID).getTotalSuccessRecords().set(0);
			driverMap.get(driverID).get(driverID).getTotalFailureRecords().set(0);
			while(iter2.hasNext()){
				String sourcePath = iter2.next();
				if(sourcePath != null && !sourcePath.equals(driverID) ){
					Map<String,CrestelMediationDriverCounter> sourcePathMap = driverMap.get(sourcePath);
					if(sourcePathMap != null){
						CrestelMediationDriverCounter srcPathCounter = sourcePathMap.get(sourcePath);
						srcPathCounter.getTotalFiles().set(0);
						srcPathCounter.getTotalPendingFiles().set(0);
						srcPathCounter.getTotalInprocessFiles().set(0);
						srcPathCounter.getTotalSuccessFiles().set(0);
						srcPathCounter.getTotalFailureFiles().set(0);
						srcPathCounter.getTotalRecords().set(0);
						srcPathCounter.getTotalSuccessRecords().set(0);
						srcPathCounter.getTotalFailureRecords().set(0);

						Iterator<String> iter3 = sourcePathMap.keySet().iterator();
						while(iter3.hasNext()){
							String destinationPath = iter3.next();
							if(destinationPath != null && !destinationPath.equals(sourcePath) ){

								CrestelMediationDriverCounter destinationCounter = sourcePathMap.get(destinationPath);
								destinationCounter.getTotalFiles().set(0);
								destinationCounter.getTotalPendingFiles().set(0);
								destinationCounter.getTotalInprocessFiles().set(0);
								destinationCounter.getTotalSuccessFiles().set(0);
								destinationCounter.getTotalFailureFiles().set(0);
								destinationCounter.getTotalRecords().set(0);
								destinationCounter.getTotalSuccessRecords().set(0);
								destinationCounter.getTotalFailureRecords().set(0);
							}
						}
					}
				}
			}
		}*/

		//reset Parsing Plugin counter
		Iterator<String> pluginIterator = crestelMediationPluginWiseCounter.keySet().iterator();
		while(pluginIterator.hasNext()){
			String sourcePath = pluginIterator.next();
			Map<String,ParsingPluginCounter> pluginCounterMap = crestelMediationPluginWiseCounter.get(sourcePath);
			if(pluginCounterMap != null){
				Iterator<String> iter2 = pluginCounterMap.keySet().iterator();
				while(iter2.hasNext()){
					String pluginType = iter2.next();
					ParsingPluginCounter pluginCounter = pluginCounterMap.get(pluginType);

					pluginCounter.getTotalFiles().set(0);
					pluginCounter.getTotalRecords().set(0);
					pluginCounter.getTotalSuccessFiles().set(0);
					pluginCounter.getTotalFailureFiles().set(0);
					pluginCounter.getTotalSuccessRecords().set(0);
					pluginCounter.getTotalFailureRecords().set(0);
					pluginCounter.getTotalPackets().set(0);

				}
			}
		}
		return true;
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getTotalSuccessFiles()
	 */
	
	public long getTotalSuccessFiles() {
		return totalSuccessFiles.get();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getTotalFailureFiles()
	 */
	
	public long getTotalFailureFiles() {
		return totalFailureFiles.get();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getTotalPendingFiles()
	 */
	
	public long getTotalPendingFiles() {
		return totalPendingFiles.get();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getTotalInprocessFiles()
	 */
	
	public long getTotalInprocessFiles() {
		return totalInprocessFiles.get();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getTotalFiles()
	 */
	
	public long getTotalFiles() {
		return totalFiles.get();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getTotalRecords()
	 */
	
	public long getTotalRecords() {
		return totalRecords.get();
	}
	
	
	public long getTotalLicenseRecords() {
		return totalRecordsLicense.get();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getTotalSuccessRecords()
	 */
	
	public long getTotalSuccessRecords() {
		return totalSuccessRecords.get();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getTotalSuccessRecords()
	 */
	
	public long getTotalProcessedPackets() {
		return totalProcessedPackets.get();
	}
	
	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getTotalFailureRecords()
	 */
	
	public long getTotalFailureRecords() {
		return totalFailureRecords.get();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#getAvgTPS()
	 */
	
	public long getAvgTPS(){
		if(totalSuccessFiles.get() != 0){
			return avgTPS.get()/totalSuccessFiles.get();
		}
		return avgTPS.get();
	}

	
	public long getTotalPartiallySuccessFiles() {
		return totalPartiallySuccessFiles.get();
	}

	
	public long getMalformRecords() {
		return totalMalformRecords.get();
	}
	/* (non-Javadoc)
	 * @see com.elitecore.mediation.commons.util.counter.CrestelMediationBaseServiceCounter#displayCounter()
	 */
	
	public String displayCounter() {
		StringBuilder str = new StringBuilder(displaySimpleCounter());
		Iterator<String> iter = crestelMediationPluginWiseCounter.keySet().iterator();

		while(iter.hasNext()){
			String sourcePath = iter.next();
			Map<String,ParsingPluginCounter> pluginCounterMap = crestelMediationPluginWiseCounter.get(sourcePath);
			if(pluginCounterMap != null){
				str.append("\n\n=============== Source Path  " + sourcePath + " Counter =============\n");

				str.append(fillChar("Plugin Name",25,' ') + " | ");
				str.append(fillChar("Total Processed Files",15,' ') + " | ");
				str.append(fillChar("Success Files",15,' ') + " | ");
				str.append(fillChar("Failure Files",15,' ') + " | ");
				str.append(fillChar("Total Packets",15,' ') + " | ");
				str.append(fillChar("Total Records",15,' ') + " | ");
				str.append(fillChar("Total Partially Success Files",15,' ') + " | ");
				str.append(fillChar("Success Records",15,' ') + " | ");
				str.append(fillChar("Failure Records",15,' ') + " | ");
				str.append(fillChar("Destination Path",50,' ') + " | ");
				str.append("\n" + fillChar("", 130 , '-'));

				Iterator<String> iter2 = pluginCounterMap.keySet().iterator();
				while(iter2.hasNext()){
					String pluginId = iter2.next();
					if(pluginId != null && !pluginId.equals(sourcePath) ){
						ParsingPluginCounter pluginWiseCounter = pluginCounterMap.get(pluginId);
						str.append("\n");
						str.append(fillChar(pluginId.split("#")[0],25,' ') + " | ");
						str.append(fillChar(pluginWiseCounter.getTotalFiles().toString(),15,' ') + " | ");
						str.append(fillChar(pluginWiseCounter.getTotalSuccessFiles().toString(), 15,' ') + " | ");
						str.append(fillChar(pluginWiseCounter.getTotalFailureFiles().toString(), 15,' ') + " | ");
						str.append(fillChar(pluginWiseCounter.getTotalPackets().toString(), 15,' ') + " | ");
						str.append(fillChar(pluginWiseCounter.getTotalRecords().toString(), 15,' ') + " | ");
						str.append(fillChar(pluginWiseCounter.getTotalSuccessRecords().toString(), 15,' ') + " | ");
						str.append(fillChar(pluginWiseCounter.getTotalFailureRecords().toString(), 15,' ') + " | ");
						str.append(fillChar(pluginId.split("#")[1],50,' ') + " | ");
					}
				}
			}
		}
		return str.toString();
	}


	private static final String fillChar(String input, int length, char chr){
		if (input == null)
			input = "";

		StringBuilder stringBuffer = new StringBuilder();
		stringBuffer.append(input);
		for(int i = input.length(); i<=length; i++){
			stringBuffer.append(chr);
		}
		return stringBuffer.toString();
	}

	public HashMap<String, HashMap<String, ParsingPluginCounter>> getCrestelMediationPluginWiseCounter() {
		return crestelMediationPluginWiseCounter;
	}

	public void setCrestelMediationPluginWiseCounter(HashMap<String, HashMap<String, ParsingPluginCounter>> crestelMediationPluginWiseCounter) {
		this.crestelMediationPluginWiseCounter = crestelMediationPluginWiseCounter;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceAvgTPS()
	 */
	
	public Long getParsingServiceAvgTPS() throws SnmpStatusException {
		return getAvgTPS();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceInProcessFiles()
	 */
	
	public Long getParsingServiceInProcessFiles() throws SnmpStatusException {
		return getTotalInprocessFiles();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceCounterResetTime()
	 */
	
	public Long getParsingServiceCounterResetTime() throws SnmpStatusException {
		if(lastCounterResetTime > 0)
			return (System.currentTimeMillis() - lastCounterResetTime) / 10;
		return 0L;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServicePendingFiles()
	 */
	
	public Long getParsingServicePendingFiles() throws SnmpStatusException {
		return getTotalPendingFiles();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceConfigReloadTime()
	 */
	
	public Long getParsingServiceConfigReloadTime() throws SnmpStatusException {
		if(lastConfigReloadTime > 0)
			return (System.currentTimeMillis() - lastConfigReloadTime) / 10;
		return 0L;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceTotalNoOfFiles()
	 */
	
	public Long getParsingServiceTotalNoOfFiles() throws SnmpStatusException {
		return getTotalFiles();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceDownTime()
	 */
	
	public Long getParsingServiceDownTime() throws SnmpStatusException {
		return 0L;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceTotalFailedParsedRecords()
	 */
	
	public Long getParsingServiceTotalFailedParsedRecords()
			throws SnmpStatusException {
		return getTotalFailureRecords();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceUpTime()
	 */
	
	public Long getParsingServiceUpTime() throws SnmpStatusException {
		return 0L;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceResetCounter()
	 */
	
	public Long getParsingServiceResetCounter() throws SnmpStatusException {
		return 0L;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#setParsingServiceResetCounter(java.lang.Long)
	 */
	
	public void setParsingServiceResetCounter(Long x)
			throws SnmpStatusException {
		resetCounter();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#checkParsingServiceResetCounter(java.lang.Long)
	 */
	
	public void checkParsingServiceResetCounter(Long x)
			throws SnmpStatusException {
		LogManager.getLogger().warn(MODULE, "Operation not supported");
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceTotalSuccessfulParsedRecords()
	 */
	
	public Long getParsingServiceTotalSuccessfulParsedRecords()
			throws SnmpStatusException {
		return getTotalSuccessRecords();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceTotalRecords()
	 */
	
	public Long getParsingServiceTotalRecords() throws SnmpStatusException {
		return getTotalRecords();
	}

	

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceID()
	 */
	
	public String getParsingServiceID() throws SnmpStatusException {
		return serviceId;
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceTotalFailedParsedFile()
	 */
	
	public Long getParsingServiceTotalFailedParsedFile()
			throws SnmpStatusException {
		return getTotalFailureFiles();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceTotalSuccessfulParsedFile()
	 */
	
	public Long getParsingServiceTotalSuccessfulParsedFile()
			throws SnmpStatusException {
		return getTotalSuccessFiles();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.mediation.snmp.mediation.autogen.ParsingServiceEntryMBean#getParsingServiceIndex()
	 */
	
	public Integer getParsingServiceIndex() throws SnmpStatusException {
		return iIndex;
	}

	
	public String displaySimpleCounter() {
		StringBuilder str = new StringBuilder("");
		str.append("Total Processed Files :: ");
		str.append(getTotalFiles());
		str.append("\n");
		str.append("Total Success Files :: ");
		str.append(getTotalSuccessFiles());
		str.append("\n");
		str.append("Total Failure Files :: ");
		str.append(getTotalFailureFiles());
		str.append("\n");
		str.append("Total Pending Files :: ");
		str.append(getTotalPendingFiles());
		str.append("\n");
		str.append("Total In Process Files :: ");
		str.append(getTotalInprocessFiles());
		str.append("\n");
		str.append("Total Partially Successful Files :: ");
		str.append(getTotalPartiallySuccessFiles());
		str.append("\n");
		str.append("Total malform flowsets :: ");
		str.append(getMalformRecords());
		str.append("\n");
		str.append("Total Packets :: ");
		str.append(getTotalProcessedPackets());
		str.append("\n");
		str.append("Total Records :: ");
		str.append(getTotalRecords());
		str.append("\n");

		str.append("Average TPS :: ");
		str.append(getAvgTPS());
		str.append("\n");
		return str.toString();
	}


	/*@SuppressWarnings("unchecked")
	protected void restoreCounterStatus(JSONObject jsonObject)
			throws JSONException {
		totalFiles.set(jsonObject.getLong(TOTAL_FILES));
		totalSuccessFiles.set(jsonObject.getLong(TOTAL_SUCCESS_FILES));
		totalSuccessRecords.set(jsonObject.getLong(TOTAL_SUCCESS_RECORDS));
		try{
			totalProcessedPackets.set(jsonObject.getLong(TOTAL_PACKETS));
		}catch(Exception e){ //NOSONAR
			LogManager.getLogger().warn(MODULE, "Adding default value of " + TOTAL_PACKETS + ", Reason : " + e.getMessage());
			totalProcessedPackets.set(0);
		}
		totalFailureFiles.set(jsonObject.getLong(TOTAL_FAILED_FILES));
		totalFailureRecords.set(jsonObject.getLong(TOTAL_FAILED_RECORDS));
		totalRecords.set(jsonObject.getLong(TOTAL_RECORDS_RECIEVED));
		totalRecordsLicense.set(jsonObject.getLong(TOTAL_RECORDS_RECIEVED_LICENSE));
		totalPendingFiles.set(jsonObject.getLong(TOTAL_PENDING_FILES));
		totalInprocessFiles.set(jsonObject.getLong(TOTAL_INPROCESS_FILES));
		avgTPS.set(jsonObject.getLong(AVG_TPS));
		totalPartiallySuccessFiles.set(jsonObject.getLong(TOTAL_PARTIALLY_SUCCESS_FILES));
		totalMalformRecords.set(jsonObject.getLong(TOTAL_MALFORM_RECORDS));

		restoreServiceDuration(jsonObject.getLong(DURATION_IN_MILLIS));
		restoreLicenseServiceDuration(jsonObject.getLong(DURATION_IN_MILLIS_LICENSE));

		JSONObject json = jsonObject.getJSONObject(PLUGIN_INFO);
		Iterator<String> keys = json.keys();
		while(keys.hasNext()){
			String key = keys.next();
			JSONObject json1 = json.getJSONObject(key);
			Iterator<String> keys1 = json1.keys();
			while(keys1.hasNext()){
				String key1 = keys1.next();
				JSONObject json2 = json1.getJSONObject(key1);
				populatePluginMap(crestelMediationPluginWiseCounter, key, key1, json2);
			}
		}

	}*/
	
	protected Map<String, Object> generateMap() {

		Map<String,Map<String,Map<String, Object>>>  data =  new HashMap<>();
		for(String level1Key : crestelMediationPluginWiseCounter.keySet()){
			HashMap<String,ParsingPluginCounter> level1Map = crestelMediationPluginWiseCounter.get(level1Key);
			for(String level2Key : level1Map.keySet()){
				populatePluginJsonMap(data, level1Key, level2Key, level1Map.get(level2Key).toMap());
			}
		}
		Map<String, Object> counterMap = generateCounterMap();
		counterMap.put(PLUGIN_INFO, data);
		counterMap.put(TOTAL_FILES,getTotalFiles());
		counterMap.put(TOTAL_SUCCESS_FILES,getTotalSuccessFiles());
		counterMap.put(TOTAL_FAILED_FILES,getTotalFailureFiles());
		counterMap.put(TOTAL_PENDING_FILES,getTotalPendingFiles());
		counterMap.put(TOTAL_INPROCESS_FILES,getTotalInprocessFiles());
		counterMap.put(TOTAL_PACKETS,getTotalProcessedPackets());
		counterMap.put(TOTAL_SUCCESS_RECORDS,getTotalSuccessRecords());
		counterMap.put(TOTAL_FAILED_RECORDS,getTotalFailureRecords());
		counterMap.put(AVG_TPS, getAvgTPS());
		counterMap.put(TOTAL_RECORDS_RECIEVED, getTotalRecords());
		counterMap.put(TOTAL_RECORDS_RECIEVED_LICENSE, getTotalLicenseRecords());
		counterMap.put(TOTAL_MALFORM_RECORDS, getMalformRecords());
		counterMap.put(TOTAL_PARTIALLY_SUCCESS_FILES, getTotalPartiallySuccessFiles());
		return counterMap;
	}

	/*private void populatePluginMap (HashMap<String,HashMap<String,ParsingPluginCounter>> crestelMediationPluginCounterMap,
			String firstLevel,String secondLevel,JSONObject jsonObject) throws JSONException{
		if(crestelMediationPluginCounterMap == null){
			crestelMediationPluginCounterMap = new HashMap<>();
		}
		if(crestelMediationPluginCounterMap.get(firstLevel)  == null ){
			crestelMediationPluginCounterMap.put(firstLevel, new HashMap<String, ParsingPluginCounter>());
		}

		ParsingPluginCounter crestelMediationPluginCounter = new ParsingPluginCounter();
		crestelMediationPluginCounterMap.get(firstLevel).put(secondLevel, crestelMediationPluginCounter);

	}*/

	private void populatePluginJsonMap (Map<String,Map<String,Map<String,Object>>> ObjectMap,
			String firstLevel,String secondLevel, Map<String,Object> map){
		if(ObjectMap == null){
			ObjectMap = new HashMap<>();
		}
		if(ObjectMap.get(firstLevel)  == null ){
			ObjectMap.put(firstLevel, new HashMap<String,Map<String,Object>>());
		}
		ObjectMap.get(firstLevel).put(secondLevel, map);
	}

	
	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalSuccessFiles()
	 */
	
	public void incrementTotalSuccessFiles() {
		incrementTotalFiles();
		totalSuccessFiles.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalFailureFiles()
	 */
	
	public void incrementTotalFailureFiles() {
		incrementTotalFiles();
		totalFailureFiles.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalPendingFiles()
	 */
	
	public void incrementTotalPendingFiles() {
		totalPendingFiles.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalPendingFiles(long)
	 */
	
	public void addTotalPendingFiles(long totalFiles){
		totalPendingFiles.addAndGet(totalFiles);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#decrementTotalPendingFiles()
	 */
	
	public void decrementTotalPendingFiles(){

		if(totalPendingFiles.get() > 0)
			totalPendingFiles.decrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalInprocessFiles()
	 */
	
	public void incrementTotalInprocessFiles() {
		totalInprocessFiles.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#decrementTotalInprocessFiles()
	 */
	
	public void decrementTotalInprocessFiles(){
		if(totalInprocessFiles.get() > 0 )
			totalInprocessFiles.decrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalFiles()
	 */
	
	public void incrementTotalFiles() {
		totalFiles.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalRecords()
	 */
	
	public void incrementTotalRecords() {
		totalRecords.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalRecords(long)
	 */
	
	public void addTotalRecords(long totalRecords){
		this.totalRecords.addAndGet(totalRecords);
		this.totalRecordsLicense.addAndGet(totalRecords);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalSuccessRecords()
	 */
	
	public void incrementTotalSuccessRecords() {
		totalSuccessRecords.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalSuccessRecords(long)
	 */
	
	public void addTotalSuccessRecords(long totalRecords){
		addTotalRecords(totalRecords);
		totalSuccessRecords.addAndGet(totalRecords);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalInvalidRecords()
	 */
	
	public void incrementTotalInvalidRecords() {
		totalInvalidRecords.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalInvalidRecords(long)
	 */
	
	public void addTotalInvalidRecords(long totalRecords){
		addTotalRecords(totalRecords);
		totalFailureRecords.addAndGet(totalRecords);
	}

	
	public void addTotalMalformRecords(long totalRecords){
		totalMalformRecords.addAndGet(totalRecords);
	}

	
	public void incrementTotalPartiallySuccessFiles() {
		incrementTotalFiles();
		totalPartiallySuccessFiles.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalFilterRecords()
	 */
	
	public void incrementTotalFilterRecords() {
		totalFilterRecords.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalFilterRecords(long)
	 */
	
	public void addTotalFilterRecords(long totalRecords){
		addTotalRecords(totalRecords);
		totalFailureRecords.addAndGet(totalRecords);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalCloneRecords()
	 */
	
	public void incrementTotalCloneRecords() {
		totalCloneRecords.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalCloneRecords(long)
	 */
	
	public void addTotalCloneRecords(long totalRecords){
		addTotalRecords(totalRecords);
		totalCloneRecords.addAndGet(totalRecords);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalDuplicateRecords()
	 */
	
	public void incrementTotalDuplicateRecords() {
		totalDuplicateRecords.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalDuplicateRecords(long)
	 */
	
	public void addTotalDuplicateRecords(long totalRecords){
		addTotalRecords(totalRecords);
		totalFailureRecords.addAndGet(totalRecords);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalFailureRecords()
	 */
	
	public void incrementTotalFailureRecords() {
		totalFailureRecords.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalFailureRecords(long)
	 */
	
	public void addTotalFailureRecords(long totalRecords){
		addTotalRecords(totalRecords);
		totalFailureRecords.addAndGet(totalRecords);
	}



	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#setAvgTPS(long)
	 */
	
	public void setAvgTPS(long avgTPS) {
		this.avgTPS.addAndGet(avgTPS);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalReceivedRequests()
	 */
	
	public void incrementTotalReceivedRequests() {
		totalReceivedRequests.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalReceivedPackets()
	 */
	
	public void incrementTotalReceivedPackets() {
		totalReceivedPackets.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalReceivedTemplateRequests()
	 */
	
	public void incrementTotalReceivedTemplateRequests() {
		totalReceivedTemplateRequests.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalReceivedMalformRequests()
	 */
	
	public void incrementTotalReceivedMalformRequests() {
		totalReceivedMalformRequests.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalReceivedOptionTemplateRequests()
	 */
	
	public void incrementTotalReceivedOptionTemplateRequests() {
		totalReceivedOptionTemplateRequests.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalReceivedFlowDataRequests()
	 */
	
	public void incrementTotalReceivedFlowDataRequests() {
		totalReceivedFlowDataRequests.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalBufferedPackets()
	 */
	
	public void incrementTotalBufferedPackets() {
		totalBufferedPackets.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalDroppedPackets()
	 */
	
	public void incrementTotalDroppedPackets() {
		totalDroppedPackets.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#incrementTotalProcessedPackets()
	 */
	
	public void incrementTotalProcessedPackets() {
		totalProcessedPackets.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalProcessedPackets(long)
	 */
	
	public void addTotalProcessedPackets(long packets) {
		totalProcessedPackets.addAndGet(packets);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#decrementTotalBufferedPackets()
	 */
	
	public void decrementTotalBufferedPackets() {
		if(totalBufferedPackets.get() > 0 )
			totalBufferedPackets.decrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#addTotalBufferedPackets(long)
	 */
	
	public void addTotalBufferedPackets(long packets) {
		totalBufferedPackets.addAndGet(packets);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#removeTotalBufferedPackets(long)
	 */
	
	public void removeTotalBufferedPackets(long packets) {
		for(long l = 0 ; l < packets ; l++){
			if(totalBufferedPackets.get() > 0)
				totalBufferedPackets.decrementAndGet();
		}
	}
	
	public void addTotalBufferedRequestPendingToWrite(long packets) {
		totalBufferedRequestPendingToWrite.addAndGet(packets);
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#decrementTotalBufferedRequestPendingToWrite()
	 */
	
	public void decrementTotalBufferedRequestPendingToWrite() {
		if(totalBufferedRequestPendingToWrite.get() > 0 )
			totalBufferedRequestPendingToWrite.decrementAndGet();
	}

	public void incrementTotalBufferedRequestPendingToWrite() {
		totalBufferedRequestPendingToWrite.incrementAndGet();
	}

	/* (non-Javadoc)
	 * @see com.elitecore.core.commons.util.counter.ICounterManager#removeTotalBufferedRequestPendingToWrite(long)
	 */
	
	public void removeTotalBufferedRequestPendingToWrite(long packets) {
		totalBufferedRequestPendingToWrite.addAndGet(0-packets);
	}
	
	public void addAvgTPS(long avgTPS) {
		this.avgTPS.addAndGet(avgTPS);
	}

	public void addGtpPrimeTotalRequestReceived(
			long gtpPrimeTotalRequestReceived) {
		this.gtpPrimeTotalRequestReceived.addAndGet(gtpPrimeTotalRequestReceived);
	}

	public void addGtpPrimeTotalEchoRequestReceived(
			long gtpPrimeTotalEchoRequestReceived) {
		this.gtpPrimeTotalEchoRequestReceived.addAndGet(gtpPrimeTotalEchoRequestReceived);
	}

	public void addGtpPrimeTotalNodeAliveRequestReceived(
			long gtpPrimeTotalNodeAliveRequestReceived) {
		this.gtpPrimeTotalNodeAliveRequestReceived.addAndGet(gtpPrimeTotalNodeAliveRequestReceived);
	}

	public void addGtpPrimeTotalRedirectionRequestReceived(
			long gtpPrimeTotalRedirectionRequestReceived) {
		this.gtpPrimeTotalRedirectionRequestReceived.addAndGet(gtpPrimeTotalRedirectionRequestReceived);
	}

	public void addGtpPrimeTotalDataRecordTransferRequestReceived(
			long gtpPrimeTotalDataRecordTransferRequestReceived) {
		this.gtpPrimeTotalDataRecordTransferRequestReceived.addAndGet(gtpPrimeTotalDataRecordTransferRequestReceived);
	}

	public void addGtpPrimeTotalDataRecordTransferRecordsReceived(
			long gtpPrimeTotalDataRecordTransferRecordsReceived) {
		this.gtpPrimeTotalDataRecordTransferRecordsReceived.addAndGet(gtpPrimeTotalDataRecordTransferRecordsReceived);
		this.gtpPrimeTotalDataRecordTransferRecordsReceivedLicense.addAndGet(gtpPrimeTotalDataRecordTransferRecordsReceived);
	}

	public void addGtpPrimeTotalMalformedRequestPacketReceived(
			long gtpPrimeTotalMalformedRequestPacketReceived) {
		this.gtpPrimeTotalMalformedRequestPacketReceived.addAndGet(gtpPrimeTotalMalformedRequestPacketReceived);
	}

	public void addGtpPrimeTotalRedirectionResponseSent(
			long gtpPrimeTotalRedirectionResponseSent) {
		this.gtpPrimeTotalRedirectionResponseSent.addAndGet(gtpPrimeTotalRedirectionResponseSent);
	}

	public void addGtpPrimeTotalEchoResponseSent(
			long gtpPrimeTotalEchoResponseSent) {
		this.gtpPrimeTotalEchoResponseSent.addAndGet(gtpPrimeTotalEchoResponseSent);
	}

	public void addGtpPrimeTotalNodeAliveResponseSent(
			long gtpPrimeTotalNodeAliveResponseSent) {
		this.gtpPrimeTotalNodeAliveResponseSent.addAndGet(gtpPrimeTotalNodeAliveResponseSent);
	}

	public void addGtpPrimeTotalDataRecordTransferResponseSent(
			long gtpPrimeTotalDataRecordTransferResponseSent) {
		this.gtpPrimeTotalDataRecordTransferResponseSent.addAndGet(gtpPrimeTotalDataRecordTransferResponseSent);
	}

	public void addGtpPrimeTotalVersionNotSupportedResponseSent(
			long gtpPrimeTotalVersionNotSupportedResponseSent) {
		this.gtpPrimeTotalVersionNotSupportedResponseSent.addAndGet(gtpPrimeTotalVersionNotSupportedResponseSent);
	}

	public void addGtpPrimeTotalInvalidClientRequestReceived(
			long gtpPrimeTotalInvalidClientRequestReceived) {
		this.gtpPrimeTotalInvalidClientRequestReceived.addAndGet(gtpPrimeTotalInvalidClientRequestReceived);
	}


	public void addGtpPrimeTotalDroppedRequest(
			long gtpPrimeTotalDroppedRequest) {
		this.gtpPrimeTotalDroppedRequest.addAndGet(gtpPrimeTotalDroppedRequest);
	}

	public void addGtpPrimeTotalEchoRequestDropped(
			long gtpPrimeTotalEchoRequestDropped) {
		this.gtpPrimeTotalEchoRequestDropped.addAndGet(gtpPrimeTotalEchoRequestDropped);
	}

	public void addGtpPrimeTotalNodeAliveRequestDropped(
			long gtpPrimeTotalNodeAliveRequestDropped) {
		this.gtpPrimeTotalNodeAliveRequestDropped.addAndGet(gtpPrimeTotalNodeAliveRequestDropped);
	}

	public void addGtpPrimeTotalRedirectionRequestDropped(
			long gtpPrimeTotalRedirectionRequestDropped) {
		this.gtpPrimeTotalRedirectionRequestDropped.addAndGet(gtpPrimeTotalRedirectionRequestDropped);
	}

	public void addGtpPrimeTotalDataRecordTransferRequestDropped(
			long gtpPrimeTotalDataRecordTransferRequestDropped) {
		this.gtpPrimeTotalDataRecordTransferRequestDropped.addAndGet(gtpPrimeTotalDataRecordTransferRequestDropped);
	}

	public void addGtpPrimeTotalDataRecordTransferFailureResponseSent(
			long gtpPrimeTotalDataRecordTransferFailureResponseSent) {
		this.gtpPrimeTotalDataRecordTransferFailureResponseSent.addAndGet(gtpPrimeTotalDataRecordTransferFailureResponseSent);
	}

	public void addGtpPrimeTotalRedirectionFailureResponseSent(
			long gtpPrimeTotalRedirectionFailureResponseSent) {
		this.gtpPrimeTotalRedirectionFailureResponseSent.addAndGet(gtpPrimeTotalRedirectionFailureResponseSent);
	}

	public void addGtpPrimeTotalEchoRequestRetry(
			long gtpPrimeTotalEchoRequestRetry) {
		this.gtpPrimeTotalEchoRequestRetry.addAndGet(gtpPrimeTotalEchoRequestRetry);
	}

	public void addGtpPrimeTotalNodeAliveRequestRetry(
			long gtpPrimeTotalNodeAliveRequestRetry) {
		this.gtpPrimeTotalNodeAliveRequestRetry.addAndGet(gtpPrimeTotalNodeAliveRequestRetry);
	}

	public void addGtpPrimeTotalEchoRequestSent(
			long gtpPrimeTotalEchoRequestSent) {
		this.gtpPrimeTotalEchoRequestSent.addAndGet(gtpPrimeTotalEchoRequestSent);
	}

	public void addGtpPrimeTotalEchoResponseReceived(
			long gtpPrimeTotalEchoResponseReceived) {
		this.gtpPrimeTotalEchoResponseReceived.addAndGet(gtpPrimeTotalEchoResponseReceived);
	}

	public void addGtpPrimeTotalNodeAliveRequestSent(
			long gtpPrimeTotalNodeAliveRequestSent) {
		this.gtpPrimeTotalNodeAliveRequestSent.addAndGet(gtpPrimeTotalNodeAliveRequestSent);
	}

	public void addGtpPrimeTotalNodeAliveResponseReceived(
			long gtpPrimeTotalNodeAliveResponseReceived) {
		this.gtpPrimeTotalNodeAliveResponseReceived.addAndGet(gtpPrimeTotalNodeAliveResponseReceived);
	}

	public void addGtpPrimeTotalMalformedEchoResponseReceived(
			long gtpPrimeTotalMalformedEchoResponseReceived) {
		this.gtpPrimeTotalMalformedEchoResponseReceived.addAndGet(gtpPrimeTotalMalformedEchoResponseReceived);
	}

	public void addGtpPrimeTotalMalformedNodeAliveResponseReceived(
			long gtpPrimeTotalMalformedNodeAliveResponseReceived) {
		this.gtpPrimeTotalMalformedNodeAliveResponseReceived.addAndGet(gtpPrimeTotalMalformedNodeAliveResponseReceived);
	}

	protected long getServiceDurationInMillis(){
		return System.currentTimeMillis() - lastCounterResetTime;
	}

	protected long getLicenseServiceDurationInMillis(){
		return System.currentTimeMillis() - lastCounterResetTimeLicense;
	}

	protected Map<String,Object> generateCounterMap(){
		Map<String,Object> counterMap = new HashMap<>();
		counterMap.put(DURATION_IN_MILLIS, getServiceDurationInMillis());
		counterMap.put(DURATION_IN_MILLIS_LICENSE, getLicenseServiceDurationInMillis());
		counterMap.put(CURRENT_TPS, currentTPS.get());
//		counterMap.put(RECORD_TIME, RECORDTIMEFORMAT.format(new Date()));
		return counterMap;
	}

	protected void restoreServiceDuration (Long duration){
		if(duration != null && duration > 0){
			lastCounterResetTime =  lastCounterResetTime - duration;
		}
	}
	
	/**
	 * Restore License check duration time
	 */
	protected void restoreLicenseServiceDuration (Long duration){
		if(duration != null && duration > 0){
			lastCounterResetTimeLicense =  lastCounterResetTimeLicense - duration;
		}
	}
	
	/*public String getCounterStatisticsToJson(long executionInterval) {

		calculateCurrentTPS(executionInterval);
		JSONObject jsonObject = new JSONObject(generateMap());
		return jsonObject.toString();
	}*/

	
	/*public String getCounterStatisticsToJson() {
		JSONObject jsonObject = new JSONObject(generateMap());
		return jsonObject.toString();
	}*/
	
	/*public boolean restoreCounterStatisticsFromJson(String jsonCounterStatistics) {
		try {
			JSONObject jsonObject = new JSONObject(jsonCounterStatistics);
			restoreCounterStatus(jsonObject);
			return true;
		} catch (JSONException e) {
			LogManager.getLogger().info(MODULE,"Error occured while restoring counter status, reason :"+ e.getMessage());
			LogManager.getLogger().trace(MODULE, e);
		}
		return false;
	}*/

	protected void calculateCurrentTPS(long timeIntervalInSecond){
		long recordCountDiff = getTotalRecords() - lastTotalRecordsProcessed.get();
		if(timeIntervalInSecond > 0){
			currentTPS.set(recordCountDiff/timeIntervalInSecond);
		}
		lastTotalRecordsProcessed.set(getTotalRecords());
	}
	
	/*public String getCounterHistoryStatistics(List<String> jsonStatisticsHistoryList){

		StringBuilder str = new StringBuilder();
		str.append("\n\t" + Utilities.fillChar("", 60 , '-'));
		str.append("\n\t" + Utilities.fillChar("| DATE",40  , ' '));
		str.append(Utilities.fillChar("| Total Packets ",20  , ' ')+"|");
		String tableContent = getTotalRecordsFromJSON(jsonStatisticsHistoryList);
		if(tableContent != null && tableContent.length() > 0){
			str.append(tableContent);
		}else{
			str.append("\n\t" + Utilities.fillChar("",20  , '-'));
			str.append(Utilities.fillChar(" No stats available ",20  , '-'));
			str.append(Utilities.fillChar("",20  , '-'));

		}
		str.append("\n\t" + Utilities.fillChar("", 60 , '-'));
		return str.toString();
	}*/

	/*protected String getTotalRecordsFromJSON(List<String> jsonStatisticsHistoryList){

		StringBuilder str = new StringBuilder();
		for(String statistics : jsonStatisticsHistoryList){
			String date = null;
			long totalRecordsVar = 0;
			try {
				JSONObject jsonObject = new JSONObject(statistics);
				date = jsonObject.getString(RECORD_TIME);
				totalRecordsVar = jsonObject.getLong(TOTAL_RECORDS_RECIEVED);
				str.append("\n\t" + Utilities.fillChar("| "+date,40  , ' '));
				str.append(Utilities.fillChar("|"+totalRecordsVar,20  , ' ')+"|");
			} catch (JSONException e) {
				LogManager.getLogger().info(MODULE,"Error occured while restoring counter status, reason :"+ e.getMessage());
				LogManager.getLogger().trace(MODULE, e);
			}
		}
		return str.toString();
	}*/
}
