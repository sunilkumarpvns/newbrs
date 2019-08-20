package com.sterlite.pcc;

import java.sql.Timestamp;

public class Logger {

    public void debug(Object message) {
        log("DEBUG", message);
    }

    public void log(String level, Object log) {
        System.out.println(String.format("%s [%s] %s", new Timestamp(System.currentTimeMillis()), level, log));
    }

    public void trace(Exception ex) {
        ex.printStackTrace();
    }

    public void warn(String message) {
        log("WARN", message);
    }

    public void info(String message) {
        log("INFO", message);
    }

    public void error(String message) {
        log("INFO", message);
    }
}
