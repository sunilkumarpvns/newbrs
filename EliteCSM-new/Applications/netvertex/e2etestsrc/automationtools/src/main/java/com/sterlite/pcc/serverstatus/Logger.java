package com.sterlite.pcc.serverstatus;

import java.sql.Timestamp;

public class Logger {

    public void debug(Object message) {
        log("DEBUG", message);
    }

    private void log(String level, Object log) {
        System.out.println(String.format("%s [%s] %s", new Timestamp(System.currentTimeMillis()), level, log));
    }

    public void trace(Exception ex) {
        ex.printStackTrace();
    }
}
