package com.elitecore.core.systemx.esix.configuration;

public class EndPointConfigurationImpl implements EndPointConfiguration {
    private String id;
    private String ipAddresses;
    private String port;
    private String contextPath;
    private String name;
    private String username;
    private String password;

    public EndPointConfigurationImpl(String id, String ipAddresses, String port,
                                     String contextPath, String name, String username, String password){
        this.id=id;
        this.ipAddresses=ipAddresses;
        this.port=port;
        this.contextPath=contextPath;
        this.name=name;
        this.username=username;
        this.password=password;
    }

    public String getId(){
        return id;
    }

    public String getIpAddresses(){
        return ipAddresses;
    }

    public String getPort(){
        return port;
    }

    public String getContextPath(){
        return contextPath;
    }

    public String getName(){
        return name;
    }

    public String getUsername(){
        return username;
    }

    public String getPassword(){
        return password;
    }
}
