package com.elitecore.aaa.core.scripts;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.aaa.core.scripts.conf.ScriptConfigurable.ScriptType;

@XmlRootElement(name="script-data")
public class ScriptDetail {

	private String name;
	private ScriptType type;
	private String description;
	private String status;
	private Map<String, File> filenameToFile = new HashMap<String, File>();

	@XmlElement(name = "name")
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	@XmlElement(name = "type")
	public ScriptType getType() {
		return type;
	}

	public void setType(ScriptType type) {
		this.type = type;
	}

	@XmlElement(name = "description")
	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@XmlElement(name = "status")
	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}

	@XmlElementWrapper(name ="script-files-map")
	public Map<String, File> getFilenameToFile() {
		return filenameToFile;
	}

	public void setFilenameToFile(Map<String, File> filenameToFile) {
		this.filenameToFile = filenameToFile;
	}




}
