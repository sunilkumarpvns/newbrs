package com.elitecore.netvertex.service.pcrf.impl;

import com.elitecore.corenetvertex.pm.pkg.ChargingRuleBaseName;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;

import java.util.Arrays;

import static org.junit.Assert.*;
import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class PCRFResponseImplTest {

    private PCRFResponse pcrfResponse = new PCRFResponseImpl();

    public class IsPolicyChanged{

        @Test
        public void true_WhenInstallablePCCRuleFound() {
            pcrfResponse.setInstallablePCCRules(Arrays.asList(mock(PCCRule.class)));
            assertTrue(pcrfResponse.isPolicyChanged());
        }

        @Test
        public void true_WhenRemovablePCCRuleFound() {
            pcrfResponse.setRemovablePCCRules(Arrays.asList("PCC1"));
            assertTrue(pcrfResponse.isPolicyChanged());
        }

        @Test
        public void true_WhenInstallableCRBNFound() {
            pcrfResponse.setRemovableChargingRuleBaseNames(Arrays.asList("CRBN1"));
            assertTrue(pcrfResponse.isPolicyChanged());
        }

        @Test
        public void true_WhenRemovableCRBNFound() {
            pcrfResponse.setInstallableChargingRuleBaseNames(Arrays.asList(mock(ChargingRuleBaseName.class)));
            assertTrue(pcrfResponse.isPolicyChanged());
        }

        @Test
        public void true_WhenPolicyChangeFlagSet() {
            pcrfResponse.setPolicyChanged(true);
            assertTrue(pcrfResponse.isPolicyChanged());
        }

        @Test
        public void false_WhenNotPCCRuleToInstallOrRemoveAndNoCRBMInstallOrRemoveAndNoPolicyChangedFlageSet() {
            assertFalse(pcrfResponse.isPolicyChanged());
        }


    }

}