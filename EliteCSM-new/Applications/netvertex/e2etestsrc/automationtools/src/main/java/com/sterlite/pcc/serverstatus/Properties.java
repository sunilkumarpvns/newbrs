package com.sterlite.pcc.serverstatus;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Properties {

    private java.util.Properties properties;

    public Properties(java.util.Properties properties) {
        this.properties = properties;
    }


    public static Properties load() throws IOException {
        java.util.Properties prop = new java.util.Properties();

        Path path = Paths.get("automationtools.properties");
        System.out.println(path.toAbsolutePath().toString());


        try (InputStream resourceAsStream = Properties.class.getClassLoader().getResourceAsStream("automationtools.properties");){

            // load a properties file
            prop.load(resourceAsStream);

            // get the property value and print it out
            System.out.println(prop);

        }

        return new Properties(prop);

    }


    public String get(String key) {
        return properties.getProperty(key);
    }
}
