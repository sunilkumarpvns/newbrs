package com.elitecore.aaa.radius.session.imdg;

import static com.elitecore.commons.logging.LogManager.getLogger;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.sql.Timestamp;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.ConcurrentHashMap;

import com.elitecore.aaa.core.conf.impl.ImdgIndexDetail;
import com.elitecore.aaa.core.conf.impl.RadiusSessionFieldMapping;
import com.elitecore.aaa.diameter.commons.AAATranslatorConstants;
import com.elitecore.aaa.radius.session.SessionEventListener;
import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.base.TimeSource;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.core.commons.data.ValueProvider;
import com.elitecore.core.commons.util.constants.CommonConstants;
import com.elitecore.core.serverx.sessionx.inmemory.ISession;
import com.elitecore.coreradius.commons.util.constants.RadiusPacketTypeConstant;
import com.elitecore.diameterapi.core.common.session.SessionState;
import com.hazelcast.nio.ObjectDataInput;
import com.hazelcast.nio.ObjectDataOutput;
import com.hazelcast.nio.serialization.IdentifiedDataSerializable;

/**
 * @author soniya
 *
 */
public class HazelcastRadiusSession implements ISession, IdentifiedDataSerializable {
	
	public static final ISession RAD_NO_SESSION = new ISession() {

		@Override
		public Object setParameter(String key, Object parameterValue) {
			return null;
		}

		@Override
		public Object removeParameter(String key) {
			return null;
		}

		@Override
		public void release() {
			//no-op
		}

		@Override
		public String getSessionId() {
			return "no-session-id";
		}

		@Override
		public Object getParameter(String str) {
			return null;
		}

		@Override
		public long getLastAccessedTime() {
			return 0;
		}

		@Override
		public long getCreationTime() {
			return 0;
		}

		@Override
		public void update(ValueProvider valueProvider) {
			// NO OP			
		}

	};

	private static final String MODULE = "RADIUS-SESSION";
	public static final String SESSION_RELEASE_KEY = "SESSION_RELEASE_KEY";
	public static final String LAST_ACCESSED_TIME_INDEX = "lastAccessedTime";
	
// below index variables are directly relevant to IndexField Enumerations defined in this class. So do not change them.
	private String index0;
	private String index1;
	private String index2;
	private String index3;
	private String index4;
	private String index5;
	
	public static final int SERIALIZATION_ID = 3;

	private String sessionId;
	private long creationTime;
	private final SessionEventListener sessionEventListener;
	private final ConcurrentHashMap<String, Object> parameters;
	private long lastAccessedTime;
	private volatile SessionState state;
	private TimeSource timeSource;
	private SessionStatus radiusSessionStatus;
	private List<RadiusSessionFieldMapping> sessionDataFieldMapping;

	
	private Map<String, ImdgIndexDetail> indexToIndexDetailMap;
	
	public HazelcastRadiusSession(String strSessionId, SessionEventListener hazelcastSessionFactory, List<ImdgIndexDetail> sessionIndexFieldMapping, List<RadiusSessionFieldMapping> sessionDataFieldMapping) {
		this(strSessionId, TimeSource.systemTimeSource(), hazelcastSessionFactory, sessionIndexFieldMapping, sessionDataFieldMapping);
	}

	public HazelcastRadiusSession(String strSessionId, TimeSource timeSource, SessionEventListener hazelcastSessionFactory, List<ImdgIndexDetail> sessionIndexFieldMapping, List<RadiusSessionFieldMapping> sessionDataFieldMapping) {
		this.sessionId = strSessionId;
		this.timeSource = timeSource;
		this.sessionEventListener = hazelcastSessionFactory;
		this.creationTime = timeSource.currentTimeInMillis();
		this.lastAccessedTime = creationTime;	
		this.parameters = new ConcurrentHashMap<>(8, CommonConstants.DEFAULT_LOAD_FACTOR, 4);
		this.state = SessionState.Ok; 
		this.indexToIndexDetailMap = new HashMap<>();
		for(ImdgIndexDetail detail : sessionIndexFieldMapping) {
			this.indexToIndexDetailMap.put(detail.getImdgIndex(), detail);
		}
		this.radiusSessionStatus = SessionStatus.INACTIVE;
		this.sessionDataFieldMapping = sessionDataFieldMapping;
	}

	public String getSessionId() {
		return sessionId;
	}

	public long getCreationTime() {
		return this.creationTime;
	}

	/**
	 * Storing or fetching any data/parameter from the session causes the access time to be refreshed.
	 */
	public long getLastAccessedTime() {
		return this.lastAccessedTime;
	}

	protected SessionState getSessionState() {
		return this.state;
	}

	protected boolean isReleased() {
		return state == SessionState.Released;
	}

	public void release() {
		if(state == SessionState.Released){
			if (getLogger().isLogLevel(LogLevel.INFO))
				getLogger().info(MODULE, "Can not remove Session: " + sessionId + " as session is already removed");
			return;
		}

		this.state = SessionState.Released;

		sessionEventListener.removeSession(this);

		if (getLogger().isLogLevel(LogLevel.INFO))
			getLogger().info(MODULE, "Session: " + sessionId + " created on: "
					+new Timestamp(creationTime) +" and last updated on: "+new Timestamp(lastAccessedTime)+" is removed");

	}

	public Object setParameter(String key, Object parameterValue){
		touch();
		return parameters.put(key, parameterValue);
	}

	public Object getParameter(String key){
		touch();
		return parameters.get(key);
	}

	@Override
	public Object removeParameter(String key){
		touch();
		return parameters.remove(key);
	}

	private void touch() {
		lastAccessedTime = timeSource.currentTimeInMillis(); 
	}

	public String toString() {
		StringWriter stringBuffer = new StringWriter();
		PrintWriter out = new PrintWriter(stringBuffer);
		out.println("Session Id: " + sessionId);
		out.println("Session create-time: " + creationTime);
		out.println("Session Last accessed time: " + lastAccessedTime);
		out.close();
		return stringBuffer.toString();
	}

	protected Map<String,Object> getParameters() {
		return this.parameters;
	}

	public void readData(ObjectDataInput input) throws IOException {
		this.sessionId = input.readUTF();
		this.creationTime = input.readLong();
		this.state = SessionState.valueOf(input.readUTF());
		int size = input.readInt();
		for (int i=0; i<size; i++) {
			setParameter(input.readUTF(), input.readObject());
		}
		// above setter may or may not change the last accessed time value 
		// so reading it last to set the original last accessed time value
		this.lastAccessedTime = input.readLong();
		this.index0 = input.readUTF();
		this.index1 = input.readUTF();
		this.index2 = input.readUTF();
		this.index3 = input.readUTF();
		this.index4 = input.readUTF();
		this.index5 = input.readUTF();
		this.radiusSessionStatus = SessionStatus.valueOf(input.readUTF());
	}

	@Override
	public void writeData(ObjectDataOutput output) throws IOException {
		output.writeUTF(getSessionId());
		output.writeLong(getCreationTime());
		output.writeUTF(getSessionState().name());
		Map<String, Object> sessionParameters = getParameters();
		output.writeInt(sessionParameters.size());
		for (Entry<String, Object> param : sessionParameters.entrySet()) {
			output.writeUTF(param.getKey());
			output.writeObject(param.getValue());
		}
		// above getters may or may not change the last accessed time value so writing it at last 
		output.writeLong(getLastAccessedTime());
		output.writeUTF(index0);
		output.writeUTF(index1);
		output.writeUTF(index2);
		output.writeUTF(index3);
		output.writeUTF(index4);
		output.writeUTF(index5);
		output.writeUTF(this.radiusSessionStatus.name());
	}

	@Override
	public int getFactoryId() {
		return HazelcastRadiusSessionSerializationFactory.FACTORY_ID;
	}

	@Override
	public int getId() {
		return SERIALIZATION_ID;
	}

	@Override
	public void update(ValueProvider valueProvider) {
		if (isReleased() == false) {                              //NOSONAR
			updateIndexField(valueProvider);
			updateSessionStatus(valueProvider);
			updateSessionData(valueProvider);
			sessionEventListener.update(this);
		}
	}

	private void updateSessionData(ValueProvider valueProvider) {
		String value;
		if(Collectionz.isNullOrEmpty(sessionDataFieldMapping)) {
			return;
		}
		for(RadiusSessionFieldMapping data : sessionDataFieldMapping) {
			if(data != null) {
				for(String attribute : data.getAttributeList()) {
					value = valueProvider.getStringValue(attribute);
					if(value != null) {
						this.setParameter(data.getFieldName(), value);
						break;
					}
				}
			}
		}
	}

	private void updateSessionStatus(ValueProvider valueProvider) {
		String packetType = valueProvider.getStringValue(AAATranslatorConstants.PACKET_TYPE);
		if (RadiusPacketTypeConstant.ACCESS_REQUEST.packetTypeId == Integer.valueOf(packetType) ) {
			this.radiusSessionStatus = SessionStatus.INACTIVE;
		} else {
			this.radiusSessionStatus = SessionStatus.ACTIVE;
		}
	}

	
	private void updateIndexField(ValueProvider valueProvider) {
		 index0 = getValue(valueProvider, IndexField.INDEX_0.getIndex());
		 index1 = getValue(valueProvider, IndexField.INDEX_1.getIndex());
		 index2 = getValue(valueProvider, IndexField.INDEX_2.getIndex());
		 index3 = getValue(valueProvider, IndexField.INDEX_3.getIndex());
		 index4 = getValue(valueProvider, IndexField.INDEX_4.getIndex());
		 index5 = getValue(valueProvider, IndexField.INDEX_5.getIndex());
	}
	
	private String getValue(ValueProvider valueProvider, String index) {
		ImdgIndexDetail indexDetail = indexToIndexDetailMap.get(index);
		if(indexDetail != null) {
			for (String id : indexDetail.getAttributeList()) {
				return valueProvider.getStringValue(id);
			}
		}
		return null;
	}
	
// below IndexField enums are directly relevant to index variables defined in this class. So do not change it. 
public enum IndexField {
	INDEX_0("index0"), 		// User-Name
	INDEX_1("index1"), 		// Framed-IP-Address
	INDEX_2("index2"), 		// CallingStation-Id
	INDEX_3("index3"),		// NAS-IP-Address
	INDEX_4("index4"),
	INDEX_5("index5");
	
	private String index;
	
	IndexField(String index) {
		this.index= index;
	}
	
	public String getIndex() {
		return this.index;
	}
}

public enum SessionStatus {
	ACTIVE, INACTIVE
}

public SessionStatus getSessionStatus() {
	return this.radiusSessionStatus;
}

}
