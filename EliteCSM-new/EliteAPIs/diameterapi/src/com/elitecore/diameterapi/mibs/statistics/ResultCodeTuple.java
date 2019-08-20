package com.elitecore.diameterapi.mibs.statistics;

import java.util.concurrent.atomic.AtomicLong;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public class ResultCodeTuple {

	private AtomicLong inResultCode;
	private AtomicLong outResultCode;

	public ResultCodeTuple() {
		inResultCode = new AtomicLong(0);
		outResultCode = new AtomicLong(0);
	}

	public long getResultCodeIn(){
		return inResultCode.get();
	}

	public void incrementResultCodeIn(DiameterPacket answer){
		inResultCode.incrementAndGet();
	}

	public long getResultCodeOut(){
		return outResultCode.get();
	}

	public void incrementResultCodeOut(DiameterPacket answer){
		outResultCode.incrementAndGet();
	}
}
