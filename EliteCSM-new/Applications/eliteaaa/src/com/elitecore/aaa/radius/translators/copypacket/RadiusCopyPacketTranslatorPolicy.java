package com.elitecore.aaa.radius.translators.copypacket;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.elitecore.aaa.radius.service.RadServiceRequest;
import com.elitecore.aaa.radius.translators.RadServiceRequestValueProvider;
import com.elitecore.aaa.radius.translators.copypacket.parser.RadiusCopyPacketParser;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.attributes.IRadiusAttribute;
import com.elitecore.coreradius.commons.attributes.IRadiusGroupedAttribute;
import com.elitecore.coreradius.commons.packet.RadiusPacket;
import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.core.translator.policy.data.TranslatorParams;
import com.elitecore.diameterapi.diameter.translator.CopyPacketTranslatorPolicy;
import com.elitecore.diameterapi.diameter.translator.data.impl.CopyPacketMappingDataImpl;
import com.elitecore.diameterapi.diameter.translator.operations.AddOperation;
import com.elitecore.diameterapi.diameter.translator.operations.MoveOperation;
import com.elitecore.diameterapi.diameter.translator.operations.PacketOperation;
import com.elitecore.diameterapi.diameter.translator.operations.RemoveOperation;
import com.elitecore.diameterapi.diameter.translator.operations.UpdateOperation;
import com.elitecore.diameterapi.diameter.translator.operations.UpgradeOperation;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;

public class RadiusCopyPacketTranslatorPolicy extends
CopyPacketTranslatorPolicy<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute> {

	private static final String MODULE = "RAD-CPY-PKT-TRANS-POLICY";

	@Override
	public boolean isApplicable(TranslatorParams params, boolean isRequest) {
		RadServiceRequest radServiceRequest;
		if (isRequest) {
			radServiceRequest = (RadServiceRequest) params.getParam(TranslatorConstants.FROM_PACKET);
		} else {
			radServiceRequest = (RadServiceRequest) params.getParam(TranslatorConstants.SOURCE_REQUEST);
		}
		if (radServiceRequest == null) {
			return false;
		}
		return inExpression == null || inExpression.evaluate(new RadServiceRequestValueProvider(radServiceRequest));
	}

	@Override
	protected List<PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>> createRequestPacketOperations(
			List<CopyPacketMappingDataImpl> mappingDataList)
					throws InvalidExpressionException {
		if(Collectionz.isNullOrEmpty(mappingDataList)){
			return Collections.emptyList();
		}
		List<PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>> operations = new ArrayList<PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>>();

		for(CopyPacketMappingDataImpl mappingData : mappingDataList) {
			PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute> packetOperation = createRequestPacketOperation(mappingData);
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

	private PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute> createRequestPacketOperation(
			CopyPacketMappingDataImpl mappingData) throws InvalidExpressionException {

		if(mappingData.getOperation() == null){
			return null;
		}
		OperationData operationData = RadiusCopyPacketParser.getRequestInstance()
				.parse(mappingData.getCheckExpression(), 
						mappingData.getDestinationExpression(), 
						mappingData.getSourceExpression(), 
						mappingData.getDefaultValue(), 
						mappingData.getValueMapping());

		switch (mappingData.getOperation()) {
		case ADD:
			return new AddOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		case DELETE:
			return new RemoveOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		case UPDATE:
			return new UpdateOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		case UPGRADE:
			return new UpgradeOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		case MOVE:
			return new MoveOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		}
		return null;
	}

	@Override
	protected List<PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>> createResponsePacketOperations(
			List<CopyPacketMappingDataImpl> mappingDataList)
					throws InvalidExpressionException {
		if(Collectionz.isNullOrEmpty(mappingDataList)){
			return Collections.emptyList();
		}
		List<PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>> operations = new ArrayList<PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>>();

		for(CopyPacketMappingDataImpl mappingData : mappingDataList) {
			PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute> packetOperation = createResponsePacketOperation(mappingData);
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

	private PacketOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute> createResponsePacketOperation(
			CopyPacketMappingDataImpl mappingData) throws InvalidExpressionException {

		if(mappingData.getOperation() == null){
			return null;
		}
		OperationData operationData = RadiusCopyPacketParser.getResponseInstance()
				.parse(mappingData.getCheckExpression(), 
						mappingData.getDestinationExpression(), 
						mappingData.getSourceExpression(), 
						mappingData.getDefaultValue(), 
						mappingData.getValueMapping());

		switch (mappingData.getOperation()) {
		case ADD:
			return new AddOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		case DELETE:
			return new RemoveOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		case UPDATE:
			return new UpdateOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		case UPGRADE:
			return new UpgradeOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		case MOVE:
			return new MoveOperation<RadiusPacket, IRadiusAttribute, IRadiusGroupedAttribute>(getPolicyName(), operationData, RadiusPacketDelegator.getInstance());
		}
		return null;
	}
	
	@Override
	protected void postOperation(TranslatorParams translatorParams) {
		RadiusPacket radiusPacket = (RadiusPacket) translatorParams.getParam(TranslatorConstants.TO_PACKET);
		radiusPacket.refreshPacketHeader();
		radiusPacket.refreshInfoPacketHeader();
	}

}
