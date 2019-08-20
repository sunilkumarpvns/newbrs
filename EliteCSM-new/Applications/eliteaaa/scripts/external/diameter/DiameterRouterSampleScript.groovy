package external.diameter;

import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.script.DiameterRouterScript;
import com.elitecore.diameterapi.script.manager.DiameterScriptContext;

public class DiameterRouterSampleScript extends DiameterRouterScript {

	public DiameterRouterSampleScript(DiameterScriptContext scriptContext) {
		super(scriptContext);
	}

	@Override
	protected String getName() {
		return "DIA-SAMPLE-SCRIPT";
	}

	@Override
	public void initGroovy() throws InitializationFailedException {
		// TODO 
		
	}

	@Override
	protected void preRequestRouting(String routingTableName,
			DiameterRequest originRequest, String requestOriginatorPeerId) {
		// TODO
		
	}

	@Override
	protected void postRequestRouting(String routingTableName,
			String routingEntryName, DiameterRequest originRequest,
			String reqOriginatorPeerId, DiameterRequest destinationRequest,
			String destPeerId) {
		// TODO
		
	}

	@Override
	protected void preAnswerRouting(String routingTableName,
			String routingEntryName, DiameterRequest originRequest,
			String ansOriginatorPeerId, DiameterRequest destinationRequest,
			DiameterAnswer originAnswer) {
		// TODO
		
	}

	@Override
	protected void postAnswerRouting(String routingTableName,
			String routingEntryName, DiameterRequest originRequest,
			String ansOriginitorPeerId, DiameterRequest destinationRequest,
			DiameterAnswer originAnswer, DiameterAnswer destinationAnswer,
			String destAnsPeerId) {
		// TODO
		
	}
}