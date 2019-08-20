package com.elitecore.netvertex.gateway.diameter.utility;


import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.TemporaryFolder;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.io.File;
import java.io.IOException;

import static com.elitecore.netvertex.gateway.diameter.utility.ResultCodeMappingTestSuite.*;
import static org.junit.Assert.*;

@RunWith(HierarchicalContextRunner.class)
public class ResultCodeMappingInitTest {

    private static final String TEST_RESOURCE_PATH = "testsrc/resources/resultcodemappingscenarios/";
    public static final int DEFAULT_RESULCODES_SIZE = 10;
    private ResultCodeMapping resultCodeMapping;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File resultCodeMappingFile;

    @Mock
    private NetVertexServerContext context;

    static {
        DummyDiameterDictionary.getInstance();
    }

    @Before
    public void setUp() throws IOException {
        MockitoAnnotations.initMocks(this);
        this.temporaryFolder.create();
        temporaryFolder.newFolder("system", "diameter");
        this.resultCodeMappingFile = this.temporaryFolder.newFile("system/diameter/result-code-mapping.xml");
        this.resultCodeMapping = ResultCodeMapping.getInstance();
        Mockito.when(context.getServerHome()).thenReturn(temporaryFolder.getRoot().getAbsolutePath());
    }


    public class InitShouldInitializeDefaultEntryOnlyWhenInitializeFailedExceptionIsThrown {

        @Test(expected = InitializationFailedException.class)
        public void WhenConfigurationFileNotExist() throws InitializationFailedException {
            resultCodeMappingFile.delete();
            try {
                resultCodeMapping.init(context);
            } catch (InitializationFailedException e) {
                checkAllDefaultResultCodes(resultCodeMapping);
                throw e;
            }
        }

        @Test(expected = InitializationFailedException.class)
        public void blankFile() throws InitializationFailedException {
            try {
                resultCodeMapping.init(context);
            } catch (InitializationFailedException e) {
                checkAllDefaultResultCodes(resultCodeMapping);
                throw e;
            }
        }

        @Test(expected = InitializationFailedException.class)
        public void parentTagIsNotExist() throws Exception {
            File scenarioFile = new File("testsrc/resources/resultcodemappingscenarios/parentTagNotExist.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            try {
                resultCodeMapping.init(context);
            } catch (InitializationFailedException e) {
                checkAllDefaultResultCodes(resultCodeMapping);
                throw e;
            }
        }

        @Test(expected = InitializationFailedException.class)
        public void XmlFormatIsWrong() throws Exception {
            File scenarioFile = new File("testsrc/resources/resultcodemappingscenarios/xmlFormatIsWrong.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            try {
                resultCodeMapping.init(context);
            } catch (InitializationFailedException e) {
                checkAllDefaultResultCodes(resultCodeMapping);
                throw e;
            }
        }


    }



    public class SingleRecordFilePassedWithoutApplicationDetail {

        private File scenarioFile = new File("testsrc/resources/resultcodemappingscenarios/success_1recordWithoutApplication.xml");

        @Before
        public void setUp() throws Exception {
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);
        }

        @Test
        public void GiveDefaultConfiguredMapping() throws Exception {
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 0);
            assertThat(resultCodeAVP, hasSameResultCode(2002));
        }

        @Test
        public void ShouldGiveUNABLETOCOMPLYRESULTCODE_WhenRespectiveMappingNotFound() throws Exception {
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("xyz", 0);
            assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code));
        }

        @Test
        public void defaultMappingShouldBeAdded() {
            checkAllDefaultResultCodes(resultCodeMapping);
        }
    }

    public class SingleRecordFilePassedWithoutVendorDetail {

        private File scenarioFile = new File(TEST_RESOURCE_PATH + "success_1RecordWithoutVendorDetail.xml");

        @Before
        public void setUp() throws Exception {
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);
        }

        @Test
        public void GiveDefaultConfiguredMapping_WhenApplicationIdEntryNotFound() throws Exception {
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 0);
            assertThat(resultCodeAVP, hasSameResultCode(2002));
        }

        @Test
        public void GiveApplicationSpecificResultCodeMapping_WhenConfiguredAppId() throws Exception {
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 4);
            assertThat(resultCodeAVP, hasSameResultCode(1001));
        }

        @Test
        public void Give_UNABLETOCOMPLYRESULTCODE_WhenRespectiveMappingNotFound() throws Exception {
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("xyz", 0);
            assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code));
        }

        @Test
        public void defaultMappingShouldBeAdded() {
            checkAllDefaultResultCodes(resultCodeMapping);
        }
    }

    public class SingleRecordFilePassedWithFullDetail {

        private File scenarioFile = new File(TEST_RESOURCE_PATH + "success_1RecordWithFullDetail.xml");

        @Before
        public void setUp() throws Exception {
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);
        }

        @Test
        public void GiveDefaultConfiguredMapping_WhenApplicationIdEntryNotFound() throws Exception {
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 0);
            assertThat(resultCodeAVP, hasSameResultCode(2002));
        }

        @Test
        public void GiveExperimentalResultCodeMapping_WhenConfiguredAppIdPassed() throws Exception {
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 4);
            assertThat(resultCodeAVP, hasSameResultCodeAndVendorId(2001, 11));
        }

        @Test
        public void Give_UNABLETOCOMPLYRESULTCODE_WhenRespectiveMappingNotFound() throws Exception {
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("xyz", 0);
            assertThat(resultCodeAVP, hasSameResultCode(ResultCode.DIAMETER_UNABLE_TO_COMPLY.code));
        }

        @Test
        public void GiveNewDiameterAVPInEveryCall() throws Exception {
            IDiameterAVP firstCall = resultCodeMapping.getResultCodeAVP("xyz", 0);
            IDiameterAVP secondCall = resultCodeMapping.getResultCodeAVP("xyz", 0);
            assertNotSame(firstCall, secondCall);
        }

        @Test
        public void defaultMappingShouldBeAdded() {
            checkAllDefaultResultCodes(resultCodeMapping);
        }
    }

    public class ValidationCheck {



        /**
         * Default ResultCode: 10
         * Configured Entry: 3
         * - Skipped Entry: 2
         *
         * Total 11 Should be Initialized
         *
         */
        @Test
        public void resultCodeEntryShouldSkipped_When_pcrfKeyTagIsNotConfiguredOrBlank() throws Exception {
            File scenarioFile = new File(TEST_RESOURCE_PATH + "validation_pcrfkeyAbsent.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);

            assertEquals(11, resultCodeMapping.size());
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 0);
            assertThat(resultCodeAVP, hasSameResultCode(2001));
        }

        /**
         * Default ResultCode: 10
         * Configured Entry: 3
         * - Skipped Entry: 2
         *
         * Total 11 Should be Initialized
         *
         * @throws Exception
         */
        @Test
        public void resultCodeEntryShouldSkipped_When_DiameterResultCodeTagIsNotConfiguredOrZeroConfigured() throws Exception {
            File scenarioFile = new File(TEST_RESOURCE_PATH + "validation_diameterResultCodeAbsent.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);

            assertEquals(11, resultCodeMapping.size());
            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key3", 0);
            assertThat(resultCodeAVP, hasSameResultCode(2003));
        }

        @Test
        public void resultCodeEntryShouldSkippedApplicationEntry_When_ApplicationIdTagIsNotConfigured() throws Exception {
            File scenarioFile = new File(TEST_RESOURCE_PATH + "validation_appIdAbsent.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);

            assertEquals(12, resultCodeMapping.size());

            checkResultCode(resultCodeMapping, "key1", 0, 2001);
            checkResultCode(resultCodeMapping, "key2", 0, 2002);

            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 41);
            assertThat(resultCodeAVP, hasSameResultCodeAndVendorId(3001, 11));
        }

        @Test
        public void resultCodeEntryShouldSkippedApplicationEntry_When_ApplicationResultCodeTagIsNotConfiguredOrZeroConfigured() throws Exception {
            File scenarioFile = new File(TEST_RESOURCE_PATH + "validation_appResultCodeTagAbsent.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);

            assertEquals(13, resultCodeMapping.size());

            checkResultCode(resultCodeMapping, "key1", 0, 2001);
            checkResultCode(resultCodeMapping, "key2", 0, 2002);
            checkResultCode(resultCodeMapping, "key3", 0, 2003);
            checkResultCode(resultCodeMapping, "key2", 42, 2002);
            checkResultCode(resultCodeMapping, "key3", 43, 2003);

            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 41);
            assertThat(resultCodeAVP, hasSameResultCodeAndVendorId(3001, 11));
        }

        @Test
        public void resultCodeEntryShouldSkippedApplicationEntry_When_NonNumericValueProvidedInResultCode() throws Exception {
            File scenarioFile = new File("testsrc/resources/resultcodemappingscenarios/stringValueInResultCode.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);
            assertEquals(DEFAULT_RESULCODES_SIZE, resultCodeMapping.size());
            checkAllDefaultResultCodes(resultCodeMapping);

            checkResultCode(resultCodeMapping, "key1", 0, ResultCode.DIAMETER_UNABLE_TO_COMPLY.code);
            checkResultCode(resultCodeMapping, "key1", 4, ResultCode.DIAMETER_UNABLE_TO_COMPLY.code);
        }

        @Test
        public void GetterShouldGiveApplicationSpecificDiameterResultCode_When_VendorIdTagIsNotConfigured() throws Exception {
            File scenarioFile = new File(TEST_RESOURCE_PATH + "validation_vendoreIdTagAbsent.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);

            System.out.println(resultCodeMapping.toString());

            assertEquals(DEFAULT_RESULCODES_SIZE + 2, resultCodeMapping.size());

            checkResultCode(resultCodeMapping, "key1", 0, 2001);
            checkResultCode(resultCodeMapping, "key2", 0, 2002);
            checkResultCode(resultCodeMapping, "key2", 42, 402);

            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 41);
            assertThat(resultCodeAVP, hasSameResultCodeAndVendorId(3001, 11));
        }

        @Test
        public void GetterShouldGiveApplicationSpecificDiameterResultCode_When_ExperimentResultCodeTagIsNotConfigured() throws Exception {
            File scenarioFile = new File(TEST_RESOURCE_PATH + "validation_experimentResultCodeTagAbsent.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);

            assertEquals(13, resultCodeMapping.size());

            checkResultCode(resultCodeMapping, "key1", 0, 2001);
            checkResultCode(resultCodeMapping, "key2", 0, 2002);
            checkResultCode(resultCodeMapping, "key3", 0, 2003);
            checkResultCode(resultCodeMapping, "key2", 42, 402);
            checkResultCode(resultCodeMapping, "key3", 43, 403);

            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 41);
            assertThat(resultCodeAVP, hasSameResultCodeAndVendorId(3001, 11));
        }


    }

    private void checkResultCode(ResultCodeMapping resultCodeMapping, String key, int appId, int expectedResultCode) {
        IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP(key, appId);
        assertThat(resultCodeAVP, hasSameResultCode(expectedResultCode));
    }
}
