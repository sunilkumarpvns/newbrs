package com.elitecore.corenetvertex.pm;

import org.hibernate.Session;

import java.util.List;

public interface DataReader {
    <T> List<T> readAll(Class<T> clas, Session session);
    <T> List<T> read(Class<T> clas, Session session, String... names);
    <T> T get(Class<T> clas, Session session, String id);
}
