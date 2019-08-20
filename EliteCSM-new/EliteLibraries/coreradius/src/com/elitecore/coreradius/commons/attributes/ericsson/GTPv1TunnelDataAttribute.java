package com.elitecore.coreradius.commons.attributes.ericsson;

import static com.elitecore.coreradius.commons.util.RadiusUtility.extractKey;
import static com.elitecore.coreradius.commons.util.RadiusUtility.extractValue;
import static com.elitecore.coreradius.commons.util.RadiusUtility.getBytesFromHexValue;
import static com.elitecore.coreradius.commons.util.RadiusUtility.isHex;
import static com.elitecore.coreradius.commons.util.RadiusUtility.isNullOrEmpty;
import static com.elitecore.coreradius.commons.util.RadiusUtility.readBytes;
import static com.elitecore.coreradius.commons.util.RadiusUtility.readInt;
import static com.elitecore.coreradius.commons.util.RadiusUtility.writeBytesSilently;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.StringTokenizer;

import javax.annotation.Nonnull;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.commons.net.AddressResolver;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.InvalidRadiusAttributeLengthException;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * <pre>
                                                               Bits

        Bytes            8          7           6          5         4          3          2         1
                     +-----------------------------------------------------------------------------------+
          1          |                              APN Restriction                                      |
                     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
          2          |                              Length = a                                           |
                     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
          3          |                              Allocation/Retention Priority                        |
                     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
     4 to (4+a-1)    |                              QOS Profile Data                                     |
                     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
     4+a to 5+a      |                              Charging Characteristics value                       |
                     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
        6+a          |                              Length = b                                           |
                     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
    7+a to 7+a+b-1   |                              Access Point Name (APN)                              |
                     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
       7+a+b         |                              Length = c                                           |
                     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
       8+a+b to      |                              IPv4 or IPv6 Address (Primary GGSN address)          |
       8+a+b+c-1     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
       8+a+b+c       |                              Length = d                                           |
                     |                                                                                   |
                     +-----------------------------------------------------------------------------------+
       9+a+b+c to    |                              IPv4 or IPv6 Address (Secondary GGSN address)        |
       9+a+b+c+d-1   |                                                                                   |
                     +-----------------------------------------------------------------------------------+</pre>
 * 
 * Required specification document for GTPv1 Tunnel Data attribute is attached in the Jira: 2420
 * 
 * @author kuldeep panchal
 *
 */
public class GTPv1TunnelDataAttribute extends BaseRadiusAttribute {

	private static final long serialVersionUID = 1L;

	private static final String MODULE = "GTPv1_TUNNEL_DATA";
	
	public static final String APN_RESTRICTION = "APNRES";
	public static final String ARP = "QOS-ARP-PRIORITY";
	public static final String QOS_PROFILE_DATA = "QOS-PROFILE";
	public static final String CHARGING_CHARACTERISTICS = "CC";
	public static final String APN_NAME = "APNNAME";
	public static final String PRIMARY_GGSN = "P-GGSN";
	public static final String SECONDARY_GGSN = "S-GGSN";
	
	private static final int APN_RESTRICTION_SIZE_IN_BYTES = 1;
	private static final int QOS_SIZE_IN_BYTES = 1;
	private static final int ARP_SIZE_IN_BYTES = 1;
	private static final int CHARGING_CHARACTERISTICS_SIZE_IN_BYTES = 2;
	private static final int APNLEN_SIZE_IN_BYTES = 1;
	private static final int IP_ADDRESS_LEN_SIZE_IN_BYTES = 1;
	private static final int APN_NAME_LEN_SIZE_IN_BYTES = 1;
	private static final int APN_LENGTH_MAXVALUE = 100;
	private static final String APN_LENGTH = "APN Length";
	private static final String DELIMETER_FOR_APN_NAME = ".";
	private static final int END_OF_STREAM = -1;
	
	private static final int MIN_KEY_VALUE_PAIR = 1;
	private static final int MAX_KEY_VALUE_PAIR = 7;
	private static final int ZERO_AS_MINVALUE = 0;
	private static final int APN_RESTRICTION_MAXVALUE = 4;
	private static final int ARP_MAXVALUE = 255;
	private static final int CHARGING_CHARACTERISTICS_MAXVALUE = 65535;
	private static final int MIN_SIZE_OF_GTPV1_TUNNEL_DATA = 16;

	private int aPNRestriction;								// 1 byte
	private int qosLen;
	private int arp;										// 1 byte
	private String qOSProfileData="";							
	private int chargingCharacteristics;					// 2 bytes
	private String accessPointName="";
	private String primaryAddress="0.0.0.0";				// 4 or 16 bytes
	private String secondaryAddress="0.0.0.0";				// 4 or 16 bytes

	@Nonnull
	private transient AddressResolver addressResolver;
	
	public GTPv1TunnelDataAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
		this.addressResolver = AddressResolver.defaultAddressResolver();
	}
	
	public GTPv1TunnelDataAttribute() { // NOSONAR
		this(AddressResolver.defaultAddressResolver());
	}
	
	public GTPv1TunnelDataAttribute(AddressResolver addressResolver) {
		this.addressResolver = addressResolver;
	}
	
	@Override
	public void setStringValue(String value) {
		if(isNullOrEmpty(value)) {
			throw new IllegalArgumentException("Null or blank string is not a valid value for GTPv1 Tunnel Data Attribute");
		}
		
		if(isHex(value)) {
			setValueBytes(getBytesFromHexValue(value));
		} else {
			String avpPairSeperator = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.ERICSSON_VENDOR_ID);
			StringTokenizer tokens = new StringTokenizer(value,avpPairSeperator);
			int tokenCount = tokens.countTokens();

			if(tokenCount < MIN_KEY_VALUE_PAIR || tokenCount > MAX_KEY_VALUE_PAIR){
				throw new IllegalArgumentException("Invalid value: " + value + ". At least one key and atmost " + MAX_KEY_VALUE_PAIR + " keys can be configured for GTPv1-Tunnel-Data, but got " + tokenCount + " key/s");
			}

			while(tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if(!isNullOrEmpty(token)) {
					setProperty(extractKey(token), extractValue(token));
				}
			}
			super.setValueBytes(getGTPv1TunnelValueBytes());
		}
	}

	private void setProperty(String key, String val) {
		try {
			if(APN_RESTRICTION.equalsIgnoreCase(key)) {
				this.aPNRestriction = Integer.parseInt(val);
				validateInRange(this.aPNRestriction, ZERO_AS_MINVALUE, APN_RESTRICTION_MAXVALUE, APN_RESTRICTION);
				
			} else if(ARP.equalsIgnoreCase(key)) {
				this.arp = Integer.parseInt(val);
				validateInRange(this.arp,ZERO_AS_MINVALUE,ARP_MAXVALUE,ARP);
			
			} else if(QOS_PROFILE_DATA.equalsIgnoreCase(key)) {
				this.qOSProfileData = val;
				this.qosLen = this.qOSProfileData.length();
			
			} else if(CHARGING_CHARACTERISTICS.equalsIgnoreCase(key)) {
				if(isHex(val)) {
					byte[] ccBytes = getBytesFromHexValue(val);
					this.chargingCharacteristics = (ccBytes[0] & 0xFF) << 8 | ccBytes[1] & 0xFF;
				} else {
					this.chargingCharacteristics = Integer.parseInt(val);
				}
				validateInRange(this.chargingCharacteristics, ZERO_AS_MINVALUE, CHARGING_CHARACTERISTICS_MAXVALUE, CHARGING_CHARACTERISTICS);
			
			} else if(APN_NAME.equalsIgnoreCase(key)) {
				this.accessPointName = val;
				
			} else if(PRIMARY_GGSN.equalsIgnoreCase(key)) {
				this.primaryAddress = addressResolver.byName(val).getHostAddress();
			
			} else if(SECONDARY_GGSN.equalsIgnoreCase(key)) {
				this.secondaryAddress = addressResolver.byName(val).getHostAddress();
			} 
		} catch (UnknownHostException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Cannot interpret ipaddress for GTPv1 Tunnel Data, Reason: " + e.getMessage());
			throw new IllegalArgumentException(e);
		} catch(NumberFormatException nfe) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, key + " is configured with invalid value: " + val);
			throw new IllegalArgumentException(nfe);
		}
	}

	private void validateInRange(long fieldValue, long minValue, long maxValue, String fieldName) {
		if(fieldValue < minValue || fieldValue > maxValue) {
			throw new IllegalArgumentException(fieldName + " value: " + fieldValue + " does not fall in closed range [" + minValue + " - " + maxValue + "]");
		}
	}
	
	@Override
	public void setStringValue(String value, String charsetName) throws UnsupportedEncodingException {
		if(value != null) {
			setStringValue(new String(value.getBytes(charsetName)));
		}
	}

	@Override
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return new String(getStringValue().getBytes() , charsetName);
	}

	@Override
	public String getStringValue() {
		StringBuilder builder = new StringBuilder();
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.ERICSSON_VENDOR_ID);
		builder.append(APN_RESTRICTION + "=" + this.aPNRestriction)
		.append(AVPAIR_SEPERATOR + ARP + "=" + this.arp)
		.append(AVPAIR_SEPERATOR + QOS_PROFILE_DATA + "=" + this.qOSProfileData)
		.append(AVPAIR_SEPERATOR + CHARGING_CHARACTERISTICS + "=" + this.chargingCharacteristics)
		.append(AVPAIR_SEPERATOR + APN_NAME + "=" + this.accessPointName)
		.append(AVPAIR_SEPERATOR + PRIMARY_GGSN + "=" + this.primaryAddress)
		.append(AVPAIR_SEPERATOR + SECONDARY_GGSN + "=" + this.secondaryAddress);
		return builder.toString();
	}
	
	@Override
	public void setValueBytes(byte[] valueBytes) {
		super.setValueBytes(valueBytes);
		parseGTPv1ValueBytes();
	}

	private void parseGTPv1ValueBytes() {
		
		byte[] valueBytes = getValueBytes();
		
		if(valueBytes.length < MIN_SIZE_OF_GTPV1_TUNNEL_DATA) {
			throw new InvalidRadiusAttributeLengthException("Invalid number of bytes: " + valueBytes.length + " for GTPv1 Tunnel Data attribute");
		}
		
		ByteArrayInputStream valueStream = new ByteArrayInputStream(valueBytes);
		try {
			aPNRestriction = readInt(valueStream, APN_RESTRICTION_SIZE_IN_BYTES);
			validateInRange(this.aPNRestriction, ZERO_AS_MINVALUE, APN_RESTRICTION_MAXVALUE, APN_RESTRICTION);
			
			qosLen = readInt(valueStream, QOS_SIZE_IN_BYTES);
			
			arp = readInt(valueStream, ARP_SIZE_IN_BYTES);
			validateInRange(this.arp,ZERO_AS_MINVALUE,ARP_MAXVALUE,ARP);
			
			qOSProfileData = new String(readBytes(valueStream, qosLen));
			
			chargingCharacteristics = readInt(valueStream, CHARGING_CHARACTERISTICS_SIZE_IN_BYTES);
			validateInRange(this.chargingCharacteristics, ZERO_AS_MINVALUE, CHARGING_CHARACTERISTICS_MAXVALUE, CHARGING_CHARACTERISTICS);
			
			int apnLen = readInt(valueStream, APNLEN_SIZE_IN_BYTES);
			validateInRange(apnLen, ZERO_AS_MINVALUE, APN_LENGTH_MAXVALUE, APN_LENGTH);
			
			accessPointName = generateApnName(valueStream, apnLen);
			
			int primaryAddressLen = readInt(valueStream, IP_ADDRESS_LEN_SIZE_IN_BYTES);
			primaryAddress = addressResolver.byAddress(readBytes(valueStream, primaryAddressLen)).getHostAddress();
			
			int secondaryAddressLen = readInt(valueStream, IP_ADDRESS_LEN_SIZE_IN_BYTES);
			secondaryAddress = addressResolver.byAddress(readBytes(valueStream, secondaryAddressLen)).getHostAddress();
			
		} catch (UnknownHostException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Cannot interpret ipaddress, Reason: " + e.getMessage());
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Invalid bytes received for GTPv1 Tunnel Data attribute");
			throw new IllegalArgumentException(e);
		}
	}

	/*
	 * apnlen must be set before calling this method
	 */
	private String generateApnName(ByteArrayInputStream valueStream, int apnLen) throws IOException {
		StringBuilder apnName = new StringBuilder();
		int labelLength;
		int bytesRead = 0;
		
		while((labelLength = readInt(valueStream, APN_NAME_LEN_SIZE_IN_BYTES)) != END_OF_STREAM) {
			bytesRead += APN_NAME_LEN_SIZE_IN_BYTES;

			apnName.append(new String(readBytes(valueStream, labelLength)));
			bytesRead += labelLength;
			
			if(bytesRead >= apnLen) {
				break;
			}
			apnName.append(DELIMETER_FOR_APN_NAME);
		}
		
		return apnName.toString();
	}

	private byte[] getGTPv1TunnelValueBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writeBytesSilently(baos, (byte) (aPNRestriction & 0xFF));
			writeBytesSilently(baos, (byte) (qosLen & 0xFF));
			writeBytesSilently(baos, (byte) (arp & 0xFF));
			writeBytesSilently(baos, qOSProfileData.getBytes());
			writeBytesSilently(baos, RadiusUtility.toByteArray(chargingCharacteristics, 2));
			byte[] apnBytes = generateApnBytes();
			writeBytesSilently(baos, (byte) (apnBytes.length & 0xFF));
			writeBytesSilently(baos, apnBytes);
			byte[] primaryAddressBytes = addressResolver.byName(this.primaryAddress).getAddress();
			writeBytesSilently(baos, (byte) (primaryAddressBytes.length & 0xFF));
			writeBytesSilently(baos, primaryAddressBytes);
			byte[] secondayAddressBytes = addressResolver.byName(this.secondaryAddress).getAddress();
			writeBytesSilently(baos, (byte) (secondayAddressBytes.length & 0xFF));
			writeBytesSilently(baos, secondayAddressBytes);
		} catch (UnknownHostException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Cannot interpret ipaddress, Reason: " + e.getMessage());
			throw new IllegalArgumentException(e);
		}
		return baos.toByteArray();
	}
	
	private byte[] generateApnBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		StringTokenizer tokens = new StringTokenizer(this.accessPointName,DELIMETER_FOR_APN_NAME);
		while(tokens.hasMoreTokens()) {
			String token = tokens.nextToken();
			writeBytesSilently(baos, (byte) token.length());
			writeBytesSilently(baos, token.getBytes());
		}
		return baos.toByteArray();
	}

	@Override
	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException {
		int lengthRead = super.readLengthOnwardsFrom(sourceStream);
		parseGTPv1ValueBytes();
		return lengthRead; 
	}
	
	@Override
	public int readFrom(InputStream sourceStream) throws IOException {
		int lengthRead = super.readFrom(sourceStream);
		parseGTPv1ValueBytes();
		return lengthRead;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n\t\t" + APN_RESTRICTION + " = " + this.aPNRestriction);
		stringBuilder.append("\n\t\t" + ARP + " = " + this.arp);
		stringBuilder.append("\n\t\t" + QOS_PROFILE_DATA + " = " + this.qOSProfileData);
		stringBuilder.append("\n\t\t" + CHARGING_CHARACTERISTICS + " = " + this.chargingCharacteristics);
		stringBuilder.append("\n\t\t" + APN_NAME + " = " + this.accessPointName);
		stringBuilder.append("\n\t\t" + PRIMARY_GGSN + " = " + this.primaryAddress);
		stringBuilder.append("\n\t\t" + SECONDARY_GGSN + " = " + this.secondaryAddress);
		return stringBuilder.toString();
	}

	public String getValueFromKey(String key) {
		String value = null;
		if(key.equalsIgnoreCase(APN_RESTRICTION)) {
			value = String.valueOf(this.aPNRestriction);
		} else if(key.equalsIgnoreCase(ARP)) {
			value = String.valueOf(this.arp);
		} else if(key.equalsIgnoreCase(QOS_PROFILE_DATA)) {
			value = this.qOSProfileData;
		} else if(key.equalsIgnoreCase(CHARGING_CHARACTERISTICS)) {
			value = String.valueOf(this.chargingCharacteristics);
		} else if(key.equalsIgnoreCase(APN_NAME)) {
			value = this.accessPointName;
		} else if(key.equalsIgnoreCase(PRIMARY_GGSN)) {
			value = this.primaryAddress;
		} else if(key.equalsIgnoreCase(SECONDARY_GGSN)) {
			value = this.secondaryAddress;
		} 
		return value;
	}
}
