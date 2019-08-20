package com.elitecore.coreradius.commons.attributes.ericsson;

import static com.elitecore.coreradius.commons.util.RadiusUtility.extractKey;
import static com.elitecore.coreradius.commons.util.RadiusUtility.extractValue;
import static com.elitecore.coreradius.commons.util.RadiusUtility.getBytesFromHexValue;
import static com.elitecore.coreradius.commons.util.RadiusUtility.isHex;
import static com.elitecore.coreradius.commons.util.RadiusUtility.isNullOrEmpty;
import static com.elitecore.coreradius.commons.util.RadiusUtility.readBytes;
import static com.elitecore.coreradius.commons.util.RadiusUtility.readInt;
import static com.elitecore.coreradius.commons.util.RadiusUtility.readLong;
import static com.elitecore.coreradius.commons.util.RadiusUtility.toByteArray;
import static com.elitecore.coreradius.commons.util.RadiusUtility.writeBytesSilently;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
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
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * <pre>
                                                            Bits

          Bytes       8          7           6          5         4          3          2         1
                  +----------------------------------------------------+------------------------------+
            1     |                 spare                              |       PDN Type               |
                  |                                                    |                              |
                  +----------------------------------------------------+------------------------------+
            2     |                               APN Restriction                                     |
                  |                                                                                   |
                  +-----------------------------------------------------------------------------------+
           3-6    |                               APN-AMBR for Uplink                                 |
                  |                                                                                   |
                  +-----------------------------------------------------------------------------------+
           7-10   |                               APN-AMBR for Downlink                               |
                  |                                                                                   |
                  +-----------------------------------------------------------------------------------+
          11-33   |                               Bearer QOS                                          |
                  |                                                                                   |
                  +-----------------------------------------------------------------------------------+
          34-35   |                               Charging Characteristics value                      |
                  |                                                                                   |
                  +-----------------------------------------------------------------------------------+
                  |                               Length = a                                          |
           36     |                                                                                   |
                  +-----------------------------------------------------------------------------------+
          37 to   |                               Access Point Name (APN)                             |
        (37+a-1)  |                                                                                   |
                  +-----------------------------------------------------------------------------------+
                  |                               Length = b                                          |
          37+a    |                                                                                   |
                  +-----------------------------------------------------------------------------------+
         38+a to  |                               IPv4 or IPv6 (Primary PDN GW) Address               |
       (38+a+b-1) |                                                                                   |
                  +-----------------------------------------------------------------------------------+
         38+a+b   |                               Length = c                                          |
                  |                                                                                   |
                  +-----------------------------------------------------------------------------------+
       39+a+b to  |                               IPv4 or IPv6 (Secondary PDN GW) Address             |
       (39+a+b+c) |                                                                                   |
                  +-----------------------------------------------------------------------------------+</pre>
 * 
 * Required specification document for GTP Tunnel Data attribute is attached in the Jira: 2420
 * 
 * @author kuldeep panchal
 * 
 */
public class GTPTunnelDataAttribute extends BaseRadiusAttribute {

	

	private static final long serialVersionUID = 1L;

	private static final String MODULE = "GTP_TUNNEL_DATA";
	
	private static final int ZERO_AS_MINVALUE = 0;
	private static final int PDN_TYPE_MINVALUE = 1;
	
	private static final int SPARE1_MAXVALUE = 31;
	private static final int PDN_TYPE_MAXVALUE = 3;
	private static final int APN_RESTRICTION_MAXVALUE = 4;
	private static final long AMBR_UPLINK_MAXVALUE = 4294967295L;
	private static final long AMBR_DOWNLINK_MAXVALUE = 4294967295L;
	private static final int CHARGING_CHARACTERISTICS_MAXVALUE = 65535;
	private static final int APN_LENGTH_MAXVALUE = 100;
	private static final String APN_LENGTH = "APN Length";
	private static final String DELIMETER_FOR_APN_NAME = ".";
	private static final int END_OF_STREAM = -1;
	
	private static final int MIN_KEY_VALUE_PAIR = 1;
	private static final int MAX_KEY_VALUE_PAIR = 20;
	
	/*
	 * Assuming 36 byte till apn len, blank access point name, 2 byte for address len and minimum 8 bytes for IPv4 addresses, so total 46 
	 */
	private static final int MIN_LEN_FOR_GTP_TUNNEL_DATA = 46;
	
	private static final int AMBR_SIZE_IN_BYTES = 4;
	private static final int BQOS_SIZE_IN_BYTES = 23;
	private static final int SPARE1_AND_PDNTYPE_SIZE_IN_BYTES = 1;
	private static final int APN_RESTRICTION_SIZE_IN_BYTES = 1;
	private static final int CHARGING_CHARACTERISTICS_SIZE_IN_BYTES = 2;
	private static final int APNLEN_SIZE_IN_BYTES = 1;
	private static final int IP_ADDRESS_LEN_SIZE_IN_BYTES = 1;
	private static final int APN_NAME_LEN_SIZE_IN_BYTES = 1;
	
	private static final int NUMBER_OF_PDN_TYPE_BITS = 3;
	private static final int PDN_TYPE_MASK = 7;
	
	public static final String SPARE1 = "SPARE1";
	public static final String PDN_TYPE = "PDNTYPE";
	public static final String APN_RESTRICTION = "APNRES";
	public static final String AMBR_UPLINK = "AMBR-U";
	public static final String AMBR_DOWNLINK = "AMBR-D";
	public static final String BEARER_QOS = "BQOS";
	public static final String CHARGING_CHARACTERISTICS = "CC";
	public static final String APN_NAME = "APNNAME";
	public static final String PRIMARY_PDN = "P-PDN";
	public static final String SECONDARY_PDN = "S-PDN";
	
	private int spare1;										// 5 bits
	private int pdnType = 1; 								// 3 bits
	private int apnRestriction;								// 1 byte
	private long ambrUpLink;								// 4 bytes
	private long ambrDownLink;								// 4 bytes
	transient private BearerQualityOfService bearerQualityOfService;  // 23 bytes
	private int chargingCharacteristics;					// 2 bytes
	private String accessPointName = "";							
	private String primaryAddress = "0.0.0.0";				// 4 or 16 bytes
	private String secondaryAddress = "0.0.0.0";			// 4 or 16 bytes

	@Nonnull
	private transient AddressResolver addressResolver;

	public GTPTunnelDataAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
		this.addressResolver = AddressResolver.defaultAddressResolver();
		bearerQualityOfService = new BearerQualityOfService();
	}
	
	public GTPTunnelDataAttribute() { // NOSONAR
		this(AddressResolver.defaultAddressResolver());
	}
	
	public GTPTunnelDataAttribute(AddressResolver addressResolver) {
		this.addressResolver = addressResolver;
		bearerQualityOfService = new BearerQualityOfService();
	}
	
	@Override
	public void setStringValue(String value) {
		if(isNullOrEmpty(value)) {
			throw new IllegalArgumentException("Null or blank string is not a valid value for GTP Tunnel Data Attribute");
		}

		if(isHex(value)) {
			setValueBytes(getBytesFromHexValue(value));
		} else {
			String avpPairSeperator = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.ERICSSON_VENDOR_ID);
			StringTokenizer tokens = new StringTokenizer(value,avpPairSeperator);
			int tokenCount = tokens.countTokens();

			if(tokenCount < MIN_KEY_VALUE_PAIR || tokenCount > MAX_KEY_VALUE_PAIR){
				throw new IllegalArgumentException("Invalid value: " + value + ", at least one key and atmost " + MAX_KEY_VALUE_PAIR + " keys can be configured for GTP-Tunnel-Data, but got " + tokenCount + " key/s");
			}

			while(tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if(!isNullOrEmpty(token)) {
					setProperty(extractKey(token), extractValue(token));
				}
			}
			super.setValueBytes(getGTPTunnelValueBytes());
		}
	} 

	private void setProperty(String key, String val) throws IllegalArgumentException {
		try {
			if(SPARE1.equalsIgnoreCase(key)) {
				this.spare1 = Integer.parseInt(val);
				validateInRange(spare1,ZERO_AS_MINVALUE,SPARE1_MAXVALUE,SPARE1);
				
			} else if(PDN_TYPE.equalsIgnoreCase(key)) {
				this.pdnType = Integer.parseInt(val);
				validateInRange(pdnType,PDN_TYPE_MINVALUE,PDN_TYPE_MAXVALUE,PDN_TYPE);

			} else if(APN_RESTRICTION.equalsIgnoreCase(key)) {
				this.apnRestriction = Integer.parseInt(val);
				validateInRange(apnRestriction, ZERO_AS_MINVALUE, APN_RESTRICTION_MAXVALUE, APN_RESTRICTION);
			
			} else if(AMBR_UPLINK.equalsIgnoreCase(key)) {
				this.ambrUpLink = Long.parseLong(val);
				validateInRange(ambrUpLink, ZERO_AS_MINVALUE, AMBR_UPLINK_MAXVALUE, AMBR_UPLINK);
			
			} else if(AMBR_DOWNLINK.equalsIgnoreCase(key)) {
				this.ambrDownLink = Long.parseLong(val);
				validateInRange(ambrDownLink, ZERO_AS_MINVALUE, AMBR_DOWNLINK_MAXVALUE, AMBR_DOWNLINK);
			
			} else if(BEARER_QOS.equalsIgnoreCase(key)) {
				this.bearerQualityOfService.setStringValue(val);
				
			} else if(CHARGING_CHARACTERISTICS.equalsIgnoreCase(key)) {
				if(isHex(val)) {
					byte[] ccBytes = getBytesFromHexValue(val);
					this.chargingCharacteristics = (ccBytes[0] & 0xFF) << 8 | ccBytes[1] & 0xFF;;
				} else {
					this.chargingCharacteristics = Integer.parseInt(val);
				}
				validateInRange(chargingCharacteristics, ZERO_AS_MINVALUE, CHARGING_CHARACTERISTICS_MAXVALUE, CHARGING_CHARACTERISTICS);
			
			} else if(APN_NAME.equalsIgnoreCase(key)) {
				this.accessPointName = val;
				validateInRange(this.accessPointName.length(), ZERO_AS_MINVALUE, APN_LENGTH_MAXVALUE, APN_NAME);
			
			} else if(PRIMARY_PDN.equalsIgnoreCase(key)) {
				this.primaryAddress = addressResolver.byName(val).getHostAddress();
			
			} else if(SECONDARY_PDN.equalsIgnoreCase(key)) {
				this.secondaryAddress = addressResolver.byName(val).getHostAddress();
				
				/*
				 * if none of the above key matches then we will assume that key might be of bearer quality of service,
				 * so we will try to find there to set the value for the key.
				 */
			} else {
				this.bearerQualityOfService.setProperty(key, val);
			}
		} catch (UnknownHostException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
				LogManager.getLogger().error(MODULE, "Cannot interpret ipaddress for GTP Tunnel Data, Reason: " + e.getMessage());
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
	public void setValueBytes(byte[] valueBytes) {
		super.setValueBytes(valueBytes);
		parseGTPValueBytes();
	}
	
	private void parseGTPValueBytes() {
		
		byte[] valueBytes = getValueBytes();
		
		if(valueBytes.length < MIN_LEN_FOR_GTP_TUNNEL_DATA) {
			throw new InvalidRadiusAttributeLengthException("Invalid number of bytes: " + valueBytes.length + " for GTP Tunnel Data attribute");
		}
		
		ByteArrayInputStream valueStream = new ByteArrayInputStream(valueBytes);
		try {
			int spareAndPDNType = readInt(valueStream, SPARE1_AND_PDNTYPE_SIZE_IN_BYTES);
			
			spare1 = spareAndPDNType >> NUMBER_OF_PDN_TYPE_BITS;
			validateInRange(spare1,ZERO_AS_MINVALUE,SPARE1_MAXVALUE,SPARE1);
			
			pdnType = spareAndPDNType & PDN_TYPE_MASK;
			validateInRange(pdnType,PDN_TYPE_MINVALUE,PDN_TYPE_MAXVALUE,PDN_TYPE);
			
			apnRestriction = readInt(valueStream, APN_RESTRICTION_SIZE_IN_BYTES);
			validateInRange(apnRestriction, ZERO_AS_MINVALUE, APN_RESTRICTION_MAXVALUE, APN_RESTRICTION);
			
			ambrUpLink = readLong(valueStream, AMBR_SIZE_IN_BYTES);
			validateInRange(ambrUpLink, ZERO_AS_MINVALUE, AMBR_UPLINK_MAXVALUE, AMBR_UPLINK);
			
			ambrDownLink = readLong(valueStream, AMBR_SIZE_IN_BYTES);
			validateInRange(ambrDownLink, ZERO_AS_MINVALUE, AMBR_DOWNLINK_MAXVALUE, AMBR_DOWNLINK);
			
			bearerQualityOfService.setBytes(readBytes(valueStream, BQOS_SIZE_IN_BYTES));
			
			chargingCharacteristics = readInt(valueStream, CHARGING_CHARACTERISTICS_SIZE_IN_BYTES);
			validateInRange(chargingCharacteristics, ZERO_AS_MINVALUE, CHARGING_CHARACTERISTICS_MAXVALUE, CHARGING_CHARACTERISTICS);
			
			int apnLen = readInt(valueStream, APNLEN_SIZE_IN_BYTES);
			validateInRange(apnLen, ZERO_AS_MINVALUE, APN_LENGTH_MAXVALUE, APN_LENGTH);
			
			accessPointName = generateApnName(valueStream, apnLen);
			
			int primaryAddressLen = readInt(valueStream, IP_ADDRESS_LEN_SIZE_IN_BYTES);
			
			primaryAddress = InetAddress.getByAddress(readBytes(valueStream, primaryAddressLen)).getHostAddress();
			
			int secondaryAddressLen = readInt(valueStream, IP_ADDRESS_LEN_SIZE_IN_BYTES);
			
			secondaryAddress = InetAddress.getByAddress(readBytes(valueStream, secondaryAddressLen)).getHostAddress();
			
		} catch (UnknownHostException e) {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Cannot interpret ipaddress, Reason: " + e.getMessage());
			throw new IllegalArgumentException(e);
		} catch (IOException e) {
			LogManager.getLogger().error(MODULE, "Invalid bytes received for GTP Tunnel Data attribute");
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

	private byte[] getGTPTunnelValueBytes() {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		try {
			writeBytesSilently(baos, (byte) (((spare1 << NUMBER_OF_PDN_TYPE_BITS) | pdnType) & 0xFF));
			writeBytesSilently(baos, (byte) (apnRestriction & 0xFF));
			writeBytesSilently(baos, toByteArray(ambrUpLink, AMBR_SIZE_IN_BYTES));
			writeBytesSilently(baos, toByteArray(ambrDownLink, AMBR_SIZE_IN_BYTES));
			writeBytesSilently(baos, bearerQualityOfService.getBytes());
			writeBytesSilently(baos, toByteArray(chargingCharacteristics, 2));
			byte[] apnBytes = generateApnBytes();
			writeBytesSilently(baos, (byte) (apnBytes.length & 0xFF));
			writeBytesSilently(baos, apnBytes);
			byte[] primaryAddressBytes = InetAddress.getByName(this.primaryAddress).getAddress();
			writeBytesSilently(baos, (byte) (primaryAddressBytes.length & 0xFF));
			writeBytesSilently(baos, primaryAddressBytes);
			byte[] secondayAddressBytes = InetAddress.getByName(this.secondaryAddress).getAddress();
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
		parseGTPValueBytes();
		return lengthRead;
	}
	
	@Override
	public int readFrom(InputStream sourceStream) throws IOException {
		int lengthRead = super.readFrom(sourceStream);
		parseGTPValueBytes();
		return lengthRead;
	}
	
	@Override
	public String getStringValue(String charsetName) throws UnsupportedEncodingException {
		return new String(getStringValue().getBytes(), charsetName);
	}
	
	@Override
	public String getStringValue() {
		StringBuilder builder = new StringBuilder();
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.ERICSSON_VENDOR_ID);
		builder.append(SPARE1 + "=" + this.spare1)
		.append(AVPAIR_SEPERATOR + PDN_TYPE + "=" + this.pdnType)
		.append(AVPAIR_SEPERATOR + APN_RESTRICTION + "=" + this.apnRestriction)
		.append(AVPAIR_SEPERATOR + AMBR_UPLINK + "=" + this.ambrUpLink)
		.append(AVPAIR_SEPERATOR + AMBR_DOWNLINK + "=" + this.ambrDownLink)
		.append(AVPAIR_SEPERATOR + bearerQualityOfService.getStringValue())
		.append(AVPAIR_SEPERATOR + CHARGING_CHARACTERISTICS + "=" + this.chargingCharacteristics)
		.append(AVPAIR_SEPERATOR + APN_NAME + "=" + this.accessPointName)
		.append(AVPAIR_SEPERATOR + PRIMARY_PDN + "=" + this.primaryAddress)
		.append(AVPAIR_SEPERATOR + SECONDARY_PDN + "=" + this.secondaryAddress);
		return builder.toString();
	}
	
	public String getValueFromKey(String key) {
		String value = null;
		if(key.equalsIgnoreCase(SPARE1)) {
			value = String.valueOf(this.spare1);
		} else if(key.equalsIgnoreCase(PDN_TYPE)) {
			value = String.valueOf(pdnType);
		} else if(key.equalsIgnoreCase(APN_RESTRICTION)) {
			value = String.valueOf(this.apnRestriction);
		} else if(key.equalsIgnoreCase(AMBR_UPLINK)) {
			value = String.valueOf(this.ambrUpLink);
		} else if(key.equalsIgnoreCase(AMBR_DOWNLINK)) {
			value = String.valueOf(this.ambrDownLink);
		} else if(key.equalsIgnoreCase(CHARGING_CHARACTERISTICS)) {
			value = String.valueOf(this.chargingCharacteristics);
		} else if(key.equalsIgnoreCase(APN_NAME)) {
			value = this.accessPointName;
		} else if(key.equalsIgnoreCase(PRIMARY_PDN)) {
			value = this.primaryAddress;
		} else if(key.equalsIgnoreCase(SECONDARY_PDN)) {
			value = this.secondaryAddress;
		} else {
			value = bearerQualityOfService.getValueFromKey(key);
		}
		return value;
	}
	
	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		stringBuilder.append("\n\t\t" + SPARE1 + " = " + this.spare1);
		stringBuilder.append("\n\t\t" + PDN_TYPE + " = " + this.pdnType);
		stringBuilder.append("\n\t\t" + APN_RESTRICTION + " = " + this.apnRestriction);
		stringBuilder.append("\n\t\t" + AMBR_UPLINK + " = " + this.ambrUpLink);
		stringBuilder.append("\n\t\t" + AMBR_DOWNLINK + " = " + this.ambrDownLink);
		stringBuilder.append("\n\t\t" + BEARER_QOS + this.bearerQualityOfService);
		stringBuilder.append("\n\t\t" + CHARGING_CHARACTERISTICS + " = " + this.chargingCharacteristics);
		stringBuilder.append("\n\t\t" + APN_NAME + " = " + this.accessPointName);
		stringBuilder.append("\n\t\t" + PRIMARY_PDN + " = " + this.primaryAddress);
		stringBuilder.append("\n\t\t" + SECONDARY_PDN + " = " + this.secondaryAddress);
		return stringBuilder.toString();
	}
}
