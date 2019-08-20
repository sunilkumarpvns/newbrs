package com.elitecore.core.util.url;

import java.io.IOException;
import java.io.StringReader;

/**
 * @author Milan Paliwal
 */
public class URLParser {

	private static final int END_OF_STRING = -1;
	public static final int UNKNOWN_PORT = 0;

	private URLParser() {}
	
	/**
	 * @author Milan Paliwal
	 */
	public static URLData parse(String url)throws InvalidURLException {
		URLData data=new URLData();
		String protocol,userName,password,host,resource;
		int port=UNKNOWN_PORT;
		protocol=userName=password=host=resource=null;
		
		if(url != null && url.trim().length() > 0){
		
		
			StringReader stringReader=new StringReader(url.trim());
			int currentChar = 0;
			char ch,ch1;
			StringBuilder currentSymbol = new StringBuilder();
			boolean isHostPortSeparatorSeen = false;
		
			try {
				while((currentChar=stringReader.read())!= END_OF_STRING){
					ch=(char)currentChar;
					
					if(ch==':'){
						ch1=(char)stringReader.read();
						if(ch1=='/'){
							if(currentSymbol.length()>0){
								protocol=new String(currentSymbol);
								currentSymbol=new StringBuilder();
								if((char)stringReader.read()!='/')
									throw new InvalidURLException("Invalid Syntax for URL: " + url);
							}
							continue;
						}else{
							if (isHostPortSeparatorSeen) {
								throw new InvalidURLException("Invalid Syntax for URL: " + url);
							}
							host=new String(currentSymbol);
							currentSymbol=new StringBuilder();
							currentSymbol.append(ch1);
							isHostPortSeparatorSeen = true;
							continue;
						}
					}
					if(ch=='@'){
						if(host!=null){
							userName=host;
							password=new String(currentSymbol);
							host=null;
						}else{
							userName=new String(currentSymbol);
						}
						currentSymbol=new StringBuilder();
						isHostPortSeparatorSeen = false;
						continue;
					}
					if(ch=='['){
						while((char)(currentChar=stringReader.read())!=']' ){
							if(currentChar==END_OF_STRING){
								throw new InvalidURLException("Invalid Syntax for URL: " + url);
								
							}
							currentSymbol.append((char)currentChar);
						}
						isHostPortSeparatorSeen = false;
						continue;
					}
					if(ch=='/'){
						if(currentSymbol.length()>0){
							if(host!=null){
								try{
									port=Integer.parseInt(new String(currentSymbol));
								}catch (NumberFormatException e) {
									throw new InvalidURLException("Invalid port: " + currentSymbol + " in URL: " + url);
								}
							}else{
								host=new String(currentSymbol);
							}
							currentSymbol=new StringBuilder();
						}
						while((currentChar=stringReader.read())!= END_OF_STRING){
							currentSymbol.append((char)currentChar);
						}
						resource=new String(currentSymbol);
						currentSymbol=new StringBuilder();
						break;
					}
					currentSymbol.append(ch);
				}
				if(currentSymbol.length()>0){
					if(host!=null){
						try{
							port=Integer.parseInt(new String(currentSymbol));
						}catch (NumberFormatException e) {
							throw new InvalidURLException("Invalid port: " + currentSymbol + " in URL: " + url);
						}
					}else{
						host=new String(currentSymbol);
					}
				}
				data.setProtocol(protocol);
				data.setUserName(userName);
				data.setPassword(password);
				data.setHost(host);
				data.setResource(resource);
				if (port < 0){
					throw new InvalidURLException("Negative port in URL: " + url);
				} else if (port > 65535) {
					throw new InvalidURLException("Port out of range in URL: " + url);
				} else {
					data.setPort(port);
				}
			}catch(IOException e){
				throw new InvalidURLException(e);
			}
		}else{
			throw new InvalidURLException("URL data can not be null");
		}
		return data;
	}
}
