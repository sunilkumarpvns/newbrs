package com.elitecore.netvertex.cli;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.util.cli.cmd.DetailProvider;
import com.elitecore.core.util.cli.cmd.RegistrationFailedException;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.data.PackageType;
import com.elitecore.corenetvertex.data.PolicyCacheDetail;
import com.elitecore.corenetvertex.data.PolicyCacheDetail.PolicyCacheDetailBuilder;
import com.elitecore.corenetvertex.data.PolicyDetail;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.bod.BoDPackage;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.rnc.pkg.RnCPackage;
import com.elitecore.corenetvertex.pm.store.BoDPackageStore;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.pm.store.RnCPackageStore;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import junitparams.JUnitParamsRunner;
import junitparams.Parameters;
import org.apache.commons.collections.IteratorUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;

@RunWith(JUnitParamsRunner.class)
public class ReloadDetailProviderTest {
	
	private static final String RELOAD = "-reload";
	private static final String HISTORY = "-history";
	private static final String HELP = "-help";
	private static final String CURRENCY="INR";
	private ReloadDetailProvider reloadDetailProvider;
	private DummyNetvertexServerContextImpl serverContext;
	private DummyDetailProvider dummyDetailProvider;
	private @Mock PolicyRepository policyRepository;
	private @Mock ProductOfferStore productOfferStore;
	private @Mock RnCPackageStore rnCPackageStore;
	private @Mock BoDPackageStore boDPackageStore;
	private PolicyCacheDetail policyCacheDetail;
	List<PolicyDetail> lstPolicyDetail;
	
	@Before
	public void setUp(){
		MockitoAnnotations.initMocks(this);
		serverContext = new DummyNetvertexServerContextImpl();
		reloadDetailProvider=new ReloadDetailProvider(serverContext);
		reloadDetailProvider = spy(reloadDetailProvider);
		dummyDetailProvider=new DummyDetailProvider("-temp", "Calling dummy detail provider's help",new HashMap<String, DetailProvider>(4,1));
		dummyDetailProvider = spy(dummyDetailProvider);
		policyCacheDetail = new PolicyCacheDetailBuilder().build();
		policyCacheDetail = spy(policyCacheDetail);
		lstPolicyDetail=new ArrayList<PolicyDetail>();
		
		
		when(policyRepository.reloadHistory()).then(new Answer<Iterator<PolicyCacheDetail>>() {

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<PolicyCacheDetail> answer(InvocationOnMock invocation) throws Throwable {
				return IteratorUtils.getIterator(new PolicyCacheDetailBuilder().build());
				
			}
		});
		
		when(policyRepository.reload()).thenReturn(new PolicyCacheDetailBuilder().build());
		serverContext.setPolicyRepository(policyRepository);

		when(policyRepository.getProductOffer()).thenReturn(productOfferStore);
		when(productOfferStore.all()).thenReturn(new ArrayList<>());
		when(policyRepository.getRnCPackage()).thenReturn(rnCPackageStore);
		when(rnCPackageStore.all()).thenReturn(new ArrayList<>());
		when(policyRepository.getBoDPackage()).thenReturn(boDPackageStore);
	}
	//Test cases for helpMsg
	@Test
	public void test_getHelpMsgCall_should_call_helpMessage_of_child_detail_provider() throws RegistrationFailedException{
		reloadDetailProvider.registerDetailProvider(dummyDetailProvider);
		reloadDetailProvider.getHelpMsg();
		Mockito.verify(dummyDetailProvider).getDescription();
	}
	
	@Test
	@Parameters({"?","-HELP","-Help","-help","-hELP"})
	public void test_helpMessage_should_call_when_help_parameter_is_passed(String parameters) throws RegistrationFailedException{
		reloadDetailProvider.execute(new String[]{parameters});
		Mockito.verify(reloadDetailProvider).getHelpMsg();
		
		
		}
	
	//Test cases for relaodHistory()
	@Test
	@Parameters({"-history" ,"-HISTORY","-History" ,"-HIStory","-HISTOry"})
	public void test_reload_should_call_reloadHistory_of_policyRepository_when_history_option_is_passed(String parameters) throws RegistrationFailedException{
		serverContext.setPolicyRepository(policyRepository);
		reloadDetailProvider.execute(new String[]{parameters});
		Mockito.verify(policyRepository).reloadHistory();
	}
	
	//Test case for Invalid Argument with history Command
	@Test
	@Parameters({"-histry","history","-history abc","-HISTORY pqw abc","-History temp" ,"-HIStory aaa","-HISTOry xzzz","abc","pqr","XYZ"})
	public void test_reload_should_call_getHelpMessage_when_Invalid_argument_is_passed(String parameters) throws RegistrationFailedException{
		serverContext.setPolicyRepository(policyRepository);
		System.out.println(reloadDetailProvider.execute(new String[]{parameters}));
		Mockito.verify(reloadDetailProvider).getHelpMsg();
		
	}
	
	@Test
	public void test_execute_reload_histroy_should_not_throw_Exception_when_policyCacheDetail_list_is_Null(){
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.reloadHistory()).thenReturn(null);
		reloadDetailProvider.execute(new String[]{"-history"});
		Mockito.verify(policyRepository).reloadHistory();
	}
	
	@Test
	public void test_execute_history_should_not_throw_Exception_when_policyCacheDetail_list_is_Empty(){
		when(policyRepository.reloadHistory()).then(new Answer<Iterator<PolicyCacheDetail>>() {

			@SuppressWarnings("unchecked")
			@Override
			public Iterator<PolicyCacheDetail> answer(InvocationOnMock invocation) throws Throwable {
				return IteratorUtils.emptyIterator();
				
			}
		});
		reloadDetailProvider.execute(new String[]{"-history"});
		Mockito.verify(policyRepository).reloadHistory();
	}
	
	
	//Test case when no parameter is passed
	@Test
	public void test_execute_reload_when_No_parameter_is_passed(){
		serverContext.setPolicyRepository(policyRepository);
		
		reloadDetailProvider.execute(new String[]{});
		Mockito.verify(policyRepository).reload();
	}
	
	
	//Register Detail provider fails
	@Test(expected=RegistrationFailedException.class)
	public void test_Registration_fail_for_detailProivder_when_key_is_null() throws RegistrationFailedException{
		reloadDetailProvider.registerDetailProvider(new DummyDetailProvider(null, "help", null));
	}
	
	@Rule public ExpectedException expectedException = ExpectedException.none();
	
	@Test
	public void test_Registration_fail_for_detailProivder_when_registerd_twice() throws RegistrationFailedException{
		reloadDetailProvider.registerDetailProvider(new DummyDetailProvider("-load", "help", null));
		
		expectedException.expect(RegistrationFailedException.class);
		reloadDetailProvider.registerDetailProvider(new DummyDetailProvider("-LOad", "help", null));
	}
	
	
	public Object[][] data_for_execute_should_call_detailProvide_based_on_argument(){
		
		return new Object[][]{
				{"-temp",new String[]{}},
				{"-TEMP",new String[]{}},
				{"-tEmP -HELP",new String[]{"-HELP"}},
				{"-TeMP -help",new String[]{"-help"}},
				{"-Temp -Help",new String[]{"-Help"}},
				{"-TEMp -history",new String[]{"-history"}},
				{"-temp xyz abc",new String[]{"xyz","abc"}},
				{"-TEMP xyz",new String[]{"xyz"}}
		};
	}
	
	@Test
	@Parameters(method="data_for_execute_should_call_detailProvide_based_on_argument")
	public void test_execute_should_call_detailProvide_based_on_argument(String parameter,String[] detailProviderArgs) throws RegistrationFailedException{
		reloadDetailProvider.registerDetailProvider(dummyDetailProvider);
		reloadDetailProvider.execute(parameter.split(" "));
		Mockito.verify(dummyDetailProvider).execute(detailProviderArgs);
	}
	
	//HotKey Help TestCases
	public Object[][] data_for_hotkeyhelp_for_reload_cmd_should_show_all_the_parameters_with_childDetail_parameters_if_registered(){
		
		String prefix = "'"+RELOAD+"':{'"+HISTORY+"':{},'"+HELP+"':{}";
		String suffix = "}";
		
		return new Object[][] {
			{new DetailProvider[]{new DummyDetailProvider("-load","Help For Reload",null)},
				prefix + ",-load" +suffix
			},
			
			{new DetailProvider[]{new DummyDetailProvider("-load","Help For Reload",null),new DummyDetailProvider("-reload1","Help For Reload",null)},
				prefix + ",-load,-reload1" + suffix
			},
			
			{new DetailProvider[]{},
				prefix + suffix
			}
		};
	}
	
	
	@Test
	@Parameters(method="data_for_hotkeyhelp_for_reload_cmd_should_show_all_the_parameters_with_childDetail_parameters_if_registered")
	public void test_hotkeyhelp_for_reload_cmd_should_show_all_the_parameters_with_childDetail_parameters_if_registered(DetailProvider[] detailProviders,String expectedHotKeyMsg) throws RegistrationFailedException{
		
		for(int i = 0; i < detailProviders.length ; i++){
			reloadDetailProvider.registerDetailProvider(detailProviders[i]);
		}
		
		assertEquals(expectedHotKeyMsg, reloadDetailProvider.getHotkeyHelp());
	}
	
	@Test
	public void test_hotkeyhelp_for_reload_data_cmd_should_show_all_data_packages() throws RegistrationFailedException{
		
		when(serverContext.getPolicyRepository().getAllDataPackageNames()).thenReturn(Arrays.asList("Base1,Base2,Base3"));
		
		assertEquals("'-reload':{'-history':{},'-help':{},'-data':{'Base1,Base2,Base3':{}}}", reloadDetailProvider.getHotkeyHelp());
	}
	
	@Test
	public void test_hotkeyhelp_for_reload_IMS_cmd_should_show_all_IMS_packages() throws RegistrationFailedException{
		
		when(serverContext.getPolicyRepository().getAllIMSPackageNames()).thenReturn(Arrays.asList("IMS1,IMS2,IMS3"));
		
		assertEquals("'-reload':{'-history':{},'-help':{},'-ims':{'IMS1,IMS2,IMS3':{}}}", reloadDetailProvider.getHotkeyHelp());
	}
	
	
	//reload() should not throw exception when PolicyList is null
	@Test
	public void test_execute_reload_should_not_throw_Exception_when_PolicyList_is_Null(){
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.reload()).thenReturn(policyCacheDetail);
		when(policyCacheDetail.getFailurePolicyList()).thenReturn(lstPolicyDetail);
		reloadDetailProvider.execute(new String[]{});
		Mockito.verify(policyCacheDetail).getFailurePolicyList();
	}
	
	@Test
	public void test_execute_reload_should_not_throw_Exception_when_policyList_is_Empty(){
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.reload()).thenReturn(policyCacheDetail);
		//when(policyCacheDetail.getListPolicy()).thenReturn(new ArrayList<PolicyDetail>());
		reloadDetailProvider.execute(new String[]{});
		Mockito.verify(policyCacheDetail).getFailurePolicyList();
	}
	
	
	
	
	//When Policy List is not Empty and Not null then list status of a policy with remark and without remark
	@Test
	public void test_execute_reload_should_list_policy_status_when_policy_list_is_not_Empty_with_Remark_list_AS_null(){
		lstPolicyDetail.add(new PolicyDetail("", "MyPolicy", PolicyStatus.SUCCESS,PackageType.BASE_DATA_PACKAGE.name(),"Package",PkgMode.LIVE,null));
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.reload()).thenReturn(policyCacheDetail);
		when(policyCacheDetail.getFailurePolicyList()).thenReturn(lstPolicyDetail);
		reloadDetailProvider.execute(new String[]{});
		Mockito.verify(policyCacheDetail).getFailurePolicyList();
	}
	
	
	@Test
	public void test_execute_reload_should_list_policy_status_when_policy_list_is_not_Empty_but_with_No_Remark(){
		lstPolicyDetail.add(new PolicyDetail("", "MyPolicy", PolicyStatus.SUCCESS,PackageType.BASE_DATA_PACKAGE.name(),"Package",PkgMode.LIVE,""));
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.reload()).thenReturn(policyCacheDetail);
		when(policyCacheDetail.getFailurePolicyList()).thenReturn(lstPolicyDetail);
		reloadDetailProvider.execute(new String[]{});
		Mockito.verify(policyCacheDetail).getFailurePolicyList();
	}
	
	@Test
	public void test_execute_reload_should_list_policy_status_when_policy_list_is_not_Empty_with_remarks(){
		lstPolicyDetail.add(new PolicyDetail("", "MyPolicy", PolicyStatus.SUCCESS,PackageType.BASE_DATA_PACKAGE.name(),"Package",PkgMode.LIVE,"Problem in configuring Session Auth rule"));
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.reload()).thenReturn(policyCacheDetail);
		when(policyCacheDetail.getFailurePolicyList()).thenReturn(lstPolicyDetail);
		reloadDetailProvider.execute(new String[]{});
		Mockito.verify(policyCacheDetail).getFailurePolicyList();
	}



	@Test
	public void test_execute_reload_should_list_policy_status_when_policy_list_is_not_null(){
		//List<String> remarks=new ArrayList<String>();
		//remarks.add("Problem in configuring Session Auth rule");
		lstPolicyDetail.add(new PolicyDetail("", "MyPolicy", PolicyStatus.FAILURE,PackageType.BASE_DATA_PACKAGE.name(),"Package",PkgMode.LIVE,null));
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.reload()).thenReturn(policyCacheDetail);
		when(policyCacheDetail.getFailurePolicyList()).thenReturn(lstPolicyDetail);
		reloadDetailProvider.execute(new String[]{});
		Mockito.verify(policyCacheDetail).getFailurePolicyList();
	}

	@Test
	public void testItReturnsNoOfferConfiguredMessageWhenOfferConfigurationIsEmpty(){
		lstPolicyDetail.add(new PolicyDetail("", "MyPolicy", PolicyStatus.FAILURE,PackageType.BASE_DATA_PACKAGE.name(),"Package",PkgMode.LIVE,null));
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.reload()).thenReturn(policyCacheDetail);
		when(policyCacheDetail.getFailurePolicyList()).thenReturn(lstPolicyDetail);

		String expected = "No product offer configured\n\n";
		String actual = reloadDetailProvider.execute(new String[]{"-offer"});
		assertEquals(expected,actual);
	}

	@Test
	public void testItAsksForOfferNameWhenNoOfferNameIsGivenInCommand(){
		lstPolicyDetail.add(new PolicyDetail("", "MyPolicy", PolicyStatus.FAILURE,PackageType.BASE_DATA_PACKAGE.name(),"Package",PkgMode.LIVE,null));
		serverContext.setPolicyRepository(policyRepository);
		when(policyRepository.reload()).thenReturn(policyCacheDetail);
		when(policyCacheDetail.getFailurePolicyList()).thenReturn(lstPolicyDetail);

		List<ProductOffer> ProductOffList = new ArrayList<>();
		ProductOffList.add(new ProductOffer(
				null, "Offer1", null,
				PkgType.BASE, PkgMode.DESIGN, null,
				null, 0.0, 0.0,
				 null, null, null,
				null, new ArrayList<>(), null,
				null, null,  null, null, false, null, null, null,null,null,new HashMap<>(),CURRENCY
		));
		when(productOfferStore.all()).thenReturn(ProductOffList);


		String expected = "\n\tSelect product offer name to reload\n\t---------------------------\n\n\tOffer1\n\n";
		String actual = reloadDetailProvider.execute(new String[]{"-offer"});
		assertEquals(expected,actual);
	}

	@Test
	public void testsGettingHotKeyHelpGivesAllAvailableRnCPackageNames(){

		RnCPackage rnCPackage = new RnCPackage(null, "RnC_Name", null,
				null, null,
				null,null, null,
				null, null, null,
				null, null, ChargingType.SESSION,CURRENCY);

		List list = new ArrayList();
		list.add(rnCPackage);

		when(rnCPackageStore.all()).thenReturn(list);
		serverContext.setPolicyRepository(policyRepository);
		assertEquals("'-reload':{'-history':{},'-help':{},'-rnc':{'RnC_Name':{}}}",
				reloadDetailProvider.getHotkeyHelp());
	}

	@Test
	public void testsItReturnsAllAvailablePackageNamesWhenViewingRnCPackageButNameNotSpecifiedInTheCommand(){

		RnCPackage rnCPackage = new RnCPackage(null, "RnC_Name", null,
				null, null,
				null,null, null,
				null, null, null,
				null, null, ChargingType.SESSION,CURRENCY);

		List list = new ArrayList();
		list.add(rnCPackage);

		when(rnCPackageStore.all()).thenReturn(list);
		serverContext.setPolicyRepository(policyRepository);

		assertEquals("\n\tSelect rnc package name to reload\n\t---------------------------\n\n\tRnC_Name\n\n",
				reloadDetailProvider.execute(new String[]{"-rnc"}));
	}

	@Test
	public void testsItReturnsRnCPackageDetailsWhenReloadingRnCPackageAndNameIsSpecified(){

		RnCPackage rnCPackage = spy(new RnCPackage(null, "RnC_Name", null,
				null, null,
				null,null, null,
				null, null, null,
				null, null, ChargingType.SESSION,CURRENCY));

		doReturn(createPolicyCacheDetail()).when(policyRepository).reloadRnCPackages(any(java.util.function.Predicate.class));

		List list = new ArrayList();
		list.add(rnCPackage);

		when(rnCPackageStore.all()).thenReturn(list);
		serverContext.setPolicyRepository(policyRepository);

		assertEquals("\nReload Status: SUCCESS\nSuccess Policy Count         :  1\n" +
						"Partial Success Policy Count :  0\nFailure Policy Count         :  0\nLast Known Good Policy Count :  0\n",
				reloadDetailProvider.execute(new String[]{"-rnc","RnC_Name"}));
	}


	@Test
	public void asksForBoDNameWhenNoBoDNameIsGivenInCommand(){

		BoDPackage boDPackage = BoDPackageDummyBuilder.createDummyBoDPackage("RELOAD_TEST");
		doReturn(createPolicyCacheDetail()).when(policyRepository).reloadBoDPackages(any(java.util.function.Predicate.class));

		List bodPackages = Collectionz.newArrayList();
		bodPackages.add(boDPackage);

		when(boDPackageStore.all()).thenReturn(bodPackages);
		serverContext.setPolicyRepository(policyRepository);

		assertEquals(BoDPackageDummyBuilder.viewBoDPackageNames(new ArrayList<String>(Arrays.asList(boDPackage.getName())), "reload")
				, reloadDetailProvider.execute(new String[]{"-bod"}));
	}

	@Test
	public void returnsBoDPackagePolicyDetailsWhenReloadingBoDPackageWithName(){

		BoDPackage boDPackage = BoDPackageDummyBuilder.createDummyBoDPackage("RELOAD_TEST");
		doReturn(createPolicyCacheDetail()).when(policyRepository).reloadBoDPackages(any(java.util.function.Predicate.class));

		List list = Collectionz.newArrayList();
		list.add(boDPackage);

		when(boDPackageStore.all()).thenReturn(list);
		serverContext.setPolicyRepository(policyRepository);

		assertEquals("\nReload Status: SUCCESS\nSuccess Policy Count         :  1\n" +
						"Partial Success Policy Count :  0\nFailure Policy Count         :  0\nLast Known Good Policy Count :  0\n",
				reloadDetailProvider.execute(new String[]{"-bod",boDPackage.getName()}));
	}

	@Test
	public void returnsNoBoDConfiguredMsgWhenNoBoDPackages(){

		doReturn(createPolicyCacheDetail()).when(policyRepository).reloadBoDPackages(any(java.util.function.Predicate.class));

		when(boDPackageStore.all()).thenReturn(Collectionz.newArrayList());
		serverContext.setPolicyRepository(policyRepository);

		assertEquals(BoDPackageDummyBuilder.viewBoDPackageNames(Collectionz.newArrayList(), "reload")
				, reloadDetailProvider.execute(new String[]{"-bod"}));
	}

	private PolicyCacheDetail createPolicyCacheDetail(){
		return new PolicyCacheDetailBuilder()
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
			return key;
		}

		@Override
		public String getDescription() {
			// TODO Auto-generated method stub
			return "Describes Detail Proivder";
		}

	}

}
