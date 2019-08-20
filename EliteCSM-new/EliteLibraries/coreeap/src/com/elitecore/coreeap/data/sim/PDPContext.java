package com.elitecore.coreeap.data.sim;

import java.io.PrintWriter;
import java.io.StringWriter;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.util.Utility;

public class PDPContext {
	public final static String MODULE = "PDP-CNTXT";
	
	private int pdp_ContextId; // To be used by anded with 0XFFFF as it is unsigned 16 bit value
	private byte[] pdp_Type;
	private short pdp_Type_Org;
	private String pdp_Address;
	private QoSSubscribed qoSSubscribed;
	private char vplmnAddressAllowed;
	private String ulcm_APN;
	private ExtQoSSubscribed extQoSSubscribed;
	private short ulcm_ChargingCharacteristics;
	private String ext2_QoS_Subscribed;
	
	public int getPdp_ContextId() {
		return pdp_ContextId;
	}
	
	public void setPdp_ContextId(byte[] pdp_ContextId) {
		this.pdp_ContextId = Utility.bytesToInteger(pdp_ContextId);
	}
	
	public byte[] getPdp_Type() {
		return pdp_Type;
	}
	
	public void setPdp_Type(byte[] pdp_Type) {
		this.pdp_Type = pdp_Type;
		if (pdp_Type.length > 0) {
			int c = pdp_Type[0];
			pdp_Type_Org = (short) (c & 0x0F);
		}
	}
		
	public short getPdp_Type_Org() {
		return pdp_Type_Org;
	}
	
	public String getPdp_Address() {
		return pdp_Address;
	}
	
	public void setPdp_Address(String pdp_Address) {
		this.pdp_Address = pdp_Address;
	}
	
	public QoSSubscribed getQoSSubscribed() {
		return qoSSubscribed;
	}
	
	public void setQos_Subscribed(byte[] qos_Subscribed) {
		this.qoSSubscribed = new QoSSubscribed(qos_Subscribed);
	}
	
	public char getVplmnAddressAllowed() {
		return vplmnAddressAllowed;
	}
	
	public void setVplmnAddressAllowed(byte vplmnAddressAllowed) {
		this.vplmnAddressAllowed = (char) vplmnAddressAllowed;
	}
	
	public String getUlcm_APN() {
		return ulcm_APN;
	}
	
	public void setUlcm_APN(String ulcm_APN) {
		this.ulcm_APN = ulcm_APN;
	}
	
	public void setUlcm_Ext_QoS_Subscribed(byte[] ulcm_Ext_QoS_Subscribed) {
		extQoSSubscribed = new ExtQoSSubscribed(ulcm_Ext_QoS_Subscribed);
	}
	
	public ExtQoSSubscribed getExtQoSSubscribed() {
		return extQoSSubscribed;
	}
	
	public short getUlcm_ChargingCharacteristics() {
		return ulcm_ChargingCharacteristics;
	}
	
	public void setUlcm_ChargingCharacteristics(byte[] ulcm_ChargingCharacteristics) {
		if (ulcm_ChargingCharacteristics == null || ulcm_ChargingCharacteristics.length == 0)
			return;
		this.ulcm_ChargingCharacteristics = (short) (ulcm_ChargingCharacteristics[0] & 0x0F);
	}
	
	public String getExt2_QoS_Subscribed() {
		return ext2_QoS_Subscribed;
	}

	public void setExt2_QoS_Subscribed(String ext2_QoS_Subscribed) {
		this.ext2_QoS_Subscribed = ext2_QoS_Subscribed;
	}
	
	@Override
	public String toString() {
		StringWriter stringWriter = new StringWriter();
		PrintWriter out = new PrintWriter(stringWriter);
		out.println();
		out.println("-------------PDP Context-------------");
		out.println("\tContext-ID: " + pdp_ContextId);
		out.println("\tPDP Type: " + (pdp_Type==null?"":Utility.bytesToHex(pdp_Type)));
		out.println("\tPDP Type Org: " + pdp_Type_Org);
		out.println("\tPDP Address: " + (pdp_Address==null?"":pdp_Address));
		out.println("\tQoS Subscribed: " + (qoSSubscribed==null?"":qoSSubscribed));
		out.println("\tVPLMN Address Allowed: " + (vplmnAddressAllowed=='\0'?"":vplmnAddressAllowed));
		out.println("\tAPN Name: " + (ulcm_APN==null?"":ulcm_APN));
		out.println("\tExt QoS Subscribed: " + (extQoSSubscribed==null?"":extQoSSubscribed));
		out.println("\tCharging Characteristics: " + ulcm_ChargingCharacteristics);
		//out.println("\tExt2_QoS_Subscribed: " + (ext2_QoS_Subscribed==null?"":ext2_QoS_Subscribed));
		return stringWriter.toString();
	}
	
	public class QoSSubscribed {
		short QoSDelayClass;
		short reliabilityClass;
		short peakThroughput;
		short precedenceClass;
		short meanThroughput;
		byte[] rawBytes;
		
		public QoSSubscribed(byte[] qos_Subscribed) {
			parseQoS(qos_Subscribed);
		}

		private void parseQoS(byte[] qos_Subscribed) {
			if (qos_Subscribed == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "QoS Unavailable");
				return;
			}
			rawBytes = qos_Subscribed;
			if (qos_Subscribed.length > 0) {
				int firstByte = qos_Subscribed[0];
				QoSDelayClass = (short) ((firstByte >> 3) & 0x07); // 00111000
				reliabilityClass = (short) (firstByte & 0x07); // 00000111
			} else {
				return;
			}
			
			if (qos_Subscribed.length > 1) {
				
				int secondByte = (qos_Subscribed[1] & 0xFF);
				peakThroughput = (short) ((secondByte >> 4) & 0x0F); // 11110000
				precedenceClass = (short) (secondByte & 0x07); // 00000111
			} else {
				return;
			}
			
			if (qos_Subscribed.length > 2) {
				int thirdByte = qos_Subscribed[2];
				meanThroughput = (short) (thirdByte & 0x1F); // 00011111
			} 			
		}

		public byte[] getRawBytes() {
			return rawBytes;
		}
		
		public short getQoSDelayClass() {
			return QoSDelayClass;
		}
		
		public short getReliabilityClass() {
			return reliabilityClass;
		}
		
		public short getPeakThroughput() {
			return peakThroughput;
		}
		
		public short getPrecedenceClass() {
			return precedenceClass;
		}
		
		public short getMeanThroughput() {
			return meanThroughput;
		}
		
		@Override
		public String toString() {
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("\t\t\tQoS Delay class: " + QoSDelayClass);
			out.println("\t\t\tReliability class: " + reliabilityClass);
			out.println("\t\t\tPeak throughpuy: " + peakThroughput);
			out.println("\t\t\tPrecedence class: " + precedenceClass);
			out.println("\t\t\tMean throughput: " + meanThroughput);
			return stringWriter.toString();
		}
	}
	
	public class ExtQoSSubscribed {
		short allocationPriority;
		short trafficClass;
		short deliveryOrder;
		short deliveryOfErroneousSDUs;
		int maxSDUSize;
		int maxUplinkBitrate;
		int maxDownlinkBitrate;
		short residualBER;
		short SDUErrorRatio;
		short transferDelay;
		short traffichandlingPriority;
		short guaranteedUplinkBitrate;
		short guaranteedDownlinkBitrate;
		byte[] rawBytes;

		public ExtQoSSubscribed(byte[] extQoSSubscribed) {
			parseExtQoS(extQoSSubscribed);
		}

		private void parseExtQoS(byte[] extQoSSubscribed) {
			if (extQoSSubscribed == null) {
				if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(MODULE, "Ext QoS Unavailable");
				return;
			}
			rawBytes = extQoSSubscribed;
			if (extQoSSubscribed.length > 0) {
				allocationPriority = (short) (extQoSSubscribed[0] & 0xFF);
			} else {
				return;
			}
			
			if (extQoSSubscribed.length > 1) {
				int secondByte = extQoSSubscribed[1];
				trafficClass = (short) ((secondByte >> 5) & 0x07); 		// 11100000
				deliveryOrder = (short) ((secondByte>> 3 )& 0x03); 		// 00011000
				deliveryOfErroneousSDUs = (short) (secondByte & 0x03); 	// 00000011
			} else {
				return;
			}
			
			if (extQoSSubscribed.length > 2) {
				maxSDUSize = gsm_map_calc_max_SDU_size(extQoSSubscribed[2] & 0xFF);
			} else {
				return;
			}
			
			if (extQoSSubscribed.length > 3) {
				int fourthByte = extQoSSubscribed[3] & 0xFF;
				maxUplinkBitrate = gsm_map_calc_bitrate(fourthByte);
			} else {
				return;
			}
			
			if (extQoSSubscribed.length > 4) {
				int fifthByte = extQoSSubscribed[4] & 0xFF;
				maxDownlinkBitrate = gsm_map_calc_bitrate(fifthByte);
			} else {
				return;
			}
			
			if (extQoSSubscribed.length > 5) {
				int sixthByte = extQoSSubscribed[5];
				residualBER = (short) ((sixthByte >> 4)& 0x0F); // 11110000
				SDUErrorRatio = (short) (sixthByte & 0x0F); // 00001111
			} else {
				return;
			}
			
			if (extQoSSubscribed.length > 6) {
				int seventhByte = extQoSSubscribed[6];
				transferDelay = (short) ((seventhByte >> 2) & 0x3F); // 11111100
				traffichandlingPriority = (short) (seventhByte & 0x03); // 00000011
			} else {
				return;
			}
			
			if (extQoSSubscribed.length > 7) {
				guaranteedUplinkBitrate = (short) (extQoSSubscribed[7] & 0xFF);
			} else {
				return;
			}
			
			if (extQoSSubscribed.length > 8) {
				guaranteedDownlinkBitrate = (short) (extQoSSubscribed[8] & 0xFF);
			} else {
				return;
			}
		}
		
		int gsm_map_calc_bitrate(int value){

			int granularity = 0;
			int returnvalue = 0;

			if ( ( value & 0xFF ) == 0xFF)
				return 0;

			granularity = value >> 6;
			returnvalue = value & 0x7f;
			switch (granularity){
			case 0:
				break;
			case 1:
				returnvalue = ((returnvalue - 0x40) << 3)+64;
				break;
			case 2:
				returnvalue = (returnvalue << 6)+576;
				break;
			case 3:
				returnvalue = (returnvalue << 6)+576;
				break;
			}
			return returnvalue;
		}
		
		int gsm_map_calc_max_SDU_size(int value){

			switch (value){
			case 0:
				return 0;
			case 0x97:
				return 1502;
			case 0x98:
				return 1510;
			case 0x99:
				return 1520;
			default:
				if (value<0x97){
					return value * 10;
				}else{
					if (LogManager.getLogger().isLogLevel(LogLevel.WARN))
						LogManager.getLogger().warn(MODULE, "invalid Max SDU size received: " + value);
					return value;
				}
			}
		}
		
		public byte[] getRawBytes() {
			return rawBytes;
		}
		public short getAllocationPriority() {
			return allocationPriority;
		}
		
		public short getTrafficClass() {
			return trafficClass;
		}
		
		public short getDeliveryOrder() {
			return deliveryOrder;
		}
		
		public short getDeliveryOfErroneousSDUs() {
			return deliveryOfErroneousSDUs;
		}
		
		public int getMaxSDUSize() {
			return maxSDUSize;
		}
		
		public int getMaxUplinkBitrate() {
			return maxUplinkBitrate;
		}
		
		public int getMaxDownlinkBitrate() {
			return maxDownlinkBitrate;
		}
		
		public short getResidualBER() {
			return residualBER;
		}
		
		public short getSDUErrorRatio() {
			return SDUErrorRatio;
		}
		
		public short getTransferDelay() {
			return transferDelay;
		}
		
		public short getTraffichandlingPriority() {
			return traffichandlingPriority;
		}
		
		public short getGuaranteedUplinkBitrate() {
			return guaranteedUplinkBitrate;
		}
		
		public short getGuaranteedDownlinkBitrate() {
			return guaranteedDownlinkBitrate;
		}
		
		@Override
		public String toString() {
			StringWriter stringWriter = new StringWriter();
			PrintWriter out = new PrintWriter(stringWriter);
			out.println();
			out.println("\t\t\tAllocation Priority: " + allocationPriority);
			out.println("\t\t\tTraffic Class: " + trafficClass);
			out.println("\t\t\tDelivery Order: " + deliveryOrder);
			out.println("\t\t\tDelivery Of Erroneous SDUs: " + deliveryOfErroneousSDUs);
			out.println("\t\t\tMax SDU Size: " + maxSDUSize);
			out.println("\t\t\tMax Uplink Bitrate: " + maxUplinkBitrate);
			out.println("\t\t\tMax Downlink Bitrate: " + maxDownlinkBitrate);
			out.println("\t\t\tResidual BER: " + residualBER);
			out.println("\t\t\tSDU Error Ratio: " + SDUErrorRatio);
			out.println("\t\t\tTransfer Delay: " + transferDelay);
			out.println("\t\t\tTraffic Handling Priority: " + traffichandlingPriority);
			out.println("\t\t\tGuaranteed Uplink Bitrate: " + guaranteedUplinkBitrate);
			out.println("\t\t\tGuaranteed Downlink Bitrate: " + guaranteedDownlinkBitrate);
			return stringWriter.toString();
		}
	}

}
