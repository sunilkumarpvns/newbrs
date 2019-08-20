package com.elitecore.aaa.core.commons.util;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.fail;

import org.junit.Test;

import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.util.StringOperationUtility;

public class StringOperationUtilityTest {
	
	@Test
	public void testReplaceFirst(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = "elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple replaceFirst operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirst failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testReplaceAll(){
		try{			
			final String DEFAULT_STRING = "eliteaaaeliteaaa";
			String expectedString = "eliteeliteeliteelite";
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,\"aaa\",\"elite\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple ReplaceAll operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAll failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testConcat(){
		try{			
			final String DEFAULT_STRING = "elite";
			String expectedString = "eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple Concat operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcat failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcat2(){
		try{			
			final String DEFAULT_STRING = "elite";
			String expectedString = "eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-concat,\"aaa\"}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple Concat2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcat failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testSubString(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = "aa";
			String resultString = StringOperationUtility.getValue("${STROP-(substring,\"6\",\"8\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple SubString operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubString failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testToUpperCase(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = "ELITEAAA";
			String resultString = StringOperationUtility.getValue("${STROP-(ToUpperCase)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple ToUpperCase operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testToUpperCase failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testToLowerCase(){
		try{			
			final String DEFAULT_STRING = "ELITEAAA";
			String expectedString = "eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ToLowerCase)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple ToLowerCase operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testToLowerCase failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testTrim(){
		try{			
			final String DEFAULT_STRING = "ELITEAAA   ";
			String expectedString = "ELITEAAA";
			String resultString = StringOperationUtility.getValue("${STROP-(Trim)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple Trim operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testTrim failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffix(){
		try{			
			final String DEFAULT_STRING = "ELITEAAA@ELITECORE";
			String expectedString = "ELITEAAA";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple StripSuffix operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffix failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffix1(){
		try{			
			final String DEFAULT_STRING = "ELITEAAA@ELITECORE";
			String expectedString = "ELITEAAA";
			String resultString = StringOperationUtility.getValue("${STROP-StripSuffix,\"@\"}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple StripSuffix1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffix1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefix(){
		try{			
			final String DEFAULT_STRING = "ELITEAAA@ELITECORE";
			String expectedString = "ELITECORE";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of simple StripPrefix operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefix failed, reason: "+e.getMessage());			
		}
	}
	
	private static ValueProvider getValueProvider(){
		return new ValueProvider(){

			@Override
			public String getStringValue(String identifier) {
				// TODO Auto-generated method stub
				return null;
			}
			
		};
	}
	
	@Test
	public void testReplaceFirstNegetive(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			
			if(expectedString.equals(resultString)){
				fail("Test case: ReplaceFirstNegetive failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+expectedString);
			}
			
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstNegetive failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllNegetive(){
		try{			
			final String DEFAULT_STRING = "eliteaaaeliteaaa";
			String expectedString = "eliteelit";
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,\"aaa\",\"elite\")}", getValueProvider(), DEFAULT_STRING);
			if(expectedString.equals(resultString)){
				fail("Test case: ReplaceAllNegetive failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+expectedString);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllNegetive failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatNegetive(){
		try{			
			final String DEFAULT_STRING = "elite";
			String expectedString = "elit";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			if(expectedString.equals(resultString)){
				fail("Test case: ConcatNegetive failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+expectedString);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatNegetive failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSubStringNegetive(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = "eliteaa";
			String resultString = StringOperationUtility.getValue("${STROP-(substring,\"6\",\"8\")}", getValueProvider(), DEFAULT_STRING);
			if(expectedString.equals(resultString)){
				fail("Test case: SubStringNegetive failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+expectedString);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubStringNegetive failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testToUpperCaseNegetive(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = "ELITEaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ToUpperCase)}", getValueProvider(), DEFAULT_STRING);
			if(expectedString.equals(resultString)){
				fail("Test case: ToUpperCaseNegetive failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+expectedString);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testToUpperCaseNegetive failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testToLowerCaseNegetive(){
		try{			
			final String DEFAULT_STRING = "ELITEAAA";
			String expectedString = "eliteAAA";
			String resultString = StringOperationUtility.getValue("${STROP-(ToLowerCase)}", getValueProvider(), DEFAULT_STRING);
			if(expectedString.equals(resultString)){
				fail("Test case: ToLowerCaseNegetive failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+expectedString);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testToLowerCaseNegetive failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testTrimNegetive(){
		try{			
			final String DEFAULT_STRING = "ELITEAAA   ";
			String expectedString = "ELITEAAA     ";
			String resultString = StringOperationUtility.getValue("${STROP-(Trim)}", getValueProvider(), DEFAULT_STRING);
			if(expectedString.equals(resultString)){
				fail("Test case: TrimNegetive failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+expectedString);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testTrimNegetive failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixNegetive(){
		try{			
			final String DEFAULT_STRING = "ELITEAAA@ELITECORE";
			String expectedString = "ELITEAAA@Elite";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\")}", getValueProvider(), DEFAULT_STRING);
			if(expectedString.equals(resultString)){
				fail("Test case: StripSuffixNegetive failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+expectedString);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixNegetive failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixNegetive(){
		try{			
			final String DEFAULT_STRING = "ELITEAAA@ELITECORE";
			String expectedString = "@ELITECORE";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\")}", getValueProvider(), DEFAULT_STRING);
			if(expectedString.equals(resultString)){
				fail("Test case: StripPrefixNegetive failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+expectedString);
			}
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixNegetive failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstWithBlankString(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst,\"aaa\",\"\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of replaceFirstWithBlankString operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstWithBlankString failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstWithMultipleArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = "elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst,\"aaa\",\"core\",\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceFirstWithMultipleArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstWithMultipleArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstWithNoArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of testReplaceFirstWithNoArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstWithMultipleArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstWithFirstNullArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst,null,\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of testReplaceFirstWithFirstNullArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstWithFirstNullArg failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testReplaceFirstWithSecondNullArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst,\"aaa\",null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of testReplaceFirstWithSecondNullArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstWithSecondNullArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstWithBothNullArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst,null,null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of ReplaceFirstWithBothNullArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstWithBothNullArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstWithNullSourceValue(){
		try{			
			final String DEFAULT_STRING = null;
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst,null,null)}", getValueProvider(), DEFAULT_STRING);
			if(resultString!=null){
				fail("Test case: ReplaceFirstWithNullSourceValue failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+resultString);
			}	
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstWithNullSourceValue failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstWithInvalidSyntax(){
		try{			
			final String DEFAULT_STRING = "eliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replacefirst,null,null}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of ReplaceFirstWithInvalidSyntax operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstWithInvalidSyntax failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testReplaceAllWithBlankString(){
		try{			
			final String DEFAULT_STRING = "eliteaaaeliteaaa";
			String expectedString = "eliteelite";
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,\"aaa\",\"\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of replaceAllWithBlankString operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllWithBlankString failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllWithMultipleArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaaeliteaaa";
			String expectedString = "elitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,\"aaa\",\"core\",\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllWithMultipleArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllWithMultipleArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllWithNoArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaaeliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of testReplaceAllWithNoArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllWithMultipleArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllWithFirstNullArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaaeliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,null,\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of testReplaceAllWithFirstNullArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllWithFirstNullArg failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testReplaceAllWithSecondNullArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaaeliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,\"aaa\",null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of testReplaceAllWithSecondNullArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllWithSecondNullArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllWithBothNullArg(){
		try{			
			final String DEFAULT_STRING = "eliteaaaeliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,null,null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of ReplaceAllWithBothNullArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllWithBothNullArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllWithNullSourceValue(){
		try{			
			final String DEFAULT_STRING = null;
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,null,null)}", getValueProvider(), DEFAULT_STRING);
			if(resultString!=null){
				fail("Test case: ReplaceAllWithNullSourceValue failed , Reason Expected String is"+DEFAULT_STRING+" and Actual String is : "+resultString);
			}	
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllWithNullSourceValue failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllWithInvalidSyntax(){
		try{			
			final String DEFAULT_STRING = "eliteaaaeliteaaa";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,null,null}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output of ReplaceAllWithInvalidSyntax operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllWithInvalidSyntax failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatWithBlankSourceKey1(){
		try{			
			String expectedString = "eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\",\"aaa\")}", getValueProvider(), "");
			assertEquals("Output of ConcatWithBlankSourceKey1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatWithBlankSourceKey1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatWithBlankSourceKey2(){
		try{			
			String sourceString = "      ";
			String expectedString = "      "+"eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\",\"aaa\")}", getValueProvider(), sourceString);
			assertEquals("Output of ConcatWithBlankSourceKey2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatWithBlankSourceKey2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatWithBlankArg1(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString = "eliteaaa"+"     "+"aaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"     \",\"aaa\")}", getValueProvider(), sourceString);
			assertEquals("Output of ConcatWithBlankArg1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatWithBlankArg1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatWithBlankArg2(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString = "eliteaaa"+"     "+"   ";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"     \",\"   \")}", getValueProvider(), sourceString);
			assertEquals("Output of ConcatWithBlankArg2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatWithBlankArg2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatWithBlankArg3(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString = "eliteaaa"+"     "+"   "+"eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"     \",\"   \",\"eliteaaa\")}", getValueProvider(), sourceString);
			assertEquals("Output of ConcatWithBlankArg3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatWithBlankArg3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatWithNullArg1(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString = "eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,null1)}", getValueProvider(), sourceString);
			assertEquals("Output of ConcatWithNullArg1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatWithNullArg1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatWithNullArg2(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString = "eliteaaa"+"elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,null1,\"elitecore\")}", getValueProvider(), sourceString);
			assertEquals("Output of ConcatWithNullArg2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatWithNullArg2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatWithNullArg3(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString = "eliteaaa"+"elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elitecore\",null2)}", getValueProvider(), sourceString);
			assertEquals("Output of ConcatWithNullArg3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatWithNullArg3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testConcatWithInvalidSyntax(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString = sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elitecore\",null2}", getValueProvider(), sourceString);
			assertEquals("Output of ConcatWithInvalidSyntax operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testConcatWithInvalidSyntax failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSTROPWithWrongKeywordName(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString = sourceString;
			String resultString = StringOperationUtility.getValue("${STRPT-(concat,\"elitecore\",null2}", getValueProvider(), sourceString);
			assertEquals("Output of STROPWithWrongKeywordName operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testSTROPWithWrongKeywordName failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSubStringWithSourceKeyNull(){
		try{			
			String sourceString =null;
			String resultString = StringOperationUtility.getValue("${STROP-(substring,\"10\",\"20\")}", getValueProvider(), sourceString);
			if(resultString!=null)
				fail("Test case: SubStringWithSourceKeyNull failed , Result string must be Null");
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubStringWithSourceKeyNull failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSubStringWithNullValueProvider(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(substring,\"10\",\"20\")}", null, sourceString);
			assertEquals("Output of SubStringWithNullValueProvider operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubStringWithNullValueProvider failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSubStringWithArgValueNotFound(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(substring,arg1,\"20\")}", getValueProvider(), sourceString);
			assertEquals("Output of SubStringWithArgValueNotFound operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubStringWithArgValueNotFound failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSubStringWithRedundantArg(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="aaa";
			String resultString = StringOperationUtility.getValue("${STROP-(substring,\"5\",\"8\",\"10\")}", getValueProvider(), sourceString);
			assertEquals("Output of SubStringWithRedundantArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubStringWithRedundantArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSubStringWithArgInWrongSeq(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(substring,\"3\",\"1\",\"10\")}", getValueProvider(), sourceString);
			assertEquals("Output of SubStringWithArgInWrongSeq operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubStringWithArgInWrongSeq failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSubStringWithInvalidArg(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(substring,\"invalid\")}", getValueProvider(), sourceString);
			assertEquals("Output of SubStringWithInvalidArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubStringWithInvalidArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSubStringWithNullArg(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(substring,null)}", getValueProvider(), sourceString);
			assertEquals("Output of SubStringWithNullArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubStringWithNullArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testSubStringWithMinusArg(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(substring,\"-10\",\"-1\")}", getValueProvider(), sourceString);
			assertEquals("Output of SubStringWithMinusArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testSubStringWithMinusArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testToLowerCaseWithSourceKeyNull(){
		try{			
			String sourceString =null;
			String resultString = StringOperationUtility.getValue("${STROP-(tolowercase,\"10\",\"20\")}", getValueProvider(), sourceString);
			if(resultString!=null)
				fail("Test case: ToLowerCaseWithSourceKeyNull failed , Result string must be Null");
		}catch(Exception e){
			e.printStackTrace();
			fail("testToLowerCaseWithSourceKeyNull failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testToLowerCaseWithNullValueProvider(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(tolowercase)}", null, sourceString);
			assertEquals("Output of ToLowerCaseWithNullValueProvider operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testToLowerCaseWithNullValueProvider failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testToLowerCaseWithRedundantArg(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(tolowercase,one)}", null, sourceString);
			assertEquals("Output of ToLowerCaseWithRedundantArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testToLowerCaseWithRedundantArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testToUpperCaseWithSourceKeyNull(){
		try{			
			String sourceString =null;
			String resultString = StringOperationUtility.getValue("${STROP-(toUppercase,\"10\",\"20\")}", getValueProvider(), sourceString);
			if(resultString!=null)
				fail("Test case: ToUpperCaseWithSourceKeyNull failed , Result string must be Null");
		}catch(Exception e){
			e.printStackTrace();
			fail("testToLowerCaseWithSourceKeyNull failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testToUpperCaseWithNullValueProvider(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(toUppercase)}", null, sourceString);
			assertEquals("Output of ToUpperCaseWithNullValueProvider operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testToUpperCaseWithNullValueProvider failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testToUpperCaseWithRedundantArg(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(toUppercase,one)}", null, sourceString);
			assertEquals("Output of ToUpperCaseWithRedundantArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testToUpperCaseWithRedundantArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testToLowerCaseWithInvalidSyntax(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(tolowercase,one", null, sourceString);
			assertEquals("Output of ToLowerCaseWithInvalidSyntax operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testToLowerCaseWithInvalidSyntax failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testToUpperCaseWithInvalidSyntax(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(tolowercase,one", null, sourceString);
			assertEquals("Output of testToUpperCaseWithInvalidSyntax operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testToUpperCaseWithInvalidSyntax failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testTrimWithSourceKeyNull(){
		try{			
			String sourceString =null;
			String resultString = StringOperationUtility.getValue("${STROP-(Trim)}", getValueProvider(), sourceString);
			if(resultString!=null)
				fail("Test case: TrimWithSourceKeyNull failed , Result string must be Null");
		}catch(Exception e){
			e.printStackTrace();
			fail("testTrimWithSourceKeyNull failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testTrimWithNullValueProvider(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(Trim)}", null, sourceString);
			assertEquals("Output of TrimWithNullValueProvider operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testTrimWithNullValueProvider failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testTrimWithRedundantArg(){
		try{			
			String sourceString ="eliteaaa      ";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(Trim,one)}", getValueProvider(), sourceString);
			assertEquals("Output of TrimWithRedundantArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testToLowerCaseWithRedundantArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testTrimWithInvalidSyntax(){
		try{			
			String sourceString ="eliteaaa";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(Trim,one", null, sourceString);
			assertEquals("Output of testTrimWithInvalidSyntax operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testTrimWithInvalidSyntax failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithSourceKeyNull(){
		try{			
			String sourceString =null;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"1\")}", getValueProvider(), sourceString);
			if(resultString!=null)
				fail("Test case: StripPrefixSourceKeyNull failed , Result string must be Null");
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithSourceKeyNull failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithNullValueProvider(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"20\")}", null, sourceString);
			assertEquals("Output of StripPrefixWithNullValueProvider operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithNullValueProvider failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithArgValueNotFound(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,arg1,\"20\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithArgValueNotFound operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithArgValueNotFound failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithRedundantArg(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString ="elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"1\",\"0\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithRedundantArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithRedundantArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithOutArgs(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix)}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithOutArgs operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithOutArgs failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithOutSeparator(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix)}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithOutSeparator operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithOutSeparator failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithIncludeIdent(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString ="@elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"1\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithIncludeIdent operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithIncludeIdent failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithTwoArgs(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString ="elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithTwoArgs operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithTwoArgs failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithThreeArgs(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString ="@elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"1\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithThreeArgs operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithThreeArgs failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithWrongPosition1(){
		try{			
			String sourceString ="eliteaaa@elitecore@eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"11\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithWrongPosition1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithWrongPosition1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithWrongPosition2(){
		try{			
			String sourceString ="eliteaaa@elitecore@eliteaaa";
			String expectedString ="@elitecore@eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"0\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithWrongPosition2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithWrongPosition2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithMinusPosition(){
		try{			
			String sourceString ="@eliteaaa@elitecore@eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"-1\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithMinusPosition operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithMinusPosition failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithNullPosition(){
		try{			
			String sourceString ="eliteaaa@elitecore@eliteaaa";
			String expectedString ="@elitecore@eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",null,\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithNullPosition operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithNullPosition failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithInvalidPosition(){
		try{			
			String sourceString ="eliteaaa@elitecore@eliteaaa";
			String expectedString ="@elitecore@eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"invalid\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithInvalidPosition operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithInvalidPosition failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithInvalidSyntax(){
		try{			
			String sourceString ="@eliteaaa@elitecore@eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"-1\",\"1\"", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithInvalidSyntax operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithInvalidSyntax failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixWithNullSeparator(){
		try{			
			String sourceString ="@eliteaaa@elitecore@eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,null,\"-1\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithNullSeparator operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithNullSeparator failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithSourceKeyNull(){
		try{			
			String sourceString =null;
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"1\")}", getValueProvider(), sourceString);
			if(resultString!=null)
				fail("Test case: StripSuffixSourceKeyNull failed , Result string must be Null");
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithSourceKeyNull failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithNullValueProvider(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"20\")}", null, sourceString);
			assertEquals("Output of StripSuffixWithNullValueProvider operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithNullValueProvider failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithArgValueNotFound(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,arg1,\"20\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithArgValueNotFound operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithArgValueNotFound failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithRedundantArg(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"1\",\"0\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithRedundantArg operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithRedundantArg failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithOutArgs(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix)}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithOutArgs operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithOutArgs failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithOutSeparator(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix)}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithOutSeparator operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithOutSeparator failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithIncludeIdent(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString ="eliteaaa@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"1\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithIncludeIdent operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithIncludeIdent failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithTwoArgs(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString ="eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithTwoArgs operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithTwoArgs failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithThreeArgs(){
		try{			
			String sourceString ="eliteaaa@elitecore";
			String expectedString ="eliteaaa@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"1\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithThreeArgs operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithThreeArgs failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithWrongPosition1(){
		try{			
			String sourceString ="eliteaaa@elitecore@eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"11\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithWrongPosition1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithWrongPosition1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithWrongPosition2(){
		try{			
			String sourceString ="eliteaaa@elitecore@eliteaaa";
			String expectedString ="eliteaaa@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"0\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithWrongPosition2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithWrongPosition2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithMinusPosition(){
		try{			
			String sourceString ="@eliteaaa@elitecore@eliteaaa";
			String expectedString ="";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"-1\",\"0\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithMinusPosition operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithMinusPosition failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithNullPosition(){
		try{			
			String sourceString ="eliteaaa@elitecore@eliteaaa";
			String expectedString ="eliteaaa@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",null,\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithNullPosition operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixWithNullPosition failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithInvalidPosition(){
		try{			
			String sourceString ="eliteaaa@elitecore@eliteaaa";
			String expectedString ="eliteaaa@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"invalid\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripPrefixWithInvalidPosition operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithInvalidPosition failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithInvalidSyntax(){
		try{			
			String sourceString ="@eliteaaa@elitecore@eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"-1\",\"1\"", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithInvalidSyntax operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithInvalidSyntax failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixWithNullSeparator(){
		try{			
			String sourceString ="@eliteaaa@elitecore@eliteaaa";
			String expectedString =sourceString;
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,null,\"-1\",\"1\")}", getValueProvider(), sourceString);
			assertEquals("Output of StripSuffixWithNullSeparator operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixWithNullSeparator failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstAndReplaceAll1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecoreeliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(replaceAll,\"core\",\"aaa\"),(replacefirst,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceFirstAndReplaceAll1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstAndReplaceAll1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstAndReplaceAll2(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecoreelitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(replaceFirst,\"core\",\"aaa\"),(replaceAll,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceFirstAndReplaceAll2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstAndReplaceAll2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstAndReplaceAll3(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaelitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(replaceFirst,\"core\",\"aaa\"),(replaceAll,\"aaa\",\"core\"),(replaceFirst,\"core\",\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceFirstAndReplaceAll3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstAndReplaceAll3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndConcat1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING+"aaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll),(Concat,\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndConcat1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndConcat1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndConcat2(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaeliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Concat)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndConcat2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndConcat2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndConcat3(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING+"aaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,null,\"aaa\"),(Concat,\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndConcat3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndConcat3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndConcat4(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING+"aaa"+"   ";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,null,\"aaa\"),(Concat,\"aaa\",\"   \")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndConcat4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndConcat4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndConcat5(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,null,\"aaa\",(Concat,\"aaa\",\"   \")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndConcat5 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndConcat5 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstReplaceAllAndConcat1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaelitecoreelitecoreaaa   ";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,null,\"aaa\"),(Concat,\"aaa\",\"   \"),(ReplaceFirst,\"core\",\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceFirstReplaceAllAndConcat1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstReplaceAllAndConcat1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstReplaceAllAndConcat2(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecoreeliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Concat),(ReplaceFirst,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceFirstReplaceAllAndConcat2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstReplaceAllAndConcat2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstReplaceAllAndConcat3(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaelitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,null,\"aaa\"),(Concat),(ReplaceFirst,\"core\",\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceFirstReplaceAllAndConcat3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstReplaceAllAndConcat3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstReplaceAllAndConcat4(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaelitecoreelitecoreaaa   ";
			String resultString = StringOperationUtility.getValue("${STROP-ReplaceAll,null,\"aaa\"),(Concat,\"aaa\",\"   \"),(ReplaceFirst,\"core\",\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceFirstReplaceAllAndConcat4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstReplaceAllAndConcat4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceFirstReplaceAllAndConcat5(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING+"aaa   ";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll),(Concat,\"aaa\",\"   \"),(ReplaceFirst)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceFirstReplaceAllAndConcat5 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceFirstReplaceAllAndConcat5 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndSubString1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(SubString,\"0\",\"8\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndSubString1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndSubString1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndSubString2(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"aaa\"),(SubString,\"0\",\"9\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndSubString2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndSubString2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndSubString3(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaeliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(SubString,null,\"8\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndSubString3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndSubString3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndSubString4(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaeliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(SubString,\"0\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndSubString4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndSubString4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndSubString5(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(SubString,\"0\",\"8\"),(ReplaceAll,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndSubString5 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndSubString5 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndTrim1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore   ";
			String expectedString = "eliteaaaeliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Trim)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndTrim1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndTrim1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndTrim2(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore      ";
			String expectedString = "elitecoreelitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"aaa\"),(Trim,\"0\",\"9\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndTrim2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndTrim2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndTrim3(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore   ";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Trim)}", null, DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndTrim3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndTrim3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndTrim4(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore      ";
			String expectedString = "elitecoreelitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-ReplaceAll,\"aaa\"),(Trim,\"0\",\"9\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndTrim4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndTrim4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Stripprefix,\"aaa\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix2(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "aaaeliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Stripprefix,\"aaa\",\"1\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix3(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,,\"aaa\"),(Stripprefix,\"aaa\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix4(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaeliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,,\"core\",\"aaa\"),(Stripprefix,)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix5(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,,\"core\"\"aaa\"),(Stripprefix,\"aaa\",\"1\")}", null, DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix5 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix5 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix6(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Stripprefix,\"aaa\",\"1\"),(ReplaceAll,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix6 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix6 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix7(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Stripprefix,\"aaa\",\"-1\"),(ReplaceAll,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix7 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix7 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix8(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "coreelitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Stripprefix,\"aaa\",\"-1\",\"1\"),(ReplaceAll,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix8 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix8 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix9(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,null),(Stripprefix,null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix9 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix9 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix10(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Stripprefix,\"aaa\",\"1\"),(ReplaceAll,\"aaa\",\"core\"),(Stripprefix,\"core\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix10 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix10 failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testReplaceAllAndStripprefix11(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Stripprefix,\"aaa\"\"1\"),(ReplaceAll,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix11 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix11 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix12(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(Stripprefix,\"aaa\",\"-1\"),(ReplaceAll,\"aaa\"\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix12 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix12 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripprefix13(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\")(Stripprefix,\"aaa\",\"-1\"),(ReplaceAll,\"aaa\"\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix13 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix13 failed, reason: "+e.getMessage());			
		}
	}
	
	@Test
	public void testReplaceAllAndStripprefix14(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\")      ,(Stripprefix,\"aaa\",\"-1\"),(ReplaceAll,\"aaa\"\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripprefix14 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix14 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(StripSuffix,\"aaa\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripSuffix1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripSuffix1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix2(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(StripSuffix,\"aaa\",\"1\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripSuffix2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripSuffix2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix3(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"aaa\"),(StripSuffix,\"core\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output ReplaceAllAndStripSuffix3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripprefix3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix4(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eliteaaaeliteaaaeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(StripSuffix,)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndStripSuffix4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripSuffix4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix5(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\"\"aaa\"),(StripSuffix,\"aaa\",\"1\")}", null, DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndStripSuffix5 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripSuffix5 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix6(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(StripSuffix,\"aaa\",\"1\",\"1\"),(ReplaceAll,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndStripSuffix6 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripSuffix6 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix7(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(StripSuffix,\"aaa\",\"-1\"),(ReplaceAll,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndStripSuffix7 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripSuffix7 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix8(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\",\"aaa\"),(StripSuffix,\"aaa\",\"-1\",\"1\"),(ReplaceAll,\"aaa\",\"core\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndStripSuffix8 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripSuffix8 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix9(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,null),(StripSuffix,null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndStripSuffix9 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripSuffix9 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testReplaceAllAndStripSuffix10(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(ReplaceAll,\"core\"\"aaa\"),(StripSuffix,\"aaa\",\"3\"),(ReplaceAll,\"aaa\"\"core\"),(StripSuffix,\"core\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output testReplaceAllAndStripSuffix10 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testReplaceAllAndStripSuffix10 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"core\"\"aaa\"),(StripSuffix,\"aaa\",\"3\")}", null, DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixAndConcat1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat2(){
		try{			
			final String DEFAULT_STRING = "elitecore";
			String expectedString = "elitecoreelite";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\",\"aaa\"),(StripSuffix,\"aaa\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixAndConcat2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat3(){
		try{			
			final String DEFAULT_STRING = "elitecore";
			String expectedString = "elitecoreeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\",null\"aaa\"),(StripSuffix,\"aaa\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixAndConcat3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat4(){
		try{			
			final String DEFAULT_STRING = null;
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\",\"aaa\"),(StripSuffix,\"aaa\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixAndConcat4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat5(){
		try{			
			final String DEFAULT_STRING = null;
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(concat),(concat,\"elite\",\"aaa\"),(StripSuffix,\"aaa\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat5 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixAndConcat5 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat6(){
		try{			
			final String DEFAULT_STRING = null;
			String expectedString = "eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat),(concat,\"elite\",\"aaa\"),(concat,null),(StripSuffix,\"core\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat6 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixAndConcat6 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat7(){
		try{			
			final String DEFAULT_STRING = "eliteaaaelitecoreelitecore";
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\"\"aaa\"),(StripSuffix,\"aaa\",\"-1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat7 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixAndConcat7 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat8(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecoreelitecoreelitecorecoreaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"core\",\"aaa\"),(StripSuffix,\"aaa\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat8 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixAndConcat8 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat9(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(concat,null,null),(StripSuffix,\"aaa\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat9 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripSuffixAndConcat9 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripSuffixAndConcat10(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(oncat,\"core\"\"aaa\"),(tripSuffix,\"aaa\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripSuffixAndConcat10 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat10 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"core\",\"aaa\"),(StripPrefix,\"aaa\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndConcat1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat2(){
		try{			
			final String DEFAULT_STRING = "elitecore";
			String expectedString = "aaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\",\"aaa\"),(StripPrefix,\"aaa\",\"1\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndConcat2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat3(){
		try{			
			final String DEFAULT_STRING = "elitecore";
			String expectedString = "";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\",null\"aaa\"),(StripPrefix,\"aaa\",null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndConcat3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat4(){
		try{			
			final String DEFAULT_STRING = "elite";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\"\"aaa\"),(StripPrefix,\"aaa\",\"1\")}", null, DEFAULT_STRING);
			assertEquals("Output StripPrefixAndConcat4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat5(){
		try{			
			final String DEFAULT_STRING = null;
			String expectedString = "";
			String resultString = StringOperationUtility.getValue("${STROP-(concat),(concat,\"elite\",\"aaa\"),(StripPrefix,\"aaa\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndConcat5 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat5 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat6(){
		try{			
			final String DEFAULT_STRING = null;
			String expectedString = "aaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat),(concat,\"elite\",\"aaa\"),(StripPrefix,\"aaa\",\"1\",\"1\")}", getValueProvider(), DEFAULT_STRING);assertEquals("Output StripPrefixAndConcat6 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat6 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat7(){
		try{			
			final String DEFAULT_STRING = "eliteaaaelitecoreelitecore";
			String expectedString = "aaaelitecoreelitecoreeliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(concat,\"elite\",\"aaa\"),(StripPrefix,\"aaa\",\"-1\",\"-1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndConcat7 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat7 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat8(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING+"aaa";
			String resultString = StringOperationUtility.getValue("${STROP-(null),(null),(concat),(stripPrefix),(concat,\"aaa\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndConcat8 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat8 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat9(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(concat,null,null),(StripPrefix,\"aaa\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndConcat9 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat9 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndConcat10(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(oncat,\"core\"\"aaa\"),(tripSuffix,\"aaa\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndConcat10 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndConcat10 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\"\"aaa\"),(StripSuffix,\"aaa\",\"3\")}", null, DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix2(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,null,null),(StripSuffix,null,null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix3(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelite";
			String expectedString = "elitecoreelite";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,null,null),(StripSuffix,\"core\",\"2\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix4(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\"),(StripSuffix,null,null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix5(){
		try{			
			final String DEFAULT_STRING = "elitecoreeliteaaa";
			String expectedString = "eliteaaa";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\"),(StripSuffix,\"aaa\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix5 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix5 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix6(){
		try{			
			final String DEFAULT_STRING = "elitecoreeliteaaa";
			String expectedString = "elite";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\",null),(StripPrefix,null),(StripSuffix,\"aaa\",null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix6 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix6 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix7(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecoreelitecoreelitecoreelite";
			String expectedString ="elitecoreelite";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\",\"2\"),(StripSuffix,\"core\",\"3\"),(StripPrefix,\"core\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix7 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix7 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix8(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecoreelitecoreelitecoreelite";
			String expectedString ="elitecoreelitecoreelite";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\",\"2\"),(StripSuffix),(StripPrefix,\"core\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix8 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix8 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix9(){
		try{			
			final String DEFAULT_STRING = "";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\"\"aaa\"),(StripSuffix,\"aaa\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix9 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix9 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix10(){
		try{			
			final String DEFAULT_STRING = null;
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\"\"aaa\"),(StripSuffix,\"aaa\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix10 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix10 failed, reason: "+e.getMessage());			
		}
	}
	
	// following test cases are related to source string which start and end with separator
	@Test
	public void testStripPrefixAndStripSuffix11(){
		
		// position = 1
		// includeIdent = false
		
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = "";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"1\",\"0\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix11 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix11 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix12(){
		// position = 1
		// includeIdent = true
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = "@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"1\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix12 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix12 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix13(){
		// position = 5
		// includeIdent = false
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = "@one@two@three@four";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"5\",\"0\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix13 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix13 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix14(){
		// position = 5
		// includeIdent = true
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = "@one@two@three@four@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"5\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix14 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix14 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix15(){
		// position = invalid
		// includeIdent = true
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(StripSuffix,\"@\",\"10\",\"0\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix15 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix15 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix16(){
		// position = 1
		// includeIdent = false
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = "one@two@three@four@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"1\",\"0\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix16 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix16 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix17(){
		// position = 1
		// includeIdent = true
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = "@one@two@three@four@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"1\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix17 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix17 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix18(){
		// position = 5
		// includeIdent = false
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = "";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"5\",\"0\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix18 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix18 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix19(){
		// position = 5
		// includeIdent = true
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = "@";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"5\",\"1\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix19 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix19 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixAndStripSuffix20(){
		// position = invalid
		// includeIdent = true
		try{			
			final String DEFAULT_STRING = "@one@two@three@four@";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"@\",\"9\",\"0\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixAndStripSuffix20 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixAndStripSuffix20 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString1(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\"\"aaa\"),(StripSuffix,\"aaa\",\"3\"),(SubString,\"1\",\"3\")}", null, DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString1 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString2(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "eli";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\"),(StripSuffix,\"core\"),(SubString,\"0\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString2 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString2 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString3(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\",\"1\",\"1\"),(StripSuffix,\"core\"),(SubString,\"1\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString3 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString3 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString4(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "or";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\",\"1\",\"1\"),(StripSuffix),(SubString,\"1\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString4 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString4 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString5(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "or";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\",\"1\",\"1\"),(StripSuffix,null,null),(SubString,\"1\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString5 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString1 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString6(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "li";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,null),(StripSuffix,\"core\"),(SubString,\"1\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString6 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString6 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString7(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "li";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,null),(StripSuffix,null),(SubString,\"1\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString7 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString7 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString8(){
		try{			
			final String DEFAULT_STRING = "elitecoreelitecoreelitecore";
			String expectedString = "elitecoreelitecore";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\"),(StripSuffix,\"aaa\"),(SubString,null)}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString8 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString8 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString9(){
		try{			
			final String DEFAULT_STRING = null;
			String expectedString = DEFAULT_STRING;
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\"core\"\"aaa\"),(StripSuffix,\"aaa\",\"3\"),(SubString,\"1\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString9 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString9 failed, reason: "+e.getMessage());			
		}
	}
	@Test
	public void testStripPrefixStripSuffixAndSubString10(){
		try{			
			final String DEFAULT_STRING = "                     ";
			String expectedString = "  ";
			String resultString = StringOperationUtility.getValue("${STROP-(StripPrefix,\" \",\"1\"),(StripSuffix,\"aaa\",\"3\"),(SubString,\"1\",\"3\")}", getValueProvider(), DEFAULT_STRING);
			assertEquals("Output StripPrefixStripSuffixAndSubString10 operation must be " + expectedString,expectedString,resultString);
		}catch(Exception e){
			e.printStackTrace();
			fail("testStripPrefixStripSuffixAndSubString10 failed, reason: "+e.getMessage());			
		}
	}
					
}
