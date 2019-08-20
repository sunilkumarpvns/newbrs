package com.elitecore.aaa.diameter.translators;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;

public class DiameterSrcDstKeyword extends TranslationKeyword{
	
	private static String MODULE = "DIA-SRCDST-KEYWORD";
	
	public DiameterSrcDstKeyword(String keyword,KeywordContext context) {
		super(keyword,context);
	}

	@Override
	public String getKeywordValue(TranslatorParams params,String strKeyword,boolean isRequest,ValueProvider valueProvider){
	
		Object object = params.getParam(getName());
		if(object!=null){
			final DiameterPacket diameterPacket = (DiameterPacket)object;
						
			String arg1 = getKeywordArgument(strKeyword);
			if(arg1!=null){
				if(context.isKeyword(arg1)){
					arg1 = context.getKeywordValue(arg1, params, isRequest,valueProvider);
					if(arg1!=null)
						return diameterPacket.getAVPValue(arg1);
				}else{
					return diameterPacket.getAVPValue(arg1);
				}
			}
			
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Can't evaluate keyword :"+strKeyword+" Reason Diameter Packet not found");
		}
		return null;
		
	}
	

}
