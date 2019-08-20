package com.elitecore.corenetvertex.pm.store;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                ParentPolicyStoreFirstTimeCreateTest.class,
                ParentPolicyStoreReloadDataPolicyTest.class,
                ParentPolicyStoreBackUpScenario.class,
        })
public class ParentPolicyStoreTestSuite {

}
