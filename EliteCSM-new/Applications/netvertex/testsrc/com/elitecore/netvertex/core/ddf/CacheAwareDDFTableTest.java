package com.elitecore.netvertex.core.ddf;

import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.spr.AlternateIdentityMapper;
import com.elitecore.corenetvertex.spr.SPRProvider;
import com.elitecore.corenetvertex.spr.SubscriberRepository;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.corenetvertex.spr.ddf.RepositorySelector;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.util.Cache;
import com.elitecore.corenetvertex.util.PartitioningCache;
import com.elitecore.corenetvertex.util.TestableTaskScheduler;
import com.elitecore.netvertex.core.DummyNetvertexServerContextImpl;
import com.elitecore.netvertex.core.conf.impl.DummyMiscellaneousConfiguration;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.EnumSet;
import java.util.UUID;

import static org.junit.Assert.assertSame;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class CacheAwareDDFTableTest {
    private String subscriberIdentityVal = UUID.randomUUID().toString();
    private String alternateIdentityVal = UUID.randomUUID().toString();

    private CacheAwareDDFTable ddfTable;
    private PCRFRequestImpl request;
    private PartitioningCache<String, String> cache;
    private String subscriberId = UUID.randomUUID().toString();
    private String alternateId = UUID.randomUUID().toString();

    @Mock private AlternateIdentityMapper mapper;
    @Mock  SubscriberRepository subscriberRepository;
    @Rule public ExpectedException exception = ExpectedException.none();

    private  DummyMiscellaneousConfiguration miscConf;
    private Cache<String, SPRInfo> primaryCache;
    private RepositorySelector repositorySelector;

    @Before
    public void setUp() throws OperationFailedException {
        MockitoAnnotations.initMocks(this);

        DummyNetvertexServerContextImpl context = DummyNetvertexServerContextImpl.spy();
        miscConf = spy(new DummyMiscellaneousConfiguration());
        context.getServerConfiguration().setMiscConf(miscConf);

        SPRProvider sprProvider = mock(SPRProvider.class);
        when(sprProvider.getDefaultRepository()).thenReturn(subscriberRepository);

        cache = spy(new PartitioningCache.CacheBuilder<String, String>(new TestableTaskScheduler()).build());
        repositorySelector = spy(RepositorySelector.create(null, sprProvider));
        doReturn(subscriberRepository).when(repositorySelector).select(anyString());
        ddfTable = new CacheAwareDDFTable(null, sprProvider, context, mapper, cache, repositorySelector);
        ddfTable.setPrimaryCache(spy(ddfTable.getPrimaryCache()));
        primaryCache = ddfTable.getPrimaryCache();
        ddfTable.initialize();
        ddfTable = spy(ddfTable);
        request = new PCRFRequestImpl();

    }

    private SPRInfoImpl getExpectedSPRInfo() {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberIdentityVal);
        return sprInfo;
    }

    private SPRInfoImpl getExpectedSPRInfoOnAnonymousUser(String subscriberId) throws OperationFailedException {
        SPRInfoImpl sprInfo = new SPRInfoImpl();
        sprInfo.setSubscriberIdentity(subscriberId);
        sprInfo.setStatus(SPRInfo.ANONYMOUS);
        sprInfo.setCui(subscriberId);
        sprInfo.setUnknownUser(true);
        return sprInfo;
    }

    @Test
    public void throwOperationFailedExceptionWhenPrimaryAndSecondaryIdentityNotFoundInRequest() throws OperationFailedException {
        SPRInfo sprInfo = getExpectedSPRInfo();

        exception.expect(OperationFailedException.class);
        exception.expectMessage("Subscriber identity not found in pcrf request");

        assertSame(sprInfo, ddfTable.getProfile(request));
    }


    @Test
    public void removeSecondaryCacheOnReAuthorizationRequest() throws Exception {
        request.setPCRFEvents(EnumSet.of(PCRFEvent.REAUTHORIZE));
        request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentityVal);
        alternateIdentityVal = UUID.randomUUID().toString();
        request.setAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val, alternateIdentityVal);

        SPRInfo sprInfo = getExpectedSPRInfo();
        when(subscriberRepository.getProfile(anyString())).thenReturn(sprInfo);
        ddfTable.getProfile(request);
        verify(ddfTable).removeSecondaryCache(alternateIdentityVal);
    }

    @Test
    public void alwaysGetSubscriberIdFromAlternateIdWhenSubscriberAndAlternateIdBothPresentInRequest() throws Exception {
        request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentityVal);
        request.setAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val, alternateIdentityVal);
        ddfTable.getProfile(request);
        verify(ddfTable).getSubscriberIdForAlternateId(alternateIdentityVal);
    }

    @Test
    public void createAnonymousSubscriberIfPrimaryIdentityNotFoundBasedOnSecondaryIdentity() throws OperationFailedException {
        request.setAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val, alternateIdentityVal);
        SPRInfo sprInfo = getExpectedSPRInfoOnAnonymousUser(alternateIdentityVal);

        when(subscriberRepository.createAnonymousProfile(alternateIdentityVal)).thenReturn(sprInfo);

        SPRInfo expected = ddfTable.getProfile(request);
        ReflectionAssert.assertReflectionEquals(expected, sprInfo);
    }


    public class WhenSprCacheEnabled{

        @Before
        public void enableSPRCache(){
           miscConf.setSprCacheEnabled(true);
         }


        @Test
        public void fetchSubscriberFromPrimaryIdentityWhenFoundInRequestAndRefreshCache() throws Exception {
            request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentityVal);
            SPRInfo sprInfo = getExpectedSPRInfo();
            when(subscriberRepository.getProfile(anyString())).thenReturn(sprInfo);

            SPRInfo profile = ddfTable.getProfile(request);
            verify(primaryCache).refresh(subscriberIdentityVal);
            assertSame(sprInfo, profile);
        }

        @Test
        public void fetchSubscriberFromSecondaryIdentityWhenPrimaryIdentityNotFoundInRequestAndRefreshCache() throws Exception {
            request.setAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val, alternateIdentityVal);
            SPRInfo sprInfo = getExpectedSPRInfo();

            when(ddfTable.getSubscriberIdForAlternateId(alternateIdentityVal)).thenReturn(subscriberIdentityVal);
            when(subscriberRepository.getProfile(anyString())).thenReturn(sprInfo);

            SPRInfo expected = ddfTable.getProfile(request);
            verify(primaryCache).refresh(subscriberIdentityVal);
            ReflectionAssert.assertReflectionEquals(expected, sprInfo);
        }

        @Test
        public void getProfileBySubscriberIdFirstAccessFromCache() throws Exception{
            primaryCache.put(subscriberIdentityVal, getExpectedSPRInfo());
            ddfTable.getProfile(subscriberIdentityVal);
            verify(primaryCache,times(1)).get(subscriberIdentityVal);
        }

        @Test
        public void removeCacheRemovesMappingsfromCache() throws OperationFailedException {
            addMapping();
            ddfTable.removeCache(alternateId);
            verify(primaryCache).remove(alternateId);
        }

        @Test
        public void getSubscriberIdForAlternateIdInteractWithCache() throws Exception {
            String expectedSubscriberId = ddfTable.getSubscriberIdForAlternateId(alternateId);
            verify(cache).getWithoutLoad(alternateId);
        }

        @Test
        public void subscriberAlternateIdentityWillBeCached() throws Exception {
            when(ddfTable.getSubscriberIdForAlternateId(alternateId)).thenReturn(subscriberId);
            ddfTable.getSubscriberIdForAlternateId(alternateId);
            verify(cache).put(alternateId,subscriberId);
        }
    }

    public class WhenSPRCacheDisabled{

        @Before
        public void enableSPRCache(){
            miscConf.setSprCacheEnabled(false);

        }


        @Test
        public void fetchSubscriberFromPrimaryIdentityWhenFoundInRequestAndNeverRefershCache() throws Exception {
            request.setAttribute(PCRFKeyConstants.SUB_SUBSCRIBER_IDENTITY.val, subscriberIdentityVal);
            SPRInfo sprInfo = getExpectedSPRInfo();
            when(subscriberRepository.getProfile(anyString())).thenReturn(sprInfo);
            SPRInfo profile = ddfTable.getProfile(request);
            verify(primaryCache,never()).refresh(subscriberIdentityVal);
            assertSame(sprInfo, profile);
        }

        @Test
        public void fetchSubscriberFromSecondaryIdentityWhenPrimaryIdentityNotFoundInRequestAndNeverRefreshCache() throws Exception {
            request.setAttribute(PCRFKeyConstants.SUB_ALTERNATE_IDENTITY.val, alternateIdentityVal);
            SPRInfo sprInfo = getExpectedSPRInfo();

            when(ddfTable.getSubscriberIdForAlternateId(alternateIdentityVal)).thenReturn(subscriberIdentityVal);
            when(subscriberRepository.getProfile(anyString())).thenReturn(sprInfo);

            SPRInfo expected = ddfTable.getProfile(request);
            verify(primaryCache,never()).refresh(subscriberIdentityVal);
            ReflectionAssert.assertReflectionEquals(expected, sprInfo);
        }


        @Test
        public void getSubscriberIdForAlternateIdNeverInteractWithCache() throws Exception {
            String expectedSubscriberId = ddfTable.getSubscriberIdForAlternateId(alternateId);
            verify(cache,never()).getWithoutLoad(alternateId);
        }


        @Test
        public void getProfileBySubscriberIdNeverAccessCache() throws Exception{
            primaryCache.put(subscriberIdentityVal,getExpectedSPRInfo());
            ddfTable.getProfile(subscriberIdentityVal);
            verify(primaryCache,never()).get(subscriberIdentityVal);
        }

        @Test
        public void removeCacheWillNeverRemovesMappingsfromCache() throws OperationFailedException {
            addMapping();
            ddfTable.removeCache(alternateId);
            verify(primaryCache,never()).remove(alternateId);

        }

        @Test
        public void subscriberIdWillNotbeCached() throws Exception {
            ddfTable.getSubscriberIdForAlternateId(alternateId);
            verify(cache, never()).put(alternateId,subscriberId);
        }

    }

    private void addMapping() throws OperationFailedException {
        mapper.addMapping(subscriberId,alternateId);
    }
    @After
    public void tearDown() throws Exception {
        cache.flush();
    }

}
