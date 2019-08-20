package com.brs.userservicemanagement.dao;

import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

@Repository
public class RolesDAOImpl 
implements RolesDAO{
	@Autowired
private SessionFactory sessionFactory;
	@Override
	public Integer getRoleId(String roleName) {
		Integer roleId = null;
		Session session = null;
		try {
			session = sessionFactory.openSession();
			String hql = "select roles.roleId from com.brs.userservicemanagement.entity.Roles as roles where roles.role=?";
			Query query = session.createQuery(hql);
			query.setParameter(0, roleName);
			roleId = (Integer) query.uniqueResult();
		} finally {
			if (session != null) {
				session.close();
			}
		}
		return roleId;
	}

}
