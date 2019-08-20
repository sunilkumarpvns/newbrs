package com.elitecore.netvertex.core.transaction;

import com.elitecore.netvertex.gateway.diameter.transaction.action.AbortSessionAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.ActionHandler;
import com.elitecore.netvertex.gateway.diameter.transaction.action.AuthorizeAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.GyAuthorizeAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.GySendCCAAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.GySessionStopAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.RemoveCoreSessionAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.RemoveFailedPCCRulesAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.RuleInstallAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.RuleProvisionAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.RuleRemoveAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.RxAuthorizeAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.SendCCAAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.SendFailureAckToAFAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.SendSuccessAckToAFAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.SessionDisconnectAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.SessionReAuthAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.SessionSaveAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.SessionStopAction;
import com.elitecore.netvertex.gateway.diameter.transaction.action.StopServiceAction;

public enum TransactionAction {
	ABORT_SESSION {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new AbortSessionAction(transactionContext);
		}
	},
	AUTHORIZE {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new AuthorizeAction(transactionContext);
		}
	},
	REMOVE_CORE_SESSION {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return  new RemoveCoreSessionAction(transactionContext);
		}
	},
	REMOVE_FAILED_PCC_RULES {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new RemoveFailedPCCRulesAction(transactionContext);
		}
	},
	RULE_INSTALL {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new RuleInstallAction(transactionContext);
		}
	},
	RULE_PROVISION {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new RuleProvisionAction(transactionContext);
		}
	},
	RULE_REMOVE {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new RuleRemoveAction(transactionContext);
		}
	},
	RX_AUTHORIZE {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new RxAuthorizeAction(transactionContext);
		}
	},
	SEND_CCA {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new SendCCAAction(transactionContext);
		}
	},
	SEND_FAILURE_ACK_TO_AF {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new SendFailureAckToAFAction(transactionContext);
		}
	},
	SEND_SUCCESS_ACK_TO_AF {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new SendSuccessAckToAFAction(transactionContext);
		}
	},
	SESSION_DISCONNECT {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new SessionDisconnectAction(transactionContext);
		}
	},
	SESSION_RE_AUTH {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new SessionReAuthAction(transactionContext);
		}
	},
	SESSION_STOP_ACTION {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new SessionStopAction(transactionContext);
		}
	},
	STOP_SERVICE {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new StopServiceAction(transactionContext);
		}
	},
	SESSION_UPDATE_ACTION {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new SessionSaveAction(transactionContext);
		}
	}, 
	GY_AUTHORIZE {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new GyAuthorizeAction(transactionContext);
		}
	},
	GY_SEND_CCA {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new GySendCCAAction(transactionContext);
		}
	},
	GY_SESSION_STOP_ACTION {
		@Override
		public ActionHandler createActionHandler(DiameterTransactionContext transactionContext) {
			return new GySessionStopAction(transactionContext);
		}
	};
	
	public abstract ActionHandler createActionHandler(DiameterTransactionContext transactionContext);	
}
