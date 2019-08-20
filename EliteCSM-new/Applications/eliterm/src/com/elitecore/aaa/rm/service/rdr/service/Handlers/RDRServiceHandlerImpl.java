package com.elitecore.aaa.rm.service.rdr.service.Handlers;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.elitecore.aaa.rm.data.RDRClientData;
import com.elitecore.aaa.rm.data.RDRClientDataImpl;
import com.elitecore.aaa.rm.service.rdr.service.RDRServiceContext;
import com.elitecore.aaa.rm.service.rdr.service.RDRServiceRequest;
import com.elitecore.aaa.rm.service.rdr.tlv.BaseRDRTLV;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.fileio.EliteFileStream;
import com.elitecore.core.commons.fileio.EliteFileWriter;
import com.elitecore.core.commons.fileio.EliteFileWriter.Builder;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;

/**
 * @author nitul.kukadia
 *
 */
public class RDRServiceHandlerImpl implements RDRServiceHandler {

	private static final String MODULE = "RDR-Handler";
	private static final String INP_EXTENSION = "inp";

	private HashMap<String, EliteFileStream> fileStreams;
	private HashMap<String,EliteFileWriter> fileAppender;
	private RDRServiceContext rdrServiceContext;

	public RDRServiceHandlerImpl(RDRServiceContext rdrServiceContext) {
		this.rdrServiceContext=rdrServiceContext;
	}
	@Override
	public void init() throws InitializationFailedException {
		List<RDRClientDataImpl> clientList = rdrServiceContext.getRDRConfiguration().getRDRClients();
		fileStreams = new HashMap<String, EliteFileStream>();
		fileAppender=new HashMap<String,EliteFileWriter>();
		String clientIP;
		RDRClientData clientData;
		for ( int i=0 ; i<clientList.size() ; i++ ){
			clientData=clientList.get(i);
			clientIP = clientData.getClientIP();
			String fileLocation = clientData.getFileLocation() + File.separator + clientData.getClientIP();
			try {
				//fileStreams.put(clientIP,new EliteFileStream(clientData.getFileName(), "csv",fileLocation, clientData.getRollingType(), clientData.getRollingUnit(), false));
				EliteFileWriter fileWriter = new Builder()
						.fileHeader(clientData.getFileName())
						.activeFileExt("csv")
						.destinationPath(fileLocation)
						.rollingType(clientData.getRollingType())
						.rollingUnit(clientData.getRollingUnit())
						.taskScheduler(rdrServiceContext.getServerContext().getTaskScheduler())
						.build();
				fileAppender.put(clientIP, 
						fileWriter);
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}	
	
	@Override
	public void reInit() throws InitializationFailedException {
		
	}

	@Override
	public void handleRequest(ServiceRequest request, ServiceResponse response,
			ISession session) {
		RDRServiceRequest rdrRequest=(RDRServiceRequest)request;
		try {
			System.out.print("RDR Packet "+request);
			//System.out.print("Request Addr"+rdrRequest.getSourceAddress());		
			//new RdrDetailLocalDriver(this.rdrServiceContext, 40).handleAccountingRequest(request);
			//EliteFileStream fileStream=fileStreams.get(rdrRequest.getSourceAddress().getHostAddress());
			HashMap<Integer, BaseRDRTLV> fields=rdrRequest.getFields();
			
			EliteFileWriter fileWriter=fileAppender.get(rdrRequest.getSourceAddress().getHostAddress());
			fileWriter.open();
			
			
			/*RDRPacketImpl requestPacket=rdrRequest.getRequestPacket();
			fileWriter.appendRecord(requestPacket.getSourceIP());
			fileWriter.appendRecord(requestPacket.getSourcePort());
			fileWriter.appendRecord(requestPacket.getDestinationIP());
			fileWriter.appendRecord(requestPacket.getDestinationPort());
			fileWriter.appendRecord(requestPacket.getTrafficProcessorID());
			fileWriter.appendRecord(requestPacket.getRdrLength());
			fileWriter.appendRecord(requestPacket.getFlowContextID());
			fileWriter.appendRecord(requestPacket.getFloatTag());
			fileWriter.appendRecord(requestPacket.getNoOfFields());*/
			
			//fileWriter.appendRecord(rdrRequest.getRequestPacket());
			for(Map.Entry<Integer, BaseRDRTLV> entry : fields.entrySet()){
				BaseRDRTLV tlv=entry.getValue();
				fileWriter.appendRecord(tlv.toString()+",");
			}
			//fileWriter.appendRecord("\n");
			fileWriter.appendRecordln("");
			fileWriter.flush();
			/*fileStream.open();
			fileStream.appendRecord(rdrRequest.getRequestBytes());*/
		} catch (Exception e1) {
			e1.printStackTrace();
		}
	}
	
	public void appendHeader(RDRServiceRequest rdrRequest){
		
	}

	@Override
	public void stop() {
		Iterator<String> iterator = fileAppender.keySet().iterator();
		String key = null;
		EliteFileWriter eliteFileWriter = null;
		while (iterator.hasNext()){
			key = iterator.next();
			eliteFileWriter = fileAppender.get(key);
			if (eliteFileWriter != null){
				eliteFileWriter.flush();
				eliteFileWriter.close();
			}
		}
	}
	@Override
	public boolean isEligible(ServiceRequest request, ServiceResponse response) {
		return true;
	}
	@Override
	public boolean isResponseBehaviorApplicable() {
		return false;
	}
}
