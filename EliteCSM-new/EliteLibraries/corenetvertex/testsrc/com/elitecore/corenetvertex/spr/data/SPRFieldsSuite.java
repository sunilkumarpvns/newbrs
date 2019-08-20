package com.elitecore.corenetvertex.spr.data;

import com.elitecore.corenetvertex.constants.PasswordEncryptionType;
import com.elitecore.corenetvertex.constants.SubscriberStatus;
import com.elitecore.corenetvertex.spr.exceptions.OperationFailedException;
import com.elitecore.corenetvertex.spr.voltdb.VoltSubscriptionOperationGetAddOnSubscriptionTest;
import com.elitecore.corenetvertex.spr.voltdb.VoltSubscriptionOperationSubscribeAddOnByNameTest;
import com.elitecore.corenetvertex.spr.voltdb.VoltSubscriptionOperationSubscribeAddOnbyIdTest;
import com.elitecore.corenetvertex.spr.voltdb.VoltSubscriptionOperationTest;
import com.elitecore.corenetvertex.spr.voltdb.VoltSubscriptionOperationUpdateSubscriptionTest;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        BillingDateTest.class
})

@Ignore
public class SPRFieldsSuite {
}
