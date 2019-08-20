package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.serverx.manager.cache.CacheConstants;
import com.elitecore.core.serverx.manager.cache.CacheDetail;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
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

import static com.elitecore.netvertex.gateway.diameter.utility.ResultCodeMappingTestSuite.checkAllDefaultResultCodes;
import static com.elitecore.netvertex.gateway.diameter.utility.ResultCodeMappingTestSuite.hasSameResultCode;
import static com.elitecore.netvertex.gateway.diameter.utility.ResultCodeMappingTestSuite.hasSameResultCodeAndVendorId;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertSame;
import static org.junit.Assert.assertThat;

@RunWith(HierarchicalContextRunner.class)
public class ResultCodeMappingReloadTest {

    public static final String TEST_RESOURCE_PATH = "testsrc/resources/resultcodemappingscenarios/";
    private ResultCodeMapping resultCodeMapping;

    @Rule
    public TemporaryFolder temporaryFolder = new TemporaryFolder();

    private File resultCodeMappingFile;

    @Mock
    private NetVertexServerContext context;

    {
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

    public class ReloadFail {

        private File scenarioFile = new File(TEST_RESOURCE_PATH + "success_1RecordWithFullDetail.xml");

        @Before
        public void setUp() throws Exception {
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);
        }

        @Test
        public void WhenFileNotFoundOnReload_ItShouldPreserveOldMapping() {
            resultCodeMappingFile.delete();

            resultCodeMapping.reloadCache();

            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("key1", 0);
            assertThat(resultCodeAVP, hasSameResultCode(2002));

            checkAllDefaultResultCodes(resultCodeMapping);
        }

        @Test
        public void ShouldGiveCacheStatusFail() {
            resultCodeMappingFile.delete();
            CacheDetail cacheDetail = resultCodeMapping.reloadCache();
            assertSame(CacheConstants.FAIL, cacheDetail.getResultCode());
            assertEquals("RESULT-CODE-MAPPING", cacheDetail.getName());
        }
    }

    public class ReloadSuccess {
        private File scenarioFile = new File(TEST_RESOURCE_PATH + "success_1RecordWithFullDetail.xml");

        @Before
        public void setUp() throws Exception {
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);
            resultCodeMapping.init(context);
        }

        @Test
        public void reloadShouldUpdateResultCodeMapping() throws IOException {
            File scenarioFile = new File(TEST_RESOURCE_PATH + "reload_success.xml");
            FileUtils.copyFile(scenarioFile, resultCodeMappingFile);

            CacheDetail cacheDetail = resultCodeMapping.reloadCache();
            assertEquals(CacheConstants.SUCCESS, cacheDetail.getResultCode());

            IDiameterAVP resultCodeAVP = resultCodeMapping.getResultCodeAVP("reloadKey1", 0);
            assertThat(resultCodeAVP, hasSameResultCode(202));

            resultCodeAVP = resultCodeMapping.getResultCodeAVP("reloadKey1", 5);
            assertThat(resultCodeAVP, hasSameResultCodeAndVendorId(201, 12));

        }
    }

}
