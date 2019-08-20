package external


import com.elitecore.aaa.diameter.commons.AAATranslatorConstants
import com.elitecore.aaa.radius.service.RadServiceRequest
import com.elitecore.aaa.script.TranslationMappingScript
import com.elitecore.commons.logging.LogManager
import com.elitecore.core.serverx.manager.scripts.ScriptContext
import com.elitecore.coreradius.commons.packet.RadiusPacket
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams

class SampleTranslationMappingScript extends TranslationMappingScript{
	public SampleTranslationMappingScript(ScriptContext scriptContext){
		super(scriptContext);
	}

	@Override
	protected void requestTranslationExt(TranslatorParams params) {
		// refer AAATranslatorConstants for other constants
		
		//this gives the access to the original request that is received at AAA 
		RadServiceRequest requestPacket = (RadServiceRequest)params.getParam(AAATranslatorConstants.RADIUS_IN_MESSAGE);
		//this gives the access to the new translated request formed using the translation mapping
		RadiusPacket translatedRequestPacket = (RadiusPacket)params.getParam(AAATranslatorConstants.RADIUS_OUT_MESSAGE);
				
		LogManager.getLogger().info(getName(), "I got called: requestTranslationExt");
	}

	@Override
	protected void responseTranslationExt(TranslatorParams params) {
		LogManager.getLogger().info(getName(), "I got called: responseTranslationExt");
	}

	@Override
	protected String getName() {
		return "RAD_TO_RAD";
	}
	
	
}
