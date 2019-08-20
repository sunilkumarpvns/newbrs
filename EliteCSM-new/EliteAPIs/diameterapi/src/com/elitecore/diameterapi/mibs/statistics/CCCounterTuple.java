package com.elitecore.diameterapi.mibs.statistics;

import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;

public class CCCounterTuple extends CounterTuple{

	private AtomicLong initialRequestRx;
	private AtomicLong initialRequestTx;
	private AtomicLong initialAnswerRx;
	private AtomicLong initialAnswerTx;
	
	private AtomicLong updateRequestRx;
	private AtomicLong updateRequestTx;
	private AtomicLong updateAnswerRx;
	private AtomicLong updateAnswerTx;
	
	private AtomicLong terminateRequestRx;
	private AtomicLong terminateRequestTx;
	private AtomicLong terminateAnswerRx;
	private AtomicLong terminateAnswerTx;
	
	private AtomicLong otherRequestRx;
	private AtomicLong otherRequestTx;
	private AtomicLong otherAnswerRx;
	private AtomicLong otherAnswerTx;
	
	public CCCounterTuple() {
		initialRequestRx = new AtomicLong(0);
		initialRequestTx = new AtomicLong(0);
		initialAnswerRx = new AtomicLong(0);
		initialAnswerTx = new AtomicLong(0);
		
		updateRequestRx = new AtomicLong(0);
		updateRequestTx = new AtomicLong(0);
		updateAnswerRx = new AtomicLong(0);
		updateAnswerTx = new AtomicLong(0);
		
		terminateRequestRx = new AtomicLong(0);
		terminateRequestTx = new AtomicLong(0);
		terminateAnswerRx = new AtomicLong(0);
		terminateAnswerTx = new AtomicLong(0);
		
		otherRequestRx = new AtomicLong(0);
		otherRequestTx = new AtomicLong(0);
		otherAnswerRx = new AtomicLong(0);
		otherAnswerTx = new AtomicLong(0);
	}

	@Override
	public void incrementAnswerInCount(DiameterPacket packet) {
		super.incrementAnswerInCount(packet);
		
		IDiameterAVP requestType = packet.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		if (requestType == null) {
			otherAnswerRx.incrementAndGet();
			return;
		}
		
		int lRequestType = (int) requestType.getInteger();
		switch (lRequestType) {
		
		case DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST:
			updateAnswerRx.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST:
			initialAnswerRx.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST:
			terminateAnswerRx.incrementAndGet();
			break;
		
		default:
			otherAnswerRx.incrementAndGet();
		}
	}
	
	@Override
	public void incrementAnswerOutCount(DiameterPacket packet) {
		super.incrementAnswerOutCount(packet);
		
		IDiameterAVP requestType = packet.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		if (requestType == null) {
			otherAnswerTx.incrementAndGet();
			return;
		}
		
		int lRequestType = (int) requestType.getInteger();
		switch (lRequestType) {
		
		case DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST:
			updateAnswerTx.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST:
			initialAnswerTx.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST:
			terminateAnswerTx.incrementAndGet();
			break;
		
		default:
			otherAnswerTx.incrementAndGet();
		}
	}
	
	@Override
	public void incrementRequestInCount(DiameterPacket packet) {
		super.incrementRequestInCount(packet);
		
		IDiameterAVP requestType = packet.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		if (requestType == null) {
			otherRequestRx.incrementAndGet();
			return;
		}
		
		int lRequestType = (int) requestType.getInteger();
		switch (lRequestType) {
		
		case DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST:
			updateRequestRx.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST:
			initialRequestRx.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST:
			terminateRequestRx.incrementAndGet();
			break;
		
		default:
			otherRequestRx.incrementAndGet();
		}
	}
	
	@Override
	public void incrementRequestOutCount(DiameterPacket packet) {
		super.incrementRequestOutCount(packet);
		
		IDiameterAVP requestType = packet.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		if (requestType == null) {
			otherRequestTx.incrementAndGet();
			return;
		}
		
		int lRequestType = (int) requestType.getInteger();
		switch (lRequestType) {
		
		case DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST:
			updateRequestTx.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST:
			initialRequestTx.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST:
			terminateRequestTx.incrementAndGet();
			break;
		
		default:
			otherRequestTx.incrementAndGet();
		}
	}
	
	public long getInitialRequestRx() {
		return initialRequestRx.get();
	}
	
	public long getInitialRequestTx() {
		return initialRequestTx.get();
	}
	
	public long getInitialAnswerRx() {
		return initialAnswerRx.get();
	}
	
	public long getInitialAnswerTx() {
		return initialAnswerTx.get();
	}
	
	public long getUpdateRequestRx() {
		return updateRequestRx.get();
	}
	
	public long getUpdateRequestTx() {
		return updateRequestTx.get();
	}
	
	public long getUpdateAnswerRx() {
		return updateAnswerRx.get();
	}
	
	public long getUpdateAnswerTx() {
		return updateAnswerTx.get();
	}
	
	public long getTerminateRequestRx() {
		return terminateRequestRx.get();
	}
	
	public long getTerminateRequestTx() {
		return terminateRequestTx.get();
	}
	
	public long getTerminateAnswerRx() {
		return terminateAnswerRx.get();
	}
	
	public long getTerminateAnswerTx() {
		return terminateAnswerTx.get();
	}
	
	public long getOtherRequestRx() {
		return otherRequestRx.get();
	}
	
	public long getOtherRequestTx() {
		return otherRequestTx.get();
	}
	
	public long getOtherAnswerRx() {
		return otherAnswerRx.get();
	}
	
	public long getOtherAnswerTx() {
		return otherAnswerTx.get();
	}
	
}
