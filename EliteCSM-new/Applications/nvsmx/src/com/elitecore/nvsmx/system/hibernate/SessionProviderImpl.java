package com.elitecore.nvsmx.system.hibernate;

import org.hibernate.Session;

import com.elitecore.corenetvertex.util.SessionProvider;

public class SessionProviderImpl implements SessionProvider{

	@Override
	public Session getSession() {
		return HibernateSessionFactory.getSession();
	}
	
}
