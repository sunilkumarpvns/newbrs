package com.elitecore.netvertex.cli;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.EntityType;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.TimeUnit;
import com.elitecore.corenetvertex.constants.ValidityPeriodUnit;
import com.elitecore.corenetvertex.data.PackageType;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.monetaryrechargeplan.MonetaryRechargePlan;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.sliceconfig.DataSliceConfiguration;
import com.elitecore.corenetvertex.pm.store.BoDPackageStore;
import com.elitecore.corenetvertex.pm.store.MonetaryRechargePlanPolicyStore;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.store.RnCPackageStore;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;


@RunWith(JUnitParamsRunner.class)
public class PolicyCommandTest {
	private static final String RELOAD="-load";
	private static final String COMMAND="policy";
	private static final String HELP = "-help";
	private static final String currency="INR";
	private PolicyCommand policyCommand;
	private PolicyDetail policyDetail;
	private List<PolicyDetail> lstPolicyDetail;
	private DummyDetailProvider reloadDetailProvider;
	private DummyNetvertexServerContextImpl serverContext;
	private @Mock PolicyRepository policyRepository;
	private @Mock ProductOfferStore productOfferStore;
	private @Mock RnCPackageStore rnCPackageStore;
	private @Mock BoDPackageStore boDPackageStore;
	private @Mock MonetaryRechargePlanPolicyStore monetaryRechargePlanPolicyStore;
	private DataSliceConfiguration dataSliceConfiguration;

	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		serverContext = new DummyNetvertexServerContextImpl();
		policyCommand=new PolicyCommand(serverContext);
		policyCommand= Mockito.spy(policyCommand);
		reloadDetailProvider = new DummyDetailProvider(RELOAD , "Detail Provider's Help",new HashMap<String, DetailProvider>(4,1));
		reloadDetailProvider = Mockito.spy(reloadDetailProvider);
		policyDetail=new PolicyDetail("", "MyPolicy",PolicyStatus.SUCCESS,PackageType.BASE_DATA_PACKAGE.name(),"Package",PkgMode.LIVE, null);
		lstPolicyDetail=new ArrayList<>();
		when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
		when(productOfferStore.all()).thenReturn(new ArrayList<>());
		when(policyRepository.getRnCPackage()).thenReturn(rnCPackageStore);
		when(rnCPackageStore.all()).thenReturn(new ArrayList<>());
		MonetaryRechargePlan monetaryRechargePlan = createDefaultMonetaryRechargePlan(BigDecimal.TEN, BigDecimal.TEN, new Integer(1));
		when(policyRepository.monetaryRechargePlan()).thenReturn(monetaryRechargePlanPolicyStore);
		when(monetaryRechargePlanPolicyStore.all()).thenReturn(Arrays.asList(monetaryRechargePlan));
		dataSliceConfiguration = new DataSliceConfiguration();
		createDataSliceConfiguration(dataSliceConfiguration);
		when(policyRepository.getSliceConfiguration()).thenReturn(dataSliceConfiguration);
		when(policyRepository.getBoDPackage()).thenReturn(boDPackageStore);
	}

	private void createDataSliceConfiguration(DataSliceConfiguration dataSliceConfiguration) {
		dataSliceConfiguration.setMonetaryReservation(Long.valueOf(10));
		dataSliceConfiguration.setVolumeMinimumSlice(Long.valueOf(10));
		dataSliceConfiguration.setVolumeMaximumSlice(Long.valueOf(400));
		dataSliceConfiguration.setVolumeMinimumSliceUnit(DataUnit.MB);
		dataSliceConfiguration.setVolumeMaximumSliceUnit(DataUnit.MB);
		dataSliceConfiguration.setTimeMinimumSlice(Long.valueOf(10));
		dataSliceConfiguration.setTimeMaximumSlice(Long.valueOf(1));
		dataSliceConfiguration.setTimeMinimumSliceUnit(TimeUnit.SECOND);
		dataSliceConfiguration.setTimeMaximumSliceUnit(TimeUnit.HOUR);
		dataSliceConfiguration.setVolumeSlicePercentage(10);
		dataSliceConfiguration.setTimeSlicePercentage(10);
		dataSliceConfiguration.setVolumeSliceThreshold(10);
		dataSliceConfiguration.setTimeSliceThreshold(10);
	}

	@Test
	public void test_getHelpMsg_should_call_description_of_child_detail_provider() throws RegistrationFailedException{
		policyCommand.registerDetailProvider(reloadDetailProvider );
		policyCommand.getHelpMsg();
		Mockito.verify(policyCommand).getCommandName();
		Mockito.verify(policyCommand).getDescription();
		Mockito.verify(reloadDetailProvider).getDescription();
	}
	
	
	@Test
	@Parameters({"?","-help","-HELP","-HeLp","-HeLP","-hELP","-HELp","-helP","","    "})
	public void test_helpMessage_should_call_when_help_parameter_is_passed(String parameter){
		policyCommand.execute(parameter);
		Mockito.verify(policyCommand).getHelpMsg();
		
	}
	
	public Object[][] data_for_execute_should_call_detailProvide_based_on_argument(){
		
		return new Object[][]{
				{"-load",new String[]{}},
				{"-LOAD",new String[]{}},
				{"-lOaD -HELP",new String[]{"-HELP"}},
				{"-LoAD -help",new String[]{"-help"}},
				{"-Load -Help",new String[]{"-Help"}},
				{"-LOAd -history",new String[]{"-history"}},
				{"-load xyz abc",new String[]{"xyz","abc"}},
				{"-LOAD xyz",new String[]{"xyz"}}
		};
	}
	
	

	
	@Test
	@Parameters(method="data_for_execute_should_call_detailProvide_based_on_argument")
	public void test_execute_should_call_detailProvide_based_on_argument(String parameter,String[] detailProviderArgs) throws RegistrationFailedException{
		policyCommand.registerDetailProvider(reloadDetailProvider);
		policyCommand.execute(parameter);
		Mockito.verify(reloadDetailProvider).execute(detailProviderArgs);
	}


	
	@Test
	@Parameters({"-statu","-SATUS","STatus","-STAt","-Sts","abc","pqr","XYZ"})
	public void test_helpmsg_should_call_when_InvalidArgument_provided(String parameter){
		policyCommand.execute(parameter );
		Mockito.verify(policyCommand).getHelpMsg();
	}
	
	
	@Test
	@Parameters({"-status","-STATUS","-STatus","-STAtus","-Status"})
	public void test_execute_should_call_policyrepository_to_get_status_of_all_the_policies(String parameter){
		lstPolicyDetail.add(policyDetail);
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.getPolicyDetail()).thenReturn(lstPolicyDetail);
		policyCommand.execute(parameter);
		Mockito.verify(policyRepository).getPolicyDetail();
	}
	
	@Test
	public void test_execute_status_should_not_throw_Exception_when_policy_is_Null(){
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.getPolicyDetail()).thenReturn(lstPolicyDetail);
		policyCommand.execute("-status");
		Mockito.verify(policyRepository).getPolicyDetail();
	}
	
	@Test
	public void test_execute_status_should_not_throw_Exception_when_policy_is_Empty(){
		serverContext.setPolicyRepository(policyRepository);
		policyCommand.execute("-status");
		Mockito.verify(policyRepository).getPolicyDetail();
	}
	
	
	public Object[][] data_for_execute_should_call_policyrepository_to_get_status_of_policies_passed_in_argument(){
		
		return new Object[][]{
				{"-Status MY\\ POLICY",new String[]{"MY POLICY"}},
				{"-STATUS PCR",new String[]{"PCR"}},
				{"-Status XYA",new String[]{"XYA"}},
				{"-sTATUS ppp",new String[]{"ppp"}},
				{"-StaTUS aaa",new String[]{"aaa"}},
				{"-STATus MyPolicy Mpolicy",new String[]{"MyPolicy","Mpolicy"}},
				{"-status xyz abc",new String[]{"xyz" ,"abc"}},
				{"-STATUS xyz PQR",new String[]{"xyz","PQR"}}
		};
	}
	
	@Test
	@Parameters(method="data_for_execute_should_call_policyrepository_to_get_status_of_policies_passed_in_argument")
	public void test_execute_should_call_policyrepository_to_get_status_of_policies_passed_in_argument(String parameter,String[] statusCmdArgs){
		serverContext.setPolicyRepository(policyRepository);
		policyCommand.execute(parameter);
		Mockito.verify(policyRepository).getPolicyDetail(statusCmdArgs);
	}
	
	@Test(expected=RegistrationFailedException.class)
	public void test_RegisterDetailProvider_Fails() throws RegistrationFailedException {
		policyCommand.registerDetailProvider(new DummyDetailProvider(null, "Help", null));
	}
	
	@Test(expected=RegistrationFailedException.class)
	public void test_Register_Same_DetailProvider_Fails() throws RegistrationFailedException{
		policyCommand.registerDetailProvider(new DummyDetailProvider(RELOAD, "Help", null));
		policyCommand.registerDetailProvider(new DummyDetailProvider(RELOAD, "Help", null));
	}
	
	
	public Object[][] data_for_hotkeyhelp_for_reload_cmd_should_show_all_the_parameters_with_childDetail_parameters_if_registered(){
		return new Object[][] {
			{new DetailProvider[]{new DummyDetailProvider(RELOAD,"Help For Reload",null)},//new DummyDetailProvider("-reload1","Help For Reload",null)},
				"{'"+COMMAND+"':{'"+HELP+"' :{},"+ "'-status':{},'-view':{'-monetaryRechargePlan':{'monetaryRechargePlan':{}},}," + RELOAD +"}}"
			},
			{new DetailProvider[]{new DummyDetailProvider(RELOAD,"Help For Reload",null),new DummyDetailProvider("-reload1","Help For Reload",null)},
				"{'"+COMMAND+"':{'"+HELP+"' :{}," + "'-status':{},'-view':{'-monetaryRechargePlan':{'monetaryRechargePlan':{}},}," + RELOAD +",-reload1}}"
			},
			{new DetailProvider[]{},
				"{'"+COMMAND+"':{'"+HELP+"' :{}," + "'-status':{},'-view':{'-monetaryRechargePlan':{'monetaryRechargePlan':{}},}" + "}}"
			}
		};
	}
	
	@Test
	@Parameters(method="data_for_hotkeyhelp_for_reload_cmd_should_show_all_the_parameters_with_childDetail_parameters_if_registered")
	public void test_hotkeyhelp_for_reload_cmd_should_show_all_the_parameters_with_childDetail_parameters_if_registered(DetailProvider[] dummyDetailProvider,String expectedHotKeyMsg ) throws RegistrationFailedException{
		serverContext.setPolicyRepository(policyRepository);
		for(DetailProvider dummy : dummyDetailProvider){
			policyCommand.registerDetailProvider(dummy);
		}
		assertEquals(expectedHotKeyMsg, policyCommand.getHotkeyHelp());
	}

	@Test
	public void testsGettingHotKeyHelpGivesAllAvailableRnCPackages(){

		RnCPackage rnCPackage;
		rnCPackage = new RnCPackage(null, "RnC_Name", null,
				null, null,
				null,null, null,
				null, null, null,
				null, null, ChargingType.SESSION,currency);

		List list = new ArrayList();
		list.add(rnCPackage);

		when(rnCPackageStore.all()).thenReturn(list);
		serverContext.setPolicyRepository(policyRepository);
		Assert.assertEquals("{'policy':{'-help' :{},'-status':{},'-view':{'-rnc':{'RnC_Name':{}}," + "'-monetaryRechargePlan':{'monetaryRechargePlan':{}},"+"}}}",policyCommand.getHotkeyHelp());
	}

	@Test
	public void testsItReturnsAllAvailablePackageNamesWhenViewingRnCPackageButNameNotSpecifiedInTheCommand(){

		RnCPackage rnCPackage = new RnCPackage(null, "RnC_Name", null,
				null, null,
				null,null, null,
				null, null, null,
				null, null, ChargingType.SESSION,currency);

		List list = new ArrayList();
		list.add(rnCPackage);

		when(rnCPackageStore.all()).thenReturn(list);
		serverContext.setPolicyRepository(policyRepository);

		Assert.assertEquals("\n\tSelect rnc package name to view\n\t---------------------------\n\n\tRnC_Name\n\n",
				policyCommand.execute("-view -rnc"));
	}

	@Test
	public void testsItReturnsRnCPackageDetailsWhenViewingRnCPackageAndNameIsSpecified(){

		RnCPackage rnCPackage = spy(new RnCPackage(null, "RnC_Name", null,
				null, null,
				null,null, null,
				null, null, null,
				null, null, ChargingType.SESSION,currency));

		doReturn("RnC Package Details").when(rnCPackage).toString();

		List list = new ArrayList();
		list.add(rnCPackage);

		when(rnCPackageStore.all()).thenReturn(list);
		serverContext.setPolicyRepository(policyRepository);

		Assert.assertEquals("\nRnC Package Details\n\n",
				policyCommand.execute("-view -rnc RnC_Name"));
	}

	@Test
	public void testsItReturnsDataSliceConfigurationWhenViewingPolicy() {

		DataSliceConfiguration dataSliceConfiguration = new DataSliceConfiguration();
		createDataSliceConfiguration(dataSliceConfiguration);
		serverContext.setPolicyRepository(policyRepository);

		String dataSliceConfigurationStr = dataSliceConfiguration.toString();
		String policyRepositorySliceConfigurationStr = policyCommand.execute("-view -slice-configuration");

		Assert.assertEquals(policyRepositorySliceConfigurationStr, dataSliceConfigurationStr);
	}

	@Test
	public void testsItReturnsNoPackageFoundMessageWhenThereIsNoRnCPackageWithMatchingName(){

		RnCPackage rnCPackage = spy(new RnCPackage(null, "RnC_Name", null,
				null, null,
				null,null, null,
				null, null, null,
				null, null, ChargingType.SESSION,currency));

		doReturn("RnC Package Details").when(rnCPackage).toString();

		List list = new ArrayList();
		list.add(rnCPackage);

		when(rnCPackageStore.all()).thenReturn(list);
		serverContext.setPolicyRepository(policyRepository);

		Assert.assertEquals("NO RnC PACKAGE FOUND\n\n",
				policyCommand.execute("-view -rnc RnC_No"));
	}

	@Test
	public void returnsAllBoDNamesWhenViewingBoDPackagesWithoutSpecifyingBoDName(){

		BoDPackage boDPackage = BoDPackageDummyBuilder.createDummyBoDPackage("VIEW_TEST");

		List bodPackages = Collectionz.newArrayList();
		bodPackages.add(boDPackage);

		when(boDPackageStore.all()).thenReturn(bodPackages);
		serverContext.setPolicyRepository(policyRepository);

		assertEquals(BoDPackageDummyBuilder.viewBoDPackageNames(new ArrayList<String>(Arrays.asList(boDPackage.getName())), "view")
				, policyCommand.execute("-view -bod"));
	}

	@Test
	public void returnsNoBoDConfiguredMsgWhenNoBoDPackages(){

		when(boDPackageStore.all()).thenReturn(Collectionz.newArrayList());
		serverContext.setPolicyRepository(policyRepository);

		assertEquals(BoDPackageDummyBuilder.viewBoDPackageNames(Collectionz.newArrayList(), "view")
				, policyCommand.execute("-view -bod"));
	}

	@Test
	public void returnsBoDDefinitionWhenViewingBoDPackagesWithSpecifyingBoDName(){

		BoDPackage boDPackage = BoDPackageDummyBuilder.createDummyBoDPackage("VIEW_TEST");

		List bodPackages = Collectionz.newArrayList();
		bodPackages.add(boDPackage);

		when(boDPackageStore.all()).thenReturn(bodPackages);
		serverContext.setPolicyRepository(policyRepository);

		assertEquals(BoDPackageDummyBuilder.viewBoDPackagesDefinition(bodPackages)
				, policyCommand.execute("-view -bod "+boDPackage.getName()));
	}

	@Test
	public void returnsNoBoDPackageFoundMsgWhenWrongBoDNameGiven(){

		assertEquals(BoDPackageDummyBuilder.viewBoDPackagesDefinition(null)
				, policyCommand.execute("-view -bod Wrong_BoD_Name"));
	}

	@Test
	public void returnsAllBoDPolicyStatusWhenCheckingBoDStatus(){

		BoDPackage boDPackage = BoDPackageDummyBuilder.createDummyBoDPackage("STATUS_TEST");
		List<PolicyDetail> policyDetails = Collectionz.newArrayList();
		policyDetails.add(createDummyPolicyDetail(boDPackage));
		doReturn(policyDetails).when(policyRepository).getPolicyDetail();
		serverContext.setPolicyRepository(policyRepository);

		assertEquals(BoDPackageDummyBuilder.viewPolicyStatus(policyDetails), policyCommand.execute("-status"));
	}

	@Test
	public void returnsSpecificBoDPolicyStatusWhenCheckingBoDStatusWithName(){

		BoDPackage boDPackage = BoDPackageDummyBuilder.createDummyBoDPackage("STATUS_TEST");
		List<PolicyDetail> policyDetails = Collectionz.newArrayList();
		policyDetails.add(createDummyPolicyDetail(boDPackage));
		doReturn(policyDetails).when(policyRepository).getPolicyDetail(boDPackage.getName());
		serverContext.setPolicyRepository(policyRepository);

		assertEquals(BoDPackageDummyBuilder.viewPolicyStatus(policyDetails)
				, policyCommand.execute("-status "+boDPackage.getName()));
	}

	@Test
	public void returnsNoPolicyFoundMsgWhenCheckingBoDStatusWithWrongName(){
		assertEquals(BoDPackageDummyBuilder.viewPolicyStatus(null)
			, policyCommand.execute("-status Wrong_BoD_Name"));
	}

	@Test
	public void returnsNoPolicyFoundMsgWhenNoBoDPackagesConfigured(){
		assertEquals(BoDPackageDummyBuilder.viewPolicyStatus(null)
			, policyCommand.execute("-status"));
	}

	
	private class DummyDetailProvider extends DetailProvider {

		String key;
		String helpMessage;
		Map<String , DetailProvider> reloadDetailProviderMap;

		public DummyDetailProvider(String key, String helpMessage,
				Map<String, DetailProvider> map){
			this.key = key;
			this.helpMessage = helpMessage;
			this.reloadDetailProviderMap = map;
		}

		
		
		
		
		@Override
		public String execute(String[] parameters) {
			// TODO Auto-generated method stub
			return "Dummy Detail Provider's Execute Called";
		}
 
		@Override
		public String getHelpMsg() {
			// TODO Auto-generated method stub
			return helpMessage;
		}

		@Override
		public String getKey() {
			// TODO Auto-generated method stub
			return key;
		}

		@Override
		public HashMap<String, DetailProvider> getDetailProviderMap() {
			// TODO Auto-generated method stub
			return (HashMap<String, DetailProvider>) reloadDetailProviderMap;
		}

		@Override
		public String getHotkeyHelp() {
			// TODO Auto-generated method stub
			return key;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return super.getDescription();
		}

	}

	private MonetaryRechargePlan createDefaultMonetaryRechargePlan(BigDecimal price, BigDecimal amount, Integer updatedValidity) {
		return new MonetaryRechargePlan(UUID.randomUUID().toString(), "monetaryRechargePlan", price, amount, updatedValidity, ValidityPeriodUnit.DAY,
				"Description", PkgMode.TEST, PolicyStatus.SUCCESS, new ArrayList<>(), PkgStatus.ACTIVE, "", "");
	}

	private PolicyCacheDetail createPolicyCacheDetail(){
		return new PolicyCacheDetail.PolicyCacheDetailBuilder()
				.withFailureCounter(0)
				.withLastKnownGoodCounter(0)
				.withSuccessCounter(1)
				.withPartialSuccessCounter(0)
				.withFailurePolicyList(new ArrayList<>())
				.withPartialSuccessPolicyList(new ArrayList<>())
				.withSuccessPolicyList(new ArrayList<>())
				.withLastKnownGoodPolicyList(new ArrayList<>())
				.build();
	}


	private PolicyDetail createDummyPolicyDetail(BoDPackage boDPackage){
		return new PolicyDetail(boDPackage.getId(), boDPackage.getName(), boDPackage.getPolicyStatus(), EntityType.BOD.getValue(),
				EntityType.BOD.getValue(), boDPackage.getPackageMode(), null);
	}
}
