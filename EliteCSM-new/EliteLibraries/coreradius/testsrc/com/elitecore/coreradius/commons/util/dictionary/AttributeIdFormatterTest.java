package com.elitecore.coreradius.commons.util.dictionary;

import static org.junit.Assert.assertEquals;

import java.util.Arrays;
import java.util.List;

import junitparams.JUnitParamsRunner;
import junitparams.Parameters;

import org.junit.Test;
import org.junit.runner.RunWith;

import com.elitecore.coreradius.commons.util.dictionary.AttributeIdFormatter;
import com.elitecore.coreradius.commons.util.dictionary.AttributeModel;

/**
 * 
 * @author narendra.pathai
 *
 */
@RunWith(JUnitParamsRunner.class)
public class AttributeIdFormatterTest {

	@Test(expected = NullPointerException.class)
	public void testConstructor_ShouldThrowNPE_WhenAttributeModelIsNull(){
		new AttributeIdFormatter(null);
	}

	@Test
	@Parameters(method = "dataForAttributeIdFormatting")
	public void testFormatId_ShouldReturnIdOfItsAttributeModel_IfItHasNoParentAttributeModel(AttributeModel attributeModel, List<Integer> expectedId, String expectedIdString){
		List<Integer> formatId = new AttributeIdFormatter(attributeModel).formatId();
		assertEquals(expectedId, formatId);
	}
	
	@Test
	@Parameters(method = "dataForAttributeIdFormatting")
	public void testFormatIdAsString_ShouldReturnIdOfItsAttributeModel_IfItHasNoParentAttributeModel(AttributeModel attributeModel, List<Integer> expectedId, String expectedIdString){
		String formatIdString = new AttributeIdFormatter(attributeModel).formatIdAsString();
		assertEquals(expectedIdString, formatIdString);
	}
	


	public Object[][] dataForAttributeIdFormatting(){
		AttributeModel attributeModel = new AttributeModel();
		attributeModel.setId(2);
		
		AttributeModel subAttributeModel = new AttributeModel();
		subAttributeModel.setId(1);
		attributeModel.getSubAttributes().add(subAttributeModel);

		AttributeModel subSubAttributeModel = new AttributeModel();
		subSubAttributeModel.setId(4);
		subAttributeModel.getSubAttributes().add(subSubAttributeModel);
		attributeModel.postRead();	

		
		return new Object[][]{
				{attributeModel, Arrays.asList(2), "2"},
				{subAttributeModel, Arrays.asList(2,1), "2:1"},
				{subSubAttributeModel, Arrays.asList(2,1,4), "2:1:4"}
		};
	}
}
