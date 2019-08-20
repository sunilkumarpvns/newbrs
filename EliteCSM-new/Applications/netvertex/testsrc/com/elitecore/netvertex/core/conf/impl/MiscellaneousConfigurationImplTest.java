package com.elitecore.netvertex.core.conf.impl;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;

import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;

@RunWith(JUnitParamsRunner.class)
public class MiscellaneousConfigurationImplTest {

	private static final String OVERLOAD_RESULT_CODE = "Overload-Result-Code";
	@Rule
	public TemporaryFolder folder = new TemporaryFolder();
	private FileWriter writer;
	private String miscFilePath;
	private DummyNetvertexServerContextImpl serverContext = new DummyNetvertexServerContextImpl();
	private MiscellaneousConfigurationImpl miscellaneousConfigurationImpl = new MiscellaneousConfigurationImpl(serverContext);

	@Before
	public void setUp() throws IOException {
		serverContext.setServerHome(folder.getRoot().getAbsolutePath());
		File system = folder.newFolder("system");
		File miscFile = new File(system, "misc-config.properties");
		//FileMustBeCreated
		assertTrue(miscFile.createNewFile());
		miscFilePath = miscFile.getAbsolutePath();
		writer = new FileWriter(miscFilePath);
	}

	@After
	public void tearDown() throws IOException {
		reOpenFile();
	}
	
	
	public Object[][] dataProvider_For_test_Parameters_Must_Take_ConfiguredValue_When_Their_Values_Are_Valid() {
		
		return new Object[][] {
				{

							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation = true\n" +
							"session-nowait = false\n" +
							"session-batch = false\n" +
							"session.cache-enabled = false\n" +
							"Overload-Result-Code = 0\n" +
							"record-Reservation-Limit = 2000\n" +
							"sy.slronsnr = false\n"
							, false, true, false, true, false, false, false, 0, 2000, false
				},
				{
							"RAR-Enabled = true   \n" +
							"AddOn-Start-RAR-Enabled = TRUE\n" +
							"AddOn-Expiry-RAR-Enabled = FALSE\n" +
							"addOn.Reservation =false\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = true\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n" +
							"sy.slronsnr = true\n"
							, true, true, false, false, false, false, true, 3001, 2000, true
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = true\n" +
							"Overload-Result-Code =3000\n" +
							"record-Reservation-Limit =2000\n" +
							"sy.slronsnr = FALSE\n"
							, false, true, false, true, false, false, true, 3000, 2000, false
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = false\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =5000\n" +
							"sy.slronsnr = TRUE\n"
							, false, true, false, true, false, false, false, 3001, 5000, true
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = TRue\n" +
							"Overload-Result-Code =4000\n" +
							"record-Reservation-Limit =40000\n" +
							"sy.slronsnr = False\n"
							, false, true, false, true, false, false, true, 4000, 40000, false
				},
				//Case insensitive
				{

							"RAR-Enabled = False\n" +
							"AddOn-Start-RAR-Enabled = tRUe\n" +
							"AddOn-Expiry-RAR-Enabled = fAlSe\n" +
							"addOn.Reservation =TRUE\n" +
							"session-nowait =FALSE\n" +
							"session-batch =\n" +
							"session.cache-enabled = faLsE\n" +
							"Overload-Result-Code =\n" +
							"record-Reservation-Limit =\n"+
							"sy.slronsnr = True\n"
							, false, true, false, true, false, true, false, ResultCode.DIAMETER_TOO_BUSY.code, 6000, true
				}

		};
	}
	@Test 
	@Parameters(method="dataProvider_For_test_Parameters_Must_Take_ConfiguredValue_When_Their_Values_Are_Valid")
	public void test_Parameters_Must_Take_ConfiguredValue_When_Their_Values_Are_Valid(String properties, boolean isRAREnabled, boolean addOnStartRAREnabled,
			boolean addOnExpiryRAREnabled, boolean addOnReservationEnabled, boolean isSessionNoWait, boolean isSessionBatch, boolean isSessionCacheEnabled,        
			int resultCodeOnOverload, int recordReservationLimit, boolean slrOnSnr) throws IOException {

		write(properties);
		
		miscellaneousConfigurationImpl.readConfiguration();
		
		assertEquals(isRAREnabled, miscellaneousConfigurationImpl.getRAREnabled());
		assertEquals(addOnStartRAREnabled, miscellaneousConfigurationImpl.getAddOnStartRAREnabled());
		assertEquals(addOnExpiryRAREnabled, miscellaneousConfigurationImpl.getAddOnExpiryRAREnabled());
		assertEquals(addOnReservationEnabled, miscellaneousConfigurationImpl.getAddOnReservationEnabled());
		assertEquals(isSessionNoWait, miscellaneousConfigurationImpl.getSessionNoWait());
		assertEquals(isSessionBatch, miscellaneousConfigurationImpl.getSessionBatch());
		assertEquals(isSessionCacheEnabled, miscellaneousConfigurationImpl.isSessionCacheEnabled());
		assertEquals(resultCodeOnOverload, miscellaneousConfigurationImpl.getResultCodeOnOverload());
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		assertEquals(slrOnSnr, miscellaneousConfigurationImpl.getSLREnabledOnSNR());
		
		
	}

	private void write(String properties) throws IOException {
		writer.write(properties);
		writer.flush();
	}

	public Object[][] dataProvider_For_Test_Parameters_Must_Take_ReconfiguredValue_When_Their_Values_Are_Valid() {
		
		return new Object[][] {
				{
							"Query-Timeout =6\n" +
							"RAR-Enabled = true\n" +
							"AddOn-Start-RAR-Enabled = false\n" +
							"AddOn-Expiry-RAR-Enabled = true\n" +
							"addOn.Reservation =false\n" +
							"session-nowait =true\n" +
							"session-batch =true\n" +
							"session.cache-enabled =true\n" +
							"Overload-Result-Code =3004\n" +
							"record-Reservation-Limit =5000\n"+
							"sy.slronsnr = False\n"
							,
							"Query-Timeout =5\n" +
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = FaLSe\n" +
							"Overload-Result-Code =0\n" +
							"record-Reservation-Limit =2000\n"+
							"sy.slronsnr = True\n"
							, 5, false, true, false, true, false, false, false, 0, 2000, true
				},
				{
							"Query-Timeout =5\n" +
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = false\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr = True\n"
							,
						"Query-Timeout =6\n" +
							"RAR-Enabled = true   \n" +
							"AddOn-Start-RAR-Enabled = TRUE\n" +
							"AddOn-Expiry-RAR-Enabled = FALSE\n" +
							"addOn.Reservation =false\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = true\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr = False\n"
							, 6, true, true, false, false, false, false, true, 3001, 2000,false
				},
				{
							"Query-Timeout =9\n" +
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = false\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr = False\n"
							,
							"Query-Timeout =10\n" +
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = fAlse\n" +
							"Overload-Result-Code =5999\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr = false\n"
							, 10, false, true, false, true, false, false, false, 5999, 2000, false
				},
				{
							"Query-Timeout =9\n" +
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = true\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =1000\n"+
									"sy.slronsnr = TRUE\n"
							,
							"Query-Timeout =5\n" +
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = TruE\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =5000\n"+
									"sy.slronsnr = true\n"

							, 5, false, true, false, true, false, false, true, 3001, 5000, true
				},
				{
							"Query-Timeout =9\n" +
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = true\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr = TRUE\n"
							,
							"Query-Timeout =1\n" +
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = false\n" +
							"Overload-Result-Code =4000\n" +
							"record-Reservation-Limit =40000\n"+
									"sy.slronsnr = FALSE\n"
							, 1, false, true, false, true, false, false, false, 4000, 40000, false
				},
				//Case insensitive
				{
							"Query-Timeout =5\n" +
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = True\n" +
							"Overload-Result-Code =3002\n" +
							"record-Reservation-Limit =10000\n"+
									"sy.slronsnr = FALSE\n"
							,
							"Query-Timeout =\n" +
							"RAR-Enabled = False\n" +
							"AddOn-Start-RAR-Enabled = tRUe\n" +
							"AddOn-Expiry-RAR-Enabled = fAlSe\n" +
							"addOn.Reservation =TRUE\n" +
							"session-nowait =FALSE\n" +
							"session-batch =\n" +
							"session.cache-enabled = faLse\n" +
							"Overload-Result-Code =\n" +
							"record-Reservation-Limit =\n"+
									"sy.slronsnr = TRUE\n"
							, 0, false, true, false, true, false, true, false, 3004, 6000, true
				}
		};
	}
	
	@Test 
	@Parameters(method="dataProvider_For_Test_Parameters_Must_Take_ReconfiguredValue_When_Their_Values_Are_Valid")
	public void test_Parameters_Must_Take_ReconfiguredValue_When_Their_Values_Are_Valid(String beforeReload,
			String onReload, int queryTimeout, boolean isRAREnabled, boolean addOnStartRAREnabled,
			boolean addOnExpiryRAREnabled, boolean addOnReservationEnabled, boolean isSessionNoWait, boolean isSessionBatch,        
			boolean isSessionCacheEnabled, int resultCodeOnOverload, int recordReservationLimit, boolean slrOnSnr) throws IOException {
		
		write(beforeReload);
		miscellaneousConfigurationImpl.readConfiguration();
		 reOpenFile();

		write(onReload);
		miscellaneousConfigurationImpl.reloadConfiguration();
		
		assertEquals(isRAREnabled, miscellaneousConfigurationImpl.getRAREnabled());
		assertEquals(addOnStartRAREnabled, miscellaneousConfigurationImpl.getAddOnStartRAREnabled());
		assertEquals(addOnExpiryRAREnabled, miscellaneousConfigurationImpl.getAddOnExpiryRAREnabled());
		assertEquals(addOnReservationEnabled, miscellaneousConfigurationImpl.getAddOnReservationEnabled());
		assertEquals(isSessionNoWait, miscellaneousConfigurationImpl.getSessionNoWait());
		assertEquals(isSessionBatch, miscellaneousConfigurationImpl.getSessionBatch());
		assertEquals(isSessionCacheEnabled, miscellaneousConfigurationImpl.isSessionCacheEnabled());
		assertEquals(resultCodeOnOverload, miscellaneousConfigurationImpl.getResultCodeOnOverload());
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		assertEquals(slrOnSnr, miscellaneousConfigurationImpl.getSLREnabledOnSNR());
		
		
	}

	private void reOpenFile() throws IOException {
		writer.close();
		writer = new FileWriter(miscFilePath);
	}

	public Object[][] dataProvider_For_Test_Parameters_Must_Take_DefaultValue_When_ConfiguredValues_Are_Undefined_Or_There_Is_Only_Whitespace() {
		
		return new Object[][] {
				//ALL values undefined
				{
							"RAR-Enabled =\n" +
							"AddOn-Start-RAR-Enabled =\n" +
							"AddOn-Expiry-RAR-Enabled =\n" +
							"addOn.Reservation =\n" +
							"session-nowait =\n" +
							"session-batch =\n" +
							"session.cache-enabled =\n" +
							"Overload-Result-Code =\n" +
							"record-Reservation-Limit =\n"+
									"sy.slronsnr =\n"
							, true, true, true, false, true, true, true, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
				//ALL values are white space
				{
							"RAR-Enabled =   \n" +
							"AddOn-Start-RAR-Enabled =   \n" +
							"AddOn-Expiry-RAR-Enabled =   \n" +
							"addOn.Reservation =   \n" +
							"session-nowait =   \n" +
							"session-batch =  \n" +
							"session.cache-enabled =  \n" +
							"Overload-Result-Code =  \n" +
							"record-Reservation-Limit =  \n"+
									"sy.slronsnr = \n"
							, true, true, true, false, true, true,true,  ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = false\n" +
							"AddOn-Expiry-RAR-Enabled =\n" +
							"addOn.Reservation =\n" +
							"session-nowait =\n" +
							"session-batch =\n" +
							"session.cache-enabled =false\n" +
							"Overload-Result-Code =\n" +
							"record-Reservation-Limit =\n"+
									"sy.slronsnr =\n"
							, false, false, true, false, true, true, false, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
				{
							"RAR-Enabled = \n" +
							"AddOn-Start-RAR-Enabled = \n" +
							"AddOn-Expiry-RAR-Enabled =\n" +
							"addOn.Reservation =\n" +
							"session-nowait =\n" +
							"session-batch =\n" +
							"Overload-Result-Code = 5000\n" +
							"record-Reservation-Limit =\n"+
									"sy.slronsnr =\n"
							, true, true, true, false, true, true, true, 5000, 6000, false
				},
				{

							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = false\n" +
							"AddOn-Expiry-RAR-Enabled =\n" +
							"addOn.Reservation =\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled =\n" +
							"Overload-Result-Code = \n" +
							"record-Reservation-Limit =\n"+
									"sy.slronsnr =     \n"
							, false, false, true, false, false, false, true, ResultCode.DIAMETER_TOO_BUSY.code , 6000, false
				},
		};
	}
	
	@Test 
	@Parameters(method="dataProvider_For_Test_Parameters_Must_Take_DefaultValue_When_ConfiguredValues_Are_Undefined_Or_There_Is_Only_Whitespace")
	public void test_Parameters_Must_Take_DefaultValue_When_ConfiguredValues_Are_Undefined_Or_There_Is_Only_Whitespace(String properties, boolean isRAREnabled, boolean addOnStartRAREnabled,
			boolean addOnExpiryRAREnabled, boolean addOnReservationEnabled, boolean isSessionNoWait, boolean isSessionBatch, boolean isSessionCacheEnabled,     
			int resultCodeOnOverload, int recordReservationLimit, boolean slrOnSnr) throws IOException {
		
		write(properties);
		
		miscellaneousConfigurationImpl.readConfiguration();
		
		assertEquals(isRAREnabled, miscellaneousConfigurationImpl.getRAREnabled());
		assertEquals(addOnStartRAREnabled, miscellaneousConfigurationImpl.getAddOnStartRAREnabled());
		assertEquals(addOnExpiryRAREnabled, miscellaneousConfigurationImpl.getAddOnExpiryRAREnabled());
		assertEquals(addOnReservationEnabled, miscellaneousConfigurationImpl.getAddOnReservationEnabled());
		assertEquals(isSessionNoWait, miscellaneousConfigurationImpl.getSessionNoWait());
		assertEquals(isSessionBatch, miscellaneousConfigurationImpl.getSessionBatch());
		assertEquals(isSessionCacheEnabled, miscellaneousConfigurationImpl.isSessionCacheEnabled());
		assertEquals(resultCodeOnOverload, miscellaneousConfigurationImpl.getResultCodeOnOverload());
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		assertEquals(slrOnSnr, miscellaneousConfigurationImpl.getSLREnabledOnSNR());
		
		
	}

	public Object[][] dataProvider_For_Test_Parameters_Must_Take_DefaultValue_When_ReConfiguredValues_Are_Undefined_Or_There_Is_Only_Whitespace() {
		
		return new Object[][] {
				//ALL values undefined
				{

							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = false\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=false\n"
							,

							"RAR-Enabled =\n" +
							"AddOn-Start-RAR-Enabled =\n" +
							"AddOn-Expiry-RAR-Enabled =\n" +
							"addOn.Reservation =\n" +
							"session-nowait =\n" +
							"session-batch =\n" +
							"Overload-Result-Code =\n" +
							"record-Reservation-Limit =\n"+
									"sy.slronsnr=\n"
							, true, true, true, false, true, true, true, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
				//ALL values are white space
				{

							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = true\n" +
							"session.cache-enabled = false\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=true\n"
							,
							"RAR-Enabled =   \n" +
							"AddOn-Start-RAR-Enabled =   \n" +
							"AddOn-Expiry-RAR-Enabled =   \n" +
							"addOn.Reservation =   \n" +
							"session-nowait =   \n" +
							"session-batch =  \n" +
							"session.cache-enabled =\n" +
							"Overload-Result-Code =  \n" +
							"record-Reservation-Limit =  \n"+
									"sy.slronsnr=   \n"
							, true, true, true, false, true, true, true, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=false\n"
							,
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = false\n" +
							"AddOn-Expiry-RAR-Enabled =\n" +
							"addOn.Reservation =\n" +
							"session-nowait =\n" +
							"session-batch =\n" +
							"Overload-Result-Code =\n" +
							"record-Reservation-Limit =\n"+
									"sy.slronsnr= \n"
							, false, false, true, false, true, true, true, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=TRUE\n"
							,
							"RAR-Enabled = \n" +
							"AddOn-Start-RAR-Enabled = \n" +
							"AddOn-Expiry-RAR-Enabled =\n" +
							"addOn.Reservation =\n" +
							"session-nowait =\n" +
							"session-batch =\n" +
							"Overload-Result-Code = 5000\n" +
							"record-Reservation-Limit =\n"+
									"sy.slronsnr=                \n"
							,  true, true, true, false, true, true, true, 5000, 6000, false
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=FALSE\n"
							,
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = false\n" +
							"AddOn-Expiry-RAR-Enabled =\n" +
							"addOn.Reservation =\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"Overload-Result-Code = \n" +
							"record-Reservation-Limit =\n"+
									"sy.slronsnr=      \n"
							, false, false, true, false, false, false, true, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
		};
	}
	
	@Test 
	@Parameters(method="dataProvider_For_Test_Parameters_Must_Take_DefaultValue_When_ReConfiguredValues_Are_Undefined_Or_There_Is_Only_Whitespace")
	public void test_Parameters_Must_Take_DefaultValue_When_ReConfiguredValues_Are_Undefined_Or_There_Is_Only_Whitespace(String reader,
			String readerOnReload, boolean isRAREnabled, boolean addOnStartRAREnabled,
			boolean addOnExpiryRAREnabled, boolean addOnReservationEnabled, boolean isSessionNoWait, boolean isSessionBatch, boolean isSessionCacheEnabled,     
			int resultCodeOnOverload, int recordReservationLimit, boolean slrOnSnr) throws IOException {
		
		write(reader);
		miscellaneousConfigurationImpl.readConfiguration();
		reOpenFile();
		write(readerOnReload);
		miscellaneousConfigurationImpl.reloadConfiguration();
		
		assertEquals(isRAREnabled, miscellaneousConfigurationImpl.getRAREnabled());
		assertEquals(addOnStartRAREnabled, miscellaneousConfigurationImpl.getAddOnStartRAREnabled());
		assertEquals(addOnExpiryRAREnabled, miscellaneousConfigurationImpl.getAddOnExpiryRAREnabled());
		assertEquals(addOnReservationEnabled, miscellaneousConfigurationImpl.getAddOnReservationEnabled());
		assertEquals(isSessionNoWait, miscellaneousConfigurationImpl.getSessionNoWait());
		assertEquals(isSessionBatch, miscellaneousConfigurationImpl.getSessionBatch());
		assertEquals(isSessionCacheEnabled, miscellaneousConfigurationImpl.isSessionCacheEnabled());
		assertEquals(resultCodeOnOverload, miscellaneousConfigurationImpl.getResultCodeOnOverload());
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		assertEquals(slrOnSnr, miscellaneousConfigurationImpl.getSLREnabledOnSNR());
		
	}

	public Object[][] dataProvider_For_Test_Parameters_Must_Take_DefaultValue_When_ConfiguredValues_Are_Invalid() {
	
		return new Object[][] { 
				
				{


							"RAR-Enabled =   @@#$@ \n" +
							"AddOn-Start-RAR-Enabled = @#@$@  \n" +
							"AddOn-Expiry-RAR-Enabled =  @32@ \n" +
							"addOn.Reservation =   @$#@\n" +
							"session-nowait =   @!@#@\n" +
							"session-batch =  @@@#\n" +
							"Overload-Result-Code =  @!#\n" +
							"record-Reservation-Limit = @@ \n"+
									"sy.slronsnr=xdds"
							, true, true, true, false, true, true, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
				{

							"RAR-Enabled =   F lse \n" +
							"AddOn-Start-RAR-Enabled = tr ue  \n" +
							"AddOn-Expiry-RAR-Enabled =  als \n" +
							"addOn.Reservation =   Fals e" +
							"session-nowait =   tr e" +
							"session-batch =  fa lse" +
							"Overload-Result-Code =  av" +
							"record-Reservation-Limit = abc \n"+
									"sy.slronsnr=disabled"
							, true, true, true, false, true, true, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				
				},
				{

							"RAR-Enabled =   true" +
							"AddOn-Start-RAR-Enabled = @#@$@  \n" +
							"AddOn-Expiry-RAR-Enabled =  @32@ \n" +
							"addOn.Reservation =   @$#@\n" +
							"session-nowait =   @!@#@\n" +
							"session-batch =  @@@#\n" +
							"Overload-Result-Code =  @!#\n" +
							"record-Reservation-Limit = @@ \n"+
									"sy.slronsnr=enabled"
							, true, true, true, false, true, true, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
				{

							"RAR-Enabled =   @@#$@ \n" +
							"AddOn-Start-RAR-Enabled = false  \n" +
							"AddOn-Expiry-RAR-Enabled =  false \n" +
							"addOn.Reservation =   @$#@\n" +
							"session-nowait =   @!@#@\n" +
							"session-batch =  @@@#\n" +
							"Overload-Result-Code =  @!#\n" +
							"record-Reservation-Limit = @@ \n"+
									"sy.slronsnr=@@@@"
							, true, false, false, false, true, true, ResultCode.DIAMETER_TOO_BUSY.code, 6000, false
				},
				{
							"Query-Timeout =  4 \n" +
							"RAR-Enabled =   @$#" +
							"AddOn-Start-RAR-Enabled = true  \n" +
							"AddOn-Expiry-RAR-Enabled =  @32@ \n" +
							"addOn.Reservation =   true\n" +
							"session-nowait =   @!@#@\n" +
							"session-batch =  true\n" +
							"Overload-Result-Code =  @!#\n" +
							"record-Reservation-Limit = 2000 \n"+
									"sy.slronsnr=####"
							, true, true, true, true, true, true, ResultCode.DIAMETER_TOO_BUSY.code, 2000, false
				},
		};
				
	}
	
	@Test 
	@Parameters(method="dataProvider_For_Test_Parameters_Must_Take_DefaultValue_When_ConfiguredValues_Are_Invalid")
	public void test_Parameters_Must_Take_DefaultValue_When_ConfiguredValues_Are_Invalid(String properties, boolean isRAREnabled, boolean addOnStartRAREnabled,
			boolean addOnExpiryRAREnabled, boolean addOnReservationEnabled, boolean isSessionNoWait, boolean isSessionBatch,        
			int resultCodeOnOverload, int recordReservationLimit, boolean slrOnSnr) throws IOException {
		
		write(properties);
		
		miscellaneousConfigurationImpl.readConfiguration();
		
		assertEquals(isRAREnabled, miscellaneousConfigurationImpl.getRAREnabled());
		assertEquals(addOnStartRAREnabled, miscellaneousConfigurationImpl.getAddOnStartRAREnabled());
		assertEquals(addOnExpiryRAREnabled, miscellaneousConfigurationImpl.getAddOnExpiryRAREnabled());
		assertEquals(addOnReservationEnabled, miscellaneousConfigurationImpl.getAddOnReservationEnabled());
		assertEquals(isSessionNoWait, miscellaneousConfigurationImpl.getSessionNoWait());
		assertEquals(isSessionBatch, miscellaneousConfigurationImpl.getSessionBatch());
		assertEquals(resultCodeOnOverload, miscellaneousConfigurationImpl.getResultCodeOnOverload());
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		assertEquals(slrOnSnr, miscellaneousConfigurationImpl.getSLREnabledOnSNR());
		
		
	}

	public Object[][] dataProvider_For_Test_Parameters_Must_Take_PreviousValue_When_ReconfiguredValues_Are_Invalid() {
		
		return new Object[][] { 
				
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled =false\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=false"
							,


							"RAR-Enabled =   @@#$@ \n" +
							"AddOn-Start-RAR-Enabled = @#@$@  \n" +
							"AddOn-Expiry-RAR-Enabled =  @32@ \n" +
							"addOn.Reservation =   @$#@\n" +
							"session-nowait =   @!@#@\n" +
							"session-batch =  @@@#\n" +
							"session.cache-enabled =false@X#$\n" +
							"Overload-Result-Code =  @!#\n" +
							"record-Reservation-Limit = @@ \n"+
									"sy.slronsnr=xxx"
							, false, true, false, true, false, false, false, 3001, 2000, false
				},
				{


							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = true\n" +
							"Overload-Result-Code =3002\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=true"
							,

							"RAR-Enabled =   F lse \n" +
							"AddOn-Start-RAR-Enabled = tr ue  \n" +
							"AddOn-Expiry-RAR-Enabled =  als \n" +
							"addOn.Reservation =   Fals e\n" +
							"session-nowait =   tr e\n" +
							"session-batch =  fa lse\n" +
							"Overload-Result-Code =  av\n" +
							"session.cache-enabled =   se@X#$\n" +
							"record-Reservation-Limit = abc \n"+
									"sy.slronsnr=fale"
							, false, true, false, true, false, false, true, 3002, 2000, true
				
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = true\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=false"
							,
							"RAR-Enabled =   true\n" +
							"AddOn-Start-RAR-Enabled = @#@$@  \n" +
							"AddOn-Expiry-RAR-Enabled =  @32@ \n" +
							"addOn.Reservation =   @$#@\n" +
							"session-nowait =   @!@#@\n" +
							"session-batch =  @@@#\n" +
							"session.cache-enabled = @true\n" +
							"Overload-Result-Code =  @!#\n" +
							"record-Reservation-Limit = @@ \n"+
									"sy.slronsnr=tre"
							, true, true, false, true, false, false, true, 3001, 2000, false
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = false\n" +
							"Overload-Result-Code =5005\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=true"
							,
							"RAR-Enabled =   @@#$@ \n" +
							"AddOn-Start-RAR-Enabled = false  \n" +
							"AddOn-Expiry-RAR-Enabled =  false \n" +
							"addOn.Reservation =   @$#@\n" +
							"session-nowait =   @!@#@\n" +
							"session-batch =  @@@#\n" +
							"session.cache-enabled = @X\n" +
							"Overload-Result-Code =  @!#\n" +
							"record-Reservation-Limit = @@ \n"+
									"sy.slronsnr=Talse"
							, false, false, false, true, false, false, false, 5005, 2000, true
				},
				{
							"RAR-Enabled = false\n" +
							"AddOn-Start-RAR-Enabled = true\n" +
							"AddOn-Expiry-RAR-Enabled = false\n" +
							"addOn.Reservation =true\n" +
							"session-nowait =false\n" +
							"session-batch =false\n" +
							"session.cache-enabled = true\n" +
							"Overload-Result-Code =3001\n" +
							"record-Reservation-Limit =2000\n"+
									"sy.slronsnr=true"
							,
							"RAR-Enabled =   @$#\n" +
							"AddOn-Start-RAR-Enabled = true  \n" +
							"AddOn-Expiry-RAR-Enabled =  @32@ \n" +
							"addOn.Reservation =   true\n" +
							"session-nowait =   @!@#@\n" +
							"session-batch =  true\n" +
							"session.cache-enabled =fal se\n" +
							"Overload-Result-Code =  @!#\n" +
							"record-Reservation-Limit = 2000 \n"+
									"sy.slronsnr=fa "
							, false, true, false, true, false, true, true, 3001, 2000, true
				},
		};
				
	}
	
	@Test 
	@Parameters(method="dataProvider_For_Test_Parameters_Must_Take_PreviousValue_When_ReconfiguredValues_Are_Invalid")
	public void test_Parameters_Must_Take_PreviousValue_When_ReconfiguredValues_Are_Invalid(String reader,
																							String onReload, boolean isRAREnabled, boolean addOnStartRAREnabled,
			boolean addOnExpiryRAREnabled, boolean addOnReservationEnabled, boolean isSessionNoWait, boolean isSessionBatch, boolean isSessionCacheEnabled,       
			int resultCodeOnOverload, int recordReservationLimit, boolean slrOnSNR) throws IOException {
		
		write(reader);
		miscellaneousConfigurationImpl.readConfiguration();

		reOpenFile();
		write(onReload);
		miscellaneousConfigurationImpl.reloadConfiguration();
		
		assertEquals(isRAREnabled, miscellaneousConfigurationImpl.getRAREnabled());
		assertEquals(addOnStartRAREnabled, miscellaneousConfigurationImpl.getAddOnStartRAREnabled());
		assertEquals(addOnExpiryRAREnabled, miscellaneousConfigurationImpl.getAddOnExpiryRAREnabled());
		assertEquals(addOnReservationEnabled, miscellaneousConfigurationImpl.getAddOnReservationEnabled());
		assertEquals(isSessionNoWait, miscellaneousConfigurationImpl.getSessionNoWait());
		assertEquals(isSessionBatch, miscellaneousConfigurationImpl.getSessionBatch());
		assertEquals(isSessionCacheEnabled, miscellaneousConfigurationImpl.isSessionCacheEnabled());
		assertEquals(resultCodeOnOverload, miscellaneousConfigurationImpl.getResultCodeOnOverload());
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		assertEquals(slrOnSNR, miscellaneousConfigurationImpl.getSLREnabledOnSNR());
		
	}

	public Object[][] dataProvider_For_Test_RecordReservationLimitParameter_Must_Take_MaxValue_When_ConfiguredValue_Is_Bigger_Then_MaxValue() {
		
		return new Object[][] {
				{

							"record-Reservation-Limit = 60000 \n"
							, 50000
				},
				{
							"record-Reservation-Limit = 50001 \n"
							, 50000
				},
				{
							"record-Reservation-Limit = 111111 \n"
							, 50000
				},
				{
							"record-Reservation-Limit = 124345 \n"
							, 50000
				},
				{
							"record-Reservation-Limit = 999999 \n"
							, 50000
				},
		};
	}
	
	@Test
	@Parameters(method="dataProvider_For_Test_RecordReservationLimitParameter_Must_Take_MaxValue_When_ConfiguredValue_Is_Bigger_Then_MaxValue")
	public void test_RecordReservationLimitParameter_Must_Take_MaxValue_When_ConfiguredValue_Is_Bigger_Then_MaxValue(String properties, int recordReservationLimit) throws IOException {
		
		write(properties);
		miscellaneousConfigurationImpl.readConfiguration();
		
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		
	}

	public Object[][] dataProvider_For_Test_RecordReservationLimitParameter_Must_Take_PreviousValue_When_ReConfiguredValue_Is_Bigger_Then_MaxValue() {
		
		return new Object[][] {
				{
							"record-Reservation-Limit = 7000 \n"
							,
							"record-Reservation-Limit = 77000 \n"
							, 7000
				
				},
				{
							"record-Reservation-Limit = 7000 \n"
							,
							"record-Reservation-Limit = 50001 \n"
							, 7000
				},
				{
							"record-Reservation-Limit = 5000 \n"
							,
							"record-Reservation-Limit = 111111 \n"
							, 5000
				},
				{
							"record-Reservation-Limit = 40000 \n"
							,
							"record-Reservation-Limit = 124345 \n"
							, 40000
				},
				{
							"record-Reservation-Limit = 7000 \n"
							,
							"record-Reservation-Limit = 999999 \n"
							, 7000
				},
		};
	}
	
	@Test
	@Parameters(method="dataProvider_For_Test_RecordReservationLimitParameter_Must_Take_PreviousValue_When_ReConfiguredValue_Is_Bigger_Then_MaxValue")
	public void test_RecordReservationLimitParameter_Must_Take_PreviousValue_When_ReConfiguredValue_Is_Bigger_Then_MaxValue(String properties,
																															String onReload, int recordReservationLimit) throws IOException {
		
		write(properties);
		miscellaneousConfigurationImpl.readConfiguration();

		reOpenFile();
		write(onReload);
		miscellaneousConfigurationImpl.reloadConfiguration();
		
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		
	}

	public Object[][] dataProvider_For_Test_RecordReservationLimitParameter_Must_Take_MinValue_When_ConfiguredValue_Is_Lesser_Then_MinValue() {
		
		return new Object[][] {
				{

							"record-Reservation-Limit = -1111 \n"
							, 1000
				},
				{
							"record-Reservation-Limit = 10 \n"
							, 1000
				},
				{
							"record-Reservation-Limit = 999 \n"
							, 1000
				},
				{
							"record-Reservation-Limit = 542 \n"
							, 1000
				},
				{
							"record-Reservation-Limit = -6324 \n"
							, 1000
				},
		};
	}
	
	@Test
	@Parameters(method="dataProvider_For_Test_RecordReservationLimitParameter_Must_Take_MinValue_When_ConfiguredValue_Is_Lesser_Then_MinValue")
	public void test_RecordReservationLimitParameter_Must_Take_MinValue_When_ConfiguredValue_Is_Lesser_Then_MinValue(String properties, int recordReservationLimit) throws IOException {

		write(properties);
		miscellaneousConfigurationImpl.readConfiguration();
		
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		
	}


	
	public Object[][] dataProvider_For_Test_RecordReservationLimitParameter_Must_Take_PreviousValue_When_ReConfiguredValue_Is_Lesser_Then_MinValue() {
		
		return new Object[][] {
				{
							"record-Reservation-Limit = 7000 \n",

							"record-Reservation-Limit = -1111 \n"
							, 7000
				},
				{
							"record-Reservation-Limit = 10000 \n"
							,
							"record-Reservation-Limit = 10 \n"
							, 10000
				},
				{
							"record-Reservation-Limit = 2000 \n"
							,
							"record-Reservation-Limit = 999 \n"
							, 2000
				},
				{
							"record-Reservation-Limit = 1000 \n"
							,
							"record-Reservation-Limit = 542 \n"
							, 1000
				},
				{
							"record-Reservation-Limit = 45000 \n"
							,
							"record-Reservation-Limit = -6324 \n"
							, 45000
				},
		};
	}
	
	@Test
	@Parameters(method="dataProvider_For_Test_RecordReservationLimitParameter_Must_Take_PreviousValue_When_ReConfiguredValue_Is_Lesser_Then_MinValue")
	public void test_RecordReservationLimitParameter_Must_Take_PreviousValue_When_ReConfiguredValue_Is_Lesser_Then_MinValue(String properties, String onReload, int recordReservationLimit) throws IOException {

		write(properties);
		miscellaneousConfigurationImpl.readConfiguration();

		reOpenFile();

		write(onReload);
		miscellaneousConfigurationImpl.reloadConfiguration();
		
		assertEquals(recordReservationLimit, miscellaneousConfigurationImpl.getRecordReservationLimit());
		
	}
	
	

	
	
	
	

	public Object[][] dataProvider_For_Test_ResutlCodeOnOverload_Must_Take_PreviousValue_When_ReConfiguredValue_Is_Not_a_Valid_Result_Code() {
		
		return new Object[][] {
				{
							OVERLOAD_RESULT_CODE  + " = 2000 \n"
							,
							OVERLOAD_RESULT_CODE  + " = -01 \n"
							, 2000
				},
				{
							OVERLOAD_RESULT_CODE  + " = 5999 \n"
							,
							OVERLOAD_RESULT_CODE  + " = 999 \n"
							, 5999
				},
				{
							OVERLOAD_RESULT_CODE  + " = 1000 \n"
							,
							OVERLOAD_RESULT_CODE  + " = 6000 \n"
							, 1000
				},
				{
							OVERLOAD_RESULT_CODE  + " = 0 \n"
							,
							OVERLOAD_RESULT_CODE  + " = 9998 \n"
							, 0
				},
				{
							OVERLOAD_RESULT_CODE  + " = 3000 \n"
							,
							OVERLOAD_RESULT_CODE  + " = 998 \n"
							, 3000
				},
		};
	}
	
	@Test
	@Parameters(method="dataProvider_For_Test_ResutlCodeOnOverload_Must_Take_PreviousValue_When_ReConfiguredValue_Is_Not_a_Valid_Result_Code")
	public void test_ResutlCodeOnOverload_Must_Take_PreviousValue_When_ReConfiguredValue_Is_Not_a_Valid_Result_Code(String reader, String onReload, int resultCodeOnOverload) throws IOException {
		
		write(reader);
		miscellaneousConfigurationImpl.readConfiguration();

		reOpenFile();

		write(onReload);
		miscellaneousConfigurationImpl.reloadConfiguration();
		
		assertEquals(resultCodeOnOverload, miscellaneousConfigurationImpl.getResultCodeOnOverload());
	}
	
}
