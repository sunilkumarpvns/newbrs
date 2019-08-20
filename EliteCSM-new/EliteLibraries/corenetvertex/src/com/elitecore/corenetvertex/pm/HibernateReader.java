package com.elitecore.corenetvertex.pm;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.Nonnull;
import com.elitecore.commons.base.Arrayz;
import org.hibernate.Criteria;
import org.hibernate.Session;
import org.hibernate.criterion.Restrictions;

import com.elitecore.commons.base.Collectionz;
import com.elitecore.commons.logging.LogManager;

/**
 * 
 * This class provides reading functionality from hibernate, with reading list of hibernate related POJOs.
 * 
 * @author Jay Trivedi
 */
@Deprecated
public class HibernateReader {
	private static final HibernateDataReader hibernateDataReader;

	static {
		hibernateDataReader = new HibernateDataReader();
	}

	@SuppressWarnings("unchecked")
	@Nonnull
    public static <T> List<T> readAll(Class<T> clas, Session session) {
		return hibernateDataReader.readAll(clas,session);
	}

	@Nonnull
    public static <T> List<T> read(Class<T> clas, Session session, String... packageNames) {
		return hibernateDataReader.read(clas,session,packageNames);
	}

    public static <T> T get(Class<T> clas, Session session, String id) {
		return hibernateDataReader.get(clas,session,id);
    }
	
}
