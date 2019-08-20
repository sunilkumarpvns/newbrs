package com.elitecore.test.channel;

import java.util.List;

import com.elitecore.test.EventData;

public interface ChannelData{
	public Channel create() throws Exception;
	public List<EventData> getEventDatas();
}
