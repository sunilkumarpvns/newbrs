package com.elitecore.diameterapi.diameter.stack.application.sessionrelease;

import static org.junit.Assert.*;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.elitecore.diameterapi.DummyDiameterDictionary;
import com.elitecore.diameterapi.diameter.common.packet.DiameterAnswer;
import com.elitecore.diameterapi.diameter.common.packet.DiameterPacket;
import com.elitecore.diameterapi.diameter.common.packet.avps.IDiameterAVP;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAVPConstants;
import com.elitecore.diameterapi.diameter.common.util.constant.DiameterAttributeValueConstants;

public class GySessionReleaseIndicatorTest {
	
	private GySessionReleaseIndicator gySessionReleaseIndicator;
	private DiameterAnswer diameterAnswer;
	
	@Before
	public void setUp() {
		this.diameterAnswer= new DiameterAnswer();
		this.gySessionReleaseIndicator = spy(new GySessionReleaseIndicator());
	}
	
	@BeforeClass
	public static void setUpBeforClass() {
		DummyDiameterDictionary.getInstance();
	}
	
	@Test
	public void test_checkCCResponseForSessionRemoval_CallSuperClassMethod() {
		gySessionReleaseIndicator.checkCCResponseForSessionRemoval(diameterAnswer);
		verify(gySessionReleaseIndicator).superCheckCCResponseForSessionRemoval(diameterAnswer);		
	}
	
	@Test
	public void test__checkCCResponseForSessionRemovalReturnsTrue_WhenSuperCheckCCResponseReturnsTrue() throws Exception {
		doReturn(true).when(gySessionReleaseIndicator).superCheckCCResponseForSessionRemoval(diameterAnswer);
		
		assertTrue(gySessionReleaseIndicator.checkCCResponseForSessionRemoval(diameterAnswer));
	}
	
	@Test
	public void test_checkCCResponseForSessionRemoval_ReturnsTrueWhenRequestTypeIsEventRequest() throws Exception {
		setRequestTypeEvent();
		doReturn(false).when(gySessionReleaseIndicator).superCheckCCResponseForSessionRemoval(diameterAnswer);

		assertTrue(gySessionReleaseIndicator.checkCCResponseForSessionRemoval(diameterAnswer));
	}
	
	@Test
	public void test_checkCCResponseForSessionRemoval_ReturnsFalseWhenRequestTypeIsNotEventRequest() throws Exception {
		addToDiameterPacket(diameterAnswer, DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_INITIAL_REQUEST_STR);
		doReturn(false).when(gySessionReleaseIndicator).superCheckCCResponseForSessionRemoval(diameterAnswer);

		assertFalse(gySessionReleaseIndicator.checkCCResponseForSessionRemoval(diameterAnswer));
	}

	private void setRequestTypeEvent() {
		addToDiameterPacket(diameterAnswer, DiameterAVPConstants.CC_REQUEST_TYPE, DiameterAttributeValueConstants.DIAMETER_EVENT_REQUEST_STR);
	}
	
	private void addToDiameterPacket(DiameterPacket diameterPacket, String attributeId, String value){
		IDiameterAVP diameterAVP  = DummyDiameterDictionary.getInstance().getKnownAttribute(attributeId);
		diameterAVP.setStringValue(value);
		diameterPacket.addAvp(diameterAVP);
		
	}
	
}
