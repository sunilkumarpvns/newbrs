package com.elitecore.coreradius.commons.attributes;

import junit.framework.TestSuite;

import com.elitecore.coreradius.BaseRadiusTestCase;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

public class EgressVlanIdTest extends BaseRadiusTestCase {

	public EgressVlanIdTest(String name) {
		super(name);
	}

	public static TestSuite suite() {
		TestSuite suite = new TestSuite();
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue1"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue2"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue3"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue4"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue5"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue6"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue7"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue8"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue9"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue10"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue11"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue12"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue13"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue14"));
		suite.addTest(new EgressVlanIdTest("testGetSetStringValue15"));
		suite.addTest(new EgressVlanIdTest("testToString"));
		suite.addTest(new EgressVlanIdTest("testWithRadiusPacket"));
	   	return suite;
	}
	
	// Test get/setStringValue method of EgressVLANID
	//Test Case: 49:1234
	public void testGetSetStringValue1(){
		try {
			EgressVLANID egressVlanId;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			egressVlanId.setStringValue("49:1234");
			assertEquals("49:1234",egressVlanId.getStringValue());
			assertEquals(6,egressVlanId.getLength());
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}
	
	// Test get/setStringValue method of EgressVLANID
	//Test Case: 50:123
	public void testGetSetStringValue2(){
		try {
			EgressVLANID egressVlanId;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			egressVlanId.setStringValue("50:123");
			assertEquals("50:123",egressVlanId.getStringValue());
			assertEquals(6,egressVlanId.getLength());
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: 49:65535
	public void testGetSetStringValue3(){
		try {
			EgressVLANID egressVlanId;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			egressVlanId.setStringValue("49:65535");
			assertEquals("49:4095",egressVlanId.getStringValue());
			assertEquals(6,egressVlanId.getLength());
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: 48:123
	public void testGetSetStringValue4(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue("48:123");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: 51:123
	public void testGetSetStringValue5(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue("51:123");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: "" value
	public void testGetSetStringValue6(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue("");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}
	
	// Test get/setStringValue method of EgressVLANID
	//Test Case: null value
	public void testGetSetStringValue7(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue(null);	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: :
	public void testGetSetStringValue8(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue(":");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: 49:
	public void testGetSetStringValue9(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue("49:");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: :1234
	public void testGetSetStringValue10(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue(":1234");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: 1234
	public void testGetSetStringValue11(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue("1234");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}
	
	// Test get/setStringValue method of EgressVLANID
	//Test Case: abcd
	public void testGetSetStringValue12(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;
 
			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue("abcd");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: abcd:xyz
	public void testGetSetStringValue13(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue("abcd:xyz");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: 50:abc
	public void testGetSetStringValue14(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue("50:abc");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	// Test get/setStringValue method of EgressVLANID
	//Test Case: abc:50
	public void testGetSetStringValue15(){
		try {
			EgressVLANID egressVlanId;
			boolean thrown;

			egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			thrown = false;
			try {
				egressVlanId.setStringValue("abc:50");	
			} catch(IllegalArgumentException e) {
				thrown = true;
			}
			assertTrue(thrown);
			
		} catch(Exception e) {
			e.printStackTrace();
			fail("testGetSetStringValue failed, reason: "+e.getMessage());
		}
	}

	
	
	public void testToString() {
		try {
			EgressVLANID egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			egressVlanId.setStringValue("49:123");
		} catch(Exception e) {
			e.printStackTrace();
			fail("testToString failed, reason: "+e.getMessage());
		}
	}
	
	public void testWithRadiusPacket() {
		try {
			EgressVLANID egressVlanId = new EgressVLANID();
			egressVlanId.setHasTag(true);
			egressVlanId.setType(RadiusAttributeConstants.EGRESS_VLANID);
			egressVlanId.setStringValue("50:123");
			
			RadiusPacket radiusPacket = new RadiusPacket();
			radiusPacket.addAttribute(egressVlanId);
			
			RadiusPacket newRadiusPacket = new RadiusPacket();
			newRadiusPacket.addAttributes(radiusPacket.getRadiusAttributes());
			assertEquals(radiusPacket.getRadiusAttribute(RadiusAttributeConstants.EGRESS_VLANID).getStringValue(), newRadiusPacket.getRadiusAttribute(RadiusAttributeConstants.EGRESS_VLANID).getStringValue());
		} catch(Exception e) {
			e.printStackTrace();
			fail("testWithRadiusPacket failed, reason: "+e.getMessage());
		}
	}
}
