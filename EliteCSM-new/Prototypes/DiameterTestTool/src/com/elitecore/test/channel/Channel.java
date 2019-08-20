package com.elitecore.test.channel;

import com.elitecore.test.dependecy.diameter.packet.DiameterPacket;
import com.elitecore.test.exception.ChennelClosedException;
import com.elitecore.test.util.EventObserver;

public interface Channel {
	String getName();
	void write(DiameterPacket diameterPacket) throws ChennelClosedException;
	void registerEventObserver(EventObserver<String, DiameterPacket> eventObserver);
	public void open() throws Exception;
	public void close() throws Exception;
	public boolean isOpen();
	public boolean isClose();
	
	static final String RECEIVE_PACKET = "RCVD-PCKT";

	void registerChnnelEventListener(ChannelEventListener event);

}
