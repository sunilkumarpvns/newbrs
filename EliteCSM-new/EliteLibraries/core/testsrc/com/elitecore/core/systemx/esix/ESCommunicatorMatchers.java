package com.elitecore.core.systemx.esix;

import static org.hamcrest.CoreMatchers.equalTo;

import org.hamcrest.FeatureMatcher;

/**
 * 
 * @author narendra.pathai
 *
 */
public class ESCommunicatorMatchers {

	public static class IsAliveMatcher extends FeatureMatcher<ESCommunicator, Boolean> {

		public IsAliveMatcher() {
			super(equalTo(true), "alive", "alive");
		}

		@Override
		protected Boolean featureValueOf(ESCommunicator communicator) {
			return communicator.isAlive();
		}
		
	}
	
}
