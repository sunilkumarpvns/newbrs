package com.elitecore.corenetvertex.spr;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.MonetaryBalanceType;
import com.elitecore.corenetvertex.core.alerts.AlertListener;
import com.elitecore.corenetvertex.core.alerts.Alerts;
import com.elitecore.corenetvertex.core.transaction.Transaction;
import com.elitecore.corenetvertex.core.transaction.TransactionException;
import com.elitecore.corenetvertex.core.transaction.TransactionFactory;
import com.elitecore.corenetvertex.core.transaction.exception.TransactionErrorCode;
import com.elitecore.corenetvertex.spr.data.SPRFields;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.exceptions.ResultCode;
import com.elitecore.corenetvertex.spr.params.MonetaryRechargeData;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.util.EnumMap;
import java.util.Objects;
import java.util.UUID;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.only;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@RunWith(HierarchicalContextRunner.class)
public class SubscriberValidityRechargeMonetaryBalanceTest {
    private final String requestIpAddress = "0:0:0:0";
    private SubscriberRepository subscriberRepository;
    private long currentTime = System.currentTimeMillis();
    private static final String SUBSCRIBERID = "test";
    private ABMFconfiguration abmFconfiguration;
    private TestSubscriberAwareSPInterface testSubscriberAwareSPInterface;
    private EnumMap<SPRFields, String> updatedProfile;
    @Mock
    private AlertListener alertListener;
    @Mock
    private MonetaryABMFOperationImpl monetaryABMFOperation;
    @Mock
    TransactionFactory transactionFactory;
    @Mock
    Transaction transaction;
    EDRListener subscriberEDListener;

    @Mock
    EDRListener balanceEDListener;

    @Before
    public void setUp() throws Exception {
        MockitoAnnotations.initMocks(this);
        alertListener = mock(AlertListener.class);
        subscriberEDListener = mock(EDRListener.class);
        testSubscriberAwareSPInterface = mock(TestSubscriberAwareSPInterface.class);
        abmFconfiguration = new ABMFconfigurationImpl(1, 100, 10);
        monetaryABMFOperation = mock(MonetaryABMFOperationImpl.class);
        subscriberRepository = new SubscriberRepositoryImpl("id",
                "name",
                transactionFactory,
                alertListener,
                null,
                null,
                abmFconfiguration,
                Collectionz.<String>newArrayList(),
                null,
                null, null, balanceEDListener, subscriberEDListener, null,
                "INR", testSubscriberAwareSPInterface, monetaryABMFOperation);

    }

    @Test
    public void testwhenUpdatedProfilewithExpiryDateIsPresentSubscriberValidityisUpdated() throws Exception {
        long expriyDate = 1571702400000L;
        updatedProfile = createSPRFieldMap(expriyDate);
        doReturn(transaction).when(transactionFactory).createTransaction();
        when(testSubscriberAwareSPInterface.updateProfile(anyString(), any(EnumMap.class), any(Transaction.class))).thenReturn(1);
        subscriberRepository.rechargeMonetaryBalance(null, updatedProfile, SUBSCRIBERID, requestIpAddress);
        verify(testSubscriberAwareSPInterface, times(1)).updateProfile(anyString(), any(EnumMap.class), any(Transaction.class));
        verify(subscriberEDListener, times(1)).updateSubscriberEDR(any(EnumMap.class), anyString(), anyString(), anyString(), anyString());
    }

    @Test
    public void testwhenMonetaryBalanceIsPresentSubscriberValidityisUpdated() throws Exception {
        doReturn(transaction).when(transactionFactory).createTransaction();
        MonetaryRechargeData monetaryRechargeData = new MonetaryRechargeData(SUBSCRIBERID, createMonetaryBalance(), null,
                BigDecimal.ZERO, BigDecimal.ZERO, null, 0, null, null, null);
        subscriberRepository.rechargeMonetaryBalance(monetaryRechargeData, null, SUBSCRIBERID, null);
        verify(monetaryABMFOperation, times(1)).rechargeMonetaryBalance(any(MonetaryRechargeData.class), any(Transaction.class));
        verify(balanceEDListener, times(1)).rechargeMonetaryBalanceEDR(any(MonetaryRechargeData.class));
    }


    @Test(expected = OperationFailedException.class)
    public void whenTransactionIsNullThrowsOperationFailedException() throws Exception {
        when(transactionFactory.createTransaction()).thenReturn(null);
        subscriberRepository.rechargeMonetaryBalance(null, null, SUBSCRIBERID, null);
    }


    @Test
    public void RechargeMonetaryBalanceThrowsTransactionException() throws Exception {
        doReturn(true).when(transactionFactory).isAlive();
        doReturn(transaction).when(transactionFactory).createTransaction();
        doThrow(new TransactionException("Connection not found", TransactionErrorCode.CONNECTION_NOT_FOUND)).when(transaction).begin();
        try {
            subscriberRepository.rechargeMonetaryBalance(null, null, SUBSCRIBERID, null);
        } catch (OperationFailedException e) {
            verify(alertListener, only())
                    .generateSystemAlert(Mockito.anyString(), Mockito.eq(Alerts.DATABASE_CONNECTION_NOT_AVAILABLE), Mockito.anyString(), Mockito.anyString());
        }
    }

    private EnumMap<SPRFields, String> createSPRFieldMap(long expiryDate) throws ParseException, OperationFailedException {

        EnumMap<SPRFields, String> sprFieldMap = new EnumMap<>(SPRFields.class);
        Timestamp dateToTimestamp = getTimestampValue(expiryDate);
        SPRFields.EXPIRY_DATE.validateTimeStampValue(dateToTimestamp);
        sprFieldMap.put(SPRFields.EXPIRY_DATE, String.valueOf(expiryDate));
        return sprFieldMap;
    }

    private Timestamp getTimestampValue(long expiryDate) throws OperationFailedException {
        final String MODULE = "SUB-WSBL-MNGR";
        Timestamp dateToTimestamp = null;
        if (Objects.isNull(expiryDate) == false) {
            try {
                dateToTimestamp = new Timestamp(expiryDate);
            } catch (NumberFormatException e) {
                LogManager.getLogger().error(MODULE, "Error while converting " + expiryDate + " to timestamp");
                throw new OperationFailedException("Error while converting " + expiryDate + " to timestamp", ResultCode.INVALID_INPUT_PARAMETER);
            } catch (Exception e) {
                LogManager.getLogger().error(MODULE, "Error while converting " + expiryDate + " to timestamp");
                throw new OperationFailedException("Error while converting " + expiryDate + " to timestamp", ResultCode.INVALID_INPUT_PARAMETER);
            }
        }
        return dateToTimestamp;
    }

    private MonetaryBalance createMonetaryBalance() {
        MonetaryBalance monetaryBalance = new MonetaryBalance(UUID.randomUUID().toString(), "1", "1"
                , 1, 1, 1, 0, 0, currentTime, currentTime, "INR", MonetaryBalanceType.DEFAULT.name(), currentTime, 0, "", "");
        return monetaryBalance;
    }

}
