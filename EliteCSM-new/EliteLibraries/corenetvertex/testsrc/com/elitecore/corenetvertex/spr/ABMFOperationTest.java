package com.elitecore.corenetvertex.spr;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses(
        {ABMFOperationAddBalanceTest.class,
                ABMFOperationDirectDebitTest.class,
                ABMFOperationReportBalanceTest.class,
                ABMFOperationReserveAndReportTest.class,
                ABMFOperationReserveBalanceTest.class,
                ABMFOperationResetTest.class,
                ABMFOperationTestGetBalance.class,
                ABMFOperationResetQuotaTest.class,
				ABMFOperationExpireBalanceTest.class
        }
)

public class ABMFOperationTest {
}