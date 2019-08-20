package com.elitecore.netvertex.service.pcrf.servicepolicy.handler;

import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
	   BasePackage_is_replacable_by_false_and_exclusive_addOn_applied.class,
	   BasePackage_is_replacable_by_false_and_non_exclusive_addOn_applied.class,
	   BasePackage_is_replacable_by_true_and_exclusive_addOn_applied.class,
	   BasePackage_is_replacable_by_true_and_non_exclusive_addOn_applied.class,
	   BasePackage_is_replacable_by_false_and_no_addOn_applied.class,
	   BasePackage_is_replacable_by_false_and_non_exclusive_and_exclusive_addOn_applied.class,
	   BasePackage_is_replacable_by_true_and_no_addOn_subscribed.class,
	   BasePackage_is_replacable_by_true_and_non_exclusive_and_exclusive_addOn_applied.class
	   
	})
public class SubscriberPolicyHandlerTest {

}
