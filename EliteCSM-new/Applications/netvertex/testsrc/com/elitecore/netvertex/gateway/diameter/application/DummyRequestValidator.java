package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;

public class DummyRequestValidator implements RequestValidator {
	
	private ValidationResult validationResult;
	
	@Override
	public ValidationResult validate(DiameterRequest request) {
		return validationResult;
	}
	
	public void setDropValidationResult() {
		validationResult = ValidationResult.drop();
	}

	public void setSuccessValidationResult() {
		validationResult = ValidationResult.success();
	}

	public void setFailValidationResult(DiameterAnswer answer) {
		validationResult = ValidationResult.fail(answer);
	}
}
