package com.elitecore.aaa.radius.systemx.esix.udp.impl;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.elitecore.commons.base.Strings;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.elitecore.coreradius.commons.packet.RadiusPacket;

/**
 * <p>This is an external system attribute handler. This will handle the attributes
 * configured in supported and unsupported attributes. These attributes are
 * configured in external system configuration and also for dynamic NAS external
 * system configuration which is in trusted client profile.</p>
 *  
 * @author narendra.pathai
 * @author kuldeep.panchal
 *
 */
public abstract class ESIAttributeHandler {
	public abstract void handleRequest(RadiusPacket radiusPacket);
	protected abstract ESIAttributeHandler init();
	
	public static ESIAttributeHandler create(@Nullable String supportedAttributeString, 
			@Nonnull String unsupportedAttributeString) {
		if (Strings.isNullOrBlank(supportedAttributeString) 
				&& Strings.isNullOrBlank(unsupportedAttributeString)) {
			return NullESIAttributeHandler.INSTANCE;
		}
		
		return new ESIAttributeHandlerImpl(supportedAttributeString, unsupportedAttributeString).init(); 
	}
	
	static class NullESIAttributeHandler extends ESIAttributeHandler {
		private static final NullESIAttributeHandler INSTANCE = new NullESIAttributeHandler();
		
		@Override
		public ESIAttributeHandler init() {
			return this;
		}
		
		@Override
		public void handleRequest(RadiusPacket radiusPacket) {
			
		}
	}
	
	static class ESIAttributeHandlerImpl extends ESIAttributeHandler {
		private final String MODULE = "ESI_ATTRIBUTE_HANDLER";
		private AttributeRemover attributeRemover; 
		private AttributeSequencer attributeSequencer;
		
		public ESIAttributeHandlerImpl(@Nonnull String supportedAttrsStr, @Nonnull String unsupportedAttrsStr) {
			this.attributeRemover = new AttributeRemover(supportedAttrsStr, unsupportedAttrsStr);
			this.attributeSequencer = new AttributeSequencer(supportedAttrsStr);
		}
		
		@Override
		protected ESIAttributeHandler init() {
			attributeRemover.init();
			attributeSequencer.init();
			return this;
		}
		
		public void handleRequest(RadiusPacket radiusPacket) {
			attributeRemover.handleRequest(radiusPacket);
			attributeSequencer.handleRequest(radiusPacket);
			if(LogManager.getLogger().isLogLevel(LogLevel.DEBUG)) {
				LogManager.getLogger().debug(MODULE, "Resultant Radius Packet: " + radiusPacket);
			}	
		}
	}
}