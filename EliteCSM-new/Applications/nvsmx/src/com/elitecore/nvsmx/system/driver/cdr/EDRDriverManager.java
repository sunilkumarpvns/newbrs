package com.elitecore.nvsmx.system.driver.cdr;

import com.elitecore.core.driverx.cdr.deprecated.BaseCSVDriver;

import java.util.HashMap;
import java.util.Map;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class EDRDriverManager {

    private static final String MODULE = "CDR-DRIVER-MANAGER";

    private Map<String, BaseCSVDriver> cdrDriverMap;

    private static final EDRDriverManager CDR_DRIVER_MANAGER;

    private EDRDriverManager(){
        cdrDriverMap = new HashMap();
    }

    static {
        CDR_DRIVER_MANAGER = new EDRDriverManager();
    }

    public static EDRDriverManager getInstance(){
        return CDR_DRIVER_MANAGER;
    }

    public void registerDriver(BaseCSVDriver baseCSVDriver){
        if(cdrDriverMap.get(baseCSVDriver.getDriverName())==null){
            cdrDriverMap.put(baseCSVDriver.getDriverName(),  baseCSVDriver);
        } else {
            if(getLogger().isDebugLogLevel()){
                getLogger().debug(MODULE, "Not registering CDRDriver with name "+baseCSVDriver.getDriverName()
                        + " in EDRDriverManager as there is already one with the " +
                        "same name");
            }
        }
    }

    public void stop() {
        for(BaseCSVDriver cdrDriver : cdrDriverMap.values()) {
            cdrDriver.stop();
        }
    }

    public BaseCSVDriver getDriver(String name) {
        return cdrDriverMap.get(name);
    }
}
