package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;


import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.UUID;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.constants.UnknownUserAction;
import com.elitecore.corenetvertex.constants.UsageType;
import com.elitecore.corenetvertex.core.conf.FailReason;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferData;
import com.elitecore.corenetvertex.pd.productoffer.ProductOfferServicePkgRelData;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pm.PolicyRepository;
import com.elitecore.corenetvertex.pm.ProductOfferBuilder;
import com.elitecore.corenetvertex.pm.offer.ProductOffer;
import com.elitecore.corenetvertex.pm.offer.ProductOfferServicePkgRel;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.RncProfileDetail;
import com.elitecore.corenetvertex.pm.store.ProductOfferStore;
import com.elitecore.corenetvertex.spr.data.SPRInfo;
import com.elitecore.corenetvertex.spr.data.SPRInfoImpl;
import com.elitecore.netvertex.core.NetVertexServerContext;
import com.elitecore.netvertex.core.ddf.CacheAwareDDFTable;
import com.elitecore.netvertex.core.servicepolicy.ExecutionContext;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.PCRFServiceContext;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import com.elitecore.netvertex.service.pcrf.servicepolicy.conf.impl.PccServicePolicyConfigurationImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;


import static org.apache.commons.lang3.RandomUtils.nextInt;
import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.when;



/**
 * Created by kirpalsinh on 6/7/17.
 */

@RunWith(HierarchicalContextRunner.class)
public class AuthenticationHandlerTest {

    public static final String INR = "INR";
    private PCRFRequest pcrfRequest;
    private SPRInfoImpl sprInfoImpl;
    private ExecutionContext executionContext;
    private PCRFResponse pcrfResponse;
    private PccServicePolicyConfigurationImpl pcrfServicePolicyConfiguration;
    private PCRFServiceContext pcrfServiceContext;

    @Before
    public void setup(){
        pcrfRequest = new PCRFRequestImpl();
        sprInfoImpl = new SPRInfoImpl();
        pcrfResponse = new PCRFResponseImpl();
        executionContext =  new ExecutionContext(pcrfRequest,pcrfResponse, CacheAwareDDFTable.getInstance(), INR);
        pcrfRequest.setSPRInfo(sprInfoImpl);
        pcrfServicePolicyConfiguration = mock(PccServicePolicyConfigurationImpl.class);
        pcrfServiceContext = spy(PCRFServiceContext.class);

    }

    @Test
    public void Success_WithValidPAPPassword_WithValidEncryptionType() {

        sprInfoImpl.setExpiryDate(new Timestamp(System.currentTimeMillis() + System.currentTimeMillis()));
        sprInfoImpl.setStatus(SubscriberStatus.ACTIVE.name());
        sprInfoImpl.setEncryptionType(null);
        pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);

        sprInfoImpl.setEncryptionType("64");
        sprInfoImpl.setPassword("dGVzdA==");
        pcrfRequest.setPapPassword("test");

        AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
        authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

    }

    public class SkipAuthentication {

        @Test
        public void When_Null_SPRInfo() {
            AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
            authenticationHandler.process(mock(PCRFRequestImpl.class), null, null);
        }

        @Test
        public void skip_PAP_CHAP_When_PasswordCheckFlag_Is_False(){
            sprInfoImpl.setPasswordCheck(false);
            AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
            pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);
            authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);
        }

        @Test
        public void skip_PAP_CHAP_When_Null_Password_In_SPR(){
            sprInfoImpl.setPasswordCheck(true);
            sprInfoImpl.setPassword(null);
            AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
            pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);
            authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);
        }


        public class KnownSubscriber {

            @Before
            public void setup(){
                sprInfoImpl.setUnknownUser(false);
            }

            @Test
            public void has_ExpiredDate() {
                sprInfoImpl.setExpiryDate(new Timestamp(0));
                AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                assertEquals(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_PROFILE_EXPIRED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
            }

            @Test
            public void has_InActiveStatus() {
                sprInfoImpl.setExpiryDate(new Timestamp(System.currentTimeMillis() + System.currentTimeMillis()));
                sprInfoImpl.setStatus(SubscriberStatus.INACTIVE.name());

                AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                assertEquals(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_PROFILE_INACTIVE.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
            }

            @Test
            public void has_InvalidEncryptionType(){
                sprInfoImpl.setExpiryDate(new Timestamp(System.currentTimeMillis() + System.currentTimeMillis()));
                sprInfoImpl.setStatus(SubscriberStatus.ACTIVE.name());
                sprInfoImpl.setEncryptionType("Invalid");
                sprInfoImpl.setPasswordCheck(true);
                sprInfoImpl.setPassword("test");
                pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);

                AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
            }

            @Test
            public void has_EmptyEncryptionType(){
                sprInfoImpl.setExpiryDate(new Timestamp(System.currentTimeMillis() + System.currentTimeMillis()));
                sprInfoImpl.setStatus(SubscriberStatus.ACTIVE.name());
                sprInfoImpl.setEncryptionType("");
                sprInfoImpl.setPasswordCheck(true);
                sprInfoImpl.setPassword("test");
                pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);

                AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
            }

            @Test
            public void has_BlankEncryptionType(){
                sprInfoImpl.setExpiryDate(new Timestamp(System.currentTimeMillis() + System.currentTimeMillis()));
                sprInfoImpl.setStatus(SubscriberStatus.ACTIVE.name());
                sprInfoImpl.setEncryptionType("  ");
                pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);
                sprInfoImpl.setPasswordCheck(true);
                sprInfoImpl.setPassword("test");

                AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
            }
        }

        public class PAPCHAPAuthentication{

            @Before
            public void setup(){
                sprInfoImpl.setPasswordCheck(true);
                sprInfoImpl.setPassword("test");
                sprInfoImpl.setExpiryDate(new Timestamp(System.currentTimeMillis() + System.currentTimeMillis()));
                sprInfoImpl.setStatus(SubscriberStatus.ACTIVE.name());
                sprInfoImpl.setEncryptionType(null);
                pcrfResponse.setAttribute(PCRFKeyConstants.RESULT_CODE.getVal(), PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val);
            }

            public class SkipPAPAuthenticationWhenPAPPassword {

                @Test
                public void Is_Null() {

                    pcrfRequest.setPapPassword(null);

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);
                }

                @Test
                public void Is_Empty() {

                    pcrfRequest.setPapPassword("");

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);
                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void Is_Blank() {

                    pcrfRequest.setPapPassword("  ");

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);
                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void Is_Blank_And_SPR_PasswordIsNull() {

                    pcrfRequest.setPapPassword("  ");
                    sprInfoImpl.setPassword(null);
                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);
                }

                @Test
                public void Is_Valid_And_EncryptionType_Is_Invalid() {

                    sprInfoImpl.setEncryptionType("123456789");
                    sprInfoImpl.setPassword("password");
                    pcrfRequest.setPapPassword("password");

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);
                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

            }

            public class SkipCHAPAuthenticationWhenPAPPasswodIsNullAndCHAPPassword {

                @Before
                public void setup(){
                    pcrfRequest.setPapPassword(null);
                    pcrfRequest.setChapChallengeBytes("hello".getBytes());
                }

                @Test
                public void Is_Null() {

                    pcrfRequest.setChapPasswordBytes(null);
                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                }

                @Test
                public void Is_Empty() {

                    pcrfRequest.setChapPasswordBytes("".getBytes());

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void Is_Blank() {

                    pcrfRequest.setChapPasswordBytes(" ".getBytes());

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void Is_Valid_With_Invalid_Encryption_Type() {

                    sprInfoImpl.setEncryptionType("123456789");
                    pcrfRequest.setChapPasswordBytes("password".getBytes());

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

               @Test
                public void Is_Valid_And_EncryptionType_Is_Invalid() {

                    sprInfoImpl.setEncryptionType("123456789");
                    pcrfRequest.setChapPasswordBytes("test".getBytes());
                    sprInfoImpl.setPassword("dGVzdA==");

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_AUTHENTICATION_FAILED.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }
            }
        }

        public class UnknownSubscriber {

            public class WithUnknowUserActionIs {

                @Before
                public void setup(){
                    sprInfoImpl.setUnknownUser(true);
                    when(pcrfServiceContext.getServerContext()).thenReturn(spy(NetVertexServerContext.class));
                }

                @Test
                public void NULL() {

                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(null);

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void DROP_REQUEST() {

                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(UnknownUserAction.DROP_REQUEST);

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }


                @Test
                public void ALLOW_UNKNOWN_USER_have_NullPackageId() {

                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(UnknownUserAction.ALLOW_UNKNOWN_USER);
                    when(pcrfServicePolicyConfiguration.getUnknownUserPkgId()).thenReturn(null);

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void ALLOW_UNKNOWN_USER_have_NullPackage() {

                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(UnknownUserAction.ALLOW_UNKNOWN_USER);
                    when(pcrfServicePolicyConfiguration.getUnknownUserPkgId()).thenReturn("123456");

                    NetVertexServerContext netVertexServerContext = spy(NetVertexServerContext.class);
                    when(pcrfServiceContext.getServerContext()).thenReturn(netVertexServerContext);
                    PolicyRepository policyRepository = spy(PolicyRepository.class);
                    when(netVertexServerContext.getPolicyRepository()).thenReturn(policyRepository);
                    when(policyRepository.getProductOffer()).thenReturn(spy(ProductOfferStore.class));

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void REJECT_UNKNOWN_USER_have_RateConfigured_with_Data_Package() {

                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(UnknownUserAction.ALLOW_UNKNOWN_USER);
                    when(pcrfServicePolicyConfiguration.getUnknownUserPkgId()).thenReturn("123456");

                    NetVertexServerContext netVertexServerContext = spy(NetVertexServerContext.class);
                    when(pcrfServiceContext.getServerContext()).thenReturn(netVertexServerContext);
                    PolicyRepository policyRepository = spy(PolicyRepository.class);
                    when(netVertexServerContext.getPolicyRepository()).thenReturn(policyRepository);
                    when(policyRepository.getProductOffer()).thenReturn(spy(ProductOfferStore.class));
                    RncProfileDetail rnCQuotaProfileDetail = spy(new RnCQuotaProfileFactory(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                            .withRate(1)
                            .withRateOn(UsageType.VOLUME)
                            .withPulse(nextInt(2, 5), 1)
                            .randomBalanceWithRate().create());
                    when(rnCQuotaProfileDetail.isRateConfigured()).thenReturn(true);

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_UNKNOWN_USER.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }
            }
        }

        public class UnAttainableSubscriber {

            public class WithUnAttainableActionIs {

                @Before
                public void setup(){
                    sprInfoImpl.setUnknownUser(false);
                    sprInfoImpl.setStatus(SPRInfo.UNAVAILABLE);
                    when(pcrfServiceContext.getServerContext()).thenReturn(spy(NetVertexServerContext.class));
                }

                @Test
                public void NULL() {

                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(null);

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void DROP_REQUEST() {


                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(UnknownUserAction.DROP_REQUEST);

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }


                @Test
                public void ALLOW_UNKNOWN_USER_have_NullPackageId() {

                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(UnknownUserAction.ALLOW_UNKNOWN_USER);
                    when(pcrfServicePolicyConfiguration.getUnknownUserPkgId()).thenReturn(null);

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void ALLOW_UNKNOWN_USER_have_NullPackage() {

                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(UnknownUserAction.ALLOW_UNKNOWN_USER);
                    when(pcrfServicePolicyConfiguration.getUnknownUserPkgId()).thenReturn("123456");

                    NetVertexServerContext netVertexServerContext = spy(NetVertexServerContext.class);
                    when(pcrfServiceContext.getServerContext()).thenReturn(netVertexServerContext);
                    PolicyRepository policyRepository = spy(PolicyRepository.class);
                    when(netVertexServerContext.getPolicyRepository()).thenReturn(policyRepository);
                    when(policyRepository.getProductOffer()).thenReturn(spy(ProductOfferStore.class));

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);

                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }

                @Test
                public void REJECT_UNKNOWN_USER_have_RateConfigured_with_Data_Package() {

                    when(pcrfServicePolicyConfiguration.getUnknownUserAction()).thenReturn(UnknownUserAction.ALLOW_UNKNOWN_USER);
                    when(pcrfServicePolicyConfiguration.getUnknownUserPkgId()).thenReturn("123456");

                    NetVertexServerContext netVertexServerContext = spy(NetVertexServerContext.class);
                    when(pcrfServiceContext.getServerContext()).thenReturn(netVertexServerContext);
                    PolicyRepository policyRepository = spy(PolicyRepository.class);
                    when(netVertexServerContext.getPolicyRepository()).thenReturn(policyRepository);
                    when(policyRepository.getProductOffer()).thenReturn(spy(ProductOfferStore.class));
                    RncProfileDetail rnCQuotaProfileDetail = spy(new RnCQuotaProfileFactory(UUID.randomUUID().toString(), UUID.randomUUID().toString())
                            .withRate(1)
                            .withRateOn(UsageType.VOLUME)
                            .withPulse(nextInt(2, 5), 1)
                            .randomBalanceWithRate().create());
                    when(rnCQuotaProfileDetail.isRateConfigured()).thenReturn(true);

                    AuthenticationHandler authenticationHandler = new AuthenticationHandler(pcrfServiceContext, pcrfServicePolicyConfiguration);
                    authenticationHandler.process(pcrfRequest, pcrfResponse, executionContext);
                    assertEquals(PCRFKeyValueConstants.RESULT_CODE_SUBSCRIBER_UNAVAILABLE.val, pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
                }
            }

        }
    }

}
