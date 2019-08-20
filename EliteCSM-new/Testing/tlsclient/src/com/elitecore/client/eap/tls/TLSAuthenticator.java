package com.elitecore.client.eap.tls;

import java.io.ByteArrayOutputStream;
import java.nio.ByteBuffer;
import java.util.Collection;
import java.util.List;

import javax.naming.AuthenticationException;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLEngine;
import javax.net.ssl.SSLEngineResult;
import javax.net.ssl.SSLEngineResult.HandshakeStatus;
import javax.net.ssl.SSLException;
import javax.net.ssl.SSLSession;

import com.elitecore.client.configuration.TlsConfiguration;
import com.elitecore.client.eap.EapMethodAuthenticator;
import com.elitecore.client.util.constants.CommunicationStates;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.core.commons.InitializationFailedException;
import com.elitecore.core.commons.tls.EliteSSLContextExt;
import com.elitecore.core.commons.tls.EliteSSLContextFactory;
import com.elitecore.core.commons.tls.EliteSSLParameter;
import com.elitecore.core.commons.tls.TLSVersion;
import com.elitecore.coreeap.data.tls.TLSSecurityParameters;
import com.elitecore.coreeap.packet.types.tls.record.TLSPlaintext;
import com.elitecore.coreeap.packet.types.tls.record.types.ITLSRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ClientHello;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ClientKeyExchange;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.HandshakeMessageRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ITLSHandshakeMessage;
import com.elitecore.coreeap.packet.types.tls.record.types.handshakemessages.ServerHello;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;
/**
 * Handles the whole TLS Handshake using SSLEngine
 * 
 * @author Kuldeep Panchal
 * @author Malav Desai
 *
 */
public class TLSAuthenticator implements EapMethodAuthenticator {
	private static final String MODULE = "TLS_AUTHENTICATOR";
	private static final String PEER_HOST = "CLIENT";
	private static final int PEER_PORT = 0;
	private SSLEngine clientEngine;
	private SSLContext sslContext;
	private EliteSSLContextExt eliteSSLContextExt;
	private byte[] outData = null;
	private TlsConfiguration configuration;
	private HandshakeStatus hsStatus;
	private ByteBuffer clientOut;       // write side of clientEngine
	private ByteBuffer clientIn;        // read side of clientEngine
	private ByteBuffer cTOs;            // "reliable" transport client->server
	private ByteBuffer sTOc;            // "reliable" transport server->client
	private SSLEngineResult clientResult;   // results from client's last operation

	/**
	 *finished will be true when Handshake status will become finish and used to change Communication Status
	 */
	private boolean finished;
	
	/**
	 * needMoreTask will be true when Handshake status will be NEED_TASK used to do processing again
	 * depending on the current HandshakeStatus (WRAP / UNWRAP)
	 */
	private boolean needMoreTask;
	private byte[] serverRandom;
	private byte[] clientRandom;
	private byte[] msk;
	private TLSPlaintext tlsPlaintext;
	private boolean doneMSKGeneration = false;
	
	public TLSAuthenticator(TlsConfiguration tlsConfiguration) throws InitializationFailedException {
		this.configuration = tlsConfiguration;
    	needMoreTask = true;
    	initTLSAuthenticator();
    	setFinished(false);
    	tlsPlaintext = new TLSPlaintext();
    }
	
	/**
	 * All initialization activities like creating SSLContext that is used to create SSLEngine 
	 * and also creating buffers to store data to and from the server are done here.
	 * 
	 * To create SSLContext, we will need CAConfiguration, CRLConfiguration, ServerCertificateProfile, 
	 * MinTlsVersion, MaxTlsVersion
	 * 
	 * @throws InitializationFailedException 
	 */
	public void initTLSAuthenticator() throws InitializationFailedException {
		EliteSSLContextFactory eliteSSLContextFactory = new EliteSSLContextFactory(configuration, configuration);
		
		TLSVersion minTlsVersion = configuration.getMinTlsVersion();
		TLSVersion maxTlsVersion = configuration.getMaxTlsVersion();
		
		EliteSSLParameter sslParameter = new EliteSSLParameter(configuration, minTlsVersion, maxTlsVersion);

		try {
			eliteSSLContextExt = eliteSSLContextFactory.createSSLContext(sslParameter);
			sslContext = eliteSSLContextExt.getSslContext();
			
			createSSLEngines();
			createBuffers();
			initHandshake();
			
		} catch (Exception e) {
			LogManager.getLogger().trace(e);
			throw new InitializationFailedException("Problem in creating sslcontext, Reason: " + e.getMessage());
		}
	}
	
	private void createSSLEngines() throws InitializationFailedException {
        clientEngine = sslContext.createSSLEngine(PEER_HOST, PEER_PORT);
        clientEngine.setEnabledCipherSuites(clientEngine.getSupportedCipherSuites());
        setSupportedTlsVersions();
        clientEngine.setUseClientMode(true);
    }
	
	private void createBuffers() {
        SSLSession session = clientEngine.getSession();
        int appBufferMax = session.getApplicationBufferSize();
        int netBufferMax = session.getPacketBufferSize();
        clientIn = ByteBuffer.allocate(appBufferMax + 50);
        cTOs = ByteBuffer.allocate(netBufferMax);
        sTOc = ByteBuffer.allocate(netBufferMax);
        clientOut = ByteBuffer.wrap("".getBytes());
    }
	
	private void setSupportedTlsVersions() throws InitializationFailedException {
		List<TLSVersion> supportedVersionList = eliteSSLContextExt.getEnabledTLSVersion();
		
		String[] supportedTLSVersions = new String [supportedVersionList.size()];

		for(int i=0; i < supportedVersionList.size(); i++){
			supportedTLSVersions[i] = supportedVersionList.get(i).version;  
		}
		
		clientEngine.setEnabledProtocols(supportedTLSVersions);  
	}
	
	public CommunicationStates process(byte[] inTlsData) throws AuthenticationException {
		setOutData(actionOnTlsHsRunning(inTlsData));
		if(isFinished()){
			return CommunicationStates.COMPLETED_PROCESSING;
		}
		return CommunicationStates.CONTINUE;
	}
	
	/**
	 * wrap and unwrap using SSLEngine is done here and TLS Record is returned
	 * @param inTlsData
	 * @return the resultant bytes of TLS Record
	 * @throws AuthenticationException 
	 */
	public byte[] actionOnTlsHsRunning(byte[] inTlsData) throws AuthenticationException {
		if(inTlsData.length > 2 && doneMSKGeneration == false){
			tlsPlaintext.setBytes(inTlsData);
		}
		ByteArrayOutputStream outDataStream = new ByteArrayOutputStream();
		sTOc.clear();
		sTOc = ByteBuffer.wrap(inTlsData);
		while(needMoreTask) {
			try {
				switch(hsStatus){
				case NEED_TASK:
					/**
					 * During handshaking, the SSLEngine might encounter tasks that might block or take a long time. 
					 * To preserve the non-blocking nature of SSLEngine, when the engine encounters such a task,
					 * it will return NEED_TASK 
					 */
					hsStatus = runDelegatedTasks(clientResult);
					break;
					
				case NEED_WRAP:
					/**
					 *  NEED_WRAP is responsible for generating network data. 
					 *  Depending on the state of the SSLEngine, this data might be handshake or application data.
					 */
					cTOs.clear();
					clientResult = getClientEngine().wrap(clientOut, cTOs);
					hsStatus = clientResult.getHandshakeStatus();
					LogManager.getLogger().info(MODULE, "Result status  : " + clientResult.getStatus() + "\tHandshake status : " + hsStatus);
					checkStatus(clientResult, inTlsData);
					cTOs.flip();
					outDataStream.write(cTOs.array(),0,cTOs.limit());
					if(hsStatus == HandshakeStatus.NEED_UNWRAP)
						needMoreTask = false;
					break;
				case NEED_UNWRAP:
					/**
					 * NEED_UNWRAP is responsible for consuming network data. 
					 * Depending on the state of the SSLEngine, this data might be handshake or application data.
					 */
					clientResult = getClientEngine().unwrap(sTOc, clientIn);
					checkStatus(clientResult, inTlsData);
					hsStatus = clientResult.getHandshakeStatus();
					LogManager.getLogger().info(MODULE, "Result status  : " + clientResult.getStatus() + "\tHandshake status : " + hsStatus);
					break;
				case FINISHED:
					/**
					 * The SSLEngine has just finished TLS Handshake and is ready for secure data communication to begin.
					 */
					setFinished(true);
					LogManager.getLogger().info(MODULE, "TLS Handshake Finished");
					LogManager.getLogger().info(MODULE, "Ready for application data");
					needMoreTask = false;
					break;
				case NOT_HANDSHAKING:
					/**
					 * The SSLEngine is not currently handshaking.
					 */
					LogManager.getLogger().info(MODULE, "Not Handshaking");
					break;
				}
			} catch (Exception e) {
				LogManager.getLogger().error(MODULE, "Error in Handshake: " + e.getMessage());
				throw new AuthenticationException(e.getMessage());
			}
		}
		needMoreTask=true;
		if(outDataStream.size() != 0  && doneMSKGeneration == false){
			tlsPlaintext.setBytes(outDataStream.toByteArray());
			generateSecretData();
		}
		return outDataStream.toByteArray();
	}
	
	/**
	 * In the result of wrap or unwrap during handshake, its status can be NEED_TASK. 
	 * It is handled here in this method 
	 * @param result
	 * @return New Handshake Status
	 * @throws Exception
	 */
	private HandshakeStatus runDelegatedTasks(SSLEngineResult result) throws Exception {

        if (result.getHandshakeStatus() == HandshakeStatus.NEED_TASK) {
        		Runnable runnable;
                while ((runnable = getClientEngine().getDelegatedTask()) != null) {
                	LogManager.getLogger().info(MODULE, "Running delegated task...");
                    runnable.run();
                }
                HandshakeStatus hsStatus = getClientEngine().getHandshakeStatus();
                if (hsStatus == HandshakeStatus.NEED_TASK) {
                    throw new Exception("handshake shouldn't need additional tasks");
                }
                LogManager.getLogger().info(MODULE, "New HandshakeStatus: " + hsStatus);
        	return hsStatus;
        } else {
        	return result.getHandshakeStatus();
        }
	}
	
	/**
	 * checking Status for Overflow and underflow
	 * @param result
	 */
	private void checkStatus(SSLEngineResult result, byte[] inTlsData) {
		 switch(result.getStatus()){
			case OK:
				/**
				 * No need to do any thing as the status is OK
				 */
				break;
			case BUFFER_OVERFLOW:
				/**
				 * Note: Needs to handle when BUFFER_OVERFLOW occurs
				 */
				sTOc.clear();
				sTOc = ByteBuffer.wrap(inTlsData);
				clientIn.clear();
				LogManager.getLogger().error(MODULE, "Status OVERFLOW..");
				break;
			case BUFFER_UNDERFLOW:
				/**
				 * Note: Needs to handle when BUFFER_UNDERFLOW occurs 
				 */
				LogManager.getLogger().info(MODULE, "Status UNDERFLOW..");
				break;
			default:
				break;
			}
	 }

	/**
	 * Method Decrypt called from other class after Handshaking completed to unwrap data
	 * @param tlsData
	 * @return byte array
	 * @throws SSLException
	 */
	public byte[] decrypt(byte[] tlsData) throws SSLException{
		sTOc.clear();
		sTOc = ByteBuffer.wrap(tlsData);
		clientIn.clear();
		clientResult = clientEngine.unwrap(sTOc, clientIn);
		clientIn.flip();
		byte[] rawData = new byte[clientIn.limit()];
		clientIn.get(rawData);
		//TODO remove below line before commit
		LogManager.getLogger().debug(MODULE, "Decrypted Data:\n" + TLSUtility.bytesToHex(rawData));
		return rawData;
	}

	/**
	 * Method Encrypt called from other class after Handshaking completed to wrap data
	 * @param rawData
	 * @return byte array
	 * @throws SSLException
	 */
	public byte[] encrypt(byte[] rawData) throws SSLException{
		LogManager.getLogger().debug(MODULE, "Bytes before encryption: " + TLSUtility.bytesToHex(rawData));
		clientOut.flip();
		clientOut.clear();
		clientOut = ByteBuffer.wrap(rawData);
		cTOs.clear();
		clientResult = clientEngine.wrap(clientOut, cTOs);
		cTOs.flip();
		byte[] tlsData = new byte[cTOs.limit()];
		cTOs.get(tlsData);
		//TODO remove below line before commit
		LogManager.getLogger().debug(MODULE, "Encrypted Data:\n" + TLSUtility.bytesToHex(tlsData));

		return tlsData;
	}
	
	private void generateSecretData(){
		Collection<ITLSRecordType> msgs = tlsPlaintext.getContent();
		for(ITLSRecordType itlsRecordType : msgs){
			if(itlsRecordType.getType() == TLSRecordConstants.Handshake.value){
				ITLSHandshakeMessage itlsHandshakeMessage = ((HandshakeMessageRecordType)itlsRecordType).getHandshakeMessage();
				switch (itlsHandshakeMessage.getType()) {
				case 1:
					clientRandom = ((ClientHello)itlsHandshakeMessage).getClientRandom();
					break;
				case 2:
					serverRandom = ((ServerHello)itlsHandshakeMessage).getServerRandom();
					break;
				case 16:
					byte[] preMSK = ((ClientKeyExchange)itlsHandshakeMessage).getKeyExchangeValue();
					msk = TLSUtility.generateMS(preMSK, serverRandom, clientRandom, new TLSSecurityParameters());
					doneMSKGeneration = true;
					break;
				default:
					break;
				}
			}
		}
		tlsPlaintext = new TLSPlaintext();
	}
	
	@Override
	public void reset() throws InitializationFailedException {
		createSSLEngines();
		initHandshake();
		needMoreTask = true;
		setFinished(false);
	}
	
	private void initHandshake() throws InitializationFailedException{
		try {
			clientEngine.beginHandshake();
			/*
			 * Need to initialize handshake status before actionOnTlsHSRunning() is called
			 */
			hsStatus = clientEngine.getHandshakeStatus();
		} catch (SSLException e) {
			LogManager.getLogger().trace(e);
			throw new InitializationFailedException("Cannot Begin TLS Handshake, Reason: " + e.getMessage());
		}
	}

	private SSLEngine getClientEngine(){
		return clientEngine;
	}
	
	@Override
	public byte[] getOutData() {
		return outData;
	}
	
	private void setOutData(byte[] outData) {
		this.outData = outData;
	}
	
	public boolean isFinished() {
		return finished;
	}

	private void setFinished(boolean finished) {
		this.finished = finished;
	}

	public byte[] getServerRandom() {
		return serverRandom;
	}

	public byte[] getClientRandom() {
		return clientRandom;
	}

	public byte[] getMsk() {
		return msk;
	}
}
