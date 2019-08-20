package com.elitecore.coreradius.commons.attributes;

import static org.junit.Assert.assertArrayEquals;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.commons.net.AddressResolver;
import com.elitecore.coreradius.commons.attributes.ericsson.BearerQualityOfService;
import com.elitecore.coreradius.commons.attributes.ericsson.GTPTunnelDataAttribute;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;

import junitparams.JUnitParamsRunner;

@RunWith(JUnitParamsRunner.class)
public class GTPTunnelDataAttributeTest {
	
	private static String ericssonDic = "<attribute-list vendorid=\"193\" vendor-name=\"ericsson\" avpair-separator=\";\"><attribute id=\"226\" name=\"GTP-Tunnel-Data\" type=\"gtptunnel\"/></attribute-list>";
	private GTPTunnelDataAttribute gtpTunnelData;
	private AddressResolver mockAddressResolver;
	private static InetAddress localHost;
	
	@BeforeClass
	public static void setUpBeforeClass() throws Exception{
		StringReader reader = new StringReader(ericssonDic);
		Dictionary.getInstance().load(reader);
		localHost = InetAddress.getByAddress(new byte[] {127, 0, 0, 1});
	}
	
	@Before
	public void setUp() throws UnknownHostException{
		mockAddressResolver = mock(AddressResolver.class);
		when(mockAddressResolver.byName("127.0.0.1")).thenReturn(localHost);
		gtpTunnelData = new GTPTunnelDataAttribute(mockAddressResolver);
	}
	
	@Test(expected = InvalidRadiusAttributeLengthException.class)
	public void testSetValueBytes_ShouldThrowInvalidRadiusAttributeLengthException_WhenValueBytesAreNull(){
		gtpTunnelData.setValueBytes(null);
	}
	
	@Test(expected = InvalidRadiusAttributeLengthException.class)
	public void testSetValueBytes_ShouldThrowInvalidRadiusAttributeLengthException_WhenEmptyValueBytesArePassed(){
		gtpTunnelData.setValueBytes(new byte[0]);
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_testSetValueBytesWithIOExceptionAsCause")
	public void testSetValueBytes_ShouldThrowIllegalArgumentExceptionWithIOExceptionAsCause_WhenSufficientBytesNotProvided(String hexValue) {
		try {
			gtpTunnelData.setValueBytes(RadiusUtility.getBytesFromHexValue(hexValue));
			fail("Expected exception: IllegalArgumentException with IOException as cause is not thrown");
		} catch(Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(IOException.class, t.getCause().getClass());
		}
	}
	
	public static Object[][] dataFor_testSetValueBytesWithIOExceptionAsCause() {
		return new Object[][] {
				/*
				 * if less bytes (not from boundary) are provided (i.e. primary ip address is not provided)
				 * Result: throw InvalidRadiusAttributeLengthException as value for GTP Tunnel Data is not proper
				 */
				{"0x01" +	// spare1 and pdntype
						"00" +	//APN Restriction
						"0003e800" +	//APN - AMBR - Uplink
						"0003e800" +	//APN - AMBR - Downlink
							"7c" +	//ARP Parameter
							"09" +	//QCI
							"0000007d00" +	//MBR-Uplink
							"0000007d00" +	//MBR-Downlink
							"0000000000" +	//GBR-Uplink
							"0000000000" +	//GBR-Downlink
							"00" +	//spare4
						"0004" +	//CC
						"16" +		//APN Length
						"08696e7465726e657406756e6974656c02636f02616f" +	//APN Name	
						"04294e1271"},	//Primary PDN GW address
				
				/*
				 * if less bytes are provided (i.e. secondary ip address is not provided)
				 * Result: throw InvalidRadiusAttributeLengthException as value for GTP Tunnel Data is not proper 
				 */
				{"0x01" +
						"00" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"04294e1261"},
		};
	}
	
	@Test
	public void testSetValueBytes_ShouldThrowIllegalArgumentExceptionWithUnknownHostExceptionAsCause_WhenInvalidIPAddressIsPassed() {
		String hexValue = "0x01" +
				"00" +
				"0003e800" +
				"0003e800" +
					"7c" +
					"09" +
					"0000007d00" +
					"0000007d00" +
					"0000000000" +
					"0000000000" +
					"00" +
				"0004" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"04294e1261" +
				"05294e12716f";
		
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);		
		try {
			gtpTunnelData.setValueBytes(valueBytes);
			fail("Expected exception: IllegalArgumentException with UnknownHostException as cause is not thrown");
		} catch(Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(UnknownHostException.class, t.getCause().getClass());
		}
	}
	
	/*
	 * passing less bytes, that is less than 46 bytes, it must throw InvalidRadiusAttributeLengthException
	 */
	@Test(expected = InvalidRadiusAttributeLengthException.class)
	public void testSetValueBytes_ShouldReturnInvlaidRadiusAttributeLengthException_WhenLessBytesArePassed() {
		String hexValue = "0x010000000000000000004500000000000000000000000000000000000000000000000000047f000001047f0000";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);		
		gtpTunnelData.setValueBytes(valueBytes);
	}
	
	@Test
	public void testReadLengthOnwardsFrom_ShouldGetSuccess_WhenLengthPrependedToValueBytesArePassed() {
		String hexString = "0x46" + // Length
				"01" +
				"00" +
				"0003e800" +
				"0003e800" +
					"7c" +
					"09" +
					"0000007d00" +
					"0000007d00" +
					"0000000000" +
					"0000000000" +
					"00" +
				"0004" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"04294e1261" +
				"04294e1271";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexString);
		ByteArrayInputStream sourceStream = new ByteArrayInputStream(valueBytes);
		int actualLength = 0;
		try {
			 actualLength = gtpTunnelData.readLengthOnwardsFrom(sourceStream);
		} catch (Throwable t) {
			fail("testReadLengthOnwardsFrom failed, Reason: " + t.getMessage());
		}
		byte[] expectedValueBytes = Arrays.copyOfRange(valueBytes, 1, valueBytes.length);
		assertArrayEquals("readLengthOnwardsFrom failed for GTPTunnelData bytes not proper", expectedValueBytes, gtpTunnelData.getValueBytes());
		int expectedLength = 69;
		assertEquals("readLengthOnwardsFrom failed for GTPTunnelData length not proper", expectedLength, actualLength);
	}
	
	@Test
	public void testReadFrom_ShouldGetSuccess_WhenTypeAndLengthPrependedToValueBytesArePassed() {
		String hexString = "0x01" + // Type
				"46" + // Length
				"01" +
				"00" +
				"0003e800" +
				"0003e800" +
					"7c" +
					"09" +
					"0000007d00" +
					"0000007d00" +
					"0000000000" +
					"0000000000" +
					"00" +
				"0004" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"04294e1261" +
				"04294e1271";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexString);
		ByteArrayInputStream sourceStream = new ByteArrayInputStream(valueBytes);
		int actualLength = 0;
		try {
			actualLength = gtpTunnelData.readFrom(sourceStream);
		} catch (IOException e) {
			fail("testReadFrom failed, Reason: " + e.getMessage());
		}
		byte[] expectedBytes = Arrays.copyOfRange(valueBytes, 2, valueBytes.length);
		assertArrayEquals("readFrom failed for GTPTunnelData bytes not proper", expectedBytes, gtpTunnelData.getValueBytes());
		int expectedLength = 70;
		assertEquals("readFrom failed for GTPTunnelData length not proper", expectedLength, actualLength);
	}
	
	@Test
	public void testToString() {
		String value = "SPARE1=0;PDNTYPE=1;APNRES=0;AMBR-U=256000;AMBR-D=256000;SPARE2=0;QOSPCI=1;QOSPL=15;SPARE3=0;QOSPVI=0;QOSQCI=9;" +
				"QOSMBR-U=32000;QOSMBR-D=32000;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=8;APNNAME=accesspoint;P-PDN=127.0.0.1;S-PDN=127.0.0.1";
		gtpTunnelData.setStringValue(value);
		
		String valueReceivedInToString = gtpTunnelData.toString();
		assertTrue(valueReceivedInToString.contains("SPARE1 = 0"));
		assertTrue(valueReceivedInToString.contains("PDNTYPE = 1"));
		assertTrue(valueReceivedInToString.contains("APNRES = 0"));
		assertTrue(valueReceivedInToString.contains("AMBR-U = 256000"));
		assertTrue(valueReceivedInToString.contains("AMBR-D = 256000"));
		assertTrue(valueReceivedInToString.contains("SPARE2 = 0"));
		assertTrue(valueReceivedInToString.contains("QOSPCI = Disabled"));
		assertTrue(valueReceivedInToString.contains("QOSPL = 15"));
		assertTrue(valueReceivedInToString.contains("SPARE3 = 0"));
		assertTrue(valueReceivedInToString.contains("QOSPVI = Enabled"));
		assertTrue(valueReceivedInToString.contains("QOSQCI = 9"));
		assertTrue(valueReceivedInToString.contains("QOSMBR-U = 32000"));
		assertTrue(valueReceivedInToString.contains("QOSMBR-D = 32000"));
		assertTrue(valueReceivedInToString.contains("QOSGBR-U = 0"));
		assertTrue(valueReceivedInToString.contains("QOSGBR-D = 0"));
		assertTrue(valueReceivedInToString.contains("SPARE4 = 0"));
		assertTrue(valueReceivedInToString.contains("CC = 8"));
		assertTrue(valueReceivedInToString.contains("APNNAME = accesspoint"));
		assertTrue(valueReceivedInToString.contains("P-PDN = 127.0.0.1"));
		assertTrue(valueReceivedInToString.contains("S-PDN = 127.0.0.1"));
	}
	/*
	 * Here APNNAME and QOSPL is not provided so default value will be taken
	 */
	@Test
	public void testToString_ShouldContainsDefaultValue_WhenSomeParamIsNotProvided() {
		String value = "SPARE1=0;PDNTYPE=1;APNRES=0;AMBR-U=256000;AMBR-D=256000;SPARE2=0;QOSPCI=1;SPARE3=0;QOSPVI=0;QOSQCI=9;" +
				"QOSMBR-U=32000;QOSMBR-D=32000;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=8;P-PDN=127.0.0.1;S-PDN=127.0.0.1";
		gtpTunnelData.setStringValue(value);
		
		String valueReceivedInToString = gtpTunnelData.toString();
		assertTrue(valueReceivedInToString.contains("SPARE1 = 0"));
		assertTrue(valueReceivedInToString.contains("PDNTYPE = 1"));
		assertTrue(valueReceivedInToString.contains("APNRES = 0"));
		assertTrue(valueReceivedInToString.contains("AMBR-U = 256000"));
		assertTrue(valueReceivedInToString.contains("AMBR-D = 256000"));
		assertTrue(valueReceivedInToString.contains("SPARE2 = 0"));
		assertTrue(valueReceivedInToString.contains("QOSPCI = Disabled"));
		assertTrue(valueReceivedInToString.contains("QOSPL = 1"));
		assertTrue(valueReceivedInToString.contains("SPARE3 = 0"));
		assertTrue(valueReceivedInToString.contains("QOSPVI = Enabled"));
		assertTrue(valueReceivedInToString.contains("QOSQCI = 9"));
		assertTrue(valueReceivedInToString.contains("QOSMBR-U = 32000"));
		assertTrue(valueReceivedInToString.contains("QOSMBR-D = 32000"));
		assertTrue(valueReceivedInToString.contains("QOSGBR-U = 0"));
		assertTrue(valueReceivedInToString.contains("QOSGBR-D = 0"));
		assertTrue(valueReceivedInToString.contains("SPARE4 = 0"));
		assertTrue(valueReceivedInToString.contains("CC = 8"));
		assertTrue(valueReceivedInToString.contains("APNNAME = "));
		assertTrue(valueReceivedInToString.contains("P-PDN = 127.0.0.1"));
		assertTrue(valueReceivedInToString.contains("S-PDN = 127.0.0.1"));
	}
	
	@Test
	public void testSetValueBytesAndGetStringValue_ShouldSuccess_WhenSetingValueBytesOnce() {
		String hexValue = "0x01" +
				"00" +
				"0003e800" +
				"0003e800" +
					"7c" +
					"09" +
					"0000007d00" +
					"0000007d00" +
					"0000000000" +
					"0000000000" +
					"00" +
				"0004" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"04294e1261" +
				"04294e1271";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);
		gtpTunnelData.setValueBytes(valueBytes);
		String receivedString = gtpTunnelData.getStringValue();
		String expectedtring = "SPARE1=0;PDNTYPE=1;APNRES=0;AMBR-U=256000;AMBR-D=256000;SPARE2=0;QOSPCI=1;QOSPL=15;SPARE3=0;QOSPVI=0;QOSQCI=9;QOSMBR-U=32000;QOSMBR-D=32000;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=4;APNNAME=internet.unitel.co.ao;P-PDN=41.78.18.97;S-PDN=41.78.18.113";
		assertEquals("setValueBytes and getStringValue are not sync properly", expectedtring, receivedString);
	}
	
	@Test
	public void testSetValueBytesAndGetStringValue_ShouldOverWriteValues_WhenSetingValueBytesMultipleTimes() {
		String hexValue = "0x01" +
				"00" +
				"0003e800" +
				"0003e800" +
					"7c" +
					"09" +
					"0000007d00" +
					"0000007d00" +
					"0000000000" +
					"0000000000" +
					"00" +
				"0004" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"04294e1261" +
				"04294e1271";
		byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);
		gtpTunnelData.setValueBytes(valueBytes);
		
		hexValue = "0x02" +
				"00" +
				"0003e800" +
				"0003e800" +
					"7c" +
					"09" +
					"0000007d00" +
					"0000007d00" +
					"0000000000" +
					"0000000000" +
					"00" +
				"0004" +
				"16" +
				"08696e7465726e657406756e6974656c02636f02616f" +
				"04294e1261" +
				"04294e1271";
		valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);
		gtpTunnelData.setValueBytes(valueBytes);
		
		String receivedString = gtpTunnelData.getStringValue();
		String expectedtring = "SPARE1=0;PDNTYPE=2;APNRES=0;AMBR-U=256000;AMBR-D=256000;SPARE2=0;QOSPCI=1;QOSPL=15;SPARE3=0;QOSPVI=0;QOSQCI=9;QOSMBR-U=32000;QOSMBR-D=32000;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=4;APNNAME=internet.unitel.co.ao;P-PDN=41.78.18.97;S-PDN=41.78.18.113";
		assertEquals("setValueBytes and getStringValue are not sync properly", expectedtring, receivedString);
	}

	@Test
	public void testSetStringValue_ShouldSetPropertiesProperly_WhenProperCharSetIsPassed() {
		String valueConfigured = "SPARE1=0;PDNTYPE=1;APNRES=0;AMBR-U=256000;AMBR-D=256000;SPARE2=0;QOSPCI=1;QOSPL=15;SPARE3=0;QOSPVI=0;QOSQCI=9;QOSMBR-U=32000;QOSMBR-D=32000;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=8;APNNAME=internet.unitel.co.ao;P-PDN=127.0.0.1;S-PDN=127.0.0.1";
		try {
			gtpTunnelData.setStringValue(valueConfigured, "UTF-8");
			assertEquals("value configured for GTPTunnelData is not proper", valueConfigured, gtpTunnelData.getStringValue("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			fail("testSetStringValueWithCharSet failed, Reason: " + e.getMessage());
		}
	}
	
	@Test(expected = UnsupportedEncodingException.class)
	public void testSetStringValue_ShouldThrowUnsupportedEncodingException_WhenInvalidCharSetIsPassed() throws UnsupportedEncodingException {
		String valueConfigured = "SPARE1=0;PDNTYPE=1;APNRES=0;AMBR-U=256000;AMBR-D=256000;SPARE2=0;QOSPCI=1;QOSPL=15;SPARE3=0;QOSPVI=0;QOSQCI=9;QOSMBR-U=32000;QOSMBR-D=32000;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=8;APNNAME=internet.unitel.co.ao;P-PDN=10.87.157.65;S-PDN=10.87.157.65";
		gtpTunnelData.setStringValue(valueConfigured, "ABC");
	}
	
	@Test
	public void testSetStringValue_ShouldReturnDefaultValues_WhenValuesAreNull() {
		String defaultValues = "SPARE1=0;PDNTYPE=1;APNRES=0;AMBR-U=0;AMBR-D=0;SPARE2=0;QOSPCI=0;QOSPL=1;SPARE3=0;QOSPVI=0;QOSQCI=0;QOSMBR-U=0;QOSMBR-D=0;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=0;APNNAME=;P-PDN=0.0.0.0;S-PDN=0.0.0.0";
		try {
			gtpTunnelData.setStringValue(null, "UTF-8");
			assertEquals("value configured for GTPTunnelData is not proper", defaultValues, gtpTunnelData.getStringValue("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			fail("testSetStringValueWithCharSet failed, Reason: " + e.getMessage());
		}
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_testSetAndGetValueBytesForSuccessfulCases")
	public void testSetAndGetValueBytes_ShouldGetSuccess_ForSuccessfulCases(String hexValue){
		try{
			byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);
			gtpTunnelData.setValueBytes(valueBytes);
			byte[] returnValueBytes = gtpTunnelData.getValueBytes();
			assertEquals("Bytes for GTPTunnelData are not proper", RadiusUtility.bytesToHex(valueBytes), RadiusUtility.bytesToHex(returnValueBytes));
		} catch(Throwable t){
			fail("Unexpected exception received: " + t);
		}
	}
	
	public static Object[][] dataFor_testSetAndGetValueBytesForSuccessfulCases(){
		return new Object[][]{
				/*
				 * Proper Bytes (successful case)
				 */
				{"0x02" +
						"00" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * passing spare1 = 1 and PDNTYPE=1 and successfully passed
				 */
				{"0x09" +
						"00" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * passing spare1 = 31 and PDNTYPE=1 and successfully passed
				 */
				{"0xF9" +
						"00" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * PDN Type value as 3 and spare1=0 and successfully passed
				 */
				{"0x03" +
						"00" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * APNRes value as 0 and successfully passed
				 */
				{"0x03" +
						"00" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * APNRes value as 4 and successfully passed 
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"1608696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * AMBR-U=0 and successfully passed
				 */
				{"0x03" +
						"04" +
						"00000000" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * AMBR-U=4294967295 and successfully passed
				 */
				{"0x03" +
						"04" +
						"ffffffff" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * AMBR-D=0 and successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"00000000" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/* 
				 * AMBR-D=4294967295 and successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"ffffffff" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"1608696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * SPARE2=1 , QOSPCI = 0 , PL=15  and PVI = 1 successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"BD" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * SPARE2=0 , QOSPCI = 1 , PL=7 and successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"5D" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * SPARE2=1 , QOSPCI = 1 , PL=11 , SPARE3=1 , PVI = 0 and successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"EE" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * SPARE2=1 , QOSPCI = 1 , PL=11 , SPARE3=0 , PVI = 1 and successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"ED" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * QOSQCI=0 and QOSMBR-U=4294967296 successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"ED" +
							"00" +
							"0100000000" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * QOSQCI=255 and QOSMBR-U=4294967296 successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"ED" +
							"FF" +
							"0100000000" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},

				/*
				 * QOSQCI=255, QOSMBR-U = 0, QOSMBR-D=4294967296 and QOSGBR = 10000000000 successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"ED" +
							"FF" +
							"0000000000" +
							"0100000000" +
							"02540be400" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * QOSQCI=255, QOSMBR-U=10000000000 , QOSMBR-D=4294967296 and successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"ED" +
							"FF" +
							"02540be400" +
							"0100000000" +
							"02540be400" +
							"0000000000" +
							"00" +
						"0004" +
						"1608696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * QOSMBR-U = 1 , QOSMBR-D=0 , QOSGBR = 10000000000 and successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"ED" +
							"FF" +
							"0000000001" +
							"0000000000" +
							"02540be400" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * QOSMBR-U=0 , QOSMBR-D=10000000000 , QOSGBR = 10000000000 and successfully passed
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"ED" +
							"FF" +
							"0000000000" +
							"02540be400" +
							"02540be400" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"},
				
				/*
				 * if more bytes are provided then extra bytes are discarded and passed successfully
				 */
				{"0x03" +
						"04" +
						"0003e800" +
						"0003e800" +
							"ED" +
							"FF" +
							"0000000000" +
							"02540be400" +
							"02540be400" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41" +
						"12345678912345678912"},	//extra bytes
				
				/*
				 * PDNType value is 1 in successful case
				 * Now trying to provide its value as 2.
				 */
				{"0x02" +
						"04" +
						"0003e800" +
						"0003e800" +
							"ED" +
							"FF" +
							"0000000000" +
							"02540be400" +
							"02540be400" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41"}
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_testSetAndGetValueBytesForFailureCases")
	public void testSetAndGetValueBytes_ShouldThrowSomeException_ForFailureCases(String hexValue, Class<? extends Exception> expectedException){
		try{
			byte[] valueBytes = RadiusUtility.getBytesFromHexValue(hexValue);
			gtpTunnelData.setValueBytes(valueBytes);
			byte[] returnValueBytes = gtpTunnelData.getValueBytes();
			assertEquals("Bytes for GTPTunnelData are not proper", RadiusUtility.bytesToHex(valueBytes), RadiusUtility.bytesToHex(returnValueBytes));
			fail("Expected exception: " + expectedException + " is not thrown");
		} catch(Throwable t){
			assertEquals(expectedException, t.getClass());
		}
	}
	
	public static Object[][] dataFor_testSetAndGetValueBytesForFailureCases(){
		return new Object[][]{
				/*
				 * passing PDNTYPE=0 and SPARE1=0, it should throw exception as 0 is invalid value for PDNTYPE
				 */
				 {"0x00" +
				 		"00" +
				 		"0003e800" +
				 		"0003e800" +
					 		"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
				 		"0004" +
				 		"16" +
				 		"08696e7465726e657406756e6974656c02636f02616f" +
				 		"040a579d41" +
				 		"040a579d41", IllegalArgumentException.class},
				
				 /*
				  * passing PDNTYPE=0 and SPARE1=31, it should throw exception as 0 is invalid value for PDNTYPE
				  */
				 {"0xF8" +
				 		"00" +
				 		"0003e800" +
				 		"0003e800" +
					 		"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
				 		"0004" +
				 		"16" +
				 		"08696e7465726e657406756e6974656c02636f02616f" +
				 		"040a579d41" +
				 		"040a579d41", IllegalArgumentException.class},

				 /*
				 * APNRes value as 5 and not valid value so IllegalArgumentException is thrown 
				 */
				{"0x03" +
						"05" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41", IllegalArgumentException.class},
				
				/*
				 * SPARE2=1 , QOSPCI = 1 , PL=0 , should throw exception as 0 is invalid value for QOSPL 
				 */
				{"0x02" +
						"00" +
						"0003e800" +
						"0003e800" +
							"C3" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41",IllegalArgumentException.class},
				
				/*
				 * QOSMBR-U=10000000001 , QOSMBR-D=0 , invalid value for QOSMBR-U so IllegalArgumentException is thrown
				 */
				{"0x01" +
						"00" +
						"00000000" +
						"00000000" +
							"45" +
							"00" +
							"02540be401" +
							"0000000000" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41",IllegalArgumentException.class},
				
				/*
				 * QOSMBR-U=0 , QOSMBR-D=10000000001 , QOSGBR = 0 , invalid value for QOSMBR-D so IllegalArgumentException is thrown
				 */
				{"0x01" +
						"00" +
						"00000000" +
						"00000000" +
							"45" +
							"00" +
							"0000000000" +
							"02540be401" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41",IllegalArgumentException.class},

				/*
				 * QOSMBR-U=0 , QOSMBR-D=10000000000 , QOSGBR-U = 10000000001 , invalid value for QOSGBR-U so IllegalArgumentException is thrown
				 */
				{"0x01" +
						"00" +
						"00000000" +
						"00000000" +
							"45" +
							"00" +
							"0000000000" +
							"02540be400" +
							"02540be401" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"040a579d41" +
						"040a579d41",IllegalArgumentException.class},
		};
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetStringValue_ShouldReturnIllegalArgumentException_WhenValuePassedAsNull() {
		gtpTunnelData.setStringValue(null);
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetStringValue_ShouldReturnIllegalArgumentException_WhenEmptyValueIsPassed() {
		gtpTunnelData.setStringValue("");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetStringValue_ShouldReturnIllegalArgumentException_WhenNoOfKeyValuePairIsOutOfRange() {
		gtpTunnelData.setStringValue("SPARE0=1;SPARE1=0;PDNTYPE=1;APNRES=0;AMBR-U=256000;AMBR-D=256000;SPARE2=0;QOSPCI=1;QOSPL=15;SPARE3=0;QOSPVI=0;QOSQCI=9;QOSMBR-U=32000;QOSMBR-D=32000;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=8;APNNAME=internet.unitel.co.ao;P-PDN=10.87.157.65;S-PDN=10.87.157.65");
	}
	
	@Test(expected = IllegalArgumentException.class)
	public void testSetStringValue_ShouldThrowIllegalArgumentException_WhenImproperHexValueIsPassed() {
		String value = "01000003e8000003e8007c090000007d000000007d00000000000000000000000000041608696e7465726e657406756e6974656c02636f02616f04294e126104294e1271"; 
		gtpTunnelData.setStringValue(value);
	}
	
	@Test
	public void testSetStringValue_ShouldThrowIllegalArgumentExceptionWithNumberFormatExceptionAsCause_WhenUnparsableValueIsPassed() {
		try {
			gtpTunnelData.setStringValue("PDNTYPE=a");
			fail("Expected exception: IllegalArgumentException with NumberFormatException as cause is not thrown");
		} catch (Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(NumberFormatException.class, t.getCause().getClass());
		}
	}
	
	@Test
	public void testSetAndGetStringValue_ShouldMatchAllValues_WhenGetStringFromOneAttributeAndSetStringInAnotherAttribute() {
		String configuredStringValue = "SPARE1=0;PDNTYPE=1;APNRES=0;AMBR-U=256000;AMBR-D=256000;SPARE2=0;QOSPCI=1;QOSPL=15;SPARE3=0;QOSPVI=0;QOSQCI=9;QOSMBR-U=32000;QOSMBR-D=32000;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=8;APNNAME=internet.unitel.co.ao;P-PDN=127.0.0.1;S-PDN=127.0.0.1";
		gtpTunnelData.setStringValue(configuredStringValue);

		GTPTunnelDataAttribute gtpTunnelDataSecond = new GTPTunnelDataAttribute(mockAddressResolver);
		gtpTunnelDataSecond.setStringValue(gtpTunnelData.getStringValue());
		
		assertEquals(configuredStringValue, gtpTunnelDataSecond.getStringValue());
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_testSetAndGetStringValueForSuccessfulCases")
	public void testSetAndGetStringValue_ShouldGetSuccess_ForSuccessfulCases(String configuredStringValue) {
		try {
			gtpTunnelData.setStringValue(configuredStringValue);
			assertEquals("value configured for GTPTunnelData is not proper", generateExpectedString(), gtpTunnelData.getStringValue());
		} catch(Throwable t) {
			fail("Unexpected exception received: " + t.getClass());
		}
	}
	
	public static Object[][] dataFor_testSetAndGetStringValueForSuccessfulCases() {
		return new Object[][] {
				/*
				 * Successful scenario
				 */
				{"SPARE1=0;PDNTYPE=1;APNRES=0;AMBR-U=256000;AMBR-D=256000;SPARE2=0;QOSPCI=1;QOSPL=15;SPARE3=0;QOSPVI=0;QOSQCI=9;QOSMBR-U=32000;QOSMBR-D=32000;QOSGBR-U=0;QOSGBR-D=0;SPARE4=0;CC=8;APNNAME=internet.unitel.co.ao;P-PDN=127.0.0.1;S-PDN=127.0.0.1"},
				
				/*
				 * passing hexvalue to setStringValue
				 */
				{"0x01" +
						"00" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"04294e1261" +
						"04294e1271"},
				
				{"0X01" +
						"00" +
						"0003e800" +
						"0003e800" +
							"7c" +
							"09" +
							"0000007d00" +
							"0000007d00" +
							"0000000000" +
							"0000000000" +
							"00" +
						"0004" +
						"16" +
						"08696e7465726e657406756e6974656c02636f02616f" +
						"04294e1261" +
						"04294e1271"},
				
				
				{"SPARE1=0"},
				
				{"SPARE1=5"},
				
				{"SPARE1=31"},
				

				{"PDNTYPE=1"},
				
				{"PDNTYPE=2"},

				{"PDNTYPE=3"},


				{"APNRES=0"},
				
				{"APNRES=2"},
				
				{"APNRES=4"},
				

				{"AMBR-U=0"},

				{"AMBR-U=25600"},
				
				{"AMBR-U=4294967295"},

				
				{"AMBR-D=0"},
				
				{"AMBR-D=25600"},

				{"AMBR-D=4294967295"},
				

				{"QOSPCI=0"},
				
				{"QOSPCI=1"},
				

				{"QOSPL=1"},
				
				{"QOSPL=10"},
				
				{"QOSPL=15"},
				
	
				{"QOSPVI=0"},
				
				{"QOSPVI=1"},
				
				
				{"QOSQCI=0"},
				
				{"QOSQCI=255"},
				
				
				{"QOSMBR-U=0"},
				
				{"QOSMBR-U=32000"},
				
				{"QOSMBR-U=10000000000"},
				

				{"QOSMBR-D=0"},
				
				{"QOSMBR-D=32000"},
				
				{"QOSMBR-D=10000000000"},
				

				{"QOSGBR-U=0"},
				
				{"QOSGBR-U=32000"},
				
				{"QOSGBR-U=10000000000"},
				

				{"QOSGBR-D=0"},
				
				{"QOSGBR-D=32000"},

				{"QOSGBR-D=10000000000"},
				
				
				{"CC=0"},
				
				{"CC=1"},
				
				{"CC=8"},
				
				{"CC=65535"},
				
				{"CC=0x0008"},
				
				{"CC=0X0008"},
				
				
				{"APNNAME=ABC.xyz@pqr123"},


				{"P-PDN=127.0.0.1"},
				
				{"S-PDN=127.0.0.1"},
				
				{"SPARE2=0"},
				
				{"SPARE2=1"},
				
				
				{"SPARE3=0"},
				
				{"SPARE3=1"},
				
				
				{"SPARE4=0"},
				
				{"SPARE4=129"},
				
				{"SPARE4=255"},
				
				
				{"BQOS=0x7c090000007d000000007d000000000000000000000000"},
				
				{"BQOS=0X7c090000007d000000007d000000000000000000000000"},
				
				{";PDNTYPE=2;"},
		};
	}
	
	@Test
	@junitparams.Parameters(method = "dataFor_testSetAndGetStringValueForFailureCases")
	public void testSetAndGetStringValue_ShouldThrowSomeException_ForFailureCases(String configuredStringValue, Class<? extends Exception> expectedException) {
		try {
			gtpTunnelData.setStringValue(configuredStringValue);
			fail("Expected exception: " + expectedException + " is not thrown");
		} catch(Throwable t) {
			assertEquals(expectedException, t.getClass());
		}
	}
	
	public static Object[][] dataFor_testSetAndGetStringValueForFailureCases() {
		return new Object[][] {
				/*
				 * Passing all possible values for all keys including with out of range values also
				 */
				{"SPARE1=-1" , IllegalArgumentException.class},
				
				{"SPARE1=32" , IllegalArgumentException.class},
				
				
				{"PDNTYPE=0" , IllegalArgumentException.class},

				{"PDNTYPE=4" , IllegalArgumentException.class},
				

				{"APNRES=-1" , IllegalArgumentException.class},
				
				{"APNRES=5" , IllegalArgumentException.class},


				{"AMBR-U=-1" , IllegalArgumentException.class},

				{"AMBR-U=4294967296" , IllegalArgumentException.class},

				
				{"AMBR-D=-1" , IllegalArgumentException.class},
				
				{"AMBR-D=4294967296" , IllegalArgumentException.class},
				

				{"QOSPCI=-1" , IllegalArgumentException.class},
				
				{"QOSPCI=2" , IllegalArgumentException.class},
				

				{"QOSPL=0" , IllegalArgumentException.class},
				
				{"QOSPL=16" , IllegalArgumentException.class},

	
				{"QOSPVI=-1" , IllegalArgumentException.class},
				
				{"QOSPVI=2" , IllegalArgumentException.class},
				
				
				{"QOSQCI=-1" , IllegalArgumentException.class},
				
				{"QOSQCI=256" , IllegalArgumentException.class},
				
				
				{"QOSMBR-U=-1" , IllegalArgumentException.class},
				
				{"QOSMBR-U=10000000001" , IllegalArgumentException.class},
				

				{"QOSMBR-D=-1" , IllegalArgumentException.class},
				
				{"QOSMBR-D=10000000001" , IllegalArgumentException.class},
				

				{"QOSGBR-U=-1" , IllegalArgumentException.class},
				
				{"QOSGBR-U=10000000001" , IllegalArgumentException.class},
				

				{"QOSGBR-D=-1" , IllegalArgumentException.class},
				
				{"QOSGBR-D=10000000001" , IllegalArgumentException.class},
				
				
				{"CC=-1" , IllegalArgumentException.class},
				
				{"CC=65536" , IllegalArgumentException.class},
				
				
				{"APNNAME=" , IllegalArgumentException.class},

				{"APNNAME=qwertyuiopasdfghjklzqwertyuiopasdfghjklzqwertyuiopasdfghjklzqwertyuiopasdfghjklzqwertyuiopasdfghjklzabc",IllegalArgumentException.class},

				
				{"SPARE2=-1", IllegalArgumentException.class},
				
				{"SPARE2=2", IllegalArgumentException.class},

				
				{"SPARE3=-1", IllegalArgumentException.class},

				{"SPARE3=2", IllegalArgumentException.class},
				
				
				{"SPARE4=-1",IllegalArgumentException.class},
				
				{"SPARE4=256", IllegalArgumentException.class},
				
				
				{"BQOS=7c090000007d000000007d000000000000000000000000",IllegalArgumentException.class},
				
				/*
				 * Passing not enough data to BQOS so IllegalArgumentException will be thrown 
				 */
				{"BQOS=0x090000007d00000000",IllegalArgumentException.class},
				
				
				
				/*
				 * Testing other possible cases 
				 */
				{";;" , IllegalArgumentException.class},
				
				{"PDNTYPE=",IllegalArgumentException.class},
				
				{"=1",IllegalArgumentException.class},

				{"QOSQCI=a",IllegalArgumentException.class},
		};
	}
	
	private String generateExpectedString() {
		StringBuilder builder = new StringBuilder();
		String AVPAIR_SEPERATOR = Dictionary.getInstance().getVendorAVPairSeparator(RadiusConstants.ERICSSON_VENDOR_ID);
		builder.append(GTPTunnelDataAttribute.SPARE1 + "=" + gtpTunnelData.getValueFromKey(GTPTunnelDataAttribute.SPARE1))
		.append(AVPAIR_SEPERATOR + GTPTunnelDataAttribute.PDN_TYPE + "=" + gtpTunnelData.getValueFromKey(GTPTunnelDataAttribute.PDN_TYPE))
		.append(AVPAIR_SEPERATOR + GTPTunnelDataAttribute.APN_RESTRICTION + "=" + gtpTunnelData.getValueFromKey(GTPTunnelDataAttribute.APN_RESTRICTION))
		.append(AVPAIR_SEPERATOR + GTPTunnelDataAttribute.AMBR_UPLINK + "=" + gtpTunnelData.getValueFromKey(GTPTunnelDataAttribute.AMBR_UPLINK))
		.append(AVPAIR_SEPERATOR + GTPTunnelDataAttribute.AMBR_DOWNLINK + "=" + gtpTunnelData.getValueFromKey(GTPTunnelDataAttribute.AMBR_DOWNLINK))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.SPARE2 + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.SPARE2))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.PCI + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.PCI))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.PL + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.PL))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.SPARE3 + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.SPARE3))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.PVI + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.PVI))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.QCI + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.QCI))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.MBR_UPLINK + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.MBR_UPLINK))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.MBR_DOWNLINK + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.MBR_DOWNLINK))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.GBR_UPLINK + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.GBR_UPLINK))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.GBR_DOWNLINK + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.GBR_DOWNLINK))
		.append(AVPAIR_SEPERATOR + BearerQualityOfService.SPARE4 + "=" + gtpTunnelData.getValueFromKey(BearerQualityOfService.SPARE4))
		.append(AVPAIR_SEPERATOR + GTPTunnelDataAttribute.CHARGING_CHARACTERISTICS + "=" + gtpTunnelData.getValueFromKey(GTPTunnelDataAttribute.CHARGING_CHARACTERISTICS))
		.append(AVPAIR_SEPERATOR + GTPTunnelDataAttribute.APN_NAME + "=" + gtpTunnelData.getValueFromKey(GTPTunnelDataAttribute.APN_NAME))
		.append(AVPAIR_SEPERATOR + GTPTunnelDataAttribute.PRIMARY_PDN + "=" + gtpTunnelData.getValueFromKey(GTPTunnelDataAttribute.PRIMARY_PDN))
		.append(AVPAIR_SEPERATOR + GTPTunnelDataAttribute.SECONDARY_PDN + "=" + gtpTunnelData.getValueFromKey(GTPTunnelDataAttribute.SECONDARY_PDN));
		return builder.toString();
	}
	
	@Test
	public void testSetStringValue_ForPrimaryPDN_ShouldThrowIllegalArgumentExceptionWithUnknownHostExceptionAsCause_WhenInvalidIPIsPassed() {
		try {
			doThrow(UnknownHostException.class).when(mockAddressResolver).byName("invalid");

			gtpTunnelData.setStringValue("P-PDN=invalid");
			
			fail("Expected exception: IllegalArgumentException with UnknownHostException as cause is not thrown");
		} catch(Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(UnknownHostException.class, t.getCause().getClass());
		}
	}
	
	@Test
	public void testSetStringValue_ForSecondaryPDN_ShouldThrowIllegalArgumentExceptionWithUnknownHostExceptionAsCause_WhenInvalidIPIsPassed() {
		try {
			doThrow(UnknownHostException.class).when(mockAddressResolver).byName("invalid");

			gtpTunnelData.setStringValue("S-PDN=invalid");
			
			fail("Expected exception: IllegalArgumentException with UnknownHostException as cause is not thrown");
		} catch(Throwable t) {
			assertEquals(IllegalArgumentException.class, t.getClass());
			assertEquals(UnknownHostException.class, t.getCause().getClass());
		}
	}
}
