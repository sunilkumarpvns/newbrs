package com.elitecore.coreradius.commons.attributes;

import com.elitecore.commons.net.AddressResolver;
import com.elitecore.commons.net.FakeAddressResolver;
import com.elitecore.coreradius.commons.attributes.ericsson.GTPv1TunnelDataAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import junitparams.JUnitParamsRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.UnknownHostException;
import java.util.Arrays;

import static org.junit.Assert.*;

@RunWith(JUnitParamsRunner.class)
public class GTPv1TunnelDataAttributeTest {
	private static final String ericssonDic = "<attribute-list vendorid=\"193\" vendor-name=\"ericsson\" avpair-separator=\";\"><attribute id=\"227\" name=\"GTPv1-Tunnel-Data\" type=\"gtpv1tunnel\"/></attribute-list>";

	private GTPv1TunnelDataAttribute gtpv1TunnelData;
	private AddressResolver fakeAddressResolver;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		StringReader reader = new StringReader(ericssonDic);
		Dictionary.getInstance().load(reader);
	}
	
	@Before
	public void setUp() throws UnknownHostException {
		fakeAddressResolver = new FakeAddressResolver();
		gtpv1TunnelData = new GTPv1TunnelDataAttribute(fakeAddressResolver);
	}
	
	@Test(expected = InvalidRadiusAttributeLengthException.class)
	public void testSetValueBytes_ShouldThrowInvalidRadiusAttributeLengthException_WhenValueBytesAreNull() {
		gtpv1TunnelData.setValueBytes(null);
	}
	
	@Test(expected = InvalidRadiusAttributeLengthException.class)
	public void testSetBytes_ShouldThrowInvalidRadiusAttributeLengthException_WhenEmptyValueBytesArePassed(){
		gtpv1TunnelData.setValueBytes(new byte[0]);
	}

	@Test
	@junitparams.Parameters(method = "dataFor_testSetValueBytesWithIOExceptionAsCause")
	public void testSetValueBytes_ShouldThrowIllegalArgumentExceptionWithIOExceptionAsCause_WhenSufficientBytesNotProvided(String hexValue) {
		try {
			gtpv1TunnelData.setValueBytes(RadiusUtility.getBytesFromHexValue(hexValue));
			fail("Expected exception: IllegalArgumentException with IOException as cause is not thrown");
		} catch(Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(IOException.class, t.getCause().getClass());
		}
	}
	
	public static Object[][] dataFor_testSetValueBytesWithIOExceptionAsCause() {
		return new Object[][] {
				/*
				 * if less bytes are provided (i.e. secondary ip address is not provided)
				 * Result: throw IllegalArgumentException Exception as value for GTPv1 Tunnel Data is not proper 
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * if less bytes (not from boundary) are provided (i.e. primary ip address is not provided)
				 * Result: throw IllegalArgumentException Exception as value for GTPv1 Tunnel Data is not proper
				 */
				{"0x01" +
						"04" +
						"02" +
						"636f6f6c" +
						"0008" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"047f000001"},	
		};
	}
	
	@Test
	public void testSetValueBytes_ShouldReturnIllegalArgumentExceptionWithUnknownHostExceptionAsCause_WhenInvalidIPAddressIsPassed() throws UnknownHostException {
		String hexValue = "0x01" +
				"04" +
				"02" +
				"636f6f6c" +
				"0008" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"047f000001" +
				"040a6a0166";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);
		try {
			gtpv1TunnelData.setValueBytes(valueBytes);
			fail("Expected exception: IllegalArgumentException with UnknownHostException as cause is not thrown");
		} catch(Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(UnknownHostException.class, t.getCause().getClass());
		}
	}
	
	/*
	 * passing less bytes, that is less than 16 bytes, it must throw InvalidRadiusAttributeLengthException
	 */
	@Test(expected = InvalidRadiusAttributeLengthException.class)
	public void testSetValueBytes_ShouldReturnInvlaidRadiusAttributeLengthException_WhenLessBytesArePassed() {
		String hexValue = "0x000000000000047f000001047f0000";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);		
		gtpv1TunnelData.setValueBytes(valueBytes);
	}

	@Test
	public void testReadLengthOnwardsFrom_ShouldGetSuccess_WhenLengthPrependedToValueBytes() {
		String hexString = "0x2C" +
				"02" +
				"04" +
				"02" +
				"636f6f6c" +
				"0008" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"047f000001" +
				"047f000001";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexString);
		ByteArrayInputStream sourceStream = new ByteArrayInputStream(valueBytes);
		int actualLength = 0;
		try {
			actualLength = gtpv1TunnelData.readLengthOnwardsFrom(sourceStream);
		} catch (IOException e) {
			fail("testReadLengthOnwardsFrom failed, Reason: " + e.getMessage());
		}
		byte[] expectedValueBytes = Arrays.copyOfRange(valueBytes, 1, valueBytes.length);
		assertArrayEquals("readLengthOnwardsFrom failed for GTPv1TunnelData not proper", expectedValueBytes, gtpv1TunnelData.getValueBytes());
		int expectedLength = valueBytes.length;
		assertEquals("readLengthOnwardsFrom failed for GTPv1TunnelData length not proper", expectedLength, actualLength);
	}
	
	@Test
	public void testReadFrom_ShouldGetSuccess_WhenTypeAndLengthPrependedToValueBytes() {
		String hexString = "0x012C" +
				"02" +
				"04" +
				"02" +
				"636f6f6c" +
				"0008" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"047f000001" +
				"047f000001";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexString);
		ByteArrayInputStream sourceStream = new ByteArrayInputStream(valueBytes);
		int actualLength = 0;
		try {
			actualLength = gtpv1TunnelData.readFrom(sourceStream);
		} catch (IOException e) {
			fail("testReadFrom failed, Reason: " + e.getMessage());
		}
		byte[] expectedBytes = Arrays.copyOfRange(valueBytes, 2, valueBytes.length);
		assertTrue("readFrom failed for GTPv1TunnelData bytes not proper", Arrays.equals(expectedBytes, gtpv1TunnelData.getValueBytes()));
		int expectedLength = valueBytes.length;
		assertEquals("readFrom failed for GTPv1TunnelData length not proper", expectedLength, actualLength);
	}
	
	@Test
	public void testToString() {
		String value = "APNRES=2;QOS-ARP-PRIORITY=2;QOS-PROFILE=abc;CC=8;APNNAME=accesspoint;P-GGSN=127.0.0.1;S-GGSN=127.0.0.1";
		gtpv1TunnelData.setStringValue(value);
		
		String valueReceivedInToString = gtpv1TunnelData.toString();
		assertTrue(valueReceivedInToString.contains("APNRES = 2"));
		assertTrue(valueReceivedInToString.contains("QOS-ARP-PRIORITY = 2"));
		assertTrue(valueReceivedInToString.contains("QOS-PROFILE = abc"));
		assertTrue(valueReceivedInToString.contains("CC = 8"));
		assertTrue(valueReceivedInToString.contains("APNNAME = accesspoint"));
		assertTrue(valueReceivedInToString.contains("P-GGSN = 127.0.0.1"));
		assertTrue(valueReceivedInToString.contains("S-GGSN = 127.0.0.1"));
	}
	
	@Test
	public void testSetValueBytesAndGetStringValue_ShouldSuccess_WhenSetingValueBytesOnce() {
		String hexValue = "0x01" +
				"04" +
				"02" +
				"636f6f6c" +
				"0008" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"047f000001" +
				"047f000001";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);
		gtpv1TunnelData.setValueBytes(valueBytes);
		String receivedString = gtpv1TunnelData.getStringValue();
		String expectedtring = "APNRES=1;QOS-ARP-PRIORITY=2;QOS-PROFILE=cool;CC=8;APNNAME=internet.unitel.co.ao;P-GGSN=127.0.0.1;S-GGSN=127.0.0.1";
		assertEquals("setValueBytes and getStringValue are not sync properly", expectedtring, receivedString);
	}
	
	@Test
	public void testSetValueBytesAndGetStringValue_ShouldSuccess_WhenSetingValueBytesMultipleTimes() {
		String hexValue = "0x01" +
				"04" +
				"02" +
				"636f6f6c" +
				"0008" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"047f000001" +
				"047f000001";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);
		gtpv1TunnelData.setValueBytes(valueBytes);
		
		valueBytes = RadiusUtility.getBytesFromHexValue("0x020402636f6f6c00081608696e7465726e657406756e6974656c02636f02616f047f000001047f000001");
		gtpv1TunnelData.setValueBytes(valueBytes);
		
		String receivedString = gtpv1TunnelData.getStringValue();
		String expectedtring = "APNRES=2;QOS-ARP-PRIORITY=2;QOS-PROFILE=cool;CC=8;APNNAME=internet.unitel.co.ao;P-GGSN=127.0.0.1;S-GGSN=127.0.0.1";
		assertEquals("setValueBytes and getStringValue are not sync properly", expectedtring, receivedString);
	}
	
	@Test
	public void testSetStringValue_ShouldSetPropertiesProperly_WhenProperCharSetIsPassed() {
		String valueConfigured = "APNRES=2;QOS-ARP-PRIORITY=2;QOS-PROFILE=xyz;CC=8;APNNAME=abc;P-GGSN=127.0.0.1;S-GGSN=127.0.0.1";
		try {
			gtpv1TunnelData.setStringValue(valueConfigured, "UTF-8");
			assertEquals("value configured for GTPTunnelData is not proper", valueConfigured, gtpv1TunnelData.getStringValue("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			fail("testSetStringValueWithCharSet failed, Reason: " + e.getMessage());
		}
	}
	
	@Test(expected = UnsupportedEncodingException.class)
	public void testSetStringValue_ShouldThrowUnsupportedEncodingException_WhenInvalidCharSetIsPassed() throws UnsupportedEncodingException {
		String valueConfigured = "APNRES=2;QOS-ARP-PRIORITY=2;QOS-PROFILE=xyz;CC=8;APNNAME=abc;P-GGSN=127.0.0.1;S-GGSN=127.0.0.1";
		gtpv1TunnelData.setStringValue(valueConfigured, "ABC");
	}
	
	@Test
	public void testSetStringValue_ShouldReturnDefaultValues_WhenValuesAreNull() {
		String defaultValues = "APNRES=0;QOS-ARP-PRIORITY=0;QOS-PROFILE=;CC=0;APNNAME=;P-GGSN=0.0.0.0;S-GGSN=0.0.0.0";
		try {
			gtpv1TunnelData.setStringValue(null, "UTF-8");
			assertEquals("value configured for GTPTunnelData is not proper", defaultValues, gtpv1TunnelData.getStringValue("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			fail("testSetStringValueWithCharSet failed, Reason: " + e.getMessage());
		}
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_testSetAndGetValueBytesForSuccessfulCases")
	public void testSetAndGetValueBytes_ShouldGetSuccess_ForSuccessfulCases(String hexString) {
		try {
			byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexString);
			gtpv1TunnelData.setValueBytes(valueBytes);
			byte[] returnValueBytes = gtpv1TunnelData.getValueBytes();
			assertEquals("Bytes for GTPv1TunnelData are not proper", RadiusUtility.bytesToHex(valueBytes), RadiusUtility.bytesToHex(returnValueBytes));
		} catch(Throwable t) {
			t.printStackTrace();
			fail("Unexpected exception received: " + t);
		}
	}
	
	public Object[][] dataFor_testSetAndGetValueBytesForSuccessfulCases() {
		return new Object[][] {
				/*
				 * Proper Bytes (successful case)
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * if more bytes are provided then extra bytes are discarded and passed successfully
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001" +	//secondary GGSN Address
						"0011"},	//extra bytes
				
				/*
				 * APNRES=0
				 */
				{"0x00" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address

				/*
				 * APNRES=3
				 */
				{"0x03" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address				
				
				
				/*
				 * QOS-ARP-PRIORITY=0
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"00" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * QOS-ARP-PRIORITY=255
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"ff" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * QOS-PROFILE=ABC.xyz@pqr123 
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"4142432e" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * CC=0
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0000" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * CC=65535
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"ffff" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * APNNAME=internet.ulticom.co.au
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * P-GGSN=255.255.255.255
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * S-GGSN=255.255.255.255
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_testSetAndGetValueBytesForFailureCases")
	public void testSetAndGetValueBytes_ShouldThrowSomeException_ForFailureCases(String hexString, Class<? extends Exception> expectedException) {
		try {
			byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexString);
			gtpv1TunnelData.setValueBytes(valueBytes);
			fail("Expected exception: " + expectedException + "is not thrown");
		} catch(Throwable t) {
			assertEquals(expectedException, t.getClass());
		}
	}
	
	public Object[][] dataFor_testSetAndGetValueBytesForFailureCases() {
		return new Object[][] {
				/*
				 * APNRES=5
				 */
				{"0x05000000000004000000000400000000",IllegalArgumentException.class},
		};
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetStringValue_ShouldReturnIllegalArgumentException_WhenValuePassedAsNull() {
		gtpv1TunnelData.setStringValue(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetStringValue_ShouldReturnIllegalArgumentException_WhenEmptyValueIsPassed() {
		gtpv1TunnelData.setStringValue("");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetStringValue_ShouldReturnIllegalArgumentException_WhenNoOfKeyValuePairIsOutOfRange() {
		gtpv1TunnelData.setStringValue("SPARE1=0;APNRES=1;QOS-ARP-PRIORITY=2;QOS-PROFILE=cool;CC=8;APNNAME=abcd;P-GGSN=127.0.0.1;S-GGSN=127.0.0.1");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetStringValue_ShouldThrowIllegalArgumentException_WhenImproperHexValueIsPassed() {
		String value = "010402636f6f6c0008076b756c64656570047f000001047f000001"; 
		gtpv1TunnelData.setStringValue(value);
	}
	
	@Test
	public void testSetStringValue_ShouldThrowIllegalArgumentExceptionWithNumberFormatExceptionAsCause_WhenUnparsableValueIsPassed() {
		try {
			gtpv1TunnelData.setStringValue("APNRES=a");
			fail("Expected exception: IllegalArgumentException with NumberFormatException as cause is not thrown");
		} catch (Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(NumberFormatException.class, t.getCause().getClass());
		}
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_testSetAndGetStringValueForSuccessfulCases")
	public void testSetAndGetStringValue_ShouldGetSuccess_ForSuccessfulCases(String valueConfigured) {
		try {
			gtpv1TunnelData.setStringValue(valueConfigured);
			assertEquals("value configured for GTPv1TunnelData is not proper", generateExpectedString(), gtpv1TunnelData.getStringValue());
		} catch(Throwable t) {
			fail("Unexpected exception received");
		}
	}
	
	public Object[][] dataFor_testSetAndGetStringValueForSuccessfulCases() {
		return new Object[][] {
				/*
				 * Successful scenario
				 */
				{"APNRES=1;QOS-ARP-PRIORITY=2;QOS-PROFILE=cool;CC=8;APNNAME=abcd;P-GGSN=127.0.0.1;S-GGSN=127.0.0.1"},
				
				
				{"APNRES=0"},
				
				{"APNRES=2"},
				
				{"APNRES=3"},

				
				{"QOS-ARP-PRIORITY=0"},
				
				{"QOS-ARP-PRIORITY=5"},
				
				{"QOS-ARP-PRIORITY=255"},
				
				
				{"QOS-PROFILE=ABC.xyz@pqr123"},

				
				{"CC=0"},
				
				{"CC=8"},
				
				{"CC=65535"},
				
				{"CC=0x0008"},
				
				{"CC=0X0008"},
				

				{"APNNAME=ABC.xyz@pqr123"},
				

				{"P-GGSN=127.0.0.1"},

				{"S-GGSN=127.0.0.1"},
				
				/*
				 * passing hexvalue to setStringValue
				 */
				{"0x01" +	//APN RES
						"04" +	//length
						"02" +	//ARP (priority)
						"636f6f6c" +	//QOS Profile Data
						"0008" +	//CC value
						"16" +	//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name
						"047f000001" +	//primary GGSN Address
						"047f000001"},	//secondary GGSN Address
				
				/*
				 * passing ;;
				 * Result it will throw IllegalArgumentException
				 */
				{";APNRES=2;"},
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_testSetAndGetStringValueForFailureCases")
	public void testSetAndGetStringValue_ShouldThrowSomeException_ForFailureCases(String valueConfigured, Class<? extends Exception> expectedException) {
		try {
			gtpv1TunnelData.setStringValue(valueConfigured);
			fail("Expected exception: " + expectedException + " is not thrown");
		} catch(Throwable t) {
			assertEquals(expectedException, t.getClass());
		}
	}
	
	public Object[][] dataFor_testSetAndGetStringValueForFailureCases() {
		return new Object[][] {
				
				{"APNRES=-1" , IllegalArgumentException.class},
				
				{"APNRES=5" , IllegalArgumentException.class},
				
				
				{"QOS-ARP-PRIORITY=-1" , IllegalArgumentException.class},
				
				{"QOS-ARP-PRIORITY=256" , IllegalArgumentException.class},
				
				
				{"QOS-PROFILE=" , IllegalArgumentException.class},
				
				
				{"CC=-1" , IllegalArgumentException.class},
				
				{"CC=65536" , IllegalArgumentException.class},

				{"APNNAME=" , IllegalArgumentException.class},
				
				/*
				 * passing ;;
				 * Result it will throw IllegalArgumentException
				 */
				{";;" , IllegalArgumentException.class},
				
				{"=1",IllegalArgumentException.class},
		};
	}
	
	private String generateExpectedString() {
		StringBuilder builder = new StringBuilder();
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.ERICSSON_VENDOR_ID);
		builder.append(GTPv1TunnelDataAttribute.APN_RESTRICTION + "=" + gtpv1TunnelData.getValueFromKey(GTPv1TunnelDataAttribute.APN_RESTRICTION))
		.append(AVPAIR_SEPERATOR + GTPv1TunnelDataAttribute.ARP + "=" + gtpv1TunnelData.getValueFromKey(GTPv1TunnelDataAttribute.ARP))
		.append(AVPAIR_SEPERATOR + GTPv1TunnelDataAttribute.QOS_PROFILE_DATA + "=" + gtpv1TunnelData.getValueFromKey(GTPv1TunnelDataAttribute.QOS_PROFILE_DATA))
		.append(AVPAIR_SEPERATOR + GTPv1TunnelDataAttribute.CHARGING_CHARACTERISTICS + "=" + gtpv1TunnelData.getValueFromKey(GTPv1TunnelDataAttribute.CHARGING_CHARACTERISTICS))
		.append(AVPAIR_SEPERATOR + GTPv1TunnelDataAttribute.APN_NAME + "=" + gtpv1TunnelData.getValueFromKey(GTPv1TunnelDataAttribute.APN_NAME))
		.append(AVPAIR_SEPERATOR + GTPv1TunnelDataAttribute.PRIMARY_GGSN + "=" + gtpv1TunnelData.getValueFromKey(GTPv1TunnelDataAttribute.PRIMARY_GGSN))
		.append(AVPAIR_SEPERATOR + GTPv1TunnelDataAttribute.SECONDARY_GGSN + "=" + gtpv1TunnelData.getValueFromKey(GTPv1TunnelDataAttribute.SECONDARY_GGSN));
		return builder.toString();
	}
	
	@Test
	public void testSetStringValueForPrimaryGGSN_ShouldThrowIllegalArgumentExceptionWithUnknownHostExceptionAsCause_WhenInvalidIPIsPassed() throws UnknownHostException {
		try {
			gtpv1TunnelData.setStringValue("P-GGSN=invalid");
			fail("Expected exception: IllegalArgumentException with UnknownHostException as cause is not thrown");
		} catch(Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(UnknownHostException.class, t.getCause().getClass());
		}
	}
	
	@Test
	public void testSetStringValueForSecondaryGGSN_ShouldThrowIllegalArgumentExceptionWithUnknownHostExceptionAsCause_WhenInvalidIPIsPassed() throws UnknownHostException {
		try {
			gtpv1TunnelData.setStringValue("S-GGSN=invalid");
			fail("Expected exception: IllegalArgumentException with UnknownHostException as cause is not thrown");
		} catch(Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(UnknownHostException.class, t.getCause().getClass());
		}
	}
}
