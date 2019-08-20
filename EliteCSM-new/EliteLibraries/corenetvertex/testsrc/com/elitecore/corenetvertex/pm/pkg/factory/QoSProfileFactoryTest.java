package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.corenetvertex.constants.BalanceLevel;
import com.elitecore.corenetvertex.pkg.qos.QosProfileData;
import com.elitecore.corenetvertex.pkg.qos.QosProfileDetailData;
import com.elitecore.corenetvertex.pkg.ratinggroup.RatingGroupData;
import com.elitecore.corenetvertex.pkg.rnc.RncProfileData;
import com.elitecore.corenetvertex.pm.PkgDataBuilder;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

public class QoSProfileFactoryTest {

    private PackageFactory packageFactory;
    private DataMonetaryRateCardFactory dataMonetaryRateCardFactory;
    private QoSProfileFactory qosProfileFactory;
    @Mock private UMBasedQuotaProfileFactory umBasedQuotaProfileFactory;
    @Mock private SyBasedQuotaProfileFactory syBasedQuotaProfileFactory;
    private RncProfileFactory rncProfileFactory;
    @Mock private QoSProfileDetailFactory qoSProfileDetailFactory;
    @Mock private RatingGroupFactory ratingGroupFactory;
    @Mock private DataServiceTypeFactory dataServiceTypeFactory;
	private String pccProfileName = "pccProfileName";

    @Before
    public void before() {
        packageFactory = new PackageFactory();
        dataMonetaryRateCardFactory = spy (new DataMonetaryRateCardFactory(packageFactory));
        rncProfileFactory = spy(new RncProfileFactory(packageFactory, ratingGroupFactory, dataServiceTypeFactory));
        qosProfileFactory = new QoSProfileFactory(umBasedQuotaProfileFactory, syBasedQuotaProfileFactory, qoSProfileDetailFactory,
                packageFactory, rncProfileFactory, dataMonetaryRateCardFactory);
    }

    @Test
    public void testCreateDataRateCardMethodIsCalledWhenRnCProfileDataIsNotAttachedAndRateCardISAttached() {
        QosProfileData qosProfileData = PkgDataBuilder.createQoSProfileData();
        List<String> qosProfileFailReasons = new ArrayList<>();
        List<String> quotaProfileFailReasons = new ArrayList<>();
        List<String> dataRateCardFailReasons = new ArrayList<>();
        List<String> qosProfilePartialFailReasons = new ArrayList<>();
        RatingGroupData ratingGroupData = new RatingGroupData();

        qosProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, Arrays.asList(ratingGroupData));

        verify(dataMonetaryRateCardFactory, times(1)).createDataMonetaryRateCard(qosProfileData.getRateCardData(), dataRateCardFailReasons);

        verify(rncProfileFactory, times(0)).create(qosProfileData.getRncProfileData(), qosProfileData.getName(), quotaProfileFailReasons);
    }

    @Test
    public void testCreateDataRateCardMethodIsNotCalledWhenRnCProfileDataIsAttachedAndRateCardIsNotAttached() {
        QosProfileData qosProfileData = PkgDataBuilder.createQoSProfileData();
        RncProfileData rncProfileData = new RncProfileData();
        rncProfileData.setName(pccProfileName);
        rncProfileData.setBalanceLevel(BalanceLevel.HSQ.name());
        rncProfileData.setFupLevel(Arrays.asList(new Integer(0)));
        rncProfileData.setQosProfiles(Arrays.asList(qosProfileData));
        qosProfileData.setRncProfileData(rncProfileData);
        qosProfileData.setRateCardData(null);
        List<String> qosProfileFailReasons = new ArrayList<>();
        List<String> quotaProfileFailReasons = new ArrayList<>();
        quotaProfileFailReasons.add("No quota profile details configured for quota profile: "+rncProfileData.getName());
        List<String> dataRateCardFailReasons = new ArrayList<>();
        List<String> qosProfilePartialFailReasons = new ArrayList<>();
        RatingGroupData ratingGroupData = new RatingGroupData();

        qosProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, Arrays.asList(ratingGroupData));

        verify(dataMonetaryRateCardFactory, times(0)).createDataMonetaryRateCard(qosProfileData.getRateCardData(), dataRateCardFailReasons);

        verify(rncProfileFactory, times(1)).create(qosProfileData.getRncProfileData(), qosProfileData.getName(), quotaProfileFailReasons);
    }

    private QosProfileDetailData createQosProfileData(QosProfileData qosProfileData){
        QosProfileDetailData qosProfileDetailData = spy(new QosProfileDetailData());
        qosProfileDetailData.setAction(0);
        doReturn(qosProfileData).when(qosProfileDetailData).getQosProfile();
        return qosProfileDetailData;
    }

    @Test
    public void createQoSProfileReturnsFailReasonWhenRnCQuotaProfileFupLevelAndQOSProfileFupLevelMisMatch() {
        QosProfileData qosProfileData = PkgDataBuilder.createQoSProfileData();
        RncProfileData rncProfileData = new RncProfileData();
        rncProfileData.setName("Profile Data");
        rncProfileData.setBalanceLevel(BalanceLevel.HSQ.name());
        rncProfileData.setFupLevel(Arrays.asList(new Integer(0)));
        rncProfileData.setQosProfiles(Arrays.asList(qosProfileData));
        qosProfileData.setRncProfileData(rncProfileData);
        qosProfileData.setRateCardData(null);

        List<QosProfileDetailData> qosProfileDetailDataList = new ArrayList<>();
        qosProfileDetailDataList.add(createQosProfileData(qosProfileData));
        qosProfileDetailDataList.add(createQosProfileData(qosProfileData));
        qosProfileData.setQosProfileDetailDataList(qosProfileDetailDataList);

        List<String> qosProfileFailReasons = new ArrayList<>();
        List<String> qosProfilePartialFailReasons = new ArrayList<>();
        RatingGroupData ratingGroupData = new RatingGroupData();

        qosProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, Arrays.asList(ratingGroupData));
        Assert.assertTrue(qosProfileFailReasons.contains("FUP levels configured for QOS and Quota Profile ("
                + rncProfileData.getName() + ") do not match"));
    }

    @Test
    public void skipFupLevelValidationWhenQoSProfileDetailNotConfigured() {
        QosProfileData qosProfileData = PkgDataBuilder.createQoSProfileData();
        RncProfileData rncProfileData = new RncProfileData();
        rncProfileData.setName("Profile Data");
        rncProfileData.setBalanceLevel(BalanceLevel.HSQ.name());
        rncProfileData.setFupLevel(Arrays.asList(new Integer(0)));
        rncProfileData.setQosProfiles(Arrays.asList(qosProfileData));
        qosProfileData.setRncProfileData(rncProfileData);
        qosProfileData.setRateCardData(null);

        List<QosProfileDetailData> qosProfileDetailDataList = new ArrayList<>();
        qosProfileData.setQosProfileDetailDataList(qosProfileDetailDataList);

        List<String> qosProfileFailReasons = new ArrayList<>();
        List<String> qosProfilePartialFailReasons = new ArrayList<>();
        RatingGroupData ratingGroupData = new RatingGroupData();

        qosProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, Arrays.asList(ratingGroupData));
        Assert.assertFalse(qosProfileFailReasons.contains("FUP levels configured for QOS and Quota Profile ("
                + rncProfileData.getName() + ") do not match"));
    }

    @Test
    public void testCreateDataRateCardMethodIsNotCalledWhenRnCProfileDataIsAttachedAndRateCardIsAttached() {
        QosProfileData qosProfileData = PkgDataBuilder.createQoSProfileData();
        RncProfileData rncProfileData = new RncProfileData();
        rncProfileData.setName(pccProfileName);
        rncProfileData.setBalanceLevel(BalanceLevel.HSQ.name());
        rncProfileData.setFupLevel(Arrays.asList(new Integer(0)));
        rncProfileData.setQosProfiles(Arrays.asList(qosProfileData));
        qosProfileData.setRncProfileData(rncProfileData);

        List<String> qosProfileFailReasons = new ArrayList<>();
        List<String> quotaProfileFailReasons = new ArrayList<>();
        quotaProfileFailReasons.add("No quota profile details configured for quota profile: "+rncProfileData.getName());

        List<String> dataRateCardFailReasons = new ArrayList<>();
        List<String> qosProfilePartialFailReasons = new ArrayList<>();

        RatingGroupData ratingGroupData = new RatingGroupData();

        qosProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, Arrays.asList(ratingGroupData));

        verify(rncProfileFactory, times(1)).create(qosProfileData.getRncProfileData(), qosProfileData.getName(), quotaProfileFailReasons);

        verify(dataMonetaryRateCardFactory, times(0)).createDataMonetaryRateCard(qosProfileData.getRateCardData(), dataRateCardFailReasons);
    }

    @Test
    public void testFailReasonIsAddedWhenNoneOfQuotaProfileOrRateCardIsConfigured() {
        QosProfileData qosProfileData = PkgDataBuilder.createQoSProfileData();
        qosProfileData.setRateCardData(null);
        qosProfileData.setRncProfileData(null);

        List<String> qosProfileFailReasons = new ArrayList<>();
        List<String> quotaProfileFailReasons = new ArrayList<>();

        List<String> qosProfilePartialFailReasons = new ArrayList<>();
        List<String> dataRateCardFailReasons = new ArrayList<>();
        RatingGroupData ratingGroupData = new RatingGroupData();

        qosProfileFactory.createQoSProfile(qosProfileData, qosProfileFailReasons, qosProfilePartialFailReasons, Arrays.asList(ratingGroupData));

        verify(rncProfileFactory, times(0)).create(qosProfileData.getRncProfileData(), pccProfileName, quotaProfileFailReasons);

        verify(dataMonetaryRateCardFactory, times(0)).createDataMonetaryRateCard(qosProfileData.getRateCardData(), dataRateCardFailReasons);

        assertEquals("Either Quota profile or Rate card is mandatory.", qosProfileFailReasons.get(0));
    }
}
