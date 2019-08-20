package com.elitecore.test.command.data;

import java.util.concurrent.TimeUnit;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.test.ScenarioContext;
import com.elitecore.test.command.Command;
import com.elitecore.test.command.WaitCommand;

@XmlRootElement(name = "wait")
public class WaitCommandData implements CommandData {
	
	private long duration;
	private TimeUnit timeUnit = TimeUnit.SECONDS;
	private String colsoleResponse;
	private String name="no-name";
	
	
	@Override
	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	@XmlAttribute(name = "duration",required=true)
	public long getDuration() {
		return duration;
	}



	public void setDuration(long duration) {
		this.duration = duration;
	}


	@XmlAttribute(name = "time-unit",required=true)
	public TimeUnit getTimeUnit() {
		return timeUnit;
	}



	public void setTimeUnit(TimeUnit timeUnit) {
		this.timeUnit = timeUnit;
	}



	@Override
	public Command create(ScenarioContext context) throws Exception {
		if(colsoleResponse != null) {
			return new WaitCommand(colsoleResponse,name);
		} else{
			return new WaitCommand(duration,timeUnit,name);
		}
	}

	@XmlAttribute(name = "cosoleResponse",required=true)
	public String getColsoleResponse() {
		return colsoleResponse;
	}

	public void setColsoleResponse(String colsoleResponse) {
		this.colsoleResponse = colsoleResponse;
	}
}