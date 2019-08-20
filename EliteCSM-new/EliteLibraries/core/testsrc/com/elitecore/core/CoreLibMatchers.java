package com.elitecore.core;

import static org.hamcrest.CoreMatchers.not;

import org.hamcrest.Matcher;

import com.elitecore.core.servicex.ServiceResponse;
import com.elitecore.core.systemx.esix.ESCommunicator;
import com.elitecore.core.systemx.esix.ESCommunicatorMatchers.IsAliveMatcher;

/**
 * 
 * A common entry class for all the hamcrest matchers created in core library. This class provides
 * direct static factory methods to create matcher instances.
 * 
 * @author narendra.pathai
 *
 */
public class CoreLibMatchers {

	/**
	 *	Matchers related to external system communicators 
	 */
	public static class ESCommunicatorMatchers {
		
		/**
		 * Matcher that matches aliveness of external system to be true. 
		 */
		public static Matcher<? super ESCommunicator> isAlive() {
			return new IsAliveMatcher();
		}
		
		/**
		 * Matcher that matches aliveness of external system to be false. 
		 */
		public static Matcher<? super ESCommunicator> isDead() {
			return not(isAlive());
		}
	}
	
	public static class ServiceResponseMatchers {
		
		/**
		 * @return a matcher that checks whether drop flag is set.
		 */
		public static Matcher<? super ServiceResponse> isDropped() {
			return com.elitecore.core.servicex.ServiceResponseMatchers.isDropped();
		}
	}
}
