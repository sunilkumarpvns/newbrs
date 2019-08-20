package com.elitecore.netvertex.gateway.diameter.utility;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.DiameterPacketMappingValueProvider;
import com.elitecore.netvertex.gateway.diameter.mapping.pcctogateway.gy.PCCToDiameterGyMapping;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterDictionary;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;

import java.util.Arrays;

import static org.mockito.Mockito.mock;

@RunWith(HierarchicalContextRunner.class)
public class PCCToDiameterGyMappingTest {

	private PCCToDiameterGyMapping pccToDiameterGyMapping;
	private DiameterPacketMappingValueProvider valueProvider;
	private AvpAccumalatorTestSupport accumalator = new AvpAccumalatorTestSupport();
	private PCRFResponse pcrfResponse;
	private AttributeFactory attributeFactory = AttributeFactories.fromDummyDictionary();

	@Before
	public void before() {
		pccToDiameterGyMapping = new PCCToDiameterGyMapping();
		pcrfResponse = new PCRFResponseImpl();
		valueProvider = new DiameterPacketMappingValueProvider(pcrfResponse, mock(DiameterPacket.class), null, null);
		DummyDiameterDictionary.getInstance();
	}

	public class EventTimestamp {

		private static final String EVENT_TIMESTAMP = "10000000";

		@Test
		public void added_InAccumulator() {

			pcrfResponse.setAttribute(PCRFKeyConstants.EVENT_TIMESTAMP.val, EVENT_TIMESTAMP);

			pccToDiameterGyMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Arrays.asList(attributeFactory.create(PCRFKeyConstants.EVENT_TIMESTAMP.val)), accumalator.getAll());
		}

		@Test
		public void notAdded_InAccumulator() {

			pccToDiameterGyMapping.apply(valueProvider, accumalator);

			ReflectionAssert.assertLenientEquals(Collectionz.newArrayList(), accumalator.getAll());
		}

	}

}
