package com.elitecore.aaa.core.server.axixserver;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.xml.namespace.QName;
import javax.xml.rpc.holders.StringHolder;
import javax.xml.soap.MimeHeader;
import javax.xml.soap.MimeHeaders;
import javax.xml.soap.SOAPMessage;

import org.apache.axis.AxisFault;
import org.apache.axis.Constants;
import org.apache.axis.Message;
import org.apache.axis.MessageContext;
import org.apache.axis.description.OperationDesc;
import org.apache.axis.description.ServiceDesc;
import org.apache.axis.encoding.Base64;
import org.apache.axis.message.SOAPEnvelope;
import org.apache.axis.message.SOAPFault;
import org.apache.axis.server.AxisServer;
import org.apache.axis.transport.http.HTTPConstants;
import org.apache.axis.transport.http.NonBlockingBufferedInputStream;
import org.apache.axis.utils.Messages;
import org.apache.axis.utils.NetworkUtils;
import org.apache.axis.utils.XMLUtils;
import org.w3c.dom.Document;

import com.elitecore.aaa.core.server.AAAServerContext;
import com.elitecore.commons.logging.LogLevel;
import com.elitecore.commons.logging.LogManager;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;

public class EliteWebServiceWorker {

	private EliteWebServiceServer server;
	private static final String MODULE="Elite Web Service Worker";

    // Axis specific constants
    private static String transportName = "SimpleHTTP";

    // HTTP status codes
    private static int OK = 200;
    private static int NOCONTENT = 202 ;
    private static int UNAUTH = 401;
    private static int SENDER = 400;
    private static int ISE = 500;

    // Mime/Content separator
    private static byte SEPARATOR[] = "\r\n\r\n".getBytes();

    // Tiddly little response
//    private static final String responseStr =
//            "<html><head><title>EliteWebServiceServer</title></head>" +
//            "<body><h1>EliteWebServiceServer</h1>" +
//            Messages.getMessage("reachedServer00") +
//            "</html>";
//    private static byte cannedHTMLResponse[] = responseStr.getBytes();

    // ASCII character mapping to lower case
    private static final byte[] toLower = new byte[256];

    static {
        for (int i = 0; i < 256; i++) {
            toLower[i] = (byte) i;
        }

        for (int lc = 'a'; lc <= 'z'; lc++) {
            toLower[lc + 'A' - 'a'] = (byte) lc;
        }
    }

    // buffer for IO
    private static final int BUFSIZ = 4096;
    
    private AAAServerContext serverContext;
    private HttpExchange exchange;

    public EliteWebServiceWorker(EliteWebServiceServer server, HttpExchange exchange ,AAAServerContext serverContext) {
        this.server = server;
        this.serverContext = serverContext;
        this.exchange = exchange;
    }

    /**
     * Run method
     */ 
    public void handleRequest() {
    	String clientIP = exchange.getRemoteAddress().getAddress().getHostAddress();
		if (serverContext.getServerConfiguration().getRadClientConfiguration().isValidClient(clientIP)) {
			handleValidClientRequest();
		} else {
			handleInvalidClientRequest();
		}
    }
    
    /**
     * The main workhorse method.
     */
    private void handleValidClientRequest () {
        byte buf[] = new byte[BUFSIZ];
        // create an Axis server
        AxisServer engine = server.getAxisServer();
  
        // create and initialize a message context
        MessageContext msgContext = new MessageContext(engine);
        Message requestMsg = null;

        // Reusuable, buffered, content length controlled, InputStream
        NonBlockingBufferedInputStream is =
                new NonBlockingBufferedInputStream();

        // buffers for the headers we care about
        StringBuffer soapAction = new StringBuffer();
        StringBuffer httpRequest = new StringBuffer();
        StringBuffer fileName = new StringBuffer();
        StringBuffer cookie = new StringBuffer();
        StringBuffer cookie2 = new StringBuffer();
        StringBuffer authInfo = new StringBuffer();
        StringBuffer contentType = new StringBuffer();
        StringBuffer contentLocation = new StringBuffer();

        Message responseMsg = null;
        
        Headers responseHeaders =  exchange.getResponseHeaders();

        // prepare request (do as much as possible while waiting for the
        // next connection).  Note the next two statements are commented
        // out.  Uncomment them if you experience any problems with not
        // resetting state between requests:
        // msgContext = new MessageContext();
        // requestMsg = new Message("", "String");
        //msgContext.setProperty("transport", "HTTPTransport");
        msgContext.setTransportName(transportName);

        responseMsg = null;
        OutputStream out = exchange.getResponseBody();

        try {
            // assume the best
            int status = OK;

            // assume we're not getting WSDL
            boolean doWsdl = false;

            // cookie for this session, if any
            String cooky = null;

            String methodName = null;

            try {
                // wipe cookies if we're doing sessions
                if (server.isSessionUsed()) {
                    cookie.delete(0, cookie.length());
                    cookie2.delete(0, cookie2.length());
                }
                authInfo.delete(0, authInfo.length());

                // read headers
                is.setInputStream(exchange.getRequestBody());
                // parse all headers into hashtable
                MimeHeaders requestHeaders = new MimeHeaders();
                StringHolder httpVersionHolder = new StringHolder();
                int contentLength = parseHeaders(is, buf, contentType,
                        contentLocation, soapAction,
                        httpRequest, fileName,
                        cookie, cookie2, authInfo, requestHeaders,
                        httpVersionHolder,exchange.getRequestHeaders());
                
                is.setContentLength(contentLength);
                int paramIdx = fileName.toString().indexOf('?');
                if (paramIdx != -1) {
                    // Got params
                    String params = fileName.substring(paramIdx + 1);
                    fileName.setLength(paramIdx);

                    LogManager.getLogger().debug(MODULE, Messages.getMessage("filename00",
                            fileName.toString()));
                    LogManager.getLogger().debug(MODULE,Messages.getMessage("params00",
                            params));

                    if ("wsdl".equalsIgnoreCase(params))
                        doWsdl = true;

                    if (params.startsWith("method=")) {
                        methodName = params.substring(7);
                    }
                }

                // Real and relative paths are the same for the
                // EliteWebServiceServer
                msgContext.setProperty(Constants.MC_REALPATH,
                        fileName.toString());
                msgContext.setProperty(Constants.MC_RELATIVE_PATH,
                        fileName.toString());
                msgContext.setProperty(Constants.MC_JWS_CLASSDIR,
                        "jwsClasses");
                msgContext.setProperty(Constants.MC_HOME_DIR, ".");

                // !!! Fix string concatenation
                String url = server.getProtocol()+"://" + getLocalHost() + ":" +
                        server.getPort() + "/" +
                        fileName.toString();
                msgContext.setProperty(MessageContext.TRANS_URL, url);

                String filePart = fileName.toString();
                if (filePart.startsWith("eliteradius/services/")) {
                    String servicePart = filePart.substring(21);
                    int separator = servicePart.indexOf('/');
                    if (separator > -1) {
                        msgContext.setProperty("objectID",
                                       servicePart.substring(separator + 1));
                        servicePart = servicePart.substring(0, separator);
                    }
                    msgContext.setTargetService(servicePart);
                }

                if (authInfo.length() > 0) {
                    // Process authentication info
                    //authInfo = new StringBuffer("dXNlcjE6cGFzczE=");
                    byte[] decoded = Base64.decode(authInfo.toString());
                    StringBuffer userBuf = new StringBuffer();
                    StringBuffer pwBuf = new StringBuffer();
                    StringBuffer authBuf = userBuf;
                    for (int i = 0; i < decoded.length; i++) {
                        if ((char) (decoded[i] & 0x7f) == ':') {
                            authBuf = pwBuf;
                            continue;
                        }
                        authBuf.append((char) (decoded[i] & 0x7f));
                    }

  /*                  if (log.isDebugEnabled()) {
                    	log.debug(Messages.getMessage("user00",
                               userBuf.toString()));
                    }
*/
                    msgContext.setUsername(userBuf.toString());
                    msgContext.setPassword(pwBuf.toString());
                }

                // if get, then return simpleton document as response
                if (httpRequest.toString().equals("GET")) {
                	
                	if(doWsdl){
                     	responseHeaders.set("Content-Type", "text/xml");
                     }else {
                     	responseHeaders.set("Content-Type", "text/html");
     				}
                    
                    
                    if(fileName.length()==0 || "/".equalsIgnoreCase(fileName.toString())) {
                    	exchange.getResponseHeaders().set("Location", "/eliteradius/");
                    	exchange.sendResponseHeaders(301, 0);
                        out.flush();
                        out.close();
                        return;                        
                    }
                    exchange.sendResponseHeaders(200, 0);

                    if (methodName != null) {
                        String body =
                            "<" + methodName + ">" +
//                               args +
                            "</" + methodName + ">";
                        String msgtxt =
                            "<SOAP-ENV:Envelope" +
                            " xmlns:SOAP-ENV=\"" + Constants.URI_SOAP12_ENV + "\">" +
                            "<SOAP-ENV:Body>" + body + "</SOAP-ENV:Body>" +
                            "</SOAP-ENV:Envelope>";

                        ByteArrayInputStream istream =
                            new ByteArrayInputStream(msgtxt.getBytes());
                        requestMsg = new Message(istream);
                    } else if (doWsdl) {
                        engine.generateWSDL(msgContext);

                        Document doc = (Document) msgContext.getProperty("WSDL");
                        if (doc != null) {
                            XMLUtils.normalize(doc.getDocumentElement());
                            String response = XMLUtils.PrettyDocumentToString(doc);
                            byte[] respBytes = response.getBytes();
                        
                            out.write(SEPARATOR);
                            out.write(respBytes);
                            out.flush();
                            out.close();
                            return;
                        }
                    } else {
                        StringBuffer sb = new StringBuffer(50);
                        sb.append("<TITLE>Elite AAA Server</TITLE>");
                        sb.append("<h1><b>Elite AAA Server </b></h1>");
                        sb.append("<h4>------- ");
                        sb.append(serverContext.getServerVersion());
                        sb.append(" -------</h4>\n");
                        sb.append('\n');
                        sb.append("<h2>Web Service List :</h2>\n");
                        Iterator<?> i = engine.getConfig().getDeployedServices();
                        sb.append("<ul>\n");
                        while (i.hasNext()) {
                            ServiceDesc sd = (ServiceDesc)i.next();
                            sb.append("<li>\n");
                            sb.append(sd.getName());
                            sb.append(" <a href=\"services/");
                            sb.append(sd.getName());
                            sb.append("?wsdl\"><i>(wsdl)</i></a></li>\n");
                            ArrayList<?> operations = sd.getOperations();
                            if (!operations.isEmpty()) {
                                sb.append("<ul>\n");
                                for (Iterator<?> it = operations.iterator(); it.hasNext();) {
                                    OperationDesc desc = (OperationDesc) it.next();
                                    sb.append("<li>").append(desc.getName());
                                }
                                sb.append("</ul>\n");
                            }
                        }
                        sb.append("</ul>\n");
                        sb.append("<h4>Elitecore Technologies Ltd. </h4>\n");

                        byte [] bytes = sb.toString().getBytes();
                        
                        out.write(SEPARATOR);
                        out.write(bytes);
                        out.flush();
                        out.close();
                        return;
                    }
                } else {

                    // this may be "" if either SOAPAction: "" or if no SOAPAction at all.
                    // for now, do not complain if no SOAPAction at all
                    String soapActionString = soapAction.toString();
                    if (soapActionString != null) {
                        msgContext.setUseSOAPAction(true);
                        msgContext.setSOAPActionURI(soapActionString);
                    }
                    requestMsg = new Message(is,
                            false,
                            contentType.toString(),
                            contentLocation.toString()
                    );
                }
                
                // Transfer HTTP headers to MIME headers for request message.
                MimeHeaders requestMimeHeaders = requestMsg.getMimeHeaders();
                for (Iterator<?> i = requestHeaders.getAllHeaders(); i.hasNext(); ) {
                    MimeHeader requestHeader = (MimeHeader) i.next();
                    requestMimeHeaders.addHeader(requestHeader.getName(), requestHeader.getValue());
                }
                msgContext.setRequestMessage(requestMsg);
                // put character encoding of request to message context
                // in order to reuse it during the whole process.   
                String requestEncoding = (String) requestMsg.getProperty(SOAPMessage.CHARACTER_SET_ENCODING);
                if (requestEncoding != null) {
                    msgContext.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, requestEncoding);
                }

                // set up session, if any
                if (server.isSessionUsed()) {
                    // did we get a cookie?
                    if (cookie.length() > 0) {
                        cooky = cookie.toString().trim();
                    } else if (cookie2.length() > 0) {
                        cooky = cookie2.toString().trim();
                    }

                    // if cooky is null, cook up a cooky
                    if (cooky == null) {
                        // fake one up!
                        // make it be an arbitrarily increasing number
                        // (no this is not thread safe because ++ isn't atomic)
                        int i = EliteWebServiceServer.sessionIndex++;
                        cooky = "" + i;
                    }

                    msgContext.setSession(server.createSession(cooky));
                }

                // invoke the Axis engine
                engine.invoke(msgContext);

                // Retrieve the response from Axis
                responseMsg = msgContext.getResponseMessage();
                
                if (responseMsg == null) {
                    status = NOCONTENT;
                }
            } catch (Exception e) {
                AxisFault af;
                if (e instanceof AxisFault) {
                    af = (AxisFault) e;
                    LogManager.getLogger().debug(MODULE,Messages.getMessage("serverFault00")+ af);
                    QName faultCode = af.getFaultCode();
                    if (Constants.FAULT_SOAP12_SENDER.equals(faultCode)) {
                        status = SENDER;
                    } else if ("Server.Unauthorized".equals(af.getFaultCode().getLocalPart())) {
                        status = UNAUTH; // SC_UNAUTHORIZED
                    } else {
                        status = ISE; // SC_INTERNAL_SERVER_ERROR
                    }
                } else {
                    status = ISE; // SC_INTERNAL_SERVER_ERROR
                    af = AxisFault.makeFault(e);
                }

                // There may be headers we want to preserve in the
                // response message - so if it's there, just add the
                // FaultElement to it.  Otherwise, make a new one.
                responseMsg = msgContext.getResponseMessage();
                if (responseMsg == null) {
                    responseMsg = new Message(af);
                    responseMsg.setMessageContext(msgContext);
                } else {
                    try {
                        SOAPEnvelope env = responseMsg.getSOAPEnvelope();
                        env.clearBody();
                        env.addBodyElement(new SOAPFault((AxisFault) e));
                    } catch (AxisFault fault) {
                        // Should never reach here!
                    }
                }
            }

            // synchronize the character encoding of request and response
            String responseEncoding = (String) msgContext.getProperty(SOAPMessage.CHARACTER_SET_ENCODING);
            if (responseEncoding != null && responseMsg != null) {
                responseMsg.setProperty(SOAPMessage.CHARACTER_SET_ENCODING, responseEncoding);
            }
            // Send it on its way...
            
            if (responseMsg != null) {
                if (server.isSessionUsed() && null != cooky &&
                        0 != cooky.trim().length()) {
                    // write cookie headers, if any
                    // don't sweat efficiency *too* badly
                    // optimize at will
                   
                    responseHeaders.set("Set-Cookie", cooky);
                    responseHeaders.set("Set-Cookie2", cooky);
                   
                }

                //out.write(XML_MIME_STUFF);
                responseHeaders.set(HTTPConstants.HEADER_CONTENT_TYPE, responseMsg.getContentType(msgContext.getSOAPConstants()));
                // Writing the length causes the entire message to be decoded twice.
                // Uncommenting the below code to support HTTP version 1.1, as Content Length is mandatory there.
                
                responseHeaders.set(HTTPConstants.HEADER_CONTENT_LENGTH, String.valueOf(responseMsg.getContentLength()));

                // Transfer MIME headers to HTTP headers for response message.
                for (Iterator<?> i = responseMsg.getMimeHeaders().getAllHeaders(); i.hasNext(); ) {
                    MimeHeader responseHeader = (MimeHeader) i.next();
                    responseHeaders.set(responseHeader.getName(), responseHeader.getValue());
                }

            }
            exchange.sendResponseHeaders(status, 0);
            responseMsg.writeTo(out);

            out.flush();
            out.close();
            
            if(LogManager.getLogger().isLogLevel(LogLevel.TRACE)){
            	LogManager.getLogger().trace(MODULE, "Soap request message: " + msgContext.getRequestMessage().getSOAPPartAsString());
            	LogManager.getLogger().trace(MODULE, "Soap repsonse message: " + msgContext.getResponseMessage().getSOAPPartAsString());
            }
        } catch (Exception e) {
        	LogManager.getLogger().info(MODULE,Messages.getMessage("exception00")+ e);
        } finally {
        	
        	if(is != null){
        		try {
					is.close();
				} catch (IOException e) {
					LogManager.getLogger().error(MODULE, e.getMessage());
					LogManager.getLogger().trace(e);
				}
        	}
        	exchange.close();
        }
        if (msgContext.getProperty(MessageContext.QUIT_REQUESTED) != null) {
            // why then, quit!
            try {
                server.stopService();
            } catch (Exception e) {
            }
        }

    }

    protected void invokeMethodFromGet(String methodName, String args) throws Exception {

    }

    /**
     * Read all mime headers, returning the value of Content-Length and
     * SOAPAction.
     * @param is         InputStream to read from
     * @param contentType The content type.
     * @param contentLocation The content location
     * @param soapAction StringBuffer to return the soapAction into
     * @param httpRequest StringBuffer for GET / POST
     * @param cookie first cookie header (if doSessions)
     * @param cookie2 second cookie header (if doSessions)
     * @param headers HTTP headers to transfer to MIME headers
     * @return Content-Length
     */
    private int parseHeaders(NonBlockingBufferedInputStream is,
                             byte buf[],
                             StringBuffer contentType,
                             StringBuffer contentLocation,
                             StringBuffer soapAction,
                             StringBuffer httpRequest,
                             StringBuffer fileName,
                             StringBuffer cookie,
                             StringBuffer cookie2,
                             StringBuffer authInfo,
                             MimeHeaders headers,
                             StringHolder httpVersion,Headers requestHeaders)
            throws IOException {
    	
        int len = 0;
        
        httpRequest.delete(0, httpRequest.length());
        fileName.delete(0, fileName.length());
        contentType.delete(0, contentType.length());
        contentLocation.delete(0, contentLocation.length());
                
        if (exchange.getRequestMethod().equalsIgnoreCase("GET")) {
            httpRequest.append("GET");
            String path = exchange.getRequestURI().getRawSchemeSpecificPart();
            if(path!=null){
            	if(path.length()>1)
            		fileName.append(exchange.getRequestURI().getRawSchemeSpecificPart().substring(1));
            	else {
            		fileName.append(exchange.getRequestURI().getRawSchemeSpecificPart());
				}
            }	
            LogManager.getLogger().debug(MODULE,Messages.getMessage("filename01", "EliteWebServiceServer", fileName.toString()));
            return 0;
        } else if (exchange.getRequestMethod().equalsIgnoreCase("POST")) {
            httpRequest.append("POST");
            String path = exchange.getRequestURI().getRawSchemeSpecificPart();
            if(path!=null){
            	if(path.length()>1)
            		fileName.append(exchange.getRequestURI().getRawSchemeSpecificPart().substring(1));
            	else {
            		fileName.append(exchange.getRequestURI().getRawSchemeSpecificPart());
				}
            }
            httpVersion.value = exchange.getProtocol();
            LogManager.getLogger().debug(MODULE,Messages.getMessage("filename01", "EliteWebServiceServer", fileName.toString()));
        } else {
            throw new IOException(Messages.getMessage("badRequest00"));
        }

        for(Map.Entry<String, List<String>> header :requestHeaders.entrySet()){
        	List<String> headerValues = header.getValue();
        	if(headerValues!=null && headerValues.size()>0){
        		headers.addHeader(header.getKey(), headerValues.get(0));
        	}
        }
        String strContentLength = requestHeaders.getFirst("Content-Length");
        if(strContentLength!=null){
        	len = Integer.parseInt(strContentLength);
        }
        return len;
    }

    	/**
     * does tolower[buf] match the target byte array, up to the target's length?
     */
    public boolean matches(byte[] buf, byte[] target) {
        for (int i = 0; i < target.length; i++) {
            if (toLower[buf[i]] != target[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * Case-insensitive match of a target byte [] to a source byte [],
     * starting from a particular offset into the source.
     */
    public boolean matches(byte[] buf, int bufIdx, byte[] target) {
        for (int i = 0; i < target.length; i++) {
            if (toLower[buf[bufIdx + i]] != target[i]) {
                return false;
            }
        }
        return true;
    }

    /**
     * One method for all host name lookups.
     */
    public static String getLocalHost() {
        return NetworkUtils.getLocalHostname();
    }

    /**
     * Reply back with Unauthorised/Forbidden access back to the caller.
     */
    
    private void handleInvalidClientRequest(){
    	
    	LogManager.getLogger().warn(MODULE, "Sending UnAuthorised/Forbidden Error code back to the caller");
    	
        byte buf[] = new byte[BUFSIZ];
        // create an Axis server
        AxisServer engine = server.getAxisServer();

        // create and initialize a message context
        MessageContext msgContext = new MessageContext(engine);

        // Reusuable, buffered, content length controlled, InputStream
        NonBlockingBufferedInputStream is =
                new NonBlockingBufferedInputStream();

        // buffers for the headers we care about
        StringBuffer soapAction = new StringBuffer();
        StringBuffer httpRequest = new StringBuffer();
        StringBuffer fileName = new StringBuffer();
        StringBuffer cookie = new StringBuffer();
        StringBuffer cookie2 = new StringBuffer();
        StringBuffer authInfo = new StringBuffer();
        StringBuffer contentType = new StringBuffer();
        StringBuffer contentLocation = new StringBuffer();

        msgContext.setTransportName(transportName);

	    try {
	        // assume the Worst
	        int status = UNAUTH;
	        
            try {
                // wipe cookies if we're doing sessions
                if (server.isSessionUsed()) {
                    cookie.delete(0, cookie.length());
                    cookie2.delete(0, cookie2.length());
                }
                authInfo.delete(0, authInfo.length());

                // read headers
                is.setInputStream(exchange.getRequestBody());
                // parse all headers into hashtable
                MimeHeaders requestHeaders = new MimeHeaders();
                StringHolder httpVersionHolder = new StringHolder();
                parseHeaders(is, buf, contentType,
                        contentLocation, soapAction,
                        httpRequest, fileName,
                        cookie, cookie2, authInfo, requestHeaders,
                        httpVersionHolder,exchange.getRequestHeaders());


            } catch (Exception e ) {
            	LogManager.getLogger().trace(MODULE, e);
            }
            
            exchange.getResponseHeaders().set("Content-Type", "text/html");
            exchange.sendResponseHeaders(status, 0);
            
	        OutputStream out = exchange.getResponseBody();
	        

            if (httpRequest.toString().equals("GET")) {
        
		        StringBuffer sb = new StringBuffer(50);
		        sb.append("</ul>\n");
                sb.append("<TITLE>Elite AAA Server</TITLE>");
		        sb.append("<h1>403 : Forbidden</h1>\n");
		        sb.append("<h4>Elite Radius Server</h4>\n");
		        byte [] bytes = sb.toString().getBytes();

		     
		        out.write(SEPARATOR);
		        out.write(bytes);
		        out.flush();
		        out.close();
		        return;
            } else {
                out.write(SEPARATOR);
                out.flush();
                out.close();
                return;
            }
	            
	     }catch(Exception e) {
	    	 LogManager.getLogger().trace(MODULE, e);
	     } finally {
	    	 
	    	 if(is != null){
	    		 try {
	    			 is.close();
	    		 } catch (IOException e) {
	    			 LogManager.getLogger().error(MODULE, e.getMessage());
	    			 LogManager.getLogger().trace(e);
	    		 }
	    	 }
	    	 exchange.close();    	 
	     }
	}
}