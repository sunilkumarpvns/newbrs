package com.elitecore.nvsmx.system.util.migrate;

import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.commons.base.Collectionz;

@XmlRootElement(name="column-name-mapping")
public class ColumnNameMappingData {

	private List<MappingData> mappings;

	public ColumnNameMappingData() {
		this.mappings = Collectionz.newArrayList();
	}

	@XmlElement(name = "mapping")
	public List<MappingData> getMappings() {
		return mappings;
	}

	public void setMappings(List<MappingData> mappings) {
		this.mappings = mappings;
	}
}
