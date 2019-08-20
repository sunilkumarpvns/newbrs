package com.elitecore.netvertex.gateway.diameter.utility.data;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="result-code-mapping")
public class ResultCodeMappingData {

	private List<ResultCodeEntryData> resultCodeEntryDatas;

	public ResultCodeMappingData() {
		this.resultCodeEntryDatas = new ArrayList<>();
	}

	@XmlElementWrapper(name="result-code-list")
	@XmlElement(name="result-code")
	public List<ResultCodeEntryData> getResultCodeEntryDatas() {
		return resultCodeEntryDatas;
	}

	public void setResultCodeEntryDatas(List<ResultCodeEntryData> resultCodeEntryDatas) {
		this.resultCodeEntryDatas = resultCodeEntryDatas;
	}
}
