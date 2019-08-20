package com.elitecore.aaa.radius.service.base;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Collection;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.conf.impl.VSAInClassConfigurable;
import com.elitecore.aaa.core.drivers.DriverFactory;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.radius.conf.RadAuthConfiguration;
import com.elitecore.aaa.radius.conf.RadClientConfiguration;
import com.elitecore.aaa.radius.data.RadClientData;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.auth.RadAuthService;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class VsaInClassAdditionTest {
	
	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();
	
	private static final String VSA_CLASS_PREFIX = "ELITECLASS";
	
	@Mock private AAAServerContext aaaServerContext;
	@Mock private AAAServerConfiguration aaaServerConfiguration;
	@Mock private RadAuthConfiguration radAuthConfiguration;
	@Mock private RadClientConfiguration radClientConfiguration;
	@Mock private RadClientData radClientData; 
	
	private RadAuthRequest request;
	private RadAuthResponse response;
	private BaseRadiusAuthService authService;
	
	private VSAInClassConfigurable vsaInClassConfigurable;
	
	
	@Before
	public void setup() throws InvalidAttributeIdException {
		
		MockitoAnnotations.initMocks(this);
		
		vsaInClassConfigurable = new VSAInClassConfigurable();
		vsaInClassConfigurable.setIsEnabled(true);
		vsaInClassConfigurable.setClassAttributeIdString(RadiusAttributeConstants.CLASS_STR);
		
		Mockito.when(aaaServerContext.getServerConfiguration()).thenReturn(aaaServerConfiguration);
		Mockito.when(aaaServerConfiguration.getAuthConfiguration()).thenReturn(radAuthConfiguration);
		Mockito.when(aaaServerConfiguration.getRadClientConfiguration()).thenReturn(radClientConfiguration);
		Mockito.when(aaaServerConfiguration.getVSAInClassConfiguration()).thenReturn(vsaInClassConfigurable);
		
		authService = new RadAuthService(aaaServerContext,
				new DriverManager(Mockito.mock(DriverConfigurationProvider.class), Mockito.mock(DriverFactory.class)),
				Mockito.mock(RadPluginManager.class), Mockito.mock(RadiusLogMonitor.class));
		
		
		request = new RadAuthRequestBuilder().addAttribute(RadiusAttributeConstants.USER_NAME_STR, "test")
							.addAttribute(RadiusAttributeConstants.USER_PASSWORD_STR, "test")
							.addAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS_STR, "1.1.1.1")
							.build();
		
		response = new RadAuthRequestBuilder()
							.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "commandCentre")
							.buildResponse();
		
		response.setClientData(radClientData);
		
	}
	
	@Test
	public void vsaInClassAttributeHasELITECLASSAsPrefix() {
		vsaInClassConfigurable.setResponsePacketString("0:31");
		vsaInClassConfigurable.doProcessing(); 
		
		authService.convertVSAToClassAttribute(request, response);
		
		assertTrue(response.getRadiusAttribute(RadiusAttributeConstants.CLASS_STR).getStringValue().startsWith(VSA_CLASS_PREFIX));
	}
	
	@Test
	public void configuredVsaAttributesArePlacedinClassAttribute() {
	
		vsaInClassConfigurable.setRequestPacketString("0:4");
		vsaInClassConfigurable.setResponsePacketString("0:31");
		vsaInClassConfigurable.doProcessing(); 
		
		
		authService.convertVSAToClassAttribute(request, response);
		
		assertEquals("ELITECLASS,0:4=1.1.1.1,0:31=commandCentre",
				response.getRadiusAttribute(RadiusAttributeConstants.CLASS_STR).getStringValue());	
	}
	
	@Test
	public void vsaAttributesWhichAreAddedInClassAttributeAreRemovedFromResponsePacket() {
		vsaInClassConfigurable.setRequestPacketString("0:4");
		vsaInClassConfigurable.setResponsePacketString("0:31");
		vsaInClassConfigurable.doProcessing(); 
		
		
		authService.convertVSAToClassAttribute(request, response);
		
		Assert.assertNull("0:4 should not be present in Response.", response.getRadiusAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS_STR));
		Assert.assertNull("0:31 should not be present in response.", response.getRadiusAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR));
	}
	
	@Test
	public void classAttributesPresentInResponseAreAutomaticallyMergedInVsaInClassRemovedEvenIfTheyAreNotConfigured() throws InvalidAttributeIdException {
		vsaInClassConfigurable.setResponsePacketString("");
		vsaInClassConfigurable.doProcessing();

		
		IRadiusAttribute classAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(RadiusAttributeConstants.CLASS_STR);
		classAttribute.setStringValue("classValue");
		response.addAttribute(classAttribute);
		
		authService.convertVSAToClassAttribute(request, response);
		
		Assert.assertNotEquals("classValue", response.getRadiusAttribute(RadiusAttributeConstants.CLASS_STR).getStringValue());
		assertEquals("ELITECLASS,0:25=classValue"
				, response.getRadiusAttribute(RadiusAttributeConstants.CLASS_STR).getStringValue());
	}
	
	public class LengthOfVsaInClassExceedsVsaInClassAttributeLength {
		
		private static final String SOME_RANDOM_STRING = "some random string with extra large length so that limit is reached quickly";
		
		@Before
		public void setup() throws InvalidAttributeIdException {
			response = new RadAuthRequestBuilder()
					.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, SOME_RANDOM_STRING)
					.addAttribute(RadiusAttributeConstants.NAS_IP_ADDRESS_STR,"1.1.1.1" )
					.addAttribute(RadiusAttributeConstants.CALLED_STATION_ID_STR, SOME_RANDOM_STRING)
					.addAttribute(RadiusAttributeConstants.NAS_IDENTIFIER_STR,SOME_RANDOM_STRING)
					.buildResponse();
			
			
			vsaInClassConfigurable.setRequestPacketString("0:4");
			vsaInClassConfigurable.setResponsePacketString("0:31, 0:4, 0:30, 0:32");
			vsaInClassConfigurable.doProcessing(); 
			
			response.setClientData(radClientData);
			
		}
		
		public class GivenMultipleClassAttributesAreAllowed {
			
			@Before
			public void setup() {
				Mockito.when(radClientData.isMultipleClassAttributeSupported()).thenReturn(true);
			}
			
			@Test
			public void allConfiguredValuesAreAccomodetedAmongMultipleVsaInClassAttribute() {
				authService.convertVSAToClassAttribute(request, response);
				
				Collection<IRadiusAttribute> classAttributes = response.getRadiusAttributes(RadiusAttributeConstants.CLASS_STR);

				assertTrue(classAttributes.size()>1);
				for (IRadiusAttribute classAttribute : classAttributes) {
					assertTrue(classAttribute.getStringValue().startsWith(VSA_CLASS_PREFIX));
				}
			}
		}
		
		public class GiveMultipleClassAttibutesAreNotAllowed {
			
			@Before
			public void setup() {
				Mockito.when(radClientData.isMultipleClassAttributeSupported()).thenReturn(false);
			}
			
			@Test
			public void onlyVsaInClassAttributeIsSentOtherClassAttributesAreRemoved() {
				authService.convertVSAToClassAttribute(request, response);
				Collection<IRadiusAttribute> classAttributes = response.getRadiusAttributes(RadiusAttributeConstants.CLASS_STR);
				assertTrue(classAttributes.size() == 1);
				assertTrue(classAttributes.iterator().next().getStringValue().startsWith(VSA_CLASS_PREFIX));
			}
		}
	}
}

