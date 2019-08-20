package com.elitecore.netvertex.core.transaction;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyValueConstants;
import com.elitecore.corenetvertex.constants.RequestStatus;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.ResultCode;
import com.elitecore.netvertex.gateway.diameter.DiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.scripts.DiameterGroovyScript;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.gateway.diameter.transaction.session.TransactionSession;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicLong;

public abstract class BaseTransaction implements Transaction {

	private static final String MODULE = "BASE-TRANS";
	private static AtomicLong incrementalVal = new AtomicLong(0);
	private static Random randomGenerator = new Random();
	protected TransactionState transactionState;
	private DiameterTransactionContext transactionContext;
	private DiameterGatewayControllerContext gatewayControllerContext;
	private TransactionSession session;
	private long startTimeInMilli;
	private long lastAccessTimeInMilli;
	private String transactionId;
	
	public BaseTransaction(DiameterGatewayControllerContext diameterGatewayControllerContext){		
		transactionId = generateTransactionId(); 
		session = new TransactionSession();
		session.put(SessionKeys.TRANSACTION_ID, transactionId);
		transactionState = TransactionState.IDLE;		
		startTimeInMilli = System.currentTimeMillis();
		lastAccessTimeInMilli = startTimeInMilli;
		this.gatewayControllerContext = diameterGatewayControllerContext;

		transactionContext = new DiameterTransactionContext() {
			@Override
			public DiameterGatewayControllerContext getControllerContext() {
				return gatewayControllerContext;
			}

			@Override
			public TransactionSession getTransactionSession() {
				return session;
			}

			@Override
			public boolean sendRequest(DiameterRequest request, String preferredPeerHostIdOrName , boolean bRegister) {
				if(bRegister) {
					return gatewayControllerContext.sendRequest(request, preferredPeerHostIdOrName, BaseTransaction.this);
				} else {
					return gatewayControllerContext.sendRequest(request, preferredPeerHostIdOrName, null);
				}
			}

			@Override
			public boolean sendAnswer(DiameterAnswer answer, DiameterRequest diameterRequest) {
				return gatewayControllerContext.sendAnswer(answer, diameterRequest);
			}

			@Override
			public TimeSource getTimeSource() {
				return TimeSource.systemTimeSource();
			}

            @Override
            public <T> T get(String key) {
                return BaseTransaction.this.getTransactionSession().get(key);
            }

            @Override
            public void put(String key, Object value) {
                BaseTransaction.this.getTransactionSession().put(key, value);
            }

            @Override
			public TransactionState getState() {
				return transactionState;
			}

			@Override
			public RequestStatus submitPCRFRequest(PCRFRequest pcrfRequest) {
				return submitPCRFRequest(pcrfRequest, true);
			}

			@Override
			public RequestStatus submitPCRFRequest(PCRFRequest pcrfRequest, boolean bRegister) {

				DiameterPacket diameterPacket = session.get(SessionKeys.DIAMETER_REQUEST);
				if(diameterPacket != null){
					String sessionID = pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val);
					String packetSessID = diameterPacket.getAVPValue(DiameterAVPConstants.SESSION_ID);
					if(sessionID != null && packetSessID != null && sessionID.equalsIgnoreCase(packetSessID)){
						applyScriptsForReceivedPacket(diameterPacket, pcrfRequest);
					}
				}

				session.put(SessionKeys.PCRF_REQUEST,pcrfRequest);
				if(bRegister) {
					gatewayControllerContext.registerTransaction(transactionId,BaseTransaction.this);
				}

				return gatewayControllerContext.submitPCRFRequest(pcrfRequest);
			}
		};
	}

	@Override
	public synchronized void process(String event, PCRFRequest request){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Base Transaction process is called");
	}
	
	@Override
	public synchronized void process(String event, DiameterRequest request){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Base Transaction process is called");
	}
	
	@Override
	public synchronized void resume(DiameterAnswer answer){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Response Received (Transaction-ID: "+ getTransactionId()  +" ) : " + answer);
		
	}
	
	@Override
	public synchronized void resume(DiameterRequest request){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Request Received (Transaction-ID: "+ getTransactionId()  +" ) : " + request);
	}
	
	@Override
	public synchronized void resume(PCRFResponse pcrfResponse){
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(MODULE, "Response Received (Transaction-ID: "+ getTransactionId()  +" ) from PCRF Service: " + pcrfResponse);
	}
	
	protected DiameterGatewayControllerContext getControllerContext(){
		return gatewayControllerContext;
	}
	
	protected DiameterTransactionContext getTransactionContext(){
		return transactionContext;
	}
	public abstract Object clone() throws CloneNotSupportedException;
	
	@Override
	public void updateLastAccessTime(){
		lastAccessTimeInMilli = System.currentTimeMillis();
	}
	
	protected final TransactionSession getTransactionSession() {
		return session;
	}

	@Override
	public long getStartTime(){
		return startTimeInMilli;
	}

	@Override
	public long getLastAccessTime(){
		return lastAccessTimeInMilli;
	}

	@Override
	public final String getTransactionId() {
		return transactionId;
	}

	private String generateTransactionId(){
		return randomGenerator.nextLong() + ";" + startTimeInMilli + ";" +incrementalVal.incrementAndGet();
	}
	
	
	public void applyScriptsForReceivedPacket(DiameterPacket diameterPacket, PCRFRequest pcrfRequest){
		
		DiameterGatewayConfiguration configuration = gatewayControllerContext.getGatewayConfigurationByName(pcrfRequest.getAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val));
		if(configuration == null){
			LogManager.getLogger().error(MODULE, "Unable to execute scripts.Reason: Configuration not found");
			return;
		}
			
		List<DiameterGroovyScript> scripts = gatewayControllerContext.getDiameterGroovyScripts(configuration.getName());
		
		if(scripts == null || scripts.isEmpty()){
			return;
		}
		
		for(DiameterGroovyScript script : scripts){
			try{
				script.postReceived(diameterPacket, pcrfRequest);
			}catch(Exception ex){
				LogManager.getLogger().error(MODULE, "Error in executing script \""+script.getName()+"\" for Diameter-Packet with Session-ID= " 
							+ pcrfRequest.getAttribute(PCRFKeyConstants.CS_SESSION_ID.val) +" for gateway = "+configuration.getName() +" . Reason: "+ ex.getMessage());
				LogManager.getLogger().trace(MODULE, ex);
			}
			
		}
	}

	protected boolean isSuccess(DiameterAnswer answer){
		IDiameterAVP resultCodeAvp = answer.getAVP(DiameterAVPConstants.RESULT_CODE);
		if(resultCodeAvp != null)
			return ResultCode.DIAMETER_SUCCESS.getCode() == resultCodeAvp.getInteger();		
		return false;
	}
	
	protected boolean isSuccess(PCRFResponse pcrfResponse){
		return PCRFKeyValueConstants.RESULT_CODE_SUCCESS.val.equals(pcrfResponse.getAttribute(PCRFKeyConstants.RESULT_CODE.getVal()));
	}
	
	protected PCRFRequest getPCRFRequest() {
		return transactionContext.getTransactionSession().get(SessionKeys.PCRF_REQUEST);
}
	
	protected PCRFResponse getPCRFResponse() {
		return transactionContext.getTransactionSession().get(SessionKeys.PCRF_RESPONSE);
	}
}
