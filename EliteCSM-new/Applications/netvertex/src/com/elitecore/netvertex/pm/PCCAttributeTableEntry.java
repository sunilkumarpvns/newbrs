package com.elitecore.netvertex.pm;

import com.elitecore.corenetvertex.pkg.constants.PCCAttribute;
import com.elitecore.corenetvertex.pkg.ims.PCCRuleAttributeAction;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.exprlib.parser.expression.LogicalExpression;
import com.elitecore.exprlib.parser.expression.ValueProvider;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class PCCAttributeTableEntry extends com.elitecore.corenetvertex.pm.pkg.imspackage.PCCAttributeTableEntry {
	
	private static final long serialVersionUID = 1L;
	private static final String MODULO = "PCC-ATTR-TBL-ENTRY";
	
	public PCCAttributeTableEntry(PCCAttribute attribute, PCCRuleAttributeAction action, String value, String expressionStr,
			LogicalExpression expression) {
		super(attribute, action, value, expressionStr, expression);
	}

	private boolean evaluate(ValueProvider valueProvider) {
		return getExpression() == null || getExpression().evaluate(valueProvider) != false;
	}
	
	public boolean apply(PCCRule pccRule, ValueProvider valueProvider) {
		
		
		if(evaluate(valueProvider)) {
			
			 PCCRuleAttributeAction action = getAction();
			 PCCAttribute attribute = getAttribute();
			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULO, "Applying action:" + action .val 
						+ " to attribute:" + attribute.displayValue 
						+ (getValue() == null ? "" : ", value:" + getValue()));
			
			switch (action) {
				case MAX :
					long val =  Long.parseLong(getValue());
					if( val > attribute.getLongVal(pccRule)){
						attribute.setLongVal(pccRule, val);
					}
					break;
					
				case MIN :
					val =  Long.parseLong(getValue());
					if( val < attribute.getLongVal(pccRule)){
						attribute.setLongVal(pccRule, val);
					}
					break;
					
				case OVERRIDE :
					attribute.setStringVal(pccRule, getValue());
					break;
					
				case SKIP:
					attribute.removeVal(pccRule);
					break;
					
				case ADD:
					if(attribute.isValuePresent(pccRule) == false) {
						attribute.setStringVal(pccRule, getValue());
					}
					break;
					
				case STANDARD :
					//NO ACTION REQUIRED AS REQUESTED ALREADY SET
			}
			
			return true;
		}
		
		return false;
	}

}
