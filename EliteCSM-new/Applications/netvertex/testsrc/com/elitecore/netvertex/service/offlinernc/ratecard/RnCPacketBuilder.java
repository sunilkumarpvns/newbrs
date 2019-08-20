package com.elitecore.netvertex.service.offlinernc.ratecard;

import com.elitecore.corenetvertex.constants.OfflineRnCKeyConstants;
import com.elitecore.netvertex.service.offlinernc.core.RnCRequest;
import com.elitecore.netvertex.service.offlinernc.core.RnCResponse;

public class RnCPacketBuilder {

	public static RnCRequestBuilder requestBuilder() {
		return new RnCRequestBuilder();
	}
	
	public static RnCResponseBuilder responseBuilder() {
		return new RnCResponseBuilder();
	}

	public static class RnCRequestBuilder {
		private RnCRequest request;

		private RnCRequestBuilder() {
			this.request = new RnCRequest();
		}

		public RnCRequestBuilder with(OfflineRnCKeyConstants key, String value) {
			this.request.setAttribute(key.getName(), value);
			return this;
		}

		public RnCRequest build() {
			return this.request;
		}
	}
	
	public static class RnCResponseBuilder {
		private RnCResponse response;

		private RnCResponseBuilder() {
			this.response = new RnCResponse();
		}

		public RnCResponseBuilder with(OfflineRnCKeyConstants key, String value) {
			this.response.setAttribute(key.getName(), value);
			return this;
		}

		public RnCResponse build() {
			return this.response;
		}
	}
}