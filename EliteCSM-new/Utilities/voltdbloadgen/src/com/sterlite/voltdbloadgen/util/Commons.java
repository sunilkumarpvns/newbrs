package com.sterlite.voltdbloadgen.util;

import org.voltdb.VoltTable;
import org.voltdb.client.ClientResponse;
import org.voltdb.client.NullCallback;

public class Commons {
    public static final long FUTURE_DATE = System.currentTimeMillis() + java.util.concurrent.TimeUnit.DAYS.toMillis(20*365); //LIFE-TIME VALIDITY
    public static NullCallback NULL_CALL_BACK = new NullCallback();
    public static long getCurrentTime() {
        return System.currentTimeMillis();
    }
    public static String getSessionId(String subscriberId) {
        return subscriberId + ":Gx";
    }
    public static void printIfFail(ClientResponse clientResponse, String forID) {

        if (clientResponse.getStatus() != ClientResponse.SUCCESS) {
            System.out.println("FAIL for ID: " + forID + ". Reason. " + clientResponse.getStatusString());
            return;
        }

        VoltTable voltTable = clientResponse.getResults()[0];

        if (voltTable.getRowCount() == 0) {
            System.out.println("FAIL, ROW:0,  for ID: " + forID);
            return;
        }

        //update count
        /*voltTable.advanceRow();
        int updateCount = (int) voltTable.get(0, VoltType.INTEGER);
        System.out.println("UPDATE COUNT: "+ updateCount);*/
    }

    public static int getInt(String param) {
        return Integer.parseInt(param);
    }
    public static long getLong(String param) {
        return Long.parseLong(param);
    }
}
