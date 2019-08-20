package com.elitecore.diameterapi.mibs.statistics;

import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public class CounterTuple {

	private AtomicLong requestRx;
	private AtomicLong requestTx;
	private AtomicLong answerRx;
	private AtomicLong answerTx;
	private AtomicLong answerDr;
	private AtomicLong requestDr;
	private AtomicLong requestRetransmitted;
	private AtomicLong unknownHbHAnswerDropped;
	private AtomicLong duplicateRequest;
	private AtomicLong duplicateEtEAnswer;
	private AtomicLong malformedPacketRx;
	private AtomicLong requestTimeOut;

	public CounterTuple() {

		requestRx = new AtomicLong(0);
		requestTx = new AtomicLong(0);
		answerRx = new AtomicLong(0);
		answerTx = new AtomicLong(0);
		requestDr = new AtomicLong(0);
		answerDr = new AtomicLong(0);
		requestRetransmitted = new AtomicLong(0);
		unknownHbHAnswerDropped = new AtomicLong(0);
		duplicateRequest = new AtomicLong(0);
		duplicateEtEAnswer = new AtomicLong(0);
		malformedPacketRx = new AtomicLong(0);
		requestTimeOut = new AtomicLong(0);
	}

	public long getAnswerDroppedCount() {
		return answerDr.get();
	}

	public void incrementAnswerDroppedCount() {
		answerDr.incrementAndGet();
	}

	public long getRequestDroppedCount() {
		return requestDr.get();
	}

	public void incrementRequestDroppedCount() {
		requestDr.incrementAndGet();
	}

	public long getAnswerOutCount() {
		return answerTx.get();
	}

	public void incrementAnswerOutCount(DiameterPacket packet) {
		answerTx.incrementAndGet();			
	}

	public long getAnswerInCount() {
		return answerRx.get();
	}

	public void incrementAnswerInCount(DiameterPacket packet) {
		answerRx.incrementAndGet();			
	}

	public long getRequestOutCount() {
		return requestTx.get();
	}

	public void incrementRequestOutCount(DiameterPacket packet) {
		requestTx.incrementAndGet();			
	}

	public long getRequestInCount() {
		return requestRx.get();
	}

	public void incrementRequestInCount(DiameterPacket packet) {
		requestRx.incrementAndGet();			
	}

	public long getRequestsRetransmittedCount() {
		return requestRetransmitted.get();
	}

	public void incrementRequestsRetransmittedCount() {
		requestRetransmitted.incrementAndGet();			
	}
	
	public long getUnknownHbHAnswerDroppedCount() {
		return unknownHbHAnswerDropped.get();
	}

	public void incrementUnknownHbHAnswerDroppedCount() {
		unknownHbHAnswerDropped.incrementAndGet();
	}
	
	public long getDuplicateEtEAnswerCount() {
		return duplicateEtEAnswer.get();
	}

	public void incrementDuplicateEtEAnswerCount() {
		duplicateEtEAnswer.incrementAndGet();
	}
	
	public long getDuplicateRequestCount() {
		return duplicateRequest.get();
	}

	public void incrementDuplicateRequestCount() {
		duplicateRequest.incrementAndGet();
	}
	
	public void incrementMalformedPacketReceivedCount() {
		malformedPacketRx.incrementAndGet();
	}
	
	public long getMalformedPacketReceivedCount() {
		return malformedPacketRx.get();
	}
	
	public void incrementTimeoutRequestStatistics() {
		requestTimeOut.incrementAndGet();
	}
	
	public long getTimeoutRequestStatistics() {
		return requestTimeOut.get();
	}
	
	public long getPendingRequestCount() {
		long pendingCount = (getRequestOutCount() - getAnswerInCount() - getRequestsRetransmittedCount()); 
		if (pendingCount < 0)
			pendingCount = 0;
		return pendingCount;
	}
}
