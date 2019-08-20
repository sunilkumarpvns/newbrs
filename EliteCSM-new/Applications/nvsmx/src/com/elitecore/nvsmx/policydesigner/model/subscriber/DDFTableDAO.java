package com.elitecore.nvsmx.policydesigner.model.subscriber;

import com.elitecore.corenetvertex.sm.ddf.DdfData;
import com.elitecore.nvsmx.system.hibernate.CRUDOperationUtil;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionFactory;
import com.elitecore.nvsmx.system.hibernate.HibernateSessionUtil;
import org.hibernate.Criteria;
import org.hibernate.HibernateException;
import org.hibernate.Session;

import java.util.List;

public class DDFTableDAO {


    public static DdfData getDDFTableData() throws HibernateException {

        DdfData ddfTableData = null;
        Session session = null;
        try {
            session = HibernateSessionFactory.getNewSession();
            session.beginTransaction();
            Criteria criteria = session.createCriteria(DdfData.class);
            List list = criteria.list();
            if (list.isEmpty() == false) {
                ddfTableData = (DdfData) list.get(0);
            }

        } finally {
            HibernateSessionUtil.closeSession(session);
        }

        return ddfTableData;
    }

}
