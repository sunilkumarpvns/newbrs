package com.elitecore.corenetvertex.core.alerts;


public enum Alerts {

    NETVERTEX_PCRF_ALERTS(null,"Netvertex-PCRF-Alerts","NETVERTEX-PCRF-ALERTS","Y",1,"P",false),

        SERVER(NETVERTEX_PCRF_ALERTS,"Server","SERVER","Y",2,"P",false),
            SERVERUP(SERVER,"ServerUp","SERVERUP","Y",3,"L",false),
            SERVERDOWN(SERVER,"ServerDown","SERVERDOWN","Y",3,"L",false),
            LICENSE_EXPIRED(SERVER,"License Expired","LICENSE EXPIRED","Y",3,"L",false),
            LICENSE_REQUEST_FAILED(SERVER,"License Request Failed", "LICENSE REQUEST FAILED","Y",3,"L",false),
            LICENSE_EXCEEDED(SERVER,"License TPS Exceeded","LICENSE TPS EXCEEDED","Y",3,"L",false),

        SERVICES(NETVERTEX_PCRF_ALERTS,"Services","SERVICES","Y",2,"P",false),
            PCRF(SERVICES,"PCRF","PCRF","Y",3,"P",false),
                PCRF_STARTUP_FAILED(PCRF,"StartUp Failed","STARTUP FAILED","Y",4,"L",false),
                SPRUP(PCRF,"SprUp","SPRUP","Y",4,"L",true),
                SPRDOWN(PCRF,"SprDown","SPRDOWN","Y",4,"L",true),
                QUERY_TIME_OUT(PCRF,"Query Timeout","Query Timeout","Y",4,"L",true),
                SERVICE_POLICY_INIT_FAILS(PCRF,"Service Policy Init Fails","SERVICE POLICY INIT FAILS","Y",4,"L",false),
                UNKNOWN_USER(PCRF,"Unknown User","UNKNOWN USER","Y",4,"L",true),
            NOTIFICATION(SERVICES,"Notification","NOTIFICATION","Y",3,"P",false),
                NOTIFICATION_STARTUP_FAILED(NOTIFICATION,"StartUp Failed","STARTUP FAILED","Y",4,"L",false),
                EMAIL_SENDING_FAILED(NOTIFICATION,"Email Sending Failed","EMAIL SENDING FAILED","Y",4,"L",false),
                SMS_SENDING_FAILED(NOTIFICATION,"SMS Sending Failed","EMAIL SENDING FAILED","Y",4,"L",false),

        GATEWAYS(NETVERTEX_PCRF_ALERTS,"Gateways","GATEWAYS","Y",2,"P",false),
            RADIUS(GATEWAYS,"RADIUS","RADIUS","Y",3,"P",false),
                RADIUS_GATEWAY_ALIVE(RADIUS,"Alive","ALIVE","Y",4,"L",false),
                RADIUS_GATEWAY_DEAD(RADIUS,"Dead","DEAD","Y",4,"L",false),
            DIAMETER(GATEWAYS,"Diameter","DIAMETER","Y",3,"P",false),
                DIAMETER_STACK_UP(DIAMETER,"Diameter Stack Up","DIAMETER STACK UP","Y",4,"L",false),
                DIAMETER_STACK_DOWN(DIAMETER,"Diameter Stack Down","DIAMETER STACK DOWN","Y",4,"L",false),
                DIAMETER_PEER_UP(DIAMETER,"Diameter Peer Up","DIAMETER PEER UP","Y",4,"L",false),
                DIAMETER_PEER_DOWN(DIAMETER,"Diameter Peer Down","DIAMETER PEER DOWN","Y",4,"L",false),
                DIAMETER_HIGH_RESPONSE_TIME(DIAMETER,"Diameter High Response Time","DIAMETER HIGH RESPONSE TIME","Y",4,"L",false),
                DIAMETER_PEER_HIGH_RESPONSE_TIME(DIAMETER,"Diameter Peer High Response Time","DIAMETER PEER HIGH RESPONSE TIME","Y",4,"L",false),
                DIAMETER_PEER_CONNECTION_REJECTED(DIAMETER, "Diameter Peer Connection Rejected", "DIAMETER PEER CONNECTION REJECTED","Y",4,"L",false),
                DIAMETER_CC_STATISTICS_NOT_FOUND(DIAMETER, "Diameter CC Statistics Not Found", "DIAMETER CC STATISTICS NOT FOUND","Y",4,"L",false),
                DIAMETER_BASE_STATISTICS_NOT_FOUND(DIAMETER, "Diameter Base Statistics Not Found", "DIAMETER BASE STATISTICS NOT FOUND","Y",4,"L",false),
            FILE(GATEWAYS,"File","FILE","Y",3,"P",false),
            	FILE_GATEWAY_DISK_SPACE_RESOLVED(FILE, "File gateway disk space resolved", "FILE GATEWAY DISK SPACE RESOLVED", "Y", 4, "L",false),
            	FILE_GATEWAY_NO_SPACE_LEFT_ON_DEVICE(FILE, "File gateway no space left on device", "FILE GATEWAY NO SPACE LEFT ON DEVICE", "Y", 4, "L", false),
            	FILE_GATEWAY_PARSING_ERROR(FILE, "File gateway parsing error", "FILE GATEWAY PARSING ERROR", "Y", 4, "L", false),
            	FILE_GATEWAY_NO_FILE_RECEIVED(FILE, "File gateway no file received", "FILE GATEWAY NO FILE RECEIVED", "Y", 4, "L", false),
            	FILE_GATEWAY_MAX_FILES_LIMIT_REACHED(FILE, "File gateway max files limit reached", "FILE GATEWAY max files limit reached", "Y", 4, "L", false),
            	


            	

            	
        PACKAGES(NETVERTEX_PCRF_ALERTS,"Packages","PACKAGES","Y",2,"P",false),
            DATA_PACKAGES_CACHING_FAILS(PACKAGES,"Data Packages Caching Fails","DATA PACKAGES CACHING FAILS","Y",3,"L",false),
            IMS_PACKAGES_CACHING_FAILS(PACKAGES,"IMS Packages Caching Fails","IMS PACKAGES CACHING FAILS","Y",3,"L",false),

        DATABASE(NETVERTEX_PCRF_ALERTS,"Database","DATABASE","Y",2,"P",false),
            DATABASE_CONNECTION_NOT_AVAILABLE(DATABASE,"Database Connection Not Available","DATABASE CONNECTION NOT AVAILABLE","Y",3,"L",true),
            HIGH_QUERY_RESPONSE_TIME(DATABASE,"High Query Response Time","HIGH QUERY RESPONSE TIME","Y",3,"L",true),
            DATASOURCE_UP(DATABASE,"DataSource Up","DATASOURCE UP","Y",3,"L",false),
            DATASOURCE_DOWN(DATABASE,"DataSource Down","DATASOURCE DOWN","Y",3,"L",false);



    private Alerts parent;
    private String name;
    private String alias;
    private String enabled;
    private int orderNo;
    private String type;
    private boolean floodControl;

    private Alerts(Alerts parent, String name, String alias, String enabled, int orderNo, String type, boolean floodControl) {
        this.parent = parent;
        this.name = name;
        this.alias = alias;
        this.enabled = enabled;
        this.orderNo = orderNo;
        this.type = type;
        this.floodControl = floodControl;
    }

    public Alerts getParent() {
        return parent;
    }

    public String getName() {
        return name;
    }

    public String getAlias() {
        return alias;
    }

    public String getEnabled() {
        return enabled;
    }

    public int getOrderNo() {
        return orderNo;
    }

    public String getType() {
        return type;
    }

    public boolean isParentAlert() {
        return "P".equals(type);
    }

    public boolean isFloodControl() {
        return floodControl;
    }
    public String getEnumName(){
        return name();
    }
}
