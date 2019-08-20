package com.elitecore.commons.base;

import static junitparams.JUnitParamsRunner.$;
import static org.junit.Assert.assertArrayEquals;

import java.util.Collection;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.junit.runner.RunWith;

@RunWith(JUnitParamsRunner.class)
public class SplitterTest {

	private static final String SOME_STRING = "";
	@Rule public ExpectedException exception = ExpectedException.none();
	
	@Test
	@Parameters(method = "dataFor_testSplit_With_PreserveTokens_TrimTokens")
	public void testSplit_With_PreserveTokens_TrimTokens(String str, char separator, boolean preserveAllTokens, boolean trimTokens, String[] expecteds){
		Splitter splitter = Splitter.on(separator);
		if(preserveAllTokens){
			splitter = splitter.preserveTokens();
		}
		if(trimTokens){
			splitter = splitter.trimTokens();
		}
		assertArrayEquals(expecteds, splitter.split(str).toArray());
	}
	
	@Test
	@Parameters(method = "dataFor_testSplit_With_PreserveTokens_TrimTokens")
	public void testSplitAsArray_With_PreserveTokens_TrimTokens(String str, char separator, boolean preserveAllTokens, boolean trimTokens, String[] expecteds){
		Splitter splitter = Splitter.on(separator);
		if(preserveAllTokens){
			splitter = splitter.preserveTokens();
		}
		if(trimTokens){
			splitter = splitter.trimTokens();
		}
		assertArrayEquals(expecteds, splitter.split(str).toArray());
	}
	
	public Object[] dataFor_testSplit_With_PreserveTokens_TrimTokens(){
		return $(
		//		input   |separator |preserve| trim | expected
		//		String  |          | Tokens |Tokens|  Tokens   
				$(null,        ',', true,     false, new String[]{}),
				
				$("",          ',', true,     false, new String[]{}),
				$("",          ',', true,     true,  new String[]{}),
				$("",          ',', false,    true,  new String[]{}),
				$("",          ',', false,    false, new String[]{}),
				
				$(" ",         ',', true,     false, new String[]{" "}),
				$(" ",         ',', true,     true,  new String[]{}),
				$(" ",         ',', false,    false, new String[]{" "}),
				$(" ",         ',', false,    true,  new String[]{}),
				
				$(",,",        ',', true,     false, new String[]{"", "" , ""}),
				$(",,",        ',', true,     true,  new String[]{"", "", ""}),
				$(",,",        ',', false,    false, new String[]{}),
				$(",,",        ',', false,    true,  new String[]{}),
				
				$("\n,\t,\r,\f",',', false,   true, new String[]{}),
				
				$(" , , ",     ',', true,     false, new String[]{" ", " " , " "}),
				$(" , , ",     ',', true,     true,  new String[]{"", "", ""}),
				$(" , , ",     ',', false,    false, new String[]{" ", " " , " "}),
				$(" , , ",     ',', false,    true,  new String[]{}),
				
				$("a",         ',', true,     false, new String[]{"a"}),
				$(" a ",       ',', true,     true,  new String[]{"a"}),
				
				$("a,",         ',', true,    false, new String[]{"a", ""}),
				$(" a ,",       ',', true,    true,  new String[]{"a", ""}),
				
				$("a,",         ',', false,   false, new String[]{"a"}),
				$(" a ,",       ',', false,   true,  new String[]{"a"}),
				
				$("a,b",       ',', true,     false, new String[]{"a", "b"}),
				$(" a , b",    ',', true,     true,  new String[]{"a", "b"}),
				
				$(" a , b",    ',', true,  false, new String[]{" a ", " b"}),
				
				$("a",         ',', true,  true,  new String[]{"a"}),
				$(" a ",       ',', true,  false, new String[]{" a "}),
				
				$("a,,b",      ',', true,  true,  new String[]{"a", "", "b"}),
				$("a,,b",      ',', false, true,  new String[]{"a", "b"}),
				$("a,,b",      ',', true,  false, new String[]{"a", "", "b"}),
				$("a,,b",      ',', false, false, new String[]{"a", "b"}),
				
				$(" a , , b ", ',', true,  true,  new String[]{"a", "", "b"}),
				$(" a , , b ", ',', false, true,  new String[]{"a", "b"}),
				$(" a , , b ", ',', true,  false, new String[]{" a ", " ", " b "}),
				$(" a , , b ", ',', false, false, new String[]{" a ", " ", " b "})
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testSplit")
	public void testSplitToArray(String str, char separator, String[] expecteds){
		assertArrayEquals(expecteds, Splitter.on(separator).splitToArray(str));
	}
	
	@Test
	@Parameters(method = "dataFor_testSplit")
	public void testSplit(String str, char separator, String[] expecteds){
		assertArrayEquals(expecteds, Splitter.on(separator).split(str).toArray());
	}
	
	public Object[] dataFor_testSplit(){
		return $(
		//		input   |separator | expected
		//		String  |          |  Tokens   
				$(null,         ',',  new String[]{}),
				
				$("",           ',', new String[]{}),
				$(" ",          ',', new String[]{" "}),
				$(",,",         ',', new String[]{}),
				$("\n,\t,\r,\f",',', new String[]{"\n","\t","\r","\f"}),
				$(" , , ",      ',',  new String[]{" ", " " , " "}),
				
				$("a",          ',',  new String[]{"a"}),
				$("a,b",        ',',  new String[]{"a", "b"}),
				$("a,,b",       ',',  new String[]{"a", "b"}),
				$("a:b:c",      '.',  new String[]{"a:b:c"})
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testSplit_with_preserveTokens")
	public void testSplit_with_preserveTokens(String str, char separator, String[] expecteds){
		assertArrayEquals(expecteds, 
				Splitter.on(separator).preserveTokens()
				.split(str).toArray());
	}
	
	public Object[] dataFor_testSplit_with_preserveTokens(){
		return $(
		//		input   |separator  | expected
		//		String  |           |  Tokens   
				$(null,        ',',  new String[]{}),
				$("",          ',',  new String[]{}),
				$(" ",         ',',  new String[]{" "}),
				$(",,",        ',',  new String[]{"", "" , ""}),
				$(" , , ",     ',',  new String[]{" ", " " , " "}),
				$("a",         ',',  new String[]{"a"}),
				$("a,",        ',',  new String[]{"a", ""}),
				$("a,b",       ',',  new String[]{"a", "b"}),
				$("a,,b",      ',',  new String[]{"a", "", "b"})
		);
	}
	
	@Test
	@Parameters(method = "dataFor_testSplit_with_TrimTokens")
	public void testSplit_with_TrimTokens(String str, char separator, String[] expecteds){
		assertArrayEquals(expecteds, 
				Splitter.on(separator).trimTokens()
				.split(str).toArray());
	}
	
	public Object[] dataFor_testSplit_with_TrimTokens(){
		return $(
		//		input      |separator| expected
		//		String     |         |  Tokens   
				$(null,         ',',  new String[]{}),
				$("",           ',',  new String[]{}),
				$(" ",          ',',  new String[]{}),
				$("\n,\t,\r,\f",',',  new String[]{}),
				$(" , , ",      ',',  new String[]{}),
				$("a",          ',',  new String[]{"a"}),
				$(" a ",        ',',  new String[]{"a"}),
				$(" a , b ",        ',',  new String[]{"a" , "b"})
		);
	}
	
	@Test
	public void testSplit_On_NullInput_Gives_EmptyImmutableList_ShouldThrowUnsupportedOperationException_On_AddingElement(){
		exception.expect(UnsupportedOperationException.class);
		Collection<String> expecteds = Splitter.on('.').split(null);
		expecteds.add(SOME_STRING);
	}
	
	@Test
	public void testSplit_On_EmptyInput_Gives_EmptyImmutableList_ShouldThrowUnsupportedOperationException_On_AddingElement(){
		exception.expect(UnsupportedOperationException.class);
		Collection<String> expecteds = Splitter.on('.').split("");
		expecteds.add(SOME_STRING);
	}

}
