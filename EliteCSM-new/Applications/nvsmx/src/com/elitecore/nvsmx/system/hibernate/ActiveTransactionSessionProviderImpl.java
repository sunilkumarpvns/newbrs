package com.elitecore.nvsmx.system.hibernate;

import com.elitecore.corenetvertex.util.SessionProvider;
import org.hibernate.Session;

public class ActiveTransactionSessionProviderImpl implements SessionProvider{

	@Override
	public Session getSession() {
		Session session = HibernateSessionFactory.getSession();
		if( session.getTransaction().isActive() == false){
			session.beginTransaction();
		}
		return session;
	}


	
}
