package com.elitecore.corenetvertex.pm.pkg.datapackage;

import com.elitecore.corenetvertex.pm.util.MockQuotaTopUp;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Arrays;
import java.util.UUID;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.anyString;

@RunWith(HierarchicalContextRunner.class)
public class QuotaTopUpTest {

    private static final  String quotaTopUpId = UUID.randomUUID().toString();
    private static final String quotaTopUpName = quotaTopUpId + "_name";
    private MockQuotaTopUp quotaTopUp;


     public class TopUpWillbeApplicableWhen {

         @Test
         public void noApplicablePCCProfileConfigured() {
             quotaTopUp = MockQuotaTopUp.create(quotaTopUpId,quotaTopUpName);
             assertTrue(quotaTopUp.isTopUpIsEligibleToApply(anyString()));
         }

         @Test
         public void applicablePccProfilesContainsNameOfPccProfile(){
             String pccProfileName1 = UUID.randomUUID().toString();
             String pccProfileName2 = UUID.randomUUID().toString();

             quotaTopUp = MockQuotaTopUp.create(quotaTopUpId,quotaTopUpName, Arrays.asList(pccProfileName1,pccProfileName2));
             assertTrue(quotaTopUp.isTopUpIsEligibleToApply(pccProfileName1));
             assertTrue(quotaTopUp.isTopUpIsEligibleToApply(pccProfileName2));
         }

     }


     @Test
     public void notApplicableWhenApplicablePCCProfileDoesNotContainsPccProfileName(){
         String pccProfileName1 = UUID.randomUUID().toString();
         String pccProfileName2 = UUID.randomUUID().toString();
         String invalidPccProfileName = UUID.randomUUID().toString();

         quotaTopUp = MockQuotaTopUp.create(quotaTopUpId,quotaTopUpName, Arrays.asList(pccProfileName1,pccProfileName2));
         assertFalse(quotaTopUp.isTopUpIsEligibleToApply(invalidPccProfileName));

     }





}