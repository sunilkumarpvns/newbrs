package com.elitecore.corenetvertex.pm.pkg.factory;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.DataUnit;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionDetailData;
import com.elitecore.corenetvertex.pkg.ratecard.DataRateCardVersionRelationData;
import com.elitecore.corenetvertex.pm.PkgDataBuilder;
import com.elitecore.corenetvertex.pm.pkg.PackageFactory;
import com.elitecore.corenetvertex.pm.pkg.datapackage.qosprofile.rnc.ratecard.DataRateCard;
import com.elitecore.corenetvertex.pm.util.RatingUtility;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class DataMonetaryRateCardFactoryTest {
    private static final PackageFactory PACKAGE_FACTORY = new PackageFactory();
    private DataMonetaryRateCardFactory dataMonetaryRateCardFactory;
    private DataRateCardData dataRateCardData;
    private  List<String> failReasons;

    @Before
    public void before() {
        dataRateCardData = PkgDataBuilder.createRateCardData();
        dataMonetaryRateCardFactory = new DataMonetaryRateCardFactory(PACKAGE_FACTORY);
        failReasons = new ArrayList<>();
    }

    @Test
    public void testFailReasonIsAddedWhenAnyVersionIsNotCreated() {
        dataRateCardData.setDataRateCardVersionRelationData(new ArrayList<>());
        dataMonetaryRateCardFactory.createDataMonetaryRateCard(dataRateCardData, failReasons);
        Assert.assertEquals(failReasons.size(), 1);
    }

    @Test
    public void testFailReasonIsAddedWhenRateUoMisNull() {
        dataRateCardData.setRateUnit(null);
        dataMonetaryRateCardFactory.createDataMonetaryRateCard(dataRateCardData, failReasons);
        Assert.assertEquals(failReasons.size(), 1);
        Assert.assertEquals("Monetary rate card (RateCard) parsing fail. " +
                "Cause by: rate Uom is not configurred", failReasons.get(0));
    }

    @Test
    public void testFailReasonIsAddedWhenPulseUoMisNull() {
        dataRateCardData.setPulseUnit(null);
        dataMonetaryRateCardFactory.createDataMonetaryRateCard(dataRateCardData, failReasons);
        Assert.assertEquals(failReasons.size(), 1);
        Assert.assertEquals("Monetary rate card (RateCard) parsing fail. " +
                "Cause by: pulse Uom is not configurred", failReasons.get(0));
    }

    @Test
    public void testFailReasonIsAddedWhenUnsupportedRateUoMeventisConfigurred() {
        dataRateCardData.setRateUnit(Uom.EVENT.getValue());
        dataRateCardData.setPulseUnit(Uom.EVENT.getValue());
        dataMonetaryRateCardFactory.createDataMonetaryRateCard(dataRateCardData, failReasons);
        Assert.assertEquals(failReasons.size(), 1);
        Assert.assertEquals("Monetary rate card (RateCard)"
                + " parsing fail. Cause by: Unsupported rate UOM: EVENT", failReasons.get(0));
    }


    @Test
    public void testFailReasonIsAddedWhenRatingTypeIsNotConfigurred() {
        DataRateCardVersionDetailData dataRateCardVersionDetailData = PkgDataBuilder.createDataRateCardVersionDetailData();
        dataRateCardVersionDetailData.setRateType(null);
        DataRateCardVersionRelationData dataRateCardVersionRelationData = PkgDataBuilder.createDataRateCardVersionRelationData();
        dataRateCardVersionRelationData.setDataRateCardVersionDetailDataList(Arrays.asList(dataRateCardVersionDetailData));
        dataRateCardData.setDataRateCardVersionRelationData(Arrays.asList(dataRateCardVersionRelationData));
        dataMonetaryRateCardFactory.createDataMonetaryRateCard(dataRateCardData, failReasons);
        Assert.assertEquals(failReasons.size(), 1);
        Assert.assertEquals("Monetary rate card (RateCard)"
                + " parsing fail. Cause by: rate type is not defined", failReasons.get(0));
    }

    @Test
    public void testRateCardIsCreatedWhenAllTheConditionsSatisfy() {

        DataRateCard dataRateCard = dataMonetaryRateCardFactory.createDataMonetaryRateCard(dataRateCardData, failReasons);

        DataRateCard expectedDataRateCard = PkgDataBuilder.createExpectedDataMonetaryRateCard(dataRateCardData);

        ReflectionAssert.assertReflectionEquals(expectedDataRateCard, dataRateCard);
    }

    @Test
    public void testDataRateCardIsNotCreatedWhenFailReasonsListIsNotEmpty() {
        failReasons.add("Fail Reason");
        DataRateCard dataRateCard = dataMonetaryRateCardFactory.createDataMonetaryRateCard(dataRateCardData, failReasons);
        Assert.assertEquals(null, dataRateCard);
    }

    @Test
    public void testUnlimitedSlabValueIsAssignedToSlab1WhenSlab1ValueIsNotDefined() {
        dataRateCardData.getDataRateCardVersionRelationData().get(0).getDataRateCardVersionDetailDataList().
                get(0).setSlab1(null);

        DataRateCard dataRateCard = dataMonetaryRateCardFactory.createDataMonetaryRateCard(dataRateCardData, failReasons);

        DataRateCard expectedDataRateCard = PkgDataBuilder.createExpectedDataMonetaryRateCard(dataRateCardData);

        long expectedSlab1Value = expectedDataRateCard.getRateCardVersions().get(0).getVersionDetails().get(0).
                getSlabs().get(0).getSlabValue();

        long actualSlab1Value = dataRateCard.getRateCardVersions().get(0).getVersionDetails().get(0).
                getSlabs().get(0).getSlabValue();


        Assert.assertEquals(expectedSlab1Value, actualSlab1Value);
    }

}

