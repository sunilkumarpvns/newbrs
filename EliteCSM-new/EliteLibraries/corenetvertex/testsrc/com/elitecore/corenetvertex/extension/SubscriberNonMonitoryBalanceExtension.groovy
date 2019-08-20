package com.elitecore.corenetvertex.extension

import com.elitecore.corenetvertex.spr.NonMonetoryBalance
import com.elitecore.corenetvertex.spr.SubscriberNonMonitoryBalance

public class SubscriberNonMonitoryBalanceExtension {
    public static SubscriberNonMonitoryBalance leftShift(final SubscriberNonMonitoryBalance self, NonMonetoryBalance nonMonetoryBalance) {
        self.addBalance(nonMonetoryBalance);
        return self;
    }
}
