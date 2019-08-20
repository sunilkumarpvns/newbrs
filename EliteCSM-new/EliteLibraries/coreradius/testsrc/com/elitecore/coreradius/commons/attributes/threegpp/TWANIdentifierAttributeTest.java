package com.elitecore.coreradius.commons.attributes.threegpp;

import static com.elitecore.coreradius.commons.util.RadiusUtility.bytesToHex;
import static com.elitecore.coreradius.commons.util.RadiusUtility.getBytesFromHexValue;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import java.io.ByteArrayInputStream;
import java.io.IOException;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import com.elitecore.coreradius.commons.util.RadiusDictionaryTestHarness;

public class TWANIdentifierAttributeTest {

	private static final String INVALID_BSSID_VALUE = "=0x0078880955e000";
	private static final String TWAN_PLMN_ID_FIVE_BYTES = "=0x0078880955";
	private static final String SSID_NAME = "SSID";
	private static final String BSSID_NAME = "BSSID";
	private static final String CIVIC_ADD_INFO = "Civic_Address_Information";
	private static final String TWAN_PLMN_ID_NAME = "TWAN_PLMN_ID";
	private static final String TWAN_OPE_NAME = "TWAN_Operator_Name";
	private static final String RELAY_ID_TYPE = "Relay_Identity_Type";
	private static final String RELAY_ID = "Relay_Identity";
	private static final String CIRCUIT_ID = "Circuit_ID";
	private static final String VALUE_IN_HEX = "0x1f0c4f66666c6f616420546573740078880955e00600788"
			+ "80955e00078880908766f6461666f6e6501060078880955e0060078880955e0";
	private static final String SSID_VALUE = "Offload Test";
	private static final String BSSID_VALUE = "0x0078880955e0";
	private static final String CIVIC_INFO_VALUE = "0x0078880955e0";
	private static final String TWAN_PLMN_ID_VALUE = "0x00788809";
	private static final String TWAN_OPE_NAME_VALUE = "vodafone";
	private static final String RELAY_ID_TYPE_VALUE = "1";
	private static final String RELAY_ID_VALUE = "0x0078880955e0";
	private static final String CIRCUIT_ID_VALUE = "0x0078880955e0";
	private static final String ASSIGNMENT_OPEATOR = "=";
	private static final String AVP_SEPARATOR = ";";

	private static final String VALUE_IN_STRING = SSID_NAME + ASSIGNMENT_OPEATOR+ SSID_VALUE +AVP_SEPARATOR + BSSID_NAME + ASSIGNMENT_OPEATOR+ BSSID_VALUE + AVP_SEPARATOR + CIVIC_ADD_INFO + ASSIGNMENT_OPEATOR+ CIVIC_INFO_VALUE
			+ AVP_SEPARATOR+ TWAN_PLMN_ID_NAME + ASSIGNMENT_OPEATOR+ TWAN_PLMN_ID_VALUE + AVP_SEPARATOR + TWAN_OPE_NAME + ASSIGNMENT_OPEATOR+  TWAN_OPE_NAME_VALUE + AVP_SEPARATOR +RELAY_ID_TYPE + ASSIGNMENT_OPEATOR + RELAY_ID_TYPE_VALUE +
			AVP_SEPARATOR + RELAY_ID + ASSIGNMENT_OPEATOR + RELAY_ID_VALUE + AVP_SEPARATOR+ CIRCUIT_ID +ASSIGNMENT_OPEATOR+ CIRCUIT_ID_VALUE;

	private TWANIdentifierAttribute twanIdentifier;

	@Rule public ExpectedException exception = ExpectedException.none();

	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setUp() {
		twanIdentifier = new TWANIdentifierAttribute();
	}

	@Test
	public void attributeValueCanBeGivenInKeyValueFormat() {
		String attributeValue = SSID_NAME + ASSIGNMENT_OPEATOR + SSID_VALUE + AVP_SEPARATOR + 
				BSSID_NAME + ASSIGNMENT_OPEATOR + BSSID_VALUE;

		twanIdentifier.setStringValue(attributeValue);

		assertThat(twanIdentifier.getStringValue(), is(equalTo(attributeValue)));
	}

	@Test
	public void attributeValueWillBeParsedAndReturnsSameValueIfItIsValid() {
		String attributeValue = SSID_NAME + ASSIGNMENT_OPEATOR + SSID_VALUE + AVP_SEPARATOR + 
				BSSID_NAME + ASSIGNMENT_OPEATOR + BSSID_VALUE;

		twanIdentifier.setStringValue(attributeValue);

		assertThat(twanIdentifier.getStringValue(), is(equalTo(attributeValue)));

	}

	@Test
	public void throwsIllegalArgumentExceptionIfValueFieldIsEmpty() {
		String attributevalue = "";

		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("Null or blank string is not a valid value for TWAN Identifier Attribute");

		twanIdentifier.setStringValue(attributevalue);

	}

	@Test
	public void baseStationIdentifierIdMustBeOfSixBytes() {
		String packet = BSSID_NAME + INVALID_BSSID_VALUE;

		exception.expect(IllegalArgumentException.class);

		twanIdentifier.setStringValue(packet);

	}

	@Test
	public void twanPlmnIdMustBeOfFourBytes() {
		String packet = TWAN_PLMN_ID_NAME + TWAN_PLMN_ID_FIVE_BYTES;

		exception.expect(IllegalArgumentException.class);

		twanIdentifier.setStringValue(packet);

	}

	@Test
	public void invalidKeyValuePairIsIgnoredFromAttributeValue() {
		String attributeValue = SSID_NAME + ASSIGNMENT_OPEATOR + SSID_VALUE + AVP_SEPARATOR + BSSID_NAME + ASSIGNMENT_OPEATOR + BSSID_VALUE + AVP_SEPARATOR + "Invalidkey = value";

		twanIdentifier.setStringValue(attributeValue);

		assertThat(twanIdentifier.getStringValue(), is(equalTo(String.format(SSID_NAME + "%s" + BSSID_NAME + "%s", 
				ASSIGNMENT_OPEATOR + SSID_VALUE + AVP_SEPARATOR, ASSIGNMENT_OPEATOR + BSSID_VALUE))));
	}

	@Test
	public void attributeValueCanBeGivenInHexString() {
		String packet = VALUE_IN_HEX;

		twanIdentifier.setValueBytes(getBytesFromHexValue(packet));

		assertThat(twanIdentifier.getStringValue(), is(equalTo(VALUE_IN_STRING)));

	}

	@Test
	public void packetCanBeEncodedInBytesAsPerStringValue() {
		twanIdentifier.setStringValue(VALUE_IN_STRING);

		assertThat(bytesToHex(twanIdentifier.getValueBytes()), is(equalTo(VALUE_IN_HEX)));

	}

	@Test
	public void givesIndividualValueOfFieldUsingItsKey() {
		twanIdentifier.setStringValue(VALUE_IN_STRING);

		assertThat(twanIdentifier.getKeyValue(SSID_NAME), is(equalTo(SSID_VALUE)));
		assertThat(twanIdentifier.getKeyValue(BSSID_NAME), is(equalTo(BSSID_VALUE)));
		assertThat(twanIdentifier.getKeyValue(CIVIC_ADD_INFO), is(equalTo(CIVIC_INFO_VALUE)));
		assertThat(twanIdentifier.getKeyValue(TWAN_PLMN_ID_NAME),is(equalTo(TWAN_PLMN_ID_VALUE)));
		assertThat(twanIdentifier.getKeyValue(TWAN_OPE_NAME), is(equalTo(TWAN_OPE_NAME_VALUE)));
		assertThat(twanIdentifier.getKeyValue(RELAY_ID_TYPE), is(equalTo(RELAY_ID_TYPE_VALUE)));
		assertThat(twanIdentifier.getKeyValue(RELAY_ID), is(equalTo(RELAY_ID_VALUE)));
		assertThat(twanIdentifier.getKeyValue(CIRCUIT_ID), is(equalTo(CIRCUIT_ID_VALUE)));
	}

	@Test
	public void throwsIllegalArgumentExceptionIfRelayTypeValueIsOtherThanZeroOrOne() {
		String packet = RELAY_ID_TYPE + "=2";

		exception.expect(IllegalArgumentException.class);

		twanIdentifier.setStringValue(packet);

	}

	@Test
	public void throwsIllegalArgumentExceptionIfAnyFieldLengthValueIsGreaterThanRemainingValueBytesOfAttribute() {
		String packet = "0x01ff4f66666c6f616420546573740078880955e0";

		exception.expect(IllegalArgumentException.class);

		twanIdentifier.setValueBytes(getBytesFromHexValue(packet));

	}

	@Test
	public void attributeValueCanBeParsedUsingReadLengthOnwardMethod() throws IOException {
		ByteArrayInputStream in = new ByteArrayInputStream(getBytesFromHexValue("0x6e1f0c4f66666c6f616420546573740078880955e00600788"
				+ "80955e00078880908766f6461666f6e6501060078880955e0060078880955e0"));

		twanIdentifier.readLengthOnwardsFrom(in);

		assertThat(twanIdentifier.getStringValue(), is(equalTo(VALUE_IN_STRING)));

	}

}
