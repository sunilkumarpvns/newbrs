package com.elitecore.netvertex.gateway.diameter.gy;

public enum RequestedAction {
	DIRECT_DEBITING(0),
	REFUND_ACCOUNT(1),
	CHECK_BALANCE(2),
	PRICE_ENQUIRY(3);

	private int val;

	RequestedAction(int val) {
		this.val =val;
	}

	public int getVal() {
		return val;
	}

	public static RequestedAction fromVal(int val) {
		if(DIRECT_DEBITING.getVal() == val) {
			return DIRECT_DEBITING;
		} else if(REFUND_ACCOUNT.getVal() == val) {
			return REFUND_ACCOUNT;
		}else if(CHECK_BALANCE.getVal() == val) {
			return CHECK_BALANCE;
		}else if(PRICE_ENQUIRY.getVal() == val) {
			return PRICE_ENQUIRY;
		}
		return null;
	}
}
