package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.EmergencyPackage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.util.PCRFRequestBuilder;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.StringWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertNull;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.*;

public class EmergencyPolicyHandlerTest {

	private static final String NAME = "name1";
	private static final String ID = "id1";
	private static final String GROUP1 = "group1";
	private static final String GROUP2 = "group2";
	private static final String GROUP3 = "group3";
	public static final String PKG_ID_1 = "PKG_ID1";
	public static final String PKG_ID_2 = "PKG_ID_2";

	private QoSInformation qosInformation;
	private PCRFRequest pcrfRequest;
	private List<String> groupIds;
	
	@Mock private SPRInfo sprInfo;
	@Mock private PolicyContext policyContext;
	@Mock private PCRFServiceContext pcrfServiceContext;
	@Mock private PolicyRepository policyRepository;
	@Mock private NetVertexServerContext netVertexServerContext;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.qosInformation = new QoSInformation();
		this.pcrfRequest = new PCRFRequestBuilder(PCRFEvent.AUTHORIZE).build();
		this.groupIds = Arrays.asList(GROUP1,"group2","group3");

		when(policyContext.getTraceWriter()).thenReturn(new IndentingPrintWriter(new StringWriter()));
		//doReturn(netVertexServerContext).when(pcrfServiceContext).getServerContext();
		doReturn(pcrfRequest).when(policyContext).getPCRFRequest();
		doReturn(sprInfo).when(policyContext).getSPInfo();
		doReturn(groupIds).when(sprInfo).getSPRGroupIds();
		doReturn(netVertexServerContext).when(pcrfServiceContext).getServerContext();
		doReturn(policyRepository).when(netVertexServerContext).getPolicyRepository();
	}
	
	@Test
	public void test_apply_should_give_null_when_no_emergency_policy_satisfied() {
		
		doReturn(policyRepository).when(netVertexServerContext).getPolicyRepository();

		when(policyRepository.getEmergencyPackages()).thenReturn(Collectionz.<EmergencyPackage>newArrayList());

		EmergencyPolicyHandler emergencyPolicyHandler = new EmergencyPolicyHandler(pcrfServiceContext);
		
		assertNull(emergencyPolicyHandler.applyPackage(policyContext, qosInformation));
	}

	@Test
	public void test_apply_should_give_null_when_no_spr_groups_configured() {
		doReturn(null).when(sprInfo).getSPRGroupIds();

		EmergencyPackage mockedEmergencyPackage1 = mock(EmergencyPackage.class);

		List<EmergencyPackage> emergencyPackages = Arrays.asList(mockedEmergencyPackage1);

		when(policyRepository.getEmergencyPackages()).thenReturn(emergencyPackages);

		EmergencyPolicyHandler emergencyPolicyHandler = new EmergencyPolicyHandler(pcrfServiceContext);

		assertNull(emergencyPolicyHandler.applyPackage(policyContext, qosInformation));
	}
	
	
	@Test
	public void test_apply_should_select_first_satisfied_policy_only() throws Exception {

		EmergencyPackage mockedEmergencyPackage1 = getMockedPackage(PKG_ID_1);
		EmergencyPackage mockedEmergencyPackage2 = getMockedPackage(PKG_ID_2);

		/// same package passed twice
		ArrayList<EmergencyPackage> emergencyPackages = new ArrayList<EmergencyPackage>(Arrays.asList(mockedEmergencyPackage1, mockedEmergencyPackage2, mockedEmergencyPackage1));

		when(policyRepository.getEmergencyPackagesOfGroup(anyString())).thenReturn(emergencyPackages);
		when(policyRepository.getEmergencyPackages()).thenReturn(emergencyPackages);

		EmergencyPolicyHandler emergencyPolicyHandler = new EmergencyPolicyHandler(pcrfServiceContext);
		
		emergencyPolicyHandler = spy(emergencyPolicyHandler);
		
		doReturn(true).when(emergencyPolicyHandler).isPolicyApplied(policyContext, qosInformation, mockedEmergencyPackage1);
		
		emergencyPolicyHandler.applyPackage(policyContext, qosInformation);
		
		// one time call for first package
		verify(emergencyPolicyHandler, times(1)).isPolicyApplied(policyContext, qosInformation, mockedEmergencyPackage1);
		// No call for second package
		verify(emergencyPolicyHandler, times(0)).isPolicyApplied(policyContext, qosInformation, mockedEmergencyPackage2);
	}
	
	@Test
	public void test_apply_should_apply_same_package_one_time_only() throws Exception {
		
		EmergencyPackage mockedEmergencyPackage1 = getMockedPackage(PKG_ID_1);
		EmergencyPackage mockedEmergencyPackage2 = getMockedPackage(PKG_ID_2);
		/// same package passed twice
		ArrayList<EmergencyPackage> packagesGroup1 = new ArrayList<EmergencyPackage>(Arrays.asList(mockedEmergencyPackage1, mockedEmergencyPackage2, mockedEmergencyPackage1));
		ArrayList<EmergencyPackage> packagesGroup2 = new ArrayList<EmergencyPackage>(Arrays.asList(mockedEmergencyPackage1, mockedEmergencyPackage2, mockedEmergencyPackage1));

		when(policyRepository.getEmergencyPackages()).thenReturn(packagesGroup1);
		doReturn(packagesGroup1).when(policyRepository).getEmergencyPackagesOfGroup(GROUP1);
		doReturn(packagesGroup2).when(policyRepository).getEmergencyPackagesOfGroup(GROUP2);

		EmergencyPolicyHandler emergencyPolicyHandler = new EmergencyPolicyHandler(pcrfServiceContext);
		
		emergencyPolicyHandler = spy(emergencyPolicyHandler);
		
		doReturn(false).when(emergencyPolicyHandler).isPolicyApplied(policyContext, qosInformation, mockedEmergencyPackage1);
		doReturn(false).when(emergencyPolicyHandler).isPolicyApplied(policyContext, qosInformation, mockedEmergencyPackage2);
		
		emergencyPolicyHandler.applyPackage(policyContext, qosInformation);
		
		// one time call for first package
		verify(emergencyPolicyHandler, times(1)).isPolicyApplied(policyContext, qosInformation, mockedEmergencyPackage1);
		// No call for second package
		verify(emergencyPolicyHandler, times(1)).isPolicyApplied(policyContext, qosInformation, mockedEmergencyPackage2);
	}

	private EmergencyPackage getMockedPackage(String pkgId1) {
		EmergencyPackage mockedPackage = mock(EmergencyPackage.class);
		when(mockedPackage.getId()).thenReturn(pkgId1);
		return mockedPackage;
	}

	@Test
	public void test_apply_should_process_all_groups_even_if_one_group_not_belogs_to_any_package() throws Exception {

		EmergencyPackage mockedPackage1 = getMockedPackage(PKG_ID_1);
		EmergencyPackage mockedPackage2 = getMockedPackage(PKG_ID_2);

		ArrayList<EmergencyPackage> packagesForGroup1 = new ArrayList<EmergencyPackage>(Arrays.asList(mockedPackage1));
		ArrayList<EmergencyPackage> packagesForGroup2 = new ArrayList<EmergencyPackage>(Arrays.asList(mockedPackage2));

		doReturn(packagesForGroup1).when(policyRepository).getEmergencyPackages();
		doReturn(packagesForGroup1).when(policyRepository).getEmergencyPackagesOfGroup(GROUP1);
		doReturn(null).when(policyRepository).getEmergencyPackagesOfGroup(GROUP2);
		doReturn(packagesForGroup2).when(policyRepository).getEmergencyPackagesOfGroup(GROUP3);

		EmergencyPolicyHandler policyHandler = new EmergencyPolicyHandler(pcrfServiceContext);

		policyHandler = spy(policyHandler);

		doReturn(false).when(policyHandler).isPolicyApplied(policyContext, qosInformation, mockedPackage1);
		doReturn(false).when(policyHandler).isPolicyApplied(policyContext, qosInformation, mockedPackage2);

		policyHandler.applyPackage(policyContext, qosInformation);

		// one time call for first package
		verify(policyHandler, times(1)).isPolicyApplied(policyContext, qosInformation, mockedPackage1);
		// one time call for second package
		verify(policyHandler, times(1)).isPolicyApplied(policyContext, qosInformation, mockedPackage2);
	}
	
}
