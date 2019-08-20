package com.elitecore.diameterapi.diameter.translator.parser;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.unitils.reflectionassert.ReflectionAssert;
import org.unitils.reflectionassert.ReflectionComparatorMode;

import com.elitecore.diameterapi.core.translator.TranslatorConstants;
import com.elitecore.diameterapi.diameter.translator.keyword.DiameterKeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.keyword.DBSessionResponseKeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.operations.data.AttributeMapping;
import com.elitecore.diameterapi.diameter.translator.operations.data.GroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.Key;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;

@RunWith(JUnitParamsRunner.class)
public class CopyPacketParserTest {

	@Test(expected=InvalidExpressionException.class)
	@Parameters(method="dataProviderFor_test_NullAndBlank_Destination_Keys_Must_throw_InvalidExpressionException")
	public void test_NullAndBlank_Destination_Keys_Must_throw_InvalidExpressionException(String destMapping) throws InvalidExpressionException{

		DiameterCopyPacketParser.getRequestInstance().parse( "*" , destMapping, "0:1", null, null);
	}
	
	public static Object[][] dataProviderFor_test_NullAndBlank_Destination_Keys_Must_throw_InvalidExpressionException(){
		return new Object[][]{
				{null},
				{"" },
				{"	"},
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_CheckExpression_Must_be_Null_for_BlankString_and_Asterisk")
	public void test_CheckExpression_Must_be_Null_for_BlankString_and_Asterisk(String preRequisiteCheckExpression) throws InvalidExpressionException {
		assertNull(DiameterCopyPacketParser.getRequestInstance().parse( preRequisiteCheckExpression , "0:1", "0:4", null, null).getCheckExpression());
	}
	
	public static Object[][] dataProviderFor_test_CheckExpression_Must_be_Null_for_BlankString_and_Asterisk(){
		return new Object[][]{
				{null},
				{""},
				{"	"},
				{"*"},
				{" * "}
		};
	}

	@Test(expected = InvalidExpressionException.class)
	public void test_Parser_Throws_InvalidExpressionException_On_Exception_From_Expression_Library() throws InvalidExpressionException {
		String srcMapping = "0:1";
		String destMapping = "0:4";

		String defValue = null;
		String valueMapping = null;
		assertNotNull(DiameterCopyPacketParser.getRequestInstance().parse( " 0:1 = " , destMapping, srcMapping, defValue, valueMapping).getCheckExpression());
	}
	
	@Test(expected = InvalidExpressionException.class)
	@Parameters(method="dataProviderFor_test_Invalid_JSON_Group_In_DestinationKey_must_throw_InvalidExpressionException")
	public void test_Invalid_JSON_Group_In_DestinationKey_must_throw_InvalidExpressionException(String destMapping) throws InvalidExpressionException{
		DiameterCopyPacketParser.getRequestInstance().parse( "*" , destMapping, "0:1", null, null);
	}
	
	public static Object[][] dataProviderFor_test_Invalid_JSON_Group_In_DestinationKey_must_throw_InvalidExpressionException() throws InvalidExpressionException{
		return new Object[][]{
				{"{ '0:1' : '0:4' } "},
				
				{"{ 'with' : '0:4' , 'when' : 'this.0:5 = \"1\"' , 'do' : { '0:6' : 'this.0:5' } }"},

				{"{ 'with' : ' 0:4 ' , 'when' : '0:4 = \"*\"' , 'do' : '  ' } " },
					
				{"{ 'with' : ' 0:4 ' , 'when' : '0:4 = \"*\"' , 'do' : '' } " },
		};
	}
	
	@Test
	public void test_CheckExpression() throws InvalidExpressionException {
		
		String expression = " 0:1 = \"*\" ";
		Expression expectedExpression = Compiler.getDefaultCompiler().parseExpression(expression);
		Expression parsedExpression = DiameterCopyPacketParser.getRequestInstance().parse( expression, "0:4", "0:1", null, null).getCheckExpression();
		ReflectionAssert.assertReflectionEquals(expectedExpression, parsedExpression, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_DestinationKey")
	public void test_DestinationKey(String destMapping, Key<?> expectedDestinationKey) throws InvalidExpressionException{
		
		OperationData data = DiameterCopyPacketParser.getRequestInstance().parse( "*" , destMapping, "0:1", null, null);
		Key<?> parsedDestinationKey = data.getAttributeMapping().getDestinationKey();
		ReflectionAssert.assertReflectionEquals(expectedDestinationKey, 
				parsedDestinationKey, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	public static Object[][] dataProviderFor_test_DestinationKey() throws InvalidExpressionException{
		return new Object[][]{
				{"0:4", new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:4\""))},
				{" 0:4 ", new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:4\""))},
				
				{" 0:456.0:431 ", new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:456.0:431\""))},
				
				{"{ 'with' : '0:4' , 'when' : '0:4 = \"*\"' } ", 
					new NonGroupedKey("0:4", Compiler.getDefaultCompiler().parseLogicalExpression("0:4 = \"*\""), 
							Compiler.getDefaultCompiler().parseExpression("\"this\"")) },
			
				{"{ 'with' : '0:4' , 'when' : '0:4 = \"*\"' , 'do' : 'this.0:2' } ", 
					new NonGroupedKey("0:4", Compiler.getDefaultCompiler().parseLogicalExpression("0:4 = \"*\""), 
							Compiler.getDefaultCompiler().parseExpression("\"this.0:2\"")) },
				
			    {"{ 'with' : ' 0:4 ' , 'when' : '0:4 = \"*\"' , 'do' : ' this.0:2 ' } ", 
					new NonGroupedKey("0:4", Compiler.getDefaultCompiler().parseLogicalExpression("0:4 = \"*\""), 
							Compiler.getDefaultCompiler().parseExpression("\"this.0:2\"")) },
				
				{"{ 'with' : '10415:1001' , 'do' : 'this.10415:1004' } ", 
					new NonGroupedKey("10415:1001", null, 
							Compiler.getDefaultCompiler().parseExpression("\"this.10415:1004\"")) },
							
				{"{ 'with' = '10415:1001' , 'do' = 'this.10415:1004' } ", 
					new NonGroupedKey("10415:1001", null, 
							Compiler.getDefaultCompiler().parseExpression("\"this.10415:1004\"")) },
								
				{"{ \"with\" = \"10415:1001\" , \"do\" = \"this.10415:1004\" } ", 
					new NonGroupedKey("10415:1001", null, 
							Compiler.getDefaultCompiler().parseExpression("\"this.10415:1004\"")) },
							
				{"{ \"with\" = \"10415:1001\" , \"do\" = \"this.10415:1004\" } ", 
					new NonGroupedKey("10415:1001", null, 
							Compiler.getDefaultCompiler().parseExpression("\"this.10415:1004\"")) },
							
		};
	}

	@Test
	@Parameters(method="dataProviderFor_test_ResponseMappingSpecific_SourceKey")
	public void test_ResponseMappingSpecific_SourceKey(String srcKey, Key<?> expectedSoruceKey) throws InvalidExpressionException{
	
		OperationData data = DiameterCopyPacketParser.getResponseInstance().parse( "*" , "0:1", srcKey, null, null);
		Key<?> parsedSourceKey = data.getAttributeMapping().getSourceKey();
		ReflectionAssert.assertReflectionEquals(expectedSoruceKey, 
				parsedSourceKey, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	public static Object[][] dataProviderFor_test_ResponseMappingSpecific_SourceKey() throws InvalidExpressionException{
		return new Object[][]{

				{"${SRCREQ}:0:443",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{SRCREQ\\}:0:443"), null, null, 
							createKeyWordMap(new String[]{ "${SRCREQ}:0:443"} , 
									new DiameterKeywordValueProvider[]{new DiameterKeywordValueProvider(TranslatorConstants.SOURCE_REQUEST, "0:443")}) )},
									
				{"${SRCREQ}:0:456.0:431",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{SRCREQ\\}:0:456.0:431"), null, null, 
							createKeyWordMap(new String[]{ "${SRCREQ}:0:456.0:431"} , 
									new DiameterKeywordValueProvider[]{new DiameterKeywordValueProvider(TranslatorConstants.SOURCE_REQUEST, "0:456.0:431")}) )},
					
				{"concat ( ${SRCREQ}:0:296 , ${DSTREQ}:0:283 )",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("concat ( \\$\\{SRCREQ\\}:0:296 , \\$\\{DSTREQ\\}:0:283 )"), null, null, 
							createKeyWordMap(new String[]{ "${SRCREQ}:0:296" , "${DSTREQ}:0:283"} , 
									new DiameterKeywordValueProvider[]{
										new DiameterKeywordValueProvider(TranslatorConstants.SOURCE_REQUEST, "0:296"), 
										new DiameterKeywordValueProvider(TranslatorConstants.DESTINATION_REQUEST, "0:283")}) )},
										
				{"concat ( ${SRCREQ}:0:296 , ${DSTREQ}:0:283 , ${DBSESSION}:PARAM1)",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("concat ( \\$\\{SRCREQ\\}:0:296 , \\$\\{DSTREQ\\}:0:283 , \\$\\{DBSESSION\\}:PARAM1 )"), null, null, 
							createKeyWordMap(new String[]{ "${SRCREQ}:0:296" , "${DSTREQ}:0:283" , "${DBSESSION}:PARAM1"} , 
									new KeywordValueProvider[]{
										new DiameterKeywordValueProvider(TranslatorConstants.SOURCE_REQUEST, "0:296"), 
										new DiameterKeywordValueProvider(TranslatorConstants.DESTINATION_REQUEST, "0:283"),
										new DBSessionResponseKeywordValueProvider("PARAM1")}) )},

				{"{'0:1':'${SRCREQ}:0:4';" +
					"'0:2':'0:3';" +
					"'0:9':{" +
						"'0:5':'${DSTREQ}:0:6'" +
						"}" +
					"}",
					new GroupedKey(createMappingList(
						new AttributeMapping(
							new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{SRCREQ\\}:0:4"), null, null, 
								createKeyWordMap(new String[]{ "${SRCREQ}:0:4"} , new DiameterKeywordValueProvider[]{new DiameterKeywordValueProvider(TranslatorConstants.SOURCE_REQUEST, "0:4")}) ), 
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:1\""))),
						new AttributeMapping(
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:3")), 
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
						new AttributeMapping(
							new GroupedKey(createMappingList(
									new AttributeMapping (
											new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{DSTREQ\\}:0:6"), null, null, 
													createKeyWordMap(new String[]{ "${DSTREQ}:0:6"} , new DiameterKeywordValueProvider[]{new DiameterKeywordValueProvider(TranslatorConstants.DESTINATION_REQUEST, "0:6")}) ),
											new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:5\"")) ))), 
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:9\""))) ))},
					
									
			{"${DBSESSION}:SESSIONID",
				new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{DBSESSION\\}:SESSIONID"), null, null, 
						createKeyWordMap(new String[]{ "${DBSESSION}:SESSIONID"} , 
						new KeywordValueProvider[]{new DBSessionResponseKeywordValueProvider("SESSIONID")}) )},
														
		};
	}
	
	@Test(expected = InvalidExpressionException.class)
	@Parameters(method="dataProviderFor_test_ResponseMappingSpecific_SourceKey_MustThrow_InvalidExpressionException_On_RequestParserInstance")
	public void test_ResponseMappingSpecific_SourceKey_MustThrow_InvalidExpressionException_On_RequestParserInstance(String srcKey) throws InvalidExpressionException{
	
		DiameterCopyPacketParser.getRequestInstance().parse( "*" , "0:1", srcKey, null, null);
	}
	
	public static Object[][] dataProviderFor_test_ResponseMappingSpecific_SourceKey_MustThrow_InvalidExpressionException_On_RequestParserInstance() throws InvalidExpressionException{
		return new Object[][]{

				{"${SRCREQ}:0:443"},
									
				{"concat ( ${SRCREQ}:0:296 , ${DSTREQ}:0:283 )"},
										
				{"{'0:1':'${SRCREQ}:0:4';" +
					"'0:2':'0:3';" +
					"'0:9':{" +
						"'0:5':'${DSTREQ}:0:6'" +
						"}" +
					"}"},
		};
	}

	
	
	@Test
	@Parameters(method="dataProviderFor_test_SourceKey")
	public void test_SourceKey_ApplicableRequestMapping(String srcKey, Key<?> expectedSoruceKey) throws InvalidExpressionException{
	
		OperationData data = DiameterCopyPacketParser.getRequestInstance().parse( "*" , "0:1", srcKey, null, null);
		Key<?> parsedSourceKey = data.getAttributeMapping().getSourceKey();
		ReflectionAssert.assertReflectionEquals(expectedSoruceKey, 
				parsedSourceKey, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_SourceKey")
	public void test_SourceKey_ApplicableResponseMapping(String srcKey, Key<?> expectedSoruceKey) throws InvalidExpressionException{
	
		OperationData data = DiameterCopyPacketParser.getResponseInstance().parse( "*" , "0:1", srcKey, null, null);
		Key<?> parsedSourceKey = data.getAttributeMapping().getSourceKey();
		ReflectionAssert.assertReflectionEquals(expectedSoruceKey, 
				parsedSourceKey, ReflectionComparatorMode.LENIENT_ORDER);
	}	
	
	public static Object[][] dataProviderFor_test_SourceKey() throws InvalidExpressionException{
		return new Object[][]{
				{"\"staticValue\"", 
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"staticValue\""))},
					
				{"\"This is spaced Value\"", 
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"This is spaced Value\""))},
					
				{"\"This is \\\"quoted\\\" string\"", 
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"This is \\\"quoted\\\" string\""))},
				{"0:443",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:443"))},
				{" 0:443 ",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:443"))},
				{"0:443.0:444",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:443.0:444"))},
				{"0:456.0:444.0:293",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:456.0:444.0:293"))},
				{"{'0:1':'0:2'}",
					new GroupedKey(
							createMappingList(new AttributeMapping(
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:2")), 
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:1\"")))))},
				{"{'0:1':'0:2'; '0:3':'\"staticValue\"'}",
					new GroupedKey(createMappingList(
								new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:2")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:1\""))),
								new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"staticValue\"")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:3\""))) ))},
				{"{'0:1':'0:2', '0:3':'0:2'}",
					new GroupedKey(createMappingList(
								new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:2")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:1\""))),
								new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:2")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:3\""))) ))},
									
				{"{'0:1':'0:4';" +
					"'0:2':'0:3';" +
					"'0:9':{" +
						"'0:5':'0:6'" +
						"}" +
					"}",
					new GroupedKey(createMappingList(
								new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:4")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:1\""))),
								new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:3")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
								new AttributeMapping(
										new GroupedKey(createMappingList(
												new AttributeMapping (
														new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:6")),
														new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:5\"")) ))), 
										new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:9\""))) ))},

				{"mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"\")",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("mac2tgpp(\"IMEI\",\"d487.d8b7.a040\",\"\",\"\")"))},
					
				{"{ 'with' : '0:283' , 'when' : '0:283 = \"*@elitecore.com\"' } ",
					new NonGroupedKey("0:283", 
							Compiler.getDefaultCompiler().parseLogicalExpression("0:283 = \"*@elitecore.com\""), 
							Compiler.getDefaultCompiler().parseExpression("this"))},
				{"{ 'with' : '0:283' , 'when' : '0:283 = \"*@elitecore.com\"' , 'do': '\"elitecore.com\"'} ",
					new NonGroupedKey("0:283", 
							Compiler.getDefaultCompiler().parseLogicalExpression("0:283 = \"*@elitecore.com\""), 
							Compiler.getDefaultCompiler().parseExpression("\"elitecore.com\""))},
				{"{ 'with' : '0:283' , 'do': '\"elitecore.com\"'} ",
					new NonGroupedKey("0:283", null, 
							Compiler.getDefaultCompiler().parseExpression("\"elitecore.com\""))},
				
				{"{ '0:443' : { 'with' : '0:443' , 'when' : 'this.0:450 = \"1\"' , 'do' : 'this.0:444' }," +
				 " 	'0:456' : { 'with' : '0:456' , 'when' : 'this.432 = \"19118817838453\"' , 'do' : 'this.0:431'}}",
				 	new GroupedKey(createMappingList(
						 new AttributeMapping(
								 new NonGroupedKey("0:443", Compiler.getDefaultCompiler().parseLogicalExpression("this.0:450 = \"1\""), Compiler.getDefaultCompiler().parseExpression("this.0:444")),
								 new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:443\""))),
						 new AttributeMapping(
								 new NonGroupedKey("0:456", Compiler.getDefaultCompiler().parseLogicalExpression("this.432 = \"19118817838453\""), Compiler.getDefaultCompiler().parseExpression("this.0:431")),
								 new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:456\""))) )) },
				 
				{"{ 'with' : '0:443' ; 'when' : 'this.0:450 = \"1\"' ; 'do' : { '0:2' : 'this.0:444' , '0:3' : 'this.0:450'} }", 
					new GroupedKey("0:443", Compiler.getDefaultCompiler().parseLogicalExpression("this.0:450 = \"1\""),
						createMappingList(
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this.0:444")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this.0:450")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:3\""))) ))},
					
				{"{ 'with' : '0:443' ; 'when' : 'this.0:450 = \"1\"' ; 'do' : { '0:2' : 'this.0:444' , '0:3' : 'this.0:450'} }", 
					new GroupedKey("0:443", Compiler.getDefaultCompiler().parseLogicalExpression("this.0:450 = \"1\""),
						createMappingList(
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this.0:444")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this.0:450")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:3\""))) ))},
				{"{ 'with' : '0:443' ; 'do' : {'0:8':'this.0:444'; '0:2':'0:3'; '0:9': { '0:5':'this.0:450' } } }",
					new GroupedKey( "0:443" , null,
						createMappingList(
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this.0:444")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\""))),
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:3")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
							new AttributeMapping(
									new GroupedKey(createMappingList(
											new AttributeMapping (
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this.0:450")),
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:5\"")) ))), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:9\""))) ))},

				{"{ 'with' : '0:443' ; 'when' : 'this.0:450 = \"1\"' ; 'do' : { '0:8': { 'with' : '0:456' , 'when' : 'this.432 = \"19118817838453\"' , 'do' : 'this.0:431'} } }",
					new GroupedKey( "0:443" , Compiler.getDefaultCompiler().parseLogicalExpression("this.0:450 = \"1\""),
							createMappingList(
								new AttributeMapping(
										new NonGroupedKey("0:456" , Compiler.getDefaultCompiler().parseLogicalExpression("this.432 = \"19118817838453\""),
											Compiler.getDefaultCompiler().parseExpression("this.0:431")),
										new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\""))) ))},

				{"{ 'with' : '0:443' ; 'when' : 'this.0:450 = \"1\"' ; 'do' : { '0:8': { 'with' : '0:456' , 'when' : 'this.432 = \"19118817838453\"' , 'do' : { '0:9' : '0:10' , '0:11' : 'this.0:431' } } } }",
					new GroupedKey( "0:443" , Compiler.getDefaultCompiler().parseLogicalExpression("this.0:450 = \"1\""),
							createMappingList(
								new AttributeMapping(
										new GroupedKey("0:456" , Compiler.getDefaultCompiler().parseLogicalExpression("this.432 = \"19118817838453\""),
											createMappingList(
												new AttributeMapping (
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:10")),
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:9\"")) ),
												new AttributeMapping (
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this.0:431")),
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:11\"")) ))), 
										new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\""))) ))},

				{"{ 'with' : '0:443' ; 'when' : 'this.0:450 = \"1\"' ; 'do' : { '0:8': { 'with' : '0:456' , 'when' : 'this.432 = \"19118817838453\"' , 'do' : { '0:9' : { 'with' : '0:10' ; 'when' : 'this = \"*@com\"' } } } } }",
					new GroupedKey( "0:443" , Compiler.getDefaultCompiler().parseLogicalExpression("this.0:450 = \"1\""),
							createMappingList(
								new AttributeMapping(
										new GroupedKey("0:456" , Compiler.getDefaultCompiler().parseLogicalExpression("this.432 = \"19118817838453\""),
											createMappingList(
												new AttributeMapping (
													new NonGroupedKey( "0:10" , Compiler.getDefaultCompiler().parseLogicalExpression("this = \"*@com\""), Compiler.getDefaultCompiler().parseExpression("this")),
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:9\"")) ))), 
										new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\""))) ))},
										
				{"{'with' = '10415:1001' , 'do' = { '10415:1004' = 'this.10415:1005'}}", 
					new GroupedKey("10415:1001", null, 
							createMappingList(
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this.10415:1005")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"10415:1004\""))))) },
		};
	}
	
	
	private static Map<String, KeywordValueProvider> createKeyWordMap(
			String[] keywords, KeywordValueProvider[] keywordValueProviders) {
		
		Map<String, KeywordValueProvider> keywordValueProviderMap = new HashMap<String, KeywordValueProvider>();
		
		for (int i = 0; i < keywords.length; i++) {
			keywordValueProviderMap.put(keywords[i],	keywordValueProviders[i]);
		}
		return keywordValueProviderMap;
	}

	private static List<AttributeMapping> createMappingList(AttributeMapping... attributeMappings) {
		return Arrays.asList(attributeMappings);
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_DefaultValues_And_ValueMappings_BasedOn_Different_Source_Keys")
	public void test_DefaultValues_And_ValueMappings_BasedOn_Different_Source_Keys(String srcMapping, String defValue, String valueMapping, Key<?> expectedSourceKey) throws InvalidExpressionException{

		OperationData parsedOperationData = DiameterCopyPacketParser.getRequestInstance().parse( "*" , "0:1", srcMapping, defValue, valueMapping);
		
		ReflectionAssert.assertReflectionEquals(expectedSourceKey, 
				parsedOperationData.getAttributeMapping().getSourceKey(), 
				ReflectionComparatorMode.LENIENT_ORDER);

	}
	
	public static Object[][] dataProviderFor_test_DefaultValues_And_ValueMappings_BasedOn_Different_Source_Keys() throws InvalidExpressionException {
		return new Object[][]{
				
				{null, "defaultValue", null,
					new NonGroupedKey("defaultValue")},
					
				{"", "defaultValue", null,
					new NonGroupedKey("defaultValue")},
					
				{"	", "defaultValue", null,
					new NonGroupedKey("defaultValue")},
					
				{"{'0:2' : '0:3'}" , "0:3 = defaultValue" , null ,
					new GroupedKey(createMappingList(
						new AttributeMapping(
							new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("0:3"), "defaultValue", null),
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\"")) ) ))},
							
				{"{'0:2' : '0:3' , '0:4' : '0:5'}" , "0:3 = defaultValue , 0:5 = defaultValue2" , null ,
					new GroupedKey(createMappingList(
						new AttributeMapping(
							new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("0:3"), "defaultValue", null),
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\"")) ) ,
						new AttributeMapping(
								new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("0:5"), "defaultValue2", null),
								new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:4\"")) ) ))},

				{"{ 'with' : '0:443' ; 'when' : 'this.0:450 = \"1\"' ; 'do' : { '0:8': { 'with' : '0:10' ; 'when' : 'this = \"*@com\"' } ; '0:11' : 'this.0:450'} } }" , 
					"0:10 = defaultValue , this.0:450 = defaultValue2" , null ,
					new GroupedKey( "0:443" , Compiler.getDefaultCompiler().parseLogicalExpression("this.0:450 = \"1\""),
						createMappingList(
						  new AttributeMapping(
							new NonGroupedKey("0:10", Compiler.getDefaultCompiler().parseLogicalExpression("this = \"*@com\""), Compiler.getDefaultCompiler().parseExpression("this"), "defaultValue", null),
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\"")) ) ,
						  new AttributeMapping(
							new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("this.0:450"), "defaultValue2", null),
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:11\"")) ) ))},
							
				{"{ 'with' : '0:10' ; 'when' : 'this = \"*@com\"' }", "defaultValue", null,
					new NonGroupedKey("0:10", Compiler.getDefaultCompiler().parseLogicalExpression("this = \"*@com\""), 
						Compiler.getDefaultCompiler().parseExpression("this"), "defaultValue", null)},
						
				{"0:2" , null, "2001 = success , 3001 = failure",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("0:2"), null, 
						buildValueMap(new String[] {"2001" , "success"} , new String[] {"3001" , "failure"} ) )},
				
				{"{ 'with' : '0:443' ; 'when' : 'this.0:450 = \"1\"' ; 'do' : { '0:8': { 'with' : '0:10' ; 'when' : 'this = \"*@com\"' } ; '0:11' : 'this.0:450'} } }" , 
					null, "0:10.2001 = success , 0:10.3001 = failure , this.0:450.abc = pqr , this.0:450.mno = xyz",
					new GroupedKey("0:443" , Compiler.getDefaultCompiler().parseLogicalExpression("this.0:450 = \"1\""),
						createMappingList(
							  new AttributeMapping(
								new NonGroupedKey("0:10", Compiler.getDefaultCompiler().parseLogicalExpression("this = \"*@com\""), Compiler.getDefaultCompiler().parseExpression("this"), null, buildValueMap(new String[] {"2001" , "success"} , new String[] {"3001" , "failure"} )),
								new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\"")) ) ,
							  new AttributeMapping(
								new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("this.0:450"), null, buildValueMap(new String[] {"abc" , "pqr"} , new String[] {"mno" , "xyz"} )),
								new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:11\"")) ) ))},

		};
	}

	private static Map<String, String> buildValueMap(String[]... values) {
		
		HashMap<String, String> map = new HashMap<String, String>();
		for (String[] kvp : values) {
			map.put(kvp[0], kvp[1]);	
		}
		return map;
	}

}
