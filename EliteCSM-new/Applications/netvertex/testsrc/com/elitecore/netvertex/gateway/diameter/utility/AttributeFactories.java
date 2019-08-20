package com.elitecore.netvertex.gateway.diameter.utility;

import java.util.HashMap;
import java.util.Map;

import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.packet.avps.derived.AvpUTF8String;
import com.elitecore.diameterapi.diameter.common.packet.avps.grouped.AvpGrouped;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;

public class AttributeFactories {

	public static AttributeFactoryBuilder newFactory() {
		// TODO Auto-generated method stub
		return new AttributeFactoryBuilder();
	}
	
	public static AttributeFactory fromDummyDictionary() {
		// TODO Auto-generated method stub
		return new AttributeFactory() {
			
			@Override
			public IDiameterAVP create(String id) {
				return DummyDiameterDictionary.getInstance().getKnownAttribute(id);
			}

			@Override
			public IDiameterAVP create(String id, String value) {
				IDiameterAVP avp = create(id);
				
				if (avp != null) {
					avp.setStringValue(value);
				}
				
				return avp;
			}
		};
	}
	
	public static class AttributeFactoryBuilder {

		Map<String, IDiameterAVP> knownAvps = new HashMap<String, IDiameterAVP>();
		
		public AttributeFactoryBuilder addStringAvp(int vendorId, int id) {
			knownAvps.put(vendorId + ":" + id, new AvpUTF8String(id, vendorId , (byte)0, "", ""));
			return this;
		}
		
		public AttributeFactoryBuilder addGroupedAvp(int vendorId, int id) {
			knownAvps.put(vendorId + ":" + id, new AvpGrouped(id, vendorId, (byte)0, "", "", null, null, null));
			return this;
		}
		
		public AttributeFactory create() {
			return new AttributeFactoryImpl(knownAvps);
		}
		
	}
	
	private static class AttributeFactoryImpl implements AttributeFactory {
		
		private Map<String, IDiameterAVP> knownAvps;
		
		public AttributeFactoryImpl(Map<String, IDiameterAVP> knownAvps) {
			this.knownAvps = knownAvps;
		}
		
		@Override
		public IDiameterAVP create(String id) {
			return knownAvps.get(id);
		}

		@Override
		public IDiameterAVP create(String id, String value) {
			IDiameterAVP avp = create(id);
			
			if (avp != null) {
				avp.setStringValue(value);
			}
			
			return avp;
		}
		
	}

}
