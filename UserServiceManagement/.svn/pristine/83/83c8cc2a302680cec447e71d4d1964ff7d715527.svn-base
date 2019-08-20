package com.brs.userservicemanagement.dao;

import org.bouncycastle.jcajce.provider.asymmetric.dsa.DSASigner.stdDSA;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.brs.userservicemanagement.entity.PassengerProfile;


@Repository
public class PassengerProfileDAOImpl 
implements PassengerProfileDAO{
	@Autowired
private SessionFactory sessionFactory;
	@Override
	public Long registerPassengerProfile(PassengerProfile passengerProfile) {
	Long userId=null;
	Session session=null;
	Transaction transaction=null;
	try{
	session=sessionFactory.openSession();
	if(session!=null){
		transaction=session.beginTransaction();
	userId=(Long)session.save(passengerProfile);
	if(transaction!=null && userId!=null){
		transaction.commit();
	}
	}
	}catch(HibernateException he){
		if(transaction!=null){
			transaction.rollback();
		}
	throw he;	
	}finally{
		if(session!=null){
			session.close();
		}
	}
		return userId;
	}
	
	@Override
	public Object[] getPassengerProfile(Long userId) {
		Object[] obj=null;
	String hql="select p.userId,p.firstName,p.lastName,p.dob,p.gender from com.brs.userservicemanagement.entity.PassengerProfile as p where p.userId=?";
	Session  session=sessionFactory.openSession();
     Query query=session.createQuery(hql);
      query.setParameter(0,userId);
      obj=(Object[])query.uniqueResult();
      session.close();
      return obj;
	}//method

}
