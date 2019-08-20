package com.elitecore.aaa.diameter.translators;

import com.elitecore.aaa.diameter.translators.BaseTranslator.KeywordContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.crestelocs.fw.attribute.IChargingAttribute;
import com.elitecore.crestelocs.fw.packet.IChargingPacket;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;

public class CrestelOCSv2SrcDstKeyword extends TranslationKeyword{

	private static String MODULE = "OCSV2-SRCDST-KEYWORD";
	public CrestelOCSv2SrcDstKeyword(String keyword,KeywordContext context) {
		super(keyword,context);
	}

	@Override
	public String getKeywordValue(TranslatorParams params,String strKeyword,boolean isRequest,ValueProvider valueProvider){
		
		Object object = params.getParam(getName());
		if(object!=null){
			final IChargingPacket chargingPacket = (IChargingPacket)object;
			String arg1 = getKeywordArgument(strKeyword);
			if(arg1!=null){
				if(context.isKeyword(arg1)){
					arg1 = context.getKeywordValue(arg1, params, isRequest,valueProvider);
					if(arg1!=null){
						IChargingAttribute chargingAttribute = chargingPacket.getAttribute(Integer.parseInt(arg1), 0);
						if(chargingAttribute!=null){
							return chargingAttribute.getStringValue();
						}
					}
				}else{
					IChargingAttribute chargingAttribute = chargingPacket.getAttribute(Integer.parseInt(arg1), 0);
					if(chargingAttribute!=null){
						return chargingAttribute.getStringValue();
					}
				}
			}
		}else {
			if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
				LogManager.getLogger().warn(MODULE, "Can't evaluate keyword :"+strKeyword+" Reason Charging Packet not found");
		}
		return null;
		
	}

}
