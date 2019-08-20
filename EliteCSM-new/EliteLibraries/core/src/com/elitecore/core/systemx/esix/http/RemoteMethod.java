package com.elitecore.core.systemx.esix.http;

import java.util.HashMap;
import java.util.Map;

/**
 * Data necessary for EndPoint Communicator to communicate with external system i.e. WebService, RMI calls.
 *
 * @author Kartik Prajapati
 */
public class RemoteMethod {
    private String baseURI;
    private String name;
    private Map<String, String > arguments;
    private HTTPMethodType methodType=HTTPMethodType.GET;

    public RemoteMethod(String baseURI,
                        String methodName,
                        HTTPMethodType methodType) {
        this.baseURI = baseURI;
        this.name = methodName;
        this.methodType = methodType;
        this.arguments = new HashMap();
    }


    public Map getArguments() {
        return arguments;
    }

    public String getName() {
        return name;
    }

    public String getBaseURI() { return baseURI; }

    public HTTPMethodType getMethodType() {
        return methodType;
    }

    public void addArgument(String key, String value){
        arguments.put(key,value);
    }


}
