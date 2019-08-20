package com.elitecore.diameterapi.diameter.translator.data;

import com.elitecore.diameterapi.diameter.translator.operations.PacketOperations;

public interface CopyPacketMappingData {

	public PacketOperations getOperation();
	
	public String getCheckExpression();
	
	public String getDestinationExpression();
	
	public String getSourceExpression();
	
	public String getDefaultValue();
	
	public String getValueMapping();
}
