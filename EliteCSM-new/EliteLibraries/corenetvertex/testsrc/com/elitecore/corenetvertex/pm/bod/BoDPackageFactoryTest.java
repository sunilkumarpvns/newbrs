package com.elitecore.corenetvertex.pm.bod;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.DeploymentMode;
import com.elitecore.corenetvertex.constants.PolicyStatus;
import com.elitecore.corenetvertex.pd.bod.BoDData;
import com.elitecore.corenetvertex.pd.bod.BoDQosMultiplierData;
import com.elitecore.corenetvertex.pd.bod.BoDServiceMultiplierData;
import com.elitecore.corenetvertex.pkg.dataservicetype.DataServiceTypeData;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

@RunWith(HierarchicalContextRunner.class)
public class BoDPackageFactoryTest {
    private static final String MODULE = "BOD-FACTORY-TEST";
    private BoDFactory boDFactory;

    @Before
    public void setup(){
        this.boDFactory = spy(new BoDFactory());
    }

    public class FailCases {
        @Test
        public void deploymentModeIsOCS(){
            BoDPackageFactory boDPackageFactory = new BoDPackageFactory(boDFactory, DeploymentMode.OCS);
            BoDData boDData = BodDataDummyBuilder.createBoDDataWithAllValues(MODULE);
            BoDPackage boDPackage = boDPackageFactory.create(boDData);

            assertEquals(PolicyStatus.FAILURE.status, boDPackage.getPolicyStatus().status);
            assertEquals("BoD package parsing failed. Reason: [BoD package is not supported with deployment mode: OCS]"
                    , boDPackage.getFailReason());
        }

        @Test
        public void noQoSProfilesAndSessionMultiplierBound(){
            BoDPackageFactory boDPackageFactory = new BoDPackageFactory(boDFactory, DeploymentMode.PCC);
            BoDData boDData = BodDataDummyBuilder.createEmptyBoDData(MODULE);
            BoDPackage boDPackage = boDPackageFactory.create(boDData);

            assertEquals(PolicyStatus.FAILURE.status, boDPackage.getPolicyStatus().status);
            assertEquals("BoD package parsing failed. Reason: [No BoD QoS Profile Multiplier Found]"
                    , boDPackage.getFailReason());
        }

        @Test
        public void noProfileSessionLevelMultipliers(){
            BoDPackageFactory boDPackageFactory = new BoDPackageFactory(boDFactory, DeploymentMode.PCC);
            BoDData boDData = BodDataDummyBuilder.createBoDDataWithQoSProfilesOnly(MODULE);
            BoDPackage boDPackage = boDPackageFactory.create(boDData);

            assertEquals(PolicyStatus.FAILURE.status, boDPackage.getPolicyStatus().status);
            assertEquals("BoD package parsing failed. Reason: [No BoD QoS Profile Multiplier Found]"
                    , boDPackage.getFailReason());
        }
    }


    public class SuccessCases{
        @Test
        public void isCreateBodPkgCalledByBoDFactory(){
            BoDPackage boDPackage = createBodPackage(DeploymentMode.PCC);
            verify(boDFactory).createBodPkg(boDPackage.getId(), boDPackage.getName(), boDPackage.getDescription()
                    , boDPackage.getPackageMode(), boDPackage.getPkgStatus(), boDPackage.getValidityPeriod(), boDPackage.getValidityPeriodUnit()
                    , boDPackage.getApplicableQosProfiles(), boDPackage.getFupLevelToBoDQosMultipliers(), boDPackage.getGroupIds()
                    , boDPackage.getFailReason(), boDPackage.getPolicyStatus(), boDPackage.getPrice(), boDPackage.getAvailabilityStartDate()
                    , boDPackage.getAvailabilityEndDate(), boDPackage.getParam1(), boDPackage.getParam2());
        }

        @Test
        public void deploymentModeIsPCC(){
            BoDPackage boDPackage = createBodPackage(DeploymentMode.PCC);
            assertEquals(PolicyStatus.SUCCESS.status, boDPackage.getPolicyStatus().status);
        }

        @Test
        public void deploymentModeIsPCRF(){
            BoDPackage boDPackage = createBodPackage(DeploymentMode.PCRF);
            assertEquals(PolicyStatus.SUCCESS.status, boDPackage.getPolicyStatus().status);
        }

        @Test
        public void noQoSProfilesPresentButSessionMultiplierBound(){
            BoDPackageFactory boDPackageFactory = new BoDPackageFactory(boDFactory, DeploymentMode.PCC);
            BoDData boDData = BodDataDummyBuilder.addBoDQosMultiplierDataToBoDData(
                    BodDataDummyBuilder.createEmptyBoDData(MODULE), MODULE);
            BoDPackage boDPackage = boDPackageFactory.create(boDData);

            assertEquals(PolicyStatus.SUCCESS.status, boDPackage.getPolicyStatus().status);
        }

        @Test
        public void addMultipliersInPackageWhenConfigured() {
            BoDPackage boDPackage = createBodPackage(DeploymentMode.PCC);

            Map<Integer, BoDQosMultiplier> fupLevelToBoDQosMultipliers
                    = createMultiplierData(BodDataDummyBuilder.createBoDDataWithAllValues(MODULE));

            ReflectionAssert.assertLenientEquals(boDPackage.getFupLevelToBoDQosMultipliers(), fupLevelToBoDQosMultipliers);
        }

        @Test
        public void addGroupsInPackageWhenConfigured() {
            BoDPackage boDPackage = createBodPackage(DeploymentMode.PCC);
            ReflectionAssert.assertLenientEquals(boDPackage.getGroupIds(), Arrays.asList("GROUP_1"));
        }
    }

    private BoDPackage createBodPackage(DeploymentMode mode) {
        BoDPackageFactory boDPackageFactory = new BoDPackageFactory(boDFactory, mode);
        BoDData boDData = BodDataDummyBuilder.createBoDDataWithAllValues(MODULE);
        return boDPackageFactory.create(boDData);
    }


    private Map<Integer, BoDQosMultiplier> createMultiplierData(BoDData boDData){
        Map<Integer, BoDQosMultiplier> fupLevelToBoDQosMultipliers = new HashMap<Integer, BoDQosMultiplier>();
        for(BoDQosMultiplierData boDQosMultiplierData : boDData.getBodQosMultiplierDatas()){
            fupLevelToBoDQosMultipliers.put(boDQosMultiplierData.getFupLevel(), createBoDQosMultiplierDummy(boDQosMultiplierData));
        }
        return fupLevelToBoDQosMultipliers;
    }


    private BoDQosMultiplier createBoDQosMultiplierDummy(BoDQosMultiplierData boDQosMultiplierData){
        BoDQosMultiplier boDQosMultiplier = new BoDQosMultiplier(boDQosMultiplierData.getMultiplier());
        if(Collectionz.isNullOrEmpty(boDQosMultiplierData.getBodServiceMultiplierDatas()) == false){
            for(BoDServiceMultiplierData boDServiceMultiplierData : boDQosMultiplierData.getBodServiceMultiplierDatas()){
                DataServiceTypeData serviceTypeData = boDServiceMultiplierData.getServiceTypeData();
                boDQosMultiplier.addDataServiceIdToServiceMultiplier(
                        serviceTypeData.getServiceIdentifier()
                        , new BoDServiceMultiplier(serviceTypeData.getId(),serviceTypeData.getName(),
                                boDServiceMultiplierData.getMultiplier(),serviceTypeData.getServiceIdentifier())
                );
            }
        }
        return boDQosMultiplier;
    }

}