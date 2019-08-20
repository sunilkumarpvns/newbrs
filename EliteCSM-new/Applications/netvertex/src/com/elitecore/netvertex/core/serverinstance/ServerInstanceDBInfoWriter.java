package com.elitecore.netvertex.core.serverinstance;

import com.elitecore.corenetvertex.sm.serverinstance.ConfigurationDatabase;
import com.elitecore.netvertex.core.util.JsonReaderAndWriter;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class ServerInstanceDBInfoWriter {
    private static final String SYSTEM_PATH = "system";
    private String serverHome;
    private JsonReaderAndWriter jsonReaderAndWriter;

    public ServerInstanceDBInfoWriter(String serverHome, JsonReaderAndWriter jsonReaderAndWriter){
        this.serverHome = serverHome;
        this.jsonReaderAndWriter = jsonReaderAndWriter;
    }

    public void writeDBInfo(ConfigurationDatabase configurationDB) throws IOException {

        try (FileWriter fileWriter = new FileWriter(new File(serverHome + File.separator + SYSTEM_PATH + File.separator + "database.json"), false)){
            jsonReaderAndWriter.write(fileWriter,configurationDB);
        }
    }

}
