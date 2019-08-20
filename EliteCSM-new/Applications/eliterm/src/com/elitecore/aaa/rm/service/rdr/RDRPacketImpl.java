/**
 * 
 */
package com.elitecore.aaa.rm.service.rdr;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import com.elitecore.aaa.rm.service.rdr.tlv.BaseRDRTLV;
import com.elitecore.aaa.rm.service.rdr.type.AnonymizedHTTPTransactionUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.AttackEndRDR;
import com.elitecore.aaa.rm.service.rdr.type.AttackStartRDR;
import com.elitecore.aaa.rm.service.rdr.type.BlockingRDR;
import com.elitecore.aaa.rm.service.rdr.type.DHCPRDR;
import com.elitecore.aaa.rm.service.rdr.type.FlowEndRDR;
import com.elitecore.aaa.rm.service.rdr.type.FlowStartRDR;
import com.elitecore.aaa.rm.service.rdr.type.GenericUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.HTTPTransactionUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.LinkUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.MaliciousTrafficPeriodicRDR;
import com.elitecore.aaa.rm.service.rdr.type.MediaFlowRDR;
import com.elitecore.aaa.rm.service.rdr.type.OngoingFlowRDR;
import com.elitecore.aaa.rm.service.rdr.type.PackageUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.QuotaBreachRDR;
import com.elitecore.aaa.rm.service.rdr.type.QuotaStatusRDR;
import com.elitecore.aaa.rm.service.rdr.type.QuotaThresholdBreachRDR;
import com.elitecore.aaa.rm.service.rdr.type.RADIUSRDR;
import com.elitecore.aaa.rm.service.rdr.type.RTSPTransactionUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.RealTimeSubscriberUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.SessionCreationRDR;
import com.elitecore.aaa.rm.service.rdr.type.SpamRDR;
import com.elitecore.aaa.rm.service.rdr.type.SubscriberUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.TransactionRDR;
import com.elitecore.aaa.rm.service.rdr.type.TransactionUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.VideoTransactionUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.VirtualLinksUsageRDR;
import com.elitecore.aaa.rm.service.rdr.type.VoIPTransactionUsageRDR;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.attributes.IPacketAttribute;
/**
 * @author nitul.kukadia
 *
 */
public class RDRPacketImpl extends BaseRDRPacket{

	private static final long serialVersionUID = 1L;
	private String clientIP;
	private int clientPort;
	private int trafficProcessorID;	
	private int rdrLength;
	private int sourceIP;
	private int destinationIP;
	private int sourcePort;
	private int destinationPort;
	private int flowContextID;
	private int rdrTag;
	private int noOfFields;
	private BaseRDR baseRDR;
	
	public RDRPacketImpl() {
	}

	public void setBytes(byte[] value) {
		ByteArrayInputStream in = new ByteArrayInputStream(value);
		try {
			readFrom(in);
		}catch(IOException ioExp){
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))			
			this.trafficProcessorID = 0;
			this.rdrLength= 0;
			this.sourceIP= 0;
			this.sourcePort =0;
			this.destinationIP = 0;
			this.destinationPort= 0;
			this.flowContextID= 0;
			this.rdrTag=0;
			this.noOfFields =0;
		}
	}

	@Override
	public int readFrom(InputStream sourceStream) throws IOException {
		int noOfBytesRead=0;
		trafficProcessorID=sourceStream.read();
		noOfBytesRead++;
		noOfBytesRead=noOfBytesRead+getNumber(sourceStream);
		sourceIP=sourceStream.read();
		noOfBytesRead++;
		destinationIP=sourceStream.read();
		noOfBytesRead++;
		sourcePort = sourceStream.read();
		noOfBytesRead++;
		sourcePort = (sourcePort << 8) | (sourceStream.read() & 0xFF);
		noOfBytesRead++;
		destinationPort = sourceStream.read();
		noOfBytesRead++;
		destinationPort = (destinationPort << 8) | (sourceStream.read() & 0xFF);
		noOfBytesRead++;
		noOfBytesRead=noOfBytesRead+getFcID(sourceStream);		
		byte []tag=new byte[4];
		tag[0]=(byte) sourceStream.read();
		noOfBytesRead++;
		tag[1]=(byte) sourceStream.read();
		noOfBytesRead++;
		tag[2]=(byte) sourceStream.read();
		noOfBytesRead++;
		tag[3]=(byte) sourceStream.read();
		noOfBytesRead++;
		rdrTag=getTagFromByteArray(tag);
		noOfFields=sourceStream.read();
		noOfBytesRead++;
		
		baseRDR=getRDR(getFloatTag());
		try{
			baseRDR.read(sourceStream);
		}catch (Exception e) {
			e.printStackTrace();
		}
		return noOfBytesRead;
	}
	
	public BaseRDR getRDR(long rdrTag){
		if(rdrTag==RDRTagConstants.TransactionRDR.tagID){
			return new TransactionRDR();
		}else if(rdrTag==RDRTagConstants.AnonymizedHTTPTransactionUsageRDR.tagID){
			return new AnonymizedHTTPTransactionUsageRDR();
		}else if(rdrTag==RDRTagConstants.AttackEndRDR.tagID){
			return new AttackEndRDR();
		}else if(rdrTag==RDRTagConstants.AttackStartRDR.tagID){
			return new AttackStartRDR();
		}else if(rdrTag==RDRTagConstants.BlockingRDR.tagID){
			return new BlockingRDR();
		}else if(rdrTag==RDRTagConstants.DHCPRDR.tagID){
			return new DHCPRDR();
		}else if(rdrTag==RDRTagConstants.FlowEndRDR.tagID){
			return new FlowEndRDR();
		}else if(rdrTag==RDRTagConstants.FlowStartRDR.tagID){
			return new FlowStartRDR();
		}else if(rdrTag==RDRTagConstants.GenericUsageRDR.tagID){
			return new GenericUsageRDR();
		}else if(rdrTag==RDRTagConstants.HTTPTransactionUsageRDR.tagID){
			return new HTTPTransactionUsageRDR();
		}else if(rdrTag==RDRTagConstants.LinkUsageRDR.tagID){
			return new LinkUsageRDR();
		}else if(rdrTag==RDRTagConstants.MaliciousTrafficPeriodicRDR.tagID){
			return new MaliciousTrafficPeriodicRDR();
		}else if(rdrTag==RDRTagConstants.MediaFlowRDR.tagID){
			return new MediaFlowRDR();
		}else if(rdrTag==RDRTagConstants.OngoingFlowRDR.tagID){
			return new OngoingFlowRDR();
		}else if(rdrTag==RDRTagConstants.PackageUsageRDR.tagID){
			return new PackageUsageRDR();
		}else if(rdrTag==RDRTagConstants.QuotaBreachRDR.tagID){
			return new QuotaBreachRDR();
		}else if(rdrTag==RDRTagConstants.QuotaStatusRDR.tagID){
			return new QuotaStatusRDR();
		}else if(rdrTag==RDRTagConstants.QuotaThresholdBreachRDR.tagID){
			return new QuotaThresholdBreachRDR();
		}else if(rdrTag==RDRTagConstants.RADIUSRDR.tagID){
			return new RADIUSRDR();
		}else if(rdrTag==RDRTagConstants.RealTimeSubscriberUsageRDR.tagID){
			return new RealTimeSubscriberUsageRDR();
		}else if(rdrTag==RDRTagConstants.RTSPTransactionUsageRDR.tagID){
			return new RTSPTransactionUsageRDR();
		}else if(rdrTag==RDRTagConstants.SessionCreationRDR.tagID){
			return new SessionCreationRDR();
		}else if(rdrTag==RDRTagConstants.SpamRDR.tagID){
			return new SpamRDR();
		}else if(rdrTag==RDRTagConstants.SubscriberUsageRDR.tagID){
			return new SubscriberUsageRDR();
		}else if(rdrTag==RDRTagConstants.TransactionRDR.tagID){
			return new TransactionRDR();
		}else if(rdrTag==RDRTagConstants.TransactionUsageRDR.tagID){
			return new TransactionUsageRDR();
		}else if(rdrTag==RDRTagConstants.VideoTransactionUsageRDR.tagID){
			return new VideoTransactionUsageRDR();
		}else if(rdrTag==RDRTagConstants.VirtualLinksUsageRDR.tagID){
			return new VirtualLinksUsageRDR();
		}else if(rdrTag==RDRTagConstants.VoIPTransactionUsageRDR.tagID){
			return new VoIPTransactionUsageRDR();
		}else{
			return null;
		}
	}
	
	public int getTagFromByteArray(byte[] data){
        return(   (data[0]   << 24 & 0xff000000)
                  | (data[1] << 16 & 0x00ff0000)
                  | (data[2] <<  8 & 0x0000ff00)
                  | (data[3]       & 0x000000ff) );
    }
	
	public long getFloatTag(){
		return ((long) (rdrTag & 0x7FFFFFFF)) | ((rdrTag & 0x80000000L));
	}
	
	public int getNumber(InputStream in){
		int intvalue=0,temp=0,bytesread=0;
		for(int i=0;i<4;i++){
			try {
				temp=in.read();
				bytesread++;			
			} catch (IOException e) {
				e.printStackTrace();
			}
			if(48<=temp && temp<=57){
				temp=temp-48; intvalue=intvalue*10+temp;
				temp=0;
			}
		}
		rdrLength=intvalue;
		return bytesread;		
	}
	
	public int getFcID(InputStream in){
		int intvalue=0,temp=0,bytesread=0;
		for(int i=0;i<4;i++){
			try {
				temp=in.read();
				bytesread++;
			} catch (IOException e) {
				e.printStackTrace();
			}		
			intvalue=intvalue*10+temp; 
			temp=0;
			}		
		flowContextID=intvalue;
		return bytesread;		
	}
	
	public String  printPacketFields(HashMap<Integer, BaseRDRTLV> map){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		  Set set = map.entrySet();
		  java.util.Iterator i = set.iterator();
		  out.println();
		  out.println("\t\t\t---RDR Fields---");
		  
		  while (i.hasNext()) {
			  Map.Entry me = (Map.Entry) i.next();
			  out.print("\t"+me.getValue()+" ");
		  }
		  return stringBuffer.toString();
	}
	
	public HashMap<Integer, BaseRDRTLV> getField(){
		return baseRDR.getField();
	}
	
	public String toString(){
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter( stringBuffer);
		out.println();
		out.println("\t\t\t---RDR HEADER---");
		out.println("\t" + "trafficProcessorID : " +trafficProcessorID);
		out.println("\t" + "rdrLength          : " + rdrLength);
		out.println("\t" + "sourceIP           : " +sourceIP);
		out.println("\t" + "destinationIP      : " +destinationIP);
		out.println("\t" + "sourcePort         : " +sourcePort);
		out.println("\t" + "destinationPort    : " +destinationPort);
		out.println("\t" + "flowContextID      : " +flowContextID);
		out.println("\t" + "rdrTag             : " +getFloatTag());
		out.println("\t" + "noOfFields         : " +noOfFields);
		out.println(printPacketFields(baseRDR.getField()));
		printPacketFields(baseRDR.getField());
		out.flush();
		out.close();
		return stringBuffer.toString();
	}
	
	public String getClientIP() {		
		return this.clientIP;
	}

	public void setClientPort(int clientPort) {
		this.clientPort=clientPort;
	}
	
	@Override
	public void writeTo(OutputStream destinationStream) throws IOException {
	}

	@Override
	public byte[] getBytes() {
		return null;
	}

	@Override
	public Collection getAttributes() {
		return null;
	}

	@Override
	public IPacketAttribute getAttribute(int id) {
		return null;
	}

	@Override
	public IPacketAttribute getAttribute(String id) {
		return null;
	}

	@Override
	public int getLength() {
		return 0;
	}

	@Override
	public int readFrom(Reader sourceReader) throws IOException {
		return 0;
	}

	@Override
	public void writeTo(Writer destinationWriter) throws IOException {
		
	}

	@Override
	public long creationTimeMillis() {
		return 0;
	}

	public void setClientIP(String hostAddress) {
		this.clientIP=hostAddress;		
	}

	public int getTrafficProcessorID() {
		return trafficProcessorID;
	}

	public void setTrafficProcessorID(int trafficProcessorID) {
		this.trafficProcessorID = trafficProcessorID;
	}

	public int getRdrLength() {
		return rdrLength;
	}

	public void setRdrLength(int rdrLength) {
		this.rdrLength = rdrLength;
	}

	public int getSourceIP() {
		return sourceIP;
	}

	public void setSourceIP(int sourceIP) {
		this.sourceIP = sourceIP;
	}

	public int getDestinationIP() {
		return destinationIP;
	}

	public void setDestinationIP(int destinationIP) {
		this.destinationIP = destinationIP;
	}

	public int getSourcePort() {
		return sourcePort;
	}

	public void setSourcePort(int sourcePort) {
		this.sourcePort = sourcePort;
	}

	public int getDestinationPort() {
		return destinationPort;
	}

	public void setDestinationPort(int destinationPort) {
		this.destinationPort = destinationPort;
	}

	public int getFlowContextID() {
		return flowContextID;
	}

	public void setFlowContextID(int flowContextID) {
		this.flowContextID = flowContextID;
	}

	public int getRdrTag() {
		return rdrTag;
	}

	public void setRdrTag(int rdrTag) {
		this.rdrTag = rdrTag;
	}

	public int getNoOfFields() {
		return noOfFields;
	}

	public void setNoOfFields(int noOfFields) {
		this.noOfFields = noOfFields;
	}
}
