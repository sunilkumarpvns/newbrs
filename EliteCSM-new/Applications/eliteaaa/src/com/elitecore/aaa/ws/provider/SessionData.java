package com.elitecore.aaa.ws.provider;

import java.util.Collection;

import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElementWrapper;
import javax.xml.bind.annotation.XmlRootElement;

@XmlRootElement(name="session-details")
public class SessionData {
	private Collection<SessionDetails> session;

	@XmlElementWrapper(name="sessions")
	@XmlElement(name="session")
	public Collection<SessionDetails> getSession() {
		return session;
	}

	public void setSession(Collection<SessionDetails> session) {
		this.session = session;
	}
	
}
