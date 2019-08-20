package com.elitecore.netvertex.ws;

import com.elitecore.corenetvertex.constants.CommonConstants;
import com.elitecore.corenetvertex.util.SystemPropertyReaders;
import org.apache.cxf.endpoint.Server;
import org.apache.cxf.jaxrs.JAXRSServerFactoryBean;

import java.util.ArrayList;
import java.util.List;

import static com.elitecore.commons.logging.LogManager.getLogger;

public class RestServer {

    private static final String MODULE = "REST-SERV";
    public static final String REST_PORT = "rest.port";
    private final JAXRSServerFactoryBean jaxrsServerFactoryBean;
    private final List<Object> services = new ArrayList<>();
    private Server restApiServer;


    public RestServer() {
        jaxrsServerFactoryBean = new JAXRSServerFactoryBean();
    }

    public void startRestApiListener() {

        getLogger().info(MODULE, "Starting Rest API Listener");

        final SystemPropertyReaders.SystemPropertyReader<Long> cleanUpIntervalReader = new SystemPropertyReaders.NumberReaderBuilder(REST_PORT)
                .between(1001, 65535)
                .onFail(9000, "Error while reading parameter " + REST_PORT).build();

        int port = cleanUpIntervalReader.read().intValue();


        try {
            String restAPIPublishURL = "http://" + CommonConstants.ALL_IP_ADDRESS + ":" + port + "/netvertex";

            jaxrsServerFactoryBean.setServiceBeans(services);


            jaxrsServerFactoryBean.setAddress(restAPIPublishURL);

            this.restApiServer = jaxrsServerFactoryBean.create();
            this.restApiServer.start();

            if (getLogger().isInfoLogLevel()) {
                getLogger().info(MODULE, "Rest API Listener published successfully on URL: " + restAPIPublishURL);
            }

        } catch (Exception e) {
            getLogger().error(MODULE, "Error while starting Rest API Listener. Reason: " + e.getMessage());
            getLogger().trace(MODULE, e);
        }
    }

    public void addService(Object service) {
        services.add(service);
    }


    public void stop() {
        getLogger().info(MODULE, "Stopping REST services");
        if (restApiServer != null) {
            restApiServer.stop();
            jaxrsServerFactoryBean.getBus().shutdown(false);
        }

        getLogger().info(MODULE, "REST services stop successfully");
    }

    public boolean isStarted() {
        return restApiServer != null;
    }

}
