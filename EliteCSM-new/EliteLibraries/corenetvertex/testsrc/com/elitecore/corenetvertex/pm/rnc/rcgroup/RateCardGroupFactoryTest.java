package com.elitecore.corenetvertex.pm.rnc.rcgroup;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.RateCardType;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.NonMonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.ratecardgroup.TimeSlotRelationData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.RnCPkgBuilder;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.MonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCard;
import com.elitecore.corenetvertex.pm.rnc.ratecard.NonMonetaryRateCardBuilder;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.ArrayList;
import java.util.List;

@RunWith(HierarchicalContextRunner.class)
public class RateCardGroupFactoryTest {
    private List<String> failReasons = new ArrayList<>();
    private List<String> partialFailReasons = new ArrayList<>();
    private RnCFactory rnCFactory = new RnCFactory();
    private RateCardFactory factory = Mockito.spy(new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory));
    private RateCardGroupFactory rateCardGroupFactory = new RateCardGroupFactory(factory, rnCFactory);
    private static final String INVALID_DAY_OF_WEEK = "AAAA";
    private static final String INVALID_TIME_PERIOD = "XXXX";

    @Before
    public void setup(){
    }

    public class SuccessCases{
        @Test
        public void createPeakRateCardIsNotNull() throws InvalidExpressionException{
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithMonetaryPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);
            RateCardGroup actual = rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()), failReasons);

            ReflectionAssert.assertLenientEquals(createExpectedRateCardGroup(rateCardGroupData),
                    actual);
            Assert.assertNotNull(actual.getPeakRateCard());
        }

        @Test
        public void createNonMonetaryRateCardSuccessfullyWhenRateCardTypeIsNonMonetary() throws InvalidExpressionException{
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithNonMonetaryPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);

            RateCardGroup actual = rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            Assert.assertTrue( actual.getPeakRateCard() instanceof NonMonetaryRateCard);
        }


        @Test
        public void createOffPeakRateCardIsNotNull() throws InvalidExpressionException{
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithMonetaryOffPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);
            RateCardGroup actual = rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            ReflectionAssert.assertLenientEquals(createExpectedRateCardGroup(rateCardGroupData),
                    actual);
            Assert.assertNotNull(actual.getOffPeakRateCard());
        }

        @Test
        public void createOffPeakRateCardOfTypeNonMonetary() throws InvalidExpressionException{
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithNonMonetaryOffPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);

            RateCardGroup actual = rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            Assert.assertTrue( actual.getOffPeakRateCard() instanceof NonMonetaryRateCard);
        }
    }

    public class FailCases{

        @Test
        public void createFailsWhenUnabledToParseAdvanceCondition(){
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithMonetaryPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);
            rateCardGroupData.setAdvanceCondition("Not going to parse");

            rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            Assert.assertEquals("Advance Condition parsing failed for Rate Card Group: RateCardGroupTest",
                    failReasons.get(0));
        }

        @Test
        public void createFailsWhenNoPeakRateCardIsConfigured(){
            RateCardGroupData rateCardGroupData = new RateCardGroupData();
            rateCardGroupData.setName("RateCardGroupTest");
            rateCardGroupData.setAdvanceCondition("\"1\" = \"1\"");

            rateCardGroupFactory.create(rateCardGroupData, null, null,null,failReasons);

            Assert.assertEquals("No peak rate card is configured for Rate Card Group: RateCardGroupTest",
                    failReasons.get(0));
        }

        @Test
        public void createFailReasonWhenRateCardParsingFails(){
            RateCardGroupData rateCardGroupData = new RateCardGroupData();
            rateCardGroupData.setName("Random");
            rateCardGroupData.setAdvanceCondition("\"1\" = \"1\"");
            RateCardData rateCardData = new RateCardData();
            rateCardData.setType(RateCardType.MONETARY.name());
            rateCardData.setMonetaryRateCardData(new MonetaryRateCardData());
            rateCardGroupData.setPeakRateRateCard(rateCardData);

            Mockito.doAnswer((InvocationOnMock invocation)->{
                ((List)(invocation.getArguments()[4])).add("failed");
                return null;
            }).when(factory).createMonetaryRateCard(Mockito.any(MonetaryRateCardData.class),Mockito.anyString(), Mockito.anyString(), Mockito.any(ChargingType.class), Mockito.any(ArrayList.class));

            rateCardGroupFactory.create(rateCardGroupData, null, null,null,failReasons);

            Assert.assertEquals("Rate card parsing failed. Reason: [failed]",
                    failReasons.get(0));
        }

        @Test
        public void createMonetaryPeakRateCardFailsWhenInvalidRateCardTypeConfigured(){
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithMonetaryPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);
            rateCardGroupData.getPeakRateRateCard().setType("XXX");
            RateCardGroup actual = rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            Assert.assertNull(actual.getPeakRateCard());
            Assert.assertEquals("Unknown rate card type XXX is set for rate card RateCardGroupTest in Rate Card Group: RateCardGroupTest",
                    failReasons.get(0));
        }

        @Test
        public void createMonetaryOffPeakRateCardFailsWhenInvalidRateCardTypeConfigured(){
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithMonetaryOffPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);
            rateCardGroupData.getOffPeakRateRateCard().setType("XXX");
            RateCardGroup actual = rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            Assert.assertNull(actual.getOffPeakRateCard());
            Assert.assertEquals("Unknown rate card type XXX is set for rate card RateCardGroupTest in Rate Card Group: RateCardGroupTest",
                    failReasons.get(0));
        }

        @Test
        public void createNonMonetaryPeakRateCardFailsWhenInvalidRateCardTypeConfigured(){
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithNonMonetaryPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);
            rateCardGroupData.getPeakRateRateCard().setType("XXX");
            RateCardGroup actual = rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            Assert.assertNull(actual.getOffPeakRateCard());
            Assert.assertEquals("Unknown rate card type XXX is set for rate card RateCardGroupTest in Rate Card Group: RateCardGroupTest",
                    failReasons.get(0));
        }

        @Test
        public void createNonMonetaryOffPeakRateCardFailsWhenInvalidRateCardTypeConfigured(){
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithNonMonetaryOffPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);
            rateCardGroupData.getOffPeakRateRateCard().setType("XXX");
            RateCardGroup actual = rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            Assert.assertNull(actual.getOffPeakRateCard());
            Assert.assertEquals("Unknown rate card type XXX is set for rate card RateCardGroupTest in Rate Card Group: RateCardGroupTest",
                    failReasons.get(0));
        }

        @Test
        public void createOffPeakRateCardFailsWhenNoTimeSlotConfigured(){
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithNonMonetaryOffPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);
            rateCardGroupData.setTimeSlotRelationData(null);
            rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            Assert.assertEquals("At-least one time slot is required to be configured with Off-Peak rate card associated with Rate Card Group: RateCardGroupTest",
                    failReasons.get(0));
        }


        @Test
        public void createOffPeakRateCardFailsWhenInvalidTimeSlotConfigured(){
            RncPackageData pkgData = RnCPkgBuilder.rncBasePackageWithNonMonetaryOffPeakRateCard();
            RateCardGroupData rateCardGroupData = pkgData.getRateCardGroupData().get(0);
            rateCardGroupData.setTimeSlotRelationData(getInvalidTimeSlotRelations());
            rateCardGroupFactory.create(rateCardGroupData, null, null,ChargingType.fromName(pkgData.getChargingType()),failReasons);

            Assert.assertEquals("Invalid TimeSlot Configuration. Reason: Invalid Input: "+INVALID_DAY_OF_WEEK+", Invalid value:"+INVALID_DAY_OF_WEEK+", Day of Week must be between 1 to 7",
                    failReasons.get(0));
        }


    }
    private RateCardGroup createExpectedRateCardGroup(RateCardGroupData rateCardGroupData) throws InvalidExpressionException{
        RateCardGroup rateCardGroup = new RateCardGroup(rateCardGroupData.getId(),
                rateCardGroupData.getName(),
                rateCardGroupData.getDescription(),
                rateCardGroupData.getAdvanceCondition()!=null?(LogicalExpression) Compiler.getDefaultCompiler().parseExpression(rateCardGroupData.getAdvanceCondition()):null,
                null, null,null, null,1,null,null);

        return rateCardGroup;
    }

    public List<TimeSlotRelationData> getInvalidTimeSlotRelations() {
        List<TimeSlotRelationData> timeSlotRelations = Collectionz.newArrayList();
        TimeSlotRelationData timeSlotRelationData = new TimeSlotRelationData();
        timeSlotRelationData.setDayOfWeek(INVALID_DAY_OF_WEEK);
        timeSlotRelationData.setTimePeriod(INVALID_TIME_PERIOD);
        timeSlotRelations.add(timeSlotRelationData);
        return timeSlotRelations;
    }

    @After
    public void after(){
        failReasons.clear();
        partialFailReasons.clear();
    }
}
