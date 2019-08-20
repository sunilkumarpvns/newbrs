package com.elitecore.coreradius.commons.attributes.ericsson;

import static com.elitecore.coreradius.commons.util.RadiusUtility.bytesToLong;
import static com.elitecore.coreradius.commons.util.RadiusUtility.getBytesFromHexValue;
import static com.elitecore.coreradius.commons.util.RadiusUtility.isHex;
import static com.elitecore.coreradius.commons.util.RadiusUtility.readBytes;
import static com.elitecore.coreradius.commons.util.RadiusUtility.readInt;
import static com.elitecore.coreradius.commons.util.RadiusUtility.toByteArray;
import static com.elitecore.coreradius.commons.util.RadiusUtility.writeBytesSilently;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * <pre>                                                   Bits

 Bytes       8          7           6          5         4          3          2         1
         +-----------------------------------------------------------------------------------+
  11     | 								  ARP Parameter   									 |
         |																					 |
         +-----------------------------------------------------------------------------------+
  12     |                                Label (QCI)                                        |
         |                                                                                   |
         +-----------------------------------------------------------------------------------+
 13-17   |                                Maximum bit rate for Uplink                        |
         |                                                                                   |
         +-----------------------------------------------------------------------------------+
 18-22   |                                Maximum bit rate for Downlink                      |
         |                                                                                   |
         +-----------------------------------------------------------------------------------+
 23-27   |                                Guaranteed bit rate for Uplink                     |
         |                                                                                   |
         +-----------------------------------------------------------------------------------+
 28-32   |                                Guaranteed bit rate for Downlink                   |
         |                                                                                   |
         +-----------------------------------------------------------------------------------+
  33     |                                spare                                              |
         |                                                                                   |
         +-----------------------------------------------------------------------------------+</pre>
 * @author kuldeep panchal
 *
 */
public class BearerQualityOfService {
	
	/*
	 * ARPParameter String constants
	 */
	public static final String SPARE2 = "SPARE2";
	public static final String PCI = "QOSPCI";
	public static final String PL = "QOSPL";
	public static final String SPARE3 = "SPARE3";
	public static final String PVI = "QOSPVI";
	
	public static final String QCI = "QOSQCI";
	public static final String MBR_UPLINK = "QOSMBR-U";
	public static final String MBR_DOWNLINK = "QOSMBR-D";
	public static final String GBR_UPLINK = "QOSGBR-U";
	public static final String GBR_DOWNLINK = "QOSGBR-D";
	public static final String SPARE4 = "SPARE4";

	private static final int ZERO_AS_MINVALUE = 0;
	private static final int MBR_GBR_SIZE_IN_BYTES = 5;
	private static final int BQOS_SIZE_IN_BYTES = 23;
	private static final int QCI_SIZE_IN_BYTES = 1;
	private static final int SPARE4_SIZE_IN_BYTES = 1;
	
	private static final long QCI_MAXVALUE = 255;
	private static final long MBR_UPLINK_MAXVALUE = 10000000000L;		// in kbps
	private static final long MBR_DOWNLINK_MAXVALUE = 10000000000L;		// in kbps
	private static final long GBR_UPLINK_MAXVALUE = 10000000000L;		// in kbps
	private static final long GBR_DOWNLINK_MAXVALUE = 10000000000L;		// in kbps
	private static final long SPARE4_MAX_VLAUE = 255;
	
	private ArpParameter arpParameter;		// 1 byte
	private int labelQCI;					// 1 byte
	private long mBRUpLink;					// 5 bytes
	private long mBRDownLink;				// 5 bytes
	private long gBRUpLink;					// 5 bytes
	private long gBRDownLink;				// 5 bytes
	private int spare4;						// 1 byte
	
	public BearerQualityOfService() {
		arpParameter = new ArpParameter();
	}
	
	public void setBytes(byte[] bearerQOSBytes) {
		
		if(bearerQOSBytes.length != BQOS_SIZE_IN_BYTES) {
			throw new IllegalArgumentException("Invalid number of bytes: " + bearerQOSBytes.length + " for Bearer Quality Of Service");
		}

		ByteArrayInputStream bearerQOSStream = new ByteArrayInputStream(bearerQOSBytes);
		try {
			arpParameter.setByte((byte)bearerQOSStream.read());

			labelQCI = readInt(bearerQOSStream, QCI_SIZE_IN_BYTES);
			validateInRange(this.labelQCI, ZERO_AS_MINVALUE, QCI_MAXVALUE, QCI);

			mBRUpLink = bytesToLong(readBytes(bearerQOSStream, MBR_GBR_SIZE_IN_BYTES));
			validateInRange(this.mBRUpLink, ZERO_AS_MINVALUE, MBR_UPLINK_MAXVALUE, MBR_UPLINK);

			mBRDownLink = bytesToLong(readBytes(bearerQOSStream, MBR_GBR_SIZE_IN_BYTES));
			validateInRange(this.mBRDownLink, ZERO_AS_MINVALUE, MBR_DOWNLINK_MAXVALUE, MBR_DOWNLINK);

			gBRUpLink = bytesToLong(readBytes(bearerQOSStream, MBR_GBR_SIZE_IN_BYTES));
			validateInRange(this.gBRUpLink, ZERO_AS_MINVALUE, GBR_UPLINK_MAXVALUE, GBR_UPLINK);

			gBRDownLink = bytesToLong(readBytes(bearerQOSStream, MBR_GBR_SIZE_IN_BYTES));
			validateInRange(this.gBRDownLink, ZERO_AS_MINVALUE, GBR_DOWNLINK_MAXVALUE, GBR_DOWNLINK);

			spare4 = readInt(bearerQOSStream, SPARE4_SIZE_IN_BYTES);
			validateInRange(this.spare4, ZERO_AS_MINVALUE, SPARE4_MAX_VLAUE, SPARE4);
			
		} catch(IOException e) {
			throw new IllegalArgumentException(e);
		}

	}
	
	public void setStringValue(String val) {
		if(!isHex(val)) {
			throw new IllegalArgumentException("BQOS value: " + val + " is not proper, It must starts with either 0x or 0X");
		} 
		setBytes(getBytesFromHexValue(val));
	}
	
	public void setProperty(String key, String value) {
		try {
			if(QCI.equalsIgnoreCase(key)) {
				this.labelQCI = Integer.parseInt(value);
				validateInRange(this.labelQCI, ZERO_AS_MINVALUE, QCI_MAXVALUE, QCI);
				
			} else if(MBR_UPLINK.equalsIgnoreCase(key)) {
				this.mBRUpLink = Long.parseLong(value);
				validateInRange(this.mBRUpLink, ZERO_AS_MINVALUE, MBR_UPLINK_MAXVALUE, MBR_UPLINK);

			} else if(MBR_DOWNLINK.equalsIgnoreCase(key)) {
				this.mBRDownLink = Long.parseLong(value);
				validateInRange(this.mBRDownLink, ZERO_AS_MINVALUE, MBR_DOWNLINK_MAXVALUE, MBR_DOWNLINK);

			} else if(GBR_UPLINK.equalsIgnoreCase(key)) {
				this.gBRUpLink = Long.parseLong(value);
				validateInRange(this.gBRUpLink, ZERO_AS_MINVALUE, GBR_UPLINK_MAXVALUE, GBR_UPLINK);

			} else if(GBR_DOWNLINK.equalsIgnoreCase(key)) {
				this.gBRDownLink = Long.parseLong(value);
				validateInRange(this.gBRDownLink, ZERO_AS_MINVALUE, GBR_DOWNLINK_MAXVALUE, GBR_DOWNLINK);
				
			} else if(SPARE4.equalsIgnoreCase(key)) {
				this.spare4 = Integer.parseInt(value);
				validateInRange(this.spare4, ZERO_AS_MINVALUE, SPARE4_MAX_VLAUE, SPARE4);
				
			} else {
				arpParameter.setProperty(key, value);
			}
		} catch(NumberFormatException nfe) {
			throw new IllegalArgumentException("Bearer Quality Of Service value is not proper, Reason: " + nfe.getMessage());
		}
	}
	
	private void validateInRange(long fieldValue, long minValue, long maxValue, String fieldName) {
		if(fieldValue < minValue || fieldValue > maxValue) {
			throw new IllegalArgumentException(fieldName + " value: " + fieldValue + " does not fall in closed range [" + minValue + " - " + maxValue + "]");
		}
	}

	public byte[] getBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		writeBytesSilently(baos, arpParameter.getByte());
		writeBytesSilently(baos, (byte) (labelQCI & 0xFF));
		writeBytesSilently(baos, toByteArray(mBRUpLink, MBR_GBR_SIZE_IN_BYTES));
		writeBytesSilently(baos, toByteArray(mBRDownLink, MBR_GBR_SIZE_IN_BYTES));
		writeBytesSilently(baos, toByteArray(gBRUpLink, MBR_GBR_SIZE_IN_BYTES));
		writeBytesSilently(baos, toByteArray(gBRDownLink, MBR_GBR_SIZE_IN_BYTES));
		writeBytesSilently(baos, (byte) (spare4 & 0xFF));
		return baos.toByteArray();
	}
	
	public String getStringValue() {
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.ERICSSON_VENDOR_ID);
		StringBuilder builder = new StringBuilder();
		builder.append(arpParameter.getStringValue())
		.append(AVPAIR_SEPERATOR + QCI + "=" + this.labelQCI)
		.append(AVPAIR_SEPERATOR + MBR_UPLINK + "=" + this.mBRUpLink)
		.append(AVPAIR_SEPERATOR + MBR_DOWNLINK + "=" + this.mBRDownLink)
		.append(AVPAIR_SEPERATOR + GBR_UPLINK + "=" + this.gBRUpLink)
		.append(AVPAIR_SEPERATOR + GBR_DOWNLINK + "=" + this.gBRDownLink)
		.append(AVPAIR_SEPERATOR + SPARE4 + "=" + this.spare4);
		return builder.toString();
	}

	public String getValueFromKey(String key) {
		String value = null;
		if(key.equalsIgnoreCase(QCI)) {
			value = String.valueOf(this.labelQCI);
		} else if(key.equalsIgnoreCase(MBR_UPLINK)) {
			value = String.valueOf(this.mBRUpLink);
		} else if(key.equalsIgnoreCase(MBR_DOWNLINK)) {
			value = String.valueOf(this.mBRDownLink);
		} else if(key.equalsIgnoreCase(GBR_UPLINK)) {
			value = String.valueOf(this.gBRUpLink);
		} else if(key.equalsIgnoreCase(GBR_DOWNLINK)) {
			value = String.valueOf(this.gBRDownLink);
		} else if(key.equalsIgnoreCase(SPARE4)) {
			value = String.valueOf(this.spare4);
		} else {
			value = arpParameter.getValueFromKey(key);
		}
		return value;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append(this.arpParameter);
		stringBuilder.append("\n\t\t\t" + QCI + " = " + this.labelQCI);
		stringBuilder.append("\n\t\t\t" + MBR_UPLINK + " = " + this.mBRUpLink);
		stringBuilder.append("\n\t\t\t" + MBR_DOWNLINK + " = " + this.mBRDownLink);
		stringBuilder.append("\n\t\t\t" + GBR_UPLINK + " = " + this.gBRUpLink);
		stringBuilder.append("\n\t\t\t" + GBR_DOWNLINK + " = " + this.gBRDownLink);
		stringBuilder.append("\n\t\t\t" + SPARE4 + " = " + this.spare4);
		return stringBuilder.toString();
	}
	
	
	/**
	 *
<pre>                                                   Bits

Bytes       8          7           6          5         4          3          2         1
       +--------+----------+-------------------------------------------+----------+--------+
11     | spare  |  PCI     |                    PL                     | spare    |  PVI   |
       |        |          |                                           |          |        |
       +--------+----------+-------------------------------------------+----------+--------+</pre>
	 * @author kuldeep panchal
	 *
	 */
	class ArpParameter {

		private static final byte ONE_AS_MINVALUE = 1;
		
		private static final byte PL_MAXVALUE = 15;
		private static final byte PVI_AS_MAXVALUE = 1;
		private static final byte PCI_AS_MAXVALUE = 1;
		private static final byte SPARE_AS_MAXVALUE = 1;
		
		private static final byte ENABLED = 0;
		private static final byte DISABLED = 1;

		private static final int SPARE2_MASK = 128;
		private static final int SPARE3_MASK = 2;
		private static final int PCI_MASK = 64;
		private static final int PL_MASK = 60;
		private static final int PVI_MASK = 1;
		private static final int PCI_SIZE_IN_BITS = 1;
		private static final int PL_SIZE_IN_BITS = 4;
		private static final int PVI_SIZE_IN_BITS = 1;
		private static final int SPARE_SIZE_IN_BITS = 1;
		
		private byte spare2;	// 1 bit
		private byte pci;	// 1 bit
		private byte pl=1;	// 4 bit
		private byte spare3; 	// 1 bit
		private byte pvi;	// 1 bit

		public void setProperty(String key, String value) {
			if(PCI.equalsIgnoreCase(key)) {
				this.pci = Byte.parseByte(value);
				validateInRange(this.pci,ZERO_AS_MINVALUE, PCI_AS_MAXVALUE,PCI);

			} else if(PL.equalsIgnoreCase(key)) {
				this.pl = Byte.parseByte(value);
				validateInRange(this.pl, ONE_AS_MINVALUE, PL_MAXVALUE, PL);
				
			} else if(PVI.equalsIgnoreCase(key)) {
				this.pvi = Byte.parseByte(value);
				validateInRange(this.pvi,ZERO_AS_MINVALUE,PVI_AS_MAXVALUE,PVI);
				
			} else if(SPARE2.equalsIgnoreCase(key)) {
				this.spare2 = Byte.parseByte(value);
				validateInRange(this.spare2, ZERO_AS_MINVALUE, SPARE_AS_MAXVALUE, SPARE2);
				
			} else if(SPARE3.equalsIgnoreCase(key)) {
				this.spare3 = Byte.parseByte(value);
				validateInRange(this.spare3, ZERO_AS_MINVALUE, SPARE_AS_MAXVALUE, SPARE3);
			}
		}

		public byte getByte() {
			byte arpParameterByte = (byte) this.spare2; 
			arpParameterByte = (byte) (arpParameterByte << PCI_SIZE_IN_BITS) ;
			arpParameterByte = (byte) (arpParameterByte | this.pci);
			arpParameterByte = (byte) (arpParameterByte << PL_SIZE_IN_BITS) ;
			arpParameterByte = (byte) (arpParameterByte | this.pl);
			arpParameterByte = (byte) (arpParameterByte << SPARE_SIZE_IN_BITS);
			arpParameterByte = (byte) (arpParameterByte | this.spare3);
			arpParameterByte = (byte) (arpParameterByte << PVI_SIZE_IN_BITS);
			arpParameterByte = (byte) (arpParameterByte | this.pvi);
			return arpParameterByte;
		}

		public void setByte(byte arpParameterByte) {
			this.spare2 = getStatus(arpParameterByte, SPARE2_MASK);

			this.pci = getStatus(arpParameterByte, PCI_MASK);
			
			this.pl = (byte) ((arpParameterByte & PL_MASK) >> (SPARE_SIZE_IN_BITS + PVI_SIZE_IN_BITS));
			validateInRange(this.pl, ONE_AS_MINVALUE, PL_MAXVALUE, PL);
			
			this.spare3 = getStatus(arpParameterByte, SPARE3_MASK);
			
			this.pvi = getStatus(arpParameterByte, PVI_MASK);
		}
		
		private byte getStatus(byte arpParameterByte, int mask) {
			return (arpParameterByte & mask) != 0 ?  DISABLED : ENABLED ;
		}

		@Override
		public String toString() {
			StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("\n\t\t\t" + SPARE2 + " = " + this.spare2);
			stringBuilder.append("\n\t\t\t" + PCI + " = " + (this.pci == ENABLED ? "Enabled" : "Disabled"));
			stringBuilder.append("\n\t\t\t" + PL + " = " + this.pl);
			stringBuilder.append("\n\t\t\t" + SPARE3 + " = " + this.spare3);
			stringBuilder.append("\n\t\t\t" + PVI + " = " + (this.pvi == ENABLED ? "Enabled" : "Disabled"));
			return stringBuilder.toString();
		}

		public String getStringValue() {
			String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.ERICSSON_VENDOR_ID);
			StringBuilder builder = new StringBuilder();
			builder.append(SPARE2 + "=" + this.spare2)
			.append(AVPAIR_SEPERATOR + PCI + "=" + this.pci)
			.append(AVPAIR_SEPERATOR + PL + "=" + this.pl)
			.append(AVPAIR_SEPERATOR + SPARE3 + "=" + this.spare3)
			.append(AVPAIR_SEPERATOR + PVI + "=" + this.pvi);
			return builder.toString();
		}

		public String getValueFromKey(String key) {
			String value = null;
			if(key.equalsIgnoreCase(SPARE2)) {
				value = String.valueOf(this.spare2);
			} else if(key.equalsIgnoreCase(PCI)) {
				value = String.valueOf(this.pci);
			} else if(key.equalsIgnoreCase(PL)) {
				value = String.valueOf(this.pl);
			} else if(key.equalsIgnoreCase(SPARE3)) {
				value = String.valueOf(this.spare3);
			} else if(key.equalsIgnoreCase(PVI)) {
				value = String.valueOf(this.pvi);
			}
			return value;
		}
	}
}