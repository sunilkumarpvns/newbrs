package com.elitecore.nvsmx.remotecommunications;

import com.elitecore.nvsmx.remotecommunications.data.HTTPMethodType;

/**
 * Created by aditya on 9/13/16.
 */
public class RemoteMethod {
    private String baseURI;
    private String name;
    private String argument;
    private HTTPMethodType methodType=HTTPMethodType.GET;

    public RemoteMethod(String baseURI,
                        String methodName,
                        String methodArgument,
                        HTTPMethodType methodType) {
        this.baseURI = baseURI;
        this.name = methodName;
        this.argument = methodArgument;
        this.methodType = methodType;
    }


    public String getArgument() {
        return argument;
    }

    public String getName() {
        return name;
    }

    public String getBaseURI() { return baseURI; }

    public HTTPMethodType getMethodType() {
        return methodType;
    }


    @Override
    public String toString() {
        return "RemoteMethod{" +
                "baseURI='" + baseURI + '\'' +
                ", name='" + name + '\'' +
                ", argument='" + argument + '\'' +
                ", methodType=" + methodType +
                '}';
    }
}
