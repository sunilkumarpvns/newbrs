package com.elitecore.diameterapi.diameter.translator.operations.data;

import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.diameterapi.diameter.common.util.DiameterUtility;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;

public enum DiameterHeaderFields implements HeaderFields<DiameterPacket> {

	APPLICATION_ID("ApplicationId") {
		
		@Override
		public boolean apply(DiameterPacket packet, long value) throws NumberFormatException {
			if (value < 0) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Not Updating Header-Field: " + this.name + 
							", Reason: Provided Negative Value: " + value );
				}
				return false;
			}
			
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Updating Header-Field: " + this.name + 
						" with Value: " + value);
			}
			packet.setApplicationID(value);
			DiameterUtility.updateHeaderApplicationInfoAVP(packet);
			return true;
		}
	},
	COMMAND_CODE("CommandCode") {
		
		@Override
		public boolean apply(DiameterPacket packet, long value) {
			int iValue = (int) value;
			if(iValue < 0) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Not Updating Header-Field: " + this.name + 
							", Reason: Parsed Negative Value: " + iValue +  
							" for provided value: " + value);
				}
				return false;
			} 
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Updating Header-Field: " + this.name + 
						" with Value: " + value);
			}
			packet.setCommandCode(iValue);
			DiameterUtility.updateHeaderCommandCodeInfoAVP(packet);
			return true;
		}
	},
	PROXY_FLAG("ProxyFlag") {
		@Override
		public boolean apply(DiameterPacket packet, long value) {
			
			if(value == 1) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Updating Header-Field: " + this.name + 
							" with Value: " + value);
				}
				packet.setProxiableBit();
			} else if(value == 0) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Updating Header-Field: " + this.name + 
							" with Value: " + value);
				}
				packet.resetProxiableBit();
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Not Updating Header-Field: " + this.name + 
							", Reason: Provided Invalid Value: " + value + ", Header-Field: " + 
							this.name + " only supports value 0 or 1" );
				}
				return false;
			}
			IDiameterAVP diameterAVP = packet.getInfoAVP(DiameterAVPConstants.EC_COMMAND_FLAGES);
			if(diameterAVP == null || diameterAVP.isGrouped() == false){
				return true;
			}
			DiameterUtility.updateHeaderProxyBitInfoAVP(packet, (AvpGrouped) diameterAVP);
			return true;
		}
	},
	ERROR_FLAG("ErrorFlag") {
		@Override
		public boolean apply(DiameterPacket packet, long value) {
			if(packet.isRequest()){
				if(LogManager.getLogger().isLogLevel(LogLevel.WARN)) {
					LogManager.getLogger().warn(MODULE, "Update Operation for Header-Field: " + this.name + 
							" is not applicable for Diameter Request.");
				}
				return false;
			}
			if(value == 1) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Updating Header-Field: " + this.name + 
							" with Value: " + value);
				}
				packet.setErrorBit();
			} else if(value == 0) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Updating Header-Field: " + this.name + 
							" with Value: " + value);
				}
				packet.resetErrorBit();
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Not updating Header-Field: " + this.name + 
							", Reason: Provided Invalid Value: " + value + ", Header-Field: " + 
							this.name + " only supports value 0 or 1" );
				}
			}
			IDiameterAVP diameterAVP = packet.getInfoAVP(DiameterAVPConstants.EC_COMMAND_FLAGES);
			if(diameterAVP == null || diameterAVP.isGrouped() == false){
				return true;
			}
			DiameterUtility.updateHeaderErrorBitInfoAVP(packet, (AvpGrouped) diameterAVP);
			return true;
		}
	},
	RETRANSMITTED_FLAG("RetransmittedFlag") {
		@Override
		public boolean apply(DiameterPacket packet, long value) {
			if(value == 1) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Updating Header-Field: " + this.name + 
							" with Value: " + value);
				}
				packet.setReTransmittedBit();
			} else if(value == 0) {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Updating Header-Field: " + this.name + 
							" with Value: " + value);
				}
				packet.resetReTransmittedBit();
			} else {
				if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
					LogManager.getLogger().debug(MODULE, "Not Updating Header-Field: " + this.name + 
							", Reason: Provided Invalid Value: " + value + ", Header-Field: " + 
							this.name + " only supports value 0 or 1" );
				}
				return false;
			}
			IDiameterAVP diameterAVP = packet.getInfoAVP(DiameterAVPConstants.EC_COMMAND_FLAGES);
			if(diameterAVP == null || diameterAVP.isGrouped() == false){
				return true;
			}
			DiameterUtility.updateHeaderReTransmitedBitInfoAVP(packet, (AvpGrouped) diameterAVP);
			return true;
		}
	};
	
	public static final String MODULE = "DIA-PKT-HDR-FIELDS";
	public final String name;
	private DiameterHeaderFields(String name) {
		this.name = name;
	}
	
	public static DiameterHeaderFields getHeaderField(String name) {
		if(check(APPLICATION_ID, name)){
			return APPLICATION_ID;
		}
		if(check(COMMAND_CODE, name)){
			return COMMAND_CODE;
		}
		if(check(PROXY_FLAG, name)){
			return PROXY_FLAG;
		}
		if(check(ERROR_FLAG, name)){
			return ERROR_FLAG;
		}
		if(check(RETRANSMITTED_FLAG, name)){
			return RETRANSMITTED_FLAG;
		}
		return null;
	}

	private static boolean check(DiameterHeaderFields headerField, String name) {
		return headerField.name.equalsIgnoreCase(name);
	}
	
	@Override
	public String toString() {
		return key();
	}
	
	@Override
	public String key() {
		return name;
	}
	
	@Override
	public final boolean apply(DiameterPacket packet, String value) throws NumberFormatException {
		return apply(packet, Long.parseLong(value));
	}
	
	protected abstract boolean apply(DiameterPacket packet, long value);
}
