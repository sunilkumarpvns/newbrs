package com.elitecore.netvertex.pm.data;

import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import org.junit.Ignore;

/**
 * The class <code>PCCRuleTest</code> contains tests for the class <code>{@link PCCRule}</code>.
 *
 * @generatedBy CodePro at 5/16/13 7:31 PM
 * @author harsh
 * @version $Revision: 1.0 $
 */
@Ignore
public class PCCRuleTest {/*



	*//**
	 * Perform pre-test initialization.
	 *
	 * @throws Exception
	 *         if the initialization fails for some reason
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Before
	public void setUp() throws Exception{
		MockitoAnnotations.initMocks(this);
	}
	*//**
	 * @throws Exception
	 *//*
	@Test
	public void testPCCRule_1()
		throws Exception {
		int pccRuleId = 1;
		String pccRuleName = "";
		int precedence = 1;
		RatingGroup ratingGroup = new RatingGroup(1L, "", "", 1L);
		String sponsorIdEntity = "";
		String appServiceProviderId = "";
		GateStatus gateStatus = GateStatus.CLOSE;
		String serviceTypeId = "1";
		UsageMetering usageMetering = UsageMetering.DISABLE_QUOTA;
		int qci = 1;
		long gbrdl = 1L;
		long gbrul = 1L;
		long mbrdl = 1L;
		long mbrul = 1L;
		String bearerIdentifier = "";
		int arp = 1;
		String peCapability = "";
		String peVulnerability = "";
		boolean dynamic = true;
		List<ServiceDataFlow> serviceDataFlows = new ArrayList<ServiceDataFlow>();
		ChargingModes chargingMode = ChargingModes.BOTH;
		FlowStatus flowStatus = FlowStatus.DISABLED;
		String flowNumber = "";
		ServiceType serviceType = new ServiceType("1", "", 1L,new ArrayList<ServiceDataFlow>(), new ArrayList<RatingGroup>());


		PCCRule result = new PCCRule(pccRuleId, pccRuleName, precedence, ratingGroup, appServiceProviderId, serviceTypeId, usageMetering, qci, gbrdl, gbrul, mbrdl, mbrul, dynamic, serviceDataFlows, chargingMode, flowStatus, flowNumber, serviceType, pccRuleName, 26214400L);

		// add additional test code here
		assertNotNull(result);
		assertEquals("", result.getServiceName());
		assertEquals(1, result.getPrecedence());
		assertEquals(new Long(1L), result.getChargingKey());
		assertEquals("", result.getAppServiceProviderId());
		assertEquals(1, result.getServiceTypeId());
		assertEquals(1L, result.getServiceIdentifier());
		assertEquals(UsageMetering.DISABLE_QUOTA, result.getUsageMetering());
		assertEquals(1, result.getQci());
		assertEquals(1, result.getArp());
		assertEquals("", result.getPeCapability());
		assertEquals("", result.getPeVulnerability());
		assertEquals(1L, result.getGBRDL());
		assertEquals(1L, result.getGBRUL());
		assertEquals(1L, result.getMBRDL());
		assertEquals(1L, result.getMBRUL());
		assertEquals(true, result.isDynamic());
		assertEquals(" (1) ", result.toString());
		assertEquals("", result.getName());
		assertEquals(1, result.getId());
	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareWithGreateQCI()
		throws Exception {

		PCCRule lowerPCCRule = new PCCRuleBuilder("1", "PCC1").withQci(1).build();
		PCCRule higherPCCRule = new PCCRuleBuilder("1", "PCC2").withQci(2).build();

		assertEquals(1, lowerPCCRule.compareTo(higherPCCRule));
		assertEquals(-1, higherPCCRule.compareTo(lowerPCCRule));
	}

	@Test
	public void testCompareOnLowerQCI()
		throws Exception {

		new PCCRuleBuilder("1", "PCC1").withQci(1).build();
		new PCCRuleBuilder("1", "PCC2").withQci(2).build();


	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareGreateGBRDL()
		throws Exception {

		PCCRule lowerPCCRule = new PCCRuleBuilder("1", "PCC1").withQci(1).withGbrdl(499).build();
		PCCRule higherPCCRule = new PCCRuleBuilder("1", "PCC2").withQci(1).withGbrdl(500).build();

		assertEquals(-1, lowerPCCRule.compareTo(higherPCCRule));
		assertEquals(1, higherPCCRule.compareTo(lowerPCCRule));


	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareLowerGBRDL()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 500, 1L, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC1",26214400L, PolicyStatus.SUCCESS);
		PCCRule arg0 = new PCCRule("1", "PCC2", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 501, 1L, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC2",26214400L, PolicyStatus.SUCCESS);

		int result = fixture.compareTo(arg0);

		// add additional test code here
		assertEquals(-1, result);
	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareGreaterGBRUL()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 500, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC1",26214400L, PolicyStatus.SUCCESS);
		PCCRule arg0 = new PCCRule("1", "PCC2", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 499, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC2",26214400L, PolicyStatus.SUCCESS);

		int result = fixture.compareTo(arg0);

		// add additional test code here
		assertEquals(1, result);
	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareLowerGBRUL()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 500, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC1",26214400L, PolicyStatus.SUCCESS);
		PCCRule arg0 = new PCCRule("1", "PCC2", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 501, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC2",26214400L, PolicyStatus.SUCCESS);

		int result = fixture.compareTo(arg0);

		// add additional test code here
		assertEquals(-1, result);
	}

	@Test
	public void testCompareGreaterMBRDL()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 500,  1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC1",26214400L, PolicyStatus.SUCCESS);
		PCCRule arg0 = new PCCRule("1", "PCC2", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 499,  1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC2",26214400L, PolicyStatus.SUCCESS);

		int result = fixture.compareTo(arg0);

		// add additional test code here
		assertEquals(1, result);
	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareLowerMBRDL()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 500, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC1",26214400L, PolicyStatus.SUCCESS);
		PCCRule arg0 = new PCCRule("1", "PCC2", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 501, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC2",26214400L, PolicyStatus.SUCCESS);

		int result = fixture.compareTo(arg0);

		// add additional test code here
		assertEquals(-1, result);
	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareGreaterMBRUL()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 500,  1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS),"PCC1",26214400L, PolicyStatus.SUCCESS);
		PCCRule arg0 = new PCCRule("1", "PCC2", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 499,  1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS),"PCC2",26214400L, PolicyStatus.SUCCESS);

		int result = fixture.compareTo(arg0);

		// add additional test code here
		assertEquals(1, result);
	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareLowerMBRUL()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 1L, 500, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC1",26214400L, PolicyStatus.SUCCESS);

		PCCRule arg0 = new PCCRule("1", "PCC2", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L,1L, 501, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC2",26214400L, PolicyStatus.SUCCESS);

		int result = fixture.compareTo(arg0);

		// add additional test code here
		assertEquals(-1, result);
	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareNullObject()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC1",26214400L, PolicyStatus.SUCCESS);
		PCCRule arg0 = null;

		int result = fixture.compareTo(arg0);

		// add additional test code here
		assertEquals(1, result);
	}

	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareWithALLComparableFieldValueSame()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector()), "PCC1",26214400L, PolicyStatus.SUCCESS);
		PCCRule arg0 = new PCCRule("1", "PCC2", 1, new RatingGroup(1L, "", "", 1L), "", "", GateStatus.CLOSE, "1", UsageMetering.DISABLE_QUOTA,
				1, 1L, 1L, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector()), "PCC2",26214400L, PolicyStatus.SUCCESS);

		int result = fixture.compareTo(arg0);

		// add additional test code here
		assertEquals(0, result);
	}


	*//**
	 * Run the int compareTo(PCCRule) method test.
	 *
	 * @throws Exception
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@Test
	public void testCompareSameObject()
		throws Exception {
		PCCRule fixture = new PCCRule("1", "PCC1", 1, new RatingGroup(1L, "", "", 1L), "", "", "1", UsageMetering.DISABLE_QUOTA, 5, 1L, 1L, 1L, 1L, "", 1, "", "", true, new Vector(), ChargingModes.BOTH, FlowStatus.DISABLED, "", new ServiceType("1", "", 1L, new Vector(), new Vector(), PolicyStatus.SUCCESS), "PCC1",26214400L);

		int result = fixture.compareTo(fixture);

		// add additional test code here
		assertEquals(0, result);
	}



	*//**
	 * Perform post-test clean-up.
	 *
	 * @throws Exception
	 *         if the clean-up fails for some reason
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	@After
	public void tearDown()
		throws Exception {
		// Add additional tear down code here
	}

	*//**
	 * Launch the test.
	 *
	 * @param args the command line arguments
	 *
	 * @generatedBy CodePro at 5/16/13 7:31 PM
	 *//*
	public static void main(String[] args) {
		new org.junit.runner.JUnitCore().run(PCCRuleTest.class);
	}
*/}