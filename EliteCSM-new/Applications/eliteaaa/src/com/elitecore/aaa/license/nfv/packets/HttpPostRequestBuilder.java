package com.elitecore.aaa.license.nfv.packets;

import java.io.UnsupportedEncodingException;
import java.net.MalformedURLException;
import java.net.URL;

import org.apache.commons.net.util.Base64;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;

import com.elitecore.license.nfv.RequestData;

import net.sf.json.JSONSerializer;

public class HttpPostRequestBuilder {

	private String protocol;
	private String host;
	private int port;
	private String resource;
	private String username;
	private String passwd;
	private String body;

	public HttpPostRequestBuilder(String protocol, String host, int port, String resource) {
		this.protocol = protocol;
		this.host = host;
		this.port = port;
		this.resource = resource;
	}

	public HttpPostRequestBuilder authDetails(String username, String passwd) {
		this.username = username;
		this.passwd = passwd;
		return this;
	}
	
	public HttpPostRequestBuilder body(RequestData requestData) {
		this.body = JSONSerializer.toJSON(requestData).toString();
		return this;
	}

	public HttpPost build() throws MalformedURLException, UnsupportedEncodingException {
		URL url = new URL(protocol, host, port, resource);
		HttpPost postRequest = new HttpPost(url.toString());
		String authDetails = username + ":" + passwd;
		String authparams = new String(Base64.encodeBase64(authDetails.getBytes()));
		postRequest.addHeader("Authorization","Basic "+authparams);
		postRequest.setEntity(new StringEntity(this.body));
		return postRequest;
	}

}
