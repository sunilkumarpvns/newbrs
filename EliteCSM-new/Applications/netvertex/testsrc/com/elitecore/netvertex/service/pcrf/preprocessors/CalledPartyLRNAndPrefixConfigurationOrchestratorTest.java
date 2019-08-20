package com.elitecore.netvertex.service.pcrf.preprocessors;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.locationmanagement.data.NetworkConfiguration;
import com.elitecore.netvertex.core.lrn.data.LRNConfiguration;
import com.elitecore.netvertex.core.prefix.DummyLRNrepository;
import com.elitecore.netvertex.core.prefix.DummyPrefixRepository;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.prefix.conf.PrefixConfiguration;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(HierarchicalContextRunner.class)
public class CalledPartyLRNAndPrefixConfigurationOrchestratorTest {
    private DummyNetvertexServerContextImpl serverContext;
    private DummyPrefixRepository prefixRepository;
    private DummyLRNrepository lrNrepository;
    private CalledPartyLRNAndPrefixConfigurationOrchestrator configurationOrchestrator;
    private PCRFRequest request;
    private PCRFResponse response;

    @Before
    public void setUp () {
        this.serverContext = new DummyNetvertexServerContextImpl();
        this.prefixRepository = serverContext.getPrefixRepository();
        this.lrNrepository = serverContext.getLRNConfigurationRepository();
        this.configurationOrchestrator = new CalledPartyLRNAndPrefixConfigurationOrchestrator(serverContext);
        this.request = new PCRFRequestImpl();
        this.response = new PCRFResponseImpl();
    }


    public class LRNisAppliedWhen {
        private LRNConfiguration lrnConfiguration;
        private NetworkConfiguration networkConfiguration;

        @Before
        public void setUp() {
            lrnConfiguration = lrNrepository.setLRNconfiguration("321");
            networkConfiguration = lrnConfiguration.getNetworkConfiguration();
        }

        @Test
        public void RequestWithLRNAndLRNConfigured() {
            request.setAttribute(PCRFKeyConstants.LRN.getVal(), lrnConfiguration.getLrn());
            configurationOrchestrator.process(request, response);
            assertEquals(networkConfiguration.getCountryName(), response.getAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal()));
            assertEquals(networkConfiguration.getOperator(), response.getAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal()));
            assertEquals(networkConfiguration.getNetworkName(), response.getAttribute(PCRFKeyConstants.CS_CALLED_NETWORK_NAME.getVal()));
        }

        public class LRNAndPrefixBothConfigured {
            private PrefixConfiguration prefixConfiguration;

            @Before
            public void setUp() {
                prefixConfiguration = prefixRepository.setPrefixConfiguration("91");
            }

            @Test
            public void AttributeFromLRNShouldBeSet() {
                request.setAttribute(PCRFKeyConstants.LRN.getVal(), lrnConfiguration.getLrn());
                request.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(), prefixConfiguration.getPrefix()+"265");

                configurationOrchestrator.process(request, response);
                assertEquals(networkConfiguration.getCountryName(), response.getAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal()));
                assertEquals(networkConfiguration.getOperator(), response.getAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal()));
                assertEquals(networkConfiguration.getNetworkName(), response.getAttribute(PCRFKeyConstants.CS_CALLED_NETWORK_NAME.getVal()));
            }

        }
    }

    public class LRNisNotAppliedWhen {
        private LRNConfiguration lrnConfiguration;
        private NetworkConfiguration networkConfiguration;
        public void setUp() {
            lrnConfiguration = lrNrepository.setLRNconfiguration("321");
            networkConfiguration = lrnConfiguration.getNetworkConfiguration();
        }

        @Test
        public void RequestNotReceivedWithLRN() {
            request.setAttribute(PCRFKeyConstants.LRN.getVal(), null);
            configurationOrchestrator.process(request, response);
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal()));
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal()));
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_NETWORK_NAME.getVal()));
        }

        @Test
        public void RequestReceivedWithLRNAndLRNNotConfigured() {
            request.setAttribute(PCRFKeyConstants.LRN.getVal(), "321");
            configurationOrchestrator.process(request, response);
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal()));
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal()));
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_NETWORK_NAME.getVal()));
        }
    }

    public class PrefixIsAppliedWhen {
        private PrefixConfiguration prefixConfiguration;

        @Before
        public void setUp() {
            prefixConfiguration = prefixRepository.setPrefixConfiguration("91");
        }

        @Test
        public void RequestWithCalledPartyAddressAndPrefixConfigured() {
            request.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(), prefixConfiguration.getPrefix() + "12335");
            configurationOrchestrator.process(request, response);
            assertEquals(prefixConfiguration.getCountry(), response.getAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal()));
            assertEquals(prefixConfiguration.getOperator(), response.getAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal()));
            assertEquals(prefixConfiguration.getNetworkName(), response.getAttribute(PCRFKeyConstants.CS_CALLED_NETWORK_NAME.getVal()));
        }
    }

    public class PrefixIsNotAppliedWhen {
        private PrefixConfiguration prefixConfiguration;

        @Before
        public void setUp() {
            prefixConfiguration = prefixRepository.setPrefixConfiguration("91");
        }

        @Test
        public void RequestNotReceivedWithCalledPartyAddress() {
            request.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(), null);
            configurationOrchestrator.process(request, response);
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal()));
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal()));
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_NETWORK_NAME.getVal()));
        }

        @Test
        public void RequestReceivedWithCalledPartyAddressAndPrefixNotConfigured() {
            request.setAttribute(PCRFKeyConstants.CS_CALLED_STATION_ID.getVal(), "12335");
            configurationOrchestrator.process(request, response);
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_COUNTRY.getVal()));
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_OPERATOR.getVal()));
            assertNull(response.getAttribute(PCRFKeyConstants.CS_CALLED_NETWORK_NAME.getVal()));
        }
    }
}
