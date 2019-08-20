package com.elitecore.diameterapi.mibs.statistics;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;

public class CounterTupleFactory {

	CounterTuple getCounterTuple(DiameterPacket packet) {
		int commandCode = packet.getCommandCode();
		if (commandCode == CommandCode.CREDIT_CONTROL.code) {
				return new CCCounterTuple();
		} else {
			return new CounterTuple();
		}
	}
	
}
