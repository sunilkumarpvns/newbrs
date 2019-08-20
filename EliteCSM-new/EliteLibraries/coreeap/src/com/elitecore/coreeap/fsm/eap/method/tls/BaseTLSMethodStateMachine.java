package com.elitecore.coreeap.fsm.eap.method.tls;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.Arrays;
import java.util.Collection;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Queue;
import java.util.concurrent.LinkedBlockingQueue;

import javax.crypto.SecretKey;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreeap.cipher.providers.constants.CipherSuites;
import com.elitecore.coreeap.commons.configuration.IEapConfigurationContext;
import com.elitecore.coreeap.commons.util.cipher.ICipherProvider;
import com.elitecore.coreeap.data.AAAEapRespData;
import com.elitecore.coreeap.data.AccountInfoProviderException;
import com.elitecore.coreeap.data.ICustomerAccountInfo;
import com.elitecore.coreeap.data.ICustomerAccountInfoProvider;
import com.elitecore.coreeap.data.tls.TLSSecurityKeys;
import com.elitecore.coreeap.data.tls.TLSSecurityParameters;
import com.elitecore.coreeap.dictionary.EAPTypeDictionary;
import com.elitecore.coreeap.dictionary.tls.TLSRecordTypeDictionary;
import com.elitecore.coreeap.fsm.EAPFailureReasonConstants;
import com.elitecore.coreeap.fsm.eap.method.BaseMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.IMethodTypesStateMachine;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.TlsAppMethodStateMachine;
import com.elitecore.coreeap.fsm.eap.method.tls.recordtypes.TlsHSMethodStateMachine;
import com.elitecore.coreeap.packet.EAPException;
import com.elitecore.coreeap.packet.EAPPacket;
import com.elitecore.coreeap.packet.TLSAlertException;
import com.elitecore.coreeap.packet.types.tls.TLSEAPType;
import com.elitecore.coreeap.packet.types.tls.record.ITLSRecord;
import com.elitecore.coreeap.packet.types.tls.record.TLSPlaintext;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ContentType;
import com.elitecore.coreeap.packet.types.tls.record.attribute.ProtocolVersion;
import com.elitecore.coreeap.packet.types.tls.record.types.AlertRecordType;
import com.elitecore.coreeap.packet.types.tls.record.types.ITLSRecordType;
import com.elitecore.coreeap.session.IEAPMethodSession;
import com.elitecore.coreeap.session.method.tls.TLSConnectionState;
import com.elitecore.coreeap.session.method.tls.TLSSessionState;
import com.elitecore.coreeap.util.Utility;
import com.elitecore.coreeap.util.constants.EapTypeConstants;
import com.elitecore.coreeap.util.constants.fsm.IEnum;
import com.elitecore.coreeap.util.constants.fsm.events.TlsEvents;
import com.elitecore.coreeap.util.constants.fsm.states.TlsStates;
import com.elitecore.coreeap.util.constants.tls.TLSFlagConstants;
import com.elitecore.coreeap.util.constants.tls.TLSRecordConstants;
import com.elitecore.coreeap.util.constants.tls.alert.TLSAlertDescConstants;
import com.elitecore.coreeap.util.constants.tls.alert.TLSAlertLevelConstants;
import com.elitecore.coreeap.util.tls.TLSUtility;

public abstract class BaseTLSMethodStateMachine extends BaseMethodStateMachine {
		
	
	private static final int MAX_TLS_RECORD_LENGTH = 18432 ; // 2^14 + 2048
	private static final boolean DECRYPT_FORCEFULLY = true;
	private static final boolean DECRYPT_IF_CCS_RECEIVED = false;
	private TLSSessionState sessionState=new TLSSessionState();

	private boolean bAlert = false;
	private boolean bCCSReceived = false;
	private boolean bCCSSend = false;
	private boolean bFragmented = false;	
	private boolean bAcknoledgement = false;
	private boolean bTTLSDraftRequest = false;
	private boolean bMACValidation=false;
	private boolean bRequestIdentity=false;
	private TLSRecordConstants recordtype;
	private IMethodTypesStateMachine m;
	private TLSSecurityKeys tlsSecurityKeys = new TLSSecurityKeys();
	private TLSSecurityKeys previousTLSSecurityKeys = new TLSSecurityKeys();
	private TLSSecurityKeys draftTTLSSecuriyKeys = new TLSSecurityKeys();
	private TLSSecurityParameters tlsSecurityParameters = new TLSSecurityParameters();
	private Queue<TLSEAPType> tlsRespRecordQueue =  new LinkedBlockingQueue<TLSEAPType>();
	
	private int retryCounter = 0;
		
	private byte[] fragmentedBytes;	
	private byte[] mskKey;
	
	private String failureReason;
	private ICustomerAccountInfo customerAccountInfo = null;
	
	public void reset() {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), "Reset TLS State machine");
		clearFlags();
		setFailureReason(null);
		setRecordtype(null);
		fragmentedBytes = null;
		clearCustomerAccountInfo();
//		TLSConnectionState connectionState = (TLSConnectionState)sessionState.getParameterValue(IEAPMethodSession.TLS_CONNECTION_STATE);
//		tlsRespRecordQueue =  new LinkedBlockingQueue<TLSEAPType>();
//		TLSConnectionState tlsConnectionState = (TLSConnectionState)sessionState.getParameterValue(TLSSessionState.TLS_CONNECTION_STATE);
//		tlsConnectionState.setParameterValue(IEAPMethodSession.TLS_SECURITY_KEY, tlsSecurityKeys);
//		tlsConnectionState.setParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER, tlsSecurityParameters);
	}

	public BaseTLSMethodStateMachine(IEapConfigurationContext eapConfigurationContext) {
		super(eapConfigurationContext);
		/***
		 * Here TLSSecurityKeys and TLSSecurityParameters are set into TLSConnectionState.
		 * So that Both the MethodTypesStateMachine and MethodStateMachine can share
		 * same session. 
		 */
		TLSConnectionState tlsConnectionState = (TLSConnectionState)sessionState.getParameterValue(TLSSessionState.TLS_CONNECTION_STATE);
		tlsConnectionState.setParameterValue(IEAPMethodSession.TLS_SECURITY_KEY, tlsSecurityKeys );
		tlsConnectionState.setParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER, tlsSecurityParameters);
		clearFlags();
	}

	@Override
	public IEnum getEvent(AAAEapRespData aaaEapRespData) {
		//parsing the request.
		parseTLSResponse(aaaEapRespData);
		// takes the decision.
		if(isAcknoledgement()){
			setAcknoledgement(false);
			return TlsEvents.TlsAckReceived;			
		}else if(isFragmented()){
			return TlsEvents.TlsFragmentedPacketReceived;
		}else if(isDone()){
			if(isSuccess()){
				return TlsEvents.TlsSuccess;
			}else if(isFailure()){
				return TlsEvents.TlsFailure;
			}
		}
		
		switch((TlsStates)getCurrentState()){
		case ACK:
			if(getRecordtype() == TLSRecordConstants.Alert){
				return TlsEvents.TlsAlertReceived;
			}else if(getRecordtype() == TLSRecordConstants.ChangeCipherSpec){
				return TlsEvents.TlsHS_CCS_HSReceived;
			}else if(getRecordtype() == TLSRecordConstants.ApplicationData){
				return TlsEvents.TlsApplicationRecordRecevied;
			}else{
				return TlsEvents.TlsUnconditionalEvent;
			}
		case ALERT:
				return TlsEvents.TlsUnconditionalEvent;
		case APPLICATION:
			if(getMethodCode() == EapTypeConstants.TTLS.typeId){
				return TlsEvents.TlsResponseIdentityReceived;
			}else{
				return TlsEvents.TlsUnconditionalEvent;
			}
		case CCS:
			if(getRecordtype() == TLSRecordConstants.Alert){
				return TlsEvents.TlsAlertReceived;
			}else if(getRecordtype() == TLSRecordConstants.ChangeCipherSpec){
				return TlsEvents.TlsHS_CCS_HSReceived;
			}else if(getRecordtype() == TLSRecordConstants.ApplicationData){
				return TlsEvents.TlsApplicationRecordRecevied;
			}else{
				return TlsEvents.TlsUnconditionalEvent;
			}			
		case DISCARD:
				return TlsEvents.TlsUnconditionalEvent;
		case FAILURE:
				return TlsEvents.TlsUnconditionalEvent;
		case FRAGMENT:
			if(getRecordtype() == TLSRecordConstants.Alert){
				return TlsEvents.TlsAlertReceived;
			}else if(getRecordtype() == TLSRecordConstants.ApplicationData){
				return TlsEvents.TlsApplicationRecordRecevied;
			}else if(getRecordtype() == TLSRecordConstants.ChangeCipherSpec){
				return TlsEvents.TlsHS_CCS_HSReceived;
			}else if(getRecordtype() == TLSRecordConstants.Handshake){
				return TlsEvents.TlsHandshakeRecordReceived;				
			}else {
				return TlsEvents.TlsUnconditionalEvent;
			}
		case HANDSHAKE:	
			if(getRecordtype() == TLSRecordConstants.Alert){
				return TlsEvents.TlsAlertReceived;
			}else if(getRecordtype() == TLSRecordConstants.ChangeCipherSpec){
				return TlsEvents.TlsHS_CCS_HSReceived;
			}else if(getRecordtype() == TLSRecordConstants.Handshake){
				return TlsEvents.TlsHandshakeRecordReceived;				
			}else {
				return TlsEvents.TlsUnconditionalEvent;
			}
		case INITIALIZE:
			if(aaaEapRespData.getEapRespPacket().getEAPType().getType() == EapTypeConstants.IDENTITY.typeId){
				return TlsEvents.TlsResponseIdentityReceived;
			}else if(aaaEapRespData.getEapRespPacket().getEAPType().getType() == EapTypeConstants.NAK.typeId){
				return TlsEvents.TlsNakReceived;
			}else if(getRecordtype() == TLSRecordConstants.Alert){
				return TlsEvents.TlsAlertReceived;
			}else if(getRecordtype() == TLSRecordConstants.ChangeCipherSpec){
				return TlsEvents.TlsHS_CCS_HSReceived;
			}else if(getRecordtype() == TLSRecordConstants.Handshake){
				return TlsEvents.TlsHandshakeRecordReceived;
			}else if(aaaEapRespData.getEapRespPacket().getEAPType().getType() == EapTypeConstants.IDENTITY.typeId){
				return TlsEvents.TlsResponseIdentityReceived;
			}else{
				return TlsEvents.TlsRequestReceived;
			}
		case RECEIVED:
			/***
			 * Do not change the order of the conditions.
			 * Because all the condition are dependent to each other.
			 */
			if(!isValidRequest()){
				return TlsEvents.TlsInvalidRequest;
			}else if(getRecordtype() == TLSRecordConstants.Alert){
				return TlsEvents.TlsAlertReceived;
			}else if(getRecordtype() == TLSRecordConstants.ApplicationData){
				return TlsEvents.TlsApplicationRecordRecevied; 
			}else if(getRecordtype() == TLSRecordConstants.ChangeCipherSpec){
				return TlsEvents.TlsHS_CCS_HSReceived;
			}else if(getRecordtype() == TLSRecordConstants.Handshake){
				return TlsEvents.TlsHandshakeRecordReceived;
			}else if(aaaEapRespData.getEapRespPacket().getEAPType().getType() == EapTypeConstants.IDENTITY.typeId){
				return TlsEvents.TlsResponseIdentityReceived;
			}else if(aaaEapRespData.getEapRespPacket().getEAPType().getType() == EapTypeConstants.NAK.typeId){
				return TlsEvents.TlsNakReceived;
			}
		case START:
			return TlsEvents.TlsRequestReceived;
		case SUCCESS:
			return TlsEvents.TlsUnconditionalEvent;
		default:
			return TlsEvents.TlsUnconditionalEvent;
		}
	}

	public IEnum getNextState(IEnum event){
		switch((TlsEvents)event){
		case TlsAckReceived:
			return TlsStates.ACK;
		case TlsAlertRaised:
		case TlsAlertReceived:
			return TlsStates.ALERT;
		case TlsApplicationRecordRecevied:
			return TlsStates.APPLICATION;
		case TlsHS_CCS_HSReceived:
			return TlsStates.CCS;
		case TlsFailure:
			return TlsStates.FAILURE;
		case TlsFragmentedPacketReceived:
			return TlsStates.FRAGMENT;
		case TlsHandshakeRecordReceived:
			return TlsStates.HANDSHAKE;
		case TlsInvalidRequest:
			return TlsStates.DISCARD;
		case TlsResponseIdentityReceived:
		case TlsNakReceived:
			return TlsStates.START;
		case TlsRequestReceived:
			return TlsStates.RECEIVED;				
		case TlsSuccess:
			return TlsStates.SUCCESS;
		case TlsUnconditionalEvent:
			return TlsStates.INITIALIZE;
		default:
			return TlsStates.DISCARD;

		}
	}
	
	@Override
	public IEnum[] getActionList(IEnum event) {
		return null;
	}
	
	@Override
	public  void applyActions(IEnum event,AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException{
		switch((TlsEvents)event){		
		case TlsAckReceived:
			actionOnHandleReceivedAck(aaaEapRespData,provider);
			break;
		case TlsAlertRaised:
			actionOnHandleAlert(aaaEapRespData,provider);
			break;
		case TlsAlertReceived:
			actionOnHandleAlert(aaaEapRespData,provider);
			break;
		case TlsApplicationRecordRecevied:
			actionOnHandleApplicationRecord(aaaEapRespData,provider);
			break;
		case TlsHS_CCS_HSReceived:
			actionOnHandleHS_CCS_HS(aaaEapRespData,provider);
			break;
		case TlsFailure:
			actionOnBuildFailure(aaaEapRespData,provider);
			break;
		case TlsFragmentedPacketReceived:
			actionOnHandleFragmentPacket(aaaEapRespData,provider);
			break;
		case TlsHandshakeRecordReceived:
			actionOnHandleHandshakeMessage(aaaEapRespData,provider);
			break;
		case TlsNakReceived:
			actionOnGenerateStart(aaaEapRespData,provider);
			break;
		case TlsRequestReceived:
			actionOnProcessRequest(aaaEapRespData,provider);
			break;
		case TlsResponseIdentityReceived:
			actionOnGenerateStart(aaaEapRespData,provider);
			break;
		case TlsSuccess:
			actionOnBuildSuccess(aaaEapRespData,provider);
			break;			
		case TlsUnconditionalEvent:
			actionOnInitialize(aaaEapRespData,provider);
			break;
		default:
			actionOnDiscardRequest(aaaEapRespData,provider);
		}
	}

	@Override
	public AAAEapRespData handleEvent(IEnum event, AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		applyActions(event, aaaEapRespData, provider);
		return null;
	}

	public void actionOnBuildAlertRecord(int level, int desc) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getModuleName() +" Action for BuildAlertRecord, State : " + getCurrentState());			
		setAlert(true);
		ITLSRecordType recordType = (ITLSRecordType) TLSRecordTypeDictionary.getInstance().createTLSRecord(TLSRecordConstants.Alert.value, tlsSecurityParameters.getProtocolVersion());
		((AlertRecordType) recordType).setAlertLevel(level);
		((AlertRecordType) recordType).setAlertDescription(desc);
		
		TLSPlaintext tlsResponseRecord = new TLSPlaintext();
		tlsResponseRecord.setContentType(new ContentType(TLSRecordConstants.Alert.value));
		tlsResponseRecord.setProtocolVersion(tlsSecurityParameters.getProtocolVersion());
	
		tlsResponseRecord.setContent(recordType);		
		tlsResponseRecord.refreshHeader();		
		
		byte[] finalResponseBytes = null;
		if(isCCSSend()){
			finalResponseBytes = encryptTLSRecord(tlsResponseRecord.getBytes(), tlsSecurityParameters.getCipher());			
		}else{
			finalResponseBytes = tlsResponseRecord.getBytes();
		}
		
		TLSEAPType tlsResponseType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(getMethodCode());
		tlsResponseType.setFlagValue(TLSFlagConstants.L_FLAG.value);
		tlsResponseType.setTLSMessageLength(finalResponseBytes.length);
		tlsResponseType.setTLSData(finalResponseBytes);
		
		fragmentTlsEapType(tlsResponseType,-1,true);
		setReqEapType(tlsRespRecordQueue.poll());
		setSuccess(false);
		setFailure(true);		
		setDone(false);
		setFailureReason(EAPFailureReasonConstants.TLS_NEGOTIATION_FAILED);
	}
	
	@Override
	public IEnum processRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
			LogManager.getLogger().debug(getModuleName(), getMethodName() + " packet being processed in state:" + getCurrentState());		
		try{
		TlsEvents event = (TlsEvents)getEvent(aaaEapRespData);
		setCurrentEvent(event);
		IEnum state = getNextState(event);
		changeCurrentState(state);
		handleEvent(event, aaaEapRespData,provider);
		}catch(TLSAlertException e){
			actionOnBuildAlertRecord(e.getLevel(), e.getDesc());
			if(LogManager.getLogger().isLogLevel(LogLevel.INFO))
				LogManager.getLogger().info(getModuleName(), "Alert generated! Level: " + TLSAlertLevelConstants.getName(e.getLevel()) + ", Desc: " +TLSAlertDescConstants.getName(e.getDesc()));
		}
		return getCurrentEvent();		
	}

	public boolean check(AAAEapRespData aaaEapRespData) {
		//TODO - Add the code for validate the TLS request.
		setValidRequest(true);
//		parseTLSResponse(aaaEapRespData);
		return isValidRequest();
	}

	abstract public String getMSKLabel();		
	
	public void setKey(byte[] key){
		if(key != null){
			this.mskKey = new byte[key.length];
			System.arraycopy(key, 0, this.mskKey, 0, key.length);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "MSK = " + Utility.bytesToHex(this.mskKey));
		}else{
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "MSK = null");
		}
	}
	
	public byte[] getKey() {
		return this.mskKey;
	}

	public void process(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		processRequest(aaaEapRespData,provider);
	}
	
	abstract public String getModuleName();
	abstract public String getMethodName();
	public boolean isAlert() {
		return bAlert;
	}

	public void setAlert(boolean alert) {
		bAlert = alert;
	}
	public void clearFlags(){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), "Flags haves been reset.");
		setDone(false);
		setSuccess(false);
		setFailure(false);		
		setCCSReceived(false);
		setAcknoledgement(false);
		setAlert(false);
		setValidRequest(false);
		setFragmented(false);
		setCCSSend(false);
		setTTLSDraftRequest(false);
		setRequestIdentity(false);
	}
	
	public void actionOnInitialize(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" action for Initialize, State : " + getCurrentState());
		
		processRequest(aaaEapRespData,provider);
	}
	public void parseTLSResponse(AAAEapRespData aaaEapRespData) {
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		if(eapPacket != null){
			setIdentifier(eapPacket.getIdentifier());
			if(eapPacket.getEAPType().getType() == EapTypeConstants.TLS.typeId
					|| eapPacket.getEAPType().getType() == EapTypeConstants.TTLS.typeId
					|| eapPacket.getEAPType().getType() == EapTypeConstants.PEAP.typeId){					
					//TODO - if the respEapType is needed more frequently,then set it in the member.
				TLSEAPType tlsEapType = (TLSEAPType)eapPacket.getEAPType();
				/***
				 * Here it is checked that received response is acknowledgment or not
				 */
				if(tlsEapType.getFlagValue() == TLSFlagConstants.NULL_FLAG.value 
						&& tlsEapType.getTLSData() == null || tlsEapType.getTLSData().length == 0)
					setAcknoledgement(true);
				/***
				 * Here it is checked that received response data is fragmented or not
				 */
				if(TLSFlagConstants.isFragmented(tlsEapType.getFlagValue())
						|| (isFragmented() && 
								 (tlsEapType.getFlagValue() == TLSFlagConstants.L_FLAG.value
										 || tlsEapType.getFlagValue() == TLSFlagConstants.NULL_FLAG.value))){
					setFragmented(true);
				}else{
					setFragmented(false);						
					List<byte[]> receivedRecords = tlsEapType.getTLSRecords();
					int size = receivedRecords.size();
					for(int i=0;i<size;i++){
						TLSPlaintext tlsPlaintextRecord = new TLSPlaintext(receivedRecords.get(i));
						if(tlsPlaintextRecord.getContentType().getType() == TLSRecordConstants.Handshake.value){
							setRecordtype(TLSRecordConstants.Handshake);
						}else if(tlsPlaintextRecord.getContentType().getType() == TLSRecordConstants.Alert.value){
							setRecordtype(TLSRecordConstants.Alert);
							break;
						}else if(tlsPlaintextRecord.getContentType().getType() == TLSRecordConstants.ChangeCipherSpec.value){
							setRecordtype(TLSRecordConstants.ChangeCipherSpec);
							break;
						}else if(tlsPlaintextRecord.getContentType().getType() == TLSRecordConstants.ApplicationData.value){
							setRecordtype(TLSRecordConstants.ApplicationData);
							break;
						}																
					}					
				}								
			}else if(eapPacket.getEAPType().getType() == EapTypeConstants.IDENTITY.typeId
					|| eapPacket.getEAPType().getType() == EapTypeConstants.NAK.typeId){
				setCCSReceived(false);
				setCCSSend(false);
			}
			setValidRequest(true);
			return;						
		}
		setValidRequest(false);
	}
	
	/**
	 * Be it any encryption algorithm from RFC 4346 the decryption logic remains the same.
	 * As we don't care about the first plain text block in TLS 1.1 (as it is discarded anyway), 
	 * so we use a fixed mask for decryption and ignore first plaintext block but according to CBC the next plain text blocks
	 * will be proper content.
	 * 
	 * @param dataBytes
	 * @param cipherSuite
	 * @return
	 */
	public byte[] decryptTLSRecord(byte[] dataBytes,CipherSuites cipherSuite){
		if(dataBytes != null){
			try {
				this.previousTLSSecurityKeys = (TLSSecurityKeys) this.tlsSecurityKeys.clone();
			} catch (CloneNotSupportedException e) {			
				if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
					LogManager.getLogger().error(getModuleName(), "problem in clonning TLS Security keys, reason : "+ e.getMessage());
			}
			/***
			 * Here only the TLSRecord Data has been decrypted,
			 * first 5 byte is in the plaintext so there is no
			 * need to decrypt it.
			 */
			final int MAX_TLS_RECORD_LENGTH = 18432 ; // 2^14 + 2048
			int length = dataBytes[3] & 0xFF;
			length = length << 8;
			length = length | (dataBytes[4] & 0xFF);
			if(length > MAX_TLS_RECORD_LENGTH || dataBytes.length > MAX_TLS_RECORD_LENGTH)
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.RECORD_OVERFLOW.typeId);
			ICipherProvider cipherProvider = tlsSecurityParameters.getCipherProvider();
			byte[] eDataBytes = new byte[dataBytes.length - 5];			
			int ivSize = cipherProvider.getIVSize();
			System.arraycopy(dataBytes, 5, eDataBytes, 0, eDataBytes.length);
			
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "TLS Record Recevied : " + Utility.bytesToHex(dataBytes));
			
			SecretKey encryptionReadKey =  cipherProvider.generateKeyFromBytes(this.tlsSecurityKeys.getEncryptionRead());
			
			/*
             * RFC 4346 recommends two algorithms used to generated the
             * per-record IV. The implementation uses the algorithm (2)(a),
             * as described at section 6.2.3.2 of RFC 4346.
             *
             * As we don't care about the initial IV value for TLS 1.1 or
             * later, so if the "iv" parameter is null, we use the default
             * value generated by Cipher.init() for encryption, and a fixed
             * mask for decryption.
             */
			if(tlsSecurityParameters.getProtocolVersion().isGreater(ProtocolVersion.TLS1_0)) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG))
					LogManager.getLogger().debug(getModuleName(), "Decrypting using TLS version " + ((tlsSecurityParameters.getProtocolVersion() == ProtocolVersion.TLS1_2)?"1.2":"1.1"));
				byte[] explicitIv = TLSUtility.getFixedMask(ivSize);
				tlsSecurityKeys.setClientIV(explicitIv);
			}
			
			byte[] decryptedData = cipherProvider.decrypt(eDataBytes, encryptionReadKey, tlsSecurityKeys.getClientIV());
			
			int hashSize = cipherProvider.getHashSize();
			
			byte[] data = Arrays.copyOfRange(decryptedData, 0, decryptedData.length - hashSize);
			byte[] receivedMAC = Arrays.copyOfRange(decryptedData, decryptedData.length-hashSize, decryptedData.length);
	
			/***
			 * Calculating mac
			 */
			byte[] macInput = new byte[data.length + 13];			
			
			System.arraycopy(tlsSecurityKeys.getReadSeq(), 0, macInput, 0, tlsSecurityKeys.getReadSeq().length);
					
			System.arraycopy(dataBytes, 0, macInput, 8, 3);
			macInput[11] = (byte)((data.length>> 8)& 0xFF);;
			macInput[12] = (byte)((data.length) & 0xFF);
			
			System.arraycopy(data, 0, macInput, 13, data.length);		
			
			/*
			 *  As per RFC 5246 Section 5 for MAC Calculation Cipher Suite specified Algorithm is used.
			 */
			byte[] calculatedMAC = TLSUtility.HMAC(tlsSecurityParameters.getMACAlgo(), macInput, tlsSecurityKeys.getMacRead());
			
			/**
			 * TLS 1.1, generating BAD_RECORD_MAC Alert on padding error after calculation of MAC for 
			 * preventing CBC Time Attack
			 */
			if(calculatedMAC == null || receivedMAC == null || !Arrays.equals(calculatedMAC, receivedMAC)){
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)) {
					LogManager.getLogger().trace(getModuleName(), "MAC received is invalid. Expected: " + TLSUtility.bytesToHex(calculatedMAC) + ", received: " + TLSUtility.bytesToHex(receivedMAC));
				}
				throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.BAD_RECORD_MAC.typeId);
			}
			/***
			 * updating the client_iv(decryption iv ) for the next decryption 
			 */
			int blockSize = cipherProvider.getBlockSize();
			byte[] clientIV = tlsSecurityKeys.getClientIV();
			ByteBuffer in = ByteBuffer.allocate(eDataBytes.length);
			in.put(eDataBytes);
			in.rewind();
		    if(clientIV != null)
		    {			        
		        ByteBuffer iv = in.duplicate();
		        if(iv.remaining() < blockSize){
		           throw new IllegalArgumentException((new StringBuilder()).append("Encrypted data too small (").append(iv.remaining()).append(")").toString());
		        }
		        iv.position(iv.limit() - blockSize);
		        iv.get(clientIV);
		    }
		    tlsSecurityKeys.setClientIV(clientIV);
		    tlsSecurityKeys.incrementReadSeq();
		    /**
		     * returns the original data as the decrypted data.
		     * here i have prepend the dataBytes becuase the header of the handshake message is not encrypted.
		     */
		    byte[] returnbytes = new byte[data.length + 5];
		    System.arraycopy(dataBytes, 0, returnbytes, 0, 5);
		    System.arraycopy(data, 0, returnbytes, 5, data.length);
		    return returnbytes;
		}		
		return dataBytes;
	}
	
	/**
	 * RFC 4346 recommends two algorithms used to generated the
	 * per-record IV. The implementation uses the algorithm (2)(a),
	 * as described at section 6.2.3.2 of RFC 4346.
	 * @param recordBytes
	 * @param cipherSuite
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public byte[] encryptTLSRecord(byte[] recordBytes,CipherSuites cipherSuite){
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), "Received record bytes for encryption : " + Utility.bytesToHex(recordBytes));
		if(recordBytes != null){			
			ICipherProvider cipherProvider = tlsSecurityParameters.getCipherProvider();
			
			SecretKey encryptionWriteKey =  cipherProvider.generateKeyFromBytes(this.tlsSecurityKeys.getEncryptionWrite());			
			int ivSize = cipherProvider.getIVSize();
			
			
			byte[] macInput = new byte[recordBytes.length + 8];
			System.arraycopy(tlsSecurityKeys.getWriteSeq(), 0, macInput, 0,	tlsSecurityKeys.getWriteSeq().length);
			System.arraycopy(recordBytes, 0, macInput, tlsSecurityKeys.getWriteSeq().length, recordBytes.length);
			
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "MAC input : " + Utility.bytesToHex(macInput));
			
			/***
			 * Generating a MAC
			 *  As per RFC 5246 Section 5 for MAC Calculation Cipher Suite specified Algorithm is used.
			 */
			byte[] genMAC = TLSUtility.HMAC(tlsSecurityParameters.getMACAlgo(), macInput,tlsSecurityKeys.getMacWrite());
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "Generated MAC "  + Utility.bytesToHex(genMAC));
			
			/**
			 * Data without the tls record header
			 */
			byte[] dataBytes = Arrays.copyOfRange(recordBytes, 5, recordBytes.length);
			
			byte[] encInput = new byte[dataBytes.length + genMAC.length];
			System.arraycopy(dataBytes, 0, encInput, 0, dataBytes.length);
			/***
			 * Appending a MAC after the original data.
			 */
			System.arraycopy(genMAC, 0, encInput, dataBytes.length, genMAC.length);
			byte[] eDataBytes = cipherProvider.encrypt(encInput, encryptionWriteKey, tlsSecurityKeys.getServerIV());
			
			//FIXME null check for encryption failure is remaining
			ByteArrayOutputStream bytebuffer = new ByteArrayOutputStream();
			bytebuffer.write(recordBytes[0]);
			bytebuffer.write(recordBytes[1]);
			bytebuffer.write(recordBytes[2]);
			bytebuffer.write((byte)(eDataBytes.length >>> 8) & 0xFF);
			bytebuffer.write((byte)eDataBytes.length & 0xFF);
			try {
				bytebuffer.write(eDataBytes);
			} catch (IOException e) {
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(getModuleName(), "Problem while generating encrypted bytes : " + e.getMessage());
				LogManager.getLogger().trace(getModuleName(), e);
			}
			
			byte[] finalBytes = bytebuffer.toByteArray();									
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "Final Encrypted Record Bytes : " + Utility.bytesToHex(finalBytes));
			
			/**
			 * According to RFC for TTLSv0, TLS completion ACK is not require for the TTLS Authentication.
			 */
			if(dataBytes == null || dataBytes.length <= 0){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN))
					LogManager.getLogger().warn(getModuleName(), "TTLSv0 draft standard has been identified, which is not advisable, continuing with warning.");
				setTTLSDraftRequest(true);
				try {
					this.draftTTLSSecuriyKeys = (TLSSecurityKeys) this.previousTLSSecurityKeys.clone();
				} catch (CloneNotSupportedException e) {
					if(LogManager.getLogger().isLogLevel(LogLevel.ERROR))
						LogManager.getLogger().error(getModuleName(), "problem in clonning TLS Security keys, reason : "+ e.getMessage());

				}
				return finalBytes;
			}
			/***
			 * updating a server_iv(encryption iv) for the next encryption
			 */
			byte[] encIV = tlsSecurityKeys.getServerIV();
			int blockSize = cipherProvider.getBlockSize();
			LinkedList out = new LinkedList();
			
			byte[] headerBytes = new byte[5];
			System.arraycopy(finalBytes, 0, headerBytes, 0, 5);			
			ByteBuffer header = ByteBuffer.allocate(5);
			header.put(headerBytes);
			header.flip();
			out.add(header);
			
			byte[] encDataBytes = new byte[finalBytes.length - 5];
			System.arraycopy(finalBytes, 5, encDataBytes, 0, finalBytes.length - 5);
			ByteBuffer encData = ByteBuffer.allocate(encDataBytes.length);
			encData.put(encDataBytes);
			encData.rewind();
			out.add(encData);
			
			if(blockSize > 0)
	        {
	            int residue = 0;
	            ListIterator buffers;	            
	            for(buffers = out.listIterator(out.size()); buffers.hasPrevious() && residue < blockSize; residue += ((ByteBuffer)buffers.previous()).remaining()){
	            }

	            if(residue < blockSize)
	                throw new IllegalArgumentException("Internal error, not enough residue for next IV");
	            int srcOffset = residue - blockSize;	            
	            int amount;
	            for(int dstOffset = 0; dstOffset < blockSize; dstOffset += amount)
	            {
	                ByteBuffer b = ((ByteBuffer)buffers.next()).duplicate();
	                if(srcOffset > 0)
	                    b.position(b.position() + srcOffset);
	                amount = b.remaining();
	                b.get(encIV, dstOffset, amount);
	                srcOffset = 0;
	            }
	        }	
			tlsSecurityKeys.setServerIV(encIV);
			tlsSecurityKeys.incrementWriteSeq();
			/***
			 * returns the encryption data bytes
			 */
			
			return finalBytes;
		}
		return null;
	}
	abstract public TLSEAPType createStartType();

	public void actionOnProcessRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for ProcessRequest, State : " + getCurrentState());
		//TODO - temporary this code is moved to the getEvent Method
//		parseTLSResponse(aaaEapRespData);
		processRequest(aaaEapRespData,provider);
	}
	public void actionOnDiscardRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for DiscardRequest, State : " + getCurrentState());
		setSuccess(false);
		setFailure(false);
		setDone(true);
	}	

	public void actionOnGenerateStart(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for GenerateStart, State : " + getCurrentState());

		/***
		 * When We received the response identity, it clears all the previous negotiation data.
		 */
		this.fragmentedBytes = null; 
		this.tlsRespRecordQueue =  new LinkedBlockingQueue<TLSEAPType>();
		TLSConnectionState tlsConnectionState = (TLSConnectionState)sessionState.getParameterValue(TLSSessionState.TLS_CONNECTION_STATE);
		tlsSecurityKeys= (TLSSecurityKeys)tlsConnectionState.getParameterValue(IEAPMethodSession.TLS_SECURITY_KEY);
		tlsSecurityParameters=(TLSSecurityParameters)tlsConnectionState.getParameterValue(IEAPMethodSession.TLS_SECURITY_PARAMETER);
		//moved the code for resetting the TLS connection state to Tls Handshake state machine
		//as the session is resumed or not is known during client hello.
		resetFlags();
		
		setReqEapType(createStartType());
//		setDone(true);
	}

	public void actionOnHandleAlert(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for HandleAlert, State : " + getCurrentState());
		try {
			TLSEAPType tlsEapType = (TLSEAPType) aaaEapRespData.getEapRespPacket().getEAPType();
			List<byte[]> tlsRecordList = tlsEapType.getTLSRecords();
			int size = tlsRecordList.size();
			TLSPlaintext tlsPlaintext = null;
			for(int i=0;i<size;i++) {
				tlsPlaintext = new TLSPlaintext(tlsRecordList.get(i));
				Collection<ITLSRecordType> ITLSRecordTypeList = tlsPlaintext.getContent();
				for (ITLSRecordType recordType :ITLSRecordTypeList) {
					if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
						LogManager.getLogger().trace(getModuleName(), getMethodName() +" Alert received ! Alert Level : " + TLSAlertLevelConstants.getName(((AlertRecordType) recordType).getAlertLevel()) + ", Alert Description : " + TLSAlertDescConstants.getName(((AlertRecordType) recordType).getAlertDescription()) + "("+ ((AlertRecordType) recordType).getAlertDescription() +")");
					if (((AlertRecordType) recordType).getAlertDescription() == TLSAlertDescConstants.CLOSE_NOTIFY.typeId) {
						throw new TLSAlertException(TLSAlertLevelConstants.WARNING.typeId,TLSAlertDescConstants.CLOSE_NOTIFY.typeId);
					}
				}
			}
		} catch (Exception e) {
			throw new TLSAlertException(TLSAlertLevelConstants.FATAL.typeId,TLSAlertDescConstants.BAD_RECORD_MAC.typeId);
		}
		setDone(true);
		setFailure(true);
		setFailureReason(EAPFailureReasonConstants.TLS_NEGOTIATION_FAILED);
	}
	
	public void actionOnHandleReceivedAck(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for HandleReceivedAck, State : " + getCurrentState());
		if(isAlert()){
			setAlert(false);
			setFailure(true);
			setDone(true);
			setFailureReason(EAPFailureReasonConstants.TLS_NEGOTIATION_FAILED);
		}	
		EAPPacket eapPacket = aaaEapRespData.getEapRespPacket();
		if(isRequestEAPIdentityInTunnelOnACK()){
			if((!m.isSuccess()) && isRequestIdentity()){
				actionOnGenerateIdentityRequest(aaaEapRespData,provider);
				setRequestIdentity(false);
				return;
			}	
		}
		if(!tlsRespRecordQueue.isEmpty()){
			setReqEapType(tlsRespRecordQueue.poll());
		}else{
			setDone(true);
		}
	}
	
	public void actionOnHandleFragmentPacket(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for HandleFragmentPacket, State : " + getCurrentState());
		TLSEAPType tlsEapType = (TLSEAPType) aaaEapRespData.getEapRespPacket().getEAPType();
		if(tlsEapType.getFlagValue() == TLSFlagConstants.LM_FLAG.value){
			this.fragmentedBytes = TLSUtility.appendBytes(this.fragmentedBytes, tlsEapType.getData());
			TLSEAPType tlsAckType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(getMethodCode());
			setReqEapType(tlsAckType);
		}else if(tlsEapType.getFlagValue() == TLSFlagConstants.M_FLAG.value){
			this.fragmentedBytes = TLSUtility.appendBytes(this.fragmentedBytes, tlsEapType.getTLSData());
			TLSEAPType tlsAckType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(getMethodCode());
			setReqEapType(tlsAckType);
		}else if((tlsEapType.getFlagValue() == TLSFlagConstants.NULL_FLAG.value || tlsEapType.getFlagValue() == TLSFlagConstants.L_FLAG.value)
				&& isFragmented()){
			this.fragmentedBytes = TLSUtility.appendBytes(this.fragmentedBytes, tlsEapType.getTLSData());
			setFragmented(false);
			TLSEAPType tlsReqEapType = (TLSEAPType )aaaEapRespData.getEapRespPacket().getEAPType();
			tlsReqEapType.setData(this.fragmentedBytes);		
			tlsReqEapType.setFlagValue(TLSFlagConstants.L_FLAG.value);
			tlsReqEapType.setTLSMessageLength(tlsReqEapType.getTLSData().length);
			aaaEapRespData.getEapRespPacket().resetLength();
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), "EAP request bytes : " + Utility.bytesToHex(aaaEapRespData.getEapRespPacket().getEAPType().getData()));
/*			try {
								
				TLSEAPType tlsReqEapType = new TLSEAPType(this.fragmentedBytes);
				aaaEapRespData.getEapRespPacket().setEAPType(tlsReqEapType);
			} catch (InvalidEAPTypeException e) {
				Logger.logTrace(getModuleName(),"Error during concatinating fragmented request : " + e.getMessage());
				Logger.logTrace(getModuleName(),e);
			}
*/			
			process(aaaEapRespData,provider);
		}		
//		setDone(true);
	}	

	public void actionOnGenerateIdentityRequest(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for GenerateIdentityRequest, State : " + getCurrentState());

			TLSPlaintext tlsReceivedRecord = new TLSPlaintext();					
			m = getMethodTypeStateMachine(TLSRecordConstants.ApplicationData.value,getMethodCode());			
			if(m != null){
				try{
					m.process(tlsReceivedRecord,provider);
				}catch(AccountInfoProviderException e){
					this.tlsSecurityKeys = this.previousTLSSecurityKeys;
					throw e;
				}
				if(m.isDone()){
					if(m.isSuccess()){
						byte[] msk = TLSUtility.generateMSK(tlsSecurityKeys.getMasterSecret(), tlsSecurityKeys.getClientRandom(), tlsSecurityKeys.getServerRandom(), getMSKLabel(), tlsSecurityParameters);
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Generated MSK Material : " + Utility.bytesToHex(msk));
						setKey(msk);						
						setCustomerAccountInfo(m.getCustomerAccountInfo());
						setDone(true);
						setSuccess(true);
					}else if(m.isFailure()){
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Application Negotiation Failed");
						setDone(true);						
						setFailure(true);
						setSuccess(false);
					}
				}else {
					if(m.isSuccess()){
						byte[] msk = TLSUtility.generateMSK(tlsSecurityKeys.getMasterSecret(), tlsSecurityKeys.getClientRandom(), tlsSecurityKeys.getServerRandom(), getMSKLabel(), tlsSecurityParameters);
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Generated MSK Material : " + Utility.bytesToHex(msk));
						setKey(msk);						
						setCustomerAccountInfo(m.getCustomerAccountInfo());
						setSuccess(true);						
					}else if(m.isFailure()){
						setFailure(true);
					}
//					setDone(true);
					byte[] responseBytes = m.getResponseTLSRecord();
					byte[] finalResponseBytes = null;
					finalResponseBytes = encryptTLSRecord(responseBytes, this.tlsSecurityParameters.getCipher());							

					TLSEAPType tlsResponseType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(getMethodCode());
					tlsResponseType.setFlagValue(TLSFlagConstants.L_FLAG.value);
					tlsResponseType.setTLSMessageLength(finalResponseBytes.length);
					tlsResponseType.setTLSData(finalResponseBytes);
					fragmentTlsEapType(tlsResponseType,aaaEapRespData.getFragmentedSize(),false);
					setReqEapType(tlsRespRecordQueue.poll());
				}
			}
	}
	
	public void actionOnBuildFailure(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for BuildFailure, State : " + getCurrentState());
		setDone(true);
		getConnectionState().setParameterValue(IEAPMethodSession.SESSION_ID,new byte[0]);
	}
	
	public void actionOnBuildSuccess(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for BuildSuccess, State : " + getCurrentState());
		setDone(true);
	}	

	public TLSPlaintext convertBytesToTLSRecord(byte[] receivedRecordBytes,boolean forcefully){
		TLSPlaintext tlsPlaintextRecord = new TLSPlaintext();
		if(isCCSReceived() || forcefully){				
			if(CipherSuites.isSupported(this.tlsSecurityParameters.getCipher().code, this.tlsSecurityParameters.getProtocolVersion())){
				byte[] plainTextBytes = decryptTLSRecord(receivedRecordBytes, this.tlsSecurityParameters.getCipher());				
				tlsPlaintextRecord.setBytes(plainTextBytes);	
			}
		}else{			
			tlsPlaintextRecord.setBytes(receivedRecordBytes);			
		}		
		return tlsPlaintextRecord;
	}	
	
	public void actionOnHandleHS_CCS_HS(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for HandleHS_CCS_HS, State : " + getCurrentState());
		
		TLSEAPType tlsEapType = (TLSEAPType)aaaEapRespData.getEapRespPacket().getEAPType();
		List<byte[]> receivedRecords = tlsEapType.getTLSRecords();
		
		//Here the recordType is set to handshake for the temporary purpose.
		setRecordtype(TLSRecordConstants.Handshake);
		TLSPlaintext tlsRecord = null;
		int size = receivedRecords.size();
		for(int i=0;i<size;i++){
			
			byte[] receivedRecordBytes = receivedRecords.get(i);
			if(!isCCSReceived()){
				tlsRecord = new TLSPlaintext();
				tlsRecord.setBytes(receivedRecordBytes);
				
				if(tlsRecord.getContentType().getType() == TLSRecordConstants.ChangeCipherSpec.value){
					setRecordtype(TLSRecordConstants.ChangeCipherSpec);
//					break;
				}
//				Collection<ITLSRecordType> tlsRecordTypeCollection = tlsRecord.getContent();
//				Iterator<ITLSRecordType> tlsRecordTypeIterator = tlsRecordTypeCollection.iterator();
//				while(tlsRecordTypeIterator.hasNext()){
//					ITLSRecordType tlsRecordType = tlsRecordTypeIterator.next();
//					if(tlsRecordType.getType() == TLSRecordConstants.ChangeCipherSpec.value){
//						setRecordtype(TLSRecordConstants.ChangeCipherSpec);
//						Logger.logTrace(getModuleName(), "CCS Record Found in Request");
//						break;
//					}
//				}
			}
			
			if(getRecordtype() == TLSRecordConstants.ChangeCipherSpec){				
				setCCSReceived(true);	
				if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
					LogManager.getLogger().trace(getModuleName(), "TLS record received" + tlsRecord);				
				receivedRecordBytes = receivedRecords.get(++i);
			}
			TLSPlaintext tlsReceivedRecord = null;
			tlsReceivedRecord = convertBytesToTLSRecord(receivedRecordBytes,DECRYPT_IF_CCS_RECEIVED);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), tlsReceivedRecord.toString());
			m = getMethodTypeStateMachine(tlsReceivedRecord.getContentType().getType(),getMethodCode());		
			if(m != null){
				m.process(tlsReceivedRecord,provider);
				if(m.isDone()){
					if(m.isSuccess()){
						setCustomerAccountInfo(m.getCustomerAccountInfo());
						setDone(true);
						setSuccess(true);
					}else if(m.isFailure()){
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Handshake Negotiation Failed");
						setDone(true);						
						setFailure(true);
						setFailureReason(EAPFailureReasonConstants.TLS_NEGOTIATION_FAILED);
					}else {
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "TLS Response Record Generated." + tlsRecord);
						
						byte[] responseBytes = m.getResponseTLSRecord();
						byte[] finalResponseBytes = null;
						if(isCCSReceived()){
							finalResponseBytes = encryptTLSRecord(responseBytes, this.tlsSecurityParameters.getCipher()); 
							finalResponseBytes = TLSUtility.appendBytes(tlsRecord.getBytes(), finalResponseBytes);
						}else{
							finalResponseBytes = responseBytes;
						}
						TLSEAPType tlsResponseType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(getMethodCode());
						tlsResponseType.setFlagValue(TLSFlagConstants.L_FLAG.value);
						tlsResponseType.setTLSMessageLength(finalResponseBytes.length);
						tlsResponseType.setTLSData(finalResponseBytes);
						fragmentTlsEapType(tlsResponseType,aaaEapRespData.getFragmentedSize(),true);
						
						setReqEapType(tlsRespRecordQueue.poll());
						
						if(isRequestEAPIdentityInTunnelOnACK()){
							m = getMethodTypeStateMachine(TLSRecordConstants.ApplicationData.value,getMethodCode());
//							actionOnGenerateIdentityRequest(aaaEapRespData,provider);
							setRequestIdentity(true);
						}
						/***
						 * if there is a TLS Negotiation then After finished message you have to send success.
						 */
						byte[] msk = TLSUtility.generateMSK(tlsSecurityKeys.getMasterSecret(), tlsSecurityKeys.getClientRandom(), tlsSecurityKeys.getServerRandom(), getMSKLabel(), tlsSecurityParameters);
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Generated MSK Material : " + Utility.bytesToHex(msk));
						setKey(msk);
						setSuccess(true);
						setCCSSend(true);
					}
				}
			}
		}		
	}	

	public void actionOnHandleHandshakeMessage(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for HandleHandshakeMessage, State : " + getCurrentState());
		
		TLSEAPType tlsEapType = (TLSEAPType)aaaEapRespData.getEapRespPacket().getEAPType();
		List<byte[]> receivedRecords = tlsEapType.getTLSRecords();
		int size = receivedRecords.size();
		for(int i=0;i<size;i++){			
			TLSPlaintext tlsReceivedRecord = null;
			tlsReceivedRecord = convertBytesToTLSRecord(receivedRecords.get(i),DECRYPT_IF_CCS_RECEIVED);
			if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
				LogManager.getLogger().trace(getModuleName(), tlsReceivedRecord.toString());
			m = getMethodTypeStateMachine(TLSRecordConstants.Handshake.value,getMethodCode());		
			if(m != null){
				byte [] tmpRespBytes = null;
				m.setOUI(aaaEapRespData.getOUI());
				m.process(tlsReceivedRecord,provider);
				if((Boolean)(getConnectionState().getParameterValue(TLSConnectionState.IS_SESSION_RESUMED))){
					tmpRespBytes=m.getResponseTLSRecord();
					TLSPlaintext tlsCCSRecord = (TLSPlaintext)createCCSRecord();
					tmpRespBytes = TLSUtility.appendBytes(tmpRespBytes,tlsCCSRecord.getBytes());
					LogManager.getLogger().trace(getModuleName(), tlsCCSRecord.toString());
					setCCSSend(true);
					m.process(tlsReceivedRecord, provider);
				}
				if(m.isDone()){
					if(m.isSuccess()){
						setCustomerAccountInfo(m.getCustomerAccountInfo());
						setDone(true);
						setSuccess(true);
					}else if(m.isFailure()){
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Handshake Negotiation Failed");
						setDone(true);						
						setFailure(true);
						setFailureReason(EAPFailureReasonConstants.TLS_NEGOTIATION_FAILED);
					}else {
						byte[] responseBytes = m.getResponseTLSRecord();
						byte[] finalResponseBytes = null;
						byte[] tResBytes=null;
						
						if(isCCSSend()){
							tResBytes = encryptTLSRecord(responseBytes, this.tlsSecurityParameters.getCipher()); 
						}
						if(tmpRespBytes!=null){
							 finalResponseBytes = tmpRespBytes;
							 finalResponseBytes=  TLSUtility.appendBytes(finalResponseBytes,tResBytes);
						 }else{
							 finalResponseBytes=responseBytes;
						 }
						
						TLSEAPType tlsResponseType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(getMethodCode());
						tlsResponseType.setFlagValue(TLSFlagConstants.L_FLAG.value);
						tlsResponseType.setTLSMessageLength(finalResponseBytes.length);
						tlsResponseType.setTLSData(finalResponseBytes);
						fragmentTlsEapType(tlsResponseType,aaaEapRespData.getFragmentedSize(),true);
						setReqEapType(tlsRespRecordQueue.poll());

						if((Boolean)(getConnectionState().getParameterValue(TLSConnectionState.IS_SESSION_RESUMED))){
							/***
							 * if there is a TLS/TTLS/PEAP Negotiation then After finished message you have to send success for Resumed Session
							 */
							byte[] msk = TLSUtility.generateMSK(tlsSecurityKeys.getMasterSecret(), tlsSecurityKeys.getClientRandom(), tlsSecurityKeys.getServerRandom(), getMSKLabel(), tlsSecurityParameters);
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(getModuleName(), "Generated MSK Material : " + Utility.bytesToHex(msk));
							setKey(msk);
							setSuccess(true);
						}
					}
				}
			}
		}		
	}	

	public void actionOnHandleApplicationRecord(AAAEapRespData aaaEapRespData,ICustomerAccountInfoProvider provider) throws EAPException {
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), getMethodName() +" Action for HandleApplicationRecord, State : " + getCurrentState());

		TLSEAPType tlsEapType = (TLSEAPType)aaaEapRespData.getEapRespPacket().getEAPType();		 
		
		if(m != null || m.getTLSConnectionState() != null){			
			m.getTLSConnectionState().setIdentifier(aaaEapRespData.getEapRespPacket().getIdentifier());
		}

		List<byte[]> receivedRecords = tlsEapType.getTLSRecords();
		int size = receivedRecords.size();
		for(int i=0;i<size;i++){
			TLSPlaintext tlsReceivedRecord = null;
			if(getMethodCode() == EapTypeConstants.TTLS.typeId || getMethodCode() == EapTypeConstants.TLS.typeId){
				tlsReceivedRecord = convertBytesToTLSRecord(receivedRecords.get(i),DECRYPT_IF_CCS_RECEIVED);
			}else{
				tlsReceivedRecord = convertBytesToTLSRecord(receivedRecords.get(i),DECRYPT_FORCEFULLY);
			}
			
			LogManager.getLogger().info(getModuleName(), tlsReceivedRecord.toString());
			m = getMethodTypeStateMachine(tlsReceivedRecord.getContentType().getType(),getMethodCode());			
			if(m != null){
				try{
					m.process(tlsReceivedRecord,provider);
				}catch(AccountInfoProviderException e){
					if(isTTLSDraftRequest()){
						if(this.draftTTLSSecuriyKeys != null){
							this.tlsSecurityKeys = this.draftTTLSSecuriyKeys;
							setTTLSDraftRequest(false);
						}else{
							if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
								LogManager.getLogger().trace(getModuleName(), getMethodName() +" Problem in reverting draft security keys " + getCurrentState());							
							this.tlsSecurityKeys = this.previousTLSSecurityKeys;
						}
					}else{
						this.tlsSecurityKeys = this.previousTLSSecurityKeys;
					}
					throw e;
				}
				if(m.isDone()){
					if(m.isSuccess()){
						/***
						 * if there is a TLS Negotiation then After finished message you have to send success.
						 */
						byte[] msk = TLSUtility.generateMSK(tlsSecurityKeys.getMasterSecret(), tlsSecurityKeys.getClientRandom(), tlsSecurityKeys.getServerRandom(), getMSKLabel(), tlsSecurityParameters);
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Generated MSK Material : " + Utility.bytesToHex(msk));
						setKey(msk);						
						setCustomerAccountInfo(m.getCustomerAccountInfo());
						setDone(true);
						setSuccess(true);
						setFailure(false);
					}else if(m.isFailure()){
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Application Negotiation Failed");
						setDone(true);						
						setFailure(true);
						setSuccess(false);
					}
				}else {
					if(m.isSuccess()){
						/***
						 * if there is a TLS Negotiation then After finished message you have to send success.
						 */
						byte[] msk = TLSUtility.generateMSK(tlsSecurityKeys.getMasterSecret(), tlsSecurityKeys.getClientRandom(), tlsSecurityKeys.getServerRandom(), getMSKLabel(), tlsSecurityParameters);
						if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
							LogManager.getLogger().trace(getModuleName(), "Generated MSK Material : " + Utility.bytesToHex(msk));
						setKey(msk);						
						setCustomerAccountInfo(m.getCustomerAccountInfo());
						setSuccess(true);						
						setFailure(false);
						setDone(false);
					}else if(m.isFailure()){
						setFailure(true);
						setSuccess(false);
						setDone(false);
					}
//					setDone(true);
					byte[] responseBytes = m.getResponseTLSRecord();
					byte[] finalResponseBytes = null;

					finalResponseBytes = encryptTLSRecord(responseBytes, this.tlsSecurityParameters.getCipher());							

					TLSEAPType tlsResponseType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(getMethodCode());
					tlsResponseType.setFlagValue(TLSFlagConstants.L_FLAG.value);
					tlsResponseType.setTLSMessageLength(finalResponseBytes.length);
					tlsResponseType.setTLSData(finalResponseBytes);
					fragmentTlsEapType(tlsResponseType,aaaEapRespData.getFragmentedSize(),true);
					setReqEapType(tlsRespRecordQueue.poll());
				}
			}
		}		

	}	

	public boolean isCCSSend() {
		return bCCSSend;
	}
		
	public void setCCSSend(boolean send) {
		bCCSSend = send;
	}

	public TLSSessionState getSessionState() {
		return sessionState;
	}
	public TLSConnectionState getConnectionState(){
		TLSConnectionState connectionState = (TLSConnectionState)this.sessionState.getParameterValue(IEAPMethodSession.TLS_CONNECTION_STATE); 
		return connectionState;
	}

	public boolean isCCSReceived() {
		return bCCSReceived;
	}

	public void setCCSReceived(boolean received) {
		bCCSReceived = received;
	}

	public TLSRecordConstants getRecordtype() {
		return recordtype;
	}

	public void setRecordtype(TLSRecordConstants recordtype) {
		this.recordtype = recordtype;
	}

	public boolean isFragmented() {
		return bFragmented;
	}

	public void setFragmented(boolean fragmented) {
		bFragmented = fragmented;
	}

	public boolean isAcknoledgement() {
		return bAcknoledgement;
	}

	public void setAcknoledgement(boolean acknoledgement) {
		bAcknoledgement = acknoledgement;
	}

	public int getRetryCounter() {
		return retryCounter;
	}

	public void incrementRetryCounter() {
		this.retryCounter++;
	}
	
	public void resetRetryCounter(){
		this.retryCounter = 0;
	}
	
	public IMethodTypesStateMachine getMethodTypeStateMachine(int type,int method){
		if(m != null && m.getType() == type){
			return m;
		}
		TLSConnectionState connectionState = (TLSConnectionState)sessionState.getParameterValue(IEAPMethodSession.TLS_CONNECTION_STATE);
		if(type == TLSRecordConstants.Handshake.value){
			TlsHSMethodStateMachine handshakeStateMachine = new TlsHSMethodStateMachine(getEapConfigurationContext(),method);
			handshakeStateMachine.setTLSConnectionState(connectionState);
			handshakeStateMachine.setSendCertificateRequest(isSendCertificateRequest());
			return handshakeStateMachine;
		}else if(type == TLSRecordConstants.ApplicationData.value){
			TlsAppMethodStateMachine applicationStateMachine = new TlsAppMethodStateMachine(getEapConfigurationContext(),method);
			applicationStateMachine.setTLSConnectionState(connectionState);
			return applicationStateMachine;	
		}else if(type == TLSRecordConstants.Alert.value){
			TlsHSMethodStateMachine handshakeStateMachine = new TlsHSMethodStateMachine(getEapConfigurationContext(),method);
			handshakeStateMachine.setTLSConnectionState(connectionState);
			handshakeStateMachine.setSendCertificateRequest(isSendCertificateRequest());
			return handshakeStateMachine;
		}
		return null;
	}
	
	private void fragmentTlsEapType(TLSEAPType receivedType,int fragmentedSize, boolean overwrite){
		int totalLength;
		int lengthRead = 0;	
		final int MAX_EAP_PACKET_SIZE = fragmentedSize;
		if(overwrite)
		tlsRespRecordQueue = new LinkedBlockingQueue<TLSEAPType>();		
		if(MAX_EAP_PACKET_SIZE <= 0){
			tlsRespRecordQueue.add(receivedType);
			return;
		}

		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), "Fragment the TLS Packet with size : " + MAX_EAP_PACKET_SIZE);
		
		TLSEAPType tlsType = null;
		byte[] fragmentData = null;		
		
		byte[] dataToBeFragmented = receivedType.getTLSData();
		totalLength = receivedType.getTLSMessageLength();		
		
		while(lengthRead < totalLength){			
			tlsType = (TLSEAPType)EAPTypeDictionary.getInstance().createEAPType(getMethodCode());
			
			if(totalLength - lengthRead < MAX_EAP_PACKET_SIZE){
				fragmentData = new byte[totalLength - lengthRead];
				System.arraycopy(dataToBeFragmented, lengthRead, fragmentData, 0, totalLength - lengthRead);
			}else{
				fragmentData = new byte[MAX_EAP_PACKET_SIZE];
				System.arraycopy(dataToBeFragmented, lengthRead, fragmentData, 0, MAX_EAP_PACKET_SIZE);
			}			
						
			lengthRead += fragmentData.length;
			
			if(totalLength - lengthRead > 0){
				if(lengthRead == MAX_EAP_PACKET_SIZE){
					tlsType.setFlagValue(TLSFlagConstants.LM_FLAG.value);
					tlsType.setTLSMessageLength(receivedType.getTLSMessageLength());
				}else{
					tlsType.setFlagValue(TLSFlagConstants.M_FLAG.value);
				}
			}else{
				tlsType.setFlagValue(TLSFlagConstants.NULL_FLAG.value);
//				tlsType.setTLSMessageLength(totalLength);
			}
									
			tlsType.setTLSData(fragmentData);			
			tlsRespRecordQueue.add(tlsType);
		}				
		if(LogManager.getLogger().isLogLevel(LogLevel.TRACE))
			LogManager.getLogger().trace(getModuleName(), "Total Fragmented Packets : " + tlsRespRecordQueue.size());		
	}

	public ICustomerAccountInfo getCustomerAccountInfo() {
		return customerAccountInfo;
	}
		
	public void setCustomerAccountInfo(ICustomerAccountInfo customerAccountInfo) {
		this.customerAccountInfo = customerAccountInfo;
	}

	public String getUserIdentity(){
		TLSConnectionState connectionState = (TLSConnectionState)sessionState.getParameterValue(IEAPMethodSession.TLS_CONNECTION_STATE);
		return (String)connectionState.getParameterValue(IEAPMethodSession.TTLS_USER_IDENTITY);	
	}
	
	public void clearCustomerAccountInfo(){
		this.customerAccountInfo = null;
	}

	public String getFailureReason() {
		if(m != null){
			String reason = m.getFailureReason();
			if(reason != null && reason.length() > 0)
				return reason;
		}
		return failureReason;
	}

	public void setFailureReason(String failureReason) {
		this.failureReason = failureReason;
	}

	abstract public boolean isSendCertificateRequest(); 

	public ITLSRecord createCCSRecord() {
		
		//Creating the CCS TLS Record.
		TLSPlaintext tlsCCSRecord = new TLSPlaintext();
		tlsCCSRecord.setContentType(new ContentType(TLSRecordConstants.ChangeCipherSpec.value));
		tlsCCSRecord.setProtocolVersion(tlsSecurityParameters.getProtocolVersion());
		tlsCCSRecord.setContent(TLSRecordTypeDictionary.getInstance().createTLSRecord(TLSRecordConstants.ChangeCipherSpec.value, tlsSecurityParameters.getProtocolVersion()));
		tlsCCSRecord.refreshHeader();	
		return tlsCCSRecord;
	}

	public boolean isTTLSDraftRequest() {
		return bTTLSDraftRequest;
	}

	public void setTTLSDraftRequest(boolean bTTLSDraftRequest) {
		this.bTTLSDraftRequest = bTTLSDraftRequest;
	}
	
	@Override
	public boolean validateMAC(String macValue){
		boolean isValidMac = false;
		if(!isSendCertificateRequest())
			isValidMac = true;
		else{
			String macAddress=(String)getConnectionState().getParameterValue(TLSConnectionState.MACADDRESS);
			macValue=TLSUtility.convertToPlainString(macValue);
			macAddress = TLSUtility.convertToPlainString(macAddress);
			isValidMac = TLSUtility.matches(macValue, macAddress);
		}
		return isValidMac;
	}

	public boolean isRequestIdentity(){
		return bRequestIdentity;
	}
	
	public void setRequestIdentity(boolean bRequestIdentity){
		this.bRequestIdentity = bRequestIdentity;
	}
	public abstract boolean isRequestEAPIdentityInTunnelOnACK();
}
