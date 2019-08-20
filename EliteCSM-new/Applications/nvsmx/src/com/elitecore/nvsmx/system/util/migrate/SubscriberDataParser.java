package com.elitecore.nvsmx.system.util.migrate;

import com.elitecore.commons.base.Splitter;
import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.subscriberimport.InputType;

/**
 * @author Chetan.Sankhala
 */
public interface SubscriberDataParser {
	public static Splitter COMMA_SPLITTER = Splitter.on(CommonConstants.COMMA).preserveTokens().trimTokens();
	
	public NV648SubscriberInfo parse(String dataString);
	public InputType getInputType();
}
