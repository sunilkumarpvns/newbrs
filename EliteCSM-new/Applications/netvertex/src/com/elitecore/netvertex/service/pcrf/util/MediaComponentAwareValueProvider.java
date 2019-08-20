package com.elitecore.netvertex.service.pcrf.util;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.exprlib.parser.exception.InvalidTypeCastException;
import com.elitecore.exprlib.parser.exception.MissingIdentifierException;
import com.elitecore.netvertex.gateway.diameter.af.MediaComponent;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MediaComponentAwareValueProvider extends PCRFValueProvider{

	private MediaComponent mediaComponent;

	public MediaComponentAwareValueProvider(MediaComponent mediaComponent, PCRFRequest pcrfRequest, PCRFResponse pcrfResponse) {
		super(pcrfRequest,pcrfResponse);
		this.mediaComponent = mediaComponent;
	}
	
	public MediaComponentAwareValueProvider(MediaComponent mediaComponent, PCRFRequest pcrfRequest) {
		super(pcrfRequest);
		this.mediaComponent = mediaComponent;
	}
	
	
	public MediaComponentAwareValueProvider(MediaComponent mediaComponent, PCRFResponse pcrfResponse) {
		super(pcrfResponse);
		this.mediaComponent = mediaComponent;
	}
	
	
	@Override
	public long getLongValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException {
		MediaComponentAttribute attribute = MediaComponentAttribute.keyToAttribute.get(identifier);
		if(attribute != null) {
			long val = attribute.getLongVal(mediaComponent);
			if(val == -1){
				throw new MissingIdentifierException("Configured identifier not found: " + identifier);
			}
			return val;
		} else {			
			return super.getLongValue(identifier);
		}
	}
	
	@Override
	public String getStringValue(String identifier) throws InvalidTypeCastException,MissingIdentifierException{
		MediaComponentAttribute attribute = MediaComponentAttribute.keyToAttribute.get(identifier);
		if(attribute != null) {
			String val = attribute.getStringVal(mediaComponent);
			if(val == null){
				throw new MissingIdentifierException("Configured identifier not found: " + identifier);
			}
			return val;
		} else {			
			return super.getStringValue(identifier);
		}
	}

	@Override
	public List<String> getStringValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		MediaComponentAttribute attribute = MediaComponentAttribute.keyToAttribute.get(identifier);
		if(attribute != null) {
			String val = attribute.getStringVal(mediaComponent);
			if(val == null){
				throw new MissingIdentifierException("Configured identifier not found: " + identifier);
			}
			List<String> values = new ArrayList<String>(2);
			values.add(val);
			return values;
		} else {			
			return super.getStringValues(identifier);
		}
	}

	@Override
	public List<Long> getLongValues(String identifier)throws InvalidTypeCastException, MissingIdentifierException {
		MediaComponentAttribute attribute = MediaComponentAttribute.keyToAttribute.get(identifier);
		if(attribute != null) {
			long val = attribute.getLongVal(mediaComponent);
			if(val == -1){
				throw new MissingIdentifierException("Configured identifier not found: " + identifier);
			}
			List<Long> values = new ArrayList<Long>(2);
			values.add(val);
			return values;
		} else {			
			return super.getLongValues(identifier);
		}
	}
	
	private enum MediaComponentAttribute {
		
		MEDIA_COMPONENT_QCI(PCRFKeyConstants.MEDIA_COMPONENT_QCI) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return mediaComponent.getQCI().stringVal;
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return mediaComponent.getQCI().val;
			}
		},
		
		MEDIA_COMPONENT_NUMBER(PCRFKeyConstants.MEDIA_COMPONENT_NUMBER) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return String.valueOf(mediaComponent.getMediaComponentNumber());
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return mediaComponent.getMediaComponentNumber();
			}
		},
		
		MEDIA_COMPONENT_AF_APP_IDENTIFIER(PCRFKeyConstants.MEDIA_COMPONENT_AF_APP_IDENTIFIER) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return mediaComponent.getAfIdentifier();
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return -1;
			}
		},
		
		MEDIA_COMPONENT_UPLINK_CODEC(PCRFKeyConstants.MEDIA_COMPONENT_UPLINK_CODEC) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return mediaComponent.getUplinkCodec();
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return -1;
			}
		},
		
		MEDIA_COMPONENT_DOWNLINK_CODEC(PCRFKeyConstants.MEDIA_COMPONENT_DOWNLINK_CODEC) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return mediaComponent.getDownloadlinkCodekc();
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return -1;
			}
		},
		
		MEDIA_COMPONENT_FLOW_STATUS(PCRFKeyConstants.MEDIA_COMPONENT_FLOW_STATUS) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				if(mediaComponent.getFlowStatus() != null) {					
					return mediaComponent.getFlowStatus().displayVal;
				}
				
				return null;
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return -1;
			}
		},
		
		MEDIA_COMPONENT_UPLINK_FLOW(PCRFKeyConstants.MEDIA_COMPONENT_UPLINK_FLOW) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return mediaComponent.getUplinkFlow();
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return -1;
			}
		},
		
		MEDIA_COMPONENT_DOWNLINK_FLOW(PCRFKeyConstants.MEDIA_COMPONENT_DOWNLINK_FLOW) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return mediaComponent.getDownlinkFlow();
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return -1;
			}
		},
		
		MEDIA_COMPONENT_AF_SIGNALLING_PROTOCOL(PCRFKeyConstants.MEDIA_COMPONENT_AF_SIGNALLING_PROTOCOL) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				if(mediaComponent.getAFSinallingProtocol() != null) {
					return mediaComponent.getAFSinallingProtocol().strVal;
				}
				
				return null;
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return -1;
			}
		},
		
		MEDIA_COMPONENT_FLOW_NUMBER(PCRFKeyConstants.MEDIA_COMPONENT_FLOW_NUMBER) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return String.valueOf(mediaComponent.getFlowNumber());
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return mediaComponent.getFlowNumber();
			}
		},
		
		MEDIA_COMPONENT_FLOW_USAGE(PCRFKeyConstants.MEDIA_COMPONENT_FLOW_USAGE) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				if(mediaComponent.getFlowUsage() != null) {
					return mediaComponent.getFlowUsage().strVal;
				}
				
				return null;
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return -1;
			}
		},
		
		MEDIA_COMPONENT_MAX_REQUESTED_BW_DOWNLOAD(PCRFKeyConstants.MEDIA_COMPONENT_MAX_REQUESTED_BW_DOWNLOAD) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return String.valueOf(mediaComponent.getMaxRequestedBitRateDownload());
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return mediaComponent.getMaxRequestedBitRateDownload();
			}
		},
		MEDIA_COMPONENT_MAX_REQUESTED_BW_UPLOAD(PCRFKeyConstants.MEDIA_COMPONENT_MAX_REQUESTED_BW_UPLOAD) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return String.valueOf(mediaComponent.getMaxRequestedBitRateUpload());
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return mediaComponent.getMaxRequestedBitRateUpload();
			}
		},
		
		MEDIA_COMPONENT_RESERVATION_PRIORITY(PCRFKeyConstants.MEDIA_COMPONENT_RESERVATION_PRIORITY) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return String.valueOf(mediaComponent.getReservationPriority());
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return mediaComponent.getReservationPriority();
			}
		},
		
		MEDIA_COMPONENT_RR_BW(PCRFKeyConstants.MEDIA_COMPONENT_RR_BW) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return String.valueOf(mediaComponent.getRrBandwidth());
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return mediaComponent.getRrBandwidth();
			}
		},
		
		MEDIA_COMPONENT_RS_BW(PCRFKeyConstants.MEDIA_COMPONENT_RS_BW) {
			@Override
			public String getStringVal(MediaComponent mediaComponent) {
				return String.valueOf(mediaComponent.getRsBandwidth());
			}

			@Override
			public long getLongVal(MediaComponent mediaComponent) {
				return mediaComponent.getRsBandwidth();
			}
		},;
		
		public final PCRFKeyConstants pcrfKey;
		
		private static Map<String, MediaComponentAttribute> keyToAttribute;
		private MediaComponentAttribute(PCRFKeyConstants pcrfKeyConstants) {
			this.pcrfKey = pcrfKeyConstants;
		}
		
		static {
			
			keyToAttribute = new HashMap<>();
			
			for(MediaComponentAttribute attribute : values()) {
				keyToAttribute.put(attribute.pcrfKey.val, attribute);
			}
		}
		
		public abstract String getStringVal(MediaComponent mediaComponent);
		public abstract long getLongVal(MediaComponent mediaComponent);
		
		
	}
	
	

}
