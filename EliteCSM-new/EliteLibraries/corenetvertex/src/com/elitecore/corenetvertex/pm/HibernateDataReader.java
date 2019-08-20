package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Arrayz;
import com.elitecore.commons.base.Collectionz;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import javax.annotation.Nonnull;
import java.util.ArrayList;
import java.util.List;

public class HibernateDataReader implements DataReader {
    @SuppressWarnings("unchecked")
    @Nonnull
    public <T> List<T> readAll(Class<T> clas, Session session) {

        if (session.getTransaction() == null || session.getTransaction().isActive() == false){
            session.beginTransaction();
        }

        return session.createCriteria(clas).list();
    }

    @Nonnull
    public <T> List<T> read(Class<T> clas, Session session, String... names) {

        List<T> entities = new ArrayList<>();

        if(Arrayz.isNullOrEmpty(names) ){
            return entities;
        }


        for (String packageName : names) {

            if (session.getTransaction() == null || session.getTransaction().isActive() == false){
                session.beginTransaction();
            }

            Criteria criteria = session.createCriteria(clas);

            List<T> dbEntities = criteria.add(Restrictions.eq("name", packageName)).list();

            if (Collectionz.isNullOrEmpty(dbEntities) == false) {
                entities.addAll(dbEntities);
            }
        }

        return entities;
    }

    public <T> T get(Class<T> clas, Session session, String id) {

        if (session.getTransaction() == null || session.getTransaction().isActive() == false){
            session.beginTransaction();
        }

        return (T)session.get(clas, id);
    }
}
