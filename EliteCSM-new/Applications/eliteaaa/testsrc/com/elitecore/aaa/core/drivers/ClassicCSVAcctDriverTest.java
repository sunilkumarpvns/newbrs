package com.elitecore.aaa.core.drivers;

import com.elitecore.aaa.core.data.AttributesRelation;
import com.elitecore.aaa.core.data.StripAttributeRelation;
import com.elitecore.aaa.core.diameter.DummyDiameterDictionary;
import com.elitecore.aaa.diameter.service.application.ApplicationRequest;
import com.elitecore.commons.base.FixedTimeSource;
import com.elitecore.core.commons.config.util.FileUtil.ExtensionFilter;
import com.elitecore.core.commons.config.util.FileUtil.LastDateModifiedComparator;
import com.elitecore.core.commons.drivers.DriverInitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.fileio.RollingTypeConstant;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.systemx.esix.FakeTaskScheduler;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;
import java.util.*;

import static com.elitecore.core.commons.config.util.FileMatchers.containsLine;
import static com.elitecore.core.commons.config.util.FileUtil.listFiles;
import static org.hamcrest.CoreMatchers.*;
import static org.junit.Assert.*;

/*
 * This test class will only test the behavior specific to ClassicCSVAcctDriver.
 * The behavior which are declared as protected methods but are implemented by
 * RadClassicCSVAcctDriver and NasClassicCSVAcctDriver will be tested in their
 * own respective test classes. 
 * */

@RunWith(HierarchicalContextRunner.class)
public class ClassicCSVAcctDriverTest {
	
	
	private static final String DEFAULT_LOCATION = "data" + File.separator + "csvfiles";
	private static final String VALUE = "value";
	private static final String HEADER = "header";

	
	private File csvTempFolder;
	private FixedTimeSource timesource;
	private FakeTaskScheduler fakeTaskScheduler;
	private DiameterRequest diameterRequest = new DiameterRequest();
	private ApplicationRequest request = new ApplicationRequest(diameterRequest);
	private ClassicCSVAcctDriverStub classicCSVAcctDriver; 


	@Rule public ExpectedException exception = ExpectedException.none();
	@Rule public TemporaryFolder temporaryFolder = new TemporaryFolder();
	@Mock private ServerContext serverContext;
	
	private List<File> cdrFiles;
	private Comparator<File> lastDateModified = new LastDateModifiedComparator();

	
	@BeforeClass
	public static void setUpBeforeClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Before
	public void setup() throws IOException {
		MockitoAnnotations.initMocks(this);
		timesource = new FixedTimeSource(System.currentTimeMillis());
		fakeTaskScheduler = new FakeTaskScheduler();
		Mockito.when(serverContext.getTaskScheduler()).thenReturn(fakeTaskScheduler);
		classicCSVAcctDriver = Mockito.spy(new ClassicCSVAcctDriverStub(serverContext));
		csvTempFolder = temporaryFolder.newFolder("csvTempFolder");
		Mockito.when(serverContext.getServerHome()).thenReturn(csvTempFolder.getAbsolutePath());
		classicCSVAcctDriver.setRollingTypeMap(RollingTypeConstant.RECORD_BASED_ROLLING, 1);		
	}

	@Test
	public void givenNoMappingIsConfiguredDriverInitializationFails() throws DriverInitializationFailedException {
		exception.expect(DriverInitializationFailedException.class);
		exception.expectMessage("No valid Attributes Mapping Found in driver : " + classicCSVAcctDriver.getName());
		Mockito.when(classicCSVAcctDriver.getAttributesRelationList()).thenReturn(new ArrayList<AttributesRelation>());
		classicCSVAcctDriver.init();
	}

	@Test
	public void cdrHeadersAndRecordsAreDumpedAsReturnedByConcreteImplementation () throws DriverProcessFailedException, DriverInitializationFailedException, IOException, InterruptedException {
		classicCSVAcctDriver.init();

		classicCSVAcctDriver.handleServiceRequest(request);
		rolloverCdrs();
		
		listCdrFiles();
		
		assertThat(cdrFiles.get(0), containsLine(1, equalTo(HEADER)));
		assertThat(cdrFiles.get(0), containsLine(2, equalTo(VALUE)));
	}

	@Test
	public void givenHeaderIsSetToFalseNoHeaderIsDumped() throws Exception {
		Mockito.when(classicCSVAcctDriver.getHeader()).thenReturn("FALSE");
		
		classicCSVAcctDriver.init();
		
		classicCSVAcctDriver.handleServiceRequest(request);
		rolloverCdrs();
		
		listCdrFiles();
		
		assertThat(cdrFiles.get(0), not(containsLine(equalTo(HEADER))));
		assertThat(cdrFiles.get(0), containsLine(1, equalTo(VALUE)));
	}
	
	@Test
	public void convertsAllInpFilesToCsvFilesWhenStopped() throws Exception {
		classicCSVAcctDriver.init();
		
		classicCSVAcctDriver.handleServiceRequest(request);
		
		listCdrFiles();
		assertTrue(cdrFiles.isEmpty());
	
		classicCSVAcctDriver.stop();
		
		listCdrFiles();
		verifyNotEmpty(cdrFiles);
	}

	public class LocationOfCdrs {
		
		@Test
		public void cdrPathCanBeRelative() throws Exception {
//		value of currentLocation is set from the classes extending RadClassicCSV and NasClassicCSV. To test the  functionality it's being set manually in this case
			classicCSVAcctDriver.currentLocation = "custompath/cdrFiles";
			classicCSVAcctDriver.init();
			classicCSVAcctDriver.handleServiceRequest(request);
			
			listInpFiles(csvTempFolder.getAbsolutePath() + File.separator + "custompath/cdrFiles");
			
			verifyNotEmpty(cdrFiles);
		}

		@Test
		public void cdrPathCanBeAbsolute() throws Exception {
//		current location is set from the classes extending RadClassicCSV and NasClassicCSV. To test the  functionality it's being set manually in this case
			classicCSVAcctDriver.currentLocation = csvTempFolder.getAbsolutePath();
			classicCSVAcctDriver.init();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			
			listInpFiles();
			
			verifyNotEmpty(cdrFiles);
		}
		
		@Test
		public void givenNoLocationIsSpecifiedFilesAreCreatedAtDefaultLocation() throws Exception {
			classicCSVAcctDriver.init();
			classicCSVAcctDriver.handleServiceRequest(request);
			
			listInpFiles(csvTempFolder.getAbsolutePath() + File.separator + DEFAULT_LOCATION);
			verifyNotEmpty(cdrFiles);
		}
	
		@Test
		public void givenNoFolderNameIsSpecifiedFilesAreCreatedAtTheDirectoryGivenInDefaultDirName() throws Exception {
			classicCSVAcctDriver.setFolderNameAttributes(null);
			
			classicCSVAcctDriver.init();
			classicCSVAcctDriver.handleServiceRequest(request);
			
			listInpFiles(csvTempFolder.getAbsolutePath() + File.separator + DEFAULT_LOCATION + File.separator + classicCSVAcctDriver.getDefaultDirName());
			verifyNotEmpty(cdrFiles);
		}
		
		@Test
		public void foldersHavingNameEqualToValueOfTheAttributeAreCreatedIfAnAttributeIsConfiguredInFolderName() throws Exception {
			classicCSVAcctDriver.setFolderNameAttributes(new String[] {"0:4"});
			classicCSVAcctDriver.init();
			
			IDiameterAVP nasIpAddrAvp = DummyDiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.NAS_IP_ADDRESS);
			request.getDiameterRequest().addAvp(nasIpAddrAvp);
			
			nasIpAddrAvp.setStringValue("0.0.0.0");
			classicCSVAcctDriver.handleServiceRequest(request);
			
			rolloverCdrs();
			
			
			nasIpAddrAvp.setStringValue("1.1.1.1");
			classicCSVAcctDriver.handleServiceRequest(request);
			
			rolloverCdrs();
			
			listCdrFiles();
			
			Collections.sort(cdrFiles);
			
			assertThat(cdrFiles.get(0).getAbsolutePath(), containsString("0.0.0.0"));
			assertThat(cdrFiles.get(0).getAbsolutePath(), not(containsString("1.1.1.1")));
			
			assertThat(cdrFiles.get(1).getAbsolutePath(), containsString("1.1.1.1"));
			assertThat(cdrFiles.get(1).getAbsolutePath(), not(containsString("0.0.0.0")));
		}
		
		@Test
		public void givenAttributeConfiguredInFolderNameIsNotPresentInRequestCdrsAreDumpedAtLocationConfiguredInDefaultDirName() throws Exception {
			classicCSVAcctDriver.setFolderNameAttributes(new String[] {"0:4"});
			classicCSVAcctDriver.init();
			
			assertNull(request.getAVP(DiameterAVPConstants.NAS_IP_ADDRESS));
			
			classicCSVAcctDriver.handleServiceRequest(request);
			afterAWhile();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			
			rolloverCdrs();
			
			listCdrFiles();
			
			Collections.sort(cdrFiles, lastDateModified);
			
			listInpFiles(csvTempFolder.getAbsolutePath() + File.separator + DEFAULT_LOCATION + File.separator + classicCSVAcctDriver.getDefaultDirName());
			verifyNotEmpty(cdrFiles);
			
		}
		
		@Test
		public void  filesAreCreatedAtDefaultLocationIfNoFolderNameOrDefaultDirNameIsSpecified() throws Exception {
			assertNull(classicCSVAcctDriver.getFolderNameAttributes());
			classicCSVAcctDriver.setDefaultDirName(null);
			
			classicCSVAcctDriver.init();
			classicCSVAcctDriver.handleServiceRequest(request);
			
			listInpFiles(csvTempFolder.getAbsolutePath() + File.separator + DEFAULT_LOCATION);
			verifyNotEmpty(cdrFiles);
		}
	}
	
	public class LocalSequencing {
		
		@Before
		public void setUp() {
			classicCSVAcctDriver.setSequenceRange("[1-2]");
		}
		
		@Test
		public void isConfiguredInPrefixSequenceIsAddedAsPrefixInFileName() throws Exception {
			classicCSVAcctDriver.setPattern("prefix");
			classicCSVAcctDriver.init();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			afterAWhile();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			afterAWhile();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			
			listCdrFiles(csvTempFolder.getAbsolutePath());
			
			Collections.sort(cdrFiles);
			
			assertThat(cdrFiles.get(0).getName(), startsWith("1"));
			assertThat(cdrFiles.get(1).getName(), startsWith("2"));
		}
		
		@Test
		public void isConfiguredInSuffixSequenceIsAddedAsSuffixInFileName() throws Exception {
			classicCSVAcctDriver.setPattern("suffix");
			classicCSVAcctDriver.init();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			afterAWhile();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			afterAWhile();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			
			listCdrFiles(csvTempFolder.getAbsolutePath());
			
			Collections.sort(cdrFiles);
			
			assertThat(cdrFiles.get(0).getName(), endsWith("1.csv"));
			assertThat(cdrFiles.get(1).getName(), endsWith("2.csv"));
		}

		@Test
		public void onceEndOfSequenceIsReachedItIsRestartedFromTheBeginningValue() throws Exception {
			classicCSVAcctDriver.setPattern("prefix");
			classicCSVAcctDriver.init();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			afterAWhile();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			afterAWhile();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			afterAWhile();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			
			listCdrFiles(csvTempFolder.getAbsolutePath());
			
			Collections.sort(cdrFiles);
			
			assertThat(cdrFiles.get(0).getName(), startsWith("1"));
			assertThat(cdrFiles.get(1).getName(), startsWith("1"));
			assertThat(cdrFiles.get(2).getName(), startsWith("2"));
		}
	}
	
	public class GlobalSequencing {
	
		@Before
		public void setUp() throws IOException {
			classicCSVAcctDriver.setSequenceRange("[1-10]");
			classicCSVAcctDriver.setPattern("prefix");
			classicCSVAcctDriver.setFolderNameAttributes(new String[] {"0:4"});
			classicCSVAcctDriver.setGlobalization(true);
	
			File file = new File(csvTempFolder.getAbsolutePath() + File.separator + "system");
			file.mkdirs();
			file = new File(csvTempFolder.getAbsolutePath()  + File.separator + "system" + File.separator + "cdr_sequence_classic_csv_driver");
			file.createNewFile();
			
		}
		
		@Test
		public void givenSequenceGlobalizationIsSetCdrsInDIfferntDirectoryHaveUniqueSequenceNumberAccordingToTheSequenceSpecified() throws Exception {
			classicCSVAcctDriver.init();
			IDiameterAVP nasIp = DummyDiameterDictionary.getInstance().getAttribute(DiameterAVPConstants.NAS_IP_ADDRESS);
			request.getDiameterRequest().addAvp(nasIp);
			
			nasIp.setStringValue("0.0.0.0");
			classicCSVAcctDriver.handleServiceRequest(request);
			rolloverCdrs();
			
			nasIp.setStringValue("1.1.1.1");
			classicCSVAcctDriver.handleServiceRequest(request);
			rolloverCdrs();
			
			nasIp.setStringValue("2.2.2.2");
			classicCSVAcctDriver.handleServiceRequest(request);
			rolloverCdrs();
			
			listCdrFiles(csvTempFolder.getAbsolutePath());
			
			Collections.sort(cdrFiles);
			assertThat(cdrFiles.get(0).getName(), startsWith("1"));
			assertThat(cdrFiles.get(1).getName(), startsWith("2"));
			assertThat(cdrFiles.get(2).getName(), startsWith("3"));
		}
		
	}
	
	public class StripPatternRelationIsConfigured {
		
		@Test
		public void givenPatternIsSuffixStringTrailingSeparatorIsRemoved() throws DriverInitializationFailedException {
			ArrayList<StripAttributeRelation> stripAttributeRelation = new ArrayList<StripAttributeRelation>();
			stripAttributeRelation.add(new StripAttributeRelation("0:1", "suffix", "@"));
			classicCSVAcctDriver.setStripAttributeRelationList(stripAttributeRelation);
			
			classicCSVAcctDriver.init();
			
			assertEquals("elitecore", classicCSVAcctDriver.getStrippedValue("0:1", "elitecore@sterlite"));
		}
		
		@Test
		public void givenPatternIsPrefixStringPrecedingSeparatorIsRemoved() throws DriverInitializationFailedException {
			ArrayList<StripAttributeRelation> stripAttributeRelation = new ArrayList<StripAttributeRelation>();
			stripAttributeRelation.add(new StripAttributeRelation("0:1", "prefix", "@"));
			classicCSVAcctDriver.setStripAttributeRelationList(stripAttributeRelation);
			
			classicCSVAcctDriver.init();
			
			assertEquals("sterlite", classicCSVAcctDriver.getStrippedValue("0:1", "elitecore@sterlite"));
		}
	}
	
	
	public class PostRollover {
		
		String archiveLocation;

		@Before
		public void setup() {
			classicCSVAcctDriver.setAllocatingProtocol("LOCAL");
			classicCSVAcctDriver.setPostOperation("Archive");
			archiveLocation = csvTempFolder.getAbsolutePath() + File.separator + "data/csvfiles/archive";
			classicCSVAcctDriver.setSArchiveLocation(archiveLocation);
		}
		
		
		@Test
		public void csvFileIsCopiedAccordingToPostOperationConfigured() throws Exception {
			classicCSVAcctDriver.init();
			
			classicCSVAcctDriver.handleServiceRequest(request);
			timesource.advance(1001);
			
			classicCSVAcctDriver.handleServiceRequest(request);
			
			listCdrFiles(archiveLocation);
			
			verifyNotEmpty(cdrFiles);
		}
	}

	private void verifyNotEmpty(List<File> cdrFiles) {
		assertFalse(cdrFiles.isEmpty());
	}
	
	private void listCdrFiles() {
		cdrFiles = listFiles(csvTempFolder.getAbsoluteFile(), new ExtensionFilter("csv"));
	}
	
	private void listCdrFiles(String path) {
		cdrFiles = listFiles(new File(path), new ExtensionFilter("csv"));
	}
	
	private void listInpFiles() {
		cdrFiles = listFiles(csvTempFolder.getAbsoluteFile(), new ExtensionFilter("inp"));
	}
	
	private void listInpFiles(String path) {
		cdrFiles = listFiles(new File(path), new ExtensionFilter("inp"));
	}
	
	private void rolloverCdrs() throws InterruptedException {
		timesource.advance(1001);
		classicCSVAcctDriver.cdrflush();
	}
	
	private class ClassicCSVAcctDriverStub extends ClassicCSVAcctDriver {
		private Map<RollingTypeConstant, Integer> rollingTypeMap = new HashMap<RollingTypeConstant, Integer>();
		private String sequenceRange;
		private String pattern;
		private String[] folderNameAttributes;
		private boolean globalization;
		private List<StripAttributeRelation> stripAttributeRealtion;
		private String allocatingProtocol = "NONE";
		private String postOperation;
		private String archiveLocation;
		private String defaultDirName = "no_nas_ip_address";
		
		public ClassicCSVAcctDriverStub(ServerContext serverContext) {
			super(serverContext, timesource);
			stripAttributeRealtion = new ArrayList<StripAttributeRelation>();
		}

		@Override
		public String getName() {
			return "classicCsvAcctDriver";
		}

		@Override
		public String getTypeName() {
			return null;
		}

		@Override
		public int getType() {
			return 0;
		}

		@Override
		protected String getCDRTimeStampHeader() {
			
			return null;
		}

		@Override
		protected String getCDRTimeStampFormat() {
			return "EEE dd MMM yyyy hh:mm:ss aaa";
		}

		@Override
		protected String getCDRTimeStampPosition() {
			
			return null;
		}

		@Override
		protected String getEnclosingChar() {
			
			return null;
		}

		@Override
		protected String[] getFileNameAttributes() {
			
			return null;
		}

		@Override
		protected String[] getFolderNameAttributes() {
			return this.folderNameAttributes;
		}
		
		public void setFolderNameAttributes(String[] folderNameAttribures) {
			this.folderNameAttributes = folderNameAttribures;
		}

		@Override
		protected String getCounterFileName() {
			return serverContext.getServerHome()+ File.separator + "system"+ File.separator + "cdr_sequence_classic_csv_driver";
		}

		@Override
		protected String getAllocatingProtocol() {
			return this.allocatingProtocol;
		}
		
		public void setAllocatingProtocol(String allowingProtocol) {
			this.allocatingProtocol  = allowingProtocol;
			
		}

		@Override
		protected String getUserName() {
			
			return null;
		}

		@Override
		protected String getPassword() {
			
			return null;
		}

		@Override
		protected int getPort() {
			
			return 0;
		}

		@Override
		protected String getDestinationLocation() {
			return null;
		}
		
		@Override
		protected String getIpAddress() {
			
			return null;
		}

		@Override
		protected int getFailOverTime() {
			
			return 0;
		}

		@Override
		protected String getPostOperation() {
			return this.postOperation;
		}
		
		void setPostOperation(String postOperation) {
			this.postOperation = postOperation;
		}

		@Override
		protected String getArchiveLocations() {
			return archiveLocation;
		}
		
		void setSArchiveLocation(String archiveLocation) {
			this.archiveLocation = archiveLocation;
		}


		@Override
		protected boolean getCreateBlankFile() {
			
			return false;
		}

		@Override
		protected String getmultiValueDelimeter() {
			
			return null;
		}

		@Override
		protected String getDefaultDirName() {
			return this.defaultDirName ;
		}

		public void setDefaultDirName(Object object) {
			this.defaultDirName = null;
		}
		
		@Override
		protected List<StripAttributeRelation> getStripAttributeRelationList() {
			return this.stripAttributeRealtion;
		}

		void setStripAttributeRelationList(List<StripAttributeRelation> stripAttributeRelation) {
			this.stripAttributeRealtion = stripAttributeRelation;
		}
		
		@Override
		protected String getNameFromArray(String[] fileAttributes, ServiceRequest request) {

			ApplicationRequest appRequest  = (ApplicationRequest)request;
			String strPrefixFileName = null;
			IDiameterAVP diameterAvp = null;
			if(fileAttributes != null && fileAttributes.length > 0) {
				int noOfFileAttr = fileAttributes.length;
				for(int i=0;i<noOfFileAttr;i++) {
					diameterAvp = appRequest.getAVP(fileAttributes[i],true);
					if(diameterAvp!=null){
						String strAttributeValue = diameterAvp.getStringValue();
						if(diameterAvp.getVendorId()==0){
							if(diameterAvp.getAVPCode()==DiameterAVPConstants.USER_NAME_INT){
								if(strAttributeValue.contains("@")) {
									strAttributeValue = strAttributeValue.substring(strAttributeValue.indexOf('@') + 1);
								}
							}
							strPrefixFileName = (strPrefixFileName != null) ? (strPrefixFileName+"_" + strAttributeValue): strAttributeValue;
						}else {
							strPrefixFileName = (strPrefixFileName != null) ? (strPrefixFileName+"_"+ strAttributeValue): strAttributeValue;
						}
					}
				}
			}
			return strPrefixFileName;	
		
		}

		@Override
		protected String getConfiguredCSVLine(ServiceRequest request) throws DriverProcessFailedException {
			return VALUE;
		}

		@Override
		protected String getConfiguredCSVHeaderLine() throws DriverInitializationFailedException {
			return HEADER;
		}

		@Override
		protected boolean isAttributeID(String str) {
			
			return false;
		}

		@Override
		protected String getAttributeValue(String attrID, ServiceRequest request) {
			
			return null;
		}

		@Override
		protected Map<RollingTypeConstant, Integer> getRollingTypeMap() {
			return rollingTypeMap;
		}
		
		void setRollingTypeMap(RollingTypeConstant rollingType, Integer value) {
			this.rollingTypeMap.put(rollingType, value);
		}

		@Override
		protected String getDelimeterLast() {
			
			return null;
		}

		@Override
		protected String getDelimeterFirst() {
			
			return null;
		}

		@Override
		protected String getHeader() {
			return "TRUE";
		}

		@Override
		protected String getDelimeter() {
			return ",";
		}

		@Override
		protected boolean getGlobalization() {
			return globalization;
		}
		
		public void setGlobalization(boolean globalization) {
			this.globalization = globalization;
		}

		@Override
		protected String getPattern() {
			return this.pattern;
		}
		
		public void setPattern(String pattern) {
			this.pattern = pattern;
		}

		@Override
		protected String getFileName() {
			return "CDRS.csv";
		}

		@Override
		protected String getSequenceRange() {
			return this.sequenceRange;
		}
		
		void setSequenceRange(String sequenceRange) {
			this.sequenceRange = sequenceRange;
		}
		@Override
		protected List<AttributesRelation> getAttributesRelationList() {
			List<AttributesRelation> attributeRelationList = new ArrayList<AttributesRelation>();
			attributeRelationList.add(new AttributesRelation("0:1", null, false, "UserName", null));
			return attributeRelationList;
		}
	}
	
	private void afterAWhile() throws InterruptedException {
		timesource.advance(1001);
	}
}
