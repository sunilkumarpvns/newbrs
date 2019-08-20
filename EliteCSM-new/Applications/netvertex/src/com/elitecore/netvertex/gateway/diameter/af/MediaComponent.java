package com.elitecore.netvertex.gateway.diameter.af;

import com.elitecore.commons.io.IndentingPrintWriter;
import com.elitecore.commons.io.IndentingWriter;
import com.elitecore.corenetvertex.constants.*;
import com.elitecore.corenetvertex.core.constant.ChargingModes;
import com.elitecore.corenetvertex.pm.constants.FlowStatus;
import com.elitecore.corenetvertex.pm.constants.UsageMetering;
import com.elitecore.corenetvertex.pm.pkg.PCCRule;
import com.elitecore.corenetvertex.pm.pkg.PCCRuleImpl.PCCRuleBuilder;
import com.elitecore.corenetvertex.pm.pkg.imspackage.MediaType;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.netvertex.core.session.SessionManagerOperations;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import javax.annotation.Nonnull;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class MediaComponent {
	
	private static final String MODULE = "MEDIA-COMPONT";
	private QCI possibleQCI;
	private String afSessionId;
	private long mediaComponentNumber;
	private String afIdentifier;
	private String uplinkCodec;
	private String downloadlinkCodec;
	private long mediaIdentifier;
	private String mediaType;
	private FlowStatus flowStatus;
	private String uplinkFlow;
	private String downlinkFlow;
	private AFSignallingProtocol afSignallingProtocol;
	private long flowNumber;
	private FlowUsage flowUsage;
	private String resultCode;
	
	
	private long maxRequestedBitRateDownload;
	private long maxRequestedBitRateUpload;
	private long reservationPriority;
	private long rsBandwidth;
	private long rrBandwidth;
	
	private AFSessionRule previousInstalledRule;
	private PCCRule pccRule;
	private QoSCalculator qoSCalculator;

	private MediaComponent() {
		
	}



	public static List<MediaComponent> newMediaComponent(AvpGrouped mediaComponentDescription, DiameterRequest rxPacket, boolean ePSQoSRoundingEnable) throws MissingAPVException{
		
		ArrayList<MediaComponent> mediaComponents = new ArrayList<MediaComponent>();
		
		
		long mediaComponentNumber = 0;
		long reservationPriority = 0;
		String afIdentifier = null;
		String uplinkCodec = null;
		String downlinkCodec = null;
		String mediaType = null;
		long mediaIdentifier = 0;
		
		long maxRequestedBitRateUpload = 0;
		long maxRequestedBitRateDownload = 0;
		
		long rsBandwidth = 0;
		long rrBandwidth = 0;
		FlowStatus flowStatus = null;
		QCI possibleQCI = QCI.QCI_NON_GBR_6;
		
		
		String afSessionId = rxPacket.getAVPValue(DiameterAVPConstants.SESSION_ID);
		
		
		IDiameterAVP mediaComponentNumberAVP = mediaComponentDescription.getSubAttribute(DiameterAVPConstants.TGPP_MEDIA_COMPONENT_NUMBER);
		if(mediaComponentNumberAVP != null) {
			mediaComponentNumber = mediaComponentNumberAVP.getInteger();
		} else {
			throw new MissingAPVException(DiameterAVPConstants.TGPP_MEDIA_COMPONENT_NUMBER);
		}
		
		
		IDiameterAVP reservationPriorityAVP = mediaComponentDescription.getSubAttribute(DiameterAVPConstants.TGPP_RESERVATION_PRIORITY);
		if(reservationPriorityAVP != null) {
			reservationPriority = reservationPriorityAVP.getInteger();
		}
		
		IDiameterAVP afIdentifierAVP = mediaComponentDescription.getSubAttribute(DiameterAVPConstants.TGPP_AF_IDENTIFIER);
		if(afIdentifierAVP != null) {
			afIdentifier = afIdentifierAVP.getStringValue();
		} else {
			afIdentifier = rxPacket.getAVPValue(DiameterAVPConstants.TGPP_AF_IDENTIFIER);
		}
		
		List<IDiameterAVP> codecDataAVPs = mediaComponentDescription.getSubAttributeList(DiameterAVPConstants.TGPP_CODEC_DATA);
		if(codecDataAVPs != null) {
			for(int i=0; i<codecDataAVPs.size(); i++) {				
				String codecData = codecDataAVPs.get(i).getStringValue();
				if(codecData.startsWith("uplink")) {
					uplinkCodec = codecData;
				} else if(codecData.startsWith("downlink")) {
					downlinkCodec = codecData;
				}
			}
		}
		
		IDiameterAVP mediaTypeAVP = mediaComponentDescription.getSubAttribute(DiameterAVPConstants.TGPP_MEDIA_TYPE);
		if(mediaTypeAVP != null) {
			switch ((int)mediaTypeAVP.getInteger()) {
			case 0:
				mediaType = PCRFKeyValueConstants.MEDIA_TYPE_AUDIO.val;
				possibleQCI = QCI.QCI_GBR_2;
				break;

			case 1:
				mediaType = PCRFKeyValueConstants.MEDIA_TYPE_VIDEO.val;
				possibleQCI = QCI.QCI_GBR_2;
				break;
				
			case 2:
				mediaType = PCRFKeyValueConstants.MEDIA_TYPE_DATA.val;
				possibleQCI = QCI.QCI_NON_GBR_8;
				break;
				
			case 3:
				mediaType = PCRFKeyValueConstants.MEDIA_TYPE_APPLICATION.val;
				possibleQCI = QCI.QCI_GBR_2;
				break;
				
			case 4:
				mediaType = PCRFKeyValueConstants.MEDIA_TYPE_CONTROL.val;
				possibleQCI = QCI.QCI_NON_GBR_6;
				break;
				
			case 5:
				mediaType = PCRFKeyValueConstants.MEDIA_TYPE_TEXT.val;
				possibleQCI = QCI.QCI_NON_GBR_9;
				break;
				
			case 6:
				mediaType = PCRFKeyValueConstants.MEDIA_TYPE_MESSAGE.val;
				possibleQCI = QCI.QCI_NON_GBR_9;
				break;
				
			case 0xFFFFFFFF:
			default:
				mediaType = PCRFKeyValueConstants.MEDIA_TYPE_OTHER.val;
				possibleQCI = QCI.QCI_NON_GBR_9;
				break;			
			}
			
			mediaIdentifier = mediaTypeAVP.getInteger();
			
		}
		
		IDiameterAVP maxRequestedBWUL = mediaComponentDescription.getSubAttribute(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_UL);
		if(maxRequestedBWUL != null) {
			maxRequestedBitRateUpload = maxRequestedBWUL.getInteger();
		}
		
		IDiameterAVP maxRequestedBWDL = mediaComponentDescription.getSubAttribute(DiameterAVPConstants.TGPP_MAX_REQUESTED_BW_DL);
		if(maxRequestedBWDL != null) {
			maxRequestedBitRateDownload = maxRequestedBWDL.getInteger();
		}
		
		
		IDiameterAVP rsBandWidthAVP = mediaComponentDescription.getSubAttribute(DiameterAVPConstants.TGPP_RS_BW);
		if(rsBandWidthAVP != null) {
			rsBandwidth = rsBandWidthAVP.getInteger();
		}
		
		IDiameterAVP rrBandWidthAVP = mediaComponentDescription.getSubAttribute(DiameterAVPConstants.TGPP_RR_BW);
		if(rrBandWidthAVP != null) {
			rrBandwidth = rrBandWidthAVP.getInteger();
		}
		
		IDiameterAVP flowStatusAVP = mediaComponentDescription.getSubAttribute(DiameterAVPConstants.TGPP_FLOW_STATUS);
		if(flowStatusAVP != null) {
			flowStatus = FlowStatus.fromValue(flowStatusAVP.getInteger());
		}
		
		List<IDiameterAVP> mediaSubComponentAVPs =  mediaComponentDescription.getSubAttributeList(DiameterAVPConstants.TGPP_MEDIA_SUB_COMPONENT);
		

		if(mediaSubComponentAVPs != null) {
			for(int i=0; i < mediaSubComponentAVPs.size(); i++) {
				MediaComponent mediaComponent = new MediaComponent();
				mediaComponent.mediaComponentNumber = mediaComponentNumber;
				mediaComponent.reservationPriority = reservationPriority;
				mediaComponent.afIdentifier = afIdentifier;
				mediaComponent.uplinkCodec = uplinkCodec;
				mediaComponent.downloadlinkCodec = downlinkCodec;
				mediaComponent.mediaType = mediaType;
				mediaComponent.maxRequestedBitRateDownload = maxRequestedBitRateDownload;
				mediaComponent.maxRequestedBitRateUpload = maxRequestedBitRateUpload;
				mediaComponent.rsBandwidth = rsBandwidth;
				mediaComponent.rrBandwidth = rrBandwidth;
				mediaComponent.flowStatus = flowStatus;
				mediaComponent.mediaIdentifier = mediaIdentifier;
				mediaComponent.afSessionId = afSessionId;
				mediaComponent.possibleQCI = possibleQCI;
				mediaComponent.qoSCalculator = ePSQoSRoundingEnable ? YTLEpsQoSRoundingCalculator.instance() : TGPP29213QoSCalculator.instance();
				newMediaSubComponent((AvpGrouped)mediaSubComponentAVPs.get(i), mediaComponent);
				mediaComponents.add(mediaComponent);
			}
		}
		
		return mediaComponents;
	}
	
	public static MediaComponent newMediaSubComponent(AvpGrouped mediaSubComponentAVP,@Nonnull MediaComponent mediaComponent) {
		

		IDiameterAVP flowNumberAVP = mediaSubComponentAVP.getSubAttribute(DiameterAVPConstants.TGPP_FLOW_NUMBER);
		if(flowNumberAVP != null) {
			mediaComponent.flowNumber = flowNumberAVP.getInteger();
		}
		
		IDiameterAVP flowStatusAVP = mediaSubComponentAVP.getSubAttribute(DiameterAVPConstants.TGPP_FLOW_STATUS);
		if(flowStatusAVP != null) {
			mediaComponent.flowStatus = FlowStatus.fromValue(flowStatusAVP.getInteger());
		}
		
		List<IDiameterAVP> flowDescriptionAVPs = mediaSubComponentAVP.getSubAttributeList(DiameterAVPConstants.TGPP_FLOW_DESCRIPTION);
		if(flowDescriptionAVPs != null) {
			for(int i = 0; i < flowDescriptionAVPs.size(); i++) {
				IDiameterAVP flowDescriptionAVP = flowDescriptionAVPs.get(i);
				
				if(flowDescriptionAVP != null) {
					if(flowDescriptionAVP.getStringValue().startsWith("permit in") || flowDescriptionAVP.getStringValue().startsWith("deny in")) {
						mediaComponent.uplinkFlow = flowDescriptionAVP.getStringValue();
					} else if(flowDescriptionAVP.getStringValue().startsWith("permit out") || flowDescriptionAVP.getStringValue().startsWith("deny out")){
						mediaComponent.downlinkFlow = flowDescriptionAVP.getStringValue();
					}
				}
			}
		}
		
		IDiameterAVP flowUsaegAVP = mediaSubComponentAVP.getSubAttribute(DiameterAVPConstants.TGPP_FLOW_USAGE);
		if(flowUsaegAVP != null) {
			mediaComponent.flowUsage = FlowUsage.fromLongVal(flowUsaegAVP.getInteger()); 
		}
		
		IDiameterAVP afSignallingProtocolAVP = mediaSubComponentAVP.getSubAttribute(DiameterAVPConstants.TGPP_AF_SIGNALLING_PROTOCOL);
		if(afSignallingProtocolAVP != null) {
			mediaComponent.afSignallingProtocol = AFSignallingProtocol.fromLongVal(afSignallingProtocolAVP.getInteger()); 
		}
		
		return mediaComponent;
	}
	
	public PCCRule getPCCRule() {
		return pccRule;
	}

	public void setPCCRule(PCCRule pccRule) {
		this.pccRule = pccRule;
	}

	public long getMediaComponentNumber() {
		return mediaComponentNumber;
	}

	public String getAfIdentifier() {
		return afIdentifier;
	}

	public String getUplinkCodec() {
		return uplinkCodec;
	}

	public String getDownloadlinkCodekc() {
		return downloadlinkCodec;
	}

	public String getMediaType() {
		return mediaType;
	}

	public FlowStatus getFlowStatus() {
		return flowStatus;
	}

	public long getMaxRequestedBitRateDownload() {
		return maxRequestedBitRateDownload;
	}

	public long getMaxRequestedBitRateUpload() {
		return maxRequestedBitRateUpload;
	}

	public long getReservationPriority() {
		return reservationPriority;
	}

	public long getRsBandwidth() {
		return rsBandwidth;
	}

	public long getRrBandwidth() {
		return rrBandwidth;
	}
	
	public long getFlowNumber() {
		return flowNumber;
	}
 
	public AFSessionRule getPreviousInstalledRule() {
		return previousInstalledRule;
	}

	public void setPreviousInstalledRule(AFSessionRule previousInstalledRule) {
		this.previousInstalledRule = previousInstalledRule;
	}

	public boolean belongsToYou(AFSessionRule afSessionRule) {
		return mediaComponentNumber == afSessionRule.getMediaComponentNumber() && afSessionRule.getFlowNumber() == flowNumber;
	}

	public void addPreviousInstalledRule(AFSessionRule afActivePCCRule) {
		this.previousInstalledRule = afActivePCCRule;
	}
	
	public long getMediaIdentifier() {
		return mediaIdentifier;
	}

	public void add(Map<SessionManagerOperations, List<AFSessionRule>> operationToAfSessionRules) {
		
		// is flow-status is remove then we nee to remove all PCCRules
		if(FlowStatus.REMOVED == this.flowStatus) {
			if(previousInstalledRule == null) {
				return;
			}
			List<AFSessionRule> deleteList = operationToAfSessionRules.get(SessionManagerOperations.DELETE);
			
			if(deleteList == null) {
				deleteList = new ArrayList<AFSessionRule>();
				operationToAfSessionRules.put(SessionManagerOperations.DELETE, deleteList);
			}
			
			deleteList.add(previousInstalledRule);
			return;
		}
		
		
		
		if(pccRule != null) {
			//if no previous installable rule is applied then we need to save all rules
			if(previousInstalledRule == null) {
				
				List<AFSessionRule> newPCCRules = operationToAfSessionRules.get(SessionManagerOperations.SAVE);
				
				if(newPCCRules == null) {
					newPCCRules = new ArrayList<AFSessionRule>();
					operationToAfSessionRules.put(SessionManagerOperations.SAVE, newPCCRules);
				}
					
				
				newPCCRules.add(new AFSessionRule("0",flowNumber, mediaIdentifier, pccRule.getName(),uplinkFlow,downlinkFlow, mediaComponentNumber,afSessionId,pccRule.getMBRDL(),pccRule.getMBRUL(),pccRule.getGBRDL(),pccRule.getGBRUL(),pccRule.getQCI(),pccRule.getFlowStatus()));
				
				return;
			}
			
			
			// creating save and update list
			
			/*
			 * isFlowDescriptionsIsChanged check only flow description of any previously installable rule, because flow-Description is same in all rule. we do not need to check all pccrule flow description
			 * 
			 * if flow-description for any rule is same it will be same for all rules.
			 * 
			 * if isFlowDescriptionsIsChanged is false then we do not need to update previous installable rule. only need to take care for new rule.
			 * if isFlowDescriptionsIsChanged is true then we need to change flow-description of previous installed pccRule and install new pcc rule with changed flow-descriptions
			 */
			if(isFlowDescriptionsIsChanged(previousInstalledRule)) {
				
				List<AFSessionRule> updateList = operationToAfSessionRules.get(SessionManagerOperations.UPDATE);
				

				if(updateList == null) {
					updateList = new ArrayList<AFSessionRule>();
					operationToAfSessionRules.put(SessionManagerOperations.UPDATE, updateList);
				}
				updateList.add(new AFSessionRule("0", flowNumber, mediaIdentifier, pccRule.getName(), uplinkFlow,downlinkFlow,mediaComponentNumber,afSessionId,pccRule.getMBRDL(),pccRule.getMBRUL(),pccRule.getGBRDL(),pccRule.getGBRUL(),pccRule.getQCI(),pccRule.getFlowStatus()));
			
					
				
				
			}
			
		} else {
			if(previousInstalledRule != null) {
				// creating removable af rule list
				List<AFSessionRule> deleteList = operationToAfSessionRules.get(SessionManagerOperations.DELETE);
				if(deleteList == null) {
					deleteList = new ArrayList<AFSessionRule>();
					operationToAfSessionRules.put(SessionManagerOperations.DELETE, deleteList);
				}
				
				deleteList.add(previousInstalledRule);
			}
		}
		
		
		
	}

	private boolean isFlowDescriptionsIsChanged(AFSessionRule afSessionRule) {
		if(downlinkFlow != null) {
			if(downlinkFlow.equals(afSessionRule.getDownlinkFlow()) == false){				
				return true;
			}
		} else {
			if(afSessionRule.getDownlinkFlow() != null){				
				return true;
			}
		}
		
		if(uplinkFlow != null) {
			if(uplinkFlow.equals(afSessionRule.getUplinkFlow()) == false){				
				return true;
			}
		} else {
			if(afSessionRule.getUplinkFlow() != null){				
				return true;
			}
		}
		
		return false;
		
		
	}

	public void addInstallablePCCRules(List<PCCRule> installablePCCRules) {
		
		if(flowStatus == FlowStatus.REMOVED) {
			return;
		}
		
		if(this.pccRule == null) {
			return;
		}
		installablePCCRules.add(pccRule);
	}

	

	public void addRemovablePCCRule(List<String> removablePCCRules) {
		if(previousInstalledRule == null) {
			if(getLogger().isDebugLogLevel())
				getLogger().debug(MODULE, "No removable PCC for media type:" + mediaType + ". Reason: No previous pcc rule found");
			return;
		}
		
		if(flowStatus == FlowStatus.REMOVED) {
			removablePCCRules.add(previousInstalledRule.getPccRule());
		}
	}

	@Override
	public String toString() {
		
		StringWriter stringBuffer=new StringWriter();
		IndentingWriter out=new IndentingPrintWriter(stringBuffer);
		toString(out);
		out.close();
		return stringBuffer.toString();
		
	}

	public void toString(IndentingWriter out) {


		out.println("MediaComponent");
		out.incrementIndentation();
		out.println("Media Component Number =" + mediaComponentNumber);
		out.println("Flow Number = " + flowNumber);
		out.println("Media Identifier = " + mediaIdentifier);
		out.println("Media Type = " + mediaType);
		out.println("AF App Identifier = " + afIdentifier);
		out.println("Flow-Status = " + flowStatus);
		out.println("Flow Usage = " + flowUsage);
		out.println("Flow Description Uplink = " + uplinkFlow);
		out.println("Flow Description Downlink = " + downlinkFlow);
		out.println("AF Signalling Protocol = " + afSignallingProtocol);
		out.println("Requested MBRDL = " + maxRequestedBitRateDownload);
		out.println("Requested MBRUL = " + maxRequestedBitRateUpload);
		out.println("Reservation Priority = " + reservationPriority);
		out.println("RS Bandwidth = " + rsBandwidth);
		out.println("RR Bandwidth = " + rrBandwidth);
		
		out.println("Previous Installed Rule");
		out.incrementIndentation();
		if(previousInstalledRule != null) {
			previousInstalledRule.toString(out);
		} else {
			out.println("No previous installed Rules");
		}
		out.decrementIndentation();
		 
		
		out.println("PCC Rules");
		out.incrementIndentation();
		if(pccRule != null) {			
			out.println(pccRule.getName());
		} else {
			out.println("No PCC Rule Installed");
		}
		out.decrementIndentation();
		out.decrementIndentation();
		
	
	}

	/*
	 * create pcc rule with some value like MBRUL,MBRDL,GBRUL,GBRUL,QCI.
	 * 
	 * The value of parameters is derive from algorithm specified in 3gpp TS 29.213#6.3
	 */
	public PCCRule newPCC(PCRFResponse response, MediaType mediaType) {
		
		String name; 
		if(previousInstalledRule != null) {
			name = previousInstalledRule.getPccRule();
		} else {			
			name = PCCRuleNameGenerator.generate(response, this);
		}
		
		List<String> flowDescriptions = new ArrayList<String>(2);
		
		if(downlinkFlow != null) {
			flowDescriptions.add(downlinkFlow);
		}
		
		if(uplinkFlow != null) {
			flowDescriptions.add(uplinkFlow);
		}
		PCCRuleBuilder pccBuilder = new PCCRuleBuilder(UUID.randomUUID().toString(), name)
				.withServiceName(mediaType.getName())
				.withPrecedence(1)
				.withServiceIdentifier(mediaType.getIdentifier())
				.withChargingMode(ChargingModes.NONE)
				.withChargingKey(0)
				.withPriorityLevel(PriorityLevel.PRIORITY_LEVEL_1)
				.withDynamic(true)
				.withPeCapability(true)
				.withPeVulnerability(false)
				.withServiceDataFlows(flowDescriptions)
				.withStatus(PolicyStatus.SUCCESS)
				.withUsageMetering(UsageMetering.DISABLE_QUOTA);
		
		qoSCalculator.calculate(this, response, pccBuilder);
		
		return pccBuilder.build();
	}

	public String getResultCode() {
		return resultCode;
	}

	public void setResultCode(String resultCode) {
		this.resultCode = resultCode;
	}

	public QCI getQCI() {
		return possibleQCI;
	}

	public String getUplinkFlow() {
		return uplinkFlow;
	}
	
	public String getDownlinkFlow() {
		return downlinkFlow;
	}

	public AFSignallingProtocol getAFSinallingProtocol() {
		return afSignallingProtocol;
	}

	public FlowUsage getFlowUsage() {
		return flowUsage;
	}
	
	private interface QoSCalculator {
		public void calculate(MediaComponent mediacomponant, PCRFResponse response, PCCRuleBuilder pccRuleBuilder);
	}
	
	
	/**
	 * <PRE>
	 * YTL specific QoS calculator.
	 * 
	 * GBR and MBR QoS calculation algorithm:
	 * 	Max-Requested-Bandwidth = RTP
	 * 	RS-Bandwidth + RR-Bandwidth = RTCP
	 * 
	 *  RTP + RTCP = Z value (Z value must round up as per TS 24.301#9.9.4.3)
	 *  
	 *  To get GBR and MRB bandwidth for RTP :-
	 *  Z / 105 * 100 = RTP BGR & MRB
	 *  
	 *  
	 *  To get GBR and MRB bandwidth for RTCP :-
	 *  Z – RTP = RTCP BGR & MRB
	 * 
	 * @author Chetan.Sankhala
	 */
	public static class YTLEpsQoSRoundingCalculator implements QoSCalculator {
		
		private static final QoSCalculator YTL_EPS_QOS_ROUNDING_CALCULTOR = new YTLEpsQoSRoundingCalculator();
		
		private YTLEpsQoSRoundingCalculator() { }

		@Override
		public void calculate(MediaComponent mediacomponant, PCRFResponse response, PCCRuleBuilder pccRuleBuilder) {

			long zDownloadBits = mediacomponant.maxRequestedBitRateDownload + mediacomponant.rrBandwidth + mediacomponant.rsBandwidth;
			long zUploadBits = mediacomponant.maxRequestedBitRateUpload + mediacomponant.rrBandwidth + mediacomponant.rsBandwidth;

			long zDownloadInKb = (long) Math.ceil(zDownloadBits/1000D);
			long zUploadInKb = (long) Math.ceil(zUploadBits/1000D);
			
			zDownloadInKb = EPSQoSRoundingTable.roundOff(zDownloadInKb);
			zUploadInKb = EPSQoSRoundingTable.roundOff(zUploadInKb);

			if (mediacomponant.flowUsage == FlowUsage.RTCP) {
				// RTCP
				long rtpDownloadKb = calculateRTPQoS(zDownloadInKb);
				long rtpUploadKb = calculateRTPQoS(zUploadInKb);

				pccRuleBuilder.withGbrdl((zDownloadInKb - rtpDownloadKb) * 1000);
				pccRuleBuilder.withMbrdl((zDownloadInKb - rtpDownloadKb) * 1000);
				pccRuleBuilder.withGbrul((zUploadInKb - rtpUploadKb) * 1000);
				pccRuleBuilder.withMbrul((zUploadInKb - rtpUploadKb) * 1000);

				pccRuleBuilder.withQci(QCI.QCI_GBR_1);
				pccRuleBuilder.withFlowStatus(FlowStatus.ENABLED);
			} else {

				// RTP
				boolean rtcpFlowExist = isRTCPFlowExist(response, mediacomponant.getMediaComponentNumber());

				if (rtcpFlowExist == false) {
					// if RTCP flow not exist, use rounded QoS for RTP
					pccRuleBuilder.withGbrdl(zDownloadInKb * 1000);
					pccRuleBuilder.withMbrdl(zDownloadInKb * 1000);
					pccRuleBuilder.withGbrul(zUploadInKb * 1000);
					pccRuleBuilder.withMbrul(zUploadInKb * 1000);
				} else {
					/* if RTCP flow exist, apply formula to divide rounded QoS
					 between RTP and RTCP flow for same media component */
					pccRuleBuilder.withGbrdl(calculateRTPQoS(zDownloadInKb) * 1000);
					pccRuleBuilder.withMbrdl(calculateRTPQoS(zDownloadInKb) * 1000);
					pccRuleBuilder.withGbrul(calculateRTPQoS(zUploadInKb) * 1000);
					pccRuleBuilder.withMbrul(calculateRTPQoS(zUploadInKb) * 1000);

				}

				pccRuleBuilder.withQci(mediacomponant.possibleQCI);
				pccRuleBuilder.withFlowStatus(mediacomponant.flowStatus);
			}
		}

		/* QoS in bits per second */
		private long calculateRTPQoS(long qosInKbps) {
			return (long) Math.ceil(qosInKbps / 105D * 100D);
		}
		
		/*
		 * Checks RTCP flow usage exist for same media component
		 */
		private boolean isRTCPFlowExist(PCRFResponse response, long componentNumber) {
			List<MediaComponent> mediaComponents = response.getMediaComponents();
			
			for (int index = 0, size = mediaComponents.size(); index < size; index++) {
				MediaComponent component = mediaComponents.get(index);
				if (componentNumber == component.getMediaComponentNumber() && FlowUsage.RTCP == component.getFlowUsage()) {
					return true;
				}
			}
			
			return false;
		}

		public static QoSCalculator instance() {
			return YTL_EPS_QOS_ROUNDING_CALCULTOR;
		}
	}
	
	private static class TGPP29213QoSCalculator implements QoSCalculator {

		
		private static final QoSCalculator TGPP_29213_QOS_CALCULTOR = new TGPP29213QoSCalculator();
		
		private TGPP29213QoSCalculator() {} 

		public static QoSCalculator instance() {
			return TGPP_29213_QOS_CALCULTOR;
		}
		
		@Override
		public void calculate(MediaComponent mediacomponant, PCRFResponse response, PCCRuleBuilder pccBuilder) {
			
			if(mediacomponant.flowUsage == FlowUsage.RTCP){
				
				/**
				 *IF RS-Bandwidth is present and RR-Bandwidth is present THEN
				 *Max_DR_UL:= (RS-Bandwidth + RR-Bandwidth);
				 *Max_DR_DL:= (RS-Bandwidth + RR-Bandwidth);
	            */
				if(mediacomponant.rrBandwidth != 0 && mediacomponant.rsBandwidth != 0) {				
					pccBuilder.withMbrdl(mediacomponant.rsBandwidth + mediacomponant.rrBandwidth)
							.withMbrul(mediacomponant.rsBandwidth + mediacomponant.rrBandwidth)
							.withGbrdl(mediacomponant.rsBandwidth + mediacomponant.rrBandwidth)
							.withGbrul(mediacomponant.rsBandwidth + mediacomponant.rrBandwidth);
							
					
				/**
					IF Max-Requested-Bandwidth-UL is present THEN
	                	IF RS-Bandwidth is present and RR-Bandwidth is not present THEN 
	                  		Max_DR_UL:= MAX[0.05 * Max-Requested-Bandwidth-UL,RS-Bandwidth];
	                	ENDIF;
	                	
	                    IF RS-Bandwidth is not present and RR-Bandwidth is present THEN 
	                      Max_DR_UL:= MAX[0.05 * Max-Requested-Bandwidth-UL,RR-Bandwidth];
	                    ENDIF;
	          
	                    IF RS-Bandwidth and RR-Bandwidth are not present THEN 
	                      Max_DR_UL:= 0.05 * Max-Requested-Bandwidth_UL ;
	                    ENDIF;
	              	ELSE
	                	Max_DR_UL:= as set by the operator;
	              	ENDIF;
	             */
				} else {
					if(mediacomponant.maxRequestedBitRateUpload > 0) {
						long maxAllocatedBw = (long) (mediacomponant.maxRequestedBitRateUpload * 0.05);
						if(maxAllocatedBw == 0) {
							maxAllocatedBw = 1;
						}
						if(mediacomponant.rsBandwidth > 0) {
							pccBuilder.withMbrul(maxAllocatedBw > mediacomponant.rsBandwidth ? maxAllocatedBw : mediacomponant.rsBandwidth);
							pccBuilder.withGbrul(maxAllocatedBw > mediacomponant.rsBandwidth ? maxAllocatedBw : mediacomponant.rsBandwidth);
						} else if(mediacomponant.rrBandwidth > 0){
							pccBuilder.withMbrul(maxAllocatedBw > mediacomponant.rrBandwidth ? maxAllocatedBw : mediacomponant.rrBandwidth);
							pccBuilder.withGbrul(maxAllocatedBw > mediacomponant.rrBandwidth ? maxAllocatedBw : mediacomponant.rrBandwidth);
						} else {
							pccBuilder.withMbrul(maxAllocatedBw);
							pccBuilder.withGbrul(maxAllocatedBw);
						}
					} 
					
					
					if(mediacomponant.maxRequestedBitRateDownload > 0) {
						/**
		    				 IF Max-Requested-Bandwidth-DL is present THEN
		                     IF RS-Bandwidth is present and RR-Bandwidth is not present THEN 
		                       Max_DR_DL:= MAX[0.05 * Max-Requested-Bandwidth-DL,RS-Bandwidth];
		                     ENDIF;
		           
		                     IF RS-Bandwidth is not present and RR-Bandwidth is present THEN 
		                       Max_DR_DL:= MAX[0.05 * Max-Requested-Bandwidth-DL,RR-Bandwidth];
		                     ENDIF;
		           
		                     IF RS-Bandwidth and RR-Bandwidth are not present THEN 
		                       Max_DR_DL:= 0.05 * Max-Requested-Bandwidth-DL;
		                     ENDIF;
		           
		                   ELSE
		                     Max_DR_DL:= as set by the operator;
		           
		                   ENDIF;
		               */
						
						long maxAllocatedBw = (long) (mediacomponant.maxRequestedBitRateDownload * 0.05);
						if(maxAllocatedBw == 0) {
							maxAllocatedBw = 1;
						}
						if(mediacomponant.rsBandwidth > 0) {
							pccBuilder.withMbrdl(maxAllocatedBw > mediacomponant.rsBandwidth ? maxAllocatedBw : mediacomponant.rsBandwidth);
							pccBuilder.withGbrdl(maxAllocatedBw > mediacomponant.rsBandwidth ? maxAllocatedBw : mediacomponant.rsBandwidth);
						} else if(mediacomponant.rrBandwidth > 0){
							pccBuilder.withMbrdl(maxAllocatedBw > mediacomponant.rrBandwidth ? maxAllocatedBw : mediacomponant.rrBandwidth);
							pccBuilder.withGbrdl(maxAllocatedBw > mediacomponant.rrBandwidth ? maxAllocatedBw : mediacomponant.rrBandwidth);
						} else {
							pccBuilder.withMbrdl(maxAllocatedBw);
							pccBuilder.withGbrdl(maxAllocatedBw);
						}
					}	
				}
				
				pccBuilder.withQci(QCI.QCI_GBR_1);
				pccBuilder.withFlowStatus(FlowStatus.ENABLED);
			} else {
				/// RTP flow
				
				/**
	                IF uplink Flow Desription AVP is supplied THEN
	                  IF Max-Requested-Bandwidth-UL is present THEN
	                    Max_DR_UL:= Max-Requested-Bandwidth-UL ;
	                  ELSE
	                    Max_DR_UL:= as set by the operator;
	                  ENDIF;
	        
	                ELSE
	                  Max_DR_UL:= 0;
	        
	                ENDIF;
	        
	        	*/
				if(mediacomponant.uplinkFlow != null) {			
					pccBuilder.withMbrul(mediacomponant.maxRequestedBitRateUpload);
					if(mediacomponant.possibleQCI.isGBRQCI()) {					
						pccBuilder.withGbrul(mediacomponant.maxRequestedBitRateUpload);
					}
				}
				
				/**
	                IF downlink Flow Desription AVPs is supplied THEN
	                  IF Max-Requested-Bandwidth-DL is present THEN
	                    Max_DR_DL:= Max-Requested-Bandwidth-DL;
	        
	                  ELSE
	                    Max_DR_DL:= as set by the operator;
	                  ENDIF;
	        
	                ELSE
	                  Max_DR_DL:= 0;
	        
	                ENDIF;
	              ENDIF; 
				 */
				if(mediacomponant.downlinkFlow != null) {				
					pccBuilder.withMbrdl(mediacomponant.maxRequestedBitRateDownload);
					if(mediacomponant.possibleQCI.isGBRQCI()) {					
						pccBuilder.withGbrdl(mediacomponant.maxRequestedBitRateDownload);
					}
				}
				
				pccBuilder.withQci(mediacomponant.possibleQCI);
				pccBuilder.withFlowStatus(mediacomponant.flowStatus);
			}
		}
	}
}
