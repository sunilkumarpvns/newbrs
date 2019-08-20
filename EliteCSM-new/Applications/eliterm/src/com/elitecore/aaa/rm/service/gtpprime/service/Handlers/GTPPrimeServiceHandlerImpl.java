package com.elitecore.aaa.rm.service.gtpprime.service.Handlers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.Inet6Address;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.alert.Alerts;
import com.elitecore.aaa.rm.conf.GTPPrimeConfiguration;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientData;
import com.elitecore.aaa.rm.service.gtpprime.data.GTPPrimeClientDataImpl;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceContext;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceRequest;
import com.elitecore.aaa.rm.service.gtpprime.service.GTPPrimeServiceResponse;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.fileio.EliteFileStream;
import com.elitecore.core.commons.fileio.EliteFileStream.Builder;
import com.elitecore.core.commons.util.sequencer.ISequencer;
import com.elitecore.core.commons.util.sequencer.SynchronizedSequencer;
import com.elitecore.core.serverx.alert.AlertSeverity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coregtp.commons.elements.BaseGTPPrimeElement;
import com.elitecore.coregtp.commons.elements.GTPPrimeCDRData;
import com.elitecore.coregtp.commons.elements.GTPPrimeDataRecordPacket;
import com.elitecore.coregtp.commons.elements.IpAddress;
import com.elitecore.coregtp.commons.elements.Octet;
import com.elitecore.coregtp.commons.elements.Sequence;
import com.elitecore.coregtp.commons.packet.GTPPrimePacketV1;
import com.elitecore.coregtp.commons.packet.ParseException;
import com.elitecore.coregtp.commons.util.Dictionary;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeConstants;
import com.elitecore.coregtp.commons.util.constants.GTPPrimeElementTypeConstants;

/**
 * @author dhaval.jobanputra
 *
 */
public class GTPPrimeServiceHandlerImpl implements GTPPrimeServiceHandler {

	private static final String MODULE = "GTP'-Handler";
	private static final String INP_EXTENSION = "inp";

	private GTPPrimeConfiguration gtpPrimeServiceConfiguration;
	private GTPPrimeServiceContext gtpServiceContext;

	private final static byte VERSION_SUPPORTED = 2;
	private final static int MANDATORY_IE_MISSING = 202;
	private final static int MANDATORY_IE_INCORRECT = 201;
	private final static int OPTIONAL_IE_INCORRECT = 203;
	private final static int SEND_DATA_RECORD_PACKET = 1;
	private final static int SEND_POSSIBLY_DUPLICATED_DATA_RECORD_PACKET = 2;
	private final static int CANCEL_DATA_RECORD_PACKET = 3;
	private final static int RELEASE_DATA_RECORD_PACKET = 4;
	private final static int REQUESTS_ACCEPTED = 128;
	private final static int SYSTEM_FAILURE = 204;
	private final static int THIS_NODE_IS_ABOUT_TO_GO_DOWN = 63;
	private final static int REQUEST_NOT_FULFILLED = 255;
	private final static int SEQ_NUM_OF_RELEASED_OR_CANCELLED_PACKETS_IE_INCORRECT = 254;

	private HashMap<String, EliteFileStream> fileStreams;
	private HashMap<String, HashMap<Integer, GTPPrimeDataRecordPacket>> clientCDRMap;

	public GTPPrimeServiceHandlerImpl(GTPPrimeServiceContext serviceContext) {
		this.gtpServiceContext = serviceContext;

		this.gtpPrimeServiceConfiguration = serviceContext.getGTPPrimeConfiguration();
		fileStreams = new HashMap<String, EliteFileStream>();
	}

	@Override
	public void handleRequest(GTPPrimeServiceRequest gtpPrimeRequest, GTPPrimeServiceResponse gtpPrimeResponse,
			ISession session) {

		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, " Received Packet from: " + gtpPrimeRequest.getSourceAddress() +":" + gtpPrimeRequest.getSourcePort()+ gtpPrimeRequest.toString());
		}

		
		gtpPrimeResponse.setClientData(gtpPrimeServiceConfiguration.getClient(gtpPrimeRequest.getSourceAddress()));

		// common header values for all type of requests
		gtpPrimeResponse.setVersion (gtpPrimeRequest.getVersion());
		gtpPrimeResponse.setProtocolType (gtpPrimeRequest.getProtocolType());
		gtpPrimeResponse.setSparebits (gtpPrimeRequest.getSpareBits());
		gtpPrimeResponse.setHeaderType (gtpPrimeRequest.getHeaderType());

		// if request is arrived for a newer version than supported version
		if ( gtpPrimeRequest.getVersion() > VERSION_SUPPORTED){
			gtpPrimeVersionNotSupported(gtpPrimeRequest , gtpPrimeResponse);
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Request version is not supported. Version: " + gtpPrimeRequest.getVersion());
			}
			return;
		}



		int messageType = gtpPrimeRequest.getMessageType();
		if(messageType == GTPPrimeConstants.ECHO_REQUEST.getTypeID()) {
			gtpPrimeEchoRequest(gtpPrimeRequest , gtpPrimeResponse);
		}
		else if(messageType == GTPPrimeConstants.NODE_ALIVE_REQUEST.getTypeID()) {
			gtpPrimeNodeAliveRequest(gtpPrimeRequest , gtpPrimeResponse);
		}
		else if (messageType == GTPPrimeConstants.REDIRECTION_REQUEST.getTypeID()){
			gtpPrimeRedirectionRequest(gtpPrimeRequest, gtpPrimeResponse);			
		}
		else if (messageType == GTPPrimeConstants.DATA_RECORD_TRANSFER_REQUEST.getTypeID()){
			gtpPrimeDataRecordTransferRequest(gtpPrimeRequest , gtpPrimeResponse );
		}
		else{	
			gtpPrimeResponse.markForDropRequest();
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Request type could not be recognized. Request type: " + messageType + ". Marked for dropped request");
			}

		}
	}
	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	private void gtpPrimeDataRecordTransferRequest(GTPPrimeServiceRequest gtpPrimeRequest,GTPPrimeServiceResponse gtpPrimeResponse ) {
		List<BaseGTPPrimeElement> receivedIEList = null;
		List<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>(); 
		int responseLength=0;
		receivedIEList = gtpPrimeRequest.getElementList();
		BaseGTPPrimeElement toSendElement;
		EliteFileStream fileStream = null;
		String fileName = null;
		String fileLocation = null;
		
		GTPPrimeClientData client = gtpPrimeServiceConfiguration.getClient(gtpPrimeRequest.getSourceAddress());
		
		fileLocation = client.getFileLocation();
		fileName = client.getFileName();
		try {
			String destinationPath = fileLocation + File.separator + gtpPrimeRequest.getSourceAddress().getHostAddress();
			final String strWriterKey = destinationPath;
			fileStream = fileStreams.get(strWriterKey);
			if(fileStream == null) {
				synchronized (fileStreams) {
					fileStream = fileStreams.get(strWriterKey);
					if(fileStream == null) {
						try{
							ISequencer sequencer = new SynchronizedSequencer(client.getMinSequenceRange()+"", client.getMaxSequenceRange()+"","","");
							sequencer.init();
							fileStream = new Builder()
									.fileName(fileName)
									.activeFileExt(INP_EXTENSION)
									.destinationPath(destinationPath)
									.rollingType(client.getRollingType())
									.rollingUnit(client.getRollingUnit())
									.sequencer(sequencer)
									.taskScheduler(gtpServiceContext.getServerContext().getTaskScheduler())
									.pattern("suffix")
									.cdrSequenceRequired(client.getIsFileSequenceRequired())
									.build();

							fileStreams.put(strWriterKey, fileStream);
						}catch(InitializationFailedException ex){
							fileStream = new Builder()
									.fileName(fileName)
									.activeFileExt(INP_EXTENSION)
									.destinationPath(destinationPath)
									.rollingType(client.getRollingType())
									.rollingUnit(client.getRollingUnit())
									.taskScheduler(gtpServiceContext.getServerContext().getTaskScheduler())
									.cdrSequenceRequired(client.getIsFileSequenceRequired())
									.build();
							fileStreams.put(strWriterKey, fileStream);
						}
						
					}
				}
			}

			int counter=0; 
			Octet packetTransferElement = (Octet)receivedIEList.get(counter);

			if ( packetTransferElement == null || (!(packetTransferElement.getElementType() == GTPPrimeElementTypeConstants.PACKET_TRANSFER_COMMAND.typeID))){
				byte[] value = new byte[]{(byte) MANDATORY_IE_MISSING};
				toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
				responseLength += 2;
				toSendIEList.add(toSendElement);

				toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
				responseLength += 3;
				toSendIEList.add(toSendElement);
				gtpPrimeResponse.setFailure();
				finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Mandatory IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
				}
				return;
			}

			int elementValue = packetTransferElement.getOctet();
			if (!(Dictionary.getInstance().getSupportedValueMap(GTPPrimeElementTypeConstants.PACKET_TRANSFER_COMMAND.typeID).containsKey(elementValue))){
				byte[] value = new byte[]{(byte) MANDATORY_IE_INCORRECT};
				toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
				responseLength += 2;
				toSendIEList.add(toSendElement);

				toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
				responseLength += 3;
				toSendIEList.add(toSendElement);
				gtpPrimeResponse.setFailure();
				finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Mandatory IE (type: Packet transfer command) value not found in Dictionary - in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
				}
				return;
			}

			if (elementValue == SEND_DATA_RECORD_PACKET){
				counter++;
				processSendDataRecordPacket(gtpPrimeRequest, gtpPrimeResponse, fileStream, counter);

			}else if(elementValue == SEND_POSSIBLY_DUPLICATED_DATA_RECORD_PACKET){
				counter++;
				processSendPossiblyDuplicatedDataRecordPacket(gtpPrimeRequest, gtpPrimeResponse, fileStream, counter);

			}else if(elementValue == CANCEL_DATA_RECORD_PACKET){
				counter++;
				processCancelDataRecordPacket(gtpPrimeRequest, gtpPrimeResponse, fileStream, counter);

			}else if(elementValue == RELEASE_DATA_RECORD_PACKET){
				counter++;
				processReleaseDataRecordPacket(gtpPrimeRequest, gtpPrimeResponse, fileStream, counter);
			}
		} catch (ClassCastException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) MANDATORY_IE_INCORRECT};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);

			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Mandatory IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0,
					"Malformed Packet Received. Sending NACK");
			
		} catch (IndexOutOfBoundsException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) MANDATORY_IE_MISSING};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);

			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Mandatory IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0,
					"Malformed Packet Received. Sending NACK");
			
		} catch (IOException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) SYSTEM_FAILURE};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "IO Exception. Error creating file writer Reason: " + e.getMessage());
			}
			finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Error creating file writer. Sending NACK", 0,
					"Error creating file writer. Sending NACK");
			
		} catch (Exception e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) SYSTEM_FAILURE};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Exception. Sending request not fulfiiled cause. Reason: " + e.getMessage());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Unknown Error.", 0, "Unknown Error.");
			
			finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
		}
	}

	private void processSendDataRecordPacket(GTPPrimeServiceRequest gtpPrimeRequest,GTPPrimeServiceResponse gtpPrimeResponse, EliteFileStream fileStream, int counter){
		int responseLength = 0;
		BaseGTPPrimeElement receivedElement;
		BaseGTPPrimeElement toSendElement;
		List<BaseGTPPrimeElement> receivedIEList = null ;
		List<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>();
		receivedIEList = gtpPrimeRequest.getElementList();
		receivedElement = receivedIEList.get(counter);

		try{
			if ( receivedElement == null || (!(receivedElement.getElementType() == GTPPrimeElementTypeConstants.DATA_RECORD_PACKET.typeID))){
				byte[] value = new byte[]{(byte) OPTIONAL_IE_INCORRECT};
				toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
				responseLength += 2;
				toSendIEList.add(toSendElement);

				toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
				responseLength += 3;
				toSendIEList.add(toSendElement);
				gtpPrimeResponse.setFailure();

				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Optional IE value incorrect in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
					LogManager.getLogger().info(MODULE, "Expected - Data record packet");
				}
				return;
			}

			GTPPrimeDataRecordPacket cdrPacket = (GTPPrimeDataRecordPacket)receivedElement;
			byte[] cdrBytes = cdrPacket.getValueByte();
			if (cdrBytes == null){
				byte[] value = new byte[]{(byte) SYSTEM_FAILURE};
				toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
				responseLength += 2;
				toSendIEList.add(toSendElement);
				
				toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 2, null);
				responseLength += 3;
				toSendIEList.add(toSendElement);
				gtpPrimeResponse.setFailure();
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "CDR bytes found null in packet no: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
				}
				return;
			}
			
			fileStream.appendRecord(cdrBytes);

			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Cdr successfully written to file of send data record packet");
			}

			// TODO: CDR processing
			byte[] value = new byte[]{(byte) REQUESTS_ACCEPTED};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);

			int seqNum = gtpPrimeRequest.getSeqNumber();
			int temp=0;
			byte[] value1 = new byte[2];
			temp = seqNum;
			temp = temp >> 8;
			value1[0] = (byte) (temp & 0xff);
			value1[1] = (byte) (seqNum & 0xff);
			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 2, value1);
			responseLength += 5;
			toSendIEList.add(toSendElement);
		}catch (ClassCastException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) MANDATORY_IE_INCORRECT};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Mandatory IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0,
					"Malformed Packet Received. Sending NACK");
		} catch (IndexOutOfBoundsException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) OPTIONAL_IE_INCORRECT};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Optional IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0,
					"Malformed Packet Received. Sending NACK");
			
		} catch (FileNotFoundException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) REQUEST_NOT_FULFILLED};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "File not found error. Sending request not fulfiiled cause.");
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "File not found to write CDR. Sending NACK", 0,
					"File not found to write CDR. Sending NACK");
			
		} catch (IOException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) SYSTEM_FAILURE};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "IO Exception. Sending request not fulfiiled cause. Reason: " + e.getMessage());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Error in File input output. Sending NACK", 0,
					"Error in File input output. Sending NACK");
		} catch (Exception e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) SYSTEM_FAILURE};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Exception. Sending request not fulfiiled cause. Reason: " + e.getMessage());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Error in File input output. Sending NACK", 0,
					"Error in File input output. Sending NACK");
		}finally{
			finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Data record transfer response created successfully of send data record packet");
		}
	}

	private void processSendPossiblyDuplicatedDataRecordPacket(GTPPrimeServiceRequest gtpPrimeRequest,GTPPrimeServiceResponse gtpPrimeResponse, EliteFileStream fileStream, int counter){
		int responseLength = 0;
		BaseGTPPrimeElement receivedElement;
		BaseGTPPrimeElement toSendElement;
		List<BaseGTPPrimeElement> receivedIEList = null ;
		receivedIEList = gtpPrimeRequest.getElementList();
		List<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>(); 
		try{
			receivedElement = receivedIEList.get(counter);

			if (receivedElement == null || (!(receivedElement.getElementType() == GTPPrimeElementTypeConstants.DATA_RECORD_PACKET.typeID))){
				byte[] value = new byte[]{(byte) OPTIONAL_IE_INCORRECT};
				toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
				responseLength += 2;
				toSendIEList.add(toSendElement);

				toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
				responseLength += 3;
				toSendIEList.add(toSendElement);
				gtpPrimeResponse.setFailure();
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Optional IE value incorrect in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
					LogManager.getLogger().info(MODULE, "Expected - Data record packet");
				}
				return;
			}
			
			GTPPrimeClientData client = gtpPrimeServiceConfiguration.getClient(gtpPrimeRequest.getSourceAddress());
			
			String clientIP = client.getClientIP();

			synchronized (clientCDRMap) {
				clientCDRMap.get(clientIP).put(gtpPrimeRequest.getSeqNumber(), (GTPPrimeDataRecordPacket) receivedElement);
			}

			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Possibly duplicated CDR stored in memory");
			}
			// TODO: CDR processing
			byte[] value = new byte[]{(byte) REQUESTS_ACCEPTED};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);

			int seqNum = gtpPrimeRequest.getSeqNumber();
			int temp = 0;
			byte[] value1 = new byte[2];
			temp = seqNum;
			temp = temp >> 8;
			value1[0] = (byte) (temp & 0xff);
			value1[1] = (byte) (seqNum & 0xff);
			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 2, value1);
			responseLength += 5;
			toSendIEList.add(toSendElement);
		} catch (IndexOutOfBoundsException e){
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) OPTIONAL_IE_INCORRECT};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Optional IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0,
					"Malformed Packet Received. Sending NACK");
		}  catch (ClassCastException e){
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) OPTIONAL_IE_INCORRECT};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Optional IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0, "Malformed Packet Received. Sending NACK");
			
		} catch (Exception e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) SYSTEM_FAILURE};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Exception. Sending request not fulfiiled cause. Reason: " + e.getMessage());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Error in File input output. Sending NACK", 0, "Error in File input output. Sending NACK");
		} finally{
			finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
		}

		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Data record transfer response created successfully of possible data record packet");
		}
	}

	private void processCancelDataRecordPacket(GTPPrimeServiceRequest gtpPrimeRequest,GTPPrimeServiceResponse gtpPrimeResponse, EliteFileStream fileStream, int counter){
		int responseLength = 0;
		BaseGTPPrimeElement toSendElement;
		List<BaseGTPPrimeElement> receivedIEList = null ;
		receivedIEList = gtpPrimeRequest.getElementList();
		List<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>(); 
		
		GTPPrimeClientData client = gtpPrimeServiceConfiguration.getClient(gtpPrimeRequest.getSourceAddress());
		try{
			Sequence sequence = (Sequence)receivedIEList.get(counter);


			if ( sequence == null || (!(sequence.getElementType() == GTPPrimeElementTypeConstants.SEQUENCE_NUMBER_OF_CANCELLED_PACKETS.typeID))){
				byte[] value = new byte[]{(byte) OPTIONAL_IE_INCORRECT};
				toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
				responseLength += 2;
				toSendIEList.add(toSendElement);

				toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
				responseLength += 3;
				toSendIEList.add(toSendElement);
				gtpPrimeResponse.setFailure();
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Optional IE value incorrect in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
					LogManager.getLogger().info(MODULE, "Expected - Sequence no of cancel CDRs");
				}
				return;
			}

			String clientIP = client.getClientIP();

			Map<Integer, GTPPrimeDataRecordPacket> tmpMap = clientCDRMap.get(clientIP);

			int seqNumber[] = (int[]) sequence.getSequence();
			int seqNumberLength = seqNumber.length;
			for ( int j=0 ; j<seqNumberLength ; j++){
				if (tmpMap.get(seqNumber[j]) == null){
					responseLength = 0;
					toSendIEList.clear();
					byte[] value = new byte[]{(byte) SEQ_NUM_OF_RELEASED_OR_CANCELLED_PACKETS_IE_INCORRECT};
					toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
					responseLength += 2;
					toSendIEList.add(toSendElement);

					toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
					responseLength += 3;
					toSendIEList.add(toSendElement);
					gtpPrimeResponse.setFailure();
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "CDRs not found in memory for request:" + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
					}
					return;
				}
			}

			synchronized (tmpMap) {
				for ( int j=0 ; j<seqNumberLength ; j++){
					tmpMap.remove(seqNumber[j]);
				}	
			}

			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Removed duplicate records from memory" + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}


			// TODO: sequence numbers processing
			byte[] value = new byte[]{(byte) REQUESTS_ACCEPTED};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);


			int seqNum = gtpPrimeRequest.getSeqNumber();
			int temp = 0;
			byte[] value1 = new byte[2];
			temp = seqNum;
			temp = temp >> 8;
				value1[0] = (byte) (temp & 0xff);
				value1[1] = (byte) (seqNum & 0xff);
				toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 2, value1);
				responseLength += 5;
				toSendIEList.add(toSendElement);
		} catch (ClassCastException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) MANDATORY_IE_INCORRECT};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Mandatory IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0, "Malformed Packet Received. Sending NACK");
		} catch (IndexOutOfBoundsException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) MANDATORY_IE_MISSING};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Mandatory IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0, "Malformed Packet Received. Sending NACK");
		} catch (Exception e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) SYSTEM_FAILURE};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Exception. Sending request not fulfiiled cause. Reason: " + e.getMessage());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Error in File input output. Sending NACK", 0, "Error in File input output. Sending NACK");
		} finally{
			finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Data record transfer response created successfully of cancel data record packet");
		}
	}

	private void processReleaseDataRecordPacket(GTPPrimeServiceRequest gtpPrimeRequest,GTPPrimeServiceResponse gtpPrimeResponse, EliteFileStream fileStream, int counter){
		int responseLength = 0;
		BaseGTPPrimeElement toSendElement;
		List<BaseGTPPrimeElement> receivedIEList = null ;
		receivedIEList = gtpPrimeRequest.getElementList();
		List<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>(); 
		
		GTPPrimeClientData client = gtpPrimeServiceConfiguration.getClient(gtpPrimeRequest.getSourceAddress());
		try{
			Sequence sequence = (Sequence)receivedIEList.get(counter);

			if ( sequence == null || (!(sequence.getElementType() == GTPPrimeElementTypeConstants.SEQUENCE_NUMBER_OF_REALSED_PACKETS.typeID))){
				byte[] value = new byte[]{(byte) OPTIONAL_IE_INCORRECT};
				toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
				responseLength += 2;
				toSendIEList.add(toSendElement);
				gtpPrimeResponse.setFailure();

				toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
				responseLength += 3;
				toSendIEList.add(toSendElement);

				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Optional IE value incorrect in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
					LogManager.getLogger().info(MODULE, "Expected - Sequence no of release CDRs");
				}
				return;
			}

			String clientIP = client.getClientIP();
			Map<Integer, GTPPrimeDataRecordPacket> tmpMap = null;

			tmpMap = clientCDRMap.get(clientIP);
			GTPPrimeDataRecordPacket cdrPacket = null;
			
			int seqNumber[] = (int[]) sequence.getSequence();
			int seqNumberLength = seqNumber.length;
			for ( int j=0 ; j<seqNumberLength ; j++){
				if (tmpMap.get(seqNumber[j]) == null){
					responseLength = 0;
					toSendIEList.clear();
					byte[] value = new byte[]{(byte) SEQ_NUM_OF_RELEASED_OR_CANCELLED_PACKETS_IE_INCORRECT};
					toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
					responseLength += 2;
					toSendIEList.add(toSendElement);

					toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
					responseLength += 3;
					toSendIEList.add(toSendElement);
					gtpPrimeResponse.setFailure();
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "CDRs not found in memory for request:" + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
					}
					return;
				}
			}

			for ( int j=0 ; j<seqNumberLength ; j++){

				synchronized (tmpMap) {
					cdrPacket = tmpMap.remove(seqNumber[j]);	
				}
				List<GTPPrimeCDRData> dataRecordList = cdrPacket.getCDRList();
				int listSize = dataRecordList.size();
				for ( int i=0 ; i<listSize ; i++){
					fileStream.appendRecord(dataRecordList.get(i).getValueByte());
				}
			}
			if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
				LogManager.getLogger().info(MODULE, "Released duplicate records from file" + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}

			// TODO: sequence numbers processing
			byte[] value = new byte[]{(byte) REQUESTS_ACCEPTED};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);

			int seqNum = gtpPrimeRequest.getSeqNumber();
			byte[] value1 = new byte[2];
			int temp = 0;
			temp = seqNum;
			temp = temp >> 8;
			value1[0] = (byte) (temp & 0xff);
			value1[1] = (byte) (seqNum & 0xff);
			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 2, value1);
			responseLength += 5;
			toSendIEList.add(toSendElement);
		} catch (ClassCastException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) MANDATORY_IE_INCORRECT};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Mandatory IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0, "Error in File input output. Sending NACK");
			gtpPrimeResponse.setFailure();
		} catch (IndexOutOfBoundsException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) MANDATORY_IE_MISSING};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Mandatory IE missing in request: " + gtpPrimeRequest.getSeqNumber() + " From: " + gtpPrimeRequest.getSourceAddress());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.WARN, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet Received. Sending NACK", 0, "Malformed Packet Received. Sending NACK");
		} catch (IOException e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) SYSTEM_FAILURE};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Error in writing cdrs to file. Sending request not fulfiiled cause.");
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Error in File input output. Sending NACK", 0, "Error in File input output. Sending NACK");
			gtpPrimeResponse.setFailure();
		} catch (Exception e) {
			responseLength = 0;
			toSendIEList.clear();
			byte[] value = new byte[]{(byte) SYSTEM_FAILURE};
			toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength += 2;
			toSendIEList.add(toSendElement);
			gtpPrimeResponse.setFailure();

			toSendElement = createElement(new Sequence(), GTPPrimeElementTypeConstants.REQUESTS_RESPONDED.typeID, 0, null);
			responseLength += 3;
			toSendIEList.add(toSendElement);
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Exception. Sending request not fulfiiled cause. Reason: " + e.getMessage());
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Error in File input output. Sending NACK", 0, "Error in File input output. Sending NACK");
		} finally{
			finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse , GTPPrimeConstants.DATA_RECORD_TRANSFER_RESPONSE.typeID);
		}
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Data record transfer response created successfully of release data record packet");
		}
	}

	private void finalProcessingForResponse( int responseLength, List<BaseGTPPrimeElement> toSendIEList, GTPPrimeServiceRequest req,GTPPrimeServiceResponse res, int messageType){

		res.setMessageType(messageType);
		res.setPayloadLength(responseLength);
		res.setSeqNumber(req.getSeqNumber());
		res.setElementList(toSendIEList);
	}

	private void gtpPrimeRedirectionRequest(GTPPrimeServiceRequest gtpPrimeRequest,GTPPrimeServiceResponse gtpPrimeResponse) {
		int ElementType=0;
		int counter=0;
		int responseLength = 0;
		List<BaseGTPPrimeElement> receivedIEList = null ;
		receivedIEList = gtpPrimeRequest.getElementList();
		List<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>(); 
		try{
			receivedIEList = gtpPrimeRequest.getElementList();
			if (receivedIEList == null){
				byte[] value =new byte[]{(byte)MANDATORY_IE_MISSING};
				Octet cause = (Octet) createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
				responseLength +=2;
				toSendIEList.add(cause);
				gtpPrimeResponse.setFailure();
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Mandatory IE not found in Redirection request");
				}
				return;
			}

			Octet octet = (Octet)receivedIEList.get(counter);
			ElementType = octet.getElementType();
			if (ElementType == GTPPrimeElementTypeConstants.CAUSE.typeID){
				int ElementValue = octet.getOctet();
				if (Dictionary.getInstance().getSupportedValueMap(ElementType).containsKey(ElementValue)){
					counter++;
					IpAddress address = (IpAddress)(receivedIEList.get(counter));
					byte[] value =new byte[]{(byte)REQUESTS_ACCEPTED};
					Octet cause = (Octet) createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
					responseLength +=2;
					toSendIEList.add(cause);
					if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
						LogManager.getLogger().info(MODULE, "Redirection IP: " + InetAddress.getByAddress(address.getValueByte()).toString());
					}
				} else{
					byte[] value =new byte[]{(byte)MANDATORY_IE_INCORRECT};
					Octet cause = (Octet) createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
					responseLength +=2;
					toSendIEList.add(cause);
					gtpPrimeResponse.setFailure();
					if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
						LogManager.getLogger().debug(MODULE, "Mandatory IE found incorrect in Redirection request");
					}
				}
			}else{
				byte[] value =new byte[]{(byte)MANDATORY_IE_MISSING};
				Octet cause = (Octet) createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
				responseLength +=2;
				toSendIEList.add(cause);
				gtpPrimeResponse.setFailure();
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Mandatory IE not found in Redirection request");
				}
			}
		} catch (IndexOutOfBoundsException exp){
			responseLength = 0;
			toSendIEList.clear();
			byte[] value =new byte[]{(byte)MANDATORY_IE_MISSING};
			Octet cause = (Octet) createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength +=2;
			toSendIEList.add(cause);
			gtpPrimeResponse.setFailure();
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Mandatory IE not found in redirection request");
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet received. Sending NACK", 0, "Malformed Packet received. Sending NACK");
		}catch (ClassCastException exp){
			responseLength = 0;
			toSendIEList.clear();
			byte[] value =new byte[]{(byte)MANDATORY_IE_INCORRECT};
			Octet cause = (Octet) createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength +=2;
			toSendIEList.add(cause);
			gtpPrimeResponse.setFailure();
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Mandatory IE found incorrect in redirection request");
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet received. Sending NACK", 0, "Malformed Packet received. Sending NACK");
		} catch (Exception e){
			responseLength = 0;
			toSendIEList.clear();
			byte[] value =new byte[]{(byte)MANDATORY_IE_MISSING};
			Octet cause = (Octet) createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
			responseLength +=2;
			toSendIEList.add(cause);
			gtpPrimeResponse.setFailure();
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Mandatory IE not found in redirection request");
			}
			gtpServiceContext.getServerContext().generateSystemAlert(AlertSeverity.ERROR, Alerts.OTHER_GENERIC, 
					MODULE, "Malformed Packet received. Sending NACK", 0, "Malformed Packet received. Sending NACK");
		} finally {
			finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse, GTPPrimeConstants.REDIRECTION_RESPONSE.typeID);
		}

	}

	private void gtpPrimeNodeAliveRequest(GTPPrimeServiceRequest gtpPrimeRequest,GTPPrimeServiceResponse gtpPrimeResponse) {
		final int responseLength = 0;
		finalProcessingForResponse(responseLength, null, gtpPrimeRequest, gtpPrimeResponse, GTPPrimeConstants.NODE_ALIVE_RESPONSE.typeID);
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Node alive response created successfully");
		}
	}

	private void gtpPrimeVersionNotSupported( GTPPrimeServiceRequest gtpPrimeRequest,	GTPPrimeServiceResponse gtpPrimeResponse) {
		final int responseLength = 0;
		List<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>(); 
		gtpPrimeResponse.setVersion(VERSION_SUPPORTED);

		finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse, GTPPrimeConstants.VERSION_NOT_SUPPORTED.typeID);
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Version not supported response created successfully");
		}
	}

	private void gtpPrimeEchoRequest(GTPPrimeServiceRequest gtpPrimeRequest,GTPPrimeServiceResponse gtpPrimeResponse) {
		int responseLength = 0;
		BaseGTPPrimeElement toSendElement;
		List<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>(); 
		byte[] value = {1};
		toSendElement = createElement(new Octet(), GTPPrimeElementTypeConstants.RECOVERY.typeID , -1 , value);
		responseLength += 2;
		toSendIEList.add(toSendElement);

		finalProcessingForResponse(responseLength, toSendIEList, gtpPrimeRequest, gtpPrimeResponse, GTPPrimeConstants.ECHO_RESPONSE.typeID);
		if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
			LogManager.getLogger().info(MODULE, "Echo response created successfully");
		}
	}

	private BaseGTPPrimeElement createElement(BaseGTPPrimeElement element, int type, int length ,byte[] value) {
		element.setType(type);
		if (length != -1){
			element.setLength(length);	
		}
		if (value != null){
			element.setValueByte(value);
		}
		return element;
	}

	@Override
	public boolean isEligible(GTPPrimeServiceRequest request, GTPPrimeServiceResponse response) {
		return true;
	}

	@Override
	public void init() throws InitializationFailedException {
		if (gtpPrimeServiceConfiguration.getClientList() == null){
			if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
				LogManager.getLogger().error(MODULE, "Client configuration is null so handler will not be initialized");
			}
			return;
		}
		
		List<GTPPrimeClientDataImpl> clientList = gtpPrimeServiceConfiguration.getClientList();
		String clientIP;
		clientCDRMap = new HashMap<String, HashMap<Integer, GTPPrimeDataRecordPacket>>();

		for ( int i=0 ; i<clientList.size() ; i++ ){
			clientIP = clientList.get(i).getClientIP();
			clientCDRMap.put(clientIP, new HashMap<Integer, GTPPrimeDataRecordPacket>());
		}
	}

	@Override
	public void stop() {
		Iterator<String> iterator = fileStreams.keySet().iterator();
		String key = null;
		EliteFileStream eliteFileStream = null;
		while (iterator.hasNext()){
			key = iterator.next();
			eliteFileStream = fileStreams.get(key);
			if (eliteFileStream != null){
				eliteFileStream.flush();
				eliteFileStream.close();
			}

		}

		List<GTPPrimeClientDataImpl> clientList = new ArrayList<GTPPrimeClientDataImpl>();
		clientList = gtpPrimeServiceConfiguration.getClientList();
		int clientListSize = clientList.size();
		for ( int i=0 ; i<clientListSize ; i++){
			if (clientList.get(i).getRedirectionIP() != null)
				sendRedirectionRequest(clientList.get(i));
		}
	}

	private void sendRedirectionRequest(GTPPrimeClientData client){

		GTPPrimePacketV1 sendGTPPacket = new GTPPrimePacketV1();
		GTPPrimePacketV1 receiveGTPPacket = new GTPPrimePacketV1();
		int sequenceNumber = 3;
		int localRetryCounter = 0;
		int clientRetryCounter = client.getRequestRetry();
		List<BaseGTPPrimeElement> toSendIEList = new ArrayList<BaseGTPPrimeElement>();

		sendGTPPacket.setFirstByte(createFirstByte());
		sendGTPPacket.setMessageType(GTPPrimeConstants.REDIRECTION_REQUEST.typeID);
		sendGTPPacket.setPayloadLength(9);
		sendGTPPacket.setSeqNumber(sequenceNumber);

		byte[] value = new byte[]{(byte)(THIS_NODE_IS_ABOUT_TO_GO_DOWN)};
		Octet octet = (Octet) createElement(new Octet(), GTPPrimeElementTypeConstants.CAUSE.typeID, -1, value);
		toSendIEList.add(octet);

		IpAddress ipAddress = new IpAddress();
		ipAddress.setType(GTPPrimeElementTypeConstants.ADDRESS_OF_RECOMMENDED_NODE.typeID);


		try {
			InetAddress addr = InetAddress.getByName(client.getRedirectionIP());
			int length;
			if (addr.getClass().equals(Inet6Address.class)){
				length = 16;
			} else {
				length = 4;
			}
			ipAddress.setAddress(addr);
			ipAddress.setLength(length);
		} catch (UnknownHostException e1) {
			LogManager.getLogger().error(MODULE, "Redirection IP is invalid of client: " + client.getClientIP());
			return;
		}
		toSendIEList.add(ipAddress);

		sendGTPPacket.setElementList(toSendIEList);

		while (localRetryCounter < clientRetryCounter){
			try {
				String clientIP = client.getClientIP();
				if (clientIP.equals("0.0.0.0")){
					clientIP = InetAddress.getLocalHost().getHostAddress();
					LogManager.getLogger().warn(MODULE, "Configured client IP is 0.0.0.0 so considering IP: " + clientIP + " to send Redirection request");
				}
				
				byte[] rawBytes = sendGTPPacket.getBytes();
				DatagramSocket socket = new DatagramSocket(0, InetAddress.getByName(gtpServiceContext.getGTPPrimeConfiguration().getSocketDetails().get(0).getIPAddress())); //NOSONAR - Reason: Resources should be closed
				DatagramPacket sendDatagramPacket = new DatagramPacket(rawBytes , rawBytes.length ,  InetAddress.getByName(clientIP), client.getClientPort());
				if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
					LogManager.getLogger().info(MODULE, "Sending Redirection request to: " + clientIP +":"+ client.getClientPort());
				}
				socket.send(sendDatagramPacket);
				localRetryCounter++;

				socket.setSoTimeout((int) client.getRequestExpiryTime());

				rawBytes = new byte[1024];
				DatagramPacket receiveDatagramPacket = new DatagramPacket(rawBytes , rawBytes.length) ;
				socket.receive(receiveDatagramPacket);
				receiveGTPPacket.setBytes(rawBytes);

				if ( receiveGTPPacket.getMessageType() == GTPPrimeConstants.REDIRECTION_RESPONSE.typeID){
					if (receiveGTPPacket.getSeqNumber() == sequenceNumber){
						if (LogManager.getLogger().isLogLevel(LogLevel.INFO)){
							LogManager.getLogger().info(MODULE, "Redirection response Received from: " + receiveDatagramPacket.getAddress().getHostAddress());
						}
						return;
					}
				}

			} catch (SocketTimeoutException e) {
				if (!(localRetryCounter < clientRetryCounter)){
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN)){
						LogManager.getLogger().warn(MODULE, "Redirection response not arrived from: " + client.getClientIP() +":"+client.getClientPort());
					}
					return;
				} 
			} catch (SocketException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error in Socket binding. Reason: " + e.getMessage());
				}
			} catch (UnknownHostException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
					LogManager.getLogger().debug(MODULE, "Clietn IP invalid. Reason: " + e.getMessage());
				}
			} catch (IOException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Error occured in sending or receiving datagram packet or in Parsing packet. Reason: " + e.getMessage());
				}
			} catch (ParseException e) {
				if (LogManager.getLogger().isLogLevel(LogLevel.ERROR)){
					LogManager.getLogger().error(MODULE, "Parsing error in response received from: " + client.getClientIP() + " Reason: "+ e.getMessage());
				} 
			}
		}
	}

	private byte createFirstByte() {
		int gtpPrimeVersion = VERSION_SUPPORTED;
		int protocolType = 0;
		int spareBits = 7;
		int headerType = 0;
		gtpPrimeVersion = gtpPrimeVersion << 5;
		protocolType = protocolType << 4;
		spareBits = spareBits << 1;
		return new Byte(new Integer(gtpPrimeVersion | protocolType | spareBits | headerType).byteValue());
	}

	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}