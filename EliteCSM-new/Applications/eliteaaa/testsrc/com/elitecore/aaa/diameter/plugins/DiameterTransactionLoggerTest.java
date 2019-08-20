package com.elitecore.aaa.diameter.plugins;

import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.core.plugins.conf.DiameterTransactionLoggerConfigurable;
import com.elitecore.aaa.core.plugins.conf.FormatMappingData;
import com.elitecore.aaa.core.radius.dictionary.RadiusDictionaryTestHarness;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.radius.service.auth.constant.AuthReplyMessageConstant;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.config.util.FileUtil.ExtensionFilter;
import com.elitecore.core.commons.config.util.FileUtil.WithoutExtensionFilter;
import com.elitecore.core.commons.plugins.PluginContext;
import com.elitecore.core.commons.plugins.PluginInfo;
import com.elitecore.core.commons.plugins.data.PluginCallerIdentity;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import static com.elitecore.core.commons.config.util.FileMatchers.containsLine;
import static com.elitecore.core.commons.config.util.FileUtil.listFiles;
import static com.elitecore.diameterapi.diameter.common.util.DiameterUtility.addOrReplaceAvp;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;


public class DiameterTransactionLoggerTest {

	private static final String DEFAULT_TIMESTAMP = "yyyy-MM-dd_HHmmss_S";
	private static final String ARGUMENT = "LOG";
	private static final String PRIFIX_ONE = "1_";
	private static final String PRIFIX_TWO = "2_";
	private static final String SUFIX_ONE = "_1.txt";
	private static final String SUFIX_TWO = "_2.txt";
	private static final String FILE_NAME_WITH_EXTENSION = "filename.txt";
	private static final PluginCallerIdentity DUMMY_IDENTITY = null;
	private static final PluginInfo DUMMY_PLUGIN_INFO = null;
	private static final ISession DUMMY_SESSION = null;
	private static final String DEFAULT_LOCATION = File.separator + "logs" + File.separator;
	private static final String GROUPED_AVP = DiameterAVPConstants.EC_PROFILE_AVPAIR_GROUP + "." + DiameterAVPConstants.EC_PROFILE_PARAM1;
	private static final String USERNAME_VALUE = "test";
	private static final String NAS_IP_VALUE = "127.0.0.1";
	private static final String ANY = "value";


	private DiameterTransactionLogger plugin;
	private DiameterRequest diameterRequest;
	private DiameterAnswer diameterAnswer;
	private File tempFolder;
	private DiameterTransactionLoggerConfigurable pluginData;
	private List<File> recordedFiles = new ArrayList<File>();
	private FakeTaskScheduler fakeTaskScheduler;
	private FixedTimeSource timesource;
	private String fileLocation;


	@Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();

	@Mock private PluginContext pluginContext;
	@Mock public AAAServerContext serverContext;


	@BeforeClass
	public static void setUpBeforeClass() throws Exception {
		DummyDiameterDictionary.getInstance();
	}

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);

		RadiusDictionaryTestHarness.getInstance();
		fakeTaskScheduler = new FakeTaskScheduler();
		createPluginData();
		timesource = new FixedTimeSource(System.currentTimeMillis());
		tempFolder = temporaryFolder.getRoot();
		when(pluginContext.getServerContext()).thenReturn(serverContext);
		when(serverContext.getServerHome()).thenReturn(tempFolder.getAbsolutePath());
		when(serverContext.getTaskScheduler()).thenReturn(fakeTaskScheduler);
		createRequestAndResponse();
		fileLocation = tempFolder.getAbsolutePath() + DEFAULT_LOCATION;

		addAttributeInRequest(DiameterAVPConstants.USER_NAME, USERNAME_VALUE);
		addAttributeInRequest(DiameterAVPConstants.NAS_IP_ADDRESS, NAS_IP_VALUE);

	}

	@Test
	public void isOnlyAPostPlugin() throws InitializationFailedException {
		createPlugin(pluginData);

		exception.expect(UnsupportedOperationException.class);

		plugin.handleInMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);
	}

	@Test
	public void transactionIsLoggedAsPerConfiguredFormat() throws Exception {
		givenFormat("username={0:1},nas-ipaddress={0:4}");

		createPlugin(pluginData);

		plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

		doRollOver();

		assertTransactionLog(String.format("username=%s,nas-ipaddress=%s", USERNAME_VALUE, NAS_IP_VALUE));
	}

	private void assertTransactionLog(String log) {
		listRecordedFilesAt(fileLocation, "txt");

		assertThat(recordedFiles.get(0), containsLine(1, equalTo(log)));
	}

	@Test
	public void attributesFromRequestCanBeRetrivedUsingREQKeyword() throws Exception {
		givenFormat("username={$REQ:0:1},nas-ipaddress={$REQ:0:4}");

		createPlugin(pluginData);

		plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

		doRollOver();

		assertTransactionLog(String.format("username=%s,nas-ipaddress=%s", USERNAME_VALUE, NAS_IP_VALUE));
	}

	@Test
	public void attributesFromResponseCanBeRetrivedUsingRESKeyword() throws Exception {
		givenFormat("response_message={$RES:0:268}");

		createPlugin(pluginData);

		plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

		doRollOver();

		assertTransactionLog(String.format("response_message=%s", ResultCode.DIAMETER_SUCCESS.code));
	}

	@Test
	public void retrivesAttributeFromRequestPacketIfNoKeyWordGivenInExpression() throws Exception {
		givenFormat("username={0:1},nas-ipaddress={0:4}");

		createPlugin(pluginData);

		plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

		doRollOver();

		assertTransactionLog(String.format("username=%s,nas-ipaddress=%s", USERNAME_VALUE, NAS_IP_VALUE ));
	}

	@Test
	public void groupedAVPCanAlsoBeConfigured() throws Exception {
		givenFormat("group-attribute={" + GROUPED_AVP + "}");

		createPlugin(pluginData);

		addOrReplaceAvp(GROUPED_AVP, diameterRequest, ANY);
		
		addAttributeInRequest(GROUPED_AVP, ANY);
		plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

		doRollOver();

		listRecordedFilesAt(fileLocation, "txt");

		assertTransactionLog(String.format("group-attribute=%s", ANY));

	}

	@Test
	public void multipleOccurencesOfAttributeAreLoggedSeparatedBySemicolon() throws Exception {
		givenFormat("username={$REQ:0:1}");
		createPlugin(pluginData);

		diameterRequest.getAVP(DiameterAVPConstants.USER_NAME).setStringValue("test1");
		diameterRequest.addAvp(DiameterAVPConstants.USER_NAME, "test2");

		plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

		doRollOver();

		listRecordedFilesAt(fileLocation, "txt");

		assertTransactionLog(String.format("username=%s", "test1;test2"));
	}

	@Test
	public void expressionLibraryFunctionsAreSupported() throws Exception {
		givenFormat("username={concat($REQ:0:1,\"@elitecore.com\")}");

		createPlugin(pluginData);

		plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

		doRollOver();

		listRecordedFilesAt(fileLocation, "txt");

		assertTransactionLog(String.format("username=%s", "test@elitecore.com"));
	}

	@Test
	public void emptyStringIsRecordedIfConfiguredAttributeIsNotPresentInRequest() throws Exception {
		givenFormat("username={0:1},calling-station-id={0:31}");

		createPlugin(pluginData);

		plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

		doRollOver();

		listRecordedFilesAt(fileLocation, "txt");

		assertTransactionLog(String.format("username=%s,calling-station-id=", USERNAME_VALUE));
	}

	public class LogFileNameAndLocation {

		@Before
		public void setUp() {
			pluginData.setSequenceRange("[1-2]");
		}

		@Test
		public void requestAttributesCanBeUtilizedInLogFileNameUsingKeywordREQ() throws Exception {

			pluginData.setLogFileName("{$REQ:0:4}.txt");

			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(fileLocation, "txt");

			assertNameOfRecordedFileIs(PRIFIX_ONE + NAS_IP_VALUE + timeStampWithTxtExtension());

		}

		@Test
		public void keywordRESCanBeConfiguredInLogFileName() throws Exception {
			pluginData.setLogFileName("{$RES:0:268}.txt");
			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(tempFolder.getAbsolutePath() + DEFAULT_LOCATION, "txt");

			assertNameOfRecordedFileIs(PRIFIX_ONE + AuthReplyMessageConstant.AUTHENTICATION_SUCCESS + timeStampWithTxtExtension());

		}

		@Test
		public void logFilesCanBeNamedAccordingToTimeUsingDateFormat() throws Exception {

			pluginData.setLogFileName("{yyyy-MM-dd}");

			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(tempFolder.getAbsolutePath() + DEFAULT_LOCATION, null);

			assertNameOfRecordedFileIs(PRIFIX_ONE + timeStamp("yyyy-MM-dd"));

		}

		@Test
		public void keyword_PacketType_IsUsedToRecordsFilesUsingRequestPacketType() throws Exception {

			pluginData.setLogFileName("{$REQ:PacketType}");

			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(tempFolder.getAbsolutePath() + DEFAULT_LOCATION, null);

			assertNameOfRecordedFileIs(PRIFIX_ONE + String.valueOf(RadiusConstants.ACCESS_REQUEST_MESSAGE) + timeStamp(DEFAULT_TIMESTAMP));

		}

		@Test
		public void canBeRelativePathRelativeToLogsDirectory() throws Exception {
			pluginData.setLogFileName("logfile");

			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(tempFolder.getAbsolutePath() + DEFAULT_LOCATION, null);

			assertNameOfRecordedFileIs(PRIFIX_ONE + "logfile" + timeStamp(DEFAULT_TIMESTAMP));
		}

		@Test
		public void canBeAbsolutePath() throws Exception {
			pluginData.setLogFileName(tempFolder.getAbsolutePath() + File.separator + FILE_NAME_WITH_EXTENSION);
			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(tempFolder.getAbsolutePath(), "txt");

			assertNameOfRecordedFileIs(PRIFIX_ONE + "filename" + timeStampWithTxtExtension());
		}

		@Test
		public void expressionWithoutKeyWordIsConsideredLiteralIfOfUnknownDateFormat() throws Exception {
			pluginData.setLogFileName("{0:1}.txt");

			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(fileLocation, "txt");

			assertNameOfRecordedFileIs(PRIFIX_ONE + "0:1.txt");
		}

		@Test
		public void dynamicFolderWillBeCreatedBasedOnConfiguredPathExpression() throws Exception {
			pluginData.setLogFileName("{$REQ:0:31}/filename.txt");

			createPlugin(pluginData);

			diameterRequest.addAvp(DiameterAVPConstants.CALLING_STATION_ID, "1.1.1.1");

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			diameterRequest.addAvp(DiameterAVPConstants.CALLING_STATION_ID, "2.2.2.2");

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(fileLocation, "txt");

			Collections.sort(recordedFiles);

			String tempLocation = tempFolder.getAbsolutePath() +DEFAULT_LOCATION;

			assertThat(tempLocation  + "1.1.1.1" + File.separator + PRIFIX_ONE + "filename" + timeStampWithTxtExtension(), 
					is(equalTo(recordedFiles.get(0).getAbsolutePath())));
			assertThat(tempLocation + "2.2.2.2" + File.separator + PRIFIX_ONE + "filename" + timeStampWithTxtExtension(), 
					is(not(recordedFiles.get(0).getAbsolutePath())));


			assertThat(tempLocation + "2.2.2.2" + File.separator + PRIFIX_ONE + "filename" + timeStampWithTxtExtension(), 
					is(equalTo(recordedFiles.get(1).getAbsolutePath())));
			assertThat(tempLocation + "1.1.1.1" + File.separator + PRIFIX_ONE + "filename" + timeStampWithTxtExtension(), 
					is(not(recordedFiles.get(1).getAbsolutePath())));

		}

		@Test
		public void requiredDirectoriesAreCreatedAlongWithFileAsPerConfiguredFileName() throws Exception {
			pluginData.setLogFileName("{$REQ:0:4}/{$REQ:0:1}.txt");

			createPlugin(pluginData);

			diameterRequest.getAVP(DiameterAVPConstants.NAS_IP_ADDRESS).setStringValue(NAS_IP_VALUE);
			diameterRequest.getAVP(DiameterAVPConstants.USER_NAME).setStringValue("user1");

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			diameterRequest.getAVP(DiameterAVPConstants.NAS_IP_ADDRESS).setStringValue(NAS_IP_VALUE);
			diameterRequest.getAVP(DiameterAVPConstants.USER_NAME).setStringValue("user2");

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(fileLocation, "txt");

			Collections.sort(recordedFiles);

			String tempLocation = tempFolder.getAbsolutePath() +DEFAULT_LOCATION;

			assertThat(tempLocation + NAS_IP_VALUE + File.separator + PRIFIX_ONE + "user1" + timeStampWithTxtExtension(), 
					is(equalTo(recordedFiles.get(0).getAbsolutePath())));
			assertThat(tempLocation  + NAS_IP_VALUE + File.separator + PRIFIX_ONE + "user2" + timeStampWithTxtExtension(), 
					is(equalTo(recordedFiles.get(1).getAbsolutePath())));

		}

		@Test
		public void noDataIsLoggedIfConfiguredAttributeInFolderNameIsNotPresentInRequest() throws Exception {
			pluginData.setLogFileName("{$REQ:0:31}/filename.txt");
			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(fileLocation, "txt");

			assertThat(recordedFiles.isEmpty(), is(true));

		}

	}

	public class LocalSequencing {

		@Before
		public void setUp() {
			pluginData.setSequenceRange("[1-2]");
		}

		@Test
		public void sequenceIsAddedAsPrefixInFileNameIfSequencePositionIsPrefix() throws Exception {
			pluginData.setSequencePosition("prefix");
			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(fileLocation, "txt");

			Collections.sort(recordedFiles);

			assertThat(recordedFiles.get(0).getName(), is(equalTo(PRIFIX_ONE + "filename" + timeStampWithTxtExtension())));
			assertThat(recordedFiles.get(1).getName(), is(equalTo(PRIFIX_TWO + "filename" + timeStampWithTxtExtension())));
		}

		@Test
		public void sequenceIsAddedAsSuffixInFileNameIfIfSequencePositionIsSuffix() throws Exception {
			pluginData.setSequencePosition("suffix");
			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(fileLocation, "txt");

			Collections.sort(recordedFiles);

			assertThat(recordedFiles.get(0).getName(), is(equalTo("filename" + timeStamp(DEFAULT_TIMESTAMP) + SUFIX_ONE)));
			assertThat(recordedFiles.get(1).getName(), is(equalTo("filename" + timeStamp(DEFAULT_TIMESTAMP) + SUFIX_TWO)));

		}

		@Test
		public void ifGivenSequenceIsExhaustedItIsReset() throws Exception {
			pluginData.setSequencePosition("suffix");

			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			timesource.advance(1001);
			doRollOver();

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			timesource.advance(1001);
			doRollOver();

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

			timesource.advance(1001);
			doRollOver();

			listRecordedFilesAt(fileLocation, "txt");

			Collections.sort(recordedFiles);

			assertThat(recordedFiles.get(0).getName(), endsWith("1.txt"));
			assertThat(recordedFiles.get(1).getName(), endsWith("2.txt"));
			assertThat(recordedFiles.get(2).getName(), endsWith("1.txt"));
		}

		@Test
		public void fileWillNotRollOverIfInvalidSequenceRangeIsConfigured() throws Exception {
			pluginData.setLogFileName(FILE_NAME_WITH_EXTENSION);
			pluginData.setSequencePosition("prefix");
			pluginData.setSequenceRange("invalid");
			createPlugin(pluginData);

			plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT, DUMMY_IDENTITY);

			doRollOver();

			listRecordedFilesAt(fileLocation, "inp");

			assertFalse(recordedFiles.isEmpty());

		}

	}

	@Test
	public void rollsOverAllInpFilesToFileAsPerConfigurationWhenStopped() throws Exception {
		createPlugin(pluginData);

		plugin.handleOutMessage(diameterRequest, diameterAnswer, DUMMY_SESSION, ARGUMENT,  DUMMY_IDENTITY);

		plugin.stop();

		listRecordedFilesAt(fileLocation, "txt");

		assertThat(recordedFiles.get(0), containsLine(1, equalTo("username=test,nas-ipaddress=127.0.0.1")));
	}

	private void doRollOver() throws InterruptedException {
		fakeTaskScheduler.tick();
	}

	private void givenFormat(String format) {
		pluginData.getFormatMappings().get(0).setKey(ARGUMENT);
		pluginData.getFormatMappings().get(0).setFormat(format);
	}

	private void createPlugin(DiameterTransactionLoggerConfigurable configurable) throws InitializationFailedException {
		plugin = new DiameterTransactionLogger(pluginContext, DUMMY_PLUGIN_INFO, configurable, timesource);
		plugin.init();
	}

	private void createPluginData() {
		FormatMappingData mappingData = new FormatMappingData();
		mappingData.setKey(ARGUMENT);
		mappingData.setFormat("username={0:1},nas-ipaddress={0:4}");

		pluginData = new DiameterTransactionLoggerConfigurable();
		pluginData.setSequenceGlobalization(false);
		pluginData.setSequenceRange("[1-2]");
		pluginData.setLogFileName(FILE_NAME_WITH_EXTENSION);
		pluginData.setSequencePosition("prefix");
		pluginData.getFormatMappings().add(mappingData);
	}

	private void createRequestAndResponse() throws Exception {

		diameterRequest = new DiameterRequest();
		diameterAnswer = new DiameterAnswer(diameterRequest);

		diameterAnswer.addAvp(DiameterAVPConstants.RESULT_CODE, ResultCode.DIAMETER_SUCCESS.code + "");

	}

	private void addAttributeInRequest(String identifier, String value) {
		diameterRequest.addAvp(identifier, value);
	}

	private void listRecordedFilesAt(String filePath, String extension) {
		if (extension == null) {
			recordedFiles = listFiles(new File(filePath), new WithoutExtensionFilter());
		} else {
			recordedFiles = listFiles(new File(filePath),new ExtensionFilter(extension));
		}
	}

	private void assertNameOfRecordedFileIs(String fileName) {
		assertThat(fileName, is(equalTo(recordedFiles.get(0).getName())));
	}

	private String timeStampWithTxtExtension() {
		return timeStamp(DEFAULT_TIMESTAMP) + ".txt";
	}

	private String timeStamp(String dateFormat) {
		Date date = new Date(timesource.currentTimeInMillis());
		SimpleDateFormat format =  new SimpleDateFormat(dateFormat);
		String timeStamp = format.format(date);
		return timeStamp;
	}


}
