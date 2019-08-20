package com.elitecore.netvertex.gateway.diameter.application;

import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;

public interface ValidationResult {

	Result getResult();

	DiameterAnswer getFailedAnswer();

	static final ValidationResult SUCCESS_RESULT = new ValidationResult() {

		@Override
		public Result getResult() {
			return Result.SUCCESS;
		}

		@Override
		public DiameterAnswer getFailedAnswer() {
			return null;
		}
	};

	static final ValidationResult DROP_RESULT = new ValidationResult() {

		@Override
		public Result getResult() {
			return Result.DROP;
		}

		@Override
		public DiameterAnswer getFailedAnswer() {
			return null;
		}
	};

	public static ValidationResult fail(DiameterAnswer diameterAnswer) {
		return new ValidationResult() {

			@Override
			public Result getResult() {
				return Result.FAIL;
			}

			@Override
			public DiameterAnswer getFailedAnswer() {
				return diameterAnswer;
			}
		};

	}

	public static ValidationResult success() {
		return SUCCESS_RESULT;

	}

	public static ValidationResult drop() {
		return DROP_RESULT;
	}

	public static enum Result {
		SUCCESS, FAIL, DROP;
	}

}
