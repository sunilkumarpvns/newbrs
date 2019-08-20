package com.elitecore.netvertex.rnc;

import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.QuotaProfileType;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.data.GyServiceUnits;
import com.elitecore.corenetvertex.data.ResetBalanceStatus;
import com.elitecore.corenetvertex.pm.pkg.DataServiceType;
import com.elitecore.corenetvertex.pm.pkg.RatingGroup;
import com.elitecore.corenetvertex.pm.pkg.datapackage.quotaprofile.QuotaProfile;
import com.elitecore.corenetvertex.spr.NonMonetoryBalance;
import com.elitecore.netvertex.gateway.diameter.gy.MSCC;
import com.elitecore.netvertex.gateway.diameter.gy.ReportingReason;
import com.elitecore.netvertex.pm.DummyPolicyRepository;
import com.elitecore.netvertex.pm.RnCQuotaProfileFactory;
import com.elitecore.netvertex.pm.quota.RnCQuotaProfileDetail;
import com.elitecore.netvertex.pm.util.MockBasePackage;
import org.junit.Before;
import org.junit.Test;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;
import java.util.Collections;
import java.util.UUID;

import static com.elitecore.netvertex.core.util.Maps.Entry.newEntry;
import static com.elitecore.netvertex.core.util.Maps.newLinkedHashMap;
import static java.util.Arrays.asList;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;


public class FinalReportedQuotaProcessorTest {

    private FinalReportedQuotaProcessor finalReportedQuotaProcessor;
    private DummyPolicyRepository policyRepository;
    private NonMonetoryBalance nonMonetaryBalance;
    private MSCC quotaReservationEntry;

    @Before
    public void setUp() {
        policyRepository = spy(new DummyPolicyRepository());
        nonMonetaryBalance = createNonMonetaryBalance();
        quotaReservationEntry = new MSCC();
        quotaReservationEntry.setGrantedServiceUnits(new GyServiceUnits());
        MSCC reportedUsage = new MSCC();
        reportedUsage.setReportingReason(ReportingReason.FINAL);
        finalReportedQuotaProcessor = new FinalReportedQuotaProcessor(reportedUsage,
                quotaReservationEntry,
                null,
                nonMonetaryBalance,
                null,
                null,
                policyRepository,
                null,
                null,
                new ReportedUsageSummary(quotaReservationEntry.getRatingGroup(), quotaReservationEntry.getServiceIdentifiers()));



    }

    @Test
    public void skipProcessingWhenPackageNotFound() {
        NonMonetoryBalance beforHandler = nonMonetaryBalance.copy();
        finalReportedQuotaProcessor.handle();
        ReflectionAssert.assertReflectionEquals(beforHandler, nonMonetaryBalance);
    }

    private NonMonetoryBalance createNonMonetaryBalance() {
        return new NonMonetoryBalance.NonMonetaryBalanceBuilder(UUID.randomUUID().toString(),
                0,
                UUID.randomUUID().toString(),
                0,
                UUID.randomUUID().toString(),
                UUID.randomUUID().toString(),
                0,
                UUID.randomUUID().toString(), ResetBalanceStatus.NOT_RESET, null, null).build();
    }

    @Test
    public void skipProcessingWhenQuotaProfileNotFound() {
        MockBasePackage basePackage = MockBasePackage.create(nonMonetaryBalance.getPackageId(), UUID.randomUUID().toString());
        policyRepository.addBasePackage(basePackage);
        NonMonetoryBalance beforHandler = nonMonetaryBalance.copy();

        finalReportedQuotaProcessor.handle();
        ReflectionAssert.assertReflectionEquals(beforHandler, nonMonetaryBalance);
    }

    @Test
    public void skipProcessingWhenQuotaProfileDetail() {
        policyRepository.addBasePackage(createBasePackage());

        NonMonetoryBalance beforHandler = nonMonetaryBalance.copy();
        finalReportedQuotaProcessor.handle();
        ReflectionAssert.assertReflectionEquals(beforHandler, nonMonetaryBalance);
    }

    @Test
    public void checkFromTopUpIfPackageNotFoundFromDataPackages() {
        finalReportedQuotaProcessor.handle();
        verify(policyRepository, times(1)).getQuotaTopUpById(nonMonetaryBalance.getPackageId());
    }

    private MockBasePackage createBasePackage() {
        MockBasePackage mockBasePackage = MockBasePackage.create(nonMonetaryBalance.getPackageId(), UUID.randomUUID().toString());
        mockBasePackage.quotaProfileTypeIsRnC();

        RatingGroup ratingGroup = new RatingGroup(UUID.randomUUID().toString(), UUID.randomUUID().toString(), "", nonMonetaryBalance.getRatingGroupId() + 1);
        RnCQuotaProfileDetail rnCQuotaProfileDetail = (RnCQuotaProfileDetail) new RnCQuotaProfileFactory(nonMonetaryBalance.getQuotaProfileId(), UUID.randomUUID().toString())
                .ratingGroup(ratingGroup)
                .level(nonMonetaryBalance.getLevel())
                .dataServiceType(new DataServiceType(UUID.randomUUID().toString(),
                        UUID.randomUUID().toString(),
                        nonMonetaryBalance.getServiceId(),
                        Collections.emptyList(),
                        Arrays.asList(ratingGroup)))
                .randomBalanceWithRate().create();
        QuotaProfile quotaProfile = new QuotaProfile(rnCQuotaProfileDetail.getName()
                , mockBasePackage.getId(),
                nonMonetaryBalance.getQuotaProfileId()
                , BalanceLevel.HSQ
                ,2, RenewalIntervalUnit.MONTH, QuotaProfileType.RnC_BASED,
                asList(newLinkedHashMap(newEntry(rnCQuotaProfileDetail.getServiceId(), rnCQuotaProfileDetail))), CommonStatusValues.DISABLE.isBooleanValue(),CommonStatusValues.DISABLE.isBooleanValue());

        mockBasePackage.mockQuotaProfie(quotaProfile);

        return mockBasePackage;
    }
}