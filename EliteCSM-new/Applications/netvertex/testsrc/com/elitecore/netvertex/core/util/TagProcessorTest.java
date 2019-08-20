package com.elitecore.netvertex.core.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.After;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

import com.elitecore.corenetvertex.constants.PCRFKeyConstants;
import com.elitecore.netvertex.core.util.exception.TagParsingException;
import com.elitecore.netvertex.service.pcrf.PCRFResponse;
import com.elitecore.netvertex.service.pcrf.impl.PCRFResponseImpl;

/**
 * The class <code>TagProcessorTest</code> contains tests for the class <code>{@link TagProcessor}</code>.
 *
 * @generatedBy CodePro at 5/2/13 7:24 PM
 * @author Harsh Patel
 * @version $Revision: 1.0 $
 */
public class TagProcessorTest {
	
	private TagProcessor tagProcessor;
	private PCRFResponse pcrfResponse;
	
	@BeforeClass
	public static void testSetup(){
	}
	
	@Before
	public void setUp() throws Exception {
		tagProcessor = new TagProcessor();
		pcrfResponse = new PCRFResponseImpl();
	
	}
	
	@Test
	public void testNotAvailableConstant() throws Exception {
		assertEquals("N/A", TagProcessor.NOT_AVAILABLE);
	}

	@Test
	public void testTagProcessorParseStringWithTag() throws Exception {
		
		String data = "Dear {" + PCRFKeyConstants.SUB_USER_NAME.val  + "}";
		
		pcrfResponse.setAttribute(PCRFKeyConstants.SUB_USER_NAME.val,"harsh");

		String result = tagProcessor.getTemplate(data, pcrfResponse);

		assertEquals("Dear " + pcrfResponse.getAttribute(PCRFKeyConstants.SUB_USER_NAME.val), result);
	}
	

	@Test
	public void testTagProcessorParseStaringOnlyHasTag() throws Exception {
		
		String data = "{" + PCRFKeyConstants.SUB_CITY.val  + "}";
		
		pcrfResponse.setAttribute(PCRFKeyConstants.SUB_CITY.val,"ahmedabad");

		String result = tagProcessor.getTemplate(data, pcrfResponse);

		assertEquals(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_CITY.val), result);
	}
	

	@Test
	public void testTagProcessorParseStringWithConsecutiveTag() throws Exception {
		
		String data = "{" + PCRFKeyConstants.SUB_CITY.val  + "}{" + PCRFKeyConstants.SUB_ARPU + "}";
		
		pcrfResponse.setAttribute(PCRFKeyConstants.SUB_CITY.val,"ahmedabad");

		String result = tagProcessor.getTemplate(data, pcrfResponse);

		assertEquals(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_CITY.val) + TagProcessor.NOT_AVAILABLE, result);
	}
	
	
	@Test
	public void testTagProcessorParseStringWithSpaceInTag() throws Exception {
		
		String data = "{ " + PCRFKeyConstants.SUB_CITY.val  + " }";
		
		pcrfResponse.setAttribute(PCRFKeyConstants.SUB_CITY.val,"ahmedabad");

		String result = tagProcessor.getTemplate(data, pcrfResponse);

		assertEquals(pcrfResponse.getAttribute(PCRFKeyConstants.SUB_CITY.val), result);
	}
	
	@Test
	public void testTagProcessorParseStringWithEmptyTag() throws Exception {
		
		String data = "{  }";
		String result = tagProcessor.getTemplate(data, pcrfResponse);

		assertEquals(TagProcessor.NOT_AVAILABLE, result);
	}
	
	@Test
	public void testTagProcessorParseStringWithImproperTagSyntext() throws Exception {
		
		try{
			String data = "{}{{";
			tagProcessor.getTemplate(data, pcrfResponse);
			fail("Expected result: TagParsingException. Actual Result: StringParse properly");
		}catch(TagParsingException ex){}
		
		try{
			String data = "}{}";
			tagProcessor.getTemplate(data, pcrfResponse);
			fail("Expected result: TagParsingException. Actual Result: StringParse properly");
		}catch(TagParsingException ex){}
		
		try{
			String data = "{";
			tagProcessor.getTemplate(data, pcrfResponse);
			fail("Expected result: TagParsingException. Actual Result: StringParse properly");
		}catch(TagParsingException ex){}
		
		
		
	}

	
	

	@Test
	public void testTagProcessorParseStringWithoutTag() throws Exception {
		
		String data = "Dear";

		String result = tagProcessor.getTemplate(data, pcrfResponse);

		assertEquals(data, result);
	}
	
	@Test
	public void testParseEmptyString() throws Exception {
		String data = "";
		String result = tagProcessor.getTemplate(data, pcrfResponse);
		assertEquals(data, result);
	}
	
	
	@Test(expected = TagParsingException.class)
	public void testParseNullString() throws Exception {
		tagProcessor.getTemplate(null, pcrfResponse);
	}
	
	@Test
	public void testParseStringWithNoTagAndProvideNullpcrfResponse() throws Exception {
		String data = "Dear";
		String result = tagProcessor.getTemplate(data, null);
		assertEquals(data, result);
	}
	
	@Test
	public void testParseStringWithTagAndProvideNullpcrfResponse() throws Exception {
		String data = "Dear { " + PCRFKeyConstants.SUB_CITY.val + "}";
		String result = tagProcessor.getTemplate(data, null);
		assertEquals("Dear " + TagProcessor.NOT_AVAILABLE, result);
	}

	@After
	public void tearDown() throws Exception {
	}
	
}