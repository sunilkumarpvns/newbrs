package com.elitecore.netvertex.core.driver.cdr;

import com.elitecore.core.driverx.cdr.deprecated.PlainTextResolver;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

@RunWith(HierarchicalContextRunner.class)
public class PCRFKeyBaseFileParameterResolverTest {

    private PlainTextResolver<ValueProviderExtImpl> filePrefixResolver = new PlainTextResolver<>("FilePrefix");
    private PlainTextResolver<ValueProviderExtImpl> folderNamePrefixResolver = new PlainTextResolver<>("FolderName");
    private PCRFKeyBaseFileParameterResolver pcrfKeyBaseFileParameterResolver = new PCRFKeyBaseFileParameterResolver(filePrefixResolver, folderNamePrefixResolver);

    @Test
    public void getFilePrefix() {
        assertEquals("FilePrefix", pcrfKeyBaseFileParameterResolver.getFilePrefix(new ValueProviderExtImpl(new PCRFRequestImpl(), new PCRFResponseImpl())));
    }

    @Test
    public void getFolderName() {
        assertEquals("FolderName", pcrfKeyBaseFileParameterResolver.getFolderName(new ValueProviderExtImpl(new PCRFRequestImpl(), new PCRFResponseImpl())));
    }


    public class create {
        private PCRFRequest pcrfRequest = new PCRFRequestImpl();
        private PCRFResponse pcrfResponse = new PCRFResponseImpl();

        private ValueProviderExtImpl valueProvider = new ValueProviderExtImpl(pcrfRequest, pcrfResponse);


        @Test
        public void createDynamicResolverWhenParameterSpecified() {
            PCRFKeyBaseFileParameterResolver test = PCRFKeyBaseFileParameterResolver.create("test1", "test");

            pcrfRequest.setAttribute("test", "124");
            pcrfRequest.setAttribute("test1", "124f");
            assertEquals("124",test.getFolderName(valueProvider));
            assertEquals("124f",test.getFilePrefix(valueProvider));
        }

        @Test
        public void createNullResolverWhenKeyIsNull() {
            PCRFKeyBaseFileParameterResolver test = PCRFKeyBaseFileParameterResolver.create(null, null);

            pcrfRequest.setAttribute("test", "124");
            assertNull(test.getFolderName(valueProvider));
        }

    }
}