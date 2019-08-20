package com.elitecore.aaa.diameter.service.application.drivers;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.elitecore.aaa.core.conf.AAAServerConfiguration;
import com.elitecore.aaa.core.conf.DriverConfigurationBuilder;
import com.elitecore.aaa.core.conf.DriverConfigurationProvider;
import com.elitecore.aaa.core.data.AttributeRelationBuilder;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.aaa.diameter.service.application.drivers.conf.impl.DiameterClassicCSVAcctDriverConfigurable;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacketBuilder;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class NasClassicCSVAcctDriverTest {

	private static final String ANY_ID = "ANY_ID";
	private static String pathToConfigurationFile = "conf" + File.separator + "db" + File.separator + "diameter" + File.separator + "driver" + File.separator;
	private static final String NO_ATTRIBUTE_MAPPING = "nasClassicCsvAcctDriverWithNoMappings.xml";
	
	@Mock private ServerContext serverContext;
	@Mock private AAAServerContext aaaServerContext;
	@Mock private DriverConfigurationProvider driverConfigurationProvider;
	@Mock private AAAServerConfiguration aaaServerConfiguration;

	private DiameterClassicCSVAcctDriverConfigurable diameterClassicCsvAcctDriverConfigurable;
	private NasClassicCSVAcctDriver nasClassicCsvAcctDriver;
	private DiameterRequest diameterRequest = new DiameterRequest();
	private ApplicationRequest applicationRequest;

	private TimeSource timeSource;

	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();
	@Rule public ExpectedException expectedException = ExpectedException.none();

	@BeforeClass
	public static void setupBeforeClass() {
		DummyDiameterDictionary.getInstance();

		/*
		 * Loading Radius dictionary in NasClassicCsvAcctDriverTest because 
		 * in the method getConfiguredCSVHeaderLine() of class NasClassicCSVAcctDriver, 
		 * in case when configured header is empty or null in attribute mapping,
		 * radius dictionary is being used to fetch the name of the attribute 
		 * to be used as header. When changing this, update the corresponding test 
		 * case as well. Affected Test Case is,
		 * 
		 * */
		// FIXME move it to specific test case
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		
		timeSource = new FixedTimeSource(System.currentTimeMillis());
		
		diameterRequest = DiameterPacketBuilder.localRequestBuilder().
				addAVP(DiameterAVPConstants.USER_NAME, "dummyUserName").
				addAVP(DiameterAVPConstants.NAS_PORT, "53").
				addAVP(DiameterAVPConstants.CALLING_STATION_ID, "dummyCallingStationId").build();

		applicationRequest = new ApplicationRequest(diameterRequest);

		nasClassicCsvAcctDriver = driverWith(defaultConfiguration());
	}

	public class AttributeRelationMapping {

		@Test
		public void driverInitializationFailsIfNoMappingIsConfigured() throws Exception {
			expectedException.expect(DriverInitializationFailedException.class);

			driverWith(baseConfiguration());
		}

		public class IsNotEmpty {

			public class AttributesConfiguredInMappingArePresentInRequest {

				@Test
				public void attributeValuesAreDumpedSeparatedByDelimiter() throws Exception {
					nasClassicCsvAcctDriver = driverWith(
							baseConfiguration()
							.withMapping(AttributeRelationBuilder.attributeRelation("0:1"))
							.withMapping(AttributeRelationBuilder.attributeRelation("0:31"))
							);

					String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);

					assertThat("dummyUserName,dummyCallingStationId," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				@Test
				public void escapesDelimiterIfPresentInAttributeValue() throws Exception {
					IDiameterAVP userName = applicationRequest.getAVP(DiameterAVPConstants.USER_NAME);
					userName.setStringValue("dummy,User,Name");
					
					String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);
					
					assertThat("dummy\\,User\\,Name,dummyCallingStationId," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				public class MappingWithMultipleAvps {
					
					@Test
					public void firstAvpFoundInOrderOfConfigurationIsDumped() throws Exception {
						nasClassicCsvAcctDriver = driverWith(
								baseConfiguration()
								.withMapping(AttributeRelationBuilder.attributeRelation("0:5,0:1")
										.header("Identity"))
								);

						String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);

						assertThat("53," + getTimeStamp(), equalTo(configuredCsvLine));
					}
					
					@Test
					public void multipleAvpsCanBeConfiguredSeparatedByComma() throws Exception {
						nasClassicCsvAcctDriver = driverWith(
								baseConfiguration()
								.withMapping(AttributeRelationBuilder.attributeRelation("0:5,0:1"))
								);

						String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);

						assertThat("53," + getTimeStamp(), equalTo(configuredCsvLine));
					}

					@Test
					public void multipleAvpsCanBeConfiguredSeparatedBySemicolon() throws Exception {
						nasClassicCsvAcctDriver = driverWith(baseConfiguration()
								.withMapping(AttributeRelationBuilder.attributeRelation("0:5;0:1"))
								);

						String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);

						assertThat("53," + getTimeStamp(), equalTo(configuredCsvLine));
					}
				}

				public class EnclosingCharacterIsConfigured {

					@Test
					public void allValuesExceptTimeStampAreEnclosed() throws Exception {
						nasClassicCsvAcctDriver = driverWith(
								defaultConfiguration()
								.enclosingCharacter("'")
								);

						String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);

						assertThat("'dummyUserName','dummyCallingStationId'," + getTimeStamp(), equalTo(configuredCsvLine));
					}

					@Test
					public void escapesEnclosingCharacterIfPresentInAttributeValue() throws Exception {
						nasClassicCsvAcctDriver = driverWith(
								defaultConfiguration()
								.enclosingCharacter("'")
								);

						IDiameterAVP userName = applicationRequest.getAVP(DiameterAVPConstants.USER_NAME);
						userName.setStringValue("'dummyUserName'");
						
						String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);
						
						assertThat("'\\'dummyUserName\\'','dummyCallingStationId'," + getTimeStamp(), equalTo(configuredCsvLine));
					}

					@Test 
					public void valuesOfAttributesWithMultipleOccurencesAreEnclosed() throws Exception {
						nasClassicCsvAcctDriver = driverWith(
								baseConfiguration()
								.withMapping(AttributeRelationBuilder.attributeRelation("0:25"))
								.enclosingCharacter("'")
								);

						List<String> classValues = new ArrayList<String>();
						classValues.add("class1");
						classValues.add("class2");
						classValues.add("class3");
						applicationRequest.getDiameterRequest().addAvp(DiameterAVPConstants.CLASS, classValues);

						String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);
						assertThat("'class1;class2;class3'," + getTimeStamp(), equalTo(configuredCsvLine));
					}
				}

				@Test
				public void givenEnclosingCharacterIsNotConfiguredValuesOfAttributesWithMultipleOccurencesAreEnclosedWithinDoubleQuotes() throws Exception {
					nasClassicCsvAcctDriver = driverWith(
							baseConfiguration()
							.withMapping(AttributeRelationBuilder.attributeRelation("0:25")
									.header("Class"))
							);

					List<String> classValues = new ArrayList<String>();
					classValues.add("class1");
					classValues.add("class2");
					classValues.add("class3");
					applicationRequest.getDiameterRequest().addAvp(DiameterAVPConstants.CLASS, classValues);

					String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);
					
					assertThat("\"class1;class2;class3\"," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				@Test
				public void cdrCanBePrefixedWithATimestamp() throws Exception {
					nasClassicCsvAcctDriver = driverWith(
							defaultConfiguration()
							.prefixedCdrTimeStamp()
							);
					
					String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);
					assertThat(getTimeStamp() +",dummyUserName,dummyCallingStationId", equalTo(configuredCsvLine));
				}
				
				@Test
				public void cdrCanBeSuffixedWithATimestamp() throws Exception {
					nasClassicCsvAcctDriver = driverWith(
							defaultConfiguration()
							.suffixedCdrTimeStamp()
							);
					
					String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);
					assertThat("dummyUserName,dummyCallingStationId," + getTimeStamp(), equalTo(configuredCsvLine));
				}
			}

			public class AttributesConfiguredInMappingAreNotPresentInRequest {

				@Test
				public void emptyStringIsDumpedIfNoDefaultValueIsConfigured() throws Exception {
					IDiameterAVP callingStaionIdAvp = applicationRequest.getAVP(DiameterAVPConstants.CALLING_STATION_ID);
					applicationRequest.getDiameterRequest().removeAVP(callingStaionIdAvp);

					String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);

					assertThat("dummyUserName,," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				@Test
				public void defaultValueIsDumpedWhenConfigured() throws Exception {
					nasClassicCsvAcctDriver = driverWith(baseConfiguration()
							.withMapping(AttributeRelationBuilder.attributeRelation("0:1")
									.defaultValue("defaultUserName"))
							.withMapping(AttributeRelationBuilder.attributeRelation("0:31")
									.defaultValue("defaultCallingStationId"))
							);

					removeAvp(DiameterAVPConstants.CALLING_STATION_ID, applicationRequest);

					String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);

					assertThat("dummyUserName,defaultCallingStationId," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				@Test
				// FIXME notify QA
				public void doesNotEscapeDelimiterIfPresentInDefaultValue() throws Exception {
					nasClassicCsvAcctDriver = driverWith(baseConfiguration()
							.withMapping(AttributeRelationBuilder.attributeRelation("0:1")
									.defaultValue("Default,User,Name"))
							);
					
					removeAvp(DiameterAVPConstants.USER_NAME, applicationRequest);
					
					String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);

					assertThat("Default,User,Name," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				@Test
				public void escapesEnclosingCharacterIfPresentInDefaultValue() throws Exception {
					nasClassicCsvAcctDriver = driverWith(baseConfiguration()
							.withMapping(AttributeRelationBuilder.attributeRelation("0:1")
									.defaultValue("'DefaultUserName'"))
							.withMapping(AttributeRelationBuilder.attributeRelation("0:31")
									.defaultValue("DefaultCallingStationId"))
							.enclosingCharacter("'"));

					removeAvp(DiameterAVPConstants.USER_NAME, applicationRequest);
					removeAvp(DiameterAVPConstants.CALLING_STATION_ID, applicationRequest);
					
					String configuredCsvLine = nasClassicCsvAcctDriver.getConfiguredCSVLine(applicationRequest);

					assertThat("'\\'DefaultUserName\\'','DefaultCallingStationId'," + getTimeStamp(), equalTo(configuredCsvLine));
				}
			}
		}
	}


	@Test
	public void makesHeaderBasedOnTheValueConfiguredInHeaderFieldOfAttributeMapping() throws DriverInitializationFailedException {
		String configuredCSVHeaderLine = nasClassicCsvAcctDriver.getConfiguredCSVHeaderLine();

		assertThat("UserName,Calling-Station-ID,CDRTimeStamp", equalTo(configuredCSVHeaderLine));
	}

	@Test
	public void usesRadiusDictionaryAvpNameIfHeaderIsNotConfiguredInAttributeMapping() throws Exception {
		nasClassicCsvAcctDriver = driverWith(baseConfiguration()
				.withMapping(AttributeRelationBuilder.attributeRelation("0:1"))
				.withMapping(AttributeRelationBuilder.attributeRelation("0:31"))
				.withMapping(AttributeRelationBuilder.attributeRelation("21067:117:1"))
			);

		String configuredCSVHeaderLine = nasClassicCsvAcctDriver.getConfiguredCSVHeaderLine();

		assertThat("User-Name,Calling-Station-Id,21067:Param1,CDRTimeStamp", equalTo(configuredCSVHeaderLine));
	}

	@Test
	public void emptyStringIsDumpedInHeaderIfConfiguredAvpIsInvalid() throws Exception {
		nasClassicCsvAcctDriver = driverWith(baseConfiguration()
				.withMapping(AttributeRelationBuilder.attributeRelation("0:31")
						.header("Calling-Station-ID"))
				.withMapping(AttributeRelationBuilder.attributeRelation("0:"))
			);
		
		String configuredCSVHeaderLine = nasClassicCsvAcctDriver.getConfiguredCSVHeaderLine();

		assertThat("Calling-Station-ID,,CDRTimeStamp", equalTo(configuredCSVHeaderLine));
	}

	public class CdrTimeStampHeaderIsConfigured {

		@Test 
		public void configuredHeaderIsUsedAsHeaderOfTimeStamp() throws Exception {
			nasClassicCsvAcctDriver = driverWith(defaultConfiguration()
					.withCdrTimeStampHeader("Time-Stamp")
					);

			String configuredCSVHeaderLine = nasClassicCsvAcctDriver.getConfiguredCSVHeaderLine();

			assertThat("UserName,Calling-Station-ID,Time-Stamp", equalTo(configuredCSVHeaderLine));
		}

		@Test
		public void cdrHeaderIsPlacedInRespectToCdrTimeStampPosition() throws Exception {
			nasClassicCsvAcctDriver = driverWith(defaultConfiguration()
					.prefixedCdrTimeStamp()
				);

			String configuredCSVHeaderLine = nasClassicCsvAcctDriver.getConfiguredCSVHeaderLine();

			assertThat("CDRTimeStamp,UserName,Calling-Station-ID", equalTo(configuredCSVHeaderLine));
			
			nasClassicCsvAcctDriver = driverWith(defaultConfiguration()
					.suffixedCdrTimeStamp()
				);
			
			configuredCSVHeaderLine = nasClassicCsvAcctDriver.getConfiguredCSVHeaderLine();
			
			assertThat("UserName,Calling-Station-ID,CDRTimeStamp", equalTo(configuredCSVHeaderLine));
		}
	}

	private void removeAvp(String avpId, ApplicationRequest applicationRequest) {
		IDiameterAVP avp = applicationRequest.getAVP(avpId);
		applicationRequest.getDiameterRequest().removeAVP(avp);
	}

	private String getTimeStamp() {
		Date date = new Date(timeSource.currentTimeInMillis());
		SimpleDateFormat format =  new SimpleDateFormat(nasClassicCsvAcctDriver.getCDRTimeStampFormat());
		String timeStamp = format.format(date);
		return timeStamp;
	}

	public NasClassicCSVAcctDriver driverWith(DriverConfigurationBuilder configuration) throws DriverInitializationFailedException {
		diameterClassicCsvAcctDriverConfigurable.postReadProcessing();

		Mockito.when((aaaServerContext).getServerConfiguration()).thenReturn(aaaServerConfiguration);
		Mockito.when(aaaServerConfiguration.getDiameterDriverConfiguration()).thenReturn(driverConfigurationProvider);
		Mockito.when(driverConfigurationProvider.getDriverConfiguration(Mockito.anyString())).thenReturn(configuration.getDriverConfiguration());
		Mockito.when(aaaServerContext.getTaskScheduler()).thenReturn(new FakeTaskScheduler());
		NasClassicCSVAcctDriver nasClassicCSVAcctDriver = new NasClassicCSVAcctDriver(aaaServerContext, ANY_ID, timeSource);
		nasClassicCSVAcctDriver.init();
		return nasClassicCSVAcctDriver;
	}

	public DriverConfigurationBuilder baseConfiguration() throws Exception {
		return readConfiguration(pathToConfigurationFile + NO_ATTRIBUTE_MAPPING);
	}

	public DriverConfigurationBuilder defaultConfiguration() throws Exception {
		return baseConfiguration()
				.withMapping(AttributeRelationBuilder.attributeRelation("0:1").header("UserName"))
				.withMapping(AttributeRelationBuilder.attributeRelation("0:31").header("Calling-Station-ID"));
	}

	private DriverConfigurationBuilder readConfiguration(String configFileName) throws Exception {
		ClasspathResource resource = ClasspathResource.at(configFileName);
		diameterClassicCsvAcctDriverConfigurable = (DiameterClassicCSVAcctDriverConfigurable)ConfigUtil.deserialize(
				new File(resource.getAbsolutePath()), DiameterClassicCSVAcctDriverConfigurable.class);
		diameterClassicCsvAcctDriverConfigurable.postReadProcessing();
		return new DriverConfigurationBuilder(diameterClassicCsvAcctDriverConfigurable.getDriverConfigurationList().get(0));
	}
}

