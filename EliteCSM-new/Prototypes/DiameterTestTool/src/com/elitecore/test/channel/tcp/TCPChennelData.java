package com.elitecore.test.channel.tcp;

import java.net.InetAddress;
import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;

import com.elitecore.test.EventData;
import com.elitecore.test.channel.ChannelData;

@XmlRootElement(name = "tcp")
public class TCPChennelData implements ChannelData {
	
	
	private String ipAddress = "127.0.0.1";
	private int port = 3868;
	private String name = "tcp-chennel";
	private List<EventData> eventDatas = new ArrayList<EventData>();
	private String action = "listen";
	
	@XmlAttribute(name="action")
	public String getAction() {
		return action;
	}

	public void setAction(String action) {
		this.action = action;
	}

	@XmlAttribute(name="ip")
	public String getIpAddress() {
		return ipAddress;
	}

	public void setIpAddress(String ipAddress) {
		this.ipAddress = ipAddress;
	}

	@XmlAttribute(name="port", required=true)
	public int getPort() {
		return port;
	}

	public void setPort(int port) {
		this.port = port;
	}

	@XmlAttribute(name="name", required=true)
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setEventDatas(List<EventData> eventDatas) {
		this.eventDatas = eventDatas;
	}

	@Override
	public TCPChennel create() throws Exception{
		return new TCPChennel(name,InetAddress.getByName(ipAddress), port, action);
	}

	@Override
	public List<EventData> getEventDatas() {
		return eventDatas;
	}

}
