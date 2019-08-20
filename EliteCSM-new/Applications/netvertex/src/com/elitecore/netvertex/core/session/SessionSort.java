package com.elitecore.netvertex.core.session;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import com.elitecore.core.serverx.sessionx.SessionData;

public class SessionSort implements Consumer<Iterator<SessionData>> {

    private Predicate<SessionData> sessionDataPredicate;
    private SessionSortOrder sessionSortOrder;

    private List<SessionData> sessionDatas;

    public SessionSort(Predicate<SessionData> sessionDataPredicate, SessionSortOrder sessionSortOrder) {
        this.sessionDataPredicate = sessionDataPredicate;
        this.sessionSortOrder = sessionSortOrder;
    }


    @Override
    public void accept(Iterator<SessionData> sessionDataIterator) {

        if(Objects.isNull(sessionDataIterator)) {
            return;
        }

        while (sessionDataIterator.hasNext()) {
            SessionData sessionData = sessionDataIterator.next();
            if(sessionDataPredicate.test(sessionData) == false) {
                continue;
            }


            if(sessionDatas == null) {
                sessionDatas = new ArrayList<>(2);
            }

            sessionDatas.add(sessionData);
        }

        if(Objects.nonNull(sessionDatas)) {
            sessionSortOrder.sort(sessionDatas);
        }
    }

    public List<SessionData> getList() {
        return sessionDatas;
    }
}
