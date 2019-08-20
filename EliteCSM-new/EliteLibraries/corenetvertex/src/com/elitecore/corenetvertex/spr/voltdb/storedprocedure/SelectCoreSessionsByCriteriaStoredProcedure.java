package com.elitecore.corenetvertex.spr.voltdb.storedprocedure;

import org.voltdb.SQLStmt;
import org.voltdb.VoltProcedure;
import org.voltdb.VoltTable;

import java.util.AbstractMap.SimpleEntry;
import java.util.Map.Entry;

import static java.util.Objects.isNull;
import static java.util.Objects.nonNull;

/**
 * @author Prakash Pala
 * @since 08-Mar-2019 (Happy Women's Day!!! :D)
 *
 * Possible Values for selecting core sessions
 * --------------------------------------------
 * 1. multiple sessionIds,
 *
 * 2. single sessionId OR subscriberId OR sessionIPv4 OR sessionIPv6,
 *
 * 3. single sessionId OR subscriberId OR sessionIPv4,
 * 4. single sessionId OR subscriberId OR sessionIPv6,
 * 5. single sessionId OR sessionIPv4 OR sessionIPv6,
 * 6. single subscriberId OR sessionIPv4 OR sessionIPv6,
 *
 * 7. single sessionId OR subscriberId,
 * 8. single sessionId OR sessionIPv4,
 * 9. single sessionId OR sessionIPv6,
 * 10. single subscriberId OR sessionIPv4,
 * 11. single subscriberId OR sessionIPv6,
 * 12. single sessionIPv4 OR sessionIPv6
 *
 *
 * Excluded (Not in this proc's scope)
 * -----------------------------------
 * NO single key and single value will come here, as already handled ahead.
 * NO any other key will come with coreSessionId.
 * Multiple subscriberIds will not come as per current implementations.
 * Multiple sessionIPv4s will not come as per current implementations.
 * Multiple sessionIPv6s will not come as per current implementations.
 */
public class SelectCoreSessionsByCriteriaStoredProcedure extends VoltProcedure {


    public final SQLStmt bySessionIds = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE SESSION_ID IN ?");

    public final SQLStmt bySessionIdOrSubscriberIdOrIpv4OrIpv6 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SESSION_ID=? OR SUBSCRIBER_IDENTITY=? OR SESSION_IP_V4=? OR SESSION_IP_V6=?");

    public final SQLStmt bySessionIdOrSubscriberIdOrIpv4 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SESSION_ID=? OR SUBSCRIBER_IDENTITY=? OR SESSION_IP_V4=?");

    public final SQLStmt bySessionIdOrSubscriberIdOrIpv6 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SESSION_ID=? OR SUBSCRIBER_IDENTITY=? OR SESSION_IP_V6=?");

    public final SQLStmt bySessionIdOrIpv4OrIpv6 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SESSION_ID=? OR SESSION_IP_V4=? OR SESSION_IP_V6=?");

    public final SQLStmt bySubscriberIdOrIpv4OrIpv6 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SUBSCRIBER_IDENTITY=? OR SESSION_IP_V4=? OR SESSION_IP_V6=?");

    public final SQLStmt bySessionIdOrSubscriberId = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SESSION_ID=? OR SUBSCRIBER_IDENTITY=?");

    public final SQLStmt bySessionIdOrIpv4 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SESSION_ID=? OR SESSION_IP_V4=?");

    public final SQLStmt bySessionIdOrIpv6 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SESSION_ID=? OR SESSION_IP_V6=?");

    public final SQLStmt bySubscriberIdOrIpv4 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SUBSCRIBER_IDENTITY=? OR SESSION_IP_V4=?");

    public final SQLStmt bySubscriberIdOrIpv6 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SUBSCRIBER_IDENTITY=? OR SESSION_IP_V6=?");

    public final SQLStmt byIpv4OrIpv6 = new SQLStmt("SELECT * FROM TBLT_SESSION WHERE " +
            "SESSION_IP_V4=? OR SESSION_IP_V6=?");



    public VoltTable[] run(String[] sessionIds, String[] subscriberIds, String[] sessionIPv4s, String[] sessionIPv6s)
            throws VoltAbortException {

        Entry<SQLStmt, Object> sqlStmtWithValue = selectSuitableSQLStmtAndValue(sessionIds, subscriberIds,
                sessionIPv4s, sessionIPv6s);


        voltQueueSQL(sqlStmtWithValue.getKey(), sqlStmtWithValue.getValue());
        return voltExecuteSQL(true);
    }


    private Entry<SQLStmt, Object> selectSuitableSQLStmtAndValue(String[] sessionIds, String[] subscriberIds
            , String[] sessionIPv4s, String[] sessionIPv6s){


        String[] values;
        if(checkArrayHasMultipleValues(sessionIds)
                && checkArraysAreNullOrEmpty(subscriberIds, sessionIPv4s, sessionIPv6s)){
            values = sessionIds;
            return new SimpleEntry<>(bySessionIds, values);
        }

        else if(checkArraysHaveSingleValue(sessionIds, subscriberIds, sessionIPv4s, sessionIPv6s)){
            values = new String[]{sessionIds[0], subscriberIds[0], sessionIPv4s[0], sessionIPv6s[0]};    //NOSONAR - It will never be null
            return new SimpleEntry<>(bySessionIdOrSubscriberIdOrIpv4OrIpv6, values);
        }

        else if(checkArraysHaveSingleValue(sessionIds, subscriberIds, sessionIPv4s)){
            values = new String[]{sessionIds[0], subscriberIds[0], sessionIPv4s[0]};    //NOSONAR - It will never be null
            return new SimpleEntry<>(bySessionIdOrSubscriberIdOrIpv4, values);
        }

        else if(checkArraysHaveSingleValue(sessionIds, subscriberIds, sessionIPv6s)){
            values = new String[]{sessionIds[0], subscriberIds[0], sessionIPv6s[0]};    //NOSONAR - It will never be null
            return new SimpleEntry<>(bySessionIdOrSubscriberIdOrIpv6, values);
        }

        else if(checkArraysHaveSingleValue(sessionIds, sessionIPv4s, sessionIPv6s)){
            values = new String[]{sessionIds[0], sessionIPv4s[0], sessionIPv6s[0]};    //NOSONAR - It will never be null
            return new SimpleEntry<>(bySessionIdOrIpv4OrIpv6, values);
        }

        else if(checkArraysHaveSingleValue(subscriberIds, sessionIPv4s, sessionIPv6s)){
            values = new String[]{subscriberIds[0], sessionIPv4s[0], sessionIPv6s[0]};
            return new SimpleEntry<>(bySubscriberIdOrIpv4OrIpv6, values);
        }

        else if(checkArraysHaveSingleValue(sessionIds, subscriberIds)){
            values = new String[]{sessionIds[0], subscriberIds[0]};    //NOSONAR - It will never be null
            return new SimpleEntry<>(bySessionIdOrSubscriberId, values);
        }

        else if(checkArraysHaveSingleValue(sessionIds, sessionIPv4s)){
            values = new String[]{sessionIds[0], sessionIPv4s[0]};    //NOSONAR - It will never be null
            return new SimpleEntry<>(bySessionIdOrIpv4, values);
        }

        else if(checkArraysHaveSingleValue(sessionIds, sessionIPv6s)){
            values = new String[]{sessionIds[0], sessionIPv6s[0]};    //NOSONAR - It will never be null
            return new SimpleEntry<>(bySessionIdOrIpv6, values);
        }

        else if(checkArraysHaveSingleValue(subscriberIds, sessionIPv4s)){
            values = new String[]{subscriberIds[0], sessionIPv4s[0]};
            return new SimpleEntry<>(bySubscriberIdOrIpv4, values);
        }

        else if(checkArraysHaveSingleValue(subscriberIds, sessionIPv6s)){
            values = new String[]{subscriberIds[0], sessionIPv6s[0]};
            return new SimpleEntry<>(bySubscriberIdOrIpv6, values);
        }

        else if(checkArraysHaveSingleValue(sessionIPv4s, sessionIPv6s)){
            values = new String[]{sessionIPv4s[0], sessionIPv6s[0]};
            return new SimpleEntry<>(byIpv4OrIpv6, values);
        }

        else{
            throw new UnsupportedOperationException("This Operation is not supported in VoltDB.");
        }

    }

    private boolean checkArrayHasMultipleValues(String[] strArr) {
        return nonNull(strArr) && strArr.length > 1;
    }

    private boolean checkArraysAreNullOrEmpty(String[]... arrs){
        for(String[] arr : arrs){
            if((isNull(arr) || arr.length == 0) == false){
                return false;
            }
        }
        return true;
    }

    private boolean checkArraysHaveSingleValue(String[]... arrs){
        for(String[] arr : arrs){
            if((nonNull(arr) && arr.length==1) == false){
                return false;
            }
        }
        return true;
    }

}
