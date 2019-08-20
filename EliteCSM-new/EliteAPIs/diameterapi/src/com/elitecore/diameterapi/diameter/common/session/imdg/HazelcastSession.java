package com.elitecore.diameterapi.diameter.common.session.imdg;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;

import com.elitecore.commons.base.TimeSource;
import com.elitecore.diameterapi.core.common.session.SessionEventListener;
import com.elitecore.diameterapi.core.common.session.SessionState;
import com.elitecore.diameterapi.diameter.common.session.DiameterSession;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

/**
 * @author malav
 *
 */
public class HazelcastSession extends DiameterSession implements IdentifiedDataSerializable {

	public static final int SERIALIZATION_ID = 2;

	public HazelcastSession(String sessionId, SessionEventListener eventListener) {
		this(sessionId, eventListener, TimeSource.systemTimeSource());
	}
	
	public HazelcastSession(String sessionId, SessionEventListener eventListener, TimeSource timeSource) {
		super(sessionId, eventListener, timeSource);
	}
	
	@Override
	public void readData(ObjectDataInput input) throws IOException {
		setSessionId(input.readUTF());
		setCreationTime(input.readLong());
		setSessionState(SessionState.valueOf(input.readUTF()));
		int size = input.readInt();
		for (int i=0; i<size; i++) {
			setParameter(input.readUTF(), input.readObject());
		}
		// above setter may or may not change the last accessed time value 
		// so reading it last to set the original last accessed time value
		setLastAccessedTime(input.readLong());
	}

	@Override
	public void writeData(ObjectDataOutput output) throws IOException {
		output.writeUTF(getSessionId());
		output.writeLong(getCreationTime());
		output.writeUTF(getSessionState().name());
		Map<String, Object> parameters = getParameters();
		output.writeInt(parameters.size());
		for (Entry<String, Object> param : parameters.entrySet()) {
			output.writeUTF(param.getKey());
			output.writeObject(param.getValue());
		}
		// above getters may or may not change the last accessed time value so writing it at last 
		output.writeLong(getLastAccessedTime());
	}

	@Override
	public int getFactoryId() {
		return HazelcastSessionSerializationFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return SERIALIZATION_ID;
	}
	
}
