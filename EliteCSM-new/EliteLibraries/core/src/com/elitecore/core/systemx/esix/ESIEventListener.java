package com.elitecore.core.systemx.esix;

public interface ESIEventListener<T extends ESCommunicator> {
	void alive(T esCommunicator);
	void  dead(T esCommunicator);
}
