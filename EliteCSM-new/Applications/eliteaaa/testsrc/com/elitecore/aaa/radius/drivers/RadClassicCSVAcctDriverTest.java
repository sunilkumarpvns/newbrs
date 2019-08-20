package com.elitecore.aaa.radius.drivers;

import static com.elitecore.aaa.core.data.AttributeRelationBuilder.attributeRelation;
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
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.conf.impl.RadClassicCSVDriverConfigurable;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.base.RadAuthRequestBuilder;
import com.elitecore.commons.base.ClasspathResource;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.config.ConfigUtil;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

@RunWith(HierarchicalContextRunner.class)
public class RadClassicCSVAcctDriverTest {

	private final String ANY_ID = "ANY_ID";
	private final String NO_ATTRIBUTE_MAPPING = "radClassicCsvAcctDriverWithNoMappings.xml";
	private String pathToConfigurationFile = "conf" + File.separator + "db" + File.separator + "services"
			+ File.separator + "acct" + File.separator + "driver" + File.separator;

	private RadAuthRequest request;
	private RadClassicCSVAcctDriver radClassicCSVAcctDriver;
	private RadClassicCSVDriverConfigurable radClassicCsvDriverConfigurable;
	private TimeSource timeSource;

	@Mock
	private ServerContext serverContext;
	@Mock
	private AAAServerContext aaaServerContext;
	@Mock
	private DriverConfigurationProvider driverConfigurationProvider;
	@Mock
	private AAAServerConfiguration aaaServerConfiguration;

	@Rule
	public PrintMethodRule printMethodRule = new PrintMethodRule();
	@Rule
	public ExpectedException expectedException = ExpectedException.none();

	@BeforeClass
	public static void setupBeforeClass() {
		RadiusDictionaryTestHarness.getInstance();
	}

	@Before
	public void setup() throws Exception {
		MockitoAnnotations.initMocks(this);
		timeSource = new FixedTimeSource(System.currentTimeMillis());
		RadAuthRequestBuilder authRequestBuilder = new RadAuthRequestBuilder();
		request = authRequestBuilder.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "dummyUserName")
				.addAttribute(RadiusAttributeConstants.CALLING_STATION_ID_STR, "dummyCallingStationId")
				.addAttribute(RadiusAttributeConstants.NAS_PORT_STR, "53").build();

		radClassicCSVAcctDriver = driverWith(defaultConfiguration());
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
					radClassicCSVAcctDriver = driverWith(baseConfiguration()
												.withMapping(attributeRelation("0:1"))
												.withMapping(attributeRelation("0:31"))
											);

					String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

					assertThat("dummyUserName,dummyCallingStationId," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				@Test
				public void escapesDelimiterIfPresentInAttributeValue() throws Exception {
					request.getRadiusAttribute(RadiusAttributeConstants.USER_NAME_STR)
							.setStringValue("dummy,User,Name");

					String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

					assertThat("dummy\\,User\\,Name,dummyCallingStationId," + getTimeStamp(),
							equalTo(configuredCsvLine));
				}

				public class MappingWithMultipleAvps {

					@Test
					public void firstAvpFoundInOrderOfConfigurationIsDumped() throws Exception {
						radClassicCSVAcctDriver = driverWith(baseConfiguration()
													.withMapping(attributeRelation("0:5,0:1")
													.header("Identity"))
												);

						String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

						assertThat("53," + getTimeStamp(), equalTo(configuredCsvLine));
					}

					@Test
					public void multipleAvpsCanBeConfiguredSeparatedByComma() throws Exception {
						radClassicCSVAcctDriver = driverWith(baseConfiguration()
													.withMapping(attributeRelation("0:5,0:1"))
												);

						String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

						assertThat("53," + getTimeStamp(), equalTo(configuredCsvLine));
					}

					@Test
					public void multipleAvpsCanBeConfiguredSeparatedBySemicolon() throws Exception {
						radClassicCSVAcctDriver = driverWith(baseConfiguration()
													.withMapping(attributeRelation("0:5;0:1"))
												);

						String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

						assertThat("53," + getTimeStamp(), equalTo(configuredCsvLine));
					}
				}

				public class EnclosingCharacterIsConfigured {

					@Test
					public void allValuesExceptTimeStampAreEnclosed() throws Exception {
						radClassicCSVAcctDriver = driverWith(defaultConfiguration()
													.enclosingCharacter("'")
												);

						String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

						assertThat("'dummyUserName','dummyCallingStationId'," + getTimeStamp(),
								equalTo(configuredCsvLine));
					}

					@Test
					public void escapesEnclosingCharacterIfPresentInAttributeValue() throws Exception {
						radClassicCSVAcctDriver = driverWith(defaultConfiguration()
													.enclosingCharacter("'"));

						request.getRadiusAttribute(RadiusAttributeConstants.USER_NAME_STR)
								.setStringValue("'dummyUserName'");

						String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

						assertThat("'\\'dummyUserName\\'','dummyCallingStationId'," + getTimeStamp(),
								equalTo(configuredCsvLine));
					}

					@Test
					public void valuesOfAttributesWithMultipleOccurencesAreEnclosed() throws Exception {
						radClassicCSVAcctDriver = driverWith(baseConfiguration()
													.withMapping(attributeRelation("0:25"))
													.enclosingCharacter("'")
													);

						List<String> classValues = new ArrayList<String>();
						classValues.add("class1");
						classValues.add("class2");
						classValues.add("class3");

						request = new RadAuthRequestBuilder()
								.addAttribute(RadiusAttributeConstants.CLASS_STR, classValues).build();
						String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

						assertThat("'class1;class2;class3'," + getTimeStamp(), equalTo(configuredCsvLine));
					}
				}

				@Test
				public void givenEnclosingCharacterIsNotConfiguredValuesOfAttributesWithMultipleOccurencesAreEnclosedWithinDoubleQuotes()
						throws Exception {
					radClassicCSVAcctDriver = driverWith(baseConfiguration()
												.withMapping(attributeRelation("0:25")
												.header("Class"))
											);

					List<String> classValues = new ArrayList<String>();
					classValues.add("class1");
					classValues.add("class2");
					classValues.add("class3");
					request = new RadAuthRequestBuilder().addAttribute(RadiusAttributeConstants.CLASS_STR, classValues)
							.build();

					String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

					assertThat("\"class1;class2;class3\"," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				@Test
				public void cdrCanBePrefixedWithATimestamp() throws Exception {
					radClassicCSVAcctDriver = driverWith(defaultConfiguration().prefixedCdrTimeStamp());

					String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

					assertThat(getTimeStamp() + ",dummyUserName,dummyCallingStationId", equalTo(configuredCsvLine));
				}

				@Test
				public void cdrCanBeSuffixedWithATimestamp() throws Exception {
					radClassicCSVAcctDriver = driverWith(defaultConfiguration().suffixedCdrTimeStamp());

					String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

					assertThat("dummyUserName,dummyCallingStationId," + getTimeStamp(), equalTo(configuredCsvLine));
				}
			}

			public class AttributesConfiguredInMappingAreNotPresentInRequest {

				@Test
				public void emptyStringIsDumpedIfNoDefaultValueIsConfigured() throws Exception {
					request = new RadAuthRequestBuilder()
							.addAttribute(RadiusAttributeConstants.USER_NAME_STR, "dummyUserName").build();

					String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

					assertThat("dummyUserName,," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				@Test
				public void defaultValueIsDumpedWhenConfigured() throws Exception {
					radClassicCSVAcctDriver = driverWith(baseConfiguration()
												.withMapping(
														attributeRelation("0:1").
														defaultValue("defaultUserName"))
												.withMapping(
														attributeRelation("0:31")
														.defaultValue("defaultCallingStationId"))
												.withMapping(
														attributeRelation("0:25")
														.defaultValue("defaultClassValue"))
											);

					String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

					assertThat("dummyUserName,dummyCallingStationId,defaultClassValue," + getTimeStamp(),
							equalTo(configuredCsvLine));
				}

				@Test
				public void doesNotEscapeDelimiterIfPresentInDefaultValue() throws Exception {
					radClassicCSVAcctDriver = driverWith(baseConfiguration()
												.withMapping(
														attributeRelation("0:1")
														.defaultValue("Default,User,Name"))
											 );

					request = new RadAuthRequestBuilder().build();

					String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

					assertThat("Default,User,Name," + getTimeStamp(), equalTo(configuredCsvLine));
				}

				@Test
				public void escapesEnclosingCharacterIfPresentInDefaultValue() throws Exception {
					radClassicCSVAcctDriver = driverWith(baseConfiguration()
												.withMapping(
														attributeRelation("0:1")
														.defaultValue("'DefaultUserName'"))
												.withMapping(
														attributeRelation("0:31")
														.defaultValue("DefaultCallingStationId"))
												.enclosingCharacter("'")
							);

					request = new RadAuthRequestBuilder().build();

					String configuredCsvLine = radClassicCSVAcctDriver.getConfiguredCSVLine(request);

					assertThat("'\\'DefaultUserName\\'','DefaultCallingStationId'," + getTimeStamp(),
							equalTo(configuredCsvLine));
				}
			}
		}
	}

	@Test
	public void makesHeaderBasedOnTheValueConfiguredInHeaderFieldOfAttributeMapping()
			throws DriverInitializationFailedException {
		String configuredCSVHeaderLine = radClassicCSVAcctDriver.getConfiguredCSVHeaderLine();

		assertThat("UserName,Calling-Station-ID,CDRTimeStamp", equalTo(configuredCSVHeaderLine));
	}

	@Test
	public void usesRadiusDictionaryAvpNameIfHeaderIsNotConfiguredInAttributeMapping() throws Exception {
		radClassicCSVAcctDriver = driverWith(
									baseConfiguration()
									.withMapping(attributeRelation("0:1"))
									.withMapping(attributeRelation("0:31"))
									.withMapping(attributeRelation("21067:117:1"))
								);

		String configuredCSVHeaderLine = radClassicCSVAcctDriver.getConfiguredCSVHeaderLine();

		assertThat("User-Name,Calling-Station-Id,21067:Param1,CDRTimeStamp", equalTo(configuredCSVHeaderLine));
	}

	@Test
	public void emptyStringIsDumpedInHeaderIfConfiguredAvpIsInvalid() throws Exception {
		radClassicCSVAcctDriver = driverWith(baseConfiguration()
									.withMapping(
											 attributeRelation("0:31")
											.header("Calling-Station-ID"))
									.withMapping(attributeRelation("0:"))
								);

		String configuredCSVHeaderLine = radClassicCSVAcctDriver.getConfiguredCSVHeaderLine();

		assertThat("Calling-Station-ID,,CDRTimeStamp", equalTo(configuredCSVHeaderLine));
	}

	public class CdrTimeStampHeaderIsConfigured {

		@Test
		public void configuredHeaderIsUsedAsHeaderOfTimeStamp() throws Exception {
			radClassicCSVAcctDriver = driverWith(
										 defaultConfiguration()
										.withCdrTimeStampHeader("Time-Stamp")
									);

			String configuredCSVHeaderLine = radClassicCSVAcctDriver.getConfiguredCSVHeaderLine();

			assertThat("UserName,Calling-Station-ID,Time-Stamp", equalTo(configuredCSVHeaderLine));
		}

		@Test
		public void cdrHeaderIsPlacedInRespectToCdrTimeStampPosition() throws Exception {
			radClassicCSVAcctDriver = driverWith(defaultConfiguration().prefixedCdrTimeStamp());

			String configuredCSVHeaderLine = radClassicCSVAcctDriver.getConfiguredCSVHeaderLine();

			assertThat("CDRTimeStamp,UserName,Calling-Station-ID", equalTo(configuredCSVHeaderLine));

			radClassicCSVAcctDriver = driverWith(defaultConfiguration().suffixedCdrTimeStamp());

			configuredCSVHeaderLine = radClassicCSVAcctDriver.getConfiguredCSVHeaderLine();

			assertThat("UserName,Calling-Station-ID,CDRTimeStamp", equalTo(configuredCSVHeaderLine));
		}
	}

	public RadClassicCSVAcctDriver driverWith(DriverConfigurationBuilder configuration)
			throws DriverInitializationFailedException {
		radClassicCsvDriverConfigurable.postReadProcessing();

		Mockito.when((aaaServerContext).getServerConfiguration()).thenReturn(aaaServerConfiguration);
		Mockito.when(aaaServerConfiguration.getDriverConfigurationProvider()).thenReturn(driverConfigurationProvider);
		Mockito.when(driverConfigurationProvider.getDriverConfiguration(Mockito.anyString()))
				.thenReturn(configuration.getDriverConfiguration());
		Mockito.when(aaaServerContext.getTaskScheduler()).thenReturn(new FakeTaskScheduler());
		RadClassicCSVAcctDriver radClassicCSVAcctDriver = new RadClassicCSVAcctDriver(aaaServerContext, ANY_ID,
				timeSource);
		radClassicCSVAcctDriver.init();
		return radClassicCSVAcctDriver;
	}

	public DriverConfigurationBuilder baseConfiguration() throws Exception {
		return readConfiguration(pathToConfigurationFile + NO_ATTRIBUTE_MAPPING);
	}

	public DriverConfigurationBuilder defaultConfiguration() throws Exception {
		return baseConfiguration().withMapping(AttributeRelationBuilder.attributeRelation("0:1").header("UserName"))
				.withMapping(AttributeRelationBuilder.attributeRelation("0:31").header("Calling-Station-ID"));
	}

	private DriverConfigurationBuilder readConfiguration(String configFileName) throws Exception {
		ClasspathResource resource = ClasspathResource.at(configFileName);
		radClassicCsvDriverConfigurable = (RadClassicCSVDriverConfigurable) ConfigUtil
				.deserialize(new File(resource.getAbsolutePath()), RadClassicCSVDriverConfigurable.class);
		radClassicCsvDriverConfigurable.postReadProcessing();
		return new DriverConfigurationBuilder(radClassicCsvDriverConfigurable.getDriverConfigurationList().get(0));
	}

	private String getTimeStamp() {
		Date date = new Date(timeSource.currentTimeInMillis());
		SimpleDateFormat format = new SimpleDateFormat(radClassicCSVAcctDriver.getCDRTimeStampFormat());
		String timeStamp = format.format(date);
		return timeStamp;
	}
}
