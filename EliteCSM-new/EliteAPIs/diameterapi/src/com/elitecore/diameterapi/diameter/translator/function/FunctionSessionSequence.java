package com.elitecore.diameterapi.diameter.translator.function;

import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.session.Session;
import com.elitecore.diameterapi.core.stack.IStackContext;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.exprlib.parser.expression.ValueProvider;
import com.elitecore.exprlib.parser.expression.impl.AbstractFunctionExpression;

public class FunctionSessionSequence extends AbstractFunctionExpression {
	
	private static final long serialVersionUID = 1L;
	private static final String SESSION_SEQUENCE = "seqsess";
	private static final String MODULE = "SESSION-SEQ-FUNC";
	transient private IStackContext stackContext;
	
	
	public FunctionSessionSequence(IStackContext stackContext) {
		this.stackContext = stackContext;
	}

	@Override
	public String getName() {
		return SESSION_SEQUENCE;
	}

	@Override
	public String getStringValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, 
			MissingIdentifierException {
		return String.valueOf(getLongValue(valueProvider));
	}

	@Override
	public long getLongValue(ValueProvider valueProvider)
			throws InvalidTypeCastException, 
			MissingIdentifierException {
		String copyPacketMapping = (String) valueProvider.getValue(TranslatorConstants.COPY_PACKET_MAPPING_NAME);
		if (copyPacketMapping == null) {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Mandatory parameter: Copy packet mapping not found. Sequence"
						+ " will not work, using default value 0");
			}
			return 0L;
		}
		Session session;
		if (stackContext.hasSession(valueProvider.getStringValue(DiameterAVPConstants.SESSION_ID),valueProvider.getLongValue(TranslatorConstants.APPLICATION_ID))) {
			session = stackContext.getOrCreateSession(valueProvider.getStringValue(DiameterAVPConstants.SESSION_ID),valueProvider.getLongValue(TranslatorConstants.APPLICATION_ID));
		} else {
			if (LogManager.getLogger().isDebugLogLevel()) {
				LogManager.getLogger().debug(MODULE, "Diameter Session, mandatory to maintain sequence not found, sequence"
						+ " will not work, using default value 0");
			}
			return 0L;
		}
		Long presentSequence = (Long) session.getParameter(copyPacketMapping);
		if (presentSequence == null) {
			presentSequence = 0L;
		}
		session.setParameter(copyPacketMapping, presentSequence + 1);
		session.update(null);
		return presentSequence;
	}

}
