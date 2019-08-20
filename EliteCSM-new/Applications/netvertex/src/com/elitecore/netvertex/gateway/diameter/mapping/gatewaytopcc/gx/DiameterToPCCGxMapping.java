package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.corenetvertex.constants.SessionTypeConstant;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.CCRMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.EventMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;

/**
 * 
 * @author Jay Trivedi
 *
 */
public class DiameterToPCCGxMapping implements DiameterToPCCPacketMapping {

	private UMBuilder umBuilder;
	private EventMapping eventMapping;
	private RequestedQoSMapping requestedQoSMapping;
	
	public DiameterToPCCGxMapping(UMBuilder umBuilder) {

		this.umBuilder = umBuilder;
		this.eventMapping = new CCRMapping.GxEventMapping();
		requestedQoSMapping = new RequestedQoSMapping();
	}

	public DiameterToPCCGxMapping() {

		this(new TgppR9Builder());
	}
	
	@Override
	public void apply(PCRFRequestMappingValueProvider valueProvider) {
		
        PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
        DiameterRequest diameterRequest = valueProvider.getDiameterRequest();
        
		pcrfRequest .setAttribute(PCRFKeyConstants.CS_SESSION_TYPE.val, SessionTypeConstant.GX.val);
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_CORESESSION_ID.getVal(), diameterRequest.getAVPValue(DiameterAVPConstants.SESSION_ID)
                       + CommonConstants.COLON + SessionTypeConstant.GX.val);
        
		requestedQoSMapping.apply(valueProvider);
		pcrfRequest.setReportedUsageInfoList(umBuilder.buildUsageMonitoringInfo(valueProvider.getDiameterRequest()));
		eventMapping.apply(diameterRequest, pcrfRequest);
		applyConfSpecificMappings(valueProvider.getConfiguration(), pcrfRequest);
	}

	private void applyConfSpecificMappings(DiameterGatewayConfiguration configuration, PCRFRequest pcrfRequest) {
		pcrfRequest.setAttribute(PCRFKeyConstants.REVALIDATION_MODE.val, configuration.getRevalidationMode().val);
		pcrfRequest.setAttribute(PCRFKeyConstants.CS_GATEWAY_NAME.val, configuration.getName());
		pcrfRequest.setAttribute(PCRFKeyConstants.USAGE_REPORTING_TYPE.getVal(), configuration.getUsageReportingType().val);
	}

}
