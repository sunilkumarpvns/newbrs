package com.brs.userservicemanagement.dao;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.brs.userservicemanagement.entity.UserAuthorization;
@Repository
public class UserAuthorizationDAOImpl implements UserAuthorizationDAO{
	
	private static final String GET_AUTHORIZED_USER="SELECT ua.loginDetails.userId,ua.statusEnum FROM com.brs.userservicemanagement.entity.UserAuthorization AS ua WHERE ua.userAuthorizationId=:AuthId AND ua.token=:Token";
	@Autowired
	private SessionFactory sessionFactory;
	
	@Override
	public Integer saveUserToken(UserAuthorization userAuth) {
		Integer userAuthId=null;
		Session session=null;
		 Transaction tx=null;
		try{
		session=sessionFactory.openSession();
        tx=session.beginTransaction(); 
		userAuthId=(Integer)session.save(userAuth);
		if(tx!=null){
		tx.commit();
		}
		}catch(HibernateException he){
			if(tx!=null){
			tx.rollback();	
			}
		throw he;	
		}finally{
			if(session!=null){
				session.close();
			}
		}
		return userAuthId;
	}
	
	@Override
	public List<Object[]> getAuthorizedUser(Integer userAuthorizationId, String token) {
		Session session=null;
		Query query=null;
		List<Object[]> authDetails=null;
		try {
			if(sessionFactory!=null) {
				//get session
				session=sessionFactory.openSession();
			}//if
			if(session!=null) {
				//create query
				query=session.createQuery(GET_AUTHORIZED_USER);
			}//if
			if(query!=null) {
				//set query parameters
				query.setParameter("AuthId",userAuthorizationId);
				query.setParameter("Token",token);
				//execute query
				authDetails=query.list();
			}//if
		}
		finally {
			if(session!=null) {
				//close session
				session.close();
			}//if
		}//finally
		return authDetails;
	}//method

}


