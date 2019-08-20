package com.elitecore.diameterapi.mibs.statistics;

import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;

public class CCResultCodeTuple extends ResultCodeTuple {
	
	private AtomicLong initialResultCodeIn;
	private AtomicLong updateResultCodeIn;
	private AtomicLong terminateResultCodeIn;
	private AtomicLong otherResultCodeIn;
	
	private AtomicLong initialResultCodeOut;
	private AtomicLong updateResultCodeOut;
	private AtomicLong terminateResultCodeOut;
	private AtomicLong otherResultCodeOut;

	public CCResultCodeTuple() {
		super();
		
		initialResultCodeIn = new AtomicLong(0);
		updateResultCodeIn = new AtomicLong(0);
		terminateResultCodeIn = new AtomicLong(0);
		otherResultCodeIn = new AtomicLong(0);
	
		initialResultCodeOut = new AtomicLong(0);
		updateResultCodeOut = new AtomicLong(0);
		terminateResultCodeOut = new AtomicLong(0);
		otherResultCodeOut = new AtomicLong(0);
	}
	
	@Override
	public void incrementResultCodeIn(DiameterPacket answer) {
		super.incrementResultCodeIn(answer);
		
		IDiameterAVP requestType = answer.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		if (requestType == null) {
			otherResultCodeIn.incrementAndGet();
			return;
		}
		
		int lRequestType = (int) requestType.getInteger();
		switch (lRequestType) {
		
		case DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST:
			updateResultCodeIn.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST:
			initialResultCodeIn.incrementAndGet();
			break;
		
		case DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST:
			terminateResultCodeIn.incrementAndGet();
			break;
		
		default:
			otherResultCodeIn.incrementAndGet();
		}
	}
	
	@Override
	public void incrementResultCodeOut(DiameterPacket answer) {
		super.incrementResultCodeOut(answer);
		
		IDiameterAVP requestType = answer.getAVP(DiameterAVPConstants.CC_REQUEST_TYPE);
		if (requestType == null) {
			otherResultCodeOut.incrementAndGet();
			return;
		}
		
		int lRequestType = (int) requestType.getInteger();
		switch (lRequestType) {
		
		case DiameterAttributeValueConstants.DIAMETER_UPDATE_REQUEST:
			updateResultCodeOut.incrementAndGet();
			break;
			
		case DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST:
			initialResultCodeOut.incrementAndGet();
			break;
			
		case DiameterAttributeValueConstants.DIAMETER_TERMINATION_REQUEST:
			terminateResultCodeOut.incrementAndGet();
			break;
			
		default:
			otherResultCodeOut.incrementAndGet();
		}
	}

	public long getInitialResultCodeIn() {
		return initialResultCodeIn.get();
	}

	public long getUpdateResultCodeIn() {
		return updateResultCodeIn.get();
	}

	public long getTerminateResultCodeIn() {
		return terminateResultCodeIn.get();
	}

	public long getOtherResultCodeIn() {
		return otherResultCodeIn.get();
	}

	public long getInitialResultCodeOut() {
		return initialResultCodeOut.get();
	}

	public long getUpdateResultCodeOut() {
		return updateResultCodeOut.get();
	}

	public long getTerminateResultCodeOut() {
		return terminateResultCodeOut.get();
	}

	public long getOtherResultCodeOut() {
		return otherResultCodeOut.get();
	}

}
