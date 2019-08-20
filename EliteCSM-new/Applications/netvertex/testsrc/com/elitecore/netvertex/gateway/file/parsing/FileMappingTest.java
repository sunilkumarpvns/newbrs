package com.elitecore.netvertex.gateway.file.parsing;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import com.elitecore.commons.tests.PrintMethodRule;

public class FileMappingTest {

	private static final String EMPTY_STRING = "";

	private FileMapping fileMapping;
	
	@Rule public PrintMethodRule printMethodRule = new PrintMethodRule();

	@Before
	public void setUp() {
		fileMapping = new FileMapping();
	}

	@Test
	public void commaAndSemicolonAreValidSeparators() {
		String valueMapping = "1=Domestic;2=International,3=Interconnect";

		fileMapping.setKeyValueMapping(valueMapping);

		assertThat(fileMapping.getValue("1"), is(equalTo("Domestic")));
		assertThat(fileMapping.getValue("2"), is(equalTo("International")));
		assertThat(fileMapping.getValue("3"), is(equalTo("Interconnect")));
	}

	@Test
	public void doesNotApplyValueMappingAndValueRemainsUnchangedIfKeyValueSeparatorOtherThanEqualTo() {

		String valueMapping = "1-Domestic,2-International";

		fileMapping.setKeyValueMapping(valueMapping);

		assertThat(fileMapping.getValue("1"), is(equalTo("1")));
		assertThat(fileMapping.getValue("2"), is(equalTo("2")));
	}

	@Test
	public void valueRemainsUnchangedIfValueForAKeyIsNotProvidedOrBlank() {

		String valueMapping = "1=";

		fileMapping.setKeyValueMapping(valueMapping);

		assertThat(fileMapping.getValue("1"), is(equalTo("1")));
	}
	
	@Test
	public void valueRemainsUnchangedIfKeyIsUnknown() {

		String valueMapping = "1=Domestic";

		fileMapping.setKeyValueMapping(valueMapping);

		assertThat(fileMapping.getValue("2"), is(equalTo("2")));
	}
	
	@Test
	public void skipsExtraSeparatorsInValueMapping() {
		
		String valueMapping = "1=Domestic,,2=International;;3=Interconnect";
		
		fileMapping.setKeyValueMapping(valueMapping);
		
		assertThat(fileMapping.getValue("1") , is(equalTo("Domestic")));
		assertThat(fileMapping.getValue("2") , is(equalTo("International")));
		assertThat(fileMapping.getValue("3") , is(equalTo("Interconnect")));
		
	}
	
	@Test
	public void valueRemainsUnchangedIfGivenInputValueIsNullOrBlank() {
		
		String valueMapping = "1=Domestic";
		String inValue = EMPTY_STRING;
		
		fileMapping.setKeyValueMapping(valueMapping);
	
		assertThat(fileMapping.getValue(inValue) , is(equalTo(EMPTY_STRING)));
	}
}
