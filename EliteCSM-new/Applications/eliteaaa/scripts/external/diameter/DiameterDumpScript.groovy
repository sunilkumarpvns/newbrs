package external.diameter;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.TreeMap.PrivateEntryIterator;

import com.elitecore.aaa.core.manager.EliteAAAServiceExposerManager;
import com.elitecore.commons.logging.LogManager
import com.elitecore.core.commons.drivers.DriverInitializationFailedException
import com.elitecore.core.commons.drivers.DriverNotFoundException
import com.elitecore.core.commons.drivers.DriverProcessFailedException
import com.elitecore.core.commons.drivers.TypeNotSupportedException
import com.elitecore.core.driverx.cdr.CDRDriver
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.coreradius.commons.util.Dictionary;
import com.elitecore.coreradius.commons.util.RadiusUtility;
import com.elitecore.coreradius.commons.util.constants.RadiusAttributeConstants;
import com.elitecore.coreradius.commons.util.constants.RadiusConstants;
import com.elitecore.diameterapi.core.common.InitializationFailedException
import com.elitecore.diameterapi.core.common.packet.Packet;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode
import com.elitecore.diameterapi.script.DiameterRouterScript
import com.elitecore.diameterapi.script.manager.DiameterScriptContext

public class DiameterDumpScript extends DiameterRouterScript {

	private static final String MODULE = "DIA-DUMP-SCRIPT";
	
	private CDRDriver<DiameterPacket> requestCsvDumpDriver;

	public DiameterDumpScript(DiameterScriptContext scriptContext) {
		super(scriptContext);
	}

	@Override
	protected String getName() {
		return MODULE;
	}

	@Override
	public void initGroovy() throws InitializationFailedException {

		String driverName = "Dia_Request_Dump_Driver";
		
		try{
			requestCsvDumpDriver = scriptContext.getStackContext().getDiameterCDRDriver(driverName);
			LogManager.getLogger().info(MODULE, "Groovy: " + getName() + " Initialized Successfully");
		}catch(DriverInitializationFailedException exception){
			LogManager.getLogger().warn(MODULE, "Error while initializing Driver : " + driverName +
					". Reason : "+exception.getMessage());
			LogManager.getLogger().trace(MODULE, exception);
		}catch(DriverNotFoundException e){
			LogManager.getLogger().warn(MODULE, "Error while getting Driver : " + driverName +
					". Reason : "+e.getMessage());
		}catch(TypeNotSupportedException ex){
			LogManager.getLogger().warn(MODULE, "Driver type is not supported for  : " + driverName +
					". Driver Type Should be Classic CSV Driver. Reason : "+ex.getMessage());
		}
	}

	@Override
	protected void preRequestRouting(String routingTableName,
			DiameterRequest originRequest, String requestOriginatorPeerId) {

	}

	@Override
	protected void postRequestRouting(String routingTableName,
			String routingEntryName, DiameterRequest originRequest,
			String reqOriginatorPeerId, DiameterRequest destinationRequest, String destPeerId) {
		try {

			requestCsvDumpDriver.handleRequest(originRequest);
		} catch (DriverProcessFailedException e) {
			LogManager.getLogger().error(MODULE, e.getMessage());
		}

	}

	@Override
	protected void preAnswerRouting(String routingTableName,
			String routingEntryName, DiameterRequest originRequest,
			String ansOriginatorPeerId, DiameterRequest destinationRequest,
			DiameterAnswer originAnswer) {
			
			try {
				
							requestCsvDumpDriver.handleRequest(originRequest);
						} catch (DriverProcessFailedException e) {
							LogManager.getLogger().error(MODULE, e.getMessage());
						}

	}
			
	@Override
	protected void postAnswerRouting(String routingTableName,
			String routingEntryName, DiameterRequest originRequest,
			String ansOriginitorPeerId, DiameterRequest destinationRequest,
			DiameterAnswer originAnswer, DiameterAnswer destinationAnswer, String destAnsPeerId) {

	}

}