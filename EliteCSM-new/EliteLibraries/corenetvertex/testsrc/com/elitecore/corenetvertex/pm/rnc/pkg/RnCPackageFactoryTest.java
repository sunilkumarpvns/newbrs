package com.elitecore.corenetvertex.pm.rnc.pkg;

import com.elitecore.commons.base.Strings;
import com.elitecore.corenetvertex.constants.PkgStatus;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pd.ratecardgroup.RateCardGroupData;
import com.elitecore.corenetvertex.pd.rncpackage.RncPackageData;
import com.elitecore.corenetvertex.pkg.ChargingType;
import com.elitecore.corenetvertex.pkg.PkgMode;
import com.elitecore.corenetvertex.pkg.PkgType;
import com.elitecore.corenetvertex.pkg.RnCPkgType;
import com.elitecore.corenetvertex.pm.rnc.RnCFactory;
import com.elitecore.corenetvertex.pm.rnc.notification.ThresholdNotificationSchemeFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardFactory;
import com.elitecore.corenetvertex.pm.rnc.ratecard.RateCardVersionFactory;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroup;
import com.elitecore.corenetvertex.pm.rnc.rcgroup.RateCardGroupFactory;
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
public class RnCPackageFactoryTest {
    private List<String> failReasons = new ArrayList<>();
    private List<String> partialFailReasons = new ArrayList<>();
    private RnCFactory rnCFactory = new RnCFactory();
    private RateCardFactory factory = new RateCardFactory(new RateCardVersionFactory(rnCFactory),rnCFactory);
    private RateCardGroupFactory rateCardGroupFactory = new RateCardGroupFactory(factory,rnCFactory);
    private ThresholdNotificationSchemeFactory thresholdNotificationSchemeFactory = new ThresholdNotificationSchemeFactory(rnCFactory);
    private RnCPackageFactory rnCPackageFactory = new RnCPackageFactory(rateCardGroupFactory,rnCFactory,thresholdNotificationSchemeFactory);

    @Before
    public void setup(){
        rateCardGroupFactory = Mockito.spy(rateCardGroupFactory);
    }

    public class FailCases{
        @Test
        public void createFailWhenThereIsNoRateCardGroupIsSetInRnCPackage(){
            RncPackageData data = new RncPackageData();
            data.setName("new");
            data.setChargingType(ChargingType.SESSION.name());

            RnCPackage rnCPackage = rnCPackageFactory.create(data);

            Assert.assertEquals("RnC package parsing failed. Reason: [Rate Card Group is not configured]", rnCPackage.getFailReason());
            Assert.assertEquals(PolicyStatus.FAILURE.status, rnCPackage.getPolicyStatus().status);
        }

        @Test
        public void createStatusIsFailWhenItReturnsFailReasonsFromRCGFactory(){
            RncPackageData data = new RncPackageData();
            data.setName("new");
            data.setChargingType(ChargingType.SESSION.name());

            RateCardGroupData rcg = createRateCardGroup();
            List<RateCardGroupData> rcgList = new ArrayList<>();

            rcgList.add(rcg);

            data.setRateCardGroupData(rcgList);

            Mockito.doAnswer((InvocationOnMock invocation)->{
                ((List)(invocation.getArguments()[4])).add("failed");
                return null;
            }).when(rateCardGroupFactory).create(Mockito.any(RateCardGroupData.class),Mockito.anyString(), Mockito.anyString(), Mockito.any(), Mockito.any(ArrayList.class));
            rnCPackageFactory = new RnCPackageFactory(rateCardGroupFactory,rnCFactory,thresholdNotificationSchemeFactory);

            RnCPackage rnCPackage = rnCPackageFactory.create(data);

            Assert.assertEquals("RnC package parsing failed. Reason: [Rate Card Group parsing failed. Reason: [failed]]",
                    rnCPackage.getFailReason());
            Assert.assertEquals(PolicyStatus.FAILURE.status, rnCPackage.getPolicyStatus().status);
        }
    }

    public class SuccessCases{
        @Test
        public void createRnCPackageObjectWithSuccessStatusWhenThereIsNoFailOrPartialFailReason(){
            RncPackageData data = new RncPackageData();
            data.setName("new");
            data.setStatus(PkgStatus.ACTIVE.name());
            data.setMode(PkgMode.TEST.name());
            data.setType(PkgType.BASE.name());
            data.setChargingType(ChargingType.SESSION.name());

            RateCardGroupData rcg = createRateCardGroup();
            List<RateCardGroupData> rcgList = new ArrayList<>();

            rcgList.add(rcg);

            data.setRateCardGroupData(rcgList);

            Mockito.doAnswer((InvocationOnMock invocation)-> createExpectedRateCardGroup())
                    .when(rateCardGroupFactory).create(Mockito.any(RateCardGroupData.class), Mockito.anyString(), Mockito.anyString(), Mockito.any(ChargingType.class),Mockito.any(ArrayList.class));
            rnCPackageFactory = new RnCPackageFactory(rateCardGroupFactory,rnCFactory,thresholdNotificationSchemeFactory);

            RnCPackage rnCPackage = rnCPackageFactory.create(data);

            ReflectionAssert.assertLenientEquals(createExpectedRnCPackage(data), rnCPackage);
        }


        @Test
        public void createRnCPackageWithGroupListWhentTheListIsNotEmptyInDataObject(){
            RncPackageData data = new RncPackageData();
            data.setName("new");
            data.setStatus(PkgStatus.ACTIVE.name());
            data.setMode(PkgMode.TEST.name());
            data.setType(PkgType.BASE.name());
            data.setChargingType(ChargingType.SESSION.name());
            data.setGroups("group,one,two");

            RateCardGroupData rcg = createRateCardGroup();
            List<RateCardGroupData> rcgList = new ArrayList<>();

            rcgList.add(rcg);

            data.setRateCardGroupData(rcgList);

            Mockito.doAnswer((InvocationOnMock invocation)-> createExpectedRateCardGroup())
                    .when(rateCardGroupFactory).create(Mockito.any(RateCardGroupData.class), Mockito.anyString(), Mockito.anyString(), Mockito.any(ChargingType.class),Mockito.any(ArrayList.class));
            rnCPackageFactory = new RnCPackageFactory(rateCardGroupFactory,rnCFactory,thresholdNotificationSchemeFactory);

            RnCPackage rnCPackage = rnCPackageFactory.create(data);

            ReflectionAssert.assertLenientEquals(createExpectedRnCPackage(data), rnCPackage);
        }
    }


    private RnCPackage createExpectedRnCPackage(RncPackageData rncPackageData){
        RnCPackage rnCPackage = new RnCPackage(
                rncPackageData.getId(), rncPackageData.getName(), rncPackageData.getDescription(),
                rncPackageData.getGroups()==null?new ArrayList<String>():createGroupList(rncPackageData.getGroups()), createExpectedRateCardGroupList(),null,
                rncPackageData.getTag(), RnCPkgType.BASE,
                PkgMode.TEST, PkgStatus.ACTIVE, PolicyStatus.SUCCESS,
                null, null, ChargingType.SESSION,rncPackageData.getCurrency()
        );

        return rnCPackage;
    }

    private List<String> createGroupList(String groups) {
        List<String> groupList = new ArrayList<>();
        for(String group: groups.split(",")){
            if(Strings.isNullOrBlank(group)==false){
                groupList.add(group);
            }
        }
        return groupList;
    }

    private List<RateCardGroup> createExpectedRateCardGroupList() {
        List<RateCardGroup> list = new ArrayList<>();
        list.add(createExpectedRateCardGroup());
        return list;
    }

    private RateCardGroup createExpectedRateCardGroup(){
        RateCardGroup rateCardGroup = new RateCardGroup(
                "id", "name", "description", null, null, null,null, null,1,null,null
        );
        return rateCardGroup;
    }

    private RateCardGroupData createRateCardGroup(){
        RateCardGroupData rateCardGroupData = new RateCardGroupData();

        rateCardGroupData.setName("RCG");

        return rateCardGroupData;
    }

    @After
    public void after(){
        failReasons.clear();
        partialFailReasons.clear();
    }
}
