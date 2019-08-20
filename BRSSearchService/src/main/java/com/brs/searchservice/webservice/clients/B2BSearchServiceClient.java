package com.brs.searchservice.webservice.clients;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.brs.searchservice.models.model.Response;

@Service
public class B2BSearchServiceClient {
	@Autowired
	private RestTemplate restTemplate;

	private String app_id = "fdcad448";
	private String app_key = "a1c2773733a5e93d41a3ae60788fed4e";
	private String format = "json";

	public Response searchBuses(String source, String destination, String doj, String dorj) {
		String url = "https://developer.goibibo.com/api/bus/search/?app_id=" + app_id + "&app_key=" + app_key
				+ "&format=" + format + "&source=" + source + "&destination=" + destination + "&dateofdeparture=" + doj
				+ "&dateofarrival=" + dorj;
		Response responseModel = restTemplate.getForObject(url, Response.class);
		return responseModel;
	}
}
