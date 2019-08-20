package com.elitecore.diameterapi.diameter.translator;

import java.util.List;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.common.InitializationFailedException;
import com.elitecore.diameterapi.core.common.TranslationFailedException;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketMappingDataImpl;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketTranslationConfigDataImpl;
import com.elitecore.diameterapi.diameter.translator.operations.PacketOperation;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.LogicalExpression;

public abstract class CopyPacketTranslatorPolicy<P, A, G extends A> {

	private static final String MODULE = "CPY-PKT-TRANS-POLICY";
	protected LogicalExpression inExpression;
	private List<PacketOperation<P, A, G>> requestPacketOperations;
	private List<PacketOperation<P, A, G>> responsePacketOperations;
	private boolean isDummyEnabled;
	private String policyName;
	
	public void init(CopyPacketTranslationConfigDataImpl policyData) throws InitializationFailedException {
		policyName = policyData.getMappingName();
		isDummyEnabled = policyData.isDummyMappingEnabled();
		if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
			LogManager.getLogger().debug(MODULE, "Compiling Mapping IN Expression: " + 
					policyData.getInExpression());
		}
		try {
			if(Strings.isNullOrBlank(policyData.getInExpression()) == false){
				inExpression = Compiler.getDefaultCompiler().parseLogicalExpression(policyData.getInExpression().trim());
			}
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Initializing Request Mapping Parameters for Mapping: " + 
						policyData.getMappingName());
			}
			requestPacketOperations = createRequestPacketOperations(policyData.getRequestMappingDataList());
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)){
				LogManager.getLogger().debug(MODULE, "Initializing Response Mapping Parameters for Mapping: " + 
						policyData.getMappingName());
			}
			responsePacketOperations = createResponsePacketOperations(policyData.getResponseMappingDataList());

		} catch (InvalidExpressionException e) {
			throw new InitializationFailedException(e);
		}
	}
	
	public abstract boolean isApplicable(TranslatorParams params, boolean isRequest);
	
	public void applyRequestOperations(TranslatorParams params) throws TranslationFailedException{
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Request before Translation: " + 
					params.getParam(TranslatorConstants.TO_PACKET));
		}
		params.setParam(TranslatorConstants.DUMMY_MAPPING, isDummyEnabled);
		
		for(PacketOperation<P, A, G> operation : requestPacketOperations) {
			operation.execute(params);
		}
		postOperation(params);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Translated Request: " +
					params.getParam(TranslatorConstants.TO_PACKET));
		}
		
	}
	
	protected abstract void postOperation(TranslatorParams params);

	public void applyResponseOperations(TranslatorParams params) throws TranslationFailedException{
		
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Response before Translation: " + 
					params.getParam(TranslatorConstants.TO_PACKET));
		}
		for(PacketOperation<P, A, G> operation : responsePacketOperations) {
			operation.execute(params);
		}
		postOperation(params);
		if(LogManager.getLogger().isLogLevel(LogLevel.INFO)) {
			LogManager.getLogger().info(MODULE, "Translated Response: " + 
					params.getParam(TranslatorConstants.TO_PACKET));
		}
	}
	
	public boolean isDummyEnabled() {
		return isDummyEnabled;
	}
	
	public String getPolicyName() {
		return policyName;
	}

	protected abstract List<PacketOperation<P, A, G>> createRequestPacketOperations(
			List<CopyPacketMappingDataImpl> mappingDataList)
			throws InvalidExpressionException;

	protected abstract List<PacketOperation<P, A, G>> createResponsePacketOperations(
			List<CopyPacketMappingDataImpl> mappingDataList)
			throws InvalidExpressionException;

}