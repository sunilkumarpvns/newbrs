package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.pkg.datapackage.PromotionalPackage;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.util.PCRFResponseBuilder;
import com.elitecore.netvertex.pm.PolicyContext;
import com.elitecore.netvertex.pm.QoSInformation;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.*;

public class PromotionalPolicyHandlerTest {

	private static final String GROUP1 = "group1";
	public static final String GROUP2 = "group2";
	public static final String GROUP3 = "group3";
	public static final String PKG_ID1 = "PKG_ID1";
	public static final String PKG_ID_2 = "PKG_ID2";
	private QoSInformation qosInformation;
	private PCRFResponse pcrfResponse;
	private List<String> groupIds;

	@Mock
	private SPRInfo sprInfo;
	@Mock
	private PolicyContext policyContext;
	@Mock
	private PCRFServiceContext pcrfServiceContext;
	@Mock
	private PolicyRepository policyRepository;
	@Mock
	private NetVertexServerContext netVertexServerContext;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		this.qosInformation = new QoSInformation();
		this.pcrfResponse = new PCRFResponseBuilder().build();
		this.groupIds = Arrays.asList(GROUP1, GROUP2, "group3");

		doReturn(pcrfResponse).when(policyContext).getPCRFResponse();
		doReturn(sprInfo).when(policyContext).getSPInfo();
		doReturn(groupIds).when(sprInfo).getSPRGroupIds();
		doReturn(netVertexServerContext).when(pcrfServiceContext).getServerContext();
		doReturn(policyRepository).when(netVertexServerContext).getPolicyRepository();
	}

	@Test
	public void test_applyPackage_should_not_call_apply_when_no_policy_configured() {

		doReturn(policyRepository).when(netVertexServerContext).getPolicyRepository();

		when(policyRepository.getPromotionalPackages()).thenReturn(Collectionz.<PromotionalPackage>newArrayList());

		PromotionalPolicyHandler policyHandler = new PromotionalPolicyHandler(netVertexServerContext);

		policyHandler = spy(policyHandler);

		policyHandler.applyPackage(policyContext);

		verify(policyHandler, times(0))
				.apply(Mockito.same(policyContext), Mockito.anyString(), Mockito.<PromotionalPackage> any());
	}

	@Test
	public void test_applyPackage_should_not_call_apply_when_no_spr_groups_found() {

		PromotionalPackage mockedPackage1 = getMockedPackage(PKG_ID1);
		PromotionalPackage mockedPackage2 = getMockedPackage(PKG_ID_2);

		List<PromotionalPackage> packages = Arrays.asList(mockedPackage1, mockedPackage2);

		doReturn(packages).when(policyRepository).getPromotionalPackages();

		doReturn(null).when(sprInfo).getSPRGroupIds();
		PromotionalPolicyHandler policyHandler = new PromotionalPolicyHandler(netVertexServerContext);

		policyHandler = spy(policyHandler);

		policyHandler.applyPackage(policyContext);

		verify(policyHandler, times(0))
				.apply(Mockito.same(policyContext), Mockito.anyString(), Mockito.<PromotionalPackage> any());
	}

	@Test
	public void test_applyPackage_should_apply_all_policy() throws Exception {

		PromotionalPackage mockedPackage1 = getMockedPackage(PKG_ID1);
		PromotionalPackage mockedPackage2 = getMockedPackage(PKG_ID_2);

		ArrayList<PromotionalPackage> packages = new ArrayList<PromotionalPackage>(Arrays.asList(mockedPackage1, mockedPackage2));

		doReturn(packages).when(policyRepository).getPromotionalPackages();
		doReturn(packages).when(policyRepository).getPromotionalPackagesOfGroup(GROUP1);

		PromotionalPolicyHandler policyHandler = new PromotionalPolicyHandler(netVertexServerContext);

		policyHandler = spy(policyHandler);

		doNothing().when(policyHandler).apply(eq(policyContext), anyString(), eq(mockedPackage1));
		doNothing().when(policyHandler).apply(eq(policyContext), anyString(), eq(mockedPackage2));

		policyHandler.applyPackage(policyContext);

		// one time call for first package
		verify(policyHandler, times(1)).apply(eq(policyContext), anyString(), eq(mockedPackage1));
		// one time call for second package
		verify(policyHandler, times(1)).apply(eq(policyContext), anyString(), eq(mockedPackage2));
	}

	@Test
	public void test_applyPackage_should_process_all_groups_even_if_one_group_not_belogs_to_any_package() throws Exception {

		PromotionalPackage mockedPackage1 = getMockedPackage(PKG_ID1);
		PromotionalPackage mockedPackage2 = getMockedPackage(PKG_ID_2);

		ArrayList<PromotionalPackage> packagesForGroup1 = new ArrayList<PromotionalPackage>(Arrays.asList(mockedPackage1));
		ArrayList<PromotionalPackage> packagesForGroup2 = new ArrayList<PromotionalPackage>(Arrays.asList(mockedPackage2));

		doReturn(packagesForGroup1).when(policyRepository).getPromotionalPackages();
		doReturn(packagesForGroup1).when(policyRepository).getPromotionalPackagesOfGroup(GROUP1);
		doReturn(null).when(policyRepository).getPromotionalPackagesOfGroup(GROUP2);
		doReturn(packagesForGroup2).when(policyRepository).getPromotionalPackagesOfGroup(GROUP3);

		PromotionalPolicyHandler policyHandler = new PromotionalPolicyHandler(netVertexServerContext);

		policyHandler = spy(policyHandler);

		doNothing().when(policyHandler).apply(eq(policyContext), anyString(), eq(mockedPackage1));
		doNothing().when(policyHandler).apply(eq(policyContext), anyString(), eq(mockedPackage2));

		policyHandler.applyPackage(policyContext);

		// one time call for first package
		verify(policyHandler, times(1)).apply(eq(policyContext), anyString(), eq(mockedPackage1));
		// one time call for second package
		verify(policyHandler, times(1)).apply(eq(policyContext), anyString(), eq(mockedPackage2));
	}

	private PromotionalPackage getMockedPackage(String pkgId1) {
		PromotionalPackage mockedPackage = mock(PromotionalPackage.class);
		when(mockedPackage.getId()).thenReturn(pkgId1);
		return mockedPackage;
	}

	@Test
	public void test_apply_should_apply_same_package_one_time_only() throws Exception {

		PromotionalPackage mockedPackage1 = getMockedPackage(PKG_ID1);
		PromotionalPackage mockedPackage2 = getMockedPackage(PKG_ID_2);

		ArrayList<PromotionalPackage> packagesGroup1 = new ArrayList<PromotionalPackage>(Arrays.asList(mockedPackage1, mockedPackage2, mockedPackage1, mockedPackage2));
		ArrayList<PromotionalPackage> packagesGroup2 = new ArrayList<PromotionalPackage>(Arrays.asList(mockedPackage1, mockedPackage2, mockedPackage1, mockedPackage2));

		doReturn(packagesGroup1).when(policyRepository).getPromotionalPackages();
		doReturn(packagesGroup1).when(policyRepository).getPromotionalPackagesOfGroup(GROUP1);
		doReturn(packagesGroup2).when(policyRepository).getPromotionalPackagesOfGroup(GROUP2);

		PromotionalPolicyHandler policyHandler = new PromotionalPolicyHandler(netVertexServerContext);

		policyHandler = spy(policyHandler);

		doNothing().when(policyHandler).apply(eq(policyContext), anyString(), eq(mockedPackage1));
		doNothing().when(policyHandler).apply(eq(policyContext), anyString(), eq(mockedPackage2));

		policyHandler.applyPackage(policyContext);

		// one time call for first package
		verify(policyHandler, times(1)).apply(eq(policyContext), anyString(), eq(mockedPackage1));
		// one time call for second package
		verify(policyHandler, times(1)).apply(eq(policyContext), anyString(), eq(mockedPackage2));
	}

}
