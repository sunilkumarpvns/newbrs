package com.elitecore.netvertex.service.offlinernc.rncpackage;



import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.*;
import static org.mockito.Mockito.doAnswer;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InOrder;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.corenetvertex.pd.pbss.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.pbss.rncpackage.RncPackageData;
import com.elitecore.netvertex.core.conf.impl.DummyNetvertexServerConfiguration;
import com.elitecore.netvertex.core.conf.impl.SystemParameterConfiguration;
import com.elitecore.netvertex.service.offlinernc.DummyOfflineRnCServiceContext;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorCodes;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCErrorMessages;
import com.elitecore.netvertex.service.offlinernc.core.OfflineRnCException;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;
import com.elitecore.netvertex.service.offlinernc.ratecard.RateCardFactory;
import com.elitecore.netvertex.service.offlinernc.ratecard.RnCResponseAssertion;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroup;
import com.elitecore.netvertex.service.offlinernc.rcgroup.RateCardGroupFactory;


public class RnCPackageTest {

	private static final String TIME_FORMAT = "dd-MM-yyyy HH:mm:ss";
	private static final String PACKAGE_NAME = "TestRnCPackage";
	private static final String CURRENCY = "INR";
	
	private RnCRequest request;
	private RnCResponse response;
	private RnCPackage rncPackage;

	private DummyOfflineRnCServiceContext serviceContext = new DummyOfflineRnCServiceContext();
	private RateCardGroupFactory mockRateCardGroupFactory;
	private RnCPackageFactory rncPackageFactory;
	private Map<String, RateCardGroup> nameToMockRcg = new HashMap<>();
	
	private RateCardGroup mockRCG1WithOrder2;
	private RateCardGroup mockRCG2WithOrder1;
	private RateCardGroup mockRCG3WithOrder3;
	private DummyNetvertexServerConfiguration serverConfiguration;

	@Before
	public void setUp() throws InitializationFailedException {
		serverConfiguration = new DummyNetvertexServerConfiguration();
		SystemParameterConfiguration spySystemParams = serverConfiguration.spySystemParameterConf();
		Mockito.when(spySystemParams.getEdrDateTimeStampFormat()).thenReturn(TIME_FORMAT);
		
		RateCardGroupData mockRCG1Data = createMockRCGData("mockRCG1", 2);
		RateCardGroupData mockRCG2Data = createMockRCGData("mockRCG2", 1);
		RateCardGroupData mockRCG3Data = createMockRCGData("mockRCG3", 3);

		mockRCG1WithOrder2 = createMockRCG();
		nameToMockRcg.put(mockRCG1Data.getName(), mockRCG1WithOrder2);
		mockRCG2WithOrder1 = createMockRCG();
		nameToMockRcg.put(mockRCG2Data.getName(), mockRCG2WithOrder1);
		mockRCG3WithOrder3 = createMockRCG();
		nameToMockRcg.put(mockRCG3Data.getName(), mockRCG3WithOrder3);
		
		mockRateCardGroupFactory = spy(new RateCardGroupFactory(new RateCardFactory(serviceContext, spySystemParams), spySystemParams));

		doAnswer(new Answer<RateCardGroup>() {
			@Override
			public RateCardGroup answer(InvocationOnMock invocation) throws Throwable {
				return nameToMockRcg.get(invocation.getArgumentAt(0, RateCardGroupData.class).getName());
			}
		}).when(mockRateCardGroupFactory).create(Mockito.any(), Mockito.any(), Mockito.any());

		RncPackageData rncPackageData = new RncPackageData();
		rncPackageData.setName(PACKAGE_NAME);
		rncPackageData.setRateCardGroupData(Arrays.asList(mockRCG1Data, mockRCG2Data, mockRCG3Data));

		rncPackageFactory = new RnCPackageFactory(mockRateCardGroupFactory);
		rncPackage = rncPackageFactory.create(rncPackageData,CURRENCY);

		request = new RnCRequest();
		response = RnCResponse.of(request);
	}

	private RateCardGroupData createMockRCGData(String name, int orderNo) {
		RateCardGroupData mockRCG1Data = new RateCardGroupData();
		mockRCG1Data.setName(name);
		mockRCG1Data.setOrderNo(orderNo);
		return mockRCG1Data;
	}

	private RateCardGroup createMockRCG() {

		return spy(new RateCardGroup("", null, null, null, null, null, null, null, null, null, null, null, null, null, null));

	}
	
	@Test
	public void failsWithRateCardGroupNotFoundErrorCodeWhenNoRCGIsApplied() throws OfflineRnCException {

		try {
			doReturn(false).when(mockRCG2WithOrder1).apply(request, response);
			doReturn(false).when(mockRCG1WithOrder2).apply(request, response);
			doReturn(false).when(mockRCG3WithOrder3).apply(request, response);

			rncPackage.apply(request, response);

			fail("Exception should be thrown but didn't come");

		} catch(OfflineRnCException ex) {
			assertThat(ex.getCode(), is(equalTo(OfflineRnCErrorCodes.RATE_CARD_GROUP_NOT_FOUND)));
			assertThat(ex.getMessage(), is(equalTo(OfflineRnCErrorMessages.RATE_CARD_GROUP_NOT_FOUND.message())));
		}
	}

	@Test
	public void appliesFirstApplicableRCG() throws OfflineRnCException {
		doReturn(false).when(mockRCG2WithOrder1).apply(request, response);
		doReturn(true).when(mockRCG1WithOrder2).apply(request, response);
		doReturn(true).when(mockRCG3WithOrder3).apply(request, response);

		rncPackage.apply(request, response);

		verify(mockRCG2WithOrder1).apply(request, response);
		verify(mockRCG1WithOrder2).apply(request, response);
		verify(mockRCG3WithOrder3, times(0)).apply(request, response);
	}

	@Test
	public void enrichesRnCPackageNameInResponseWhenRCGIsSuccessfullyApplied() throws OfflineRnCException {
		doReturn(true).when(mockRCG2WithOrder1).apply(request, response);

		request.setAttribute(OfflineRnCKeyConstants.RNC_PACKAGE.getName(), PACKAGE_NAME);

		rncPackage.apply(request, response);

		RnCResponseAssertion.assertThat(response).containsAttributeWithValue(OfflineRnCKeyConstants.RNC_PACKAGE, PACKAGE_NAME);

	}
	
	@Test
	public void applisRCGInManagedOrder() throws OfflineRnCException {
		doReturn(false).when(mockRCG2WithOrder1).apply(request, response);
		doReturn(false).when(mockRCG1WithOrder2).apply(request, response);
		doReturn(true).when(mockRCG3WithOrder3).apply(request, response);
		
		rncPackage.apply(request, response);
		
		InOrder inOrder = Mockito.inOrder(mockRCG1WithOrder2, mockRCG2WithOrder1, mockRCG3WithOrder3);
		
		inOrder.verify(mockRCG2WithOrder1, times(1)).apply(request, response);
		inOrder.verify(mockRCG1WithOrder2, times(1)).apply(request, response);
		inOrder.verify(mockRCG3WithOrder3, times(1)).apply(request, response);
	}
}