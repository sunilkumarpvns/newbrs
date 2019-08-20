package com.elitecore.diameterapi.mibs.statistics;

import com.elitecore.diameterapi.diameter.common.util.constant.CommandCode;

public class ResultCodeTupleFactory {

	ResultCodeTuple getResultCodeTuple(int commandCode) {
		if (commandCode == CommandCode.CREDIT_CONTROL.code) {
			return new CCResultCodeTuple();
		} else {
			return new ResultCodeTuple();
		}
	}
	
}
