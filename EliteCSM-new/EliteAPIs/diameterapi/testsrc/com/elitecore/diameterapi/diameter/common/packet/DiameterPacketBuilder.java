package com.elitecore.diameterapi.diameter.common.packet;

import com.elitecore.diameterapi.diameter.common.data.PeerData;
import com.elitecore.diameterapi.diameter.common.data.impl.PeerDataImpl;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.Parameter;
import com.elitecore.diameterapi.diameter.common.util.constant.ApplicationIdentifier;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.diameterapi.diameter.common.util.identifierpool.EndToEndPool;

public class DiameterPacketBuilder {
	
	public static DiameterRequestBuilder requestBuilder(PeerData peerData) {
		return new DiameterRequestBuilder(peerData);
	}
	
	public static DiameterRequestBuilder localRequestBuilder() {
		PeerDataImpl peerData = new PeerDataImpl();
		peerData.setHostIdentity(Parameter.getInstance().getOwnDiameterIdentity());
		peerData.setRealmName(Parameter.getInstance().getOwnDiameterRealm());
		return new DiameterRequestBuilder(peerData);
	}
	
	public static DiameterAnswerBuilder answerBuilder(DiameterRequest request) {
		return new DiameterAnswerBuilder(request);
	}
	
	public static class DiameterRequestBuilder {
		private DiameterRequest request;
		
		public DiameterRequestBuilder(PeerData peerData) {
			request = new DiameterRequest(false);
			request.setEnd_to_endIdentifier(EndToEndPool.get());
			request.setPeerData(peerData);
			request.setRequestingHost(peerData.getHostIdentity());
			addAVP(DiameterAVPConstants.ORIGIN_HOST, peerData.getHostIdentity());
			addAVP(DiameterAVPConstants.ORIGIN_REALM, peerData.getRealmName());
		}

		public DiameterRequestBuilder addAVP(String id, String value) {
			request.addAvp(id, value);
			return this;
		}

		public DiameterRequestBuilder commandCode(CommandCode commandCode) {
			request.setCommandCode(commandCode.code);
			return this;
		}
		
		public DiameterRequest build() {
			return request;
		}

		public DiameterRequestBuilder application(ApplicationIdentifier appId) {
			request.setApplicationID(appId.applicationId);
			return this;
		}

	}
	
	public static class DiameterAnswerBuilder {
		private DiameterAnswer answer;

		public DiameterAnswerBuilder(DiameterRequest request) {
			answer = new DiameterAnswer(request);
		}
		
		public DiameterAnswerBuilder addAVP(String id, String value) {
			answer.addAvp(id, value);
			return this;
		}
		
		public DiameterAnswer build() {
			return answer;
		}

		public DiameterAnswerBuilder resultCode(ResultCode resultCode) {
			answer.addAvp(DiameterAVPConstants.RESULT_CODE, resultCode.code + "");
			return this;
		}

		public DiameterAnswerBuilder addOrReplaceAVP(String id, String value) {
			DiameterUtility.addOrReplaceAvp(id, answer, value);
			return this;
		}
	}
}
