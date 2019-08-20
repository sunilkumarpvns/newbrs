package com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.gx;

import java.util.List;

import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.usagemetering.UsageMonitoringInfo;

/**
 * @author harsh patel
 *  
 * {@link UMBuilder} build {@link UsageMonitoringInfo} from UsageMeteringAVPs received In {@link DiameterPacket} and vice versa   
 */
public interface UMBuilder {
	public List<UsageMonitoringInfo> buildUsageMonitoringInfo(DiameterPacket diameterPacket);
	public List<IDiameterAVP> buildUsageMonitoringAVP(PCRFResponse pcrfResponse);
}
