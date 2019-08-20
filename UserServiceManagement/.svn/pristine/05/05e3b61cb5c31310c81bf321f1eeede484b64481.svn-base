package com.brs.userservicemanagement.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.brs.userservicemanagement.entity.UserOtp;

@Repository
public class UserOTPDAOImpl implements UserOTPDAO {

	@Autowired
	private SessionFactory sessionFactory;

	@Override
	public Integer saveOtp(UserOtp userOtp) {
		Integer userOtpId = null;
		Session session = null;
		Transaction tx = null;
		try {
			session = sessionFactory.openSession();
			if (session != null) {
				tx = session.beginTransaction();
				userOtpId = (Integer) session.save(userOtp);
				if (tx != null && userOtpId != null) {
					tx.commit();
				}
			}
		} catch (HibernateException he) {
			if (tx != null) {
				tx.rollback();
			}
			throw he;
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return userOtpId;
	}

	@Override
	public Integer 
	getOTPTimeDifferenceInMinutes(Long userId, String otp) {
	Integer diffInMin=null;
		Session session=null;
		try{
			session=sessionFactory.openSession();
		if(session!=null){
	String hql="SELECT minute(CURRENT_TIMESTAMP() - userOtp.generatedTime)  as minutes FROM com.brs.userservicemanagement.entity.UserOtp as userOtp Inner Join userOtp.loginDetails as loginDetails where loginDetails.userId=? and userOtp.otp=? ";		
	Query query=session.createQuery(hql);
	   query.setParameter(0,userId);
	   query.setParameter(1,otp);
	   diffInMin=(Integer)query.uniqueResult();
		}
		}finally{
			if(session!=null){
				session.close();
			}
		}
			
		return diffInMin;
	}
}
//where OTP='4630' ;









