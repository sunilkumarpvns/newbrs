package com.elitecore.commons.base;

import static org.junit.Assert.assertSame;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

/**
 * 
 * @author narendra.pathai
 *
 */
public class PreconditionsTest {

	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	public void testCheckArgument_ShouldNotThrowAnyException_WhenExpressionEvaluesToTrue(){
		Preconditions.checkArgument(true, "don't care");
	}
	
	@Test
	public void testCheckArgument_ShouldThrowIllegalArgumentException_WhenExpressionEvaluatesToFalse(){
		String messageString = "Any message";
		
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage(messageString);
		
		Preconditions.checkArgument(false, messageString);
	}
	
	@Test
	public void testCheckArgument_ShouldThrowIllegalArgumentExceptionWithNullMessage_WhenExpressionEvaluatesToFalseAndMessageStringIsNull(){
		exception.expect(IllegalArgumentException.class);
		exception.expectMessage("null");
		
		Preconditions.checkArgument(false, null);
	}
	
	@Test
	public void testCheckNotNull_ShouldThrowNullPointerException_WhenReferencePassedIsNull(){
		String messageString = "Any message";
		
		exception.expect(NullPointerException.class);
		exception.expectMessage(messageString);
		
		Preconditions.checkNotNull(null, messageString);
	}
	
	@Test
	public void testCheckNotNull_ShouldThrowNullPointerExceptionWithNullMessage_WhenReferenceIsNullAndMessageStringIsNull(){
		exception.expect(NullPointerException.class);
		exception.expectMessage("null");
		
		Preconditions.checkNotNull(null, null);
	}
	
	@Test
	public void testCheckNotNull_ShouldReturnTheSameReferencePassed_WhenReferenceIsNotNull(){
		Object reference = new Object();
		Object returnedReference = Preconditions.checkNotNull(reference, null);
		
		assertSame(reference, returnedReference);
	}
	
	@Test
	public void testCheckState_ShouldNotThrowAnyException_WhenExpressionEvaluesToTrue(){
		Preconditions.checkState(true, "don't care");
	}
	
	@Test
	public void testCheckState_ShouldThrowIllegalStateException_WhenExpressionEvaluatesToFalse(){
		String messageString = "Any message";
		
		exception.expect(IllegalStateException.class);
		exception.expectMessage(messageString);
		
		Preconditions.checkState(false, messageString);
	}
	
	@Test
	public void testCheckState_ShouldThrowIllegalStateExceptionWithNullMessage_WhenExpressionEvaluatesToFalseAndMessageStringIsNull(){
		exception.expect(IllegalStateException.class);
		exception.expectMessage("null");
		
		Preconditions.checkState(false, null);
	}
}
