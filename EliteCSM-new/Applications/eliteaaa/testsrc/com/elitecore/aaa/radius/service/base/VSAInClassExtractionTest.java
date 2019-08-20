package com.elitecore.aaa.radius.service.base;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Iterator;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.conf.impl.VSAInClassConfigurable;
import com.elitecore.aaa.core.drivers.DriverManager;
import com.elitecore.aaa.core.plugins.conf.PluginConfigurable;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.util.cli.RadiusLogMonitor;
import com.elitecore.aaa.radius.conf.RadAcctConfiguration;
import com.elitecore.aaa.radius.drivers.RadiusDriverFactory;
import com.elitecore.aaa.radius.plugins.core.RadPluginManager;
import com.elitecore.aaa.radius.service.acct.RadAcctRequest;
import com.elitecore.aaa.radius.service.acct.RadAcctService;
import com.elitecore.aaa.radius.service.base.BaseRadiusAcctService.RadiusAcctRequestImpl;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.core.util.url.SocketDetail;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.InvalidAttributeIdException;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(HierarchicalContextRunner.class)
public class VSAInClassExtractionTest {
	
	private static final char COMMA = ',';
	private static final String ANY_STRING = "ANY";
	private static final String PREFIX = "ELITECLASS";
	
	@Mock AAAServerContext context;
	@Mock private AAAServerConfiguration aaaServerConfiguration;

	private VSAInClassConfigurable configurable = new VSAInClassConfigurable();
	private BaseRadiusAcctService acctService;
	
	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		when(context.getServerConfiguration()).thenReturn(aaaServerConfiguration);
		when(aaaServerConfiguration.getAcctConfiguration()).thenReturn(mock(RadAcctConfiguration.class));
		
		acctService = new RadAcctService(context, new DriverManager(mock(DriverConfigurationProvider.class),
					new RadiusDriverFactory(context)),
					new RadPluginManager(context, mock(PluginConfigurable.class)),
				mock(RadiusLogMonitor.class));
	}
	
	public class VSAInClassIsEnabledContext {
		
		@Before
		public void setUp() {
			configurable.setIsEnabled(true);
			configurable.setSeparator(COMMA);
			configurable.setClassAttributeIdString(RadiusAttributeConstants.CLASS_STR);
			
			when(aaaServerConfiguration.getVSAInClassConfiguration()).thenReturn(configurable);
		}
		
		public class RequestContainsConfiguredAttributeContext {
			
			@Test
			public void shouldNotAddVSA_IfAttributeIsFoundWithoutMatchingPrefix() throws UnknownHostException {
				RadAcctRequest expectedRequest = request().addClassAttribute("0:1=abc,0:31=cde").build();
				RadAcctRequest actualRequest = request().addClassAttribute("0:1=abc,0:31=cde").build();

				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldNotAddVSAAndRemoveConfiguredAttributeFromActualRequest_IfAttributeHasOnlyPrefixValue() throws UnknownHostException {
				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX)
				.build();

				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX) /// FIXME this attribute should be removed
				.build();
				
				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldAddAttributeFoundInConfiguredAttributeValue_AndRemoveTheOriginalAttributeFromRequest() throws UnknownHostException, InvalidAttributeIdException {

				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc")
				.build();
				
				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc" ) ///FIXME this attribute should be removed
				.addInfoAttribute("0:1", "abc")
				.build();
				
				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldAddAllAttributesFoundInConfiguredAttributeValue_AndRemoveTheOriginalAttributeFromRequest() throws UnknownHostException, InvalidAttributeIdException {
				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,0:31=0A-0B-0C-0D-0E")
				.build();

				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,0:31=0A-0B-0C-0D-0E") ///FIXME this attribute should be removed
				.addInfoAttribute("0:1", "abc")
				.addInfoAttribute("0:31", "0A-0B-0C-0D-0E")
				.build();
				
				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldAddMultipleOccurancesOfSameAttributeFoundInConfiguredAttributeValue_AndRemoveTheOriginalAttributeFromRequest() throws UnknownHostException, InvalidAttributeIdException {
				
				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,0:1=cde")
				.build();

				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,0:1=cde") ///FIXME this attribute should be removed
				.addInfoAttribute("0:1", "abc")
				.addInfoAttribute("0:1", "cde")
				.build();
				
				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldNotAddAnyAttribute_AndKeepTheOriginalAttributeIntact_IfValueDoesNotContainValueSeparator() throws UnknownHostException, InvalidAttributeIdException {

				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX + COMMA + ANY_STRING)
				.build();
				
				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + ANY_STRING)
				.build();

				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldSkipAnyUnknownAttribute_AndKeepTheOriginalAttributeRemovingThePrefixValue() throws UnknownHostException {

				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + "1:1=abc")
				.build();
				
				RadAcctRequest expectedRequest = request()
				.addClassAttribute("1:1=abc")
				.build();

				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldSkipAnyUnknownAttributesAndAddKnownAttributes_AndKeepTheOriginalAttributeRemovingThePrefixValueAndPreservingTheUnknownAttributes() throws UnknownHostException, InvalidAttributeIdException {

				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + "1:1=abc,0:1=abc,21067:1=cde,1:2=xyz")
				.build();
				
				RadAcctRequest expectedRequest = request()
				.addClassAttribute("1:1=abc,1:2=xyz")
				.addInfoAttribute("0:1", "abc")
				.addInfoAttribute("21067:1", "cde")
				.build();

				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			/// FIXME is this behavior appropriate? Ideally only the values that are skipped should be kept intact
			public void shouldSkipTokenWithoutValueSeparatorAndAddingOthers_KeepingTheOriginalAttributeIntact() throws UnknownHostException, InvalidAttributeIdException {

				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,21067:1=cde" + COMMA + ANY_STRING)
				.build();
				
				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,21067:1=cde" + COMMA + ANY_STRING)
				.addInfoAttribute("0:1", "abc")
				.addInfoAttribute("21067:1", "cde")
				.build();

				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldSupportEscapeSyntax_WhenAttributeValueItselfContainsAttributeSeparator() throws UnknownHostException, InvalidAttributeIdException {
				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc\\,cde,0:31=0A-0B-0C-0D,21067:1=param1")
				.build();
				
				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc\\,cde,0:31=0A-0B-0C-0D,21067:1=param1")
				.addInfoAttribute("0:1", "abc,cde")
				.addInfoAttribute("0:31", "0A-0B-0C-0D")
				.addInfoAttribute("21067:1", "param1")
				.build();
				
				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldSupportEscapeSyntax_WhenAttributeValueItselfContainsKeyValueSeparator() throws UnknownHostException, InvalidAttributeIdException {
				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,0:31=0A-0B-0C-0D,21067:1=param1\\=continueparam")
				.build();
				
				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,0:31=0A-0B-0C-0D,21067:1=param1\\=continueparam")
				.addInfoAttribute("0:1", "abc")
				.addInfoAttribute("0:31", "0A-0B-0C-0D")
				.addInfoAttribute("21067:1", "param1=continueparam")
				.build();
				
				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldSkipAnyAttributeSeparatorsWithoutValue() throws UnknownHostException, InvalidAttributeIdException {
				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,,0:31=0A-0B-0C-0D,")
				.build();
				
				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX + COMMA + "0:1=abc,,0:31=0A-0B-0C-0D,")
				.addInfoAttribute("0:1", "abc")
				.addInfoAttribute("0:31", "0A-0B-0C-0D")
				.build();
				
				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			@Test
			public void shouldSkipPrefixIfItOccursTwiceInAttributeValue() throws UnknownHostException, InvalidAttributeIdException {
				RadAcctRequest actualRequest = request()
				.addClassAttribute(PREFIX + COMMA + PREFIX + COMMA + "0:1=abc")
				.build();
				
				RadAcctRequest expectedRequest = request()
				.addClassAttribute(PREFIX + COMMA + PREFIX + COMMA + "0:1=abc") ///FIXME this should be removed
				.addInfoAttribute("0:1", "abc")
				.build();
				
				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
			
			public class MultipleOccurancesOfConfiguredAttribute {
				
				@Test
				public void shouldAddVSAFromAllAttributesContainingConfiguredPrefix() throws UnknownHostException, InvalidAttributeIdException {
					RadAcctRequest actualRequest = request()
					.addClassAttribute(PREFIX + COMMA + "0:1=abc,21067:1=cde")
					.addClassAttribute(PREFIX + COMMA + "0:31=0A-0B-0C-0D,21067:1=param1")
					.build();
					
					RadAcctRequest expectedRequest = request()
					.addClassAttribute(PREFIX + COMMA + "0:1=abc,21067:1=cde") /// FIXME this attribute should be removed
					.addClassAttribute(PREFIX + COMMA + "0:31=0A-0B-0C-0D,21067:1=param1") /// FIXME this attribute should be removed
					.addInfoAttribute("0:1", "abc")
					.addInfoAttribute("21067:1", "cde")
					.addInfoAttribute("0:31", "0A-0B-0C-0D")
					.addInfoAttribute("21067:1", "param1")
					.build();

					acctService.convertClassAttributeToVSA(actualRequest);
					
					assertPacketEquals(expectedRequest, actualRequest);
				}
				
				@Test
				public void shouldAddVSAFromAllAttributesContainingPrefixSkippingTheOnesThatDontContainPrefix_AndKeepingThemIntactInRequest() throws UnknownHostException, InvalidAttributeIdException {
					RadAcctRequest actualRequest = request()
					.addClassAttribute("0:1=abc,21067:1=cde")
					.addClassAttribute(PREFIX + COMMA + "0:31=0A-0B-0C-0D,21067:1=param1")
					.build();
					
					RadAcctRequest expectedRequest = request()
					.addClassAttribute("0:1=abc,21067:1=cde")
					.addClassAttribute(PREFIX + COMMA + "0:31=0A-0B-0C-0D,21067:1=param1") /// FIXME this attribute should be removed
					.addInfoAttribute("0:31", "0A-0B-0C-0D")
					.addInfoAttribute("21067:1", "param1")
					.build();

					acctService.convertClassAttributeToVSA(actualRequest);
					
					assertPacketEquals(expectedRequest, actualRequest);
				}
			}
			
			/**
			 * This VSA in class feature works with MUI concept. User can configure multiple attributes
			 * from which the VSA can be added in request.
			 * 
			 * When extracting if request does not contain first attribute then it will continue to the
			 * other attribute till the first configured attribute is found in request and skip all other
			 * attributes.
			 *  
			 */
			public class MultipleAttributesConfiguredInConfigurationContext {
				private static final String FIRST_CONFIGURED_ATTRIBUTE = "21067:117:1";
				private static final String OTHER_CONFIGURED_ATTRIBUTE = "21067:2";
				
				@Before
				public void setUp() {
					configurable.setClassAttributeIdString(FIRST_CONFIGURED_ATTRIBUTE + COMMA + RadiusAttributeConstants.CLASS_STR + COMMA + OTHER_CONFIGURED_ATTRIBUTE);
					configurable.doProcessing();
				}
				
				@Test
				public void shouldConsiderFirstFoundConfiguredAttributeInRequest_AndExtractVSAFromIt_SkippingFurtherConfiguredAttributesEvenWhenPresentInRequest() throws UnknownHostException, InvalidAttributeIdException {
					RadAcctRequest actualRequest = request()
					.addClassAttribute(PREFIX + COMMA + "0:1=abc,21067:1=cde")
					.addAttribute(OTHER_CONFIGURED_ATTRIBUTE, PREFIX + COMMA + "0:31=abc,0:89=cde")
					.build();
					
					RadAcctRequest expectedRequest = request()
					.addClassAttribute(PREFIX + COMMA + "0:1=abc,21067:1=cde") /// FIXME this attribute should be removed
					.addAttribute(OTHER_CONFIGURED_ATTRIBUTE, PREFIX + COMMA + "0:31=abc,0:89=cde")
					.addInfoAttribute("0:1", "abc")
					.addInfoAttribute("21067:1", "cde")
					.build();

					acctService.convertClassAttributeToVSA(actualRequest);
					
					assertPacketEquals(expectedRequest, actualRequest);
				}
				
				@Test
				public void shouldNotConsiderOtherConfiguredAttributes_IfFirstConfiguredAttributeIsFound_AndDoesNotMatchExpectedFormat() throws UnknownHostException, InvalidAttributeIdException {
					RadAcctRequest actualRequest = request()
					.addClassAttribute("0:1=abc,21067:1=cde")
					.addAttribute(OTHER_CONFIGURED_ATTRIBUTE, PREFIX + COMMA + "0:31=abc,0:89=cde")
					.build();
					
					RadAcctRequest expectedRequest = request()
					.addClassAttribute("0:1=abc,21067:1=cde")
					.addAttribute(OTHER_CONFIGURED_ATTRIBUTE, PREFIX + COMMA + "0:31=abc,0:89=cde")
					.build();

					acctService.convertClassAttributeToVSA(actualRequest);
					
					assertPacketEquals(expectedRequest, actualRequest);
				}
			}
		}
		
		public class RequestDoesNotContainClassAttributeContext {
			
			@Test
			public void shouldNotAddAnyClassAttribute_IfVSAInClassIsEnabled_AndConfigureAttributesNotFound() throws UnknownHostException {
				RadAcctRequest expectedRequest = request().build();
				RadAcctRequest actualRequest = request().build();

				acctService.convertClassAttributeToVSA(actualRequest);
				
				assertPacketEquals(expectedRequest, actualRequest);
			}
		}
	}
	
	@Test
	public void testConvertClassAttributeToVSA_ShouldNotAddAnyClassAttribute_IfVSAInClassConfigurationIsNotFound() throws UnknownHostException {
		when(aaaServerConfiguration.getVSAInClassConfiguration()).thenReturn(null);
		
		RadAcctRequest expectedRequest = request().addClassAttribute(PREFIX + COMMA + "0:1=abc").build();
		RadAcctRequest actualRequest = request().addClassAttribute(PREFIX + COMMA + "0:1=abc").build();
		
		acctService.convertClassAttributeToVSA(actualRequest);
		
		assertPacketEquals(expectedRequest, actualRequest);
	}
	
	@Test
	public void testConvertClassAttributeToVSA_ShouldNotAddAnyClassAttribute_IfVSAInClassIsDisabled() throws UnknownHostException {
		VSAInClassConfigurable configurable = new VSAInClassConfigurable();
		configurable.setIsEnabled(false);
		when(aaaServerConfiguration.getVSAInClassConfiguration()).thenReturn(configurable);
		
		RadAcctRequest expectedRequest = request().addClassAttribute(PREFIX + COMMA + "0:1=abc").build();
		RadAcctRequest actualRequest = request().addClassAttribute(PREFIX + COMMA + "0:1=abc").build();

		acctService.convertClassAttributeToVSA(actualRequest);
		
		assertPacketEquals(expectedRequest, actualRequest);
	}
	
	private RadAcctRequestBuilder request() {
		return new RadAcctRequestBuilder();
	}

	private void assertPacketEquals(RadAcctRequest expectedPacket, RadAcctRequest actualPacket) {
		try {
			assertEquals("Main attributes size mismatch", expectedPacket.getRadiusAttributes().size(), actualPacket.getRadiusAttributes().size());

			Iterator<IRadiusAttribute> expectedIterator = expectedPacket.getRadiusAttributes().iterator();

			for (IRadiusAttribute attribute : actualPacket.getRadiusAttributes()) {
				assertEquals(expectedIterator.next(), attribute);
			}

			assertEquals("Info attributes size mismatch", expectedPacket.getInfoAttributes().size(), actualPacket.getInfoAttributes().size());
			expectedIterator = expectedPacket.getInfoAttributes().iterator();

			for (IRadiusAttribute attribute : actualPacket.getInfoAttributes()) {
				assertEquals(expectedIterator.next(), attribute);
			}
		} catch (AssertionError error) {
			System.out.println("Expected packet" + expectedPacket);
			System.out.println("Actual packet" + actualPacket);
			throw error;
		}
	}
	
	class RadAcctRequestBuilder {
		RadiusPacket packet = new RadiusPacket();
		List<IRadiusAttribute> infoAttributes = Collectionz.newArrayList();
		
		public RadAcctRequestBuilder addClassAttribute(String value) {
			IRadiusAttribute classAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(RadiusAttributeConstants.CLASS);
			classAttribute.setStringValue(value);
			packet.addAttribute(classAttribute);
			packet.refreshPacketHeader();
			return this;
		}
		
		public RadAcctRequestBuilder addInfoAttribute(String attributeId,
				String value) throws InvalidAttributeIdException {
			IRadiusAttribute infoAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(attributeId);
			infoAttribute.setStringValue(value);
			infoAttributes.add(infoAttribute);
			return this;
		}

		public RadAcctRequestBuilder addAttribute(String attributeId, String value) throws InvalidAttributeIdException {
			IRadiusAttribute classAttribute = RadiusDictionaryTestHarness.getInstance().getAttribute(attributeId);
			classAttribute.setStringValue(value);
			packet.addAttribute(classAttribute);
			packet.refreshPacketHeader();
			return this;
		}
		
		public RadAcctRequest build() throws UnknownHostException {
			RadiusAcctRequestImpl request = new RadiusAcctRequestImpl(packet.getBytes(), InetAddress.getLocalHost(), 0, new SocketDetail("0.0.0.0", 0));
			for (IRadiusAttribute infoAttribute : infoAttributes) {
				request.addInfoAttribute(infoAttribute);
			}
			return request;
		}
	}
}
