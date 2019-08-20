package com.elitecore.netvertex.gateway.diameter.transaction.action;

import com.elitecore.corenetvertex.constants.PCRFEvent;
import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.diameterapi.diameter.common.packet.DiameterRequest;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.DiameterToPCCPacketMapping;
import com.elitecore.netvertex.gateway.diameter.mapping.gatewaytopcc.PCRFRequestMappingValueProvider;
import com.elitecore.netvertex.core.transaction.DiameterTransactionContext;
import com.elitecore.netvertex.gateway.diameter.DummyDiameterGatewayControllerContext;
import com.elitecore.netvertex.gateway.diameter.conf.DiameterGatewayConfiguration;
import com.elitecore.netvertex.gateway.diameter.transaction.MappingFailedException;
import com.elitecore.netvertex.gateway.diameter.transaction.TransactionState;
import com.elitecore.netvertex.gateway.diameter.transaction.session.SessionKeys;
import com.elitecore.netvertex.service.pcrf.PCRFRequest;
import com.elitecore.netvertex.service.pcrf.impl.PCRFRequestImpl;
import de.bechte.junit.runners.context.HierarchicalContextRunner;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import javax.annotation.Nonnull;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.*;

@RunWith(HierarchicalContextRunner.class)
public class ActionHandlerTest {

	private ActionHandler actionHandler;
	private DummyDiameterTransactionContext context;
	@Mock
	private DiameterRequest diameterRequest;
	private PCRFRequest pcrfRequest;
	private DummyDiameterGatewayControllerContext gatewayControllerContext;

	@Before
	public void setUp() {
		context = new DummyDiameterTransactionContext();
		actionHandler = new ActionHandlerExt(context);
		MockitoAnnotations.initMocks(this);
		pcrfRequest = new PCRFRequestImpl();

		gatewayControllerContext = new DummyDiameterGatewayControllerContext();
		gatewayControllerContext.setPCRFRequest(pcrfRequest);
		context.setDiameterGatewayControllerContext(gatewayControllerContext);
		context.getTransactionSession().put(SessionKeys.DIAMETER_REQUEST, diameterRequest);
	}

	public class CreatePCRFRequest {

		@Before
		public void setUp() {
			actionHandler = spy(actionHandler);

			try {
				doNothing().when(actionHandler).addCoreSessionAttibutes(any(PCRFRequest.class));
			}catch (MappingFailedException e){
			}
		}

		@Test
		public void AddsTransactionIdFromTransactionSession() throws MappingFailedException{
			String inputTransactionId = "215451125359";
			context.getTransactionSession().put(SessionKeys.TRANSACTION_ID, inputTransactionId);

			try {
				PCRFRequest pcrfRequest = actionHandler.createPCRFRequest(mock(DiameterToPCCPacketMapping.class), mock(DiameterGatewayConfiguration.class));
				assertEquals(inputTransactionId, pcrfRequest.getAttribute(PCRFKeyConstants.TRANSACTION_ID.val));
			}catch(MappingFailedException e){
				throw e;
			}
		}
		
		@Test
		public void callAddCoreSessionAttibutesWhenPCRFEventIsNotSessionStart() throws MappingFailedException{


			DiameterToPCCPacketMapping mapping = new DiameterToPCCPacketMapping() {

				@Override
				public void apply(@Nonnull PCRFRequestMappingValueProvider valueProvider) {
					PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
					pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_STOP);
				}
			};

			try {
				PCRFRequest pcrfRequest = actionHandler.createPCRFRequest(mapping, mock(DiameterGatewayConfiguration.class));
				verify(actionHandler).addCoreSessionAttibutes(pcrfRequest);
			}catch (MappingFailedException e){
				throw e;
			}
		}
		
		@Test
		public void notCallAddCoreSessionAttibutesWhenPCRFEventSessionStart() throws MappingFailedException {

			DiameterToPCCPacketMapping mapping = new DiameterToPCCPacketMapping() {

				@Override
				public void apply(@Nonnull PCRFRequestMappingValueProvider valueProvider) {
					PCRFRequest pcrfRequest = valueProvider.getPCRFRequest();
					pcrfRequest.addPCRFEvent(PCRFEvent.SESSION_START);
				}
			};

			try {
				PCRFRequest pcrfRequest = actionHandler.createPCRFRequest(mapping, mock(DiameterGatewayConfiguration.class));
				verify(actionHandler, times(0)).addCoreSessionAttibutes(pcrfRequest);
			}catch (MappingFailedException e){
				throw e;
			}
		}
	}
	
	private class ActionHandlerExt extends ActionHandler {

		public ActionHandlerExt(DiameterTransactionContext transactionContext) {
			super(transactionContext);
		}

		@Override
		public TransactionState handle() {
			return null;
		}

		@Override
		public String getName() {
			return null;
		}
	}
}
