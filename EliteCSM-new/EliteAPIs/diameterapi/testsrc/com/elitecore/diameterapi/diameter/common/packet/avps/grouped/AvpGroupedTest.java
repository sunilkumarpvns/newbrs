package com.elitecore.diameterapi.diameter.common.packet.avps.grouped;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class AvpGroupedTest {
	
	private AvpGrouped avpGrouped = (AvpGrouped)DummyDiameterDictionary.getInstance().getAttribute("10415:1001");
	
	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	public class AddSubAVP_UnknownAVPId{
		
		private String unknownAvpCode="365265261:365265261";
		
		@Test
		public void addsSubAvpWithStringValue() {
			String expectedValue = "xyz";
			avpGrouped.addSubAvp(unknownAvpCode, expectedValue);
			IDiameterAVP subAttribute = avpGrouped.getSubAttribute(unknownAvpCode);
			byte[] actualValue = subAttribute.getValueBytes();

			assertNotNull(avpGrouped.getSubAttribute(unknownAvpCode));
			assertEquals(expectedValue, new String(actualValue));
		}

		@Test
		public void addsSubAvpLongValue() {
			long expectedValue = 11;
			avpGrouped.addSubAvp(unknownAvpCode, expectedValue);
			IDiameterAVP subAttribute = avpGrouped.getSubAttribute(unknownAvpCode);
			byte[] actualValue = subAttribute.getValueBytes();
			
			assertNotNull(avpGrouped.getSubAttribute(unknownAvpCode));
			assertEquals(String.valueOf(expectedValue), new String(actualValue));
		}
		
	}
	
	public class AddSubAVP_KnownAVPId {
		
		private String knownAvpId="0:1";
		
		@Test
		public void addsSubAvpStringValue() {
			String expectedValue = "xyz";
			avpGrouped.addSubAvp(knownAvpId, expectedValue);
			IDiameterAVP subAttribute = avpGrouped.getSubAttribute(knownAvpId);
			String actualValue = subAttribute.getStringValue();

			assertNotNull(subAttribute);
			assertEquals(expectedValue, actualValue);
		}
		
		@Test
		public void addsSubAvpLongValue() {
			long expectedValue = 11;
			avpGrouped.addSubAvp(knownAvpId, 11);
			IDiameterAVP subAttribute = avpGrouped.getSubAttribute(knownAvpId);
			String actualValue = subAttribute.getStringValue();

			assertNotNull(subAttribute);
			assertEquals(expectedValue+"", actualValue);
		}
	}
}
