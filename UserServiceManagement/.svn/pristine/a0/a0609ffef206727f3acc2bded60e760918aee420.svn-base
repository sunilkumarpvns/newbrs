package com.brs.userservicemanagement.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.Transaction;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import com.brs.userservicemanagement.entity.LoginDetails;
import com.brs.userservicemanagement.enums.StatusEnum;


/**
 * The Class LoginDetailsDAOImpl.
 */
@Repository
public class LoginDetailsDAOImpl implements LoginDetailsDAO {
	
	/** The session factory. */
	@Autowired
	private SessionFactory sessionFactory;

	/* (non-Javadoc)
	 * @see com.brs.userservicemanagement.dao.LoginDetailsDAO#login(com.brs.userservicemanagement.entity.LoginDetails)
	 */
	@Override
	public LoginDetails login(LoginDetails loginDetails) {
		Session session = sessionFactory.openSession();
		String hql = "from "
+ " com.brs.userservicemanagement.entity.LoginDetails "
+ "as login  where (login.userName=:p1 OR login.email=:p2 OR login.mobile=:p3)  and "
+ "login.password=:p4";
		Query query = session.createQuery(hql);
		query.setParameter("p1", loginDetails.getUserName());
		query.setParameter("p2", loginDetails.getEmail());
		query.setParameter("p3", loginDetails.getMobile());
		query.setParameter("p4", loginDetails.getPassword());
		LoginDetails ld = (LoginDetails) query.uniqueResult();
		
		session.close();
		return ld;
	}
	
	@Override
	public Integer changePassword(String oldPassword, String newPassword,Long userId){
		Integer result=null;
		Session session=null;
		Transaction transaction=null;
		
		try{
			session=sessionFactory.openSession();
			if(session!=null){
				transaction=session.beginTransaction();
				String hql="UPDATE com.brs.userservicemanagement.entity.LoginDetails as changepassword"
						+" set changepassword.password=:newpassword"
						+" where changepassword.userId=:userid and changepassword.password=:oldpassword";
				Query query=session.createQuery(hql);
				query.setParameter("newpassword", newPassword);
				query.setParameter("userid", userId);
				query.setParameter("oldpassword", oldPassword);
				result=query.executeUpdate();
				if(transaction!=null && result!=null&&result>0){
					transaction.commit();
				}
			}
		}catch(HibernateException he){
			if(transaction!=null){
				transaction.rollback();
			}
		}finally{
			if(session!=null){
				session.close();
			}
				}
		
		return result;
	}

	@Override
	public int updateUserStatus(Long userId, StatusEnum statusEnum) {
		int count=0;
		Session session=null;
		Transaction transaction=null;
		
		try{
			session=sessionFactory.openSession();
			if(session!=null){
				transaction=session.beginTransaction();
				String hql="update com.brs.userservicemanagement.entity.LoginDetails as loginDetails set loginDetails.status=? where loginDetails.userId=?";
				Query query=session.createQuery(hql);
				query.setParameter(0, statusEnum);
				query.setParameter(1, userId);
				count=query.executeUpdate();
				if(transaction!=null && count>0){
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
		
		return count;
	}
	}
	
