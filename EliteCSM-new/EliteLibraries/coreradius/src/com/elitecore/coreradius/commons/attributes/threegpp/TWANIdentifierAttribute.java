package com.elitecore.coreradius.commons.attributes.threegpp;


import static com.elitecore.coreradius.commons.util.RadiusUtility.bytesToHex;
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
import java.util.StringTokenizer;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.AttributeId;
import com.elitecore.coreradius.commons.attributes.BaseRadiusAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.CommonConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

/**
 * <pre>
 * 																		Bits				
 * 						  Bytes
							            8        7        6       5        4         3       2          1
								    +------------------------------+--------+-------+-------+--------+---------------+
								    |                              |        |       |       |        |               |
							1	    |      Spare                   |  LAII  | OPNAI |PLMNI  | CIVAI  |   BSSIDI      |
								    |                              |        |       |       |        |               |
								    +------------------------------+--------+-------+-------+--------+---------------+
								    |                            SSID  Length                                        |
							2	    |                                                                                |
								    +--------------------------------------------------------------------------------+
								    |                                SSID                                            |
							3 to k  |                                                                                |
								    +--------------------------------------------------------------------------------+
								    |                                BSSID                                           |
					(k+1) to (k+6)	|                                                                                |
								    +--------------------------------------------------------------------------------+
								q   |                           Civic Address Length                                 |
								    |                                                                                |
								    +--------------------------------------------------------------------------------+
								    |                                                                                |
					(q+1) to (q+r)	|                           Civic Address Information                            |
								    +--------------------------------------------------------------------------------+
								    |                                                                                |
					 s to (s+3)	    |                           TWAN PLMN-ID                                         |
								    +--------------------------------------------------------------------------------+
							t	    |                       TWAN Operator Name Length                                |       +
								    |                                                                                |
								    +----------------------- --------------------------------------------------------+
								    |                                                                                |
					(t+1) to (t+u)  |                            TWAN Operator Name                                  |
								    +----------------------- --------------------------------------------------------+
								    |                        Relay Identity Type                                     |
							v	    |                                                                                |
								    +--------------------------------------------------------------------------------+
								    |                                                                                |
						(v+1)	    |                          Relay Identity Length                                 |
								    +--------------------------------------------------------------------------------+
								    |                                                                                |
					(v+2) to (v+w)  |                           Relay Identity                                       |
								    +--------------------------------------------------------------------------------+
								    |                            Circuit-ID Length                                   |
							x	    |                                                                                |
								    +--------------------------------------------------------------------------------+
								    |                                                                                |
					(x+1) to (x+y)  |                             Circuit-ID                                         |
								    +--------------------------------------------------------------------------------+</pre>

/**
 * The TWAN Identifier is used for reporting UE location in a Trusted WLAN Access Network (TWAN).
 * 
 * Required documents are attached in JIRA:3515.
 * 
 * @author soniya
 *
 */

public class TWANIdentifierAttribute extends BaseRadiusAttribute {
	private static final long serialVersionUID = 1L;

	private static final int RELAY_IDENTITY_TYPE_IPV4_6 = 0;

	private static final int RELAY_IDENTITY_TYPE_FQDN = 1;

	private static final String MODULE = "TWAN-Identifier-Data";

	private static final int LAII_BIT = 0x10;

	private static final int OPNIN_BIT = 0x08;

	private static final int PLMNI_BIT = 0x04;

	private static final int CIVAI_BIT = 0x02;

	private static final int BSSID_BIT = 0x01;

	private static final String SSID_NAME = "SSID";
	private static final String BSSID_NAME = "BSSID";
	private static final String CIVIC_ADD_INFO = "Civic_Address_Information";
	private static final String TWAN_PLMN_ID_NAME = "TWAN_PLMN_ID";
	private static final String TWAN_OPE_NAME = "TWAN_Operator_Name";
	private static final String RELAY_ID_TYPE = "Relay_Identity_Type";
	private static final String RELAY_ID = "Relay_Identity";
	private static final String CIRCUIT_ID = "Circuit_ID";

	private static final int CIVIC_ADDRESS_LENGTH_IN_BYTES = 1;

	private static final int MAX_BSSID_SIZE_IN_BYTES = 6;


	private static final int CIRCUIT_ID_LENGTH_SIZE_IN_BYTES = 1;

	private static final int RELAY_IDENTITY_LENGTH_SIZE_IN_BYTES = 1;

	private static final int RELAY_IDENTITY_TYPE_SIZE_IN_BYTES = 1;

	private static final int TWAN_OPERATOR_NAME_SIZE_IN_BYTES = 1;

	private static final int TWAN_PLMN_ID_SIZE_IN_BYTES = 4;

	private static final int SSID_SIZE_IN_BYTES = 1;

	private static final int MIN_KEY_VALUE_PAIR = 1;

	private static final int MAX_KEY_VALUE_PAIR = 8;

	private static final int ZERO = 0;

	private static final int ONE_BYTE = 255;

	private byte flag = 0;

	private int ssidLength;

	private byte[] ssid;

	private byte[] bssid;

	private int civicAddressLength;

	private byte[] civicAddressInformation;

	private byte[] twanPlmnId;

	private int twanOperatorNameLength;

	private byte[] twanOperatorName;

	private int relayIdentityType;

	private int relayIdentityLength;

	private byte[] relayIdentity;

	private int circuitIDLength;

	private byte[] circuitID;

	public TWANIdentifierAttribute(AttributeId attributeDetail) {
		super(attributeDetail);
	}

	public TWANIdentifierAttribute() {

	}

	@Override
	public void setStringValue(String value) {

		if(isNullOrEmpty(value)) {
			throw new IllegalArgumentException("Null or blank string is not a valid value for TWAN Identifier Attribute");
		}

		if(isHex(value)) {
			setValueBytes(getBytesFromHexValue(value));
		} else {
			String avpPairSeperator = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
			StringTokenizer tokens = new StringTokenizer(value,avpPairSeperator);
			int tokenCount = tokens.countTokens();

			if (tokenCount < MIN_KEY_VALUE_PAIR || tokenCount > MAX_KEY_VALUE_PAIR) {
				throw new IllegalArgumentException("Invalid value is configured for 3GPP-TWAN-Identifier. At least one key value pair and atmost " + MAX_KEY_VALUE_PAIR + " should be configured, but got " + tokenCount + " key/s");
			}

			while (tokens.hasMoreTokens()) {
				String token = tokens.nextToken();
				if (!isNullOrEmpty(token)) {
					setProperty(extractKey(token), extractValue(token));
				}
			}

			super.setValueBytes(getTWANIdentifierValueBytes());
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
		String avPairSeprator = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.VENDOR_3GPP_ID);
		try {
			if (isNullOrEmpty(ssid) == false) {
				builder.append(SSID_NAME + "=" + new String(ssid,CommonConstants.UTF8));
			}
			if (isSet(BSSID_BIT)) {
				builder.append(avPairSeprator + BSSID_NAME + "=" + bytesToHex(bssid));
			}
			if (isSet(CIVAI_BIT)) {
				builder.append(avPairSeprator + CIVIC_ADD_INFO + "=" + bytesToHex(civicAddressInformation));
			}
			if (isSet(PLMNI_BIT)) {
				builder.append(avPairSeprator + TWAN_PLMN_ID_NAME + "=" + bytesToHex(twanPlmnId));
			}
			if (isSet(OPNIN_BIT)) {
				builder.append(avPairSeprator + TWAN_OPE_NAME + "=" + new String(twanOperatorName,CommonConstants.UTF8));
			}
			if (isSet(LAII_BIT)) {
				builder.append(avPairSeprator + RELAY_ID_TYPE + "=" + this.relayIdentityType);
				if (isNullOrEmpty(relayIdentity) == false) {
					builder.append(avPairSeprator + RELAY_ID + "=" + bytesToHex(relayIdentity));
				}
				if (isNullOrEmpty(circuitID) == false) {
					builder.append(avPairSeprator + CIRCUIT_ID + "=" + bytesToHex(circuitID));
				}
			}

		} catch (UnsupportedEncodingException ex) {
			LogManager.getLogger().warn(MODULE, "Invalid data bytes are received in TWAN identifier attribute");
			throw new IllegalArgumentException(ex);
		}
		return builder.toString();


	}

	@Override
	public void setValueBytes(byte[] valueBytes) {
		super.setValueBytes(valueBytes);
		parseTWANIdentifierBytes();
	}

	private void parseTWANIdentifierBytes() {

		byte[] valueBytes = getValueBytes();

		ByteArrayInputStream in = new ByteArrayInputStream(valueBytes);

		try {
			flag =  (byte) (in.read() & 0xFF);

			ssidLength = readInt(in, SSID_SIZE_IN_BYTES);

			ensureAvailable(in, ssidLength);

			ssid = readBytes(in, ssidLength);

			if (isSet(BSSID_BIT)) {
				bssid = readBytes(in, MAX_BSSID_SIZE_IN_BYTES);
			} 

			if (isSet(CIVAI_BIT)) {
				civicAddressLength = readInt(in, CIVIC_ADDRESS_LENGTH_IN_BYTES);

				ensureAvailable(in, civicAddressLength);

				civicAddressInformation = readBytes(in, civicAddressLength);
			}

			if (isSet(PLMNI_BIT)) {
				twanPlmnId = readBytes(in, TWAN_PLMN_ID_SIZE_IN_BYTES);
			}

			if (isSet(OPNIN_BIT)) {
				twanOperatorNameLength = readInt(in, TWAN_OPERATOR_NAME_SIZE_IN_BYTES);

				ensureAvailable(in, twanOperatorNameLength);

				twanOperatorName = readBytes(in, twanOperatorNameLength);
			}

			if (isSet(LAII_BIT)) {
				relayIdentityType = readInt(in, RELAY_IDENTITY_TYPE_SIZE_IN_BYTES);
				validateRelayIdentityType();

				relayIdentityLength = readInt(in, RELAY_IDENTITY_LENGTH_SIZE_IN_BYTES);
				ensureAvailable(in, relayIdentityLength);

				relayIdentity = readBytes(in, relayIdentityLength);

				circuitIDLength = readInt(in, CIRCUIT_ID_LENGTH_SIZE_IN_BYTES);
				ensureAvailable(in, circuitIDLength);

				circuitID = readBytes(in, circuitIDLength);
			}

		} catch (IOException ex) {
			LogManager.getLogger().warn(MODULE, "Invalid data bytes are received in TWAN identifier attribute");
			throw new IllegalArgumentException(ex);
		}
	}

	private void ensureAvailable(ByteArrayInputStream valueStream, int length) {
		if (valueStream.available() < length) {
			LogManager.getLogger().warn(MODULE, "Invalid data bytes are received in TWAN identifier attribute, "
					+ "field length is greater than remaining bytes of attribute value. Value Bytes are: " + RadiusUtility.bytesToHex(getValueBytes()));
			throw new IllegalArgumentException("Invalid value bytes received in TWAN identifier attribute: " + RadiusUtility.bytesToHex(getValueBytes()));
		}
	}

	private boolean isSet(int flagValue) {
		return (flag & flagValue) != 0;
	}

	@Override
	public int readLengthOnwardsFrom(InputStream sourceStream) throws IOException {
		int lengthRead = super.readLengthOnwardsFrom(sourceStream);
		parseTWANIdentifierBytes();
		return lengthRead; 
	}

	@Override
	public int readFrom(InputStream sourceStream) throws IOException {
		int lengthRead = super.readFrom(sourceStream);
		parseTWANIdentifierBytes();
		return lengthRead;
	}

	@Override
	public String toString() {
		StringBuilder stringBuilder = new StringBuilder();
		try {
			if (isNullOrEmpty(ssid) == false) {
				stringBuilder.append("\n\t\t" + SSID_NAME + " = "  + new String(ssid,CommonConstants.UTF8));
			}
			if (isSet(BSSID_BIT)) {
				stringBuilder.append("\n\t\t" + BSSID_NAME + " = " + bytesToHex(bssid));
			}
			if(isSet(CIVAI_BIT)) {
				stringBuilder.append("\n\t\t" + CIVIC_ADD_INFO + " = " + bytesToHex(civicAddressInformation));
			}
			if (isSet(PLMNI_BIT)) {
				stringBuilder.append("\n\t\t" + TWAN_PLMN_ID_NAME + " = " + bytesToHex(twanPlmnId));
			}
			if (isSet(OPNIN_BIT)) {
				stringBuilder.append("\n\t\t" + TWAN_OPE_NAME + " = " + new String(twanOperatorName,CommonConstants.UTF8));
			}
			if (isSet(LAII_BIT)) {
				stringBuilder.append("\n\t\t" + RELAY_ID_TYPE + " = " + relayIdentityType);
				if (isNullOrEmpty(relayIdentity) == false) {
					stringBuilder.append("\n\t\t" + RELAY_ID + " = " + bytesToHex(relayIdentity));
				}
				if (RadiusUtility.isNullOrEmpty(circuitID) == false) {
					stringBuilder.append("\n\t\t" + CIRCUIT_ID + " = " + bytesToHex(circuitID));
				}

			}
		} catch (UnsupportedEncodingException ex) {
			LogManager.getLogger().warn(MODULE, "Invalid data bytes are received in TWAN identifier attribute");
			throw new IllegalArgumentException(ex);
		}
		return stringBuilder.toString();
	}

	@Override
	public String getKeyValue(String key) {
		String value = null;
		try {
			if (key.equalsIgnoreCase(SSID_NAME)) {
				value = new String(ssid,CommonConstants.UTF8);
			} else if (key.equalsIgnoreCase(BSSID_NAME)) {
				value = bytesToHex(bssid);
			} else if (key.equalsIgnoreCase(CIVIC_ADD_INFO)) {
				value = bytesToHex(civicAddressInformation);
			} else if (key.equalsIgnoreCase(TWAN_PLMN_ID_NAME)) {
				value = bytesToHex(twanPlmnId);
			} else if (key.equalsIgnoreCase(TWAN_OPE_NAME)) {
				value = new String(twanOperatorName,CommonConstants.UTF8);
			} else if (key.equalsIgnoreCase(RELAY_ID_TYPE)) {
				value = String.valueOf(relayIdentityType);
			} else if (key.equalsIgnoreCase(RELAY_ID)) {
				value = bytesToHex(relayIdentity);
			} else if (key.equalsIgnoreCase(CIRCUIT_ID)) {
				value = bytesToHex(circuitID);
			} 
		} catch (UnsupportedEncodingException ex) {
			LogManager.getLogger().warn(MODULE, "Invalid data bytes are received in TWAN identifier attribute");
			throw new IllegalArgumentException(ex);
		}
		return value;
	}


	private void setProperty(String key, String val) {

		try {
			if (SSID_NAME.equalsIgnoreCase(key)) {
				ssid = val.getBytes();
				ssidLength = this.ssid.length;

				validateInRange(ssidLength, ZERO, ONE_BYTE, SSID_NAME);

			} else if (BSSID_NAME.equalsIgnoreCase(key)) {
				bssid = getBytesFromHexValue(val);

				validateInRange(bssid.length, MAX_BSSID_SIZE_IN_BYTES, BSSID_NAME);
				flag = (byte) (flag | BSSID_BIT);

			} else if (CIVIC_ADD_INFO.equalsIgnoreCase(key)) {
				civicAddressInformation = getBytesFromHexValue(val);
				civicAddressLength = civicAddressInformation.length;

				validateInRange(civicAddressLength, ZERO, ONE_BYTE, CIVIC_ADD_INFO);
				flag = (byte) (flag | CIVAI_BIT);

			} else if (TWAN_PLMN_ID_NAME.equalsIgnoreCase(key)) {
				twanPlmnId = getBytesFromHexValue(val);

				validateInRange(twanPlmnId.length, TWAN_PLMN_ID_SIZE_IN_BYTES, TWAN_PLMN_ID_NAME);
				flag = (byte) (flag | PLMNI_BIT);

			} else if (TWAN_OPE_NAME.equalsIgnoreCase(key)) {
				twanOperatorName = val.getBytes();
				twanOperatorNameLength = twanOperatorName.length;

				validateInRange(twanOperatorNameLength, ZERO, ONE_BYTE, TWAN_OPE_NAME);
				flag = (byte) (flag | OPNIN_BIT);
			} else if (RELAY_ID_TYPE.equalsIgnoreCase(key)) {
				relayIdentityType = Integer.parseInt(val);
				validateRelayIdentityType();

			} else if (RELAY_ID.equalsIgnoreCase(key)) {
				relayIdentity = getBytesFromHexValue(val);
				relayIdentityLength = relayIdentity.length;

				validateInRange(relayIdentityLength, ZERO, ONE_BYTE, RELAY_ID);
			} else if(CIRCUIT_ID.equalsIgnoreCase(key)) {
				circuitID = getBytesFromHexValue(val);
				circuitIDLength = circuitID.length;

				validateInRange(circuitIDLength, ZERO, ONE_BYTE, CIRCUIT_ID);
				flag = (byte) (flag | LAII_BIT);
			}  else {
				LogManager.getLogger().warn(MODULE, "Invalid key " + key + "is configured, so " + val + " will not added TWAN-Identifier-attribute " );
			}
		} catch (NumberFormatException nfe) {
			if (LogManager.getLogger().isWarnLogLevel()) {
				LogManager.getLogger().warn(MODULE, key + " is configured with invalid value: " + val);
			}
			throw new IllegalArgumentException(nfe);
		}

	}

	private void validateRelayIdentityType() {
		if (relayIdentityType != RELAY_IDENTITY_TYPE_IPV4_6 && relayIdentityType != RELAY_IDENTITY_TYPE_FQDN) {
			LogManager.getLogger().warn(MODULE, "Given relay identity type "+ relayIdentityType + " is invalid. It should be 0 for IPV4/IPV6 or 1 for FQDN.");
			throw new IllegalArgumentException("Invalid relay identity type: " + relayIdentityType);
		}
	}

	private void validateInRange(long fieldValue, long minValue, long maxValue, String fieldName) {
		if (fieldValue < minValue || fieldValue > maxValue) {
			throw new IllegalArgumentException(fieldName + " value: " + fieldValue + " does not fall in closed range [" + minValue + " - " + maxValue + "]");
		}
	}

	private void validateInRange(long fieldValue, long size, String fieldName) {
		if (fieldValue != size) {
			throw new IllegalArgumentException("Size of " + fieldName + " is " + fieldValue + ". It should be of size " + size);
		}
	}

	private byte[] getTWANIdentifierValueBytes() {
		ByteArrayOutputStream out = new ByteArrayOutputStream();
		writeBytesSilently(out, flag);
		writeBytesSilently(out, (byte) (ssidLength & 0xFF));
		if (ssidLength > 0) {
			writeBytesSilently(out, ssid);
		}
		if (isSet(BSSID_BIT)) {
			writeBytesSilently(out, bssid);
		}
		if (isSet(CIVAI_BIT)) {
			writeBytesSilently(out, (byte)(civicAddressLength & 0xFF));
			writeBytesSilently(out, civicAddressInformation);
		}
		if (isSet(PLMNI_BIT)) {
			writeBytesSilently(out, twanPlmnId);
		}
		if (isSet(OPNIN_BIT)) {
			writeBytesSilently(out, (byte) (twanOperatorNameLength & 0xFF));
			writeBytesSilently(out, twanOperatorName);
		}
		if (isSet(LAII_BIT)) {
			writeBytesSilently(out, (byte) (relayIdentityType & 0xFF));
			writeBytesSilently(out, (byte) (relayIdentityLength & 0xFF));
			writeBytesSilently(out, relayIdentity);
			writeBytesSilently(out, (byte) (circuitIDLength & 0xFF));
			writeBytesSilently(out, circuitID);
		}
		return out.toByteArray();
	}

}
