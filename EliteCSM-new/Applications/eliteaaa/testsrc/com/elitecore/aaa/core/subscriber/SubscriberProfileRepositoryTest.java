package com.elitecore.aaa.core.subscriber;

import com.elitecore.aaa.core.data.AccountData;
import com.elitecore.aaa.core.drivers.ChangeCaseStrategy;
import com.elitecore.aaa.core.drivers.IEliteAuthDriver;
import com.elitecore.aaa.core.drivers.StripUserIdentityStrategy;
import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.aaa.core.subscriber.conf.SubscriberProfileRepositoryDetails;
import com.elitecore.aaa.radius.conf.impl.AdditionalDriverDetail;
import com.elitecore.aaa.radius.conf.impl.PrimaryDriverDetail;
import com.elitecore.aaa.radius.conf.impl.SecondaryAndCacheDriverDetail;
import com.elitecore.aaa.radius.service.auth.RadAuthRequest;
import com.elitecore.aaa.radius.service.auth.RadAuthResponse;
import com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthResponseImpl;
import com.elitecore.aaa.radius.subscriber.conf.RadiusSubscriberProfileRepositoryDetails;
import com.elitecore.commons.tests.PrintMethodRule;
import com.elitecore.commons.threads.SettableFuture;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.drivers.DriverProcessFailedException;
import com.elitecore.core.commons.plugins.script.DriverScript;
import com.elitecore.core.serverx.ServerContext;
import com.elitecore.core.serverx.internal.tasks.AsyncTaskContext;
import com.elitecore.core.serverx.internal.tasks.IntervalBasedTask;
import com.elitecore.core.serverx.internal.tasks.SingleExecutionAsyncTask;
import com.elitecore.core.serverx.manager.scripts.ExternalScriptsManager;
import com.elitecore.core.serverx.manager.scripts.ScriptContext;
import com.elitecore.core.servicex.ServiceRequest;
import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.ESCommunicatorImpl;
import com.elitecore.core.systemx.esix.TaskScheduler;
import com.elitecore.core.util.url.SocketDetail;

import de.bechte.junit.runners.context.HierarchicalContextRunner;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import javax.annotation.Nullable;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Future;

import static com.elitecore.aaa.radius.service.base.BaseRadiusAuthService.RadiusAuthRequestImpl;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.mockito.Mockito.*;

/**
 * @author narendra.pathai
 */
@RunWith(HierarchicalContextRunner.class)
public class SubscriberProfileRepositoryTest {

    private static final String DONT_CARE = "0";
    private static final String UNKNOWN_ID = "0";

    @Rule public ExpectedException exception = ExpectedException.none();
    @Rule public PrintMethodRule printMethod = new PrintMethodRule();

    private AAAServerContext mockContext = mock(AAAServerContext.class);
    private ExternalScriptsManager externalScriptsManager = mock(ExternalScriptsManager.class);
    private RadiusSubscriberProfileRepositoryDetails data;
    private AccountData accountData;
    private AccountData additionalAccountData;
    private AccountData accountDataFromScript;
    private SubscriberProfileRepositoryStub repository;
    private RadiusAuthRequestImpl request;
    private RadiusAuthResponseImpl response;

    @Before
    public void setUp() throws UnknownHostException, InitializationFailedException {
        data = new RadiusSubscriberProfileRepositoryDetails();
        request = new RadiusAuthRequestImpl(new byte[] {}, InetAddress.getLocalHost(), 0, null, new SocketDetail(InetAddress.getLocalHost().getHostAddress(), 0));
        response = new RadiusAuthResponseImpl(request.getAuthenticator(), 0, null);
        accountData = spy(new AccountData());
        accountData.setUserIdentity("User1");
        accountData.setAccessPolicy("AccessPolicy1");
        additionalAccountData = spy(new AccountData());
        accountData.setAuthorizationPolicy("additional-authorization-policy");
        accountDataFromScript = spy(new AccountData());
        accountDataFromScript.setUserIdentity("UserFromScript");
        repository = new SubscriberProfileRepositoryStub(mockContext, data);
        when(mockContext.getExternalScriptsManager()).thenReturn(externalScriptsManager);
        TaskScheduler sameThreadTaskScheduler = new SameThreadTaskScheduler();
        when(mockContext.getTaskScheduler()).thenReturn(sameThreadTaskScheduler);
    }

    @Test
    public void failsToInitializeIfNoPrimaryAndSecondaryDriversAreFound() throws InitializationFailedException {
        data.getDriverDetails().getPrimaryDriverGroup().add(new PrimaryDriverDetail(UNKNOWN_ID, 1));
        data.getDriverDetails().getSecondaryDriverGroup().add(new SecondaryAndCacheDriverDetail(UNKNOWN_ID, DONT_CARE));

        exception.expect(InitializationFailedException.class);

        repository.init();
    }

    @Test
    public void initializesIgnoringDriverScriptInitializationFailure() throws InitializationFailedException {
        addPrimaryGroupDriver(1, driverWithoutAccountData("1"));

        String SCRIPT_NAME = "FAILURE_SCRIPT";
        data.getDriverDetails().setDriverScript(SCRIPT_NAME);
        doThrow(IllegalArgumentException.class).when(externalScriptsManager).getScript(SCRIPT_NAME, DriverScript.class);

        repository.init();
    }

    @Test
    public void isAliveIfBothPrimaryAndSecondaryGroupsAreAlive() throws InitializationFailedException {
        FakeDriver primaryDriver = driverWithoutAccountData("1");
        addPrimaryGroupDriver(1, primaryDriver);

        FakeDriver secondaryDriver = driverWithoutAccountData("2");
        addSecondaryGroupDriver(secondaryDriver);

        repository.init();

        assertThat(repository.isAlive(), is(true));
    }

    @Test
    public void isAliveIfOnlyPrimaryGroupIsAlive() throws InitializationFailedException {
        FakeDriver primaryDriver = driverWithoutAccountData("1");
        addPrimaryGroupDriver(1, primaryDriver);

        FakeDriver secondaryDriver = driverWithoutAccountData("2");
        addSecondaryGroupDriver(secondaryDriver);

        repository.init();

        secondaryDriver.markDead();

        assertThat(repository.isAlive(), is(true));
    }

    @Test
    public void isAliveIfOnlySecondaryGroupIsAlive() throws InitializationFailedException {
        FakeDriver primaryDriver = driverWithoutAccountData("1");
        addPrimaryGroupDriver(1, primaryDriver);

        FakeDriver secondaryDriver = driverWithoutAccountData("2");
        addSecondaryGroupDriver(secondaryDriver);

        repository.init();

        primaryDriver.markDead();

        assertThat(repository.isAlive(), is(true));
    }

    @Test
    public void isDeadIfBothPrimaryAndSecondaryGroupsAreDead() throws InitializationFailedException {
        FakeDriver primaryDriver = driverWithoutAccountData("1");
        addPrimaryGroupDriver(1, primaryDriver);

        FakeDriver secondaryDriver = driverWithoutAccountData("2");
        addSecondaryGroupDriver(secondaryDriver);

        repository.init();

        primaryDriver.markDead();
        secondaryDriver.markDead();

        assertThat(repository.isAlive(), is(false));
    }

    public class NonEAPRequest {

        @Test
        public void doesNotCheckSecondaryGroupIfSubscriberProfileIsFoundFromPrimaryGroup() throws InitializationFailedException, DriverProcessFailedException {

            FakeDriver driver = driverWithAccountData("1", accountData);

            addPrimaryGroupDriver(1, driver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(driver).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
        }

        @Test
        public void findsProfileFromSecondaryGroupIfNotFoundInPrimaryGroup() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");

            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);

            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(secondaryDriver).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
        }

        @Test
        public void attemptsToLocateProfileFromSecondaryGroupIfPrimaryDriverProcessingFails() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            doThrow(DriverProcessFailedException.class).when(driver).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);

            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(equalTo(accountData)));
        }

        @Test
        public void cachesProfileFoundFromSecondaryGroupIntoCacheDriver() throws DriverProcessFailedException, InitializationFailedException {

            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);
            FakeDriver cacheDriver = driverWithoutAccountData("3");

            addSecondaryGroupDriver(secondaryDriver, cacheDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(cacheDriver).saveAccountData(accountData);
        }

        @Test
        public void cachesProfileFoundFromAnyOfSecondaryGroupDriversIntoCacheDriver() throws DriverProcessFailedException, InitializationFailedException {

            FakeDriver secondaryDriver = driverWithoutAccountData("2");
            FakeDriver secondaryDriver1 = driverWithAccountData("3", accountData);
            FakeDriver cacheDriver = driverWithoutAccountData("4");

            addSecondaryGroupDriver(secondaryDriver, cacheDriver);
            addSecondaryGroupDriver(secondaryDriver1, cacheDriver);

            repository.init();

            repository.getAccountData(request, response);

            verify(cacheDriver).saveAccountData(accountData);
        }

        @Test
        public void attemptsToLocateProfileFromAllSecondaryDriversOnce() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver1 = driverWithoutAccountData("2");

            addSecondaryGroupDriver(secondaryDriver1);

            FakeDriver secondaryDriver2 = driverWithoutAccountData("3");

            addSecondaryGroupDriver(secondaryDriver2);

            FakeDriver secondaryDriver3 = driverWithAccountData("4", accountData);
            addSecondaryGroupDriver(secondaryDriver3);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(secondaryDriver1).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
            verify(secondaryDriver2).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
            verify(secondaryDriver3).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
        }

        @Test
        public void attemptsToLocateProfileFromAllSecondaryDriversOnceIfSecondaryDriverProcessingFails() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver1 = driverWithoutAccountData("2");
            doThrow(DriverProcessFailedException.class).when(secondaryDriver1).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
            addSecondaryGroupDriver(secondaryDriver1);

            FakeDriver secondaryDriver2 = driverWithoutAccountData("3");
            doThrow(DriverProcessFailedException.class).when(secondaryDriver2).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
            addSecondaryGroupDriver(secondaryDriver2);

            FakeDriver secondaryDriver3 = driverWithAccountData("4", accountData);
            addSecondaryGroupDriver(secondaryDriver3);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(secondaryDriver1).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
            verify(secondaryDriver2).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
            verify(secondaryDriver3).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
        }

        @Test
        public void failsToFindProfileIfProfileNotFoundInPrimaryGroupAndSecondaryGroupIsDead() throws InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");

            addPrimaryGroupDriver(1, driver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(nullValue()));
        }

        @Test
        public void findsProfileFromSecondaryGroupIfPrimaryGroupIsDead() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);

            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(secondaryDriver).getAccountData(request, Collections.<String>emptyList(), ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null);
        }

        @Test
        public void failsToFindProfileIfItIsNotFoundInEitherOfPrimaryOrSecondaryGroup() throws InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver = driverWithoutAccountData("2");
            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(nullValue()));
        }

        @Test
        public void failsToFindProfileIfBothPrimaryAndSecondaryGroupsAreDead() throws InitializationFailedException {
            FakeDriver driver = driverWithAccountData("1", accountData);
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);
            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            driver.markDead();
            secondaryDriver.markDead();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData, is(nullValue()));
        }

        @Test
        public void mergesAdditionalProfileIntoPrimaryGroupProfile() throws InitializationFailedException {
            FakeDriver driver = driverWithAccountData("1", accountData);
            addPrimaryGroupDriver(1, driver);

            FakeDriver additionalDriver = driverWithAccountData("2", additionalAccountData);
            addAdditionalDriver(additionalDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            verify(accountData, atLeastOnce()).append(additionalAccountData);
        }

        @Test
        public void mergesAdditionalProfileIntoSecondaryGroupProfile() throws InitializationFailedException {
            FakeDriver driver = driverWithAccountData("1", accountData);
            addSecondaryGroupDriver(driver);

            FakeDriver additionalDriver = driverWithAccountData("2", additionalAccountData);
            addAdditionalDriver(additionalDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            accountData.append(additionalAccountData);
            assertThat(actualAccountData, is(equalTo(accountData)));
            verify(accountData, atLeastOnce()).append(additionalAccountData);
        }

        @Test
        public void mergesPrimaryGroupProfileIntoProfileFromPreDriverScript() throws InitializationFailedException {
            repositoryWithScript(scriptWithAccountData(accountDataFromScript));

            FakeDriver driver = driverWithAccountData("1", accountData);
            addPrimaryGroupDriver(1, driver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response);

            assertThat(actualAccountData.getUserIdentity(), is(equalTo("UserFromScript")));
            verify(accountDataFromScript, atLeastOnce()).append(accountData);
        }
    }

    private void repositoryWithScript(DriverScript driverScript) {
        data.getDriverDetails().setDriverScript(driverScript.getName());
        doReturn(driverScript).when(externalScriptsManager).getScript(driverScript.getName(), DriverScript.class);
    }

    private DriverScript scriptWithAccountData(AccountData accountDataFromScript) {
        return new FakeScript(accountDataFromScript);
    }

    public class EAPRequest {

        private static final String ANY_IDENTITY = "";

        @Test
        public void doesNotCheckSecondaryGroupIfSubscriberProfileIsFoundFromPrimaryGroup() throws InitializationFailedException, DriverProcessFailedException {
            FakeDriver driver = driverWithAccountData("1", accountData);
            addPrimaryGroupDriver(1, driver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(driver).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
        }

        @Test
        public void returnsFoundProfileFromSecondaryGroupIfProfileIsNotFoundInPrimaryGroup() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);
            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(secondaryDriver).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
        }

        @Test
        public void attemptsToLocateProfileFromSecondaryGroupIfPrimaryDriverProcessingFails() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            doThrow(DriverProcessFailedException.class).when(driver).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);

            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(equalTo(accountData)));
        }

        @Test
        public void cachesProfileFoundFromSecondaryGroupIntoCacheDriver() throws DriverProcessFailedException, InitializationFailedException {

            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);
            FakeDriver cacheDriver = driverWithoutAccountData("3");

            addSecondaryGroupDriver(secondaryDriver, cacheDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(cacheDriver).saveAccountData(accountData);
        }

        @Test
        public void cachesProfileFoundFromAnyOfSecondaryGroupDriversIntoCacheDriver() throws DriverProcessFailedException, InitializationFailedException {

            FakeDriver secondaryDriver = driverWithoutAccountData("2");
            FakeDriver secondaryDriver1 = driverWithAccountData("3", accountData);
            FakeDriver cacheDriver = driverWithoutAccountData("4");

            addSecondaryGroupDriver(secondaryDriver, cacheDriver);
            addSecondaryGroupDriver(secondaryDriver1, cacheDriver);

            repository.init();

            repository.getAccountData(request, response, ANY_IDENTITY);

            verify(cacheDriver).saveAccountData(accountData);
        }

        @Test
        public void attemptsToLocateProfileFromAllSecondaryDriversOnce() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver1 = driverWithoutAccountData("2");
            addSecondaryGroupDriver(secondaryDriver1);

            FakeDriver secondaryDriver2 = driverWithoutAccountData("3");
            addSecondaryGroupDriver(secondaryDriver2);

            FakeDriver secondaryDriver3 = driverWithAccountData("4", accountData);
            addSecondaryGroupDriver(secondaryDriver3);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(secondaryDriver1).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
            verify(secondaryDriver2).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
            verify(secondaryDriver3).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
        }

        @Test
        public void attemptsToLocateProfileFromAllSecondaryDriversOnceIfSecondaryDriverProcessingFails() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver1 = driverWithoutAccountData("2");
            doThrow(DriverProcessFailedException.class).when(secondaryDriver1).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
            addSecondaryGroupDriver(secondaryDriver1);

            FakeDriver secondaryDriver2 = driverWithoutAccountData("3");
            doThrow(DriverProcessFailedException.class).when(secondaryDriver2).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
            addSecondaryGroupDriver(secondaryDriver2);

            FakeDriver secondaryDriver3 = driverWithAccountData("4", accountData);
            addSecondaryGroupDriver(secondaryDriver3);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(secondaryDriver1).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
            verify(secondaryDriver2).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
            verify(secondaryDriver3).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
        }

        @Test
        public void failsToFindProfileIfProfileNotFoundInPrimaryGroupAndSecondaryGroupIsDead() throws InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            addPrimaryGroupDriver(1, driver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(nullValue()));
        }

        @Test
        public void returnsFoundProfileFromSecondaryGroupIfPrimaryGroupIsDead() throws DriverProcessFailedException, InitializationFailedException {
            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);
            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(equalTo(accountData)));

            verify(secondaryDriver).getAccountData(request, ChangeCaseStrategy.none(), false, StripUserIdentityStrategy.none(), null, ANY_IDENTITY);
        }

        @Test
        public void failsToFindProfileIfItIsNotFoundInEitherOfPrimaryOrSecondaryGroup() throws InitializationFailedException {
            FakeDriver driver = driverWithoutAccountData("1");
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver = driverWithoutAccountData("2");
            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(nullValue()));
        }

        @Test
        public void failsToFindProfileIfBothPrimaryAndSecondaryGroupsAreDead() throws InitializationFailedException {
            FakeDriver driver = driverWithAccountData("1", accountData);
            addPrimaryGroupDriver(1, driver);

            FakeDriver secondaryDriver = driverWithAccountData("2", accountData);
            addSecondaryGroupDriver(secondaryDriver);

            repository.init();

            driver.markDead();
            secondaryDriver.markDead();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData, is(nullValue()));
        }

        @Test
        public void mergesAdditionalProfileIntoPrimaryGroupProfile() throws InitializationFailedException {
            FakeDriver driver = driverWithAccountData("1", accountData);
            addPrimaryGroupDriver(1, driver);

            FakeDriver additionalDriver = driverWithAccountData("2", additionalAccountData);
            addAdditionalDriver(additionalDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            accountData.append(additionalAccountData);
            assertThat(actualAccountData, is(equalTo(accountData)));
            verify(accountData, atLeastOnce()).append(additionalAccountData);
        }

        @Test
        public void mergesAdditionalProfileIntoSecondaryGroupProfile() throws InitializationFailedException {
            FakeDriver driver = driverWithAccountData("1", accountData);
            addSecondaryGroupDriver(driver);

            FakeDriver additionalDriver = driverWithAccountData("2", additionalAccountData);
            addAdditionalDriver(additionalDriver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            accountData.append(additionalAccountData);
            assertThat(actualAccountData, is(equalTo(accountData)));
            verify(accountData, atLeastOnce()).append(additionalAccountData);
        }

        @Test
        public void mergesPrimaryGroupProfileIntoProfileFromPreDriverScript() throws InitializationFailedException {
            repositoryWithScript(scriptWithAccountData(accountDataFromScript));

            FakeDriver driver = driverWithAccountData("1", accountData);
            addPrimaryGroupDriver(1, driver);

            repository.init();

            AccountData actualAccountData = repository.getAccountData(request, response, ANY_IDENTITY);

            assertThat(actualAccountData.getUserIdentity(), is(equalTo("UserFromScript")));
            verify(accountDataFromScript, atLeastOnce()).append(accountData);
        }
    }

    private class SubscriberProfileRepositoryStub extends SubscriberProfileRepository<RadAuthRequest, RadAuthResponse> {
        private Map<String, FakeDriver> driverIdToDriver = new HashMap<String, FakeDriver>();

        public SubscriberProfileRepositoryStub(ServerContext serverContext, SubscriberProfileRepositoryDetails data) {
            super(serverContext, data);
        }

        @Override
        protected IEliteAuthDriver getDriver(String driverInstanceId) {
            return driverIdToDriver.get(driverInstanceId);
        }

        @Override
        protected AccountData getAccountDataFromRequest(RadAuthRequest request) {
            return request.getAccountData();
        }

        @Override
        protected void setAccountDataIntoRequest(RadAuthRequest request, AccountData accountData) {
            request.setAccountData(accountData);
        }

        @Override
        protected List<String> getUserIdentities(RadAuthRequest request) {
            return Collections.emptyList();
        }

        @Override
        protected String getModuleName() {
            return "SUBSCRIBER-PROFILE-REPO-STUB";
        }

        @Override
        protected void addSubscriberProfileVSA(RadAuthRequest request, AccountData accountData) {

        }
    }

    private class FakeDriver extends ESCommunicatorImpl implements  IEliteAuthDriver {

        private String driverInstanceId;
        @Nullable
        private final AccountData accountData;

        public FakeDriver(String driverInstanceId, @Nullable AccountData accountData) {
            super(null);
            this.driverInstanceId = driverInstanceId;
            this.accountData = accountData;
        }

        @Override
        public AccountData getAccountData(ServiceRequest serviceRequest, List<String> userIdentities, ChangeCaseStrategy caseStrategy, boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator) throws DriverProcessFailedException {
            return accountData;
        }

        @Override
        public AccountData getAccountData(ServiceRequest serviceRequest, ChangeCaseStrategy caseStrategy, boolean btrimUserIdentity, StripUserIdentityStrategy stripStrategy, String realmSeparator, String userIdentity) throws DriverProcessFailedException {
            return accountData;
        }

        @Override
        public String getDriverInstanceId() {
            return driverInstanceId;
        }

        @Override
        public void saveAccountData(AccountData accountData) {

        }

        @Override
        public void scan() {

        }

        @Override
        public String getName() {
            return "FAKE-DRIVER-" + driverInstanceId;
        }

        @Override
        public String getTypeName() {
            return "FAKE";
        }

        @Override
        protected int getStatusCheckDuration() {
            return 0;
        }
    }

    private class FakeScript extends DriverScript {

        private AccountData accountData;

        public FakeScript(AccountData accountData) {
            super(new ScriptContext() {
                @Override
                public ServerContext getServerContext() {
                    return SubscriberProfileRepositoryTest.this.mockContext;
                }
            });
            this.accountData = accountData;
        }

        @Override
        public String getName() {
            return "FAKE_SCRIPT";
        }

        @Override
        protected void pre(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {
            RadAuthRequest authRequest = (RadAuthRequest) serviceRequest;
            authRequest.setAccountData(accountData);
        }

        @Override
        protected void post(ServiceRequest serviceRequest, ServiceResponse serviceResponse) {

        }
    }

    private void addSecondaryGroupDriver(FakeDriver secondaryDriver) {
        data.getDriverDetails().getSecondaryDriverGroup().add(new SecondaryAndCacheDriverDetail(secondaryDriver.getDriverInstanceId(), DONT_CARE));
        repository.driverIdToDriver.put(secondaryDriver.getDriverInstanceId(), secondaryDriver);
    }

    private void addSecondaryGroupDriver(FakeDriver secondaryDriver, FakeDriver cacheDriver) {
        data.getDriverDetails().getSecondaryDriverGroup().add(new SecondaryAndCacheDriverDetail(secondaryDriver.getDriverInstanceId(), cacheDriver.getDriverInstanceId()));
        repository.driverIdToDriver.put(secondaryDriver.getDriverInstanceId(), secondaryDriver);
        repository.driverIdToDriver.put(cacheDriver.getDriverInstanceId(), cacheDriver);
    }

    private FakeDriver driverWithoutAccountData(String driverInstanceId) {
        return spy(new FakeDriver(driverInstanceId, null));
    }

    private FakeDriver driverWithAccountData(String driverInstanceId, AccountData accountData) {
        return spy(new FakeDriver(driverInstanceId, accountData));
    }

    private void addPrimaryGroupDriver(int weightage, FakeDriver driver) {
        data.getDriverDetails().getPrimaryDriverGroup().add(new PrimaryDriverDetail(driver.getDriverInstanceId(), weightage));
        repository.driverIdToDriver.put(driver.getDriverInstanceId(), driver);
    }


    private void addAdditionalDriver(FakeDriver driver) {
        data.getDriverDetails().getAdditionalDriverList().add(new AdditionalDriverDetail(driver.getDriverInstanceId()));
        repository.driverIdToDriver.put(driver.getDriverInstanceId(), driver);
    }

    public static class SameThreadTaskScheduler implements TaskScheduler {

        @Nullable
        @Override
        public Future<?> scheduleSingleExecutionTask(@Nullable SingleExecutionAsyncTask task) {
            SettableFuture<?> completedFuture = SettableFuture.create();
            completedFuture.set(null);

            if (task == null) {
                return SettableFuture.create();
            }
            task.execute(new AsyncTaskContext() {
                @Override
                public void setAttribute(String key, Object attribute) {

                }

                @Override
                public Object getAttribute(String key) {
                    return null;
                }
            });
            return completedFuture;
        }

        @Nullable
        @Override
        public Future<?> scheduleIntervalBasedTask(@Nullable IntervalBasedTask task) {
            SettableFuture<?> completedFuture = SettableFuture.create();
            completedFuture.set(null);

            if (task == null) {
                return SettableFuture.create();
            }
            task.execute(new AsyncTaskContext() {
                @Override
                public void setAttribute(String key, Object attribute) {

                }

                @Override
                public Object getAttribute(String key) {
                    return null;
                }
            });
            return completedFuture;
        }

        @Override
        public void execute(Runnable command) {
            command.run();
        }
    }
}
