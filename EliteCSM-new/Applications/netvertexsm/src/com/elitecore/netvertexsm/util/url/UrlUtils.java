package com.elitecore.netvertexsm.util.url;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.util.List;
import java.util.StringTokenizer;

import com.elitecore.netvertexsm.util.logger.Logger;


public class UrlUtils {

	private static final String MODULE="URLUTILS";
	public static List<String> parseFindColumns(String query){

		List<String> listColumns = new java.util.ArrayList<String>();

		if(query!=null){
			StringTokenizer tokenizer = new java.util.StringTokenizer(query,"&= ,;:",false);
			while(tokenizer.hasMoreTokens()){
				String token = tokenizer.nextToken();
				int dollarIndex = token.indexOf('$');
				if(dollarIndex>-1){
					token = token.substring(dollarIndex+1);
					if(!token.trim().equals("")){
						listColumns.add(token.trim());
						Logger.logDebug(MODULE,"Adding column in list :"+token);
					}
				}
			}
		}

		return listColumns;
	}	
	public static String getUrlQuery(String urlString) throws MalformedURLException {
		String query=null;

		if(urlString!=null){
			URL url = new URL(urlString);
			query = url.getQuery();
		}

		return query;
	}

	public static String replaceQueryColumnsWithValue(String urlString,String column,String value){

		if(column==null){
			column="";
		}

		if(value==null){
			value="";
		}

		if(!column.trim().equals("") || !column.trim().equals("")){
			urlString=urlString.replace(column, value);
		}

		return urlString;
	}

	public static void connectUrl(String urlString,boolean connectByThread) throws IOException{
	
		if(connectByThread){
			Runnable runnable = new URLConnectionThread(urlString);
			Thread t = new Thread(runnable);
			t.start();
		}else{
			connectToUrl(urlString);
		}
	}
	private static class URLConnectionThread implements Runnable{
		
		String urlString = null;
	
		URLConnectionThread(String urlString){
			this.urlString = urlString;
		}
		
		public void run() {
			try{
				System.out.println("[URL-CONNECTION-THREAD] - Connecting to :"+urlString);
				connectToUrl(this.urlString);
			}catch(Exception e){
				System.out.println("Error in connection URL ["+e.getMessage()+"]  :"+urlString);
			}
		}
		
	}
	private static void connectToUrl(String urlString) throws IOException {
		URL url = new URL(urlString);
		URLConnection connection = url.openConnection();
		connection.connect();
		if(connection instanceof HttpURLConnection) {
			HttpURLConnection httpURLConnection = (HttpURLConnection)connection ;
			int reponseCode = httpURLConnection.getResponseCode();
			String status = httpURLConnection.getHeaderField(0);
			System.out.println("Response status is [ "+status+" ]  for url hit : "+urlString);
		}
		try{
			connection.getInputStream().close();
		}catch(Exception e){
			
		}
	}
}
