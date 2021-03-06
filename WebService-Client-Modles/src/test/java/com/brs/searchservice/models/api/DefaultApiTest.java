/*
 * Open API Goibibo API Program
 * Open API Spec of Goibibo APIs. You can find more about Open API Spec here (https://openapis.org/)
 *
 * OpenAPI spec version: 1.0.0
 * Contact: apiteam@goibibo.com
 *
 * NOTE: This class is auto generated by the swagger code generator program.
 * https://github.com/swagger-api/swagger-codegen.git
 * Do not edit the class manually.
 */


package com.brs.searchservice.models.api;

import com.brs.searchservice.models.model.Response;
import org.junit.Test;
import org.junit.Ignore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * API tests for DefaultApi
 */
@Ignore
public class DefaultApiTest {

    private final DefaultApi api = new DefaultApi();

    
    /**
     * Search for Busses
     *
     * 
     *
     * @throws ApiException
     *          if the Api call fails
     */
    @Test
    public void searchBusTest() {
        String appId = null;
        String appKey = null;
        String format = null;
        String source = null;
        String destination = null;
        String dateofdeparture = null;
        Response response = api.searchBus(appId, appKey, format, source, destination, dateofdeparture);

        // TODO: test validations
    }
    
}
