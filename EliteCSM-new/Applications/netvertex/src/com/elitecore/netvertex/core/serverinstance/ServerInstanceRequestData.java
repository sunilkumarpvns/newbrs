package com.elitecore.netvertex.core.serverinstance;

import static org.apache.commons.lang3.StringUtils.isBlank;

public class ServerInstanceRequestData {

    public String getSmIp() { return System.getenv("SM_IP"); }

    public String getSmPort() { return System.getenv("SM_PORT"); }

    public String getServerName() { return System.getenv("SERVER_NAME"); }

    public String getContextPath() { return System.getenv("CONTEXT_PATH"); }

    public String getOriginHost() { return System.getenv("ORIGIN_HOST"); }

    public String getOriginRealm() { return System.getenv("ORIGIN_REALM"); }

    public String getServerGroupName() { return System.getenv("SERVER_GROUP_NAME"); }

    public boolean validateRequestData() {
        return (isBlank(getServerName()) ||
                isBlank(getServerGroupName()) ||
                        isBlank(getSmIp()) ||
                        isBlank(getSmPort()) ||
                        isBlank(getContextPath()) ||
                isBlank(getOriginHost()) ||
                isBlank(getOriginRealm()));

    }
}
