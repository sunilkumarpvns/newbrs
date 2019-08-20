package com.elitecore.netvertex.service.offlinernc.ratecard;

import org.junit.Assert;

import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public class RnCResponseAssertion {
	public static ResponseAssertion assertThat(RnCResponse response) {
		return new ResponseAssertion(response);
	}

	public static class ResponseAssertion {

		private final RnCResponse response;

		public ResponseAssertion(RnCResponse response) {
			this.response = response;
		}

		public ResponseAssertion containsAttributeWithValue(OfflineRnCKeyConstants key, String value) {
			Assert.assertEquals(value, response.getAttribute(key));
			return this;
		}
	}
}