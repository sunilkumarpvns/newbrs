package com.brs.searchservice.models.api;

import com.brs.searchservice.models.invoker.ApiClient;

import com.brs.searchservice.models.model.Response;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.util.UriComponentsBuilder;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.core.io.FileSystemResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;

@javax.annotation.Generated(value = "io.swagger.codegen.languages.JavaClientCodegen", date = "2018-07-25T08:25:14.822+05:30")
@Component("com.brs.searchservice.models.api.DefaultApi")
public class DefaultApi {
    private ApiClient apiClient;

    public DefaultApi() {
        this(new ApiClient());
    }

    @Autowired
    public DefaultApi(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    public ApiClient getApiClient() {
        return apiClient;
    }

    public void setApiClient(ApiClient apiClient) {
        this.apiClient = apiClient;
    }

    /**
     * Search for Busses
     * 
     * <p><b>200</b> - OK
     * @param appId 
     * @param appKey 
     * @param format 
     * @param source 
     * @param destination 
     * @param dateofdeparture 
     * @return Response
     * @throws RestClientException if an error occurs while attempting to invoke the API
     */
    public Response searchBus(String appId, String appKey, String format, String source, String destination, String dateofdeparture) throws RestClientException {
        Object postBody = null;
        
        // verify the required parameter 'appId' is set
        if (appId == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'appId' when calling searchBus");
        }
        
        // verify the required parameter 'appKey' is set
        if (appKey == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'appKey' when calling searchBus");
        }
        
        // verify the required parameter 'format' is set
        if (format == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'format' when calling searchBus");
        }
        
        // verify the required parameter 'source' is set
        if (source == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'source' when calling searchBus");
        }
        
        // verify the required parameter 'destination' is set
        if (destination == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'destination' when calling searchBus");
        }
        
        // verify the required parameter 'dateofdeparture' is set
        if (dateofdeparture == null) {
            throw new HttpClientErrorException(HttpStatus.BAD_REQUEST, "Missing the required parameter 'dateofdeparture' when calling searchBus");
        }
        
        String path = UriComponentsBuilder.fromPath("/api/bus/search/").build().toUriString();
        
        final MultiValueMap<String, String> queryParams = new LinkedMultiValueMap<String, String>();
        final HttpHeaders headerParams = new HttpHeaders();
        final MultiValueMap<String, Object> formParams = new LinkedMultiValueMap<String, Object>();
        
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "app_id", appId));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "app_key", appKey));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "format", format));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "source", source));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "destination", destination));
        queryParams.putAll(apiClient.parameterToMultiValueMap(null, "dateofdeparture", dateofdeparture));

        final String[] accepts = { 
            "application/json; charset=utf-8"
        };
        final List<MediaType> accept = apiClient.selectHeaderAccept(accepts);
        final String[] contentTypes = { };
        final MediaType contentType = apiClient.selectHeaderContentType(contentTypes);

        String[] authNames = new String[] {  };

        ParameterizedTypeReference<Response> returnType = new ParameterizedTypeReference<Response>() {};
        return apiClient.invokeAPI(path, HttpMethod.GET, queryParams, postBody, headerParams, formParams, accept, contentType, authNames, returnType);
    }
}
