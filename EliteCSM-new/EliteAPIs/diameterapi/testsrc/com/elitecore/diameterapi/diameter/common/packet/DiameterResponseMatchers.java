package com.elitecore.diameterapi.diameter.common.packet;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeDiagnosingMatcher;

import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;

public class DiameterResponseMatchers {

	public static class ContainsAttributeMatcher extends TypeSafeDiagnosingMatcher<DiameterAnswer> {

		private String avp;
		private String value;

		public ContainsAttributeMatcher(String attributeId, String value) {
			this.avp = attributeId;
			this.value = value;
		}

		@Override
		public void describeTo(Description arg0) {
			arg0.appendText("answer to contain avp ").appendText(avp).appendText(" with value ")
			.appendText(value);
		}

		@Override
		protected boolean matchesSafely(DiameterAnswer arg0, Description arg1) {
			IDiameterAVP avp = arg0.getAVP(this.avp, true);
			if (avp == null) {
				arg1.appendText("avp was not found in answer");
				return false;
			}

			if (!avp.getStringValue().equals(value)) {
				arg1.appendText("was found with value ").appendText(avp.getStringValue());
				return false;
			}
			return true;
		}
	}
}
