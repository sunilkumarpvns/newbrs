package com.brs.userservicemanagement.dao;

import org.apache.log4j.Logger;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.brs.userservicemanagement.entity.LoginDetails;
import com.brs.userservicemanagement.entity.UserPasswordLinks;

@Repository
public class ForgotPasswordDAOImpl implements ForgotPasswordDAO {
	private static final Logger logger=Logger.getLogger(ForgotPasswordDAOImpl.class);
	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public LoginDetails findByEmail(String email) {
		Session session=sessionFactory.openSession();
		String hql="from "
				+ " com.brs.userservicemanagement.entity.LoginDetails "
				+ "as login  where login.email=:p1";
		Query query=session.createQuery(hql);
		query.setParameter("p1", email);
		LoginDetails ldetails=(LoginDetails) query.uniqueResult();
		
		session.close();
		return ldetails;
	}

	@Override
	public Integer saveDynamicURL(UserPasswordLinks userPasswordLinks) {
		Integer userPwdLinkId=null;
		Session session=null;
		Transaction transaction=null;
		try{
			session=sessionFactory.openSession();
			logger .info("session opened");
			if(session!=null){
				transaction=session.beginTransaction();
				logger.info("transaction begins");
				userPwdLinkId=(Integer)session.save(userPasswordLinks);
				if(transaction!=null && userPwdLinkId!=null){
					transaction.commit();
					logger.info("transaction commited");
				}
			}
		}catch(HibernateException he){
			if(transaction!=null){
				transaction.rollback();
				logger.info("transaction roolbacks");
			}
		throw he;
		}finally{
			if(session!=null){
				session.close();
			}
		}
		return userPwdLinkId;
	}
	
	@Override
	public UserPasswordLinks verifyDynamicURL(String verificationURL){
		Session session = sessionFactory.openSession();
		String hql =" from com.brs.userservicemanagement.entity.UserPasswordLinks as userpasswordlinks"
				    +" where userpasswordlinks.dynamicUrl=:p1";
		Query query=session.createQuery(hql);
		query.setParameter("p1", verificationURL);
		UserPasswordLinks uplinks=(UserPasswordLinks)query.uniqueResult();
		
		session.close();
		return uplinks;
	}

}
