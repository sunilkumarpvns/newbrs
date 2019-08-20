package com.elitecore.corenetvertex.pm;

import com.elitecore.commons.base.Collectionz;
import org.hibernate.Session;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DummyDataReader implements DataReader {

    private Map<Class, List<Object>> readList = new HashMap<>();
    private Map<Class, Object> getList = new HashMap<>();

    private List emptyList = new ArrayList();

    public DummyDataReader(){
        readList = new HashMap<>();
        getList = new HashMap<>();
    }

    public void setReadList(Class clas, List readList) {
        this.readList.put(clas, readList);
    }

    public void setGetObject(Class clas, Object getObject) {
        this.getList.put(clas, getObject);
    }

    @Override
    public <T> List<T> readAll(Class<T> clas, Session session) {
        return extractList(clas);
    }

    @Override
    public <T> List<T> read(Class<T> clas, Session session, String... names) {
        return extractList(clas);
    }

    private <T> List<T> extractList(Class<T> clas) {
        List<Object> dataStack = readList.get(clas);
        if(Collectionz.isNullOrEmpty(dataStack) == false){
            return (List<T>)dataStack;
        }
        return emptyList;
    }


    @Override
    public <T> T get(Class<T> clas, Session session, String id) {
        return (T)getList.get(clas);
    }
}
