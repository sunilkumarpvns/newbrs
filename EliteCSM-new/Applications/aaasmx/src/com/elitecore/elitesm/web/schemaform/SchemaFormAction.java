package com.elitecore.elitesm.web.schemaform;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.http.HttpResponse;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.methods.HttpDelete;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.struts.action.ActionForm;
import org.apache.struts.action.ActionForward;
import org.apache.struts.action.ActionMapping;

import com.elitecore.elitesm.util.logger.Logger;
import com.elitecore.elitesm.web.core.base.BaseDispatchAction;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;

public class SchemaFormAction extends BaseDispatchAction {
	
	protected static final String MODULE = "SchemaFormAction";

	public ActionForward updateModuleData(ActionMapping mapping, ActionForm form,HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		try {

			String requestUrl = servletRequest.getParameter("Url").trim();
			String detaildata = servletRequest.getParameter("Data").trim();
			StringEntity xmlEntity = new StringEntity(detaildata);

			DefaultHttpClient httpClient = getHttpClient(servletRequest);

			HttpPut updateRequest = new HttpPut(requestUrl);
			updateRequest.addHeader("content-type", "application/json");
			updateRequest.setEntity(xmlEntity);
			
			Logger.logInfo(MODULE, "Sending request to : " + requestUrl);
			HttpResponse response = httpClient.execute(updateRequest);
			
			printAndSetResponseForSuccess(servletResponse, response);
			
			httpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException ex) {

			ex.printStackTrace();

		} catch (IOException ep) {

			ep.printStackTrace();
		}
		return null;
	} 


	public ActionForward getModuleData(ActionMapping mapping, ActionForm form,HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

		try {

			String requestUrl = servletRequest.getParameter("Url").trim() + "?_type=json";;

			DefaultHttpClient httpClient = getHttpClient(servletRequest);

			HttpGet getRequest = new HttpGet(requestUrl);
			getRequest.addHeader("accept", "application/json");
			
			Logger.logInfo(MODULE, "Sending request to : " + requestUrl);
			HttpResponse response = httpClient.execute(getRequest);
			
			printAndSetResponseForSuccess(servletResponse, response);

			httpClient.getConnectionManager().shutdown();
			mapping.findForward("/schemaform");
		} catch (ClientProtocolException ex) {

			ex.printStackTrace();

		} catch (IOException ep) {

			ep.printStackTrace();
		}
		return null;
	}
	public ActionForward createModuleData(ActionMapping mapping, ActionForm form,HttpServletRequest servletRequest, HttpServletResponse servletResponse) {
		try {

			String requestUrl = servletRequest.getParameter("Url").trim();
			String detaildata = servletRequest.getParameter("Data").trim();
			StringEntity xmlEntity = new StringEntity(detaildata);

			DefaultHttpClient httpClient = getHttpClient(servletRequest);

			HttpPost createRequest = new HttpPost(requestUrl);
			createRequest.addHeader("content-type", "application/json");
			createRequest.setEntity(xmlEntity);
			
			Logger.logInfo(MODULE, "Sending request to : " + requestUrl);
			HttpResponse response = httpClient.execute(createRequest);
			
			printAndSetResponseForSuccess(servletResponse, response);
			
			httpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException ex) {

			ex.printStackTrace();

		} catch (IOException ep) {

			ep.printStackTrace();
		}
		return null;
	}

	public ActionForward deleteModuleData(ActionMapping mapping, ActionForm form,HttpServletRequest servletRequest, HttpServletResponse servletResponse) {

		try {

			String requestUrl = servletRequest.getParameter("Url").trim();

			DefaultHttpClient httpClient = getHttpClient(servletRequest);

			HttpDelete deleteRequest = new HttpDelete(requestUrl);
			deleteRequest.addHeader("accept", "application/json");
			
			Logger.logInfo(MODULE, "Sending request to : " + requestUrl);
			HttpResponse response = httpClient.execute(deleteRequest);

			printAndSetResponseForSuccess(servletResponse, response);

			httpClient.getConnectionManager().shutdown();

		} catch (ClientProtocolException ex) {

			ex.printStackTrace();

		} catch (IOException ep) {

			ep.printStackTrace();
		}
		return null;
	}


	private void printAndSetResponse(HttpServletResponse servletResponse, HttpResponse responses) throws IOException {
		servletResponse.setContentType("text/plain");
		BufferedReader br = new BufferedReader(new InputStreamReader((responses.getEntity().getContent())));
		StringBuilder responseValue = new StringBuilder();
		Gson gson = new GsonBuilder().setPrettyPrinting().create();
		JsonParser jp = new JsonParser();
		JsonElement je;
		String output;

		while ((output = br.readLine()) != null) {
			responseValue.append(output);
		}

		servletResponse.getWriter().println(responseValue.toString());

		je = jp.parse(responseValue.toString());
		String responseOutPut = gson.toJson(je);
		Logger.logInfo(MODULE, "Received Response is: \n" + responseOutPut);

	}
	
	private void printAndSetResponseForSuccess(HttpServletResponse servletResponse, HttpResponse responses) throws IOException {
		servletResponse.setContentType("text/plain");
		BufferedReader br = new BufferedReader(new InputStreamReader((responses.getEntity().getContent())));
		StringBuilder responseValue = new StringBuilder();
		String output;

		while ((output = br.readLine()) != null) {
			responseValue.append(output);
		}

		servletResponse.getWriter().println(responseValue.toString());

		Logger.logInfo(MODULE, "Received Response is: \n" + responseValue.toString());

	}

	private DefaultHttpClient getHttpClient(HttpServletRequest request) {

		DefaultHttpClient httpClient = new DefaultHttpClient();
		String UserName = request.getParameter("UserName").trim();
		String PassWord = request.getParameter("PassWord").trim();

		Credentials credentials = new UsernamePasswordCredentials(UserName, PassWord);
		httpClient.getCredentialsProvider().setCredentials(AuthScope.ANY, credentials);

		return httpClient;
	}

}
