package com.elitecore.coreradius.commons.util.dictionary;

import java.util.ArrayList;
import java.util.List;

import com.elitecore.coreradius.commons.util.RadiusUtility;

/**
 * 
 * @author narendra.pathai
 *
 */
//FIXME the attribute ID formatter does not know about vendor so will not be able to generate vendor id in format
public class AttributeIdFormatter{

	private final AttributeModel attributeModel;
	public AttributeIdFormatter(AttributeModel attributeModel) {
		RadiusUtility.checkNotNull(attributeModel, "attributeModel is null");
		this.attributeModel = attributeModel;
	}

	public List<Integer> formatId() {
		ArrayList<Integer> ids = new ArrayList<Integer>(); 
		fillIds(attributeModel, ids);
		return ids;
	}

	private void fillIds(AttributeModel model, ArrayList<Integer> ids) {
		if(model.getParent() != null){
			fillIds(model.getParent(), ids);
		}
		
		ids.add(model.getId());
	}

	public String formatIdAsString() {
		return RadiusUtility.getDelimiterSeparatedString(formatId(), ":");
	}
}
