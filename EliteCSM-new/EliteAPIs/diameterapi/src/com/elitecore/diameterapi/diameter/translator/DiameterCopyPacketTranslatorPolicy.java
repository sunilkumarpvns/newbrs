package com.elitecore.diameterapi.diameter.translator;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterAVPValueProvider;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketMappingDataImpl;
import com.elitecore.diameterapi.diameter.translator.delegator.DiameterPacketDelegator;
import com.elitecore.diameterapi.diameter.translator.operations.AddOperation;
import com.elitecore.diameterapi.diameter.translator.operations.MoveOperation;
import com.elitecore.diameterapi.diameter.translator.operations.PacketOperation;
import com.elitecore.diameterapi.diameter.translator.operations.RemoveOperation;
import com.elitecore.diameterapi.diameter.translator.operations.UpdateOperation;
import com.elitecore.diameterapi.diameter.translator.operations.UpgradeOperation;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.diameterapi.diameter.translator.parser.DiameterCopyPacketParser;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

public class DiameterCopyPacketTranslatorPolicy extends
		CopyPacketTranslatorPolicy<DiameterPacket, IDiameterAVP, AvpGrouped> {

	private static final String MODULE = "DIA-CPY-PKT-TRANS-POLICY";

	@Override
	public boolean isApplicable(TranslatorParams params, boolean isRequest)  {
		
		DiameterRequest diameterRequest = null;
		
		if (isRequest) {
			diameterRequest = (DiameterRequest) params.getParam(TranslatorConstants.FROM_PACKET);
		} else {
			diameterRequest = (DiameterRequest) params.getParam(TranslatorConstants.SOURCE_REQUEST);
		}
		if (diameterRequest == null) {
			return false;
		}
		return inExpression == null || inExpression.evaluate(new DiameterAVPValueProvider(diameterRequest));
	}

	@Override
	protected List<PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped>> createRequestPacketOperations(
			List<CopyPacketMappingDataImpl> mappingDataList) throws InvalidExpressionException {

		if(Collectionz.isNullOrEmpty(mappingDataList)){
			return Collections.emptyList();
		}
		List<PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped>> operations = new ArrayList<PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped>>();

		for(CopyPacketMappingDataImpl mappingData : mappingDataList) {
			PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped> packetOperation = createRequestPacketOperation(mappingData);
			if(packetOperation == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Unable to Initialize Operation : " + 
							mappingData);
				}
				continue;
			}
			operations.add(packetOperation);
		}
		return operations;
	}
	
	@Override
	protected List<PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped>> createResponsePacketOperations(
			List<CopyPacketMappingDataImpl> mappingDataList) throws InvalidExpressionException {

		if(Collectionz.isNullOrEmpty(mappingDataList)){
			return Collections.emptyList();
		}
		List<PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped>> operations = new ArrayList<PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped>>();

		for(CopyPacketMappingDataImpl mappingData : mappingDataList) {
			PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped> packetOperation = createResponsePacketOperation(mappingData);
			if(packetOperation == null) {
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Unable to Initialize Operation : " + 
							mappingData);
				}
				continue;
			}
			operations.add(packetOperation);
		}
		return operations;
	}

	private PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped> createResponsePacketOperation(
			CopyPacketMappingDataImpl mappingData) throws InvalidExpressionException {
		
		if(mappingData.getOperation() == null){
			return null;
		}
		
		OperationData operationData = DiameterCopyPacketParser.getResponseInstance()
				.parse(mappingData.getCheckExpression(), 
				mappingData.getDestinationExpression(), 
				mappingData.getSourceExpression(), 
				mappingData.getDefaultValue(), 
				mappingData.getValueMapping());

		switch (mappingData.getOperation()) {
		case ADD:
			return new AddOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		case DELETE:
			return new RemoveOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		case UPDATE:
			return new UpdateOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		case UPGRADE:
			return new UpgradeOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		case MOVE:
			return new MoveOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		}
		return null;
	}
	
	private PacketOperation<DiameterPacket, IDiameterAVP, AvpGrouped> createRequestPacketOperation(
			CopyPacketMappingDataImpl mappingData) throws InvalidExpressionException {
		
		if(mappingData.getOperation() == null){
			return null;
		}
		
		OperationData operationData = DiameterCopyPacketParser.getRequestInstance()
				.parse(mappingData.getCheckExpression(), 
				mappingData.getDestinationExpression(), 
				mappingData.getSourceExpression(), 
				mappingData.getDefaultValue(), 
				mappingData.getValueMapping());

		switch (mappingData.getOperation()) {
		case ADD:
			return new AddOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		case DELETE:
			return new RemoveOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		case UPDATE:
			return new UpdateOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		case UPGRADE:
			return new UpgradeOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		case MOVE:
			return new MoveOperation<DiameterPacket, IDiameterAVP, AvpGrouped>(getPolicyName(), operationData, DiameterPacketDelegator.getInstance());
		}
		return null;
	}

	@Override
	protected void postOperation(TranslatorParams params) {
		
		DiameterPacket diameterTranslatedPacket = (DiameterPacket) params.getParam(TranslatorConstants.TO_PACKET);
		diameterTranslatedPacket.refreshPacketHeader();
		diameterTranslatedPacket.refreshInfoPacketHeader();
		
	}



}
