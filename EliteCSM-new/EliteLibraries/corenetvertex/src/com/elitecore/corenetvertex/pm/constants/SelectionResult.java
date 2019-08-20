package com.elitecore.corenetvertex.pm.constants;

public enum SelectionResult {

	FULLY_APPLIED("FULLY APPLIED") {
		@Override
		public SelectionResult and(SelectionResult selectionResult) {
			return FULLY_APPLIED;
		}
	},
	PARTIALLY_APPLIED("PARTIALLY APPLIED") {
		@Override
		public SelectionResult and(SelectionResult selectionResult) {
				return PARTIALLY_APPLIED;
		}
	},
	NOT_APPLIED("NOT APPLIED") {
		@Override
		public SelectionResult and(SelectionResult selectionResult) {
			return selectionResult;
		}
	};
	
	public final String displayValue;
	
	private SelectionResult(String displayValue) {
		this.displayValue = displayValue;
	}

	public abstract SelectionResult and(SelectionResult selectionResult);
}
