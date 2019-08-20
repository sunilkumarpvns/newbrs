package com.elitecore.aaa.core.radius.translators.copypacket;

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

import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.radius.translators.copypacket.keyword.RadiusPacketKeywordValueProvider;
import com.elitecore.aaa.radius.translators.copypacket.keyword.RadiusServiceRequestKeywordValueProvider;
import com.elitecore.aaa.radius.translators.copypacket.keyword.RadiusSrcResKeyworValueProvider;
import com.elitecore.aaa.radius.translators.copypacket.parser.RadiusCopyPacketParser;
import com.elitecore.diameterapi.diameter.translator.keyword.KeywordValueProvider;
import com.elitecore.diameterapi.diameter.translator.operations.data.AttributeMapping;
import com.elitecore.diameterapi.diameter.translator.operations.data.GroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.Key;
import com.elitecore.diameterapi.diameter.translator.operations.data.NonGroupedKey;
import com.elitecore.diameterapi.diameter.translator.operations.data.OperationData;
import com.elitecore.diameterapi.diameter.translator.parser.CopyPacketParser;
import com.elitecore.exprlib.compiler.Compiler;
import com.elitecore.exprlib.parser.exception.InvalidExpressionException;
import com.elitecore.exprlib.parser.expression.Expression;


@RunWith(JUnitParamsRunner.class)
public class RadiusCopyPacketParserTest {

	@Test(expected=InvalidExpressionException.class)
	@Parameters(method="dataProviderFor_test_NullAndBlank_Destination_Keys_Must_throw_InvalidExpressionException")
	public void test_NullAndBlank_Destination_Keys_Must_throw_InvalidExpressionException(String destMapping) throws InvalidExpressionException{
		
		RadiusCopyPacketParser.getRequestInstance().parse( "*" , destMapping, "0:1", null, null);
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
		assertNull(RadiusCopyPacketParser.getRequestInstance().parse( preRequisiteCheckExpression , "0:1", "0:4", null, null).getCheckExpression());
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
		assertNotNull(RadiusCopyPacketParser.getRequestInstance().parse( " 0:1 = " , destMapping, srcMapping, defValue, valueMapping).getCheckExpression());
	}
	
	@Test(expected = InvalidExpressionException.class)
	@Parameters(method="dataProviderFor_test_Invalid_JSON_Group_In_DestinationKey_must_throw_InvalidExpressionException")
	public void test_Invalid_JSON_Group_In_DestinationKey_must_throw_InvalidExpressionException(String destMapping) throws InvalidExpressionException{
		RadiusCopyPacketParser.getRequestInstance().parse( "*" , destMapping, "0:1", null, null);
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
		Expression parsedExpression = RadiusCopyPacketParser.getRequestInstance().parse( expression, "0:4", "0:1", null, null).getCheckExpression();
		ReflectionAssert.assertReflectionEquals(expectedExpression, parsedExpression, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_DestinationKey")
	public void test_DestinationKey(String destMapping, Key<?> expectedDestinationKey) throws InvalidExpressionException{
		
		OperationData data = RadiusCopyPacketParser.getRequestInstance().parse( "*" , destMapping, "0:1", null, null);
		Key<?> parsedDestinationKey = data.getAttributeMapping().getDestinationKey();
		ReflectionAssert.assertReflectionEquals(expectedDestinationKey, 
				parsedDestinationKey, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	public static Object[][] dataProviderFor_test_DestinationKey() throws InvalidExpressionException{
		return new Object[][]{
				{"0:4", new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:4\""))},
				{" 0:4 ", new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:4\""))},
				
				{"12067:145:1", new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"12067:145:1\""))},
				
				{"{ 'with' : '0:4' , 'when' : '0:4 = \"*\"' } ", 
					new NonGroupedKey("0:4", Compiler.getDefaultCompiler().parseLogicalExpression("0:4 = \"*\""), 
							Compiler.getDefaultCompiler().parseExpression("\"this\"")) },
			
				{"{ 'with' : '0:4' , 'when' : '0:4 = \"*\"' , 'do' : 'this:2' } ", 
					new NonGroupedKey("0:4", Compiler.getDefaultCompiler().parseLogicalExpression("0:4 = \"*\""), 
							Compiler.getDefaultCompiler().parseExpression("\"this:2\"")) },
				
			    {"{ 'with' : ' 0:4 ' , 'when' : '0:4 = \"*\"' , 'do' : ' this:2 ' } ", 
					new NonGroupedKey("0:4", Compiler.getDefaultCompiler().parseLogicalExpression("0:4 = \"*\""), 
							Compiler.getDefaultCompiler().parseExpression("\"this:2\"")) },
				
				{"{ 'with' : '12067:145' , 'do' : 'this:1' } ", 
					new NonGroupedKey("12067:145", null, 
							Compiler.getDefaultCompiler().parseExpression("\"this:1\"")) },
							
				{"{ \"with\" = \"10415:3\" , \"do\" = \"this:1\" } ", 
					new NonGroupedKey("10415:3", null, 
							Compiler.getDefaultCompiler().parseExpression("\"this:1\"")) },
							
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_SourceKey")
	public void test_SourceKey(String srcKey, Key<?> expectedSoruceKey) throws InvalidExpressionException{
	
		OperationData data = RadiusCopyPacketParser.getRequestInstance().parse( "*" , "0:1", srcKey, null, null);
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
				{"10415:3",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("10415:3"))},
				{" 10415:3 ",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("10415:3"))},
				{"10415:3:1",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("10415:3:1"))},
				{"10415:3:1:2",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("10415:3:1:2"))},
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
					
				{"{ 'with' : '0:1' , 'when' : '0:1 = \"*@elitecore.com\"' } ",
					new NonGroupedKey("0:1", 
							Compiler.getDefaultCompiler().parseLogicalExpression("0:1 = \"*@elitecore.com\""), 
							Compiler.getDefaultCompiler().parseExpression(CopyPacketParser.THIS))},
							
				{"{ 'with' : '0:1' , 'when' : '0:1 = \"*@elitecore.com\"' , 'do': '\"elitecore.com\"'} ",
					new NonGroupedKey("0:1", 
							Compiler.getDefaultCompiler().parseLogicalExpression("0:1 = \"*@elitecore.com\""), 
							Compiler.getDefaultCompiler().parseExpression("\"elitecore.com\""))},
							
				{"{ 'with' : '0:1' , 'do': '\"elitecore.com\"'} ",
					new NonGroupedKey("0:1", null, 
							Compiler.getDefaultCompiler().parseExpression("\"elitecore.com\""))},
				
				{"{ '12067:145' : { 'with' : '12067:145' , 'when' : 'this:3 = \"1\"' , 'do' : 'this:3' }," +
				 " 	'10415:3' : { 'with' : '10415:3' , 'when' : 'this:1 = \"19118817838453\"' , 'do' : 'this:1'}}",
				 	new GroupedKey(createMappingList(
						 new AttributeMapping(
								 new NonGroupedKey("12067:145", Compiler.getDefaultCompiler().parseLogicalExpression("this:3 = \"1\""), Compiler.getDefaultCompiler().parseExpression("this:3")),
								 new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"12067:145\""))),
						 new AttributeMapping(
								 new NonGroupedKey("10415:3", Compiler.getDefaultCompiler().parseLogicalExpression("this:1 = \"19118817838453\""), Compiler.getDefaultCompiler().parseExpression("this:1")),
								 new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"10415:3\""))) )) },
				 
				{"{ 'with' : '12067:145' ; 'when' : 'this:3 = \"1\"' ; 'do' : { '0:2' : 'this:3' , '0:3' : 'this:1'} }", 
					new GroupedKey("12067:145", Compiler.getDefaultCompiler().parseLogicalExpression("this:3 = \"1\""),
						createMappingList(
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:3")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:1")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:3\""))) ))},
					
				{"{ 'with' : '12067:145' ; 'do' : {'0:8':'this:3'; '0:2':'0:3'; '0:9': { '0:5':'this:1' } } }",
					new GroupedKey( "12067:145" , null,
						createMappingList(
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:3")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\""))),
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:3")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
							new AttributeMapping(
									new GroupedKey(createMappingList(
											new AttributeMapping (
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:1")),
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:5\"")) ))), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:9\""))) ))},
									
				{"{'with' = '10415:145' , 'do' = { '10415:3' = 'this:3'}}", 
						new GroupedKey("10415:145", null, 
								createMappingList(
									new AttributeMapping(
											new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:3")), 
											new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"10415:3\""))))) },
		};
	}
	
	@Test
	@Parameters(method="dataProviderFor_test_SourceKey_ResponseSpecific")
	public void test_SourceKey_ResponseSpecific(String srcKey, Key<?> expectedSoruceKey) throws InvalidExpressionException{
	
		OperationData data = RadiusCopyPacketParser.getResponseInstance().parse( "*" , "0:1", srcKey, null, null);
		Key<?> parsedSourceKey = data.getAttributeMapping().getSourceKey();
		ReflectionAssert.assertReflectionEquals(expectedSoruceKey, 
				parsedSourceKey, ReflectionComparatorMode.LENIENT_ORDER);
	}
	
	public static Object[][] dataProviderFor_test_SourceKey_ResponseSpecific() throws InvalidExpressionException{
		return new Object[][]{
				{"\"staticValue\"", 
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"staticValue\""))},
					
				{"\"This is spaced Value\"", 
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"This is spaced Value\""))},
					
				{"\"This is \\\"quoted\\\" string\"", 
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"This is \\\"quoted\\\" string\""))},
				{"10415:3",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("10415:3"))},
				{" 10415:3 ",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("10415:3"))},
				{"10415:3:1",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("10415:3:1"))},
				{"10415:3:1:2",
					new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("10415:3:1:2"))},
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
					
				{"{ 'with' : '0:1' , 'when' : '0:1 = \"*@elitecore.com\"' } ",
					new NonGroupedKey("0:1", 
							Compiler.getDefaultCompiler().parseLogicalExpression("0:1 = \"*@elitecore.com\""), 
							Compiler.getDefaultCompiler().parseExpression(CopyPacketParser.THIS))},
							
				{"{ 'with' : '0:1' , 'when' : '0:1 = \"*@elitecore.com\"' , 'do': '\"elitecore.com\"'} ",
					new NonGroupedKey("0:1", 
							Compiler.getDefaultCompiler().parseLogicalExpression("0:1 = \"*@elitecore.com\""), 
							Compiler.getDefaultCompiler().parseExpression("\"elitecore.com\""))},
							
				{"{ 'with' : '0:1' , 'do': '\"elitecore.com\"'} ",
					new NonGroupedKey("0:1", null, 
							Compiler.getDefaultCompiler().parseExpression("\"elitecore.com\""))},
				
				{"{ '12067:145' : { 'with' : '12067:145' , 'when' : 'this:3 = \"1\"' , 'do' : 'this:3' }," +
				 " 	'10415:3' : { 'with' : '10415:3' , 'when' : 'this:1 = \"19118817838453\"' , 'do' : 'this:1'}}",
				 	new GroupedKey(createMappingList(
						 new AttributeMapping(
								 new NonGroupedKey("12067:145", Compiler.getDefaultCompiler().parseLogicalExpression("this:3 = \"1\""), Compiler.getDefaultCompiler().parseExpression("this:3")),
								 new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"12067:145\""))),
						 new AttributeMapping(
								 new NonGroupedKey("10415:3", Compiler.getDefaultCompiler().parseLogicalExpression("this:1 = \"19118817838453\""), Compiler.getDefaultCompiler().parseExpression("this:1")),
								 new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"10415:3\""))) )) },
				 
				{"{ 'with' : '12067:145' ; 'when' : 'this:3 = \"1\"' ; 'do' : { '0:2' : 'this:3' , '0:3' : 'this:1'} }", 
					new GroupedKey("12067:145", Compiler.getDefaultCompiler().parseLogicalExpression("this:3 = \"1\""),
						createMappingList(
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:3")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:1")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:3\""))) ))},
					
				{"{ 'with' : '12067:145' ; 'do' : {'0:8':'this:3'; '0:2':'0:3'; '0:9': { '0:5':'this:1' } } }",
					new GroupedKey( "12067:145" , null,
						createMappingList(
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:3")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\""))),
							new AttributeMapping(
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:3")), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
							new AttributeMapping(
									new GroupedKey(createMappingList(
											new AttributeMapping (
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:1")),
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:5\"")) ))), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:9\""))) ))},

				{"${SRCREQ}:10415:3",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{SRCREQ\\}:10415:3"), null, null, 
							createKeyWordMap(new String[]{ "${SRCREQ}:10415:3"} , 
									new KeywordValueProvider[]{new RadiusServiceRequestKeywordValueProvider("10415:3", AAATranslatorConstants.SOURCE_REQUEST)}) )},
				
				{"${SRCREQ}:10415:22.mnc",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{SRCREQ\\}:10415:22.mnc"), null, null, 
							createKeyWordMap(new String[]{ "${SRCREQ}:10415:22.mnc"} , 
									new KeywordValueProvider[]{new RadiusServiceRequestKeywordValueProvider("10415:22.mnc", AAATranslatorConstants.SOURCE_REQUEST)}) )},
														
				{"${SRCREQ}:12067:145:3",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{SRCREQ\\}:12067:145:3"), null, null, 
							createKeyWordMap(new String[]{ "${SRCREQ}:12067:145:3"} , 
									new KeywordValueProvider[]{new RadiusServiceRequestKeywordValueProvider("12067:145:3", AAATranslatorConstants.SOURCE_REQUEST)}) )},
					
				{"concat ( ${SRCREQ}:0:1 , ${DSTREQ}:0:2)",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("concat ( \\$\\{SRCREQ\\}:0:1 , \\$\\{DSTREQ\\}:0:2 )"), null, null, 
							createKeyWordMap(new String[]{ "${SRCREQ}:0:1" , "${DSTREQ}:0:2"} , 
									new KeywordValueProvider[]{
										new RadiusServiceRequestKeywordValueProvider("0:1", AAATranslatorConstants.SOURCE_REQUEST), 
										new RadiusPacketKeywordValueProvider("0:2", AAATranslatorConstants.DESTINATION_REQUEST)}) )},

				{"{'0:1':'${SRCREQ}:0:4';" +
					"'0:2':'0:3';" +
					"'0:9':{" +
						"'0:5':'${DSTREQ}:0:6'" +
						"}" +
					"}",
					new GroupedKey(createMappingList(
						new AttributeMapping(
							new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{SRCREQ\\}:0:4"), null, null, 
								createKeyWordMap(new String[]{ "${SRCREQ}:0:4"} , new KeywordValueProvider[]{new RadiusServiceRequestKeywordValueProvider("0:4", AAATranslatorConstants.SOURCE_REQUEST)}) ), 
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:1\""))),
						new AttributeMapping(
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("0:3")), 
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:2\""))),
						new AttributeMapping(
							new GroupedKey(createMappingList(
									new AttributeMapping (
											new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{DSTREQ\\}:0:6"), null, null, 
													createKeyWordMap(new String[]{ "${DSTREQ}:0:6"} , new KeywordValueProvider[]{new RadiusPacketKeywordValueProvider("0:6", AAATranslatorConstants.DESTINATION_REQUEST)}) ),
											new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:5\"")) ))), 
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:9\""))) ))},
					
				{"{ 'with' : '10415:3' ; 'when' : 'subString(this:1, \"2\") = \"1\"' ; 'do' : { '0:8': { 'with' : '21067:145' , 'when' : 'this:1 = \"19118817838453\"' , 'do' : { '0:9' : '${SRCREQ}:0:10' , '0:11' : 'this:3' } } } }",
					new GroupedKey( "10415:3" , Compiler.getDefaultCompiler().parseLogicalExpression("subString(this:1, \"2\") = \"1\""),
						createMappingList(
								new AttributeMapping(
									new GroupedKey("21067:145" , Compiler.getDefaultCompiler().parseLogicalExpression("this:1 = \"19118817838453\""),
											createMappingList(
												new AttributeMapping (
													new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("\\$\\{SRCREQ\\}:0:10"), null, null,
															createKeyWordMap(new String[]{ "${SRCREQ}:0:10"} , new KeywordValueProvider[]{new RadiusServiceRequestKeywordValueProvider("0:10", AAATranslatorConstants.SOURCE_REQUEST)})),
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:9\"")) ),
												new AttributeMapping (
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:3")),
													new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:11\"")) ))), 
									new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\""))) ))},
									
				{"{'with' = '10415:145' , 'do' = { '10415:3' = 'this:3'}}", 
						new GroupedKey("10415:145", null, 
								createMappingList(
									new AttributeMapping(
											new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("this:3")), 
											new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"10415:3\""))))) },
											
											
				{
					"${SRCRES}:0:1",
					new NonGroupedKey(null,null, Compiler.getDefaultCompiler().parseExpression("\\$\\{SRCRES\\}:0:1"), null, null,
							createKeyWordMap(new String[] {"${SRCRES}:0:1"}, 
							new KeywordValueProvider[] {new RadiusSrcResKeyworValueProvider("0:1")})
						)
				},
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

		OperationData parsedOperationData = RadiusCopyPacketParser.getRequestInstance().parse( "*" , "0:1", srcMapping, defValue, valueMapping);
		
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

				{"{ 'with' : '10415:145' ; 'when' : 'this:1 = \"1\"' ; 'do' : { '0:8': { 'with' : '0:10' ; 'when' : 'this = \"*@com\"' } ; '0:11' : 'this:3'} } }" , 
					"0:10 = defaultValue , this:3 = defaultValue2" , null ,
					new GroupedKey( "10415:145" , Compiler.getDefaultCompiler().parseLogicalExpression("this:1 = \"1\""),
						createMappingList(
						  new AttributeMapping(
							new NonGroupedKey("0:10", Compiler.getDefaultCompiler().parseLogicalExpression("this = \"*@com\""), Compiler.getDefaultCompiler().parseExpression("this"), "defaultValue", null),
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\"")) ) ,
						  new AttributeMapping(
							new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("this:3"), "defaultValue2", null),
							new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:11\"")) ) ))},
							
				{"{ 'with' : '0:10' ; 'when' : 'this = \"*@com\"' }", "defaultValue", null,
					new NonGroupedKey("0:10", Compiler.getDefaultCompiler().parseLogicalExpression("this = \"*@com\""), 
						Compiler.getDefaultCompiler().parseExpression("this"), "defaultValue", null)},
						
				{"0:2" , null, "2001 = success , 3001 = failure",
					new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("0:2"), null, 
						buildValueMap(new String[] {"2001" , "success"} , new String[] {"3001" , "failure"} ) )},
				
				{"{ 'with' : '10415:145' ; 'when' : 'this:1 = \"1\"' ; 'do' : { '0:8': { 'with' : '0:10' ; 'when' : 'this = \"*@com\"' } ; '0:11' : 'this:3'} } }" , 
					null, "0:10.2001 = success , 0:10.3001 = failure , this:3.abc = pqr , this:3.mno = xyz",
					new GroupedKey("10415:145" , Compiler.getDefaultCompiler().parseLogicalExpression("this:1 = \"1\""),
						createMappingList(
							  new AttributeMapping(
								new NonGroupedKey("0:10", Compiler.getDefaultCompiler().parseLogicalExpression("this = \"*@com\""), Compiler.getDefaultCompiler().parseExpression("this"), null, buildValueMap(new String[] {"2001" , "success"} , new String[] {"3001" , "failure"} )),
								new NonGroupedKey(Compiler.getDefaultCompiler().parseExpression("\"0:8\"")) ) ,
							  new AttributeMapping(
								new NonGroupedKey(null, null, Compiler.getDefaultCompiler().parseExpression("this:3"), null, buildValueMap(new String[] {"abc" , "pqr"} , new String[] {"mno" , "xyz"} )),
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
