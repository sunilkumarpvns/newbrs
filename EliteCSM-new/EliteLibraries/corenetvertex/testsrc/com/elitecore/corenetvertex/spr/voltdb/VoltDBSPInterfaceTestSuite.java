package com.elitecore.corenetvertex.spr.voltdb;


import org.junit.Ignore;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@Ignore
@RunWith(Suite.class)
@Suite.SuiteClasses(
        {
                GetProfileTest.class,
                AddProfileTest.class,
                MarkDeleteSubscriberProfileTest.class,
                RestoreSubscriberProfileTest.class,
                PurgeProfileTest.class,
                UpdateProfileTest.class,
                ChangeIMSPackageTest.class
        })
public class VoltDBSPInterfaceTestSuite {
}
