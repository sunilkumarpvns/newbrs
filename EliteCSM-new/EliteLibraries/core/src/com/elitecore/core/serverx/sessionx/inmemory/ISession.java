package com.elitecore.core.serverx.sessionx.inmemory;
import com.elitecore.core.commons.data.ValueProvider;

public interface ISession {

	public static final ISession NO_SESSION = new ISession() {

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

		}

		@Override
		public String getSessionId() {
			return null;
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

	public abstract String getSessionId();

	public abstract long getCreationTime();

	public abstract long getLastAccessedTime();

	public Object setParameter(String key, Object parameterValue);

	public Object getParameter(String str);

	public Object removeParameter(String key);

	public void update(ValueProvider valueProvider);

	void release();

}
