package com.elitecore.corenetvertex.pm.rnc.ratecard;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.CommonStatusValues;
import com.elitecore.corenetvertex.constants.RenewalIntervalUnit;
import com.elitecore.corenetvertex.constants.TierRateType;
import com.elitecore.corenetvertex.constants.Uom;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersionDetail;
import com.elitecore.corenetvertex.pd.ratecard.NonMonetaryRateCardData;
import com.elitecore.corenetvertex.pd.ratecard.RateCardData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

@RunWith(HierarchicalContextRunner.class)
public class RateCardFactoryTest {
	private List<String> failReasons = new ArrayList<>();
	private RnCFactory rnCFactory = new RnCFactory();
	private RateCardFactory factory = new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory);

	@Before
	public void setup(){

	}

	public class Monetary{
		public class FailCases{

			@Test
			public void createMonetaryRateCardReturnNullAndSetsFailReasonWhenRateUnitIsNotSet(){
				MonetaryRateCardData monetaryRateCardData = new MonetaryRateCardData();
				monetaryRateCardData.setRateCardData(createRateCardData());
				MonetaryRateCard rnCRateCard = factory.createMonetaryRateCard(monetaryRateCardData, null, null,null, failReasons);
				assertNull(rnCRateCard);
				assertEquals("Rate Unit is not set for Monetary Rate Card: mone", failReasons.get(0));
			}

			@Test
			public void createMonetaryRateCardReturnNullAndSetsFailReasonWhenPulseUnitIsNotSet(){
				MonetaryRateCardData monetaryRateCardData = new MonetaryRateCardData();
				monetaryRateCardData.setRateCardData(createRateCardData());
				monetaryRateCardData.setRateUnit("GB");

				MonetaryRateCard rnCRateCard = factory.createMonetaryRateCard(monetaryRateCardData, null, null,ChargingType.SESSION, failReasons);
				assertNull(rnCRateCard);
				assertEquals("Pulse Unit is not set for Monetary Rate Card: mone", failReasons.get(0));
			}

			@Test
			public void createMonetaryRateCardReturnNullAndSetsFailReasonWhenPulseUnitAndRateUnitDoNotComply(){
				MonetaryRateCardData monetaryRateCardData = new MonetaryRateCardData();
				monetaryRateCardData.setRateCardData(createRateCardData());
				monetaryRateCardData.setRateUnit("GB");
				monetaryRateCardData.setPulseUnit("SECOND");

				MonetaryRateCard rnCRateCard = factory.createMonetaryRateCard(monetaryRateCardData, null, null,null, failReasons);
				assertNull(rnCRateCard);
				assertEquals("Pulse Unit and Rate Units are incompatible with each other for Monetary Rate Card: mone",
						failReasons.get(0));
			}

			@Test
			public void createMonetaryRateCardReturnNullAndSetsFailReasonWhenRateUnitIsNotCompatibleForRateCard(){
				MonetaryRateCardData monetaryRateCardData = new MonetaryRateCardData();
				monetaryRateCardData.setRateCardData(createRateCardData());
				monetaryRateCardData.setRateUnit("GB");
				monetaryRateCardData.setPulseUnit("GB");

				MonetaryRateCard rnCRateCard = factory.createMonetaryRateCard(monetaryRateCardData, null, null,ChargingType.SESSION, failReasons);
				assertNull(rnCRateCard);
				assertEquals("Invalid Pulse Unit: GB for Rate Card: mone",
						failReasons.get(0));
			}

			@Test
			public void createMonetaryRateCardReturnNullAndSetFailReasonWhenThereIsNoVersionDetailInTheDataObject(){
				MonetaryRateCardData monetaryRateCardData = new MonetaryRateCardData();
				monetaryRateCardData.setRateCardData(createRateCardData());
				monetaryRateCardData.setRateUnit("SECOND");
				monetaryRateCardData.setPulseUnit("SECOND");
				monetaryRateCardData.getRateCardData().setRncPackageData(new RncPackageData());

				MonetaryRateCard rnCRateCard = factory.createMonetaryRateCard(monetaryRateCardData, null, null,ChargingType.SESSION, failReasons);
				assertNull(rnCRateCard);
				assertEquals("Rate card details not configured for rate card: mone",
						failReasons.get(0));
			}

			@Test
			public void createMonetaryRateCardSetPartialFailReasonWhenMonetaryRateCardDataIsNull(){
				MonetaryRateCard rnCRateCard = factory.createMonetaryRateCard(null, null, "rcg", ChargingType.SESSION, failReasons);
				assertNull(rnCRateCard);
				assertEquals("Rate card details are not configured for a rate card in rate card group: rcg",
						failReasons.get(0));
			}

			@Test
			public void createMonetaryRateCardFailRateCardWhenRateCardVersionDetailIsNotConfigured() {
				MonetaryRateCardData monetaryRateCardData = new MonetaryRateCardData();
				monetaryRateCardData.setRateCardData(createRateCardData());
				monetaryRateCardData.setRateUnit("SECOND");
				monetaryRateCardData.setPulseUnit("SECOND");
				monetaryRateCardData.getRateCardData().setRncPackageData(new RncPackageData());

				List<com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion> versionList = new ArrayList<>();

				com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion monetaryRateCardVersion = createRateCardVersion();

				versionList.add(monetaryRateCardVersion);

				monetaryRateCardData.setMonetaryRateCardVersions(versionList);

				factory.createMonetaryRateCard(monetaryRateCardData, null, null, ChargingType.SESSION,failReasons);
				assertEquals("Rate card version details not configured for rate card: mone",
						failReasons.get(0));
			}

			@Test
			public void createMonetaryRateCardFailRateCardUnknownRatingBehaviorConfiguredInVersionDetails() {
				MonetaryRateCardData monetaryRateCardData = new MonetaryRateCardData();
				monetaryRateCardData.setRateCardData(createRateCardData());
				monetaryRateCardData.setRateUnit("SECOND");
				monetaryRateCardData.setPulseUnit("SECOND");
				monetaryRateCardData.getRateCardData().setRncPackageData(new RncPackageData());

				List<com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion> versionList = new ArrayList<>();

				com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion monetaryRateCardVersion = createRateCardVersion();

				List<MonetaryRateCardVersionDetail> versionDetailList = new ArrayList<>();
				MonetaryRateCardVersionDetail versionDetail = createMonetaryRateCardVersionDetail();
				versionDetail.setRateType("FAILTHEPARSING");
				versionDetailList.add(versionDetail);

				monetaryRateCardVersion.setMonetaryRateCardVersionDetail(versionDetailList);
				versionList.add(monetaryRateCardVersion);

				monetaryRateCardData.setMonetaryRateCardVersions(versionList);

				factory.createMonetaryRateCard(monetaryRateCardData, null, null, ChargingType.SESSION,failReasons);
				assertEquals("Invalid Rate type: FAILTHEPARSING in RC: mone",
						failReasons.get(0));
			}
		}

		public class SuccessCases{
			@Test
			public void createMonetaryRateCardFailRateCardUnknownRatingBehaviorConfiguredInVersionDetails() {
				MonetaryRateCardData monetaryRateCardData = new MonetaryRateCardData();
				monetaryRateCardData.setRateCardData(createRateCardData());
				monetaryRateCardData.setRateUnit("SECOND");
				monetaryRateCardData.setPulseUnit("SECOND");
				monetaryRateCardData.getRateCardData().setRncPackageData(new RncPackageData());
				monetaryRateCardData.setLabelKey1("CS.CallingStationId");
				monetaryRateCardData.setLabelKey2("CS.CalledStationId");

				List<com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion> versionList = new ArrayList<>();

				com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion monetaryRateCardVersion = createRateCardVersion();

				List<MonetaryRateCardVersionDetail> versionDetailList = new ArrayList<>();
				MonetaryRateCardVersionDetail versionDetail = createMonetaryRateCardVersionDetail();
				versionDetailList.add(versionDetail);

				versionDetail.setSlab1(null);
				versionDetail.setRate1(BigDecimal.ONE);
				versionDetail.setPulse1(3l);

				versionDetail.setPulse2(2l);
				versionDetail.setPulse3(3l);
				versionDetail.setLabel1("12345");
				versionDetail.setLabel1("12345");


				monetaryRateCardVersion.setMonetaryRateCardVersionDetail(versionDetailList);
				versionList.add(monetaryRateCardVersion);

				monetaryRateCardData.setMonetaryRateCardVersions(versionList);

				MonetaryRateCard actual = factory.createMonetaryRateCard(monetaryRateCardData, null, null, ChargingType.SESSION,failReasons);

				ReflectionAssert.assertReflectionEquals(createExpectedRateCard(monetaryRateCardData), actual);
			}
		}
	}

	public class NonMonetary{
		public class FailCases{
			@Test
			public void createNonMonetaryRateCardReturnNullAndSetsFailReasonWhenNonMonetaryRateCardDataNullPassed(){
				NonMonetaryRateCard rnCRateCard = factory.createNonMonetaryRateCard(null, null, null, null,failReasons);
				assertNull(rnCRateCard);
				assertEquals("Rate card details are not configured for a rate card in rate card group: null",
						failReasons.get(0));
			}

			@Test
			public void createNonMonetaryRateCardReturnNullAndSetsFailReasonWhenTimeUniNotDefinedAndTimeQuotaIsSet(){
				NonMonetaryRateCardData data = NonMonetaryRateCardBuilder.createNonMonetaryRateCardData();
				data.setTimeUom(null);
				data.setEvent(null);
                data.setRateCardData(createRateCardData());
				NonMonetaryRateCard rnCRateCard = factory.createNonMonetaryRateCard(data, null, null, null,failReasons);
				assertNull(rnCRateCard);
				assertEquals("Time Unit is not set for Non Monetary Rate Card: mone",
						failReasons.get(0));
			}

			@Test
			public void createNonMonetaryRateCardReturnNullAndSetsFailReasonWhenPulseUnitNotDefinedAndTimeQuotaIsSet(){
				NonMonetaryRateCardData data = NonMonetaryRateCardBuilder.createNonMonetaryRateCardData();
				data.setPulseUom(null);
				data.setEvent(null);
                data.setRateCardData(createRateCardData());
				NonMonetaryRateCard rnCRateCard = factory.createNonMonetaryRateCard(data, null, null, ChargingType.SESSION,failReasons);
				assertNull(rnCRateCard);
				assertEquals("Pulse Unit is not set for Non Monetary Rate Card: mone",
						failReasons.get(0));
			}

            @Test
            public void createNonMonetaryRateCardReturnNullAndSetsFailReasonWhenInValidPulseValueIsProvidedAndTimeQuotaIsSet(){
				NonMonetaryRateCardData data = NonMonetaryRateCardBuilder.createNonMonetaryRateCardData();
				data.setPulse(null);
				data.setEvent(null);
                data.setRateCardData(createRateCardData());
                NonMonetaryRateCard rnCRateCard = factory.createNonMonetaryRateCard(data, null, null, null,failReasons);
                assertNull(rnCRateCard);
                assertEquals("Invalid pulse value is set for Non Monetary Rate Card: mone",
                        failReasons.get(0));
            }

            @Test
            public void createNonMonetaryRateCardReturnNullAndSetsFailReasonWhenPulseUnitAndTimeUnitDoNotComply(){
				NonMonetaryRateCardData data = NonMonetaryRateCardBuilder.createNonMonetaryRateCardData();
				data.setPulseUom(Uom.SECOND.name());
				data.setTimeUom(Uom.GB.name());
				data.setEvent(null);
                data.setRateCardData(createRateCardData());
                NonMonetaryRateCard rnCRateCard = factory.createNonMonetaryRateCard(data, null, null, null,failReasons);
                assertNull(rnCRateCard);
                assertEquals("Pulse Unit and Rate Unit are incompatible with each other for Monetary Rate Card: mone",
                        failReasons.get(0));
            }

			@Test
			public void createNonMonetaryRateCardReturnNullAndSetsFailReasonWhenTimeUnitIsUnsupported(){
				NonMonetaryRateCardData data = NonMonetaryRateCardBuilder.createNonMonetaryRateCardData();
				data.setTimeUom(Uom.GB.name());
				data.setPulseUom(Uom.GB.name());
				data.setEvent(null);
				data.setRateCardData(createRateCardData());
                NonMonetaryRateCard rnCRateCard = factory.createNonMonetaryRateCard(data, null, null, null,failReasons);
                assertNull(rnCRateCard);
                assertEquals("Invalid time unit: GB for Rate Card: mone",
                        failReasons.get(0));
			}

		}

		public class SuccessCases{
			@Test
			public void createNonMonetaryRateCardSuccessWhenNoMisconfiguration() {
				NonMonetaryRateCardData data = NonMonetaryRateCardBuilder.createNonMonetaryRateCardData();
				data.setEvent(null);
				data.setRateCardData(createRateCardData());
				NonMonetaryRateCard rnCRateCard = factory.createNonMonetaryRateCard(data, null, null,null, failReasons);

				assertNotNull(rnCRateCard);
				NonMonetaryRateCard expected = new NonMonetaryRateCard(data.getRateCardData().getId(), data.getRateCardData().getName(),
						data.getRateCardData().getDescription(),  Uom.SECOND, 100L,100L, CommonConstants.QUOTA_UNDEFINED,
						Uom.SECOND, 2L, 2l, null, null, null, null,1, RenewalIntervalUnit.MONTH,CommonStatusValues.DISABLE.isBooleanValue());

				ReflectionAssert.assertReflectionEquals(expected, rnCRateCard);
			}

			@Test
			public void createNonMonetaryRateCardSuccessWhenEventQuotaConfigured() {
				NonMonetaryRateCardData data = NonMonetaryRateCardBuilder.createNonMonetaryRateCardData();
				data.setEvent(100L);
				data.setPulseUom(Uom.EVENT.name());
				data.setRateCardData(createRateCardData());
				NonMonetaryRateCard rnCRateCard = factory.createNonMonetaryRateCard(data, null, null, null,failReasons);

				assertNotNull(rnCRateCard);
				NonMonetaryRateCard expected = new NonMonetaryRateCard(data.getRateCardData().getId(), data.getRateCardData().getName(),
						data.getRateCardData().getDescription(),  Uom.SECOND, CommonConstants.QUOTA_UNDEFINED, CommonConstants.QUOTA_UNDEFINED,100L,
						Uom.EVENT, 1L,1L, null, null, null, null,1,RenewalIntervalUnit.MONTH, CommonStatusValues.DISABLE.isBooleanValue());

				ReflectionAssert.assertReflectionEquals(expected, rnCRateCard);
			}
		}
	}

	private RateCardData createRateCardData(){
		RateCardData rateCardData = new RateCardData();
		rateCardData.setRncPackageData(new RncPackageData());
		rateCardData.setName("mone");
		rateCardData.setScope("LOCAL");
		return rateCardData;
	}

	private com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion createRateCardVersion(){
		com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion version = new com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion();

		version.setEffectiveFromDate(new Timestamp(100));
		version.setId("one");
		return version;
	}

	private MonetaryRateCardVersionDetail createMonetaryRateCardVersionDetail(){
		MonetaryRateCardVersionDetail versionDetail = new MonetaryRateCardVersionDetail();

		versionDetail.setId("version_id");
		versionDetail.setLabel1(null);
		versionDetail.setRate1(BigDecimal.TEN);
		versionDetail.setPulse1(3l);

		return versionDetail;
	}

	private MonetaryRateCard createExpectedRateCard(MonetaryRateCardData monetaryRateCardData ){
		MonetaryRateCard rnCRateCard = new MonetaryRateCard(monetaryRateCardData.getId(),
				monetaryRateCardData.getRateCardData()!=null?monetaryRateCardData.getRateCardData().getName():null,
				monetaryRateCardData.getRateCardData()!=null?monetaryRateCardData.getRateCardData().getDescription():null,
				monetaryRateCardData.getLabelKey1(), monetaryRateCardData.getLabelKey2(),
				createExpectedRateCardVersion(monetaryRateCardData.getMonetaryRateCardVersions())
				, Uom.fromVaue(monetaryRateCardData.getRateUnit()) ,
				Uom.fromVaue(monetaryRateCardData.getPulseUnit()),null,null,null,null);

		return rnCRateCard;
	}

	private List<MonetaryRateCardVersion> createExpectedRateCardVersion(List<com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion> monetaryRateCardVersions){
		List<MonetaryRateCardVersion>  rateCardVersions = new ArrayList<>();

		for(com.elitecore.corenetvertex.pd.ratecard.MonetaryRateCardVersion version : monetaryRateCardVersions){
			MonetaryRateCardVersion rateCardVersion = new MonetaryRateCardVersion(version.getId(),version.getName(),
					createExpectedRatingBehaviours(version.getMonetaryRateCardVersionDetail()), null, null
			,null, null, null, "mone");
					rateCardVersions.add(rateCardVersion);
		}

		return rateCardVersions;
	}

	private List<MonetaryRateCardVeresionDetail> createExpectedRatingBehaviours(List<MonetaryRateCardVersionDetail> monetaryRateCardVersionDetails){
		List<MonetaryRateCardVeresionDetail>  ratingBehaviors = new ArrayList<>();

		for(MonetaryRateCardVersionDetail versionDetail : monetaryRateCardVersionDetails){
			MonetaryRateCardVeresionDetail ratingBehavior = new MonetaryRateCardVeresionDetail(versionDetail.getId(), versionDetail.getLabel1()==null?"":versionDetail.getLabel1(), versionDetail.getLabel2()==null?"": versionDetail.getLabel2(),
					new Timestamp(100), TierRateType.FLAT, createExpectedRateSlabs(versionDetail),null, null, null, null,null,"mone","one",null);
			ratingBehaviors.add(ratingBehavior);
		}

		return ratingBehaviors;
	}

	private List<RateSlab> createExpectedRateSlabs(MonetaryRateCardVersionDetail versionDetail){
		List<RateSlab> rateSlabs = new ArrayList<>();
		RateSlab rateSlabOne;
		RateSlab rateSlabTwo;
		RateSlab rateSlabThree;

		if (versionDetail.getSlab1() == null) {
			versionDetail.setSlab1(CommonConstants.SLAB_UNLIMITED);
		}

		/*if (versionDetail.getSlab2() == null) {
			versionDetail.setSlab2(CommonConstants.SLAB_UNLIMITED);
		}

		if (versionDetail.getSlab3() == null) {
			versionDetail.setSlab3(CommonConstants.SLAB_UNLIMITED);
		}*/

		rateSlabOne = new RateSlab(versionDetail.getSlab1()
				, versionDetail.getPulse1()
				, versionDetail.getRate1()
				, Uom.SECOND
				, Uom.SECOND
				, TierRateType.FLAT, null, null);
		rateSlabs.add(rateSlabOne);

		/*rateSlabTwo = new RateSlab(versionDetail.getSlab2(),
				versionDetail.getPulse2(),
				versionDetail.getRate2(),
				Uom.SECOND,
				Uom.SECOND,
				TierRateType.FLAT);
		rateSlabs.add(rateSlabTwo);

		rateSlabThree = new RateSlab(versionDetail.getSlab3(),
				versionDetail.getPulse3(),
				versionDetail.getRate3(),
				Uom.SECOND,
				Uom.SECOND,
				TierRateType.FLAT);
		rateSlabs.add(rateSlabThree);*/

		return rateSlabs;
	}

	@After
	public void after(){
		failReasons.clear();
	}
}
